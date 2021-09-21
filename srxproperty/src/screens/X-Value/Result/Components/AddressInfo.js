import React, { Component } from "react";
import { View } from "react-native";
import PropTypes from "prop-types";
import { BodyText, Button, FeatherIcon } from "../../../../components";
import { SRXColor, Tenures } from "../../../../constants";
import { Spacing } from "../../../../styles";
import { PropertyTypeUtil, ObjectUtil, StringUtil } from "../../../../utils";
import Moment from "moment";

class AddressInfo extends Component {
  static defaultProps = {
    xValuePO: {},
    projectDetail: {}
  };

  state = {
    showFullContent: true
  };

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
          onPress={() => {
            this.setState({ showFullContent: !this.state.showFullContent });
          }}
        >
          {showFullContent ? "Show Less" : "Show More"}
        </Button>
      </View>
    );
  }

  renderEachRow({ title, description }) {
    return (
      <View style={styles.RowContainer}>
        <BodyText style={styles.KeyTitle}>{title}</BodyText>
        <BodyText style={styles.KeyDescription}>{description}</BodyText>
      </View>
    );
  }

  renderDetails() {
    const { showFullContent } = this.state;
    if (showFullContent) {
      return (
        <View style={{ paddingTop: Spacing.L }}>
          {this.renderPropertyType()}
          {this.renderSize()}
          {this.renderConstructionYear()}
          {this.renderTenure()}
          {this.renderEffectiveTenure()}
        </View>
      );
    }
  }

  renderPropertyType() {
    const { xValuePO } = this.props;
    if (!ObjectUtil.isEmpty(xValuePO)) {
      const { subType } = xValuePO;
      const propertyTypeDesc = PropertyTypeUtil.getPropertyTypeDescription(
        subType
      );
      if (!ObjectUtil.isEmpty(propertyTypeDesc)) {
        return this.renderEachRow({
          title: "Type",
          description: propertyTypeDesc
        });
      }
    }
  }

  renderSize() {
    const { xValuePO } = this.props;
    if (!ObjectUtil.isEmpty(xValuePO)) {
      const { subType, size } = xValuePO;

      return this.renderEachRow({
        title: "Size",
        description:
          StringUtil.formatThousand(size) +
          (PropertyTypeUtil.isHDB(subType) ? " sqm" : " sqft")
      });
    }
  }

  renderConstructionYear() {
    const { projectDetail, xValuePO } = this.props;
    if (!ObjectUtil.isEmpty(projectDetail)) {
      const { dateComplete } = projectDetail;
      if (!PropertyTypeUtil.isHDB(xValuePO.subType)) {
        if (!ObjectUtil.isEmpty(dateComplete)) {
          var builtYear = "-";
          // var completed = true;

          var completeDate = Moment(dateComplete.time);
          builtYear = completeDate.format("YYYY");

          // var now = Moment();

          // const diffInDays = now.diff(completeDate, "days");

          // if (diffInDays <= 0) {
          //   completed = false;
          // }

          return this.renderEachRow({
            title: "Construction year",
            description: builtYear
          });
        }
      }
    }
  }

  renderTenure() {
    const { projectDetail, xValuePO } = this.props;
    if (!ObjectUtil.isEmpty(projectDetail)) {
      const { tenureShortString, yearComplete } = projectDetail;
      if (!PropertyTypeUtil.isHDB(xValuePO.subType)) {
        if (!ObjectUtil.isEmpty(tenureShortString)) {
          return this.renderEachRow({
            title: "Tenure",
            description: tenureShortString
          });
        }
      } else {
        if (yearComplete) {
          return this.renderEachRow({
            title: "Tenure",
            description: "99 yrs from " + yearComplete
          });
        } else {
          return this.renderEachRow({
            title: "Tenure",
            description: "99 yrs"
          });
        }
      }
    }
  }

  renderEffectiveTenure() {
    const { projectDetail, xValuePO } = this.props;
    if (!ObjectUtil.isEmpty(projectDetail)) {
      const { dateLease } = projectDetail;
      if (!ObjectUtil.isEmpty(dateLease)) {
        return this.renderEachRow({
          title: "Effective Tenure",
          description: dateLease
        });
      }
    }
  }

  render() {
    const { style } = this.props;

    return (
      <View style={[{ padding: Spacing.M }, style]}>
        {this.renderDetails()}
        {this.renderShowMoreButton()}
      </View>
    );
  }
}

export { AddressInfo };
