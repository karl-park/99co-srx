import { TabBarIcon_Shortlist } from "../assets";
import { IS_IOS } from "../constants";
import { AppTheme } from "../styles";
import DeviceInfo from "react-native-device-info";

const systemVersion = DeviceInfo.getSystemVersion();

const ShortlistStack = () => ({
  stack: {
    id: "ShortlistTab",
    children: [
      {
        component: {
          id: "ShortlistedId",
          name: "ShortlistStack.shortlistedPage"
        }
      }
    ],
    options: {
      modalPresentationStyle: "overFullScreen",
      bottomTab: {
        // text: "Shortlist",
        icon: TabBarIcon_Shortlist,
        textColor: AppTheme.bottomTabTextColor,
        selectedTextColor: AppTheme.bottomTabSelectedTextColor,
        iconColor: AppTheme.bottomTabIconColor,
        selectedIconColor: AppTheme.bottomTabSelectedIconColor,
        iconInsets:
          IS_IOS && systemVersion >= 11
            ? { top: 0, left: 0, bottom: -8, right: 0 }
            : { top: 0, left: 0, bottom: 0, right: 0 }
      },
      topBar: {
        title: {
          text: "Shortlist"
        }
      }
    }
  }
});

export { ShortlistStack };
