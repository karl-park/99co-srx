import React, {Component} from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  TouchableHighlight,
  TouchableWithoutFeedback,
  Modal,
  FlatList,
} from 'react-native';
import SafeAreaView from 'react-native-safe-area-view';
import PropTypes from 'prop-types';
import {Separator} from '../../components';
import {SRXColor} from '../../constants';
import {Spacing} from '../../styles';

export default class DialogPicker extends Component {
  static propTypes = {
    /**
     * component's data in shape of
     * [object 1, object 2, ...]
     */
    data: PropTypes.array,

    selectedValue: PropTypes.oneOfType([
      PropTypes.object,
      PropTypes.string,
      PropTypes.number,
    ]),

    prompt: PropTypes.string,

    titleOfItem: PropTypes.func,

    valueOfItem: PropTypes.func,

    onCancel: PropTypes.func,

    onSubmit: PropTypes.func,

    inputStyle: PropTypes.oneOfType([
      PropTypes.object,
      PropTypes.arrayOf(PropTypes.object),
    ]), //input style

    style: PropTypes.oneOfType([
      PropTypes.object,
      PropTypes.arrayOf(PropTypes.object),
    ]), //input container style,

    renderLeftAccessory: PropTypes.func, //method to return left Accessories for left side of text

    renderRightAccessory: PropTypes.func, //method to return left Accessories for right side of text

    titleForSelectedResult: PropTypes.func, //method to return display text
  };

  static defaultProps = {
    data: [],
  };

  constructor(props) {
    super(props);

    const {selectedValue} = this.props;

    this.state = {
      visible: false,
      selectedValue,
    };
  }

  componentWillReceiveProps(nextProps) {
    let currentValue = this.state.selectedValue;

    if (nextProps.selectedValue !== currentValue) {
      this.setState({
        selectedValue: nextProps.selectedValue,
      });
    }
  }

  toggle() {
    this.setState({visible: !this.state.visible});
  }

  show() {
    this.setState({visible: true});
  }

  hide() {
    this.setState({visible: false});
  }

  onValueSelected(itemValue, itemIndex) {
    this.setState({
      selectedValue: itemValue,
    });

    this.hide();

    const {onSubmit} = this.props;

    onSubmit && onSubmit(itemValue);
  }

  onCancelPress() {
    this.hide();

    const {selectedValue, onCancel} = this.props;

    this.setState({
      selectedValue: selectedValue,
    });

    onCancel && onCancel();
  }

  /*
   * return an array of selected objects
   */
  selectedValueObject() {
    const {pickerMode, data, valueOfItem, selectedValue} = this.props;
    const array = [];
    for (i = 0; i < data.length; i++) {
      if (valueOfItem(data[i], i, 0) == selectedValue) {
        array.push(data[i]);
      }
    }

    return array;
  }

  textForSelectedValue() {
    const {titleForSelectedResult} = this.props;

    if (titleForSelectedResult) {
      const selectedObjects = this.selectedValueObject();

      return titleForSelectedResult(selectedObjects);
    } else {
      return this.defaultTextForSelectedValue();
    }
  }

  defaultTextForSelectedValue() {
    const {data, valueOfItem, titleOfItem, selectedValue} = this.props;

    var label;

    label = data.map((object, index) => {
      if (valueOfItem(object, index) == selectedValue) {
        return titleOfItem(object, index);
      }
    });
    return label || '';
  }

  renderDialogItem = ({item, index}) => {
    const {titleOfItem} = this.props;

    return (
      <TouchableHighlight
        underlayColor={'#DDDDDD'}
        onPress={() => this.onValueSelected(item, index)}>
        <View
          style={{
            height: 40,
            justifyContent: 'center',
            paddingLeft: Spacing.S,
          }}>
          <Text style={{fontSize: 16}}>{titleOfItem(item, index)}</Text>
        </View>
      </TouchableHighlight>
    );
  };

  renderDialog() {
    const {prompt, data, height} = this.props;

    return (
      <View
        style={{
          marginHorizontal: 15,
          marginTop: 20,
          marginBottom: 20,
          backgroundColor: 'white',
          paddingLeft: 15,
          paddingTop: 10,
          shadowColor: 'black',
          shadowOffset: {width: 3, height: 3},
          shadowOpacity: 0.5,
          shadowRadius: 3,
        }}>
        {prompt ? (
          <View style={{justifyContent: 'center', height: 40}}>
            <Text style={{fontSize: 16, fontWeight: 'bold'}}>{prompt}</Text>
          </View>
        ) : null}

        <FlatList
          data={data}
          renderItem={this.renderDialogItem}
          style={{paddingRight: 15, paddingBottom: 15, height: height}}
          ItemSeparatorComponent={() => <Separator />}
        />
      </View>
    );
  }

  render() {
    const {
      style,
      inputStyle,
      renderLeftAccessory,
      renderRightAccessory,
    } = this.props;

    return (
      <TouchableOpacity
        style={[{alignItems: 'center'}, {flexDirection: 'row'}, style]}
        onPress={() => this.toggle()}>
        {renderLeftAccessory && renderLeftAccessory()}
        <Text style={[{flex: 1}, inputStyle]}>
          {this.textForSelectedValue()}
        </Text>
        {renderRightAccessory && renderRightAccessory()}
        <Modal
          animationType="fade"
          transparent={true}
          visible={this.state.visible}
          onRequestClose={() => {
            this.onCancelPress();
          }}>
          <TouchableWithoutFeedback
            style={{flex: 1}}
            onPress={() => this.onCancelPress()}>
            <View style={{flex: 1, backgroundColor: 'rgba(0, 0, 0, 0.3)'}}>
              <SafeAreaView style={{flex: 1, justifyContent: 'center'}}>
                {this.renderDialog()}
              </SafeAreaView>
            </View>
          </TouchableWithoutFeedback>
        </Modal>
      </TouchableOpacity>
    );
  }
}
