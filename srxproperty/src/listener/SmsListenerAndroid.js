import { NativeModules, DeviceEventEmitter } from "react-native";

let smsReceivedListener;

const SMS_LISTENER_EVENT_NAME = "SmsListener";

export class SmsListenerAndroid {
  //For SMS Listener for android
  static setSmsReceivedListener(listener) {
    smsReceivedListener = DeviceEventEmitter.addListener(
      SMS_LISTENER_EVENT_NAME,
      sms => listener(sms)
    );
  }

  //Remove Listener
  static clearSmsReceivedListener() {
    if (smsReceivedListener) {
      DeviceEventEmitter.removeAllListeners(SMS_LISTENER_EVENT_NAME);
      // smsReceivedListener.remove;
      smsReceivedListener = null;
    }
  }
}
