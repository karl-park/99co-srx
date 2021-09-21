import React, { Component } from "react";
import { View, TouchableOpacity, ScrollView } from "react-native";
import PropTypes from "prop-types";
import { BodyText, Separator } from "../../components";
import { SRXColor } from "../../constants";
import { Spacing } from "../../styles";
import { AmenitiesOptions, AmenityTypes } from "./constant";
import { ObjectUtil } from "../../utils";

class AmenityCategoryTabBar extends Component {
  state = {
    data: AmenitiesOptions,
    tabLayouts: [],
    contentSize: { width: 0, height: 0 },
    scrollViewLayout: { x: 0, y: 0, width: 0, height: 0 }
  };

  static defaultProps = {
    category: AmenityTypes.Others
  };

  constructor(props) {
    super(props);

    this.renderItem = this.renderItem.bind(this);
  }

  componentDidMount() {
    this.updateAmenitiesOptions();
    this.scrollToCategory(this.props.category);
  }

  updateAmenitiesOptions() {
    const { amenitiesOptions } = this.props;
    if (!ObjectUtil.isEmpty(amenitiesOptions)) {
      this.setState({ data: amenitiesOptions });
    }
  }

  componentDidUpdate(prevProps, prevState) {
    if (prevProps.category !== this.props.category) {
      this.scrollToCategory(this.props.category);
    } else if (
      prevState.contentSize !== this.state.contentSize ||
      prevState.scrollViewLayout !== this.state.scrollViewLayout
    ) {
      this.scrollToCategory(this.props.category);
    }

    if (prevProps.amenitiesOptions !== this.props.amenitiesOptions) {
      this.setState({ data: this.props.amenitiesOptions });
    }
  }

  getCategoryIndex(type) {
    const { data } = this.state;
    const index = data.findIndex(function({ key }) {
      return key === type;
    });

    return index;
  }

  scrollToCategory(type) {
    const index = this.getCategoryIndex(type);
    if (index >= 0) {
      const bounds = this.getItemBoundsAtIndex(index);
      const { scrollViewLayout, contentSize } = this.state;

      let newOffSet = bounds.x + bounds.width / 2 - scrollViewLayout.width / 2;
      if (newOffSet < 0) {
        newOffSet = 0;
      } else if (newOffSet > contentSize.width - scrollViewLayout.width) {
        newOffSet = contentSize.width - scrollViewLayout.width;
      }
      this.scrollView.scrollTo({ x: newOffSet, y: 0, animated: true });
    }
  }

  onTypeSelected(item) {
    const { onTypeSelected } = this.props;
    if (onTypeSelected) {
      onTypeSelected({ type: item.key });
    }
  }

  renderIndicator() {
    return (
      <View
        style={{
          height: 4,
          backgroundColor: SRXColor.TextLink
        }}
      />
    );
  }

  renderItem = ({ item, index }) => {
    const { showIndicator, category } = this.props;
    // const { data } = this.state;

    const isSelected = item.key === category;

    const indicate = showIndicator && isSelected;

    const textColor = indicate
      ? {
          color: SRXColor.TextLink
        }
      : {
          color: SRXColor.Black
        };

    return (
      <TouchableOpacity
        onPress={() => this.onTypeSelected(item)}
        onLayout={({ nativeEvent: { layout } }) => {
          if (showIndicator) {
            const { tabLayouts } = this.state;
            if (ObjectUtil.isEmpty(tabLayouts[index])) {
              const newLayouts = [...tabLayouts];
              newLayouts[index] = layout;

              this.setState({ tabLayouts: newLayouts });
            }
          }
        }}
      >
        <View
          style={{
            backgroundColor: SRXColor.White,
            paddingHorizontal: Spacing.XS / 2
          }}
        >
          <BodyText
            style={[
              {
                paddingVertical: Spacing.S,
                paddingHorizontal: Spacing.XS
              },
              textColor
            ]}
          >
            {item.title}
          </BodyText>
          {indicate ? this.renderIndicator() : null}
        </View>
      </TouchableOpacity>
    );
  };

  getItemBoundsAtIndex(index) {
    const { tabLayouts } = this.state;
    let itemLayout = { x: 0, y: 0, width: 0, height: 0 };
    let offsetX = 0;

    if (!ObjectUtil.isEmpty(tabLayouts[index])) {
      itemLayout = tabLayouts[index];
    }
    return itemLayout;
  }

  render() {
    const { data, scrollViewLayout, contentSize } = this.state;
    return (
      <View>
        <ScrollView
          ref={component => (this.scrollView = component)}
          style={{
            backgroundColor: SRXColor.White,
            paddingHorizontal: Spacing.XS / 2
          }}
          horizontal
          showsHorizontalScrollIndicator={false}
          showsVerticalScrollIndicator={false}
          onContentSizeChange={(contentWidth, contentHeight) => {
            if (
              contentWidth !== contentSize.width ||
              contentHeight !== contentSize.height
            ) {
              this.setState({
                contentSize: { width: contentWidth, height: contentHeight }
              });
            }
          }}
          onLayout={({ nativeEvent: { layout } }) => {
            if (layout.width !== scrollViewLayout.width) {
              this.setState({ scrollViewLayout: layout });
            }
          }}
        >
          {data.map((item, index) => {
            return this.renderItem({ item, index });
          })}
        </ScrollView>
        <Separator />
      </View>
    );
  }
}

AmenityCategoryTabBar.propTypes = {
  /**
   * Optional
   *
   * To inform a category is selected
   */
  onTypeSelected: PropTypes.func,
  /**
   * Optional
   */
  showIndicator: PropTypes.bool,
  /**
   * Optional
   *
   * Indicate the selected categoty
   * if showIndicator = true, providing 'category' will indicate which one is selected
   */
  category: PropTypes.oneOf(Object.keys(AmenityTypes)),
  amenitiesOptions: PropTypes.arrayOf(PropTypes.object)
  /**
   * Optional
   * Update data if default Amenities Options are changed
   */
};

export { AmenityCategoryTabBar };
