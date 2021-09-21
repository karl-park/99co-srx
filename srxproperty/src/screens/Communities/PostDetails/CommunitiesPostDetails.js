import React, {Component} from 'react';
import {
  View,
  ActivityIndicator,
  SectionList,
  KeyboardAvoidingView,
  Alert,
} from 'react-native';
import SafeAreaView from 'react-native-safe-area-view';
import {Navigation} from 'react-native-navigation';
import {connect} from 'react-redux';
import {EventRegister} from 'react-native-event-listeners';
import PropTypes from 'prop-types';

import {
  SRXColor,
  IS_IOS,
  LoadingState,
  AlertMessage,
  IS_IPHONE_X,
} from '../../../constants';
import {
  FeatherIcon,
  Separator,
  BodyText,
  LoadMoreIndicator,
} from '../../../components';
import {
  CommunitiesPostDescription,
  CommunitiesPostActions,
  CommunitiesPostJoinCommunity,
} from './DetailsContent';
import {
  CommunitiesPostBusinessService,
  CommunitiesService,
} from '../../../services';
import {Spacing} from '../../../styles';
import {CommunityPostPO, YoutubePO} from '../../../dataObject';
import {CommunitiesCommentsSearchManager} from './DetailsManager';
import {
  ObjectUtil,
  NavigationUtil,
  StringUtil,
  YoutubeUtil,
} from '../../../utils';
import {
  CommunitiesCommentListItem,
  CommunitiesCommentComposer,
  CommunitiesCommentSectionHeader,
} from '../PostComments';
import {CommunitiesPostPhotos} from './DetailsContent/CommunitiesPostPhotos';
import {CommunitiesStack, LoginStack} from '../../../config';
import {CommunitiesConstant, CommunitiesCommentConstant} from '../Constants';
import {DetailActionOptionsConstant} from './DetailsModal/DetailActionOptionsConstant';
import {CommunitiesNewPostAction} from '../Post/CommunitiesNewPostAction';

const SECTION_TYPE = {
  Photo: 'Photo',
  JoinCommunity: 'Join Community',
  Description: 'Description',
  Actions: 'Action',
  Comments: 'Comments',
};

class CommunitiesPostDetails extends Component {
  static options(passProps) {
    return {
      bottomTabs: {
        visible: false,
        animate: true,
        drawBehind: true,
      },
    };
  }

  constructor(props) {
    super(props);
    Navigation.events().bindComponent(this);

    this.showParentPostDetails = this.showParentPostDetails.bind(this);
    this.onSelectOption = this.onSelectOption.bind(this);
    this.reportComment = this.reportComment.bind(this);
    this.showPostCommentContact = this.showPostCommentContact.bind(this);
    this.showPostDetailContact = this.showPostDetailContact.bind(this);
    this.deleteComment = this.deleteComment.bind(this);
    this.onSortTypeUpdate = this.onSortTypeUpdate.bind(this);
    this.loadMore = this.loadMore.bind(this);
    this.onLoadPreviousComments = this.onLoadPreviousComments.bind(this);
  }

  state = {
    sections: [],
    loadingState: LoadingState.Normal,
    communityPostPO: null,
    retrievedCommentCount: 0,
    postErrorMessage: null,
    showKeyboard: false,

    commentLoadingState: LoadingState.Normal,
    selectedSortType: CommunitiesCommentConstant.defaultCommentSort,
  };

  componentDidMount() {
    this.getPost();
  }

  showSharePost() {
    const {userPO} = this.props;
    const {sections, communityPostPO} = this.state;
    if (Array.isArray(sections) && sections.length > 0) {
      if (!ObjectUtil.isEmpty(userPO)) {
        if (userPO.mobileVerified) {
          const passProps = {
            previousComponentId: this.props.componentId,
            post: communityPostPO,
            showShareModal: this.showShareModal,
          };
          CommunitiesStack.showShareOptionsModal(passProps);
        } else {
          CommunitiesStack.showMobileVerificationModal();
        }
      } else {
        CommunitiesStack.showSignInRegisterModal();
      }
    } else {
      console.log('do nothing');
    }
  }

  showShareModal = passProps => {
    /**
     * Show SharePost Modal
     * Dismiss ShareOptionsModal in parent post details
     */
    CommunitiesStack.showSharePostModal(passProps);
    Navigation.dismissModal('ShareOptionsModal');
  };

