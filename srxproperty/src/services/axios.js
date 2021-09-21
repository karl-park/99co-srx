import * as axios from "axios";
import { AsyncStorage } from "react-native";
import DeviceInfo from "react-native-device-info";
import { AppConstant } from "../constants";
import { store } from "../store";
import { ObjectUtil, DebugUtil } from "../utils";
import {logoutUser} from "../actions";

var client = axios.create({withCredentials: true});

client.defaults.baseURL = AppConstant.DomainUrl;

client.defaults.timeout = 1000 * 60 * 1;

/**
 * Request Wrapper with default success/error actions
 */

const request = async function(options) {
  
  let domain = DebugUtil.retrieveStoreDomainURL();
  if (!ObjectUtil.isEmpty(domain)) {
    client.defaults.baseURL = domain;
  }
  var encryptedUserId, username;
  const userPO = store.getState().loginData.userPO;
  if (!ObjectUtil.isEmpty(userPO)) {
    encryptedUserId = userPO.encryptedUserId;
    username = userPO.email;
  }

  //seet common parameters 1st
  client.defaults.params = {
    //device information
    uid: DeviceInfo.getUniqueId(),
    deviceModel: DeviceInfo.getModel(),
    deviceType: DeviceInfo.getSystemName(),
    osversion: DeviceInfo.getSystemVersion(),

    //app information
    appName: "Consumer",
    version: DeviceInfo.getVersion(),

    //user information
    encryptedUserId: encryptedUserId,
    username: username
  };

  //set custom headers
  client.defaults.headers["srx-app-name"] = "Consumer";
  client.defaults.headers["Accept-Encoding"] = "gzip";

  if (__DEV__) {
    console.log("options - ");
    console.log(options);
    console.log(client.defaults.params);
  }
  const onSuccess = function(response) {
    console.debug("Request Successful!", response);
    if (__DEV__) {
      console.log(response.data);
    }
    return response.data;
  };

  const onError = function(error) {
    console.error("Request Failed:", error.config);
    console.log("Request log:");
    console.log(error.message);

    if (error.response) {
      // Request was made but server responded with something
      // other than 2xx
      console.log(error.response.data);
      console.error("Status:", error.response.status);
      console.error("Data:", error.response.data);
      console.error("Headers:", error.response.headers);

      if (
        !ObjectUtil.isEmpty(error.response.data) &&
        error.response.data.errorCode === 'NoLoggedInUser'
      ) {
        store.dispatch(logoutUser());
      }
    } else {
      // Something else happened while setting up the request
      // triggered the error
      console.error("Error Message:", error.message);
    }

    return Promise.reject(error.response || error.message);
  };

  return client(options)
    .then(onSuccess)
    .catch(onError);
};

export const multiPartRequest = async function(url, params) {
  var encryptedUserId, username;
  const userPO = store.getState().loginData.userPO;
  if (!ObjectUtil.isEmpty(userPO)) {
    encryptedUserId = userPO.encryptedUserId;
    username = userPO.email;
  }

  const data = new FormData();
  //device information
  data.append("uid", DeviceInfo.getUniqueId());
  data.append("deviceModel", DeviceInfo.getModel());
  data.append("deviceType", DeviceInfo.getSystemName());
  data.append("osversion", DeviceInfo.getSystemVersion());

  //app information
  data.append("appName", "Consumer");
  data.append("version", DeviceInfo.getVersion());

  //user information
  data.append("encryptedUserId", encryptedUserId);
  data.append("username", username);

  //params convert & append to formdata
  for (var key in params) {
    const value = params[key];
    if (value) {
      data.append(key, value);
    }
  }

  if (__DEV__) {
    console.log("options multipart - ");
    console.log(data);
  }
  const onSuccess = function(response) {
    console.log("on success");
    console.debug("Request Successful!", response);
    if (__DEV__) {
      console.log(response);
    }
    return response;
  };

  const onError = function(error) {
    console.log("on fail");
    console.error("Request Failed:", error.config);
    console.log("Request log:");
    console.log(error.response);

    if (error.response) {
      // Request was made but server responded with something
      // other than 2xx
      console.error("Status:", error.response.status);
      console.error("Data:", error.response.data);
      console.error("Headers:", error.response.headers);
    } else {
      // Something else happened while setting up the request
      // triggered the error
      console.error("Error Message:", error.message);
    }

    return Promise.reject(error.response || error.message);
  };
  let domain = DebugUtil.retrieveStoreDomainURL();
  if (ObjectUtil.isEmpty(domain)) {
    domain = AppConstant.DomainUrl;
  }
  return fetch(domain + url, {
    method: "post",
    headers: { "srx-app-name": "Consumer", "Accept-Encoding": "gzip"},
    body: data
  })
    .then(response => response.json())
    .then(responseJson => onSuccess(responseJson))
    .catch(onError);
};

export default request;

// export { request, multiPartRequest };
