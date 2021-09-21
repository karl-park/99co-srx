import React, { Component } from "react";
import { TouchableOpacity, View, BackHandler } from "react-native";
import SafeAreaView from "react-native-safe-area-view";
import PropTypes from "prop-types";
import { Navigation } from "react-native-navigation";

import { Button, TextInput, FeatherIcon, BodyText } from "../../components";
import { IS_IOS, SRXColor } from "../../constants";
import { ObjectUtil, UserUtil } from "../../utils";
import { Spacing } from "../../styles";
/**
 * TODO: display as modal
 * to prevent using eventLsitener
 */
class PasswordModal extends Component {
  static options(passProps) {
    return {
      topBar: {
        visible: false,
        drawBehind: true
      },
      layout: {
        backgroundColor: SRXColor.Transparent
      }
    };
  }

  constructor(props) {
    super(props);

    this.state = {
      errorMessage: "",
      securePassword: "",
      showSecurePassword: false
    };

    this.onPressConfirm = this.onPressConfirm.bind(this);
    this.onCloseModal = this.onCloseModal.bind(this);
  }

  componentDidMount() {
    if (!IS_IOS) {
      //for android
      this.backHandler = BackHandler.addEventListener(
        "hardwareBackPress",
        () => {
          this.onCloseModal(); // works best when the goBack is async
          return true;
        }
      );
    }
  }

  componentWillUnmount() {
    //Remove all listeners
    if (!IS_IOS) {
      this.backHandler.remove();
    }
  }

  onPressConfirm() {
    const { onSuccess } = this.props;
    const { securePassword } = this.state;
    UserUtil.getDataForLogin().then(({ username, password, facebookId }) => {
      if (!ObjectUtil.isEmpty(password)) {
        //Login with password
        if (this.isSecuredPassword(password)) {
          if (onSuccess) {
            onSuccess(securePassword);
          }
          this.onCloseModal();
        }
      }
    });
  }

  isValidate() {
    const { securePassword } = this.state;

    var isValidate = true;
    if (ObjectUtil.isEmpty(securePassword)) {
      isValidate = false;
      this.setState({ errorMessage: "Required field" });
    }
    return isValidate;
  }

  isSecuredPassword(savedPassword) {
    const { securePassword } = this.state;

    var isValidate = this.isValidate();

    //if secure password is not same with verification password
    if (savedPassword !== securePassword) {
      isValidate = false;
      this.setState({ errorMessage: "Invalid Password" });
    }

    return isValidate;
  }

  onCloseModal = () => {
    return Navigation.dismissOverlay(this.props.componentId);
  };

  renderContent() {
    const { securePassword, showSecurePassword, errorMessage } = this.state;
    return (
      <View style={styles.passwordContainer}>
        {/* Close Button Container */}
        <TouchableOpacity
          style={{ alignItems: "flex-end" }}
          onPress={this.onCloseModal}
        >
          <FeatherIcon name="x" size={25} color={"black"} />
        </TouchableOpacity>

        {/* Password Verificaiton Title */}
        <BodyText
          style={{ marginTop: Spacing.XS / 2, marginBottom: Spacing.XS }}
        >
          Password Verification
        </BodyText>

        {/* Secured Password Input */}
        <TextInput
          placeholder={"Password"}
          defaultValue={securePassword}
          error={errorMessage}
          autoFocus={true}
          secureTextEntry={!showSecurePassword}
          rightView={
            <Button
              buttonStyle={{ alignItems: "center", justifyContent: "center" }}
              onPress={() =>
                this.setState({ showSecurePassword: !showSecurePassword })
              }
              leftView={
                showSecurePassword ? (
                  <FeatherIcon name="eye" size={25} color={"#858585"} />
                ) : (
                  <FeatherIcon name="eye-off" size={25} color={"#858585"} />
                )
              }
            />
          }
          onChangeText={newSecurePassword =>
            this.setState({
              securePassword: newSecurePassword,
              errorMessage: ""
            })
          }
        />

        {/* Confirm Button */}
        {this.renderActionButton()}
      </View>
    );
  }

  renderActionButton() {
    return (
      <View style={styles.buttonContainer}>
        <Button
          buttonType={Button.buttonTypes.primary}
          onPress={this.onPressConfirm}
        >
          Confirm
        </Button>
      </View>
    );
  }

  render() {
    return (
      <SafeAreaView style={styles.container}>
        <View style={styles.subContainer}>{this.renderContent()}</View>
      </SafeAreaView>
    );
  }
}

PasswordModal.propTypes = {
  // after user update
  onSuccess: PropTypes.func.isRequired
};

const styles = {
  container: {
    flex: 1,
    backgroundColor: "rgba(0, 0, 0, 0.3)"
  },
  subContainer: {
    flex: 1,
    backgroundColor: SRXColor.Transparent,
    overflow: "hidden",
    alignItems: "center",
    justifyContent: "center"
  },
  passwordContainer: {
    width: "90%",
    opacity: 1,
    borderRadius: 10,
    backgroundColor: SRXColor.White,
    padding: Spacing.M
  },
  buttonContainer: {
    alignItems: "center",
    justifyContent: "center",
    width: "100%",
    marginTop: Spacing.S,
    marginBottom: Spacing.XS
  }
};

export { PasswordModal };
