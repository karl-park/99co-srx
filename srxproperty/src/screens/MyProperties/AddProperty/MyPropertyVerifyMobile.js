import React, { Component } from "react";
import { View, SafeAreaView, Alert } from "react-native";
import { KeyboardAwareScrollView } from "react-native-keyboard-aware-scroll-view";
import PropTypes from "prop-types";
import { connect } from "react-redux";
import { Navigation } from "react-native-navigation";
import {
  TextInput,
  LargeTitleComponent,
  Button,
  FeatherIcon,
  SmallBodyText
} from "../../../components";
import {
  AppConstant,
  LoadingState,
  SRXColor,
  AlertMessage
} from "../../../constants";
import { Spacing } from "../../../styles";
import { StringUtil, ObjectUtil, CommonUtil } from "../../../utils";
import { PropertyTrackerService } from "../../../services";

class MyPropertyVerifyMobile extends LargeTitleComponent {
  state = {
    mobileNum: "",
    ptUserId: null,
    requestState: LoadingState.Normal
  };

  constructor(props) {
    super(props);

    this.sendOTPPressed = this.sendOTPPressed.bind(this);
    this.directToOTPVerification = this.directToOTPVerification.bind(this);
  }

  componentDidMount() {
    const { userPO } = this.props;
    if (!ObjectUtil.isEmpty(userPO)) {
      if (
        !ObjectUtil.isEmpty(userPO.mobileLocalNum) ||
        userPO.mobileLocalNum > 0
      ) {
        this.updateMobileNumber(userPO.mobileLocalNum);
      }
    }
  }

  updateMobileNumber(mobile) {
    let newMobile = StringUtil.decimalValue(mobile, 0);
    if (newMobile == "0") {
      this.setState({
        mobileNum: ""
      });
    } else {
      this.setState({
        mobileNum: newMobile
      });
    }
  }

  sendOTPPressed() {
    //if have ptuserid, call resend or go to otp verification
    //else call signup

    const { trackerSignUpPO } = this.props;
    const { ptUserId, mobileNum } = this.state;

    this.setState({ requestState: LoadingState.Loading }, () => {
      if (!ObjectUtil.isEmpty(ptUserId) || ptUserId > 0) {
        PropertyTrackerService.resendVerificationCode({
          ptUserId,
          mobile: mobileNum
        })
          .then(response => {
            this.setState(
              {
                requestState: LoadingState.Normal
              },
              () => {
                if (
                  !ObjectUtil.isEmpty(response) &&
                  response.result === "success"
                ) {
                  this.directToOTPVerification();
                }
              }
            );
          })
          .catch(() => {
            this.setState({ resendCodeState: LoadingState.Normal });
          });
      } else {
        PropertyTrackerService.signUp({
          ...trackerSignUpPO,
          mobile: mobileNum
        }).then(response => {
          const errorMsg = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(errorMsg)) {
            this.setState({ requestState: LoadingState.Normal }, () => {
              Alert.alert(AlertMessage.ErrorMessageTitle, errorMsg);
            });
          } else {
            const { result, isOtpVerified } = response;
            this.setState(
              {
                requestState: LoadingState.Normal,
                ptUserId: result
              },
              () => {
                if (!ObjectUtil.isEmpty(result) || result > 0) {
                  //according to documentation, result is ptUserId
                  //no need to check isOtpVerified
                  //directly bring to otp page
                  this.directToOTPVerification();
                }
              }
            );
          }
        });
      }
    });
  }

  directToOTPVerification() {
    const { onVerified } = this.props;
    const { ptUserId, mobileNum } = this.state;
    Navigation.push(this.props.componentId, {
      component: {
        name: "MyProperties.OTPConfirmation",
        passProps: {
          ptUserId,
          mobile: mobileNum,
          onVerified: onVerified
        }
      }
    });
  }

  render() {
    const { mobileNum, requestState } = this.state;
    const canVerify = !ObjectUtil.isEmpty(mobileNum) && mobileNum.length >= 8;
    const isLoadingRequest = requestState === LoadingState.Loading;
    return (
      <SafeAreaView style={{ flex: 1 }}>
        <KeyboardAwareScrollView
          style={{ flex: 1 }}
          keyboardShouldPersistTaps={"always"}
          onScroll={this.onScroll}
        >
          {this.renderLargeTitle("Verify Mobile No.")}
          <View
            style={{
              paddingTop: Spacing.M,
              paddingBottom: Spacing.S,
              paddingHorizontal: Spacing.M
            }}
          >
            <SmallBodyText
              style={{
                marginBottom: Spacing.S,
                color: SRXColor.Gray,
                lineHeight: 22
              }}
            >
              You need to verify your mobile number in order to add properties
              to your account
            </SmallBodyText>
            <TextInput
              keyboardType={"phone-pad"}
              placeholder={"Enter mobile number"}
              autoCorrect={false}
              clearButtonMode={"while-editing"}
              returnKeyType={"done"}
              value={mobileNum}
              onChangeText={text => this.updateMobileNumber(text)}
            />
          </View>
          <View
            style={{
              marginTop: Spacing.L,
              marginBottom: 50,
              alignItems: "center",
              paddingHorizontal: Spacing.M
            }}
          >
            <Button
              buttonType={Button.buttonTypes.primary}
              disabled={!canVerify}
              isSelected={isLoadingRequest}
              onPress={this.sendOTPPressed}
            >
              {isLoadingRequest ? "Sending OTP..." : "Send OTP"}
            </Button>
          </View>
        </KeyboardAwareScrollView>
      </SafeAreaView>
    );
  }
}

MyPropertyVerifyMobile.propTypes = {
  trackerSignUpPO: PropTypes.object.isRequired,
  onVerified: PropTypes.func
};

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO
  };
};

export default connect(mapStateToProps)(MyPropertyVerifyMobile);
