import React, { Component } from "react";
import { View, StyleSheet, Image, ScrollView } from "react-native";
import PropTypes from "prop-types";
import { Navigation } from "react-native-navigation";

import { Button, FeatherIcon, SmallBodyText } from "../../../components";
import { Spacing, Typography } from "../../../styles";
import { ObjectUtil, StringUtil } from "../../../utils";
import {
  All_PropertyType_Options,
  Concierge_FilterOptions
} from "../Constants";
import { SRXColor } from "../../../constants";
import {
  DistrictsOption,
  HDBEstatesOption
} from "../../PropertySearch/SearchOptions/Options";

class AgentFilterOptions extends Component {
  state = {
    //variable object
    selectedOptions: {
      selectedPropertyType: null,
      selectedAreaSpecializations: "",
      selectedDistrictIds: "",
      selectedHdbTownIds: ""
    },
    selectedAreaOrDistrictDescription: ""
  };

  //get selected areas or district count
  getAreaOrDistrictFilterCount() {
    const {
      selectedDistrictIds,
      selectedHdbTownIds
    } = this.state.selectedOptions;
    let filterCount = 0;

    if (!ObjectUtil.isEmpty(selectedDistrictIds)) {
      filterCount = selectedDistrictIds.split(",").length;
    } else if (!ObjectUtil.isEmpty(selectedHdbTownIds)) {
      filterCount = selectedHdbTownIds.split(",").length;
    }
    return filterCount;
  }

  //get Area or District Name
  getAreaOrDistrictName() {
    const { selectedPropertyType } = this.state.selectedOptions;
    if (!ObjectUtil.isEmpty(selectedPropertyType)) {
      if (selectedPropertyType.key === Concierge_FilterOptions.hdb) {
        return Concierge_FilterOptions.area;
      } else {
        return Concierge_FilterOptions.district;
      }
    }
  }

  //Clear Fitlers
  clearFilters = () => {
    const { onSearch } = this.props;
    const { selectedOptions } = this.state;

    if (onSearch) {
      //clear all filters
      let newSelectedOptions = {
        ...selectedOptions,
        selectedPropertyType: null,
        selectedAreaSpecializations: "",
        selectedDistrictIds: "",
        selectedHdbTownIds: ""
      };
      this.setState(
        {
          selectedOptions: newSelectedOptions,
          selectedAreaOrDistrictDescription: ""
        },
        () => {
          onSearch(null);
        }
      );
    }
  };

  //Search By Property Type
  filterByPropertyType = item => {
    const { onSearch } = this.props;
    const { selectedOptions } = this.state;

    if (onSearch) {
      let newSelectedOptions = {
        ...selectedOptions,
        selectedPropertyType: item,
        selectedAreaSpecializations: item.value
      };
      this.setState({ selectedOptions: newSelectedOptions }, () => {
        onSearch(newSelectedOptions);
      });
    }
  };

  //Search By HDB Estate Areas Selected
  onHDBEstatesSelected = item => {
    console.log("triggered - onHDBEstatesSelected");
    const { onSearch } = this.props;
    const { selectedOptions } = this.state;

    if (!ObjectUtil.isEmpty(item)) {
      let newSelectedOptions = {
        ...selectedOptions,
        selectedDistrictIds: ""
      };

      const { addressData } = item;
      var selectedAreasDescription = "";
      if (!ObjectUtil.isEmpty(addressData)) {
        newSelectedOptions = {
          ...newSelectedOptions,
          selectedHdbTownIds: addressData.selectedHdbTownIds
        };

        //add selected areas description
        selectedAreasDescription = addressData.displayText;
      }

      this.setState(
        {
          selectedOptions: newSelectedOptions,
          selectedAreaOrDistrictDescription: selectedAreasDescription
        },
        () => {
          if (onSearch) {
            onSearch(newSelectedOptions);
          }
        }
      );
    }
  };

