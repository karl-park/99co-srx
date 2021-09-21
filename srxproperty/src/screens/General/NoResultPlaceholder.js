/**
 * General usage
 * You may edit the design
 * but make sure there isn't any other function adding in
 * unless it is commonly used whenever there is not result
 */

import React, { Component } from "react";
import { View, Platform } from "react-native";
import PropTypes from "prop-types";
import { SmallBodyText } from "../../components";
import { SRXColor } from "../../constants";
import { Spacing } from "../../styles";

isIOS = Platform.OS === "ios";

class NoResultPlaceholder extends Component {
  render() {
    return (
      <View
        style={{
          flex: 1,
          alignItems: "center",
          justifyContent: "center",
          backgroundColor: SRXColor.White,
          paddingHorizontal: Spacing.M,
          paddingVertical: Spacing.L
        }}
      >
        <SmallBodyText style={{ color: SRXColor.Black, fontWeight: "bold" }}>
          No result found
        </SmallBodyText>
      </View>
    );
  }
}

export { NoResultPlaceholder };
