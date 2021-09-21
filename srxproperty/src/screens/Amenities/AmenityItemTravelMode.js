import React, { Component } from "react";
import { View, Image } from "react-native";
import PropTypes from "prop-types";
import { Listings_MRT_Icon } from "../../assets";
import {
  SmallBodyText,
  MaterialCommunityIcons,
  Button,
} from "../../components";
import { SRXColor } from "../../constants";
import { AmenityPO } from "../../dataObject";
import { Spacing } from "../../styles";
import { ObjectUtil } from "../../utils";
import { TravelTypes } from "./constant";

class AmenityItemTravelMode extends Component {
  constructor(props) {
    super(props);
    this.onTransitSelected = this.onTransitSelected.bind(this);
    this.onDrivingSelected = this.onDrivingSelected.bind(this);
    this.onWalkingSelected = this.onWalkingSelected.bind(this);
  }

  onTransitSelected() {
    const { onTravelModeSelected, item } = this.props;
    if (onTravelModeSelected) {
      onTravelModeSelected({ travelMode: TravelTypes.Transit, amenity: item });
    }
  }

  onDrivingSelected() {
    const { onTravelModeSelected, item } = this.props;
    if (onTravelModeSelected) {
      onTravelModeSelected({ travelMode: TravelTypes.Drive, amenity: item });
    }
  }

  onWalkingSelected() {
    const { onTravelModeSelected, item } = this.props;
    if (onTravelModeSelected) {
      onTravelModeSelected({ travelMode: TravelTypes.Walk, amenity: item });
    }
  }

  renderTransitButton() {
    const { item, travelMethod } = this.props;

    var travelTimeRequired = "-";

    try {
      if (
        !ObjectUtil.isEmpty(item) &&
        !ObjectUtil.isEmpty(item.getTransitRoute())
      ) {
        const route = item.getTransitRoute();

        travelTimeRequired = route.legs[0].duration.text.replace("hour", "hr");
      }
    } catch (error) {
      console.log(error);
    }

    const isSelected = travelMethod === TravelTypes.Transit && !ObjectUtil.isEmpty(item);
    console.log(travelTimeRequired);
    return (
      <Button
        buttonType={Button.buttonTypes.big}
        buttonStyle={Styles.TravelTimeButton}
        textStyle={Styles.TravelTimeButtonText}
        leftView={({ highlighted }) =>
          this.renderTravelTimeTitle({
            title: "Bus / MRT",
            travelMethod: TravelTypes.Transit,
            highlighted
          })
        }
        isSelected={isSelected}
        onPress={this.onTransitSelected}
      >
        {travelTimeRequired}
      </Button>
    );
  }

  renderDrivingButton() {
    const { item, travelMethod } = this.props;

    var travelTimeRequired = "-";

    try {
      if (
        !ObjectUtil.isEmpty(item) &&
        !ObjectUtil.isEmpty(item.getDrivingRoute())
      ) {
        const route = item.getDrivingRoute();

        travelTimeRequired = route.legs[0].duration.text.replace("hour", "hr");
      }
    } catch (error) {
      console.log(error);
    }

    const isSelected = travelMethod === TravelTypes.Drive && !ObjectUtil.isEmpty(item);
    console.log(travelTimeRequired);
    return (
      <Button
        buttonType={Button.buttonTypes.big}
        buttonStyle={[
          Styles.TravelTimeButton,
          {
            marginHorizontal: Spacing.XS
          }
        ]}
        textStyle={Styles.TravelTimeButtonText}
        leftView={({ highlighted }) =>
          this.renderTravelTimeTitle({
            title: "Drive",
            travelMethod: TravelTypes.Drive,
            highlighted
          })
        }
        isSelected={isSelected}
        onPress={this.onDrivingSelected}
      >
        {travelTimeRequired}
      </Button>
    );
  }

  renderWalkButton() {
    const { item, travelMethod } = this.props;

    var travelTimeRequired = "-";

    try {
      if (
        !ObjectUtil.isEmpty(item) &&
        !ObjectUtil.isEmpty(item.getWalkingRoute())
      ) {
        const route = item.getWalkingRoute();

        travelTimeRequired = route.legs[0].duration.text.replace("hour", "hr");
      }
    } catch (error) {
      console.log(error);
    }

    const isSelected = travelMethod === TravelTypes.Walk && !ObjectUtil.isEmpty(item);
    console.log(travelTimeRequired);
    return (
      <Button
        buttonType={Button.buttonTypes.big}
        buttonStyle={{
          flexDirection: "column",
          flex: 1
        }}
        textStyle={Styles.TravelTimeButtonText}
        leftView={({ highlighted }) =>
          this.renderTravelTimeTitle({
            title: "Walk",
            travelMethod: TravelTypes.Walk,
            highlighted
          })
        }
        isSelected={isSelected}
        onPress={this.onWalkingSelected}
      >
        {travelTimeRequired}
      </Button>
    );
  }

  renderTravelTimeTitle({ title, travelMethod, highlighted }) {
    const tintColor = highlighted ? SRXColor.White : SRXColor.Gray;
    const textColor = highlighted ? SRXColor.White : SRXColor.Black;

    return (
      <View style={{ flexDirection: "row" }}>
        {this.renderTravelTimeTitleIcon({ travelMethod, tintColor })}
        <SmallBodyText style={{ color: textColor, fontWeight: "bold" }}>
          {title}
        </SmallBodyText>
      </View>
    );
  }

  renderTravelTimeTitleIcon({ travelMethod, tintColor }) {
    if (travelMethod === TravelTypes.Transit) {
      return (
        <Image
          source={Listings_MRT_Icon}
          style={{
            tintColor: tintColor,
            width: 16,
            height: 16,
            marginRight: Spacing.XS
          }}
          resizeMode={"contain"}
        />
      );
    } else if (travelMethod === TravelTypes.Drive) {
      return (
        <MaterialCommunityIcons
          name={"car-side"}
          size={16}
          color={tintColor}
          style={{ marginRight: Spacing.XS }}
        />
      );
    } else if (travelMethod === TravelTypes.Walk) {
      return (
        <MaterialCommunityIcons
          name={"walk"}
          size={16}
          color={tintColor}
          style={{ marginRight: Spacing.XS }}
        />
      );
    }

    return null;
  }

  render() {
    const { style } = this.props;
    return (
      <View
        style={[
          {
            flexDirection: "row",
            paddingHorizontal: Spacing.M,
            paddingVertical: Spacing.XS
          },
          style
        ]}
      >
        {this.renderTransitButton()}
        {this.renderDrivingButton()}
        {this.renderWalkButton()}
      </View>
    );
  }
}

const Styles = {
  TravelTimeButtonText: {
    fontSize: 16,
    fontWeight: "bold",
    lineHeight: 18,
    marginVertical: Spacing.S
  },
  TravelTimeButton: {
    flexDirection: "column",
    flex: 1
  }
};

AmenityItemTravelMode.propTypes = {
  style: PropTypes.object, //style if the container
  item: PropTypes.instanceOf(AmenityPO),
  travelMethod: PropTypes.oneOf(Object.keys(TravelTypes)),
  onTravelModeSelected: PropTypes.func //a callback function to indicate which travel method selected, providing { travelMethod }
};

export { AmenityItemTravelMode };
