import React, { Component } from "react";
import { View, Platform } from "react-native";
import PropTypes from "prop-types";

//custom components
import { SmallBodyText, Button } from "../../../../components";
import { SRXColor } from "../../../../constants";
import { Spacing } from "../../../../styles";

isIOS = Platform.OS === "ios";

class NoSearchResult extends Component {
  //Reset Search Options
  onPressResetSearch = () => {
    //to reset search options
    const { onResetSearchOptions } = this.props;
    if (onResetSearchOptions) {
      onResetSearchOptions();
    }
  };

  //Start Presentation method
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
          No properties found
        </SmallBodyText>

        <SmallBodyText style={{ color: SRXColor.Black, marginTop: Spacing.S }}>
          Try adjusting your search
        </SmallBodyText>

        <Button
          buttonStyle={{
            borderWidth: 1,
            borderColor: SRXColor.Teal,
            paddingVertical: Spacing.XS,
            paddingHorizontal: Spacing.S,
            marginTop: Spacing.M
          }}
          textStyle={{ color: SRXColor.Gray }}
          onPress={() => this.onPressResetSearch()}
        >
          Reset search
        </Button>
      </View>
    );
  }
}

NoSearchResult.propTypes = {
  onResetSearchOptions: PropTypes.func.isRequired
};

export { NoSearchResult };
