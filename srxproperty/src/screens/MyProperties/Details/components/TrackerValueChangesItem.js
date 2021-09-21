import React, { Component } from "react";
import { View } from "react-native";
import PropTypes from "prop-types";
import {
  BodyText,
  Subtext,
  EntypoIcon,
  Heading2
} from "../../../../components";
import { SRXColor } from "../../../../constants";
import { ObjectUtil, StringUtil } from "../../../../utils";
import { Spacing } from "../../../../styles";

class TrackerValueChangesItem extends Component {
  renderChangesIndicator(difference) {
    if (difference > 0) {
      return (
        <EntypoIcon
          name={"triangle-up"}
          size={16}
          color={SRXColor.Green}
          style={{ flex: 1, minWidth: 16 }}
        />
      );
    } else if (difference < 0) {
      return (
        <EntypoIcon
          name={"triangle-down"}
          size={16}
          color={SRXColor.Red}
          style={{ flex: 1, minWidth: 16 }}
        />
      );
    }
  }

  renderItem({ title, value, style }) {
    return (
      <View style={[{ flex: 1 }, style]}>
        <Subtext>{title}</Subtext>
        <View style={{ flexDirection: "row" }}>
          <BodyText style={{ color: SRXColor.Purple }}>
            {StringUtil.formatThousand(value) + "%"}
          </BodyText>
          {this.renderChangesIndicator(value)}
        </View>
      </View>
    );
  }

  renderPropertyChange() {
    const { valueChangePO } = this.props;
    if (!ObjectUtil.isEmpty(valueChangePO)) {
      const { propertyTitle, propertyChangePercentage } = valueChangePO;
      if (
        !ObjectUtil.isEmpty(propertyTitle) &&
        !isNaN(propertyChangePercentage)
      ) {
        return this.renderItem({
          title: propertyTitle,
          value: propertyChangePercentage,
          style: { marginRight: Spacing.XS }
        });
      }
    }
  }

  renderAreaChange() {
    const { valueChangePO } = this.props;
    if (!ObjectUtil.isEmpty(valueChangePO)) {
      const { areaTitle, areaChangePercentage } = valueChangePO;
      if (!ObjectUtil.isEmpty(areaTitle) && !isNaN(areaChangePercentage)) {
        return this.renderItem({
          title: areaTitle,
          value: areaChangePercentage,
          style: { marginRight: Spacing.XS }
        });
      }
    }
  }

  renderCountryChange() {
    const { valueChangePO } = this.props;
    if (!ObjectUtil.isEmpty(valueChangePO)) {
      const { countryTitle, countryChangePercentage } = valueChangePO;
      if (
        !ObjectUtil.isEmpty(countryTitle) &&
        !isNaN(countryChangePercentage)
      ) {
        return this.renderItem({
          title: countryTitle,
          value: countryChangePercentage
        });
      }
    }
  }

  renderTitle() {
    const { valueChangePO } = this.props;
    if (!ObjectUtil.isEmpty(valueChangePO)) {
      const { description } = valueChangePO;
      if (!ObjectUtil.isEmpty(description)) {
        return (
          <Heading2 style={{ marginBottom: Spacing.XS }}>
            {description}
          </Heading2>
        );
      }
    }
  }

  render() {
    return (
      <View
        style={{
          marginTop: Spacing.S,
          marginBottom: Spacing.XL,
          marginHorizontal: Spacing.M
        }}
      >
        {this.renderTitle()}
        <View style={{ flexDirection: "row" }}>
          {this.renderPropertyChange()}
          {this.renderAreaChange()}
          {this.renderCountryChange()}
        </View>
      </View>
    );
  }
}

TrackerValueChangesItem.propTypes = {
  //item from array saleValueChanges or rentValueChanges from SRXPropertyUserPO
  valueChangePO: PropTypes.object
};

export { TrackerValueChangesItem };
