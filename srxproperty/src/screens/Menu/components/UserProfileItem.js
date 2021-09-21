import React, { Component } from "react";
import { View, Image } from "react-native";
import PropTypes from "prop-types";
import {
  TouchableHighlight,
  BodyText,
  Subtext,
  Text,
  FeatherIcon,
  Heading1,
  Avatar
} from "../../../components";
import { ObjectUtil, CommonUtil } from "../../../utils";
import { Spacing } from "../../../styles";
import { SRXColor } from "../../../constants";

class UserProfileItem extends Component {
  constructor(props) {
    super(props);

    this.onSelected = this.onSelected.bind(this);
  }

  onSelected() {
    const { item, onItemSelected } = this.props;
    if (onItemSelected) {
      onItemSelected(item);
    }
  }

  renderUserIcon() {
    const { userPO } = this.props;
    let firstLetter = "";
    if (!ObjectUtil.isEmpty(userPO.getCommunityPostUserName())) {
      firstLetter = userPO.getCommunityPostUserName().charAt(0).toUpperCase();
    }
    return (
      <View
        style={{
          marginRight: Spacing.S,
        }}>
        <Avatar
          backgroundColor={'#e3c464'}
          size={40}
          textSize={25}
          imageUrl={CommonUtil.handleImageUrl(userPO.photo)}
          name={firstLetter}
          borderColor={'#DCDCDC'}
          style={{marginRight: Spacing.L}}
        />
      </View>
    );
  }

  renderUserInfo() {
    return (
      <View style={{ flex: 1 }}>
        {this.renderUserName()}
        {this.renderUserEmail()}
      </View>
    );
  }

  renderUserName() {
    const { userPO } = this.props;
    if (!ObjectUtil.isEmpty(userPO.getCommunityPostUserName())) {
      return <Heading1>{userPO.getCommunityPostUserName()}</Heading1>;
    }
  }

  renderUserEmail() {
    const { userPO } = this.props;
    if (!ObjectUtil.isEmpty(userPO.email)) {
      return (
        <Subtext style={{ color: SRXColor.TextLink }}>{userPO.email}</Subtext>
      );
    }
  }

  renderAccessoryView() {
    return (
      <FeatherIcon
        name="chevron-right"
        size={24}
        color={SRXColor.Black}
        style={{ marginLeft: Spacing.S }}
      />
    );
  }

  render() {
    const { containerStyle } = this.props;
    return (
      <TouchableHighlight onPress={this.onSelected}>
        <View
          style={[
            {
              flex: 1,
              flexDirection: "row",
              // alignItems: "center",
              padding: Spacing.M,
              backgroundColor: SRXColor.White
            },
            containerStyle
          ]}
        >
          {this.renderUserIcon()}
          {this.renderUserInfo()}
          {this.renderAccessoryView()}
        </View>
      </TouchableHighlight>
    );
  }
}

UserProfileItem.propTypes = {
  userPO: PropTypes.object.isRequired,
  /**
   * style of item content
   */
  containerStyle: PropTypes.object,

  onItemSelected: PropTypes.func
};

export { UserProfileItem };
