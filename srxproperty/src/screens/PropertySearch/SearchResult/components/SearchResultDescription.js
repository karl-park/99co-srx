import React, { Component } from "react";
import PropTypes from "prop-types";
import { SmallBodyText } from "../../../../components";
import { ObjectUtil, StringUtil } from "../../../../utils";
import { SRXColor } from "../../../../constants";

class SearchResultDescription extends Component {
  static defaultProps = {
    searchOptions: null,
    isTransacted: false,
    counts: null
  };

  getResultCountDescription() {
    const { searchOptions, isTransacted, counts } = this.props;

    let description = "";

    if (!ObjectUtil.isEmpty(searchOptions) && !ObjectUtil.isEmpty(counts)) {
      description = "No Property";

      const { total, srxStpCount, matchedCount, nearbyCount } = counts;

      if (!ObjectUtil.isEmpty(searchOptions.searchText)) {
        //number of result
        if (matchedCount > 0) {
          if (matchedCount == 1) {
            description = matchedCount + " Property";
          } else {
            description =
              StringUtil.formatThousand(matchedCount) + " Properties";
          }
        }
        //for location
        if (!ObjectUtil.isEmpty(searchOptions.displayText)) {
          description += " for " + searchOptions.displayText;
        } else if (!ObjectUtil.isEmpty(searchOptions.searchText)) {
          description += " for " + searchOptions.searchText;
        }

        //nearby
        if (nearbyCount > 0) {
          description += ". ";
          if (nearbyCount == 1) {
            description += nearbyCount + " nearby";
          } else {
            description += StringUtil.formatThousand(nearbyCount) + " nearby";
          }
        }
      } else {
        if (srxStpCount > 0) {
          if (srxStpCount == 1) {
            description = srxStpCount + " Property";
          } else {
            description =
              StringUtil.formatThousand(srxStpCount) + " Properties";
          }
        } else if (total > 0) {
          if (total == 1) {
            description = total + " Property";
          } else {
            description = StringUtil.formatThousand(total) + " Properties";
          }
        }
      }

      //sale or sold, rent or rented
      if (searchOptions.type === "S") {
        description += isTransacted ? " recently sold" : " for sale";
      } else {
        description += isTransacted ? " recently rented" : " for rent";
      }
    }

    return description;
  }

  render() {
    const message = this.getResultCountDescription();

    return (
      <SmallBodyText
        style={{ flex: 1, color: SRXColor.Gray }}
        numberOfLines={1}
      >
        {message}
      </SmallBodyText>
    );
  }
}

SearchResultDescription.propTypes = {
  searchOptions: PropTypes.object,
  isTransacted: PropTypes.bool,
  counts: PropTypes.object
};

export { SearchResultDescription };
