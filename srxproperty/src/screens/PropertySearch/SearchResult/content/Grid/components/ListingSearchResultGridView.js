import React, {Component} from 'react';
import {
  View,
  ActivityIndicator,
  Alert,
  SectionList,
  Dimensions,
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
import {ObjectUtil} from '../../../../../../utils';
import {SRXColor, AppConstant, AlertMessage} from '../../../../../../constants';
import {Spacing} from '../../../../../../styles';
import {
  ListingGridItem,
  ListingListItem2,
  ListingListItem3,
} from '../../../../../Listings';
import {EnquiryForm} from '../../../../../Enquiry';
import {SearchFeaturedAgents} from '../../../components';
import {NoResultPlaceholder} from '../../../../../General';
import {Navigation} from 'react-native-navigation';

var {width} = Dimensions.get('window');

class ListingSearchResultGridView extends Component {
  state = {
    selectedListings: [],
    changedSearchOptions: false,
  };

  static defaultProps = {
    sections: [],
    isLoading: false,
    isLoadingMore: false,
    onEndReached: null,
    disabledSearchModification: false,
  };

  constructor(props) {
    super(props);
    this.onGridItemPressed = this.onGridItemPressed.bind(this);
    this.onListingSelected = this.onListingSelected.bind(this);
    this.onEnquiryListings = this.onEnquiryListings.bind(this);
    this.onSelectFeaturedAgentItem = this.onSelectFeaturedAgentItem.bind(this);
  }

  componentDidUpdate(prevProps) {
    if (prevProps.searchOptions !== this.props.searchOptions) {
      this.setState({changedSearchOptions: true});
    }
  }

  //Direct to listing details screen
  onGridItemPressed = listingPO => {
    if (!ObjectUtil.isEmpty(listingPO)) {
      const {onGridItemPressed} = this.props;
      if (onGridItemPressed) {
        onGridItemPressed(listingPO);
      }
    }
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

  //Direct to agent cv
  onSelectFeaturedAgentItem = agentPO => {
    const {onFeaturedAgentItemSelected} = this.props;
    if (onFeaturedAgentItemSelected) {
      onFeaturedAgentItemSelected(agentPO);
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
    } = this.props;
    const {changedSearchOptions} = this.state;
    console.log(sections);
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
            <RefreshControl
              refreshing={refreshing}
              onRefresh={onRefresh}
              progressViewOffset={120}
            />
          }
          contentInset={{
            top: disabledSearchModification
              ? 0
              : searchOptions.searchSource === 'AdvancedSearch' &&
                changedSearchOptions
              ? 90
              : 140,
          }}
          contentOffset={{
            x: 0,
            y: disabledSearchModification
              ? 0
              : searchOptions.searchSource === 'AdvancedSearch' &&
                changedSearchOptions
              ? -90
              : -140,
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
          <View style={{flex: 1, backgroundColor: SRXColor.White}}>
            <Heading2
              style={{
                paddingHorizontal: Spacing.M,
                marginVertical: Spacing.S,
              }}>
              {section.headerTitle}
            </Heading2>
            <Separator />
          </View>
        );
      }
    }
  };

  renderItem = ({item, index, section}) => {
    if (section.key === 'no_srxStp_searchTerm_Result') {
      /**Show no properties handler if there is no search result
       * for srxStpArray & mclpSearchTermArray*/
      return (
        <View key={index}>
          {this.renderListEmptyComponent()}
          <Separator />
        </View>
      );
    } else if (!ObjectUtil.isEmpty(item.featuredAgents)) {
      //Show featured agents in gridview if data in grid search
      return (
        <SearchFeaturedAgents
          key={index}
          data={item.featuredAgents}
          title={item.featuredAgentsTitle}
          onSelectFeaturedAgentItem={this.onSelectFeaturedAgentItem}
        />
      );
    } else if (Array.isArray(item)) {
      /**change items to array because of gridview &
       * Show listing item view
       */
      return this.renderListingItem({item, index, section});
    } else if (
      !ObjectUtil.isEmpty(item) ||
      !ObjectUtil.isEmpty(item.featured)
    ) {
      const {selectedListings} = this.state;
      if (item.featured.category === 1) {
        return (
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
      //Show no views for else condition
      return <View />;
    }
  };

  renderVerticalGridLineSeparator() {
    return <View style={{width: 1, backgroundColor: '#e0e0e0'}} />;
  }

  renderListingItem = ({item, index, section}) => {
    //after chunking data, each item contain two arrays
    const {selectedListings} = this.state;
    let displayWidth = width - Spacing.M * 2;
    return (
      <View style={{flex: 1, marginHorizontal: Spacing.M}}>
        <View style={{flexDirection: 'row'}}>
          {/* to show two items per row  */}
          {item.map((item, i) => (
            <View
              style={[
                {flexDirection: 'row'},
                i % 2 == 0
                  ? {width: (displayWidth + Spacing.S) / 2}
                  : {width: (displayWidth - Spacing.S) / 2},
              ]}>
              <View style={{width: (displayWidth - Spacing.S) / 2}}>
                <ListingGridItem
                  // containerStyle={{
                  //   backgroundColor:
                  //     index % 2 == 0
                  //       ? SRXColor.White
                  //       : SRXColor.AccordionBackground
                  // }}
                  listingPO={item}
                  selectedListingList={selectedListings}
                  onSelectListing={this.onGridItemPressed}
                  onSelectedEnquiryList={this.onListingSelected}
                />
              </View>

              <View style={i % 2 == 0 ? {width: Spacing.S} : null} />
              {/* {this.renderVerticalGridLineSeparator()} */}
            </View>
          ))}
        </View>
        {/* <Separator /> */}
      </View>
    );
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

ListingSearchResultGridView.propTypes = {
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

  /**direct to listing details when select each grid list item */
  onGridItemPressed: PropTypes.func,

  /**direct to agent cv when select each featured agents*/
  onFeaturedAgentItemSelected: PropTypes.func,

  /**
   * Rendered when the list is empty. Can be a React Component Class, a render function
   * Note: This item is not added directly to the section list props "ListEmptyComponent"
   */
  renderListEmptyComponent: PropTypes.func,
};

export default ListingSearchResultGridView;
