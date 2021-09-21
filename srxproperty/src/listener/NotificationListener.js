import React, { Component } from 'react';
import { View, Platform, Alert } from 'react-native';
import PropTypes from 'prop-types';
// import firebase from "react-native-firebase";
import firebase from '@react-native-firebase/app';
import messaging from '@react-native-firebase/messaging';
import NotificationsIOS, {
  NotificationsAndroid,
  PendingNotifications,
  Notifications,
} from 'react-native-notifications';
import { connect } from 'react-redux';
import { LoginService } from '../services';
import { ObjectUtil, UserUtil } from '../utils';
import { FCMAndroidNotificationListener } from "../listener";

const isIOS = Platform.OS === 'ios';

class NotificationListener extends Component {
  state = {
    deviceToken: null,
  };

  constructor(props) {
    super(props);

    this.onPushRegistered = this.onPushRegistered.bind(this);
    this.onPushRegistrationFailed = this.onPushRegistrationFailed.bind(this);
    this.onNotificationReceivedForeground = this.onNotificationReceivedForeground.bind(
      this,
    );
    this.onNotificationReceivedBackground = this.onNotificationReceivedBackground.bind(
      this,
    );
    this.onNotificationOpened = this.onNotificationOpened.bind(this);

    //Register notifications
    Notifications.registerRemoteNotifications();
    Notifications.events().registerRemoteNotificationsRegistered(event => {
      // TODO: Send the token to my server so it could send back push notifications...
      console.log('Device Token Received', event.deviceToken);
      this.onPushRegistered(event.deviceToken);
    });

    Notifications.events().registerRemoteNotificationsRegistrationFailed(
      event => {
        console.error(event);
      },
    );

    //Received notification foreground
    Notifications.events().registerNotificationReceivedForeground(
      (notification, completion) => {
        console.log('Notification Received - Foreground', notification.payload);
        this.onNotificationReceivedForeground(notification);
        // Calling completion on iOS with `alert: true` will present the native iOS inApp notification.
        completion({ alert: false, sound: false, badge: false });
      },
    );

    Notifications.events().registerNotificationOpened(
      (notification, completion, action) => {
        console.log('Notification opened by device user', notification.payload);
        this.onNotificationOpened(notification);
        completion();
      },
    );

    Notifications.events().registerNotificationReceivedBackground(
      (notification, completion) => {
        console.log('Notification Received - Background', notification.payload);
        this.onNotificationReceivedBackground(notification);
        // Calling completion on iOS with `alert: true` will present the native iOS inApp notification.
        completion({ alert: true, sound: true, badge: false });
      },
    );

    Notifications.getInitialNotification()
      .then(notification => {
        console.log(
          'Initial notification was:',
          notification ? notification.payload : 'N/A',
        );
        if (!ObjectUtil.isEmpty(notification)) {
          this.onNotificationOpened(notification)
        } else {
          console.log("Empty notification")
        }
      })
      .catch(err => console.error('getInitialNotifiation() failed', err));

    if (!isIOS) {
      FCMAndroidNotificationListener.setFCMNotifcationReceivedListener(params => {
        if (!ObjectUtil.isEmpty(params)) {
          this.onNotificationOpened(params)
        } else {
          console.log("Empty notification")
        }
      })
    }
  } //end of constructor

