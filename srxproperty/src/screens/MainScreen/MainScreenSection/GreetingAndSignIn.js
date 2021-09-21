import React, {Component} from 'react';
import {View, TouchableOpacity, Image, ImageBackground} from 'react-native';
import CookieManager from '@react-native-community/cookies';
import {Spacing} from '../../../styles';
import {Heading2, SmallBodyText, LottieView} from '../../../components';
import {SRXColor, AppConstant} from '../../../constants';
import {connect} from 'react-redux';
import {LoginStack, CommunitiesStack} from '../../../config';
import {ObjectUtil, StringUtil} from '../../../utils';
import {Navigation} from 'react-native-navigation';
import {MainScreenService, CommunitiesService} from '../../../services';
import {Communities_Alert, Communities_Search} from '../../../assets';
import PropTypes from 'prop-types';
import {SignInRegisterSource} from '../../UserAuthentication/Constants/UserAuthenticationConstants';
import {CommunitiesNotificationListener} from '../../../listener';
import {getCommunities} from '../../../actions';
import PushNotificationIOS from '@react-native-community/push-notification-ios';

class GreetingAndSignIn extends Component {
  static propTypes = {
    directToUserProfile: PropTypes.func,
  };

  state = {
    greetingTime: '',
    animation: null,
    mediaUrl: null,
    showAlert: false,
    unreadCount: 0,
  };

  constructor(props) {
    super(props);
    this.timer = null;

    this.directToUserProfile = this.directToUserProfile.bind(this);
    this.refreshGreetingTime = this.refreshGreetingTime.bind(this);
  }

  componentDidMount() {
    this.getAppGreeting();
    this.checkCurrentTime();
  }

  componentWillUnmount() {
    clearInterval(this.timer);
  }

  componentDidUpdate(prevProps) {
    const {userPO, communities, getCommunities, serverDomain} = this.props;

    if (prevProps.userPO !== userPO) {
      if (!ObjectUtil.isEmpty(userPO)) {
        CookieManager.get(serverDomain.domainURL).then(cookies => {
          const userSessionTokenCookie = cookies['userSessionToken'];
          if (!ObjectUtil.isEmpty(userSessionTokenCookie)) {
            getCommunities();
          }
        });
      }
    }

    if (prevProps.communities !== communities) {
      if (!ObjectUtil.isEmpty(userPO)) {
        if (
          ObjectUtil.isEmpty(communities) &&
          Array.isArray(communities) &&
          communities.length == 0
        ) {
          this.setState({showAlert: false});
        } else {
          this.setState({showAlert: true}, () => {
            this.getUnreadCount();
          });
        }
      } else {
        this.setState({showAlert: false});
      }
    }
  }

  getUnreadCount = () => {
    CommunitiesService.getUnreadCount().then(response => {
      if (!ObjectUtil.isEmpty(response)) {
        const {result} = response;
        console.log('Unread Count: ');
        console.log(response);
        if (result && result === 'success') {
          this.setState({unreadCount: response.count});
          PushNotificationIOS.setApplicationIconBadgeNumber(response.count);
        }
      }
    });
  };

