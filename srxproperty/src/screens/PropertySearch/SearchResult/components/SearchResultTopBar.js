import React, { Component } from "react";
import {
  View,
  TouchableOpacity,
  Dimensions,
  Platform,
  Image
} from "react-native";
import PropTypes from "prop-types";
import {
  Button,
  Ionicons,
  SmallBodyText,
  FeatherIcon
} from "../../../../components";
import { SRXColor } from "../../../../constants";
import { TopBarStyle, Spacing, InputStyles } from "../../../../styles";
import { ObjectUtil } from "../../../../utils";
import { PropertySearchLocationStack } from "../../Location";
import { Styles } from "../Styles";
import { AppTopBar_BackBtn } from "../../../../assets";
import { SearchType } from "../PropertySearchConstant";

import { Navigation } from "react-native-navigation";

var { height, width } = Dimensions.get("window");

const isIOS = Platform.OS === "ios";

class SearchResultTopBar extends Component {
  static defaultProps = {
    initialLocation: null
  };

  constructor(props) {
    super(props);

    this.onListSearchPressed = this.onListSearchPressed.bind(this);
    this.onMapSearchPressed = this.onMapSearchPressed.bind(this);

    const { initialLocation } = props;
    console.log(initialLocation);
    this.state = {
      searchOptions: initialLocation,
      searchType: SearchType.List,
      location: initialLocation
    };
  }

  componentDidMount() {
    this.props.passRef(this);
  }

  onListSearchPressed() {
    const { onSearchTypeSelected } = this.props;
    this.setState({ searchType: SearchType.List }, () => {
      if (onSearchTypeSelected) onSearchTypeSelected(SearchType.List);
    });
  }

  onMapSearchPressed() {
    const { onSearchTypeSelected } = this.props;
    this.setState({ searchType: SearchType.Map }, () => {
      if (onSearchTypeSelected) onSearchTypeSelected(SearchType.Map);
    });
  }

  updateSearchBar = searchOptions => {
    this.setState({
      searchOptions: searchOptions
    });
  };

  onSearchLocation = () => {
    const { location, searchOptions } = this.state;
    Navigation.showModal(
      PropertySearchLocationStack({
        onLocationSelected: this.onLocationUpdated,
        location: location,
        searchOptions: searchOptions
      })
    );
  };

  onLocationUpdated = (addressData, type) => {
    const { onLocationSelected } = this.props;
    if (onLocationSelected) {
      onLocationSelected(addressData, type);
    }

    this.setState({
      location: addressData
    });
  };

  renderSearchMethodButton() {
    if (this.state.searchType === SearchType.Map) {
      return (
        <Button
          leftView={
            <FeatherIcon
              name={"list"}
              size={24}
              style={{ marginRight: Spacing.XS / 2 }}
              color={SRXColor.Teal}
            />
          }
          onPress={this.onListSearchPressed}
        >
          List
        </Button>
      );
    } else {
      return (
        <Button
          leftView={
            <FeatherIcon
              name={"globe"}
              size={24}
              style={{ marginRight: Spacing.XS / 2 }}
              color={SRXColor.Teal}
            />
          }
          onPress={this.onMapSearchPressed}
        >
          Map
        </Button>
      );
    }
  }

  render() {
    const { onBackPressed } = this.props;
    const { location } = this.state;
    console.log("SearchResultTopBar -- refnder");
    console.log(location);
    return (
      <View
        style={[
          TopBarStyle.topBar,
          {
            height: 50,
            width: width - 2 * Spacing.XS,
            paddingLeft: 0
          }
        ]}
      >
        {/* Back Button */}
        <Button
          buttonStyle={Styles.topBarBackButton}
          leftView={<Image source={AppTopBar_BackBtn} resizeMode={"contain"} />}
          onPress={onBackPressed}
        />
        <View
          style={[
            InputStyles.singleLineTextHeight,
            {
              flex: 1,
              borderRadius: isIOS ? 20 : 22.5,
              //color
              backgroundColor: SRXColor.Purple,
              color: SRXColor.White,
              //margin
              marginLeft: Spacing.XS / 2,
              //padding
              paddingRight: Spacing.S,
              paddingLeft: 20
            }
          ]}
        >
          {/* Search Text Display */}
          <TouchableOpacity
            style={Styles.topBarTextContainerStyle}
            onPress={this.onSearchLocation}
          >
            <SmallBodyText style={{ color: SRXColor.White }} numberOfLines={1}>
              {!ObjectUtil.isEmpty(location) ? location.displayText : ""}
            </SmallBodyText>
          </TouchableOpacity>

          {/* By new design requirement, hide List and Map buttons */}
          {/* {this.renderSearchMethodButton()} */}
        </View>
      </View>
    );
  }
}

SearchResultTopBar.propTypes = {
  /**
   * on type selected (List or Map), send back the selected type
   */
  onSearchTypeSelected: PropTypes.func,

  /**
   * to return new selected location
   */
  onLocationSelected: PropTypes.func,

  /**
   * initial location
   */
  initialLocation: PropTypes.object,

  /**
   * action while back is pressed
   */
  onBackPressed: PropTypes.func
};

export { SearchResultTopBar };
