import React, {Component} from 'react';
import {View, SafeAreaView, Alert, Platform} from 'react-native';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import {updateUserPO} from '../../../actions';
import {
  ConfirmationCodeInput,
  BodyText,
  Button,
  SmallBodyText,
} from '../../../components';
import {
  SRXColor,
  LoadingState,
  AppConstant,
  AlertMessage,
} from '../../../constants';
import {SmsListenerAndroid} from '../../../listener';
import {Spacing, Typography} from '../../../styles';
import {CommonUtil, ObjectUtil} from '../../../utils';
import {PropertyTrackerService} from '../../../services';

const isAndroid = Platform.OS === 'android';

const codeCount = 6;

class MyPropertyOTPVerification extends Component {
  static options(passProps) {
    return {
      topBar: {
        title: {
          text: '',
        },
        visible: true,
        drawBehind: true,
        noBorder: true,
        elevation: 0,
        background: {
          color: 'transparent',
        },
        backButton: {
          color: SRXColor.Black,
        },
      },
      layout: {
        backgroundColor: SRXColor.White,
      },
      bottomTabs: {
        visible: false,
        animate: true,
        drawBehind: true,
      },
    };
  }

  state = {
    timerCount: 180000, //3 minutes,
    verificationCode: '',
    resendCodeState: LoadingState.Normal,
  };

  constructor(props) {
    super(props);
    this.interval = null;

    this.onResendCodes = this.onResendCodes.bind(this);
    this.decreaseCounter = this.decreaseCounter.bind(this);
    this.updateUserMobileNumber = this.updateUserMobileNumber.bind(this);
  }

  componentDidMount() {
    //Grapping verfication code from sms for android
    if (isAndroid) {
      SmsListenerAndroid.setSmsReceivedListener(sms => {
        if (!ObjectUtil.isEmpty(sms)) {
          let verificationCodeRegex = /([\d]{6})/;
          if (verificationCodeRegex.test(sms.message)) {
            let verificationCode = sms.message.match(verificationCodeRegex)[1];
            this.setState({verificationCode});
          }
        }
      });
    } //end of if

    this.startCounter();
  }

  componentWillUnmount() {
    //Remove all listeners
    clearInterval(this.interval);
    if (isAndroid) {
      SmsListenerAndroid.clearSmsReceivedListener(message => {
        console.log('Removed sms listener' + message);
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

  onResendCodes() {
    this.setState({resendCodeState: LoadingState.Loading}, () => {
      this.requestToResendVerificationCode();
    });
  }

  onOTPFilled = code => {
    this.setState(
      {
        verificationCode: code,
      },
      () => {
        if (!ObjectUtil.isEmpty(code) && code.length == codeCount) {
          this.verifyOTP();
        }
      },
    );
  };

  requestToResendVerificationCode() {
    const {ptUserId, mobile} = this.props;

    PropertyTrackerService.resendVerificationCode({
      ptUserId,
      mobile,
    })
      .then(response => {
        if (!ObjectUtil.isEmpty(response) && response.result === 'success') {
          this.setState({
            timerCount: 180000,
            resendCodeState: LoadingState.Normal,
          });
        } else {
          this.setState({
            resendCodeState: LoadingState.Normal,
          });
        }
      })
      .catch(() => {
        this.setState({resendCodeState: LoadingState.Normal});
      });
  }

  verifyOTP() {
    const {ptUserId, mobile} = this.props;
    const {verificationCode} = this.state;

    PropertyTrackerService.confirmSignup({
      ptUserId,
      mobile,
      verificationCode,
      hasAgreedToPDPA: true,
    })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const {result, error} = response;
          if (error) {
            Alert.alert(AlertMessage.ErrorMessageTitle, error);
          } else {
            //back to the list
            if (result === 'success') {
              const {onVerified} = this.props;

              this.updateUserMobileNumber();
              if (onVerified) {
                onVerified();
              } else {
                Alert.alert(
                  AlertMessage.SuccessMessageTitle,
                  'Your property is added successfully.',
                );
              }
            }
          }
        }
      })
      .catch(error => {
        Alert.alert(AlertMessage.ErrorMessageTitle, 'Error');
      });
  }

  updateUserMobileNumber() {
    const {mobile, updateUserPO, userPO} = this.props;
    if (!ObjectUtil.isEmpty(userPO)) {
      updateUserPO({
        ...userPO,
        mobileLocalNum: mobile,
        mobileVerified: true,
      });
    }
  }

  render() {
    const {timerCount, verificationCode, resendCodeState} = this.state;
    return (
      <SafeAreaView style={{flex: 1}}>
        <KeyboardAwareScrollView
          style={{flex: 1}}
          keyboardShouldPersistTaps={'always'}
          bounces={false}>
          <View
            style={{
              padding: Spacing.M,
              paddingHorizontal: Spacing.XL,
              marginTop: 70,
              alignItems: 'center',
            }}>
            <BodyText
              style={{
                color: SRXColor.Black,
                textAlign: 'center',
                lineHeight: 22,
                marginBottom: 52,
              }}>
              Verification of your mobile number is required - Please verify
              with the {codeCount}-digit OTP which has been sent to your mobile.
            </BodyText>
            <ConfirmationCodeInput
              codeInputStyle={[
                Typography.Body,
                {
                  color: SRXColor.Black,
                  fontWeight: '600',
                  backgroundColor: SRXColor.SmallBodyBackground,
                },
              ]}
              codeLength={6}
              height={55}
              width={36}
              space={12}
              activeColor={SRXColor.SmallBodyBackground}
              inactiveColor={SRXColor.SmallBodyBackground}
              verificationCode={verificationCode}
              onFulfill={this.onOTPFilled}
            />
            <Button
              buttonStyle={{marginTop: Spacing.M}}
              textStyle={[Typography.SmallBody, {color: SRXColor.Teal}]}
              disabled={
                timerCount <= 0 && resendCodeState != LoadingState.Loading
                  ? false
                  : true
              }
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
              onPress={this.onResendCodes}>
              Resend OTP
            </Button>
          </View>
        </KeyboardAwareScrollView>
      </SafeAreaView>
    );
  }
}

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
  };
};

MyPropertyOTPVerification.propTypes = {
  ptUserId: PropTypes.oneOfType([PropTypes.number, PropTypes.string])
    .isRequired,
  mobile: PropTypes.oneOfType([PropTypes.number, PropTypes.string]).isRequired,
  onVerified: PropTypes.func,
};

export default connect(
  mapStateToProps,
  {updateUserPO},
)(MyPropertyOTPVerification);
