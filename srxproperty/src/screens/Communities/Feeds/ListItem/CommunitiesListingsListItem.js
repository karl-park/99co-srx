import React, {Component} from 'react';
import {View, Image, TouchableOpacity} from 'react-native';
import {SRXColor} from '../../../../constants';
import {
  ExtraSmallBodyText,
  Avatar,
  BodyText,
  Separator,
  Button,
  FeatherIcon,
  OcticonsIcon,
  SmallBodyText,
  TouchableHighlight,
} from '../../../../components';
import {Spacing} from '../../../../styles';
import PropTypes from 'prop-types';
import {CommunityPostPO} from '../../../../dataObject';
import {ObjectUtil} from '../../../../utils';
import {CommunitiesConstant} from '../../Constants';
import {
  CommunitiesPostItemContent,
  CommunitiesPostItemCreator,
} from './Contents';

class CommunitiesListingsListItem extends Component {
  constructor(props) {
    super(props);

    this.onPressPostCreator = this.onPressPostCreator.bind(this);
    this.onSharePost = this.onSharePost.bind(this);
    this.onEnquiryListing = this.onEnquiryListing.bind(this);
    this.onViewListingDetail = this.onViewListingDetail.bind(this);
    this.onPressGotoListings = this.onPressGotoListings.bind(this);
  }

  onPressPostCreator() {
    const {onPostCreatorPressed, post} = this.props;
    if (onPostCreatorPressed) {
      onPostCreatorPressed(post);
    }
  }

  onSharePost() {
    const {onSharePressed, post} = this.props;
    if (onSharePressed) {
      onSharePressed(post);
    }
  }

  onEnquiryListing() {
    const {onEnquireListing, post} = this.props;
    const {listing} = post;

    if (onEnquireListing && !ObjectUtil.isEmpty(listing)) {
      onEnquireListing(listing);
    }
  }

  onViewListingDetail() {
    const {onViewListingPressed, post} = this.props;
    const {listing} = post;

    if (!ObjectUtil.isEmpty(listing) && onViewListingPressed) {
      onViewListingPressed(listing);
    }
  }

  onPressGotoListings() {
    const {onGotoListingsPressed, post} = this.props;

    if (onGotoListingsPressed) {
      onGotoListingsPressed(post);
    }
  }

  renderCellHeader() {
    return (
      <View
        style={{
          flexDirection: 'row',
          alignItems: 'center',
          marginBottom: Spacing.XS,
        }}>
        {this.renderPostCreator()}
        {this.renderPostDate()}
      </View>
    );
  }

  renderPostCreator() {
    const {post} = this.props;
    if (!ObjectUtil.isEmpty(post.user)) {
      return (
        <CommunitiesPostItemCreator
          post={post}
          onPress={this.onPressPostCreator}
        />
      );
    }
  }

  renderPostDate() {
    const {post} = this.props;
    const {listing, community} = post;

    let saleRentType = null;
    if (!ObjectUtil.isEmpty(listing)) {
      saleRentType = listing.type === 'S' ? 'For Sale' : 'For Rent';
    }
    return (
      <ExtraSmallBodyText
        style={{marginLeft: Spacing.XS, color: SRXColor.Gray}}>
        {!ObjectUtil.isEmpty(saleRentType) ? (
          <ExtraSmallBodyText style={{color: SRXColor.Purple}}>
            {saleRentType}
          </ExtraSmallBodyText>
        ) : null}
        {!ObjectUtil.isEmpty(community) ? ' in ' : ''}
        {!ObjectUtil.isEmpty(community) ? (
          <ExtraSmallBodyText
            style={{textTransform: 'capitalize', color: SRXColor.Gray}}>
            {community.name}
          </ExtraSmallBodyText>
        ) : null}
        {/* · {post.getDateDisplay()} */}
      </ExtraSmallBodyText>
    );
  }

  renderActions() {
    return (
      <View
        style={{
          flexDirection: 'row',
          alignItems: 'center',
          justifyContent: 'space-between',
        }}>
        <Button
          buttonStyle={{
            paddingVertical: Spacing.XS,
            marginRight: Spacing.L,
          }}
          onPress={this.onEnquiryListing}>
          Enquire now
        </Button>
        <Button
          rightView={
            <FeatherIcon
              name={'external-link'}
              size={20}
              color={SRXColor.Teal}
              style={{marginLeft: Spacing.XS / 2}}
            />
          }
          buttonStyle={{
            paddingVertical: Spacing.XS,
            marginRight: Spacing.L,
          }}
          onPress={this.onPressGotoListings}>
          Go to listings
        </Button>
        <Button
          buttonStyle={{
            paddingVertical: Spacing.XS,
          }}
          onPress={this.onSharePost}>
          Share
        </Button>
      </View>
    );
  }

  render() {
    const {post} = this.props;
    return (
      <TouchableHighlight
        style={{
          borderRadius: 10,
          backgroundColor: SRXColor.White,
          marginBottom: Spacing.XS,
          overflow: 'hidden',
        }}
        onPress={this.onViewListingDetail}>
        <View
          style={{
            backgroundColor: SRXColor.White,
            paddingHorizontal: Spacing.M,
            paddingVertical: Spacing.XS,
          }}>
          {this.renderCellHeader()}
          <CommunitiesPostItemContent post={post} />
          <Separator />
          {this.renderActions()}
        </View>
      </TouchableHighlight>
    );
  }
}

CommunitiesListingsListItem.propTypes = {
  post: PropTypes.instanceOf(CommunityPostPO),

  /**
   * callback when Share button pressed, will pass Community in the method
   */
  onSharePressed: PropTypes.func,

  /**
   * callback when Enquiry button pressed, will pass ListingPO in the method
   */
  onEnquireListing: PropTypes.func,

  /**
   * callback when Go to Listing button pressed, will pass ListingPO in the method
   */
  onViewListingPressed: PropTypes.func,

  /**
   * callback when press agent's photo or name, will pass CommunityPostPO in the method
   */
  onPostCreatorPressed: PropTypes.func,

  /**
   * callback when press 'Go to Listings', will pass CommunityPostPO in the method
   */
  onGotoListingsPressed: PropTypes.func,
};

export {CommunitiesListingsListItem};
