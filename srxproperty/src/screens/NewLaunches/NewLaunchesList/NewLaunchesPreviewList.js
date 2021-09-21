import React, { Component } from "react";
import { HorizontalCardPreview, Heading2 } from "../../../components";
import { NewLaunchesCardItem } from "../NewLaunchesItems";

class NewLaunchesPreviewList extends Component {
  renderListingItem = ({ item, index }) => {
    const { onListingSelected } = this.props;
    return (
      <NewLaunchesCardItem
        projectDetailPO={item}
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

export { NewLaunchesPreviewList };
