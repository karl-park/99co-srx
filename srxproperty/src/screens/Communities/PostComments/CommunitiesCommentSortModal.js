import React, {Component} from 'react';
import {View, ScrollView, TouchableOpacity} from 'react-native';
import PropTypes from 'prop-types';

import {BodyText, FeatherIcon, Heading2, Separator} from '../../../components';
import {SRXColor} from '../../../constants';
import {Spacing} from '../../../styles';
import {CommunitiesCommentConstant} from '../Constants';
import {Navigation} from 'react-native-navigation';

class CommunitiesCommentSortModal extends Component {
  static options(passProps) {
    return {
      layout: {
        backgroundColor: SRXColor.Transparent,
      },
    };
  }

  constructor(props) {
    super(props);

    const {selectedSortOption} = props;
    this.state = {
      sortOption:
        selectedSortOption ?? CommunitiesCommentConstant.defaultCommentSort,
    };

    this.onCloseModal = this.onCloseModal.bind(this);
    this.onSelectSortOption = this.onSelectSortOption.bind(this);
  }

  onCloseModal() {
    Navigation.dismissModal(this.props.componentId);
  }

  onSelectSortOption(item) {
    const {onSelectSortOption} = this.props;
    this.setState({sortOption: item}, () => {
      if (onSelectSortOption) {
        onSelectSortOption(item);
      }
      this.onCloseModal();
    });
  }

  renderSortOptionsHeader() {
    return (
      <View
        style={{
          flexDirection: 'row',
          marginBottom: Spacing.S,
        }}>
        <Heading2>Sort comments by</Heading2>
        <TouchableOpacity
          style={{flex: 1, alignItems: 'flex-end'}}
          onPress={() => this.onCloseModal()}>
          <FeatherIcon name="x" size={24} color={'gray'} />
        </TouchableOpacity>
      </View>
    );
  }

  renderSortOptionsContent(item, index) {
    const {sortOption} = this.state;
    return (
      <TouchableOpacity
        style={{
          flexDirection: 'row',
          marginTop: Spacing.M,
          alignItems: 'center',
          height: 30,
        }}
        onPress={() => this.onSelectSortOption(item)}>
        <BodyText style={{flex: 1}}>{item.key}</BodyText>
        {sortOption.value === item.value ? (
          <FeatherIcon name="check" size={25} color={SRXColor.Teal} />
        ) : null}
      </TouchableOpacity>
    );
  }

  render() {
    return (
      <View style={Styles.overlay}>
        <View style={Styles.mainContainer}>
          <View style={Styles.subContainer}>
            <ScrollView>
              <View>
                {this.renderSortOptionsHeader()}
                <Separator />
                {CommunitiesCommentConstant.sortOptions.map((item, index) => {
                  return this.renderSortOptionsContent(item, index);
                })}
              </View>
            </ScrollView>
          </View>
        </View>
      </View>
    );
  }
}

CommunitiesCommentSortModal.propTypes = {
  /**
   * selected sort option from post details
   */
  selectedSortOption: PropTypes.object.isRequired,
  /**
   * onSelectSortOption to update selected options
   */
  onSelectSortOption: PropTypes.func.isRequired,
};

const Styles = {
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.3)',
  },
  mainContainer: {
    flex: 1,
    backgroundColor: SRXColor.Transparent,
    overflow: 'hidden',
    alignItems: 'center',
    justifyContent: 'center',
  },
  subContainer: {
    width: '90%',
    opacity: 1,
    borderRadius: 10,
    padding: Spacing.M,
    backgroundColor: SRXColor.White,
  },
};

export {CommunitiesCommentSortModal};
