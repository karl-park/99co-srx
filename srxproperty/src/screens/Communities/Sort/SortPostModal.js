import React, {Component} from 'react';
import {View, TouchableOpacity, ScrollView} from 'react-native';
import SafeAreaView from 'react-native-safe-area-view';
import {Navigation} from 'react-native-navigation';
import PropTypes from 'prop-types';

import {SRXColor} from '../../../constants';
import {FeatherIcon, Separator, Heading2, BodyText} from '../../../components';
import {Spacing} from '../../../styles';
import {CommunitiesConstant} from '../Constants';
import {CommunitiesSponsoredListItem} from '../Feeds/ListItem';
import {ObjectUtil} from '../../../utils';

class SortPostModal extends Component {
  static options(passProps) {
    return {
      layout: {
        backgroundColor: SRXColor.Transparent,
      },
    };
  }

  constructor(props) {
    super(props);

    this.onPressSortBy = this.onPressSortBy.bind(this);
  }

  static defaultProps = {
    selectedOption: CommunitiesConstant.sortOptions[0],
  };

  onPressSortBy(item) {
    const {onSortTypeSelected} = this.props;
    if (onSortTypeSelected) {
      onSortTypeSelected(item);
    }
    this.onCloseModal();
  }

  onCloseModal = () => {
    Navigation.dismissModal(this.props.componentId);
  };

  renderSortPost() {
    console.log(CommunitiesConstant.sortOptions);
    return (
      <View style={Styles.sortPostModalSubContainer}>
        <View style={Styles.sortPostRowContainer}>
          <Heading2>Sort post by</Heading2>

          <TouchableOpacity
            style={{flex: 1, alignItems: 'flex-end'}}
            onPress={() => this.onCloseModal()}>
            <FeatherIcon name="x" size={24} color={'gray'} />
          </TouchableOpacity>
        </View>
        <Separator />
        <ScrollView>
          <View style={{marginTop: Spacing.S, paddingHorizontal: Spacing.S}}>
            {CommunitiesConstant.sortOptions.map((item, index) => {
              return this.renderSortByContent(item, index);
            })}
          </View>
        </ScrollView>
      </View>
    );
  }

  renderSortByContent(item, index) {
    const {selectedOption} = this.props;
    return (
      <TouchableOpacity
        style={{flexDirection: 'row'}}
        key={index}
        onPress={() => this.onPressSortBy(item)}>
        <BodyText style={{marginBottom: Spacing.L, flex: 1}}>
          {item.title}
        </BodyText>
        {!ObjectUtil.isEmpty(selectedOption) &&
        selectedOption.value === item.value ? (
          <FeatherIcon name="check" size={25} color={SRXColor.Teal} />
        ) : null}
      </TouchableOpacity>
    );
  }

  render() {
    return (
      <SafeAreaView style={Styles.sortPostOverlay}>
        <View style={Styles.sortPostModalMainContainer}>
          {this.renderSortPost()}
        </View>
      </SafeAreaView>
    );
  }
}

const Styles = {
  sortPostOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.3)',
  },
  sortPostModalMainContainer: {
    flex: 1,
    backgroundColor: SRXColor.Transparent,
    overflow: 'hidden',
    alignItems: 'center',
    justifyContent: 'center',
  },
  sortPostModalSubContainer: {
    width: '90%',
    opacity: 1,
    borderRadius: 10,
    backgroundColor: SRXColor.White,
  },
  sortPostRowContainer: {
    flexDirection: 'row',
    marginVertical: Spacing.XS,
    paddingHorizontal: Spacing.S,
  },
};

SortPostModal.propTypes = {
  onSortTypeSelected: PropTypes.func,
};

export {SortPostModal};