  //from comment list item
  showPostCommentContact = comment => {
    var communityPostPO = new CommunityPostPO(comment);
    this.showPostContactMain(communityPostPO);
  };

  showPostDetailContact() {
    const {communityPostPO} = this.state;
    this.showPostContactMain(communityPostPO);
  }

  showPostContactMain(communityPostPO) {
    const {userPO} = this.props;
    if (!ObjectUtil.isEmpty(userPO)) {
      //if login
      if (!ObjectUtil.isEmpty(communityPostPO)) {
        const {user} = communityPostPO;
        if (!ObjectUtil.isEmpty(user) && user.id !== userPO.id) {
          let chatUser = {
            userId: user.id,
            conversationId: null, //assume it's null
            name: user.getCommunityPostUserName(),
            photo: user.photo,
            mobile: user.mobileLocalNum,
            agent: user.agent,
          };
          Navigation.push(this.props.componentId, {
            component: {
              name: 'ChatStack.chatRoom',
              passProps: {
                agentInfo: chatUser,
              },
            },
          });
        }
      } //end of communityPostPO
    } else {
      //not logged in
      LoginStack.showSignInRegisterModal();
    }
  }

  showParentPostDetails(parentPost) {
    if (!ObjectUtil.isEmpty(parentPost)) {
      if (parentPost.type == CommunitiesConstant.postType.news) {
        NavigationUtil.openUrl({
          url: parentPost.externalUrl,
          fromComponentId: this.props.componentId,
        });
      } else {
        Navigation.push(this.props.componentId, {
          component: {
            name: 'Communities.CommunitiesPostDetails',
            passProps: {
              postId: parentPost.id,
            },
          },
        });
        return;
      }
    }
  }

  showMoreOptions() {
    const {userPO, componentId} = this.props;
    const {communityPostPO} = this.state;
    if (
      !ObjectUtil.isEmpty(userPO) &&
      userPO.encryptedUserId === communityPostPO.user.encryptedUserId
    ) {
      CommunitiesStack.showDetailActionOptionsModal({
        options: DetailActionOptionsConstant.actionOptionsForOwnPost,
        onSelectOption: this.onSelectOption,
      });
    } else {
      CommunitiesStack.showDetailActionOptionsModal({
        options: DetailActionOptionsConstant.actionOptionsForOtherPost,
        onSelectOption: this.onSelectOption,
      });
    }
  }

  onSelectOption(option) {
    if (option.value == DetailActionOptionsConstant.actionOptionType.share) {
      this.showSharePost();
    } else if (
      option.value == DetailActionOptionsConstant.actionOptionType.delete
    ) {
      this.deletePost();
    } else if (
      option.value == DetailActionOptionsConstant.actionOptionType.report
    ) {
      const {communityPostPO} = this.state;
      this.reportPost(communityPostPO);
    } else if (
      option.value == DetailActionOptionsConstant.actionOptionType.edit
    ) {
      const {communityPostPO} = this.state;
      CommunitiesStack.showNewPostModal({
        onPosted: this.onPostUpdated,
        initialCommunity: communityPostPO.community,
        action: CommunitiesNewPostAction.EditPost,
        communityPostPO: communityPostPO,
      });
    }
  }

  onPostUpdated = ({post, inCommunity}) => {
    //Note: post must be instance of CommunityPostPO
    EventRegister.emit(CommunitiesConstant.events.updateFeedListEvent, {
      action: 'update',
      communityPostPO: post,
    });
    this.populatePostDetails(post);
  };

  reportPost(postPO) {
    CommunitiesStack.showReportAbuse({
      post: postPO,
    });
  }

  reportComment(commentPostPO) {
    Alert.alert('', 'Report this comment?', [
      {
        text: 'Report',
        onPress: () => this.reportPost(commentPostPO),
      },
      {
        text: 'Cancel',
        onPress: () => console.log('Cancel Pressed'),
        style: 'cancel',
      },
    ]);
  }

  deleteComment(commentPostPO) {
    Alert.alert('', 'Delete this comment?', [
      {
        text: 'Delete',
        onPress: () => {
          this.performDeleteComment(commentPostPO);
        },
      },
      {
        text: 'Cancel',
        onPress: () => console.log('Cancel Pressed'),
        style: 'cancel',
      },
    ]);
  }

