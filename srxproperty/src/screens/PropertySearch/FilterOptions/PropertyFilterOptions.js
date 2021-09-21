import React, {Component} from 'react';
import {
  View,
  SafeAreaView,
  ScrollView,
  Platform,
  Image,
  Switch,
} from 'react-native';
import {Navigation} from 'react-native-navigation';

import {
  Picker,
  FeatherIcon,
  BodyText,
  Button,
  TextInput,
  SmallBodyText,
  ExtraSmallBodyText,
} from '../../../components';
import {
  All_Tenure_Options,
  PropertyType_Residential_Options,
  PropertyType_Commerical_Options,
  All_Property_Type_Set,
  Furnishing_Options,
  Floor_Options,
  ListedDate_Array,
  ROOM_TYPE_ARRAY,
  // RoomType_Rent_Options,
  Lease_Term_Options,
  PropertyTypeValueSet,
} from '../../PropertySearch/Constants';
import {SearchPropertyService} from '../../../services';
import {
  ObjectUtil,
  FilterOptionsUtil,
  SetUtil,
  StringUtil,
  PropertyTypeUtil,
  CollectionConversionUtil,
} from '../../../utils';
import {Styles} from './Styles';
import {SRXColor, FilterOptions} from '../../../constants';
import {Typography, ButtonStyles, Spacing} from '../../../styles';
import {PICKER_MODES} from '../../../components/Picker/Constant';
import {
  GroupedSelectionPropertyTypeList,
  GroupedSelectionList,
  RoomTypeFilterOption,
  SingleCheckboxFilterOption,
} from './Component';
import {FilterOptionsHelper} from './FilterOptionsHelper';
import {SmartFilter} from '../SearchResult/components';
import Moment, {min} from 'moment';
import {Listing_CertifiedIcon} from '../../../assets';

const isIOS = Platform.OS === 'ios';
class PropertyFilterOptions extends Component {
  //state variable
  state = {
    //only property type and room types need state variables
    selectedPropertyAndModelType: new Set(),
    selectedRoomTypes: undefined,

    priceRangeArray: [],
    psfRangeArray: [],
    activeSections: [],

    searchOptions: null,
    totalCount: 0,
    isResidential: true,
    minPSF: '',
    maxPSF: '',
    minPriceRange: '',
    maxPriceRange: '',
    minBuiltSize: '',
    maxBuiltSize: '',
    startConstructYear: '',
    endConstructYear: '',
    //for landed
    minLandSize: '',
    maxLandSize: '',
  };

  constructor(props) {
    super(props);

    Navigation.events().bindComponent(this);

    this.setNavigationOptionsForFilterResults();
  }

  setNavigationOptionsForFilterResults() {
    const componentId = this.props.componentId;
    return new Promise(function(resolve, reject) {
      FeatherIcon.getImageSource('x', 25, 'blue')
        .then(icon_close => {
          Navigation.mergeOptions(componentId, {
            topBar: {
              leftButtons: [
                {
                  id: 'btn_close',
                  icon: icon_close,
                },
              ],
              title: {
                text: 'Filter Results',
                alignment: 'center',
              },
              rightButtons: [
                {
                  id: 'btn_reset',
                  text: 'Reset',
                  color: SRXColor.TextLink,
                },
              ],
            },
          });
          resolve(true);
        })
        .catch(error => {
          console.log(error);
          reject(error);
        });
    }); //end of promise
  }

  navigationButtonPressed({buttonId}) {
    if (buttonId === 'btn_reset') {
      this.resetAllSelectedArray();
    } else if (buttonId === 'btn_close') {
      Navigation.dismissModal(this.props.componentId);
    }
  }

  componentDidMount() {
    this.populateFiltersData();
  }

