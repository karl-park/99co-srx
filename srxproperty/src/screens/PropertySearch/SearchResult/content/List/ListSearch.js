import React, {Component} from 'react';
import {View, Dimensions, Animated, Platform, Alert} from 'react-native';
import {Navigation} from 'react-native-navigation';

import PropTypes from 'prop-types';
import {
  Separator,
  EnquirySheetSource,
  EnquirySheet,
} from '../../../../../components';
import {SortByOptions, SRXColor, AlertMessage} from '../../../../../constants';
import {ObjectUtil} from '../../../../../utils';
import {ListingSearchManager} from '../../../Manager';
import {
  FilterButton,
  SortButton,
  SmartFilter,
  TransactedSwitch,
  SearchResultDescription,
  NoSearchResult,
} from '../../components';
import {Styles} from '../../Styles';
import {
  ListingSearchResultList,
  TransactedListingResultList,
} from './components';
import {Spacing} from '../../../../../styles';

var {width} = Dimensions.get('window');
const isIOS = Platform.OS === 'ios';

class ListSearch extends Component {
  static propTypes = {
    onFilterPressed: PropTypes.func,
    onSmartFilterSelected: PropTypes.func,
    onTransactedSwitchValueChange: PropTypes.func,
    isTransacted: PropTypes.bool,
    searchOptions: PropTypes.object,
    componentId: PropTypes.string,
    disabledSearchModification: PropTypes.bool,
    disableGoogleAnalytic: PropTypes.bool,
    source: PropTypes.string,

    selectedFilterOptionsCount: PropTypes.oneOfType([
      PropTypes.string,
      PropTypes.number,
    ]),
  };

  static defaultProps = {
    disabledSearchModification: false,
    disableGoogleAnalytic: false,
  };

  state = {
    sortOptionList: SortByOptions.SORT_OPTION_ARRAY,
    selectedSortOption: SortByOptions.sortOption.default, //default sort option

    sectionedListings: [],

    //display related state
    containerHeight: 45,
    movableTop: new Animated.Value(45),
    refreshing: false,
  };

  contentOffSetY = 0;

  constructor(props) {
    super(props);

    this.onListingLoaded = this.onListingLoaded.bind(this);
    this.onFilterPressed = this.onFilterPressed.bind(this);
    this.onSortOptionSelected = this.onSortOptionSelected.bind(this);
    this.onContentScroll = this.onContentScroll.bind(this);

    this.renderEmptyListComponent = this.renderEmptyListComponent.bind(this);
  }

