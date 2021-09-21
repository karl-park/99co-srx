import React, {Component} from 'react';
import {
  StyleSheet,
  View,
  ImageBackground,
  SafeAreaView,
  ScrollView,
  Image,
  TextInput,
  Keyboard,
  TouchableHighlight,
  Platform,
  ActivityIndicator,
  TouchableOpacity,
  Alert,
  Dimensions,
} from 'react-native';
import {connect} from 'react-redux';
import {ChatMessagePO, ListingPO} from '../../../dataObject';
import {Navigation} from 'react-native-navigation';
import PropTypes from 'prop-types';
import {EventRegister} from 'react-native-event-listeners';
import {TopBarStyle} from '../../../styles';
import {ChatDatabaseUtil, CommonUtil, ObjectUtil} from '../../../utils';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
import {
  Button,
  FeatherIcon,
  Text,
  ImagePicker,
  LoadMoreIndicator,
  GiftedChat,
  MessageText,
  Composer,
  Send,
  Actions,
  Bubble,
  InputToolbar,
  Time,
  BodyText,
} from '../../../components';
import {ChatService, SearchPropertyService} from '../../../services';
import {Spacing, Typography} from '../../../styles';
import {
  SRXColor,
  LoadingState,
  AlertMessage,
  IS_IOS,
  IS_IPHONE_X,
} from '../../../constants';
import {ChatListingListItem, ChatTransactedListItem} from '../ChatListItem';
import {FullScreenImagePreview} from '../../../components/ImagePreview/FullScreenImagePreview';

// const Realm = require("realm");
var {width} = Dimensions.get('window');
const alertErrorMsg = 'An error occurred unexpectedly. Please try again later.';
const defaultInputToolbarHeight = 44;

const DirectorySource = {
  EnquirySheet: 'EnquirySheet',
};

class ChatRoom extends Component {
  /** If need to add calling method e.g. this.onPressAgent, the method cannot be added here,
   * you can add in constructor or componendDidMount, please refer to this.updateTopBar in constructor for example
   *  */
  static options(passProps) {
    const {agentInfo} = passProps;
    return {
      topBar: {
        title: {
          component: {
            id: 'chatRoomHeader',
            name: 'ChatRoom.TopBar',
            passProps: {
              agentInfo: agentInfo,
            },
          },
        },
        visible: true,
        animate: true,
        backButton: {
          visible: false,
        },
      },
      statusBar: {
        hideWithTopBar: false,
      },
      bottomTabs: {
        visible: false,
        animate: true,
        drawBehind: true,
      },
    };
    // return {
    //   topBar: {
    //     title: {
    //       component: {
    //         id: "chatRoomHeader",
    //         name: "ChatRoom.TopBar",
    //         passProps: {
    //           agentInfo: agentInfo
    //         }
    //       }
    //     },
    //     backButton: {
    //       visible: false
    //     }
    //   }
    // };
  }

  constructor(props) {
    super(props);
    Navigation.events().bindComponent(this);
    this.onSend = this.onSend.bind(this);
    this.updateTopBar();

    this.state = {
      messages: [],
      msgText: '',
      screenHeight: 0,
      conversationId: '',
      msgId: '',
      loadingState: LoadingState.Normal,
      listingId: props.listingId,
      listingPO: props.listingPO,
      sendPhotoState: LoadingState.Normal,
      minInputToolbarHeight: defaultInputToolbarHeight,
    };
  }

  componentDidMount() {
    const {msgTemplate} = this.props;
    let msg = '';
    if (!ObjectUtil.isEmpty(msgTemplate)) {
      msg = msgTemplate;
    }
    this.setState({loadingState: LoadingState.Loading, msgText: msg}, () => {
      this.getListing();
      this.loadSsmMessages();
    });
    this.listener = EventRegister.addEventListener(
      'myChatRoomNotification',
      async data => {
        //catch new conversationId if the chatRoom was navigated from other screen
        await this.updateSSM(data);
      },
    );
  }

