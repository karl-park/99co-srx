import React, {Component} from 'react';
import {SafeAreaView, FlatList, View, Alert} from 'react-native';
import PropTypes from 'prop-types';
import {
  CommunitiesUserPostListItem,
  CommunitiesTransactedListItem,
  CommunitiesListingsListItem,
  CommunitiesNewsListItem,
  CommunitiesSponsoredListItem,
  CommunitiesFeedAdvisorAdListItem,
} from './ListItem';
import {SRXColor, AlertMessage} from '../../../constants';
import {CommunityPostPO, ListingPO, AgentPO} from '../../../dataObject';
import {Navigation} from 'react-native-navigation';
import {
  ObjectUtil,
  PropertyTypeUtil,
  NavigationUtil,
  GoogleAnalyticUtil,
} from '../../../utils';
import {CommunitiesConstant} from '../Constants';
import {CommunitiesService, ChatService} from '../../../services';
import {CommunitiesStack, LoginStack} from '../../../config';
import {connect} from 'react-redux';
import {PropertyTypeValueSet} from '../../PropertySearch/Constants';

class CommunitiesFeedList extends Component {
  constructor(props) {
    super(props);

    this.onPostSelected = this.onPostSelected.bind(this);
    this.onReactionSelected = this.onReactionSelected.bind(this);
    this.onCommentSelected = this.onCommentSelected.bind(this);
    this.openPostUrl = this.openPostUrl.bind(this);
    this.directToSharePost = this.directToSharePost.bind(this);
    this.onShowPostCreator = this.onShowPostCreator.bind(this);
    this.onEnquireListing = this.onEnquireListing.bind(this);
    this.showListingDetails = this.showListingDetails.bind(this);
    this.showPostDetails = this.showPostDetails.bind(this);
    this.showListingsFromPost = this.showListingsFromPost.bind(this);
    this.onEnquireSponsoredPost = this.onEnquireSponsoredPost.bind(this);
  }

  componentDidMount() {
    this.props.onRef(this);
  }

  componentWillUnmount() {
    this.props.onRef(undefined);
  }

  scrollToTop() {
    if (this.flatList) {
      this.flatList.scrollToOffset({animated: true, offset: 0});
    }
  }

  onPostSelected(item) {
    if (!ObjectUtil.isEmpty(item)) {
      GoogleAnalyticUtil.trackCommunityActivity({parameters: {type: 'detail'}});
      if (item.type === CommunitiesConstant.postType.news) {
        //note: if parent post is news, show with app browser
        NavigationUtil.openUrl({
          url: item.externalUrl,
          fromComponentId: this.props.componentId,
        });
      } else {
        const {isBusinssPost} = this.props;
        console.log('isbusinessPost' + isBusinssPost);
        this.showPostDetails({postId: item.id, isBusinssPost});
      }
    }
  }

  onCommentSelected(item) {
    const {isBusinssPost} = this.props;
    if (!ObjectUtil.isEmpty(item)) {
      GoogleAnalyticUtil.trackCommunityActivity({parameters: {type: 'detail'}});
      this.showPostDetails({
        postId: item.id,
        isCommentBtnPressed: true,
        isBusinssPost,
      });
    }
  }

  openPostUrl(url) {
    NavigationUtil.openUrl({url, fromComponentId: this.props.componentId});
  }

  showPostDetails({postId, isCommentBtnPressed, isBusinssPost}) {
    console.log('isbusinessPost' + isBusinssPost);
    Navigation.push(this.props.componentId, {
      component: {
        name: 'Communities.CommunitiesPostDetails',
        passProps: {
          postId,
          isCommentBtnPressed,
          isBusinssPost,
        },
      },
    });
  }

