import React, { Component } from "react";
import { View, TouchableOpacity, Platform } from "react-native";
import PropTypes from "prop-types";
import {
  ExtraSmallBodyText,
  FoundationIcon,
  FeatherIcon
} from "../../../../components";
import { SRXColor } from "../../../../constants";
import { SearchType } from "../PropertySearchConstant";

isIOS = Platform.OS === "ios";

class FloatingShortcutItem extends Component {
  //prop types
  static propTypes = {
    searchType: PropTypes.string,
    onToggleShortcutViews: PropTypes.func
  };

  //default value for props
  static defaultProps = {
    searchType: SearchType.List //default
  };

  //onPress
  onPressEachShortcutItem = selectedSearchType => {
    const { onToggleSearchTypeViews } = this.props;
    if (onToggleSearchTypeViews) {
      onToggleSearchTypeViews(selectedSearchType);
    }
  };

  //ListView
  renderListViewShortcut() {
    return (
      <TouchableOpacity
        style={Styles.itemContainer}
        onPress={() => this.onPressEachShortcutItem(SearchType.List)}
      >
        <FoundationIcon name="list" size={16} color={SRXColor.White} />
        <ExtraSmallBodyText style={{ color: SRXColor.White }}>
          List view
        </ExtraSmallBodyText>
      </TouchableOpacity>
    );
  }

  //GridView
  renderGridViewShortcut() {
    return (
      <TouchableOpacity
        style={Styles.itemContainer}
        onPress={() => this.onPressEachShortcutItem(SearchType.Grid)}
      >
        <FeatherIcon name="grid" size={16} color={SRXColor.White} />
        <ExtraSmallBodyText style={{ color: SRXColor.White }}>
          Grid view
        </ExtraSmallBodyText>
      </TouchableOpacity>
    );
  }

  //MapView
  renderMapViewShortcut() {
    return (
      <TouchableOpacity
        style={Styles.itemContainer}
        onPress={() => this.onPressEachShortcutItem(SearchType.Map)}
      >
        <FeatherIcon name="map-pin" size={16} color={SRXColor.White} />
        <ExtraSmallBodyText style={{ color: SRXColor.White }}>
          Map view
        </ExtraSmallBodyText>
      </TouchableOpacity>
    );
  }

  renderSeparator() {
    return <View style={{ width: 1, backgroundColor: "#696969" }} />;
  }

  renderContent() {
    const { searchType } = this.props;
    if (searchType === SearchType.Map) {
      //show list and grid
      return (
        <View style={Styles.container}>
          {this.renderListViewShortcut()}
          {this.renderSeparator()}
          {this.renderGridViewShortcut()}
        </View>
      );
    } else if (searchType === SearchType.List) {
      //show grid and map
      return (
        <View style={Styles.container}>
          {this.renderGridViewShortcut()}
          {this.renderSeparator()}
          {this.renderMapViewShortcut()}
        </View>
      );
    } else if (searchType === SearchType.Grid) {
      //show list and map
      return (
        <View style={Styles.container}>
          {this.renderListViewShortcut()}
          {this.renderSeparator()}
          {this.renderMapViewShortcut()}
        </View>
      );
    }
  }

  render() {
    return this.renderContent();
  }
}

//style for floating shortcut
const Styles = {
  container: {
    position: "absolute",
    width: 140,
    height: 44,
    borderBottomLeftRadius: 10,
    borderTopLeftRadius: 10,
    right: 0,
    bottom: 100,
    backgroundColor: SRXColor.Gray,
    flexDirection: "row"
  },
  itemContainer: {
    flex: 1,
    backgroundColor: SRXColor.Transparent,
    alignItems: "center",
    justifyContent: "center"
  }
};

export { FloatingShortcutItem };
