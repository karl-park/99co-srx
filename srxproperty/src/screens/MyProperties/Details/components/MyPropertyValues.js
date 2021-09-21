/**
 * if occupancy is own or interested,
 * can show all value
 *
 * else if occupancy is rented
 * only show rental xvalue
 *
 * other case
 * treated as rented (as no instruction)
 */

import React, { Component } from "react";
import { View } from "react-native";
import PropTypes from "prop-types";
import { BodyText, Subtext } from "../../../../components";
import { SRXColor } from "../../../../constants";
import { SRXPropertyUserPO } from "../../../../dataObject";
import { ObjectUtil, StringUtil } from "../../../../utils";
import { Spacing } from "../../../../styles";
import { PropertyTrackerUtil } from "../utils";

class MyPropertyValues extends Component {
  renderSaleXValue() {
    const { trackerPO } = this.props;
    let value = "-";
    if (PropertyTrackerUtil.showSaleInformation(trackerPO)) {
      const saleXValue = trackerPO.getSaleXValue();
      if (saleXValue > 0) {
        value = StringUtil.formatCurrency(saleXValue);
      }
    }

    return (
      <View style={styles.valueContainers}>
        <Subtext style={styles.titleStyle}>Sale X-Value</Subtext>
        <BodyText style={styles.valueStyle}>{value}</BodyText>
      </View>
    );
  }

  renderRentalXValue() {
    const { trackerPO } = this.props;
    let value = "-";
    if (!ObjectUtil.isEmpty(trackerPO)) {
      const rentXValue = trackerPO.getRentXValue();
      if (rentXValue > 0) {
        value = StringUtil.formatCurrency(rentXValue);
      }
    }

    return (
      <View style={styles.valueContainers}>
        <Subtext style={styles.titleStyle}>Rental X-Value</Subtext>
        <BodyText style={styles.valueStyle}>{value}</BodyText>
      </View>
    );
  }

  renderRentalYield() {
    const { trackerPO } = this.props;
    let value = "-";
    if (PropertyTrackerUtil.showSaleInformation(trackerPO)) {
      const rentalYield = trackerPO.getRentalYield();
      if (rentalYield > 0) {
        value = StringUtil.formatThousand(rentalYield) + "%";
      }
    }

    return (
      <View style={styles.valueContainers}>
        <Subtext style={styles.titleStyle}>Rental Yield</Subtext>
        <BodyText style={{ color: SRXColor.Purple }}>{value}</BodyText>
      </View>
    );
  }

  renderSpace() {
    return <View style={{ width: Spacing.S }} />;
  }

  render() {
    return (
      <View
        style={{
          flexDirection: "row",
          paddingHorizontal: Spacing.M,
          marginBottom: Spacing.M
        }}
      >
        {this.renderSaleXValue()}
        {this.renderSpace()}
        {this.renderRentalXValue()}
        {this.renderSpace()}
        {this.renderRentalYield()}
      </View>
    );
  }
}

const styles = {
  valueContainers: {
    flex: 1,
    backgroundColor: SRXColor.White,
    alignItems: "center",
    paddingTop: Spacing.S,
    paddingBottom: Spacing.S,
    borderWidth: 1,
    borderColor: "#e0e0e0",
    borderRadius: 5,
    shadowColor: "rgb(110,129,154)",
    shadowOffset: { width: 1, height: 2 },
    shadowOpacity: 0.32,
    shadowRadius: 1
  },
  titleStyle: {
    marginBottom: Spacing.XS / 2
  },
  valueStyle: {
    color: SRXColor.Currency
  }
};

MyPropertyValues.propTypes = {
  trackerPO: PropTypes.instanceOf(SRXPropertyUserPO)
};

export { MyPropertyValues };
