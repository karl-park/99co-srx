import React, { Component } from "react";
import { View } from "react-native";
import PropTypes from "prop-types";
import Moment from "moment";
import {
  Heading2,
  Heading2_Currency,
  Subtext,
  LinearGradient
} from "../../../../components";
import { SRXColor } from "../../../../constants";
import { Spacing } from "../../../../styles";
import { ObjectUtil, StringUtil } from "../../../../utils";

class ValueResult extends Component {
  static defaultProps = {
    data: [],
    concealValue: false
  };

  renderXValue() {
    const { data, concealValue } = this.props;

    var display = "-";
    var dateDisplay = "";

    const lastItem = data[data.length - 1];

    if (!ObjectUtil.isEmpty(lastItem)) {
      display = StringUtil.formatCurrency(lastItem.value);
      if (concealValue) {
        display = StringUtil.concealValue(display);
      }
      dateDisplay = Moment(lastItem.key).format("MMM YYYY");
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
        <Subtext>Latest</Subtext>
      </LinearGradient>
    );
  }

  renderHighestXValue() {
    const { data, concealValue } = this.props;
    const lastItem = data[data.length - 1];

    var display = "-";
    var dateDisplay = "";

    var maxValueItem = data[0];
    data.map(item => {
      if (item.value >= maxValueItem.value) {
        maxValueItem = item;
      }
    });

    if (!ObjectUtil.isEmpty(maxValueItem.key) && maxValueItem.value > 0) {
      display = StringUtil.formatCurrency(maxValueItem.value);
      if (concealValue) {
        display = StringUtil.concealValue(display);
      }
      if (Moment(lastItem.key).isSame(maxValueItem.key, "day")) {
        dateDisplay = "Latest";
      } else {
        dateDisplay = this.checkQuarterMonth(maxValueItem.key);
      }
    }

    return (
      <LinearGradient
        style={Styles.subValueContainer}
        colors={["#FFFFFF", "#EAEAEA"]}
      >
        <Subtext style={Styles.textAlignment}>Highest Value</Subtext>
        <Heading2 style={[Styles.textAlignment, Styles.valueTextMargin]}>
          {display}
        </Heading2>
        <Subtext>{dateDisplay}</Subtext>
      </LinearGradient>
    );
  }

  renderLowestXValue() {
    const { data, concealValue } = this.props;
    const lastItem = data[data.length - 1];
    var display = "-";
    var dateDisplay = "";

    var minValueItem = data[0];
    data.map(item => {
      if (item.value <= minValueItem.value) {
        minValueItem = item;
      }
    });

    if (!ObjectUtil.isEmpty(minValueItem.key) && minValueItem.value > 0) {
      display = StringUtil.formatCurrency(minValueItem.value);
      if (concealValue) {
        display = StringUtil.concealValue(display);
      }
      if (Moment(lastItem.key).isSame(minValueItem.key, "day")) {
        dateDisplay = "Latest";
      } else {
        dateDisplay = this.checkQuarterMonth(minValueItem.key);
      }
    }

    return (
      <LinearGradient
        style={Styles.subValueContainer}
        colors={["#FFFFFF", "#EAEAEA"]}
      >
        <Subtext style={Styles.textAlignment}>Lowest Value</Subtext>
        <Heading2 style={[Styles.textAlignment, Styles.valueTextMargin]}>
          {display}
        </Heading2>
        <Subtext>{dateDisplay}</Subtext>
      </LinearGradient>
    );
  }

  checkQuarterMonth(date) {
    //date is in the format of yyyy-mm-dd
    let quarterString = "Q" + Moment(date).format("Q YYYY");
    return quarterString;
  }

  render() {
    const { data } = this.props;
    if (!ObjectUtil.isEmpty(data)) {
      return (
        <View style={Styles.valueInfoContainer}>
          {this.renderLowestXValue()}
          <View style={{ width: Spacing.XS }} />
          {this.renderXValue()}
          <View style={{ width: Spacing.XS }} />
          {this.renderHighestXValue()}
        </View>
      );
    } else {
      return <View />;
    }
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

export { ValueResult };
