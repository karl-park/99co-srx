import React, { Component } from "react";
import {
  View,
  TouchableHighlight,
  Platform,
  Image,
  Alert,
  StyleSheet
} from "react-native";
import { Navigation } from "react-native-navigation";
import PropTypes from "prop-types";
import { Placeholder_Agent } from "../../../assets";
import {
  Avatar,
  Heading2,
  ExtraSmallBodyText,
  BodyText,
  Text,
  FeatherIcon
} from "../../../components";
import { Concierge_Expert } from "../../../assets";
import { AgentPO } from "../../../dataObject";
import { SRXColor, AppConstant } from "../../../constants";
import { ObjectUtil, CommonUtil } from "../../../utils";
import { Spacing } from "../../../styles";
import { ChatService } from "../../../services";
import Moment from "moment";
const isIOS = Platform.OS === "ios";
const isToday = someDate => {
  const today = new Date();
  return Moment(today).isSame(someDate, "day");
};

const isLastWeek = someDate => {
  const today = new Date();
  let res = Moment(today).diff(someDate, "days") <= 7;
  console.log(res);
  return res;
};

class ChatListItem extends Component {
  constructor(props) {
    super(props);
  }

  viewChatRoom = () => {
    const { agentInfo } = this.props;
    this.props.viewChatRoom(agentInfo);
  };

  render() {
    const { agentInfo } = this.props;
    const { dateSent } = agentInfo;
    let isDateToday = isToday(dateSent);
    let isWithinAWeek = isLastWeek(dateSent);
    let imageUri = CommonUtil.handleImageUrl(agentInfo.otherUserPhoto);
    return (
      <TouchableHighlight onPress={() => this.viewChatRoom()}>
        <View
          style={{
            backgroundColor: SRXColor.White,
            paddingHorizontal: Spacing.M,
            paddingVertical: Spacing.S,
            flexDirection: "row"
          }}
        >
          <View style={styles.userInitialNameCirlce}>
          <Avatar
            name={agentInfo.name}
            imageUrl={agentInfo.otherUserPhoto}
            size={50}
            borderColor={"#DCDCDC"}
            textSize={25}
          />
            {/* {ObjectUtil.isEmpty(imageUri) ? (
              <Text style={{ color: SRXColor.White, fontSize: 25 }}>
                {agentInfo.name.charAt(0).toUpperCase()}
              </Text>
            ) : (
              <Image
                source={{ uri: imageUri }}
                style={{
                  width: 50,
                  height: 50,
                  borderRadius: 25,
                  borderWidth: 1,
                  borderColor: "#DCDCDC"
                }}
                resizeMode={"cover"}
              />
            )} */}
          </View>

          <View style={{ flex: 1 }}>
            <View
              style={{ flexDirection: "row", justifyContent: "space-between" }}
            >
              <Heading2>{agentInfo.name}</Heading2>
              <View style={{ flexDirection: "row" }}>
                {agentInfo.unreadCount > 0 ? (
                  <View
                    style={{
                      width: 25,
                      height: 25,
                      borderRadius: 12,
                      backgroundColor: SRXColor.Red,
                      marginRight: Spacing.XS
                    }}
                  >
                    <BodyText
                      style={[
                        {
                          textAlign: "center",
                          justifyContent: "center",
                          color: SRXColor.White
                        },
                        isIOS ? { paddingTop: 2 } : null
                      ]}
                    >
                      {agentInfo.unreadCount}
                    </BodyText>
                  </View>
                ) : null}
                <Text style={{ marginRight: Spacing.XS, color: SRXColor.Gray }}>
                  {Moment(dateSent).calendar(null, {
                    sameDay: "hh:mmA",
                    nextDay: "[Tomorrow]",
                    nextWeek: "dddd",
                    lastDay: "[Yesterday]",
                    lastWeek: "dddd",
                    sameElse: "DD MMM YY"
                  })}
                </Text>
                <FeatherIcon
                  name={"chevron-right"}
                  size={20}
                  style={{ alignSelf: "center" }}
                />
              </View>
            </View>

            <Text numberOfLines={2}>{agentInfo.message}</Text>
          </View>
        </View>
      </TouchableHighlight>
    );
  }
}
const styles = StyleSheet.create({
  userInitialNameCirlce: {
    justifyContent: "center",
    alignSelf: "center",
    alignItems: "center",
    width: 50,
    height: 50,
    backgroundColor: "#F0E68C",
    borderRadius: 25,
    marginRight: Spacing.S
  }
});

ChatListItem.propTypes = {
  agentInfo: PropTypes.object.isRequired,
  userPO: PropTypes.object.isRequired,
  viewChatRoom: PropTypes.func.isRequired
};

export { ChatListItem };
