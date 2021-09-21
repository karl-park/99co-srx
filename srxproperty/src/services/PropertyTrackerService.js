import request from "./axios";
import { store } from "../store";
import { ObjectUtil } from "../utils";

function loadPropertyTrackers({ populateXvalue }) {
  return request({
    url: "/srx/tracker/loadPropertyTrackers/propertyTracker.srx",
    method: "GET",
    params: {
      action: "loadPropertyTrackers",
      // ptUserId : "" ,
      populateXvalue
    }
  });
}

function loadPropertyTrackersById({ ptUserId, populateXvalue }) {
  return request({
    url: "/srx/tracker/loadPropertyTrackers/propertyTracker.srx",
    method: "GET",
    params: {
      action: "loadPropertyTrackers",
      ptUserId,
      populateXvalue
    }
  });
}

//Remove property
function removePropertyUser({ ptUserId }) {
  return request({
    url: "/srx/tracker/removePropertyUser/propertyTracker.srx",
    method: "POST",
    params: {
      action: "removePropertyUser",
      ptUserId
    }
  });
}

function loadListingsNearby({ ptUserId }) {
  return request({
    url: "/srx/tracker/loadListingsNearby/propertyTracker.srx",
    method: "GET",
    params: {
      action: "loadPropertyTrackers",
      ptUserId
    }
  });
}

function loadTransactionsNearby({ ptUserId }) {
  return request({
    url: "/srx/tracker/loadTransactionsNearby/propertyTracker.srx",
    method: "GET",
    params: {
      action: "loadTransactionsNearby",
      ptUserId
    }
  });
}

//Trends
function loadPropertyTrackerXValueTrend({ ptUserId, isSale }) {
  return request({
    url: "/srx/tracker/loadPropertyTrackerXValueTrend/propertyTracker.srx",
    method: "POST",
    params: {
      ptUserId,
      type: isSale ? "S" : "R"
    }
  });
}

//update proeprty user po
function updatePropertyUser({ data }) {
  return request({
    url: "/srx/tracker/updatePropertyUser/propertyTracker.srx",
    method: "POST",
    params: {
      action: "updatePropertyUser",
      data
    }
  });
}

//Save password for mySGHome Sign In
function securePropertyUser({ ptUserId, userId, password, token }) {
  return request({
    url: "/srx/tracker/securePropertyUser/propertyTracker.srx",
    method: "POST",
    params: {
      action: "securePropertyUser",
      ptUserId,
      userId,
      password,
      token
    }
  });
}

signUp = ({
  postalCode,
  buildingNumber,
  cdResearchSubtype,
  mobile,
  size,
  streetId,
  unitFloor,
  unitNo,
  tenure,
  landType,
  builtYear,
  purpose,
  occupancy,
  ownerNric,
  inviteId,
  source
}) => {
  return request({
    url: "/srx/tracker/signup/propertyTracker.srx",
    method: "POST",
    params: {
      //compulsory
      postalCode,
      buildingNumber,
      cdResearchSubtype,
      mobile,
      size,
      streetId,
      //optional
      unitFloor,
      unitNo,
      tenure,
      landType,
      builtYear,
      purpose,
      occupancy,
      ownerNric,
      //optional, for agent invitation
      inviteId,
      source
    }
  });
};

confirmSignup = ({
  ptUserId,
  mobile,
  verificationCode,
  hasAgreedToPDPA,
  inviteId,
  source
}) => {
  const userPO = store.getState().loginData.userPO;
  return request({
    url: "/srx/tracker/confirmSignup/propertyTracker.srx",
    method: "POST",
    params: {
      ptUserId,
      email: userPO.email,
      name: userPO.name,
      mobile: mobile,
      verificationCode,
      hasAgreedToPDPA: hasAgreedToPDPA,
      //optional
      inviteId,
      source
    }
  });
};

function addCapitalGain({ ptUserId, lastTransactedPrice, lastTransactedDate }) {
  return request({
    url: "/srx/tracker/addCapitalGain/propertyTracker.srx",
    method: "POST",
    params: {
      action: "addCapitalGain",
      ptUserId,
      lastTransactedPrice,
      lastTransactedDate
    }
  });
}

function resendVerificationCode({ ptUserId, mobile }) {
  return request({
    url: "/srx/tracker/resendVerificationCode/propertyTracker.srx",
    method: "POST",
    params: {
      ptUserId,
      mobile
    }
  });
}

updatePropertyUserV2 = ({
  srxPropertyUserPO
}) => {
  return request({
    url: "/srx/tracker/updatePropertyUserV2/propertyTracker.srx",
    method: "POST",
    params: {
      data: JSON.stringify(srxPropertyUserPO)
    }
  });
};

function loadPropertyTrackersNearby({ptUserId}){
  return request({
    url: "/srx/tracker/loadPropertyTrackersNearby/propertyTracker.srx",
    method: "POST",
    params: {
      ptUserId,
    }
  })
}


const PropertyTrackerService = {
  loadPropertyTrackers,
  loadPropertyTrackersById,
  removePropertyUser,
  loadListingsNearby,
  loadTransactionsNearby,
  loadPropertyTrackersNearby,
  loadPropertyTrackerXValueTrend,
  securePropertyUser,
  updatePropertyUser,
  signUp,
  confirmSignup,
  addCapitalGain,
  resendVerificationCode,
  updatePropertyUserV2
};

export { PropertyTrackerService };
