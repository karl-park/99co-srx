import React, { Component } from "react";
import { View } from "react-native";
import PropTypes from "prop-types";
import { SRXColor } from "../../../../constants";

class QualityBar extends Component {
  static defaultProps = {
    quality: 0
  };

  renderEachSubBar(index) {
    const { quality } = this.props;

    let percentagePerBar = 0;
    if (quality >= index * 20) {
      percentagePerBar = 100;
    } else if (quality < (index - 1) * 20) {
      percentagePerBar = 0;
    } else {
      const remaining = quality % 20;
      percentagePerBar = (remaining / 20) * 100;
    }
    const width = percentagePerBar + "%";
    return (
      <View style={BarStyles.subBar}>
        <View
          style={{
            height: 20,
            width: width,
            backgroundColor: SRXColor.Teal
          }}
        />
      </View>
    );
  }

  renderInterval() {
    return <View style={{ width: 2 }} />;
  }

  render() {
    return (
      <View style={{ flexDirection: "row", width: 48 }}>
        {this.renderEachSubBar(1)}
        {this.renderInterval()}
        {this.renderEachSubBar(2)}
        {this.renderInterval()}
        {this.renderEachSubBar(3)}
        {this.renderInterval()}
        {this.renderEachSubBar(4)}
        {this.renderInterval()}
        {this.renderEachSubBar(5)}
      </View>
    );
  }
}

QualityBar.propTypes = {
  quality: PropTypes.oneOfType([PropTypes.number, PropTypes.string]) //0-100
};

BarStyles = {
  subBar: {
    width: 8,
    height: 20,
    borderRadius: 2,
    overflow: "hidden",
    backgroundColor: SRXColor.LightGray
  }
};

export { QualityBar };
