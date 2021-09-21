import React, { Component } from "react";
import { View, Dimensions } from "react-native";
import PropTypes from "prop-types";
import {
  HorizontalFlatList,
  Heading2,
  Separator
} from "../../../../components";
import { AgentCardItem4 } from "../../../Concierge/AgentItems";
import { Spacing } from "../../../../styles";
import { SRXColor } from "../../../../constants";
import { ObjectUtil } from "../../../../utils";
import PageControl from "react-native-page-control";

var { height, width } = Dimensions.get("window");

class SearchFeaturedAgents extends Component {
  constructor(props) {
    super(props);
    this.viewabilityConfig = { itemVisiblePercentThreshold: 70 };
  }

  state = {
    currentIndex: 0
  };

  onViewableItemsChanged = ({ viewableItems, changed }) => {
    const { currentIndex } = this.state;
    var newIndex = currentIndex;
    if (viewableItems.length == 1) {
      const firstObject = viewableItems[0];
      newIndex = firstObject.index;
      this.setState({ currentIndex: newIndex });
    }
  };

  //render each agent item
  renderAgentItem = agentItem => {
    const { item, index } = agentItem;
    return (
      <AgentCardItem4
        key={index}
        agentPO={item}
        onItemSelected={this.onFeaturedAgentSelected}
      />
    );
  };

  //onSelect featured agent item
  onFeaturedAgentSelected = agentPO => {
    if (!ObjectUtil.isEmpty(agentPO)) {
      //direct to agent cv in parent component
      const { onSelectFeaturedAgentItem } = this.props;
      if (onSelectFeaturedAgentItem) {
        onSelectFeaturedAgentItem(agentPO);
      }
    }
  };

  // renderSeparator() {
  //   return <Separator />;
  // }

  //main render method
  render() {
    const { data, title } = this.props;
    const { currentIndex } = this.state;
    return (
      <View style={styles.container}>
        {/* <View
          style={{
            flex: 1,
            paddingVertical: Spacing.S,
            paddingHorizontal: Spacing.M,
            alignItems: "center",
            justifyContent: "center"
          }}
        >
          <Heading2 numberOfLines={1}>{title}</Heading2>
        </View> */ }

        <HorizontalFlatList
          ref={component => (this.flatlist = component)}
          scrollEnabled={true}
          data={data}
          renderItem={this.renderAgentItem}
          onViewableItemsChanged={this.onViewableItemsChanged}
          bounces={false}
          getItemLayout={(data, index) => ({
            length: width,
            offset: width * index + (index > 0 ? 15 * (index - 1) : 0),
            index
          })}
          viewabilityConfig={this.viewabilityConfig}
          pagingEnabled={true}
        />
        <PageControl
          style={{ marginBottom: Spacing.XS }}
          numberOfPages={data.length}
          currentPage={currentIndex}
          pageIndicatorTintColor={SRXColor.Gray}
          currentPageIndicatorTintColor={SRXColor.Teal}
          indicatorStyle={{ borderWidth: 1, borderColor: "white" }}
        />
        {/* {this.renderSeparator()} */}
      </View>
    );
  }
}

const styles = {
  container: {
    flex: 1,
    marginVertical: Spacing.XS
  }
};

//Search Featured agents prop types
SearchFeaturedAgents.propTypes = {
  data: PropTypes.array.isRequired,
  title: PropTypes.string,
  onSelectFeaturedAgentItem: PropTypes.func
};

export { SearchFeaturedAgents };
