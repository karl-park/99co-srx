import React, { Component } from "react";
import { View } from "react-native";
import PropTypes from "prop-types";
import { BodyText, Heading1, Subtext } from "../../../../components";
import { Spacing } from "../../../../styles";
import { ObjectUtil, StringUtil } from "../../../../utils";

class TransactionItem extends Component {
  static defaultProps = {
    item: {}
  };

  static propTypes = {
    item: PropTypes.object
  };

  getTransactedAddress() {
    const { item } = this.props;
    if (!ObjectUtil.isEmpty(item.address)) {
      if (item.address.indexOf("#") >0 ) {
        const dashIndex = item.address.indexOf("-");
        if (dashIndex >= 0) {
          return item.address.substring(0, dashIndex + 1) + "xx";
        }
      }
      return item.address;
    }
    return "";
  }

  getTransactedSizeAndPSF() {
    const { item } = this.props;
    const combineArray = [];
    if (!ObjectUtil.isEmpty(item.size)) {
      combineArray.push(item.size);
    }
    if (!ObjectUtil.isEmpty(item.psf)) {
      combineArray.push(item.psf);
    }
    return combineArray.join(" | ");
  }

  getTransactedDate() {
    const { item } = this.props;

    if (!ObjectUtil.isEmpty(item.dateSold)) {
      if (item.type === "R") {
        return "Rented on " + item.dateSold;
      } else {
        return "Sold on " + item.dateSold;
      }
    }
    return "";
  }

  getTransactedPrice() {
    const { item } = this.props;
    if (!ObjectUtil.isEmpty(item.price)) {
      return StringUtil.formatCurrency(item.price);
    }
    return "";
  }

  render() {
    return (
      <View style={ItemStyles.container}>
        <BodyText>{this.getTransactedAddress()}</BodyText>
        <View
          style={{
            flexDirection: "row",
            justifyContent: "space-between",
            marginVertical: Spacing.XS/2
          }}
        >
          <Subtext>{this.getTransactedSizeAndPSF()}</Subtext>
          <Subtext
            style={{
              marginLeft: Spacing.XS,
              maxWidth: "45%",
              textAlign: "right"
            }}
          >
            {this.getTransactedDate()}
          </Subtext>
        </View>
        <Heading1>{this.getTransactedPrice()}</Heading1>
      </View>
    );
  }
}

ItemStyles = {
  container: {
    paddingVertical: Spacing.S,
  }
};

export { TransactionItem };
