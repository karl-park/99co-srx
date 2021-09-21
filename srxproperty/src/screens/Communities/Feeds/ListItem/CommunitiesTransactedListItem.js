import React, {Component} from 'react';
import {View, FlatList, Text} from 'react-native';
import {SRXColor, IS_IOS} from '../../../../constants';
import {
  ExtraSmallBodyText,
  Avatar,
  BodyText,
  Separator,
  Button,
  FeatherIcon,
  OcticonsIcon,
  SmallBodyText,
  HorizontalFlatList,
} from '../../../../components';
import {Spacing} from '../../../../styles';
import PropTypes from 'prop-types';
import {CommunityPostPO} from '../../../../dataObject';
import {ObjectUtil} from '../../../../utils';
import {CommunitiesTransactedListingListItem} from './Contents';

class CommunitiesTransactedListItem extends Component {
  constructor(props) {
    super(props);
    this.renderItem = this.renderItem.bind(this);
    this.onSharePost = this.onSharePost.bind(this);
    this.onEnquiryListing = this.onEnquiryListing.bind(this);
  }

  onSharePost() {
    const {onSharePressed, post} = this.props;
    onSharePressed(post);
  }

  onEnquiryListing(listing) {
    const {onEnquireListing} = this.props;

    if (onEnquireListing) {
      onEnquireListing(listing);
    }
  }

  renderCellHeader() {
    return (
      <View
        style={{
          flexDirection: 'row',
          alignItems: 'center',
          marginBottom: Spacing.XS,
          paddingHorizontal: Spacing.M,
          justifyContent: 'space-between',
        }}>
        <BodyText>Just transacted in your neighborhood</BodyText>
        {this.renderPostDate()}
      </View>
    );
  }

  renderPostDate() {
    const {post} = this.props;

    return (
      <ExtraSmallBodyText
        style={{
          marginLeft: Spacing.XS,
          color: SRXColor.Gray,
        }}>
        {post.getDateDisplay()}
      </ExtraSmallBodyText>
    );
  }

  renderItem({item}) {
    return (
      <CommunitiesTransactedListingListItem
        listing={item}
        onEnquireListing={this.onEnquiryListing}
      />
    );
  }

  renderContent() {
    const {post} = this.props;
    const {transactedListings} = post;
    let listings = [];
    if (!ObjectUtil.isEmpty(transactedListings)) {
      listings = transactedListings;
    }
    return (
      <FlatList
        style={[{paddingVertical: Spacing.XS / 2}]}
        horizontal={true}
        showsHorizontalScrollIndicator={false}
        keyExtractor={item => item.id}
        data={listings}
        renderItem={this.renderItem}
        ItemSeparatorComponent={() => <View style={{width: Spacing.S}} />}
        ListHeaderComponent={<View style={{width: Spacing.M}} />}
        ListFooterComponent={<View style={{width: Spacing.M}} />}
      />
    );
  }

  render() {
    return (
      <View
        style={{
          borderRadius: 10,
          backgroundColor: SRXColor.White,
          paddingVertical: Spacing.S,
          marginBottom: Spacing.XS,
        }}>
        {this.renderCellHeader()}
        {this.renderContent()}
      </View>
    );
  }
}

CommunitiesTransactedListItem.propTypes = {
  post: PropTypes.instanceOf(CommunityPostPO),

  /**
   * callback when Share button pressed, will pass Community in the method
   */
  onSharePressed: PropTypes.func,

  /**
   * callback when Enquiry button pressed, will pass ListingPO in the method
   */
  onEnquireListing: PropTypes.func,
};

export {CommunitiesTransactedListItem};
