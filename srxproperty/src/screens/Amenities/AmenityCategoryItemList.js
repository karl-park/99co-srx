import React, { Component } from "react";
import {
  View,
  FlatList,
  SafeAreaView,
  ActivityIndicator,
  Dimensions
} from "react-native";
import PropTypes from "prop-types";
import { Separator } from "../../components";
import { AmenityPO } from "../../dataObject";
import { ObjectUtil } from "../../utils";
import { AmenityItem } from "./AmenityItem";
import { AmenityTypes, TravelTypes } from "./constant";
import { SRXColor } from "../../constants";

var { height, width } = Dimensions.get("window");

class AmenityCategoryItemList extends Component {
  state = {
    selectedIndex: -1 //-1 means no select
  };

  constructor(props) {
    super(props);

    this.onItemSelected = this.onItemSelected.bind(this);
    this.renderAmenityItems = this.renderAmenityItems.bind(this);
  }

  onItemSelected({ item, showing }) {
    const { onAmenitySelected, category } = this.props;
    if (!showing) {
      if (onAmenitySelected) {
        onAmenitySelected({ amenity: null, category });
      }
      this.setState({ selectedIndex: -1 });
    } else {
      const { amenities } = this.props;
      const index = amenities.indexOf(item);
      if (index >= 0) {
        if (onAmenitySelected) {
          onAmenitySelected({ amenity: item, category });
        }
        this.setState({ selectedIndex: index });
      }
    }
  }

  renderAmenityItems({ item, index }) {
    const {
      selectedAmenity,
      selectedTravelMethod,
      onTravelModeSelected
    } = this.props;
    return (
      <AmenityItem
        item={item}
        showTravelTime={item == selectedAmenity}
        onTravelTimeToggle={this.onItemSelected}
        travelMethod={selectedTravelMethod}
        onTravelModeSelected={onTravelModeSelected}
      />
    );
  }

  render() {
    const { amenities } = this.props;
    if (Array.isArray(amenities) && !ObjectUtil.isEmpty(amenities)) {
      return (
        <SafeAreaView style={{ flex: 1 }}>
          <FlatList
            style={{ flex: 1 }}
            data={amenities}
            renderItem={this.renderAmenityItems}
            ItemSeparatorComponent={() => <Separator />}
          />
        </SafeAreaView>
      );
    } else if (typeof amenities === "string" && amenities === "loading") {
      return (
        <View style={{ flex: 1, justifyContent: "center" }}>
          <ActivityIndicator animating />
        </View>
      );
    }

    return <View />;
  }
}

AmenityCategoryItemList.propTypes = {
  amenities: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.instanceOf(AmenityPO)),
    PropTypes.string
  ]),
  category: PropTypes.oneOf(Object.keys(AmenityTypes)),
  selectedAmenity: PropTypes.instanceOf(AmenityPO),
  selectedTravelMethod: PropTypes.oneOf(Object.keys(TravelTypes)),
  onAmenitySelected: PropTypes.func,
  onTravelModeSelected: PropTypes.func //refer to AmenityItem
};

export { AmenityCategoryItemList };
