import React, { Component } from "react";
import { View, Image } from "react-native";
import PropTypes from "prop-types";
import { Listings_MRT_Icon } from "../../assets";
import {
  Heading2,
  SmallBodyText,
  Subtext,
  TouchableHighlight,
  FeatherIcon,
  MaterialCommunityIcons,
  Button,
  Collapsible
} from "../../components";
import { SRXColor } from "../../constants";
import { AmenityPO } from "../../dataObject";
import { ObjectUtil } from "../../utils";
import { Spacing } from "../../styles";
import { TravelTypes } from "./constant";
import { AmenityItemTravelMode } from "./AmenityItemTravelMode";

class AmenityItem extends Component {
  static defaultProps = {
    travelMethod: TravelTypes.None,
    showTravelTime: false, //to overwrite the state
    onTravelTimeToggle: null
  };

  constructor(props) {
    super(props);
    this.toggle = this.toggle.bind(this);
  }

  toggle() {
    const { onTravelTimeToggle, item, showTravelTime } = this.props;
    if (onTravelTimeToggle) {
      onTravelTimeToggle({ item, showing: !showTravelTime });
    }
  }

  renderDistance() {
    const { item } = this.props;
    return (
      <Subtext style={{ marginLeft: Spacing.XS }}>
        {item.getDistanceDesc()}
      </Subtext>
    );
  }

  renderTravelTime() {
    const { item, travelMethod, onTravelModeSelected } = this.props;
    return (
      <AmenityItemTravelMode
        item={item}
        travelMethod={travelMethod}
        onTravelModeSelected={onTravelModeSelected}
      />
    );
  }

  renderAmenityItem() {
    const { item, showTravelTime } = this.props;
    if (!ObjectUtil.isEmpty(item)) {
      return (
        <TouchableHighlight onPress={this.toggle}>
          <View
            style={{
              backgroundColor: SRXColor.White,
              flexDirection: "row",
              alignItems: "center",
              paddingHorizontal: Spacing.M,
              paddingVertical: Spacing.S
            }}
          >
            <Heading2 style={{ flex: 1 }}>{item.getName()}</Heading2>
            {this.renderDistance()}
            <FeatherIcon
              name={showTravelTime ? "chevron-up" : "chevron-down"}
              size={20}
              color={SRXColor.Black}
              style={{ marginLeft: 8 }}
            />
          </View>
        </TouchableHighlight>
      );
    } else {
      return <View />;
    }
  }

  render() {
    return (
      <View>
        {this.renderAmenityItem()}
        <Collapsible collapsed={!this.props.showTravelTime}>
          {this.renderTravelTime()}
        </Collapsible>
      </View>
    );
  }
}

AmenityItem.propTypes = {
  item: PropTypes.instanceOf(AmenityPO).isRequired,
  showTravelTime: PropTypes.bool, //to hide or show the collapsible content, use to overwrite the state
  travelMethod: PropTypes.oneOf(Object.keys(TravelTypes)),
  onTravelTimeToggle: PropTypes.func, //a function to call when collapsible content toggle, providing {item, showing}
  onTravelModeSelected: PropTypes.func //a callback function to indicate which travel method selected, providing { travelMethod }
};

export { AmenityItem };
