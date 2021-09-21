import React, { Component } from "react";
import {
  Image,
  View,
  Platform,
  PermissionsAndroid,
  Linking,
  TouchableWithoutFeedback
} from "react-native";
import {
  Heading2,
  ExtraSmallBodyText,
  BodyText,
  FeatherIcon,
  Button,
  MediaItem
} from "../../../components";
import { SRXColor, LeadTypes, LeadSources } from "../../../constants";
import { Placeholder_Agent, Concierge_Expert } from "../../../assets";
import { AgentPO, ListingPO } from "../../../dataObject";
import { ObjectUtil, PermissionUtil, GoogleAnalyticUtil } from "../../../utils";
import { Spacing, Typography } from "../../../styles";
import PropTypes from "prop-types";
import { FullScreenImagePreview } from "../../../components/ImagePreview/FullScreenImagePreview";
import { TrackingService } from "../../../services";

const isIOS = Platform.OS === "ios";

class AgentCvProfile extends Component {
  static propTypes = {
    listingPO: PropTypes.instanceOf(ListingPO),
    agentPO: PropTypes.instanceOf(AgentPO)
  };

  static defaultProps = {
    listingPO: null,
    agentPO: null
  };

  getAgentPO() {
    const { listingPO, agentPO } = this.props;
    if (
      !ObjectUtil.isEmpty(listingPO) &&
      !ObjectUtil.isEmpty(listingPO.agentPO)
    ) {
      return listingPO.agentPO;
    }
    return agentPO;
  }

  trackContactAgentActionsForAgentCV({
    subject,
    leadSource,
    feature,
    medium,
    encryptedToUserId
  }) {
    /**
     * 12 - subject, 3 - platform, 8 - leadSource, 12 - feature
     * medium
     * 2 - email, call - 3
     */
    TrackingService.leadTracking({
      subject,
      leadSource,
      feature,
      medium,
      encryptedToUserId
    });
  }

  sendEmailAgentClicked() {
    const agentPO = this.getAgentPO();
    GoogleAnalyticUtil.trackLeads({leadType: LeadTypes.email, source: LeadSources.agentCV});
    this.trackContactAgentActionsForAgentCV({
      subject: 12,
      leadSource: 8,
      feature: 12,
      medium: 2,
      encryptedToUserId: agentPO.getAgentId()
    });
    if (!ObjectUtil.isEmpty(agentPO) && !ObjectUtil.isEmpty(agentPO.email)) {
      var url = "mailto:" + agentPO.email;
      const msgTemplate =
        "Hi " +
        agentPO.name +
        ", I saw your Agent CV on SRX Property App and wanted to see if you could help me. Thank you.";
      url += "?body=" + msgTemplate;
      Linking.canOpenURL(url)
        .then(supported => {
          if (!supported) {
            console.log(
              "AgentCvProfile - send email - Can't handle url: " + url
            );
          } else {
            return Linking.openURL(url);
          }
        })
        .catch(err =>
          console.error(
            "AgentCvProfile - An error occurred - send email - ",
            err
          )
        );
    }
  }

  callAgentClicked() {
    if (isIOS) {
      this.callAgent();
    } else {
      this.requestAndroidCallPermission();
    }
  }

