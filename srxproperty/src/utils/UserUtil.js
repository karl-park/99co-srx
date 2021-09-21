import {AsyncStorage, Alert} from 'react-native';
import {Notifications} from 'react-native-notifications';
import Moment from 'moment';
import DeviceInfo from 'react-native-device-info';
import {LoginService} from '../services';
import {AppConstant} from '../constants';
import {ObjectUtil} from '../utils';

const UserUtil = {
  saveUserEmail,
  saveUserPassword,
  saveUserFBId,
  saveLoginUserPO,
  getDataForLogin,
  clearUserDataWhileLogout,

  updateRecentPropertySearch,
  retrieveRecentPropertySearch,
  checkIOSNotificationStatus,

  updateRecentXValue,
  retrieveRecentXValue,
  removeRecentXValue,

  saveCommunitiesIntroIndicator,
  retrieveCommunitiesIntroIndicator,
};

const KEY_NotificationPermission = '@hasAskedPermissionForNotification';

//save user email
function saveUserEmail(email) {
  if (email && email.length > 0) {
    AsyncStorage.setItem('@email', email, err => {
      if (err) console.log(err);
    });
    return true;
  }
  return false;
}

//save user password
function saveUserPassword(password) {
  if (password && password.length > 0) {
    AsyncStorage.setItem('@password', password, err => {
      if (err) console.log(err);
    });
    return true;
  }
  return false;
}

function saveUserFBId(facebookId) {
  if (facebookId && facebookId.length > 0) {
    AsyncStorage.setItem('@facebookId', facebookId, err => {
      if (err) console.log(err);
    });
    return true;
  }
  return false;
}

//save user PO
function saveLoginUserPO(userPO) {
  if (!ObjectUtil.isEmpty(userPO)) {
    AsyncStorage.setItem('@userPO', JSON.stringify(userPO), err => {
      if (err) console.log(err);
    });
    return true;
  }
  return false;
}

/**
 * Temporary method to grab data for login, will move to redux-persist or keychain/keystore
 * Saving in asynstorage is not safe
 */
function getDataForLogin() {
  return new Promise(function(resolve, reject) {
    AsyncStorage.multiGet(
      ['@email', '@password', '@facebookId', '@userPO', '@AppDomain'],
      (err, stores) => {
        if (err) {
          reject(err);
        } else {
          resolve({
            username: stores[0][1],
            password: stores[1][1],
            facebookId: stores[2][1],
            userPO: JSON.parse(stores[3][1]),
            appdomain: stores[4][1],
          });
        }
      },
    );
  });
}

function clearUserDataWhileLogout() {
  return new Promise(function(resolve, reject) {
    AsyncStorage.multiRemove(
      ['@email', '@password', '@facebookId', '@recentXValue', '@userPO'],
      err => {
        if (err) {
          reject(err);
        } else {
          resolve();
        }
      },
    );
  });
}

function updateRecentPropertySearch({
  cdResearchSubTypes,
  displayText,
  searchText,
  selectedAmenitiesIds,
  selectedDistrictIds,
  selectedHdbTownIds,
  locationType,
  latitude,
  longitude,
  suggestionEntryType,
  postalCode,
}) {
  let newSearch = {
    cdResearchSubTypes,
    displayText,
    searchText,
    selectedAmenitiesIds,
    selectedDistrictIds,
    selectedHdbTownIds,
    locationType,
    latitude,
    longitude,
    suggestionEntryType,
    date: new Date(), //adding date, in case in future more function added and this might be required
    postalCode,
  };
  return new Promise(function(resolve, reject) {
    UserUtil.retrieveRecentPropertySearch().then(result => {
      //create arrays
      let recentSearches = [];
      if (!ObjectUtil.isEmpty(result)) {
        recentSearches = [newSearch, ...result];
      } else {
        recentSearches = [newSearch];
      }

      if (recentSearches.length > 5) {
        recentSearches.length = 5;
      }
      AsyncStorage.setItem(
        '@recentPropertySearches',
        JSON.stringify(recentSearches),
        err => {
          if (err) {
            reject(err);
          } else {
            resolve(recentSearches);
          }
        },
      );
    });
  });
}

function retrieveRecentPropertySearch() {
  return new Promise(function(resolve, reject) {
    AsyncStorage.getItem('@recentPropertySearches', (err, result) => {
      if (err) {
        reject(err);
      } else {
        resolve(JSON.parse(result));
      }
    });
  });
}

