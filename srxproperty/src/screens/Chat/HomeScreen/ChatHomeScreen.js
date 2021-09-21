import React, {Component} from 'react';
import {
  View,
  ImageBackground,
  SafeAreaView,
  ScrollView,
  Image,
  Dimensions,
  TextInput,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  Platform,
  BackHandler,
  ActivityIndicator,
  Alert,
} from 'react-native';
import {Navigation} from 'react-native-navigation';
import appleAuth, {
  AppleAuthError,
  AppleAuthRequestScope,
  AppleAuthRequestOperation,
} from '@invertase/react-native-apple-authentication';
import {CommonUtil, ObjectUtil} from '../../../utils';
import {connect} from 'react-redux';
import {EventRegister} from 'react-native-event-listeners';
import {fblogin, appleSignIn, performGoogleSignin} from '../../../actions';
import {
  Button,
  FeatherIcon,
  Text,
  LoadMoreIndicator,
  Separator,
  HorizontalFlatList,
  BodyText,
  LargeTitleComponent,
  Subtext,
  SmallBodyText,
} from '../../../components';
import {AgentPO} from '../../../dataObject';
import {Spacing, Typography} from '../../../styles';
import {IS_IOS, SRXColor, LoadingState, AlertMessage} from '../../../constants';
import {ChatListItem} from '../ChatListItem';
import {AgentDirectory} from '../../Concierge/AgentDirectory';
import {AgentHomeListItem} from '../AgentHomeListItem';
import {AgentSearchService, ChatService} from '../../../services';
import {ChatNotificationListener} from '../../../listener';
import {
  TabBarIcon_Chat,
  UserAuth_SmallIcon_Apple,
  UserAuth_SmallIcon_FB,
  UserAuth_SmallIcon_Google,
} from '../../../assets';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
import {GoogleSignin, statusCodes} from '@react-native-community/google-signin';

import {
  LoginManager,
  AccessToken,
  GraphRequest,
  GraphRequestManager,
} from 'react-native-fbsdk';
import {LoginStack} from '../../../config';
import {
  SignInRegisterSource,
  GoogleSignInIds,
} from '../../UserAuthentication/Constants/UserAuthenticationConstants';

const isIOS = Platform.OS === 'ios';
const DirectorySource = {
  ChatNotificationListener: 'ChatNotificationListener',
  ChatRoom: 'ChatRoom',
};
const notificationPressed = {true: true, false: false};

class ChatHomeScreen extends LargeTitleComponent {
  // static options(passProps) {}

  constructor(props) {
    super(props);
    Navigation.events().bindComponent(this);
    this.renderContactList = this.renderContactList.bind(this);
    this.renderAgentList = this.renderAgentList.bind(this);
  }

  pendingAction = null;

  navigationButtonPressed({buttonId}) {
    const {userPO} = this.props;
    if (buttonId === 'find_Agent') {
      if (!ObjectUtil.isEmpty(userPO)) {
        Navigation.showModal({
          stack: {
            id: 'AgentDirectory',
            children: [
              {
                component: {
                  name: 'ConciergeStack.AgentDirectory',
                  passProps: {
                    source: AgentDirectory.Sources.ChatHome,
                  },
                  options: {
                    modalPresentationStyle: 'overFullScreen',
                  },
                },
              },
            ],
          },
        });
      }
    }
  }

  state = {
    searchQuery: '',
    loadingState: LoadingState.Normal,
    completeList: [],
    displayList: [],
    agentList: [],
    currentRoom: '',
    loadingMore: false,
    allConversationRetrieve: false,
  };

  componentDidMount() {
    this.setupTopBar(false);
    this.listener = EventRegister.addEventListener('myChat', async data => {
      await this.updateSSM(data);
    });
    this.setupGoogleSignInConfigure();
  }

  componentWillUnmount() {
    EventRegister.removeEventListener(this.listener);
  }

  componentDidAppear() {
    if (!IS_IOS) {
      //for android
      this.backHandler = BackHandler.addEventListener(
        'hardwareBackPress',
        () => {
          Navigation.mergeOptions(this.props.componentId, {
            bottomTabs: {
              currentTabIndex: 0,
            },
          });
          return true;
        },
      );
    }
  }

