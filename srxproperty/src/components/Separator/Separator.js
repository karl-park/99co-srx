import React, { Component } from "react";
import { View } from "react-native";
import PropTypes from "prop-types";

const EdgeInset = PropTypes.shape({
  top: PropTypes.number,
  bottom: PropTypes.number,
  left: PropTypes.number,
  right: PropTypes.number
});

class Separator extends Component {
  static propTypes = {
    /*
     * all props are optional
     */
    
    edgeInset: EdgeInset,
    separatorColor: PropTypes.string,
    backgroundColor: PropTypes.string
  };

  static defaultProps = {
    edgeInset: { top: 0, bottom: 0, left: 0, right: 0 },
    separatorColor: "#e0e0e0",
    backgroundColor: "white"
  };

  render() {
    const { edgeInset, separatorColor, backgroundColor } = this.props;

    return (
      <View style={{ backgroundColor: backgroundColor }}>
        <View
          style={{
            marginLeft: edgeInset.left,
            marginRight: edgeInset.right,
            marginTop: edgeInset.top,
            marginBottom: edgeInset.bottom,
            height: 1,
            backgroundColor: separatorColor
          }}
        />
      </View>
    );
  }
}

export { Separator };
