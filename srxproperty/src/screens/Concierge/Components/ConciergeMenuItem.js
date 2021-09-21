import React, { Component } from "react";
import { View, TouchableHighlight, StyleSheet, Platform } from "react-native";
import PropTypes from "prop-types";

import { FeatherIcon, BodyText, LinearGradient } from "../../../components";
import { SRXColor } from "../../../constants";

isIOS = Platform.OS === "ios";

class ConciergeMenuItem extends Component {
  static propTypes = {
    itemLabel: PropTypes.string,
    onPress: PropTypes.func,
    alignment: PropTypes.oneOf(["left", "right"])
  };

  static defaultProps = {
    itemLabel: "",
    alignment: "right"
  };

  constructor(props) {
    super(props);
  }

  renderLeftAccessory() {
    const { alignment } = this.props;
    if (alignment === "left") {
      return (
        <View style={styles.accessoryContainer}>
          <FeatherIcon name="chevron-left" size={25} color={SRXColor.Black} />
        </View>
      );
    } else {
      return <View style={{ flex: 1 }} />;
    }
  }

  renderRightAccessory() {
    const { alignment } = this.props;
    if (alignment === "right") {
      return (
        <View style={styles.accessoryContainer}>
          <FeatherIcon name="chevron-right" size={25} color={SRXColor.Black} />
        </View>
      );
    } else {
      return <View style={{ flex: 1 }} />;
    }
  }

  render() {
    const { itemLabel, onPress } = this.props;
    return (
      <TouchableHighlight
        style={{ flex: 1, justifyContent: "center" }}
        onPress={onPress}
      >
        <LinearGradient
          style={{ flex: 1 }}
          colors={[SRXColor.White, SRXColor.AccordionBackground]}
        >
          <View style={[styles.accessoryContainer, { flexDirection: "row" }]}>
            {this.renderLeftAccessory()}
            <BodyText style={{ fontWeight: "400" }}>{itemLabel}</BodyText>
            {this.renderRightAccessory()}
          </View>
        </LinearGradient>
      </TouchableHighlight>
    );
  }
}

const styles = StyleSheet.create({
  accessoryContainer: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center"
  }
});

export { ConciergeMenuItem };
