import React, { Component } from "react";
import { Text, TextInput, View } from "react-native";
import { SRXColor } from "../../constants";
import { FeatherIcon } from "../../components";
import { InputStyles, Typography, Spacing } from "../../styles";
import PropTypes from "prop-types";

const InputStates = {
  focus: "focus",
  blur: "blur"
  // disabled: "disabled",
  // error: "error"
};

class Input extends Component {
  state = {
    inputState: InputStates.blur,
    text: ""
  };

  static defaultProps = {
    editable: true,
    multiline: false,
    systemPopulated: false
  };

  //methods

  blur() {
    this._textInput.blur();
  }

  focus() {
    this._textInput.focus();
  }

  clear() {
    this._textInput.clear();
  }

  isFocused() {
    return this._textInput.isFocused();
  }

  //render

  renderLabel() {
    const { title, titleStyle, ...rest } = this.props;

    if (title) {
      return <Text style={[InputStyles.label, titleStyle]}>{title}</Text>;
    }
  }

  hasError() {
    const { error } = this.props;
    return error && error.length > 0;
  }

  renderErrorMessage() {
    const { error } = this.props;
    if (this.hasError()) {
      return (
        <Text style={[InputStyles.errorMessage, { flex: 1 }]}>{error}</Text>
      );
    }
  }

  renderMaxChar() {
    const { maxChar } = this.props;
    const { text } = this.state;

    if (maxChar && maxChar > 0) {
      const charLeft = maxChar - text.length;

      return (
        <Text
          style={[
            Typography.ExtraSmallBody,
            charLeft < 0 ? { color: "#FF151F" } : null,
            { marginTop: 2, marginLeft: Spacing.XS }
          ]}
        >
          {charLeft}
        </Text>
      );
    }
  }

  render() {
    const {
      disabled,
      leftView,
      rightView,
      style,
      inputContainerStyle,
      placeholderTextColor,
      onFocus,
      onBlur,
      error,
      onChangeText,
      multiline,
      editable,
      systemPopulated,
      ...rest
    } = this.props;
    const { inputState } = this.state;

    let stateContainerStyle;
    if (systemPopulated) {
      stateContainerStyle = InputStyles.containerBorder_SystemPopulated;
    } else if (this.hasError()) {
      stateContainerStyle = InputStyles.containerBorder_Error;
    } else if (inputState === InputStates.focus) {
      stateContainerStyle = InputStyles.containerBorder_Focus;
    } else {
      stateContainerStyle = InputStyles.containerBorder;
    }

    return (
      <View style={style}>
        {this.renderLabel()}
        <View
          style={[
            InputStyles.container,
            stateContainerStyle,
            InputStyles.containerPadding,
            InputStyles.containerAlign,
            leftView ? { paddingLeft: 8 } : null,
            rightView || this.hasError() ? { paddingRight: 8 } : null,
            editable ? { opacity: 1.0 } : { opacity: 0.5 },
            inputContainerStyle
          ]}
        >
          {leftView}
          <TextInput
            {...rest}
            ref={component => (this._textInput = component)}
            style={[
              InputStyles.text,
              leftView ? { marginLeft: 8 } : null,
              rightView ? { marginRight: 8 } : null,
              multiline
                ? InputStyles.multilineTextHeight
                : InputStyles.singleLineTextHeight
            ]}
            editable={editable && !systemPopulated}
            multiline={multiline}
            placeholderTextColor={placeholderTextColor || "#8DABC4"}
            underlineColorAndroid={"transparent"}
            onFocus={() => {
              this.setState({ inputState: InputStates.focus });
              if (onFocus) onFocus();
            }}
            onBlur={() => {
              this.setState({ inputState: InputStates.blur });
              if (onBlur) onBlur();
            }}
            onChangeText={text => {
              this.setState({ text });
              if (onChangeText) onChangeText(text);
            }}
          />
          {this.hasError() ? (
            <FeatherIcon name="x" size={20} color={"#FF151F"} />
          ) : (
            rightView
          )}
        </View>
        <View style={{ flexDirection: "row", alignSelf: "flex-end" }}>
          {this.renderErrorMessage()}
          {this.renderMaxChar()}
        </View>
      </View>
    );
  }
}

Input.propTypes = {
  /*
   * Component add on the left of TextInput
   */
  leftView: PropTypes.element,
  /*
   * Component add on the right of TextInput
   */
  rightView: PropTypes.element,
  /*
   * Title above the TextInput
   */
  title: PropTypes.string,
  /*
   * Style for the view wrapping Textinput's container & the title Text
   */
  style: PropTypes.oneOfType([PropTypes.object, PropTypes.array]),

  /*
   * Style for the wrapper of react-native's TextInput, if set fontSize, etc, might get warning
   */
  inputContainerStyle: PropTypes.oneOfType([PropTypes.object, PropTypes.array]),
  /*
   * Style for title.
   * You should follow the design guidelines, which means this shouldn't be use unless necessary
   */
  titleStyle: PropTypes.oneOfType([PropTypes.object, PropTypes.array]),
  /**
   * place holder color
   */
  placeholderTextColor: PropTypes.string,
  /*
   * Error message wanted to show, if passing this, the TextInput will be in state of Error
   */
  error: PropTypes.string,
  /*
   * Number of max characters allowed, if provide this, will add a count at bottom right corner
   * Note: This is different with maxLimit, you may still set maxLimit if you didn't want to show the number
   */
  maxChar: PropTypes.number,

  multiline: PropTypes.bool,

  editable: PropTypes.bool,

  systemPopulated: PropTypes.bool
};
export { Input };
