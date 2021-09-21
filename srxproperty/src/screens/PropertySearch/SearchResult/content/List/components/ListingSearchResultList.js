import React, {Component} from 'react';
import {
  View,
  ActivityIndicator,
  SectionList,
  Alert,
  RefreshControl,
} from 'react-native';
import SafeAreaView from 'react-native-safe-area-view';
import PropTypes from 'prop-types';
import {
  LoadMoreIndicator,
  Button,
  BodyText,
  Separator,
  Heading2,
} from '../../../../../../components';
import {ListingPO} from '../../../../../../dataObject';
import {ObjectUtil} from '../../../../../../utils';
import {SRXColor, AppConstant, AlertMessage} from '../../../../../../constants';
import {Spacing} from '../../../../../../styles';
import {
  ListingListItem,
  ListingListItem2,
  ListingListItem3,
} from '../../../../../Listings';
import {EnquiryForm} from '../../../../../Enquiry';

import {SearchFeaturedAgents} from '../../../components';
import {Navigation} from 'react-native-navigation';
import {NoResultPlaceholder} from '../../../../../General';

const ListingSearchResultListSource = {
  AgentCV: 'AgentCV',
};

class ListingSearchResultList extends Component {
  static options(passProps) {
    let title;
    if (!ObjectUtil.isEmpty(passProps)) {
      if (!ObjectUtil.isEmpty(passProps.title)) {
        title = passProps.title;
      }
    }
    return {
      topBar: {
        title: {
          text: title || '',
        },
      },
    };
  }

  state = {
    selectedListings: [],
    changedSearchOptions: false,
  };

  static defaultProps = {
    sections: [],
    isLoading: false,
    isLoadingMore: false,
    onEndReached: null,
    refreshing: false,
    disabledSearchModification: false,
  };

  constructor(props) {
    super(props);
    this.onEnquiriesSubmitted = this.onEnquiriesSubmitted.bind(this);
    this.onListItemPressed = this.onListItemPressed.bind(this);
    this.onListingSelected = this.onListingSelected.bind(this);
    this.onEnquiryListings = this.onEnquiryListings.bind(this);
  }

  componentDidUpdate(prevProps) {
    if (prevProps.searchOptions !== this.props.searchOptions) {
      this.setState({changedSearchOptions: true});
    }
  }

  //direct to listing details
  onListItemPressed = listingPO => {
    if (!ObjectUtil.isEmpty(listingPO)) {
      const {onListItemPressed, componentId} = this.props;
      if (onListItemPressed) {
        onListItemPressed(listingPO);
      } else if (componentId) {
        Navigation.push(componentId, {
          component: {
            name: 'PropertySearchStack.ListingDetails',
            passProps: {
              listingId: listingPO.getListingId(),
              refType: listingPO.listingType,
            },
          },
        });
      }
    }
  };

  onListingSelected = listingPO => {
    const {selectedListings} = this.state;

    if (!ObjectUtil.isEmpty(listingPO)) {
      var newList = [...selectedListings];
      if (selectedListings.includes(listingPO)) {
        newList.splice(selectedListings.indexOf(listingPO), 1);
      } else {
        newList = [...selectedListings, listingPO];
      }

      this.setState({selectedListings: newList});
    }
  };

  onEnquiriesSubmitted = () => {
    const {selectedListings} = this.state;
    let message = '';
    if (!ObjectUtil.isEmpty(selectedListings)) {
      this.setState({selectedListings: []}, () => {
        message = 'Your enquiries for ';
        message += selectedListings.length + ' ';
        message += selectedListings.length > 1 ? 'listings' : 'listing';
        message += ' have been submitted.';
      });
    } else {
      message = 'Your enquiry for 1 listing has been submitted.';
    }
    Alert.alert(AlertMessage.SuccessMessageTitle, message);
  };

  onEnquiryNowPressed = () => {
    const {selectedListings} = this.state;

    this.onEnquiryListings(selectedListings);
  };

  onEnquireListing = listingPO => {
    this.onEnquiryListings([listingPO]);
  };

