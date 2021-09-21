import React, {Component} from 'react';
import {Platform, View, TouchableOpacity, Image, Alert} from 'react-native';
import {Navigation} from 'react-native-navigation';
import SafeAreaView from 'react-native-safe-area-view';
import appleAuth, {
  AppleAuthError,
  AppleAuthRequestScope,
  AppleAuthRequestOperation,
} from '@invertase/react-native-apple-authentication';
import {
  FeatherIcon,
  BodyText,
  Heading2,
  Subtext,
  ExtraSmallBodyText,
  SmallBodyText,
} from '../../components';
import {SRXColor, AlertMessage, IS_IOS} from '../../constants';
import {Spacing} from '../../styles';
import {
  UserAuth_FacebookIcon,
  Splash_Screen_Content,
  UserAuth_SmallIcon_Apple,
  UserAuth_SmallIcon_FB,
  UserAuth_SmallIcon_Google,
} from '../../assets';
import analytics from '@react-native-firebase/analytics';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';

import {GoogleSignin, statusCodes} from '@react-native-community/google-signin';
import {
  LoginManager,
  AccessToken,
  GraphRequest,
  GraphRequestManager,
} from 'react-native-fbsdk';

import {Register, SignInPage} from '../UserAuthentication';
import {connect} from 'react-redux';
import {
  SignInRegisterSource,
  GoogleSignInIds,
} from '../UserAuthentication/Constants/UserAuthenticationConstants';
import {ObjectUtil, CommonUtil, StringUtil} from '../../utils';
import {LoginStack} from '../../config';
import {
  fblogin,
  login,
  updateUserPO,
  signup,
  appleSignIn,
  performGoogleSignin,
  loginWithPtUserId,
} from '../../actions';
import {SRXPropertyUserPO} from '../../dataObject';
import {LoginService} from '../../services';
import {Button} from '../../components/Button/Button';

const isIOS = Platform.OS === 'ios';

class SignInRegister extends Component {
  static options(passProps) {
    return {
      topBar: {
        visible: false,
        drawBehind: isIOS ? false : true,
      },
    };
  }

  state = {
    selectedSignIn: this.props.type
      ? this.props.type === 'S'
        ? true
        : false
      : true,
    userLoginPO: {
      username: '',
      password: '',
    },
    errorMessage: {
      errMsgUserName: '',
      errMsgPassword: '',
    },
    userLoginPOSgHome: {
      postalCode: '',
      mobileNumber: '',
    },
    errorMessageSgHome: {
      errorMsgPostalCode: '',
      errorMsgMobileNumber: '',
    },
    userSignUpPO: {
      name: '',
      email: '',
      mobile: '',
      passwordSignUp: '',
    },
    errorMessageObj: {
      errMsgName: '',
      errMsgEmail: '',
      errMsgMobile: '',
      errMsgPasswordSignUp: '',
    },
  };

  constructor(props) {
    super(props);
    this.onCloseSignInRegisterModal = this.onCloseSignInRegisterModal.bind(
      this,
    );
    this.onAppleButtonPress = this.onAppleButtonPress.bind(this);
  }

  componentDidMount() {
    const {conciergeName, conciergeEmail} = this.props;
    const {userSignUpPO} = this.state;

    this.setupGoogleSignInConfigure();

    //Getting Data from Concierge
    this.setState({
      userSignUpPO: {
        ...userSignUpPO,
        name: conciergeName ? conciergeName : '',
        email: conciergeEmail ? conciergeEmail : '',
      },
    });
  }

