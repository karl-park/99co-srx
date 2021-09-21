import React, { Component } from "react";
import {
  View,
  Image,
  Dimensions,
  TouchableHighlight,
  Linking,
  Platform
} from "react-native";
import PropTypes from "prop-types";
import {
  Button,
  FeatherIcon,
  Heading2,
  SmallBodyText
} from "../../../../components";
import { SRXColor } from "../../../../constants";
import { AgentPO } from "../../../../dataObject";
import { Spacing, Typography } from "../../../../styles";
import { XValueService } from "../../../../services";
import { PermissionUtil, ObjectUtil } from "../../../../utils";

const isIOS = Platform.OS === "ios";

var { height, width } = Dimensions.get("window");

/**
 * content size = width - Spacing.M*2
 */

class FeaturedAgentItem extends Component {
  static propTypes = {
    item: PropTypes.instanceOf(AgentPO),
    onSelected: PropTypes.func
  };

  constructor(props) {
    super(props);

    this.agentMobileClicked = this.agentMobileClicked.bind(this);
    this.callAgent = this.callAgent.bind(this);
  }

  agentMobileClicked() {
    const { item } = this.props;

    XValueService.trackAgentAdViewMobileInXValue({
      agentPO: item,
      source: "M",
      viewId: 230 //230 = x-value
    }); //no need response

    if (isIOS) {
      this.callAgent();
    } else {
      this.requestAndroidCallPermission();
    }
  }

  callAgent() {
    const { item } = this.props;
    if (
      !ObjectUtil.isEmpty(item) &&
      !ObjectUtil.isEmpty(item.getMobileNumber())
    ) {
      const url = "tel:" + item.getMobileNumber();

      Linking.canOpenURL(url)
        .then(supported => {
          if (!supported) {
            console.log(
              "Featured Agent Cell - XValue - call - Can't handle url: " + url
            );
          } else {
            return Linking.openURL(url);
          }
        })
        .catch(err =>
          console.error(
            "Featured Agent Cell - XValue - An error occurred - call agent - ",
            err
          )
        );
    }
  }

  requestAndroidCallPermission = () => {
    PermissionUtil.requestAndroidCallPermission().then(granted => {
      if (granted === PermissionsAndroid.RESULTS.GRANTED) {
        this.callAgent();
      }
    });
  };

  renderLeftContent() {
    const { item } = this.props;
    return (
      <View style={{ marginRight: Spacing.XS }}>
        <Image
          style={{
            width: 46,
            height: 46,
            borderRadius: 23,
            borderWidth: 1,
            borderColor: "#DCDCDC"
          }}
          source={{ uri: item.getAgentPhoto() }}
          resizeMode={"cover"}
        />
        <Image
          style={{ marginBottom: Spacing.XS, height: 25 }}
          source={{ uri: item.getAgencyLogoURL() }}
          resizeMode={"contain"}
        />
      </View>
    );
  }

  renderRightContent() {
    const { item } = this.props;
    return (
      <View style={{ flex: 1 }}>
        <Heading2 numberOfLines={1}>{item.name}</Heading2>
        <Button
          buttonStyle={{ paddingVertical: Spacing.XS / 2 }}
          textStyle={[Typography.SmallBody]}
          leftView={
            <FeatherIcon
              name="phone"
              size={12}
              color={SRXColor.Teal}
              style={{ marginRight: Spacing.XS / 2 }}
            />
          }
          onPress={this.agentMobileClicked}
        >
          Call Agent
        </Button>
        {this.renderAgentNumOfListings()}
      </View>
    );
  }

  renderAgentNumOfListings() {
    const { item } = this.props;
    if (item.numOfListings > 0) {
      return (
        <SmallBodyText
          style={{
            marginTop: Spacing.S,
            textAlign: "right",
            color: SRXColor.Teal
          }}
        >
          {item.numOfListings +
            (item.numOfListings > 1 ? " Listings" : "Listing")}
        </SmallBodyText>
      );
    }
  }

  render() {
    var itemWidth = (width - Spacing.M * 3) / 2;
    const { item, onSelected } = this.props;
    return (
      <TouchableHighlight
        style={{
          borderWidth: 1,
          borderColor: "#DCDCDC",
          borderRadius: Spacing.XS / 2,
          backgroundColor: "white",
          shadowColor: "rgb(110,129,154)",
          shadowOffset: { height: 1, width: 1 },
          shadowOpacity: 0.32,
          shadowRadius: 1,
          marginRight: Spacing.M,
          marginBottom: Spacing.M,
          width: itemWidth
        }}
        onPress={() => {
          if (onSelected) onSelected(item);
        }}
      >
        <View
          style={{
            backgroundColor: "white",
            flexDirection: "row",
            padding: Spacing.XS,
            paddingBottom: Spacing.XS / 2,
            overflow: "hidden"
          }}
        >
          {this.renderLeftContent()}
          {this.renderRightContent()}
        </View>
      </TouchableHighlight>
    );
  }
}

export { FeaturedAgentItem };
