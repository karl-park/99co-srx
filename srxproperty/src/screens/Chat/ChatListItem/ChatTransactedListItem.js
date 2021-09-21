import React, {Component} from 'react';
import {View} from 'react-native';
import {Spacing} from '../../../styles';
import {SRXColor} from '../../../constants';
import {
  SmallBodyText,
  ExtraSmallBodyText,
  Text,
  TouchableHighlight,
} from '../../../components';
import { ObjectUtil } from '../../../utils';

class ChatTransactedListItem extends Component {
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

  render() {
    const {containerStyle, listingPO, onItemPress} = this.props;
    if (!ObjectUtil.isEmpty(listingPO)) {
      return (
        <TouchableHighlight
          style={containerStyle}
          underlayColor={'rgba(0,0,0, 0.2)'}
          onPress={onItemPress}>
          <View>
            {this.renderItemHeader(listingPO)}
            {this.renderItemDetails(listingPO)}
          </View>
        </TouchableHighlight>
      );
    } else {
      return <View />;
    }
  }
}

export {ChatTransactedListItem};
