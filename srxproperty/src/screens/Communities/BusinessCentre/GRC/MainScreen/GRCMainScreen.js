import React, {Component} from 'react';
import {View, Image, RefreshControl} from 'react-native';
import {EventRegister} from 'react-native-event-listeners';
import {Navigation} from 'react-native-navigation';
import {connect} from 'react-redux';
import {Object} from 'realm';
import {getElectoralCommunityBusiness} from '../../../../../actions';
import {GRC_Empty_Icon, GRC_Coming_Soon} from '../../../../../assets';
import {
  LargeTitleComponent,
  SmallBodyText,
  Button,
  Heading2,
  Heading1,
} from '../../../../../components';
import {CommunitiesStack} from '../../../../../config';
import {LoadingState, SRXColor} from '../../../../../constants';
import {CommunityItem, CommunityPostPO} from '../../../../../dataObject';
import {CommunitiesPostBusinessService} from '../../../../../services';
import {Spacing, Typography} from '../../../../../styles';
import {ObjectUtil} from '../../../../../utils';
import {CommunitiesConstant} from '../../../Constants';
import {CommunitiesFeedList} from '../../../Feeds';
import CommunityOptionsMap from '../../../Sort/CommunitySelection/CommunityOptionsMap';
import {GRCCreatePostView} from './components';

class GRCMainScreen extends LargeTitleComponent {
  static options(passProps) {
    return {
      bottomTabs: {
        visible: false,
        animate: true,
        drawBehind: true,
      },
    };
  }

  state = {
    posts: [],
    total: 0,
    isLoading: false,
    isRefreshing: false,
  };

  constructor(props) {
    super(props);

    this.renderHeader = this.renderHeader.bind(this);
    this.renderListEmptyComponent = this.renderListEmptyComponent.bind(this);
    this.loadMore = this.loadMore.bind(this);
    this.reload = this.reload.bind(this);
    this.refresh = this.refresh.bind(this);
    this.onSponsoredPostCreated = this.onSponsoredPostCreated.bind(this);

    const {business, communities} = props;
    if (ObjectUtil.isEmpty(business)) {
      this.loadCommunityBusiness();
    } else {
      if (!ObjectUtil.isEmpty(communities)) {
        const firstCommunity = communities[0];
        this.state = {
          ...this.state,
          community: new CommunityItem(firstCommunity),
        };
      }
      this.updateTitle();
    }
  }

  componentDidMount() {
    this.reload();
    this.registerEventListener();
  }

  componentWillUnmount() {
    EventRegister.removeEventListener(this.listener);
  }

  componentDidUpdate(prevProps, prevState) {
    if (prevProps.business !== this.props.business) {
      const {communities} = this.props;
      if (
        !ObjectUtil.isEmpty(communities) &&
        prevProps.communities !== this.props.communities
      ) {
        const firstCommunity = communities[0];
        this.setState(
          {
            community: new CommunityItem(firstCommunity),
          },
          () => {
            this.updateTitle();
            this.reload();
          },
        );
      }
    }
  }

