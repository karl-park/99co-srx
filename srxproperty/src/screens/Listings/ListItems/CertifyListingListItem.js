import React, { Component } from "react";
import {
  StyleSheet,
  Image,
  View,
  Text,
  ImageBackground,
  Alert,
  Platform
} from "react-native";
import PropTypes from "prop-types";
import { connect } from "react-redux";

import { Placeholder_Agent, Placeholder_General } from "../../../assets";
import {
  Heading2,
  Heading2_Currency,
  SmallBodyText,
  ExtraSmallBodyText,
  FeatherIcon,
  FontAwesomeIcon,
  Separator,
  Button,
  TouchableHighlight,
  BodyText,
  OcticonsIcon
} from "../../../components";
import { LoginStack } from "../../../config";
import { SRXColor, AlertMessage } from "../../../constants";
import { ListingPO } from "../../../dataObject";
import { Spacing, CheckboxStyles, Typography } from "../../../styles";
import { ObjectUtil, CommonUtil, ShortlistUtil } from "../../../utils";
import {
  Listings_BathTubIcon,
  Listings_BedIcon,
  Listings_XDroneIcon_Blue,
  Listings_V360Icon_Blue,
  Listing_CertifiedIcon
} from "../../../assets";
import {
  shortlistListing,
  removeShortlist,
  saveViewedListings
} from "../../../actions";
import { ShortlistedAgent } from "../../Shortlist";
import { ModelDescription } from "../../../constants/Models";

const isIOS = Platform.OS === "ios";
class CertifyListingListItem extends Component {
  static propTypes = {
    listingPO: PropTypes.instanceOf(ListingPO).isRequired,
    isCertifiedListing: PropTypes.bool.isRequired,
    certifyList: PropTypes.func.isRequired,
    directListing: PropTypes.func.isRequired
  };

  constructor(props) {
    super(props);
  }

  //Search Listing
  renderProjectNameAndPrice() {
    return (
      <View style={{ flex: 1, padding: Spacing.S }}>
        {this.renderProjectName()}
      </View>
    );
  }

