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
import { View, Alert } from "react-native";
import PropTypes from "prop-types";
import {
  Button,
  TextInput,
  DatePicker,
  Heading2
} from "../../../../components";
import { SRXColor, AlertMessage } from "../../../../constants";
import { SRXPropertyUserPO } from "../../../../dataObject";
import { ObjectUtil, StringUtil, NumberUtil } from "../../../../utils";
import { Spacing } from "../../../../styles";
import { PropertyTrackerUtil } from "../utils";
import { PropertyTrackerService } from "../../../../services";
import Moment from "moment";

class PurchaseDetailUpdates extends Component {
  state = {
    updatedPerchasePrice: null,
    selectedDate: null
  };

  constructor(props) {
    super(props);

    this.updatePurchasePriceAndDate = this.updatePurchasePriceAndDate.bind(
      this
    );
  }

  componentDidMount() {
    Moment.locale("en");
    const { trackerPO } = this.props;
    if (!ObjectUtil.isEmpty(trackerPO)) {
      const purchasePrice = trackerPO.getTransactedPrice();
      const purchaseDate = trackerPO.getTransactedDate();
      const newState = {};
      if (!ObjectUtil.isEmpty(purchasePrice) || purchasePrice > 0) {
        newState.updatedPerchasePrice = purchasePrice;
      }
      if (!ObjectUtil.isEmpty(purchaseDate)) {
        newState.selectedDate = Moment(purchaseDate, "DD MMM YYYY").format(
          "DD-MMM-YYYY"
        );
      }

      if (!ObjectUtil.isEmpty(newState)) {
        this.setState(newState);
      }
    }
  }

  updatePurchasePriceAndDate() {
    const { trackerPO, onUpdate } = this.props;
    const { updatedPerchasePrice, selectedDate } = this.state;
    PropertyTrackerService.addCapitalGain({
      ptUserId: trackerPO.ptUserId,
      lastTransactedPrice: updatedPerchasePrice,
      lastTransactedDate: Moment(selectedDate, "DD-MMM-YYYY").format(
        "YYYY-MM-DD"
      )
    })
      .catch(error => {
        Alert.alert(
          AlertMessage.ErrorMessageTitle,
          "Updates failed. Please try again."
        );
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const { result } = response;
          if (result === "success") {
            const newPO = new SRXPropertyUserPO(trackerPO);
            (newPO.capitalGainLastTransactedPrice = NumberUtil.intValue(
              updatedPerchasePrice
            )),
              (newPO.capitalGainLastTransactedDate = Moment(
                selectedDate,
                "DD-MMM-YYYY"
              ).format("DD MMM YYYY"));

            if (onUpdate) {
              onUpdate(newPO);
            }
          }
        }
      });
  }

  /**
   * Editing
   */

  renderPurchasePriceTextInput() {
    return (
      <TextInput
        style={{ marginBottom: Spacing.M }}
        placeholder={"Purchase Price"}
        keyboardType={"number-pad"}
        value={StringUtil.formatCurrency(this.state.updatedPerchasePrice, 0)}
        onChangeText={newText =>
          this.setState({
            updatedPerchasePrice: StringUtil.decimalValue(newText, 0)
          })
        }
      />
    );
  }

  renderPurchaseDatePicker() {
    return (
      <DatePicker
        mode={"date"}
        format={"DD-MMM-YYYY"}
        placeholder={"Purchase Date"}
        maxDate={new Date()}
        date={this.state.selectedDate}
        onDateChange={date => {
          this.setState({ selectedDate: date });
        }}
      />
    );
  }

  renderSaveButton() {
    const { updatedPerchasePrice, selectedDate } = this.state;
    const enabled =
      (!ObjectUtil.isEmpty(updatedPerchasePrice) || updatedPerchasePrice > 0) &&
      !ObjectUtil.isEmpty(selectedDate);
    return (
      <Button
        buttonType={Button.buttonTypes.secondary}
        buttonStyle={{ marginTop: Spacing.M, alignSelf: "center" }}
        disabled={!enabled}
        onPress={this.updatePurchasePriceAndDate}
      >
        Save
      </Button>
    );
  }

  render() {
    const { cancelUpdate } = this.props;
    return (
      <View style={{ padding: Spacing.M, paddingBottom: Spacing.XL }}>
        <View style={{ flexDirection: "row", marginBottom: Spacing.M }}>
          <Heading2 style={{ flex: 1 }}>Update purchase price</Heading2>
          <Button
            buttonStyle={{ marginLeft: Spacing.XS }}
            onPress={cancelUpdate}
          >
            Cancel
          </Button>
        </View>
        {this.renderPurchasePriceTextInput()}
        {this.renderPurchaseDatePicker()}
        {this.renderSaveButton()}
      </View>
    );
  }
}

PurchaseDetailUpdates.propTypes = {
  trackerPO: PropTypes.instanceOf(SRXPropertyUserPO),
  cancelUpdate: PropTypes.func,
  /**
   * function to callback after successfully updating to server
   * return new SRXPropertyUserPO
   */
  onUpdate: PropTypes.func
};

export { PurchaseDetailUpdates };
