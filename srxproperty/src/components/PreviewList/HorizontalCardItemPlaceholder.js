import React, { Component } from "react";
import { View } from "react-native";
import { BodyText, Subtext, Heading2_Currency } from "../../components";
import { SRXColor } from "../../constants";
import { Spacing } from "../../styles";

class HorizontalCardItemPlaceholder extends Component {

  render() {
      return <View style={[styles.containerStyle, styles.containerBorder]}>
          <View style={{ backgroundColor: SRXColor.White, padding: Spacing.XS }}>
            <View style={{ marginBottom: Spacing.XS }}>
              <View style={styles.imageStyle} />
            </View>
            <View>
              <View style={{ height: 16, width: "80%", backgroundColor: SRXColor.LightGray, marginBottom: 4 }} />
              <View style={{ height: 16, width: "40%", backgroundColor: SRXColor.LightGray, marginBottom: 4}} />
              <View style={{ height: 16, width: "60%", backgroundColor: SRXColor.LightGray }} />
            </View>
          </View>
        </View>;
  }
}

const styles = {
  containerStyle: {
    backgroundColor: SRXColor.White,
    overflow: "hidden",
    width: 136 + Spacing.XS * 2,
    shadowColor: "rgb(110,129,154)",
    shadowOffset: { width: 1, height: 2 },
    shadowOpacity: 0.32,
    shadowRadius: 1
  },
  containerBorder: {
    borderRadius: 5,
    borderWidth: 1,
    borderColor: "#e0e0e0"
  },
  imageStyle: {
    borderRadius: 5,
    width: 136,
    height: 88,
    backgroundColor: SRXColor.LightGray
  }
};

export { HorizontalCardItemPlaceholder };