  renderListingImageLayout() {
    return (
      <View
        style={{ width: 150, overflow: "hidden", borderBottomLeftRadius: 6 }}
      >
        {this.renderListingImage()}
        {/* {this.renderExclusiveListing()} */}
        {/* {this.renderViewedListingsStatus()} */}
      </View>
    );
  }

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
        ></ImageBackground>
      );
    } else {
      //Not a good way to solve android default source issue
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
        </ImageBackground>
      );
    }
  }

  //exclusive indicator for listing list item.
  renderExclusiveListing() {
    const { listingPO } = this.props;
    if (listingPO.isExclusive()) {
      return (
        <View
          style={{
            width: "100%",
            backgroundColor: SRXColor.Orange,
            paddingHorizontal: 4
          }}
        >
          <ExtraSmallBodyText
            style={{
              color: SRXColor.White,
              textAlign: "center"
            }}
          >
            Exclusive
          </ExtraSmallBodyText>
        </View>
      );
    }
  }

  renderV360DroneRow() {
    return (
      <View style={styles.v360DroneRow}>
        {/* {this.renderShorlistedIcon()} */}
        {/* <View
          style={{
            flexDirection: "row",
            justifyContent: "flex-end"
          }}
        >
          {this.renderV360Icon()}
          {this.renderDroneIcon()}
        </View> */}
      </View>
    );
  }

  renderV360Icon() {
    const { listingPO } = this.props;
    if (listingPO.hasVirtualTour()) {
      return (
        <View style={styles.v360DroneContainer}>
          <Image
            style={{ height: 20, width: 20 }}
            resizeMode={"contain"}
            source={Listings_V360Icon_Blue}
          />
        </View>
      );
    }
  }

  renderDroneIcon() {
    const { listingPO } = this.props;
    if (listingPO.hasDroneView()) {
      return (
        <View style={[styles.v360DroneContainer, { marginLeft: 5 }]}>
          <Image
            style={{ height: 20, width: 20 }}
            resizeMode={"contain"}
            source={Listings_XDroneIcon_Blue}
          />
        </View>
      );
    }
  }

  //agent info section
  renderAgentInfoAndCertifyListing() {
    return (
      <View
        style={[
          styles.agentInfoContainer,
          styles.subContainerStyle,
          { justifyContent: "space-between" },
          isIOS ? null : { borderWidth: 1, borderColor: "#e0e0e0" }
        ]}
      >
        {this.renderAgentInfo()}
        {this.renderCertifyListing()}
        {/* {this.renderAgentInfoSeparator()} */}
        {/* {this.renderListingAddress()} */}
      </View>
    );
  }

  renderCertifyListing() {
    const { isCertifiedListing, listingPO } = this.props;
    if (isCertifiedListing) {
      return (
        <View style={{ flexDirection: "row" }}>
          <Image
            style={{ height: 20, width: 20 }}
            resizeMode={"contain"}
            source={Listing_CertifiedIcon}
          />
          <BodyText style={{ paddingHorizontal: Spacing.M }}>
            Certified
          </BodyText>
        </View>
      );
    } else {
      return (
        <Button
          buttonStyle={{
            borderColor: SRXColor.Teal,
            borderWidth: 1,
            padding: Spacing.XS,
            borderWidth: 1,
            borderRadius: 10,
            marginRight: Spacing.M
          }}
          textStyle={Typography.Body}
          onPress={() => {
            this.certifyList();
          }}
        >
          Certify this listing
        </Button>
      );
    }
  }

  certifyList = () => {
    const { certifyList, listingPO } = this.props;
    console.log("listingPO");
    console.log(listingPO);
    if (certifyList) {
      if (!ObjectUtil.isEmpty(listingPO)) {
        certifyList(listingPO.id);
      }
    }
  };

  renderAgentInfo() {
    const { listingPO } = this.props;
    let agentPO = listingPO.getAgentPO();
    let agentImageUrl = CommonUtil.handleImageUrl(agentPO.photo);
    let agencyLogo = agentPO.getAgencyLogoURL();
    return (
      <View
        style={{
          flexDirection: "row",
          width: 150,
          paddingHorizontal: Spacing.XS,
          alignItems: "center"
        }}
      >
        <Image
          style={styles.agentImageStyle}
          defaultSource={Placeholder_Agent}
          source={{ uri: agentImageUrl }}
          resizeMode={"cover"}
        />
        <View>
          <SmallBodyText>Posted by</SmallBodyText>
          <SmallBodyText
            style={{ lineHeight: 22, fontWeight: "600", width: 100 }}
            numberOfLines={1}
          >
            {agentPO.name}
          </SmallBodyText>
        </View>
      </View>
    );
  }

  //vertical separtor
  renderAgentInfoSeparator() {
    return (
      <View style={{ backgroundColor: "#e0e0e0", width: 1, height: "100%" }} />
    );
  }

  renderListingAddress() {
    const { listingPO } = this.props;
    if (!ObjectUtil.isEmpty(listingPO.getListingHeader())) {
      return (
        <ExtraSmallBodyText
          numberOfLines={2}
          style={{ flex: 1, marginHorizontal: Spacing.XS }}
        >
          {listingPO.getListingHeader()}
        </ExtraSmallBodyText>
      );
    }
  }

  renderViewedListingsStatus() {
    const { listingPO, viewlistData } = this.props;
    if (viewlistData && !ObjectUtil.isEmpty(listingPO)) {
      if (viewlistData.viewlistedItems.includes(listingPO.getListingId())) {
        return (
          <ExtraSmallBodyText style={{ lineHeight: 22, color: "#858585" }}>
            Viewed
          </ExtraSmallBodyText>
        );
      }
    }
  }

  renderListingInfoLayout() {
    return (
      <View
        style={{
          flex: 1,
          paddingHorizontal: Spacing.XS,
          paddingTop: Spacing.XS / 2,
          justifyContent: "center"
        }}
      >
        {this.renderListingDetailType()}
        {this.renderNewProject()}
        {/* {this.renderSize()} */}
        {this.renderRoomsAndClassfied()}
        {this.renderExclusiveV360Drone()}
      </View>
    );
  }

  renderProjectName() {
    const { listingPO } = this.props;
    return (
      <View style={styles.projectNameContainer}>
        <Heading2 numberOfLines={2} style={{ overflow: "hidden", flex: 1 }}>
          {listingPO.getListingName()}
        </Heading2>
        <View style={{ alignItems: "flex-end" }}>
          {this.renderPrice()}
          {this.renderXListing()}
        </View>
      </View>
    );
  }

  renderListingCheckbox() {
    const { shortListedTab, listingPO } = this.props;
    let isSelected = this.isListingSelected(listingPO);
    return (
      <Button
        buttonStyle={{
          paddingVertical: Spacing.XS,
          paddingRight: Spacing.L / 2
        }}
        onPress={() => this.onSelectedEnquiryList()}
        leftView={
          isSelected ? (
            <View style={CheckboxStyles.checkStyle}>
              <FeatherIcon name={"check"} size={15} color={"white"} />
            </View>
          ) : (
            <View style={CheckboxStyles.unCheckStyle} />
          )
        }
      />
    );
  }

  renderListingDetailType() {
    const { listingPO } = this.props;

    var listingTypeInfo = "";

    if (listingPO.isSale()) {
      listingTypeInfo = listingPO.getSaleListingDetail();
    } else {
      listingTypeInfo = listingPO.getRentListingDetail();
    }
    //Condominium • Executive Condominium • Leasehold-99 • 2019 (TOP) => Executive Condominium • Leasehold-99 • 2019 (TOP)
    if (listingTypeInfo.includes(ModelDescription.executive_Condo)) {
      var indexOfFirst = listingTypeInfo.indexOf(
        ModelDescription.executive_Condo
      );
      let listingTypeInfo1 = listingTypeInfo;
      listingTypeInfo = listingTypeInfo1.substring(indexOfFirst);
    }

    if (!ObjectUtil.isEmpty(listingTypeInfo)) {
      return (
        <View style={{ height: 30 }}>
          <ExtraSmallBodyText style={{ color: "#858585" }}>
            {listingTypeInfo}
          </ExtraSmallBodyText>
        </View>
      );
    }
  }

  //New Project indicator
  renderNewProject() {
    const { listingPO } = this.props;

    return (
      <View style={{ flexDirection: "row", marginVertical: Spacing.XS / 2 }}>
        {listingPO.newLaunchInd === true ? (
          <View style={styles.exclusiveV360DroneNewProjectContainer}>
            <ExtraSmallBodyText style={{ color: SRXColor.Purple }}>
              New Project
            </ExtraSmallBodyText>
          </View>
        ) : (
          <View style={{ height: 16 }} />
        )}
      </View>
    );
  }

  renderBedroomBathroom = () => {
    return (
      <View
        style={{
          flexDirection: "row"
        }}
      >
        {this.renderBedroom()}
        {this.renderBathroom()}
      </View>
    );
  };

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

  renderExclusiveV360Drone = () => {
    const { listingPO } = this.props;
    if (
      listingPO.isExclusive() ||
      listingPO.hasVirtualTour() ||
      listingPO.hasDroneView()
    ) {
      return (
        <View
          style={{
            flexDirection: "row"
          }}
        >
          {this.renderExclusive()}
          {this.renderV360()}
          {this.renderDrone()}
        </View>
      );
    } else {
      return <View style={{ height: Spacing.L }} />;
    }
  };

  renderExclusive() {
    const { listingPO } = this.props;
    if (listingPO.isExclusive()) {
      return (
        <View
          style={[styles.exclusiveV360DroneRow, styles.rightContentItemMargin]}
        >
          <View style={styles.exclusiveV360DroneNewProjectContainer}>
            <ExtraSmallBodyText style={{ color: SRXColor.Purple }}>
              Exclusive
            </ExtraSmallBodyText>
          </View>
        </View>
      );
    }
  }

  renderV360() {
    const { listingPO } = this.props;
    if (listingPO.hasVirtualTour()) {
      return (
        <View
          style={[styles.exclusiveV360DroneRow, styles.rightContentItemMargin]}
        >
          <View style={styles.exclusiveV360DroneNewProjectContainer}>
            <ExtraSmallBodyText style={{ color: SRXColor.Purple }}>
              v360{"\u00b0"}
            </ExtraSmallBodyText>
          </View>
        </View>
      );
    }
  }

  renderDrone() {
    const { listingPO } = this.props;
    if (listingPO.hasDroneView()) {
      return (
        <View
          style={[styles.exclusiveV360DroneRow, styles.rightContentItemMargin]}
        >
          <View style={styles.exclusiveV360DroneNewProjectContainer}>
            <ExtraSmallBodyText style={{ color: SRXColor.Purple }}>
              Drone
            </ExtraSmallBodyText>
          </View>
        </View>
      );
    }
  }

  renderSize() {
    const { listingPO } = this.props;
    if (!ObjectUtil.isEmpty(listingPO.getSizeDisplay())) {
      return (
        <ExtraSmallBodyText
          style={{ color: "#858585", marginBottom: Spacing.XS }}
        >
          {listingPO.getSizeDisplay()}
        </ExtraSmallBodyText>
      );
    }
  }

  renderPrice() {
    const { listingPO } = this.props;
    if (!ObjectUtil.isEmpty(listingPO.getAskingPrice())) {
      return (
        <Heading2_Currency>{listingPO.getAskingPrice()}</Heading2_Currency>
      );
    }
  }

  renderRoomsAndClassfied() {
    return (
      <View style={{ flexDirection: "row", alignItems: "center" }}>
        {this.renderBedroomBathroom()}
        {/* {this.renderClassified()} */}
      </View>
    );
  }

  renderXListing() {
    const { listingPO } = this.props;
    if (listingPO.hasXListing()) {
      return (
        //change x listing price to certified valuation
        <Text style={{ color: SRXColor.Currency, marginLeft: 4, fontSize: 10 }}>
          With SRX Certified Valuation
        </Text>
      );
    }
  }

  renderClassified() {
    const { listingPO } = this.props;
    if (listingPO.hasClassified()) {
      return (
        <View
          style={{
            flex: 1,
            flexDirection: "row",
            justifyContent: "flex-end"
          }}
        >
          <Text
            style={{
              textAlign: "right",
              color: SRXColor.Gray,
              fontSize: 10,
              fontStyle: "italic"
            }}
          >
            Also on Classifieds
          </Text>
        </View>
      );
    }
  }

  renderSeparator() {
    return <Separator />;
  }

  render() {
    const { listingPO } = this.props;
    if (!ObjectUtil.isEmpty(listingPO)) {
      return (
        <View style={styles.containerStyle}>
          <TouchableHighlight onPress={()=> this.props.directListing(listingPO)}>
            <View
              style={[
                styles.subContainerStyle,
                isIOS ? null : { borderWidth: 1, borderColor: "#e0e0e0" }
              ]}
            >
              {this.renderProjectNameAndPrice()}
              <View
                style={{
                  flex: 1,
                  flexDirection: "row",
                  borderTopWidth: 1,
                  borderTopColor: "#e0e0e0"
                }}
              >
                {this.renderListingImageLayout()}
                {this.renderListingInfoLayout()}
              </View>
            </View>
          </TouchableHighlight>
          {/* {this.renderViewedListingsStatus()} */}
          {this.renderAgentInfoAndCertifyListing()}
          {/* {this.renderSeparator()} */}
        </View>
      );
    } else {
      return <View />;
    }
  }
}