  componentDidMount() {
    const {searchOptions, isTransacted} = this.props;

    var stateToUpdate;
    const {searchText, selectedAmenitiesIds} = searchOptions;
    if (
      !ObjectUtil.isEmpty(searchText) ||
      !ObjectUtil.isEmpty(selectedAmenitiesIds)
    ) {
      stateToUpdate = {
        sortOptionList: SortByOptions.SORT_OPTION_SEARCH_LOCATION_ARRAY,
      };
    } else {
      stateToUpdate = {
        sortOptionList: SortByOptions.SORT_OPTION_ARRAY,
      };
    }

    //this shall be removed in future
    stateToUpdate = {
      ...stateToUpdate,
    };

    this.setState(stateToUpdate);

    console.log('calling 1st');

    this.onCallSearchManager({
      ...searchOptions,
      orderCriteria: stateToUpdate.selectedSortOption,
      isTransacted: isTransacted,
    });
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    if (this.props.searchOptions !== prevProps.searchOptions) {
      var stateToUpdate;
      const searchTextChanged =
        this.props.searchOptions.searchText !==
        prevProps.searchOptions.searchText;
      const amenitiesChanged =
        this.props.searchOptions.selectedAmenitiesIds !==
        prevProps.searchOptions.selectedAmenitiesIds;
      const districtChanged =
        this.props.searchOptions.selectedDistrictIds !==
        prevProps.searchOptions.selectedDistrictIds;
      const areaChanged =
        this.props.searchOptions.selectedHdbTownIds !==
        prevProps.searchOptions.selectedHdbTownIds;
      if (
        searchTextChanged ||
        amenitiesChanged ||
        districtChanged ||
        areaChanged
      ) {
        if (
          !ObjectUtil.isEmpty(this.props.searchOptions.searchText) ||
          !ObjectUtil.isEmpty(this.props.searchOptions.selectedAmenitiesIds)
        ) {
          stateToUpdate = {
            sortOptionList: SortByOptions.SORT_OPTION_SEARCH_LOCATION_ARRAY,
          };
        } else {
          stateToUpdate = {
            sortOptionList: SortByOptions.SORT_OPTION_ARRAY,
          };

          /**
           * since distance is removed from the sort options
           * change back to default sorting if sorted by distane
           * else keep the sorting
           */

          if (
            this.state.selectedSortOption ===
              SortByOptions.sortOption.distance_near_to_far ||
            this.state.selectedSortOption ===
              SortByOptions.sortOption.distance_far_to_near
          ) {
            stateToUpdate = {
              ...stateToUpdate,
              selectedSortOption: SortByOptions.sortOption.default,
            };
          }
        }
      }

      if (!ObjectUtil.isEmpty(stateToUpdate)) {
        //setting selectedSortOption will trigger componentDidUpdate again
        //onCallSearchManager will be added again once location is separated from searchOptions
        if (
          !ObjectUtil.isEmpty(stateToUpdate.selectedSortOption) &&
          this.state.selectedSortOption != stateToUpdate.selectedSortOption
        ) {
          this.setState(stateToUpdate);
        } else {
          this.setState(stateToUpdate, () => {
            console.log('calling 5th -- Not changing sorting');
            this.onCallSearchManager({
              ...this.props.searchOptions,
              isTransacted: this.props.isTransacted,
              orderCriteria: this.state.selectedSortOption,
            });
          });
        }
      } else {
        console.log('calling 2nd');
        this.onCallSearchManager({
          ...this.props.searchOptions,
          isTransacted: this.props.isTransacted,
          orderCriteria: this.state.selectedSortOption,
        });
      }
    } else if (this.state.selectedSortOption != prevState.selectedSortOption) {
      console.log('calling 3rd');
      this.onCallSearchManager({
        ...this.props.searchOptions,
        isTransacted: this.props.isTransacted,
        orderCriteria: this.state.selectedSortOption,
      });
    } else if (this.props.isTransacted != prevProps.isTransacted) {
      console.log('calling 4th');
      this.onCallSearchManager({
        ...this.props.searchOptions,
        isTransacted: this.props.isTransacted,
        orderCriteria: this.state.selectedSortOption,
      });
    }
  }

  //Call Search Manager for searching
  onCallSearchManager(searchOptions) {
    const {disableGoogleAnalytic} = this.props;
    this.setState({isLoading: true}, () => {
      this.searchManager = new ListingSearchManager(searchOptions);
      this.searchManager.register(this.onListingLoaded);
      this.searchManager.search({tracking: !disableGoogleAnalytic});

      Animated.timing(this.state.movableTop, {
        toValue: 45,
        duration: 200,
        useNativeDriver: true,
      }).start();
    });
  }

  onRefreshResult(searchOptions) {
    const {disableGoogleAnalytic} = this.props;

    this.searchManager = new ListingSearchManager(searchOptions);
    this.searchManager.register(this.onListingLoaded);
    this.searchManager.search({tracking: !disableGoogleAnalytic});

    Animated.timing(this.state.movableTop, {
      toValue: 45,
      duration: 200,
      useNativeDriver: true,
    }).start();
  }

