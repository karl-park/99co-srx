import React, { Component } from "react";
import {
  StyleSheet,
  Image,
  View,
  TouchableHighlight,
  ImageBackground
} from "react-native";
import PropTypes from "prop-types";
import {
  Placeholder_General,
  Placeholder_Agent,
  TransactedListings_SoldBanner,
  TransactedListings_RentedBanner
} from "../../../assets";
import {
  BodyText,
  SmallBodyText,
  FeatherIcon,
  ExtraSmallBodyText
} from "../../../components";
import { SRXColor } from "../../../constants";
import { ListingPO } from "../../../dataObject";
import { Spacing } from "../../../styles";
import { ObjectUtil, CommonUtil, PropertyTypeUtil } from "../../../utils";

class TransactedListingCardItem extends Component {
  static propTypes = {
    listingPO: PropTypes.instanceOf(ListingPO).isRequired,
    onSelected: PropTypes.func
  };

  getListingSummary() {
    const { listingPO } = this.props;

    const combiningArray = [];

    //property type
    const propertyType = PropertyTypeUtil.getPropertyTypeDescription(
      listingPO.cdResearchSubType
    );
    if (!ObjectUtil.isEmpty(propertyType)) {
      combiningArray.push(propertyType);
    }

    //model
    if (!ObjectUtil.isEmpty(listingPO.getModel())) {
      combiningArray.push(listingPO.getModel());
    }

    if (
      listingPO.isSale() &&
      !ObjectUtil.isEmpty(listingPO.tenure) &&
      !PropertyTypeUtil.isHDB(listingPO.cdResearchSubType)
    ) {
      combiningArray.push(listingPO.tenure);
    }

    //built year
    if (!ObjectUtil.isEmpty(listingPO.builtYear)) {
      let year = parseInt(new Date().getFullYear());
      if (parseInt(listingPO.builtYear) >= year) {
        combiningArray.push("TOP-" + this.builtYear);
      } else {
        combiningArray.push("Built-" + this.builtYear);
      }
    }

    return combiningArray.join(" • ");
  }

  renderAgentPhotoAndName() {
    const { listingPO } = this.props;
    let agentPO = listingPO.getAgentPO();
    let agentImageUrl = CommonUtil.handleImageUrl(agentPO.getAgentPhoto());
    return (
      <View style={{ flexDirection: "row", alignItems: "center" }}>
        <Image
          style={styles.agentImageStyle}
          defaultSource={Placeholder_Agent}
          source={{ uri: agentImageUrl }}
          resizeMode={"cover"}
        />
        <SmallBodyText style={{ flex: 1 }} numberOfLines={1}>
          {agentPO.name}
        </SmallBodyText>
      </View>
    );
  }

  renderAgencyLogo() {
    const { listingPO } = this.props;
    let agentPO = listingPO.getAgentPO();
    let agencyImageURL = agentPO.getAgencyLogoURL();
    if (!ObjectUtil.isEmpty(agencyImageURL)) {
      return (
        <Image
          style={styles.agencyLogo}
          defaultSource={Placeholder_General}
          source={{ uri: agencyImageURL }}
          resizeMode={"contain"}
        />
      );
    }
  }

  renderAgentMobileNumber() {
    const { listingPO } = this.props;
    let agentPO = listingPO.getAgentPO();

    return (
      <View style={{ flexDirection: "row", alignItems: "center" }}>
        <FeatherIcon
          name="phone"
          size={12}
          color={SRXColor.Black}
          style={{ marginRight: Spacing.XS / 4 }}
        />
        <SmallBodyText>{agentPO.getMobileNumberForDisplay()}</SmallBodyText>
      </View>
    );
  }

  renderAgencyImageAndPhone() {
    return (
      <View
        style={{
          flexDirection: "row",
          alignItems: "center"
        }}
      >
        {this.renderAgencyLogo()}
        {this.renderAgentMobileNumber()}
      </View>
    );
  }

  renderAgentInfo() {
    const { listingPO } = this.props;
    let agentPO = listingPO.getAgentPO();
    if (!ObjectUtil.isEmpty(agentPO)) {
      return (
        <View
          style={{
            alignItems: "center",
            justifyContent: "center",
            padding: Spacing.XS,
            backgroundColor: "#FFFFFFBF",
            height: "100%",
            width: "100%"
          }}
        >
          {this.renderAgentPhotoAndName()}
          {this.renderAgencyImageAndPhone()}
        </View>
      );
    }
  }

  renderListingImageAndAgentInfo = () => {
    const { listingPO } = this.props;
    let imageUrl = listingPO.getListingImageUrl();
    return (
      <ImageBackground
        style={[styles.imageStyle, { marginBottom: Spacing.XS }]}
        defaultSource={Placeholder_General}
        source={{ uri: imageUrl }}
        resizeMode={"cover"}
      >
        {this.renderAgentInfo()}
      </ImageBackground>
    );
  };

  renderPropertyInfo() {
    const { listingPO } = this.props;
    return (
      <View style={{ marginBottom: 12 }}>
        <BodyText numberOfLines={1}>{listingPO.getListingName()}</BodyText>
        <ExtraSmallBodyText style={{ color: SRXColor.Gray }} numberOfLines={2}>
          {/* {listingPO.getSizeDisplay()} */}
          {this.getListingSummary()}
        </ExtraSmallBodyText>
      </View>
    );
  }

  renderCallAgentText() {
    return (
      <ExtraSmallBodyText
        style={{
          color: SRXColor.Gray,
          fontStyle: "italic",
          textAlign: "center"
        }}
      >
        Call the agent to know more about this listing
      </ExtraSmallBodyText>
    );
  }

  renderSoldRentedBanner() {
    const { listingPO } = this.props;
    let bannerSource = TransactedListings_SoldBanner;
    if (!listingPO.isSale()) {
      bannerSource = TransactedListings_RentedBanner;
    }
    return (
      <Image
        style={{
          height: 50,
          width: 53,
          alignSelf: "flex-end",
          position: "absolute"
        }}
        resizeMode={"contain"}
        source={bannerSource}
      />
    );
  }

  render() {
    const { listingPO, onSelected } = this.props;
    if (!ObjectUtil.isEmpty(listingPO)) {
      return (
        <TouchableHighlight
          style={[styles.containerStyle, styles.containerBorder]}
          onPress={() => {
            if (onSelected) onSelected(listingPO);
          }}
        >
          <View
            style={{ backgroundColor: SRXColor.White, padding: Spacing.XS }}
          >
            {this.renderListingImageAndAgentInfo()}
            {this.renderPropertyInfo()}
            {this.renderCallAgentText()}
            {this.renderSoldRentedBanner()}
          </View>
        </TouchableHighlight>
      );
    } else {
      return <View />;
    }
  }
}

const styles = StyleSheet.create({
  containerStyle: {
    backgroundColor: SRXColor.White,
    overflow: "hidden",
    width: 136 + Spacing.XS * 2,
    shadowColor: "rgb(110,129,154)",
    shadowOffset: { width: 1, height: 2 },
    shadowOpacity: 0.32,
    shadowRadius: 1
  },
  containerBorder: {
    borderRadius: 5,
    borderWidth: 1,
    borderColor: "#e0e0e0"
  },
  imageStyle: {
    borderRadius: 5,
    width: 136,
    height: 88
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
  agencyLogo: {
    height: 24,
    width: 32,
    marginVertical: Spacing.XS / 2,
    marginRight: Spacing.XS / 2
  }
});

export { TransactedListingCardItem };