  //Get total count of listings depend on selection
  getTotalNumberOfListings = options => {
    //For getting listing count
    var channelsFilter = '7';
    var wantedListingGroups = 'srxstp';
    var searchType = 'feature';

    if (!ObjectUtil.isEmpty(options)) {
      if (!ObjectUtil.isEmpty(options.searchText)) {
        searchType = 'keyword';
      }
    }

    if (searchType === 'keyword') {
      channelsFilter = '4';
      wantedListingGroups = 'mclpAllMatchSearchTerm,mclpAllNearby';
    }

    SearchPropertyService.searchListing({
      ...options,
      startResultIndex: 0,
      isTotalOnly: true,
      channelsFilter,
      wantedListingGroups,
      searchType,
    })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const {listingResult} = response;
          if (!ObjectUtil.isEmpty(listingResult)) {
            const {
              srxStpListings,
              mclpAllMatchSearchTerm,
              mclpAllNearby,
            } = listingResult;

            var mclpSearchTermTotal = 0;
            var mclpNearbyTotal = 0;
            var srxStpTotal = 0;
            if (!ObjectUtil.isEmpty(mclpAllMatchSearchTerm)) {
              const {total} = mclpAllMatchSearchTerm;
              mclpSearchTermTotal = total;
            }

            if (!ObjectUtil.isEmpty(mclpAllNearby)) {
              const {total} = mclpAllNearby;
              mclpNearbyTotal = total;
            }

            if (!ObjectUtil.isEmpty(srxStpListings)) {
              const {total} = srxStpListings;
              srxStpTotal = total;
            }

            this.setState({
              totalCount: mclpSearchTermTotal + mclpNearbyTotal + srxStpTotal,
            });
          }
        }
      })
      .catch(error => {
        console.log(error);
        console.log(
          'SearchPropertyService.searchListing - failed - some where in filter',
        );
      });
  };

  populateFiltersData() {
    const {searchOptions} = this.props;

    let newSearchOptions = null;
    let floorAreaArray = FilterOptionsHelper.getFloorAreaArray(searchOptions);

    let minBuiltSize = '';
    let maxBuiltSize = '';
    if (FilterOptionsHelper.isHDBOnly(searchOptions)) {
      minBuiltSize = floorAreaArray[0];
      maxBuiltSize = floorAreaArray[1];
    } else {
      if (floorAreaArray[0] != '') {
        minBuiltSize = Math.round(floorAreaArray[0] * 10.76);
      }

      if (floorAreaArray[1] != '') {
        maxBuiltSize = Math.round(floorAreaArray[1] * 10.76);
      }
    }

    const {cdResearchSubTypes, minLandSize, maxLandSize} = searchOptions;
    if (FilterOptionsHelper.isLandedOnly(cdResearchSubTypes)) {
      this.setState({
        minLandSize: minLandSize,
        maxLandSize: maxLandSize,
      });
      console.log('IsLandedOnly');
      console.log(cdResearchSubTypes);
      console.log(searchOptions);
      console.log('CD Research subtype');
    }

    let psfArray = FilterOptionsHelper.getPSFValueArray(searchOptions);
    let minPSF = psfArray[0];
    let maxPSF = psfArray[1];
    let priceRangeArray = FilterOptionsHelper.getPriceRangeValueArray(
      searchOptions,
    );
    let minPriceRange = priceRangeArray[0];
    let maxPriceRange = priceRangeArray[1];

    let constructionYearArray = FilterOptionsHelper.getBuildYearValueArray(
      searchOptions,
    );

    let startConstructYear = constructionYearArray[0];
    let endConstructYear = constructionYearArray[1];

    let selectedRoomTypes = searchOptions.isRoomRental;

    //Check whether selected property types are residential or commerical
    let isResidential = FilterOptionsHelper.isResidentialOrCommercial(
      searchOptions,
    );

    newSearchOptions = {
      ...searchOptions,
      minDateFirstPosted: '',
    };

    this.setState(
      {
        searchOptions: newSearchOptions,
        selectedPropertyAndModelType: FilterOptionsHelper.getPropertyTypeValueSet(
          searchOptions,
        ),
        isResidential,
        minPSF,
        maxPSF,
        minPriceRange,
        maxPriceRange,
        minBuiltSize,
        maxBuiltSize,
        startConstructYear,
        endConstructYear,
        selectedRoomTypes,
      },
      () => {
        this.getTotalNumberOfListings(this.state.searchOptions);
      },
    );
  }

  resetAllSelectedArray() {
    const {searchOptions} = this.state;

    let newSearchOptions = {
      ...searchOptions,
      cdResearchSubTypes: this.props.searchOptions.cdResearchSubTypes,
      bedrooms: '',
      bathrooms: '',
      minBedrooms: '',
      maxBedrooms: '',
      minBathrooms: '',
      maxBathrooms: '',
      minPSF: '',
      maxPSF: '',
      builtYearMin: '',
      builtYearMax: '',
      minSalePrice: '',
      maxSalePrice: '',
      minRentPrice: '',
      maxRentPrice: '',
      minBuiltSize: '',
      maxBuiltSize: '',
      cdTenure: '',
      furnish: '',
      floor: '',
      models: '',
      modelsNotRequired: '',
      minDateFirstPosted: '',
      leaseTermOptions: '',
      //include filters
      spvFilter: '',
      vt360Filter: '',
      droneViewFilter: '',
      exclusiveFilter: '',
      ownerCertificationFilter: '',
      //reset
      minLandSize: '',
      maxLandSize: '',
    };

    this.setState(
      {
        searchOptions: newSearchOptions,
        selectedRoomTypes: undefined,
        minPSF: '',
        maxPSF: '',
        minPriceRange: '',
        maxPriceRange: '',
        minBuiltSize: '',
        maxBuiltSize: '',
        minLandSize: '',
        maxLandSize: '',
        startConstructYear: '',
        endConstructYear: '',
        selectedPropertyAndModelType: FilterOptionsHelper.getPropertyTypeValueSet(
          newSearchOptions,
        ),
      },
      () => {
        this.getTotalNumberOfListings(newSearchOptions);
      },
    );
  }

  //Title for Selected result of Picker
  titleForSelectedResult = selectedReultList => {
    label = selectedReultList
      .map((item, index) => {
        return item.key;
      })
      .join(' - ');
    return label;
  };

  //Active Sections
  onSelectActiveSectionHeader = section => {
    if (!ObjectUtil.isEmpty(section)) {
      if (
        section.key === FilterOptions.FilterOptionDescription.all_residential
      ) {
        this.onSelectPropertyType(section, section);
      } else {
        const {activeSections} = this.state;
        if (!ObjectUtil.isEmpty(section)) {
          if (activeSections.includes(section.key)) {
            activeSections.splice(activeSections.indexOf(section.key), 1);
          } else {
            activeSections.push(section.key);
          }
          this.setState({activeSections});
        }
      }
    }
  };

  onSelectGroupedSelectionActiveSectionHeader = section => {
    const {isResidential, activeSections} = this.state;
    if (!ObjectUtil.isEmpty(section)) {
      this.onSelectGroupedSelectionPropertyType(section, section);
      if (
        section.key === FilterOptions.FilterOptionDescription.all_residential
      ) {
        this.setState({activeSections: []});
      } else if (isResidential) {
        if (!activeSections.includes(section.key)) {
          this.setState({activeSections: [section.key]});
        }
      } else {
        this.onSelectGroupedSelectionPropertySubType(0, section, []);
      }
    }
  };

  //Property Type done
  onSelectPropertySubType = (index, item, section) => {
    if (All_Property_Type_Set.has(item.value)) {
      this.onSelectPropertyType(section, section);
    } else {
      const {searchOptions, selectedPropertyAndModelType} = this.state;
      if (section.key === FilterOptions.FilterOptionDescription.condo) {
        this.onSelectPropertyType(item, section);
      } else {
        if (selectedPropertyAndModelType.has(item.value)) {
          selectedPropertyAndModelType.delete(item.value);
        } else {
          selectedPropertyAndModelType.add(item.value);
        }
        this.setState(
          {
            searchOptions: FilterOptionsHelper.propertyTypeSearchOptions(
              selectedPropertyAndModelType,
              searchOptions,
              section,
            ),
          },
          () => {
            this.getTotalNumberOfListings(this.state.searchOptions);
          },
        );
      }
    }
  };

  onSelectPropertyType = (section, fromSection) => {
    const {searchOptions, selectedPropertyAndModelType} = this.state;
    let propertyTypeTemp = null;
    if (section.value instanceof Set) {
      if (SetUtil.isSuperset(selectedPropertyAndModelType, section.value)) {
        propertyTypeTemp = SetUtil.difference(
          selectedPropertyAndModelType,
          section.value,
        );
      } else {
        propertyTypeTemp = SetUtil.union(
          selectedPropertyAndModelType,
          section.value,
        );
      }

      this.setState(
        {
          selectedPropertyAndModelType: propertyTypeTemp,
          searchOptions: FilterOptionsHelper.propertyTypeSearchOptions(
            propertyTypeTemp,
            searchOptions,
            fromSection,
          ),
        },
        () => {
          this.getTotalNumberOfListings(this.state.searchOptions);
        },
      );
    }
  };

  onSelectGroupedSelectionPropertyType = (section, fromSection) => {
    const {
      searchOptions,
      selectedPropertyAndModelType,
      isResidential,
    } = this.state;
    let propertyTypeTemp = null;
    if (section.value instanceof Set) {
      if (section === fromSection) {
        propertyTypeTemp = new Set(section.value);
      } else if (
        SetUtil.isSuperset(selectedPropertyAndModelType, section.value)
      ) {
        //Check if there is only 1 remaining selected property and Model
        //For Condo
        if (SetUtil.isSuperset(section.value, selectedPropertyAndModelType)) {
          propertyTypeTemp = selectedPropertyAndModelType;
        } else {
          propertyTypeTemp = SetUtil.difference(
            selectedPropertyAndModelType,
            section.value,
          );
        }
      } else {
        propertyTypeTemp = SetUtil.union(
          selectedPropertyAndModelType,
          section.value,
        );
      }

      this.setState(
        {
          selectedPropertyAndModelType: propertyTypeTemp,
          searchOptions: FilterOptionsHelper.propertyTypeSearchOptions(
            propertyTypeTemp,
            searchOptions,
            fromSection,
          ),
        },
        () => {
          this.getTotalNumberOfListings(this.state.searchOptions);
        },
      );
    }
  };

  //Property Sub Type
  onSelectGroupedSelectionPropertySubType = (index, item, section) => {
    const {isResidential} = this.state;
    if (All_Property_Type_Set.has(item.value)) {
      this.onSelectGroupedSelectionPropertyType(section, section);
    } else {
      const {searchOptions, selectedPropertyAndModelType} = this.state;
      if (section.key === FilterOptions.FilterOptionDescription.condo) {
        this.onSelectGroupedSelectionPropertyType(item, section);
      } else {
        if (isResidential) {
          //Residential room type is added here e.g HDB 1 room, 2 room
          if (selectedPropertyAndModelType.has(item.value)) {
            if (selectedPropertyAndModelType.size > 1) {
              selectedPropertyAndModelType.delete(item.value);
            }
          } else {
            selectedPropertyAndModelType.add(item.value);
          }
        } else {
          //Commercial subheader e.g Office, Warehouse is added here
          selectedPropertyAndModelType.clear();
          selectedPropertyAndModelType.add(item.value);
        }
        this.setState(
          {
            searchOptions: FilterOptionsHelper.propertyTypeSearchOptions(
              selectedPropertyAndModelType,
              searchOptions,
              section,
            ),
          },
          () => {
            this.getTotalNumberOfListings(this.state.searchOptions);
          },
        );
      }
    }
  };

  //Price Range
  onSelectPriceRange = () => {
    const {searchOptions, minPriceRange, maxPriceRange} = this.state;
    let newSearchOptions = null;
    if (searchOptions.type === 'S') {
      //For Sale
      newSearchOptions = {
        ...searchOptions,
        maxSalePrice: maxPriceRange,
        minSalePrice: minPriceRange,
      };
    } else {
      //For Rent
      newSearchOptions = {
        ...searchOptions,
        maxRentPrice: maxPriceRange,
        minRentPrice: minPriceRange,
      };
    }

    this.setState({searchOptions: newSearchOptions}, () => {
      this.getTotalNumberOfListings(this.state.searchOptions);
    });
  };

  //Bedrooms
  onSelectBedRooms = item => {
    if (!ObjectUtil.isEmpty(item)) {
      const {searchOptions} = this.state;

      //Get BedRooms Array
      let bedRoomsArray = FilterOptionsUtil.getBedAndBathRoomsOptions();
      let lastBedRoom = bedRoomsArray[bedRoomsArray.length - 1];

      let selectedBedRoomsSet = FilterOptionsHelper.getBedroomsSet(
        searchOptions,
      );
      let newSearchOptions = null;

      if (item.key === 'Any') {
        newSearchOptions = {
          ...searchOptions,
          minBedrooms: '',
          bedrooms: '',
        };
        //clear selected bedrooms
        selectedBedRoomsSet.clear();
      } else {
        //Remove Any options from selectedBedRoomsSet
        selectedBedRoomsSet.delete('');

        if (selectedBedRoomsSet.has(item.value)) {
          selectedBedRoomsSet.delete(item.value);
        } else {
          selectedBedRoomsSet.add(item.value);
        }

        //If select max Bedrooms
        if (item.value === lastBedRoom.value) {
          newSearchOptions = {
            ...searchOptions,
            minBedrooms: item.value,
            bedrooms: Array.from(selectedBedRoomsSet).join(','),
          };
        } else {
          newSearchOptions = {
            ...searchOptions,
            minBedrooms: '',
            bedrooms: Array.from(selectedBedRoomsSet).join(','),
          };
        }
      }

      this.setState({searchOptions: newSearchOptions}, () => {
        this.getTotalNumberOfListings(this.state.searchOptions);
      });
    }
  };

  //Bathrooms
  onSelectBathRooms = item => {
    if (!ObjectUtil.isEmpty(item)) {
      const {searchOptions} = this.state;

      //Get bathrooms array
      let bathRoomsArray = FilterOptionsUtil.getBedAndBathRoomsOptions();
      let lastBathRoom = bathRoomsArray[bathRoomsArray.length - 1];
      let selectedBathRoomsSet = FilterOptionsHelper.getBathroomSet(
        searchOptions,
      );
      let newSearchOptions = null;

      if (item.key === 'Any') {
        newSearchOptions = {
          ...searchOptions,
          minBathrooms: '',
          bathrooms: '',
        };
        //clear bathrooms set
        selectedBathRoomsSet.clear();
      } else {
        //remove "Any" option from selected bathrooms set
        selectedBathRoomsSet.delete('');

        if (selectedBathRoomsSet.has(item.value)) {
          selectedBathRoomsSet.delete(item.value);
        } else {
          selectedBathRoomsSet.add(item.value);
        }

        if (item.value === lastBathRoom.value) {
          newSearchOptions = {
            ...searchOptions,
            minBathrooms: item.value,
            bathrooms: Array.from(selectedBathRoomsSet).join(','),
          };
        } else {
          newSearchOptions = {
            ...searchOptions,
            minBathrooms: '',
            bathrooms: Array.from(selectedBathRoomsSet).join(','),
          };
        }
      }

      this.setState({searchOptions: newSearchOptions}, () => {
        this.getTotalNumberOfListings(this.state.searchOptions);
      });
    }
  };

  //PSF
  onSelectPsfRange = () => {
    const {searchOptions, minPSF, maxPSF} = this.state;
    let newSearchOptions = {
      ...searchOptions,
      maxPSF,
      minPSF,
    };

    this.setState({searchOptions: newSearchOptions}, () => {
      this.getTotalNumberOfListings(this.state.searchOptions);
    });
  };

  //Floor Size
  onSelectFloorSize = () => {
    const {
      searchOptions,
      minBuiltSize,
      maxBuiltSize,
      activeSections,
    } = this.state;
    let minimumFA = minBuiltSize;
    let maximumFA = maxBuiltSize;
    if (!activeSections.includes('HDB')) {
      if (minBuiltSize != '') {
        minimumFA = Math.round(parseInt(minBuiltSize) / 10.76);
      }
      if (maxBuiltSize != '') {
        maximumFA = Math.round(parseInt(maxBuiltSize) / 10.76);
      }
    }
    let newSearchOptions = {
      ...searchOptions,
      maxBuiltSize: maximumFA,
      minBuiltSize: minimumFA,
    };

    this.setState({searchOptions: newSearchOptions}, () => {
      this.getTotalNumberOfListings(this.state.searchOptions);
    });
  };

  onSelectLandSize = () => {
    const {searchOptions, minLandSize, maxLandSize} = this.state;

    let newSearchOptions = {
      ...searchOptions,
      minLandSize,
      maxLandSize,
    };

    this.setState({searchOptions: newSearchOptions}, () => {
      this.getTotalNumberOfListings(this.state.searchOptions);
    });
  };

  //Floor Level
  onSelectFloorLevel = (index, item, section) => {
    const {searchOptions} = this.state;
    let floorValueSet = FilterOptionsHelper.getFloorLevelValueSet(
      searchOptions,
    );

    if (!ObjectUtil.isEmpty(section) && !ObjectUtil.isEmpty(item)) {
      let newSearchOptions = null;

      if (item.value === FilterOptions.FilterOptionDescription.floorLevel) {
        if (SetUtil.isSuperset(floorValueSet, section.value)) {
          //uncheck all
          newSearchOptions = {
            ...searchOptions,
            floor: Array.from(
              SetUtil.difference(floorValueSet, section.value),
            ).join(','),
          };
        } else {
          newSearchOptions = {
            ...searchOptions,
            floor: Array.from(SetUtil.union(floorValueSet, section.value)).join(
              ',',
            ),
          };
        }
      } else {
        if (floorValueSet.has(item.value)) {
          floorValueSet.delete(item.value);
        } else {
          floorValueSet.add(item.value);
        }
        newSearchOptions = {
          ...searchOptions,
          floor: Array.from(floorValueSet).join(','),
        };
      }

      this.setState({searchOptions: newSearchOptions}, () => {
        this.getTotalNumberOfListings(this.state.searchOptions);
      });
    }
  };

  //Tenure Sub Type
  onSelectTenure = (index, item, section) => {
    const {searchOptions} = this.state;
    let tenureSet = FilterOptionsHelper.getTenureValueSet(searchOptions);

    if (!ObjectUtil.isEmpty(section) && !ObjectUtil.isEmpty(item)) {
      let newSearchOptions = null;

      if (item.value === FilterOptions.FilterOptionDescription.all_tenure) {
        if (SetUtil.isSuperset(tenureSet, section.value)) {
          //uncheck all
          newSearchOptions = {
            ...searchOptions,
            cdTenure: Array.from(
              SetUtil.difference(tenureSet, section.value),
            ).join(','),
          };
        } else {
          newSearchOptions = {
            ...searchOptions,
            cdTenure: Array.from(SetUtil.union(tenureSet, section.value)).join(
              ',',
            ),
          };
        }
      } else {
        if (tenureSet.has(item.value)) {
          tenureSet.delete(item.value);
        } else {
          tenureSet.add(item.value);
        }
        newSearchOptions = {
          ...searchOptions,
          cdTenure: Array.from(tenureSet).join(','),
        };
      }

      this.setState({searchOptions: newSearchOptions}, () => {
        this.getTotalNumberOfListings(this.state.searchOptions);
      });
    }
  };

  //Construction Year
  onSelectBuiltYear = item => {
    const {searchOptions} = this.state;
    let newSearchOptions = {
      ...searchOptions,
      builtYearMax: item[1],
      builtYearMin: item[0],
    };

    this.setState({searchOptions: newSearchOptions}, () => {
      this.getTotalNumberOfListings(this.state.searchOptions);
    });
  };

  //Furnishing
  onSelectFurnish = (index, item, section) => {
    const {searchOptions} = this.state;
    let furnishSet = FilterOptionsHelper.getFurnishValueSet(searchOptions);
    if (!ObjectUtil.isEmpty(item)) {
      if (furnishSet.has(item.value)) {
        furnishSet.delete(item.value);
      } else {
        furnishSet.add(item.value);
      }

      this.setState(
        {
          searchOptions: {
            ...searchOptions,
            furnish: Array.from(furnishSet).join(','),
          },
        },
        () => {
          this.getTotalNumberOfListings(this.state.searchOptions);
        },
      );
    }
  };

  onSelectRoomType = item => {
    const {searchOptions} = this.state;

    this.setState(
      {
        searchOptions: {
          ...searchOptions,
          isRoomRental: item.value,
        },
        selectedRoomTypes: item.value,
      },
      () => {
        this.getTotalNumberOfListings(this.state.searchOptions);
      },
    );
  };

  //Listed Date
  onSelectListedDate = item => {
    const {searchOptions} = this.state;

    this.setState(
      {
        searchOptions: {
          ...searchOptions,
          minDateFirstPosted: item.value,
        },
      },
      () => {
        this.getTotalNumberOfListings(this.state.searchOptions);
      },
    );
  };

  //Lease Term
  onSelectLeaseTerm = (index, item, section) => {
    const {searchOptions} = this.state;
    let leaseTermValueSet = FilterOptionsHelper.getLeaseTermValueSet(
      searchOptions,
    );

    if (!ObjectUtil.isEmpty(item)) {
      let newSearchOptions = null;
      if (leaseTermValueSet.has(item.value)) {
        leaseTermValueSet.delete(item.value);
      } else {
        leaseTermValueSet.add(item.value);
      }
      newSearchOptions = {
        ...searchOptions,
        leaseTermOptions: Array.from(leaseTermValueSet).join(','),
      };

      this.setState(
        {
          searchOptions: newSearchOptions,
        },
        () => {
          this.getTotalNumberOfListings(this.state.searchOptions);
        },
      );
    }
  };

  //XListing
  onPressXListingPrice = () => {
    const {searchOptions} = this.state;
    let spvFilter = searchOptions.spvFilter;
    if (spvFilter) {
      spvFilter = '';
    } else {
      spvFilter = true;
    }
    this.setState(
      {
        searchOptions: {
          ...searchOptions,
          spvFilter: spvFilter,
        },
      },
      () => {
        this.getTotalNumberOfListings(this.state.searchOptions);
      },
    );
  };

  //Virtual Tour
  onPressVirtualTour = () => {
    const {searchOptions} = this.state;
    let vt360Filter = searchOptions.vt360Filter;
    if (vt360Filter) {
      vt360Filter = '';
    } else {
      vt360Filter = true;
    }
    this.setState(
      {
        searchOptions: {
          ...searchOptions,
          vt360Filter: vt360Filter,
        },
      },
      () => {
        this.getTotalNumberOfListings(this.state.searchOptions);
      },
    );
  };

  //drone views
  onPressDroneViews = () => {
    const {searchOptions} = this.state;
    let droneViewFilter = searchOptions.droneViewFilter;
    if (droneViewFilter) {
      droneViewFilter = '';
    } else {
      droneViewFilter = true;
    }
    this.setState(
      {
        searchOptions: {
          ...searchOptions,
          droneViewFilter: droneViewFilter,
        },
      },
      () => {
        this.getTotalNumberOfListings(this.state.searchOptions);
      },
    );
  };

  // exclusive
  onPressExclusive = () => {
    const {searchOptions} = this.state;
    let exclusiveFilter = searchOptions.exclusiveFilter;
    if (exclusiveFilter) {
      exclusiveFilter = '';
    } else {
      exclusiveFilter = true;
    }
    this.setState(
      {
        searchOptions: {
          ...searchOptions,
          exclusiveFilter: exclusiveFilter,
        },
      },
      () => {
        this.getTotalNumberOfListings(this.state.searchOptions);
      },
    );
  };

  // certified
  onSwitchChangeCertified = () => {
    const {searchOptions} = this.state;
    let ownerCertificationFilter = searchOptions.ownerCertificationFilter;
    if (ownerCertificationFilter) {
      ownerCertificationFilter = '';
    } else {
      ownerCertificationFilter = true;
    }
    this.setState(
      {
        searchOptions: {
          ...searchOptions,
          ownerCertificationFilter: ownerCertificationFilter,
        },
      },
      () => {
        this.getTotalNumberOfListings(this.state.searchOptions);
      },
    );
  };

  //Event On Press Apply Filters
  onPressApplyFilters = () => {
    const {filterOptions, getSelectedFilterOptionsCount} = this.props;
    const {
      searchOptions,
      minPriceRange,
      maxPriceRange,
      minBuiltSize,
      maxBuiltSize,
      minPSF,
      maxPSF,
      activeSections,
      startConstructYear,
      endConstructYear,

      minLandSize,
      maxLandSize,
    } = this.state;

    let newSearchOptions = searchOptions;
    if (maxPriceRange != '' || minPriceRange != '') {
      if (newSearchOptions.type === 'S') {
        //For Sale
        if (
          searchOptions.maxSalePrice != maxPriceRange ||
          searchOptions.minSalePrice != minPriceRange
        ) {
          newSearchOptions = {
            ...newSearchOptions,
            maxSalePrice: maxPriceRange,
            minSalePrice: minPriceRange,
          };
        }
      } else {
        //For Rent
        if (
          searchOptions.maxRentPrice != maxPriceRange ||
          searchOptions.minRentPrice != minPriceRange
        ) {
          newSearchOptions = {
            ...newSearchOptions,
            maxRentPrice: maxPriceRange,
            minRentPrice: minPriceRange,
          };
        }
      }
    }

    if (minBuiltSize != '') {
      let minimumFA = minBuiltSize;
      if (!activeSections.includes('HDB')) {
        minimumFA = Math.round(parseInt(minBuiltSize) / 10.76);
      }
      if (searchOptions.minBuiltSize != minimumFA) {
        newSearchOptions = {
          ...newSearchOptions,
          minBuiltSize: minimumFA,
        };
      }
    } else {
      newSearchOptions = {
        ...newSearchOptions,
        minBuiltSize: '',
      };
    }

    if (maxBuiltSize != '') {
      let maximumFA = maxBuiltSize;
      if (!activeSections.includes('HDB')) {
        maximumFA = Math.round(parseInt(maxBuiltSize) / 10.76);
      }
      if (searchOptions.maxBuiltSize != maximumFA) {
        newSearchOptions = {
          ...newSearchOptions,
          maxBuiltSize: maximumFA,
        };
      }
    } else {
      newSearchOptions = {
        ...newSearchOptions,
        maxBuiltSize: '',
      };
    }

    //For LandSize for landed properties
    newSearchOptions = {
      ...newSearchOptions,
      minLandSize,
      maxLandSize,
    };

    if (searchOptions.minPSF != minPSF && minPSF != '') {
      if (minPSF == '') {
        newSearchOptions = {
          ...newSearchOptions,
          minPSF: '',
        };
      } else {
        newSearchOptions = {
          ...newSearchOptions,
          minPSF,
        };
      }
    }

    if (searchOptions.maxPSF != maxPSF && maxPSF != '') {
      if (maxPSF == '') {
        newSearchOptions = {
          ...newSearchOptions,
          maxPSF: '',
        };
      } else {
        newSearchOptions = {
          ...newSearchOptions,
          maxPSF,
        };
      }
    }
    let year = Moment().year() + 5;
    if (startConstructYear != searchOptions.builtYearMin) {
      if (startConstructYear == '') {
        newSearchOptions = {
          ...newSearchOptions,
          builtYearMin: '',
        };
      } else {
        let startingYear = parseInt(startConstructYear);
        if (startingYear < 1960) {
          startingYear = '';
        } else if (startingYear > year) {
          startingYear = year.toString();
        }
        newSearchOptions = {
          ...newSearchOptions,
          builtYearMin: startingYear.toString(),
        };
      }
    }

    if (searchOptions.builtYearMax != endConstructYear) {
      if (endConstructYear == '') {
        newSearchOptions = {
          ...newSearchOptions,
          builtYearMax: '',
        };
      } else {
        let endingYear = parseInt(endConstructYear);
        if (endingYear < 1960) {
          endingYear = '';
        } else if (endingYear > year) {
          endingYear = year.toString();
        }
        newSearchOptions = {
          ...newSearchOptions,
          builtYearMax: endingYear.toString(),
        };
      }
    }

    let selectedFilterOptionsCount = FilterOptionsHelper.getSelectedFilterOptionsCount(
      newSearchOptions,
    );

    //get Description for filter
    if (getSelectedFilterOptionsCount) {
      getSelectedFilterOptionsCount(
        selectedFilterOptionsCount > 0 ? selectedFilterOptionsCount : '',
      );
    }

    //to call filter options
    if (filterOptions) {
      newSearchOptions = {
        ...newSearchOptions,
        searchSource: SmartFilter.Sources.advancedSearch,
      };
      filterOptions(newSearchOptions);
    }

    this.setState({searchOptions: newSearchOptions}, () => {
      this.getTotalNumberOfListings(this.state.searchOptions);
    });

    Navigation.dismissModal(this.props.componentId);
  };

  //Start Rendering methods
  //Room or Entire House
  renderRoomType() {
    const {activeSections, selectedRoomTypes, searchOptions} = this.state;
    if (searchOptions.type === 'R') {
      const {searchOptions} = this.state;
      return (
        <View style={Styles.whiteBackgroundContainer}>
          <BodyText style={[Styles.bodyTextExtraStyle]}>Rental Type</BodyText>
          <View style={{flexDirection: 'row', flexWrap: 'wrap'}}>
            {ROOM_TYPE_ARRAY.map(item => {
              return (
                <Button
                  buttonStyle={[
                    {
                      borderWidth: 1,
                      paddingHorizontal: Spacing.S,
                      borderRadius: 18,
                      margin: Spacing.XS,
                    },
                    isIOS
                      ? {height: 40, borderRadius: 20}
                      : {height: 36, borderRadius: 18},
                    selectedRoomTypes == item.value
                      ? {borderColor: SRXColor.Teal}
                      : {borderColor: SRXColor.LightGray},
                  ]}
                  textStyle={{color: SRXColor.Black}}
                  onPress={() => this.onSelectRoomType(item)}>
                  {item.key}
                </Button>
              );
            })}
          </View>
        </View>
      );
      // return (
      //   <GroupedSelectionList
      //     containerStyle={{ backgroundColor: SRXColor.AccordionBackground }}
      // dataArray={RoomType_Rent_Options}
      //     activeSections={activeSections}
      //     selectedArray={selectedRoomTypes}
      //     onSelectHeader={this.onSelectActiveSectionHeader}
      //     onSelectContent={this.onSelectRoomType}
      //   />
      // );
    }
  }

  //Property Type
  renderPropertyType() {
    const {
      activeSections,
      selectedPropertyAndModelType,
      isResidential,
    } = this.state;
    if (isResidential) {
      PropertyType_Residential_Options.map((section, index) => {
        if (index !== 0) {
          if (SetUtil.isSuperset(section.value, selectedPropertyAndModelType)) {
            activeSections.push(section.key);
          }
        }
      });
    }
    return (
      <View style={{flex: 1}}>
        <GroupedSelectionPropertyTypeList
          dataArray={
            isResidential
              ? PropertyType_Residential_Options
              : PropertyType_Commerical_Options
          }
          isResidential={isResidential}
          activeSections={activeSections}
          selectedArray={selectedPropertyAndModelType}
          onSelectHeader={this.onSelectGroupedSelectionActiveSectionHeader}
          onSelectContent={this.onSelectGroupedSelectionPropertySubType}
        />
      </View>
    );
  }

  //Certified Listings
  renderCertifiedListings() {
    const {searchOptions} = this.state;
    return (
      <View style={{padding: Spacing.M, backgroundColor: SRXColor.White}}>
        <View style={{flexDirection: 'row'}}>
          <BodyText
            style={[
              Styles.bodyTextExtraStyle,
              {marginRight: Spacing.XS, alignSelf: 'center'},
            ]}>
            Certified Listings
          </BodyText>
          <Image
            style={{
              height: 20,
              width: 20,
              alignSelf: 'center',
              marginRight: Spacing.S,
            }}
            resizeMode={'contain'}
            source={Listing_CertifiedIcon}
          />
          <View style={Styles.newTextContainer}>
            <ExtraSmallBodyText style={{color: SRXColor.White}}>
              NEW
            </ExtraSmallBodyText>
          </View>
          <View
            style={{flexDirection: 'row', justifyContent: 'flex-end', flex: 1}}>
            <Switch
              trackColor={{
                true: SRXColor.TextLink,
                false: SRXColor.LightGray,
              }}
              thumbColor={isIOS ? null : SRXColor.White}
              value={searchOptions.ownerCertificationFilter}
              onValueChange={this.onSwitchChangeCertified}
            />
          </View>
        </View>
        <SmallBodyText
          style={{marginRight: Spacing.M * 3, marginTop: Spacing.XS / 2}}>
          A certified listing is a genuine listing certified by the property
          owner
        </SmallBodyText>
      </View>
    );
  }

  // Price Range
  renderPriceRange() {
    const {
      priceRangeArray,
      searchOptions,
      minPriceRange,
      maxPriceRange,
    } = this.state;
    // if (priceRangeArray.length > 0) {
    return (
      <View style={Styles.whiteBackgroundContainer}>
        <BodyText style={[Styles.bodyTextExtraStyle, {flex: 1}]}>
          Price Range
        </BodyText>
        <View
          style={{
            flexDirection: 'row',
            alignItems: 'center',
            marginTop: Spacing.XS,
          }}>
          <TextInput
            style={{flex: 1}}
            placeholder={'Any'}
            placeholderTextColor={SRXColor.Black}
            value={StringUtil.formatCurrency(minPriceRange)}
            keyboardType={isIOS ? 'number-pad' : 'numeric'}
            onChangeText={text =>
              this.setState({minPriceRange: StringUtil.decimalValue(text, 0)})
            }
            onBlur={() => {
              this.onSelectPriceRange();
            }}
          />
          <BodyText style={{margin: Spacing.M}}>to</BodyText>
          <TextInput
            style={{flex: 1}}
            placeholder={'Any'}
            placeholderTextColor={SRXColor.Black}
            value={StringUtil.formatCurrency(maxPriceRange)}
            keyboardType={isIOS ? 'number-pad' : 'numeric'}
            onChangeText={text =>
              this.setState({maxPriceRange: StringUtil.decimalValue(text, 0)})
            }
            onBlur={() => {
              this.onSelectPriceRange();
            }}
          />
        </View>
      </View>
      /* <View style={Styles.accordionBackgroundContainer}>
          <Picker
            prompt={"Price Range"}
            pickerMode={PICKER_MODES.MULTI}
            inputStyle={[
              Typography.SmallBody,
              { flex: 0, marginRight: Spacing.XS }
            ]}
            data={priceRangeArray}
            selectedValue={FilterOptionsHelper.getPriceRangeValueArray(
              searchOptions
            )}
            titleOfItem={(item, itemIndex, componentIndex) => item.key}
            valueOfItem={(item, itemIndex, componentIndex) => item.value}
            titleForSelectedResult={selectedObjects =>
              this.titleForSelectedResult(selectedObjects)
            }
            onSubmit={item => this.onSelectPriceRange(item)}
            renderLeftAccessory={() => (
              <BodyText style={[Styles.bodyTextExtraStyle, { flex: 1 }]}>
                Price Range
              </BodyText>
            )}
            renderRightAccessory={() => (
              <FeatherIcon name={"chevron-down"} size={24} color={"black"} />
            )}
          />
        </View> */
    );
    // }
  }

  //Bed rooms
  renderBedrooms() {
    const {searchOptions, isResidential} = this.state;
    if (isResidential) {
      return (
        <View style={{backgroundColor: SRXColor.White}}>
          <RoomTypeFilterOption
            name={'Bedrooms'}
            dataArray={FilterOptionsUtil.getBedAndBathRoomsOptions()}
            selectedRoomsSet={FilterOptionsHelper.getBedroomsSet(searchOptions)}
            onSelectRooms={this.onSelectBedRooms}
          />
        </View>
      );
    }
  }

  //Bath rooms
  renderBathrooms() {
    const {searchOptions, isResidential} = this.state;
    if (isResidential) {
      return (
        <View style={{backgroundColor: SRXColor.White}}>
          <RoomTypeFilterOption
            name={'Bathrooms'}
            dataArray={FilterOptionsUtil.getBedAndBathRoomsOptions()}
            selectedRoomsSet={FilterOptionsHelper.getBathroomSet(searchOptions)}
            onSelectRooms={this.onSelectBathRooms}
          />
        </View>
      );
    }
  }

  floorAreaMsg(size) {
    if (!ObjectUtil.isEmpty(size)) {
      let sqmSize = Math.round(size / 10.764);
      return size + ' sqft (' + sqmSize + ' sqm)';
    }
    return;
  }

  //PSF
  renderPSF() {
    const {
      psfRangeArray,
      searchOptions,
      activeSections,
      minPSF,
      maxPSF,
    } = this.state;
    // if (psfRangeArray.length > 0) {
    return (
      <View style={Styles.whiteBackgroundContainer}>
        <BodyText style={[Styles.bodyTextExtraStyle, {flex: 1}]}>
          Price per square feet
        </BodyText>
        <View
          style={{
            flexDirection: 'row',
            alignItems: 'center',
            marginTop: Spacing.XS,
          }}>
          <TextInput
            style={{flex: 1}}
            placeholder={'Any'}
            placeholderTextColor={SRXColor.Black}
            value={StringUtil.formatCurrency(minPSF)}
            keyboardType={isIOS ? 'number-pad' : 'numeric'}
            onChangeText={text =>
              this.setState({minPSF: StringUtil.decimalValue(text, 0)})
            }
            onBlur={() => {
              this.onSelectPsfRange();
            }}
          />
          <BodyText style={{margin: Spacing.M}}>to</BodyText>
          <TextInput
            style={{flex: 1}}
            placeholder={'Any'}
            placeholderTextColor={SRXColor.Black}
            value={StringUtil.formatCurrency(maxPSF)}
            keyboardType={isIOS ? 'number-pad' : 'numeric'}
            onChangeText={text =>
              this.setState({maxPSF: StringUtil.decimalValue(text, 0)})
            }
            onBlur={() => {
              this.onSelectPsfRange();
            }}
          />
        </View>
      </View>
    );
  }

  //Floor Area
  renderFloorArea() {
    const {
      searchOptions,
      minBuiltSize,
      maxBuiltSize,
      activeSections,
    } = this.state;

    return (
      <View style={Styles.whiteBackgroundContainer}>
        <BodyText style={[Styles.bodyTextExtraStyle, {flex: 1}]}>
          Floor Area
        </BodyText>
        <View
          style={{
            flexDirection: 'row',
            alignItems: 'center',
            marginTop: Spacing.XS,
          }}>
          <TextInput
            style={{flex: 1}}
            placeholder={'Any'}
            placeholderTextColor={SRXColor.Black}
            value={StringUtil.formatThousand(minBuiltSize)}
            keyboardType={isIOS ? 'number-pad' : 'numeric'}
            onChangeText={text => {
              if (text == 0) {
                this.setState({minBuiltSize: ''});
              } else {
                this.setState({
                  minBuiltSize: StringUtil.decimalValue(text, 0),
                });
              }
            }}
            rightView={
              <BodyText>
                {activeSections.includes('HDB') ? 'sqm' : 'sqft'}
              </BodyText>
            }
            onBlur={() => {
              this.onSelectFloorSize();
            }}
          />
          <BodyText style={{margin: Spacing.M}}>to</BodyText>
          <TextInput
            style={{flex: 1}}
            placeholder={'Any'}
            placeholderTextColor={SRXColor.Black}
            value={StringUtil.formatThousand(maxBuiltSize)}
            keyboardType={isIOS ? 'number-pad' : 'numeric'}
            onChangeText={text => {
              if (text == 0) {
                this.setState({maxBuiltSize: ''});
              } else {
                this.setState({
                  maxBuiltSize: StringUtil.decimalValue(text, 0),
                });
              }
            }}
            rightView={
              <BodyText>
                {activeSections.includes('HDB') ? 'sqm' : 'sqft'}
              </BodyText>
            }
            onBlur={() => {
              this.onSelectFloorSize();
            }}
          />
        </View>
      </View>
    );
  }

  //TODO: to add land size
  renderLandSize() {
    const {
      searchOptions,
      minLandSize,
      maxLandSize,
      activeSections,
    } = this.state;
    const {cdResearchSubTypes} = searchOptions;
    if (FilterOptionsHelper.isLandedOnly(cdResearchSubTypes)) {
      console.log('Inside isLanded Only');
      console.log('CdResearchSubType');
      console.log(cdResearchSubTypes);
      return (
        <View style={Styles.whiteBackgroundContainer}>
          <BodyText style={[Styles.bodyTextExtraStyle, {flex: 1}]}>
            Land Size
          </BodyText>
          <View
            style={{
              flexDirection: 'row',
              alignItems: 'center',
              marginTop: Spacing.XS,
            }}>
            <TextInput
              style={{flex: 1}}
              placeholder={'Any'}
              placeholderTextColor={SRXColor.Black}
              value={StringUtil.formatThousand(minLandSize)}
              keyboardType={isIOS ? 'number-pad' : 'numeric'}
              onChangeText={text => {
                if (text == 0) {
                  this.setState({minLandSize: ''});
                } else {
                  this.setState({
                    minLandSize: StringUtil.decimalValue(text, 0),
                  });
                }
              }}
              rightView={<BodyText>{'sqft'}</BodyText>}
              onBlur={() => {
                this.onSelectLandSize();
              }}
            />
            <BodyText style={{margin: Spacing.M}}>to</BodyText>
            <TextInput
              style={{flex: 1}}
              placeholder={'Any'}
              placeholderTextColor={SRXColor.Black}
              value={StringUtil.formatThousand(maxLandSize)}
              keyboardType={isIOS ? 'number-pad' : 'numeric'}
              onChangeText={text => {
                if (text == 0) {
                  this.setState({maxLandSize: ''});
                } else {
                  this.setState({
                    maxLandSize: StringUtil.decimalValue(text, 0),
                  });
                }
              }}
              rightView={<BodyText>{'sqft'}</BodyText>}
              onBlur={() => {
                this.onSelectLandSize();
              }}
            />
          </View>
        </View>
      );
    }
    //change cdResearchSubTypes to set
    // var landedSubTypes = CollectionConversionUtil.convertValueStringToSet(
    //   cdResearchSubTypes,
    // );
    // if (SetUtil.isEqualSet(PropertyTypeValueSet.landed, landedSubTypes)) {
    //   return (
    //     <View style={Styles.whiteBackgroundContainer}>
    //       <BodyText style={[Styles.bodyTextExtraStyle, {flex: 1}]}>
    //         Land Size
    //       </BodyText>
    //       <View
    //         style={{
    //           flexDirection: 'row',
    //           alignItems: 'center',
    //           marginTop: Spacing.XS,
    //         }}>
    //         <TextInput
    //           style={{flex: 1}}
    //           placeholder={'Any'}
    //           placeholderTextColor={SRXColor.Black}
    //           value={StringUtil.formatThousand(minLandSize)}
    //           keyboardType={isIOS ? 'number-pad' : 'numeric'}
    //           onChangeText={text => {
    //             if (text == 0) {
    //               this.setState({minLandSize: ''});
    //             } else {
    //               this.setState({
    //                 minLandSize: StringUtil.decimalValue(text, 0),
    //               });
    //             }
    //           }}
    //           rightView={<BodyText>{'sqft'}</BodyText>}
    //           onBlur={() => {
    //             this.onSelectLandSize();
    //           }}
    //         />
    //         <BodyText style={{margin: Spacing.M}}>to</BodyText>
    //         <TextInput
    //           style={{flex: 1}}
    //           placeholder={'Any'}
    //           placeholderTextColor={SRXColor.Black}
    //           value={StringUtil.formatThousand(maxLandSize)}
    //           keyboardType={isIOS ? 'number-pad' : 'numeric'}
    //           onChangeText={text => {
    //             if (text == 0) {
    //               this.setState({maxLandSize: ''});
    //             } else {
    //               this.setState({
    //                 maxLandSize: StringUtil.decimalValue(text, 0),
    //               });
    //             }
    //           }}
    //           rightView={<BodyText>{'sqft'}</BodyText>}
    //           onBlur={() => {
    //             this.onSelectLandSize();
    //           }}
    //         />
    //       </View>
    //     </View>
    //   );
    // }
  }

  //Floor Level
  renderFloorLevel() {
    const {activeSections, searchOptions} = this.state;
    return (
      <GroupedSelectionList
        containerStyle={{backgroundColor: SRXColor.White}}
        dataArray={Floor_Options}
        activeSections={activeSections}
        selectedArray={FilterOptionsHelper.getFloorLevelValueSet(searchOptions)}
        onSelectHeader={this.onSelectActiveSectionHeader}
        onSelectContent={this.onSelectFloorLevel}
      />
    );
  }

  //Tenure
  renderTenure() {
    const {activeSections, searchOptions} = this.state;
    return (
      <GroupedSelectionList
        containerStyle={{backgroundColor: SRXColor.AccordionBackground}}
        dataArray={All_Tenure_Options}
        activeSections={activeSections}
        selectedArray={FilterOptionsHelper.getTenureValueSet(searchOptions)}
        onSelectHeader={this.onSelectActiveSectionHeader}
        onSelectContent={this.onSelectTenure}
      />
    );
  }

  onUpdateStartConstructionYear = () => {
    const {startConstructYear, endConstructYear} = this.state;
    if (endConstructYear != '' && startConstructYear != '') {
      let startingYear = parseInt(startConstructYear);
      let endingYear = parseInt(endConstructYear);
      if (startingYear > endingYear) {
        this.setState({startConstructYear: ''}, () => {
          this.onSelectContructionYear();
        });
      } else {
        this.onSelectContructionYear();
      }
    } else {
      this.onSelectContructionYear();
    }
  };

  onUpdateEndConstructionYear = () => {
    const {startConstructYear, endConstructYear} = this.state;
    if (endConstructYear != '' && startConstructYear != '') {
      let startingYear = parseInt(startConstructYear);
      let endingYear = parseInt(endConstructYear);
      if (endingYear < startingYear) {
        this.setState({endConstructYear: ''}, () => {
          this.onSelectContructionYear();
        });
      } else {
        this.onSelectContructionYear();
      }
    } else {
      this.onSelectContructionYear();
    }
  };
  onSelectContructionYear = () => {
    const {startConstructYear, endConstructYear, searchOptions} = this.state;
    let year = Moment().year() + 5;
    let constructYear = [];
    let numStartConstructYear = parseInt(startConstructYear);
    let numEndConstructYear = parseInt(endConstructYear);
    if (startConstructYear != '' && numStartConstructYear < 1960) {
      constructYear.push('');
    } else if (numStartConstructYear > year) {
      constructYear.push(year.toString());
    } else {
      constructYear.push(startConstructYear);
    }
    console.log(constructYear);

    if (endConstructYear != '' && numEndConstructYear < 1960) {
      constructYear.push('');
    } else if (numEndConstructYear > year) {
      constructYear.push(year.toString());
    } else {
      constructYear.push(endConstructYear);
    }
    console.log(constructYear);
    let newSearchOptions = {
      ...searchOptions,
      builtYearMax: constructYear[1],
      builtYearMin: constructYear[0],
    };
    this.setState(
      {
        searchOptions: newSearchOptions,
        startConstructYear: constructYear[0],
        endConstructYear: constructYear[1],
      },
      () => {
        this.getTotalNumberOfListings(this.state.searchOptions);
      },
    );
  };

  //Construction Year
  renderConstructionYear() {
    const {searchOptions, startConstructYear, endConstructYear} = this.state;
    return (
      <View style={Styles.whiteBackgroundContainer}>
        <BodyText style={[Styles.bodyTextExtraStyle, {flex: 1}]}>
          Construction Year
        </BodyText>
        <View
          style={{
            flexDirection: 'row',
            alignItems: 'center',
            marginTop: Spacing.XS,
          }}>
          <TextInput
            style={{flex: 1}}
            placeholder={'Any'}
            placeholderTextColor={SRXColor.Black}
            value={startConstructYear}
            keyboardType={isIOS ? 'number-pad' : 'numeric'}
            onChangeText={text => this.setState({startConstructYear: text})}
            onBlur={() => {
              this.onUpdateStartConstructionYear();
            }}
          />
          <BodyText style={{margin: Spacing.M}}>to</BodyText>
          <TextInput
            style={{flex: 1}}
            placeholder={'Any'}
            placeholderTextColor={SRXColor.Black}
            value={endConstructYear}
            keyboardType={isIOS ? 'number-pad' : 'numeric'}
            onChangeText={text => this.setState({endConstructYear: text})}
            onBlur={() => {
              this.onUpdateEndConstructionYear();
            }}
          />
        </View>
      </View>
    );
  }

  //Furnish
  renderFurnishing() {
    const {activeSections, searchOptions} = this.state;
    return (
      <GroupedSelectionList
        containerStyle={{backgroundColor: SRXColor.AccordionBackground}}
        dataArray={Furnishing_Options}
        activeSections={activeSections}
        selectedArray={FilterOptionsHelper.getFurnishValueSet(searchOptions)}
        onSelectHeader={this.onSelectActiveSectionHeader}
        onSelectContent={this.onSelectFurnish}
      />
    );
  }

  //Listed Date
  renderListedDate() {
    const {searchOptions} = this.state;
    return (
      <View style={Styles.whiteBackgroundContainer}>
        <BodyText style={[Styles.bodyTextExtraStyle]}>Listed</BodyText>
        <View style={{flexDirection: 'row', flexWrap: 'wrap'}}>
          {ListedDate_Array.map(item => {
            return (
              <Button
                buttonStyle={[
                  {
                    borderWidth: 1,
                    paddingHorizontal: Spacing.S,
                    borderRadius: 18,
                    margin: Spacing.XS,
                  },
                  isIOS
                    ? {height: 40, borderRadius: 20}
                    : {height: 36, borderRadius: 18},
                  searchOptions.minDateFirstPosted == item.value
                    ? {borderColor: SRXColor.Teal}
                    : {borderColor: SRXColor.LightGray},
                ]}
                textStyle={{color: SRXColor.Black}}
                onPress={() => this.onSelectListedDate(item)}>
                {item.key}
              </Button>
            );
          })}
        </View>
        {/* <Picker
          prompt={"Listed Date"}
          pickerMode={PICKER_MODES.SINGLE}
          inputStyle={[
            Typography.SmallBody,
            { flex: 0, marginRight: Spacing.XS }
          ]}
          data={ListedDate_Array}
          selectedValue={
            searchOptions.minDateFirstPosted
              ? searchOptions.minDateFirstPosted
              : ""
          }
          titleOfItem={(item, itemIndex, componentIndex) => item.key}
          valueOfItem={(item, itemIndex, componentIndex) => item.value}
          titleForSelectedResult={selectedObjects =>
            this.titleForSelectedResult(selectedObjects)
          }
          onSubmit={item => this.onSelectListedDate(item)}
          renderLeftAccessory={() => (
            <BodyText style={[Styles.bodyTextExtraStyle, { flex: 1 }]}>
              Listed
            </BodyText>
          )}
          renderRightAccessory={() => (
            <FeatherIcon name={"chevron-down"} size={24} color={"black"} />
          )}
        /> */}
      </View>
    );
  }

  //Lease Term
  renderLeaseTerm() {
    const {searchOptions, activeSections} = this.state;
    if (searchOptions.type === 'R') {
      return (
        <GroupedSelectionList
          containerStyle={{backgroundColor: SRXColor.AccordionBackground}}
          dataArray={Lease_Term_Options}
          activeSections={activeSections}
          selectedArray={FilterOptionsHelper.getLeaseTermValueSet(
            searchOptions,
          )}
          onSelectHeader={this.onSelectActiveSectionHeader}
          onSelectContent={this.onSelectLeaseTerm}
        />
      );
    }
  }

  renderOtherOptions() {
    const {searchOptions} = this.state;
    let dataArray = [
      {
        name: 'v360°',
        value: searchOptions.vt360Filter,
        onPressFunction: this.onPressVirtualTour,
      },
      {
        name: 'Drone',
        value: searchOptions.droneViewFilter,
        onPressFunction: this.onPressDroneViews,
      },
      {
        name: 'Exclusive',
        value: searchOptions.exclusiveFilter,
        onPressFunction: this.onPressExclusive,
      },
      {
        name: 'with SRX certified valuations',
        value: searchOptions.spvFilter,
        onPressFunction: this.onPressXListingPrice,
      },
    ];
    return (
      <View
        style={{flex: 1, padding: Spacing.M, backgroundColor: SRXColor.White}}>
        <BodyText style={[Styles.bodyTextExtraStyle]}>
          Listing features
        </BodyText>
        <View style={{flexDirection: 'row', flexWrap: 'wrap'}}>
          {dataArray.map(item => {
            return (
              <Button
                buttonStyle={[
                  {
                    borderWidth: 1,
                    paddingHorizontal: Spacing.S,
                    margin: Spacing.XS,
                  },
                  isIOS
                    ? {height: 40, borderRadius: 20}
                    : {height: 36, borderRadius: 18},
                  item.value
                    ? {borderColor: SRXColor.Teal}
                    : {borderColor: SRXColor.LightGray},
                ]}
                textStyle={{color: SRXColor.Black}}
                onPress={() => item.onPressFunction()}>
                {item.name}
              </Button>
            );
          })}
        </View>
      </View>
    );
  }

  //X Listing Price
  renderXListingPrice() {
    const {searchOptions} = this.state;
    return (
      <SingleCheckboxFilterOption
        containerStyle={
          searchOptions.type === 'R'
            ? Styles.accordionBackgroundContainer
            : Styles.whiteBackgroundContainer
        }
        name={'X-Listing Price'}
        value={searchOptions.spvFilter}
        onToggleOption={this.onPressXListingPrice}
      />
    );
  }

  renderVirtualTour() {
    const {searchOptions} = this.state;
    return (
      <SingleCheckboxFilterOption
        containerStyle={
          searchOptions.type === 'R'
            ? Styles.whiteBackgroundContainer
            : Styles.accordionBackgroundContainer
        }
        name={'v360 Virtual Tours'}
        value={searchOptions.vt360Filter}
        onToggleOption={this.onPressVirtualTour}
      />
    );
  }

  renderDroneViews() {
    const {searchOptions} = this.state;
    return (
      <SingleCheckboxFilterOption
        containerStyle={
          searchOptions.type === 'R'
            ? Styles.accordionBackgroundContainer
            : Styles.whiteBackgroundContainer
        }
        name={'Drone Views'}
        value={searchOptions.droneViewFilter}
        onToggleOption={this.onPressDroneViews}
      />
    );
  }

  renderSpace() {
    return <View style={{height: 50}} />;
  }

  //Get Total Count For Filters
  renderTotalCountResult() {
    const {totalCount, isResidential} = this.state;
    if (totalCount && totalCount > 0) {
      return (
        <BodyText style={Styles.bodyTextExtraStyle}>
          {StringUtil.formatThousand(totalCount)}
          {isResidential ? ' Homes' : ' Properties'}
        </BodyText>
      );
    } else {
      return <BodyText style={{color: 'white'}}>{'No Results'}</BodyText>;
    }
  }

  renderApplyFilter() {
    return (
      <View style={Styles.applyFilterContainerStyle}>
        {this.renderTotalCountResult()}
        <View
          style={{flexDirection: 'row', justifyContent: 'flex-end', flex: 1}}>
          <Button
            buttonStyle={[
              ButtonStyles.primary_Container,
              ButtonStyles.primary_Container_Highlighted,
            ]}
            textStyle={ButtonStyles.primary_Title}
            onPress={() => {
              this.onPressApplyFilters();
            }}>
            Apply Filters
          </Button>
        </View>
      </View>
    );
  }

  renderMoreOptions() {
    return (
      <View>
        {this.renderBathrooms()}
        {this.renderPSF()}
        {this.renderFloorArea()}
        {this.renderLandSize()}
        {this.renderRoomType()}
        {this.renderFloorLevel()}
        {this.renderTenure()}
        {this.renderConstructionYear()}
        {this.renderFurnishing()}
        {this.renderListedDate()}
        {this.renderLeaseTerm()}
        {this.renderOtherOptions()}
        {this.renderSpace()}
      </View>
    );
  }

  render() {
    const {searchOptions} = this.state;
    if (!ObjectUtil.isEmpty(searchOptions)) {
      return (
        <SafeAreaView style={{flex: 1, backgroundColor: SRXColor.White}}>
          <ScrollView>
            {this.renderPropertyType()}
            {this.renderCertifiedListings()}
            {this.renderPriceRange()}
            {this.renderBedrooms()}
            {this.renderMoreOptions()}
          </ScrollView>
          {this.renderApplyFilter()}
        </SafeAreaView>
      );
    } else {
      return <View />;
    }
  }
}

export {PropertyFilterOptions};
