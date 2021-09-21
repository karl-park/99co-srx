import React, { Component } from "react";
import { StyleSheet, View } from "react-native";
import { Navigation } from "react-native-navigation";

import { FontAwesomeIcon, BodyText, Button } from "../../../components";
import { Spacing, Typography } from "../../../styles";
import { SRXColor } from "../../../constants";

class NoShortlistScreen extends Component {
  constructor(props) {
    super(props);
  }

  onPressStartSearching() {
    const { componentId } = this.props;
    Navigation.mergeOptions(componentId, {
      bottomTabs: {
        currentTabIndex: 0
      }
    });
  }

  render() {
    return (
      <View style={{ flex: 1, alignItems: "center", justifyContent: "center" }}>
        <BodyText style={styles.textStyle}>
          {"You have not shortlisted any \n properties yet"}
        </BodyText>

        <FontAwesomeIcon name={"heart"} size={35} color={SRXColor.Teal} />

        <Button
          onPress={() => this.onPressStartSearching()}
          textStyle={[
            Typography.SmallBody,
            { color: SRXColor.Teal, marginVertical: Spacing.M }
          ]}
        >
          Start searching
        </Button>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  textStyle: {
    lineHeight: 22,
    textAlign: "center",
    marginVertical: Spacing.M
  }
});

export { NoShortlistScreen };
