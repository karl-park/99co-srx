import React, { Component } from "react";
import { TouchableHighlight as Highlight, Platform } from "react-native";
import { SRXColor } from "../../constants";

const isIOS = Platform.OS === "ios";

class TouchableHighlight extends Component {
  render() {
    const { children, ...rest } = this.props;
    return (
      <Highlight
        underlayColor={isIOS?"black":SRXColor.AccordionBackground} 
        {...rest}
      >
        {children}
      </Highlight>
    );
  }
}

export { TouchableHighlight };
