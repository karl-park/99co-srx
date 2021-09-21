import React, {Component} from 'react';
import {Alert, View, Keyboard, TouchableOpacity, Text} from 'react-native';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
import {connect} from 'react-redux';
import SafeAreaView from 'react-native-safe-area-view';
import {Navigation} from 'react-native-navigation';

import {Styles} from './Styles';
import {
  Button,
  TextInput,
  LargeTitleComponent,
  FeatherIcon,
  BodyText,
  Separator,
  Avatar,
  ImagePicker,
} from '../../components';
import {IS_IOS, AlertMessage, SRXColor} from '../../constants';
import {ObjectUtil, CommonUtil, UserUtil} from '../../utils';
import {LoginService} from '../../services';
import {
  updateEmailAndPassword,
  updateUserPO,
  loadUserProfile,
} from '../../actions';
import {Spacing, InputStyles} from '../../styles';
import PropTypes from 'prop-types';

const UpdateState = {
  Normal: 'normal', //default, not update
  Updating: 'updating', //sending update request
  Updated: 'updated',
};

const UpdateProfileSource = {
  XValueForm: 'X-Value Form', //from X-Value Form
  XValueResult: 'X-Value Result', //from X-Value Result
};

class UpdateProfile extends LargeTitleComponent {
  //options
  static options(passProps) {
    return {
      ...super.options(passProps),
      bottomTabs: {
        visible: false,
        animate: true,
        drawBehind: IS_IOS ? false : true,
      },
    };
  }

  //State Variables
  state = {
    updateState: UpdateState.Normal,
    userPO: {
      name: '',
      email: '',
      mobile: '',
      facebookId: '',
      oldPassword: '',
      newPassword: '',
      communityAlias: '',
    },
    errorMessageObj: {
      errMsgName: '',
      errMsgEmail: '',
      errMsgMobile: '',
      errMsgPassword: '',
      errMsgCommunityAlias: '',
    },
    editablePassword: false,
    showPassword: false,
    isUpdatingPwd: false,
  };

  static propTypes = {
    source: PropTypes.oneOf(Object.keys(UpdateProfileSource)),
    updateMobileVerification: PropTypes.func,
    loadXValue: PropTypes.func,
    updateXValueResult: PropTypes.func,
    isModal: PropTypes.bool, //to indicate if this screen is presented as modal
  };

  static defaultProps = {
    isModal: false,
  };

  //Constructor
  constructor(props) {
    super(props);

    Navigation.events().bindComponent(this);
    this.setupTopBar();

    this.populateUserInformation = this.populateUserInformation.bind(this);
    this.onUpdateProfile = this.onUpdateProfile.bind(this);
    this.onPressPassword = this.onPressPassword.bind(this);
    this.onSuccessVerification = this.onSuccessVerification.bind(this);
    this.updateProfilePic = this.updateProfilePic.bind(this);
  }

  componentDidMount() {
    this.getStateToUpdateWhileUserPOPropsUpdate().then(stateForUpdates => {
      this.setState(stateForUpdates, () => {
        if (!ObjectUtil.isEmpty(stateForUpdates.userPO)) {
          const {userPO} = stateForUpdates;
          if (
            ObjectUtil.isEmpty(userPO.communityAlias) &&
            !ObjectUtil.isEmpty(userPO.name)
          ) {
            var nameArray = userPO.name.split(' ');
            var nameInitials = nameArray.map(item => {
              return item.charAt(0);
            });
            this.setState(
              {
                userPO: {
                  ...this.state.userPO,
                  communityAlias: nameInitials.join(''),
                },
              },
              () => {
                if (this.communityNameTF) {
                  this.communityNameTF.focus();
                }
              },
            );
          }
        }
      });
    });
  }

  componentDidUpdate(prevProps) {
    if (prevProps.userPO !== this.props.userPO) {
      this.populateUserInformation();
    }
  }

  setupTopBar() {
    const {isModal} = this.props;
    if (isModal) {
      FeatherIcon.getImageSource('x', 25, 'black').then(icon_close => {
        Navigation.mergeOptions(this.props.componentId, {
          topBar: {
            leftButtons: [
              {
                id: 'close_btn',
                icon: icon_close,
              },
            ],
          },
        });
      });
    }
  }