  componentDidDisappear() {
    //Remove all listeners
    if (!IS_IOS) {
      this.backHandler.remove();
    }
  }

  setupGoogleSignInConfigure() {
    //setup google sign in configure
    GoogleSignin.configure({
      webClientId: GoogleSignInIds.android_client_id,
      iosClientId: GoogleSignInIds.ios_client_id,
      offlineAccess: false,
    });
  }

  updateSSM = data => {
    if (data) {
      this.updateConversationRoomId(data.toString());
    } else {
      this.setState({currentRoom: ''}, () => {
        this.retrieveContactList();
        this.retrieveAgentList();
      });
    }
  };

  retrieveAgentList() {
    ChatService.findFeaturedAgentsV2()
      .then(response => {
        const newAgentList = [];
        const userIdList = [];
        if (!ObjectUtil.isEmpty(response)) {
          if (!ObjectUtil.isEmpty(response.result)) {
            const {agentPOs} = response.result;
            if (!ObjectUtil.isEmpty(agentPOs) && Array.isArray(agentPOs)) {
              agentPOs.map((item, index) => {
                newAgentList.push(
                  new AgentPO({
                    ...item,
                  }),
                );
                userIdList.push(item.userId.toString());
              });
              ChatService.findConversationIds({
                otherUserIds: JSON.stringify(userIdList),
              }).then(secondResponse => {
                if (!ObjectUtil.isEmpty(secondResponse)) {
                  const {conversationIds} = secondResponse;
                  if (!ObjectUtil.isEmpty(conversationIds)) {
                    let conversationUserIdList = Object.keys(conversationIds);
                    let conversationIdList = Object.values(conversationIds);
                    newAgentList.map((item, index) => {
                      let ind = conversationUserIdList.indexOf(
                        item.userId.toString(),
                      );
                      if (ind >= 0) {
                        item.conversationId = conversationIdList[ind];
                      }
                    });
                  }
                }
                this.setState({agentList: newAgentList});
              });
            }
          }
        }
      })
      .catch(error => {
        console.log('ChatHome Screen Search Agents Failed');
        console.log(error);
      });
  }

  setupTopBar(isLogin) {
    if (isLogin) {
      FeatherIcon.getImageSource('edit', 25, 'black').then(find_Agent => {
        Navigation.mergeOptions(this.props.componentId, {
          topBar: {
            rightButtons: [
              {
                id: 'find_Agent',
                icon: find_Agent,
              },
            ],
          },
        });
      });
    } else {
      Navigation.mergeOptions(this.props.componentId, {
        topBar: {
          rightButtons: [],
        },
      });
    }
  }

  directToSignInRegister = type => {
    const passProps = {
      source: SignInRegisterSource.ChatHomeScreen,
      type: type,
    };

    LoginStack.showSignInRegisterModal(passProps);
  };

  renderFBButton() {
    return (
      <Button
        onPress={() => this.onPressFacebookLogin()}
        leftView={
          <Image
            style={{width: 35, height: 35}}
            source={UserAuth_SmallIcon_FB}
          />
        }
      />
    );
  }

  renderAppleLoginButton() {
    if (IS_IOS) {
      return (
        <Button
          buttonStyle={{
            marginRight: Spacing.M,
          }}
          onPress={() => this.onAppleButtonPress()}
          leftView={
            <Image
              style={{width: 35, height: 35}}
              source={UserAuth_SmallIcon_Apple}
            />
          }
        />
      );
    }
  }

  renderGoogleSignInBtn() {
    return (
      <TouchableOpacity
        style={{marginLeft: Spacing.M}}
        onPress={() => this.onGoogleBtnPress()}>
        <Image
          style={{width: 35, height: 35}}
          source={UserAuth_SmallIcon_Google}
        />
      </TouchableOpacity>
    );
  }

