import React, { Component } from "react";
import { View } from "react-native";
import PropTypes from "prop-types";

import { Styles } from "../Styles";
import { BodyText, Button, FeatherIcon } from "../../../../components";
import { CheckboxStyles } from "../../../../styles";

class SingleCheckboxFilterOption extends Component {
  static propTypes = {
    /*
     * prop types are optional
     */
    containerStyle: PropTypes.oneOf([PropTypes.object, PropTypes.array]),
    name: PropTypes.string,
    value: PropTypes.bool,
    onToggleOption: PropTypes.func
  };

  static defaultProps = {
    name: "",
    value: false
  };

  //on select each room
  onToggleOption = () => {
    const { onToggleOption } = this.props;
    if (onToggleOption) {
      onToggleOption();
    }
  };

  render() {
    const { containerStyle, name, value } = this.props;
    return (
      <View style={containerStyle}>
        <Button
          leftView={
            <BodyText style={[Styles.bodyTextExtraStyle, { flex: 1 }]}>
              {name}
            </BodyText>
          }
          rightView={
            value ? (
              <View style={CheckboxStyles.checkStyle}>
                <FeatherIcon name={"check"} size={15} color={"white"} />
              </View>
            ) : (
              <View style={CheckboxStyles.unCheckStyle} />
            )
          }
          onPress={() => this.onToggleOption()}
        />
      </View>
    );
  }
}

export { SingleCheckboxFilterOption };
