import React, { Component } from "react";
import { View } from "react-native";
import { Spacing, Typography } from "../../../styles";
import { HorizontalCardPreview, Heading2 } from "../../../components";
import { ListingCardItem } from "../ListItems";

class ListingsCardPreview extends Component {
  renderListingItem = ({ item, index }) => {
    const { onListingSelected } = this.props;
    return (
      <ListingCardItem
        listingPO={item}
        key={index}
        onSelected={selectedListing => {
          if (onListingSelected) onListingSelected(selectedListing);
        }}
      />
    );
  };

  render() {
    const { data, title, onViewAll, style } = this.props;
    return (
      <HorizontalCardPreview
        style={style}
        onViewAll={onViewAll}
        data={data}
        renderItem={this.renderListingItem}
        title={title}
      />
    );
  }
}

export { ListingsCardPreview };