import React, {Component} from 'react';
import {View, Image} from 'react-native';
import PropTypes from 'prop-types';

import {
  Avatar,
  ExtraSmallBodyText,
  FeatherIcon,
  Separator,
  SmallBodyText,
  Subtext,
  TouchableHighlight,
} from '../../../../../../../components';
import {Button} from '../../../../../../../components';
import {SRXColor} from '../../../../../../../constants';
import {Spacing} from '../../../../../../../styles';
import {CommunityPostPO} from '../../../../../../../dataObject';
import {ObjectUtil} from '../../../../../../../utils';

class CommunitiesGRCSponsoredPostItem extends Component {
  renderCellHeader() {
    const {communityPostPO} = this.props;
    if (
      !ObjectUtil.isEmpty(communityPostPO.business) &&
      !ObjectUtil.isEmpty(communityPostPO.user)
    ) {
      return (
        <View
          style={{
            flexDirection: 'row',
            alignItems: 'center',
            justifyContent: 'space-between',
          }}>
          <View
            style={{
              marginRight: Spacing.S,
              flexDirection: 'row',
              alignItems: 'center',
              flex: 1,
            }}>
            <Avatar
              name={communityPostPO.user.name}
              imageUrl={communityPostPO.business.logoUrl}
              size={24}
              borderColor={'#DCDCDC'}
              textSize={12}
            />
            <Subtext
              numberOfLines={1}
              style={{
                marginLeft: Spacing.XS / 2,
                marginRight: Spacing.XS / 2,
                color: SRXColor.Black,
              }}>
              {communityPostPO.user.getCommunityPostUserName()}
            </Subtext>
          </View>
          <ExtraSmallBodyText
            style={{marginLeft: Spacing.M, color: SRXColor.Gray}}>
            Sponsored
          </ExtraSmallBodyText>
        </View>
      );
    }
  }

  renderImage() {
    const {communityPostPO} = this.props;
    var media = null;
    if (
      !ObjectUtil.isEmpty(communityPostPO.media) &&
      communityPostPO.media.length > 0
    ) {
      media = communityPostPO.media[0];
      return (
        <Image
          source={{uri: media ? media.url : ''}}
          style={{
            height: 200,
            backgroundColor: SRXColor.LightGray,
            marginTop: Spacing.M,
          }}
        />
      );
    }
  }

  renderPostTitleAndDetail() {
    const {communityPostPO} = this.props;
    return (
      <View>
        <SmallBodyText style={{marginTop: Spacing.XS}} numberOfLines={2}>
          {communityPostPO.title}
        </SmallBodyText>
        <ExtraSmallBodyText
          style={{marginTop: Spacing.XS, color: SRXColor.Gray}}
          numberOfLines={2}>
          {communityPostPO.content}
        </ExtraSmallBodyText>
      </View>
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
        <View
          style={{
            flexDirection: 'row',
            alignItems: 'center',
            justifyContent: 'center',
          }}>
          <FeatherIcon name={'thumbs-up'} size={18} color={SRXColor.Black} />
          <Subtext style={{marginLeft: Spacing.XS / 2}}>Shiok</Subtext>
          <FeatherIcon
            name={'thumbs-down'}
            size={18}
            color={SRXColor.Black}
            style={{marginLeft: Spacing.M, marginTop: Spacing.XS / 4}}
          />
          <Subtext style={{marginLeft: Spacing.XS / 2}}>Jialat</Subtext>
        </View>
        <Subtext style={{color: SRXColor.Teal}}>Share</Subtext>
      </View>
    );
  }

  render() {
    return (
      <TouchableHighlight
        style={{
          borderRadius: 10,
          backgroundColor: SRXColor.White,
          marginBottom: Spacing.XS,
          overflow: 'hidden',
        }}
        onPress={null}>
        <View
          style={{
            backgroundColor: SRXColor.White,
            borderRadius: 10,
            borderWidth: 1,
            borderColor: SRXColor.LightGray,
            padding: Spacing.M,
          }}>
          {this.renderCellHeader()}
          {this.renderImage()}
          {this.renderPostTitleAndDetail()}
          <Separator edgeInset={{top: Spacing.M, bottom: Spacing.M}} />
          {this.renderActions()}
        </View>
      </TouchableHighlight>
    );
  }
}

CommunitiesGRCSponsoredPostItem.propTypes = {
  communityPostPO: PropTypes.instanceOf(CommunityPostPO),
};

export {CommunitiesGRCSponsoredPostItem};
