import React, {Component} from 'react';
import {
  View,
  TouchableOpacity,
  Platform,
  TouchableWithoutFeedback,
} from 'react-native';
import {connect} from 'react-redux';

import {SRXColor} from '../../constants';
import {
  Button,
  FeatherIcon,
  BodyText,
  TextInput,
  Heading1,
} from '../../components';
import {Spacing} from '../../styles';
import {ObjectUtil} from '../../utils';
import PropTypes from 'prop-types';
import {Styles} from './Styles';

const isIOS = Platform.OS === 'ios';

class SignInPage extends Component {
  static options(passProps) {
    return {
      layout: {
        backgroundColor: SRXColor.Transparent,
      },
    };
  }

  state = {
    isShowPwd: false,
    selectedEmailSection:
      this.props.userLoginPOSgHome.postalCode !== '' ||
      this.props.userLoginPOSgHome.mobileNumber !== ''
        ? false
        : true,
  };

  static propTypes = {
    onSuccessSignIn: PropTypes.func,
  };

  constructor(props) {
    super(props);
    this.toggleShowHidePassword = this.toggleShowHidePassword.bind(this);
    this.onPressSignInButton = this.onPressSignInButton.bind(this);
    this.onPressSgHomeSignInButton = this.onPressSgHomeSignInButton.bind(this);

    this.showForgotPasswordModal = this.showForgotPasswordModal.bind(this);
  }

  componentDidMount() {
    console.log(this.props.userPO);
  }

  componentDidUpdate(prevProps) {
    const {onSuccessSignIn} = this.props;
    if (
      prevProps.userPO !== this.props.userPO &&
      !ObjectUtil.isEmpty(this.props.userPO)
    ) {
      if (onSuccessSignIn) {
        onSuccessSignIn();
      }
    }
  }

  onPressType = type => {
    this.setState({
      selectedEmailSection: type == 'E' ? true : false,
    });
  };

  toggleShowHidePassword() {
    const {isShowPwd} = this.state;
    if (isShowPwd) {
      this.setState({isShowPwd: false});
    } else {
      this.setState({isShowPwd: true});
    }
  }

  showForgotPasswordModal = () => {
    const {showForgotPasswordModal} = this.props;
    if (showForgotPasswordModal) {
      showForgotPasswordModal();
    }
  };

  onPressSignInButton = () => {
    const {onPressSignInButton} = this.props;
    if (onPressSignInButton) {
      onPressSignInButton();
    }
  };

  onChangeUserName = username => {
    const {userLoginPO, errorMessage, onChangeUserLoginPO} = this.props;

    const newUserInfo = {
      ...userLoginPO,
      username,
    };

    const newErrorMessage = {
      ...errorMessage,
      errMsgUserName: '',
    };

    if (onChangeUserLoginPO) {
      onChangeUserLoginPO(newUserInfo, newErrorMessage);
    }
  };

  onChangePassword = password => {
    const {userLoginPO, errorMessage, onChangeUserLoginPO} = this.props;

    const newUserInfo = {
      ...userLoginPO,
      password,
    };

    const newErrorMessage = {
      ...errorMessage,
      errMsgPassword: '',
    };

    if (onChangeUserLoginPO) {
      onChangeUserLoginPO(newUserInfo, newErrorMessage);
    }
  };

  onChangePostalCode = postalCode => {
    const {
      userLoginPOSgHome,
      errorMessageSgHome,
      onChangeUserLoginPOSgHome,
    } = this.props;

    const newUserInfo = {
      ...userLoginPOSgHome,
      postalCode,
    };

    const newErrorMessage = {
      ...errorMessageSgHome,
      errorMsgPostalCode: '',
    };

    if (onChangeUserLoginPOSgHome) {
      onChangeUserLoginPOSgHome(newUserInfo, newErrorMessage);
    }
  };

  onChangeMobileNumber = mobileNumber => {
    const {
      userLoginPOSgHome,
      errorMessageSgHome,
      onChangeUserLoginPOSgHome,
    } = this.props;

    const newUserInfo = {
      ...userLoginPOSgHome,
      mobileNumber,
    };

    const newErrorMessage = {
      ...errorMessageSgHome,
      errorMsgMobileNumber: '',
    };

    if (onChangeUserLoginPOSgHome) {
      onChangeUserLoginPOSgHome(newUserInfo, newErrorMessage);
    }
  };

  onPressSgHomeSignInButton = () => {
    const {onPressSgHomeSignInButton} = this.props;
    if (onPressSgHomeSignInButton) {
      onPressSgHomeSignInButton();
    }
  };

  renderEmailAndPasswordClose() {
    return (
      <TouchableWithoutFeedback
        style={Styles.continer}
        onPress={() => this.onPressType('E')}>
        <View style={Styles.itemContainer}>
          <View
            style={{
              flex: 1,
              flexDirection: 'row',
              justifyContent: 'space-between',
            }}>
            <Heading1>Email and password</Heading1>
            <FeatherIcon name="chevron-down" size={23} color={SRXColor.Black} />
          </View>
        </View>
      </TouchableWithoutFeedback>
    );
  }

  renderEmailAndPasswordOpen() {
    return (
      <TouchableWithoutFeedback
        style={Styles.continer}
        onPress={() => this.onPressType('E')}>
        <View style={Styles.itemContainer}>
          <View
            style={{
              flex: 1,
              flexDirection: 'row',
              justifyContent: 'space-between',
            }}>
            <Heading1>Email and password</Heading1>
            <FeatherIcon name="chevron-up" size={23} color={SRXColor.Black} />
          </View>
          {this.renderAccountDetailsForm()}
        </View>
      </TouchableWithoutFeedback>
    );
  }