  getListing() {
    const {listingId} = this.state;
    if (listingId) {
      SearchPropertyService.getListing({listingId, listingType: 'A'}).then(
        response => {
          console.log('check get listing');
          console.log(response);

          if (!ObjectUtil.isEmpty(response)) {
            if (!ObjectUtil.isEmpty(response.fullListing)) {
              if (!ObjectUtil.isEmpty(response.fullListing.listingDetailPO)) {
                let listingPO = new ListingPO(
                  response.fullListing.listingDetailPO.listingPO,
                );
                this.setState({listingPO});
              }
            }
          }
        },
      );
    }
  }

  componentWillUnmount() {
    EventRegister.removeEventListener(this.listener);
  }

  updateTopBar() {
    const {agentInfo, userPO} = this.props;
    Navigation.mergeOptions(this.props.componentId, {
      topBar: {
        title: {
          component: {
            id: 'chatRoomHeader',
            name: 'ChatRoom.TopBar',
            passProps: {
              agentInfo: agentInfo,
              userPO: userPO,
              onPressAgent: this.onPressAgent,
              onBackPressed: this.onPressBackBtn,
            },
          },
        },
      },
    });
  }

  onPressAgent = () => {
    const {agentInfo} = this.props;
    Navigation.push(this.props.componentId, {
      component: {
        name: 'ConciergeStack.AgentCV',
        passProps: {
          agentUserId: agentInfo.userId,
        },
      },
    });
  };

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

  loadSsmMessages() {
    const {userPO, agentInfo, source} = this.props;
    const {conversationId} = agentInfo;
    // for clearing database
    // ChatDatabaseUtil.deleteAll();
    // console.log("Clear data");
    if (conversationId != null) {
      this.retrieveSSM();
      // if (source) {
      //to notify chatHomeScreen of the conversationId if navigate from screen other than ChatHomeScreen
      EventRegister.emit('myChat', conversationId);
      // }
    } else {
      const userIdList = [agentInfo.userId.toString()];
      ChatService.findConversationIds({
        otherUserIds: JSON.stringify(userIdList),
      }).then(response => {
        console.log(response);
        if (!ObjectUtil.isEmpty(response)) {
          const {conversationIds} = response;
          if (!ObjectUtil.isEmpty(conversationIds)) {
            let conversationIdList = Object.values(conversationIds);
            let conversationId = conversationIdList[0];
            agentInfo.conversationId = conversationId;
            EventRegister.emit('myChat', conversationId);
            this.retrieveSSM();
            // this.setState({ conversationId: conversationIdList[0] });
          } else {
            this.setState({loadingState: LoadingState.Loaded});
          }
        } else {
          Alert.alert(AlertMessage.ErrorMessageTitle, alertErrorMsg);

          this.setState({
            loadingState: LoadingState.Loaded, //Error
          });
        }
      });
    }
  }

