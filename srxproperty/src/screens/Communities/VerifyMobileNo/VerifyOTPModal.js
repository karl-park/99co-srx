import React, {Component} from 'react';
import {View, Alert, KeyboardAvoidingView} from 'react-native';
import SafeAreaView from 'react-native-safe-area-view';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
import {Navigation} from 'react-native-navigation';
import {connect} from 'react-redux';
import PropTypes from 'prop-types';

import {Heading2, TextInput, Button, SmallBodyText} from '../../../components';
import {Spacing, Typography} from '../../../styles';
import {SRXColor, AlertMessage, IS_IOS} from '../../../constants';
import {LoginService} from '../../../services';
import {ObjectUtil, CommonUtil} from '../../../utils';
import {updateUserPO} from '../../../actions';
import {SmsListenerAndroid} from '../../../listener';

class VerifyOTPModal extends Component {
  static options(passProps) {
    return {
      layout: {
        backgroundColor: SRXColor.Transparent,
      },
    };
  }

  static propTypes = {
    mobile: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
  };

  static defaultProps = {
    mobile: '',
  };

  state = {
    verificationCode: '',
    timerCount: 180000, //3 minutes
  };

  constructor(props) {
    super(props);
    this.interval = null;

    this.onResendOTP = this.onResendOTP.bind(this);
    this.decreaseCounter = this.decreaseCounter.bind(this);
  }

  componentDidMount() {
    //Grapping verfication code from sms for android
    if (!IS_IOS) {
      SmsListenerAndroid.setSmsReceivedListener(sms => {
        if (!ObjectUtil.isEmpty(sms)) {
          let verificationCodeRegex = /([\d]{6})/;
          if (verificationCodeRegex.test(sms.message)) {
            let verificationCode = sms.message.match(verificationCodeRegex)[1];
            this.setState({verificationCode});
          }
        }
      });
    }

    this.startCounter();
  }

  componentWillUnmount() {
    //Remove all listeners
    if (!IS_IOS) {
      SmsListenerAndroid.clearSmsReceivedListener(message => {
        console.log('Successfully remove sms listener' + message);
      });
    }
  }

  //Timer functions
  startCounter() {
    this.interval = setInterval(this.decreaseCounter, 1000);
  }

  decreaseCounter() {
    const {timerCount} = this.state;
    if (timerCount == 0) {
      this.setState({timerCount});
      return clearInterval(this.interval);
    } else {
      this.setState({timerCount: timerCount - 1000});
    }
  }

  onCloseModal = () => {
    return Navigation.dismissModal(this.props.componentId);
  };

  onOTPFilled(code) {
    this.setState({
      verificationCode: code,
    });
  }

