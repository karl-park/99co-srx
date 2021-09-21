import React, { Component } from "react";
import { View, Text, TouchableOpacity, Modal } from "react-native";
import SafeAreaView from "react-native-safe-area-view";
import PropTypes from "prop-types";
import { PICKER_MODES } from "./Constant";
import { SRXColor } from "../../constants";
import Picker from "react-native-wheel-picker";
import { Spacing } from "../../styles";
import { Heading2, BodyText, SmallBodyText } from "../../components";

export default class PickerAndroid extends Component {
  static propTypes = {
    /**
     * component's data in shape of
     * [object 1, object 2, ...] for PICKER_MODES.SINGLE
     * or
     * [[object 1, object 2, ...], [object 3, object 4, ...], ...] for PICKER_MODES.MULTI
     */
    data: PropTypes.array,

    selectedValue: PropTypes.oneOfType([
      PropTypes.array,
      PropTypes.arrayOf(PropTypes.array),
      PropTypes.object,
      PropTypes.string,
      PropTypes.number
    ]),

    /**
     * SINGLE mode or MULTI mode
     */
    pickerMode: PropTypes.oneOf(Object.keys(PICKER_MODES)),

    titleOfItem: PropTypes.func,

    valueOfItem: PropTypes.func,

    onCancel: PropTypes.func,

    onSubmit: PropTypes.func,

    inputStyle: PropTypes.oneOfType([
      PropTypes.object,
      PropTypes.arrayOf(PropTypes.object)
    ]), //input style

    style: PropTypes.oneOfType([
      PropTypes.object,
      PropTypes.arrayOf(PropTypes.object)
    ]), //input container style,

    renderLeftAccessory: PropTypes.func, //method to return left Accessories for left side of text

    renderRightAccessory: PropTypes.func, //method to return left Accessories for right side of text

    titleForSelectedResult: PropTypes.func //method to return display text
  };

  static defaultProps = {
    data: [],
    pickerMode: PICKER_MODES.SINGLE
  };

  constructor(props) {
    super(props);

    const { selectedValue, pickerMode } = this.props;

    var copiedSelectedValue = selectedValue;

    if (!selectedValue && pickerMode == PICKER_MODES.MULTI) {
      copiedSelectedValue = [];
    }

    this.state = {
      visible: false,
      selectedValue: copiedSelectedValue
    };
  }

  componentWillReceiveProps(nextProps) {
    let currentValue = this.state.selectedValue;

    if (nextProps.selectedValue !== currentValue) {
      this.setState({
        selectedValue: nextProps.selectedValue
      });
    }
  }

  toggle() {
    this.setState({ visible: !this.state.visible });
  }

  show() {
    this.setState({ visible: true });
  }

  hide() {
    this.setState({ visible: false });
  }

  onValueChange = (itemValue, itemIndex, componentIndex) => {
    const { selectedValue } = this.state;
    const { pickerMode } = this.props;

    if (pickerMode == PICKER_MODES.MULTI) {
      const copiedSelectedValue = [...selectedValue];

      copiedSelectedValue[componentIndex] = itemValue;

      this.setState({
        selectedValue: copiedSelectedValue
      });
    } else {
      this.setState({
        selectedValue: itemValue
      });
    }
  };

  onCancelPress() {
    this.hide();

    const { selectedValue, onCancel } = this.props;

    this.setState({
      selectedValue: selectedValue
    });

    onCancel && onCancel();
  }

  onSubmitPress() {
    this.hide();

    const { onSubmit } = this.props;

    onSubmit && onSubmit(this.state.selectedValue);
  }

  renderHeaderBar() {
    const { prompt } = this.props;
    return (
      <View
        style={{
          height: 45,
          paddingLeft: Spacing.L,
          paddingRight: Spacing.L,
          flexDirection: "row",
          alignItems: "center"
        }}
      >
        <Heading2>{prompt}</Heading2>
      </View>
    );
  }

  renderToolBar() {
    // const { onDone, onCancel, topBarProps } = this.props;
    return (
      <View
        style={{
          height: 45,
          paddingLeft: Spacing.L,
          paddingRight: Spacing.L,
          flexDirection: "row",
          justifyContent: "flex-end",
          alignItems: "center"
        }}
      >
        <TouchableOpacity
          onPress={() => this.onCancelPress()}
          style={{
            paddingVertical: Spacing.XS,
            paddingHorizontal: Spacing.XS / 2,
            marginRight: Spacing.L
          }}
        >
          <BodyText style={{ color: SRXColor.Teal }}>Cancel</BodyText>
        </TouchableOpacity>
        <TouchableOpacity
          onPress={() => this.onSubmitPress()}
          style={{
            paddingVertical: Spacing.XS,
            paddingHorizontal: Spacing.XS / 2
          }}
        >
          <BodyText style={{ color: SRXColor.Teal }}>Done</BodyText>
        </TouchableOpacity>
      </View>
    );
  }