  updateTitle() {
    const {business} = this.props;
    if (!ObjectUtil.isEmpty(business)) {
      Navigation.mergeOptions(this.props.componentId, {
        topBar: {
          title: {
            component: {
              // id: 'GRCBusinessTopBar',
              name: 'GRC.MainScreen.TopBar',
              passProps: {
                business: business,
              },
            },
          },
          backButton: {
            visible: true,
          },
        },
      });
    }
  }

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
        this.setState({posts: tempPosts});
      }
    }
  };

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
    const {business} = this.props;
    if (!ObjectUtil.isEmpty(business)) {
      this.setState({isLoading: true}, () => {
        CommunitiesPostBusinessService.findPosts({
          startIndex: startIndex,
          maxResults: maxResults,
          businessId: business.id,
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
              this.setState({
                isLoading: false,
                isRefreshing: false,
                posts: newPosts,
                total,
              });
            } else {
              this.setState({
                isLoading: false,
                isRefreshing: false,
                posts: [...this.state.posts, ...newPosts],
                total,
              });
            }
          })
          .catch(error => {
            console.log(error);
          });
      });
    }
  }

  createNewPost() {
    const {communities, business} = this.props;
    let community = null;
    if (!ObjectUtil.isEmpty(communities)) {
      const firstCommunity = communities[0];
      community = new CommunityItem(firstCommunity);
    }
    CommunitiesStack.showGRCNewPost({
      business,
      community,
      onSponsoredPostCreated: this.onSponsoredPostCreated,
    });
  }

  onSponsoredPostCreated() {
    this.refresh();
  }

  loadCommunityBusiness() {
    const {getElectoralCommunityBusiness} = this.props;
    getElectoralCommunityBusiness();
  }

  renderListEmptyComponent() {
    const {community} = this.state;
    return (
      <View
        style={{
          flex: 1,
          backgroundColor: SRXColor.White,
          alignItems: 'center',
          paddingTop: 50,
        }}>
        <Image source={GRC_Empty_Icon} style={{height: 73, width: 85}} />
        <SmallBodyText style={{marginTop: Spacing.M}}>
          You have not created any posts yet
        </SmallBodyText>
        <GRCCreatePostView
          onCreatePost={() => this.createNewPost()}
          community={community}
        />
      </View>
    );
  }

  renderMap() {
    const {community} = this.state;
    return <CommunityOptionsMap community={community} />;
  }

  renderHeader() {
    const {posts, community} = this.state;
    return (
      <View
        style={{
          backgroundColor: 'transparent',
        }}>
        {this.renderMap()}
        {ObjectUtil.isEmpty(posts) ? null : (
          <View
            style={{
              overflow: 'hidden',
              borderBottomLeftRadius: 10,
              borderBottomRightRadius: 10,
            }}>
            <GRCCreatePostView
              onCreatePost={() => this.createNewPost()}
              community={community}
            />
          </View>
        )}
        {ObjectUtil.isEmpty(posts) ? null : (
          <Heading2
            style={{marginHorizontal: Spacing.M, marginVertical: Spacing.S}}>
            My Posts
          </Heading2>
        )}
      </View>
    );
  }

  renderFeeds() {
    const {posts, isRefreshing} = this.state;
    return (
      <CommunitiesFeedList
        onRef={ref => (this.feedList = ref)}
        componentId={this.props.componentId}
        style={{flex: 1}}
        data={posts}
        ListHeaderComponent={this.renderHeader}
        ListEmptyComponent={this.renderListEmptyComponent}
        contentContainerStyle={{flexGrow: 1}}
        onEndReached={this.loadMore}
        onEndReachedThreshold={3}
        refreshControl={
          <RefreshControl
            style={{backgroundColor: SRXColor.White, marginBottom: -2}}
            refreshing={isRefreshing}
            onRefresh={this.refresh}
          />
        }
        isBusinssPost={true}
      />
    );
  }

  /**
   * If no business, show big title with comming soon view
   */
  renderNoBusinessScreen() {
    return (
      <View style={{flex: 1}}>
        {this.renderLargeTitle('Business Centre')}
        <View style={{alignItems: 'center', marginTop: 55}}>
          <Image
            style={{width: 225, height: 138}}
            source={GRC_Coming_Soon}
            resizeMode={'contain'}
          />
          <Heading1 style={{position: 'absolute', bottom: 35}}>
            Coming Soon
          </Heading1>
        </View>
      </View>
    );
  }

  render() {
    const {business, businessLoadingState} = this.props;
    if (businessLoadingState === LoadingState.Loaded) {
      if (!ObjectUtil.isEmpty(business)) {
        return <View style={{flex: 1}}>{this.renderFeeds()}</View>;
      } else {
        return this.renderNoBusinessScreen();
      }
    } else {
      return <View style={{flex: 1}} />;
    }
  }
}

const mapStateToProps = state => {
  return {
    business: state.communitiesBusinessData.business,
    communities: state.communitiesBusinessData.communities,
    businessLoadingState: state.communitiesBusinessData.loadingState,
  };
};

export default connect(
  mapStateToProps,
  {getElectoralCommunityBusiness},
)(GRCMainScreen);
