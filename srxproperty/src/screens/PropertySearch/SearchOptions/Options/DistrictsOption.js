import React, { Component } from "react";
import { View, ScrollView, SectionList } from "react-native";
import { Navigation } from "react-native-navigation";
import SafeAreaView from "react-native-safe-area-view";
import {AppTopBar_BackBtn} from "../../../../assets";
import {AppTheme} from "../../../../styles";

import { ObjectUtil, UserUtil, SetUtil } from "../../../../utils";
import { BodyText } from "../../../../components";
import { SRXColor, District } from "../../../../constants";
import { Styles } from "../Styles";
import { All_Districts } from "../../Constants";
import { GroupedSearchOptionList } from "../OptionsComponent";
import { Spacing } from "../../../../styles";
import { LocationSearchOptions } from "../../Location"

const DistrictsSource = {
  ListingSearch: "ListingSearch",
  AgentSearch: "AgentSearch"
};

class DistrictsOption extends Component {
  static options(passProps) {
    //Right button text by screen
    let rightButtonText = "Search";
    if (!ObjectUtil.isEmpty(passProps)) {
      rightButtonText =
        passProps.source === DistrictsSource.AgentSearch ? "Done" : "Search";
    }

    return {
      topBar: {
        backButton: {
          icon: AppTopBar_BackBtn,
          color: AppTheme.topBarBackButtonColor,
          title: ""
        },
        title: {
          text: "Search by Districts",
          // alignment: "center"
        },
        rightButtons: [
          {
            id: "btn_district_search",
            text: rightButtonText,
          }
        ]
      }
    };
  }

  state = {
    selectedDistrict: new Set(),
    activeSections: [
      District.Districts.CCR,
      District.Districts.OCR,
      District.Districts.RCR
    ]
  };

  constructor(props) {
    super(props);
    Navigation.events().bindComponent(this);

    this.renderHeader = this.renderHeader.bind(this);
    this.renderContent = this.renderContent.bind(this);
  }

  componentDidMount() {
    //Agent Search
    const { initialSelectedDistrictIds } = this.props;
    if (initialSelectedDistrictIds) {
      this.setState({
        selectedDistrict: new Set(initialSelectedDistrictIds.split(","))
      });
    }
  }

  navigationButtonPressed({ buttonId }) {
    if (buttonId === "btn_district_search") {
      //to pass to API for searching
      this.searchListingResultByDistrict();
    }
  }

  // isActiveSection = item => {
  //   const { activeSections } = this.state;
  //   let isActive = false;
  //   if (!ObjectUtil.isEmpty(item)) {
  //     if (activeSections.includes(item.code)) {
  //       isActive = true;
  //     }
  //   }
  //   return isActive;
  // };

  onPressSectionHeader = section => {
    const { activeSections } = this.state;
    if (!ObjectUtil.isEmpty(section)) {
      if (activeSections.includes(section.code)) {
        activeSections.splice(activeSections.indexOf(section.code), 1);
      } else {
        activeSections.push(section.code);
      }
      this.setState({ activeSections });
    }
  };

  onSelectAllGroupedDistrict = section => {
    const { selectedDistrict } = this.state;
    if (SetUtil.isSuperset(selectedDistrict, section.value)) {
      //UnCheck all
      this.setState({
        selectedDistrict: SetUtil.difference(selectedDistrict, section.value)
      });
    } else {
      //Check all
      this.setState({
        selectedDistrict: SetUtil.union(selectedDistrict, section.value)
      });
    }
  };

  onSelectEachDistrict = (index, item, section) => {
    const { selectedDistrict } = this.state;
    if (!ObjectUtil.isEmpty(item)) {
      if (selectedDistrict.has(item.value)) {
        selectedDistrict.delete(item.value);
      } else {
        selectedDistrict.add(item.value);
      }
      this.setState({ selectedDistrict });
    }
  };

  // onPressSectionHeader = section => {
  //   const { activeSections } = this.state;
  //   if (!ObjectUtil.isEmpty(section)) {
  //     if (activeSections.includes(section.code)) {
  //       activeSections.splice(activeSections.indexOf(section.code), 1);
  //     } else {
  //       activeSections.push(section.code);
  //     }
  //     this.setState({ activeSections });
  //   }
  // };

  searchListingResultByDistrict() {
    const { selectedDistrict } = this.state;
    const { onLocationSelected, source } = this.props;

    //For Recent Searches
    var recentSearchText = "";
    selectedDistrict.forEach(value => {
      recentSearchText =
        recentSearchText +
        District.getDistrictDescription(value).substr(0, 3) +
        ", ";
    });

    const returningItem = {
      displayText: recentSearchText.substring(0, recentSearchText.length - 2),
      searchText: null,
      selectedAmenitiesIds: null,
      selectedDistrictIds: Array.from(selectedDistrict).toString(),
      selectedHdbTownIds: null,
      locationType: LocationSearchOptions.district
    };

    if (onLocationSelected) {
      onLocationSelected({addressData:returningItem, updateRecentSearches: true});
    }

    if (source === DistrictsSource.AgentSearch) {
      Navigation.pop(this.props.componentId);
    }
  }

  //Show District Line as headers
  renderHeader = ({ section }) => {
    return (
      // <TouchableHighlight onPress={() => this.onPressSectionHeader(section)}>
      <View
        style={[
          Styles.itemContainerStyle,
          {
            backgroundColor:
              section.index % 2 == 0
                ? SRXColor.White
                : SRXColor.AccordionBackground
          }
        ]}
      >
        <View style={{ flex: 1 }}>
          <BodyText style={{ marginVertical: Spacing.XS, fontWeight: "600" }}>
            {section.key}
          </BodyText>
        </View>
        {/* <FeatherIcon
            name={this.isActiveSection(section) ? "chevron-up" : "chevron-down"}
            size={20}
            color={"black"}
          /> */}
      </View>
      // </TouchableHighlight>
    );
  };

  renderContent = ({ index, item, section }) => {
    const { selectedDistrict, activeSections } = this.state;
    if (!ObjectUtil.isEmpty(section)) {
      if (activeSections.includes(section.code)) {
        if (!ObjectUtil.isEmpty(section.data)) {
          return (
            <View
              style={{
                backgroundColor:
                  section.index % 2 == 0
                    ? SRXColor.White
                    : SRXColor.AccordionBackground
              }}
              index={index}
            >
              <GroupedSearchOptionList
                dataArray={section.data}
                selectedArray={selectedDistrict}
                onHeaderSelected={this.onSelectAllGroupedDistrict}
                onItemSelected={this.onSelectEachDistrict}
              />
            </View>
          );
        }
      }
    }
  };

  renderDistrictList() {
    return (
      <SectionList
        sections={All_Districts}
        stickySectionHeadersEnabled={false}
        renderItem={({ index, item, section }) =>
          this.renderContent({ index, item, section })
        }
        renderSectionHeader={({ section }) => this.renderHeader({ section })}
        SectionSeparatorComponent={() => {
          return <View />;
        }}
        keyExtractor={(item, index) => item + index}
      />
    );
  }

  render() {
    return (
      <SafeAreaView
        style={{ flex: 1, backgroundColor: SRXColor.White }}
        forceInset={{ bottom: "never", top:"never"}}
      >
          <ScrollView style={{ flex: 1 }}>
            {this.renderDistrictList()}
          </ScrollView>
      </SafeAreaView>
    );
  }
}

DistrictsOption.Sources = DistrictsSource;

export { DistrictsOption };
