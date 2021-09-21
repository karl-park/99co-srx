import React, { Component } from "react";
import { StyleSheet, View, Image, TouchableOpacity } from "react-native";

import { BodyText, FeatherIcon } from "../../../components";
import { Spacing } from "../../../styles";
import { SRXColor } from "../../../constants";
import { Placeholder_Agent } from "../../../assets";

class ShortlistedAgent extends Component {
  constructor(props) {
    super(props);
  }

  renderLookingForAgent() {
    return (
      <View style={{ flex: 1, flexDirection: "row" }}>
        <View style={styles.chooseCheckLeftContainer}>
          <BodyText>Looking for the agent best suited to served you?</BodyText>
        </View>
        <View style={styles.chooseCheckRightContainer}>
          <View style={styles.crossCheckContainer}>
            <TouchableOpacity>
              <FeatherIcon name="x" size={25} color={"black"} />
            </TouchableOpacity>
          </View>
          <View style={styles.crossCheckContainer}>
            <TouchableOpacity>
              <FeatherIcon name="check" size={25} color={"black"} />
            </TouchableOpacity>
          </View>
        </View>
      </View>
    );
  }

  renderChooseAgent() {
    return (
      <View style={{ flex: 1, flexDirection: "row" }}>
        <View
          style={[
            styles.chooseCheckLeftContainer,
            { justifyContent: "center" }
          ]}
        >
          <BodyText>Choose your agent</BodyText>
        </View>
        <View style={styles.chooseCheckRightContainer}>
          <View style={styles.crossCheckContainer}>
            <TouchableOpacity>
              <Image
                style={styles.agentImageStyle}
                defaultSource={Placeholder_Agent}
                source={Placeholder_Agent}
                resizeMode={"cover"}
              />
            </TouchableOpacity>
          </View>
          <View style={styles.crossCheckContainer}>
            <TouchableOpacity>
              <Image
                style={styles.agentImageStyle}
                defaultSource={Placeholder_Agent}
                source={Placeholder_Agent}
                resizeMode={"cover"}
              />
            </TouchableOpacity>
          </View>
        </View>
      </View>
    );
  }

  renderConnectedAgent() {
    return (
      <View style={{ flex: 1, flexDirection: "row" }}>
        <View
          style={[styles.chooseCheckLeftContainer, { flexDirection: "row" }]}
        >
          <Image
            style={styles.agentImageStyle}
            defaultSource={Placeholder_Agent}
            source={Placeholder_Agent}
            resizeMode={"cover"}
          />
          <BodyText style={{ marginLeft: 3, paddingRight: Spacing.XS }}>
            Your connected agent will contact you
          </BodyText>
        </View>
        <View style={styles.chooseCheckRightContainer}>
          <View style={styles.crossCheckContainer}>
            <TouchableOpacity>
              <BodyText style={{ color: SRXColor.Teal }}>What's next?</BodyText>
            </TouchableOpacity>
          </View>
        </View>
      </View>
    );
  }

  renderAppointment() {
    return (
      <View style={{ flex: 1, flexDirection: "row" }}>
        <View
          style={[styles.chooseCheckLeftContainer, { flexDirection: "row" }]}
        >
          <FeatherIcon name="watch" size={25} color={"black"} />
          <BodyText style={{ marginLeft: Spacing.XS }}>
            Tuesday May 8th at 4pm
          </BodyText>
        </View>
        <View style={styles.chooseCheckRightContainer}>
          <View style={styles.crossCheckContainer}>
            <TouchableOpacity>
              <BodyText style={{ color: SRXColor.Teal }}>What's next?</BodyText>
            </TouchableOpacity>
          </View>
        </View>
      </View>
    );
  }

  renderOffer() {
    return (
      <View style={{ flex: 1, flexDirection: "row" }}>
        <View
          style={[styles.chooseCheckLeftContainer, { flexDirection: "row" }]}
        >
          <BodyText style={{ marginLeft: Spacing.XS }}>Deal closed</BodyText>
          <View
            style={{
              flexDirection: "row",
              justifyContent: "flex-end",
              flex: 1
            }}
          >
            <TouchableOpacity>
              <BodyText style={{ color: SRXColor.Teal }}>Tap to rate</BodyText>
            </TouchableOpacity>
          </View>
        </View>
        <View style={styles.chooseCheckRightContainer}>
          <View style={styles.crossCheckContainer}>
            <TouchableOpacity>
              <BodyText style={{ color: SRXColor.Teal }}>What's next?</BodyText>
            </TouchableOpacity>
          </View>
        </View>
      </View>
    );
  }

  renderShortlistedAgentConnect() {
    //stage is just test data for UI. will remove after concierge agent
    const { stage } = this.props;
    if (stage == 1) {
      return (
        <View style={styles.container}>{this.renderLookingForAgent()}</View>
      );
    } else if (stage == 2) {
      return <View style={styles.container}>{this.renderChooseAgent()}</View>;
    } else if (stage == 3) {
      return (
        <View style={styles.container}>{this.renderConnectedAgent()}</View>
      );
    } else if (stage == 4) {
      return <View style={styles.container}>{this.renderAppointment()}</View>;
    } else if (stage == 5) {
      return <View style={styles.container}>{this.renderOffer()}</View>;
    }
  }

  render() {
    return (
      <View style={{ flex: 1 }}>{this.renderShortlistedAgentConnect()}</View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#FFFFFF",

    borderWidth: 1,
    borderColor: "#E3E3E3",
    borderRadius: 5,

    shadowRadius: 4,
    shadowColor: "#E3E3E3"
  },

  chooseCheckRightContainer: {
    flex: 1,
    flexDirection: "row",
    justifyContent: "flex-end"
  },
  chooseCheckLeftContainer: {
    flex: 2,
    padding: Spacing.S,
    alignItems: "center"
  },

  crossCheckContainer: {
    borderLeftWidth: 1,
    borderLeftColor: "#E0E0E0",
    padding: Spacing.S,
    alignItems: "center",
    justifyContent: "center"
  },
  agentImageStyle: {
    borderWidth: 1,
    borderColor: "rgba(0,0,0,0.2)",
    alignItems: "center",
    justifyContent: "center",
    width: 40,
    height: 40,
    backgroundColor: "#fff",
    borderRadius: 100
  }
});

export { ShortlistedAgent };
