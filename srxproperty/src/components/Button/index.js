import React, { Component } from "react";
import PropTypes from "prop-types";
import { BigButton } from "./BigButton";
import { Button } from "./Button";
import { PrimaryButton } from "./PrimaryButton";
import { SecondaryButton } from "./SecondaryButton";

const ButtonTypes = {
  default: "default",
  primary: "primary",
  secondary: "secondary",
  big: "big"
};

export default class extends Component {
  static buttonTypes = ButtonTypes;

  static propTypes = {
      /*
       * specific type for SRX App used
       * if passing this, the style will be auto populated,
       * buttonStyle & textStyle are not required unless you wish to overwrite the style, which is not recommended
       */
      buttonType: PropTypes.oneOf(Object.keys(ButtonTypes)),
      /*
       * Style for button, eg, background, border, etc
       * please take note style is not used, use buttonStyle instead
       */
      buttonStyle: PropTypes.oneOfType([PropTypes.object, PropTypes.array]),
      /*
       * Style for text in the button
       */
      textStyle: PropTypes.oneOfType([PropTypes.object, PropTypes.array]),
      /*
       * text in button, only string is accepted
       */
      children: PropTypes.string,
      /*
       *  component on the left side of title
       *  For Big Button, {highlighted: boolean} will be provided if leftView is used as a function
       */
      leftView: PropTypes.oneOfType([PropTypes.element, PropTypes.func]),
      /*
       *  component on the right side of title
       */
      rightView: PropTypes.element,

      /*
       * the rest of props, please refer to https://facebook.github.io/react-native/docs/touchableopacity
       */
  };

  render() {
    const { buttonType, ...rest } = this.props;

    if (buttonType === ButtonTypes.primary) {
      return <PrimaryButton {...rest} />;
    } else if (buttonType === ButtonTypes.secondary) {
      return <SecondaryButton {...rest} />;
    } else if (buttonType === ButtonTypes.big) {
      return <BigButton {...rest} />;
    } else {
      return <Button {...this.props} />;
    }
  }
}