  onEnquiryListings(listingPOs) {
    const {onEnquiryButtonPressed} = this.props;

    const passProps = {
      listingPOs: listingPOs,
      source: EnquiryForm.Sources.Listings,
      onSuccess: this.onEnquiriesSubmitted,
    };

    if (onEnquiryButtonPressed) {
      onEnquiryButtonPressed(passProps);
    }
  }

  //direct to agent cv
  onSelectFeaturedAgentItem = agentPO => {
    const {onFeaturedAgentItemSelected} = this.props;
    if (onFeaturedAgentItemSelected) {
      onFeaturedAgentItemSelected(agentPO);
    }
  };

  renderEnquiryLists() {
    const {selectedListings} = this.state;
    if (Array.isArray(selectedListings) && selectedListings.length > 0) {
      return (
        <SafeAreaView
          style={{
            flexDirection: 'row',
            alignItems: 'center',
            height: 60,
            backgroundColor: SRXColor.White,
            paddingHorizontal: Spacing.XL,
            borderTopWidth: 1,
            borderTopColor: '#e0e0e0',
          }}>
          <BodyText>
            {selectedListings.length}{' '}
            {selectedListings.length > 1 ? 'listings' : 'listing'} selected
          </BodyText>
          <View
            style={{
              flexDirection: 'row',
              justifyContent: 'flex-end',
              flex: 1,
            }}>
            <Button
              buttonType={Button.buttonTypes.primary}
              onPress={() => this.onEnquiryNowPressed()}>
              Enquire Now
            </Button>
          </View>
        </SafeAreaView>
      );
    }
  }

  renderListEmptyComponent() {
    if (this.props.renderListEmptyComponent) {
      return this.props.renderListEmptyComponent();
    } else {
      return <NoResultPlaceholder />;
    }
  }

  renderResultList() {
    const {
      sections,
      onEndReached,
      contentContainerStyle,
      onMomentumScrollBegin,
      onMomentumScrollEnd,
      onScroll,
      onScrollEndDrag,
      onRefresh,
      refreshing,
      searchOptions,
      disabledSearchModification,
      source,
    } = this.props;
    const {changedSearchOptions} = this.state;
    if (!ObjectUtil.isEmpty(sections)) {
      return (
        <SectionList
          style={{flex: 1}}
          onLayout={({
            nativeEvent: {
              layout: {x, y, width, height},
            },
          }) => {
            this.setState({listViewHeight: height});
          }}
          sections={sections}
          stickySectionHeadersEnabled={false}
          renderItem={({index, item, section}) =>
            this.renderItem({index, item, section})
          }
          renderSectionHeader={({section}) => this.renderHeader({section})}
          keyExtractor={(item, index) => item + index}
          onEndReached={onEndReached}
          ListFooterComponent={this.renderFooter}
          refreshControl={
            source === ListingSearchResultListSource.AgentCV ? null : (
              <RefreshControl
                refreshing={refreshing}
                onRefresh={onRefresh}
                progressViewOffset={120}
              />
            )
          }
          contentInset={{
            top: disabledSearchModification
              ? 10
              : searchOptions
              ? searchOptions.searchSource === 'AdvancedSearch' &&
                changedSearchOptions
                ? 90
                : 140
              : 10,
          }}
          contentOffset={{
            x: 0,
            y: disabledSearchModification
              ? -10
              : searchOptions
              ? searchOptions.searchSource === 'AdvancedSearch' &&
                changedSearchOptions
                ? -90
                : -140
              : -10,
          }}
          //
          contentContainerStyle={contentContainerStyle}
          onMomentumScrollBegin={onMomentumScrollBegin}
          onMomentumScrollEnd={onMomentumScrollEnd}
          onScroll={onScroll}
          onScrollEndDrag={onScrollEndDrag}
          removeClippedSubviews={true}
        />
      );
    } else {
      return this.renderListEmptyComponent();
    }
  }

  renderHeader = ({section}) => {
    if (!ObjectUtil.isEmpty(section)) {
      if (!ObjectUtil.isEmpty(section.headerTitle) && section.data.length > 0) {
        return (
          <View style={{flex: 1}}>
            <Heading2
              style={{
                paddingHorizontal: Spacing.M,
                //marginVertical: Spacing.S
              }}>
              {section.headerTitle}
            </Heading2>
            {/* <Separator /> */}
          </View>
        );
      }
    }
  };

