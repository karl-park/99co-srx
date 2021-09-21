import request from "./axios";

function findCertifiedListings({ptUserId }) {
  return request({
    url: "/srx/tracker/findCertifiedListings/propertyTracker.srx",
    method: "POST",
    params: {
      ptUserId
    }
  });
}

function certifyListings({listingIds,ptUserId}){
  return request({
    url: "/srx/tracker/certifyListings/propertyTracker.srx",
    method: "POST",
    params: {
      listingIds,
      ptUserId
    }
  });
}

function findListedListings({ptUserId,type}){
  return request({
    url: "/srx/tracker/findListedListings/propertyTracker.srx",
    method: "GET",
    params: {
      ptUserId,
      type
    }
  });
}

const CertifiedListingService = {
  findCertifiedListings,
  certifyListings,
  findListedListings
}

export {CertifiedListingService};