  refreshCommentList(commentPostPO) {
    const {sections, communityPostPO} = this.state;
    var commentSection = this.getCommentSection();
    if (!ObjectUtil.isEmpty(commentSection)) {
      var newComments = commentSection.data.filter(
        item => item.id != commentPostPO.id,
      );
      commentSection.data = newComments;

      var tempCommunityPostPO = {
        ...communityPostPO,
        commentsTotal: communityPostPO.commentsTotal - 1,
      };

      //update comment count
      var reactionSection = sections.find(
        item => item.key == SECTION_TYPE.Actions,
      );
      if (!ObjectUtil.isEmpty(reactionSection)) {
        reactionSection.data = [{communityPostPO: tempCommunityPostPO}];
      }

      this.setState({
        sections: sections,
        retrievedCommentCount: commentSection.data.length,
        communityPostPO: tempCommunityPostPO,
      });
    }
  }

  scrollToCommentSection = itemIndex => {
    const {selectedSortType} = this.state;
    var tempItemIndex = 0;
    if (
      selectedSortType.value ==
      CommunitiesCommentConstant.sortOptionsValue.oldestToNewest
    ) {
      tempItemIndex = itemIndex;
    }
    this.scrollToSectionIndex(4, tempItemIndex);
  };

  scrollToSectionIndex = (sectionIndex, itemIndex) => {
    if (this.scrollView) {
      setTimeout(() => {
        this.scrollView.scrollToLocation({
          animated: true,
          itemIndex: itemIndex ?? 0,
          sectionIndex: sectionIndex ?? 0,
          viewPosition: 0,
        });
      }, 900);
    }
  };

  //API Calls
  getPost() {
    const {postId, isBusinssPost} = this.props;
    this.setState({loadingState: LoadingState.Loading}, () => {
      if (isBusinssPost) {
        CommunitiesPostBusinessService.getBusinessSponsoredPost({
          postId,
        })
          .then(response => {
            if (response) {
              const {post} = response;
              let communityPostPO = new CommunityPostPO(post);
              this.populatePostDetails(communityPostPO);
            }
          })
          .catch(error => {
            const {data} = error;
            var errorMsg = null;
            if (data && data.error) {
              errorMsg = data.error;
            }
            this.setState({
              loadingState: LoadingState.Failed,
              postErrorMessage:
                errorMsg ??
                'Failed to response your request.Please contact customerservice@streetsine.com',
            });
          });
      } else {
        CommunitiesService.getPost({
          id: postId,
        })
          .then(response => {
            if (response) {
              const {post} = response;
              let communityPostPO = new CommunityPostPO(post);
              this.populatePostDetails(communityPostPO);
            }
          })
          .catch(error => {
            const {data} = error;
            var errorMsg = null;
            if (data && data.error) {
              errorMsg = data.error;
            }
            this.setState({
              loadingState: LoadingState.Failed,
              postErrorMessage:
                errorMsg ??
                'Failed to response your request.Please contact customerservice@streetsine.com',
            });
          });
      }
    });
  }

  deletePost() {
    const {communityPostPO} = this.state;
    CommunitiesService.deletePosts({
      postIds: [communityPostPO.id],
    })
      .then(response => {
        const {result} = response;
        if (result == 'success') {
          EventRegister.emit(CommunitiesConstant.events.updateFeedListEvent, {
            action: 'delete',
            communityPostPO: communityPostPO,
          });
          Navigation.pop(this.props.componentId);
        }
      })
      .catch(error => {
        const {data} = error;
        if (data && data.error) {
          Alert.alert(AlertMessage.ErrorMessageTitle, data.error);
        }
      });
  }

  performDeleteComment(commentPostPO) {
    CommunitiesService.deleteComments({
      commentIds: [commentPostPO.id],
    })
      .then(response => {
        const {result} = response;
        if (result == 'success') {
          this.refreshCommentList(commentPostPO);
        }
      })
      .catch(error => {
        const {data} = error;
        if (data && data.error) {
          Alert.alert(AlertMessage.ErrorMessageTitle, data.error);
        }
      });
  }

