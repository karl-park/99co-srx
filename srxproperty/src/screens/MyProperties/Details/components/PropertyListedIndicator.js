import React, { Component } from "react";
import { View, Image, TouchableOpacity, Platform } from "react-native";
import PropTypes from "prop-types";
import { MyProperties_ListedTag } from "../../../../assets";
import { BodyText } from "../../../../components";
import { SRXColor, pt_Occupancy } from "../../../../constants";
import { Spacing } from "../../../../styles";
import { ObjectUtil } from "../../../../utils";
import { SRXPropertyUserPO } from "../../../../dataObject";

const isIOS = Platform.OS === "ios";

class PropertyListedIndicator extends Component {
  state = {
    indicatorText: null
  };

  constructor(props) {
    super(props);

    this.onIndicatorPress = this.onIndicatorPress.bind(this);
  }

  componentDidMount() {
    this.updateIndicatorText();
  }

  componentDidUpdate(prevProps, prevState) {
    if (prevProps.trackerPO !== this.props.trackerPO) {
      this.updateIndicatorText();
    }
  }

  updateIndicatorText() {
    const { trackerPO } = this.props;

    let indicatorText;
    let clickable = false;

    if (!ObjectUtil.isEmpty(trackerPO)) {
      const {
        listedSaleListingId,
        listedRentListingId,
        occupancy,
        ownerNric,
        dateOwnerNricVerified
      } = trackerPO;
      const verified =
        !ObjectUtil.isEmpty(ownerNric) &&
        !ObjectUtil.isEmpty(dateOwnerNricVerified);
      if (occupancy === pt_Occupancy.Own) {
        if (!verified) {
          indicatorText = "Verify your home to enjoy more privileges";
          clickable = true;
          //direct to verify ownership
        } else {
          //verified
          if (
            ObjectUtil.isEmpty(listedSaleListingId) &&
            listedSaleListingId == 0 &&
            (ObjectUtil.isEmpty(listedRentListingId) &&
              listedRentListingId == 0)
          ) {
            indicatorText = "Your home has been verified";
            //do nothing
          } else if (
            !ObjectUtil.isEmpty(listedSaleListingId) ||
            listedSaleListingId > 0
          ) {
            indicatorText = "Your property is listed for sale";
            clickable = true;
            //direct to listings when click
          } else if (
            !ObjectUtil.isEmpty(listedRentListingId) ||
            listedRentListingId > 0
          ) {
            indicatorText = "Your property is listed for rent";
            clickable = true;
            //direct to listings when click
          }
        }
      }
      // else if (occupancy === pt_Occupancy.Interested) {
      //   if (
      //     !ObjectUtil.isEmpty(listedSaleListingId) ||
      //     listedSaleListingId > 0
      //   ) {
      //     indicatorText = "This property is listed for sale";
      //     clickable = true;
      //     //direct to listings when click
      //   } else if (
      //     !ObjectUtil.isEmpty(listedRentListingId) ||
      //     listedRentListingId > 0
      //   ) {
      //     indicatorText = "This property is listed for rent";
      //     clickable = true;
      //     //direct to listings when click
      //   }
      // }
    }

    this.setState({
      indicatorText,
      clickable
    });
  }

  onIndicatorPress() {
    const { trackerPO, onViewListing, onManageProperty, certifyListingNavigation } = this.props;

    if (!ObjectUtil.isEmpty(trackerPO)) {
      const {
        listedSaleListingId,
        listedRentListingId,
        occupancy,
        ownerNric,
        dateOwnerNricVerified
      } = trackerPO;
      const verified =
        !ObjectUtil.isEmpty(ownerNric) &&
        !ObjectUtil.isEmpty(dateOwnerNricVerified);
      if (occupancy === pt_Occupancy.Own) {
        if (!verified) {
          indicatorText = "Verify your home to enjoy more privileges";
          clickable = true;
          //direct to verify ownership
          if (onManageProperty) {
            onManageProperty();
          }
        } else {
          //verified
          if (
            ObjectUtil.isEmpty(listedSaleListingId) &&
            listedSaleListingId == 0 &&
            (ObjectUtil.isEmpty(listedRentListingId) &&
              listedRentListingId == 0)
          ) {
            indicatorText = "Your home has been verified";
            //do nothing
          } else if (
            !ObjectUtil.isEmpty(listedSaleListingId) ||
            listedSaleListingId > 0
          ) {
            indicatorText = "Your property is listed for sale";
            clickable = true;
            //direct to listings when click
            if(onManageProperty && certifyListingNavigation){
              onManageProperty(certifyListingNavigation);
            }else if (onViewListing) {
              onViewListing({ listingId: listedSaleListingId, refType: "V" });
            }
          } else if (
            !ObjectUtil.isEmpty(listedRentListingId) ||
            listedRentListingId > 0
          ) {
            indicatorText = "Your property is listed for rent";
            clickable = true;
            //direct to listings when click
            if (onViewListing) {
              onViewListing({ listingId: listedRentListingId, refType: "V" });
            }
          }
        }
      }
      // else if (occupancy === pt_Occupancy.Interested) {
      //   if (
      //     !ObjectUtil.isEmpty(listedSaleListingId) ||
      //     listedSaleListingId > 0
      //   ) {
      //     //direct to listings when click
      //     if (onViewListing) {
      //       onViewListing({ listingId: listedSaleListingId, refType: "V" });
      //     }
      //   } else if (
      //     !ObjectUtil.isEmpty(listedRentListingId) ||
      //     listedRentListingId > 0
      //   ) {
      //     //direct to listings when click
      //     if (onViewListing) {
      //       onViewListing({ listingId: listedRentListingId, refType: "V" });
      //     }
      //   }
      // }
    }
  }

  renderContent() {
    const { indicatorText } = this.state;

    return (
      <View
        style={{
          height: isIOS ? 40 : 45,
          borderRadius: isIOS ? 20 : 22.5,
          flexDirection: "row",
          alignItems: "center",
          paddingHorizontal: Spacing.S,
          backgroundColor: SRXColor.Purple
        }}
      >
        <Image
          source={MyProperties_ListedTag}
          style={{ height: 20, width: 20, marginRight: Spacing.XS }}
        />
        <BodyText style={{ color: SRXColor.White }}>{indicatorText}</BodyText>
      </View>
    );
  }

  render() {
    const { indicatorText, clickable } = this.state;

    if (!ObjectUtil.isEmpty(indicatorText)) {
      return (
        <View style={{ paddingHorizontal: Spacing.M, marginTop: Spacing.M }}>
          {clickable ? (
            <TouchableOpacity
              style={{ borderRadius: 25 }}
              onPress={this.onIndicatorPress}
            >
              {this.renderContent()}
            </TouchableOpacity>
          ) : (
            this.renderContent()
          )}
        </View>
      );
    }
    return <View />;
  }
}

PropertyListedIndicator.propTypes = {
  trackerPO: PropTypes.instanceOf(SRXPropertyUserPO),
  onViewListing: PropTypes.func,
  onManageProperty: PropTypes.func
};

export { PropertyListedIndicator };