  navigationButtonPressed({buttonId}) {
    if (buttonId === 'close_btn') {
      Navigation.dismissModal(this.props.componentId);
    }
  }

  populateUserInformation() {
    this.getStateToUpdateWhileUserPOPropsUpdate().then(stateForUpdates => {
      this.setState(stateForUpdates);
    });
  }

  /**
   * returning an object used to update the state
   */
  getStateToUpdateWhileUserPOPropsUpdate() {
    const {userPO} = this.props;
    const stateUserPO = this.state.userPO;
    return new Promise(function(resolve, reject) {
      UserUtil.getDataForLogin()
        .then(({username, password, facebookId}) => {
          //Check user login with facebook or not to allow update password
          if (!ObjectUtil.isEmpty(userPO)) {
            var newUserPO = {
              ...stateUserPO,
              name: userPO.name, //from props
              email: userPO.email, //from props
              //saved password is old password
              oldPassword: !ObjectUtil.isEmpty(password) ? password : '',
              //get facbook id
              facebookId: !ObjectUtil.isEmpty(facebookId) ? facebookId : '',

              communityAlias: userPO.communityAlias ?? '',
            };

            if (userPO.mobileLocalNum) {
              newUserPO = {
                ...newUserPO,
                mobile: userPO.mobileLocalNum.toString(), //from props
              };
              resolve({
                userPO: newUserPO,
                updateState: UpdateState.Updated,
              });
            } else {
              resolve({
                userPO: newUserPO,
              });
            }

            resolve(newUserPO);
          } else {
            reject('Missing userPO');
          }
        })
        .catch(error => {
          reject(error);
        });
    });
  }

  //On Click Update Button
  onUpdateProfile() {
    if (this.isAllValidate()) {
      //Dimiss Keyboard after clicking update button
      Keyboard.dismiss();

      //Updating state before call API
      this.setState({updateState: UpdateState.Updating}, () => {
        this.updateUserAccount();
      });
    }
  }

  onPressPassword() {
    const {editablePassword} = this.state;
    if (!editablePassword) {
      this.showSecuredPasswordModal();
    }
  }

  showSecuredPasswordModal() {
    //TODO: display as modal
    Navigation.showOverlay({
      component: {
        name: 'UpdateProfile.PasswordModal',
        passProps: {
          onSuccess: this.onSuccessVerification,
        },
      },
    });
  }

  onSuccessVerification = confirmedPassword => {
    if (!ObjectUtil.isEmpty(confirmedPassword)) {
      const {userPO} = this.state;
      var newUserPO = {
        ...userPO,
        oldPassword: confirmedPassword, //it's old password
        newPassword: confirmedPassword,
      };

      this.setState({
        editablePassword: true,
        userPO: newUserPO,
        updateState: UpdateState.Normal, //Edited password, back button to normal
      });
    }
  };

  updateXValueResult = () => {
    const {updateXValueResult} = this.props;
    if (updateXValueResult) {
      updateXValueResult();
    }
  };

  goBack = () => {
    const {
      updateMobileVerification,
      loadXValue,
      updateXValueResult,
      source,
    } = this.props;
    if (updateMobileVerification) {
      updateMobileVerification();
    }
    if (loadXValue) {
      loadXValue();
    }
    if (updateXValueResult) {
      updateXValueResult();
    }
    Navigation.pop(this.props.componentId);
  };

  showSuccessfulMessage() {
    const {source, isModal} = this.props;
    this.setState({updateState: UpdateState.Updated}, () => {
      if (
        source == UpdateProfileSource.XValueForm ||
        source == UpdateProfileSource.XValueResult
      ) {
        Alert.alert(
          AlertMessage.SuccessMessageTitle,
          'Account Details Successfully Updated',
          [
            {text: 'Cancel', style: 'cancel'},
            {
              text: 'OK',
              onPress: () => this.goBack(),
            },
          ],
        );
      } else {
        Alert.alert(
          AlertMessage.SuccessMessageTitle,
          'Account Details Successfully Updated',
          [
            {
              text: 'OK',
              onPress: () => {
                if (isModal) {
                  Navigation.dismissModal(this.props.componentId);
                }
              },
            },
          ],
        );
      }
    });
  }

