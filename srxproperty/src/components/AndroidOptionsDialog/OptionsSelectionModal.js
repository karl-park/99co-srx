import React, {Component} from 'react';
import {View, ScrollView, TouchableOpacity, StyleSheet} from 'react-native';
import PropTypes from 'prop-types';
import {Navigation} from 'react-native-navigation';

import {FeatherIcon, Heading2, BodyText} from '../../components';
import {SRXColor} from '../../constants';
import {ModalStyles, Spacing} from '../../styles';
import {ObjectUtil} from '../../utils';

class OptionsSelectionModal extends Component {
  static options(passProps) {
    console.log(passProps);
    return {
      layout: {
        backgroundColor: SRXColor.Transparent,
      },
    };
  }

  constructor(props) {
    super(props);

    this.onClickItem = this.onClickItem.bind(this);
  }

  onCloseModal() {
    Navigation.dismissModal(this.props.componentId);
  }

  onClickItem(item) {
    const {onSelectOption} = this.props;
    if (onSelectOption) {
      onSelectOption(item);
      this.onCloseModal();
    }
  }

  renderHeader() {
    const {title} = this.props;
    return (
      <View
        style={{
          flexDirection: 'row',
        }}>
        {!ObjectUtil.isEmpty(title) ? (
          <Heading2 style={{marginBottom: Spacing.S}}>{title}</Heading2>
        ) : null}
        <TouchableOpacity
          style={{flex: 1, alignItems: 'flex-end'}}
          onPress={() => this.onCloseModal()}>
          <FeatherIcon name="x" size={24} color={SRXColor.Gray} />
        </TouchableOpacity>
      </View>
    );
  }

  renderOptions() {
    const {options} = this.props;
    if (!ObjectUtil.isEmpty(options) && Array.isArray(options)) {
      return (
        <View>
          {options.map((item, index) => {
            return this.renderItem(item, index);
          })}
        </View>
      );
    }
  }

  renderItem(item, index) {
    if (!ObjectUtil.isEmpty(item)) {
      return (
        <TouchableOpacity
          key={index}
          style={{
            flexDirection: 'row',
            alignItems: 'center',
            height: 45,
          }}
          onPress={() => this.onClickItem(item)}>
          <BodyText>{item.key}</BodyText>
        </TouchableOpacity>
      );
    }
    return <View />;
  }

  render() {
    return (
      <View style={ModalStyles.overlay}>
        <View style={ModalStyles.mainContainer}>
          <View style={ModalStyles.subContainer}>
            <ScrollView>
              <View>
                {this.renderHeader()}
                {this.renderOptions()}
              </View>
            </ScrollView>
          </View>
        </View>
      </View>
    );
  }
}

OptionsSelectionModal.propTypes = {
  /**
   * should be array of object with key value
   * {
   *    key : "SMS",
   *    value : int or string
   * }
   */
  options: PropTypes.arrayOf(Object).isRequired,

  /***
   *  Optional
   */
  selectedOption: PropTypes.object,

  /***
   *  Optional
   */
  title: PropTypes.string,

  /***
   * onSelectOption to update the selected options
   */
  onSelectOption: PropTypes.func.isRequired,
};

export {OptionsSelectionModal};
