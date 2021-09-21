import request from "./axios";

//Auto Complete for property lists
//to be rename
function searchWithWalkup({ query, limit }) {
  return request({
    url: "/common/sveMobile.agent?action=searchWithWalkup",
    method: "GET",
    params: {
      query,
      limit
    }
  });
}

function findPostalCodeForCurrentLocation({ latitude, longitude }) {
  return request({
    url: "/mobile/cobroke/postCobrokeListing2.cobroke",
    method: "GET",
    params: {
      action: "findPostalCodeForCurrentLocation",
      latitude,
      longitude
    }
  });
}

//currently for listing search
function getSuggestionSearchResult({ query, source }) {
  return request({
    url: "/common/autolocation.suggestions",
    method: "GET",
    params: {
      query,
      source: source ? source : "listingSearch"
    }
  });
}

//Get Address By Postal Codes
function getAddressByPostalCode({ postalCode, skipCommercial }) {
  return request({
    url: "/srx/listing/getAddressByPostalCode/redefinedSearch.srx",
    method: "GET",
    params: {
      action: "getAddressByPostalCode",
      postalCode,
      skipCommercial
    }
  });
}

function getProject({ postal, type, floor, unit, typeOfArea, buildingNum }) {
  return request({
    url: "/mobile/iphone/consumer2.mobile3",
    method: "GET",
    params: {
      action: "getProject",
      postal,
      type,
      floor,
      unit,
      typeOfArea,
      buildingNum
    }
  });
}

const AddressDetailService = {
  searchWithWalkup,
  findPostalCodeForCurrentLocation,
  getSuggestionSearchResult,
  getAddressByPostalCode,
  getProject
};

export { AddressDetailService };
