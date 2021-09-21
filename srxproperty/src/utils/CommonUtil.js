import {Linking, Platform, Alert} from 'react-native';
import Geolocation from '@react-native-community/geolocation';

import {ObjectUtil} from './ObjectUtil';
import {DebugUtil} from './DebugUtil';
import {NumberUtil} from './NumberUtil';
import {IS_IOS} from '../constants';

function getUserLocation(callback, onError) {
  Geolocation.getCurrentPosition(
    position => {
      const latLngCoord = {
        latitude: position.coords.latitude,
        longitude: position.coords.longitude,
      };

      if (callback) {
        callback({coordinate: latLngCoord});
      }
    },
    error => {
      console.log(error);
      if (
        error.message &&
        error.message === 'User denied access to location services.'
      ) {
        if (Platform.OS === 'ios') {
          const title = 'Permission Denied';
          const message =
            "This function required to access your location.\nTo allow, please visit Settings App, and allow 'Location' for SRX Property.";
          CommonUtil.directToiOSSettings(title, message);
        }
      }
      if (onError) {
        onError(onError);
      }
    },
    {enableHighAccuracy: true, timeout: 20000, maximumAge: 5000},
    //show error with message: unable to retrieve location
    //if possible, detect if permission given, and send to settings if permission denied
  );
}

/**
 * if not passing title, will directly directed to
 */
directToiOSSettings = (title, message) => {
  const url = 'app-settings:';
  Linking.canOpenURL(url)
    .then(supported => {
      if (!supported) {
        console.log("Can't handle url: " + url);
      } else {
        if (title) {
          //show alert to ask user to take action
          Alert.alert(
            title,
            message,
            [
              {
                text: 'Cancel',
                onPress: () => console.log('Cancel Pressed'),
                style: 'cancel',
              },
              {
                text: 'Settings',
                onPress: () => Linking.openURL(url),
              },
            ],
            {cancelable: false},
          );
        } else {
          Linking.openURL(url);
        }
      }
    })
    .catch(err => console.error('An error occurred', err));
};

directToAndroidSettings = () => {
  NativeModules.OpenSettings.openNetworkSettings(supported => {
    console.log('is able to open android settings  ' + supported);
  });
};

function handleImageUrl(urlString) {
  if (!ObjectUtil.isEmpty(urlString)) {
    if (urlString.startsWith('/')) {
      const domainURL = DebugUtil.retrieveStoreDomainURL();
      return decodeURI(domainURL + urlString);
    }
  }
  return decodeURI(urlString);
}

/*
    Size conversion
*/

function convertFeetToMetre(sizeInSqft) {
  const size = NumberUtil.floatValue(sizeInSqft);

  if (size > 0) {
    return Math.round(size / 10.76391);
  }

  return 0;
}

function convertMetreToFeet(sizeInSqm) {
  const size = NumberUtil.floatValue(sizeInSqm);

  if (size > 0) {
    return Math.round(size * 10.76391);
  }

  return 0;
}

/*
    Time conversion
*/
function convertMilisecondsToMinutesSecondsFormat(miliseconds) {
  var totalSeconds = miliseconds / 1000;
  var minutes = Math.floor(totalSeconds / 60);
  var seconds =
    Math.floor(totalSeconds % 60) >= 10
      ? Math.floor(totalSeconds % 60)
      : '0' + Math.floor(totalSeconds % 60);

  return minutes + ':' + seconds;
}

function getErrorMessageFromSRXResponse(response) {
  return response.error || response.Error || null;
}

/**
 * get distance with 2 lat lng
 *
 * location: {latitude, longitude}
 */
function getDistance({location1, location2}) {
  const RADIAN_PER_DEGREE = Math.PI / 180;
  const EARTH_RADIUS = 6378.1370072880936362;

  const lat1 = location1.latitude;
  const lng1 = location1.longitude;

  const lat2 = location2.latitude;
  const lng2 = location2.longitude;

  if (lat1 == lat2 && lng1 == lng2) {
    return 0;
  } else {
    return (
      Math.acos(
        Math.sin(lat1 * RADIAN_PER_DEGREE) *
          Math.sin(lat2 * RADIAN_PER_DEGREE) +
          Math.cos(lat1 * RADIAN_PER_DEGREE) *
            Math.cos(lat2 * RADIAN_PER_DEGREE) *
            Math.cos(lng1 * RADIAN_PER_DEGREE - lng2 * RADIAN_PER_DEGREE),
      ) * EARTH_RADIUS
    );
  }
}

function dialPhoneNumber({phoneNumber}) {
  var url = 'tel:' + phoneNumber;
  Linking.canOpenURL(url)
    .then(supported => {
      if (!supported) {
        console.log('An error occured while dialing phone number' + url);
      } else {
        return Linking.openURL(url);
      }
    })
    .catch(error =>
      console.error('An error occured while dialing phone number', error),
    );
}

function openWhatsApp({phoneNumber, message}) {
  var url =
    'https://api.whatsapp.com/send?phone=65' + phoneNumber + '&text=' + message;
  Linking.canOpenURL(url)
    .then(supported => {
      if (!supported) {
        console.log('An error occured in opening WhatsApp');
      } else {
        return Linking.openURL(url);
      }
    })
    .catch(error => {
      console.log('An error occured in opening WhatsApp', error);
    });
}

function openSMS({phoneNumber, message}) {
  var url = 'sms:' + phoneNumber;
  if (IS_IOS) {
    url += '&body=' + message;
  } else {
    url += '?body=' + message;
  }
  Linking.canOpenURL(url)
    .then(supported => {
      if (!supported) {
        console.log('An error occured in opening sms' + url);
      } else {
        return Linking.openURL(url);
      }
    })
    .catch(err => console.error('An error occured in opening sms ', err));
}

const CommonUtil = {
  getUserLocation,
  directToiOSSettings,
  directToAndroidSettings,
  handleImageUrl,
  convertFeetToMetre,
  convertMetreToFeet,
  convertMilisecondsToMinutesSecondsFormat,
  dialPhoneNumber,
  getErrorMessageFromSRXResponse,
  getDistance,
  openWhatsApp,
  openSMS,
};

export {CommonUtil};
