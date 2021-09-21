import { TabBarIcon_XValue, TabBarIcon_XValue_Big } from "../assets";
import { IS_IOS } from "../constants";
import DeviceInfo from "react-native-device-info";

const systemVersion = DeviceInfo.getSystemVersion();
const versionArray = systemVersion.split(".");
const XValueStack = () => ({
  stack: {
    id: "XValueTab",
    children: [
      {
        component: {
          id: "XValueFormId",
          name: "xvalueStack.xvalueForm"
        }
      }
    ],
    options: {
      modalPresentationStyle: "overFullScreen",
      bottomTab: {
        // text: "X-Value",
        icon: IS_IOS ? TabBarIcon_XValue_Big : TabBarIcon_XValue,
        selectedIcon: IS_IOS ? TabBarIcon_XValue_Big : TabBarIcon_XValue,
        // disableIconTint: true, //set true if you want to disable the icon tinting
        // disableSelectedIconTint: true,
        iconInsets: IS_IOS
          ? parseInt(versionArray[0])>=13
            ? { top: -8, left: 0, bottom: 8, right: 0 }
            : { top: 0, left: 0, bottom: 0, right: 0 }
          : { top: 0, left: 0, bottom: 0, right: 0 }
      },
      topBar: {
        title: {
          text: "X-Value Calculator"
        }
      }
    }
  }
});

export { XValueStack };
