import React, { Component } from "react";
import { StyleSheet, View, ScrollView, Image } from "react-native";
import PropTypes from "prop-types";

import {
  ObjectUtil,
  SetUtil,
  StringUtil,
  CollectionConversionUtil
} from "../../../../utils";
import { Button, SmallBodyText, Separator } from "../../../../components";
import { SRXColor, FilterOptions } from "../../../../constants";
import { SearchPropertyService } from "../../../../services";
import {
  PropertyTypeValueSet,
  Residential_Options,
  Commercial_Options,
  CommercialType_Options,
  BuildYear_Set,
  Tenure_Set,
  ModelNotRequired_Set,
  CondoBedRoom_Set,
  PropertyType_Set,
  LeaseTerm_Set
} from "../../Constants";
import { Spacing, Typography } from "../../../../styles";

const SmartFilterSource = {
  advancedSearch: "AdvancedSearch",
  smartFilterSearch: "SmartFilterSearch"
};

class SmartFilter extends Component {
  constructor(props) {
    super(props);
    this.state = {
      filterOptionArray: [],
      selectedOptionArray: [],

      isAPIsCallCompleted: false,
      isFiltersShowed: true,
      counter: 0
    };
  }

  componentDidMount() {
    const { searchOptions } = this.props;

    if (!ObjectUtil.isEmpty(searchOptions)) {
      let cdResearchSubTypesSet = CollectionConversionUtil.convertValueStringToSet(
        searchOptions.cdResearchSubTypes
      );

      let filterOptionArray = [];
      let selectedPropertyType = null;
      if (
        SetUtil.isSuperset(
          PropertyTypeValueSet.commercial,
          cdResearchSubTypesSet
        )
      ) {
        filterOptionArray = CommercialType_Options;
      } else {
        filterOptionArray = Residential_Options;
      }

      //If select, condo, hdb or landed or commerical property type
      if (Array.isArray(filterOptionArray) && filterOptionArray.length > 0) {
        for (i = 0; i < filterOptionArray.length; i++) {
          if (!ObjectUtil.isEmpty(filterOptionArray[i])) {
            //Do checking for property type only
            // if (PropertyType_Set.has(filterOptionArray[i].key)) {
            if (
              searchOptions.cdResearchSubTypes ===
              filterOptionArray[i].value.toString()
            ) {
              selectedPropertyType = filterOptionArray[i];
            }
            // }
          }
        }
      }

      this.setState({ filterOptionArray }, () => {
        if (!ObjectUtil.isEmpty(selectedPropertyType)) {
          this.onPressCount(selectedPropertyType, false, searchOptions);
        } else {
          this.onConcurrentAPICalled(searchOptions);
        }
      });
    }
  }

  componentDidUpdate(prevProps) {
    if (prevProps.searchOptions !== this.props.searchOptions) {
      const { searchSource } = this.props.searchOptions;
      //Hide SmartFilter part if advanced filter is used and values are changed
      if (searchSource !== SmartFilterSource.advancedSearch) {
        this.handleSearchOptions(
          prevProps.searchOptions,
          this.props.searchOptions
        );
      } else {
        //if comes from advanced filter, hide smart filter
        this.setState({ isAPIsCallCompleted: false });
        return;
      }
    }
  }

