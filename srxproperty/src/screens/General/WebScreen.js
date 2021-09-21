//general web screen
// props passing from navigation:
// 1. screenTitle
// 2. url

import React, { Component } from "react";
import {Linking} from "react-native";
// import { WebView } from "react-native"; //this has been deprecated
import { WebView } from "react-native-webview";
import SafeAreaView from "react-native-safe-area-view";
import { Navigation } from "react-native-navigation";

import { CommonUtil, ObjectUtil } from "../../utils";

class WebScreen extends Component {
  static options(passProps) {
    const { screenTitle } = passProps;
    return {
      //remove bottom bar in webscreen
      bottomTabs: {
        visible: false,
        animate: true,
        drawBehind: true
      },
      topBar: {
        title: {
          text: screenTitle ? screenTitle : ""
        }
      }
    };
  }

  render() {
    const { url } = this.props;

    let urlString = CommonUtil.handleImageUrl(url);

    if (!ObjectUtil.isEmpty(urlString)) {
      if (urlString.includes("?")) {
        urlString += "&showAppBanner=false";
      } else {
        urlString += "?showAppBanner=false";
      }
    }
    return (
      <SafeAreaView style={{ flex: 1 }}>
        <WebView
          source={{ uri: urlString }}
        />
      </SafeAreaView>
    );
  }
}

export { WebScreen };
