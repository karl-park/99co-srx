/**
 * My Residence, My Investment property & property of interest
 *    ==> showing all informaion
 *
 * My Rented Property
 *    ==> can only view proeprty ty[ppe, size & construction Year
 */

import React, { Component } from "react";
import { View } from "react-native";
import PropTypes from "prop-types";
import { BodyText } from "../../../../components";
import { SRXPropertyUserPO } from "../../../../dataObject";
import { Spacing } from "../../../../styles";
import { ObjectUtil, PropertyTypeUtil } from "../../../../utils";
import { PropertyTrackerUtil } from "../utils";

class ProjectInformation extends Component {
  renderEachRow({ title, description }) {
    return (
      <View style={styles.RowContainer}>
        <BodyText style={styles.KeyTitle}>{title}</BodyText>
        <BodyText style={styles.KeyDescription}>{description}</BodyText>
      </View>
    );
  }

  renderPropertyTypes() {
    const { trackerPO } = this.props;

    if (!ObjectUtil.isEmpty(trackerPO)) {
      const { cdResearchSubtype } = trackerPO;
      const propertyType = PropertyTypeUtil.getPropertyTypeDescription(
        cdResearchSubtype
      );

      if (!ObjectUtil.isEmpty(propertyType)) {
        return this.renderEachRow({
          title: "Type",
          description: propertyType
        });
      }
    }
  }

  renderSize() {
    const { trackerPO } = this.props;

    if (!ObjectUtil.isEmpty(trackerPO)) {
      const sizeDisplay = trackerPO.getSizeDisplay();

      if (!ObjectUtil.isEmpty(sizeDisplay)) {
        return this.renderEachRow({
          title: "Size",
          description: sizeDisplay
        });
      }
    }
  }

  renderConstructionYear() {
    const { trackerPO } = this.props;

    if (!ObjectUtil.isEmpty(trackerPO)) {
      const { builtYear } = trackerPO;

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
              title: "Construction Year",
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

  renderTenure() {
    const { trackerPO } = this.props;
    if (PropertyTrackerUtil.showSaleInformation(trackerPO)) {
      const { tenureDescription } = trackerPO;
      if (!ObjectUtil.isEmpty(tenureDescription)) {
        return this.renderEachRow({
          title: "Tenure",
          description: tenureDescription
        });
      }
    }
  }

  renderEffectiveTenure() {
    const { trackerPO } = this.props;
    if (PropertyTrackerUtil.showSaleInformation(trackerPO)) {
      const { effectiveTenure } = trackerPO;
      if (!ObjectUtil.isEmpty(effectiveTenure)) {
        return this.renderEachRow({
          title: "Effective Tenure",
          description: effectiveTenure
        });
      }
    }
  }

  render() {
    return (
      <View
        style={[
          {
            paddingHorizontal: Spacing.M,
            paddingTop: Spacing.M,
            paddingBottom: Spacing.M
          }
        ]}
      >
        {this.renderPropertyTypes()}
        {this.renderSize()}
        {this.renderConstructionYear()}
        {this.renderTenure()}
        {this.renderEffectiveTenure()}
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

ProjectInformation.propTypes = {
  trackerPO: PropTypes.instanceOf(SRXPropertyUserPO)
};

export { ProjectInformation };