  componentDidUpdate(prevProps) {
    if (prevProps.userPO !== this.props.userPO) {
      if (!ObjectUtil.isEmpty(this.props.userPO)) {
        this.onCloseSignInRegisterModal();
      }
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

  onChangeUserLoginPO = (newUserInfo, newErrorMessage) => {
    const {userLoginPO, errorMessage} = this.state;
    this.setState({
      userLoginPO: {
        ...userLoginPO,
        username: newUserInfo.username,
        password: newUserInfo.password,
      },
      errorMessage: {
        ...errorMessage,
        ...newErrorMessage,
      },
    });
  };

  isAllValidate() {
    const {username, password} = this.state.userLoginPO;

    if (!ObjectUtil.isEmpty(this.state.userLoginPO)) {
      let isAllValidate = true;
      let errorMessage = {username: '', password: ''};

      if (ObjectUtil.isEmpty(username) || username === '') {
        isAllValidate = false;
        errorMessage.username = 'Required field';
      }

      if (ObjectUtil.isEmpty(password) || password === '') {
        isAllValidate = false;
        errorMessage.password = 'Required field';
      }

      this.setState({
        errorMessage: {
          ...this.state.errorMessage,
          errMsgUserName: errorMessage.username,
          errMsgPassword: errorMessage.password,
        },
      });

      return isAllValidate;
    } else {
      return false;
    }
  }

  onChangeUserLoginPOSgHome = (newUserInfo, newErrorMessage) => {
    const {userLoginPOSgHome, errorMessageSgHome} = this.state;
    this.setState({
      userLoginPOSgHome: {
        ...userLoginPOSgHome,
        postalCode: newUserInfo.postalCode,
        mobileNumber: newUserInfo.mobileNumber,
      },
      errorMessageSgHome: {
        ...errorMessageSgHome,
        ...newErrorMessage,
      },
    });
  };

  onPressSignInButton = () => {
    if (this.isAllValidate()) {
      //..call login function for login
      this.login();
    }
  };

  login = () => {
    const {username, password} = this.state.userLoginPO;
    this.props.login({username, password});
  };

  showForgotPasswordModal = () => {
    const {source} = this.props;
    if (source === SignInRegisterSource.SplashScreen) {
      this.showForgotPasswordModalfromIntro();
    } else {
      this.onCloseSignInRegisterModal().then(() => {
        Navigation.showModal({
          stack: {
            children: [
              {
                component: {
                  name: 'LoginStack.forgotPassword',
                  options: {
                    modalPresentationStyle: 'overFullScreen',
                  },
                },
              },
            ],
          },
        });
      });
    }
  };

  showForgotPasswordModalfromIntro = () => {
    const {showForgotPasswordModalfromIntro} = this.props;
    if (showForgotPasswordModalfromIntro) {
      showForgotPasswordModalfromIntro();
    }
  };

  onPressOK = () => {
    const {source} = this.props;
    if (source === SignInRegisterSource.SplashScreen) {
      this.onPressOKfromIntro();
    } else {
      this.onCloseSignInRegisterModal().then(() => {
        LoginStack.showSignInRegisterModal();
      });
    }
  };

  onPressOKfromIntro = () => {
    const {onPressOKfromIntro} = this.props;
    if (onPressOKfromIntro) {
      onPressOKfromIntro();
    }
  };

  onCloseSignInRegisterModal() {
    return Navigation.dismissModal(this.props.componentId);
  }

  closeIntro = () => {
    const {closeIntro} = this.props;
    if (closeIntro) {
      closeIntro();
    }
  };

  isAllValidateSgHome() {
    const {userLoginPOSgHome} = this.state;

    if (!ObjectUtil.isEmpty(userLoginPOSgHome)) {
      //get value from userLoginPOSgHome
      const {postalCode, mobileNumber} = userLoginPOSgHome;
      let isAllValidate = true;
      let errorMessage = {postalCode: '', mobileNumber: ''};

      if (ObjectUtil.isEmpty(postalCode) || postalCode == '') {
        isAllValidate = false;
        errorMessage.postalCode = 'Required field';
      }

      if (ObjectUtil.isEmpty(mobileNumber) || mobileNumber == '') {
        isAllValidate = false;
        errorMessage.mobileNumber = 'Required field';
      }

      this.setState({
        errorMessageSgHome: {
          ...this.state.errorMessageSgHome,
          errorMsgPostalCode: errorMessage.postalCode,
          errorMsgMobileNumber: errorMessage.mobileNumber,
        },
      });

      return isAllValidate;
    } else {
      return false;
    }
  }

  onPressSgHomeSignInButton = () => {
    if (this.isAllValidateSgHome()) {
      this.findByMobile();
    }
  };

  onChangeUserSignUpPO = (newUserInfo, newErrorMessage) => {
    const {userSignUpPO, errorMessageObj} = this.state;
    this.setState({
      userSignUpPO: {
        ...userSignUpPO,
        name: newUserInfo.name,
        email: newUserInfo.email,
        mobile: newUserInfo.mobile,
        passwordSignUp: newUserInfo.passwordSignUp,
      },
      errorMessageObj: {
        ...errorMessageObj,
        ...newErrorMessage,
      },
    });
  };

  //Validation multi check
  isAllInputsValidate() {
    const {name, email, mobile, passwordSignUp} = this.state.userSignUpPO;
    const {source} = this.props;

    if (!ObjectUtil.isEmpty(this.state.userSignUpPO)) {
      let isAllValidate = true;
      let errorMessage = {
        name: '',
        email: '',
        mobile: '',
        password: '',
      };

      if (ObjectUtil.isEmpty(name) || name === '') {
        isAllValidate = false;
        errorMessage.name = 'Required field';
      }

      if (ObjectUtil.isEmpty(email) || email === '') {
        isAllValidate = false;
        errorMessage.email = 'Required field';
      } else if (!StringUtil.validateEmailFormat(email)) {
        isAllValidate = false;
        errorMessage.email = 'Please enter a valid email';
      }

      if (source === SignInRegisterSource.XValueResult) {
        if (ObjectUtil.isEmpty(mobile) || mobile === '') {
          isAllValidate = false;
          errorMessage.mobile = 'Required field';
        } else if (mobile && mobile.length !== 8) {
          isAllValidate = false;
          errorMessage.mobile = 'Please enter a valid mobile number';
        }
      }

      if (ObjectUtil.isEmpty(passwordSignUp) || passwordSignUp === '') {
        isAllValidate = false;
        errorMessage.password = 'Required field';
      } else if (passwordSignUp.length < 6) {
        isAllValidate = false;
        errorMessage.password = 'minimum of 6 characters';
      }

      this.setState({
        errorMessageObj: {
          ...this.state.errorMessageObj,
          errMsgName: errorMessage.name,
          errMsgEmail: errorMessage.email,
          errMsgMobile: errorMessage.mobile,
          errMsgPasswordSignUp: errorMessage.password,
        },
      });

      return isAllValidate;
    } else {
      return false;
    }
  }

  //API called
  findByMobile() {
    const {mobileNumber, postalCode} = this.state.userLoginPOSgHome;
    const {updateUserPO, loginWithPtUserId} = this.props;

    LoginService.findByMobile({
      mobile: mobileNumber,
      postalCode,
    })
      .catch(error => {
        Alert.alert(AlertMessage.ErrorMessageTitle, error);
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            const {errorCode} = response;
            if (errorCode === 'Conflict') {
              this.showSgHomeMirtgatedAlert(error);
            } else {
              Alert.alert(AlertMessage.ErrorMessageTitle, error);
            }
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              const {user, passwordToken, result} = response;
              if (!ObjectUtil.isEmpty(user)) {
                if (!ObjectUtil.isEmpty(user.encryptedUserId)) {
                  //save firebase
                  analytics().setUserId(user.encryptedUserId);
                  analytics().setUserProperty('email', user.email);

                  //password token will be returned if user haven't updated password for mySGhome login
                  if (
                    !ObjectUtil.isEmpty(result) &&
                    !ObjectUtil.isEmpty(passwordToken)
                  ) {
                    loginWithPtUserId({
                      ptUserId: result.ptUserId,
                      email: user.email,
                      mobile: user.mobileLocalNum,
                    });

                    this.directToSGHomeUpdateDetails(
                      new SRXPropertyUserPO(result),
                      passwordToken,
                    );
                  }

                  //update userPO in redux
                  if (updateUserPO) {
                    updateUserPO(user);
                  }
                }
              }
            }
          }
        } else {
          Alert.alert(
            AlertMessage.ErrorMessageTitle,
            'An error occurred unexpectedly. Please try again later.',
          );
        }
      });
  }

  showSgHomeMirtgatedAlert(error) {
    Alert.alert(
      AlertMessage.SuccessMessageTitle,
      error,
      [
        {
          text: 'Forgot Password?',
          onPress: () => this.showForgotPasswordModal(),
        },
        {text: 'OK', onPress: () => this.onPressOK()},
      ],
      {cancelable: true},
    );
  }

  directToSGHomeUpdateDetails(srxPropertyUserPO, token) {
    //show sg home update detail for first time screen
    LoginStack.showSGHomeUpdateDetailsModal({srxPropertyUserPO, token});
  }

  onPressSingUpBtn = () => {
    if (this.isAllInputsValidate()) {
      const {name, email, mobile, passwordSignUp} = this.state.userSignUpPO;
      this.props.signup(name, email, mobile, passwordSignUp, true);
    }
  };

  onPressFacebookLogin = () => {
    const {source} = this.props;
    if (source === SignInRegisterSource.SplashScreen) {
      this.facebookLogin(this.props);
    } else {
      this.onCloseSignInRegisterModal().then(() => {
        this.facebookLogin(this.props);
      });
    }
  };

  facebookLogin = props => {
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
                  console.log(error);
                } else {
                  if (!ObjectUtil.isEmpty(result.email)) {
                    props.fblogin({
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

  //google sign in section
  onPressGoogleSignIn = () => {
    const {source} = this.props;
    if (source === SignInRegisterSource.SplashScreen) {
      this.performGoogleSignIn();
    } else {
      this.onCloseSignInRegisterModal().then(() => {
        this.performGoogleSignIn();
      });
    }
  };

  performGoogleSignIn = () => {
    const {performGoogleSignin} = this.props;
    GoogleSignin.hasPlayServices()
      .then(() => {
        GoogleSignin.signIn()
          .then(userInfo => {
            if (
              !ObjectUtil.isEmpty(userInfo) &&
              !ObjectUtil.isEmpty(userInfo.user)
            ) {
              performGoogleSignin({
                socialId: userInfo.user.id,
                socialIdToken: userInfo.idToken,
                name: userInfo.user.name,
                username: userInfo.user.email,
                photoUrl: userInfo.user.photo,
              });
            }
          })
          .catch(error => {
            console.log(error);
            Alert.alert(
              AlertMessage.ErrorMessageTitle,
              'An error occurred unexpectedly. Please try again later',
            );
          });
      })
      .catch(error => {
        console.log(error);
        if (error.code === statusCodes.PLAY_SERVICES_NOT_AVAILABLE) {
          Alert.alert(
            AlertMessage.ErrorMessageTitle,
            'An error occurred unexpectedly. Please try again later',
          );
        }
      });
  };

  onAppleButtonPress() {
    const {source} = this.props;
    if (source === SignInRegisterSource.SplashScreen) {
      this.signInWithApple(this.props);
    } else {
      this.onCloseSignInRegisterModal().then(() => {
        this.signInWithApple(this.props);
      });
    }
  }

  signInWithApple(props) {
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
          console.log(result);
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

            props.appleSignIn({
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

  onPressType = type => {
    this.setState({
      selectedSignIn: type == 'S' ? true : false,
    });
  };

  onClickTermsOfUse = () => {
    const {source} = this.props;
    if (source === SignInRegisterSource.SplashScreen) {
      this.onClickTermsOfUsefromIntro();
    } else {
      Navigation.push(this.props.componentId, {
        component: {
          name: 'LoginStack.generalWebScreen',
          passProps: {
            url: '/terms-of-use',
            screenTitle: 'Terms of Use',
          },
        },
      });
    }
  };

  onClickTermsOfUsefromIntro = () => {
    const {onClickTermsOfUsefromIntro} = this.props;
    if (onClickTermsOfUsefromIntro) {
      onClickTermsOfUsefromIntro();
    }
  };

  onClickPrivacyPolicy = () => {
    const {source} = this.props;
    if (source === SignInRegisterSource.SplashScreen) {
      this.onClickPrivacyPolicyfromIntro();
    } else {
      Navigation.push(this.props.componentId, {
        component: {
          name: 'LoginStack.generalWebScreen',
          passProps: {
            url: '/privacy-policy',
            screenTitle: 'Privacy Policy',
          },
        },
      });
    }
  };

  onClickPrivacyPolicyfromIntro = () => {
    const {onClickPrivacyPolicyfromIntro} = this.props;
    if (onClickPrivacyPolicyfromIntro) {
      onClickPrivacyPolicyfromIntro();
    }
  };

  renderHeader() {
    return (
      <View
        style={{
          paddingHorizontal: Spacing.M,
          marginTop: Spacing.S,
        }}>
        <View
          style={{
            flexDirection: 'row',
            justifyContent: 'flex-end',
          }}>
          <TouchableOpacity onPress={() => this.onCloseSignInRegisterModal()}>
            <FeatherIcon name="x" size={25} color={'black'} />
          </TouchableOpacity>
        </View>
        <View style={{alignItems: 'center'}}>
          <Image
            source={Splash_Screen_Content}
            resizeMode={'cover'}
            style={{width: 192, height: 152}}
          />
        </View>
      </View>
    );
  }

  renderOtherLoginTypesButton() {
    const {source} = this.props;
    return (
      <View
        style={{
          marginTop: source === SignInRegisterSource.SplashScreen ? 30 : null,
          paddingHorizontal: Spacing.M,
          paddingTop: Spacing.XS,
          paddingBottom: Spacing.L,
          alignItems: 'center',
        }}>
        <SmallBodyText>CONTINUE WITH</SmallBodyText>
        <View
          style={{
            marginTop: Spacing.M,
            flexDirection: 'row',
            justifyContent: 'center',
            alignItems: 'center',
          }}>
          {this.renderAppleSignInBtn()}
          {this.renderFacebookLogin()}
          {this.renderGoogleSignInBtn()}
        </View>
      </View>
    );
  }

  renderFacebookLogin() {
    return (
      <TouchableOpacity onPress={() => this.onPressFacebookLogin()}>
        <Image style={{width: 35, height: 35}} source={UserAuth_SmallIcon_FB} />
      </TouchableOpacity>
    );
  }

  renderAppleSignInBtn() {
    if (IS_IOS) {
      return (
        <TouchableOpacity
          style={{marginRight: Spacing.M}}
          onPress={() => this.onAppleButtonPress()}>
          <Image
            style={{width: 35, height: 35}}
            source={UserAuth_SmallIcon_Apple}
          />
        </TouchableOpacity>
      );
    }
  }

  renderGoogleSignInBtn() {
    return (
      <TouchableOpacity
        style={{marginLeft: Spacing.M}}
        onPress={() => this.onPressGoogleSignIn()}>
        <Image
          style={{width: 35, height: 35}}
          source={UserAuth_SmallIcon_Google}
        />
      </TouchableOpacity>
    );
  }

  renderContent() {
    const {selectedSignIn} = this.state;
    return (
      <View
        style={{
          paddingHorizontal: Spacing.M,
          marginVertical: Spacing.S,
        }}>
        <View style={{flexDirection: 'row', justifyContent: 'center'}}>
          <TouchableOpacity
            style={[
              Styles.signInRegisterBarStyle,
              selectedSignIn === true
                ? {borderBottomColor: SRXColor.Teal}
                : {borderBottomColor: SRXColor.LightGray},
            ]}
            onPress={() => this.onPressType('S')}>
            <Heading2
              style={
                selectedSignIn === true
                  ? {color: SRXColor.Teal}
                  : {color: SRXColor.Purple}
              }>
              Sign in
            </Heading2>
          </TouchableOpacity>
          <TouchableOpacity
            style={[
              Styles.signInRegisterBarStyle,
              selectedSignIn === false
                ? {borderBottomColor: SRXColor.Teal}
                : {borderBottomColor: SRXColor.LightGray},
            ]}
            onPress={() => this.onPressType('R')}>
            <Heading2
              style={
                selectedSignIn === false
                  ? {color: SRXColor.Teal}
                  : {color: SRXColor.Purple}
              }>
              Register
            </Heading2>
          </TouchableOpacity>
        </View>
        <View>
          {selectedSignIn === true
            ? this.renderSignIn()
            : this.renderRegister()}
        </View>
      </View>
    );
  }

  renderRegister() {
    const {source, onSuccessSignUp} = this.props;
    const {userSignUpPO, errorMessageObj} = this.state;
    return (
      <Register
        onClickTermsOfUse={this.onClickTermsOfUse}
        onClickPrivacyPolicy={this.onClickPrivacyPolicy}
        source={source}
        onSuccessSignUp={onSuccessSignUp}
        userSignUpPO={userSignUpPO}
        errorMessageObj={errorMessageObj}
        onChangeUserSignUpPO={this.onChangeUserSignUpPO}
        onPressSingUpBtn={this.onPressSingUpBtn}
      />
    );
  }

  renderSignIn() {
    const {source, onSuccessSignIn} = this.props;
    const {
      userLoginPO,
      errorMessage,
      userLoginPOSgHome,
      errorMessageSgHome,
    } = this.state;
    return (
      <SignInPage
        source={source}
        showForgotPasswordModal={this.showForgotPasswordModal}
        onPressOK={this.onPressOK}
        onSuccessSignIn={onSuccessSignIn}
        userLoginPO={userLoginPO}
        errorMessage={errorMessage}
        onChangeUserLoginPO={this.onChangeUserLoginPO}
        onPressSignInButton={this.onPressSignInButton}
        userLoginPOSgHome={userLoginPOSgHome}
        errorMessageSgHome={errorMessageSgHome}
        onChangeUserLoginPOSgHome={this.onChangeUserLoginPOSgHome}
        onPressSgHomeSignInButton={this.onPressSgHomeSignInButton}
      />
    );
  }

  renderSkip() {
    return (
      <TouchableOpacity
        style={{alignItems: 'center', marginTop: 40, marginBottom: Spacing.M}}
        onPress={() => this.closeIntro()}>
        <Heading2 style={{color: SRXColor.Gray}}>SKIP</Heading2>
      </TouchableOpacity>
    );
  }

  render() {
    const {source} = this.props;
    const isSplashScreen = source === SignInRegisterSource.SplashScreen;
    if (isSplashScreen) {
      return (
        <KeyboardAwareScrollView bounces={false}>
          {this.renderOtherLoginTypesButton()}
          {this.renderContent()}
          {this.renderSkip()}
        </KeyboardAwareScrollView>
      );
    } else {
      return (
        <SafeAreaView style={{flex: 1}}>
          <KeyboardAwareScrollView bounces={false}>
            {this.renderHeader()}
            {this.renderOtherLoginTypesButton()}
            {this.renderContent()}
          </KeyboardAwareScrollView>
        </SafeAreaView>
      );
    }
  }
}

const Styles = {
  facebookContainer: {
    marginTop: 10,
    borderRadius: isIOS ? 20 : 22.5,
    borderColor: SRXColor.Teal,
    borderWidth: 1,
    alignItems: 'center',
    height: isIOS ? 40 : 45,
    flexDirection: 'row',
    justifyContent: 'center',
  },
  signInRegisterBarStyle: {
    borderBottomWidth: 2,
    flex: 1,
    alignItems: 'center',
    paddingBottom: Spacing.S,
  },
};

SignInRegister.Sources = SignInRegisterSource;

const mapStateToProps = state => {
  return {
    errorMessage: state.loginData.errorMessage,
    userPO: state.loginData.userPO,
  };
};

export default connect(
  mapStateToProps,
  {
    fblogin,
    login,
    updateUserPO,
    signup,
    appleSignIn,
    performGoogleSignin,
    loginWithPtUserId,
  },
)(SignInRegister);
