import React, { Component } from "react";
import { ButtonStyles } from "../../styles";
import { Button, ButtonStates } from "./Button";

class PrimaryButton extends Component {
  state = {
    buttonState: ButtonStates.normal
  };

  render() {
    const {
      buttonStyle,
      textStyle,
      disabled,
      isSelected
    } = this.props;

    const { buttonState } = this.state;

    let typeButtonStyle;
    if (buttonState === ButtonStates.highlighted) {
      typeButtonStyle = ButtonStyles.primary_Container_Highlighted;
    } else {
      typeButtonStyle = ButtonStyles.primary_Container;
    }

    return (
      <Button
       { ... this.props}
        buttonStyle={[typeButtonStyle, buttonStyle, disabled || isSelected ? { opacity: 0.5 } : null]}
        textStyle={[ButtonStyles.primary_Title, textStyle]}
        disabled={disabled || isSelected}
        activeOpacity={1}
        onPressIn={() => {
          this.setState({ buttonState: ButtonStates.highlighted });
        }}
        onPressOut={() => {
          this.setState({ buttonState: ButtonStates.normal });
        }}
      />
    );
  }
}

export { PrimaryButton };
