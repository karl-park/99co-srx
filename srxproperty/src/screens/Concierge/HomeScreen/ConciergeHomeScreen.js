import React, { Component } from "react";
import { View, SafeAreaView, Image, Dimensions } from "react-native";
import { Navigation } from "react-native-navigation";
import { Concierge_MenuHeader } from "../../../assets";
import { Button, FeatherIcon } from "../../../components";
import { Spacing, Typography } from "../../../styles";
import { AgentDirectory } from "../AgentDirectory";
import { SRXColor } from "../../../constants";
import { ConciergeMenuItem } from "../Components";
import { Concierge_Menu } from "../Constants";

var { height, width } = Dimensions.get("window");

class ConciergeHomeScreen extends Component {
  static options(passProps) {
    return {
      topBar: {
        title: {
          text: "Find Agent"
        }
      },
      layout: {
        backgroundColor: "white"
      }
    };
  }

  directToAgentDirectory = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: "ConciergeStack.AgentDirectory",
        passProps: {
          source: AgentDirectory.Sources.ConciergeHome
        }
      }
    });
  };

  directToConciergeForm = selectedMenu => {
    Navigation.push(this.props.componentId, {
      component: {
        name: "ConciergeStack.ConciergeMainForm",
        passProps: {
          selectedMenu
        }
      }
    });
  };

  renderHeader() {
    return (
      <View style={{ flex: 1 }}>
        <Image
          source={Concierge_MenuHeader}
          style={{
            width: "100%",
            height: "100%"
          }}
          resizeMode={"cover"}
        />
      </View>
    );
  }

  renderConciergeMenus() {
    return (
      <View style={{ flex: 1 }}>
        <ConciergeMenuItem
          itemLabel={Concierge_Menu.findAgent}
          onPress={() => this.directToConciergeForm(Concierge_Menu.findAgent)}
        />
        <ConciergeMenuItem
          itemLabel={Concierge_Menu.ownAgent}
          onPress={() => this.directToConciergeForm(Concierge_Menu.ownAgent)}
        />
        <ConciergeMenuItem
          itemLabel={Concierge_Menu.agentDirectory}
          onPress={this.directToAgentDirectory}
        />
      </View>
    );
  }

  renderAgentDirectory() {
    return (
      <View
        style={{
          alignItems: "center",
          paddingVertical: Spacing.S,
          paddingHorizontal: Spacing.M
        }}
      >
        <Button
          textStyle={[
            Typography.SmallBody,
            { color: SRXColor.Teal, marginLeft: Spacing.XS }
          ]}
          leftView={
            <FeatherIcon name="users" size={20} color={SRXColor.Teal} />
          }
          onPress={this.directToAgentDirectory}
        >
          View agent directory
        </Button>
      </View>
    );
  }

  render() {
    return (
      <SafeAreaView style={{ flex: 1, backgroundColor: "white" }}>
        {/* <ScrollView style={{ flex: 1 }} bounces={false}> */}
        <View style={{ flex: 1 }}>
          {this.renderHeader()}
          {this.renderConciergeMenus()}
        </View>
        {/* </ScrollView> */}
      </SafeAreaView>
    );
  }
}

export default ConciergeHomeScreen;