  // SRX SignIn Section
  renderAccountDetailsForm() {
    const {isShowPwd} = this.state;
    const {username, password} = this.props.userLoginPO;
    const {errMsgUserName, errMsgPassword} = this.props.errorMessage;
    return (
      <View
        style={{
          width: '100%',
          marginVertical: Spacing.S,
        }}>
        {/* User Name */}
        <View style={{paddingBottom: Spacing.M}}>
          <TextInput
            autoCorrect={false}
            placeholder={'Email'}
            defaultValue={username}
            error={errMsgUserName}
            onChangeText={newName => this.onChangeUserName(newName)}
          />
        </View>

        {/* Password */}
        <View style={{paddingBottom: Spacing.XS}}>
          <TextInput
            placeholder={'Password'}
            error={errMsgPassword}
            secureTextEntry={isShowPwd ? false : true}
            defaultValue={password}
            onChangeText={newPassword => this.onChangePassword(newPassword)}
            rightView={
              <TouchableOpacity
                onPress={this.toggleShowHidePassword}
                style={{alignItems: 'center', justifyContent: 'center'}}>
                <FeatherIcon
                  name={isShowPwd ? 'eye' : 'eye-off'}
                  size={25}
                  color={'#858585'}
                />
              </TouchableOpacity>
            }
          />
        </View>

        {/* Forgot password */}
        <View
          style={{
            alignItems: 'flex-end',
            justifyContent: 'flex-end',
          }}>
          <Button
            textStyle={{
              fontSize: 14,
              fontWeight: '400',
              color: SRXColor.TextLink,
            }}
            onPress={() => this.showForgotPasswordModal()}>
            Forgot password?
          </Button>
        </View>

        {/* SignIn Action Button */}
        <View style={{alignItems: 'center', justifyContent: 'center'}}>
          <Button
            buttonStyle={{
              width: '100%',
              alignItems: 'center',
              justifyContent: 'center',
              marginTop: Spacing.M,
            }}
            buttonType={Button.buttonTypes.primary}
            onPress={() => this.onPressSignInButton()}>
            Sign in
          </Button>
        </View>

        <View
          style={{
            flexDirection: 'row',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        />
      </View>
    );
  }

  renderPCodeMobileNoClose() {
    return (
      <View style={{marginTop: Spacing.M}}>
        <TouchableWithoutFeedback
          style={Styles.continer}
          onPress={() => this.onPressType('P')}>
          <View style={Styles.itemContainer}>
            <View
              style={{
                flex: 1,
                flexDirection: 'row',
                justifyContent: 'space-between',
              }}>
              <Heading1>Postal code and mobile number</Heading1>
              <FeatherIcon
                name="chevron-down"
                size={23}
                color={SRXColor.Black}
              />
            </View>
          </View>
        </TouchableWithoutFeedback>
      </View>
    );
  }

  renderPCodeMobileNoOpen() {
    return (
      <View style={{marginTop: Spacing.M}}>
        <TouchableWithoutFeedback
          style={Styles.continer}
          onPress={() => this.onPressType('P')}>
          <View style={Styles.itemContainer}>
            <View
              style={{
                flex: 1,
                flexDirection: 'row',
                justifyContent: 'space-between',
              }}>
              <Heading1>Postal code and mobile number</Heading1>
              <FeatherIcon name="chevron-up" size={23} color={SRXColor.Black} />
            </View>
            {this.renderSgHomeAccountDetailsForm()}
          </View>
        </TouchableWithoutFeedback>
      </View>
    );
  }

  renderSgHomeAccountDetailsForm() {
    const {postalCode, mobileNumber} = this.props.userLoginPOSgHome;
    const {
      errorMsgPostalCode,
      errorMsgMobileNumber,
    } = this.props.errorMessageSgHome;

    return (
      <View>
        {/* Postal Code */}
        <View style={{marginVertical: Spacing.M}}>
          <TextInput
            placeholder={'Postal code'}
            defaultValue={postalCode}
            keyboardType={isIOS ? 'number-pad' : 'numeric'}
            error={errorMsgPostalCode}
            onChangeText={newPostalCode =>
              this.onChangePostalCode(newPostalCode)
            }
          />
        </View>

        {/* Mobile Number */}
        <View style={{marginBottom: Spacing.M}}>
          <TextInput
            leftView={<BodyText style={{marginLeft: Spacing.XS}}>+65</BodyText>}
            placeholder={'Mobile number'}
            defaultValue={mobileNumber}
            keyboardType={isIOS ? 'number-pad' : 'numeric'}
            error={errorMsgMobileNumber}
            onChangeText={newMobileNumber =>
              this.onChangeMobileNumber(newMobileNumber)
            }
          />
        </View>

        {/* SignIn Action Button */}
        <View style={{alignItems: 'center', justifyContent: 'center', flex: 1}}>
          <Button
            buttonStyle={{
              width: '100%',
              alignItems: 'center',
              justifyContent: 'center',
            }}
            buttonType={Button.buttonTypes.primary}
            onPress={() => this.onPressSgHomeSignInButton()}>
            Sign in
          </Button>
        </View>
      </View>
    );
  }

  render() {
    const {selectedEmailSection} = this.state;
    return (
      <View style={{marginTop: Spacing.L}}>
        {selectedEmailSection === true
          ? this.renderEmailAndPasswordOpen()
          : this.renderEmailAndPasswordClose()}

        {selectedEmailSection === true
          ? this.renderPCodeMobileNoClose()
          : this.renderPCodeMobileNoOpen()}
      </View>
    );
  }
}

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
  };
};

export default connect(mapStateToProps)(SignInPage);
