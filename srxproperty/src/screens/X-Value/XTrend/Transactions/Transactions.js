import React, { Component } from "react";
import { View } from "react-native";
import PropTypes from "prop-types";
import { SRXColor } from "../../../../constants";
import { Heading1, FeatherIcon, Button } from "../../../../components";
import { Spacing } from "../../../../styles";
import { ObjectUtil } from "../../../../utils";
import { TransactionItem } from "./TransactionItem";

class Transactions extends Component {
  static propTypes = {
    data: PropTypes.oneOfType([PropTypes.array, PropTypes.string])
  };

  static defaultProps = {
    data: []
  };

  state = {
    showFullList: false
  };

  renderTransactionList() {
    const { data } = this.props;
    const { showFullList } = this.state;

    //length is checked in render() method
    if (showFullList) {
      return data.map((item, index) => {
        return <TransactionItem item={item} key={index} />;
      });
    } else {
      const firstTransaction = data[0];
      return <TransactionItem item={firstTransaction} />;
    }
  }

  renderShowMoreButton() {
    const { showFullList } = this.state;
    return (
      <View style={{ alignItems: "flex-end", paddingTop: Spacing.M }}>
        <Button
          textStyle={{ fontSize: 14, color: SRXColor.Teal, marginRight: 4 }}
          rightView={
            <FeatherIcon
              name={showFullList ? "chevron-up" : "chevron-down"}
              size={16}
              color={SRXColor.Teal}
            />
          }
          onPress={() => {
            this.setState({ showFullList: !this.state.showFullList });
          }}
        >
          {showFullList ? "Close" : "Show More"}
        </Button>
      </View>
    );
  }

  render() {
    const { data } = this.props;
    if (!ObjectUtil.isEmpty(data) && Array.isArray(data)) {
      return (
        <View style={{ padding: Spacing.M }}>
          <Heading1>Latest Transactions</Heading1>
          {this.renderTransactionList()}
          {this.renderShowMoreButton()}
        </View>
      );
    } else {
      return <View />;
    }
  }
}

export { Transactions };
