/**
 * if occupancy is own or interested,
 * can show
 *
 * if rented
 * hide
 *
 * else show (as default)
 */

import React, { Component } from "react";
import { View } from "react-native";
import PropTypes from "prop-types";
import {
  BodyText,
  Subtext,
  Button,
} from "../../../../components";
import { SRXColor } from "../../../../constants";
import { SRXPropertyUserPO } from "../../../../dataObject";
import { ObjectUtil, StringUtil } from "../../../../utils";
import { Spacing } from "../../../../styles";
import { PropertyTrackerUtil } from "../utils";
import { PurchaseDetailUpdates } from "./PurchaseDetailUpdates";

class PurchaseDetail extends Component {
  state = {
    isEditing: false
  };

  constructor(props) {
    super(props);

    this.onTrasactedPriceAndDateUpdated = this.onTrasactedPriceAndDateUpdated.bind(this);
  }

  onTrasactedPriceAndDateUpdated(newTrackerPO) {
    const { onUpdate } = this.props;
    this.setState({
      isEditing: false
    }, () => {
      if (onUpdate) {
        onUpdate(newTrackerPO);
      }
    })
  }

  /**
   * Detail Display
   */

  renderPurchasePrice() {
    const { trackerPO } = this.props;
    let value = "-";

    if (!ObjectUtil.isEmpty(trackerPO)) {
      const price = trackerPO.getTransactedPrice();
      if (price > 0) {
        value = StringUtil.formatCurrency(price);

        const date = trackerPO.getTransactedDate();
        if (!ObjectUtil.isEmpty(date)) {
          value += " (" + date + ")";
        }
      }
    }

    return (
      <View style={{ flex: 1, marginRight: Spacing.XS }}>
        <BodyText>Purchase Price</BodyText>
        <BodyText>{value}</BodyText>
      </View>
    );
  }

  renderEditButton() {
    return (
      <Button
        buttonType={Button.buttonTypes.secondary}
        onPress={() => this.setState({ isEditing: true })}
      >
        Edit
      </Button>
    );
  }

  renderPurchaseSection() {
    return (
      <View style={{ flexDirection: "row" }}>
        {this.renderPurchasePrice()}
        {this.renderEditButton()}
      </View>
    );
  }

  renderCapitalGain() {
    const { trackerPO } = this.props;
    let value = "-";

    if (!ObjectUtil.isEmpty(trackerPO)) {
      const transactedPrice = trackerPO.getTransactedPrice();
      const currentXValue = trackerPO.getSaleXValue();
      if (transactedPrice > 0 && currentXValue > 0) {
        const gain = currentXValue - transactedPrice;
        value = StringUtil.formatCurrency(gain);
      }
    }

    return (
      <View style={{ marginTop: Spacing.XL, alignItems: "center" }}>
        <View
          style={{
            backgroundColor: SRXColor.White,
            padding: Spacing.XS,
            alignItems: "center",
            borderWidth: 1,
            borderColor: "#e0e0e0",
            borderRadius: 3,
            shadowColor: "rgb(110,129,154)",
            shadowOffset: { width: 1, height: 2 },
            shadowOpacity: 0.32,
            shadowRadius: 1
          }}
        >
          <Subtext>Capital gain</Subtext>
          <BodyText
            style={{ color: SRXColor.Currency, marginVertical: Spacing.XS / 2 }}
          >
            {value}
          </BodyText>
          <Subtext>Based on current X-Value</Subtext>
        </View>
      </View>
    );
  }

  render() {
    const { trackerPO } = this.props;
    const { isEditing } = this.state;
    if (PropertyTrackerUtil.showSaleInformation(trackerPO)) {
      if (isEditing) {
        return (
          <PurchaseDetailUpdates
            trackerPO={trackerPO}
            cancelUpdate={() => {
              this.setState({ isEditing: false });
            }}
            onUpdate={this.onTrasactedPriceAndDateUpdated}
          />
        );
      } else {
        return (
          <View style={{ padding: Spacing.M, paddingBottom: Spacing.XL }}>
            {this.renderPurchaseSection()}
            {this.renderCapitalGain()}
          </View>
        );
      }
    } else {
      return <View />;
    }
  }
}

PurchaseDetail.propTypes = {
  trackerPO: PropTypes.instanceOf(SRXPropertyUserPO),
  /**
   * function to callback after successfully updating to server,
   * return new SRXPropertyUserPO from PurchaseDetailUpdates
   */
  onUpdate: PropTypes.func,
};

export { PurchaseDetail };