  renderPicker() {
    const { selectedValue } = this.state;
    const { data, pickerMode } = this.props;

    if (pickerMode == PICKER_MODES.MULTI) {
      return (
        <View
          style={{
            justifyContent: "center",
            alignItems: "center",
            alignSelf: "center",
            paddingTop: Spacing.S,
            paddingBottom: Spacing.S
          }}
        >
          <View
            style={{
              flexDirection: "row",
              alignItems: "center",
              justifyContent: "center",
              paddingLeft: Spacing.L,
              paddingRight: Spacing.L
            }}
          >
            {data.map((items, componentIndex) => {
              return this.renderComponent(
                items,
                componentIndex,
                selectedValue[componentIndex]
              );
            })}
          </View>

          {/* Highlight color */}
          <View
            style={{
              position: "absolute",
              backgroundColor: "rgba(0, 0, 0, 0.2)",
              height: 38,
              width: "100%"
            }}
          />
        </View>
      );
    } else {
      return (
        <View
          style={{
            flexDirection: "row",
            alignItems: "center",
            justifyContent: "center"
          }}
        >
          {this.renderComponent(data, 0, selectedValue)}
        </View>
      );
    }
  }

  renderComponent(items, componentIndex, selectedValue) {
    const { valueOfItem, titleOfItem, pickerMode } = this.props;

    //force it to be string
    var selectedValueInString = selectedValue;
    if (!isNaN(selectedValueInString)) {
      selectedValueInString = selectedValueInString.toString();
    }

    return (
      <View style={{ flexDirection: "row", width: "50%" }} key={componentIndex}>
        <Picker
          key={componentIndex}
          data={items}
          itemSpace={26}
          itemStyle={{ color: SRXColor.Teal, fontSize: 16 }}
          style={{ width: "90%", height: 150 }}
          selectedValue={selectedValue}
          onValueChange={(itemValue, itemIndex) =>
            this.onValueChange(itemValue, itemIndex, componentIndex)
          }
        >
          {items.map((option, itemIndex) => {
            //force it to be string
            var itemValueInString = valueOfItem(
              option,
              itemIndex,
              componentIndex
            );
            if (!isNaN(itemValueInString)) {
              itemValueInString = itemValueInString.toString();
            }
            return (
              <Picker.Item
                label={titleOfItem(option, itemIndex, componentIndex)}
                value={itemValueInString}
                key={componentIndex + "-" + itemIndex}
              />
            );
          })}
        </Picker>
        {(componentIndex + 1) % 2 !== 0 && pickerMode == PICKER_MODES.MULTI ? (
          <SmallBodyText style={{ alignSelf: "center" }}>to</SmallBodyText>
        ) : null}
      </View>
    );
  }

  /*
   * return an array of selected objects
   */
  selectedValueObject() {
    const { pickerMode, data, valueOfItem, selectedValue } = this.props;
    const array = [];
    if (pickerMode == PICKER_MODES.MULTI) {
      if (Array.isArray(selectedValue)) {
        selectedValue.map((eachValue, index) => {
          const componentsArray = data[index];

          componentsArray.map((item, itemIndex) => {
            if (valueOfItem(item, itemIndex, index) == eachValue) {
              array.push(item);
            }
          });
        });
      }
    } else {
      for (i = 0; i < data.length; i++) {
        if (valueOfItem(data[i], i, 0) == selectedValue) {
          array.push(data[i]);
        }
      }
    }

    return array;
  }

  textForSelectedValue() {
    const { titleForSelectedResult } = this.props;

    if (titleForSelectedResult) {
      const selectedObjects = this.selectedValueObject();

      return titleForSelectedResult(selectedObjects);
    } else {
      return this.defaultTextForSelectedValue();
    }
  }

  defaultTextForSelectedValue() {
    const {
      pickerMode,
      data,
      valueOfItem,
      titleOfItem,
      selectedValue
    } = this.props;

    var label;

    const selectedObjects = this.selectedValueObject();
    console.log(selectedObjects);
    if (pickerMode == PICKER_MODES.MULTI) {
      const array = [];

      label = selectedObjects
        .map((item, index) => {
          return titleOfItem(item, data[index].indexOf(item), index);
        })
        .join(", ");
    } else {
      label = data.map((object, index) => {
        if (valueOfItem(object, index, 0) == selectedValue) {
          return titleOfItem(object, index, 0);
        }
      });
    }

    return label || "";
  }

  render() {
    const {
      style,
      inputStyle,
      renderLeftAccessory,
      renderRightAccessory
    } = this.props;

    return (
      <TouchableOpacity
        style={[{ alignItems: "center" }, { flexDirection: "row" }, style]}
        onPress={() => this.toggle()}
      >
        {renderLeftAccessory && renderLeftAccessory()}
        <Text style={[{ flex: 1 }, inputStyle]}>
          {this.textForSelectedValue()}
        </Text>
        {renderRightAccessory && renderRightAccessory()}
        <Modal
          animationType="fade"
          transparent={true}
          visible={this.state.visible}
          onRequestClose={() => {
            this.onCancelPress();
          }}
        >
          <View
            style={{
              flex: 1,
              backgroundColor: "rgba(0, 0, 0, 0.3)",
              justifyContent: "center"
            }}
          >
            <SafeAreaView>
              <View
                style={{
                  backgroundColor: "white",
                  marginLeft: Spacing.M,
                  marginRight: Spacing.M,
                  borderRadius: 5
                }}
              >
                {this.renderHeaderBar()}
                {this.renderPicker()}
                {this.renderToolBar()}
              </View>
            </SafeAreaView>
          </View>
        </Modal>
      </TouchableOpacity>
    );
  }
}
