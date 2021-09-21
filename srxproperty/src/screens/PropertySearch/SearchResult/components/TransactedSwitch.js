import React, { Component } from "react";
import { View, Switch, Platform } from "react-native";
import PropTypes from "prop-types";
import { SmallBodyText } from "../../../../components";
import { SRXColor } from "../../../../constants";

isIOS = Platform.OS === "ios";

class TransactedSwitch extends Component {
  render() {
    const { isSale, isTransacted, onSwitchValueChange } = this.props;

    return (
      <View style={{ flexDirection: "row", alignItems: "center" }}>
        <SmallBodyText style={{ color: SRXColor.Gray, marginRight: 5 }}>
          {isSale ? "Sold properties" : "Rented properties"}
        </SmallBodyText>
        <Switch
          trackColor={{
            true: SRXColor.TextLink,
            false: SRXColor.LightGray
          }}
          thumbColor={isIOS ? null : SRXColor.White}
          value={isTransacted}
          onValueChange={onSwitchValueChange}
        />
      </View>
    );
  }
}

TransactedSwitch.propTypes = {
  isSale: PropTypes.bool,
  isTransacted: PropTypes.bool.isRequired,
  onSwitchValueChange: PropTypes.func
};

export { TransactedSwitch };
