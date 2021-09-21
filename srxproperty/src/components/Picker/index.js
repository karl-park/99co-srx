import React, { Component } from "react";
import { View, Picker, Platform } from "react-native";
import PickerIOS from "./PickerIOS";
import PickerAndroid from "./PickerAndroid";
import DialogPicker from "./DialogPicker";
import { PICKER_MODES } from "./Constant";
import PropTypes from "prop-types";

const isIOS = Platform.OS === "ios";

export default class extends Component {
  static pickerMode = PICKER_MODES;

  static propTypes = {
    /**
     * component's data in shape of
     * [object 1, object 2, ...] for PICKER_MODES.SINGLE
     * or
     * [[object 1, object 2, ...], [object 3, object 4, ...], ...] for PICKER_MODES.MULTI
     */
    data: PropTypes.array,


    selectedValue: PropTypes.oneOfType([
      PropTypes.array,//only for MULTI
      PropTypes.arrayOf(PropTypes.array),//onlt for MULTI
      PropTypes.object,
      PropTypes.string,
      PropTypes.number
    ]),

    /**
     * only for iOS (work pending for android)
     * SINGLE mode or MULTI mode
     * if MULTI is selected, will ignore mode
     */
    pickerMode: PropTypes.oneOf(Object.keys(PICKER_MODES)),

    /**
     *  'dialog' or 'dropdown' for android
     *  'dialog' for ios, similar to android dialog picker, else will be normal picker
     */
    mode: PropTypes.string,

    /*
     * title of the dialog
     */
    prompt: PropTypes.string,

    /*
     * only for iOS
     */
    titleOfItem: PropTypes.func,

    /*
     * only for iOS
     */
    valueOfItem: PropTypes.func,

    /*
     * only for iOS
     */
    onCancel: PropTypes.func,

    /*
     * for iOS and Android
     */
    onSubmit: PropTypes.func,
    
    /*
     * for iOS and Android
     * style for the text
     */
    inputStyle: PropTypes.oneOfType([
      PropTypes.object,
      PropTypes.arrayOf(PropTypes.object)
    ]), //input style

    style: PropTypes.oneOfType([
      PropTypes.object,
      PropTypes.arrayOf(PropTypes.object)
    ]), //input container style,

    /*
     * only for iOS
     */
    renderLeftAccessory: PropTypes.func, //method to return left Accessories for left side of text

    /*
     * only for iOS
     */
    renderRightAccessory: PropTypes.func, //method to return left Accessories for right side of text

    /*
     * only for iOS
     */
    titleForSelectedResult: PropTypes.func, //method to return display text

    /*
     * only for Android
     * to use custom dialog picker
     * using this will follow iOS version
     */
    useCustomPicker: PropTypes.bool
  };

  render() {
    const { mode, pickerMode } = this.props;

    const canUseCustomDialog = mode && mode === "dialog" && (!pickerMode || pickerMode == PICKER_MODES.SINGLE);

    if (isIOS) {
      if (canUseCustomDialog) {
        return <DialogPicker {...this.props} />;
      } else {
        return <PickerIOS {...this.props} />;
      }
    } else {
      const { useCustomPicker } = this.props;
      if (useCustomPicker && canUseCustomDialog) {
        return <DialogPicker {...this.props} />;
      }
      else {
        const { onSubmit, style, inputStyle, ...rest } = this.props;
        return <PickerAndroid {...this.props} />;
        // return (
        //   <View style={[style, { justifyContent: "center" }]}>
        //     <Picker {...rest} onValueChange={onSubmit} style={inputStyle}>
        //       {this.props.data.map((option, itemIndex) => {
        //         return (
        //           <Picker.Item
        //             label={this.props.titleOfItem(option, itemIndex, 0)}
        //             value={this.props.valueOfItem(option, itemIndex, 0)}
        //             key={0 + "-" + itemIndex}
        //           />
        //         );
        //       })}
        //     </Picker>
        //  </View>
        // );
      }
    }
  }
}
