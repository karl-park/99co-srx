import React, { Component } from "react";
import {
  View,
  ScrollView,
  SafeAreaView,
  TouchableOpacity,
  Dimensions
} from "react-native";
import PropTypes from "prop-types";
import { TextInput, FeatherIcon, Text } from "../../components";
import { SRXColor } from "../../constants";
import { AmenityPO } from "../../dataObject";
import { Spacing, InputStyles } from "../../styles";
import { ObjectUtil, CommonUtil } from "../../utils";
import { AddressAutoComplete, AutoCompletePurposes } from "../AutoComplete";
import { AmenityItemTravelMode } from "./AmenityItemTravelMode";
import { TravelTypes } from "./constant";

var { height, width } = Dimensions.get("window");

class AmenityCustomLocation extends Component {
  componentDidMount() {
    const { latitude, longitude, onAmenityUpdated } = this.props;
    CommonUtil.getUserLocation(({ coordinate }) => {
      const userLocation = new AmenityPO();
      userLocation.name = "My Location";
      userLocation.latitude = coordinate.latitude;
      userLocation.longitude = coordinate.longitude;

      const distance = CommonUtil.getDistance({
        location1: { latitude, longitude },
        location2: coordinate
      });
      userLocation.distance = Math.round(distance * 100) / 100 + "km";

      userLocation.id = "my_Location";
      userLocation.isUserLocation = true;

      if (onAmenityUpdated) onAmenityUpdated({ amenity: userLocation });
    });
  }

  showAddressAutoComplete = () => {
    AddressAutoComplete.show({
      onSuggestionSelected: this.onSuggestionSelected,
      purpose: AutoCompletePurposes.Amenities
    });
  };

  onSuggestionSelected = selectedDict => {
    const { latitude, longitude, onAmenityUpdated } = this.props;

    const newAmenity = new AmenityPO();
    newAmenity.name = selectedDict.displayText;
    // newAmenity.id = id;
    newAmenity.latitude = selectedDict.latitude;
    newAmenity.longitude = selectedDict.longitude;
    const distance = CommonUtil.getDistance({
      location1: { latitude, longitude },
      location2: {
        latitude: selectedDict.latitude,
        longitude: selectedDict.longitude
      }
    });
    newAmenity.distance = Math.round(distance * 100) / 100 + "km";

    if (onAmenityUpdated) onAmenityUpdated({ amenity: newAmenity });
  };

  renderFromLocation() {
    return (
      <View style={[styles.inputContainer, {marginBottom: Spacing.XS}]}>
        <TextInput
          inputContainerStyle={{ backgroundColor:SRXColor.LightGray+"66" }}
          title={"From"}
          keyboardType={"default"}
          underlineColorAndroid="transparent"
          autoCorrect={false}
          value={"21 Stirling Road, 148960"}
          systemPopulated
          editable={false}
        />
      </View>
    );
  }

  renderToLocation() {
    const { toLocation } = this.props;
    return (
      <View style={styles.inputContainer}>
        <Text style={[InputStyles.label]}>{"To"}</Text>
        <TouchableOpacity
          style={[
            InputStyles.container,
            InputStyles.containerBorder,
            InputStyles.containerPadding,
            InputStyles.containerAlign,
            InputStyles.singleLineTextHeight
          ]}
          onPress={() => this.showAddressAutoComplete()}
        >
          <FeatherIcon
            name="search"
            size={24}
            color={"#8DABC4"}
            style={{ alignSelf: "center", marginRight: Spacing.XS / 2 }}
          />
          {ObjectUtil.isEmpty(toLocation) ? (
            <Text style={[InputStyles.text, { color: "#8DABC4" }]}>
              Enter a location
            </Text>
          ) : (
            <Text style={[InputStyles.text, { color: SRXColor.Black }]}>
              {toLocation.name}
            </Text>
          )}
        </TouchableOpacity>
      </View>
    );
  }

  renderTravelTime() {
    const { toLocation, travelMethod, onTravelModeSelected } = this.props;
    return (
      <AmenityItemTravelMode
        style={{
          paddingHorizontal: 0,
          paddingVertical: 0
        }}
        item={toLocation}
        travelMethod={travelMethod}
        onTravelModeSelected={onTravelModeSelected}
      />
    );
  }

  render() {
    return (
      <SafeAreaView style={{ flex: 1 }}>
        <ScrollView style={{ flex: 1 }}>
          <View style={styles.contentContainer}>
            {this.renderToLocation()}
            {this.renderFromLocation()}
            {this.renderTravelTime()}
          </View>
        </ScrollView>
      </SafeAreaView>
    );
  }
}

const styles = {
  contentContainer: {
    paddingHorizontal: Spacing.M,
    paddingVertical: Spacing.XS,
    backgroundColor: "white"
  },
  inputContainer: {
    flexDirection: "column",
    paddingVertical: Spacing.XS
  }
};

AmenityCustomLocation.propTypes = {
  toLocation: PropTypes.instanceOf(AmenityPO),
  travelMethod: PropTypes.oneOf(Object.keys(TravelTypes)),
  onTravelModeSelected: PropTypes.func,
  onAmenityUpdated: PropTypes.func
};

export { AmenityCustomLocation };
