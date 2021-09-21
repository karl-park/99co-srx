import React, {Component} from 'react';
import {View, FlatList, SafeAreaView, Alert} from 'react-native';
import {connect} from 'react-redux';

import {loadShortlistedItems, retrieveViewedListings} from '../../../actions';
import {ObjectUtil} from '../../../utils';
import {ListingListItem, ExpiredListingListItem} from '../../Listings';
import {SRXColor, AlertMessage} from '../../../constants';
import {NoShortlistScreen} from '../ShortlistedScreen';
import {Spacing} from '../../../styles';
import {Navigation} from 'react-native-navigation';
import PropTypes from 'prop-types';
/*
    Do not use this component as a screen, this component is merely display shortlisted listing
*/

class ShortlistedListingsList extends Component {
  static propTypes = {
    sortBy: PropTypes.string,
    onSelectListing: PropTypes.func,
    removeFromSelectedListingList: PropTypes.func,
  };

  static defaultProps = {
    sortBy: null,
  };

  constructor() {
    super();
    this.onListingSelected = this.onListingSelected.bind(this);
    this.removeFromSelectedListingList = this.removeFromSelectedListingList.bind(
      this,
    );
  }

  componentDidMount() {
    //get props
    const {
      sortBy,
      loadShortlistedItems,
      retrieveViewedListings,
      userPO,
    } = this.props;
    //retrieve view listings
    retrieveViewedListings();
    //get shortlisted items
    // if (!ObjectUtil.isEmpty(userPO)) {
    loadShortlistedItems({sortBy});
    //  }
  }

  componentDidUpdate(prevProps) {
    const {sortBy, loadShortlistedItems} = this.props;
    if (
      (prevProps.userPO != this.props.userPO &&
        !ObjectUtil.isEmpty(this.props.userPO)) ||
      prevProps.serverDomain !== this.props.serverDomain
    ) {
      loadShortlistedItems({sortBy});
    } else if (
      prevProps.sortBy != this.props.sortBy &&
      !ObjectUtil.isEmpty(this.props.userPO)
    ) {
      loadShortlistedItems({sortBy});
    } else if (
      prevProps.loginData.loadingState !== this.props.loginData.loadingState
    ) {
      loadShortlistedItems({sortBy});
    }
  }

  loadSortedListShortlistedItems = sortBy => {
    const {loadShortlistedItems} = this.props;
    loadShortlistedItems({sortBy});
  };

  onSelectListing = listingPO => {
    const {onSelectListing} = this.props;
    if (onSelectListing) {
      onSelectListing(listingPO);
    }
  };

  onListingSelected = listingPO => {
    if (!ObjectUtil.isEmpty(listingPO)) {
      const {onListingSelected} = this.props;
      if (onListingSelected) {
        onListingSelected(listingPO);
      }
    }
  };

  removeFromSelectedListingList = listingPO => {
    if (!ObjectUtil.isEmpty(listingPO)) {
      const {removeFromSelectedListingList} = this.props;
      if (removeFromSelectedListingList) {
        removeFromSelectedListingList(listingPO);
      }
    }
  };

  renderShortlistedList() {
    const {shortlistedItems} = this.props.shortlistData;

    if (!ObjectUtil.isEmpty(shortlistedItems)) {
      return (
        <FlatList
          keyExtractor={item => item.key}
          extraData={this.props}
          data={shortlistedItems}
          renderItem={({item, index}) =>
            this.renderShortlistItem({item, index})
          }
        />
      );
    }
  }

  renderShortlistItem = ({item, index}) => {
    const {selectedListings} = this.props;
    const {liveInd} = item.refListing;
    if (liveInd) {
      return (
        <ListingListItem
          // containerStyle={{
          //   backgroundColor:
          //     index % 2 == 0 ? SRXColor.White : SRXColor.AccordionBackground
          // }}
          key={index}
          listingPO={item.refListing}
          shortListedTab={true}
          onSelectListing={this.onSelectListing.bind()}
          selectedListingList={selectedListings}
          onSelectedEnquiryList={this.onListingSelected}
          removeFromSelectedListingList={this.removeFromSelectedListingList}
        />
      );
    } else {
      return <ExpiredListingListItem key={index} listingPO={item.refListing} />;
    }
  };

  render() {
    const {shortlistedItems} = this.props.shortlistData;
    if (Array.isArray(shortlistedItems)) {
      if (shortlistedItems.length > 0) {
        return (
          <SafeAreaView style={{flex: 1}}>
            {this.renderShortlistedList()}
          </SafeAreaView>
        );
      } else {
        return (
          <SafeAreaView style={{flex: 1}}>
            <NoShortlistScreen componentId={this.props.componentId} />
          </SafeAreaView>
        );
      }
    }
    return null;
  }
}

const mapStateToProps = state => {
  return {
    shortlistData: state.shortlistData,
    userPO: state.loginData.userPO,
    loginData: state.loginData,
    serverDomain: state.serverDomain.domainURL,
  };
};

export default connect(
  mapStateToProps,
  {
    loadShortlistedItems,
    retrieveViewedListings,
  },
  null,
  {withRef: true},
)(ShortlistedListingsList);
