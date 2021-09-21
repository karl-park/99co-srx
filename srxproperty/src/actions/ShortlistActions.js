import { ShortlistPO, ListingPO } from "../dataObject";
import { ShortlistService } from "../services";
import {
  ObjectUtil,
  GoogleAnalyticUtil,
  ShortlistDatabaseUtil
} from "../utils";
import { UPDATE_SHORTLIST_ITEM, UPDATE_SHORTLIST_STATE } from "./types";
import { store } from "../store";
import { LoadingState } from "../constants";

const handleShortlistResponse = ({ dispatch, response }) => {
  if (!ObjectUtil.isEmpty(response)) {
    const { result } = response;
    if (!ObjectUtil.isEmpty(result) && Array.isArray(result)) {
      const shortlistArray = [];

      result.map((data, index) => {
        shortlistArray.push(new ShortlistPO(data));
      });

      dispatch({ type: UPDATE_SHORTLIST_ITEM, payload: shortlistArray });
    } else {
      dispatch({
        type: UPDATE_SHORTLIST_STATE,
        shortlistState: LoadingState.Loaded
      });
    }
  }
};

//Track shortlist
const trackShortlistListing = ({ shortlistPO }) => {
  //get some attributes to track
  if (!ObjectUtil.isEmpty(shortlistPO)) {
    if (!ObjectUtil.isEmpty(shortlistPO.refListing)) {
      const {
        cdResearchSubType,
        districtHdbTown,
        postalDistrictId,
        postalHdbTownId,
        propertyType,
        type
      } = shortlistPO.refListing;

      //call trackShortlisting method
      GoogleAnalyticUtil.trackShortlisting({
        parameters: {
          cdResearchSubType,
          districtHdbTown,
          postalDistrictId,
          postalHdbTownId,
          propertyType,
          type
        }
      });
    }
  }
};

const getAllShortListedItemsByUser = ({ sortBy, dispatch }) => {
  ShortlistService.getAllShortListedItemsByUser({
    sortBy
  })
    .then(response => {
      handleShortlistResponse({ dispatch, response });
    })
    .catch(error => {
      console.log(
        "ShortlistService.getAllShortListedItemsByUser - failed - ShortlistAction"
      );
      console.log(error);
    });
};

export const loadShortlistedItems = ({ sortBy }) => {
  const { userPO, loadingState } = store.getState().loginData;

  return dispatch => {
    //after login
    if (!ObjectUtil.isEmpty(userPO) && loadingState === LoadingState.Loaded) {
      var localShortlistArray = [];
      var shortlistItemArray = [];

      //retrieve shortlist from db
      var retrieveShortlist = ShortlistDatabaseUtil.retrieveAll();

      retrieveShortlist.map(item => {
        localShortlistArray.push(
          new ShortlistPO(JSON.parse(item.shortlistitem))
        );
      });

      //if local shortlist array
      if (
        !ObjectUtil.isEmpty(localShortlistArray) &&
        localShortlistArray.length > 0
      ) {
        for (let i = 0; i < localShortlistArray.length; i++) {
          shortlistItemArray.push({
            encryptedRefId: localShortlistArray[i].encryptedRefId,
            refType: localShortlistArray[i].refListing.listingType
          });
        }

        ShortlistDatabaseUtil.deleteAll();
        // set empty in localShortlistArray
        localShortlistArray = [];

        ShortlistService.shortListItems({
          items: shortlistItemArray
        })
          .then(response => {
            const { result } = response;

            result.map((data, index) => {
              //get shortlistPO
              var shortlistPO = new ShortlistPO(data);

              //google analytics track for shortlist
              trackShortlistListing({ shortlistPO });
            });

            getAllShortListedItemsByUser({
              sortBy,
              dispatch
            });
          })
          .catch(error => {
            console.log(
              "ShortlistService.shortListItem - failed - ShortlistAction"
            );
          });
      } else {
        //if no shortlist in local shortlist array
        getAllShortListedItemsByUser({
          sortBy,
          dispatch
        });
      }
    } else if (
      ObjectUtil.isEmpty(userPO) &&
      loadingState === LoadingState.Failed
    ) {
      //user not login
      retrieveShortlistList = ShortlistDatabaseUtil.retrieveAll();
      const shortlistArray = [];
      retrieveShortlistList.map(item => {
        shortlistArray.push(new ShortlistPO(JSON.parse(item.shortlistitem)));
      });
      //2
      dispatch({ type: UPDATE_SHORTLIST_ITEM, payload: shortlistArray });
    }
  };
};

