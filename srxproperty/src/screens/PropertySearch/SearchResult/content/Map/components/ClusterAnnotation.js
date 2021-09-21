import React, { Component } from "react";
import { SafeAreaView, View } from "react-native";
import PropTypes from "prop-types";
import { Marker, Callout, Polygon } from "react-native-maps";
import { Text, SmallBodyText } from "../../../../../../components";
import { SRXColor } from "../../../../../../constants";
import { ObjectUtil } from "../../../../../../utils";

class ClusterAnnotation extends Component {
  state = {};

  constructor(props) {
    super(props);

    this.onPress = this.onPress.bind(this);
  }

  onLayout = ({ nativeEvent: { layout } }) => {
    this.setState({ bounds: layout });
  };

  onPress = data => {
    const { onPress } = this.props;
    console.log(data.nativeEvent);
    if (onPress) {
      onPress(data);
    }
  };

  render() {
    const { coordinate, children, isTransacted } = this.props;
    const { bounds } = this.state;

    var stateStyle = {
      opacity: 0
    };

    if (!ObjectUtil.isEmpty(bounds)) {
      const itemWidth = Math.ceil(bounds.width);
      stateStyle = {
        width: itemWidth,
        height: itemWidth,
        borderRadius: itemWidth / 2
      };
    }

    return (
      <Marker coordinate={coordinate} onPress={this.onPress}>
        <View
          style={[
            {
              // borderColor: SRXColor.Teal,
              // borderWidth: 1,
              backgroundColor: isTransacted ? "#d1031c" : SRXColor.Teal,
              justifyContent: "center",
              alignItems: "center",
              paddingHorizontal: 3,
              minWidth: 46,
              maxWidth: 60,
              minHeight: 46,
              maxHeight: 60,
              margin: 4
            },
            stateStyle
          ]}
          onLayout={this.onLayout}
        >
          <SmallBodyText style={{ color: SRXColor.White }}>
            {children}
          </SmallBodyText>
        </View>
      </Marker>
    );
  }
}

ClusterAnnotation.propTypes = {
  /**
   * coordinate of marker to be place
   */
  coordinate: PropTypes.object,
  /**
   * Text to show on the marker, to specify number of listings
   */
  children: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),

  isTransacted: PropTypes.bool,

  /**
   * function triggered when the marker is pressed, will pass the native event
   */
  onPress: PropTypes.func
};

export { ClusterAnnotation };
