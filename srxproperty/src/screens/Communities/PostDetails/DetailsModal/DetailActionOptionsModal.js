import React, {Component} from 'react';
import SafeAreaView from 'react-native-safe-area-view';
import {Navigation} from 'react-native-navigation';
import PropTypes from 'prop-types';

import {
  View,
  ScrollView,
  TouchableWithoutFeedback,
  TouchableOpacity,
} from 'react-native';
import {SRXColor} from '../../../../constants';
import {BodyText} from '../../../../components';
import {Spacing} from '../../../../styles';
import {DetailActionOptionsConstant} from './DetailActionOptionsConstant';

class DetailActionOptionsModal extends Component {
  static defaultProps = {
    options: DetailActionOptionsConstant.actionOptionsForOwnPost
  };

  static options(passProps) {
    return {
      layout: {
        backgroundColor: SRXColor.Transparent,
      },
    };
  }

  onCloseModal = () => {
    Navigation.dismissModal(this.props.componentId);
  };

  onPressEachOption = item => {
    const {onSelectOption} = this.props;
    if (onSelectOption) {
      onSelectOption(item);
    }
    this.onCloseModal();
  };

  renderOptions() {
    const {options} = this.props;
    return (
      <View style={Styles.optionsContainer}>
        <ScrollView>
          <View style={{padding: Spacing.S}}>
            {options.map((item, index) => {
              return this.renderSortByContent(item, index);
            })}
          </View>
        </ScrollView>
      </View>
    );
  }

  renderSortByContent(item, index) {
    return (
      <TouchableOpacity onPress={() => this.onPressEachOption(item)}>
        <View style={{height: 45, justifyContent: 'center'}}>
          <BodyText>{item.title}</BodyText>
        </View>
      </TouchableOpacity>
    );
  }

  render() {
    return (
      <TouchableWithoutFeedback
        style={{flex: 1}}
        onPress={() => this.onCloseModal()}>
        <View style={Styles.mainContainer}>
          <SafeAreaView style={{flex: 1, justifyContent: 'center'}}>
            <View style={Styles.subContainer}>{this.renderOptions()}</View>
          </SafeAreaView>
        </View>
      </TouchableWithoutFeedback>
    );
  }
}

DetailActionOptionsModal.propTypes = {
  onSelectOption: PropTypes.func,
  options: PropTypes.array,
};

const Styles = {
  mainContainer: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.3)',
  },
  subContainer: {
    flex: 1,
    backgroundColor: SRXColor.Transparent,
    overflow: 'hidden',
    alignItems: 'center',
    justifyContent: 'center',
  },
  optionsContainer: {
    width: '90%',
    opacity: 1,
    borderRadius: 10,
    backgroundColor: SRXColor.White,
  },
};

export {DetailActionOptionsModal};
