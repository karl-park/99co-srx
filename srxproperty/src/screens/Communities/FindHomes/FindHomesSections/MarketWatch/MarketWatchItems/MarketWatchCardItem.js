import React, { Component } from "react";
import { StyleSheet, View } from "react-native";
import PropTypes from "prop-types";
import Moment from "moment";

import {
  BodyText,
  SmallBodyText,
  FeatherIcon,
  ExtraSmallBodyText
} from "../../../../../../components";
import { Spacing } from "../../../../../../styles";
import { SRXColor } from "../../../../../../constants";
import { ObjectUtil } from "../../../../../../utils";

class MarketWatchCardItem extends Component {
  //start rendering methods
  renderMarketWatchTitle() {
    const { marketWatchPO } = this.props;
    return (
      <BodyText style={{ color: SRXColor.Black }} numberOfLines={1}>
        {marketWatchPO.getSubType()}
      </BodyText>
    );
  }

  renderPreviousVSCurrentMonth() {
    const { marketWatchPO } = this.props;
    if (!ObjectUtil.isEmpty(marketWatchPO)) {
      if (!ObjectUtil.isEmpty(marketWatchPO.getMarketWatchDate())) {
        const currentMonthAndYear = Moment(
          marketWatchPO.getMarketWatchDate(),
          "MMM YYYY"
        ).format("MMM YY");
        const previousMonthAndYear = Moment(currentMonthAndYear, "MMM YYYY")
          .subtract(1, "months")
          .endOf("month")
          .format("MMM YY");
        const previousVSCurrentMonthAndYear =
          currentMonthAndYear + " vs " + previousMonthAndYear;

        return (
            <ExtraSmallBodyText>{previousVSCurrentMonthAndYear}</ExtraSmallBodyText>
        );
      }
    }
  }

  renderPriceAndVolume() {
    return (
      <View
        style={{
          flexDirection: "row",
          marginTop: Spacing.M
        }}
      >
        {this.renderMarketWatchPrice()}
        {this.renderMarketWatchVolume()}
      </View>
    );
  }

  renderMarketWatchPrice() {
    const { marketWatchPO } = this.props;
    var price = marketWatchPO.getMarketWatchPrice();
    if (price) {
      return (
        <View style={{ flex: 1, alignItems: "center" }}>
          {this.renderPriceAndVolumeValue(price)}
          <SmallBodyText style={styles.priceAndVolumeText}>Price</SmallBodyText>
        </View>
      );
    }
  }

  renderMarketWatchVolume() {
    const { marketWatchPO } = this.props;
    var volume = marketWatchPO.getMarketWatchVolume();
    if (volume) {
      return (
        <View style={{ flex: 1, alignItems: "center" }}>
          {this.renderPriceAndVolumeValue(volume)}
          <SmallBodyText style={styles.priceAndVolumeText}>
            Volume
          </SmallBodyText>
        </View>
      );
    }
  }

  renderPriceAndVolumeValue(value) {
    return (
      <View style={{ flexDirection: "row" }}>
        <SmallBodyText
          style={{
            color: value > 0 ? SRXColor.Green : SRXColor.Red,
            fontWeight: "bold"
          }}
        >
          {Math.abs(value)}
          {"%"}
        </SmallBodyText>
        <FeatherIcon
          name={value > 0 ? "arrow-up" : "arrow-down"}
          size={16}
          color={"#8DABC4"}
        />
      </View>
    );
  }

  renderLastUpdatedDate(){
    const { marketWatchPO } = this.props;
    if (!ObjectUtil.isEmpty(marketWatchPO)) {
      if (!ObjectUtil.isEmpty(marketWatchPO.getLastUpdatedDate())) {
    return (
      <View style={{ flexDirection: "row", alignItems: "center", paddingTop: Spacing.XS }}>
            <FeatherIcon name="clock" size={16} color={SRXColor.Gray} />
            <ExtraSmallBodyText
              style={{ fontStyle: "italic", marginLeft: Spacing.XS / 2, color: SRXColor.Gray }}
            >
              {marketWatchPO.getLastUpdatedDate()}
            </ExtraSmallBodyText>
          </View>
    );
      }}
  }

  render() {
    const { marketWatchPO } = this.props;
    if (!ObjectUtil.isEmpty(marketWatchPO)) {
      return (
        <View style={styles.containerStyle}>
          {this.renderMarketWatchTitle()}
          {this.renderPreviousVSCurrentMonth()}
          {this.renderPriceAndVolume()}
          {this.renderLastUpdatedDate()}
        </View>
      );
    }
    return <View />;
  }
}

MarketWatchCardItem.propTypes = {
  /** market watch list */
  marketWatchPO: PropTypes.object
};

const styles = StyleSheet.create({
  containerStyle: {
    backgroundColor: SRXColor.White,
    paddingHorizontal: Spacing.XS,
    paddingVertical: Spacing.M,
    width: 136 + Spacing.S * 2,
    alignItems: "center",
    //border
    borderRadius: 8,
    borderWidth: 1,
    borderColor: "#e0e0e0",
    //Shadow for iOS
    shadowColor: "rgb(110,129,154)",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.23,
    shadowRadius: 2
  },
  priceAndVolumeText: {
    marginTop: Spacing.XS / 2,
    color: SRXColor.Black
  }
});

export { MarketWatchCardItem };