  markAsRead = msgObj => {
    var id = msgObj.id;
    const {notificationList} = this.state;
    console.log('postId');
    console.log(id);
    CommunitiesService.markAsRead({id})
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          console.log('check read');
          if (response.result === 'success') {
            this.getUnreadCount();
            this.onNavigatePost(msgObj);
          }
        }
      })
      .catch(error => {
        console.log(error);
      });
  };

  onNavigatePost(item) {
    console.log('Post item');
    console.log(item);
    if (!ObjectUtil.isEmpty(item)) {
      Navigation.push('HomeScreenId', {
        component: {
          name: 'Communities.CommunitiesPostDetails',
          passProps: {
            postId: item.postId,
          },
        },
      });
    }
  }

  updateAlert = (item, isNotificationOpen) => {
    if (isNotificationOpen) {
      this.markAsRead(item);
    } else {
      this.getUnreadCount();
    }
  };

  //Timer functions
  checkCurrentTime() {
    this.timer = setInterval(() => {
      this.refreshGreetingTime();
    }, 600000);
  }

  playAnimation = () => {
    if (!this.state.animation) {
      this.loadAnimationAsync();
    } else {
      this.animation.play();
    }
  };

  loadAnimationAsync = async url => {
    console.log('Greeting url: ' + url);
    let result = await fetch(url)
      .then(data => {
        return data.json();
      })
      .catch(error => {
        console.error(error);
      });
    console.log('Retrieve json ' + result);
    this.setState({animation: result}, this.playAnimation);
  };

  refreshGreetingTime() {
    var currentHour = new Date().getHours();
    const splitAfternoon = 12; // 24hr time to split the afternoon
    const splitEvening = 18; // 24hr time to split the evening
    const splitMorning = 0;
    if (
      currentHour === splitAfternoon ||
      currentHour === splitEvening ||
      currentHour === splitMorning
    ) {
      this.getAppGreeting();
    }
  }

  getAppGreeting() {
    MainScreenService.getAppGreeting()
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const {result} = response;
          if (!ObjectUtil.isEmpty(result) && result.greeting != '') {
            if (!ObjectUtil.isEmpty(result.mediaUrl) && result.mediaUrl != '') {
              if (result.mediaUrl.includes('json')) {
                this.setState({greeting: result.greeting}, () => {
                  this.loadAnimationAsync(result.mediaUrl);
                });
              } else {
                this.setState({
                  greeting: result.greeting,
                  mediaUrl: result.mediaUrl,
                });
              }
            } else {
              this.setState({greeting: result.greeting});
            }
          } else {
            this.getGreetingTime(new Date().getHours());
          }
        }
      })
      .catch(error => {
        console.log(error);
      });
  }

  getGreetingTime = currentHour => {
    const splitAfternoon = 12; // 24hr time to split the afternoon
    const splitEvening = 18; // 24hr time to split the evening

    if (currentHour >= splitAfternoon && currentHour < splitEvening) {
      // Between 12 PM and 6PM ( 12 noon to 6 pm )
      this.setState({greetingTime: 'Good afternoon!'});
    } else if (currentHour >= splitEvening) {
      // Between 6 PM and 12 AM ( 6pm to 12 midnight )
      this.setState({greetingTime: 'Good evening!'});
    } else {
      // Between 12 AM to 12 PM  (12 midnight to 12 noon)
      this.setState({greetingTime: 'Good morning!'});
    }
  };

  directToUserProfile = () => {
    const {directToUserProfile} = this.props;
    if (directToUserProfile) {
      directToUserProfile();
    }
  };

  directToSignInRegister = () => {
    const passProps = {
      source: SignInRegisterSource.MainScreen,
    };

    LoginStack.showSignInRegisterModal(passProps);
  };

  directToNotification = () => {
    const passProps = {
      navigatePost: this.markAsRead,
    };
    Navigation.showModal({
      stack: {
        id: 'CommunitiesNotification',
        children: [
          {
            component: {
              name: 'CommunitiesNotification',
              passProps,
            },
          },
        ],
      },
    });
  };

  renderGreetingAndSignIn() {
    const {userPO} = this.props;
    const {greetingTime, greeting, mediaUrl} = this.state;

    const greetingTimeStateVariable = !ObjectUtil.isEmpty(userPO)
      ? StringUtil.replace(greetingTime, '!', ', ')
      : greetingTime;

    const greetingNote = greeting ? greeting : greetingTimeStateVariable;
    const welcomeNote = !ObjectUtil.isEmpty(userPO)
      ? greetingNote + userPO.getCommunityPostUserName() + '!'
      : greetingNote;

    return (
      <View style={Styles.greetingAndSignInContainer}>
        <View style={{flex: 1, flexDirection: 'row', flexWrap: 'wrap'}}>
          <Heading2>{welcomeNote}</Heading2>
          {mediaUrl ? (
            <Image
              source={{uri: mediaUrl}}
              style={{width: 20, height: 20, marginLeft: Spacing.XS / 4}}
              resizeMode={'cover'}
            />
          ) : null}
          {this.state.animation && (
            <LottieView
              ref={animation => {
                this.animation = animation;
              }}
              style={{height: 25, maxWidth: 50, backgroundColor: 'transparent'}}
              source={this.state.animation}
            />
          )}
        </View>

        <View
          style={
            {
              // flex: 1,
              // justifyContent: 'flex-end',
              // flexDirection: 'row',
            }
          }>
          {this.renderLoginInfo()}
        </View>
      </View>
    );
  }

  renderLoginInfo() {
    const {userPO, showSearch} = this.props;
    const {showAlert, unreadCount} = this.state;
    if (ObjectUtil.isEmpty(userPO)) {
      return (
        <TouchableOpacity
          style={{flexDirection: 'row', alignItems: 'center'}}
          onPress={() => this.directToSignInRegister()}>
          <SmallBodyText
            style={{marginLeft: Spacing.XS, color: SRXColor.TextLink}}>
            Sign in / Register
          </SmallBodyText>
        </TouchableOpacity>
      );
    } else {
      return (
        <View style={{flexDirection: 'row'}}>
          {showSearch ? (
            <TouchableOpacity>
              <Image
                source={Communities_Search}
                style={{
                  width: 20,
                  height: 20,
                  alignSelf: 'center',
                  marginRight: 10,
                }}
                resizeMode={'cover'}
              />
            </TouchableOpacity>
          ) : null}
          {showAlert ? (
            <TouchableOpacity onPress={() => this.directToNotification()}>
              <ImageBackground
                source={Communities_Alert}
                style={{
                  width: 20,
                  height: 20,
                  alignSelf: 'center',
                }}
                resizeMode={'cover'}>
                {unreadCount > 0 ? (
                  <View
                    style={{
                      position: 'absolute',
                      right: -5,
                      top: -4,
                      backgroundColor: SRXColor.Red,
                      borderRadius: 6,
                      height: 12,
                      width: 12,
                    }}>
                    <SmallBodyText
                      style={{
                        paddingTop: 1,
                        textAlign: 'center',
                        fontSize: 8,
                        color: SRXColor.White,
                      }}>
                      {unreadCount < 99 ? unreadCount : '99'}
                    </SmallBodyText>
                  </View>
                ) : null}
              </ImageBackground>
            </TouchableOpacity>
          ) : null}
          {showAlert ? (
            <CommunitiesNotificationListener updateAlert={this.updateAlert} />
          ) : null}
        </View>
      );
    }
    return <View />;
  }

  render() {
    return <View>{this.renderGreetingAndSignIn()}</View>;
  }
}

const Styles = {
  greetingAndSignInContainer: {
    paddingHorizontal: Spacing.M,
    paddingTop: Spacing.L,
    backgroundColor: SRXColor.White,
    // flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
  },
};

GreetingAndSignIn.propTypes = {
  showSearch: PropTypes.bool,
};

GreetingAndSignIn.defaultProps = {
  showSearch: false,
};

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
    communities: state.communitiesData.list,
    serverDomain: state.serverDomain,
  };
};

export default connect(
  mapStateToProps,
  {getCommunities},
)(GreetingAndSignIn);
