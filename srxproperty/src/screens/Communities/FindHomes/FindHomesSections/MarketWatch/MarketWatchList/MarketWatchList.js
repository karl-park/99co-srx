import React, { Component } from "react";
import PropTypes from "prop-types";

import { HorizontalFlatList } from "../../../../../../components";
import { MarketWatchCardItem } from "../MarketWatchItems";

class MarketWatchList extends Component {
  //rendering each
  renderMarketWatchItem = ({ item, index }) => {
    return <MarketWatchCardItem key={index} marketWatchPO={item} />;
  };

  render() {
    const { data } = this.props;
    return (
      <HorizontalFlatList data={data} renderItem={this.renderMarketWatchItem} />
    );
  }
}

MarketWatchList.propTypes = {
  /** market watch list */
  data: PropTypes.array
};

export { MarketWatchList };
