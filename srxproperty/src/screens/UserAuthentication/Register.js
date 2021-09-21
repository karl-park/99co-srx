import React, {Component} from 'react';
import {View} from 'react-native';
import {connect} from 'react-redux';

import {Styles} from './Styles';
import {Button, FeatherIcon, TextInput, SmallBodyText} from '../../components';
import {SRXColor, IS_IOS} from '../../constants';
import {ObjectUtil} from '../../utils';
import {Spacing} from '../../styles';
import {SignInRegisterSource} from '../UserAuthentication/Constants/UserAuthenticationConstants';

class Register extends Component {
  state = {
    isShowPwd: false,
  };

  constructor(props) {
    super(props);
    //Navigation.events().bindComponent(this);
    this.toggleShowHidePassword = this.toggleShowHidePassword.bind(this);
    this.onPressSingUpBtn = this.onPressSingUpBtn.bind(this);
  }

  componentDidUpdate(prevProps) {
    const {onSuccessSignUp} = this.props;
    if (
      prevProps.userPO != this.props.userPO &&
      !ObjectUtil.isEmpty(this.props.userPO)
    ) {
      if (onSuccessSignUp) {
        onSuccessSignUp();
      }
    }
  }

  // componentWillUnmount() {
  //   const { onclickSignIn, onPressSignIn } = this.props;
  //   const { clickSignIn } = this.state;

  //   //onclickSignIn => from Intro.js (Splash Screens)
  //   if (onclickSignIn && clickSignIn) {
  //     onclickSignIn();
  //   }

  //   //onPressSignIn => from ChatHomeScreen
  //   if (onPressSignIn && clickSignIn) {
  //     onPressSignIn();
  //   }
  // }

  onChangeUserName = name => {
    const {userSignUpPO, errorMessageObj, onChangeUserSignUpPO} = this.props;

    const newUserInfo = {
      ...userSignUpPO,
      name,
    };

    const newErrorMessage = {
      ...errorMessageObj,
      errMsgName: '',
    };

    if (onChangeUserSignUpPO) {
      onChangeUserSignUpPO(newUserInfo, newErrorMessage);
    }
  };

  onChangeUserEmail = email => {
    const {userSignUpPO, errorMessageObj, onChangeUserSignUpPO} = this.props;

    const newUserInfo = {
      ...userSignUpPO,
      email,
    };

    const newErrorMessage = {
      ...errorMessageObj,
      errMsgEmail: '',
    };

    if (onChangeUserSignUpPO) {
      onChangeUserSignUpPO(newUserInfo, newErrorMessage);
    }
  };

  onChangeMobileNumber = mobile => {
    const {userSignUpPO, errorMessageObj, onChangeUserSignUpPO} = this.props;

    const newUserInfo = {
      ...userSignUpPO,
      mobile,
    };

    const newErrorMessage = {
      ...errorMessageObj,
      errMsgMobile: '',
    };

    if (onChangeUserSignUpPO) {
      onChangeUserSignUpPO(newUserInfo, newErrorMessage);
    }
  };

  onChangeUserPassword = passwordSignUp => {
    const {userSignUpPO, errorMessageObj, onChangeUserSignUpPO} = this.props;

    const newUserInfo = {
      ...userSignUpPO,
      passwordSignUp,
    };

    const newErrorMessage = {
      ...errorMessageObj,
      errMsgPasswordSignUp: '',
    };

    if (onChangeUserSignUpPO) {
      onChangeUserSignUpPO(newUserInfo, newErrorMessage);
    }
  };

  onPressSingUpBtn = () => {
    const {onPressSingUpBtn} = this.props;
    if (onPressSingUpBtn) {
      onPressSingUpBtn();
    }
  };

  onClickTermsOfUse = () => {
    const {onClickTermsOfUse} = this.props;
    if (onClickTermsOfUse) {
      onClickTermsOfUse();
    }
  };

  onClickPrivacyPolicy = () => {
    const {onClickPrivacyPolicy} = this.props;
    if (onClickPrivacyPolicy) {
      onClickPrivacyPolicy();
    }
  };

