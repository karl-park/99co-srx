import React, { Component } from "react";
import { View, Platform, Alert, BackHandler } from "react-native";
import SafeAreaView from "react-native-safe-area-view";
import { Navigation } from "react-native-navigation";
import { connect } from "react-redux";
import PropTypes from "prop-types";

import {
  Button,
  BodyText,
  FeatherIcon,
  SmallBodyText,
  ConfirmationCodeInput
} from "../../components";
import { Styles } from "./Styles";
import { ObjectUtil, CommonUtil } from "../../utils";
import { Spacing, Typography } from "../../styles";
import { SRXColor, AppConstant, AlertMessage } from "../../constants";
import { updateUserPO } from "../../actions";
import { LoginService } from "../../services";
import { SmsListenerAndroid } from "../../listener";

isAndroid = Platform.OS === "android";

class VerifyMobileNumber extends Component {
  static propTypes = {
    name: PropTypes.string,
    mobile: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    showSuccessfulMessage: PropTypes.func,
    updateXValueResult: PropTypes.func
  };

  static defaultProps = {
    name: "",
    mobile: ""
  };

  static options(passProps) {
    return {
      topBar: {
        visible: false,
        drawBehind: true
      }
    };
  }

  state = {
    timerCount: 180000, //3 minutes,
    verificationCode: ""
  };

  constructor(props) {
    super(props);
    this.interval = null;

    this.onResendCodes = this.onResendCodes.bind(this);
    this.decreaseCounter = this.decreaseCounter.bind(this);
  }

  componentDidMount() {
    //Grapping verfication code from sms for android
    if (isAndroid) {
      SmsListenerAndroid.setSmsReceivedListener(sms => {
        if (!ObjectUtil.isEmpty(sms)) {
          let verificationCodeRegex = /([\d]{6})/;
          if (verificationCodeRegex.test(sms.message)) {
            let verificationCode = sms.message.match(verificationCodeRegex)[1];
            this.setState({ verificationCode });
          }
        }
      });

      this.backHandler = BackHandler.addEventListener(
        "hardwareBackPress",
        () => {
          this.onClose(); // works best when the goBack is async
          return true;
        }
      );
    } //end of if

    this.startCounter();
  }

  componentWillUnmount() {
    //Remove all listeners
    if (isAndroid) {
      SmsListenerAndroid.clearSmsReceivedListener(message => {
        console.log("Successfully remove sms listener" + message);
      });

      this.backHandler.remove();
    }
  }

  //Timer functions
  startCounter() {
    this.interval = setInterval(this.decreaseCounter, 1000);
  }

  decreaseCounter() {
    const { timerCount } = this.state;
    if (timerCount == 0) {
      this.setState({ timerCount });
      return clearInterval(this.interval);
    } else {
      this.setState({ timerCount: timerCount - 1000 });
    }
  }

  onResendCodes() {
    this.setState({ timerCount: 180000 }, () => {
      this.requestMobileVerification();
    });
  }

  //API methods
  verifyMobile(verificationCode) {
    const {
      name,
      mobile,
      userPO,
      updateUserPO,
      showSuccessfulMessage,
      updateXValueResult
    } = this.props;

    LoginService.verifyMobile({
      verificationCode,
      countryCode: "+65", //hardcoded, need to discuss them to add country code or not
      mobile
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
              if (response.result == "success") {
                var newUserPO = {
                  ...userPO,
                  name: name,
                  mobileLocalNum: parseInt(mobile),
                  mobileVerified: true
                };

                if (updateUserPO) {
                  updateUserPO(newUserPO);
                }
                this.onClose();
                if (showSuccessfulMessage) {
                  showSuccessfulMessage();
                }
                if (updateXValueResult) {
                  updateXValueResult();
                }
              }
            }
          }
        } else {
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
        }
      });
  }

  requestMobileVerification() {
    const { mobile } = this.props;

    LoginService.requestMobileVerification({
      countryCode: "+65", //hardcoded, need to check with rupesh allow to update country code or not
      mobile
    })
      .catch(error => {
        this.setState({ timerCount: 0 });
        Alert.alert(AlertMessage.ErrorMessageTitle, error);
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            this.setState({ timerCount: 0 }, () => {
              Alert.alert(AlertMessage.ErrorMessageTitle, error);
            });
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              if (response.result == "success") {
                this.startCounter();
              }
            }
          }
        } else {
          this.setState({ timerCount: 0 }, () => {});
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
        }
      });
  }

  onSubmitOTP = code => {
    if (!ObjectUtil.isEmpty(code) && code.length == 6) {
      this.verifyMobile(code);
    }
  };

  onClose = () => {
    //call close method.
    const { closeVerifyMobileModal } = this.props;
    if (closeVerifyMobileModal) {
      closeVerifyMobileModal();
    }
    Navigation.dismissModal(this.props.componentId);
  };

  //Start Rendering methods
  renderVerficationCodesForm() {
    const { timerCount, verificationCode } = this.state;

    return (
      <View style={Styles.vertifyContainerSytle}>
        <Button
          leftView={<FeatherIcon name="x" size={25} color={SRXColor.Black} />}
          onPress={this.onClose}
        />

        <View style={Styles.verifySubContainerStyle}>
          <BodyText style={Styles.otpScreenInfoTextStyle}>
            Verification of your mobile number is required - Please verify with
            the 6-digit OTP which has been sent to your mobile.
          </BodyText>

          <ConfirmationCodeInput
            verificationCode={verificationCode}
            containerStyle={{ marginTop: 52 }}
            codeInputStyle={[Typography.Body, Styles.codeInputStyle]}
            codeLength={6}
            height={55}
            width={36}
            space={12}
            activeColor={SRXColor.SmallBodyBackground}
            inactiveColor={SRXColor.SmallBodyBackground}
            onFulfill={code => this.onSubmitOTP(code)}
          />

          <Button
            buttonStyle={{ marginTop: Spacing.M }}
            disabled={timerCount <= 0 ? false : true}
            rightView={
              timerCount > 0 ? (
                <SmallBodyText
                  style={{
                    color: SRXColor.Teal,
                    marginLeft: Spacing.XS / 2
                  }}
                >
                  (
                  {CommonUtil.convertMilisecondsToMinutesSecondsFormat(
                    timerCount
                  )}
                  )
                </SmallBodyText>
              ) : null
            }
            leftView={
              <SmallBodyText
                style={[Typography.SmallBody, { color: SRXColor.TextLink }]}
              >
                Resend OTP
              </SmallBodyText>
            }
            onPress={this.onResendCodes}
          />
        </View>
      </View>
    );
  }

  render() {
    return (
      <SafeAreaView
        style={{ flex: 1, backgroundColor: SRXColor.DarkBlue }}
        forceInset={{ bottom: "never" }}
      >
        <SafeAreaView
          style={{ flex: 1, backgroundColor: "white" }}
          forceInset={{ bottom: "never" }}
        >
          {this.renderVerficationCodesForm()}
        </SafeAreaView>
      </SafeAreaView>
    );
  }
}

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO
  };
};

export default connect(
  mapStateToProps,
  { updateUserPO }
)(VerifyMobileNumber);
