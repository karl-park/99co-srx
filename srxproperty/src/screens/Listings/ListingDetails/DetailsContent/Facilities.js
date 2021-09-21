import React, { Component } from "react";
import { View, Dimensions } from "react-native";
import PropTypes from "prop-types";
import { BodyText, FeatherIcon } from "../../../../components";
import { Spacing } from "../../../../styles";
import { SRXColor } from "../../../../constants";

var { height, width } = Dimensions.get("window");

class Facilities extends Component {

  static defaultProps = {
    facilities: [],
    features: [],
    fixtures: []
  }

  render() {
    const { style, facilities, features, fixtures } = this.props;

    const combinedArray = [...facilities, ...features, ...fixtures];

    const itemMinWidth = (width - 2 * Spacing.M -2 *Spacing.S) / 2;

    return (
      <View
        style={[
          {
            paddingHorizontal: Spacing.M,
            paddingTop: Spacing.XS,
            paddingBottom: Spacing.L
          },
          {
            flexDirection: "row",
            flexWrap: "wrap"
          },
          style
        ]}
      >
        {combinedArray.map((item, index) => (
          <View key={item.value} style={{ minWidth: itemMinWidth, flexDirection: "row", alignItems: "center", marginRight: Spacing.S, marginBottom: Spacing.S }}>
            <FeatherIcon name="check" size={16} color={SRXColor.Black} style={{marginRight: 4}}/>
            <BodyText>{item.name}</BodyText>
          </View>
        ))}
      </View>
    );
  }
}

Facilities.propsType = {
  facilities: PropTypes.array,
  features: PropTypes.array,
  fixtures: PropTypes.array,
}

export { Facilities };
