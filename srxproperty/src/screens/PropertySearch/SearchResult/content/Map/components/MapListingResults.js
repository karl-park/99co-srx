import React, { Component } from "react";
import { View } from "react-native";
import SafeAreaView from "react-native-safe-area-view";
import PropTypes from "prop-types";
import {
  HorizontalCardPreview,
  Separator,
  BodyText,
  HorizontalFlatList
} from "../../../../../../components";
import { SRXColor } from "../../../../../../constants";
import { AgentPO, ListingPO } from "../../../../../../dataObject";
import { Spacing } from "../../../../../../styles";
import { ObjectUtil } from "../../../../../../utils";
import {
  ListingCardItem2,
  TransactedListingCardItem
} from "../../../../../Listings";
import { AgentCardItem3 } from "../../../../../Concierge";

class MapListingResults extends Component {
  renderFeaturedAgentItem = ({ item, index }) => {
    const { onFeaturedAgentSelected } = this.props;
    return (
      <AgentCardItem3 agentPO={item} onItemSelected={onFeaturedAgentSelected} />
    );
  };

  renderFeaturedAgents = () => {
    const { featuredAgents } = this.props;
    if (!ObjectUtil.isEmpty(featuredAgents)) {
      return (
        <View style={{backgroundColor:  SRXColor.AccordionBackground}}>
          <Separator />
          <BodyText style={{marginLeft: Spacing.M, marginVertical: Spacing.XS}}>SRX Featured Agents</BodyText>
          <HorizontalFlatList
            style={{paddingBottom: Spacing.XS}}
            data={featuredAgents}
            renderItem={this.renderFeaturedAgentItem}
          />
        </View>
      );
    }
  };

  renderItem = ({ item, index }) => {
    const { onItemSelected, isTransacted } = this.props;
    if (isTransacted) {
      return (
        <TransactedListingCardItem
          listingPO={item}
          onSelected={onItemSelected}
        />
      );
    } else {
      return <ListingCardItem2 listingPO={item} onSelected={onItemSelected} />;
    }
  };

  render() {
    const { style, onLayout, listingPOs, onEndReached } = this.props;
    return (
      <View onLayout={onLayout}>
        <SafeAreaView
          style={[
            { backgroundColor: "white" },
            style,
            { flexDirection: "column-reverse" }
          ]}
          forceInset={{ bottom: "always" }}
        >
          <HorizontalCardPreview
            style={{ paddingBottom: Spacing.XS }}
            data={listingPOs}
            renderItem={this.renderItem}
            onEndReached={onEndReached}
          />
          <Separator />
          {this.renderFeaturedAgents()}
        </SafeAreaView>
      </View>
    );
  }
}

MapListingResults.propTypes = {
  style: PropTypes.object,
  onLayout: PropTypes.func,
  listingPOs: PropTypes.arrayOf(PropTypes.instanceOf(ListingPO)),
  featuredAgents: PropTypes.arrayOf(PropTypes.instanceOf(AgentPO)),
  isTransacted: PropTypes.bool,
  onItemSelected: PropTypes.func,
  onFeaturedAgentSelected: PropTypes.func,
  onEndReached: PropTypes.func
};

export { MapListingResults };