  callAgent() {
    const agentPO = this.getAgentPO();
    GoogleAnalyticUtil.trackLeads({leadType: LeadTypes.call, source: LeadSources.agentCV});
    this.trackContactAgentActionsForAgentCV({
      subject: 12,
      leadSource: 8,
      feature: 12,
      medium: 3,
      encryptedToUserId: agentPO.getAgentId()
    });
    if (
      !ObjectUtil.isEmpty(agentPO) &&
      !ObjectUtil.isEmpty(agentPO.getMobileNumber())
    ) {
      const url = "tel:" + agentPO.getMobileNumber();

      Linking.canOpenURL(url)
        .then(supported => {
          if (!supported) {
            console.log("AgentCvProfile - call - Can't handle url: " + url);
          } else {
            return Linking.openURL(url);
          }
        })
        .catch(err =>
          console.error(
            "AgentCvProfile - An error occurred - call agent - ",
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

  //render
  renderAgentProfile() {
    return (
      <View
        style={{
          backgroundColor: SRXColor.White,
          paddingHorizontal: Spacing.M,
          paddingVertical: Spacing.S,
          flexDirection: "row",
          borderBottomWidth: 1,
          borderBottomColor: "#e0e0e0"
        }}
      >
        {this.renderAgentImage()}
        {this.renderInfo()}
        {this.renderAgencyAndSubscription()}
      </View>
    );
  }

  renderAgentImage() {
    const { agentPO } = this.props;
    const mediaItem = {
      thumbnailUrl: agentPO.getAgentPhoto(),
      url: agentPO.getAgentPhoto()
    };
    let mediaItems = [mediaItem];

    if (!ObjectUtil.isEmpty(agentPO)) {
      return (
        <View style={{ marginRight: Spacing.S }}>
          <TouchableWithoutFeedback
            onPress={() => {
              FullScreenImagePreview.show({
                mediaItems: mediaItems,
                shouldShowThumbnails: false
              });
            }}
          >
            <Image
              style={{
                width: 60,
                height: 60,
                borderRadius: 30,
                borderWidth: 1,
                borderColor: "#DCDCDC"
              }}
              defaultSource={Placeholder_Agent}
              source={{ uri: agentPO.getAgentPhoto() }}
              resizeMode={"cover"}
            />
          </TouchableWithoutFeedback>
        </View>
      );
    } else {
      return <View />;
    }
  }

  renderInfo() {
    return (
      <View style={{ flex: 1 }}>
        {this.renderAgentName()}
        {this.renderAppointment()}
        {this.renderCeaAndLicenseNo()}
      </View>
    );
  }

  renderAgentName() {
    const { agentPO } = this.props;
    if (!ObjectUtil.isEmpty(agentPO.name)) {
      return <Heading2 numberOfLines={1}>{agentPO.name}</Heading2>;
    }
  }

  renderAppointment() {
    const { agentPO } = this.props;
    if (!ObjectUtil.isEmpty(agentPO)) {
      const { appointment } = agentPO.agentCvPO;
      if (!ObjectUtil.isEmpty(appointment)) {
        return (
          <BodyText
            numberOfLines={1}
            style={{ color: SRXColor.Gray, lineHeight: 22 }}
          >
            {appointment}
          </BodyText>
        );
      }
    }
  }

  renderCeaAndLicenseNo() {
    const { agentPO } = this.props;
    const content = [];
    if (!ObjectUtil.isEmpty(agentPO.ceaRegNo)) {
      content.push(agentPO.ceaRegNo);
    }
    if (!ObjectUtil.isEmpty(agentPO.licenseNumber)) {
      content.push(agentPO.licenseNumber);
    }
    if (!ObjectUtil.isEmpty(content)) {
      return (
        <View style={{ marginTop: 10 }}>
          <ExtraSmallBodyText style={{ color: SRXColor.Gray }}>
            {content.join(" / ")}
          </ExtraSmallBodyText>
        </View>
      );
    }
  }

  renderAgencyAndSubscription() {
    const { agentPO } = this.props;
    const hasAgencyLogo = !ObjectUtil.isEmpty(agentPO.getAgencyLogoURL());
    if (hasAgencyLogo || isExpert) {
      return (
        <View>
          <Image
            source={{ uri: agentPO.getAgencyLogoURL() }}
            style={{ width: 50, height: 35 }}
            resizeMode={"contain"}
          />
          {this.renderExpertAgentIcon()}
        </View>
      );
    }
  }

  renderExpertAgentIcon() {
    const { agentPO } = this.props;
    const isExpert = agentPO.isExpert();
    if (isExpert) {
      return (
        <Image
          source={Concierge_Expert}
          style={{ width: 50, height: 35, marginTop: Spacing.XS }}
          resizeMode={"contain"}
        />
      );
    }
  }

  renderAgentEmailAndMobileNo() {
    const { agentPO } = this.props;
    if (!ObjectUtil.isEmpty(agentPO.email)) {
      return (
        <View style={Styles.emailAndMobileNoContainer}>
          <View style={Styles.subContainer}>
            <Button
              textStyle={[Typography.SmallBody, Styles.contactTextStyle]}
              leftView={<FeatherIcon name={"mail"} size={20} color={"black"} />}
              onPress={() => this.sendEmailAgentClicked()}
            >
              {agentPO.email}
            </Button>
          </View>
          <View style={{ flex: 0.5 }} />
          {this.renderMobileNo()}
        </View>
      );
    }
  }

  renderMobileNo() {
    const { agentPO } = this.props;
    if (!ObjectUtil.isEmpty(agentPO.mobile)) {
      return (
        <View style={{ flexDirection: "row", alignItems: "center", flex: 1 }}>
          <Button
            textStyle={[Typography.SmallBody, Styles.contactTextStyle]}
            leftView={<FeatherIcon name={"phone"} size={20} color={"black"} />}
            onPress={() => this.callAgentClicked()}
          >
            {agentPO.getMobileNumberForDisplay()}
          </Button>
        </View>
      );
    }
  }

  render() {
    return (
      <View>
        {this.renderAgentProfile()}
        {this.renderAgentEmailAndMobileNo()}
      </View>
    );
  }
}

const Styles = {
  emailAndMobileNoContainer: {
    flexDirection: "row",
    paddingHorizontal: Spacing.M,
    paddingVertical: Spacing.M,
    minHeight: 45,
    backgroundColor: SRXColor.AccordionBackground,
    borderBottomWidth: 1,
    borderBottomColor: "#e0e0e0"
  },
  subContainer: {
    flex: 2,
    flexDirection: "row"
  },
  contactTextStyle: {
    fontWeight: "400",
    color: SRXColor.TextLink,
    marginLeft: Spacing.XS
  }
};

export { AgentCvProfile };