  onListingLoaded({
    newListings,
    allListings,
    error,
    totalListing,
    counts,
    searchTermAndNearByTotalListing,
    srxStpArray,
    mclpSearchTermArray,
    mclpNearByArray,
    featuredAgents,
    manager,
  }) {
    const {searchOptions} = this.props;
    console.log(searchOptions);

    const sectionsArray = [];

    /**
     * Featured agent shall be inserted in 3rd row of 1st section
     * no matter which result to be the 1st
     */

    if (!ObjectUtil.isEmpty(srxStpArray)) {
      const newSection = {
        data: srxStpArray,
        key: 'srxStp_Result',
      };
      sectionsArray.push(newSection);
    }

    if (!ObjectUtil.isEmpty(mclpSearchTermArray)) {
      const newSection = {
        data: mclpSearchTermArray,
        key: 'searchTerm_Result',
      };
      sectionsArray.push(newSection);
    }

    /**by new design, need to add no properties handler
     * if no data in srxStpArray & mclpSearchTermArray but
     * has data in mclpNearByArray */
    if (
      ObjectUtil.isEmpty(srxStpArray) &&
      ObjectUtil.isEmpty(mclpSearchTermArray) &&
      !ObjectUtil.isEmpty(mclpNearByArray)
    ) {
      /**If select PROJECT, STREET, DISTRICT HDBTOWN,
       * show no properties handler */
      if (this.showNoPropertiesHanlder()) {
        const newSection = {
          data: [
            {
              //to show no properties handler in section list
              noResultArray: true,
            },
          ],
          key: 'no_srxStp_searchTerm_Result',
        };
        sectionsArray.push(newSection);
      }
    }

    if (!ObjectUtil.isEmpty(mclpNearByArray)) {
      const newSection = {
        data: mclpNearByArray,
        key: 'Nearby_Result',
        headerTitle: 'Nearby Properties within 2KM',
      };
      sectionsArray.push(newSection);
    }

    if (!ObjectUtil.isEmpty(featuredAgents)) {
      const firstSection = sectionsArray[0];
      if (!ObjectUtil.isEmpty(firstSection)) {
        const firstPart = firstSection.data.slice(0, 2);
        const secondPart = firstSection.data.slice(2);
        firstSection.data = [
          ...firstPart,
          {
            featuredAgents: featuredAgents,
            //  featuredAgentsTitle: this.getFeaturedAgentsTitle()
          },
          ...secondPart,
        ];
      }
    }

    this.setState({
      sectionedListings: sectionsArray,
      resultCounts: counts,
      isLoading: false,
      refreshing: false,
    });
  }

  //Show or not no properties hanlder depend on suggestionEntryType
  showNoPropertiesHanlder() {
    const {searchOptions} = this.props;
    let showHanlder = false;

    if (!ObjectUtil.isEmpty(searchOptions)) {
      const {
        suggestionEntryType,
        selectedDistrictIds,
        selectedHdbTownIds,
        latitude,
        longitude,
      } = searchOptions;

      //For Entry Type
      if (
        suggestionEntryType === 'DISTRICT' ||
        (suggestionEntryType === 'STREET' &&
          latitude === 0 &&
          longitude === 0) ||
        suggestionEntryType === 'HDB_ESTATE' ||
        suggestionEntryType === 'CONDO'
      ) {
        showHanlder = true;
      }

      //Select distrcit hdb towns
      if (selectedDistrictIds || selectedHdbTownIds) {
        showHanlder = true;
      }
    }

    return showHanlder;
  }

  //featured agent title
  getFeaturedAgentsTitle() {
    const {searchOptions} = this.props;

    console.log(searchOptions);

    var title = 'SRX Featured Agents';

    if (!ObjectUtil.isEmpty(searchOptions)) {
      const {suggestionEntryType, searchText, displayText} = searchOptions;

      if (!ObjectUtil.isEmpty(suggestionEntryType)) {
        if (
          suggestionEntryType === 'DISTRICT' ||
          suggestionEntryType === 'HDB_ESTATE' ||
          suggestionEntryType === 'CONDO' ||
          suggestionEntryType === 'LANDED'
        ) {
          if (!ObjectUtil.isEmpty(searchText)) {
            title = 'Featured agents in ' + searchText;
          } else if (!ObjectUtil.isEmpty(displayText)) {
            title = 'Featured agents in ' + displayText;
          }
        }
      }
    }
    return title;
  }

  onFilterPressed = () => {
    const {onFilterPressed} = this.props;
    if (onFilterPressed) {
      onFilterPressed();
    }
  };

  onSortOptionSelected = item => {
    if (!ObjectUtil.isEmpty(item)) {
      this.setState({
        selectedSortOption: item.value,
      });
    }
  };

  onRefresh = () => {
    this.setState({refreshing: true}, () =>
      this.onRefreshResult({
        ...this.props.searchOptions,
        isTransacted: this.props.isTransacted,
        orderCriteria: this.state.selectedSortOption,
      }),
    );
  };