  onDistrictsSelected = item => {
    console.log("triggered - onDistrictsSelected");
    const { onSearch } = this.props;
    const { selectedOptions } = this.state;
    if (!ObjectUtil.isEmpty(item)) {
      let newSelectedOptions = {
        ...selectedOptions,
        selectedHdbTownIds: ""
      };

      const { addressData } = item;
      var selectedDistrictsDescription = "";
      if (!ObjectUtil.isEmpty(addressData)) {
        newSelectedOptions = {
          ...newSelectedOptions,
          selectedDistrictIds: addressData.selectedDistrictIds
        };

        selectedDistrictsDescription = addressData.selectedDistrictIds;
      }

      this.setState(
        {
          selectedOptions: newSelectedOptions,
          selectedAreaOrDistrictDescription: StringUtil.replace(
            selectedDistrictsDescription,
            ",",
            ", "
          )
        },
        () => {
          if (onSearch) {
            onSearch(newSelectedOptions);
          }
        }
      );
    }
  };

  //Goes to Areas page selectedHdbTownIds
  onPressAreaOrDistrict = () => {
    const { selectedPropertyType } = this.state.selectedOptions;
    if (!ObjectUtil.isEmpty(selectedPropertyType)) {
      //if select hdb property type, direct to area options
      if (selectedPropertyType.key === Concierge_FilterOptions.hdb) {
        this.directToAreasOption();
      } else {
        this.directToDistrictsOption();
      }
    }
  };

  directToAreasOption() {
    const { selectedHdbTownIds } = this.state.selectedOptions;
    Navigation.push(this.props.componentId, {
      component: {
        name: "PropertySearchStack.HDBEstatesOption",
        passProps: {
          onLocationSelected: this.onHDBEstatesSelected.bind(this),
          source: HDBEstatesOption.Sources.AgentSearch,
          initialSelectedHDBTownIds: selectedHdbTownIds
        }
      }
    });
  }

  directToDistrictsOption() {
    const { selectedDistrictIds } = this.state.selectedOptions;
    Navigation.push(this.props.componentId, {
      component: {
        name: "PropertySearchStack.DistrictsOption",
        passProps: {
          onLocationSelected: this.onDistrictsSelected.bind(this),
          source: DistrictsOption.Sources.AgentSearch,
          initialSelectedDistrictIds: selectedDistrictIds
        }
      }
    });
  }

  renderFilterByType() {
    const { selectedPropertyType } = this.state.selectedOptions;
    if (ObjectUtil.isEmpty(selectedPropertyType)) {
      return (
        <View style={{ flexDirection: "row" }}>
          {All_PropertyType_Options.map((item, index) =>
            this.renderOption(item, index)
          )}
        </View>
      );
    }
  }

  renderOption(item, index) {
    if (!ObjectUtil.isEmpty(item)) {
      return (
        <Button
          buttonStyle={{ marginRight: Spacing.M, alignItems: "center" }}
          textStyle={[Typography.SmallBody, { lineHeight: 22 }]}
          leftView={
            <Image
              source={item.icon}
              resizeMode={"contain"}
              style={{ width: 21, height: 24 }}
            />
          }
          rightView={
            <SmallBodyText style={{ marginLeft: 2 }}>{item.key}</SmallBodyText>
          }
          onPress={() => this.filterByPropertyType(item)}
        />
      );
    }
  }

  renderFilterByAreaOrDistrict() {
    const { selectedPropertyType } = this.state.selectedOptions;
    if (!ObjectUtil.isEmpty(selectedPropertyType)) {
      return (
        <View style={{ flexDirection: "row" }}>
          <Button
            buttonStyle={styles.clearContainer}
            textStyle={[
              Typography.SmallBody,
              { lineHeight: 22, color: SRXColor.Teal }
            ]}
            onPress={() => this.clearFilters()}
          >
            Clear
          </Button>

          <Button
            buttonStyle={{ marginRight: Spacing.S, alignItems: "center" }}
            textStyle={[Typography.SmallBody, { lineHeight: 22 }]}
            leftView={
              <Image
                source={selectedPropertyType.icon}
                resizeMode={"contain"}
                style={{ width: 21, height: 24 }}
              />
            }
            rightView={
              <SmallBodyText style={{ marginLeft: 2 }}>
                {selectedPropertyType.key}
              </SmallBodyText>
            }
          />

          {this.renderSelectedAreaOrDistrictOption()}
        </View>
      );
    }
  }

