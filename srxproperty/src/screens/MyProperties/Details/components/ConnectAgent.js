import React, { Component } from "react";
import { View } from "react-native";
import PropTypes from "prop-types";
import {
  Heading2,
  TouchableHighlight,
  SmallBodyText
} from "../../../../components";
import { SRXColor } from "../../../../constants";
import { Spacing } from "../../../../styles";

class ConnectAgent extends Component {
  render() {
    return (
      <View
        style={{
          paddingBottom: 46,
          paddingTop: Spacing.L,
          paddingHorizontal: Spacing.M,
          alignItems: "center"
        }}
      >
        <Heading2 style={{ marginBottom: Spacing.XS, textAlign: "center" }}>
          Looking to sell/rent{"\n"}your property?
        </Heading2>
        <TouchableHighlight
          style={{
            backgroundColor: SRXColor.White,
            borderWidth: 1,
            borderColor: "#e0e0e0",
            borderRadius: 3,
            shadowColor: "rgb(110,129,154)",
            shadowOffset: { width: 1, height: 2 },
            shadowOpacity: 0.32,
            shadowRadius: 1
          }}
          onPress={this.props.onConnectAgent}
        >
          <View
            style={{
              backgroundColor: SRXColor.White,
              borderRadius: 3,
              paddingVertical: Spacing.M,
              paddingHorizontal: Spacing.S
            }}
          >
            <SmallBodyText>Connect to an expert agent</SmallBodyText>
          </View>
        </TouchableHighlight>
      </View>
    );
  }
}

ConnectAgent.propTypes = {
  onConnectAgent: PropTypes.func
};

export { ConnectAgent };
