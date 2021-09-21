import React, { Component } from "react";
import { View, Image } from "react-native";
import PropTypes from "prop-types";

import { Placeholder_Agent } from "../../../assets";
import {
  Heading2,
  ExtraSmallBodyText,
  BodyText,
  TouchableHighlight
} from "../../../components";
import { Concierge_Expert } from "../../../assets";
import { AgentPO } from "../../../dataObject";
import { SRXColor } from "../../../constants";
import { ObjectUtil } from "../../../utils";
import { Spacing } from "../../../styles";

class AgentListItem extends Component {
  constructor(props) {
    super(props);

    this.viewAgentCv = this.viewAgentCv.bind(this);
  }

  //direct to agent cv
  viewAgentCv = () => {
    const { agentPO, viewAgentCv } = this.props;
    if (!ObjectUtil.isEmpty(agentPO) && viewAgentCv) {
      viewAgentCv(agentPO);
    }
  };

  renderAgentImage() {
    const { agentPO } = this.props;
    return (
      <View style={{ marginRight: Spacing.S }}>
        <Image
          style={{
            width: 60,
            height: 60,
            borderRadius: 30,
            borderWidth: 1,
            borderColor: "#DCDCDC"
          }}
          defaultSource={Placeholder_Agent}
          source={{ uri: agentPO.getAgentPhoto() }}
          resizeMode={"cover"}
        />
      </View>
    );
  }

  renderAgencyAndSubscription() {
    const { agentPO } = this.props;
    const hasAgencyLogo = !ObjectUtil.isEmpty(agentPO.getAgencyLogoURL());
    if (hasAgencyLogo || isExpert) {
      return (
        <View>
          <Image
            source={{ uri: agentPO.getAgencyLogoURL() }}
            style={{ width: 50, height: 35 }}
            resizeMode={"contain"}
          />
          {this.renderExpertAgentIcon()}
        </View>
      );
    }
  }

  renderExpertAgentIcon() {
    const { agentPO } = this.props;
    const isExpert = agentPO.isExpert();
    if (isExpert) {
      return (
        <Image
          source={Concierge_Expert}
          style={{ width: 50, height: 35, marginTop: Spacing.XS }}
          resizeMode={"contain"}
        />
      );
    }
  }

  renderInfo() {
    return (
      <View style={{ flex: 1 }}>
        {this.renderAgentName()}
        {this.renderAppointment()}
        {this.renderActiveListings()}
        {this.renderCeaAndLicenseNo()}
      </View>
    );
  }

  renderAgentName() {
    const { agentPO } = this.props;
    if (!ObjectUtil.isEmpty(agentPO.name)) {
      return <Heading2 numberOfLines={1}>{agentPO.name}</Heading2>;
    }
  }

  renderAppointment() {
    const { agentPO } = this.props;
    if (!ObjectUtil.isEmpty(agentPO.getAgentCvPO())) {
      const { appointment } = agentPO.getAgentCvPO();
      if (!ObjectUtil.isEmpty(appointment)) {
        return (
          <BodyText
            numberOfLines={1}
            style={{ color: SRXColor.Gray, lineHeight: 22 }}
          >
            {appointment}
          </BodyText>
        );
      }
    }
  }

  renderActiveListings() {
    const { agentPO } = this.props;
    const activeListings = agentPO.getActiveListings();
    if (!ObjectUtil.isEmpty(activeListings)) {
      return (
        <BodyText
          numberOfLines={1}
          style={{ color: SRXColor.Teal, lineHeight: 22 }}
        >
          {activeListings}
        </BodyText>
      );
    }
  }

  renderCeaAndLicenseNo() {
    const { agentPO } = this.props;
    const content = [];
    if (!ObjectUtil.isEmpty(agentPO.ceaRegNo)) {
      content.push(agentPO.ceaRegNo);
    }
    if (!ObjectUtil.isEmpty(agentPO.licenseNumber)) {
      content.push(agentPO.licenseNumber);
    }
    if (!ObjectUtil.isEmpty(content)) {
      return (
        <ExtraSmallBodyText style={{ color: SRXColor.Gray, lineHeight: 22 }}>
          {content.join(" / ")}
        </ExtraSmallBodyText>
      );
    }
  }

  render() {
    const { contentStyle } = this.props;
    return (
      <TouchableHighlight onPress={() => this.viewAgentCv()}>
        <View
          style={[
            {
              backgroundColor: SRXColor.White,
              paddingHorizontal: Spacing.M,
              paddingVertical: Spacing.S,
              flexDirection: "row"
            },
            contentStyle
          ]}
        >
          {this.renderAgentImage()}
          {this.renderInfo()}
          {this.renderAgencyAndSubscription()}
        </View>
      </TouchableHighlight>
    );
  }
}

AgentListItem.propTypes = {
  agentPO: PropTypes.instanceOf(AgentPO),
  /** Direct to Agent CV */
  viewAgentCv: PropTypes.func
};

export { AgentListItem };