  renderLoginForm() {
    return (
      <View style={{alignItems: 'center', flex: 1}}>
        <View style={{marginTop: Spacing.L}}>
          <SmallBodyText>CONTINUE WITH</SmallBodyText>
          <View
            style={{
              marginTop: Spacing.M,
              flexDirection: 'row',
              justifyContent: 'center',
            }}>
            {this.renderAppleLoginButton()}
            {this.renderFBButton()}
            {this.renderGoogleSignInBtn()}
          </View>
        </View>
        <Button
          buttonStyle={[
            {
              width: 250,
              borderColor: SRXColor.Teal,
              borderWidth: 1,
              backgroundColor: SRXColor.White,
              justifyContent: 'center',
              marginBottom: Spacing.S,
              marginTop: 25,
            },
            isIOS
              ? {height: 40, borderRadius: 20}
              : {height: 45, borderRadius: 22.5},
          ]}
          textStyle={[Typography.Body, {marginLeft: Spacing.XS}]}
          onPress={() => this.directToSignInRegister('R')}
          leftView={
            <FeatherIcon
              name="mail"
              size={25}
              color={'black'}
              style={{marginRight: Spacing.XS}}
            />
          }>
          Sign up with Email
        </Button>
        <Button
          onPress={() => this.directToSignInRegister('S')}
          textStyle={[Typography.Body, {color: SRXColor.Teal}]}>
          I have an account, sign me in
        </Button>
      </View>
    );
  }

  onPressFacebookLogin = () => {
    const {fblogin} = this.props;
    if (Platform.OS === 'android') {
      LoginManager.setLoginBehavior('web_only');
    }
    LoginManager.logInWithPermissions(['public_profile', 'email']).then(
      function(result) {
        if (result.isCancelled) {
          console.log(result);
        } else {
          AccessToken.getCurrentAccessToken()
            .then(user => {
              return user;
            })
            .then(user => {
              const responseInfoCallback = (error, result) => {
                if (error) {
                  console.log(result);
                } else {
                  if (!ObjectUtil.isEmpty(result.email)) {
                    fblogin({
                      name: result.name,
                      facebookId: result.id,
                      username: result.email,
                    });
                  }
                }
              };
              const infoRequest = new GraphRequest(
                '/me',
                {
                  accessToken: user.accessToken,
                  parameters: {
                    fields: {
                      string: 'email,name,first_name,last_name',
                    },
                  },
                },
                responseInfoCallback,
              );
              new GraphRequestManager().addRequest(infoRequest).start();
            });
        }
      },
      function(error) {
        console.log(error);
      },
    );
  };

  onAppleButtonPress() {
    const {appleSignIn} = this.props;
    try {
      // make sign in request and return a response object containing authentication data
      const appleAuthRequestResponse = appleAuth
        .performRequest({
          requestedOperation: AppleAuthRequestOperation.LOGIN,
          requestedScopes: [
            AppleAuthRequestScope.EMAIL,
            AppleAuthRequestScope.FULL_NAME,
          ],
        })
        .then(result => {
          // retrieve identityToken from sign in request
          const {identityToken, user, fullName, email} = result;
          // identityToken generated
          if (identityToken) {
            // send data to server for verification and sign in
            var nameArray = [];
            if (fullName) {
              const {familyName, givenName, middleName} = fullName;
              if (givenName) {
                nameArray = [...nameArray, givenName];
              }
              if (middleName) {
                nameArray = [...nameArray, middleName];
              }
              if (familyName) {
                nameArray = [...nameArray, familyName];
              }
            }

            appleSignIn({
              name: nameArray.join(' '),
              identityToken,
              email,
              appleUser: user,
            });
          } else {
            // no token, failed sign in
            console.log('no token, failed sign in');
            Alert.alert(
              AlertMessage.ErrorMessageTitle,
              'Failed to sign in with Apple',
            );
          }
        });
    } catch (error) {
      if (error.code === AppleAuthError.CANCELED) {
        // user cancelled Apple Sign-in
        console.log('user cancelled Apple Sign-in');
      } else {
        // other unknown error
        console.log('other unknown error');
        Alert.alert(
          AlertMessage.ErrorMessageTitle,
          'Sign in with Apple is only available on iOS 13.',
        );
      }
    }
  }

