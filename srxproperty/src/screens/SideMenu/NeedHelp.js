import React, { Component } from "react";
import {
  View,
  Platform,
  PermissionsAndroid,
  Linking,
  Alert
} from "react-native";
import SafeAreaView from "react-native-safe-area-view";
import { KeyboardAwareScrollView } from "react-native-keyboard-aware-scroll-view";
import { Navigation } from "react-native-navigation";
import { connect } from "react-redux";

import {
  Button,
  TextInput,
  FeatherIcon,
  Heading2,
  Subtext,
  LargeTitleComponent
} from "../../components";
import { Styles } from "./Styles";
import { SRXColor, AppConstant, AlertMessage } from "../../constants";
import {
  ObjectUtil,
  CommonUtil,
  PermissionUtil,
  StringUtil
} from "../../utils";
import { Typography, Spacing } from "../../styles";
import { LoginService } from "../../services";

const isIOS = Platform.OS === "ios";

const SubmissionState = {
  Normal: "normal", //default, not submitting
  Submitting: "submitting", //sending requests
  Submitted: "submitted"
};

class NeedHelp extends LargeTitleComponent {
  //Constructor
  constructor(props) {
    super(props);

    Navigation.events().bindComponent(this);

    this.onPressSendMessage = this.onPressSendMessage.bind(this);
    this.onPressCallSRX = this.onPressCallSRX.bind(this);
  }

  state = {
    submissionState: SubmissionState.Normal,
    isCallBack: false,
    needHelp: {
      name: "",
      mobile: "",
      email: "",
      message: ""
    },
    // loginUserPO: {
    //   name: "",
    //   mobile: "",
    //   email: ""
    // },
    error: {
      errMsgName: "",
      errMsgMobile: "",
      errMsgEmail: "",
      errMsgMessage: ""
    }
  };

  componentDidMount() {
    this.setupTopBar();

    const { userPO } = this.props;
    if (!ObjectUtil.isEmpty(userPO)) {
      this.setState({
        needHelp: {
          ...this.state.needHelp,
          name: userPO.name,
          mobile: userPO.mobileLocalNum ? userPO.mobileLocalNum.toString() : "",
          email: userPO.email
        }
        // loginUserPO: {
        //   ...this.state.loginUserPO,
        //   name: userPO.name,
        //   mobile: userPO.mobileLocalNum ? userPO.mobileLocalNum.toString() : "",
        //   email: userPO.email
        // }
      });
    }
  }

  setupTopBar() {
    FeatherIcon.getImageSource("x", 25, "black").then(icon_close => {
      Navigation.mergeOptions(this.props.componentId, {
        topBar: {
          leftButtons: [
            {
              id: "needHelp_close",
              icon: icon_close
            }
          ]
        }
      });
    });
  }

  navigationButtonPressed({ buttonId }) {
    if (buttonId === "needHelp_close") {
      Navigation.dismissModal(this.props.componentId);
    }
  }

  //onPress and event method lists
  onPressSendMessage = () => {
    if (this.isAllValidate()) {
      this.setState({ submissionState: SubmissionState.Submitting }, () => {
        const { userPO } = this.props;
        if (!ObjectUtil.isEmpty(userPO)) {
          //Login
          this.sendProfessionalHelp();
        } else {
          //NOT Login
          this.sendProfessionalHelpNologin();
        }
      });
    }
  };

