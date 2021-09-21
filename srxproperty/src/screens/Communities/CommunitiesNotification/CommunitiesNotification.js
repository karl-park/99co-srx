import React, {Component} from 'react';
import {
  View,
  ScrollView,
  SafeAreaView,
  AppState,
  Linking,
  Platform,
  Alert,
  Text,
  FlatList,
} from 'react-native';
import {
  Button,
  FeatherIcon,
  TextInput,
  LargeTitleComponent,
  BodyText,
  Separator,
  Avatar,
  LoadMoreIndicator,
} from '../../../components';
import {Navigation} from 'react-native-navigation';
import {SRXColor} from '../../../constants';
import {Spacing} from '../../../styles';
import {CommunitiesNotificationItem} from '.';
import {CommunitiesService} from '../../../services';
import {ObjectUtil} from '../../../utils';
import { CommunitiesNotificationListener } from '../../../listener';
import PropTypes from 'prop-types';

class CommunitiesNotification extends Component {
  constructor(props) {
    super(props);

    //Set options here
    this.setupTopBar();
    Navigation.events().bindComponent(this);
    this.onPressCloseModal = this.onPressCloseModal.bind(this);
    this.renderPostList = this.renderPostList.bind(this);
    this.state = {
      isAllRetrieved: false,
      loadingMore: false,
      notificationList: [],
      mockUpList: [
        {
          comment: 'notification test last comment',
          commenterName: 'NTZH123',
          notificationDate: 1594177503,
          postId: 741,
          postOwnerName: 'Kok Soon',
          read: false,
          templateMsg:
            "${commenterName} has commented on ${postOwnerName}'s post",
        },
        {
          comment: 'reply to cg post',
          commenterName: 'Kok Soon',
          notificationDate: 1594176779,
          postId: 961,
          postOwnerName: '',
          read: false,
          templateMsg: '${commenterName} has commented on your post',
        },
      ],
      startIndex: 0,
    };
  }

  setupTopBar() {
    FeatherIcon.getImageSource('x', 25, 'black').then(icon_close => {
      Navigation.mergeOptions(this.props.componentId, {
        topBar: {
          leftButtons: [
            {
              id: 'notification_close',
              icon: icon_close,
            },
          ],
          title: {
            text: 'Notifications',
          },
        },
      });
    });
  }

  navigationButtonPressed({buttonId}) {
    if (buttonId === 'notification_close') {
      this.onPressCloseModal();
    }
  }

  onPressCloseModal() {
    return Navigation.dismissModal(this.props.componentId);
  }

  componentDidMount() {
    this.getNotifications();
  }

  getNotifications() {
    const {startIndex, notificationList, isAllRetrieved} = this.state;
    var maxResults = 20;
    if (isAllRetrieved) {
      this.setState({loadingMore: false});
    } else {
      this.setState({loadingMore: true}, () => {
        CommunitiesService.getNotifications({startIndex, maxResults})
          .then(response => {
            if (!ObjectUtil.isEmpty(response)) {
              console.log('Get notifications: ');
              console.log(response);
              var allRetrieved = false;
              if (response.notifications.length < 20) {
                allRetrieved = true;
              }
              console.log('All Retrieved');
              console.log(allRetrieved);
              var mergeList = notificationList.concat(response.notifications);
              this.setState({
                startIndex: startIndex + maxResults,
                notificationList: mergeList,
                isAllRetrieved: allRetrieved,
                loadingMore: false,
              });
            }
          })
          .catch(error => {
            console.log(error);
          });
      });
    }
  }

  navigatePost = msgObj => {
    const{navigatePost} = this.props;
    if(navigatePost){
      navigatePost(msgObj);
    }
    this.onPressCloseModal();
  };

  renderFooter = () => {
    const {loadingMore} = this.state;
    if (loadingMore) {
      return <LoadMoreIndicator />;
    }
    return null;
  };

  renderPostList({item, index}) {
    return (
      <CommunitiesNotificationItem
        msgObj={item}
        navigatePost={this.navigatePost}
      />
      // <View
      //   style={{
      //     flex: 1,
      //     flexDirection: 'row',
      //     paddingHorizontal: Spacing.M,
      //     paddingVertical: Spacing.S,
      //   }}>
      //     <Avatar size={24} name={item.name} borderLess={true}/>
      //   <Text style={{flex: 1}}>{item.name}</Text>
      //   <Text style={{flex: 1}}>{item.msg}</Text>
      // </View>
    );
  }

  render() {
    return (
      <SafeAreaView style={{flex: 1, backgroundColor: 'white'}}>
        <FlatList
          style={{flex: 1}}
          data={this.state.notificationList}
          extraData={this.state}
          renderItem={this.renderPostList}
          keyExtractor={item => item.name}
          onEndReached={() => this.getNotifications()}
          onEndReachedThreshold={0.1}
          ListFooterComponent={this.renderFooter}
          // ItemSeparatorComponent={() => <Separator />}
        />
      </SafeAreaView>
    );
  }
}

CommunitiesNotification.propTypes = {
  navigatePost: PropTypes.func
}

export default CommunitiesNotification;
