import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {View, Dimensions, Linking, TouchableOpacity, Image} from 'react-native';

import {CommunityPostPO} from '../../../../dataObject';
import {SRXColor} from '../../../../constants';
import {Spacing, Typography} from '../../../../styles';
import {
  SmallBodyText,
  Avatar,
  BodyText,
  FeatherIcon,
} from '../../../../components';
import {CommunitiesConstant} from '../../Constants';
import {ObjectUtil, CommonUtil} from '../../../../utils';
import HTML from 'react-native-render-html';

var {width} = Dimensions.get('window');

class CommunitiesParentPost extends Component {
  constructor(props) {
    super(props);

    this.openPostUrl = this.openPostUrl.bind(this);
  }

  openPostUrl(url) {
    const {showWebScreen} = this.props;
    if (showWebScreen && url) {
      showWebScreen(url);
    }
  }

  renderAvatarAndName() {
    const {communityParentPost} = this.props;

    var name = communityParentPost.user
      ? communityParentPost.user.getCommunityPostUserName()
      : '';
    var photoURL = communityParentPost.user
      ? communityParentPost.user.photo
      : '';

    return (
      <View style={Styles.userInfoContainer}>
        {this.renderAvatarAndIcon(name, photoURL, communityParentPost.type)}
        <SmallBodyText
          style={{marginLeft: Spacing.XS, marginRight: Spacing.XS, flex: 1}}
          numberOfLines={1}>
          {name}
        </SmallBodyText>
      </View>
    );
  }

  renderAvatarAndIcon(name, url, type) {
    if (type == CommunitiesConstant.postType.news && url) {
      return (
        <Image
          source={{uri: CommonUtil.handleImageUrl(url)}}
          style={{width: 30, height: 30}}
          resizeMode={'contain'}
        />
      );
    } else {
      return (
        <Avatar
          name={name}
          imageUrl={url}
          size={35}
          borderColor={'#DCDCDC'}
          textSize={18}
        />
      );
    }
  }

  renderMedias() {
    const {communityParentPost} = this.props;
    if (
      Array.isArray(communityParentPost.media) &&
      communityParentPost.media.length > 0
    ) {
      const firstMedia = communityParentPost.media[0];
      if (firstMedia.type === CommunitiesConstant.mediaType.photo) {
        return (
          <View style={{marginTop: Spacing.XS}}>
            <Image
              style={{
                backgroundColor: SRXColor.LightGray,
                aspectRatio: 16 / 9,
              }}
              source={{uri: firstMedia.url}}
            />
          </View>
        );
      }
    }
  }

  renderTitle() {
    const {communityParentPost} = this.props;
    if (!ObjectUtil.isEmpty(communityParentPost.title)) {
      return (
        <BodyText style={{fontWeight: 'bold', marginTop: Spacing.XS}}>
          {communityParentPost.title}
        </BodyText>
      );
    }
  }

  renderContent() {
    const {communityParentPost} = this.props;
    if (!ObjectUtil.isEmpty(communityParentPost.content)) {
      return (
        <View style={{marginTop: Spacing.XS}}>
          <HTML
            html={communityParentPost.content}
            imagesMaxWidth={width - 40}
            onLinkPress={(event, href) => {
              Linking.openURL(href);
            }}
            baseFontStyle={{
              lineHeight: 20,
              fontSize: 14,
              color: SRXColor.Black,
            }}
          />
        </View>
      );
    }
  }

  renderExternalLink() {
    const {communityParentPost} = this.props;
    if (!ObjectUtil.isEmpty(communityParentPost.externalUrl)) {
      return (
        <TouchableOpacity
          onPress={() => this.openPostUrl(communityParentPost.externalUrl)}>
          <View style={Styles.urlContainer}>
            <FeatherIcon name="link-2" size={20} color={SRXColor.Black} />
            <SmallBodyText
              style={{
                marginLeft: Spacing.XS,
                marginRight: Spacing.XS,
                color: SRXColor.Gray,
              }}
              numberOfLines={1}>
              {communityParentPost.externalUrl}
            </SmallBodyText>
          </View>
        </TouchableOpacity>
      );
    }
  }

  render() {
    return (
      <View style={Styles.mainContainer}>
        {this.renderAvatarAndName()}
        {this.renderMedias()}
        <View style={{paddingLeft: Spacing.S, paddingRight: Spacing.S}}>
          {this.renderTitle()}
          {this.renderContent()}
          {this.renderExternalLink()}
        </View>
      </View>
    );
  }
}

CommunitiesParentPost.propTypes = {
  communityParentPost: PropTypes.instanceOf(CommunityPostPO).isRequired,

  showWebScreen: PropTypes.func,
};

const Styles = {
  mainContainer: {
    flex: 1,
    backgroundColor: SRXColor.White,
    borderColor: SRXColor.LightGray,
    borderWidth: 1,
    borderRadius: 10,
    paddingTop: Spacing.XS,
    paddingBottom: Spacing.XS,
  },
  userInfoContainer: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    paddingLeft: Spacing.S,
    paddingRight: Spacing.S,
  },
  urlContainer: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: Spacing.XS,
    paddingLeft: Spacing.S,
    paddingRight: Spacing.S,
    paddingTop: Spacing.XS,
    paddingBottom: Spacing.XS,
    borderColor: SRXColor.LightGray,
    borderWidth: 1,
    borderStyle: 'dashed',
    borderRadius: 4,
  },
};
export {CommunitiesParentPost};
