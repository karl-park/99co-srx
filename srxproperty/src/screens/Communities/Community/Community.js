import React, {Component} from 'react';
import {View, RefreshControl, SafeAreaView, Image} from 'react-native';
import {Navigation} from 'react-native-navigation';
import {SRXColor} from '../../../constants';
import {connect} from 'react-redux';
import {Communities_EmptyList_Icon} from '../../../assets';
import {selectCommunity, getCommunities, selectSortBy} from '../../../actions';
import {CommunitiesStack} from '../../../config';
import {CommunityPostPO, AgentPO} from '../../../dataObject';
import {CommunitiesService} from '../../../services';
import {ObjectUtil, CommunitiesUtil, SetUtil} from '../../../utils';
import {CommunityInvitation} from '../Community';
import {CommunitiesFeedList, CommunitiesFeedFilters} from '../Feeds';
import {CommunitiesConstant} from '../Constants';
import {EventRegister} from 'react-native-event-listeners';
import {DetailActionOptionsConstant} from '../PostDetails/DetailsModal/DetailActionOptionsConstant';
import {CommunitiesNewPostAction} from '../Post/CommunitiesNewPostAction';
import {EnquiryForm} from '../../Enquiry';
import {Subtext} from '../../../components';
import {Spacing} from '../../../styles';
import {Button} from '../../../components/Button/Button';

class Community extends Component {
  state = {
    posts: [],
    total: 0,
    isLoading: false,
    isRefreshing: false,
  };

  constructor(props) {
    super(props);

    this.isFlatListOnTop = false;

    //register event
    Navigation.events().bindComponent(this);
    this.eventSubscription = Navigation.events().registerBottomTabSelectedListener(
      this.tabChanged,
    );

    this.loadPosts = this.loadPosts.bind(this);
    this.loadMore = this.loadMore.bind(this);
    this.reload = this.reload.bind(this);
    this.refresh = this.refresh.bind(this);
    this.renderListEmptyComponent = this.renderListEmptyComponent.bind(this);
    this.onNewPostPosted = this.onNewPostPosted.bind(this);
    this.onCommunitySelected = this.onCommunitySelected.bind(this);
    this.renderHeader = this.renderHeader.bind(this);
    this.tabChanged = this.tabChanged.bind(this);
  }

  componentDidMount() {
    const {previousPosts} = this.props;

    this.registerEventListener();
    this.reload();

    if (!ObjectUtil.isEmpty(previousPosts)) {
      const {feedList, total} = previousPosts;
      if (!ObjectUtil.isEmpty(feedList)) {
        this.setState({posts: feedList, total: total});
      }
    }
  }

  componentDidUpdate(prevProps, prevState) {
    if (prevProps.selectedCommunity.id != this.props.selectedCommunity.id) {
      this.reload();
    }

    if (prevProps.properties !== this.props.properties) {
      const {properties, getCommunities, selectCommunity} = this.props;

      let prevPropertiesLength = 0;
      let currentPropertiesLength = 0;
      if (!ObjectUtil.isEmpty(prevProps.properties)) {
        prevPropertiesLength = prevProps.properties.length;
      }
      if (!ObjectUtil.isEmpty(properties)) {
        currentPropertiesLength = properties.length;
      }

      if (prevPropertiesLength > currentPropertiesLength) {
        /***
         * Note: need to call reload.
         * if initial community and new selected community is not same,
         * so will reload in selectedCommunity condition
         * if both initial community and new community is Singapore, reload
         * in this properties condition
         */
        if (
          prevProps.selectedCommunity.id ==
          CommunitiesConstant.singaporeCommunity.id
        ) {
          this.reload();
        }
        selectCommunity({
          communityItem: CommunitiesConstant.singaporeCommunity,
        });
        getCommunities();
      } else if (currentPropertiesLength > prevPropertiesLength) {
        //add new property and refresh list
        getCommunities();
        this.reload();
      }
    }

    if (prevProps.userPO !== this.props.userPO) {
      const {selectSortBy} = this.props;
      selectSortBy({sortByItem: CommunitiesConstant.sortOptions[0]});
    }

    if (prevProps.selectedSortBy !== this.props.selectedSortBy) {
      this.reload();
    }
  }

  componentWillUnmount() {
    if (this.eventSubscription) {
      this.eventSubscription.remove();
    }
    EventRegister.removeEventListener(this.listener);
  }

  tabChanged = ({selectedTabIndex, unselectedTabIndex}) => {
    if (selectedTabIndex === 0 && this.feedList) {
      if (this.isFlatListOnTop == true) {
        //change tab to home tab
        const {showFindHomeTab} = this.props;
        if (showFindHomeTab) {
          showFindHomeTab();
        }
      } else {
        this.feedList.scrollToTop();
      }
    }
  };