  isAllValidate() {
    const { needHelp, error } = this.state;
    if (!ObjectUtil.isEmpty(needHelp)) {
      const { name, mobile, email, message } = needHelp;
      let isAllValidate = true;
      let errorMessage = { name: "", mobile: "", email: "", message: "" };

      //Name
      if (ObjectUtil.isEmpty(name) || name === "") {
        isAllValidate = false;
        errorMessage.name = "Required field";
      }

      //Either Mobile or Email
      if (ObjectUtil.isEmpty(mobile) && ObjectUtil.isEmpty(email)) {
        isAllValidate = false;
        errorMessage.mobile = "Required field";
        errorMessage.email = "Required field";
      }

      if (
        !ObjectUtil.isEmpty(email) &&
        !StringUtil.validateEmailFormat(email)
      ) {
        isAllValidate = false;
        errorMessage.email = "Please enter a valid email";
      }

      //Message
      if (ObjectUtil.isEmpty(message) || message === "") {
        isAllValidate = false;
        errorMessage.message = "Required field";
      }

      this.setState({
        error: {
          ...error,
          errMsgName: errorMessage.name,
          errMsgMobile: errorMessage.mobile,
          errMsgEmail: errorMessage.email,
          errMsgMessage: errorMessage.message
        }
      });
      return isAllValidate;
    } else {
      return false;
    }
  }

