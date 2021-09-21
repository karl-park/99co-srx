import React, {Component} from 'react';
import {View, Platform} from 'react-native';
import PropTypes from 'prop-types';
import {Notifications} from 'react-native-notifications';
import {connect} from 'react-redux';
import {ObjectUtil} from '../utils';
import {FCMAndroidNotificationListener} from '../listener';

const isIOS = Platform.OS === 'ios';

class CommunitiesNotificationListener extends Component {
  constructor(props) {
    super(props);

    this.onNotificationReceivedForeground = this.onNotificationReceivedForeground.bind(
      this,
    );
    this.onNotificationReceivedBackground = this.onNotificationReceivedBackground.bind(
      this,
    );
    this.onNotificationOpened = this.onNotificationOpened.bind(this);
  }

  componentDidMount() {
    //done
    Notifications.events().registerNotificationReceivedForeground(
      (notification, completion) => {
        console.log('Notification Received - Foreground', notification.payload);
        this.onNotificationReceivedForeground(notification);
        // Calling completion on iOS with `alert: true` will present the native iOS inApp notification.
        completion({alert: false, sound: false, badge: false});
      },
    );

    //done
    Notifications.events().registerNotificationOpened(
      (notification, completion, action) => {
        console.log('Notification opened by device user', notification.payload);
        this.onNotificationOpened(notification);
        completion();
      },
    );

    //done
    Notifications.events().registerNotificationReceivedBackground(
      (notification, completion) => {
        console.log('Notification Received - Background', notification.payload);
        this.onNotificationReceivedBackground(notification);
        // Calling completion on iOS with `alert: true` will present the native iOS inApp notification.
        completion({alert: true, sound: true, badge: false});
      },
    );

    Notifications.getInitialNotification()
      .then(notification => {
        console.log(
          'Initial notification was:',
          notification ? notification.payload : 'N/A',
        );
        if (!ObjectUtil.isEmpty(notification)) {
          this.onNotificationOpened(notification);
        } else {
          console.log('Empty notification');
        }
      })
      .catch(err => console.error('getInitialNotifiation() failed', err));

    if (!isIOS) {
      FCMAndroidNotificationListener.setFCMNotifcationReceivedListener(
        notification => {
          if (!ObjectUtil.isEmpty(notification)) {
            this.onNotificationOpened(notification);
          } else {
            console.log('Empty notification');
          }
        },
      );
    }
  }

  componentWillUnmount() {
    if (!isIOS) {
      FCMAndroidNotificationListener.clearFCMNotificationReceivedListener(
        message => {
          console.log('Removed listener' + message);
        },
      );
    }
  }

  onNotificationReceivedForeground(notification) {
    console.log('Communities Notification Received - Foreground', notification);
    this.handleNotification(notification, false);
  }

  onNotificationOpened(notification) {
    console.log('Communities Notification opened by device user', notification);
    //onNotificationOpened shud be handled by NotificationListener
    this.handleNotification(notification, true);
  }

  onNotificationReceivedBackground(notification) {
    console.log('Notification Received - Background', notification);
  }

  handleNotification(notification, isNotificationPressed) {
    let data;
    // TODO: not sure iOS also same with andorid using payload or not
    // TODO: test on iOS
    if (isIOS) {
      data = notification.payload;
    } else {
      if (!ObjectUtil.isEmpty(notification.payload)) {
        data = notification.payload;
      } else {
        data = notification;
      }
    }

    if (data.type == 'community') {
      const {updateAlert} = this.props;
      let object = {
        id: parseInt(data.id),
        postId: parseInt(data.postId),
      };
      console.log("check payload from community");
      console.log(object);
      if (updateAlert) {
        updateAlert(object,isNotificationPressed);
      }
    }

    // if (data.type == 'AgentToAgentAlert') {
    //   let object = {
    //     conversationId: data.conversationId,
    //     messageId: data.messageId,
    //     userId: data.otherUser,
    //   };

    //   this.props.updateContactList(
    //     object,
    //     ChatHomeScreen.Sources.ChatNotificationListener,
    //     isNotificationPressed,
    //   );
    // }
  }

  render() {
    //a dummy view
    return <View />;
  }
}

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
  };
};

CommunitiesNotificationListener.propTypes = {
  updateAlert: PropTypes.func.isRequired,
};

export default connect(
  mapStateToProps,
  null,
)(CommunitiesNotificationListener);
