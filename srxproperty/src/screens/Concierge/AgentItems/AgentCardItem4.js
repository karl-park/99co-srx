import React, { Component } from "react";
import { View, Image, TouchableOpacity, Dimensions } from "react-native";
import PropTypes from "prop-types";
import { Placeholder_Agent } from "../../../assets";
import { BodyText } from "../../../components";
import { SRXColor } from "../../../constants";
import { AgentPO } from "../../../dataObject";
import { ObjectUtil } from "../../../utils";
import { Spacing } from "../../../styles";

var { height, width } = Dimensions.get("window");

class AgentCardItem4 extends Component {
  onAgentSelected = () => {
    const { agentPO, onItemSelected } = this.props;
    if (onItemSelected) {
      onItemSelected(agentPO);
    }
  };

  renderImage() {
    const { agentPO } = this.props;
    const imageUrl = agentPO.getAgentPhoto();
    return (
      <Image
        style={{
          height: 60,
          width: 60,
          borderRadius: 30,
          borderWidth: 1,
          borderColor: SRXColor.LightGray,
          marginRight: Spacing.S
        }}
        defaultSource={Placeholder_Agent}
        source={{ uri: imageUrl }}
      />
    );
  }

  renderAgentInfo() {
    const { agentPO } = this.props;
    return (
      <View style={{ width: (width - 120) }}>
        <BodyText
          style={{
            color: SRXColor.Black,
            paddingHorizontal: Spacing.XS / 2,
            lineHeight: 22
          }}
        >
          Hi, I'm{" "}
          <BodyText
            style={{
              fontWeight: "600"
            }}
          >
            {agentPO.name}
          </BodyText>
          {"\n"}
          {agentPO.eyecatcherMsg}
        </BodyText>
      </View>
    );
  }

  render() {
    const { agentPO } = this.props;
    if (!ObjectUtil.isEmpty(agentPO) && agentPO instanceof AgentPO) {
      return (
        <TouchableOpacity
          style={{
            flexDirection: "row",
            paddingLeft: Spacing.M,
            paddingRight: Spacing.M,
            alignItems: "center"
          }}
          onPress={this.onAgentSelected}
        >
          {this.renderImage()}
          {this.renderAgentInfo()}
        </TouchableOpacity>
      );
    } else {
      return <View />;
    }
  }
}

AgentCardItem4.propTypes = {
  agentPO: PropTypes.instanceOf(AgentPO),
  onItemSelected: PropTypes.func
};

export { AgentCardItem4 };