  handleSearchOptions(prevSearchOptions, newSearchOptions) {
    const { selectedOptionArray } = this.state;

    if (
      !ObjectUtil.isEmpty(prevSearchOptions) &&
      !ObjectUtil.isEmpty(newSearchOptions)
    ) {
      //get CdResearchSubType and change to Set value
      let cdResearchSubTypesSet = CollectionConversionUtil.convertValueStringToSet(
        newSearchOptions.cdResearchSubTypes
      );

      //if sub type multi select, hide the smart filter
      this.isPropertyTypeMultiSelected(cdResearchSubTypesSet);

      //Check if there is proeprty type changes or not with prev and new search options
      if (
        prevSearchOptions.cdResearchSubTypes !==
        newSearchOptions.cdResearchSubTypes
      ) {
        if (
          PropertyTypeValueSet.allResidential.size ===
            cdResearchSubTypesSet.size &&
          SetUtil.isSuperset(
            PropertyTypeValueSet.allResidential,
            cdResearchSubTypesSet
          )
        ) {
          selectedOptionArray.length = 0;
          this.setState(
            { filterOptionArray: Residential_Options, isFiltersShowed: true },
            () => {
              this.onConcurrentAPICalled(newSearchOptions);
            }
          );
          return;
        } else {
          //selected other property type hdb, condo, landed, commercial
          let propertyTypeObject = null;
          if (
            SetUtil.isSuperset(PropertyTypeValueSet.hdb, cdResearchSubTypesSet)
          ) {
            //HDB
            propertyTypeObject = Residential_Options.find(
              item => item.key === FilterOptions.FilterOptionDescription.hdb
            );
          } else if (
            SetUtil.isSuperset(
              PropertyTypeValueSet.landed,
              cdResearchSubTypesSet
            )
          ) {
            //Landed
            propertyTypeObject = Residential_Options.find(
              item => item.key === FilterOptions.FilterOptionDescription.landed
            );
          } else if (
            SetUtil.isSuperset(
              PropertyTypeValueSet.condo,
              cdResearchSubTypesSet
            )
          ) {
            //Condo
            propertyTypeObject = Residential_Options.find(
              item => item.key === FilterOptions.FilterOptionDescription.condo
            );
          } else if (
            SetUtil.isSuperset(
              PropertyTypeValueSet.commercial,
              cdResearchSubTypesSet
            )
          ) {
            //Commercial
            propertyTypeObject = Commercial_Options.find(
              item =>
                item.key === FilterOptions.FilterOptionDescription.commercial
            );
          }
          //check null or not
          if (!ObjectUtil.isEmpty(propertyTypeObject)) {
            let valueSet = new Set([...Array.from(propertyTypeObject.value)]);
            if (valueSet.size === cdResearchSubTypesSet.size) {
              selectedOptionArray.length = 0;
              this.onPressCount(propertyTypeObject, false, newSearchOptions);
              return;
            } else {
              selectedOptionArray.length = 0;
              selectedOptionArray.push(propertyTypeObject);
              this.handlePropertySubType(
                propertyTypeObject,
                cdResearchSubTypesSet,
                newSearchOptions
              );
              return;
            }
          }
        }
      } else if (
        prevSearchOptions.bedrooms !== newSearchOptions.bedrooms &&
        SetUtil.isSuperset(PropertyTypeValueSet.condo, cdResearchSubTypesSet)
      ) {
        //bedrooms for condo only
        let condoObject = Residential_Options.find(
          item => item.key === FilterOptions.FilterOptionDescription.condo
        );
        if (!ObjectUtil.isEmpty(newSearchOptions.bedrooms)) {
          //get array first
          let newBedRoomsArray = newSearchOptions.bedrooms.split(",");
          if (newBedRoomsArray.length === 1) {
            if (!ObjectUtil.isEmpty(condoObject)) {
              newSearchOptions = {
                ...newSearchOptions,
                cdTenure: "",
                leaseTermOptions: ""
              };
              selectedOptionArray.length = 0;
              selectedOptionArray.push(condoObject);
              this.onPressCount(
                condoObject.child.find(
                  item => item.value.toString() === newSearchOptions.bedrooms
                ),
                false,
                newSearchOptions
              );
              return;
            }
          } else {
            //more than one bedrooms from advanced filter,hide smart filter by requirement
            this.setState({ isAPIsCallCompleted: false });
            return;
          }
        } else {
          //if select "Any" in bedrooms, show condo lists
          selectedOptionArray.length = 0;
          this.onPressCount(condoObject, false, newSearchOptions);
          return;
        }
      } else if (
        prevSearchOptions.cdTenure !== newSearchOptions.cdTenure &&
        newSearchOptions.type === "S" &&
        this.isCondoOrLanded(cdResearchSubTypesSet)
      ) {
        if (newSearchOptions.cdTenure && prevSearchOptions.cdTenure) {
          //Hide SmartFilter when select more than one tenures from advanced filter
          this.setState({ isAPIsCallCompleted: false });
          return;
        }
      } else if (
        prevSearchOptions.leaseTermOptions !==
          newSearchOptions.leaseTermOptions &&
        newSearchOptions.type === "R" &&
        this.isCondoOrLanded(cdResearchSubTypesSet)
      ) {
        if (
          newSearchOptions.leaseTermOptions &&
          prevSearchOptions.leaseTermOptions
        ) {
          //Hide SmartFilter when select more than one lease term options from advanced filter
          this.setState({ isAPIsCallCompleted: false });
          return;
        }
      }

      //call concurrent api called
      this.onConcurrentAPICalled(newSearchOptions);
    }
  }

