import React, { Component } from "react";
import {
  StyleSheet,
  View,
  TouchableHighlight,
  Image,
  Platform,
  PermissionsAndroid,
  Linking
} from "react-native";
import { Navigation } from "react-native-navigation";

import {
  Heading2,
  SmallBodyText,
  FeatherIcon,
  Button
} from "../../../components";
import { TrackingService } from "../../../services";
import { SRXColor } from "../../../constants";
import { ObjectUtil, PermissionUtil } from "../../../utils";
import { Spacing, Typography } from "../../../styles";

const isIOS = Platform.OS === "ios";

class AgentCardItem extends Component {
  constructor(props) {
    super(props);

    this.agentMobileClicked = this.agentMobileClicked.bind(this);
    this.viewAgentCV = this.viewAgentCV.bind(this);
  }

  agentMobileClicked() {
    const { agentItem } = this.props;

    if (!ObjectUtil.isEmpty(agentItem)) {
      //Tracking
      TrackingService.trackAgentAdViewMobile({
        agentUserId: agentItem.userId,
        agentBookingId: agentItem.trackingId,
        viewId: 1 //For listing search
      });

      if (isIOS) {
        this.callAgent();
      } else {
        this.requestAndroidCallPermission();
      }
    } //end of validate
  }

  callAgent() {
    const { agentItem } = this.props;
    if (
      !ObjectUtil.isEmpty(agentItem) &&
      !ObjectUtil.isEmpty(agentItem.getMobileNumber())
    ) {
      const url = "tel:" + agentItem.getMobileNumber();

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

  //View Agent CV
  viewAgentCV = () => {
    const { componentId, agentItem } = this.props;
    //api tracking for viewing agent cv
    if (componentId) {
      Navigation.push(componentId, {
        component: {
          name: "ConciergeStack.AgentCV",
          passProps: {
            agentUserId: agentItem.id
          }
        }
      });
    }
  };

  //Left Content of featured agent item
  renderLeftContent() {
    return (
      <View style={{ marginRight: Spacing.S }}>
        {this.renderAgentPhoto()}
        {this.renderAgencyLogo()}
      </View>
    );
  }

  //Agent Photo
  renderAgentPhoto() {
    const { agentItem } = this.props;
    let agentPhotoURL = agentItem.getAgentPhoto();
    if (!ObjectUtil.isEmpty(agentPhotoURL)) {
      return (
        <Image
          style={styles.featureAgentImage}
          source={{ uri: agentPhotoURL }}
          resizeMode={"cover"}
        />
      );
    }
  }

  //Agency Logo
  renderAgencyLogo() {
    const { agentItem } = this.props;
    let agencyPhotoURL = agentItem.getAgencyLogoURL();
    if (!ObjectUtil.isEmpty(agencyPhotoURL)) {
      return (
        <Image
          style={{
            width: 50,
            height: 35,
            marginVertical: Spacing.XS / 2
          }}
          source={{ uri: agencyPhotoURL }}
          resizeMode={"contain"}
        />
      );
    }
  }

  //Right Content of featured agent item
  renderRightContent() {
    return (
      <View style={{ flex: 1 }}>
        {this.renderAgentName()}
        {this.renderAgentSubscription()}
        <View
          style={{
            flexDirection: "row",
            position: "absolute",
            bottom: Spacing.XS / 2
          }}
        >
          {this.renderAgentMobileNumber()}
        </View>
      </View>
    );
  }

  //Agent Name
  renderAgentName() {
    const { agentItem } = this.props;
    if (!ObjectUtil.isEmpty(agentItem.name)) {
      return (
        <Heading2 style={{ lineHeight: 22 }} numberOfLines={1}>
          {agentItem.name}
        </Heading2>
      );
    }
  }

  //Agent Subscirption Expert Analyzer
  renderAgentSubscription() {
    const { agentItem } = this.props;
    let expertSubscriptionString = agentItem.getExpertAnalyzerAgent();
    if (!ObjectUtil.isEmpty(expertSubscriptionString)) {
      return (
        <SmallBodyText style={styles.subscriptionTextStyle} numberOfLines={1}>
          {expertSubscriptionString}
        </SmallBodyText>
      );
    }
  }

  //Eye Catcher Message
  renderAgentEyeCatcherMessage() {
    const { agentItem } = this.props;
    let eyecatcherMsgString = agentItem.eyecatcherMsg;
    if (!ObjectUtil.isEmpty(eyecatcherMsgString)) {
      return (
        <SmallBodyText style={styles.subscriptionTextStyle} numberOfLines={1}>
          {eyecatcherMsgString}
        </SmallBodyText>
      );
    }
  }

  //Agent Mobile Number for Showing
  renderAgentMobileNumber() {
    const { agentItem } = this.props;
    if (!ObjectUtil.isEmpty(agentItem.getMobileNumber())) {
      return (
        <Button
          buttonStyle={{ flex: 1 }}
          textStyle={[Typography.Subtext, { color: SRXColor.Teal }]}
          leftView={
            <FeatherIcon
              name="phone"
              size={14}
              color={SRXColor.Teal}
              style={{ marginRight: Spacing.XS / 2 }}
            />
          }
          onPress={this.agentMobileClicked}
        >
          Call Agent
        </Button>
      );
    }
  }

  render() {
    const { agentItem } = this.props;
    if (!ObjectUtil.isEmpty(agentItem)) {
      return (
        <TouchableHighlight
          style={styles.featureAgentItemContainer}
          underlayColor={SRXColor.AccordionBackground}
          onPress={this.viewAgentCV}
        >
          <View style={styles.featureAgentItemSubContainer}>
            {this.renderLeftContent()}
            {this.renderRightContent()}
          </View>
        </TouchableHighlight>
      );
    } else {
      return <View />;
    }
  }
}

const styles = StyleSheet.create({
  featureAgentItemContainer: {
    borderWidth: 1,
    borderColor: "#DCDCDC",
    borderRadius: Spacing.XS / 2,
    backgroundColor: "white",
    shadowColor: "rgb(110,129,154)",
    shadowOffset: { height: 1, width: 1 },
    shadowOpacity: 0.32,
    shadowRadius: 1,
    width: 250,
    height: 100
  },
  featureAgentItemSubContainer: {
    flexDirection: "row",
    padding: Spacing.XS,
    overflow: "hidden"
  },
  featureAgentImage: {
    width: 40,
    height: 40,
    borderRadius: 20,
    borderWidth: 1,
    borderColor: "#DCDCDC"
  },
  subscriptionTextStyle: {
    fontWeight: "600",
    fontStyle: "italic",
    lineHeight: 19
  }
});

export { AgentCardItem };
