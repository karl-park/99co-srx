import React, { Component } from "react";
import { View } from "react-native";
import PropTypes from "prop-types";
import {
  BodyText,
  Button,
  FeatherIcon,
  Heading2
} from "../../../../components";
import { Spacing } from "../../../../styles";
import {
  SRXColor,
  District,
  HDBEstates,
  ListingDetailsViewingItems
} from "../../../../constants";
import { ListingDetailPO } from "../../../../dataObject";
import {
  ObjectUtil,
  PropertyTypeUtil,
  PropertyOptionsUtil,
  GoogleAnalyticUtil
} from "../../../../utils";

class KeyInformation extends Component {
  static propTypes = {
    listingDetailPO: PropTypes.instanceOf(ListingDetailPO)
  };

  static defaultProps = {};

  state = {
    showFullContent: false
  };

  constructor(props) {
    super(props);

    this.onShowMoreOrLessPressed = this.onShowMoreOrLessPressed.bind(this);
  }

  onShowMoreOrLessPressed = () => {
    const { showFullContent } = this.state;
    const isToShowFullContent = !showFullContent;
    if (isToShowFullContent) {
      GoogleAnalyticUtil.trackListingDetailsUserActions({
        viewingItem: ListingDetailsViewingItems.keyInformation
      });
    }
    this.setState({ showFullContent: isToShowFullContent });
  };

  renderEachRow({ title, description }) {
    return (
      <View style={styles.RowContainer}>
        <BodyText style={styles.KeyTitle}>{title}</BodyText>
        <BodyText style={styles.KeyDescription}>{description}</BodyText>
      </View>
    );
  }

  renderContent() {
    const { listingDetailPO } = this.props;
    let isSale = true;
    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const { listingPO } = listingDetailPO;
      if (!ObjectUtil.isEmpty(listingPO)) {
        isSale = listingPO.isSale();
      }
    }

