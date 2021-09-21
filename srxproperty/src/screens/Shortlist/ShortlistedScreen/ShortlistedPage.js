import React, {Component} from 'react';
import {
  SafeAreaView,
  StyleSheet,
  View,
  BackHandler,
  Alert,
  Share,
} from 'react-native';
import {connect} from 'react-redux';
import {Navigation} from 'react-native-navigation';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';

import {
  Picker,
  SmallBodyText,
  Button,
  FeatherIcon,
  LargeTitleComponent,
  BodyText,
  Separator,
  EnquirySheetSource,
  EnquirySheet,
} from '../../../components';
import {ObjectUtil, CommonUtil} from '../../../utils';
import {ShortlistedListingsList} from '../ShortlistedListing';
import {Spacing, Typography} from '../../../styles';
import {
  IS_IOS,
  SRXColor,
  SortByOptions,
  AlertMessage,
} from '../../../constants';
import {fblogin} from '../../../actions';
import {ShortlistService} from '../../../services';
import {EnquiryForm} from '../../Enquiry';

class ShortlistedPage extends LargeTitleComponent {
  state = {
    selectedSortOption: SortByOptions.sortOption.date_new_to_old, //default sort
    selectedListings: [],
  };

  constructor(props) {
    super(props);

    Navigation.events().bindComponent(this);

    this.showEnquirySheet = this.showEnquirySheet.bind(this);
    this.showEnquiryForm = this.showEnquiryForm.bind(this);
    this.onEnquiriesSubmitted = this.onEnquiriesSubmitted.bind(this);
  }

  componentDidAppear() {
    if (!IS_IOS) {
      //for android
      this.backHandler = BackHandler.addEventListener(
        'hardwareBackPress',
        () => {
          Navigation.mergeOptions(this.props.componentId, {
            bottomTabs: {
              currentTabIndex: 0,
            },
          });
          return true;
        },
      );
    }
  }

  componentDidDisappear() {
    //Remove all listeners
    if (!IS_IOS) {
      if (this.backHandler) {
        this.backHandler.remove();
      }
    }
  }

  componentDidUpdate(prevProps) {
    if (
      prevProps.shortlistData.shortlistedItems !==
      this.props.shortlistData.shortlistedItems
    ) {
      const {shortlistedItems} = this.props.shortlistData;
      if (!ObjectUtil.isEmpty(shortlistedItems)) {
        this.setupTopBar(true);
      } else {
        this.setupTopBar(false);
      }
    } else if (prevProps.userPO !== this.props.userPO) {
      this.setState({selectedListings: []});
    }
  }

  navigationButtonPressed({buttonId}) {
    const {shortlistedItems} = this.props.shortlistData;
    if (buttonId === 'share_Shortlist') {
      if (!ObjectUtil.isEmpty(shortlistedItems)) {
        this.shareShortlist();
      }
    }
  }

  onSelectSortOption = item => {
    if (!ObjectUtil.isEmpty(item)) {
      this.setState({selectedSortOption: item.value});
    }
  };