  onReactionSelected({post, reaction}) {
    const {onCheckMobileVerified, userPO} = this.props;
    if (!ObjectUtil.isEmpty(post) && !ObjectUtil.isEmpty(reaction)) {
      console.log(reaction);
      GoogleAnalyticUtil.trackCommunityActivity({
        parameters: {type: reaction.name},
      });
      if (!ObjectUtil.isEmpty(userPO) && userPO.mobileVerified) {
        //update count 1st
        if (!ObjectUtil.isEmpty(post.reactions)) {
          let selected = post.reactions.find(item => item.reacted);
          if (!ObjectUtil.isEmpty(selected)) {
            //if originally selected reaction is same as current selected, it will be unselect, hence, remove count
            if (selected.cdReaction === reaction.cdReaction) {
              reaction.count -= 1;
              reaction.reacted = false;
            } else {
              //originally selected reaction is different with the current selected, so need to add 1 to current selected, and remove 1 from originally selected
              post.reactions = post.reactions.map(item => {
                if (item.cdReaction === reaction.cdReaction) {
                  item.count += 1;
                  item.reacted = true;
                } else if (item.cdReaction === selected.cdReaction) {
                  item.count -= 1;
                  item.reacted = false;
                }
                return item;
              });
            }
          } else {
            //since no originally selected reaction, directly add count + 1
            post.reactions = post.reactions.map(item => {
              if (item.cdReaction === reaction.cdReaction) {
                item.count += 1;
                item.reacted = true;
              }
              return item;
            });
          }
          this.forceUpdate();
        }

        CommunitiesService.postReaction({
          id: post.id,
          reactionId: reaction.cdReaction,
        })
          .then(response => {
            if (response) {
              const {reactions} = response;
              post.reactions = reactions;
              this.forceUpdate(); //update this directly as it is not save in state
            }
          })
          .catch(error => {
            console.log(error);
          });
      } else {
        if (onCheckMobileVerified) {
          onCheckMobileVerified();
        }
      }
    }
  }

  directToSharePost(postPO) {
    const {onCheckMobileVerified, userPO} = this.props;
    if (!ObjectUtil.isEmpty(postPO)) {
      GoogleAnalyticUtil.trackCommunityActivity({parameters: {type: 'share'}});
      if (!ObjectUtil.isEmpty(userPO) && userPO.mobileVerified) {
        const passProps = {
          previousComponentId: this.props.componentId,
          post: postPO,
        };
        CommunitiesStack.showShareOptionsModal(passProps);
      } else {
        if (onCheckMobileVerified) {
          onCheckMobileVerified();
        }
      }
    }
  }

  //Go to listing details
  showListingDetails(listingPO) {
    if (!ObjectUtil.isEmpty(listingPO)) {
      Navigation.push(this.props.componentId, {
        component: {
          name: 'PropertySearchStack.ListingDetails',
          passProps: {
            listingId: listingPO.getListingId(),
            refType: listingPO.listingType,
          },
        },
      });
    }
  }

  onShowPostCreator(postPO) {
    const {userPO} = this.props;
    if (!ObjectUtil.isEmpty(userPO)) {
      //if login
      if (postPO.type === CommunitiesConstant.postType.listing) {
        CommunitiesStack.showPostContact({
          sourceComponentId: this.props.componentId,
          post: postPO,
        });
      } else if (
        postPO.type === CommunitiesConstant.postType.sponsored ||
        postPO.type === CommunitiesConstant.postType.normal
      ) {
        //go to chat
        const {user} = postPO;
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
      } else if (postPO.id === 'findCommunityId') {
        //Currently there is only 1 item for this view, therefore using custom created id for it
        const {agentPO} = postPO;
        let chatUser = {
          userId: agentPO.userId,
          conversationId: null, //assume it's null
          name: agentPO.name,
          photo: agentPO.photo,
          mobile: agentPO.mobileLocalNum,
        };

        this.trackCommunityAdvisorImpressionAction(postPO.impressionId,1);
        
        Navigation.push(this.props.componentId, {
          component: {
            name: 'ChatStack.chatRoom',
            passProps: {
              agentInfo: chatUser,
              // listingPO, //to be modified
            },
          },
        });
      }
    } else {
      //not logged in yet
      LoginStack.showSignInRegisterModal();
    }
  }

