import React, { Component } from "react";
import { View, ImageBackground } from "react-native";
import PropTypes from "prop-types";
import { Marker } from "react-native-maps";
import {
  Listing_Listing_Marker,
  Listing_Listing_Marker_Selected,
  Listing_Transaction_Marker,
  Listing_Transaction_Marker_Selected
} from "../../../../../../assets";
import { Text } from "../../../../../../components";
import { SRXColor } from "../../../../../../constants";

class ListingAnnotation extends Component {
  state = {
    tracksViewChanges: true
  };

  constructor(props) {
    super(props);

    this.onPress = this.onPress.bind(this);
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    if (this.props !== prevProps) {
      this.setState(() => ({
        tracksViewChanges: true
      }));
    } else if (this.state.tracksViewChanges) {
      this.setState(() => ({
        tracksViewChanges: false
      }));
    }
  }

  onPress = event => {
    const { onPress, data } = this.props;
    if (onPress) {
      onPress({ event, data });
    }
  };

  render() {
    const { coordinate, children, isSelected, isTransacted } = this.props;

    let markerSource = Listing_Listing_Marker;
    let markerTextColor = SRXColor.White;
    if (isTransacted) {
      if (isSelected) {
        markerSource = Listing_Transaction_Marker_Selected;
        markerTextColor = SRXColor.Black;
      } else {
        markerSource = Listing_Transaction_Marker;
        markerTextColor = SRXColor.White;
      }
    } else {
      if (isSelected) {
        markerSource = Listing_Listing_Marker_Selected;
        markerTextColor = SRXColor.Teal;
      }
    }

    return (
      <Marker
        coordinate={coordinate}
        onPress={this.onPress}
        tracksViewChanges={this.state.tracksViewChanges}
        // image={markerSource}
      >
        <ImageBackground
          style={[
            {
              justifyContent: "flex-start",
              alignItems: "center",
              width: 40,
              height: 40
            }
          ]}
          resizeMode={"cover"}
          source={markerSource}
        >
          <Text
            numberOfLines={1}
            style={{
              color: markerTextColor,
              fontSize: 12,
              textAlign: "center",
              marginTop: 8
            }}
          >
            {children}
          </Text>
        </ImageBackground>
      </Marker>
    );
  }
}

ListingAnnotation.propTypes = {
  /**
   * coordinate of marker to be place
   */
  coordinate: PropTypes.object,
  /**
   * Text to show on the marker, to specify number of listings
   */
  children: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),

  /**
   * to indicate if this marker is for transaction or not
   */
  isTransacted: PropTypes.bool,

  /**
   * to indicate if this marker is selected, will show a different style
   */
  isSelected: PropTypes.bool,

  /**
   * some data to pass when the marker is pressed
   */
  data: PropTypes.object,

  /**
   * function triggered when the marker is pressed, will pass the original event & data
   */
  onPress: PropTypes.func
};

export { ListingAnnotation };