  onSelectListing = listingPO => {
    //direct to listing detailed page
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

  removeFromSelectedListingList = listingPO => {
    const {selectedListings} = this.state;

    if (!ObjectUtil.isEmpty(listingPO)) {
      var newList = [...selectedListings];
      if (selectedListings.includes(listingPO)) {
        newList.splice(selectedListings.indexOf(listingPO), 1);
      }
      this.setState({selectedListings: newList});
    }
  };

  setupTopBar(hasShortList) {
    if (hasShortList) {
      FeatherIcon.getImageSource(
        IS_IOS ? 'share' : 'share-2',
        24,
        'black',
      ).then(share_Shortlist => {
        Navigation.mergeOptions(this.props.componentId, {
          topBar: {
            rightButtons: [
              {
                id: 'share_Shortlist',
                icon: share_Shortlist,
              },
            ],
          },
        });
      });
    } else {
      Navigation.mergeOptions(this.props.componentId, {
        topBar: {
          rightButtons: [],
        },
      });
    }
  }

  //Enquiry Now Button
  onEnquiryNowPressed = () => {
    const {selectedListings} = this.state;
    if (
      !ObjectUtil.isEmpty(selectedListings) &&
      Array.isArray(selectedListings)
    ) {
      if (selectedListings.length == 1) {
        this.showEnquirySheet();
      } else {
        this.showEnquiryForm();
      }
    }
  };

  showEnquirySheet() {
    const {selectedListings} = this.state;
    const passData = {
      enquiryCallerComponentId: this.props.componentId,
      onEnquirySubmitted: this.onEnquiriesSubmitted,
      source: EnquirySheetSource.Shortlist,
      listingPO: selectedListings[0],
    };
    EnquirySheet.show(passData);
  }

  showEnquiryForm() {
    const {selectedListings} = this.state;
    const passProps = {
      listingPOs: selectedListings,
      source: EnquiryForm.Sources.Shortlist,
      onSuccess: this.onEnquiriesSubmitted,
    };
    Navigation.push(this.props.componentId, {
      component: {
        name: 'PropertySearchStack.EnquiryForm',
        passProps,
      },
    });
  }

  onEnquiriesSubmitted = () => {
    const {selectedListings} = this.state;
    this.setState({selectedListings: []}, () => {
      let message = 'Your enquiries for ';
      message += selectedListings.length + ' ';
      message += selectedListings.length > 1 ? 'listings' : 'listing';
      message += ' have been submitted.';
      Alert.alert(AlertMessage.SuccessMessageTitle, message);
    });
  };

  shareShortlist = () => {
    const {selectedListings} = this.state;

    var listingIdList = [];
    selectedListings.map(item => {
      listingIdList.push(parseInt(item.getListingId()));
    });

    this.getSharedListingsUrl(listingIdList);
  };

  getSharedListingsUrl = listingIdList => {
    ShortlistService.getSharedListingsUrl({
      listingIds: JSON.stringify(listingIdList),
    }).then(response => {
      if (!ObjectUtil.isEmpty(response)) {
        const error = CommonUtil.getErrorMessageFromSRXResponse(response);
        if (!ObjectUtil.isEmpty(error)) {
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
        } else {
          if (!ObjectUtil.isEmpty(response)) {
            const {result} = response;
            const sharedUrl = result;

            let msg =
              'Hey there! Check out this shortlist I found on SRX… what do you think?😊';
            if (!ObjectUtil.isEmpty(sharedUrl)) {
              msg = msg + ' ' + sharedUrl;
            }
            const content = {
              message: msg,
            };
            const options = {
              dialogTitle: 'Share Shortlist', //iOS only
            };

            Share.share(content, options);
          }
        }
      } else {
        Alert.alert(AlertMessage.ErrorMessageTitle, 'Error');
      }
    });
  };

  renderSortOptions() {
    const {shortlistedItems} = this.props.shortlistData;
    const {selectedSortOption} = this.state;

    if (!ObjectUtil.isEmpty(shortlistedItems)) {
      return (
        <View style={styles.shortListSortTopBarContainer}>
          <View style={{flex: 1}}>
            <SmallBodyText style={{fontWeight: '500'}}>
              {shortlistedItems.length}
              {' Shortlisted Properties'}
            </SmallBodyText>
          </View>
          <Picker
            mode={'dialog'}
            prompt={'Sort by'}
            inputStyle={[
              Typography.SmallBody,
              {
                fontWeight: '400',
                color: SRXColor.Teal,
                marginLeft: 3,
                flex: 0,
              },
            ]}
            data={SortByOptions.SORT_OPTION_ARRAY}
            selectedValue={selectedSortOption}
            titleOfItem={(item, itemIndex, componentIndex) => item.key}
            valueOfItem={(item, itemIndex, componentIndex) => item.value}
            useCustomPicker={true}
            renderLeftAccessory={() => <SmallBodyText>Sort by</SmallBodyText>}
            onSubmit={item => this.onSelectSortOption(item)}
          />
        </View>
      );
    }
  }

  renderShortListListings() {
    const {selectedSortOption, selectedListings} = this.state;

    return (
      <ShortlistedListingsList
        ref="shortListInfo"
        componentId={this.props.componentId}
        onSelectListing={this.onSelectListing}
        sortBy={selectedSortOption}
        onListingSelected={this.onListingSelected}
        removeFromSelectedListingList={this.removeFromSelectedListingList}
        selectedListings={selectedListings}
      />
    );
  }

  renderEnquiryLists() {
    const {selectedListings} = this.state;
    if (Array.isArray(selectedListings) && selectedListings.length > 0) {
      return (
        <SafeAreaView>
          <Separator />
          <View
            style={{
              flexDirection: 'row',
              alignItems: 'center',
              justifyContent: 'center',
              height: 60,
              paddingHorizontal: Spacing.M,
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
          </View>
        </SafeAreaView>
      );
    }
  }

  render() {
    return (
      <SafeAreaView style={{flex: 1}}>
        <KeyboardAwareScrollView onScroll={this.onScroll} style={{flex: 1}}>
          {this.renderLargeTitle('Shortlist')}
          {this.renderSortOptions()}
          {this.renderShortListListings()}
        </KeyboardAwareScrollView>
        {this.renderEnquiryLists()}
      </SafeAreaView>
    );
  }
}

const styles = StyleSheet.create({
  shortListSortTopBarContainer: {
    flexDirection: 'row',
    paddingHorizontal: Spacing.M,
    minHeight: 45,
    backgroundColor: 'white',
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
    alignItems: 'center',
  },
});

const mapStateToProps = state => {
  return {
    shortlistData: state.shortlistData,
    userPO: state.loginData.userPO,
  };
};

export default connect(
  mapStateToProps,
  {fblogin},
)(ShortlistedPage);
