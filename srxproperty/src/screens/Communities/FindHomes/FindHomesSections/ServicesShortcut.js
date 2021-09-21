import React, { Component } from "react";
import { View, TouchableOpacity, Image } from "react-native";
import { Spacing } from "../../../../styles";
import { SRXColor } from "../../../../constants";
import PropTypes from "prop-types";
import { SmallBodyText } from "../../../../components";
import {
  Concierge_MainMenuIcon,
  HomeScreen_Loans,
  HomeScreen_News,
  HomeScreen_Valuations
} from "../../../../assets";

class ServicesShortcut extends Component {
  static propTypes = {
    viewFindAgent: PropTypes.func,
    directToValuationsPage: PropTypes.func,
    directToMortgagesPage: PropTypes.func,
    directToPropertyNews: PropTypes.func
  };

  //go to find agent module
  viewFindAgent = () => {
    const { viewFindAgent } = this.props;
    if (viewFindAgent) {
      viewFindAgent();
    }
  };

  directToValuationsPage = () => {
    const { directToValuationsPage } = this.props;
    if (directToValuationsPage) {
      directToValuationsPage();
    }
  };

  directToMortgagesPage = () => {
    const { directToMortgagesPage } = this.props;
    if (directToMortgagesPage) {
      directToMortgagesPage();
    }
  };

  directToPropertyNews = () => {
    const { directToPropertyNews } = this.props;
    if (directToPropertyNews) {
      directToPropertyNews();
    }
  };

  renderFindAgent() {
    return (
      <TouchableOpacity
        onPress={() => this.viewFindAgent()}
        style={{
          justifyContent: "center",
          alignItems: "center",
          flex: 1
        }}
      >
        <Image
          style={{ width: 48, height: 45 }}
          source={Concierge_MainMenuIcon}
          resizeMode={"contain"}
        />
        <SmallBodyText>Find Agent</SmallBodyText>
      </TouchableOpacity>
    );
  }

  renderValuations() {
    return (
      <TouchableOpacity
        onPress={() => this.directToValuationsPage()}
        style={{
          justifyContent: "center",
          alignItems: "center",
          flex: 1
        }}
      >
        <Image
          style={{ width: 48, height: 45 }}
          source={HomeScreen_Valuations}
          resizeMode={"contain"}
        />
        <SmallBodyText>Valuations</SmallBodyText>
      </TouchableOpacity>
    );
  }

  renderLoans() {
    return (
      <TouchableOpacity
        onPress={() => this.directToMortgagesPage()}
        style={{
          justifyContent: "center",
          alignItems: "center",
          flex: 1
        }}
      >
        <Image
          style={{ width: 48, height: 45 }}
          source={HomeScreen_Loans}
          resizeMode={"contain"}
        />
        <SmallBodyText>Loans</SmallBodyText>
      </TouchableOpacity>
    );
  }

  renderNews() {
    return (
      <TouchableOpacity
        onPress={() => this.directToPropertyNews()}
        style={{
          justifyContent: "center",
          alignItems: "center",
          flex: 1
        }}
      >
        <Image
          style={{ width: 48, height: 45 }}
          source={HomeScreen_News}
          resizeMode={"contain"}
        />
        <SmallBodyText>News</SmallBodyText>
      </TouchableOpacity>
    );
  }

  render() {
    return (
      <View style={Styles.servicesShortcutContainer}>
        {this.renderFindAgent()}
        {this.renderValuations()}
        {this.renderLoans()}
        {this.renderNews()}
      </View>
    );
  }
}

const Styles = {
  servicesShortcutContainer: {
    flex: 1,
    flexDirection: "row",
    justifyContent: "center",
    backgroundColor: SRXColor.White,
    borderRadius: 10,
    //padding
    paddingVertical: Spacing.L,
    paddingHorizontal: Spacing.XS,
    //margin
    marginBottom: Spacing.S
  }
};

export { ServicesShortcut };