  populatePostDetails(communityPostPO) {
    //communityPostPO is instance of CommunityPostPO
    if (!ObjectUtil.isEmpty(communityPostPO)) {
      let tempSections = [];

      this.addTopBarMenuOptions(communityPostPO);

      let photoSection = {
        data: [
          {
            medias: communityPostPO.media,
          },
        ],
        key: SECTION_TYPE.Photo,
      };
      tempSections.push(photoSection);

      let joinCommunitySection = {
        data: ['Community'],
        key: SECTION_TYPE.JoinCommunity,
      };
      tempSections.push(joinCommunitySection);

      let descriptionSection = {
        data: [{communityPostPO: communityPostPO}],
        key: SECTION_TYPE.Description,
      };
      tempSections.push(descriptionSection);

      if (communityPostPO.type === CommunitiesConstant.postType.sponsored) {
        if (
          !ObjectUtil.isEmpty(communityPostPO.sponsored) &&
          communityPostPO.sponsored.sponsoredType ===
            CommunitiesConstant.sponsoredType.business
        ) {
          //sponsored business type - add comment
          let actionCountSection = {
            data: [{communityPostPO: communityPostPO}],
            key: SECTION_TYPE.Actions,
          };
          tempSections.push(actionCountSection);

          this.setState(
            {
              sections: tempSections,
              communityPostPO: communityPostPO,
            },
            () => {
              this.onCallCommunitiesCommentSearchManager();
            },
          );
        } else {
          //sponsored listing type
          this.setState({
            sections: tempSections,
            communityPostPO: communityPostPO,
            loadingState: LoadingState.Loaded,
          });
        }
      } else {
        let actionCountSection = {
          data: [{communityPostPO: communityPostPO}],
          key: SECTION_TYPE.Actions,
        };
        tempSections.push(actionCountSection);

        this.setState(
          {
            sections: tempSections,
            communityPostPO: communityPostPO,
          },
          () => {
            this.onCallCommunitiesCommentSearchManager();
          },
        );
      }
    } else {
      this.setState({loadingState: LoadingState.Failed});
    }
  }

  addTopBarMenuOptions = communityPostPO => {
    if (!ObjectUtil.isEmpty(communityPostPO)) {
      const {componentId} = this.props;

      FeatherIcon.getImageSource('more-horizontal', 24, SRXColor.White).then(
        more => {
          Navigation.mergeOptions(componentId, {
            topBar: {
              title: {
                text: communityPostPO.title ? communityPostPO.title : '',
              },
              rightButtons: [
                {
                  id: 'btn_more',
                  icon: more,
                },
              ],
            },
          });
        },
      );
    } //end of community check
  };

  navigationButtonPressed({buttonId}) {
    if (buttonId == 'btn_share') {
      this.showSharePost();
    } else if (buttonId == 'btn_more') {
      this.showMoreOptions();
    }
  }

  onCallCommunitiesCommentSearchManager() {
    const {communityPostPO} = this.state;
    if (!ObjectUtil.isEmpty(communityPostPO)) {
      this.searchManager = new CommunitiesCommentsSearchManager({
        postId: communityPostPO.id,
      });
      this.searchManager.register(this.onCommentsLoaded.bind(this));
      this.searchManager.search();
    }
  }

  onCommentsLoaded({startIndex, newComments, allComments, error}) {
    if (allComments && newComments) {
      const {sections, selectedSortType} = this.state;
      const {isCommentBtnPressed} = this.props;

      var commentSection = this.getCommentSection();
      if (!ObjectUtil.isEmpty(commentSection)) {
        var tempAllComments = [];
        if (
          selectedSortType.value ==
          CommunitiesCommentConstant.sortOptionsValue.oldestToNewest
        ) {
          tempAllComments = [
            ...[...newComments].reverse(),
            ...commentSection.data,
          ];
        } else {
          tempAllComments = [...commentSection.data, ...newComments];
        }
        commentSection.data = tempAllComments;
      } else {
        //AS New Section
        var tempAllComments = [];
        if (
          selectedSortType.value ==
          CommunitiesCommentConstant.sortOptionsValue.oldestToNewest
        ) {
          tempAllComments = [...allComments].reverse();
        } else {
          tempAllComments = [...allComments];
        }
        commentSection = {
          data: tempAllComments,
          key: SECTION_TYPE.Comments,
        };
        sections.push(commentSection);
      }

      this.setState({
        sections: sections,
        loadingState: LoadingState.Loaded,
        commentLoadingState: LoadingState.Loaded,
        retrievedCommentCount: commentSection.data.length,
        showKeyboard: isCommentBtnPressed,
      });
    } else if (error) {
      let errorMessage = error.data
        ? error.data.error
        : 'Unable to load comments';
      Alert.alert(AlertMessage.ErrorMessageTitle, errorMessage);

      this.setState({loadingState: LoadingState.Loaded});
    }
  }

