import React, { Component } from "react";
import {
  Image,
  View,
  Linking,
  Platform,
  StyleSheet,
  ImageBackground,
  TouchableHighlight,
  PermissionsAndroid
} from "react-native";
import PropTypes from "prop-types";

import { Placeholder_Agent, Placeholder_General } from "../../../assets";
import {
  Button,
  Heading2,
  FeatherIcon,
  SmallBodyText,
  ExtraSmallBodyText,
  Separator
} from "../../../components";
import {
  ObjectUtil,
  CommonUtil,
  PermissionUtil,
  PropertyTypeUtil
} from "../../../utils";
import { TrackingService } from "../../../services";
import { SRXColor } from "../../../constants";
import { ListingPO } from "../../../dataObject";
import { Spacing } from "../../../styles";
import {
  Listings_BathTubIcon,
  Listings_BedIcon,
  TransactedListings_SoldBanner,
  TransactedListings_RentedBanner
} from "../../../assets";

const isIOS = Platform.OS === "ios";

const TransListingListItemSource = {
  AgentCV: "AgentCV" //currently only this one will have some effect to this form
};

class TransactedListingListItem extends Component {
  static propTypes = {
    listingPO: PropTypes.instanceOf(ListingPO),
    source: PropTypes.oneOf(Object.keys(TransListingListItemSource))
  };

  constructor(props) {
    super(props);

    this.agentMobileClicked = this.agentMobileClicked.bind(this);
  }

  agentMobileClicked() {
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
  }

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

  //Start Rendering methods
  //Left Content
  renderLeftContent() {
    return <View style={{ width: 136 }}>{this.renderListingImage()}</View>;
  }

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
            style={styles.subImageStyle}
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
            marginHorizontal: Spacing.XS
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

  //Agency Image and Phone
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

  //Right Content
  renderRightContent() {
    return (
      <View style={{ flex: 1, paddingLeft: Spacing.XS }}>
        {this.renderProjectName()}
        {this.renderListingDetailType()}
        {this.renderSize()}
        {this.renderBedroomBathroom()}
      </View>
    );
  }

  //Project Name
  renderProjectName() {
    const { listingPO } = this.props;
    return <Heading2 numberOfLines={1}>{listingPO.getListingName()}</Heading2>;
  }

  //Listing Detailed Type
  renderListingDetailType() {
    const { listingPO } = this.props;
    var listingTypeInfo = "";
    if (listingPO.isSale()) {
      listingTypeInfo = listingPO.getSaleListingDetail();
    } else {
      listingTypeInfo = listingPO.getRentListingDetail();
    }
    return (
      <ExtraSmallBodyText
        style={{ color: SRXColor.Gray, marginTop: Spacing.XS }}
      >
        {listingTypeInfo}
      </ExtraSmallBodyText>
    );
  }

  //Size
  renderSize() {
    const { listingPO } = this.props;
    return (
      <ExtraSmallBodyText
        style={[
          styles.leftContentItemMargin,
          { color: SRXColor.Gray, marginTop: Spacing.XS }
        ]}
      >
        {listingPO.getSizeDisplay()}
      </ExtraSmallBodyText>
    );
  }

  //Rooms
  renderBedroomBathroom = () => {
    return (
      <View style={{ flexDirection: "row", marginTop: Spacing.XS }}>
        {this.renderBedroom()}
        {this.renderBathroom()}
      </View>
    );
  };

  //Bedrooms
  renderBedroom() {
    const { listingPO } = this.props;
    const bedroom = listingPO.getRoom();
    if (!ObjectUtil.isEmpty(bedroom)) {
      return (
        <View
          style={[
            styles.bedroomBathroomContainer,
            styles.rightContentItemMargin
          ]}
        >
          <Image
            style={{ height: 20, width: 20 }}
            resizeMode={"contain"}
            source={Listings_BedIcon}
          />
          <Heading2 style={{ paddingLeft: Spacing.XS }}>{bedroom}</Heading2>
        </View>
      );
    }
  }

  //Bathroom
  renderBathroom() {
    const { listingPO } = this.props;
    const bathroom = listingPO.getBathroom();
    if (!ObjectUtil.isEmpty(bathroom)) {
      return (
        <View
          style={[
            styles.bedroomBathroomContainer,
            styles.rightContentItemMargin
          ]}
        >
          <Image
            style={{ height: 20, width: 20 }}
            resizeMode={"contain"}
            source={Listings_BathTubIcon}
          />
          <Heading2 style={{ paddingLeft: Spacing.XS }}>{bathroom}</Heading2>
        </View>
      );
    }
  }

  //Call Agent Label
  renderCallAgent() {
    const { source } = this.props;
    if (source === TransListingListItemSource.AgentCV) {
      return <View />;
    } else {
      return (
        <ExtraSmallBodyText style={styles.descriptionStyle}>
          Call the agent to know more about this listing
        </ExtraSmallBodyText>
      );
    }
  }

  render() {
    const { containerStyle, listingPO, onPress } = this.props;
    if (!ObjectUtil.isEmpty(listingPO)) {
      return (
        <TouchableHighlight onPress={onPress}>
          <View style={{ backgroundColor: SRXColor.White }}>
            <View style={[styles.mainContainer, containerStyle]}>
              <View style={{ flexDirection: "row", flex: 1 }}>
                {this.renderLeftContent()}
                {this.renderRightContent()}
              </View>
              {this.renderCallAgent()}
            </View>
            <Separator />
          </View>
        </TouchableHighlight>
      );
    } else {
      return <View />;
    }
  }
}

TransactedListingListItem.Sources = TransListingListItemSource;

const styles = StyleSheet.create({
  mainContainer: {
    flex: 1,
    paddingVertical: Spacing.S,
    paddingHorizontal: Spacing.M
  },
  imageStyle: {
    width: "100%",
    height: 108,
    backgroundColor: SRXColor.AccordionBackground,
    alignItems: "center"
  },
  subImageStyle: {
    width: "100%",
    height: "100%",
    backgroundColor: "transparent"
  },
  rightContentItemMargin: {
    marginBottom: Spacing.XS,
    marginRight: Spacing.M
  },
  bedroomBathroomContainer: {
    flexDirection: "row",
    alignItems: "center"
  },
  leftContentItemMargin: {
    marginBottom: Spacing.XS
  },
  descriptionStyle: {
    fontStyle: "italic",
    color: SRXColor.Gray,
    textAlign: "center",
    marginVertical: Spacing.XS
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
  agencyLogo: {
    height: 30,
    width: 40,
    marginVertical: Spacing.XS / 2,
    marginRight: Spacing.XS / 2
  }
});

export { TransactedListingListItem };
