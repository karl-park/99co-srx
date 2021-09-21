import { ObjectUtil } from "./ObjectUtil";
import { AppConstant, ConstantList } from "../constants";
import { AsyncStorage } from "react-native";
import { store } from "../store";

function getChosenAppDomainURL() {
  return new Promise(function(resolve, reject) {
    AsyncStorage.getItem("@AppDomain", (err, result) => {
      if (err) {
        reject(err);
      } else {
        if (!ObjectUtil.isEmpty(result)) {
          resolve(result);
        } else {
          resolve(AppConstant.DomainUrl);
        }
      }
    });
  });
}

function retrieveStoreDomainURL() {
  const domainURL = store.getState().serverDomain.domainURL;
  return domainURL;
}

function setChosenAppDomainURL({ newDomainUrl }) {
  return new Promise(function(resolve, reject) {
    AsyncStorage.setItem("@AppDomain", newDomainUrl, err => {
      if (err) {
        reject(err);
      } else {
        resolve(newDomainUrl);
      }
    });
  });
}

async function removeChosenAppDomainURL() {
  return new Promise(function(resolve, reject) {
    AsyncStorage.removeItem("@AppDomain", (err, result) => {
      if (err) {
        reject(err);
      } else {
        resolve(result);
      }
    });
  });
}

const DebugUtil = {
  getChosenAppDomainURL,
  removeChosenAppDomainURL,
  retrieveStoreDomainURL,
  setChosenAppDomainURL
};

export { DebugUtil };