  //from ListingSearchResultList
  directToListingDetails = listingPO => {
    //direct to listing details
    Navigation.push(this.props.componentId, {
      component: {
        name: 'PropertySearchStack.ListingDetails',
        passProps: {
          listingId: listingPO.getListingId(),
          refType: listingPO.listingType,
        },
      },
    });
  };

  //from ListingSearchResultList
  directToAgentCV = agentPO => {
    //direct to agent cv from featured agent
    Navigation.push(this.props.componentId, {
      component: {
        name: 'ConciergeStack.AgentCV',
        passProps: {
          agentUserId: agentPO.id,
        },
      },
    });
  };

  //From ListingSearchResultList
  onEnquiryButtonPressed = passProps => {
    const {listingPOs} = passProps;
    //if selected listing size is one, show Enquiry Sheet
    if (!ObjectUtil.isEmpty(listingPOs) && Array.isArray(listingPOs)) {
      if (listingPOs.length == 1) {
        const {onSuccess} = passProps;
        this.showEnquirySheet(listingPOs, onSuccess);
      } else {
        this.showEnquiryForm(passProps);
      }
    }
  };

  showEnquirySheet = (listingPOs, onSuccess) => {
    const passData = {
      enquiryCallerComponentId: this.props.componentId,
      onEnquirySubmitted: onSuccess ?? this.onEnquiriesSubmitted,
      source: EnquirySheetSource.ListingSearch,
      listingPO: listingPOs[0],
    };

    EnquirySheet.show(passData);
  };

  onEnquiriesSubmitted = () => {
    let message = 'Your enquiry has been submitted.';
    Alert.alert(AlertMessage.SuccessMessageTitle, message);
  };

