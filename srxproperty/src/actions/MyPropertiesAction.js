import {
  LOAD_MY_PROPERTIES,
  LOAD_MY_PROPERTIES_SUCCESS,
  LOAD_MY_PROPERTIES_FAILED,
  REMOVE_MY_PROPERTY,
  UPDATE_MY_PROPERTY
} from "./types";
import { ObjectUtil, CommonUtil } from "../utils";
import { PropertyTrackerService } from "../services";
import { SRXPropertyUserPO } from "../dataObject";

const loadPropertyTrackers = () => {
  return dispatch => {
    dispatch({ type: LOAD_MY_PROPERTIES });

    PropertyTrackerService.loadPropertyTrackers({ populateXvalue: false })
      .then(resp => {
        if (!ObjectUtil.isEmpty(resp)) {
          const errorMsg = CommonUtil.getErrorMessageFromSRXResponse(resp);
          if (!ObjectUtil.isEmpty(errorMsg)) {
            dispatch({
              type: LOAD_MY_PROPERTIES_FAILED
            });
          } else {
            const { result } = resp;
            if (!ObjectUtil.isEmpty(result) && Array.isArray(result)) {
              const trackerArray = result.map(
                item => new SRXPropertyUserPO(item)
              );
              dispatch({
                type: LOAD_MY_PROPERTIES, //change to loading
                payload: trackerArray
              });
              PropertyTrackerService.loadPropertyTrackers({
                populateXvalue: true,
              })
                .then(resp => {
                  if (!ObjectUtil.isEmpty(resp)) {
                    const errorMsg = CommonUtil.getErrorMessageFromSRXResponse(
                      resp,
                    );
                    if (!ObjectUtil.isEmpty(errorMsg)) {
                      //do nothing keep the current one
                    } else {
                      const {result} = resp;
                      if (
                        !ObjectUtil.isEmpty(result) &&
                        Array.isArray(result)
                      ) {
                        const trackerArray = result.map(
                          item => new SRXPropertyUserPO(item),
                        );
                        dispatch({
                          type: LOAD_MY_PROPERTIES_SUCCESS,
                          payload: trackerArray,
                        });
                      } else {
                        //do nothing keep the current one
                      }
                    }
                  } else {
                    //do nothing keep the current one
                  }
                })
                .catch(error => {
                  //do nothing keep the current one
                });
            } else {
              dispatch({
                type: LOAD_MY_PROPERTIES_SUCCESS,
                payload: [],
              });
            }
          }
        } else {
          dispatch({
            type: LOAD_MY_PROPERTIES_FAILED,
          });
        }
      })
      .catch(error => {
        dispatch({ type: LOAD_MY_PROPERTIES_FAILED });
      });
  };
};

const clearPropertyTrackers = () => {
  return { type: LOAD_MY_PROPERTIES_SUCCESS, payload: [] };
};

const removeTracker = trackerPO => {
  return dispatch => {
    PropertyTrackerService.removePropertyUser({
      ptUserId: trackerPO.ptUserId
    }).then(response => {
      if (!ObjectUtil.isEmpty(response)) {
        const { result } = response;
        if (result === "success") {
          dispatch({ type: REMOVE_MY_PROPERTY, payload: trackerPO });
        }
      }
    });
  };
};

const updateTracker = ({ originalTrackerPO, newTrackerPO }) => {
  return {
    type: UPDATE_MY_PROPERTY,
    payload: {originalTrackerPO, newTrackerPO},
  };
};

export {
  loadPropertyTrackers,
  clearPropertyTrackers,
  removeTracker,
  updateTracker,
};
