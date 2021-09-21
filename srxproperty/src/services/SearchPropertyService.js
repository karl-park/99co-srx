import request from './axios';

const maxPerCount = 10;

function getAmenitiesForProjectSearch() {
  return request({
    url: '/srx/project/getAmenitiesForProjectSearch/redefinedSearch.srx',
    method: 'POST',
    params: null,
  });
}

function loadFeaturedListings({cdResearchSubtype}) {
  return request({
    url: '/srx/loadFeaturedListings/listing.srx?',
    method: 'GET',
    params: {
      action: 'loadFeaturedListings',
      cdResearchSubtype,
    },
  });
}

function getListing({listingId, listingType}) {
  return request({
    url: '/api/v1/listings/search?action=getListing',
    method: 'GET',
    params: {
      listingId,
      listingType,
    },
  });
}

function loadFullListingDetails({listingId, refType, shortlistId}) {
  return request({
    url: '/mobile/iphone/consumer2.mobile3',
    method: 'POST',
    params: {
      action: 'loadFullListingDetails',
      listingId,
      // encryptedListingRefId
      refType,
      shortlistId,
    },
  });
}

function loadFullProjectDetails({crsId, postalCode}) {
  return request({
    url: '/mobile/iphone/consumer2.mobile3',
    method: 'POST',
    params: {
      action: 'loadFullProjectDetails',
      projectId: crsId,
      postalCode,
    },
  });
}

function loadListingsCount({
  cdResearchSubTypes,
  orderCriteria,
  searchText,
  searchType,
  selectedAmenitiesIds,
  selectedDistrictIds,
  selectedHdbTownIds,
  startResultIndex,
  cdTenure,
  builtYearMax,
  builtYearMin,
  bathrooms,
  maxBathrooms,
  minBathrooms,
  furnish,
  floor,
  leaseTermOptions,
  bedrooms,
  maxBedrooms,
  minBedrooms,
  maxBuiltSize,
  minBuiltSize,
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
  projectLaunchStatus,
  type,
  isTotalOnly, //get total number of listings only
}) {
  return request({
    url: '/srx/loadListingsCount/listing.srx',
    method: 'POST',
    params: {
      channelsFilter: '7',
      searchSource: '1',
      resultGrouping: 'byListing',
      searchType: searchType ? searchType : 'feature',
      maxResults: maxPerCount,
      startResultIndex,
      //other parameters add below
      cdResearchSubTypes,
      orderCriteria,
      searchText,
      selectedAmenitiesIds,
      selectedDistrictIds,
      selectedHdbTownIds,
      cdTenure,
      builtYearMax,
      builtYearMin,
      bathrooms,
      maxBathrooms,
      minBathrooms,
      furnish,
      floor,
      leaseTermOptions,
      bedrooms,
      maxBedrooms,
      minBedrooms,
      maxBuiltSize,
      minBuiltSize,
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
      projectLaunchStatus,
      type,
      isTotalOnly, //get total number of listings only
    },
  });
}

function loadRecommendedListings({listingId, refType}) {
  return request({
    url: '/mobile/iphone/consumer2.mobile3',
    method: 'GET',
    params: {
      action: 'loadRecommendedListings',
      listingId,
      refType,
    },
  });
}

searchListing = ({
  cdResearchSubTypes,
  orderCriteria,
  searchText,
  searchType,
  selectedAmenitiesIds,
  selectedDistrictIds,
  selectedHdbTownIds,
  startResultIndex,

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
  type,
  //get total number of listings only
  isTotalOnly,

  //for Sectioning nearBy Listings
  channelsFilter,
  wantedListingGroups,
  keywords,
  isSale,

  /**
   * comma separated string
   * passing this will return the listings
   */
  listingIds,
  isTransacted,

  /**
   * seed param for sorting
   */
  seed,
}) => {
  console.log('To Call APIs in search listings....');
  console.log('Min Land Size ' + minLandSize + 'Max Land Size ' + maxLandSize);

  let saleOrRentType = 'S';
  if (isSale && typeof isSale == 'boolean') {
    saleOrRentType == (isSale === true) ? 'S' : 'R';
  }
  if (type) {
    saleOrRentType = type;
  }

  return request({
    url: 'srx/project/findCrunchResearchStreets/redefinedSearch.srx',
    method: 'POST',
    params: {
      channelsFilter: channelsFilter ? channelsFilter : '7', //4 to 7
      searchSource: '1',
      resultGrouping: 'byListing', //to be collected
      wantedListingGroups: wantedListingGroups ? wantedListingGroups : 'srxstp',
      searchType: searchType ? searchType : 'feature',
      maxResults: 20,
      startResultIndex,

      //other parameters add below
      cdResearchSubTypes,
      orderCriteria,
      searchText,
      selectedAmenitiesIds,
      selectedDistrictIds,
      selectedHdbTownIds,

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

      type: saleOrRentType,
      //get total number of listings only
      isTotalOnly,
      featuredAgents: startResultIndex == 0 ? true : false,

      //for Sectioning nearBy Listings
      keywords,
      isSale: saleOrRentType === 'S',

      /**
       * comma separated string
       * passing this will return the listings
       */
      listingIds,
      isTransacted,
      /**
       * seed param for sorting
       */
      seed,
    },
  });
};

findMapRegionView = ({filterType, filterId}) => {
  /**
   * Finds the area polygon and returns the area name & marker point of the area
   *
   * ----------
   * filterType
   * ----------
   * 0 : region
   * 1 : planning area
   * 2 : subzone
   * 3 : gcb
   * 4 : landed
   * 5 : district
   * 6 : hdb town
   * 7 : conversation buildings
   */

  let parameters = {
    filterType: filterType ? filterType : 0,
  };

  if (filterId) {
    parameters.filterId = filterId;
  }

  return request({
    url: 'listings/search/findMapRegionView/search.srx',
    method: 'POST',
    params: parameters,
  });
};

findMapView = ({
  cdResearchSubTypes,
  searchText,
  selectedAmenitiesIds,
  selectedDistrictIds,
  selectedHdbTownIds,
  searchMode,

  zoomLevel,
  displayMinLat,
  displayMinLng,
  displayMaxLat,
  displayMaxLng,
  ignoreDisplayBounds,
  searchTypeAuto,

  distance,

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
  projectLaunchStatus,
  type,
  isTransacted,
}) => {
  return request({
    // url: "listings/search/findMapViewWithProjectSearch/search.srx",
    url: 'listings/search/findMapView/search.srx',
    method: 'POST',
    params: {
      //other parameters add below
      cdResearchSubTypes,
      searchText,
      selectedAmenitiesIds,
      districtIds: selectedDistrictIds,
      selectedHdbTownIds,
      searchMode,

      zoomLevel,
      displayMinLat,
      displayMinLng,
      displayMaxLat,
      displayMaxLng,
      ignoreDisplayBounds,

      distance: distance || 0.5, //default, required, for amenities
      searchTypeAuto,

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
      projectLaunchStatus,
      type,
      isTransacted,
    },
  });
};

const SearchPropertyService = {
  //constant
  maxPerCount,
  //method
  getAmenitiesForProjectSearch,
  loadFeaturedListings,
  loadFullListingDetails,
  loadFullProjectDetails,
  loadListingsCount,
  loadRecommendedListings,
  searchListing,
  findMapRegionView,
  findMapView,
  getListing,
};

export {SearchPropertyService};
