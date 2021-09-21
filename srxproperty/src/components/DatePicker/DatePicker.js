import React, { Component } from "react";
import { View } from "react-native";
import DatePicker from "react-native-datepicker";
import PropTypes from "prop-types";
import { FeatherIcon } from "..";
import { SRXColor } from "../../constants";
import { InputStyles, Spacing } from "../../styles";

class Picker extends Component {
  render() {
    const { style, ...rest } = this.props;
    return (
      <View style={{ flexDirection: "row" }}>
        <DatePicker
          style={[
            InputStyles.container,
            InputStyles.containerBorder,
            { flex: 1, paddingHorizontal: Spacing.S },
            style
          ]}
          customStyles={{
            dateText: {
              fontSize: 16,
              color: SRXColor.Black,
              textAlign: "left"
            },
            placeholderText: {
              fontSize: 16,
              color: "#8DABC4"
            },
            dateInput: {
              alignItems: "flex-start",
              borderWidth: 0,
              paddingRight: Spacing.XS
            },
            btnTextConfirm: {
              color: SRXColor.Teal
            }
          }}
          iconComponent={
            <FeatherIcon
              name={"chevron-down"}
              size={20}
              color={SRXColor.Black}
            />
          }
          confirmBtnText={"Done"}
          cancelBtnText={"Cancel"}
          {...rest}
        />
      </View>
    );
  }
}

Picker.propTypes = {
  style: PropTypes.oneOf([PropTypes.object, PropTypes.array])
};

//for more props, please check https://github.com/xgfe/react-native-datepicker

export default Picker;
