import { DeviceEventEmitter } from "react-native";

const FCM_NOTIFICATION_LISTENER = "FCMNotificationNewIntent";

let fcmNotificationAndroidListener = null;

export class FCMAndroidNotificationListener {
  static setFCMNotifcationReceivedListener(listener) {
    fcmNotificationAndroidListener = DeviceEventEmitter.addListener(
      FCM_NOTIFICATION_LISTENER,
      notification => listener(notification)
    );
  }

  //Remove Listener
  static clearFCMNotificationReceivedListener() {
    if (fcmNotificationAndroidListener) {
      DeviceEventEmitter.removeAllListeners(FCM_NOTIFICATION_LISTENER);
      fcmNotificationAndroidListener = null;
    }
  }
}
