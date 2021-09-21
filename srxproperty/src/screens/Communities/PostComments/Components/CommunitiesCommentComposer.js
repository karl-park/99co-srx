import React, {Component} from 'react';
import {
  View,
  TouchableOpacity,
  TextInput,
  Keyboard,
  Alert,
  ActivityIndicator,
} from 'react-native';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';

import {
  FeatherIcon,
  HorizontalFlatList,
  ImagePicker,
  Button,
} from '../../../../components';
import {
  SRXColor,
  IS_IOS,
  AlertMessage,
  LoadingState,
} from '../../../../constants';
import {Spacing} from '../../../../styles';
import {ButtonStates} from '../../../../components/Button/Button';
import {CommunitiesCommentPhotoListItem} from '../CommentItems';
import {ObjectUtil, GoogleAnalyticUtil} from '../../../../utils';
import {CommunitiesStack} from '../../../../config';
import {CommunitiesService} from '../../../../services';

class CommunitiesCommentComposer extends Component {
  state = {
    message: '',
    uploadedImages: [],
    buttonState: ButtonStates.normal,
    sendCommentState: LoadingState.Normal,
  };

  constructor(props) {
    super(props);

    this.chooseImage = this.chooseImage.bind(this);
    this.uploadImage = this.uploadImage.bind(this);
  }

  componentDidMount() {
    this.keyboardDidShowListener = Keyboard.addListener(
      'keyboardDidShow',
      this.keyboardDidShow,
    );
    this.keyboardDidHideListener = Keyboard.addListener(
      'keyboardDidHide',
      this.keyboardDidHide,
    );

    const {showKeyboard} = this.props;
    if (showKeyboard == true && this.textInput) {
      this.textInput.focus();
    }
  }

  componentWillUnmount() {
    this.keyboardDidShowListener.remove();
    this.keyboardDidHideListener.remove();
  }

  componentDidUpdate(prevProps) {
    if (prevProps.showKeyboard !== this.props.showKeyboard) {
      if (this.props.showKeyboard && this.textInput) {
        this.textInput.focus();
      }
    }
  }

  keyboardDidHide = () => {
    const {onKeyboardHide} = this.props;
    if (onKeyboardHide) {
      onKeyboardHide();
    }
  };

  keyboardDidShow = () => {
    const {onKeyboardShow} = this.props;
    if (onKeyboardShow) {
      onKeyboardShow();
    }
  };