  onGoogleBtnPress = async () => {
    const {performGoogleSignin} = this.props;
    try {
      await GoogleSignin.hasPlayServices();
      const userInfo = await GoogleSignin.signIn();
      if (!ObjectUtil.isEmpty(userInfo) && !ObjectUtil.isEmpty(userInfo.user)) {
        performGoogleSignin({
          socialId: userInfo.user.id,
          socialIdToken: userInfo.idToken,
          name: userInfo.user.name,
          username: userInfo.user.email,
          photoUrl: userInfo.user.photo,
        });
      }
    } catch (error) {
      if (error.code === statusCodes.PLAY_SERVICES_NOT_AVAILABLE) {
        Alert.alert(
          AlertMessage.ErrorMessageTitle,
          'An error occurred unexpectedly. Please try again later',
        );
      }
    }
  };

  retrieveContactList = () => {
    const {userPO} = this.props;
    ChatService.loadSsmConversations({
      userId: userPO.encryptedUserId,
      startIndex: 0,
    }).then(response => {
      if (!ObjectUtil.isEmpty(response.Success)) {
        let resultList = response.Success;
        let unreadConversationsCount = response.unreadConversationsCount;
        let newResultList = [];
        if (!ObjectUtil.isEmpty(resultList) && Array.isArray(resultList)) {
          resultList.map((item, index) => {
            newResultList.push({
              ...item,
              userId: item.otherUser,
              name: item.otherUserName,
              mobile: item.otherUserNumber,
              photo: item.otherUserPhoto,
            });
          });
        }
        this.updateUnReadConverstationBadge(unreadConversationsCount);
        this.setState(
          {
            loadingState: LoadingState.Loaded,
            completeList: newResultList,
            displayList: newResultList,
            allConversationRetrieve: false,
          },
          () => {
            if (!ObjectUtil.isEmpty(this.pendingAction)) {
              //Trigger when notification is pressed
              if (this.pendingAction.type === 'notification_pressed') {
                try {
                  this.viewChatRoom(this.pendingAction.agentInfo);
                } catch (e) {
                  console.log(e);
                }
              }
            }
          },
        );
      } else {
        this.setState({
          completeList: [],
          displayList: [],
          loadingState: LoadingState.Loaded,
        });
      }
      console.log('Load SSM Conversation');
      console.log(response);
    });
  };

  renderFooter = () => {
    const {loadingMore} = this.state;
    if (loadingMore) {
      return <LoadMoreIndicator />;
    }
    return null;
  };

  loadMore = () => {
    const {userPO} = this.props;
    const {completeList, allConversationRetrieve} = this.state;
    let length = completeList.length;

    if (allConversationRetrieve) {
      this.setState({loadingMore: false});
    } else if (length) {
      this.setState({loadingMore: true}, () => {
        ChatService.loadSsmConversations({
          userId: userPO.encryptedUserId,
          startIndex: length - 1,
        }).then(response => {
          if (!ObjectUtil.isEmpty(response.Success)) {
            let resultList = response.Success;
            let newResultList = [];
            if (!ObjectUtil.isEmpty(resultList) && Array.isArray(resultList)) {
              if (resultList.length > 1) {
                resultList.shift();
                resultList.map((item, index) => {
                  newResultList.push({
                    ...item,
                    userId: item.otherUser,
                    name: item.otherUserName,
                    mobile: item.otherUserNumber,
                    photo: item.otherUserPhoto,
                  });
                });
              }
            }
            let mergedList = completeList.concat(newResultList);
            let isRetrievedAll = resultList.length % 19 != 0;
            this.setState({
              completeList: mergedList,
              displayList: mergedList,
              loadingMore: false,
              allConversationRetrieve: isRetrievedAll,
            });
          }
          console.log('Load SSM Conversation');
          console.log(response);
        });
      });
    }
  };

