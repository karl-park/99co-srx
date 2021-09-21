import React, { Component } from "react";
import { View } from "react-native";
import PropTypes from "prop-types";
import { Separator, BodyText } from "../../../components";

import { MyPropertyOccupancyAndPurpose } from "../../MyProperties";
import { ObjectUtil } from "../../../utils";
import { Spacing } from "../../../styles";
import { pt_Occupancy } from "../../../constants";

class MyPropertyUpdateListItem extends Component {
  constructor(props) {
    super(props);

    this.onSelectOccupancyAndPurpose = this.onSelectOccupancyAndPurpose.bind(
      this
    );
  }

  //start rendering methods
  renderPropertyAddress() {
    const { srxPropertyUserPO } = this.props;
    return <BodyText>{srxPropertyUserPO.getFullAddress()}</BodyText>;
  }

  onSelectOccupancyAndPurpose({ occupancy, purpose }) {
    const { srxPropertyUserPO, onUpdateSRXPropertyPO, error } = this.props;
    var newSRXPropertyUserPO = {
      ...srxPropertyUserPO,
      occupancy,
      purpose
    };
    var newError = {
      ...error,
      occupancyError: !occupancy ? "Required field" : null,
      purposeError:
        !purpose && occupancy === pt_Occupancy.Own ? "Required field" : null
    };
    if (onUpdateSRXPropertyPO) {
      onUpdateSRXPropertyPO(newSRXPropertyUserPO, newError);
    }
  }

  renderOccupancyAndPurpose() {
    const { srxPropertyUserPO, error } = this.props;
    return (
      <View style={{ marginTop: Spacing.M }}>
        <MyPropertyOccupancyAndPurpose
          initialOccupancy={srxPropertyUserPO.occupancy}
          initialPurpose={srxPropertyUserPO.purpose}
          error={error}
          onValueUpdated={this.onSelectOccupancyAndPurpose}
        />
      </View>
    );
  }

  render() {
    const { containerStyle, srxPropertyUserPO } = this.props;
    if (!ObjectUtil.isEmpty(srxPropertyUserPO)) {
      return (
        <View style={containerStyle}>
          <View style={{ padding: Spacing.M }}>
            {this.renderPropertyAddress()}
            {this.renderOccupancyAndPurpose()}
          </View>
          <Separator />
        </View>
      );
    }
    //for else case
    return <View />;
  }
}

MyPropertyUpdateListItem.propTypes = {
  error: PropTypes.object,
  srxPropertyUserPO: PropTypes.object,
  /** method used when update occupancy or purpose in srxproperty user po */
  onUpdateSRXPropertyPO: PropTypes.func
};

export { MyPropertyUpdateListItem };
