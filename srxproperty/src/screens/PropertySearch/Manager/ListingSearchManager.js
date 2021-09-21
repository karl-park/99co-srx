import PropTypes from 'prop-types';
import {SearchPropertyService} from '../../../services';
import {
  ObjectUtil,
  PropertyTypeUtil,
  CommonUtil,
  StringUtil,
  GoogleAnalyticUtil,
} from '../../../utils';
import {ListingPO, AgentPO} from '../../../dataObject';

// const LISTING_SEARCH_MANAGER_STATE = {
//   DEFAULT: "DEFAULT",//usually state, not loading
//   LOADING: "LOADING"
// };

const SearchOptionsStruct = PropTypes.shape({
  /*
   * String, to be display in search result, optional
   */
  displayText: PropTypes.string,
  propertyType: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.array,
    PropTypes.instanceOf(Set),
  ]),
  /*
   * String, could be address or postal code, etc
   */
  searchText: PropTypes.string,
  /*
   * comma seperated Ids
   */
  selectedAmenitiesIds: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number,
  ]),
  selectedDistrictIds: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number,
  ]),
  selectedHdbTownIds: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),

  /*
   * Filter Options
   */
  cdResearchSubTypes: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  cdTenure: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),

  builtYearMax: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  builtYearMin: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),

  furnish: PropTypes.string,
  floor: PropTypes.string,

  leaseTermOptions: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),

  maxBathrooms: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  minBathrooms: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),

  bedrooms: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  maxBedrooms: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  minBedrooms: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),

  maxBuiltSize: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  minBuiltSize: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),

  maxLandSize: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  minLandSize: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),

  maxPSF: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  minPSF: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),

  maxRentPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  minRentPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),

  maxSalePrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  minSalePrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),

  minDateFirstPosted: PropTypes.string,

  modelsNotRequired: PropTypes.string,
  models: PropTypes.string,

  droneViewFilter: PropTypes.oneOfType([PropTypes.string, PropTypes.bool]),
  isRoomRental: PropTypes.oneOfType([PropTypes.string, PropTypes.bool]),
  pcFilter: PropTypes.oneOfType([PropTypes.string, PropTypes.bool]),
  spvFilter: PropTypes.oneOfType([PropTypes.string, PropTypes.bool]),
  vt360Filter: PropTypes.oneOfType([PropTypes.string, PropTypes.bool]),
  exclusiveFilter: PropTypes.oneOfType([PropTypes.string, PropTypes.bool]),
  ownerCertificationFilter: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.bool,
  ]),

  projectLaunchStatus: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number,
  ]),

  /*
   * S or R
   */
  type: PropTypes.string,
  /*
   *  Show Listings or Transacted Listings
   */
  isTransacted: PropTypes.oneOfType([PropTypes.string, PropTypes.bool]),
});

class ListingSearchManager {
  // static searchState = LISTING_SEARCH_MANAGER_STATE;
  tracking = false;
  listingArray = [];
  srxStpArray = [];
  mclpSearchTermArray = [];
  mclpNearByArray = [];
  totalListing = 0;
  featuredAgentsArray = [];

  isLoading = false;
  seed = null;

  /*
   * Arguments for listing search api
   */
  options = {
    searchType: 'feature',
    type: 'S',
    isTotalOnly: false, //get all listings
  };

  constructor(data) {
    if (data) {
      const {
        /*
         * String, to be display in search result, optional
         */
        displayText,
        /**
         * String, one of sort options
         */
        orderCriteria,
        propertyType,
        /*
         * String, could be address or postal code, etc
         */
        searchText,
        /*
         * comma seperated Ids
         */
        selectedAmenitiesIds,
        selectedDistrictIds,
        selectedHdbTownIds,

        /**
         *  distance from selected locations
         */
        radiusInKm,

        /**Filter Option*/
        cdResearchSubTypes,
        cdTenure,

        builtYearMax,
        builtYearMin,

        furnish,
        floor,

        leaseTermOptions,

        bathrooms,
        maxBathrooms,
        minBathrooms,

        bedrooms,
        maxBedrooms,
        minBedrooms,

        maxBuiltSize,
        minBuiltSize,

        maxLandSize,
        minLandSize,

        maxPSF,
        minPSF,

        maxRentPrice,
        minRentPrice,

        maxSalePrice,
        minSalePrice,

        minDateFirstPosted,
        modelsNotRequired,
        models,

        droneViewFilter,
        isRoomRental,
        pcFilter,
        spvFilter,
        vt360Filter,
        exclusiveFilter,
        ownerCertificationFilter,
        projectLaunchStatus, //1 - New launch, 2 - Resale, 3 - New launch excluded
        /*
         * S or R
         */
        type,

        listingIds,
        isTransacted,
      } = data;
      console.log(data);
      let searchType;

      //Need them for sectioning nearBy Listings
      let channelsFilter;
      let wantedListingGroups;
      let keywords;
      let isSale;

      if (!ObjectUtil.isEmpty(searchText)) {
        searchType = 'keyword';
      } else {
        searchType = 'feature';
      }

      //For sectioning nearby listings
      if (searchType == 'keyword') {
        channelsFilter = '4';
        wantedListingGroups = 'mclpAllMatchSearchTerm,mclpAllNearby';
        keywords = searchText;
      } else {
        channelsFilter = '7';
        wantedListingGroups = 'srxstp';
      }

      this.options = {
        ...this.options,
        searchText,
        searchType,
        orderCriteria,
        selectedAmenitiesIds,
        selectedDistrictIds,
        selectedHdbTownIds,
        radiusInKm,

        cdResearchSubTypes,
        cdTenure,

        builtYearMax,
        builtYearMin,

        furnish,
        floor,

        leaseTermOptions,

        bathrooms,
        maxBathrooms,
        minBathrooms,

        bedrooms,
        maxBedrooms,
        minBedrooms,

        maxBuiltSize,
        minBuiltSize,

        maxLandSize,
        minLandSize,

        maxPSF,
        minPSF,

        maxRentPrice,
        minRentPrice,

        maxSalePrice,
        minSalePrice,

        minDateFirstPosted,
        modelsNotRequired,
        models,

        droneViewFilter,
        isRoomRental,
        pcFilter,
        spvFilter,
        vt360Filter,
        exclusiveFilter,
        ownerCertificationFilter,
        projectLaunchStatus,

        type,

        channelsFilter,
        wantedListingGroups,
        keywords,
        isSale,

        listingIds,
        isTransacted,
      };
    }
  }