const styles = StyleSheet.create({
  containerStyle: {
    backgroundColor: SRXColor.White,
    flex: 1,
    paddingTop: Spacing.XS,
    paddingBottom: Spacing.S,
    paddingHorizontal: Spacing.M
  },
  subContainerStyle: {
    borderRadius: 8,
    backgroundColor: SRXColor.White,
    //Shadow for iOS
    shadowColor: "rgb(110,129,154)",
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.4,
    shadowRadius: 4
  },

  imageStyle: {
    width: "100%",
    height: "100%",
    marginBottom: Spacing.S,
    alignSelf: "center",
    backgroundColor: SRXColor.White
  },
  agentInfoContainer: {
    marginTop: Spacing.XS,
    alignItems: "center",
    flexDirection: "row",
    backgroundColor: SRXColor.White,
    paddingVertical: Spacing.XS
  },
  agentImageStyle: {
    borderWidth: 1,
    borderColor: "rgba(0,0,0,0.2)",
    alignItems: "center",
    justifyContent: "center",
    width: 30,
    height: 30,
    backgroundColor: "#fff",
    borderRadius: 15,
    marginRight: Spacing.XS
  },
  projectNameContainer: {
    flexDirection: "row",
    marginVertical: Spacing.XS / 2,
    alignItems: "center",
    backgroundColor: SRXColor.White
  },
  checkBoxContainer: {
    width: 35,
    height: 30,
    alignItems: "center",
    paddingTop: 3,
    paddingBottom: 3,
    paddingLeft: Spacing.XS,
    paddingRight: Spacing.XS / 2
  },
  rightContentItemMargin: {
    marginBottom: Spacing.XS,
    marginRight: Spacing.S
  },
  bedroomBathroomContainer: {
    flexDirection: "row",
    alignItems: "center"
  },
  // leftContentItemMargin: {
  //   marginBottom: Spacing.XS
  // },
  v360DroneRow: {
    flexDirection: "row",
    flex: 1,
    position: "absolute",
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    marginTop: 3,
    marginRight: 3
  },
  v360DroneContainer: {
    borderRadius: 15,
    backgroundColor: "rgba(0,0,0,0.5)",
    width: 30,
    height: 30,
    alignItems: "center",
    justifyContent: "center"
  },
  sectionContainerStyle: {
    flex: 1,
    paddingVertical: Spacing.S,
    paddingHorizontal: Spacing.M,
    borderBottomWidth: 1,
    borderBottomColor: "#e0e0e0"
  },
  exclusiveV360DroneRow: {
    flexDirection: "row",
    alignItems: "center"
  },
  exclusiveV360DroneNewProjectContainer: {
    borderRadius: 4,
    borderWidth: 1,
    borderColor: SRXColor.Purple,
    paddingHorizontal: Spacing.XS / 2
  }
});

export { CertifyListingListItem };
