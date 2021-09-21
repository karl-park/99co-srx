import React, { Component } from "react";
import { ButtonStyles } from "../../styles";
import { Button, ButtonStates } from "./Button";

class SecondaryButton extends Component {
  state = {
    buttonState: ButtonStates.normal
  };

  render() {
    const { buttonStyle, textStyle, disabled } = this.props;

    const { buttonState } = this.state;

    let typeButtonStyle;
    if (buttonState === ButtonStates.highlighted) {
      typeButtonStyle = ButtonStyles.secondary_Container_Highlighted;
    } else {
      typeButtonStyle = ButtonStyles.secondary_Container;
    }

    return (
      <Button
        {...this.props}
        buttonStyle={[
          typeButtonStyle,
          buttonStyle,
          disabled ? { opacity: 0.5 } : null
        ]}
        textStyle={[ButtonStyles.secondary_Title, textStyle]}
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

export { SecondaryButton };
