import { TabBarIcon_Menu } from "../assets";
import { IS_IOS } from "../constants";
import { AppTheme } from "../styles";
import DeviceInfo from "react-native-device-info";

const systemVersion = DeviceInfo.getSystemVersion();

const MenuStack = () => ({
  stack: {
    id: "MenuTab",
    children: [
      {
        component: {
          name: "MenuList.Main"
        }
      }
    ],
    options: {
      modalPresentationStyle: "overFullScreen",
      bottomTab: {
        // text: "Menu",
        icon: TabBarIcon_Menu, //need to be replaced with new icon
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

export { MenuStack };
