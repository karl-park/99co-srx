import { Platform } from "react-native";
import { AppConstant } from "../../../constants";
import { PropertyTypeUtil, DebugUtil, ObjectUtil } from "../../../utils";
import { store } from "../../../store";

let SRX_URL = AppConstant.DomainUrl;
// const SRX_URL = "https://www.srx.com.sg";

const SRX_Agency = 0;

function promotionGetXValueTrend({
  type,
  postalCode,
  floorNum,
  unitNum,
  buildingNum,
  subType,
  landType,
  streetId,
  size,
  builtSizeGfa,
  pesSize,
  tenure,
  builtYear
}) {
  let domain = DebugUtil.retrieveStoreDomainURL();
  if (!ObjectUtil.isEmpty(domain)) {
    SRX_URL = domain;
  }

  var formData = new FormData();
  formData.append("action", "promotionGetXValueTrend");
  if (type) formData.append("type", type);
  if (postalCode) formData.append("postalCode", postalCode);
  if (floorNum) formData.append("floorNum", floorNum);
  if (unitNum) formData.append("unitNum", unitNum);
  if (buildingNum) formData.append("buildingNum", buildingNum);
  if (subType) formData.append("subType", subType);
  if (subType) formData.append("landType", landType);
  if (streetId) formData.append("id", streetId);
  if (size) formData.append("size", size);
  if (builtSizeGfa) formData.append("builtSizeGfa", builtSizeGfa);
  if (pesSize) formData.append("pesSize", pesSize);
  if (tenure) formData.append("tenure", tenure);
  if (builtYear) formData.append("builtYear", builtYear);

  if (subType)
    formData.append(
      "landedXValue",
      PropertyTypeUtil.isLanded(subType) ? "Y" : "N"
    );
  formData.append("cdApp", "1");
  formData.append("builtYear", Platform.OS == "ios" ? "0" : "1"); //0 = ios, 1 = android

  console.log(formData);

  return fetch(
    SRX_URL + "/srx/listings/promotionGetXValueTrend/redefinedSearch.srx",
    {
      method: "POST",
      body: formData
    }
  )
    .then(response => {
      if (response.status >= 200 && response.status < 300) {
        // return Promise.resolve(response);
        return response.json();
      } else {
        return Promise.reject(new Error(response.statusText));
      }
    })
    .catch(error => {
      console.log(error);
      return error;
    });
}

function loadXValueTrendTransaction({
  postalCode,
  cdResearchSubtype,
  unitFloor,
  unitNo,
  sizeInSqm,
  isSale
}) {
  let domain = DebugUtil.retrieveStoreDomainURL();
  if (!ObjectUtil.isEmpty(domain)) {
    SRX_URL = domain;
  }
  var formData = new FormData();
  formData.append("action", "loadXValueTrend");
  formData.append("cdAgency", SRX_Agency);
  formData.append("postalCode", postalCode);
  formData.append("cdResearchSubtype", cdResearchSubtype);
  formData.append("unitFloor", unitFloor);
  formData.append("unitNo", unitNo);
  formData.append("size", sizeInSqm);
  formData.append("saleOrRent", isSale ? "S" : "R");

  console.log(formData);

  return fetch(SRX_URL + "/mobile/iphone/consumer.mobile3?", {
    method: "POST",
    body: formData
  })
    .then(response => {
      if (response.status >= 200 && response.status < 300) {
        // return Promise.resolve(response);
        return response.json();
      } else {
        return Promise.reject(new Error(response.statusText));
      }
    })
    .catch(error => {
      console.log(error);
      return error;
    });
}

function promotionGetXValueTrendForListing({
  listingType, //A
  listingId
}) {
  let domain = DebugUtil.retrieveStoreDomainURL();
  if (!ObjectUtil.isEmpty(domain)) {
    SRX_URL = domain;
  }
  var formData = new FormData();
  formData.append("listingType", "A");
  formData.append("id", listingId);
  formData.append("cdApp", "1");

  console.log(formData);

  return fetch(SRX_URL + "/srx/listings/loadXValueTrend/redefinedSearch.srx", {
    method: "POST",
    body: formData
  })
    .then(response => {
      if (response.status >= 200 && response.status < 300) {
        // return Promise.resolve(response);
        return response.json();
      } else {
        return Promise.reject(new Error(response.statusText));
      }
    })
    .catch(error => {
      console.log(error);
      return error;
    });
}

function loadXValueTrendTransactionForListing({
  postalCode,
  cdResearchSubtype,
  listingId,
  listingType,
  isSale
}) {
  let domain = DebugUtil.retrieveStoreDomainURL();
  if (!ObjectUtil.isEmpty(domain)) {
    SRX_URL = domain;
  }
  var formData = new FormData();
  formData.append("action", "loadXValueTrend");
  formData.append("cdAgency", SRX_Agency);
  formData.append("postalCode", postalCode);
  formData.append("cdResearchSubtype", cdResearchSubtype);
  formData.append("listingId", listingId);
  formData.append("listingType", listingType);
  formData.append("saleOrRent", isSale ? "S" : "R");

  console.log(formData);

  return fetch(SRX_URL + "/mobile/iphone/consumer.mobile3?", {
    method: "POST",
    body: formData
  })
    .then(response => {
      if (response.status >= 200 && response.status < 300) {
        // return Promise.resolve(response);
        return response.json();
      } else {
        return Promise.reject(new Error(response.statusText));
      }
    })
    .catch(error => {
      console.log(error);
      return error;
    });
}

const XValueTrendService = {
  promotionGetXValueTrend,
  loadXValueTrendTransaction,
  promotionGetXValueTrendForListing,
  loadXValueTrendTransactionForListing
};

export { XValueTrendService };