export const shortlistListing = ({
  encryptedListingId,
  listingType,
  listingPO
}) => {
  const { userPO } = store.getState().loginData;
  return dispatch => {
    return new Promise(function(resolve, reject) {
      //if user login
      if (!ObjectUtil.isEmpty(userPO)) {
        ShortlistService.shortListItem({
          encryptedRefId: encryptedListingId,
          refType: listingType
        })
          .then(response => {
            //reponse only one added shortlisted object
            const { result } = response;
            const savedShortlistArray = [];

            result.map((data, index) => {
              //get shortlistPO
              var shortlistPO = new ShortlistPO(data);

              //google analytics track for shortlist
              trackShortlistListing({ shortlistPO });
              //save in shortlist array
              savedShortlistArray.push(shortlistPO);
            });

            const newShortListArray = [
              ...savedShortlistArray,
              ...store.getState().shortlistData.shortlistedItems
            ];

            dispatch({
              type: UPDATE_SHORTLIST_ITEM,
              payload: newShortListArray
            });

            resolve(response);
          })
          .catch(error => {
            console.log(
              "ShortlistService.shortListItem - failed - ShortlistAction"
            );
            console.log(error);
            reject(error);
          });
      }
      // user not login
      else {
        const customShortlistPO = {
          // generate random id
          id: Math.floor(100000 + Math.random() * 900000),
          encryptedRefId: encryptedListingId,
          refId: parseInt(encryptedListingId.match(/\d/g).join("")),
          refListing: new ListingPO(listingPO)
        };

        const savedCustomShortlistArray = [];

        //get shortlistPO
        var shortlistPO = new ShortlistPO(customShortlistPO);
        //google analytics track for shortlist
        trackShortlistListing({ shortlistPO });
        //save in shortlist array
        savedCustomShortlistArray.push(shortlistPO);

        //for redux state
        const newCustomShortListArray = [
          ...savedCustomShortlistArray,
          ...store.getState().shortlistData.shortlistedItems
        ];

        dispatch({
          type: UPDATE_SHORTLIST_ITEM,
          payload: newCustomShortListArray
        });

        //Realm save
        newCustomShortListArray.map(item => {
          const findAllshortlist = ShortlistDatabaseUtil.findAll(item.id);

          if (ObjectUtil.isEmpty(findAllshortlist)) {
            ShortlistDatabaseUtil.save(item.id, item);
          }
        });
      }
    });
  };
};

export const removeShortlist = ({ shortlistId }) => {
  const { userPO } = store.getState().loginData;
  return dispatch => {
    return new Promise(function(resolve, reject) {
      //if user login
      if (!ObjectUtil.isEmpty(userPO)) {
        ShortlistService.removeShortlist({
          shortlistId
        })
          .then(response => {
            //remove list from existing shortlistedItems;
            let shortlistArray = [
              ...store.getState().shortlistData.shortlistedItems
            ];

            shortlistArray.splice(
              shortlistArray.findIndex(obj => obj.id === shortlistId),
              1
            );

            dispatch({ type: UPDATE_SHORTLIST_ITEM, payload: shortlistArray });

            resolve(response);
          })
          .catch(error => {
            console.log(
              "ShortlistService.removeShortlist - failed - ShortlistAction"
            );
            console.log(error);
            reject(error);
          });
      } else {
        // user is not login
        //remove list from existing shortlist
        let shortlistList = [
          ...store.getState().shortlistData.shortlistedItems
        ];

        //Realm delete
        shortlistList.map(item => {
          ShortlistDatabaseUtil.deleteShortlistById(shortlistId);
        });

        shortlistList.splice(
          shortlistList.findIndex(obj => obj.id === shortlistId),
          1
        );
        dispatch({ type: UPDATE_SHORTLIST_ITEM, payload: shortlistList });
      }
    });
  };
};

export const clearAllShortlist = () => {
  return { type: UPDATE_SHORTLIST_ITEM, payload: [] };
};
