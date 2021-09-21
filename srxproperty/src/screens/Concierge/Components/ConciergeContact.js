import React, { Component } from "react";
import { View, StyleSheet } from "react-native";
import PropTypes from "prop-types";
import { connect } from "react-redux";

import { BodyText, TextInput } from "../../../components";
import { Spacing } from "../../../styles";
import { ObjectUtil } from "../../../utils";

class ConciergeContact extends Component {
  static propTypes = {
    userInfo: PropTypes.object
  };

  static defaultProps = {
    userInfo: null
  };

  constructor(props) {
    super(props);
  }

  //On Change Events
  onChangeUserName = name => {
    const { userInfo, onChangeUserInfo, errorMessage } = this.props;

    const newUserInfo = {
      ...userInfo,
      name
    };

    const newErrorMessage = {
      ...errorMessage,
      errMsgName: ObjectUtil.isEmpty(name) ? "Required field" : ""
    };

    if (onChangeUserInfo) {
      onChangeUserInfo(newUserInfo, newErrorMessage);
    }
  };

  onChangeUserEmail = email => {
    const { userInfo, onChangeUserInfo, errorMessage } = this.props;

    const newUserInfo = {
      ...userInfo,
      email
    };

    const newErrorMessage = {
      ...errorMessage,
      errMsgEmail: ObjectUtil.isEmpty(email) ? "Required field" : ""
    };

    if (onChangeUserInfo) {
      onChangeUserInfo(newUserInfo, newErrorMessage);
    }
  };

  onChangeUserPhone = mobileNum => {
    const { userInfo, onChangeUserInfo, errorMessage } = this.props;
    const newUserInfo = {
      ...userInfo,
      mobileNum
    };
    const newErrorMessage = {
      ...errorMessage,
      errMsgPhone: ObjectUtil.isEmpty(mobileNum)
        ? "Required field"
        : ""
    };
    if (onChangeUserInfo) {
      onChangeUserInfo(newUserInfo, newErrorMessage);
    }
  };

  //Add Remark
  onChangeRemark = remark => {
    const { userInfo, onChangeUserInfo, errorMessage } = this.props;
    const newUserInfo = {
      ...userInfo,
      remark
    };
    if (onChangeUserInfo) {
      onChangeUserInfo(newUserInfo, errorMessage);
    }
  };

  renderContactForm() {
    const { name, email, mobileNum } = this.props.userInfo;
    const { errMsgName, errMsgEmail, errMsgPhone } = this.props.errorMessage;
    const { userPO } = this.props;

    return (
      <View style={styles.container}>
        <BodyText style={{ fontWeight: "600", lineHeight: 24 }}>
          Tell us how to contact you
        </BodyText>

        <TextInput
          style={{ marginTop: Spacing.M }}
          placeholder={"Name"}
          defaultValue={name}
          error={errMsgName}
          editable={
            !ObjectUtil.isEmpty(userPO) ? ObjectUtil.isEmpty(userPO.name) : true
          }
          onChangeText={newName => this.onChangeUserName(newName)}
        />

        <TextInput
          style={{ marginTop: Spacing.M }}
          placeholder={"Email"}
          defaultValue={email}
          error={errMsgEmail}
          editable={
            !ObjectUtil.isEmpty(userPO)
              ? ObjectUtil.isEmpty(userPO.email)
              : true
          }
          onChangeText={newEmail => this.onChangeUserEmail(newEmail)}
        />

        <TextInput
          style={{ marginTop: Spacing.M }}
          keyboardType={"number-pad"}
          placeholder={"Mobile"}
          defaultValue={mobileNum}
          error={errMsgPhone}
          editable={!ObjectUtil.isEmpty(userPO) ? !userPO.mobileLocalNum : true}
          onChangeText={newPhone => this.onChangeUserPhone(newPhone)}
        />

        <TextInput
          style={{ marginTop: Spacing.M }}
          multiline={true}
          placeholder={"Add Remarks (Optional)"}
          onChangeText={newRemark => this.onChangeRemark(newRemark)}
        />
      </View>
    );
  }

  render() {
    return this.renderContactForm();
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    marginTop: 44,
    marginHorizontal: Spacing.M
  }
});

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO
  };
};

export default connect(mapStateToProps)(ConciergeContact);
