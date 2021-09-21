import { TabBarIcon_HomeStack } from "../assets";
import { IS_IOS } from "../constants";
import { AppTheme } from "../styles";
import DeviceInfo from "react-native-device-info";

const systemVersion = DeviceInfo.getSystemVersion();

const PropertySearchStack = () => ({
  stack: {
    id: "PropertySearchTab",
    children: [
      {
        component: {
          id: "HomeScreenId",
          name: "PropertySearchStack.homeScreen"
        }
      }
    ],
    options: {
      modalPresentationStyle: "overFullScreen",
      bottomTab: {
        // text: "Search",
        icon: TabBarIcon_HomeStack,
        textColor: AppTheme.bottomTabTextColor,
        selectedTextColor: AppTheme.bottomTabSelectedTextColor,
        iconColor: AppTheme.bottomTabIconColor,
        selectedIconColor: AppTheme.bottomTabSelectedIconColor,
        iconInsets:
          IS_IOS && systemVersion >= 11
            ? { top: 0, left: 0, bottom: -8, right: 0 }
            : { top: 0, left: 0, bottom: 0, right: 0 }
      }
    }
  }
});

export { PropertySearchStack };