  showEnquiryForm = passProps => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'PropertySearchStack.EnquiryForm',
        passProps: passProps,
      },
    });
  };

  //reset Search options
  onResetSearchOptions = () => {
    const {onResetSearchOptions} = this.props;
    if (onResetSearchOptions) {
      onResetSearchOptions();
    }
  };

  onContentScroll = e => {
    const {containerHeight, movableTop} = this.state;

    const currentY = e.nativeEvent.contentOffset.y;
    const previousY = this.contentOffSetY;

    let top = Math.max(45 - currentY, 45 - containerHeight);

    if (currentY > previousY && currentY >= containerHeight) {
    } else if (currentY < previousY) {
      top = 45;
    }

    if (top > 45) {
      top = 45;
    }

    this.contentOffSetY = currentY;

    Animated.timing(movableTop, {
      toValue: top,
      duration: 40,
      useNativeDriver: true,
    }).start();
  };

  renderFilterButton() {
    const {selectedFilterOptionsCount} = this.props;
    return (
      <FilterButton
        selectedFilterOptionsCount={selectedFilterOptionsCount}
        onPress={this.onFilterPressed}
      />
    );
  }

  renderSortButton() {
    const {selectedSortOption, sortOptionList} = this.state;
    return (
      <SortButton
        data={sortOptionList}
        selectedOption={selectedSortOption}
        onSubmit={this.onSortOptionSelected}
      />
    );
  }

  renderFilterAndSortBy() {
    return (
      <View
        style={{
          paddingHorizontal: Spacing.M,
          backgroundColor: SRXColor.White,
        }}>
        <View style={Styles.filterAndSortContainer}>
          {this.renderFilterButton()}
          {this.renderSortButton()}
        </View>
        <Separator />
      </View>
    );
  }

  renderSmartFilter() {
    const {searchOptions, onSmartFilterSelected, isTransacted} = this.props;

    if (!ObjectUtil.isEmpty(searchOptions) && !isTransacted) {
      return (
        <SmartFilter
          searchOptions={searchOptions}
          onSearchListingCalled={onSmartFilterSelected}
        />
      );
    }
  }

  renderListingCountAndTransactedSwitch() {
    return (
      <View
        style={[
          Styles.propertiesAndMapContainer,
          {paddingHorizontal: Spacing.M, backgroundColor: SRXColor.White},
        ]}>
        {this.renderListingCount()}
        {this.renderTransactedSwitch()}
      </View>
    );
  }

  renderListingCount() {
    const {isTransacted, searchOptions} = this.props;
    const {resultCounts, isLoading} = this.state;
    return (
      <SearchResultDescription
        searchOptions={searchOptions}
        isTransacted={isTransacted}
        counts={isLoading ? null : resultCounts}
      />
    );
  }

  renderTransactedSwitch() {
    const {
      isTransacted,
      onTransactedSwitchValueChange,
      searchOptions,
    } = this.props;
    return (
      <TransactedSwitch
        isSale={searchOptions.type === 'S'}
        isTransacted={isTransacted}
        onSwitchValueChange={onTransactedSwitchValueChange}
      />
    );
  }

  renderListingSearchOptionsContainer() {
    return (
      <View style={{position: 'absolute', width: '100%'}}>
        {this.renderHidableViews()}
        {this.renderFilterAndSortBy()}
      </View>
    );
  }

  renderHidableViews() {
    const {containerHeight, movableTop} = this.state;

    return (
      <Animated.View
        style={[
          {
            position: 'absolute',
            width: '100%',
            overflow: 'hidden',
            borderBottomLeftRadius: 10,
            borderBottomRightRadius: 10,
          },
          {
            transform: [
              {
                translateY: movableTop,
              },
            ],
          },
        ]}
        onLayout={({
          nativeEvent: {
            layout: {x, y, width, height},
          },
        }) => {
          this.setState({containerHeight: height});
        }}>
        {this.renderListingCountAndTransactedSwitch()}
        {this.renderSmartFilter()}
      </Animated.View>
    );
  }

  renderEmptyListComponent() {
    return <NoSearchResult onResetSearchOptions={this.onResetSearchOptions} />;
  }

  renderResultList() {
    const {
      sectionedListings,
      isLoading,
      containerHeight,
      refreshing,
    } = this.state;
    const {
      isTransacted,
      disabledSearchModification,
      searchOptions,
      source,
    } = this.props;

    if (!ObjectUtil.isEmpty(this.searchManager)) {
      if (isTransacted) {
        return (
          <TransactedListingResultList
            sections={sectionedListings}
            isLoading={isLoading}
            isLoadingMore={this.searchManager.canLoadMore()}
            onEndReached={this.searchManager.loadMore}
            onRefresh={this.onRefresh}
            refreshing={refreshing}
            contentContainerStyle={
              disabledSearchModification
                ? null
                : {paddingTop: isIOS ? 0 : containerHeight + 45}
            }
            onScroll={this.onContentScroll}
            directToAgentCV={this.directToAgentCV}
            renderListEmptyComponent={
              disabledSearchModification ? null : this.renderEmptyListComponent
            }
            source={source}
          />
        );
      } else {
        console.log(sectionedListings);
        return (
          <ListingSearchResultList
            sections={sectionedListings}
            isLoading={isLoading}
            isLoadingMore={this.searchManager.canLoadMore()}
            onEndReached={this.searchManager.loadMore}
            onRefresh={this.onRefresh}
            refreshing={refreshing}
            contentContainerStyle={
              disabledSearchModification
                ? null
                : {paddingTop: isIOS ? 0 : containerHeight + 45}
            } //(45 -> between hidableview and list)
            onScroll={this.onContentScroll}
            //direct to listing details, agent cv and enquiry form screens
            onListItemPressed={this.directToListingDetails}
            onFeaturedAgentItemSelected={this.directToAgentCV}
            onEnquiryButtonPressed={this.onEnquiryButtonPressed}
            renderListEmptyComponent={
              disabledSearchModification ? null : this.renderEmptyListComponent
            }
            searchOptions={searchOptions}
            disabledSearchModification={disabledSearchModification}
          />
        );
      }
    }
  }

  render() {
    const {disabledSearchModification} = this.props;
    if (disabledSearchModification) {
      return (
        <View style={{flex: 1, width: width, backgroundColor: SRXColor.White}}>
          {this.renderResultList()}
        </View>
      );
    } else {
      return (
        <View
          style={{
            flex: 1,
            width: width,
            backgroundColor: SRXColor.White,
          }}>
          {this.renderResultList()}
          {this.renderListingSearchOptionsContainer()}
        </View>
      );
    }
  }
}

export {ListSearch};