  renderItem = ({item, index, section}) => {
    if (item instanceof ListingPO) {
      //show listing item view if item is instance of ListingPO
      return this.renderListingItem({item, index, section});
    } else if (!ObjectUtil.isEmpty(item.featuredAgents)) {
      //show featured agents list if there is data in listing search
      return (
        <SearchFeaturedAgents
          key={section.key}
          data={item.featuredAgents}
          title={item.featuredAgentsTitle}
          onSelectFeaturedAgentItem={this.onSelectFeaturedAgentItem}
        />
      );
    } else if (section.key === 'no_srxStp_searchTerm_Result') {
      //show no properties handler if there is no search result for srxStpArray & mclpSearchTermArray
      return (
        <View key={index}>
          {this.renderListEmptyComponent()}
          <Separator />
        </View>
      );
    } else {
      //show no views for else condition
      return <View />;
    }
  };

  renderListingItem = ({item, index, section}) => {
    const {selectedListings} = this.state;
    if (!ObjectUtil.isEmpty(item.featured)) {
      // category === 1 - Featured Listing, 2 - Featured Plus Listing
      if (item.featured.category === 1) {
        return (
          //Featured Listing
          <ListingListItem3
            key={index}
            listingPO={item}
            shortListedTab={false}
            selectedListingList={selectedListings}
            onSelectListing={this.onListItemPressed}
            onSelectedEnquiryList={this.onListingSelected}
            onEnquireListing={this.onEnquireListing}
          />
        );
      } else {
        return (
          //Featured Plus Listing
          <ListingListItem2
            key={index}
            listingPO={item}
            shortListedTab={false}
            selectedListingList={selectedListings}
            onSelectListing={this.onListItemPressed}
            onSelectedEnquiryList={this.onListingSelected}
            onEnquireListing={this.onEnquireListing}
          />
        );
      }
    } else {
      return (
        //Listing
        <ListingListItem
          key={index}
          listingPO={item}
          shortListedTab={false}
          selectedListingList={selectedListings}
          onSelectListing={this.onListItemPressed}
          onSelectedEnquiryList={this.onListingSelected}
        />
      );
    }
  };

  renderFooter = () => {
    const {isLoadingMore} = this.props;
    if (isLoadingMore) {
      return (
        <View style={{backgroundColor: SRXColor.White}}>
          <LoadMoreIndicator />
        </View>
      );
    }
    return null;
  };

  render() {
    const {isLoading} = this.props;
    if (isLoading) {
      return (
        <View
          style={{
            flex: 1,
            alignItems: 'center',
            justifyContent: 'center',
            backgroundColor: SRXColor.White,
          }}>
          <ActivityIndicator />
        </View>
      );
    } else {
      return (
        <View style={{flex: 1}}>
          {this.renderResultList()}
          {this.renderEnquiryLists()}
        </View>
      );
    }
  }
}

ListingSearchResultList.Sources = ListingSearchResultListSource;

ListingSearchResultList.propTypes = {
  /**
   * array of objects containing section data
   * {
   *   data: [ class object listingPO ],
   *   key : string represent this section,
   *   headerTitle: title of section, optional, if you want to show title for the section
   * }
   */
  sections: PropTypes.array.isRequired,
  isLoading: PropTypes.bool,
  isLoadingMore: PropTypes.bool,
  onEndReached: PropTypes.func,
  searchOptions: PropTypes.object,
  disabledSearchModification: PropTypes.bool,
  source: PropTypes.oneOf(Object.keys(ListingSearchResultListSource)),

  /**direct to listing details when select each listing list item */
  onListItemPressed: PropTypes.func,

  /**direct to agent cv when select each featured agents*/
  onFeaturedAgentItemSelected: PropTypes.func,

  /**direct to enquiry form when press enquiry now for selected listings*/
  onEnquiryButtonPressed: PropTypes.func,

  /**
   * Rendered when the list is empty. Can be a React Component Class, a render function
   * Note: This item is not added directly to the section list props "ListEmptyComponent"
   */
  renderListEmptyComponent: PropTypes.func,
};

export default ListingSearchResultList;
