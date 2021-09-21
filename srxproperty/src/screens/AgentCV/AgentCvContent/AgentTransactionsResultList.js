import React, { Component } from "react";
import { View, FlatList } from "react-native";
import { connect } from "react-redux";
import { ObjectUtil } from "../../../utils";
import { Spacing } from "../../../styles";
import { SRXColor } from "../../../constants";
import { BodyText } from "../../../components";
import PropTypes from "prop-types";
import { ListingPO } from "../../../dataObject";
import { TransactedListingListItem, ListingListItem } from "../../Listings";

class AgentTransactionsResultList extends Component {
  static propTypes = {
    listingPOs: PropTypes.arrayOf(PropTypes.instanceOf(ListingPO))
  };

  static defaultProps = {
    listingPOs: []
  };

  //render
  renderResultList() {
    const { listingPOs } = this.props;
    if (!ObjectUtil.isEmpty(listingPOs)) {
      return (
        <FlatList
          data={listingPOs}
          extraData={this.state}
          keyExtractor={item => item.key}
          renderItem={({ item, index }) =>
            this.renderListingItem({ item, index })
          }
        />
      );
    }
  }

  renderListingItem = ({ item, index }) => {
    return (
      <TransactedListingListItem
        key={index}
        containerStyle={{
          backgroundColor:
            index % 2 == 0 ? SRXColor.White : SRXColor.AccordionBackground
        }}
        listingPO={item}
        source={TransactedListingListItem.Sources.AgentCV}
      />
    );
  };

  render() {
    return <View style={{ flex: 1 }}>{this.renderResultList()}</View>;
  }
}

export { AgentTransactionsResultList };