  handlePropertySubType(
    propertyTypeObject,
    cdResearchSubTypesSet,
    newSearchOptions
  ) {
    let subTypeObject = propertyTypeObject.child.find(
      item =>
        item.value.toString() === Array.from(cdResearchSubTypesSet).toString()
    );

    if (!ObjectUtil.isEmpty(subTypeObject)) {
      this.onPressCount(subTypeObject, false, newSearchOptions);
    } else {
      //select more than one property sub type
      subTypeObject = Object.assign(
        {},
        propertyTypeObject.child.find(
          item => item.key === FilterOptions.FilterOptionDescription.others
        )
      );
      if (!ObjectUtil.isEmpty(subTypeObject)) {
        subTypeObject.value = Array.from(cdResearchSubTypesSet).toString();
        this.onPressCount(subTypeObject, false, newSearchOptions);
      }
    }
  }

  isCondoOrLanded(cdResearchSubTypesSet) {
    if (
      SetUtil.isSuperset(PropertyTypeValueSet.condo, cdResearchSubTypesSet) ||
      SetUtil.isSuperset(PropertyTypeValueSet.landed, cdResearchSubTypesSet)
    ) {
      return true;
    }
    return false;
  }

  isPropertyTypeMultiSelected(cdResearchSubTypesSet) {
    let typeCount = 0;

    if (
      SetUtil.intersection(cdResearchSubTypesSet, PropertyTypeValueSet.condo)
        .size > 0
    ) {
      typeCount += 1;
    }
    if (
      SetUtil.intersection(cdResearchSubTypesSet, PropertyTypeValueSet.hdb)
        .size > 0
    ) {
      typeCount += 1;
    }
    if (
      SetUtil.intersection(cdResearchSubTypesSet, PropertyTypeValueSet.landed)
        .size > 0
    ) {
      typeCount += 1;
    }

    this.setState({ isAPIsCallCompleted: !(typeCount > 1) });
  }

  onConcurrentAPICalled(newSearchOptions) {
    const { filterOptionArray } = this.state;
    const { searchOptions } = this.props;

    filterOptionArray.forEach((item, index) => {
      //option Parameter
      var optionParameter = {
        ...searchOptions,
        ...newSearchOptions,
        ...this.generateParameterForAPI(item, true)
      };

      this.loadListingsCount(optionParameter, index);
    });
  }

  generateParameterForAPI(item, isForward) {
    //isForward is true to add the selected value if press filter options
    //isForward is false to remove value from params if press selected options by backward
    var newSearchOptions = null;
    if (!ObjectUtil.isEmpty(item)) {
      if (BuildYear_Set.has(item.key)) {
        //Built Year
        newSearchOptions = {
          builtYearMin: isForward ? item.value[0] : "",
          builtYearMax: isForward ? item.value[1] : ""
        };
      } else {
        //change item.value to string bcoz item.value is array value
        const itemValue = isForward ? item.value.toString() : "";

        if (CondoBedRoom_Set.has(item.key)) {
          //Condo Bedroom
          newSearchOptions = {
            bedrooms: itemValue,
            //if choose 6 bedroom, retrieve all listings which has bedrooms 6 and above
            //so, need to pass value in minBedrooms
            minBedrooms: itemValue === "6" ? itemValue : ""
          };
        } else if (Tenure_Set.has(item.key)) {
          //Tenure For Sale
          newSearchOptions = {
            cdTenure: itemValue
          };
        } else if (LeaseTerm_Set.has(item.key)) {
          //Lease Term for Rent
          newSearchOptions = {
            leaseTermOptions: itemValue
          };
        } else if (ModelNotRequired_Set.has(item.key)) {
          //Semi detached and detached
          newSearchOptions = {
            cdResearchSubTypes: itemValue,
            modelsNotRequired: "CLUSTER HOUSE"
          };
        } else {
          //Other subtypes
          newSearchOptions = {
            cdResearchSubTypes: itemValue,
            modelsNotRequired: ""
          };
        }
      }
    }
    return newSearchOptions;
  }

