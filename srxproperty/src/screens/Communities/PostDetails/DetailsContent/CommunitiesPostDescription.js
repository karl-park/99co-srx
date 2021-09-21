import React, {Component} from 'react';
import {View, TouchableOpacity, Image, Linking, Dimensions} from 'react-native';
import PropTypes from 'prop-types';
import HTML from 'react-native-render-html';

import {
  Avatar,
  FeatherIcon,
  SmallBodyText,
  BodyText,
  TouchableHighlight,
} from '../../../../components';
import {Spacing, Typography} from '../../../../styles';
import {SRXColor} from '../../../../constants';
import {ObjectUtil, CommonUtil} from '../../../../utils';
import {CommunityPostPO} from '../../../../dataObject';
import {CommunitiesConstant} from '../../Constants';
import {CommunitiesParentPost} from './CommunitiesParentPost';
import {connect} from 'react-redux';

var {width} = Dimensions.get('window');

class CommunitiesPostDescription extends Component {
  constructor(props) {
    super(props);

    this.openPostUrl = this.openPostUrl.bind(this);
    this.showParentPostDetails = this.showParentPostDetails.bind(this);
  }

  onClickUser = () => {
    const {showProfile} = this.props;
    if (showProfile) {
      showProfile();
    }
  };

  openPostUrl = postUrl => {
    const {directWebScreen} = this.props;
    if (postUrl && directWebScreen) {
      directWebScreen(postUrl);
    }
  };

  showParentPostDetails() {
    const {communityPostPO, showParentPostDetails} = this.props;
    if (showParentPostDetails) {
      showParentPostDetails(communityPostPO.parentPost);
    }
  }

  //start rendering methods
  renderNameAndDate() {
    const {communityPostPO} = this.props;
    const {userPO} = this.props;

    var isOwnPost = false;
    if (
      !ObjectUtil.isEmpty(userPO) &&
      !ObjectUtil.isEmpty(communityPostPO.user) &&
      communityPostPO.user.id === userPO.id
    ) {
      //own post
      isOwnPost = true;
    }

    var name = communityPostPO.user
      ? communityPostPO.user.getCommunityPostUserName()
      : '';
    var photoURL = communityPostPO.user ? communityPostPO.user.photo : '';

    return (
      <View style={{flexDirection: 'row', alignItems: 'center'}}>
        <TouchableOpacity
          disabled={isOwnPost}
          style={{flexDirection: 'row', alignItems: 'center', flex: 1}}
          onPress={() => this.onClickUser()}>
          {this.renderAvatarAndIcon(name, photoURL, communityPostPO.type)}
          <SmallBodyText
            style={{
              flex: 1,
              marginLeft: Spacing.XS,
              marginRight: Spacing.XS,
              flex: 1,
            }}
            numberOfLines={1}>
            {name}
          </SmallBodyText>
        </TouchableOpacity>
        <SmallBodyText
          style={{justifyContent: 'flex-end', color: SRXColor.Gray}}>
          {communityPostPO.getDateDisplay()}
        </SmallBodyText>
      </View>
    );
  }

  renderAvatarAndIcon(name, photoURL, type) {
    if (type == CommunitiesConstant.postType.news && photoURL) {
      return (
        <Image
          source={{uri: CommonUtil.handleImageUrl(photoURL)}}
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

  renderTitle() {
    const {communityPostPO} = this.props;
    if (!ObjectUtil.isEmpty(communityPostPO.title)) {
      return (
        <View style={Styles.titleContainer}>
          <BodyText style={{fontWeight: 'bold'}}>
            {communityPostPO.title}
          </BodyText>
        </View>
      );
    }
  }

  renderDescription() {
    const {communityPostPO} = this.props;
    if (!ObjectUtil.isEmpty(communityPostPO.content)) {
      return (
        <View style={Styles.descriptionContainer}>
          <HTML
            html={communityPostPO.content}
            imagesMaxWidth={width - 40}
            onLinkPress={(event, href) => this.openPostUrl(href)}
            baseFontStyle={{
              lineHeight: 20,
              fontSize: 14,
              color: SRXColor.Black,
            }}
            imagesInitialDimensions={{
              width: width,
            }}
            staticContentMaxWidth={width}
          />
        </View>
      );
    }
  }

  renderPostUrl() {
    const {communityPostPO} = this.props;
    if (!ObjectUtil.isEmpty(communityPostPO.externalUrl)) {
      return (
        <TouchableOpacity
          onPress={() => this.openPostUrl(communityPostPO.externalUrl)}>
          <View style={Styles.postUrlContainer}>
            <FeatherIcon name="link-2" size={20} color={SRXColor.Black} />
            <SmallBodyText
              style={{
                marginLeft: Spacing.XS,
                marginRight: Spacing.XS,
                alignSelf: 'center',
                color: SRXColor.Gray,
              }}
              numberOfLines={1}>
              {communityPostPO.externalUrl}
            </SmallBodyText>
          </View>
        </TouchableOpacity>
      );
    }
  }

  renderParentPost() {
    const {communityPostPO} = this.props;
    if (!ObjectUtil.isEmpty(communityPostPO.parentPost)) {
      return (
        <TouchableHighlight
          style={{marginTop: Spacing.S, borderRadius: 10}}
          onPress={() => this.showParentPostDetails()}>
          <CommunitiesParentPost
            communityParentPost={communityPostPO.parentPost}
            showWebScreen={this.openPostUrl}
          />
        </TouchableHighlight>
      );
    }
  }

  render() {
    return (
      <View style={Styles.mainContainer}>
        {this.renderNameAndDate()}
        {this.renderTitle()}
        {this.renderDescription()}
        {this.renderParentPost()}
        {this.renderPostUrl()}
      </View>
    );
  }
}

CommunitiesPostDescription.propTypes = {
  communityPostPO: PropTypes.instanceOf(CommunityPostPO).isRequired,

  /** to show agent cv for now for listing and transacted listing */
  showProfile: PropTypes.func,

  directWebScreen: PropTypes.func,

  showParentPostDetails: PropTypes.func,
};

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
  };
};

const Styles = {
  mainContainer: {
    paddingLeft: Spacing.M,
    paddingRight: Spacing.M,
    paddingTop: Spacing.S,
    paddingBottom: Spacing.S,
    backgroundColor: SRXColor.White,
  },
  titleContainer: {
    marginTop: Spacing.S,
  },
  descriptionContainer: {
    marginTop: Spacing.S,
  },
  postUrlContainer: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: Spacing.S,
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

export default connect(mapStateToProps)(CommunitiesPostDescription);