function updateRecentXValue({
  address,
  postalCode,
  type,
  floorNum,
  unitNum,
  buildingNum,
  propertyType,
  subType,
  landType,
  size,
  streetId,
  gfa,
  pesSize,
  tenure,
  lastConstructed,
}) {
  let newSearch = {
    date: new Date(), //adding date, in case in future more function added and this might be required
    address,
    postalCode,
    type,
    floorNum,
    unitNum,
    buildingNum,
    propertyType,
    subType,
    landType,
    size,
    streetId,
    gfa,
    pesSize,
    tenure,
    lastConstructed,
  };
  return new Promise(function(resolve, reject) {
    UserUtil.retrieveRecentXValue().then(result => {
      //create arrays
      let recentSearches = [];
      if (!ObjectUtil.isEmpty(result)) {
        recentSearches = [newSearch, ...result];
      } else {
        recentSearches = [newSearch];
      }
      if (recentSearches.length > 5) {
        recentSearches.length = 5;
      }
      AsyncStorage.setItem(
        '@recentXValue',
        JSON.stringify(recentSearches),
        err => {
          if (err) {
            reject(err);
          } else {
            resolve(recentSearches);
          }
        },
      );
    });
  });
}

function retrieveRecentXValue() {
  return new Promise(function(resolve, reject) {
    AsyncStorage.getItem('@recentXValue', (err, result) => {
      if (err) {
        reject(err);
      } else {
        resolve(JSON.parse(result));
      }
    });
  });
}

async function removeRecentXValue() {
  return new Promise(function(resolve, reject) {
    AsyncStorage.removeItem('@recentXValue', (err, result) => {
      if (err) {
        reject(err);
      } else {
        resolve(result);
      }
    });
  });
}

function checkIOSNotificationStatus() {
  Notifications.ios.checkPermissions().then(currentPermissions => {
    if (
      currentPermissions.badge ||
      currentPermissions.sound ||
      currentPermissions.alert
    ) {
      requestIOSNotificationPermission();
      // requestIOSNotificationPermission();
    } else {
      //unregister on server side, in case user turn off in deivce's Settings
      LoginService.unregisterAPNSToken();
    }
  });
  // NotificationsIOS.isRegisteredForRemoteNotifications().then(
  //   hasCheckedPermissions => {
  //     if (!hasCheckedPermissions) {
  //       requestIOSNotificationPermission();
  //       /**
  //        * Disable additional layer of prompt
  //        * once calling NotificationsIOS.requestPermissions(),
  //        * next time when calling this methond
  //        * hasCheckedPermissions will be true
  //        * and never enter this block again
  //        */

  //       /*
  //       AsyncStorage.getItem(KEY_NotificationPermission, (err, result) => {
  //         if (result) {
  //           var lastPromptDate = Moment(result, "MM-DD-YYYY, HH:mm:ss");

  //           var now = Moment();

  //           const diffInMS = now.diff(lastPromptDate);

  //           const diffInDays = diffInMS / 1000 / 60 / 60 / 24 / 30;

  //           if (diffInDays >= 1) {
  //             promptFirstPermissionCheck();
  //           }
  //         } else {
  //           // no result, not set before
  //           promptFirstPermissionCheck();
  //         }
  //       });
  //       */
  //     } else {
  //       NotificationsIOS.checkPermissions().then(currentPermissions => {
  //         if (
  //           currentPermissions.badge ||
  //           currentPermissions.sound ||
  //           currentPermissions.alert
  //         ) {
  //           requestIOSNotificationPermission();
  //         } else {
  //           //unregister on server side, in case user turn off in deivce's Settings
  //           LoginService.unregisterAPNSToken();
  //         }
  //       });
  //     }
  //   }
  // );
}

function requestIOSNotificationPermission() {
  if (!DeviceInfo.isEmulator()) {
    Notifications.ios.requestPermissions();
  }
}

// //private method, shouldn't expose to other files
function promptFirstPermissionCheck() {
  AsyncStorage.setItem(
    KEY_NotificationPermission,
    Moment().format('MM-DD-YYYY, HH:mm:ss'),
  );
  Alert.alert(
    AppConstant.AppName,
    'Would you like to receive your monthly price updates via this app instead of sms?',
    [
      {
        text: 'No, thanks',
        style: 'cancel',
      },
      {
        text: "Yes, that's great",
        onPress: () => {
          requestIOSNotificationPermission();
        },
      },
    ],
    {cancelable: false},
  );
}

//Communities Intro Indicator
function saveCommunitiesIntroIndicator(isIntroShowed) {
  if (isIntroShowed) {
    AsyncStorage.setItem(
      '@communitiesIntroIndicator',
      JSON.stringify(isIntroShowed),
      err => {
        if (err) {
          console.log(err);
        }
      },
    );
    return true;
  }
  return false;
}

//Communities Intro Indicator
function retrieveCommunitiesIntroIndicator() {
  return new Promise(function(resolve, reject) {
    AsyncStorage.getItem('@communitiesIntroIndicator', (err, result) => {
      if (err) {
        reject(err);
      } else {
        resolve(JSON.parse(result));
      }
    });
  });
}

export {UserUtil};