  renderSelectedAreaOrDistrictOption() {
    let filterCount = this.getAreaOrDistrictFilterCount();
    let optionName = this.getAreaOrDistrictName();

    return (
      <Button
        buttonStyle={
          filterCount > 0
            ? styles.selectedOptionContaienr
            : styles.optionContainer
        }
        textStyle={[
          Typography.SmallBody,
          {
            lineHeight: 20,
            marginRight: Spacing.XS / 2,
            color: filterCount > 0 ? SRXColor.White : SRXColor.Black
          }
        ]}
        rightView={
          <View style={{ flexDirection: "row" }}>
            {/* {this.renderAreaCount()} */}
            {this.renderSelectedAreaOrDistrict()}
            <FeatherIcon
              name={"chevron-down"}
              size={20}
              color={filterCount > 0 ? SRXColor.White : SRXColor.Black}
            />
          </View>
        }
        onPress={() => this.onPressAreaOrDistrict()}
      >
        {optionName}
      </Button>
    );
  }

  renderSelectedAreaOrDistrict() {
    const { selectedAreaOrDistrictDescription } = this.state;
    if (!ObjectUtil.isEmpty(selectedAreaOrDistrictDescription)) {
      return (
        <View style={{ maxWidth: 90 }}>
          <SmallBodyText
            style={{
              overflow: "hidden",
              color: SRXColor.White
            }}
            numberOfLines={1}
          >
            {" - "}
            {selectedAreaOrDistrictDescription}
          </SmallBodyText>
        </View>
      );
    }
  }

  renderAreaCount() {
    let filterCount = this.getAreaOrDistrictFilterCount();
    if (filterCount > 0) {
      return (
        <View style={styles.viewCircleContainer}>
          <SmallBodyText style={{ color: SRXColor.Red }}>
            {filterCount}
          </SmallBodyText>
        </View>
      );
    }
  }

  render() {
    return (
      <View style={styles.container}>
        <ScrollView horizontal={true} showsHorizontalScrollIndicator={false}>
          <View style={styles.mainContainer}>
            {this.renderFilterByType()}
            {this.renderFilterByAreaOrDistrict()}
          </View>
        </ScrollView>
      </View>
    );
  }
}

AgentFilterOptions.propTypes = {
  onSearch: PropTypes.func
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: SRXColor.White,
    paddingVertical: Spacing.S,
    //border bottom radius
    borderBottomLeftRadius: 10,
    borderBottomRightRadius: 10
  },
  mainContainer: {
    flexDirection: "row",
    paddingHorizontal: Spacing.M,
    alignItems: "center"
  },
  clearContainer: {
    paddingVertical: Spacing.XS,
    marginRight: Spacing.S
  },
  optionContainer: {
    paddingHorizontal: Spacing.S,
    paddingVertical: Spacing.XS,
    marginRight: Spacing.M,
    borderWidth: 1,
    borderColor: "#e0e0e0",
    backgroundColor: SRXColor.White
  },
  selectedOptionContaienr: {
    paddingHorizontal: Spacing.S,
    paddingVertical: Spacing.XS,
    marginRight: Spacing.M,
    backgroundColor: SRXColor.Teal
  },
  viewCircleContainer: {
    width: 20,
    height: 20,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: "#e0e0e0",
    backgroundColor: SRXColor.White,
    alignItems: "center",
    justifyContent: "center"
  }
});

export { AgentFilterOptions };
