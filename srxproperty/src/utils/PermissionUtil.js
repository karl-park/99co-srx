//This Util file is for android permissions.
import { PermissionsAndroid } from "react-native";

//Access Fine Location Permission
requestAndroidAccessFineLocation = () => {
  return new Promise(function(resolve, reject) {
    try {
      PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION
      ).then(granted => {
        resolve(granted);
      });
    } catch (err) {
      reject(err);
    }
  });
};

//Call Phone Permission
requestAndroidCallPermission = () => {
  return new Promise(function(resolve, reject) {
    try {
      PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.CALL_PHONE
      ).then(granted => {
        resolve(granted);
      });
    } catch (err) {
      reject(err);
    }
  });
};

// requestAndroidSMSPermission = () => {
//   return new Promise(function(resolve, reject) {
//     try {
//       PermissionsAndroid.request(PermissionsAndroid.PERMISSIONS.SEND_SMS).then(
//         granted => {
//           resolve(granted);
//         }
//       );
//     } catch (err) {
//       reject(err);
//     }
//   });
// };

//Receive SMS permission
// requestAndroidReceiveSMSPermission = () => {
//   return new Promise(function(resolve, reject) {
//     try {
//       PermissionsAndroid.request(
//         PermissionsAndroid.PERMISSIONS.RECEIVE_SMS
//       ).then(granted => {
//         resolve(granted);
//       });
//     } catch (err) {
//       reject(err);
//     }
//   });
// };

const PermissionUtil = {
  requestAndroidAccessFineLocation,
  requestAndroidCallPermission
  // requestAndroidSMSPermission,
  // requestAndroidReceiveSMSPermission
};

export { PermissionUtil };