  componentDidUpdate(prevProps, prevState) {
    const {userPO, serverDomain} = this.props;
    if (
      prevProps.userPO !== userPO ||
      prevProps.serverDomain !== serverDomain
    ) {
      if (userPO != null) {
        this.setState({loadingState: LoadingState.Loading}, () => {
          this.retrieveAgentList();
          this.retrieveContactList();
          this.setupTopBar(true);
        });
      } else {
        this.setupTopBar(false);
        this.clearAllData();
        /**
         * the flatlist data is cleared here, but the flatlist may not be updated as the rendered screen has been changed,
         * retrieveContactList will be called and update the flatlist.
         *  */
      }
    }
  }

  clearAllData = () => {
    this.setState({
      displayList: [],
      completeList: [],
      agentList: [],
    });
    this.resetNavigationBarStyle();
    /**
     * this is required as FlatList is not using KeyboardAwareScrollView,
     * the onScroll is not passing accurately from ScrollView to FlatList
     * The user may see largeTitle and the Navigation bar title together
     */
    //
    this.updateUnReadConverstationBadge(0);
  };

  renderAgentList({item, index}) {
    const {userPO} = this.props;
    let imageUri = CommonUtil.handleImageUrl(item.photo);
    return (
      <AgentHomeListItem
        agentInfo={item}
        userPO={userPO}
        viewChatRoom={this.viewChatRoom}
      />
    );
  }

  renderAgents() {
    const {agentList} = this.state;
    const {userPO} = this.props;
    if (agentList.length > 0 && userPO != null) {
      return (
        <View>
          {this.renderLargeTitle('Live Chat')}
          <Text style={{color: SRXColor.Black, margin: Spacing.XS}}>
            Suggested SRX Expert Agents
          </Text>
          <HorizontalFlatList
            nestedScrollEnabled={true}
            data={agentList}
            keyExtractor={item => item.userId}
            renderItem={this.renderAgentList}
          />
          <Separator />
        </View>
      );
    } else {
      return <View>{this.renderLargeTitle('Live Chat')}</View>;
    }
  }

  updateLastMessage = obj => {
    if (ObjectUtil.isEmpty(obj)) {
      this.setState({currentRoom: ''});
    } else {
      this.setState({currentRoom: ''}, () => {
        this.updateContactList(
          obj,
          DirectorySource.ChatRoom,
          notificationPressed.false,
        );
      });
    }
    this.loadUnreadConversationsCount();
  };

  loadUnreadConversationsCount = () => {
    const {userPO} = this.props;
    ChatService.loadUnreadConversationsCount({
      userId: userPO.encryptedUserId,
    }).then(response => {
      if (!ObjectUtil.isEmpty(response)) {
        this.updateUnReadConverstationBadge(response.result);
      }
    });
  };

  updateUnReadConverstationBadge = badgeNumber => {
    let badgeString = '';
    if (badgeNumber > 0) {
      badgeString = badgeNumber.toString();
    }

    Navigation.mergeOptions(this.props.componentId, {
      bottomTab: {
        icon: TabBarIcon_Chat,
        badge: badgeString,
        badgeColor: 'red',
      },
    });
  };

  updateConversationRoomId = id => {
    this.setState({currentRoom: id}, () => {
      this.resetUnreadChatRoomCounter();
    });
  };

  viewChatRoom = agentInfo => {
    const {userPO} = this.props;
    // let backIcon;

    // backIcon = await Ionicons.getImageSource(
    //   "ios-arrow-back",
    //   20,
    //   SRXColor.Teal
    // );
    if (!ObjectUtil.isEmpty(this.pendingAction)) {
      try {
        Navigation.pop(this.props.componentId);
      } catch (e) {}
      this.pendingAction = null;
    }

    Navigation.push(this.props.componentId, {
      component: {
        name: 'ChatStack.chatRoom',
        passProps: {
          agentInfo,
        },
        // options: {
        //   topBar: {
        //     leftButtons: [
        //       {
        //         id: "backButton",
        //         icon: backIcon
        //       }
        //     ]
        //   },
        //   bottomTabs: { visible: false, drawBehind: true, animate: true }
        // }
      },
    });
  };

  renderContactList({item, index}) {
    const {userPO} = this.props;
    return (
      <View>
        <ChatListItem
          agentInfo={item}
          userPO={userPO}
          viewChatRoom={this.viewChatRoom}
        />
      </View>
    );
  }

