import React, { Component } from "react";
import { View } from "react-native";
import { Button, FeatherIcon, SmallBodyText } from "../../../../components";
import { SRXColor } from "../../../../constants";
import { Typography, Spacing } from "../../../../styles";

class FilterButton extends Component {
  render() {
    const { selectedFilterOptionsCount, onPress } = this.props;

    return (
      <Button
        textStyle={[
          Typography.SmallBody,
          {
            color: SRXColor.TextLink,
            marginLeft: 3
          }
        ]}
        leftView={
          <FeatherIcon name={"filter"} size={20} color={SRXColor.Black} />
        }
        rightView={
          selectedFilterOptionsCount ? (
            <View
              style={{
                marginLeft: Spacing.XS / 2,
                width: 20,
                height: 20,
                borderRadius: 10,
                borderColor: SRXColor.LightGray,
                borderWidth: 1,
                alignItems: "center",
                justifyContent: "center"
              }}
            >
              <SmallBodyText
                style={{ color: SRXColor.TextLink, fontWeight: "400" }}
              >
                {selectedFilterOptionsCount}
              </SmallBodyText>
            </View>
          ) : null
        }
        onPress={onPress}
      >
        Filter results
      </Button>
    );
  }
}

export { FilterButton };
