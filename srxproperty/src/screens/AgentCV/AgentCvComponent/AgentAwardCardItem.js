import React, { Component } from "react";
import { StyleSheet, View } from "react-native";
import { BodyText } from "../../../components";
import { SRXColor } from "../../../constants";
import { Spacing } from "../../../styles";
import { ObjectUtil } from "../../../utils";
import PropTypes from "prop-types";
import { AgentAwardPO } from "../../../dataObject";

class AgentAwardCardItem extends Component {
  static propTypes = {
    agentAwardPO: PropTypes.instanceOf(AgentAwardPO)
  };

  static defaultProps = {
    agentAwardPO: null
  };

  render() {
    const { agentAwardPO } = this.props;
    if (!ObjectUtil.isEmpty(agentAwardPO)) {
      return (
        <View style={[styles.containerStyle, styles.containerBorder]}>
          <View style={{ backgroundColor: SRXColor.White, padding: 29 }}>
            <BodyText
              style={{ lineHeight: 22, textAlign: "center" }}
              numberOfLines={3}
            >
              {agentAwardPO.description}
            </BodyText>
          </View>
        </View>
      );
    } else {
      return <View />;
    }
  }
}

const styles = StyleSheet.create({
  containerStyle: {
    backgroundColor: SRXColor.White,
    overflow: "hidden",
    width: 136 + Spacing.XS * 2,
    height: 129,
    shadowColor: "rgb(110,129,154)",
    shadowOffset: { width: 1, height: 2 },
    shadowOpacity: 0.32,
    shadowRadius: 1
  },
  containerBorder: {
    borderRadius: 5,
    borderWidth: 1,
    borderColor: "#e0e0e0"
  }
});

export { AgentAwardCardItem };
