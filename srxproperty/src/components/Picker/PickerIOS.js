import React, { Component } from "react";
import { View, Text, TouchableOpacity, Modal, Picker } from "react-native";
import SafeAreaView from "react-native-safe-area-view";
import PropTypes from 'prop-types';
import { PICKER_MODES } from "./Constant";

export default class PickerIOS extends Component {
    
    
    static propTypes = {

        /** 
         * component's data in shape of 
         * [object 1, object 2, ...] for PICKER_MODES.SINGLE
         * or 
         * [[object 1, object 2, ...], [object 3, object 4, ...], ...] for PICKER_MODES.MULTI
         */
        data: PropTypes.array,

        selectedValue: PropTypes.oneOfType([PropTypes.array, PropTypes.arrayOf(PropTypes.array) , PropTypes.object, PropTypes.string, PropTypes.number]),

        /**
         * SINGLE mode or MULTI mode
         */
        pickerMode: PropTypes.oneOf(Object.keys(PICKER_MODES)),

        titleOfItem: PropTypes.func,

        valueOfItem: PropTypes.func,

        onCancel: PropTypes.func,

        onSubmit: PropTypes.func,

        inputStyle: PropTypes.oneOfType([PropTypes.object, PropTypes.arrayOf(PropTypes.object)]),//input style

        style: PropTypes.oneOfType([PropTypes.object, PropTypes.arrayOf(PropTypes.object)]),//input container style,

        renderLeftAccessory: PropTypes.func, //method to return left Accessories for left side of text

        renderRightAccessory: PropTypes.func, //method to return left Accessories for right side of text

        titleForSelectedResult: PropTypes.func, //method to return display text
    }

    static defaultProps = {
        data: [],
        pickerMode: PICKER_MODES.SINGLE,
    }

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
        }
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
        this.setState({visible: !this.state.visible });
    }

    show() {
        this.setState({ visible: true });
    }

    hide() {
        this.setState({ visible: false });
    }

    onValueChange(itemValue, itemIndex, componentIndex) {

        const { selectedValue } = this.state;
        const { pickerMode } = this.props;

        if (pickerMode == PICKER_MODES.MULTI) {

            const copiedSelectedValue = [...selectedValue];

            copiedSelectedValue[componentIndex] = itemValue;

            this.setState({
                selectedValue: copiedSelectedValue
            });
        }
        else {
            this.setState({
                selectedValue: itemValue
            });
        }
    }

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

    renderToolBar() {
        // const { onDone, onCancel, topBarProps } = this.props;

        return (
            <View style={{ height: 44, backgroundColor: "#f7f7f7", paddingLeft: 15, paddingRight: 15, flexDirection: "row", justifyContent: "space-between", alignItems: "center", borderColor: "#dbdbdb", borderTopWidth: 1, borderBottomWidth: 1 }}>
                <TouchableOpacity onPress={() => this.onCancelPress()}>
                    <Text style={{ color: 'blue' }}>Cancel</Text>
                </TouchableOpacity>
                <TouchableOpacity onPress={() => this.onSubmitPress()}>
                    <Text style={{ color: 'blue' }}>Done</Text>
                </TouchableOpacity>
            </View>
        );
    }

    renderPicker() {

        const { selectedValue } = this.state;
        const { data, pickerMode } = this.props;

        if (pickerMode == PICKER_MODES.MULTI) {
            return (
                <View style={{ flexDirection: "row" }}>
                    {
                        data.map((items, componentIndex) => {
                            return this.renderComponent(items, componentIndex, selectedValue[componentIndex]);
                        })
                    }
                </View>
            );            
        }
        else {
            return (
                <View style={{ flexDirection: "row" }}>
                    {this.renderComponent(data, 0, selectedValue)}
                </View>
            );
        }

        
    }

    renderComponent(items, componentIndex, selectedValue ) {
        const { valueOfItem, titleOfItem } = this.props;
        console.log(this.props);
        console.log(selectedValue);
        console.log(typeof(selectedValue));

        //force it to be string
        var selectedValueInString = selectedValue;
        if (!isNaN(selectedValueInString)) {
            selectedValueInString = selectedValueInString.toString();
        }
        return (
            <Picker
                key={componentIndex}
                style={{ flex: 1 }}
                selectedValue={selectedValueInString}
                onValueChange={(itemValue, itemIndex) => this.onValueChange(itemValue, itemIndex, componentIndex)}
            >
                {
                    items.map((option, itemIndex) => {
                        //force it to be string
                        var itemValueInString = valueOfItem(option, itemIndex, componentIndex);
                        if (!isNaN(itemValueInString)) {
                            itemValueInString = itemValueInString.toString();
                        }
                        return <Picker.Item label={titleOfItem(option, itemIndex, componentIndex)} value={itemValueInString} key={componentIndex + "-" + itemIndex} />;
                    })
                }
            </Picker>        
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
        }
        else {
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
        }
        else {
            return this.defaultTextForSelectedValue();
        }
    }

    defaultTextForSelectedValue() {
        const { pickerMode, data, valueOfItem, titleOfItem, selectedValue } = this.props;

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
        }
        else {
            label = data.map((object, index) => {
              if (valueOfItem(object, index, 0) == selectedValue) {
                return titleOfItem(object, index, 0);
              }
            });
        }

        return label || '';
    }

    render() {
        const { style, inputStyle, renderLeftAccessory, renderRightAccessory } = this.props;

        return (
            <TouchableOpacity style={[{ alignItems: "center" }, { flexDirection: "row" }, style]} onPress={() => this.toggle()}>
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
                        this.onCancelPress()
                    }}>
                    <View style={{ flex: 1, backgroundColor: 'rgba(0, 0, 0, 0.3)', justifyContent: "flex-end"}}>
                        <SafeAreaView style={{backgroundColor: 'white'}}>
                            {this.renderToolBar()}
                            {this.renderPicker()}
                        </SafeAreaView>
                    </View>
                </Modal>
            </TouchableOpacity>
        );
    }
}