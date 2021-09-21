import React, {Component} from 'react';
import {View, Text, Alert, Platform} from 'react-native';
import SafeAreaView from 'react-native-safe-area-view';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
import {Navigation} from 'react-native-navigation';
import PropTypes from 'prop-types';
import {LoginStack} from '../../config';

//Custom Components
import {
  Button,
  TextInput,
  LargeTitleComponent,
  FeatherIcon,
} from '../../components';
import {Styles} from './Styles';
import {ObjectUtil, CommonUtil} from '../../utils';
import {LoginService} from '../../services';
import {Spacing, Typography} from '../../styles';
import {SRXColor, AppConstant, AlertMessage} from '../../constants';

const ForgotPasswordSource = {
  SignIn: 'SignIn',
  SgHomeSignIn: 'SgHomeSignIn',
};

const isIOS = Platform.OS === 'ios';

class ForgotPassword extends LargeTitleComponent {
  state = {
    registeredEmail: '',
    errorMessageEmail: '',
    pressedSignIn: false,
  };

  constructor(props) {
    super(props);

    this.setupTopBar();
    Navigation.events().bindComponent(this);

    this.onPressResetPassword = this.onPressResetPassword.bind(this);
    this.onBackSignIn = this.onBackSignIn.bind(this);
  }

  componentWillUnmount() {
    const {pressedSignIn} = this.state;
    if (pressedSignIn) {
      LoginStack.showSignInRegisterModal();
    }
  }

  //set up top bar
  setupTopBar() {
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

  //top bar button pressed
  navigationButtonPressed({buttonId}) {
    if (buttonId === 'close_btn') {
      //close forgot password screen
      Navigation.dismissModal(this.props.componentId);
    }
  }

  onPressResetPassword() {
    if (this.isValidate()) {
      this.resetPassword();
    }
  }

  isValidate() {
    if (
      ObjectUtil.isEmpty(this.state.registeredEmail) ||
      this.state.registeredEmail === ''
    ) {
      this.setState({errorMessageEmail: 'Required field'});
      return false;
    }
    return true;
  }

  resetPassword() {
    if (this.isValidate()) {
      this.forgotPassword();
    }
  }

  onBackSignIn() {
    //go back to normal sign in screen
    this.setState({pressedSignIn: true}, () =>
      Navigation.dismissModal(this.props.componentId),
    );
  }

  //API method lists
  forgotPassword() {
    const {registeredEmail} = this.state;
    LoginService.forgotPassword({
      email: registeredEmail,
    })
      .catch(error => {
        Alert.alert(AlertMessage.ErrorMessageTitle, error);
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            Alert.alert(AlertMessage.ErrorMessageTitle, error);
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              const {success, status} = response;
              let successMsg =
                'A link for resetting password has been sent. Please check your email.';
              if (!ObjectUtil.isEmpty(success)) {
                successMsg = success;
              }
              if (status === 'y') {
                Alert.alert(AlertMessage.SuccessMessageTitle, successMsg, [
                  {
                    text: 'OK',
                    onPress: this.onBackSignIn,
                  },
                ]);
              } else {
                this.onBackSignIn();
              }
            } //end of response
          }
        } else {
          Alert.alert(
            AlertMessage.ErrorMessageTitle,
            'An error occurred unexpectedly. Please try again later.',
          );
        }
      });
  }

  renderForgorPasswordForm() {
    return (
      <View style={Styles.accountDetailsContainer}>
        <View style={Styles.inputContainer}>
          <TextInput
            placeholder={'E-mail address'}
            error={this.state.errorMessageEmail}
            onChangeText={newEmail =>
              this.setState({
                registeredEmail: newEmail,
                errorMessageEmail: '',
              })
            }
            autoCorrect={false}
          />
        </View>

        <Text style={Typography.Subtext}>
          Enter your registered email below and we will send you instructions
          for setting a new password.
        </Text>

        <View style={{alignItems: 'center', justifyContent: 'center', flex: 1}}>
          <Button
            buttonType={Button.buttonTypes.primary}
            buttonStyle={{marginTop: 24, marginBottom: 5}}
            onPress={this.onPressResetPassword}>
            Submit
          </Button>

          <Button
            buttonStyle={{flex: 1, marginTop: Spacing.M}}
            textStyle={{
              fontSize: 14,
              fontWeight: '400',
              color: SRXColor.TextLink,
            }}
            onPress={this.onBackSignIn}>
            Return to Sign in
          </Button>
        </View>
      </View>
    );
  }

  render() {
    return (
      <SafeAreaView style={{flex: 1, backgroundColor: 'white'}}>
        <KeyboardAwareScrollView
          onScroll={this.onScroll}
          style={{flex: 1}}
          bounces={false}
          keyboardShouldPersistTaps={'always'}>
          {/* title for forgot password screen */}
          {this.renderLargeTitle('Forgot Password')}
          {this.renderForgorPasswordForm()}
        </KeyboardAwareScrollView>
      </SafeAreaView>
    );
  }
}

ForgotPassword.Sources = ForgotPasswordSource;

ForgotPassword.propTypes = {
  source: PropTypes.oneOf(Object.keys(ForgotPasswordSource)),
};

export {ForgotPassword};
