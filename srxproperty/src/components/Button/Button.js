import React, {Component} from 'react';
import {Text, TouchableOpacity} from 'react-native';
import {SRXColor} from '../../constants';

const ButtonStates = {
  normal: 'normal',
  highlighted: 'highlighted',
  selected: 'selected',
  disabled: 'disabled',
};

class Button extends Component {
  render() {
    const {
      onPress,
      buttonStyle,
      selectedButtonStyle,
      textStyle,
      selectedTextStyle,
      children,
      disabled,
      leftView,
      rightView,
      ...rest
    } = this.props;
    const {isSelected} = this.props;
    return (
      <TouchableOpacity
        {...rest}
        style={[
          styles.containerStyle,
          buttonStyle,
          isSelected ? selectedButtonStyle : null,
        ]}
        onPress={onPress}
        disabled={disabled}>
        {leftView}
        {children ? (
          <Text
            style={[
              styles.textStyle,
              textStyle,
              isSelected ? selectedTextStyle : null,
            ]}>
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
    justifyContent: 'flex-start',
    alignItems: 'center',
    flexDirection: 'row',
  },
  textStyle: {
    color: SRXColor.Teal,
    fontSize: 15,
  },
};

export {Button, ButtonStates};

/*
optional parameter:
onPress, buttonStyle, textStyle, children

1. onPress : Event to trigger when button is pressed

2. buttonStyle: border, background for the button, no default value for this

3. textStyle: text size, color, etc. If not provided, will be default to styles.textStyle

4. children: content of button, could be text, or you can also add image

** set own style while disabled, until this class is changed

*/
