import React, { Component } from "react";
import { HorizontalCardPreview, Heading2 } from "../../../../../components";
import { ListingCardItem3 } from "../../../../Listings/ListItems";
import PropTypes from "prop-types";

class FeaturedListingsPreviewList extends Component {
  static propTypes = {
    onListingSelected: PropTypes.func
  };

  renderListingItem = ({ item, index }) => {
    const { onListingSelected } = this.props;
    return (
      <ListingCardItem3
        listingPO={item}
        key={index}
        onSelected={selectedListing => {
          if (onListingSelected) onListingSelected(selectedListing);
        }}
      />
    );
  };

  render() {
    const { data, title, onViewAll } = this.props;
    return (
      <HorizontalCardPreview
        onViewAll={onViewAll}
        data={data}
        renderItem={this.renderListingItem}
        title={title}
      />
    );
  }
}

export { FeaturedListingsPreviewList };
