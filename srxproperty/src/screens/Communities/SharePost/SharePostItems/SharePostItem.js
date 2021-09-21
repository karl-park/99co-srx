import React, {Component} from 'react';
import {View, Image, Linking, Dimensions} from 'react-native';

//Custom Components
import {SmallBodyText, Heading2, Avatar} from '../../../../components';
import {Spacing, Typography} from '../../../../styles';
import {SRXColor} from '../../../../constants';
import HTML from 'react-native-render-html';
import {CommunitiesPostPhotos} from '../../PostDetails/DetailsContent/CommunitiesPostPhotos';
import {
  ObjectUtil,
  StringUtil,
  CommonUtil,
  YoutubeUtil,
} from '../../../../utils';
import {CommunityPostPO} from '../../../../dataObject';
import PropTypes from 'prop-types';
import {CommunitiesConstant} from '../../Constants';

var {width} = Dimensions.get('window');

class SharePostItem extends Component {
  static propTypes = {
    communityPostPO: PropTypes.instanceOf(CommunityPostPO),
  };

  static defaultProps = {
    communityPostPO: null,
  };

  constructor(props) {
    super(props);
  }

  renderUserInfo() {
    return (
      <View style={Styles.userInfoContainer}>
        {this.renderUserAvator()}
        {this.renderUserName()}
      </View>
    );
  }

  renderUserAvator() {
    const {communityPostPO} = this.props;
    const {user} = this.props.communityPostPO;
    var name = user.getCommunityPostUserName();
    var photoURL = CommonUtil.handleImageUrl(user.photo);
    if (communityPostPO.type == CommunitiesConstant.postType.news) {
      return (
        <Image
          source={{uri: photoURL}}
          style={{width: 30, height: 30}}
          resizeMode={'contain'}
        />
      );
    } else {
      return (
        <Avatar
          name={name}
          imageUrl={photoURL}
          size={40}
          borderColor={'#DCDCDC'}
          textSize={20}
        />
      );
    }
  }

  renderUserName() {
    const {user} = this.props.communityPostPO;
    var name = user.getCommunityPostUserName();
    return (
      <SmallBodyText
        style={{marginLeft: Spacing.XS, flex: 1}}
        numberOfLines={1}>
        {name}
      </SmallBodyText>
    );
  }

  renderSharePost() {
    return (
      <View style={Styles.sharePostItemContainer}>
        {this.renderUserInfoAndGroupNameRow()}
        {this.renderPostTitle()}
        {this.renderPostContent()}
        {this.renderPostImage()}
      </View>
    );
  }

  renderUserInfoAndGroupNameRow() {
    return (
      <View style={{flex: 1, flexDirection: 'row'}}>
        {this.renderUserInfo()}
        {this.renderGroupNameAndTimeStamp()}
      </View>
    );
  }

  renderGroupNameAndTimeStamp() {
    const {communityPostPO} = this.props;
    if (!ObjectUtil.isEmpty(communityPostPO)) {
      var communityName = '';
      if (
        !ObjectUtil.isEmpty(communityPostPO.community) &&
        communityPostPO.type === CommunitiesConstant.postType.normal
      ) {
        communityName = communityPostPO.community.name;
      }
      return (
        <View style={Styles.groupNameAndTimeStampContainer}>
          <SmallBodyText style={{color: SRXColor.Gray}}>
            {StringUtil.capitalize(communityName)}{' '}
            {communityName === '' ? null : '•'}{' '}
            {communityPostPO.getDateDisplay()}
          </SmallBodyText>
        </View>
      );
    }
  }

  renderPostTitle() {
    const {communityPostPO} = this.props;
    return (
      <View style={{marginTop: Spacing.M}}>
        <Heading2>{communityPostPO.title}</Heading2>
      </View>
    );
  }

  renderPostContent() {
    const {content} = this.props.communityPostPO;

    if (!ObjectUtil.isEmpty(content)) {
      return (
        <View style={{marginTop: Spacing.XS}}>
          <HTML
            html={content}
            imagesMaxWidth={width - 40}
            onLinkPress={(event, href) => {
              Linking.openURL(href);
            }}
            baseFontStyle={
              ([Typography.SmallBody, {color: SRXColor.Gray}], {lineHeight: 20})
            }
          />
        </View>
      );
    }
  }

  renderPostImage() {
    const {communityPostPO} = this.props;
    if (!ObjectUtil.isEmpty(communityPostPO)) {
      const {media} = this.props.communityPostPO;
      var youtubeList = [];
      if (communityPostPO.externalUrl !== '') {
        var youtubePO = YoutubeUtil.getValidatedYoutubeLink(
          communityPostPO.externalUrl,
        );
        if (!ObjectUtil.isEmpty(youtubePO)) {
          youtubeList.push(youtubePO);
        }
      }

      if (
        (Array.isArray(media) && media.length > 0) ||
        youtubeList.length > 0
      ) {
        return <CommunitiesPostPhotos medias={media} youtube={youtubeList} />;
      }
    }
    return <View />;
  }

  render() {
    return <View style={{flex: 1}}>{this.renderSharePost()}</View>;
  }
}

const Styles = {
  userInfoContainer: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
  },
  sharePostItemContainer: {
    overflow: 'hidden',
    marginLeft: Spacing.M,
    //Padding
    padding: Spacing.S,
    //Border
    borderRadius: Spacing.XS,
    borderWidth: 1,
    borderColor: '#e0e0e0',
  },
  groupNameAndTimeStampContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'flex-end',
  },
  imageStyle: {
    width: '100%',
    height: 180,
  },
};

export {SharePostItem};
