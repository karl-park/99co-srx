import DeviceInfo from "react-native-device-info";
import { IS_IOS, IS_IPHONE_X } from "../constants";
import { AppTheme } from "./AppTheme";

const IOS_11_And_Above = IS_IOS && DeviceInfo.getSystemVersion() >= 11;

const TopBarConstants = {
  statusBarHeight: IS_IOS ? (IS_IPHONE_X ? 44 : 20) : 0,
  topBarHeight: IS_IOS ? 44 : 56
};

//status bar while topbar is invisible
const TopBarStyle = {
  statusBar: {
    height: TopBarConstants.statusBarHeight,
    backgroundColor: AppTheme.topBarBackgroundColor
  },
  topBar: {
    height: TopBarConstants.topBarHeight,
    backgroundColor: AppTheme.topBarBackgroundColor,
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 8,
    // paddingLeft: IS_IOS ? (IOS_11_And_Above ? 15 : 29) : 10
  },
  topBarItem: { height: 40, width: 40 },
  topBarItemIcon: IS_IOS
    ? IOS_11_And_Above
      ? { height: 31, width: 31 }
      : { height: 25, width: 25 }
    : { height: 37, width: 37 }
};

export { TopBarStyle, TopBarConstants };