  //API
  loadListingsCount = (searchOptions, index) => {
    const { filterOptionArray } = this.state;
    SearchPropertyService.loadListingsCount({
      ...searchOptions,
      startResultIndex: 0
    })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          if (!ObjectUtil.isEmpty(filterOptionArray[index])) {
            filterOptionArray[index].count = response.result;
            this.setState({ counter: this.state.counter + 1 }, () => {
              if (this.state.counter === filterOptionArray.length) {
                this.setState({
                  isAPIsCallCompleted: true,
                  counter: 0
                });
              }
            });
          } //end of null validation
        }
      })
      .catch(error => {
        console.log("Error .. loadListingCount");
        console.log(error);
      });
  };

  onPressClear = () => {
    const { selectedOptionArray } = this.state;
    const { searchOptions, onSearchListingCalled } = this.props;

    var filterOptionArray = [];
    var changeState = false;

    //will remove last option
    var optionToRemove = selectedOptionArray[selectedOptionArray.length - 1];

    //CLEAR values of last index value from search options
    var newSearchOptions = {
      ...searchOptions,
      ...this.generateParameterForAPI(optionToRemove, false)
    };

    // will get child array from second last option
    var optionToGetValue = selectedOptionArray[selectedOptionArray.length - 2];

    if (!ObjectUtil.isEmpty(optionToGetValue)) {
      //removed last item from selected option array
      selectedOptionArray.pop();

      if (!ObjectUtil.isEmpty(optionToGetValue.child)) {
        filterOptionArray = optionToGetValue.child;
      } else if (
        !ObjectUtil.isEmpty(optionToGetValue.tenure) &&
        searchOptions.type === "S"
      ) {
        filterOptionArray = optionToGetValue.tenure;
      } else if (
        !ObjectUtil.isEmpty(optionToGetValue.leaseterm) &&
        searchOptions.type === "R"
      ) {
        filterOptionArray = optionToGetValue.leaseterm;
      }

      //flag whether need to change state or not
      changeState = true;
    } else if (
      ObjectUtil.isEmpty(optionToGetValue) &&
      !ObjectUtil.isEmpty(optionToRemove)
    ) {
      //removed last item from selected option array
      selectedOptionArray.pop();

      var valueSet = new Set([...Array.from(optionToRemove.value)]);

      if (SetUtil.isSuperset(PropertyTypeValueSet.commercial, valueSet)) {
        //set cdResearchSubTypes values
        newSearchOptions = {
          ...newSearchOptions,
          cdResearchSubTypes: Array.from(PropertyTypeValueSet.commercial).join(
            ","
          )
        };
        filterOptionArray = CommercialType_Options;
      } else {
        //set cdResearchSubTypes values
        newSearchOptions = {
          ...newSearchOptions,
          cdResearchSubTypes: Array.from(
            PropertyTypeValueSet.allResidential
          ).join(",")
        };
        filterOptionArray = Residential_Options;
      }

      //flag whether need to change state or not
      changeState = true;
    }

    if (changeState) {
      this.setState(
        {
          selectedOptionArray,
          filterOptionArray,
          isFiltersShowed: filterOptionArray.length > 0 ? true : false
        },
        () => {
          if (onSearchListingCalled) {
            onSearchListingCalled(newSearchOptions);
          }
        }
      );
    }
  };

  //onPress option count to filter by forwarding
  onPressCount = (item, callSearchListing) => {
    if (!ObjectUtil.isEmpty(item)) {
      const { searchOptions } = this.props;
      const { selectedOptionArray } = this.state;

      //selected option
      selectedOptionArray.push(item);

      //Populate search options value by forward
      const newSearchOptions = {
        ...searchOptions,
        ...this.generateParameterForAPI(item, true)
      };

      this.handleSelectedOption(item, callSearchListing, newSearchOptions);
    }
  };

  handleSelectedOption(item, callSearchListing, newSearchOptions) {
    const { onSearchListingCalled } = this.props;
    let newFilterOptionsArray = [];
    let isConcurrentCalled = false;

    if (!ObjectUtil.isEmpty(item.child)) {
      isConcurrentCalled = true;
      newFilterOptionsArray = item.child;
    } else if (
      !ObjectUtil.isEmpty(item.tenure) &&
      newSearchOptions.type === "S"
    ) {
      //For Sale, show tenure
      isConcurrentCalled = true;
      newFilterOptionsArray = item.tenure;
    } else if (
      !ObjectUtil.isEmpty(item.leaseterm) &&
      newSearchOptions.type === "R"
    ) {
      //For Rent, show lease term
      isConcurrentCalled = true;
      newFilterOptionsArray = item.leaseterm;
    }

    if (isConcurrentCalled) {
      this.setState(
        {
          isAPIsCallCompleted: false, //show smartfilter fully after finish calling all concurrent APIs.
          isFiltersShowed: true,
          filterOptionArray: newFilterOptionsArray
        },
        () => {
          //if need to call search listing api, no need to call loadlisting count api
          if (onSearchListingCalled && callSearchListing) {
            onSearchListingCalled(newSearchOptions);
          } else {
            this.onConcurrentAPICalled(newSearchOptions);
          }
        }
      );
    } else {
      this.setState({ isFiltersShowed: false }, () => {
        if (onSearchListingCalled && callSearchListing) {
          onSearchListingCalled(newSearchOptions);
        }
      });
    } //end of if item case
  }

  //Start Presentation Methods
  renderSelectedAndFilterOptions() {
    const {
      selectedOptionArray,
      filterOptionArray,
      isFiltersShowed
    } = this.state;
    return (
      <View style={styles.smartFilterSubContainer}>
        {this.renderClearButton()}
        {selectedOptionArray.map((item, index) =>
          this.renderSelectedOptionItem(item, index)
        )}
        {isFiltersShowed
          ? filterOptionArray.map((item, index) =>
              this.renderFilterOptionsItem(item, index)
            )
          : null}
      </View>
    );
  }

  renderClearButton() {
    const { selectedOptionArray } = this.state;
    if (!ObjectUtil.isEmpty(selectedOptionArray)) {
      if (
        Array.isArray(selectedOptionArray) &&
        selectedOptionArray.length > 0
      ) {
        return (
          <Button
            buttonStyle={{ marginRight: Spacing.XS }}
            textStyle={[Typography.SmallBody, { color: SRXColor.TextLink }]}
            onPress={() => this.onPressClear()}
          >
            Clear
          </Button>
        );
      }
    }
  }

  renderSelectedOptionItem(item, index) {
    if (!ObjectUtil.isEmpty(item)) {
      return (
        <View
          style={{
            flexDirection: "row",
            marginRight: Spacing.XS,
            alignItems: "center",
            justifyContent: "center"
          }}
        >
          {this.renderFilterOptionsIcon(item)}
          <SmallBodyText style={{ color: SRXColor.Black }}>
            {item.key}
          </SmallBodyText>
        </View>
      );
    }
  }

  renderFilterOptionsItem = (item, index) => {
    const { selectedOptionArray } = this.state;

    //Hide Others for Landed and Commerical
    let isLanded = selectedOptionArray.some(
      item => item.key === FilterOptions.FilterOptionDescription.landed
    );
    let isCommercial = selectedOptionArray.some(
      item => item.key === FilterOptions.FilterOptionDescription.commercial
    );

    if (!ObjectUtil.isEmpty(item)) {
      if (
        item.key !== FilterOptions.FilterOptionDescription.others ||
        !(isLanded || isCommercial)
      ) {
        if (item.count && item.count > 0) {
          return (
            <Button
              key={index}
              buttonStyle={{
                marginRight: Spacing.XS,
                alignItems: "center",
                justifyContent: "center"
              }}
              leftView={this.renderFilterOptionsIcon(item)}
              rightView={
                <View style={{ flexDirection: "row" }}>
                  <SmallBodyText style={styles.itemTextStyle}>
                    {item.key}
                  </SmallBodyText>
                  <SmallBodyText style={styles.itemTextStyle}>
                    {"("}
                    {StringUtil.formatThousand(item.count)}
                    {")"}
                  </SmallBodyText>
                </View>
              }
              onPress={() => this.onPressCount(item, true)}
            />
          );
        }
      }
    }
  };

  renderSeparator() {
    return <Separator edgeInset={{ left: Spacing.M, right: Spacing.M }} />;
  }
  renderFilterOptionsIcon(item) {
    if (item.icon) {
      return (
        <Image
          source={item.icon}
          resizeMode={"contain"}
          style={styles.optionIcon}
        />
      );
    }
  }

  render() {
    const { isAPIsCallCompleted } = this.state;
    if (isAPIsCallCompleted) {
      return (
        <View style={{ flex: 1 }}>
          {this.renderSeparator()}
          <ScrollView
            style={styles.container}
            horizontal={true}
            showsHorizontalScrollIndicator={false}
          >
            {this.renderSelectedAndFilterOptions()}
          </ScrollView>
        </View>
      );
    } else {
      return <View />;
    }
  }
}

SmartFilter.Sources = SmartFilterSource;

SmartFilter.propTypes = {
  searchOptions: PropTypes.object,

  /** function when click each filter option */
  onSearchListingCalled: PropTypes.func
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingTop: Spacing.XS,
    paddingBottom: Spacing.S,
    backgroundColor: SRXColor.White
  },
  smartFilterSubContainer: {
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: Spacing.M
  },
  itemTextStyle: {
    color: SRXColor.TextLink,
    lineHeight: 22,
    marginRight: 2
  },
  optionIcon: {
    width: 20,
    height: 22,
    marginRight: 2
  }
});

export { SmartFilter };