  trackCommunityAdvisorImpressionAction(impression, action){
    CommunitiesService.trackCommunityAdvisorImpressionAction({
      impressionId:impression,
      trackAction: action,
    })
      .then(response => {
        if (response) {
          console.log("trackCommunityAdvisorImpressionAction");
          console.log(response);
        }
      })
      .catch(error => {
        console.log(error);
      });
  }

  onEnquireListing(listing) {
    if (!ObjectUtil.isEmpty(listing)) {
      const message =
        'Do you wish to submit an enquiry for this ' +
        (listing.isTransacted() ? 'transacted ' : '') +
        'listing?';
      Alert.alert('', message, [
        {text: 'No', style: 'cancel'},
        {
          text: 'Yes',
          onPress: () => {
            this.sendAgentChatWithListing(listing);
          },
        },
      ]);
    }
  }

  onEnquireSponsoredPost(post) {
    if (!ObjectUtil.isEmpty(post.listing)) {
      const message = 'Do you wish to submit an enquiry for this post?';
      Alert.alert('', message, [
        {text: 'No', style: 'cancel'},
        {
          text: 'Yes',
          onPress: () => {
            this.sendSSMForPost(post);
          },
        },
      ]);
    }
  }

  sendAgentChatWithListing = listing => {
    const {onCheckMobileVerified, userPO} = this.props;
    if (!ObjectUtil.isEmpty(userPO) && userPO.mobileVerified) {
      if (
        !ObjectUtil.isEmpty(listing) &&
        !ObjectUtil.isEmpty(listing.agentPO)
      ) {
        let userIdString = listing.agentPO.userId.toString();
        let userIds = JSON.stringify([userIdString]);
        ChatService.createSsmConversation({
          userIds,
          isBlast: false,
        })
          .then(response => {
            if (!ObjectUtil.isEmpty(response)) {
              if (response.Success) {
                let converseId = response.Success;

                let message =
                  'Hi, I’m interested in your listing please contact me ASAP!';
                if (listing.isTransacted()) {
                  message =
                    'Hi, I’m interested to know more about your transactions, please contact me ASAP!';
                }

                ChatService.sendSsm({
                  conversationId: converseId,
                  userId: userPO.encryptedUserId,
                  isBlast: false,
                  messageType: !ObjectUtil.isEmpty(listing) ? 2 : 1,
                  message,
                  listingId: !ObjectUtil.isEmpty(listing) ? listing.id : null,
                }).then(response => {
                  let chatUser = {
                    userId: listing.agentPO.getAgentId(),
                    conversationId: null, //assume it's null
                    name: listing.agentPO.name,
                    photo: listing.agentPO.photo,
                    mobile: listing.agentPO.getMobileNumber(),
                    agent: true,
                  };
                  Navigation.push(this.props.componentId, {
                    component: {
                      name: 'ChatStack.chatRoom',
                      passProps: {
                        agentInfo: chatUser,
                        // listingPO, //to be modified
                      },
                    },
                  });
                });
              }
            }
          })
          .catch(error => {
            console.log(error);
          });
      }
    } else {
      if (onCheckMobileVerified) {
        onCheckMobileVerified();
      }
    }
  };

