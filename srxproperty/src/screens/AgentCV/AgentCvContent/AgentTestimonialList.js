import React, { Component } from "react";
import { View, StyleSheet } from "react-native";
import PropTypes from "prop-types";
import { TestimonialPO } from "../../../dataObject";
import { Spacing } from "../../../styles";
import { BodyText, Heading2, HorizontalFlatList } from "../../../components";
import PageControl from "react-native-page-control";
import { SRXColor } from "../../../constants";
import { ObjectUtil } from "../../../utils";

import { AgentTestimonialCardItem } from "./";

const SECTION_TITLE = "Testimonials";

class AgentTestimonialList extends Component {
  static propTypes = {
    testimonials: PropTypes.arrayOf(PropTypes.instanceOf(TestimonialPO))
  };

  static defaultProps = {
    testimonials: []
  };

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

  renderItem = ({ item, index }) => {
    return <AgentTestimonialCardItem testimonialPO={item} />;
  };

  render() {
    const { style, testimonials } = this.props;
    const { currentIndex } = this.state;
    return (
      <View style={[styles.containerStyle, style]}>
        <Heading2 style={styles.titleStyle}>{SECTION_TITLE}</Heading2>
        <HorizontalFlatList
          data={testimonials}
          bounces={false}
          keyExtractor={item => item.id}
          indicatorStyle={"white"}
          onViewableItemsChanged={this.onViewableItemsChanged}
          getItemLayout={(data, index) => ({
            length: 270,
            offset: 270 * index + (index > 0 ? 15 * (index - 1) : 0),
            index
          })}
          viewabilityConfig={this.viewabilityConfig}
          renderItem={this.renderItem}
          extraData={this.state}
        />
        <PageControl
          style={{ marginBottom: Spacing.XS }}
          numberOfPages={testimonials.length}
          currentPage={currentIndex}
          pageIndicatorTintColor={SRXColor.Gray}
          currentPageIndicatorTintColor={SRXColor.Teal}
          indicatorStyle={{ borderWidth: 1, borderColor: "white" }}
        />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  containerStyle: {
    paddingHorizontal: Spacing.M,
    paddingTop: Spacing.M,
    paddingBottom: Spacing.S
  },
  titleStyle: {
    flex: 1,
    marginRight: Spacing.XS,
    marginBottom: Spacing.XL
  },
  testimonialStyle: {
    width: 270,
    elevation: 2,
    shadowColor: SRXColor.Black,
    shadowOffset: { width: 2, height: 2 },
    shadowRadius: 2,
    shadowOpacity: 0.5,
    borderWidth: 1,
    borderColor: SRXColor.LightGray,
    borderRadius: 5,
    marginRight: 15,
    backgroundColor: SRXColor.White
  },
  quoteLeftBox: {
    margin: 10,
    width: 40,
    height: 40,
    backgroundColor: SRXColor.Teal,
    justifyContent: "center",
    borderRadius: 5
  },
  textStyle: {
    textAlign: "center",
    margin: Spacing.XS
  }
});

export { AgentTestimonialList };
