import React, {Component} from 'react';
import {connect} from 'react-redux';
import {View, Alert, Platform} from 'react-native';
import analytics from '@react-native-firebase/analytics';
import {Navigation} from 'react-native-navigation';
import SafeAreaView from 'react-native-safe-area-view';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';

import {Styles} from './Styles';
import {SRXColor, IS_IOS, AlertMessage} from '../../constants';
import {
  Button,
  FeatherIcon,
  TextInput,
  LargeTitleComponent,
  BodyText,
} from '../../components';
import {ObjectUtil, CommonUtil} from '../../utils';
import {Spacing} from '../../styles';
import {updateUserPO} from '../../actions';
import {LoginService} from '../../services';
import {LoginStack} from '../../config';
import {SRXPropertyUserPO} from '../../dataObject';
import {ForgotPassword} from './ForgotPassword';
import PropTypes from 'prop-types';

const isIOS = Platform.OS === 'ios';

class SgHomeSignInScreen extends LargeTitleComponent {
  state = {
    userLoginPO: {
      postalCode: '',
      mobileNumber: '',
      errorMsgPostalCode: '',
      errorMsgMobileNumber: '',
    },
    clickSignUp: false,
  };

  static propTypes = {
    onclickSignUp: PropTypes.func,
  };

  constructor(props) {
    super(props);

    //Set options here
    this.setupTopBar();
    Navigation.events().bindComponent(this);

    this.onPressCloseModal = this.onPressCloseModal.bind(this);
    this.onPressSignUp = this.onPressSignUp.bind(this);
    this.onPressNeedHelp = this.onPressNeedHelp.bind(this);
    this.onPressSignInButton = this.onPressSignInButton.bind(this);
    this.directToSGHomeUpdateDetails = this.directToSGHomeUpdateDetails.bind(
      this,
    );

    this.showForgotPasswordModal = this.showForgotPasswordModal.bind(this);
  }

  componentDidUpdate(prevProps) {
    if (this.props.userPO !== prevProps.userPO) {
      this.onPressCloseModal();
    }
  }

  componentWillUnmount() {
    const {onclickSignUp} = this.props;
    const {clickSignUp} = this.state;
    if (onclickSignUp && clickSignUp) {
      onclickSignUp();
    }
  }

  setupTopBar() {
    FeatherIcon.getImageSource('x', 25, 'black').then(icon_close => {
      Navigation.mergeOptions(this.props.componentId, {
        topBar: {
          leftButtons: [
            {
              id: 'signin_close',
              icon: icon_close,
            },
          ],
        },
      });
    });
  }

  navigationButtonPressed({buttonId}) {
    if (buttonId === 'signin_close') {
      this.onPressCloseModal();
    }
  }

  //Action method lists
  onPressSignInButton() {
    if (this.isAllValidate()) {
      this.findByMobile();
    }
  }

