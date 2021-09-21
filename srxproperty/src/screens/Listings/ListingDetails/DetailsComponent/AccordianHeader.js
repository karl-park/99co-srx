import React, { Component } from "react";
import { View } from "react-native";
import { FeatherIcon, Heading2 } from "../../../../components";
import { Spacing } from "../../../../styles";
import { SRXColor } from "../../../../constants";

const AccordianHeader = ({ children, isActive, headerStyle, titleIcon }) => {
  return (
    <View
      style={[
        {
          padding: Spacing.M,
          flexDirection: "row",
          alignItems: "center",
          backgroundColor: SRXColor.White
        },
        headerStyle
      ]}
    >
      {titleIcon ? titleIcon: null}
      <Heading2 style={{ flex: 1, marginRight: Spacing.XS }}>
        {children}
      </Heading2>
      {isActive ? (
        <FeatherIcon name="chevron-up" size={20} color={SRXColor.Black} />
      ) : (
        <FeatherIcon name="chevron-down" size={20} color={SRXColor.Black} />
      )}
    </View>
  );
};

export { AccordianHeader };
