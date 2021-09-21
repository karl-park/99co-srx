import React, { Component } from "react";
import { View, ScrollView } from "react-native";
import { Navigation } from "react-native-navigation";
import SafeAreaView from "react-native-safe-area-view";

import { ObjectUtil, UserUtil } from "../../../../utils";
import { SRXColor, HDBEstates } from "../../../../constants";
import { SearchOptionList } from "../OptionsComponent";
import { LocationSearchOptions } from "../../Location";

const HDBEstatesSource = {
  ListingSearch: "ListingSearch",
  AgentSearch: "AgentSearch"
};

class HDBEstatesOption extends Component {
  static options(passProps) {
    //Right button text by source
    let rightButtonText = "Search";
    if (!ObjectUtil.isEmpty(passProps)) {
      rightButtonText =
        passProps.source === HDBEstatesSource.AgentSearch ? "Done" : "Search";
    }

    return {
      topBar: {
        title: {
          text: "Search by Area",
          // alignment: "center"
        },
        rightButtons: [
          {
            id: "btn_search",
            text: rightButtonText,
          }
        ]
      }
    };
  }

  constructor(props) {
    super(props);

    Navigation.events().bindComponent(this);

    this.onEachHdbSelected = this.onEachHdbSelected.bind(this);
  }

  state = {
    selectedHdbArray: []
  };

  componentDidMount() {
    const { source, searchOptions } = this.props;
    if (source === HDBEstatesSource.AgentSearch) {
      //from agent search directory
      const { initialSelectedHDBTownIds } = this.props;
      if (!ObjectUtil.isEmpty(initialSelectedHDBTownIds)) {
        this.setState({
          selectedHdbArray: initialSelectedHDBTownIds.split(",")
        });
      }
    } else {
      //from filters for listing search
      if (!ObjectUtil.isEmpty(searchOptions)) {
        if (searchOptions.selectedHdbTownIds) {
          this.setState({
            selectedHdbArray: searchOptions.selectedHdbTownIds.split(",")
          });
        }
      }
    } //end of list
  }

  navigationButtonPressed({ buttonId }) {
    if (buttonId === "btn_search") {
      this.searchListingResultByHDBEstate();
    }
  }

  searchListingResultByHDBEstate() {
    const { selectedHdbArray } = this.state;
    const { onLocationSelected, source, onHDBTownSelected } = this.props;

    let recentSearchText = "";
    selectedHdbArray.forEach(value => {
      recentSearchText =
        recentSearchText + HDBEstates.getHDBEstateDescription(value) + ", ";
    });

    const returningItem = {
      displayText: recentSearchText.substring(0, recentSearchText.length - 2),
      searchText: null,
      selectedAmenitiesIds: null,
      selectedDistrictIds: null,
      selectedHdbTownIds: Array.from(selectedHdbArray).toString(),
      locationType: LocationSearchOptions.hdb_estate
    };

    if (onLocationSelected) {
      onLocationSelected({addressData:returningItem, updateRecentSearches: true});
    }

    if (source === HDBEstatesSource.AgentSearch) {
      Navigation.pop(this.props.componentId);
    }
  }

  onEachHdbSelected = item => {
    const { selectedHdbArray } = this.state;
    if (!ObjectUtil.isEmpty(item)) {
      if (selectedHdbArray.includes(item.value)) {
        selectedHdbArray.splice(selectedHdbArray.indexOf(item.value), 1);
      } else {
        selectedHdbArray.push(item.value);
      }
      this.setState({ selectedHdbArray });
    }
  };

  //rendering methods for presentation
  renderHDBEstatesLists() {
    const { selectedHdbArray } = this.state;
    return (
      <View style={{ paddingBottom: 5 }}>
        <SearchOptionList
          dataArray={HDBEstates.HDB_TOWN_ARRAY}
          selectedArray={selectedHdbArray}
          onItemSelected={this.onEachHdbSelected}
          isSchoolOption={false}
        />
      </View>
    );
  }

  render() {
    return (
      <SafeAreaView
        style={{ flex: 1, backgroundColor: SRXColor.White }}
        forceInset={{top:"never", bottom: "never" }}
      >
        {/* <SafeAreaView
          style={{ flex: 1, backgroundColor: "white" }}
          forceInset={{ top: "never" }}
        > */}
        
          {this.renderHDBEstatesLists()}
        {/* </SafeAreaView> */}
      </SafeAreaView>
    );
  }
}

HDBEstatesOption.Sources = HDBEstatesSource;

export { HDBEstatesOption };
