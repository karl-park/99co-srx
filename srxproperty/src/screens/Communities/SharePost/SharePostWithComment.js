import React, {Component} from 'react';
import {View, TextInput, ActivityIndicator, Alert} from 'react-native';
import SafeAreaView from 'react-native-safe-area-view';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
import {Navigation} from 'react-native-navigation';
import {connect} from 'react-redux';
import {EventRegister} from 'react-native-event-listeners';

import {
  FeatherIcon,
  SmallBodyText,
  Avatar,
  Button,
  BodyText,
} from '../../../components';
import {Spacing} from '../../../styles';
import {SRXColor, LoadingState, AlertMessage} from '../../../constants';
import {SharePostItem} from './SharePostItems';
import {CommunitiesService} from '../../../services';
import {
  CommunityPostPO,
  CommunityPO,
  CommunityItem,
  CommunityTopDownPO,
} from '../../../dataObject';
import {
  ObjectUtil,
  CommunitiesUtil,
  StringUtil,
  CommonUtil,
} from '../../../utils';
import {CommunitiesConstant} from '../Constants';
import {CommunitiesStack} from '../../../config';

class SharePostWithComment extends Component {
  static options(passProps) {
    return {
      topBar: {
        title: {
          text: 'Share',
        },
        visible: true,
        animate: true,
      },
    };
  }

  constructor(props) {
    super(props);

    this.state = {
      comment: '',
      communityPostPO: null,
      canSharedCommunities: [],
      loadingState: LoadingState.Normal,
      selectedCommunity: CommunitiesConstant.singaporeCommunity, //default singapore community
    };

    Navigation.events().bindComponent(this);

    this.setNavigationOptions();
    this.onChooseCommunity = this.onChooseCommunity.bind(this);
    this.onCommunitySelected = this.onCommunitySelected.bind(this);
  }

  componentDidMount() {
    this.getPost();
  }

  setNavigationOptions() {
    const componentId = this.props.componentId;
    return new Promise(function(resolve, reject) {
      FeatherIcon.getImageSource('x', 25, 'blue')
        .then(icon_close => {
          Navigation.mergeOptions(componentId, {
            topBar: {
              leftButtons: [
                {
                  id: 'btn_close',
                  icon: icon_close,
                },
              ],
              rightButtons: [
                {
                  id: 'btn_Share',
                  text: 'Post',
                  color: SRXColor.TextLink,
                },
              ],
            },
          });
          resolve(true);
        })
        .catch(error => {
          console.log(error);
          reject(error);
        });
    });
  }

  navigationButtonPressed({buttonId}) {
    if (buttonId === 'btn_Share') {
      this.sharePost();
    } else if (buttonId === 'btn_close') {
      Navigation.dismissModal(this.props.componentId);
    }
  }

  setPostButton(enabled, title) {
    Navigation.mergeOptions(this.props.componentId, {
      topBar: {
        rightButtons: [
          {
            id: 'btn_Share',
            text: title,
            color: SRXColor.TextLink,
            enabled: enabled,
          },
        ],
      },
    });
  }

  getPost() {
    const {postId} = this.props;
    this.setState({loadingState: LoadingState.Loading});
    CommunitiesService.getPost({
      id: postId,
    })
      .then(response => {
        if (response && response.post) {
          var postPO = new CommunityPostPO(response.post);
          this.setState(
            {
              communityPostPO: postPO,
              loadingState: LoadingState.Loaded,
            },
            () => {
              this.handleRelatedCommunities();
            },
          );
        }
      })
      .catch(error => {
        console.log(error);
        this.setState({loadingState: LoadingState.Failed});
      });
  }

  handleRelatedCommunities() {
    const {communityPostPO} = this.state;
    if (
      !ObjectUtil.isEmpty(communityPostPO) &&
      !ObjectUtil.isEmpty(communityPostPO.community) &&
      communityPostPO.type !== CommunitiesConstant.postType.news &&
      communityPostPO.community.id !== CommunitiesConstant.singaporeCommunity.id
    ) {
      this.setState({selectedCommunity: communityPostPO.community}, () => {
        this.getRelatedCommunitiesToShare();
      });
    } else {
      //if post type is not normal && community from communityPostPO is singapore => use singapore community
      //do nothing for this case
    }
  }

