import React, { Component } from "react";
import { StyleSheet, View } from "react-native";
import { BodyText } from "../../../components";
import { SRXColor } from "../../../constants";
import { Spacing } from "../../../styles";
import { ObjectUtil } from "../../../utils";
import PropTypes from "prop-types";
import { AgentAwardPO } from "../../../dataObject";

class AgentAwardListItem extends Component {
  static propTypes = {
    awardPO: PropTypes.instanceOf(AgentAwardPO)
  };

  static defaultProps = {
    awardPO: null
  };

  render() {
    const { awardPO } = this.props;
    if (!ObjectUtil.isEmpty(awardPO)) {
      return (
        <View
          style={{
            flex: 1,
            paddingHorizontal: Spacing.M,
            paddingTop: Spacing.M
          }}
        >
          <View
            style={[
              styles.containerStyle,
              styles.containerBorder,
              {
                paddingVertical: Spacing.M,
                paddingHorizontal: Spacing.S,
                justifyContent: "center"
              }
            ]}
          >
            <BodyText>{awardPO.description}</BodyText>
          </View>
        </View>
      );
    }
  }
}

const styles = StyleSheet.create({
  containerStyle: {
    backgroundColor: SRXColor.White,
    overflow: "hidden",
    height: 61,
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

export { AgentAwardListItem };