  //TODO: remove it if not needed
  componentDidMount() {
    // if (isIOS) {
    //   NotificationsIOS.addEventListener(
    //     "remoteNotificationsRegistered",
    //     this.onPushRegistered
    //   );
    //   NotificationsIOS.addEventListener(
    //     "remoteNotificationsRegistrationFailed",
    //     this.onPushRegistrationFailed
    //   );
    //   NotificationsIOS.addEventListener(
    //     "notificationReceivedForeground",
    //     this.onNotificationReceivedForeground
    //   );
    //   NotificationsIOS.addEventListener(
    //     "notificationReceivedBackground",
    //     this.onNotificationReceivedBackground
    //   );
    //   NotificationsIOS.addEventListener(
    //     "notificationOpened",
    //     this.onNotificationOpened
    //   );
    //   // NotificationsIOS.requestPermissions();
    //   if (!ObjectUtil.isEmpty(this.props.userPO)) {
    //     UserUtil.checkIOSNotificationStatus();
    //   }
    //   NotificationsIOS.consumeBackgroundQueue();
    // } else {
    //   //Register Token server side
    //   NotificationsAndroid.refreshToken();
    //   NotificationsAndroid.setRegistrationTokenUpdateListener(deviceToken => {
    //     console.log("Push-notifications registered! Android", deviceToken);
    //     this.onPushRegistered(deviceToken);
    //   });
    //   //On Notification Received
    //   NotificationsAndroid.setNotificationReceivedListener(notification => {
    //     this.onNotificationReceivedForeground(notification);
    //     console.log("Notification received on device", notification.getData());
    //   });
    //   PendingNotifications.getInitialNotification()
    //     .then(notification => {
    //       if (!ObjectUtil.isEmpty(notification)) {
    //         this.onNotificationOpened(notification);
    //         console.log(
    //           "Get Initial Notification was",
    //           notification ? notification.getData() : "N/A"
    //         );
    //       }
    //     })
    //     .catch(err => console.error("getInitialNotifiation() failed", err));
    //   //On Notification Opened
    //   NotificationsAndroid.setNotificationOpenedListener(notification => {
    //     this.onNotificationOpened(notification);
    //     console.log(
    //       "Notification opened by device user",
    //       notification.getData()
    //     );
    //   });
    // }
  }

  componentWillUnmount() {
    if (isIOS) {
      // prevent memory leaks!
      NotificationsIOS.removeEventListener(
        'remoteNotificationsRegistered',
        this.onPushRegistered,
      );
      NotificationsIOS.removeEventListener(
        'remoteNotificationsRegistrationFailed',
        this.onPushRegistrationFailed,
      );
      NotificationsIOS.removeEventListener(
        'notificationReceivedForeground',
        this.onNotificationReceivedForeground,
      );
      NotificationsIOS.removeEventListener(
        'notificationReceivedBackground',
        this.onNotificationReceivedBackground,
      );
      NotificationsIOS.removeEventListener(
        'notificationOpened',
        this.onNotificationOpened,
      );
    } else {
      FCMAndroidNotificationListener.clearFCMNotificationReceivedListener(message => {
        console.log("Removed listener" + message)
      })
    }
  }

  componentDidUpdate(prevProps, prevState) {
    if (
      prevState.deviceToken != this.state.deviceToken ||
      prevProps.userPO !== this.props.userPO
    ) {
      if (!ObjectUtil.isEmpty(this.props.userPO)) {
        if (!ObjectUtil.isEmpty(this.state.deviceToken)) {
          LoginService.registerAPNSToken({ token: this.state.deviceToken });
        } else {
          if (isIOS) {
            UserUtil.checkIOSNotificationStatus();
          }
        }
      }
    }
  }

  onPushRegistered(deviceToken) {
    // TODO: Send the token to my server so it could send back push notifications...
    console.log('Device Token Received', deviceToken);

    if (isIOS) {
      UserUtil.checkIOSNotificationStatus();
    }

    this.setState({ deviceToken });
  }

  onPushRegistrationFailed(error) {
    console.error(error);
    this.setState({ deviceToken: null });
  }

  onNotificationReceivedForeground(notification) {
    console.log('Notification Received - Foreground', notification);
  }

  onNotificationReceivedBackground(notification) {
    console.log('Notification Received - Background', notification);
  }

  onNotificationOpened(notification) {
    if (!ObjectUtil.isEmpty(notification)) {
      this.handleNotification(notification);
    }
    console.log('Notification opened by device user', notification);
  }

  handleNotification(notification) {
    //TODO: not sure iOS same with android or not.. please do testing
    let data;
    if (isIOS) {
      data = notification.payload;
    } else {
      if (!ObjectUtil.isEmpty(notification.payload)) {
        data = notification.payload;
      } else {
        data = notification;
      }
    }

    if (!ObjectUtil.isEmpty(data)) {
      if (data.a == 'anc') {
        firebase.analytics().logEvent('In_appnotification', { Inapp: 'Inapp' });
      }

      if (data.type == 'AgentToAgentAlert') {
        this.props.handleNavigation();
      }
    }
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

NotificationListener.propTypes = {
  //this is used to navigate to another screen
  componentId: PropTypes.string.isRequired,
};

export default connect(mapStateToProps, null)(NotificationListener);