  onCommentSuccess = response => {
    const {comment} = response;
    const {sections, communityPostPO, selectedSortType} = this.state;

    var commentPostPO = new CommunityPostPO(comment);

    //Comment Section
    var commentSection = this.getCommentSection();
    if (
      !ObjectUtil.isEmpty(commentSection) &&
      selectedSortType.value ===
        CommunitiesCommentConstant.sortOptionsValue.oldestToNewest
    ) {
      commentSection.data = [...commentSection.data, ...commentPostPO];
    } else if (
      !ObjectUtil.isEmpty(commentSection) &&
      selectedSortType.value ==
        CommunitiesCommentConstant.sortOptionsValue.newestToOldest
    ) {
      commentSection.data = [...commentPostPO, ...commentSection.data];
    } else {
      commentSection = {
        data: [commentPostPO],
        key: SECTION_TYPE.Comments,
      };
      sections.push(commentSection);
    }
    var tempCommunityPostPO = {
      ...communityPostPO,
      commentsTotal: communityPostPO.commentsTotal + 1,
    };

    //update comment count
    var reactionSection = sections.find(
      item => item.key == SECTION_TYPE.Actions,
    );
    if (!ObjectUtil.isEmpty(reactionSection)) {
      reactionSection.data = [{communityPostPO: tempCommunityPostPO}];
    }

    this.setState(
      {
        sections: sections,
        retrievedCommentCount: commentSection.data.length,
        communityPostPO: tempCommunityPostPO,
      },
      () => {
        this.scrollToCommentSection(commentSection.data.length);
        //notifify to update post in feedlist after adding comment
        EventRegister.emit(CommunitiesConstant.events.updateFeedListEvent, {
          action: 'comment',
          communityPostPO: this.state.communityPostPO,
        });
      },
    );
  };

  onClickJoinCommunity = () => {
    const {userPO} = this.props;
    if (!ObjectUtil.isEmpty(userPO)) {
      //after user logged in
      const passProps = {
        onAddPropertyPressed: this.onAddPropertyPressed.bind(this),
      };
      CommunitiesStack.showAddPropertyModal(passProps);
    } else {
      //if not logged in
      CommunitiesStack.showSignInRegisterModal();
    }
  };