  chooseImage() {
    const options = {
      chooseFromLibraryButtonTitle: 'Upload Photo',
      title: 'Upload Image/Photo',
      storageOptions: {
        skipBackup: true,
        path: 'images',
      },
    };

    ImagePicker.showImagePicker(options, response => {
      if (response.didCancel) {
        console.log('User cancelled image picker');
      } else if (response.error) {
        if (response.error == 'Camera permissions not granted') {
          if (isIOS) {
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
        //Successfully get image uri
        const source = {uri: response.uri};
        console.log(source);

        var fileName = '';
        if (!ObjectUtil.isEmpty(response.fileName)) {
          fileName = response.fileName;
        }
        if (IS_IOS) {
          let photoItem = {
            uri: response.uri,
            type: 'image/jpeg',
            name: fileName, //a random name for now, it is needed else fetch wont grab the data from the uri
          };
          this.uploadImage(photoItem);
        } else {
          let photoItem = {
            uri: response.uri,
            type: 'image/gif',
            name: fileName, //a random name for now, it is needed else fetch wont grab the data from the uri
          };
          this.uploadImage(photoItem);
        } //end
      }
    });
  }

  uploadImage = photoItem => {
    const {uploadedImages} = this.state;
    var photo = {
      id: this.generatePhotoId(),
      ...photoItem,
    };
    uploadedImages.push(photo);

    this.setState({uploadedImages}, () => {
      if (this.textInput) {
        this.textInput.focus();
      }
    });
  };

  generatePhotoId() {
    const {uploadedImages} = this.state;
    if (Array.isArray(uploadedImages) && uploadedImages.length > 0) {
      var ids = uploadedImages.map(item => {
        return item.id;
      });
      var maxValue = Math.max(...ids) + 1;
      return maxValue;
    } else {
      return 0;
    }
  }

  onRemovePhoto = photo => {
    const {uploadedImages} = this.state;
    var tempUploadedImages = uploadedImages.filter(item => item.id != photo.id);
    this.setState({uploadedImages: tempUploadedImages});
  };

  //Send Image
  onSendMessage = () => {
    const {message, uploadedImages} = this.state;
    const {userPO} = this.props;

    if (!ObjectUtil.isEmpty(userPO)) {
      if (userPO.mobileVerified) {
        var images = uploadedImages.map((item, index) => {
          var tempPhoto = {
            key: item.name + index,
            value: item,
          };
          return tempPhoto;
        });
        this.setState(
          {
            sendCommentState: LoadingState.Loading,
            message: '',
            uploadedImages: [],
          },
          () => {
            this.addUpdateComment(message, images);
          },
        );
        Keyboard.dismiss();
      } else {
        //show mobile verification modal
        CommunitiesStack.showMobileVerificationModal();
      }
    } else {
      //show sign in register modal
      CommunitiesStack.showSignInRegisterModal();
    }
  };

  addUpdateComment = (message, medias) => {
    const {postId} = this.props;

    var hasText = 'false';
    var hasImage = 'false';
    var hasGIF = 'false';
    if (message !== '') {
      hasText = 'true';
    }
    for (var i = 0; i < medias.length; i++) {
      if (medias[i].value) {
        if (medias[i].value.name.includes('gif')) {
          hasGIF = 'true';
        } else {
          hasImage = 'true';
        }
      }
    }
    console.log('images media');
    console.log(hasGIF);
    console.log(hasImage);
    GoogleAnalyticUtil.trackCommunityActivity({
      parameters: {type: 'post', hasGIF, hasText, hasImage},
    });
    CommunitiesService.addUpdateComment({
      id: null,
      postId: postId,
      comment: message,
      photos: medias,
    })
      .then(response => {
        const {result} = response;
        if (result == 'success') {
          this.setState({sendCommentState: LoadingState.Loaded}, () => {
            const {onCommentSuccess} = this.props;
            if (onCommentSuccess) {
              onCommentSuccess(response);
            }
          });
        } else {
          const {error} = response;
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
          this.setState({sendCommentState: LoadingState.Failed});
        }
      })
      .catch(error => {
        const {data} = error;
        if (data && data.error) {
          Alert.alert(AlertMessage.ErrorMessageTitle, data.error);
        }
        this.setState({sendCommentState: LoadingState.Failed});
      });
  };

  renderSelectedImages() {
    const {uploadedImages} = this.state;
    if (Array.isArray(uploadedImages) && uploadedImages.length > 0) {
      return (
        <HorizontalFlatList
          data={uploadedImages}
          showsHorizontalScrollIndicator={false}
          extraData={this.state}
          keyExtractor={item => item.key}
          renderItem={({item, index}) => (
            <View
              style={{
                alignItems: 'center',
                justifyContent: 'center',
              }}>
              <CommunitiesCommentPhotoListItem
                photo={item}
                onRemove={item => this.onRemovePhoto(item)}
              />
            </View>
          )}
        />
      );
    }
  }

  renderImagePicker() {
    const {uploadedImages, sendCommentState} = this.state;
    var isUploadBtnDisabled = false;
    if (sendCommentState == LoadingState.Loading) {
      isUploadBtnDisabled = true;
    } else {
      isUploadBtnDisabled = uploadedImages.length != 0;
    }
    return (
      <TouchableOpacity
        onPress={this.chooseImage}
        disabled={isUploadBtnDisabled}>
        <FeatherIcon
          name={'camera'}
          size={26}
          color={isUploadBtnDisabled ? SRXColor.Gray : SRXColor.Black}
          style={{alignSelf: 'center'}}
        />
      </TouchableOpacity>
    );
  }

  onContentSizeChange = contentSize => {
    //TODO: to check on iOS
    this.setState({screenHeight: contentSize.height});
  };

  renderTextInput() {
    const {screenHeight, message} = this.state;
    let restrictHeight = 4 * 25;
    let compareHeight = screenHeight > restrictHeight;
    return (
      <View style={{flex: 1}}>
        <TextInput
          ref={ref => (this.textInput = ref)}
          style={[
            Styles.textInputStyle,
            {
              height:
                screenHeight < 50 ? 40 : compareHeight ? restrictHeight : null,
            },
          ]}
          autoCorrect={false}
          multiline={true}
          onContentSizeChange={e =>
            this.onContentSizeChange(e.nativeEvent.contentSize)
          }
          value={message}
          placeholder={'Add your comment'}
          onChangeText={text => {
            this.setState({message: text});
          }}
        />
      </View>
    );
  }

  renderSendButton() {
    const {buttonState, sendCommentState, message, uploadedImages} = this.state;

    var buttonDisabled = sendCommentState == LoadingState.Loading;
    if (message.trim() === '' && uploadedImages.length < 1) {
      buttonDisabled = true;
    }

    if (!buttonDisabled) {
      return (
        <Button
          disabled={buttonDisabled}
          buttonStyle={[
            Styles.sendButtonContainer,
            {
              backgroundColor:
                buttonState == ButtonStates.highlighted
                  ? '#FFAD33'
                  : SRXColor.Orange,
            },
          ]}
          onPressIn={() => {
            this.setState({buttonState: ButtonStates.highlighted});
          }}
          onPressOut={() => {
            this.setState({buttonState: ButtonStates.normal});
          }}
          activeOpacity={1}
          leftView={
            sendCommentState == LoadingState.Loading ? (
              <ActivityIndicator />
            ) : (
              <FeatherIcon
                name={'send'}
                size={25}
                color={SRXColor.White}
                style={[
                  {
                    alignSelf: 'center',
                    transform: [{rotate: '45deg'}],
                  },
                ]}
              />
            )
          }
          onPress={this.onSendMessage}
        />
      );
    }
    return <View />;
  }

  renderPickerAndSender() {
    return (
      <View style={{flexDirection: 'row', alignItems: 'center'}}>
        {this.renderImagePicker()}
        {this.renderTextInput()}
        {this.renderSendButton()}
      </View>
    );
  }

  render() {
    return (
      <View style={Styles.mainContainer}>
        {this.renderSelectedImages()}
        {this.renderPickerAndSender()}
      </View>
    );
  }
}

CommunitiesCommentComposer.propTypes = {
  /** post id is required if comment or react */
  postId: PropTypes.number.isRequired,

  showKeyboard: PropTypes.bool,

  onKeyboardHide: PropTypes.func,

  onKeyboardShow: PropTypes.func,

  onCommentSuccess: PropTypes.func,
};

const Styles = {
  mainContainer: {
    paddingLeft: Spacing.M,
    paddingRight: Spacing.M,
    paddingTop: Spacing.S,
    paddingBottom: Spacing.S,
  },
  textInputStyle: {
    borderWidth: 1,
    borderColor: SRXColor.LightGray,
    borderRadius: 20,
    marginLeft: Spacing.XS,
    paddingLeft: Spacing.M,
    paddingRight: Spacing.M,
    fontSize: 14,
    paddingTop: IS_IOS ? 10 : null,
  },
  sendButtonContainer: {
    width: 60,
    height: 40,
    marginLeft: -30,
    justifyContent: 'center',
    alignSelf: 'flex-end',
    borderRadius: 20,
  },
};

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
  };
};

export default connect(
  mapStateToProps,
  null,
)(CommunitiesCommentComposer);
