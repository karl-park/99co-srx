import React, { Component } from "react";
import { Text, TouchableOpacity } from "react-native";
import LinearGradient from "react-native-linear-gradient";
import { SRXColor } from "../../constants";
import { ButtonStyles } from "../../styles";
import { Button, ButtonStates } from "./Button";

class BigButton extends Component {
  state = {
    buttonState: ButtonStates.normal
  };

  render() {
    const {
      onPress,
      buttonStyle,
      textStyle,
      children,
      disabled,
      isSelected,
      leftView,
      rightView,
      ...rest
    } = this.props;

    const { buttonState } = this.state;

    let typeButtonStyle, typeButtonTextStyle;
    if (buttonState === ButtonStates.highlighted || isSelected) {
      typeButtonStyle = ButtonStyles.big_Container_Highlighted;
      typeButtonTextStyle = ButtonStyles.big_Title_Highlighted;
    } else {
      typeButtonStyle = ButtonStyles.big_Container;
      typeButtonTextStyle = ButtonStyles.big_Title;
    }

    let leftViewItem = null;
    if (leftView) {
      if (typeof leftView === "function") {
        leftViewItem = leftView({
          highlighted: buttonState === ButtonStates.highlighted || isSelected
        });
      } else {
        leftViewItem = leftView;
      }
    }

    return (
      <TouchableOpacity
        {...rest}
        style={[
          styles.containerStyle,
          typeButtonStyle,
          buttonStyle,
          disabled ? { opacity: 0.5 } : null
        ]}
        onPress={onPress}
        disabled={disabled || isSelected}
        activeOpacity={1}
        onPressIn={() =>
          this.setState({ buttonState: ButtonStates.highlighted })
        }
        onPressOut={() => this.setState({ buttonState: ButtonStates.normal })}
      >
        {leftViewItem}
        {children ? (
          <Text style={[styles.textStyle, typeButtonTextStyle, textStyle]}>
            {children}
          </Text>
        ) : null}
        {rightView}
      </TouchableOpacity>
    );
  }
}

const styles = {
  containerStyle: {
    justifyContent: "flex-start",
    alignItems: "center",
    flexDirection: "row"
  },
  textStyle: {
    color: SRXColor.Teal,
    fontSize: 15
  }
};

export { BigButton };
