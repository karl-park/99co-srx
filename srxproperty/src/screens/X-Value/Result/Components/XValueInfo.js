import React, { Component } from "react";
import { View } from "react-native";
import PropTypes from "prop-types";
import {
  Heading2,
  Heading2_Currency,
  Subtext,
  LinearGradient
} from "../../../../components";
import { SRXColor } from "../../../../constants";
import { Spacing } from "../../../../styles";
import { ObjectUtil, StringUtil } from "../../../../utils";

class XValueInfo extends Component {
  static defaultProps = {
    xValueResultPO: {},
    xValueTrend: [],
    concealValue: false
  };

  renderXValue() {
    const { xValueResultPO, concealValue } = this.props;

    var display = "-";
    if (xValueResultPO == "loading") {
      display = "Loading...";
    } else if (!ObjectUtil.isEmpty(xValueResultPO)) {
      display = StringUtil.formatCurrency(xValueResultPO.xValue);
      if (concealValue) {
        display = StringUtil.concealValue(display);
      }
    }

    return (
      <LinearGradient
        style={Styles.mainValueContainer}
        colors={["#FFFFFF", "#EAEAEA"]}
      >
        <Subtext style={Styles.textAlignment}>X-Value</Subtext>
        <Heading2_Currency
          style={[Styles.textAlignment, Styles.valueTextMargin]}
        >
          {display}
        </Heading2_Currency>
        <Subtext />
      </LinearGradient>
    );
  }

  renderLastQuarterChange() {
    const { xValueTrend, concealValue } = this.props;

    var display = "-";

    if (xValueTrend == "loading") {
      display = "Loading...";
    } else if (Array.isArray(xValueTrend) && xValueTrend.length > 2) {
      const latestItem = xValueTrend[0];
      const lastQuarterItem = xValueTrend[1];

      if (latestItem.value > 0 && lastQuarterItem.value > 0) {
        var difference = latestItem.value - lastQuarterItem.value;
        display = StringUtil.formatCurrency(difference);
        if (concealValue) {
          display = StringUtil.concealValue(display);
        }
      }
    }

    return (
      <LinearGradient
        style={Styles.subValueContainer}
        colors={["#FFFFFF", "#EAEAEA"]}
      >
        <Subtext style={Styles.textAlignment}>Last Quarter Change</Subtext>
        <Heading2 style={[Styles.textAlignment, Styles.valueTextMargin]}>
          {display}
        </Heading2>
        <Subtext />
      </LinearGradient>
    );
  }

  renderPSF() {
    const { xValueResultPO, concealValue } = this.props;

    var display = "-";
    if (xValueResultPO == "loading") {
      display = "Loading...";
    } else if (
      !ObjectUtil.isEmpty(xValueResultPO) &&
      !ObjectUtil.isEmpty(xValueResultPO.xValuePsf)
    ) {
      display = StringUtil.formatCurrency(xValueResultPO.xValuePsf);
      if (concealValue) {
        display = StringUtil.concealValue(display);
      }
    }

    return (
      <LinearGradient
        style={Styles.subValueContainer}
        colors={["#FFFFFF", "#EAEAEA"]}
      >
        <Subtext style={Styles.textAlignment}>PSF</Subtext>
        <Heading2 style={[Styles.textAlignment, Styles.valueTextMargin]}>
          {display}
        </Heading2>
        <Subtext />
      </LinearGradient>
    );
  }

  render() {
    const { style } = this.props;
    return (
      <View style={[Styles.valueInfoContainer, style]}>
        {this.renderLastQuarterChange()}
        <View style={{ width: Spacing.XS }} />
        {this.renderXValue()}
        <View style={{ width: Spacing.XS }} />
        {this.renderPSF()}
      </View>
    );
  }
}

Styles = {
  valueInfoContainer: {
    paddingVertical: Spacing.L,
    paddingHorizontal: Spacing.M,
    flexDirection: "row",
    alignItems: "stretch"
  },
  mainValueContainer: {
    flex: 1,
    paddingHorizontal: Spacing.XS,
    paddingVertical: Spacing.M,
    borderColor: SRXColor.LightGray,
    borderWidth: 1,
    justifyContent: "center",
    alignItems: "center"
  },
  subValueContainer: {
    flex: 1,
    padding: Spacing.XS,
    borderColor: SRXColor.LightGray,
    borderWidth: 1,
    marginVertical: Spacing.XS,
    justifyContent: "center",
    alignItems: "center"
  },
  textAlignment: {
    textAlign: "center"
  },
  valueTextMargin: {
    paddingVertical: Spacing.XS
  }
};

XValueInfo.propTypes = {
  xValueResultPO: PropTypes.oneOfType([PropTypes.object, PropTypes.string]),
  xValueTrend: PropTypes.oneOfType([PropTypes.array, PropTypes.string]),
  /**
   * style of the view container
   */
  style: PropTypes.object,
  /**
   * to show the value or not
   */
  concealValue: PropTypes.bool
};

export { XValueInfo };
