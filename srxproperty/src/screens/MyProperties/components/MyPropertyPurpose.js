import React, { Component } from "react";
import { View, StyleSheet, TouchableOpacity } from "react-native";
import PropTypes from "prop-types";
import { SmallBodyText } from "../../../components";

import { Spacing } from "../../../styles";
import { SRXColor, pt_Purpose_Array } from "../../../constants";

class MyPropertyPurpose extends Component {
  //on update selected purpose value
  onSelectPurpose = item => {
    const { onSelectPurpose } = this.props;
    if (onSelectPurpose) {
      onSelectPurpose(item);
    }
  };

  render() {
    const { purpose, style } = this.props;
    return (
      <View style={[styles.occupanyAndPurposeContainer, style]}>
        {pt_Purpose_Array.map((item, index) => {
          return (
            <TouchableOpacity
              key={index}
              style={{
                flex: 1,
                alignItems: "center",
                justifyContent: "center",
                padding: Spacing.S,

                borderColor: purpose === item.value ? SRXColor.Teal : "#e0e0e0",
                borderRightWidth: 1,
                borderLeftWidth: index === 0 || purpose === item.value ? 1 : 0,
                borderBottomWidth: 1,
                borderTopWidth: 1,

                //border radius,
                borderTopLeftRadius: index == 0 ? 5 : 0,
                borderBottomLeftRadius: index == 0 ? 5 : 0,
                borderTopRightRadius:
                  index + 1 == pt_Purpose_Array.length ? 5 : 0,

                borderBottomRightRadius:
                  index + 1 == pt_Purpose_Array.length ? 5 : 0
              }}
              onPress={() => this.onSelectPurpose(item)}
            >
              <SmallBodyText style={{ textAlign: "center" }}>
                {item.key}
              </SmallBodyText>
            </TouchableOpacity>
          );
        })}
      </View>
    );
  }
}

//styles for property update list item
const styles = StyleSheet.create({
  occupanyAndPurposeContainer: {
    flex: 1,
    flexDirection: "row",
    backgroundColor: SRXColor.White,

    borderRadius: 5,

    //added for shadow
    shadowColor: "rgb(110,129,154)",
    shadowOffset: { width: 1, height: 2 },
    shadowOpacity: 0.32,
    shadowRadius: 1
  }
});

MyPropertyPurpose.propTypes = {
  /**purpose value */
  purpose: PropTypes.number,
  /**select property each purpose */
  onSelectPurpose: PropTypes.func,

  style: PropTypes.oneOfType([PropTypes.object, PropTypes.array])
};

export { MyPropertyPurpose };
