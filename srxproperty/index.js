/** @format */

import { Navigation } from "react-native-navigation";
import { bugsnag } from "./src/Bugsnag";
import DeviceInfo from "react-native-device-info";
import { AppTopBar_BackBtn } from "./src/assets";
import { MainScreenTab, ComponentRegisterHandler } from "./src/config";
import { SRXColor, IS_IOS } from "./src/constants";
import { AppTheme } from "./src/styles";
import firebase from "@react-native-firebase/app";
import '@react-native-firebase/analytics';

const systemVersion = DeviceInfo.getSystemVersion();

firebase.analytics().setAnalyticsCollectionEnabled(true);

ComponentRegisterHandler.registerComponents();

Navigation.events().registerAppLaunchedListener(() => {
  Navigation.setDefaultOptions({
    statusBar: {
      visible: true,
      style: IS_IOS ? "dark" : "ANDROID_COLOR_CODE",
      hideWithTopBar: false
    },
    bottomTabs: {
      backgroundColor: AppTheme.bottomTabsBackgroundColor,
      titleDisplayMode: "alwaysHide",
    },
    // bottomTab: {
    //   iconInsets:
    //     IS_IOS && systemVersion >= 11
    //       ? { top: 0, left: 0, bottom: -8, right: 0 }
    //       : { top: 0, left: 0, bottom: 0, right: 0 }
    // },
    topBar: {
      backButton: {
        icon: AppTopBar_BackBtn,
        color: AppTheme.topBarBackButtonColor,
        title: ""
      },
      /**
       * For left right button,
       * you should try to keep with the color define here
       * If the color is reqiured to changed according to mockup
       * Use
       * SRXColor.LeftButtonColor or RightButtonColor
       *
       * unless there is special cases. E.g. FullScreenImagePreview.js
       */
      leftButtonColor: AppTheme.topBarLeftButtonColor,
      rightButtonColor: AppTheme.topBarRightButtonColor,
      background: {
        color: AppTheme.topBarBackgroundColor
      },
      title: {
        color: AppTheme.topBarTitleColor
      },
      subtitle: {
        color: AppTheme.topBarSubtitleColor
      }
    },
    layout: {
      orientation: ["portrait"],
      backgroundColor: AppTheme.layoutBackgroundColor
    },
    sideMenu: {
      right: {
        enabled: false,
        visible: false,
        shouldStretchDrawer: false
      }
    }
  });

  Navigation.setRoot({
    root: MainScreenTab()
  });
});
