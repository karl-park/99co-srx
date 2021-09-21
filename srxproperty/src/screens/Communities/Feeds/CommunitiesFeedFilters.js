import React, {Component} from 'react';
import {View} from 'react-native';
import PropTypes from 'prop-types';
import {Button, FeatherIcon, ExtraSmallBodyText} from '../../../components';
import {SRXColor} from '../../../constants';
import {Spacing, Typography} from '../../../styles';
import {CommunitiesStack} from '../../../config';
import {CommunityItem} from '../../../dataObject';
import {CommunitiesConstant} from '../Constants';

class CommunitiesFeedFilters extends Component {
  static defaultProps = {
    selectedSortType: CommunitiesConstant.sortOptions[0],
    selectedCommunity: CommunitiesConstant.singaporeCommunity,
    postsTotal: 0,
  };

  constructor(props) {
    super(props);
    this.onSortTypeSelected = this.onSortTypeSelected.bind(this);
    this.onChooseCommunity = this.onChooseCommunity.bind(this);
    this.onCommunitySelected = this.onCommunitySelected.bind(this);
  }

  onSortTypeSelected(item) {
    const {onSortTypeSelected} = this.props;
    if (onSortTypeSelected) {
      onSortTypeSelected(item);
    }
  }

  onChooseCommunity() {
    const {
      selectedCommunity,
      onAddPropertyPressed,
      hasCommunities,
    } = this.props;
    if (hasCommunities) {
      CommunitiesStack.showCommunityOptions({
        initialSelectedCommunity: selectedCommunity,
        onCommunitySelected: this.onCommunitySelected,
      });
    } else {
      CommunitiesStack.showCommunitiesModal({
        title: 'Feeds in',
        selectedCommunity: selectedCommunity,
        onCommunitySelected: this.onCommunitySelected,
        onAddPropertyPressed: onAddPropertyPressed,
      });
    }
  }

  onCommunitySelected(community) {
    const {onCommunitySelected} = this.props;
    if (onCommunitySelected) {
      onCommunitySelected(community);
    }
  }

  renderCommunitiesOptions() {
    const {selectedCommunity, communities} = this.props;
    return (
      <View
        style={{
          flexDirection: 'row',
          alignItems: 'center',
        }}>
        <FeatherIcon
          name={'map-pin'}
          size={16}
          color={SRXColor.Purple}
          style={{marginRight: Spacing.XS / 2}}
        />
        <Button
          buttonStyle={{
            backgroundColor: SRXColor.White,
            height: 24,
            borderRadius: 12,
            paddingHorizontal: Spacing.XS,
            borderWidth: 1,
            borderColor: SRXColor.Teal,
          }}
          textStyle={{
            fontSize: Typography.ExtraSmallBody.fontSize,
          }}
          rightView={
            <FeatherIcon
              name={'chevron-down'}
              size={16}
              color={SRXColor.Gray}
            />
          }
          onPress={this.onChooseCommunity}>
          {selectedCommunity.id == CommunitiesConstant.singaporeCommunity.id
            ? CommunitiesConstant.singaporeCommunity.name
            : selectedCommunity.name}
        </Button>
        {this.renderPostCount()}
      </View>
    );
  }

  renderPostCount() {
    const {postsTotal} = this.props;
    if (postsTotal > 0) {
      return (
        <ExtraSmallBodyText style={{marginLeft: Spacing.XS / 2}}>
          {postsTotal} posts
        </ExtraSmallBodyText>
      );
    }
  }

  renderSortButtons() {
    const {selectedSortType} = this.props;
    return (
      <Button
        textStyle={{
          fontSize: Typography.ExtraSmallBody.fontSize,
          color: SRXColor.Black,
        }}
        rightView={
          <FeatherIcon name={'chevron-down'} size={16} color={SRXColor.Black} />
        }
        onPress={() =>
          CommunitiesStack.showSortModal({
            selectedOption: selectedSortType,
            onSortTypeSelected: this.onSortTypeSelected,
          })
        }>
        {selectedSortType.title}
      </Button>
    );
  }

  render() {
    return (
      <View
        style={{
          paddingHorizontal: Spacing.S,
          paddingVertical: Spacing.S,
          flexDirection: 'row',
          justifyContent: 'space-between',
          alignItems: 'center',
        }}>
        {this.renderCommunitiesOptions()}
        {this.renderSortButtons()}
      </View>
    );
  }
}

CommunitiesFeedFilters.propTypes = {
  /**
   * A boolean to indicate whether there is communities
   * This parameter is not intended to be added, this is added due to different screen for community selection
   */
  hasCommunities: PropTypes.bool,

  /**
   * CommunityItem that are selected
   */
  selectedCommunity: PropTypes.oneOfType([
    PropTypes.instanceOf(CommunityItem),
    PropTypes.object,
  ]),

  /**
   * Sort type object that are selected, object must be from CommunitiesConstant.SortOptions
   */
  selectedSortType: PropTypes.object,

  /**
   * Number of posts to be display
   */
  postsTotal: PropTypes.number,
};

export {CommunitiesFeedFilters};