  closeVerifyMobileModal() {
    //when close modal without verfiying OTP, back to the previous name and mobile num
    const {userPO} = this.props;
    if (!ObjectUtil.isEmpty(userPO)) {
      const {name, mobileLocalNum} = userPO;
      //Back to button normal state
      this.setState({
        updateState: UpdateState.Normal,
        userPO: {
          ...this.state.userPO,
          name: !ObjectUtil.isEmpty(name) ? name : '',
          mobile: mobileLocalNum ? mobileLocalNum.toString() : '',
        },
      });
    } else {
      this.setState({
        updateState: UpdateState.Normal,
      });
    }
  }

  //API Method
  updateUserAccount() {
    const {userPO} = this.props;
    const {errorMessageObj, isUpdatingPwd} = this.state;
    const {
      name,
      mobile,
      oldPassword,
      newPassword,
      facebookId,
      communityAlias,
    } = this.state.userPO;

    let updatingParams = {
      name,
      hasAgreedToPDPA: true,
      isUpdatingPwd,
      oldPwd: isUpdatingPwd ? oldPassword : '',
      newPwd: isUpdatingPwd ? newPassword : '',
    };

    if (userPO.communityAlias != communityAlias) {
      updatingParams.communityAlias = communityAlias;
    }

    LoginService.updateUserAccount(updatingParams)
      .catch(error => {
        //back to normal state
        this.setState({updateState: UpdateState.Normal}, () => {
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
        });
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            //back to normal state
            this.setState({updateState: UpdateState.Normal}, () => {
              Alert.alert(AlertMessage.ErrorMessageTitle, error);
            });
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              if (response.result === 'success') {
                //if there is changes in mobile number, need to update
                if (!ObjectUtil.isEmpty(userPO)) {
                  //For updating password, need to update in local storage
                  if (ObjectUtil.isEmpty(facebookId) && isUpdatingPwd) {
                    this.props.updateEmailAndPassword(
                      userPO.email,
                      newPassword,
                      {...userPO, name, communityAlias},
                    );
                  } else if (
                    userPO.name !== name ||
                    userPO.communityAlias !== communityAlias
                  ) {
                    this.props.updateUserPO({
                      ...userPO,
                      name,
                      communityAlias,
                    });
                  }

                  //Change states
                  this.setState(
                    {
                      updateState: UpdateState.Updated,
                      editablePassword: false,
                      showPassword: false,
                      isUpdatingPwd: false,
                      userPO: {
                        ...this.state.userPO,
                        oldPassword: newPassword,
                        newPassword: '',
                      },
                    },
                    () => {
                      this.handleMobileVerificaitonRequest();
                    },
                  );
                } //end of userPO
              } //end of success
            }
          }
        } else {
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
        }
      });
  }

  handleMobileVerificaitonRequest() {
    //Const variables
    const {userPO} = this.props;
    const {errorMessageObj} = this.state;
    const {mobile} = this.state.userPO;

    if (userPO.mobileLocalNum) {
      if (
        !ObjectUtil.isEmpty(mobile) &&
        userPO.mobileLocalNum !== parseInt(mobile)
      ) {
        this.requestMobileVerification();
        return;
      } else if (ObjectUtil.isEmpty(mobile)) {
        this.setState({
          errorMessageObj: {
            ...errorMessageObj,
            errMsgMobile: 'Required field',
          },
        });
        return;
      } else {
        this.showSuccessfulMessage();
      }
    } else {
      //haven't added mobile number in userPO and allow to update
      if (!ObjectUtil.isEmpty(mobile)) {
        this.requestMobileVerification();
        return;
      } else {
        this.showSuccessfulMessage();
      }
    }
  }

  //requestMobileVerification API
  requestMobileVerification() {
    const {mobile} = this.state.userPO;

    LoginService.requestMobileVerification({
      countryCode: '+65', //default
      mobile,
    })
      .catch(error => {
        //back to normal state
        this.setState({updateState: UpdateState.Normal}, () => {
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
        });
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            //back to normal state
            this.setState({updateState: UpdateState.Normal}, () => {
              Alert.alert(AlertMessage.ErrorMessageTitle, error);
            });
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              if (response.result === 'success') {
                //Updated state
                this.setState({updateState: UpdateState.Updated}, () => {
                  this.directToVerfiyMobileNumber();
                });
              }
            }
          }
        } else {
          //back to normal state
          this.setState({updateState: UpdateState.Normal}, () => {
            Alert.alert(AlertMessage.ErrorMessageTitle, error);
          });
        }
      });
  }

  directToVerfiyMobileNumber = () => {
    const {name, mobile} = this.state.userPO;
    Navigation.showModal({
      stack: {
        children: [
          {
            component: {
              name: 'SideMainMenu.VerifyMobileNumber',
              passProps: {
                name,
                mobile,
                showSuccessfulMessage: this.showSuccessfulMessage.bind(this),
                closeVerifyMobileModal: this.closeVerifyMobileModal.bind(this),
                updateXValueResult: this.updateXValueResult.bind(this),
              },
              options: {
                modalPresentationStyle: 'overFullScreen',

                topBar: {
                  visible: false,
                  drawBehind: true,
                },
              },
            },
          },
        ],
      },
    });
  };

  //Validation all checks
  isAllValidate() {
    if (!ObjectUtil.isEmpty(this.state.userPO)) {
      //variables
      const {name, mobile, newPassword, facebookId} = this.state.userPO;
      const {isUpdatingPwd, errorMessageObj} = this.state;
      let isAllValidate = true;

      let errorMessage = {
        name: '',
        mobile: '',
        password: '',
      };

      if (!ObjectUtil.isEmpty(errorMessageObj.errMsgCommunityAlias)) {
        isAllValidate = false;
      }

      if (ObjectUtil.isEmpty(name)) {
        isAllValidate = false;
        errorMessage.name = 'Required field';
      }

      if (ObjectUtil.isEmpty(mobile)) {
        isAllValidate = false;
        errorMessage.mobile = 'Required field';
      } else if (mobile && mobile.length !== 8) {
        isAllValidate = false;
        errorMessage.mobile = 'Please enter a valid mobile number';
      }

      //no check for facebook login user
      if (
        ObjectUtil.isEmpty(newPassword) &&
        ObjectUtil.isEmpty(facebookId) &&
        isUpdatingPwd
      ) {
        isAllValidate = false;
        errorMessage.password = 'Required field';
      }

      this.setState({
        errorMessageObj: {
          ...errorMessageObj,
          errMsgName: errorMessage.name,
          errMsgMobile: errorMessage.mobile,
          errMsgPassword: errorMessage.password,
        },
      });

      return isAllValidate;
    } else {
      return false;
    }
  }

  updateProfilePic() {
    const options = {
      title: null,
      cancelButtonTitle: 'Dismiss',
      takePhotoButtonTitle: 'Take a photo',
      chooseFromLibraryButtonTitle: 'Choose a photo',
      customButtons: [{name: 'remove', title: 'Remove photo'}],
      mediaType: 'photo',
      maxWidth: 1024,
      maxHeight: 1024,
      allowsEditing: true,
      storageOptions: {
        skipBackup: true,
        path: 'images',
      },
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
        } else if (response.error == 'Photo library permissions not granted') {
          if (IS_IOS) {
            const title = 'Permission Denied';
            const message =
              "This function required to access your photos.\nTo allow, please visit Settings App, and allow 'Photos' for SRX Property.";
            CommonUtil.directToiOSSettings(title, message);
          }
        }
        console.log('ImagePicker Error: ', response.error);
      } else if (response.customButton) {
        console.log('User tapped custom button: ', response.customButton);
        //remove
        if (response.customButton == 'remove') {
          this.removePhoto();
        }
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
        this.uploadPhoto(photoItem);
      }
    });
  }

  uploadPhoto(photo) {
    const {loadUserProfile, userPO} = this.props;
    LoginService.updateProfilePhoto({photo})
      .then(response => {
        loadUserProfile(userPO);
      })
      .catch(error => {
        console.log(error);
      });
  }

  removePhoto() {
    const {loadUserProfile, userPO} = this.props;
    LoginService.removePhoto()
      .then(response => {
        loadUserProfile(userPO);
      })
      .catch(error => {
        console.log(error);
      });
  }

  validateCommunityAlias() {
    const {loadUserProfile} = this.props;
    const {userPO, errorMessageObj} = this.state;
    if (
      !ObjectUtil.isEmpty(userPO) &&
      !ObjectUtil.isEmpty(userPO.communityAlias)
    ) {
      LoginService.vetCommunityAlias({communityAlias: userPO.communityAlias})
        .then(response => {
          this.setState({
            errorMessageObj: {
              ...errorMessageObj,
              errMsgCommunityAlias: '',
            },
          });
        })
        .catch(err => {
          if (!ObjectUtil.isEmpty(err.data)) {
            const {error} = err.data;
            this.setState({
              errorMessageObj: {
                ...errorMessageObj,
                errMsgCommunityAlias: error ?? '',
              },
            });
          }
        });
    }
  }

  //Render Start Method
  renderUpdateForm() {
    return (
      <View style={Styles.accountDetailsContainer}>
        {this.renderProfilePicAndCommunityName()}
        <Separator />
        {this.renderPerssonaldetailsSection()}
        {this.renderUpdateButton()}
      </View>
    );
  }

  renderProfilePicAndCommunityName() {
    return (
      <View style={{marginBottom: Spacing.L}}>
        {this.renderProfilePic()}
        {this.renderCommunityName()}
      </View>
    );
  }

  renderProfilePic() {
    const {userPO} = this.props; //this one directly get from props, as this will not be included in update api
    return (
      <View style={{alignItems: 'flex-start'}}>
        <BodyText style={{marginBottom: Spacing.XS}}>Profile picture</BodyText>
        <TouchableOpacity onPress={this.updateProfilePic}>
          <View>
            <Avatar
              name={userPO.name}
              size={75}
              imageUrl={CommonUtil.handleImageUrl(userPO.photo)}
              textSize={35}
              backgroundColor={SRXColor.LightGray}
              borderColor={SRXColor.LightGray}
            />
            <FeatherIcon
              name={'camera'}
              size={12}
              color={SRXColor.White}
              style={{
                backgroundColor: SRXColor.Tertiary,
                padding: Spacing.XS / 2,
                borderRadius: 11,
                borderWidth: 1,
                borderColor: SRXColor.White,
                overflow: 'hidden',
                position: 'absolute',
                bottom: 0,
                right: 0,
              }}
            />
          </View>
        </TouchableOpacity>
      </View>
    );
  }

  renderCommunityName() {
    const {errorMessageObj, userPO} = this.state;
    const {communityAlias} = userPO;
    return (
      <View style={{marginTop: Spacing.XL}}>
        <BodyText>Display Name</BodyText>
        <View style={[Styles.inputContainer]}>
          <TextInput
            ref={component => (this.communityNameTF = component)}
            placeholder={'Display name'}
            defaultValue={communityAlias}
            maxLength={40}
            onChangeText={newText =>
              this.setState(
                {
                  userPO: {...userPO, communityAlias: newText},
                  errorMessageObj: {...errorMessageObj, errMsgName: ''},
                  updateState: UpdateState.Normal,
                },
                () => {
                  this.validateCommunityAlias();
                },
              )
            }
          />
        </View>
        {this.renderCommunityNameError()}
      </View>
    );
  }

  renderCommunityNameError() {
    const {errorMessageObj} = this.state;
    if (!ObjectUtil.isEmpty(errorMessageObj.errMsgCommunityAlias)) {
      return (
        <View style={{flexDirection: 'row', alignItems: 'center'}}>
          <FeatherIcon name={'x'} size={16} color={SRXColor.Red} />
          <Text
            style={{
              marginLeft: Spacing.XS / 2,
              fontSize: 12,
              color: '#FF151F',
            }}>
            {errorMessageObj.errMsgCommunityAlias}
          </Text>
        </View>
      );
    }
  }

  renderPerssonaldetailsSection() {
    const {errorMessageObj, userPO} = this.state;
    const {name, email, mobile} = userPO;
    return (
      <View>
        <BodyText style={{marginTop: Spacing.L}}>Personal details</BodyText>
        {/* User Name */}
        <View style={[Styles.inputContainer]}>
          <TextInput
            placeholder={'Name'}
            error={errorMessageObj.errMsgName}
            defaultValue={name}
            onChangeText={newUserName =>
              this.setState({
                userPO: {...userPO, name: newUserName},
                errorMessageObj: {...errorMessageObj, errMsgName: ''},
                updateState: UpdateState.Normal,
              })
            }
          />
        </View>

        {/* Email Address */}
        <View style={[Styles.inputContainer]}>
          <TextInput
            placeholder={'E-mail address'}
            editable={false}
            error={errorMessageObj.errMsgEmail}
            defaultValue={email}
            onChangeText={newEmail =>
              this.setState({
                userPO: {...userPO, email: newEmail},
                errorMessageObj: {...errorMessageObj, errMsgEmail: ''},
                updateState: UpdateState.Normal,
              })
            }
          />
        </View>

        {/* Mobile Number */}
        <View style={[Styles.inputContainer]}>
          <TextInput
            placeholder={'Mobile number'}
            keyboardType={IS_IOS ? 'number-pad' : 'numeric'}
            error={errorMessageObj.errMsgMobile}
            defaultValue={mobile}
            onChangeText={newMobileNumber =>
              this.setState({
                userPO: {...userPO, mobile: newMobileNumber},
                errorMessageObj: {...errorMessageObj, errMsgMobile: ''},
                updateState: UpdateState.Normal,
              })
            }
          />
        </View>

        {this.renderPasswordField()}
      </View>
    );
  }

  renderPasswordField() {
    const {
      showPassword,
      editablePassword,
      errorMessageObj,
      userPO,
    } = this.state;
    const {oldPassword, facebookId, newPassword} = userPO;

    //show password field for create account
    if (!ObjectUtil.isEmpty(oldPassword) && ObjectUtil.isEmpty(facebookId)) {
      if (editablePassword) {
        return (
          <View style={[Styles.inputContainer]}>
            <TextInput
              placeholder={'Password'}
              error={errorMessageObj.errMsgPassword}
              defaultValue={newPassword}
              editable={editablePassword}
              secureTextEntry={!showPassword}
              rightView={
                <Button
                  buttonStyle={{
                    alignItems: 'center',
                    justifyContent: 'center',
                  }}
                  onPress={() => this.setState({showPassword: !showPassword})}
                  leftView={
                    showPassword ? (
                      <FeatherIcon name="eye" size={25} color={'#858585'} />
                    ) : (
                      <FeatherIcon name="eye-off" size={25} color={'#858585'} />
                    )
                  }
                />
              }
              onChangeText={newPassword =>
                this.setState({
                  userPO: {...userPO, newPassword},
                  isUpdatingPwd: true, //changing text
                  errorMessageObj: {...errorMessageObj, errMsgPassword: ''},
                  updateState: UpdateState.Normal,
                })
              }
            />
          </View>
        );
      } else {
        //show masked password
        return (
          <TouchableOpacity
            style={[
              InputStyles.container,
              InputStyles.singleLineTextHeight,
              Styles.maskPasswordFieldContainer,
            ]}
            onPress={this.onPressPassword}>
            <BodyText style={{flex: 1}}>{'••••••'}</BodyText>
            <FeatherIcon name="eye-off" size={25} color={'#858585'} />
          </TouchableOpacity>
        );
      }
    }
  }

  //Render Update Button
  renderUpdateButton() {
    const {updateState} = this.state;
    var buttonTitle = 'Update';
    if (updateState === UpdateState.Updating) {
      buttonTitle = 'Updating';
    }

    //Disabled buttons for updating and updated state
    return (
      <View style={{alignItems: 'center', justifyContent: 'center', flex: 1}}>
        <Button
          buttonStyle={{marginTop: Spacing.L, marginBottom: 5}}
          buttonType={Button.buttonTypes.primary}
          isSelected={updateState !== UpdateState.Normal}
          onPress={this.onUpdateProfile}>
          {buttonTitle}
        </Button>
      </View>
    );
  }

  render() {
    return (
      <SafeAreaView style={{flex: 1, backgroundColor: SRXColor.White}}>
        <KeyboardAwareScrollView
          style={{flex: 1}}
          onScroll={this.onScroll}
          keyboardShouldPersistTaps={'handled'}
          bounces={false}>
          {/* title for update profile screen */}
          {this.renderLargeTitle('My Profile')}
          {this.renderUpdateForm()}
        </KeyboardAwareScrollView>
      </SafeAreaView>
    );
  }
}

UpdateProfile.Sources = UpdateProfileSource;

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
  };
};

export default connect(
  mapStateToProps,
  {updateEmailAndPassword, updateUserPO, loadUserProfile},
)(UpdateProfile);