  onPressVerifyButton = () => {
    const {verificationCode} = this.state;
    const {mobile, userPO, updateUserPO} = this.props;
    LoginService.verifyMobile({
      verificationCode,
      countryCode: '+65',
      mobile,
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
              if (response.result == 'success') {
                var newUserPO = {
                  ...userPO,
                  mobileLocalNum: parseInt(mobile),
                  mobileVerified: true,
                };

                if (updateUserPO) {
                  updateUserPO(newUserPO);
                }
                this.onCloseModal();
              }
            }
          }
        } else {
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
        }
      });
  };

  onResendOTP() {
    this.setState({timerCount: 180000}, () => {
      this.requestMobileVerification();
    });
  }

  requestMobileVerification() {
    const {mobile} = this.props;

    LoginService.requestMobileVerification({
      countryCode: '+65',
      mobile,
    })
      .catch(error => {
        this.setState({timerCount: 0});
        Alert.alert(AlertMessage.ErrorMessageTitle, error);
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            this.setState({timerCount: 0}, () => {
              Alert.alert(AlertMessage.ErrorMessageTitle, error);
            });
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              if (response.result == 'success') {
                this.startCounter();
              }
            }
          }
        } else {
          this.setState({timerCount: 0}, () => {});
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
        }
      });
  }

  renderVerifyOTPModal() {
    return (
      <View>
        {this.renderTextInput()}
        {this.renderButton()}
      </View>
    );
  }

  renderTextInput() {
    const {verificationCode, timerCount} = this.state;
    return (
      <View>
        <Heading2 style={{lineHeight: 22}}>Verify your mobile number</Heading2>
        <View style={{marginVertical: Spacing.M}}>
          <TextInput
            keyboardType={'phone-pad'}
            placeholder={'Enter 6 digit OTP'}
            autoCorrect={false}
            clearButtonMode={'while-editing'}
            returnKeyType={'done'}
            value={verificationCode}
            onChangeText={text => this.onOTPFilled(text)}
          />
          <View style={{flexDirection: 'row', marginTop: Spacing.XS}}>
            <SmallBodyText style={{color: SRXColor.Gray}}>
              Haven't received OTP?
            </SmallBodyText>
            <Button
              disabled={timerCount <= 0 ? false : true}
              rightView={
                timerCount > 0 ? (
                  <SmallBodyText
                    style={{
                      color: SRXColor.Teal,
                      marginLeft: Spacing.XS / 2,
                    }}>
                    (
                    {CommonUtil.convertMilisecondsToMinutesSecondsFormat(
                      timerCount,
                    )}
                    )
                  </SmallBodyText>
                ) : null
              }
              leftView={
                <SmallBodyText
                  style={[
                    Typography.SmallBody,
                    {color: SRXColor.Teal, marginLeft: Spacing.XS},
                  ]}>
                  Resend
                </SmallBodyText>
              }
              onPress={this.onResendOTP}
            />
          </View>
        </View>
      </View>
    );
  }

  renderButton() {
    const {verificationCode} = this.state;
    const canVerify =
      !ObjectUtil.isEmpty(verificationCode) && verificationCode.length == 6;
    return (
      <View style={{flexDirection: 'row', marginVertical: Spacing.M}}>
        <View style={{flex: 1, marginRight: Spacing.S}}>
          <Button
            buttonStyle={{
              alignItems: 'center',
              justifyContent: 'center',
            }}
            buttonType={Button.buttonTypes.secondary}
            onPress={() => this.onCloseModal()}>
            Cancel
          </Button>
        </View>

        <View style={{flex: 1, marginLeft: Spacing.S}}>
          <Button
            buttonStyle={{
              alignItems: 'center',
              justifyContent: 'center',
            }}
            buttonType={Button.buttonTypes.primary}
            disabled={!canVerify}
            onPress={() => this.onPressVerifyButton()}>
            Verify
          </Button>
        </View>
      </View>
    );
  }

  render() {
    if (IS_IOS) {
      return (
        <SafeAreaView
          style={Styles.modalOverlay}
          forceInset={{bottom: 'never'}}>
          <KeyboardAvoidingView
            style={{flex: 1, justifyContent: 'flex-end'}}
            behavior="padding">
            <View style={Styles.container}>{this.renderVerifyOTPModal()}</View>
          </KeyboardAvoidingView>
        </SafeAreaView>
      );
    } else {
      return (
        <View style={Styles.modalOverlay}>
          <KeyboardAwareScrollView
            style={{position: 'absolute', bottom: 0, left: 0, right: 0}}
            keyboardShouldPersistTaps={'always'}>
            <View style={Styles.container}>{this.renderVerifyOTPModal()}</View>
          </KeyboardAwareScrollView>
        </View>
      );
    }
  }
}

const Styles = {
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.3)',
  },
  container: {
    paddingHorizontal: Spacing.M,
    paddingTop: Spacing.L,
    backgroundColor: SRXColor.White,
    borderTopLeftRadius: 10,
    borderTopRightRadius: 10,
  },
};

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
  };
};

export default connect(
  mapStateToProps,
  {updateUserPO},
)(VerifyOTPModal);