  registerEventListener() {
    this.listener = EventRegister.addEventListener(
      CommunitiesConstant.events.updateFeedListEvent,
      async data => {
        await this.updateFeedList(data);
      },
    );
  }

  updateFeedList = data => {
    const {action, communityPostPO} = data;
    const {posts} = this.state;

    if (action === 'delete' || action === 'share' || action == 'update') {
      this.reload();
    } else if (action === 'comment' || action === 'react') {
      var tempPosts = posts;
      var updatedPostIndex = tempPosts.findIndex(
        item => item.id === communityPostPO.id,
      );
      if (updatedPostIndex > -1) {
        tempPosts[updatedPostIndex] = new CommunityPostPO(communityPostPO);
        this.setState({posts: tempPosts}, () => {
          this.savePosts();
        });
      }
    }
  };

  directToAddMyProperty = invitationInfo => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'MyProperties.AddUpdateMyProperty',
        passProps: {
          initiatingComponentId: this.props.componentId,
          invitationInfo,
        },
      },
    });
  };

  //check mobile verified before like, comment, share with all of SG feed
  //TODO: check all of SG feed or not
  onCheckMobileVerified = () => {
    const {userPO} = this.props;
    if (!ObjectUtil.isEmpty(userPO)) {
      if (userPO.mobileVerified) {
        return true;
      } else {
        CommunitiesStack.showMobileVerificationModal();
      }
    } else {
      CommunitiesStack.showSignInRegisterModal();
    }
  };

  onNewPostInitiate = () => {
    //Note: special case. create new post screen need to show "Singapore"
    const {selectedCommunity, communities} = this.props;
    var tempSelectedCommunity = selectedCommunity;
    if (
      !ObjectUtil.isEmpty(selectedCommunity) &&
      selectedCommunity.id == CommunitiesConstant.singaporeCommunity.id
    ) {
      var singapore = communities.find(
        item => item.id == CommunitiesConstant.singaporeCommunity.id,
      );
      if (!ObjectUtil.isEmpty(singapore)) {
        tempSelectedCommunity = singapore;
      }
    }
    CommunitiesStack.showNewPostModal({
      onPosted: this.onNewPostPosted,
      initialCommunity: tempSelectedCommunity,
      action: CommunitiesNewPostAction.CreateNewPost,
    });
  };

  onNewPostPosted = ({post, inCommunity}) => {
    const {selectCommunity, selectedCommunity} = this.props;

    if (selectedCommunity.id !== inCommunity.id) {
      //updating this will causing the list to be reloaded
      selectCommunity({communityItem: inCommunity});
    } else {
      this.reload();
    }
  };

  onSortSelected = item => {
    const {selectSortBy} = this.props;
    selectSortBy({sortByItem: item});
  };

  onCommunitySelected(community) {
    const {selectCommunity} = this.props;
    selectCommunity({communityItem: community});
  }

  reload() {
    this.loadPosts(0, 10);
  }

  refresh() {
    this.setState({isRefreshing: true}, () => {
      this.reload();
    });
  }

  loadMore() {
    const {posts, total, isLoading} = this.state;
    if (
      isLoading == false &&
      !ObjectUtil.isEmpty(posts) &&
      posts.length < total
    ) {
      this.loadPosts(posts.length, 10);
    }
  }

  loadPosts(startIndex, maxResults) {
    const {
      selectedCommunity,
      selectedSortBy,
      reloadCommunityCount,
    } = this.props;
    this.setState({isLoading: reloadCommunityCount <= 1 ? true : false}, () => {
      CommunitiesService.findPosts({
        startIndex: startIndex,
        maxResults: maxResults,
        orderBy: selectedSortBy.value,
        communityId: selectedCommunity.id,
      })
        .then(response => {
          /**
           * {
           *  posts: [CommunityPostPO],
           *  total: number of posts in total
           * }
           */

          const {posts, total} = response;
          const newPosts = [];

          if (!ObjectUtil.isEmpty(posts)) {
            posts.map(item => {
              newPosts.push(new CommunityPostPO(item));
            });
          }
          if (startIndex === 0) {
            CommunitiesService.findCommunityAdvisor({
              communityId: selectedCommunity.id,
            })
              .then(response2 => {
                const {advisors} = response2;
                if (advisors.length > 0) {
                  newPosts.splice(1, 0, {
                    ...advisors[0],
                    id: 'findCommunityId',
                  });
                }
                this.setState(
                  {
                    isLoading: false,
                    isRefreshing: false,
                    posts: newPosts,
                    total,
                  },
                  () => {
                    this.savePosts();
                  },
                );
              })
              .catch(err => {
                console.log(err);
                this.setState(
                  {
                    isLoading: false,
                    isRefreshing: false,
                    posts: newPosts,
                    total,
                  },
                  () => {
                    this.savePosts();
                  },
                );
              });
          } else {
            this.setState(
              {
                isLoading: false,
                isRefreshing: false,
                posts: [...this.state.posts, ...newPosts],
                total,
              },
              () => {
                this.savePosts();
              },
            );
          }
        })
        .catch(error => {
          console.log(error);
        });
    });
  }

  savePosts() {
    const {saveFeedList} = this.props;
    const {posts, total} = this.state;
    if (saveFeedList) {
      saveFeedList(posts, total);
    }
  }

  showProfileUpdate() {
    Navigation.showModal({
      stack: {
        children: [
          {
            component: {
              name: 'SideMainMenu.UpdateProfile',
              passProps: {
                isModal: true,
              },
            },
          },
        ],
      },
    });
  }

  renderCommunityAndSort() {
    const {selectedCommunity, communities, selectedSortBy} = this.props;
    return (
      <CommunitiesFeedFilters
        //postsTotal={total}
        hasCommunities={!ObjectUtil.isEmpty(communities)}
        selectedSortType={selectedSortBy}
        onSortTypeSelected={this.onSortSelected}
        selectedCommunity={selectedCommunity}
        onCommunitySelected={this.onCommunitySelected}
        onAddPropertyPressed={this.directToAddMyProperty}
      />
    );
  }

  renderCommunityInvitation() {
    const {reloadCommunityCount, communities} = this.props;
    return (
      <CommunityInvitation
        hasCommunities={!ObjectUtil.isEmpty(communities)}
        reloadCommunityCount={reloadCommunityCount}
        onAddPropertyPressed={this.directToAddMyProperty}
        onNewPostInitiate={this.onNewPostInitiate}
        onUpdateCommunityName={this.showProfileUpdate}
      />
    );
  }

  renderHeader() {
    return (
      <View>
        {this.renderCommunityInvitation()}
        {this.renderCommunityAndSort()}
      </View>
    );
  }

  renderListEmptyComponent() {
    return (
      <View
        style={{
          flex: 1,
          backgroundColor: SRXColor.White,
          alignItems: 'center',
          borderTopLeftRadius: 10,
          borderTopRightRadius: 10,
        }}>
        <Image
          style={{
            height: 85,
            aspectRatio: 206 / 182,
            marginTop: Spacing.XL * 2,
            marginBottom: Spacing.L,
          }}
          source={Communities_EmptyList_Icon}
        />
        <Subtext>There 's nothing here yet</Subtext>
        <Button
          onPress={this.onNewPostInitiate}
          buttonStyle={{
            marginTop: Spacing.M,
            height: 36,
            borderRadius: 19,
            borderWidth: 1,
            borderColor: SRXColor.Teal,
            paddingHorizontal: Spacing.M,
          }}>
          Be the first to connect with your community
        </Button>
      </View>
    );
  }

  renderCommunityPosts() {
    const {posts, isRefreshing} = this.state;
    return (
      <CommunitiesFeedList
        onRef={ref => (this.feedList = ref)}
        componentId={this.props.componentId}
        style={{flex: 1}}
        data={posts}
        ListHeaderComponent={this.renderHeader}
        onEndReached={this.loadMore}
        onEndReachedThreshold={3}
        refreshControl={
          <RefreshControl
            style={{backgroundColor: SRXColor.White, marginBottom: -2}}
            refreshing={isRefreshing}
            onRefresh={this.refresh}
          />
        }
        ListEmptyComponent={this.renderListEmptyComponent}
        contentContainerStyle={{flexGrow: 1}}
        onCheckMobileVerified={this.onCheckMobileVerified}
        onScroll={event => {
          if (event && event.nativeEvent.contentOffset.y == 0) {
            this.isFlatListOnTop = true;
          } else {
            this.isFlatListOnTop = false;
          }
        }}
      />
    );
  }

  render() {
    return (
      <SafeAreaView style={{flex: 1, backgroundColor: SRXColor.White}}>
        <View style={{flex: 1, backgroundColor: SRXColor.White}}>
          {this.renderCommunityPosts()}
        </View>
      </SafeAreaView>
    );
  }
}

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
    selectedCommunity: state.communitiesData.selectedCommunity,
    selectedSortBy: state.communitiesData.selectedSortBy,
    communities: CommunitiesUtil.convertCommunityTopDownPOArray(
      state.communitiesData.list,
    ),
    properties: state.myPropertiesData.list,
    serverDomain: state.serverDomain,
  };
};

export default connect(
  mapStateToProps,
  {selectCommunity, getCommunities, selectSortBy},
)(Community);