  toggleShowHidePassword() {
    const {isShowPwd} = this.state;
    if (isShowPwd) {
      this.setState({isShowPwd: false});
    } else {
      this.setState({isShowPwd: true});
    }
  }

  renderSignUpContent() {
    return <View>{this.renderSignUpForm()}</View>;
  }

  // SRX SignUp Section
  renderSignUpForm() {
    const {isShowPwd} = this.state;
    const {name, email, mobile, passwordSignUp} = this.props.userSignUpPO;
    const {
      errMsgName,
      errMsgEmail,
      errMsgMobile,
      errMsgPasswordSignUp,
    } = this.props.errorMessageObj;
    const {source} = this.props;
    return (
      <View>
        {/* User Name */}
        <View style={{paddingBottom: Spacing.M}}>
          <TextInput
            placeholder={'Name'}
            error={errMsgName}
            defaultValue={name}
            onChangeText={newUserName => this.onChangeUserName(newUserName)}
          />
        </View>

        {/*Email */}
        <View style={{paddingBottom: Spacing.M}}>
          <TextInput
            placeholder={'Email'}
            error={errMsgEmail}
            defaultValue={email}
            onChangeText={newUserEmail => this.onChangeUserEmail(newUserEmail)}
          />
        </View>

        {source == SignInRegisterSource.XValueResult ? (
          <View style={{paddingBottom: Spacing.M}}>
            <TextInput
              placeholder={'Mobile number'}
              keyboardType={IS_IOS ? 'number-pad' : 'numeric'}
              error={errMsgMobile}
              defaultValue={mobile}
              onChangeText={newMobileNumber =>
                this.onChangeMobileNumber(newMobileNumber)
              }
            />
          </View>
        ) : null}

        {/*Password */}
        <View style={{paddingBottom: Spacing.M}}>
          <TextInput
            placeholder={'Password'}
            error={errMsgPasswordSignUp}
            defaultValue={passwordSignUp}
            secureTextEntry={isShowPwd ? false : true}
            onChangeText={newPassword => this.onChangeUserPassword(newPassword)}
            rightView={
              <Button
                buttonStyle={{alignItems: 'center', justifyContent: 'center'}}
                onPress={this.toggleShowHidePassword}
                leftView={
                  isShowPwd ? (
                    <FeatherIcon name="eye" size={25} color={'#858585'} />
                  ) : (
                    <FeatherIcon name="eye-off" size={25} color={'#858585'} />
                  )
                }
              />
            }
          />
        </View>

        {this.renderRegistrationAgreement()}

        <Button
          buttonStyle={{
            width: '100%',
            marginTop: Spacing.L,
            marginBottom: Spacing.M,
            alignItems: 'center',
            justifyContent: 'center',
          }}
          buttonType={Button.buttonTypes.primary}
          onPress={() => this.onPressSingUpBtn()}>
          Register
        </Button>
      </View>
    );
  }

  renderRegistrationAgreement() {
    return (
      <View style={Styles.pdpaContainerStyle}>
        <Button
          textStyle={{color: SRXColor.Gray, marginLeft: 3, flex: 1}}
          rightView={
            <View style={Styles.registrationAgreementStyle}>
              <SmallBodyText style={{color: SRXColor.Gray}}>
                By tapping Register, I agree to{' '}
                <SmallBodyText
                  onPress={() => this.onClickTermsOfUse()}
                  style={{color: SRXColor.TextLink, marginRight: 3}}>
                  SRX Terms of Use
                </SmallBodyText>{' '}
                &{' '}
                <SmallBodyText
                  onPress={() => this.onClickPrivacyPolicy()}
                  style={{color: SRXColor.TextLink, marginRight: 3}}>
                  Privacy Policy
                </SmallBodyText>
              </SmallBodyText>
            </View>
          }
        />
      </View>
    );
  }

  render() {
    return (
      <View style={{marginTop: Spacing.L}}>{this.renderSignUpContent()}</View>
    );
  }
}

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
  };
};

export default connect(mapStateToProps)(Register);
