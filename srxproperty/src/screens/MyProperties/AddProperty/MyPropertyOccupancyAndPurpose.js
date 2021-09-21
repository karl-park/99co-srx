import React, { Component } from "react";
import { View, StyleSheet, TouchableOpacity } from "react-native";
import PropTypes from "prop-types";
import { SRXColor, pt_Occupancy } from "../../../constants";
import { Spacing } from "../../../styles";
import { ObjectUtil } from "../../../utils";

import { MyPropertyOccupancy, MyPropertyPurpose } from "../components";

class MyPropertyOccupancyAndPurpose extends Component {
  state = {
    occupancy: null,
    purpose: null
  };

  constructor(props) {
    super(props);

    this.onSelectOccupancy = this.onSelectOccupancy.bind(this);
    this.onSelectPurpose = this.onSelectPurpose.bind(this);
  }

  componentDidMount() {
    const { initialOccupancy, initialPurpose } = this.props;
    this.setState({
      occupancy: initialOccupancy,
      purpose: initialOccupancy === pt_Occupancy.Own ? initialPurpose : null
    });
  }

  onSelectOccupancy = item => {
    const { onValueUpdated } = this.props;
    const { purpose } = this.state;

    const newOccupancy = item.value;
    let newPurpose = purpose;

    if (newOccupancy != pt_Occupancy.Own) {
      newPurpose = null;
    }

    this.setState(
      {
        occupancy: newOccupancy,
        purpose: newPurpose
      },
      () => {
        if (onValueUpdated) {
          onValueUpdated({
            occupancy: newOccupancy,
            purpose: newPurpose
          });
        }
      }
    );
  };

  onSelectPurpose = item => {
    const { onValueUpdated } = this.props;
    const { occupancy } = this.state;

    const newPurpose = item.value;

    this.setState(
      {
        purpose: newPurpose
      },
      () => {
        if (onValueUpdated) {
          onValueUpdated({
            occupancy: occupancy,
            purpose: newPurpose
          });
        }
      }
    );
  };

  renderOccupancy() {
    const { occupancy } = this.state;
    const { error } = this.props;
    let hasError = false;
    if (
      !ObjectUtil.isEmpty(error) &&
      !ObjectUtil.isEmpty(error.occupancyError)
    ) {
      hasError = true;
    }
    return (
      <MyPropertyOccupancy
        style={[
          hasError ? { borderWidth: 1, borderColor: SRXColor.Red } : null
        ]}
        occupancy={occupancy}
        onSelectOccupancy={this.onSelectOccupancy}
      />
    );
  }

  renderPurpose() {
    const { occupancy, purpose } = this.state;
    const { error } = this.props;
    if (occupancy === pt_Occupancy.Own) {
      let hasError = false;
      if (
        !ObjectUtil.isEmpty(error) &&
        !ObjectUtil.isEmpty(error.purposeError)
      ) {
        hasError = true;
      }
      return (
        <MyPropertyPurpose
          style={[
            { marginTop: Spacing.M },
            hasError ? { borderWidth: 1, borderColor: SRXColor.Red } : null
          ]}
          purpose={purpose}
          onSelectPurpose={this.onSelectPurpose}
        />
      );
    }
  }

  render() {
    const { style } = this.props;
    return (
      <View style={style}>
        {this.renderOccupancy()}
        {this.renderPurpose()}
      </View>
    );
  }
}

MyPropertyOccupancyAndPurpose.propTypes = {
  initialOccupancy: PropTypes.number,
  initialPurpose: PropTypes.number,
  /**
   * {
   *   occupancyError,
   *   purposeError
   * }
   */
  error: PropTypes.object,
  /**
   * when occupancy or purpose updated
   * return
   * {
   *   occupancy: value,
   *   purpose: value
   * }
   */
  onValueUpdated: PropTypes.func,

  style: PropTypes.oneOfType([PropTypes.object, PropTypes.array])
};

export { MyPropertyOccupancyAndPurpose };