  register = callBack => {
    this.callBack = callBack;
  };

  sendBackResponse = (
    newListings,
    allListings,
    error,
    counts,
    srxStpArray,
    mclpSearchTermArray,
    mclpNearByArray,
    featuredAgents,
  ) => {
    if (this.callBack) {
      const a = {
        newListings,
        allListings,
        error,
        counts,
        srxStpArray,
        mclpSearchTermArray,
        mclpNearByArray,
        featuredAgents,
        manager: this,
      };
      console.log(a);
      this.callBack(a);
    }
  };

  canLoadMore = () => {
    return this.listingArray.length < this.totalListing;
  };

  search = ({tracking}) => {
    this.tracking = tracking;
    console.log('manager - search triggered');
    if (this.isLoading) return;

    //to reset arrays
    this.listingArray = [];
    this.srxStpArray = [];
    this.mclpSearchTermArray = [];
    this.mclpNearByArray = [];
    this.featuredAgentsArray = [];

    if (tracking) {
      GoogleAnalyticUtil.trackListingSearch({
        parameters: {
          ...this.options,
          startResultIndex: this.listingArray.length,
        },
      });
    }

    this.loadListingFromServer();
  };

  loadMore = () => {
    if (this.isLoading) return;

    if (!this.canLoadMore()) return;

    if (this.tracking) {
      GoogleAnalyticUtil.trackListingSearch({
        parameters: {
          ...this.options,
          startResultIndex: this.listingArray.length,
        },
      });
    }

    this.loadListingFromServer();
  };

  loadListingFromServer = () => {
    this.isLoading = true;

    console.log(this.options);

    SearchPropertyService.searchListing({
      ...this.options,
      startResultIndex: this.listingArray.length,
      /**
       * only need to pass seed value if startResultIndex is not zero
       */
      seed: this.listingArray.length > 0 ? this.seed : '',
    })
      .then(response => {
        this.isLoading = false;
        this.handleResponse(response);
      })
      .catch(error => {
        this.isLoading = false;
        console.log(
          'SearchPropertyService.searchListing - failed - ListingSearchManager',
        );
        console.log(error);
      });
  };

  handleResponse(response) {
    if (!ObjectUtil.isEmpty(response)) {
      const {listingResult, featuredAgents, seed} = response;

      const newListings = [];

      let srxStpCount = 0,
        matchedCount = 0,
        nearbyCount = 0;

      /**
       * get seed value from response
       */
      this.seed = seed;

      //feature agents
      if (!ObjectUtil.isEmpty(featuredAgents)) {
        const {agentPOs} = featuredAgents;
        this.featuredAgentsArray = [];
        agentPOs.map((item, index) => {
          this.featuredAgentsArray.push(new AgentPO(item));
        });
      }

      //listingResult
      if (!ObjectUtil.isEmpty(listingResult)) {
        //Get SrxstpListings
        const {
          srxStpListings,
          mclpAllMatchSearchTerm,
          mclpAllNearby,
        } = listingResult;

        if (!ObjectUtil.isEmpty(srxStpListings)) {
          const {listingPOs, total} = srxStpListings;
          srxStpCount = total;
          if (!ObjectUtil.isEmpty(listingPOs) && Array.isArray(listingPOs)) {
            listingPOs.map((item, index) => {
              const newPO = new ListingPO(item);
              this.srxStpArray.push(newPO);
              newListings.push(newPO);
            });
          }
        }
        if (!ObjectUtil.isEmpty(mclpAllMatchSearchTerm)) {
          const {listingPOs, total} = mclpAllMatchSearchTerm;
          matchedCount = total;
          if (!ObjectUtil.isEmpty(listingPOs) && Array.isArray(listingPOs)) {
            listingPOs.map((item, index) => {
              const newPO = new ListingPO(item);
              this.mclpSearchTermArray.push(newPO);
              newListings.push(newPO);
            });
          }
        }
        if (!ObjectUtil.isEmpty(mclpAllNearby)) {
          const {listingPOs, total} = mclpAllNearby;
          nearbyCount = total;
          if (!ObjectUtil.isEmpty(listingPOs) && Array.isArray(listingPOs)) {
            listingPOs.map((item, index) => {
              const newPO = new ListingPO(item);
              this.mclpNearByArray.push(newPO);
              newListings.push(newPO);
            });
          }
        }

        this.totalListing = srxStpCount + matchedCount + nearbyCount;
        this.listingArray = [...this.listingArray, ...newListings];
      }

      this.sendBackResponse(
        newListings,
        this.listingArray,
        null,
        /**
         * return in object form,
         * count for each sections
         */
        {
          total: this.totalListing,
          srxStpCount,
          matchedCount,
          nearbyCount,
        },
        this.srxStpArray,
        this.mclpSearchTermArray,
        this.mclpNearByArray,
        this.featuredAgentsArray,
      );
    }
  }
}

export {ListingSearchManager, SearchOptionsStruct};
