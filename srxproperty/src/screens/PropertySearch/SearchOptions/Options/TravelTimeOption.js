import React, { Component } from "react";
import { View, Text, Slider } from "react-native";
import { KeyboardAwareScrollView } from "react-native-keyboard-aware-scroll-view";
import { Navigation } from "react-native-navigation";
import SafeAreaView from "react-native-safe-area-view";
import Icon from "react-native-vector-icons/Feather";
import {AppTopBar_BackBtn} from "../../../../assets";
import {AppTheme} from "../../../../styles";

import { TextInput, BodyText } from "../../../../components";
import { SRXColor } from "../../../../constants";
import { Spacing } from "../../../../styles";
import { Styles } from "../Styles";

class TravelTimeOption extends Component {
  static options(passProps) {
    return {
      topBar: {
        backButton: {
          icon: AppTopBar_BackBtn,
          color: AppTheme.topBarBackButtonColor,
          title: ""
        },
        title: {
          text: "Search by Travel Time"
          // alignment: "center"
        },
        rightButtons: [
          {
            id: "btn_search",
            text: "Search"
          }
        ]
      }
    };
  }

  constructor(props) {
    super(props);

    Navigation.events().bindComponent(this);
  }

  state = {
    travelTimeArray: [0, 10, 20, 30, 40, 50, 60],
    selectedTime: 0,
    minTime: 0,
    maxTime: 60,
    locationText: ""
  };

  navigationButtonPressed({ buttonId }) {
    if (buttonId === "btn_search") {
      console.log(this.state.selectedTime);
      console.log("Selected Time");
    }
  }

  componentDidMount() {}

  renderTravelTime() {
    const { selectedTime, minTime, maxTime, travelTimeArray } = this.state;

    return (
      <View style={Styles.travelTimeContainer}>
        <BodyText style={Styles.travelTimeHeaderStyle}>
          What is the maximum time you wish to travel by public transport?
        </BodyText>
        <BodyText style={Styles.travelTimeHeaderStyle}>
          {this.state.selectedTime}
          {" mins"}
        </BodyText>
        <Slider
          style={{ flex: 1 }}
          trackStyle={{ height: 7 }}
          thumbStyle={Styles.sliderThumbStyle}
          maximumTrackTintColor={SRXColor.LightGray}
          minimumTrackTintColor={SRXColor.Teal}
          thumbTintColor={SRXColor.White}
          step={1}
          value={selectedTime}
          minimumValue={minTime}
          maximumValue={maxTime}
          onValueChange={val => this.setState({ selectedTime: val })}
        />
        <View style={{ flexDirection: "row", justifyContent: "space-between" }}>
          {travelTimeArray.map(value => (
            <BodyText style={{ alignSelf: "center" }}>{value}</BodyText>
          ))}
        </View>
      </View>
    );
  }

  renderFindLocation() {
    const { locationText } = this.state;

    return (
      <View style={Styles.locationContainer}>
        <BodyText style={Styles.locationHeaderStyle}>
          Where is your location?
        </BodyText>
        <TextInput
          leftView={
            <Icon
              name="search"
              size={25}
              color={"#8DABC4"}
              style={{ alignSelf: "center", marginRight: Spacing.XS }}
            />
          }
          placeholder={"Enter place, address or location"}
          defaultValue={locationText}
          onChangeText={value => this.setState({ locationText: value })}
        />
      </View>
    );
  }

  render() {
    return (
      <SafeAreaView
        style={{ flex: 1, backgroundColor: SRXColor.White }}
        forceInset={{ bottom: "never" }}
      >
        {/* <SafeAreaView
          style={{ flex: 1, backgroundColor: "white" }}
          forceInset={{ bottom: "never" }}
        > */}
          <KeyboardAwareScrollView style={{ flex: 1 }}>
            <View style={{ flex: 1 }}>
              {this.renderTravelTime()}
              {this.renderFindLocation()}
            </View>
          </KeyboardAwareScrollView>
        {/* </SafeAreaView> */}
      </SafeAreaView>
    );
  }
}

export { TravelTimeOption };
