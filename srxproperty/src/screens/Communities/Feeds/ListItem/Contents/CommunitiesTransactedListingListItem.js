import React, {Component} from 'react';
import {View, Text} from 'react-native';
import PropTypes from 'prop-types';
import {SRXColor, IS_IOS} from '../../../../../constants';
import {
  ExtraSmallBodyText,
  Separator,
  Button,
  SmallBodyText,
} from '../../../../../components';
import {Spacing} from '../../../../../styles';
import {ListingPO} from '../../../../../dataObject';
import {ObjectUtil} from '../../../../../utils';

class CommunitiesTransactedListingListItem extends Component {
  constructor(props) {
    super(props);
    this.onEnquiryListing = this.onEnquiryListing.bind(this);
  }

  onEnquiryListing() {
    const {onEnquireListing, listing} = this.props;

    if (onEnquireListing) {
      onEnquireListing(listing);
    }
  }

  renderItemHeader(listingPO) {
    return (
      <View style={{paddingTop: Spacing.XS, paddingBottom: Spacing.XS / 2}}>
        <SmallBodyText numberOfLines={1}>
          {listingPO.getListingName()}
        </SmallBodyText>
      </View>
    );
  }

  renderItemDetails(listingPO) {
    const isSale = listingPO.type !== 'R';
    return (
      <View
        style={{
          flexDirection: 'row',
          paddingTop: 4,
          paddingBottom: Spacing.S,
        }}>
        <Text
          style={{
            backgroundColor: SRXColor.Purple,
            color: SRXColor.White,
            paddingHorizontal: Spacing.XS / 2,
            paddingVertical: Spacing.XS / 4,
            borderRadius: 3,
            overflow: 'hidden',
            marginRight: Spacing.XS,
            fontSize: 10,
          }}>
          {isSale ? 'SOLD' : 'RENTED'}
        </Text>
        <ExtraSmallBodyText style={{color: SRXColor.Gray}}>
          {listingPO.getPropertyType()} · {listingPO.getSizeDisplay()}
        </ExtraSmallBodyText>
      </View>
    );
  }

  renderActions(listingPO) {
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
          textStyle={{fontSize: 14}}
          onPress={this.onEnquiryListing}>
          Enquire now
        </Button>
      </View>
    );
  }

  render() {
    const {listing} = this.props;
    return (
      <View
        style={{
          backgroundColor: SRXColor.White,
          paddingHorizontal: 12,
          width: 260,
          borderRadius: 5,
          borderColor: SRXColor.LightGray,
          borderWidth: IS_IOS ? 0 : 1,
          shadowColor: 'rgb(110,129,154)',
          shadowOffset: {width: 0, height: 0},
          shadowOpacity: 0.32,
          shadowRadius: 3,
          elevation: 2,
        }}>
        {this.renderItemHeader(listing)}
        {this.renderItemDetails(listing)}
        <Separator />
        {this.renderActions(listing)}
      </View>
    );
  }
}

CommunitiesTransactedListingListItem.propTypes = {
  listing: PropTypes.instanceOf(ListingPO),

  /**
   * callback when Enquiry button pressed, will pass ListingPO in the method
   */
  onEnquireListing: PropTypes.func,
};

export {CommunitiesTransactedListingListItem};