    if (isSale) {
      return (
        <View>
          {this.renderPropertyType()}
          {this.renderModel()}
          {this.renderDistrictHdbTown()}
          {this.renderBuiltYear()}
          {/* {this.renderConstructionYr()} */}
          {this.renderTenure()}
          {this.renderRestContent()}
        </View>
      );
    } else {
      return (
        <View>
          {this.renderLeaseTerm()}
          {this.renderFurnish()}
          {this.renderPropertyType()}
          {this.renderModel()}
          {this.renderDistrictHdbTown()}
          {this.renderRestContent()}
        </View>
      );
    }
  }

  renderRestContent() {
    const { showFullContent } = this.state;
    const { listingDetailPO } = this.props;
    let isSale = true;
    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const { listingPO } = listingDetailPO;
      if (!ObjectUtil.isEmpty(listingPO)) {
        isSale = listingPO.isSale();
      }
    }

    if (showFullContent) {
      if (isSale) {
        return (
          <View>
            {this.renderFurnish()}
            {this.renderFloor()}
            {this.renderDeveloper()}
            {this.renderListingId()}
            {this.renderListedDate()}
            {this.renderAddress()}
            {this.renderLeaseTerm()}
          </View>
        );
      } else {
        return (
          <View>
            {this.renderBuiltYear()}
            {this.renderTenure()}
            {this.renderFloor()}
            {this.renderDeveloper()}
            {this.renderListingId()}
            {this.renderListedDate()}
            {this.renderAddress()}
          </View>
        );
      }
    }
  }

  renderPropertyType() {
    const { listingDetailPO } = this.props;

    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const { listingPO } = listingDetailPO;

      if (!ObjectUtil.isEmpty(listingPO)) {
        const subtype = PropertyTypeUtil.getPropertyTypeDescription(
          listingPO.cdResearchSubType
        );

        if (!ObjectUtil.isEmpty(subtype)) {
          return this.renderEachRow({
            title: "Type",
            description: subtype
          });
        }
      }
    }
  }

  renderModel() {
    const { listingDetailPO } = this.props;

    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const { listingPO } = listingDetailPO;

      if (!ObjectUtil.isEmpty(listingPO)) {
        const { model } = listingPO;

        if (!ObjectUtil.isEmpty(model)) {
          return this.renderEachRow({
            title: "Model",
            description: model
          });
        }
      }
    }
  }

  renderDistrictHdbTown() {
    const { listingDetailPO } = this.props;

    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const { listingPO } = listingDetailPO;

      if (!ObjectUtil.isEmpty(listingPO)) {
        const {
          cdResearchSubType,
          postalDistrictId,
          postalHdbTownId
        } = listingPO;

        //value to be populated after merged
        if (PropertyTypeUtil.isHDB(cdResearchSubType)) {
          const description = HDBEstates.getHDBEstateDescription(
            postalHdbTownId
          );
          if (!ObjectUtil.isEmpty(description)) {
            return this.renderEachRow({
              title: "HDB Town",
              description: description
            });
          }
        } else {
          const description = District.getDistrictDescription(postalDistrictId);
          if (!ObjectUtil.isEmpty(description)) {
            return this.renderEachRow({
              title: "District",
              description: description
            });
          }
        }
      }
    }
  }

  renderBuiltYear() {
    const { listingDetailPO } = this.props;

    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const { listingPO } = listingDetailPO;

      if (!ObjectUtil.isEmpty(listingPO)) {
        const { builtYear } = listingPO;

        if (builtYear > 0) {
          if (builtYear == 1800) {
            //unknown
            //dont show
          } else if (builtYear == 1900) {
            return this.renderEachRow({
              title: "TOP",
              description: "Under Construction"
            });
          } else {
            const currentDate = new Date();
            if (builtYear <= currentDate.getFullYear()) {
              return this.renderEachRow({
                title: "Built Year",
                description: builtYear
              });
            } else {
              return this.renderEachRow({
                title: "TOP",
                description: builtYear
              });
            }
          }
        }
      }
    }
  }

  renderConstructionYr() {
    const { listingDetailPO } = this.props;

    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const { completeDate } = listingDetailPO;

      if (!ObjectUtil.isEmpty(completeDate)) {
        return this.renderEachRow({
          title: "Construction year",
          description: completeDate
        });
      }
    }
  }

  renderTenure() {
    const { listingDetailPO } = this.props;

    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const { listingPO } = listingDetailPO;

      if (!ObjectUtil.isEmpty(listingPO)) {
        const { tenure } = listingPO;

        if (!ObjectUtil.isEmpty(tenure)) {
          return this.renderEachRow({ title: "Tenure", description: tenure });
        }
      }
    }
  }

  renderFurnish() {
    const { listingDetailPO } = this.props;

    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const { listingPO } = listingDetailPO;

      if (!ObjectUtil.isEmpty(listingPO)) {
        const { furnish } = listingPO;

        const furnishDescription = PropertyOptionsUtil.getFurnishLevelDescription(
          furnish
        );

        if (!ObjectUtil.isEmpty(furnishDescription)) {
          return this.renderEachRow({
            title: "Furnish",
            description: furnishDescription
          });
        }
      }
    }
  }

  renderFloor() {
    const { listingDetailPO } = this.props;

    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const { listingPO } = listingDetailPO;

      if (!ObjectUtil.isEmpty(listingPO)) {
        const { floor } = listingPO;

        const floorDescription = PropertyOptionsUtil.getFloorTypeDescription(
          floor
        );

        if (!ObjectUtil.isEmpty(floorDescription)) {
          return this.renderEachRow({
            title: "Floor",
            description: floorDescription
          });
        }
      }
    }
  }

  renderDeveloper() {
    const { listingDetailPO } = this.props;

    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const { developer } = listingDetailPO;

      if (!ObjectUtil.isEmpty(developer)) {
        return this.renderEachRow({
          title: "Developer",
          description: developer
        });
      }
    }
  }

  renderListingId() {
    const { listingDetailPO } = this.props;

    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const { listingPO } = listingDetailPO;

      if (!ObjectUtil.isEmpty(listingPO)) {
        const { id } = listingPO;

        if (!ObjectUtil.isEmpty(id)) {
          return this.renderEachRow({
            title: "Listing ID",
            description: id
          });
        }
      }
    }
  }

  renderListedDate() {
    const { listingDetailPO } = this.props;

    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const { listingPO } = listingDetailPO;

      if (!ObjectUtil.isEmpty(listingPO)) {
        const { actualDatePosted } = listingPO;

        if (!ObjectUtil.isEmpty(actualDatePosted)) {
          return this.renderEachRow({
            title: "Listing Date",
            description: actualDatePosted
          });
        }
      }
    }
  }

  renderAddress() {
    const { listingDetailPO } = this.props;

    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const { listingPO } = listingDetailPO;

      if (!ObjectUtil.isEmpty(listingPO)) {
        const { cdResearchSubType } = listingPO;
        var address = "";
        if (
          PropertyTypeUtil.isCondo(cdResearchSubType) ||
          PropertyTypeUtil.isHDB(cdResearchSubType)
        ) {
          address = listingPO.getFullAddressWithPostalCode();
        } else {
          address = listingPO.getFullAddress();
        }
        if (!ObjectUtil.isEmpty(address)) {
          return this.renderEachRow({
            title: "Address",
            description: address
          });
        }
      }
    }
  }

  renderLeaseTerm() {
    const { listingDetailPO } = this.props;

    if (!ObjectUtil.isEmpty(listingDetailPO)) {
      const { listingPO } = listingDetailPO;

      if (!ObjectUtil.isEmpty(listingPO)) {
        if (!listingPO.isSale()) {
          return this.renderEachRow({
            title: "Lease Term",
            description: listingPO.getLeaseTermDescription()
          });
        }
      }
    }
  }

  renderShowMoreButton() {
    const { showFullContent } = this.state;
    return (
      <View style={{ alignItems: "flex-end", paddingTop: Spacing.M }}>
        <Button
          textStyle={{ fontSize: 14, color: SRXColor.Teal, marginRight: 4 }}
          rightView={
            <FeatherIcon
              name={showFullContent ? "chevron-up" : "chevron-down"}
              size={16}
              color={SRXColor.Teal}
            />
          }
          onPress={this.onShowMoreOrLessPressed}
        >
          {showFullContent ? "Close" : "Show More"}
        </Button>
      </View>
    );
  }

  render() {
    const { showFullContent } = this.state;
    const { style, listingDetailPO } = this.props;
    return (
      <View
        style={[
          {
            paddingHorizontal: Spacing.M,
            paddingTop: Spacing.M,
            paddingBottom: Spacing.L
          },
          style
        ]}
      >
        <Heading2 style={{ marginBottom: Spacing.L }}>Key Information</Heading2>
        {this.renderContent()}
        {this.renderShowMoreButton()}
      </View>
    );
  }
}

styles = {
  RowContainer: {
    flexDirection: "row",
    marginBottom: Spacing.XS
  },
  KeyTitle: {
    flex: 1
  },
  KeyDescription: {
    flex: 1
  }
};

export { KeyInformation };