  getRelatedCommunitiesToShare() {
    const {communityPostPO} = this.state;
    const {selectedCommunity} = this.props;

    CommunitiesService.getRelatedCommunitiesTopDownToShare({
      postId: communityPostPO.id, //post id
      communityId: selectedCommunity.id, //communityId
    })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const {communitiesTopDown} = response;
          if (
            !ObjectUtil.isEmpty(communitiesTopDown) &&
            Array.isArray(communitiesTopDown)
          ) {
            const communitiesArray = communitiesTopDown.map(
              item => new CommunityTopDownPO(item),
            );
            const convertedItems = CommunitiesUtil.convertCommunityTopDownPOArray(
              communitiesArray,
            );
            this.setState({
              canSharedCommunities: convertedItems,
            });
          }
        }
      })
      .catch(error => {
        console.log(error);
      });
  }

  sharePost = () => {
    const {postId} = this.props;
    const {comment, selectedCommunity, communityPostPO} = this.state;
    //update post button title and state
    this.setPostButton(false, 'Posting');

    CommunitiesService.sharePost({
      postId: postId,
      communityId: selectedCommunity.id,
      comment: comment,
    })
      .then(response => {
        const {result} = response;
        if (result === 'success') {
          //notify to refresh feed list
          EventRegister.emit(CommunitiesConstant.events.updateFeedListEvent, {
            action: 'share',
            communityPostPO: communityPostPO,
          });

          Navigation.dismissModal(this.props.componentId);
        } else {
          this.setPostButton(true, 'Post');
        }
      })
      .catch(error => {
        this.setPostButton(true, 'Post');
        const {data} = error;
        if (data && data.error) {
          Alert.alert(AlertMessage.ErrorMessageTitle, data.error);
        }
        console.log(error);
      });
  };

  onChooseCommunity() {
    const {
      selectedCommunity,
      canSharedCommunities,
      communityPostPO,
    } = this.state;

    if (!ObjectUtil.isEmpty(selectedCommunity)) {
      if (
        selectedCommunity.id == CommunitiesConstant.singaporeCommunity.id &&
        communityPostPO.community.id ==
          CommunitiesConstant.singaporeCommunity.id &&
        communityPostPO.type === CommunitiesConstant.postType.news
      ) {
        // CommunitiesStack.showCommunitiesModal({
        //   title: 'Who can see your posts?',
        //   selectedCommunity: selectedCommunity,
        //   onCommunitySelected: this.onCommunitySelected,
        // });
        CommunitiesStack.showCommunityOptions({
          selectedCommunity: selectedCommunity,
          onCommunitySelected: this.onCommunitySelected,
        });
      } else {
        // CommunitiesStack.showCommunitiesModal({
        //   title: 'Who can see your posts?',
        //   allowingCommunities: canSharedCommunities,
        //   selectedCommunity: selectedCommunity,
        //   onCommunitySelected: this.onCommunitySelected,
        // });
        CommunitiesStack.showCommunityOptions({
          allowingCommunities: canSharedCommunities,
          selectedCommunity: selectedCommunity,
          onCommunitySelected: this.onCommunitySelected,
        });
      }
    }
  }

  onCommunitySelected(community) {
    this.setState({
      selectedCommunity: community,
    });
  }

  renderUserInfoAndPostIn() {
    return (
      <View
        style={{
          flex: 1,
          flexDirection: 'row',
        }}>
        {this.renderUserInfo()}
        {this.renderPostIn()}
      </View>
    );
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
    const {userPO} = this.props;
    var creatorName = userPO.getCommunityPostUserName();
    var creatorPhoto = CommonUtil.handleImageUrl(userPO.photo);
    return (
      <View>
        <Avatar
          name={creatorName}
          imageUrl={creatorPhoto}
          size={40}
          borderColor={'#DCDCDC'}
          textSize={20}
        />
      </View>
    );
  }

  renderUserName() {
    const {userPO} = this.props;
    var creatorName = userPO.getCommunityPostUserName();
    return (
      <SmallBodyText
        style={{marginLeft: Spacing.XS, flex: 1}}
        numberOfLines={1}>
        {creatorName}
      </SmallBodyText>
    );
  }

  renderPostIn() {
    const {selectedCommunity} = this.state;
    return (
      <View style={Styles.postInContainer}>
        <SmallBodyText style={{color: SRXColor.Gray, marginRight: Spacing.XS}}>
          Post in
        </SmallBodyText>
        <Button
          buttonStyle={{
            height: 25,
            borderRadius: 12.5,
            borderWidth: 1,
            paddingHorizontal: Spacing.XS,
            borderColor: SRXColor.Teal,
          }}
          textStyle={{fontSize: 12, marginHorizontal: 2}}
          rightView={
            <FeatherIcon
              name={'chevron-down'}
              size={16}
              color={SRXColor.Teal}
            />
          }
          onPress={this.onChooseCommunity}>
          {' '}
          {StringUtil.capitalize(selectedCommunity.name)}
        </Button>
      </View>
    );
  }

  renderComment() {
    const {comment} = this.state;
    return (
      <View style={{marginTop: Spacing.L, marginBottom: Spacing.M}}>
        <TextInput
          style={{
            paddingTop: Spacing.XS,
            paddingBottom: Spacing.M,
            fontSize: 16,
          }}
          underlineColorAndroid="transparent"
          placeholder={'Add a comment'}
          placeholderTextColor={SRXColor.Gray}
          value={comment}
          onChangeText={text => {
            this.setState({comment: text});
          }}
        />
      </View>
    );
  }

  renderSharePost() {
    const {communityPostPO} = this.state;
    if (!ObjectUtil.isEmpty(communityPostPO)) {
      return <SharePostItem communityPostPO={communityPostPO} />;
    }
  }

  render() {
    const {loadingState} = this.state;
    if (loadingState == LoadingState.Loading) {
      return (
        <SafeAreaView style={Styles.loadingContainer}>
          <ActivityIndicator />
        </SafeAreaView>
      );
    } else if (loadingState == LoadingState.Loaded) {
      return (
        <SafeAreaView style={{flex: 1}}>
          <KeyboardAwareScrollView
            style={{flex: 1}}
            keyboardShouldPersistTaps={'always'}>
            <View style={Styles.container}>
              {this.renderUserInfoAndPostIn()}
              {this.renderComment()}
              {this.renderSharePost()}
            </View>
          </KeyboardAwareScrollView>
        </SafeAreaView>
      );
    } else if (loadingState == LoadingState.Failed) {
      return (
        <SafeAreaView style={Styles.loadingContainer}>
          <BodyText style={{textAlign: 'center'}}>
            Failed to response your request.Please contact
            customerservice@streetsine.com
          </BodyText>
        </SafeAreaView>
      );
    } else {
      return <View />;
    }
  }
}

const Styles = {
  container: {
    paddingHorizontal: Spacing.M,
    paddingTop: Spacing.L,
    backgroundColor: SRXColor.White,
    flex: 1,
  },
  userInfoContainer: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
  },
  postInContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'flex-end',
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
};

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
    communities: state.communitiesData.list,
    selectedCommunity: state.communitiesData.selectedCommunity,
  };
};

export default connect(mapStateToProps)(SharePostWithComment);
