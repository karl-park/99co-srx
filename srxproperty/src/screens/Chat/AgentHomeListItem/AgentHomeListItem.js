import React, { Component } from "react";
import {
  View,
  TouchableHighlight,
  Platform,
  Image,
  TouchableOpacity,
  Alert
} from "react-native";
import { Navigation } from "react-native-navigation";
import PropTypes from "prop-types";
import { Placeholder_Agent } from "../../../assets";
import {
  Avatar,
  Heading2,
  ExtraSmallBodyText,
  BodyText,
  SmallBodyText,
  Text,
  FeatherIcon
} from "../../../components";
import { Concierge_Expert } from "../../../assets";
import { AgentPO } from "../../../dataObject";
import { AppConstant, SRXColor } from "../../../constants";
import { CommonUtil, ObjectUtil } from "../../../utils";
import { Spacing } from "../../../styles";
import { ChatService } from "../../../services";
import Moment from "moment";

class AgentHomeListItem extends Component {
  constructor(props) {
    super(props);
  }

  createChatRoom = () => {
    const { agentInfo } = this.props;
    let agentId = agentInfo.userId.toString();
    agentInfo.userId = agentId;
    this.props.viewChatRoom(agentInfo);
  };

  render() {
    const { agentInfo } = this.props;
    // imageUri = CommonUtil.handleImageUrl(agentInfo.photo);
    return (
      <View style={{ marginRight: Spacing.XS }}>
        <TouchableOpacity onPress={() => this.createChatRoom()}>
          <Avatar
            name={agentInfo.name}
            imageUrl={agentInfo.photo}
            size={50}
            borderColor={"#DCDCDC"}
            textSize={25}
          />
          {/* <Image
            source={{ uri: imageUri }}
            style={{
              width: 50,
              height: 50,
              borderRadius: 25,
              borderWidth: 1,
              borderColor: "#DCDCDC",
              alignSelf: "center"
            }}
            resizeMode={"cover"}
          /> */}
          <SmallBodyText
            style={{ width: 80, textAlign: "center", color: SRXColor.TextLink }}
            numberOfLines={2}
          >
            {agentInfo.name}
          </SmallBodyText>
        </TouchableOpacity>
      </View>
    );
  }
}

AgentHomeListItem.propTypes = {
  agentInfo: PropTypes.object.isRequired,
  userPO: PropTypes.object.isRequired,
  viewChatRoom: PropTypes.func.isRequired
};

export { AgentHomeListItem };
