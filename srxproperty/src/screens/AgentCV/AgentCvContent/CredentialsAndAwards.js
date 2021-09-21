import React, { Component } from "react";
import { View, Linking } from "react-native";
import { BodyText, Button, FeatherIcon, Html } from "../../../components";
import { Spacing, Typography } from "../../../styles";
import { SRXColor } from "../../../constants";
import PropTypes from "prop-types";
import { StringUtil } from "../../../utils";

class CredentialsAndAwards extends Component {
  static propTypes = {
    credentialAwards: PropTypes.string
  };

  static defaultProps = {
    credentialAwards: ""
  };

  state = {
    showFullContent: false
  };

  renderShowMoreButton() {
    const { showFullContent } = this.state;
    return (
      <View style={{ alignItems: "flex-end", paddingTop: Spacing.M }}>
        <Button
          textStyle={[
            Typography.SmallBody,
            { color: SRXColor.Teal, marginRight: 4 }
          ]}
          rightView={
            <FeatherIcon
              name={showFullContent ? "chevron-up" : "chevron-down"}
              size={16}
              color={SRXColor.Teal}
            />
          }
          onPress={() => {
            this.setState({ showFullContent: !showFullContent });
          }}
        >
          {showFullContent ? "Close" : "Show more"}
        </Button>
      </View>
    );
  }

  render() {
    const { showFullContent } = this.state;
    const { style, credentialAwards } = this.props;
    const credential_Awards =
      "<p>" + StringUtil.replace(credentialAwards, "\n", "<br>");
    +"</p>";

    return (
      <View
        style={[
          {
            paddingHorizontal: Spacing.M,
            paddingTop: Spacing.M,
            paddingBottom: Spacing.L,
            borderBottomWidth: 1,
            borderBottomColor: "#e0e0e0"
          },
          style
        ]}
      >
        {showFullContent ? (
          <Html
            html={credential_Awards}
            tagsStyles={styles}
            onLinkPress={(event, href) => {
              Linking.openURL(href);
            }}
          />
        ) : (
          <BodyText style={{ lineHeight: 26 }} numberOfLines={4}>
            {credentialAwards}
          </BodyText>
        )}
        {this.renderShowMoreButton()}
      </View>
    );
  }
}

const styles = {
  p: {
    lineHeight: 26,
    ...Typography.Body
  }
};

export { CredentialsAndAwards };