  resetUnreadChatRoomCounter = () => {
    const {completeList, currentRoom} = this.state;
    let updatedConversationIndex = completeList
      .map((item, index) => {
        return item.conversationId;
      })
      .indexOf(currentRoom);
    if (updatedConversationIndex >= 0) {
      let unreadCount = completeList[updatedConversationIndex].unreadCount;
      if (unreadCount != 0) {
        agentInfo = completeList.splice(updatedConversationIndex, 1);
        let msgObj = agentInfo[0];
        msgObj.unreadCount = 0;
        completeList.unshift(msgObj);
        this.setState({
          completeList: completeList,
          displayList: completeList,
        });
      }
    }
    return;
  };

  updateContactList = (obj, source, isNotificationPressed) => {
    const {completeList, currentRoom} = this.state;

    if (isNotificationPressed == notificationPressed.true) {
      let otherUserId = obj.userId;
      let conversationId = obj.conversationId;
      const {completeList} = this.state;
      let res = completeList.find(obj => {
        return (obj.conversationId = conversationId);
      });
      if (!ObjectUtil.isEmpty(res)) {
        this.pendingAction = {
          type: 'notification_pressed',
          agentInfo: res,
        };
      }
    }
    this.retrieveContactList();
    this.retrieveAgentList();
    if (currentRoom == obj.conversationId) {
      EventRegister.emit('myChatRoomNotification', obj);
    }
    // }
    this.loadUnreadConversationsCount();
    console.log(obj);
  };

  renderChatRecord() {
    const {displayList} = this.state;
    if (displayList.length == 0) {
      return (
        <View style={{flex: 1}}>
          {this.renderAgents()}
          <View
            style={{
              justifyContent: 'center',
              alignContent: 'center',
              flex: 1,
            }}>
            <Text style={{textAlign: 'center'}}>No previous conversations</Text>
          </View>
        </View>
      );
    } else {
      return (
        <FlatList
          onScroll={this.onScroll}
          nestedScrollEnabled={true}
          style={{flex: 1}}
          data={displayList}
          extraData={this.state}
          renderItem={this.renderContactList}
          ListHeaderComponent={this.renderAgents()}
          ListFooterComponent={this.renderFooter}
          onEndReached={() => this.loadMore()}
          onEndReachedThreshold={0.1}
          keyExtractor={item => item.conversationId}
          ItemSeparatorComponent={() => <Separator />}
        />
      );
    }
  }
  render() {
    const {userPO} = this.props;
    const {loadingState, displayList} = this.state;
    if (ObjectUtil.isEmpty(userPO)) {
      return (
        <SafeAreaView style={{flex: 1, backgroundColor: 'white'}}>
          <KeyboardAwareScrollView onScroll={this.onScroll} style={{flex: 1}}>
            {this.renderLargeTitle('Live Chat')}
            <View style={{flex: 1}}>{this.renderLoginForm()}</View>
          </KeyboardAwareScrollView>
        </SafeAreaView>
      );
    } else if (loadingState === LoadingState.Loading) {
      return (
        <SafeAreaView style={{flex: 1, backgroundColor: 'white'}}>
          <KeyboardAwareScrollView onScroll={this.onScroll}>
            {this.renderLargeTitle('Live Chat')}
            <ActivityIndicator size="large" />
          </KeyboardAwareScrollView>
        </SafeAreaView>
      );
    } else if (loadingState === LoadingState.Loaded) {
      return (
        <SafeAreaView style={{flex: 1, backgroundColor: 'white'}}>
          <ChatNotificationListener
            componentId={this.props.componentId}
            updateContactList={this.updateContactList}
          />
          <View style={{flex: 1}}>{this.renderChatRecord()}</View>
        </SafeAreaView>
      );
    } else {
      return <View />;
    }
  }
}

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    justifyContent: 'center',
    flex: 1,
  },
});
ChatHomeScreen.Sources = DirectorySource;
const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
    serverDomain: state.serverDomain,
  };
};

export default connect(
  mapStateToProps,
  {fblogin, appleSignIn, performGoogleSignin},
)(ChatHomeScreen);