  showForgotPasswordModal() {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'LoginStack.forgotPassword',
        passProps: {
          source: ForgotPassword.Sources.SgHomeSignIn,
        },
      },
    });
  }

  //API called
  findByMobile() {
    const {mobileNumber, postalCode} = this.state.userLoginPO;
    const {updateUserPO} = this.props;

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

  onPressCloseModal() {
    return Navigation.dismissModal(this.props.componentId);
  }

  onPressSignUp() {
    this.setState({clickSignUp: true}, () => {
      Navigation.dismissModal(this.props.componentId);
    });
  }

  onPressNeedHelp() {
    //open need help screen
    Navigation.push(this.props.componentId, {
      component: {
        name: 'SideMainMenu.NeedHelp',
      },
    });
  }

  onPressOK() {
    if (Platform.OS === 'ios') {
      Navigation.dismissModal(this.props.componentId);
    } else {
      Navigation.dismissModal(this.props.componentId);
      LoginStack.showSignInRegisterModal();
    }
  }

  directToSGHomeUpdateDetails(srxPropertyUserPO, token) {
    //show sg home update detail for first time screen
    LoginStack.showSGHomeUpdateDetailsModal({srxPropertyUserPO, token});
  }

  isAllValidate() {
    const {userLoginPO} = this.state;

    if (!ObjectUtil.isEmpty(userLoginPO)) {
      //get value from userLoginPO
      const {postalCode, mobileNumber} = userLoginPO;
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
        userLoginPO: {
          ...this.state.userLoginPO,
          errorMsgPostalCode: errorMessage.postalCode,
          errorMsgMobileNumber: errorMessage.mobileNumber,
        },
      });

      return isAllValidate;
    } else {
      return false;
    }
  }

  //start Rendering methods
  renderAccountDetailsForm() {
    //get user login PO state variables
    const {
      postalCode,
      mobileNumber,
      errorMsgPostalCode,
      errorMsgMobileNumber,
    } = this.state.userLoginPO;

    return (
      <View style={Styles.accountDetailsContainer}>
        {/* Postal Code */}
        <View style={Styles.inputContainer}>
          <TextInput
            placeholder={'Postal code'}
            defaultValue={postalCode}
            keyboardType={IS_IOS ? 'number-pad' : 'numeric'}
            error={errorMsgPostalCode}
            onChangeText={newPostalCode =>
              this.setState({
                userLoginPO: {
                  ...this.state.userLoginPO,
                  postalCode: newPostalCode,
                  errorMsgPostalCode: '',
                },
              })
            }
          />
        </View>

        {/* Mobile Number */}
        <View style={[Styles.inputContainer, {marginTop: Spacing.M}]}>
          <TextInput
            leftView={<BodyText style={{marginLeft: Spacing.XS}}>+65</BodyText>}
            placeholder={'Mobile number'}
            defaultValue={mobileNumber}
            keyboardType={IS_IOS ? 'number-pad' : 'numeric'}
            error={errorMsgMobileNumber}
            onChangeText={newMobileNumber =>
              this.setState({
                userLoginPO: {
                  ...this.state.userLoginPO,
                  mobileNumber: newMobileNumber,
                  errorMsgMobileNumber: '',
                },
              })
            }
          />
        </View>

        {/* SignIn Action Button */}
        <View style={{alignItems: 'center', justifyContent: 'center', flex: 1}}>
          <Button
            buttonStyle={{marginTop: 24, marginBottom: 5}}
            buttonType={Button.buttonTypes.primary}
            onPress={this.onPressSignInButton}>
            Sign in
          </Button>
        </View>

        {this.renderSignUp()}
        {this.renderNeedHelp()}
      </View>
    );
  }

  renderSignUp() {
    return (
      <View
        style={{
          flex: 1,
          marginTop: Spacing.L,
          alignItems: 'center',
          justifyContent: 'center',
        }}>
        <Button
          textStyle={{
            fontSize: 14,
            fontWeight: '400',
            color: SRXColor.TextLink,
          }}
          onPress={this.onPressSignUp}>
          Sign up
        </Button>
      </View>
    );
  }

  renderNeedHelp() {
    return (
      <View
        style={{
          flex: 1,
          marginTop: Spacing.L,
          alignItems: 'center',
          justifyContent: 'center',
        }}>
        <Button
          textStyle={{
            fontSize: 14,
            fontWeight: '400',
            color: SRXColor.TextLink,
          }}
          onPress={this.onPressNeedHelp}>
          Need help?
        </Button>
      </View>
    );
  }

  render() {
    return (
      <SafeAreaView style={{flex: 1, backgroundColor: SRXColor.White}}>
        <KeyboardAwareScrollView
          onScroll={this.onScroll}
          style={{flex: 1}}
          bounces={false}
          keyboardShouldPersistTaps={'always'}>
          {/* title for sign in screen */}
          {this.renderLargeTitle('Sign In')}
          {this.renderAccountDetailsForm()}
        </KeyboardAwareScrollView>
      </SafeAreaView>
    );
  }
}

const mapStateToProps = state => {
  return {
    errorMessage: state.loginData.errorMessage,
    userPO: state.loginData.userPO,
  };
};

export default connect(
  mapStateToProps,
  {updateUserPO},
)(SgHomeSignInScreen);