  /**
   *
   * Basically for sponsored post now
   * But one thing i dont understand is what it checks for listing
   * Probably according to panda, currently is for advertise listing
   * But after checking whether it contain a listing, it checks if the listing is empty again.
   * This is the part I don't understand and it seems never go in to that block
   */
  sendSSMForPost = post => {
    const {listing} = post;
    const {onCheckMobileVerified, userPO} = this.props;
    if (!ObjectUtil.isEmpty(userPO) && userPO.mobileVerified) {
      if (
        !ObjectUtil.isEmpty(listing) &&
        !ObjectUtil.isEmpty(listing.agentPO)
      ) {
        let userIdString = listing.agentPO.userId.toString();
        let userIds = JSON.stringify([userIdString]);
        ChatService.createSsmConversation({
          userIds,
          isBlast: false,
        })
          .then(response => {
            if (!ObjectUtil.isEmpty(response)) {
              if (response.Success) {
                let converseId = response.Success;

                let message =
                  'Hi, I’m interested in your listing please contact me ASAP!';
                if (listing.isTransacted()) {
                  message =
                    'Hi, I’m interested to know more about your transactions, please contact me ASAP!';
                }
                //non-listing sponsored post
                else if (
                  post.type === CommunitiesConstant.postType.sponsored &&
                  ObjectUtil.isEmpty(listing)
                ) {
                  message = 'Hi, would like to find out more about your post!';
                }
                ChatService.sendSsm({
                  conversationId: converseId,
                  userId: userPO.encryptedUserId,
                  isBlast: false,
                  messageType: !ObjectUtil.isEmpty(listing) ? 2 : 1,
                  message,
                  listingId: !ObjectUtil.isEmpty(listing) ? listing.id : null,
                }).then(response => {
                  let chatUser = {
                    userId: listing.agentPO.getAgentId(),
                    conversationId: null, //assume it's null
                    name: listing.agentPO.name,
                    photo: listing.agentPO.photo,
                    mobile: listing.agentPO.getMobileNumber(),
                    agent: true,
                  };
                  Navigation.push(this.props.componentId, {
                    component: {
                      name: 'ChatStack.chatRoom',
                      passProps: {
                        agentInfo: chatUser,
                        // listingPO, //to be modified
                      },
                    },
                  });
                });
              }
            }
          })
          .catch(error => {
            console.log(error);
          });
      }
    } else {
      if (onCheckMobileVerified) {
        onCheckMobileVerified();
      }
    }
  };

  //will be deleted soon once confirmed
  onEnquireListingWithSRXChat = listingPO => {
    let chatUser;
    if (
      !ObjectUtil.isEmpty(listingPO) &&
      !ObjectUtil.isEmpty(listingPO.agentPO)
    ) {
      chatUser = {
        userId: listingPO.agentPO.getAgentId(),
        conversationId: null, //assume it's null
        name: listingPO.agentPO.name,
        photo: listingPO.agentPO.photo,
        mobile: listingPO.agentPO.getMobileNumber(),
        agent: true,
      };
    }

    if (!ObjectUtil.isEmpty(chatUser)) {
      Navigation.push(this.props.componentId, {
        component: {
          name: 'ChatStack.chatRoom',
          passProps: {
            agentInfo: chatUser,
            listingPO, //to be modified
          },
        },
      });
    }
  };

  showListingsFromPost(post) {
    if (!ObjectUtil.isEmpty(post)) {
      const {listing} = post;
      if (!ObjectUtil.isEmpty(listing)) {
        var newSearchOptions = {
          type: listing.type,
          searchText: listing.name,
          displayText: listing.name,
        };

        if (PropertyTypeUtil.isHDB(listing.cdResearchSubType)) {
          newSearchOptions.cdResearchSubTypes = Array.from(
            PropertyTypeValueSet.hdb,
          ).join(',');
        } else if (PropertyTypeUtil.isCondo(listing.cdResearchSubType)) {
          newSearchOptions.cdResearchSubTypes = Array.from(
            PropertyTypeValueSet.condo,
          ).join(',');
        } else if (PropertyTypeUtil.isLanded(listing.cdResearchSubType)) {
          newSearchOptions.cdResearchSubTypes = Array.from(
            PropertyTypeValueSet.landed,
          ).join(',');
        } else {
          newSearchOptions.cdResearchSubTypes = Array.from(
            PropertyTypeValueSet.commercial,
          ).join(',');
        }

        Navigation.push(this.props.componentId, {
          component: {
            name: 'PropertySearchStack.searchResult',
            passProps: {
              initialSearchOptions: newSearchOptions,
            },
          },
        });
      }
    }
  }

