import React, { Component } from "react";
import {
  StyleSheet,
  Image,
  View,
  ImageBackground,
  Platform,
  PermissionsAndroid,
  Linking
} from "react-native";
import PropTypes from "prop-types";

import {
  Heading2,
  Heading2_Currency,
  ExtraSmallBodyText,
  SmallBodyText,
  TouchableHighlight,
  Text,
  Button,
  FeatherIcon
} from "../../../components";
import { SRXColor } from "../../../constants";
import { ListingPO } from "../../../dataObject";
import { Spacing } from "../../../styles";
import {
  ObjectUtil,
  CommonUtil,
  PropertyTypeUtil,
  PermissionUtil
} from "../../../utils";
import {
  TransactedListings_SoldBanner,
  TransactedListings_RentedBanner,
  Placeholder_Agent,
  Placeholder_General
} from "../../../assets";
import { TrackingService } from "../../../services";

const isIOS = Platform.OS === "ios";

class TransactedListingGridItem extends Component {
  //Prop types
  static propTypes = {
    listingPO: PropTypes.instanceOf(ListingPO),
    onPress: PropTypes.func
  };

  //Event
  agentMobileClicked = () => {
    const { listingPO } = this.props;
    let agentPO = listingPO.getAgentPO();

    if (!ObjectUtil.isEmpty(agentPO)) {
      //Tracking
      TrackingService.trackAgentAdViewMobile({
        agentUserId: agentPO.userId,
        agentBookingId: agentPO.trackingId,
        viewId: 0 //From Transacted Properties
      });

      if (isIOS) {
        this.callAgent();
      } else {
        this.requestAndroidCallPermission();
      }
    } //end of validate
  };