  retrieveSSM = () => {
    const {userPO, agentInfo} = this.props;
    const {conversationId} = agentInfo;
    let chatMsgList = ChatDatabaseUtil.findAll(conversationId);
    if (!ObjectUtil.isEmpty(chatMsgList)) {
      let storedArray = [];
      let latestMsgId = '';
      //checking whether the database has been corrupted by user
      try {
        let msg = JSON.parse(chatMsgList[0].messages);
        latestMsgId = msg.messageId;
      } catch (error) {
        ChatDatabaseUtil.deleteCorruptedData(conversationId);
      }

      ChatService.loadSsmMessages({
        userId: userPO.encryptedUserId,
        conversationId: conversationId,
        messageId: latestMsgId,
      }).then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          if (!ObjectUtil.isEmpty(response.messages)) {
            let arrayList = response.messages.results;
            let resultArray = [];
            if (arrayList.length > 1) {
              arrayList.map(item => {
                ChatDatabaseUtil.save(conversationId, item);
              });
            }
            let updatedChatMsgList = ChatDatabaseUtil.findAll(conversationId);

            try {
              updatedChatMsgList.map(item => {
                let msg = JSON.parse(item.messages);
                resultArray.push(new ChatMessagePO(msg));
              });
              latestMsgId = resultArray[0].messageId;
            } catch (error) {
              ChatDatabaseUtil.deleteCorruptedData(conversationId);
            }
            this.resetUnreadCount(conversationId);
            this.setState({
              messages: resultArray,
              conversationId: conversationId,
              msgId: latestMsgId,
              loadingState: LoadingState.Loaded,
            });
          } else {
            const error = CommonUtil.getErrorMessageFromSRXResponse(response);
            if (!ObjectUtil.isEmpty(error)) {
              Alert.alert(AlertMessage.ErrorMessageTitle, error);
            } else {
              Alert.alert(AlertMessage.ErrorMessageTitle, alertErrorMsg);
            }
            this.setState({
              loadingState: LoadingState.Loaded,
            });
          }
        } else {
          Alert.alert(AlertMessage.ErrorMessageTitle, alertErrorMsg);

          this.setState({
            loadingState: LoadingState.Loaded, //Error
          });
        }
      });
    } else {
      ChatService.loadSsmMessages({
        userId: userPO.encryptedUserId,
        conversationId: conversationId,
      }).then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          if (!ObjectUtil.isEmpty(response.messages)) {
            let msgList = response.messages.results;
            let resultArray = [];
            let latestMsgId = '';
            if (msgList.length > 0) {
              msgList.map(item => {
                ChatDatabaseUtil.save(conversationId, item);
                // let mergedItem = new ChatMessagePO(item);
                // resultArray.push(mergedItem);
              });
              // latestMsgId = resultArray[0].messageId;
            }
            let updatedChatMsgList = ChatDatabaseUtil.findAll(conversationId);
            try {
              updatedChatMsgList.map(item => {
                let msg = JSON.parse(item.messages);
                resultArray.push(new ChatMessagePO(msg));
              });
              latestMsgId = resultArray[0].messageId;
            } catch (error) {
              ChatDatabaseUtil.deleteCorruptedData(conversationId);
            }
            this.resetUnreadCount(conversationId);
            this.setState({
              messages: resultArray,
              conversationId: conversationId,
              msgId: latestMsgId,
              loadingState: LoadingState.Loaded,
            });
          } else {
            const error = CommonUtil.getErrorMessageFromSRXResponse(response);
            if (!ObjectUtil.isEmpty(error)) {
              Alert.alert(AlertMessage.ErrorMessageTitle, error);
            } else {
              Alert.alert(AlertMessage.ErrorMessageTitle, alertErrorMsg);
            }
          }
          this.setState({
            loadingState: LoadingState.Loaded, //Error
          });
        } else {
          Alert.alert(AlertMessage.ErrorMessageTitle, alertErrorMsg);

          this.setState({
            loadingState: LoadingState.Loaded, //Error
          });
        }
      });
    }
  };
  resetUnreadCount = conversationId => {
    const {userPO} = this.props;
    ChatService.resetUnreadCount({
      userId: userPO.encryptedUserId,
      conversationId,
    }).then(response => {
      if (!ObjectUtil.isEmpty(response)) {
        if (!ObjectUtil.isEmpty(response.result))
          if (response.result == 'success') {
          }
      }
    });
  };

  updateSSM = data => {
    const {userPO, agentInfo} = this.props;
    const {conversationId} = agentInfo;
    const {messages, msgId} = this.state;
    let incomingMsgId = data.messageId;
    if (messages.length > 0) {
      let msgIdIndex = messages
        .map((item, index) => {
          return item.messageId;
        })
        .indexOf(incomingMsgId);
      if (msgIdIndex < 0) {
        if (conversationId != null) {
          ChatService.loadSsmMessages({
            userId: userPO.encryptedUserId,
            conversationId: conversationId,
            messageId: msgId,
          }).then(response => {
            let arrayList = response.messages.results;

            let resultArray = [];
            if (arrayList.length > 1) {
              // remove the duplicate message as the api will include the message with the given messageId
              arrayList.pop();
              arrayList.map(item => {
                ChatDatabaseUtil.save(conversationId, item);
                if (item.isFromOtherUser == 'true') {
                  let mergedItem = new ChatMessagePO(item);
                  resultArray.push(mergedItem);
                }
              });
            }
            let mergedArray = resultArray.concat(messages);
            // use arrayList to retrieve messageId instead of mergedArray as the mergedArray sequence may be incorrect due to time overlapping for sending and receiving msg
            let latestMsgId = arrayList[0].messageId;
            /*
            msgId is the last messageid from the api, if there is another api call to loadSSM, the messageId will be used to retrieve unretrieved new message
            ConversationId may not be retrieved from the props as the conversation room may not yet been generated, storing in the state for the usage of sending SSM 
            and updating parent screen for message update*/
            this.resetUnreadCount(conversationId);
            this.setState({
              messages: mergedArray,
              conversationId: conversationId,
              msgId: latestMsgId,
            });
          });
        }
      } //end
    }
  };

  sendSSM(msg, type) {
    const {agentInfo, userPO, updateConversationRoomId} = this.props;
    const {conversationId} = this.state;

    //check if there exists a conversationId, and create one if there isn't
    if (conversationId == '') {
      var userIdString = agentInfo.userId.toString();
      let userIds = JSON.stringify([userIdString]);
      ChatService.createSsmConversation({
        userIds: userIds,
        encryptedUserId: userPO.encryptedUserId,
        isBlast: false,
      }).then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          if (response.Success) {
            let converseId = response.Success;
            if (updateConversationRoomId) {
              updateConversationRoomId(converseId);
            } else {
              EventRegister.emit('myChat', converseId);
            }
            this.setState({conversationId: converseId}, () => {
              this.sendSSMService(msg, type);
            });
          } else {
            const error = CommonUtil.getErrorMessageFromSRXResponse(response);
            if (!ObjectUtil.isEmpty(error)) {
              Alert.alert(AlertMessage.ErrorMessageTitle, error);
            } else {
              Alert.alert(AlertMessage.ErrorMessageTitle, alertErrorMsg);
            }
            this.setState({sendPhotoState: LoadingState.Loaded});
          }
        } else {
          Alert.alert(AlertMessage.ErrorMessageTitle, alertErrorMsg);
          this.setState({sendPhotoState: LoadingState.Loaded});
        }
      });
    } else {
      this.sendSSMService(msg, type);
    }
  }

  sendSSMService(msg, type) {
    const {userPO} = this.props;
    const {conversationId, messages, listingPO} = this.state;
    let dataToSend;
    if (type == 1) {
      dataToSend = {message: msg};
    } else {
      dataToSend = {filename: msg, message: msg.caption};
    }
    //adding type = 1 prevents user to send Image with listings together
    if (listingPO && type == 1) {
      ChatService.sendSsm({
        conversationId: conversationId,
        userId: userPO.encryptedUserId,
        isBlast: false,
        messageType: 2,
        listingId: listingPO.getListingId(),
        ...dataToSend,
      }).then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          if (!ObjectUtil.isEmpty(response.Success)) {
            let res = response.Success.results[0];
            messages[0].messageId = res.messageId;
            messages[0].isFromOtherUser = res.isFromOtherUser;
            messages[0].conversationId = conversationId;
            console.log(messages);
            console.log('send ssm success');
            this.removeAttachedListing();
          } else {
            const error = CommonUtil.getErrorMessageFromSRXResponse(response);
            if (!ObjectUtil.isEmpty(error)) {
              Alert.alert(AlertMessage.ErrorMessageTitle, error);
            } else {
              Alert.alert(
                AlertMessage.ErrorMessageTitle,
                'An error occurred unexpectedly. Please try again later.',
              );
            }
          }
        } else {
          Alert.alert(
            AlertMessage.ErrorMessageTitle,
            'An error occurred unexpectedly. Please try again later.',
          );
        }
        console.log(response);
      });
    } else {
      ChatService.sendSsm({
        conversationId: conversationId,
        userId: userPO.encryptedUserId,
        isBlast: false,
        messageType: type,
        ...dataToSend,
      }).then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          if (!ObjectUtil.isEmpty(response.Success)) {
            if (type === 3) {
              this.setState({sendPhotoState: LoadingState.Loaded}, () =>
                this.loadSsmMessages(),
              );
            } else {
              let res = response.Success.results[0];
              messages[0].messageId = res.messageId;
              messages[0].isFromOtherUser = res.isFromOtherUser;
              messages[0].conversationId = conversationId;
              console.log(messages);
            }
          } else {
            const error = CommonUtil.getErrorMessageFromSRXResponse(response);
            if (!ObjectUtil.isEmpty(error)) {
              Alert.alert(AlertMessage.ErrorMessageTitle, error);
            } else {
              Alert.alert(
                AlertMessage.ErrorMessageTitle,
                'An error occurred unexpectedly. Please try again later.',
              );
            }
            this.setState({sendPhotoState: LoadingState.Loaded});
          }
        } else {
          Alert.alert(
            AlertMessage.ErrorMessageTitle,
            'An error occurred unexpectedly. Please try again later.',
          );
          this.setState({sendPhotoState: LoadingState.Loaded});
        }
        console.log(response);
      });
    }
  }

  onSend(messages = []) {
    console.log('Check message');
    console.log(messages);
    const {listingPO, listingId} = this.state;
    const {userPO} = this.props;
    if (listingPO) {
      let newMsg = [{...messages[0], listingId, listing: listingPO}];
      // let msg = {
      //   message: 'test listing',
      //   dateSent: new Date(),
      //   listingId,
      //   listing: listingPO,
      //   isFromOtherUser: 'false',
      //   sourceUserEncryptedId: userPO.encryptedUserId,
      //   sourceUserName: userPO.name,
      // };
      // let message = new ChatMessagePO(msg);
      // let newMsg = GiftedChat.append(this.state.messages, message);
      console.log('new msg');
      console.log(newMsg);

      this.setState(
        previousState => ({
          messages: GiftedChat.append(previousState.messages, newMsg),
        }),
        () => {
          console.log(messages);
          this.sendSSM(messages[0].text, 1);
        },
      );
    } else {
      this.setState(
        previousState => ({
          messages: GiftedChat.append(previousState.messages, messages),
        }),
        () => {
          console.log(messages);
          this.sendSSM(messages[0].text, 1);
          // Keyboard.dismiss();
        },
      );
    }
  }

  //back button
  onPressBackBtn = () => {
    //Go Back to home screen page
    // const { updateLastMessage } = this.props;
    // const { messages, msgId } = this.state;
    // if (messages.length > 0) {
    //   if (updateLastMessage) {
    //     if (messages[0]._id == msgId) {
    //       updateLastMessage(null);
    //     } else {
    //       messages[0].message = messages[0].text;
    //       updateLastMessage(messages[0]);
    //     }
    //   } else {
    EventRegister.emit('myChat', '');
    // }
    // }
    if (this.state.sendPhotoState === LoadingState.Loading) {
      Alert.alert('', 'Uploading photo, please wait.');
    } else {
      Navigation.pop(this.props.componentId);
    }
  };

  // navigationButtonPressed({ buttonId }) {
  //   const { updateLastMessage } = this.props;
  //   if (buttonId === "backButton") {
  //     //Go Back to home screen page
  //     const { messages, msgId } = this.state;
  //     if (messages.length > 0) {
  //       if (updateLastMessage) {
  //         if (messages[0]._id == msgId) {
  //           updateLastMessage(null);
  //         } else {
  //           messages[0].message = messages[0].text;
  //           updateLastMessage(messages[0]);
  //         }
  //       }
  //     }

  //     Navigation.pop(this.props.componentId);
  //   }
  // }

  sendImage(uri) {
    const {userPO} = this.props;
    let msg = {
      message: uri.caption,
      test: '',
      dateSent: new Date(),
      thumbUrl: uri.uri,
      isFromOtherUser: 'false',
      sourceUserEncryptedId: userPO.encryptedUserId,
      sourceUserName: userPO.name,
    };
    let message = new ChatMessagePO(msg);
    this.setState(
      {
        sendPhotoState: LoadingState.Loading,
      },
      () => {
        console.log(message);
        this.sendSSM(uri, 3);
      },
    );
  }

  chooseImage() {
    const options = {
      chooseFromLibraryButtonTitle: 'Upload Photo',
      title: 'Upload Image/Photo',
      storageOptions: {
        skipBackup: true,
        path: 'images',
      },
      maxHeight: 1080,
      maxWidth: 1080,
    };

    ImagePicker.showImagePicker(options, response => {
      console.log('Response = ', response);

      if (response.didCancel) {
        console.log('User cancelled image picker');
      } else if (response.error) {
        if (response.error == 'Camera permissions not granted') {
          if (IS_IOS) {
            const title = 'Permission Denied';
            const message =
              "This function required to access your camera.\nTo allow, please visit Settings App, and allow 'Camera' for SRX Property.";
            CommonUtil.directToiOSSettings(title, message);
          }
        }
        console.log('ImagePicker Error: ', response.error);
      } else if (response.customButton) {
        console.log('User tapped custom button: ', response.customButton);
      } else {
        const source = {uri: response.uri};
        var fileName = '';
        if (!ObjectUtil.isEmpty(response.fileName)) {
          fileName = response.fileName;
        }
        let photoItem = {
          uri: response.uri,
          type: 'image/jpeg',
          name: fileName, //a random name for now, it is needed else fetch wont grab the data from the uri
        };
        //  this.sendImage(photoItem);
        this.showPhotoWithCaption(photoItem);
        console.log(source);
      }
    });
  }

  showPhotoWithCaption = photoItem => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'ChatRoom.PhotoWithCaption',
        passProps: {
          photoItem: photoItem,
          sendImage: this.sendImage.bind(this),
        },
      },
    });
  };

  renderCustomAction = giftChatProps => {
    const options = {
      'Send Attacment': giftChatProps => {
        alert('option 1');
      },
      'Send Photo': giftChatProps => {
        this.chooseImage();
      },
      Cancel: () => {},
    };
    return (
      <TouchableOpacity onPress={() => this.chooseImage()}>
        <View style={{marginBottom: 10}}>
          <FeatherIcon
            name={'camera'}
            size={25}
            color={SRXColor.Black}
            style={{
              alignSelf: 'center',
              marginLeft: Spacing.XS,
              marginBottom: Spacing.XS,
            }}
          />
        </View>
      </TouchableOpacity>
    );
  };

  renderCustomBubble = giftChatProps => {
    return (
      <Bubble
        {...giftChatProps}
        touchableProps={{
          onPress: () => {
            Keyboard.dismiss();
          },
        }}
        wrapperStyle={{
          left: {
            backgroundColor: '#F8F8F8',
            marginLeft: Spacing.XS,
            marginBottom: Spacing.XS,
          },
          right: {
            backgroundColor: SRXColor.PurpleShade,
            marginRight: Spacing.XS,
            marginBottom: Spacing.XS,
          },
        }}
        textStyle={{
          right: {
            color: SRXColor.Black,
            fontSize: 16,
          },
          left: {
            color: SRXColor.Black,
            fontSize: 16,
          },
        }}
      />
    );
  };

  renderCustomMessageImage = giftChatProps => {
    const mediaItem = {
      thumbnailUrl: giftChatProps.currentMessage.image,
      url: giftChatProps.currentMessage.image,
    };
    let mediaItems = [mediaItem];
    return (
      <View style={{padding: Spacing.XS / 2}}>
        <TouchableHighlight
          underlayColor={SRXColor.Transparent}
          onPress={() => {
            FullScreenImagePreview.show({
              mediaItems: mediaItems,
              shouldShowThumbnails: false,
            });
          }}>
          <Image
            style={{
              width: 150,
              height: 100,
              borderRadius: Spacing.S,
            }}
            source={{uri: giftChatProps.currentMessage.image}}
          />
        </TouchableHighlight>
      </View>
    );
  };

  onContentSizeChange = (contentSize, giftChatProps) => {
    // Save the content height in state
    if (IS_IOS) {
      let restrictHeight = 100;
      let compareHeight = contentSize.height < restrictHeight;
      if (compareHeight) {
        if (contentSize.height < 30) {
          giftChatProps.onInputSizeChanged(contentSize);
        } else {
          let adjustedSize = contentSize;
          let height = (Math.floor(adjustedSize.height / 20) + 1) * 20;

          // if (height < restrictHeight) {
          adjustedSize.height = height;

          giftChatProps.onInputSizeChanged(adjustedSize);
          // }
        }
      }
    }
    this.setState({screenHeight: contentSize.height});
  };

  removeAttachedListing = () => {
    this.setState({listingPO: null, listingId: null});
  };

  renderAttachedListingInComposer() {
    const {listingPO} = this.state;
    if (!ObjectUtil.isEmpty(listingPO)) {
      return (
        <View style={[{flexDirection: 'row'}, Styles.attachedComponentPreview]}>
          {this.renderAttachedListing()}
          <TouchableOpacity
            style={{paddingVertical: Spacing.XS / 2}}
            onPress={() => this.removeAttachedListing()}>
            <FeatherIcon name="x" size={25} color={SRXColor.Teal} />
          </TouchableOpacity>
        </View>
      );
    }
    return null;
  }

  renderAttachedListing() {
    const {listingPO} = this.state;
    if (listingPO.isTransacted()) {
      return (
        <ChatTransactedListItem
          containerStyle={{flex: 1}}
          listingPO={listingPO}
        />
      );
    } else {
      return (
        <ChatListingListItem containerStyle={{flex: 1}} listingPO={listingPO} />
      );
    }
  }

  renderCustomComposer = giftChatProps => {
    const {msgTemplate} = this.props;
    if (IS_IOS) {
      return (
        <View
          style={{padding: 1, flex: 1}}
          onLayout={event => {
            const {layout} = event.nativeEvent;
            if (layout && layout.height) {
              this.setState(
                {
                  minInputToolbarHeight: layout.height,
                },
                () => {
                  if (this.chat) {
                    this.chat.resetInputToolbar();
                  }
                },
              );
            }
          }}>
          {this.renderCustomContentComposer(giftChatProps)}
        </View>
      );
    } else {
      return (
        <View style={{padding: 1, flex: 1}}>
          {this.renderCustomContentComposer(giftChatProps)}
        </View>
      );
    }
  };

  renderCustomContentComposer = giftChatProps => {
    //restricting the number of lines to scrollView
    const {screenHeight, msgText} = this.state;
    let restrictHeight = 4 * 25;
    let compareHeight = screenHeight > restrictHeight;
    //android 2 line height is 56.5
    return (
      <View>
        {this.renderAttachedListingInComposer()}
        <TextInput
          style={[
            {
              borderWidth: 1,
              borderColor: SRXColor.LightGray,
              borderRadius: 20,
              margin: Spacing.XS,
              paddingLeft: Spacing.M,
              fontSize: 14,
            },
            IS_IOS ? {paddingTop: 10} : null,
            screenHeight < 50
              ? {height: 40}
              : compareHeight
              ? {height: restrictHeight}
              : null,
          ]}
          autoCorrect={false}
          multiline={true}
          onContentSizeChange={e =>
            this.onContentSizeChange(e.nativeEvent.contentSize, giftChatProps)
          }
          onChangeText={text => this.setState({msgText: text})}
          placeholder={giftChatProps.placeholder}
          value={msgText}
        />
      </View>
    );
  };

  renderInputToolbar = props => {
    const {agentInfo} = this.props;
    if (agentInfo.name == 'SRX') {
      return null;
    } else {
      return <InputToolbar {...props} />;
    }
  };

  sendAndClearMsg = giftChatProps => {
    const {msgText} = this.state;
    giftChatProps.onSend({text: msgText.trim()}, true);
    this.setState({msgText: ''});
  };

  renderTime = giftChatProps => {
    const {currentMessage} = giftChatProps;
    if (currentMessage.listing) {
      return;
    }
    return (
      <Time
        {...giftChatProps}
        textStyle={{
          right: {
            color: SRXColor.Gray,
            fontSize: 10,
          },
          left: {
            color: SRXColor.Gray,
            fontSize: 10,
          },
        }}
      />
    );
  };

  renderSend = giftChatProps => {
    const {msgText} = this.state;
    if (msgText.trim() != '') {
      return (
        <TouchableOpacity onPress={() => this.sendAndClearMsg(giftChatProps)}>
          <View
            style={[
              {
                backgroundColor: SRXColor.Orange,
                width: 50,
                marginRight: Spacing.XS,
                justifyContent: 'center',
              },
              {borderRadius: 17, height: 34, marginBottom: Spacing.S},
            ]}>
            <FeatherIcon
              name={'send'}
              size={20}
              color={SRXColor.Black}
              style={[
                {
                  alignSelf: 'center',
                  transform: [{rotate: '45deg'}],
                },
              ]}
            />
          </View>
        </TouchableOpacity>
      );
    } else {
      return;
    }
  };

  renderCustomView = props => {
    const {currentMessage} = props;
    const {listing} = currentMessage;
    console.log('Check current message 123');
    console.log(currentMessage);
    if (!ObjectUtil.isEmpty(listing)) {
      let listingPO = new ListingPO(listing);
      if (listingPO.isTransacted()) {
        return (
          <ChatTransactedListItem
            containerStyle={Styles.messageCustomViewStyle}
            listingPO={listingPO}
            isMessage={true}
          />
        );
      } else {
        return (
          <ChatListingListItem
            containerStyle={Styles.messageCustomViewStyle}
            listingPO={listingPO}
            isMessage={true}
            onItemPress={() => this.showListingDetails(listingPO)}
          />
        );
      }
    }
    return null;
  };

  render() {
    const {agentInfo, userPO} = this.props;
    const {loadingState, minInputToolbarHeight} = this.state;
    if (loadingState === LoadingState.Loading) {
      return (
        <View style={{flex: 1, alignItems: 'center', justifyContent: 'center'}}>
          <ActivityIndicator size="large" />
        </View>
      );
    } else if (loadingState === LoadingState.Loaded) {
      console.log(minInputToolbarHeight + 'Toolbar height');
      return (
        /**
         * The reason for not using SafeAreaView
         *
         * 1. The bottom space sometimes shows extra spaces, which make the space very large
         * 2. If using SafeAreaView from react-native-safe-area-view, it might not show the bottom space
         *
         * Hence forcing the bottom bar when the constant IS_IPHONE_X is true ==> this need to be updated according to apple release
         */
        // <SafeAreaView style={{flex: 1, backgroundColor: 'white'}}>
        <View style={{flex: 1, marginBottom: IS_IPHONE_X ? 34 : 0}}>
          <GiftedChat
            ref={ref => (this.chat = ref)}
            messages={this.state.messages}
            placeholder="Leave a comment"
            renderCustomView={this.renderCustomView}
            onSend={messages => this.onSend(messages)}
            renderActions={this.renderCustomAction}
            renderBubble={this.renderCustomBubble}
            renderMessageImage={this.renderCustomMessageImage}
            renderComposer={this.renderCustomComposer}
            renderSend={this.renderSend}
            renderTime={this.renderTime}
            renderInputToolbar={this.renderInputToolbar}
            renderAvatar={null}
            textInputProps={{
              autoCorrect: false,
              multiline: true,
              numberOfLines: 4,
            }}
            user={{
              _id: userPO.encryptedUserId,
            }}
            minInputToolbarHeight={
              agentInfo.name == 'SRX' ? 0 : minInputToolbarHeight
            }
            bottomOffset={IS_IPHONE_X ? 34 : 0}
          />
        </View>
        // {/* </SafeAreaView> */}
      );
    } else {
      return <View />;
    }
  }
}

const Styles = StyleSheet.create({
  messageCustomViewStyle: {
    borderRadius: 10,
    overflow: 'hidden',
    backgroundColor: 'rgba(0,0,0, 0.1)',
    paddingHorizontal: Spacing.XS,
    margin: Spacing.XS / 2,
    minWidth: 250,
  },
  attachedComponentPreview: {
    left: -30,
    width: width - 10,
  },
});

ChatRoom.propTypes = {
  agentInfo: PropTypes.object.isRequired,
  msgTemplate: PropTypes.string,
  listingId: PropTypes.number,
  listingPO: PropTypes.instanceOf(ListingPO),
  //userPO: PropTypes.object.isRequired,
  //the 2 functions are only required for ChatHomeScreen
  // updateLastMessage: PropTypes.func,
  // updateConversationRoomId: PropTypes.func
};
ChatRoom.Sources = DirectorySource;

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
  };
};

export default connect(
  mapStateToProps,
  null,
)(ChatRoom);
// export default ChatRoom;