  onAddPropertyPressed = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'MyProperties.AddUpdateMyProperty',
        passProps: {
          initiatingComponentId: this.props.componentId,
        },
      },
    });
  };

  onPressComment = () => {
    const {showKeyboard} = this.state;
    if (showKeyboard != true) {
      this.setState({showKeyboard: true});
    }
  };

  onUpdateReactions = reactions => {
    const {communityPostPO} = this.state;
    this.setState(
      {
        communityPostPO: {...communityPostPO, reactions: reactions},
      },
      () => {
        EventRegister.emit(CommunitiesConstant.events.updateFeedListEvent, {
          action: 'react',
          communityPostPO: this.state.communityPostPO,
        });
      },
    );
  };

  directWebScreen = postUrl => {
    NavigationUtil.openUrl({
      url: postUrl,
      fromComponentId: this.props.componentId,
    });
  };

  getCommentSection() {
    const {sections} = this.state;
    return sections.find(item => item.key == SECTION_TYPE.Comments);
  }

  onSortTypeUpdate(sortType) {
    if (sortType.value !== this.state.selectedSortType.value) {
      this.setState({selectedSortType: sortType}, () => {
        var commentSection = this.getCommentSection();
        commentSection.data.reverse();
        this.forceUpdate();
      });
    }
  }

  //Start rendering methods
  renderSectionHeader = ({section}) => {
    if (section.key == SECTION_TYPE.Comments) {
      const {communityPostPO} = this.state;
      if (!ObjectUtil.isEmpty(communityPostPO)) {
        return (
          <CommunitiesCommentSectionHeader
            section={section}
            commentsTotal={communityPostPO.commentsTotal}
            sortType={this.state.selectedSortType}
            loadingState={this.state.commentLoadingState}
            onSortTypeUpdate={this.onSortTypeUpdate}
            onLoadPreviousComments={this.onLoadPreviousComments}
          />
        );
      }
    }
    return <View />;
  };

  renderItem = ({item, index, section}) => {
    if (section.key == SECTION_TYPE.Photo) {
      return this.renderPostImage(item, index);
    } else if (section.key == SECTION_TYPE.JoinCommunity) {
      return this.renderJoinCommunity();
    } else if (section.key == SECTION_TYPE.Description) {
      return this.renderPostContent(item, index);
    } else if (section.key == SECTION_TYPE.Actions) {
      return this.renderPostActions(item, index);
    } else if (section.key == SECTION_TYPE.Comments) {
      return this.renderCommentItem(item, index);
    } else {
      return <View />;
    }
  };

  renderPostImage = (item, index) => {
    const {medias} = item;
    var youtubeList = [];
    const {communityPostPO} = this.state;
    if (!ObjectUtil.isEmpty(communityPostPO)) {
      if (communityPostPO.externalUrl !== '') {
        var youTubePO = YoutubeUtil.getValidatedYoutubeLink(
          communityPostPO.externalUrl,
        );
        if (!ObjectUtil.isEmpty(youTubePO)) {
          youtubeList.push(youTubePO);
        }
      }
    }
    if (
      (Array.isArray(medias) && medias.length > 0) ||
      youtubeList.length > 0
    ) {
      return <CommunitiesPostPhotos medias={medias} youtube={youtubeList} />;
    }
    return <View />;
  };

  renderJoinCommunity() {
    const {userPO, communities} = this.props;
    if (ObjectUtil.isEmpty(userPO)) {
      return (
        <CommunitiesPostJoinCommunity
          onPressJoinCommunity={this.onClickJoinCommunity}
        />
      );
    } else if (
      ObjectUtil.isEmpty(communities) ||
      (Array.isArray(communities) && communities.length == 0)
    ) {
      return (
        <CommunitiesPostJoinCommunity
          onPressJoinCommunity={this.onClickJoinCommunity}
        />
      );
    }
    return <View />;
  }

  renderPostContent = (item, index) => {
    const {communityPostPO} = item;
    if (!ObjectUtil.isEmpty(communityPostPO)) {
      return (
        <CommunitiesPostDescription
          communityPostPO={communityPostPO}
          showProfile={this.showPostDetailContact}
          directWebScreen={this.directWebScreen}
          showParentPostDetails={this.showParentPostDetails}
        />
      );
    }
    return <View />;
  };

  renderPostActions = (item, index) => {
    const {communityPostPO} = item;
    if (!ObjectUtil.isEmpty(communityPostPO)) {
      return (
        <CommunitiesPostActions
          communityPostPO={communityPostPO}
          onPressComment={this.onPressComment}
          onUpdateReactions={this.onUpdateReactions}
        />
      );
    }
    return <View />;
  };

  renderCommentItem = (item, index) => {
    if (!ObjectUtil.isEmpty(item)) {
      const {retrievedCommentCount, selectedSortType} = this.state;
      let comment = new CommunityPostPO(item);
      return (
        <CommunitiesCommentListItem
          key={index}
          itemStyle={{
            borderTopRightRadius:
              selectedSortType.value ==
                CommunitiesCommentConstant.sortOptionsValue.newestToOldest &&
              index == 0
                ? 10
                : 0,
            borderTopLeftRadius:
              selectedSortType.value ==
                CommunitiesCommentConstant.sortOptionsValue.newestToOldest &&
              index == 0
                ? 10
                : 0,
          }}
          index={index}
          comment={comment}
          commentsTotal={retrievedCommentCount}
          directWebScreen={this.directWebScreen}
          showPostCommentContact={this.showPostCommentContact}
          reportComment={this.reportComment}
          deleteComment={this.deleteComment}
        />
      );
    }

    return <View />;
  };

  renderDetailSections() {
    const {sections} = this.state;
    if (!ObjectUtil.isEmpty(sections)) {
      return (
        <SectionList
          ref={ref => {
            if (ref) {
              this.scrollView = ref;
            }
          }}
          style={{backgroundColor: SRXColor.White}}
          sections={sections}
          nestedScrollEnabled={true}
          stickySectionHeadersEnabled={false}
          renderItem={({index, item, section}) =>
            this.renderItem({index, item, section})
          }
          renderSectionHeader={({section}) =>
            this.renderSectionHeader({section})
          }
          onEndReachedThreshold={0.01}
          onEndReached={this.loadMore}
          ListFooterComponent={this.renderFooter}
          onScrollToIndexFailed={error => {
            console.log(error);
          }}
        />
      );
    }
  }

  onLoadPreviousComments() {
    //LoadMore for Oldest to Newest
    const {
      selectedSortType,
      retrievedCommentCount,
      communityPostPO,
    } = this.state;
    if (
      this.searchManager &&
      this.searchManager.canLoadMore(
        retrievedCommentCount,
        communityPostPO.commentsTotal,
      ) &&
      selectedSortType.value ===
        CommunitiesCommentConstant.sortOptionsValue.oldestToNewest
    ) {
      this.setState({commentLoadingState: LoadingState.Loading}, () => {
        this.searchManager.loadMore(retrievedCommentCount);
      });
    }
  }

  loadMore() {
    //LoadMore for Newest to Oldest
    const {
      selectedSortType,
      retrievedCommentCount,
      communityPostPO,
    } = this.state;
    if (
      this.searchManager &&
      this.searchManager.canLoadMore(
        retrievedCommentCount,
        communityPostPO.commentsTotal,
      ) &&
      selectedSortType.value ==
        CommunitiesCommentConstant.sortOptionsValue.newestToOldest
    ) {
      this.searchManager.loadMore(retrievedCommentCount);
    }
  }

  renderFooter = () => {
    if (this.searchManager) {
      const {
        retrievedCommentCount,
        communityPostPO,
        selectedSortType,
      } = this.state;
      if (
        this.searchManager.canLoadMore(
          retrievedCommentCount,
          communityPostPO.commentsTotal,
        ) &&
        selectedSortType.value ===
          CommunitiesCommentConstant.sortOptionsValue.newestToOldest
      ) {
        return <LoadMoreIndicator />;
      }
    }
    return null;
  };

  renderBottomActionView() {
    const {showKeyboard, communityPostPO} = this.state;
    //Note: News post & sponored business type are not allowed to comment
    var isSponsoredListingType =
      !ObjectUtil.isEmpty(communityPostPO) &&
      !ObjectUtil.isEmpty(communityPostPO.sponsored) &&
      communityPostPO.sponsored.sponsoredType ===
        CommunitiesConstant.sponsoredType.listing;
    if (
      !ObjectUtil.isEmpty(communityPostPO) &&
      communityPostPO.type != CommunitiesConstant.postType.news &&
      !isSponsoredListingType
    ) {
      return (
        <View>
          <Separator />
          <CommunitiesCommentComposer
            postId={communityPostPO.id}
            showKeyboard={showKeyboard}
            onKeyboardShow={() => {
              var commentSection = this.getCommentSection();
              if (!ObjectUtil.isEmpty(commentSection)) {
                this.scrollToCommentSection(commentSection.data.length);
              }
            }}
            onKeyboardHide={() => this.setState({showKeyboard: false})}
            onCommentSuccess={response => this.onCommentSuccess(response)}
          />
        </View>
      );
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
      if (IS_IOS) {
        return (
          <SafeAreaView style={{flex: 1}}>
            <KeyboardAvoidingView
              style={{flex: 1}}
              behavior="padding"
              keyboardVerticalOffset={IS_IPHONE_X ? 85 : 55}>
              <View style={Styles.mainContainer}>
                {this.renderDetailSections()}
                {this.renderBottomActionView()}
              </View>
            </KeyboardAvoidingView>
          </SafeAreaView>
        );
      } else {
        return (
          <View style={Styles.mainContainer}>
            {this.renderDetailSections()}
            {this.renderBottomActionView()}
          </View>
        );
      }
    } else if (loadingState == LoadingState.Failed) {
      return (
        <SafeAreaView style={Styles.loadingContainer}>
          <BodyText style={{textAlign: 'center'}}>
            {this.state.postErrorMessage}
          </BodyText>
        </SafeAreaView>
      );
    } else {
      return <View />;
    }
  }
}

CommunitiesPostDetails.propTypes = {
  /** post id to get post details */
  postId: PropTypes.number.isRequired,

  /** check it's showing post details by pressing comment or not */
  isCommentBtnPressed: PropTypes.bool,

  /** indicate if this post is a business grc post */
  isBusinssPost: PropTypes.bool,
};

const mapStateToProps = state => {
  return {
    communities: state.communitiesData.list,
    userPO: state.loginData.userPO,
  };
};

const Styles = {
  mainContainer: {
    flex: 1,
    backgroundColor: SRXColor.White,
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
};

export default connect(mapStateToProps)(CommunitiesPostDetails);
