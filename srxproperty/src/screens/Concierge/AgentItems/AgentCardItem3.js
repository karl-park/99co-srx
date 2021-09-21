import React, { Component } from "react";
import { View, Image, TouchableOpacity } from "react-native";
import PropTypes from "prop-types";
import { Placeholder_Agent } from "../../../assets";
import { SmallBodyText } from "../../../components";
import { SRXColor } from "../../../constants";
import { AgentPO } from "../../../dataObject";
import { Spacing } from "../../../styles";
import { ObjectUtil } from "../../../utils";

class AgentCardItem3 extends Component {

  onAgentSelected =() => {
    const { agentPO, onItemSelected } = this.props;
    if (onItemSelected) {
      onItemSelected(agentPO);
    }
  }

  renderImage() {
    const { agentPO } = this.props;
    const imageUrl = agentPO.getAgentPhoto();
    return (
      <Image
        style={{
          height: 40,
          width: 40,
          borderRadius: 20,
          borderWidth: 1,
          borderColor: SRXColor.LightGray
        }}
        defaultSource={Placeholder_Agent}
        source={{ uri: imageUrl }}
      />
    );
  }

  renderAgentName() {
    const { agentPO } = this.props;
    return (
      <SmallBodyText style={{ marginLeft: Spacing.XS / 2, maxWidth: 100 }} numberOfLines={1}>
        {agentPO.name}
      </SmallBodyText>
    );
  }

  render() {
    const { agentPO, onItemSelected } = this.props;
    if (!ObjectUtil.isEmpty(agentPO) && agentPO instanceof AgentPO) {
      return (
        <TouchableOpacity onPress={this.onAgentSelected}>
          <View
            style={{
              flexDirection: "row",
              alignItems: "center"
            }}
          >
            {this.renderImage()}
            {this.renderAgentName()}
          </View>
        </TouchableOpacity>
      );
    } else {
      return <View />;
    }
  }
}

AgentCardItem3.propTypes = {
  agentPO: PropTypes.instanceOf(AgentPO),
  onItemSelected: PropTypes.func
};

export { AgentCardItem3 };
