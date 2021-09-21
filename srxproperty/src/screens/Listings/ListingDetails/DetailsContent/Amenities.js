import React, { Component } from "react";
import { View, TouchableWithoutFeedback } from "react-native";
import { Avatar, BodyText } from "../../../../components";
import { Navigation } from "react-native-navigation";
import PropTypes from "prop-types";
import MapView, { PROVIDER_GOOGLE, Marker, Circle } from "react-native-maps";
import { AmenitiesCategoryPO } from "../../../../dataObject";
import { NumberUtil, ObjectUtil } from "../../../../utils";
import { SRXColor } from "../../../../constants";
import {
  AmenityCategoryTabBar,
  AmenityTypes,
  AmenitiesOptions
} from "../../../Amenities";
import { Spacing } from "../../../../styles";

class Amenities extends Component {
  static propTypes = {
    locationTitle: PropTypes.string,
    latitude: PropTypes.oneOfType([PropTypes.number, PropTypes.string])
      .isRequired,
    longitude: PropTypes.oneOfType([PropTypes.number, PropTypes.string])
      .isRequired,

    /**
     * indicate to show fuzzy location
     * instead of an accurate location of the property (to prevent indicating the exact landed property)
     */
    showFuzzyLocation: PropTypes.bool,

    amenitiesGroups: PropTypes.arrayOf(
      PropTypes.instanceOf(AmenitiesCategoryPO)
    ),
    /**
     * Optional
     *
     */
    nearbyUsers: PropTypes.arrayOf(PropTypes.object),
    /**
     * component id of the screen that contain this class
     */
    componentId: PropTypes.string,
    source: PropTypes.string
  };

  static defaultProps = {
    latitude: 1.29027,
    longitude: 103.851959
  };

  constructor(props) {
    super(props);
    this.viewAmenitiesInFullScreen = this.viewAmenitiesInFullScreen.bind(this);
    this.renderMarker = this.renderMarker.bind(this);
  }

  state = { amenitiesOptions: AmenitiesOptions };

  componentDidMount() {
    this.updateNearByUsers();
  }

  updateNearByUsers() {
    const { nearbyUsers, source } = this.props;
    if (!ObjectUtil.isEmpty(source)) {
      let amenitiesOptions = [...AmenitiesOptions];
      switch (source) {
        case "ListingDetails":
          amenitiesOptions[0].title = "Your Location";
          this.setState({ amenitiesOptions });
          break;
        case "MyPropertyDetails":
          // amenitiesOptions.title = "Nearby owners";
          amenitiesOptions.shift();
          this.setState({ amenitiesOptions });
          break;
      }
    }
  }

  viewAmenitiesInFullScreen(type) {
    if (!ObjectUtil.isEmpty(this.props.componentId)) {
      const { source } = this.props;
      //Temporary solution to hide nearbyOwner Maps fullscreen in my PropertyDetails
      if (source === "MyPropertyDetails") {
        Navigation.push(this.props.componentId, {
          component: {
            name: "PropertySearchStack.AmenitiesScreen",
            passProps: {
              locationTitle: this.props.locationTitle,
              latitude: this.props.latitude,
              longitude: this.props.longitude,
              showFuzzyLocation: this.props.showFuzzyLocation,
              amenitiesGroups: this.props.amenitiesGroups,
              initialCategory: type || AmenityTypes.MRT,
              amenitiesOptions: this.state.amenitiesOptions,
              nearbyUsers: this.props.nearbyUsers
            }
          }
        });
      } else {
        Navigation.push(this.props.componentId, {
          component: {
            name: "PropertySearchStack.AmenitiesScreen",
            passProps: {
              locationTitle: this.props.locationTitle,
              latitude: this.props.latitude,
              longitude: this.props.longitude,
              showFuzzyLocation: this.props.showFuzzyLocation,
              amenitiesGroups: this.props.amenitiesGroups,
              initialCategory: type || AmenityTypes.Others,
              amenitiesOptions: this.state.amenitiesOptions,
              nearbyUsers: this.props.nearbyUsers
            }
          }
        });
      }
    }
  }

  renderMarker() {
    const { latitude, longitude, showFuzzyLocation } = this.props;
    let lat_Num = NumberUtil.floatValue(latitude);
    let long_Num = NumberUtil.floatValue(longitude);
    if (showFuzzyLocation) {
      return (
        <Circle
          center={{
            latitude: lat_Num,
            longitude: long_Num
          }}
          radius={60}
          strokeColor={SRXColor.Teal}
          fillColor={SRXColor.Teal + "7E"} //#rrggbbaa, 7E ==> 50% transparency
        />
      );
    } else {
      return (
        <Marker
          coordinate={{
            latitude: lat_Num,
            longitude: long_Num
          }}
          zIndex={51} //Currently Nearby Pointer max is 50
        />
      );
    }
  }

  renderNearByOwner() {
    const { nearbyUsers } = this.props;
    if (!ObjectUtil.isEmpty(nearbyUsers)) {
      let i =nearbyUsers.length;
      let output = nearbyUsers.map((item, index) => (
        <Marker
          coordinate={{
            latitude: item.latitude,
            longitude: item.longitude
          }}
          zIndex={i--}
        >
          <Avatar size={24} name={item.name} borderLess={true} />
        </Marker>
      ));
      return output;
    }
  }

  render() {
    const { latitude, longitude, style, nearbyUsers } = this.props;
    const { amenitiesOptions } = this.state;
    let lat_Num = NumberUtil.floatValue(latitude);
    let long_Num = NumberUtil.floatValue(longitude);

    return (
      <TouchableWithoutFeedback
        onPress={() => this.viewAmenitiesInFullScreen()}
      >
        <View>
          <AmenityCategoryTabBar
            onTypeSelected={({ type }) => this.viewAmenitiesInFullScreen(type)}
            amenitiesOptions={amenitiesOptions}
          />
          <View style={[{ height: 200 }, style]}>
            <MapView
              provider={PROVIDER_GOOGLE}
              style={{ flex: 1 }}
              initialRegion={{
                latitude: lat_Num,
                longitude: long_Num,
                latitudeDelta: 0.002,
                longitudeDelta: 0.002
              }}
              zoomEnabled={false}
              scrollEnabled={false}
              pitchEnabled={false}
              rotateEnabled={false}
            >
              {this.renderMarker()}
              {this.renderNearByOwner()}
            </MapView>

            {!ObjectUtil.isEmpty(nearbyUsers) ? (
              <View
                style={{
                  position: "absolute",
                  top: 10,
                  backgroundColor: SRXColor.White,
                  height: 40,
                  borderRadius: 5,
                  borderColor: SRXColor.LightGray,
                  borderWidth: 0.5,
                  justifyContent: "center",
                  alignSelf: "center",
                  paddingHorizontal: Spacing.M
                }}
              >
                <BodyText>Nearby tracked properties</BodyText>
              </View>
            ) : null}
          </View>
        </View>
      </TouchableWithoutFeedback>
    );
  }
}

export { Amenities };