  onAgentSelected = post => {
    const {agentPO} = post;
    this.trackCommunityAdvisorImpressionAction(post.impressionId,2);
    Navigation.push(this.props.componentId, {
      component: {
        name: 'ConciergeStack.AgentCV',
        passProps: {
          agentUserId: agentPO.userId,
        },
      },
    });
  };

  render() {
    const {data, ListHeaderComponent, ...rest} = this.props;
    console.log(data);
    console.log('before flatlist');
    return (
      <FlatList
        {...rest}
        ref={ref => (this.flatList = ref)}
        style={{flex: 1, backgroundColor: SRXColor.SmallBodyBackground}}
        data={data}
        ListHeaderComponent={ListHeaderComponent}
        keyExtractor={item => item.id.toString()}
        renderItem={({item, index}) => {
          if (item instanceof CommunityPostPO) {
            if (item.type === CommunitiesConstant.postType.normal) {
              //normal post
              return (
                <CommunitiesUserPostListItem
                  post={item}
                  onPostSelected={this.onPostSelected}
                  onReactionSelected={this.onReactionSelected}
                  onSharePressed={this.directToSharePost}
                  onCommentPressed={this.onCommentSelected}
                  onLinkPressed={this.openPostUrl}
                  onPostCreatorPressed={this.onShowPostCreator}
                />
              );
            } else if (item.type === CommunitiesConstant.postType.listing) {
              //listing
              return (
                <CommunitiesListingsListItem
                  post={item}
                  onSharePressed={this.directToSharePost}
                  onEnquireListing={this.onEnquireListing}
                  onViewListingPressed={this.showListingDetails}
                  onPostCreatorPressed={this.onShowPostCreator}
                  onGotoListingsPressed={this.showListingsFromPost}
                />
              );
            } else if (
              item.type === CommunitiesConstant.postType.transactedListing
            ) {
              return (
                <CommunitiesTransactedListItem
                  post={item}
                  onSharePressed={this.directToSharePost}
                  onEnquireListing={this.onEnquireListing}
                />
              );
            } else if (item.type === CommunitiesConstant.postType.news) {
              return (
                <CommunitiesNewsListItem
                  post={item}
                  onPostSelected={this.onPostSelected}
                  onReactionSelected={this.onReactionSelected}
                  onSharePressed={this.directToSharePost}
                  onCommentPressed={this.onCommentSelected}
                />
              );
            } else if (item.type === CommunitiesConstant.postType.sponsored) {
              return (
                <CommunitiesSponsoredListItem
                  post={item}
                  onPostSelected={this.onPostSelected}
                  onSharePressed={this.directToSharePost}
                  onPostCreatorPressed={this.onShowPostCreator}
                  onEnquiryPressed={this.onEnquireSponsoredPost}
                />
              );
            }
          } else if (item.msg) {
            return (
              <CommunitiesFeedAdvisorAdListItem
                onChatSelected={this.onShowPostCreator}
                post={item}
                onAgentSelected={this.onAgentSelected}
                // onPostSelected={this.onPostSelected}
                // onSharePressed={this.directToSharePost}
                // onPostCreatorPressed={this.onShowPostCreator}
                // onEnquiryPressed={this.onEnquireSponsoredPost}
                // onReactionSelected={this.onReactionSelected}
                // onCommentPressed={this.onCommentSelected}
              />
            );
          } else {
            return <View />;
          }
        }}
        onScrollToIndexFailed={error => {
          console.log(error);
        }}
      />
    );
  }
}

CommunitiesFeedList.propTypes = {
  data: PropTypes.arrayOf(
    PropTypes.oneOfType([
      PropTypes.instanceOf(CommunityPostPO),
      PropTypes.object,
    ]),
  ),
  //The PropTypes.object is mainly for the specific Community Advisor object in format {AgentPO,msg}

  onCheckMobileVerified: PropTypes.func,
  /**
   * indicate if this post is a business grc post
   * if there are move types please change to enum
   * if there are different options, please move the methods out from this files
   */
  isBusinssPost: PropTypes.bool,
};

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
  };
};

export default connect(mapStateToProps)(CommunitiesFeedList);
