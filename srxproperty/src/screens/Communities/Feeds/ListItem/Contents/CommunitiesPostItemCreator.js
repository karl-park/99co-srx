import React, {Component} from 'react';
import {View, Image, TouchableOpacity} from 'react-native';
import {Avatar, ExtraSmallBodyText} from '../../../../../components';
import {Spacing} from '../../../../../styles';
import {ObjectUtil, CommonUtil} from '../../../../../utils';
import {SRXColor} from '../../../../../constants';
import {CommunitiesConstant} from '../../../Constants';

class CommunitiesPostItemCreator extends Component {
  renderAvatar(user) {
    var creatorName = user.getCommunityPostUserName();
    return (
      <Avatar
        name={creatorName}
        imageUrl={CommonUtil.handleImageUrl(user.photo)}
        size={24}
        borderColor={'#DCDCDC'}
        textSize={12}
        style={{marginRight: Spacing.XS}}
      />
    );
  }

  renderPostCreatorImage() {
    const {post} = this.props;
    if (post.type === CommunitiesConstant.postType.news) {
      if (!ObjectUtil.isEmpty(post.user.photo)) {
        if (
          !ObjectUtil.isEmpty(post.user) &&
          post.user.id == CommunitiesConstant.srxInfoUserIds.newPostUserId &&
          post.user.name == 'SRX'
        ) {
          //note: To differentiate srx new post icon from other news
          return this.renderAvatar(post.user);
        } else {
          return (
            <Image
              source={{uri: CommonUtil.handleImageUrl(post.user.photo)}}
              resizeMode={'contain'}
              style={{
                backgroundColor: SRXColor.White,
                height: 16,
                width: 16,
                marginRight: Spacing.XS,
              }}
            />
          );
        }
      }
    }
    if (post.type === CommunitiesConstant.postType.sponsored) {
      if (!ObjectUtil.isEmpty(post.user.photo)) {
        return this.renderAvatar(post.user);
      }
    } else {
      return this.renderAvatar(post.user);
    }

    return null;
  }

  render() {
    const {post, onPress} = this.props;
    if (!ObjectUtil.isEmpty(post.user)) {
      var creatorName = post.user.getCommunityPostUserName();
      return (
        <View style={{flex: 1}}>
          <TouchableOpacity onPress={onPress} disabled={!onPress}>
            <View
              style={{
                flexDirection: 'row',
                alignItems: 'center',
                flex: 1,
                overflow: 'hidden',
              }}>
              {this.renderPostCreatorImage()}
              <ExtraSmallBodyText
                style={{marginLeft: Spacing.XS, flex: 1}}
                numberOfLines={1}>
                {creatorName}
              </ExtraSmallBodyText>
            </View>
          </TouchableOpacity>
        </View>
      );
    }
    return null;
  }
}

export {CommunitiesPostItemCreator};
