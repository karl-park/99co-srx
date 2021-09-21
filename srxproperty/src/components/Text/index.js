import React from "react";
import { Text } from "react-native";
import { Typography } from "../../styles";

const Heading1 = ({ style, ...rest }) => {
  return <Text style={[Typography.Heading1, style]} {...rest} />;
};

const Heading2 = ({ style, ...rest }) => {
  return <Text style={[Typography.Heading2, style]} {...rest} />;
};

const Heading2_Currency = ({ style, ...rest }) => {
  return <Text style={[Typography.Heading2_Currency, style]} {...rest} />;
};

const BodyText = ({ style, ...rest }) => {
  return <Text style={[Typography.Body, style]} {...rest} />;
};

const SmallBodyText = ({ style, ...rest }) => {
  return <Text style={[Typography.SmallBody, style]} {...rest} />;
};

const Subtext = ({ style, ...rest }) => {
  return <Text style={[Typography.Subtext, style]} {...rest} />;
};

const ExtraSmallBodyText = ({ style, ...rest }) => {
  return <Text style={[Typography.ExtraSmallBody, style]} {...rest} />;
};

const LargeTitleText = ({ style, ...rest }) => {
  return <Text style={[Typography.LargeTitle, style]} {...rest} />;
};

export {
  Text,
  Heading1,
  Heading2,
  Heading2_Currency,
  BodyText,
  SmallBodyText,
  Subtext,
  ExtraSmallBodyText,
  LargeTitleText
};
