import React, { Component } from "react";
import { View } from "react-native";
import PropTypes from "prop-types";
import { BodyText, Button, FeatherIcon } from "../../../../components";
import { SRXColor } from "../../../../constants";
import { AgentPO } from "../../../../dataObject";
import { Spacing } from "../../../../styles";
import { PropertyTypeUtil, ObjectUtil, StringUtil } from "../../../../utils";
import { FeaturedAgentItem } from "./FeaturedAgentItem";

/**
 * content size = width - Spacing.M*2
 */

class FeaturedAgents extends Component {

  static propTypes = {
    featuredAgentPOs: PropTypes.arrayOf(PropTypes.instanceOf(AgentPO)),
    onAgentSelected: PropTypes.func
  }

  static defaultProps = {
    featuredAgentPOs: [],
  };

  renderAgentList() {
    const { featuredAgentPOs, onAgentSelected } = this.props;
    return (
      <View style={{ paddingLeft: Spacing.M, marginTop: Spacing.M, backgroundColor: "transparent", flexDirection: "row", flexWrap: "wrap", alignItems: "stretch" }}>
        {featuredAgentPOs.map((item) => {
          return (
            <FeaturedAgentItem 
              item={item} 
              key={item.email}
              onSelected={selectedAgent => {
                if (onAgentSelected) onAgentSelected(selectedAgent);
              }} 
              />
          );
        })}
      </View>
    );
  }

  render() {
    const { style } = this.props;

    return (
      <View style={[{ paddingVertical: Spacing.M },style]}>
        <BodyText style={{marginHorizontal: Spacing.M}}>Get an X-Value Transaction Report with unit details from one of our Featured Agents</BodyText>
        {this.renderAgentList()}
      </View>
    );
  }
}

export { FeaturedAgents };
