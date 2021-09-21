import React, { Component } from "react";
import { Platform } from "react-native";
import { Navigation } from "react-native-navigation";
import { LargeTitleText } from "../../components";
import { Spacing } from "../../styles";
import { ObjectUtil } from "../../utils";

const defaultElevation = 4; //default value copied from wix documentation

const isIOS = Platform.OS === "ios";

class LargeTitleComponent extends Component {
  static options(passProps) {
    return {
      topBar: {
        title: {
          text: ""
        },
        noBorder: true,
        elevation: 0
      }
    };
  }

  /**
   * local variable
   *
   * 1. largeTitleLayout: { x, y, height, width }
   * 2. screenTitle
   * 3. displayingTitle
   * 4. displayingElevation
   */
  largeTitleLayout = { x: 0, y: 0, height: 0, width: 0 };
  screenTitle = "";
  displayingTitle = "";
  displayingElevation = 0;

  onScroll = ({
    nativeEvent: {
      contentOffset: { x, y }
    }
  }) => {
    const topBarUpdates = {};
    //update title
    if (y < this.largeTitleLayout.height + this.largeTitleLayout.y) {
      if (this.displayingTitle !== "") {
        topBarUpdates.title = {
          text: ""
        };
        topBarUpdates.noBorder = true;
        this.displayingTitle = "";
      }
    } else {
      if (this.displayingTitle !== this.screenTitle) {
        topBarUpdates.title = {
          text: this.screenTitle
        };
        topBarUpdates.noBorder = false;
        this.displayingTitle = this.screenTitle;
      }
    }

    //update android's elevation
    if (y > 3) {
      if (this.displayingElevation != defaultElevation) {
        topBarUpdates.elevation = defaultElevation;
        this.displayingElevation = defaultElevation;
      }
    } else {
      if (this.displayingElevation != 0) {
        topBarUpdates.elevation = 0;
        this.displayingElevation = 0;
      }
    }
    if (!ObjectUtil.isEmpty(topBarUpdates)) {
      Navigation.mergeOptions(this.props.componentId, {
        topBar: topBarUpdates
      });
    }
  };

  resetNavigationBarStyle = () => {
    this.onScroll({
      nativeEvent: {
        contentOffset: { x: 0, y: 0 }
      }
    });
  };

  onLayoutLargeTitle = ({
    nativeEvent: {
      layout: { x, y, height, width }
    }
  }) => {
    this.largeTitleLayout = { x, y, height, width };
  };

  renderLargeTitle = (title, style) => {
    this.screenTitle = title || "";
    return (
      <LargeTitleText
        style={[{ marginHorizontal: Spacing.M }, style, { padding: 0 }]}
        onLayout={this.onLayoutLargeTitle}
      >
        {title || ""}
      </LargeTitleText>
    );
  };
}

export { LargeTitleComponent };
