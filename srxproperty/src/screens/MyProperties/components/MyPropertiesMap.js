import React, { Component } from "react";
import { Image } from "react-native";
import PropTypes from "prop-types";
import MapView, { PROVIDER_GOOGLE, Marker } from "react-native-maps";
import {
  MyProperties_Condo,
  MyProperties_HDB,
  MyProperties_Landed,
  Placeholder_Logo_User
} from "../../../assets";
import { ObjectUtil, PropertyTypeUtil } from "../../../utils";

const INIT_REGION = {
  // latitude: 1.3245622141929942,
  // longitude: 103.81980293808238,
  latitude: 1.335758310278024,
  longitude: 103.81860293808234,
  latitudeDelta: 0.2,
  longitudeDelta: 0.45
};

class MyPropertiesMap extends Component {
  state = {
    isMapReady: false,
    myProperties: null
  };

  componentDidUpdate(prevProps, prevState) {
    if (prevProps.properties !== this.props.properties || prevState.isMapReady !== this.state.isMapReady) {
      if (this.state.isMapReady) {
        this.updateMyPropertiesToState();
      }
    }
  }

  updateMyPropertiesToState() {
    const { properties } = this.props;
    const newCoords = [];

    if (!ObjectUtil.isEmpty(properties)) {
      properties.map(item => {
        if (item.cdResearchSubtype && item.latitude && item.longitude) {
          newCoords.push({
            cdResearchSubtype: item.cdResearchSubtype,
            latitude: item.latitude,
            longitude: item.longitude
          });
        }
      });
    }
    this.setState({ myProperties: newCoords }, () => {
      this.animateToBoundsOfProperties(newCoords);
    });
  }

  animateToBoundsOfProperties(data) {
    const lats = [];
    const lngs = [];
    if (!ObjectUtil.isEmpty(data)) {
      data.map(item => {
        const { latitude, longitude } = item;

        lats.push(latitude);
        lngs.push(longitude);
      });

      // calc the min and max lng and lat
      var minlat = Math.min.apply(null, lats),
        maxlat = Math.max.apply(null, lats);
      var minlng = Math.min.apply(null, lngs),
        maxlng = Math.max.apply(null, lngs);

      const centerLat = (maxlat + minlat) / 2;
      const centerLng = (maxlng + minlng) / 2;
      const latDelta = Math.max(
        Math.abs(maxlat - centerLat) * 2 + 0.2 / 111,
        1.0 / 111
      );
      const lngDelta = Math.max(
        Math.abs(maxlng - centerLng) * 2 + 2 / 111,
        1.0 / 111
      );

      const newRegion = {
        latitude: centerLat,
        longitude: centerLng,
        latitudeDelta: latDelta,
        longitudeDelta: lngDelta
      };

      this.map.animateToRegion(newRegion);
    }
  }

  onMapLayout = () => {
    this.setState({ isMapReady: true });
  }

  renderMarker() {
    const { myProperties, isMapReady } = this.state;
    if (isMapReady && !ObjectUtil.isEmpty(myProperties)) {
      return myProperties.map(item => {
        const { cdResearchSubtype, latitude, longitude } = item;
        var markerImage;
        if (PropertyTypeUtil.isCondo(cdResearchSubtype)) {
          markerImage = Placeholder_Logo_User;
        } else if (PropertyTypeUtil.isHDB(cdResearchSubtype)) {
          markerImage = Placeholder_Logo_User;
        } else if (PropertyTypeUtil.isLanded(cdResearchSubtype)) {
          markerImage = Placeholder_Logo_User;
        }

        if (markerImage) {
          return (
            <Marker
              // image={markerImage}
              coordinate={{
                latitude: latitude,
                longitude: longitude
              }}
            >
              <Image source={markerImage} style={{ width: 30, height: 30 }} />
            </Marker>
          );
        }
      });
    }
  }

  render() {
    return (
      <MapView
        ref={r => {
          this.map = r;
        }}
        style={{ flex: 1 }}
        provider={PROVIDER_GOOGLE}
        paddingAdjustmentBehavior={"automatic"}
        initialRegion={INIT_REGION}
        onLayout={this.onMapLayout}
      >
        {this.renderMarker()}
      </MapView>
    );
  }
}

export { MyPropertiesMap };