  //Call Agents
  callAgent() {
    const { listingPO } = this.props;
    let agentPO = listingPO.getAgentPO();
    if (
      !ObjectUtil.isEmpty(agentPO) &&
      !ObjectUtil.isEmpty(agentPO.getMobileNumber())
    ) {
      const url = "tel:" + agentPO.getMobileNumber();

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

  //Request Permission For android
  requestAndroidCallPermission = () => {
    PermissionUtil.requestAndroidCallPermission().then(granted => {
      if (granted === PermissionsAndroid.RESULTS.GRANTED) {
        this.callAgent();
      }
    });
  };

  //Start Rendering methods for transacted grid item
  //Listing Image
  renderListingImage() {
    const { listingPO } = this.props;
    let imageUrl = listingPO.getListingImageUrl();
    if (isIOS) {
      return (
        <ImageBackground
          style={styles.imageStyle}
          defaultSource={Placeholder_General}
          source={{ uri: imageUrl }}
          resizeMode={"cover"}
        >
          {this.renderAgentInfo()}
        </ImageBackground>
      );
    } else {
      return (
        <ImageBackground
          style={styles.imageStyle}
          source={Placeholder_General}
          resizeMode={"cover"}
        >
          <Image
            style={{ flex: 1, backgroundColor: "transparent" }}
            source={{ uri: imageUrl }}
            resizeMode={"cover"}
          />
          {this.renderAgentInfo()}
        </ImageBackground>
      );
    }
  }

  //Agent Information
  renderAgentInfo() {
    return (
      <View style={styles.agentInfoStyle}>
        {this.renderPropertyType()}
        <View
          style={{
            flex: 1,
            justifyContent: "center",
            marginHorizontal: Spacing.S
          }}
        >
          {this.renderAgentImageAndName()}
          {this.renderAgencyImageAndPhone()}
        </View>
      </View>
    );
  }

  //Right Corner Label
  renderPropertyType() {
    const { listingPO } = this.props;
    let bannerSource = TransactedListings_SoldBanner;
    if (!listingPO.isSale()) {
      bannerSource = TransactedListings_RentedBanner;
    }
    return (
      <Image
        style={{
          height: 53,
          width: 56,
          alignSelf: "flex-end",
          position: "absolute"
        }}
        resizeMode={"contain"}
        source={bannerSource}
      />
    );
  }

  //Agent Image And Name
  renderAgentImageAndName() {
    const { listingPO } = this.props;
    let agentPO = listingPO.getAgentPO();

    if (!ObjectUtil.isEmpty(agentPO)) {
      let agentImageUrl = CommonUtil.handleImageUrl(agentPO.getAgentPhoto());
      return (
        <View
          style={{
            flexDirection: "row",
            alignItems: "center"
          }}
        >
          <Image
            style={styles.agentImageStyle}
            defaultSource={Placeholder_Agent}
            source={{ uri: agentImageUrl }}
            resizeMode={"cover"}
          />
          {this.renderAgentName()}
        </View>
      );
    }
  }

  //Agent Name
  renderAgentName() {
    const { listingPO } = this.props;
    let agentPO = listingPO.getAgentPO();

    return (
      <SmallBodyText
        style={{ lineHeight: 16, flex: 1, fontWeight: "600" }}
        numberOfLines={1}
      >
        {agentPO.name}
      </SmallBodyText>
    );
  }

  renderAgencyImageAndPhone() {
    const { listingPO } = this.props;
    let agentPO = listingPO.getAgentPO();
    if (!ObjectUtil.isEmpty(agentPO)) {
      let agencyImageURL = agentPO.getAgencyLogoURL();
      return (
        <View
          style={{
            flexDirection: "row",
            alignItems: "center"
          }}
        >
          <Image
            style={styles.agencyLogo}
            defaultSource={Placeholder_General}
            source={{ uri: agencyImageURL }}
            resizeMode={"contain"}
          />
          {this.renderAgentMobileNumber()}
        </View>
      );
    }
  }

  //Mobile Number
  renderAgentMobileNumber() {
    const { listingPO } = this.props;
    let agentPO = listingPO.getAgentPO();

    return (
      <Button
        textStyle={{ fontSize: 14, color: SRXColor.Black }}
        leftView={
          <FeatherIcon
            name="phone"
            size={12}
            color={SRXColor.Black}
            style={{ marginRight: Spacing.XS / 4 }}
          />
        }
        onPress={this.agentMobileClicked}
      >
        {agentPO.getMobileNumberForDisplay()}
      </Button>
    );
  }

  //project name
  renderProjectName() {
    const { listingPO } = this.props;
    var listingName = !ObjectUtil.isEmpty(listingPO.getListingName())
      ? listingPO.getListingName()
      : "";

    return (
      <Heading2
        numberOfLines={1}
        style={{
          overflow: "hidden",
          flex: 1,
          lineHeight: 22,
          marginBottom: Spacing.XS
        }}
      >
        {listingName}
      </Heading2>
    );
  }

  //Listing Details Info
  renderListingDetailType() {
    const { listingPO } = this.props;

    var listingTypeInfo = "";
    if (listingPO.isSale()) {
      listingTypeInfo = listingPO.getSaleListingDetail();
    } else {
      listingTypeInfo = listingPO.getRentListingDetail();
    }

    return (
      <View style={{ height: 35, marginBottom: Spacing.XS }}>
        <ExtraSmallBodyText numberOfLines={2} style={{ color: "#858585" }}>
          {listingTypeInfo ? listingTypeInfo : ""}
        </ExtraSmallBodyText>
      </View>
    );
  }

  //Size
  renderListingSize() {
    const { listingPO } = this.props;
    return (
      <ExtraSmallBodyText
        style={{ color: "#858585", marginBottom: Spacing.XS }}
      >
        {listingPO.getSizeDisplay()}
      </ExtraSmallBodyText>
    );
  }

  renderCallAgentDescription() {
    return (
      <ExtraSmallBodyText style={styles.descriptionStyle}>
        Call the agent to know more about this listing
      </ExtraSmallBodyText>
    );
  }

  renderListingGridItemContent() {
    const { containerStyle } = this.props;
    return (
      <View style={[styles.itemContainerStyle, containerStyle]}>
        {this.renderListingImage()}
        {this.renderProjectName()}
        {this.renderListingDetailType()}
        {this.renderListingSize()}
        {this.renderCallAgentDescription()}
      </View>
    );
  }

  render() {
    const { listingPO, onPress } = this.props;
    if (!ObjectUtil.isEmpty(listingPO)) {
      return (
        <TouchableHighlight onPress={onPress}>
          {this.renderListingGridItemContent()}
        </TouchableHighlight>
      );
    } else {
      return <View />;
    }
  }
}

const styles = StyleSheet.create({
  itemContainerStyle: {
    flex: 1,
    paddingHorizontal: Spacing.M,
    paddingVertical: Spacing.S
  },
  imageStyle: {
    width: "100%",
    height: 121,
    backgroundColor: SRXColor.AccordionBackground,
    marginBottom: Spacing.XS
  },
  agentInfoStyle: {
    width: "100%",
    height: "100%",
    backgroundColor: "rgba(255, 255, 255, 0.75)",
    position: "absolute",
    overflow: "hidden"
  },
  agentImageStyle: {
    borderWidth: 1,
    borderColor: "rgba(0,0,0,0.2)",
    alignItems: "center",
    justifyContent: "center",
    width: 24,
    height: 24,
    backgroundColor: SRXColor.White,
    borderRadius: 12,
    marginRight: Spacing.XS / 2
  },
  descriptionStyle: {
    fontStyle: "italic",
    color: SRXColor.Gray,
    marginVertical: Spacing.XS,
    textAlign: "center"
  },
  agencyLogo: {
    height: 30,
    width: 40,
    marginVertical: Spacing.XS / 2,
    marginRight: Spacing.XS / 2
  }
});

export { TransactedListingGridItem };