  sendProfessionalHelpNologin() {
    const { needHelp, isCallBack } = this.state;

    LoginService.sendProfessionalHelpNologin({
      name: needHelp.name,
      email: needHelp.email,
      mobile: needHelp.mobile,
      callbackInd: isCallBack,
      message: needHelp.message
    })
      .catch(error => {
        this.setState({ submissionState: SubmissionState.Normal }, () => {
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
        });
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            this.setState({ submissionState: SubmissionState.Normal }, () => {
              Alert.alert(AlertMessage.ErrorMessageTitle, error);
            });
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              if (response.result === "success") {
                this.setState(
                  { submissionState: SubmissionState.Submitted }, //after getting result
                  () => {
                    this.showSuccessfulMessage();
                  }
                );
              }
            }
          }
        } else {
          console.log("Error In Send Professional Help No Login");
        }
      });
  }

  sendProfessionalHelp() {
    const { needHelp, isCallBack } = this.state;

    LoginService.sendProfessionalHelp({
      callbackInd: isCallBack,
      message: needHelp.message
    })
      .catch(error => {
        this.setState({ submissionState: SubmissionState.Normal }, () => {
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
        });
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            this.setState({ submissionState: SubmissionState.Normal }, () => {
              Alert.alert(AlertMessage.ErrorMessageTitle, error);
            });
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              if (response.result === "success") {
                this.setState(
                  { submissionState: SubmissionState.Submitted }, //after getting result
                  () => {
                    this.showSuccessfulMessage();
                  }
                );
              }
            }
          }
        } else {
          console.log("Error In Send Professional Help");
        }
      });
  }

  showSuccessfulMessage() {
    Alert.alert(
      AlertMessage.SuccessMessageTitle,
      "Thanks for reaching out. We'll contact you shortly",
      [
        {
          text: "OK",
          onPress: () => Navigation.dismissModal(this.props.componentId)
        }
      ]
    );
  }

  onPressCallSRX = () => {
    if (isIOS) {
      this.callSRXCustomerService();
    } else {
      this.requestAndroidCallPermission();
    }
  };

  callSRXCustomerService = () => {
    const url = "tel:66353388";

    Linking.canOpenURL(url)
      .then(supported => {
        if (!supported) {
          console.log("In Need Help - cannot handle this url" + url);
        } else {
          return Linking.openURL(url);
        }
      })
      .catch(err => console.error("In Need Help - An error occured", err));
  };

  requestAndroidCallPermission = () => {
    PermissionUtil.requestAndroidCallPermission().then(granted => {
      if (granted === PermissionsAndroid.RESULTS.GRANTED) {
        this.callSRXCustomerService();
      }
    });
  };

  renderNeedHelpForm() {
    const { needHelp, error } = this.state;
    const { userPO } = this.props;

    return (
      <View style={Styles.accountDetailsContainer}>
        <Heading2 style={{ lineHeight: 22 }}>How can We help you?</Heading2>
        <Subtext style={{ lineHeight: 22 }}>
          We can usually respond within 24 hours
        </Subtext>

        {/* Name */}
        <View style={[Styles.inputContainer, { marginTop: Spacing.S }]}>
          <TextInput
            placeholder={"Name"}
            editable={
              !ObjectUtil.isEmpty(userPO)
                ? ObjectUtil.isEmpty(userPO.name)
                : true
            }
            value={needHelp.name}
            onChangeText={newName =>
              this.setState({
                needHelp: { ...needHelp, name: newName },
                error: { ...error, errMsgName: "" }
              })
            }
            error={error.errMsgName}
            inputContainerStyle={
              !ObjectUtil.isEmpty(userPO)
                ? { backgroundColor: SRXColor.LightGray + "66" }
                : null
            }
            systemPopulated={!ObjectUtil.isEmpty(userPO) ? true : false}
          />
        </View>

        {/* Mobile */}
        <View style={Styles.inputContainer}>
          <TextInput
            placeholder={"Mobile"}
            keyboardType={isIOS ? "number-pad" : "numeric"}
            editable={
              !ObjectUtil.isEmpty(userPO) ? !userPO.mobileLocalNum : true
            }
            value={needHelp.mobile}
            onChangeText={newMobileNumber =>
              this.setState({
                needHelp: { ...needHelp, mobile: newMobileNumber },
                error: { ...error, errMsgMobile: "", errMsgEmail: "" }
              })
            }
            error={error.errMsgMobile}
            inputContainerStyle={
              !ObjectUtil.isEmpty(userPO)
                ? { backgroundColor: SRXColor.LightGray + "66" }
                : null
            }
            systemPopulated={!ObjectUtil.isEmpty(userPO) ? true : false}
          />
        </View>

        {/* Email */}
        <View style={Styles.inputContainer}>
          <TextInput
            placeholder={"E-mail address"}
            editable={
              !ObjectUtil.isEmpty(userPO)
                ? ObjectUtil.isEmpty(userPO.email)
                : true
            }
            value={needHelp.email}
            onChangeText={newEmail =>
              this.setState({
                needHelp: { ...needHelp, email: newEmail },
                error: { ...error, errMsgEmail: "", errMsgMobile: "" }
              })
            }
            error={error.errMsgEmail}
            inputContainerStyle={
              !ObjectUtil.isEmpty(userPO)
                ? { backgroundColor: SRXColor.LightGray + "66" }
                : null
            }
            systemPopulated={!ObjectUtil.isEmpty(userPO) ? true : false}
          />
        </View>

        {/* Description */}
        <View style={Styles.inputContainer}>
          <TextInput
            placeholder={"Write Something..."}
            multiline={true}
            onChangeText={newMessage =>
              this.setState({
                needHelp: { ...needHelp, message: newMessage },
                error: { ...error, errMsgMessage: "" }
              })
            }
            error={error.errMsgMessage}
            maxChar={140}
          />
        </View>

        {/* Action Buttons */}
        {this.renderActionButtons()}
      </View>
    );
  }

  renderActionButtons() {
    const { submissionState } = this.state;
    var buttonTitle = "Send message";
    if (submissionState === SubmissionState.Submitting) {
      buttonTitle = "Sending message";
    }

    return (
      <View
        style={{
          alignItems: "center",
          justifyContent: "center",
          flex: 1
        }}
      >
        <Button
          buttonStyle={{ marginTop: 24, marginBottom: 5 }}
          buttonType={Button.buttonTypes.primary}
          isSelected={submissionState === SubmissionState.Submitting}
          onPress={this.onPressSendMessage}
        >
          {buttonTitle}
        </Button>

        <Button
          buttonStyle={{ marginTop: Spacing.S }}
          textStyle={[Typography.Body, { color: SRXColor.TextLink }]}
          onPress={this.onPressCallSRX}
        >
          Call SRX directly
        </Button>
      </View>
    );
  }

  render() {
    return (
      <SafeAreaView style={{ flex: 1, backgroundColor: "white" }}>
        <KeyboardAwareScrollView
          style={{ flex: 1 }}
          onScroll={this.onScroll}
          bounces={false}
          keyboardShouldPersistTaps={"always"}
        >
          {/* title for need help screen */}
          {this.renderLargeTitle("Need Help")}
          {this.renderNeedHelpForm()}
        </KeyboardAwareScrollView>
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
  null
)(NeedHelp);
