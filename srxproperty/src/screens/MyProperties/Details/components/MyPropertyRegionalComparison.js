import React, { Component } from "react";
import { View } from "react-native";
import PropTypes from "prop-types";
import { BodyText, Subtext, Heading2 } from "../../../../components";
import { SRXColor } from "../../../../constants";
import { SRXPropertyUserPO } from "../../../../dataObject";
import { ObjectUtil, StringUtil } from "../../../../utils";
import { Spacing } from "../../../../styles";
import { PropertyTrackerUtil } from "../utils";

const RegionalComparisonType = {
  psf: "psf",
  rent: "rent"
};

class MyPropertyRegionalComparison extends Component {
  state = {
    typeOfComparison: RegionalComparisonType.psf,
    comparisonPO: null
  };

  componentDidMount() {
    this.getRequiredInfoFromTrackerPO();
  }

  componentDidUpdate(prevProps, prevState) {
    if (prevProps.trackerPO !== this.props.trackerPO) {
      this.getRequiredInfoFromTrackerPO();
    }
  }

  getRequiredInfoFromTrackerPO() {
    const { trackerPO } = this.props;
    let stateToUpdate = { typeOfComparison: RegionalComparisonType.psf };
    if (!ObjectUtil.isEmpty(trackerPO)) {
      if (!PropertyTrackerUtil.showSaleInformation(trackerPO)) {
        stateToUpdate = {
          typeOfComparison: RegionalComparisonType.rent
        };
      }
      stateToUpdate = {
        ...stateToUpdate,
        comparisonPO: trackerPO.getRegionalComparison()
      };
    }

    this.setState(stateToUpdate);
  }

  getCompareTypeTitle(type) {
    if (type === RegionalComparisonType.rent) {
      return "(Rental)";
    } else if (type === RegionalComparisonType.psf) {
      return "(PSF)";
    }
    return "";
  }

  renderMyPropertyValue() {
    const { comparisonPO, typeOfComparison } = this.state;
    let value = "-";
    if (!ObjectUtil.isEmpty(comparisonPO)) {
      if (typeOfComparison === RegionalComparisonType.rent) {
        value = StringUtil.formatCurrency(comparisonPO.rent, 2);
      } else if (typeOfComparison === RegionalComparisonType.psf) {
        value = StringUtil.formatCurrency(comparisonPO.psfSale, 2);
      }
    }
    return (
      <View style={{ flexDirection: "row", paddingVertical: Spacing.M }}>
        <View style={{ flex: 1 }}>
          <BodyText>
            Your Property{" "}
            <BodyText style={{ color: SRXColor.Gray }}>
              {this.getCompareTypeTitle(typeOfComparison)}
            </BodyText>
          </BodyText>
        </View>
        {this.renderSpaceInBetweenTitleAndValue()}
        <BodyText style={[{ flex: 1 }, styles.valueStyle]}>{value}</BodyText>
      </View>
    );
  }

  renderOtherPropertiesValue() {
    const { comparisonPO, typeOfComparison } = this.state;
    let minValue = "-";
    let maxValue = "-";
    if (!ObjectUtil.isEmpty(comparisonPO)) {
      if (typeOfComparison === RegionalComparisonType.rent) {
        minValue = StringUtil.formatCurrency(comparisonPO.minRent, 2);
        maxValue = StringUtil.formatCurrency(comparisonPO.maxRent, 2);
      } else if (typeOfComparison === RegionalComparisonType.psf) {
        minValue = StringUtil.formatCurrency(comparisonPO.psfMinSale, 2);
        maxValue = StringUtil.formatCurrency(comparisonPO.psfMaxSale, 2);
      }
    }
    return (
      <View style={{ flexDirection: "row", paddingVertical: Spacing.M }}>
        <View style={{ flex: 1 }}>
          <BodyText>
            Others{" "}
            <BodyText style={{ color: SRXColor.Gray }}>
              {this.getCompareTypeTitle(typeOfComparison)}
            </BodyText>
          </BodyText>
        </View>
        {this.renderSpaceInBetweenTitleAndValue()}
        <View
          style={{
            flex: 1,
            flexDirection: "row",
            flexWrap: "wrap"
          }}
        >
          <View style={{ marginRight: Spacing.M }}>
            <BodyText style={styles.valueStyle}>{minValue}</BodyText>
            <Subtext>Lowest</Subtext>
          </View>
          <View>
            <BodyText style={styles.valueStyle}>{maxValue}</BodyText>
            <Subtext>Highest</Subtext>
          </View>
        </View>
      </View>
    );
  }

  renderTitle() {
    const { comparisonPO } = this.state;
    return (
      <Heading2 style={{ marginBottom: Spacing.M }}>
        {comparisonPO ? comparisonPO.title : ""}
      </Heading2>
    );
  }

  renderSpaceInBetweenTitleAndValue() {
    return <View style={{ width: Spacing.S }} />;
  }

  render() {
    return (
      <View style={{ padding: Spacing.M }}>
        {this.renderTitle()}
        {this.renderMyPropertyValue()}
        {this.renderOtherPropertiesValue()}
      </View>
    );
  }
}

MyPropertyRegionalComparison.propTypes = {
  trackerPO: PropTypes.instanceOf(SRXPropertyUserPO)
};

const styles = {
  valueStyle: {
    color: SRXColor.Currency
  }
};

export { MyPropertyRegionalComparison };
