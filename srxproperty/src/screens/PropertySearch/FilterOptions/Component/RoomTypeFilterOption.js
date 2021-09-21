import React, { Component } from "react";
import { View, Platform } from "react-native";
import PropTypes from "prop-types";

import { Styles } from "../Styles";
import { BodyText, Button } from "../../../../components";
import { Typography, Spacing } from "../../../../styles";
import { SRXColor } from "../../../../constants";
const isIOS = Platform.OS === "ios";
class RoomTypeFilterOption extends Component {
  static propTypes = {
    /*
     * prop types are optional
     */
    name: PropTypes.string,
    dataArray: PropTypes.array,
    selectedRoomsSet: PropTypes.array,
    onSelectRooms: PropTypes.func
  };

  static defaultProps = {
    name: "",
    dataArray: [],
    selectedRoomsSet: []
  };

  //on select each room
  onSelectRooms = (item, index) => {
    const { onSelectRooms } = this.props;
    if (onSelectRooms) {
      onSelectRooms(item, index);
    }
  };

  render() {
    const { name, dataArray, selectedRoomsSet } = this.props;
    return (
      <View style={Styles.eachOptionContainer}>
        <View style={{ paddingBottom: Spacing.XS }}>
          <BodyText style={Styles.bodyTextExtraStyle}>{name}</BodyText>
        </View>

        <View style={Styles.roomListsContainer}>
          {dataArray.map((item, index) => (
            <Button
              key={index}
              buttonStyle={[
                Styles.roomsContainer,
                {
                  backgroundColor: selectedRoomsSet.has(item.value)
                    ? SRXColor.Teal
                    : SRXColor.White
                },
                isIOS?{height:40}:{height:45}
                ,
                index == 0
                ? isIOS?{ borderTopLeftRadius: 20, borderBottomLeftRadius: 20 }:{ borderTopLeftRadius: 22.5, borderBottomLeftRadius: 22.5 }
                : null,
              index == dataArray.length - 1
                ? isIOS?{ borderTopRightRadius: 20, borderBottomRightRadius: 20 }:{ borderTopRightRadius: 22.5, borderBottomRightRadius: 22.5 }
                : null
              ]}
              textStyle={[
                Typography.Body,
                {
                  color: selectedRoomsSet.has(item.value)
                    ? SRXColor.White
                    : "#333333"
                }
              ]}
              onPress={() => this.onSelectRooms(item, index)}
            >
              {item.key}
            </Button>
          ))}
        </View>
      </View>
    );
  }
}

export { RoomTypeFilterOption };
