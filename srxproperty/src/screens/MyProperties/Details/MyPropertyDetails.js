import React, {Component} from 'react';
import {View, SafeAreaView, Alert, ActivityIndicator} from 'react-native';
import {Navigation} from 'react-native-navigation';
import PropTypes from 'prop-types';
import Moment from 'moment';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
import {connect} from 'react-redux';
import {updateTracker, removeTracker} from '../../../actions';
import {
  Separator,
  TouchableHighlight,
  BodyText,
  FeatherIcon,
  LargeTitleComponent,
} from '../../../components';
import {SRXColor, AlertMessage, LoadingState} from '../../../constants';
import {
  AmenitiesCategoryPO,
  SRXPropertyUserPO,
  ListingPO,
} from '../../../dataObject';
import {XTrendGraph, Transactions} from '../../../screens';
import {
  CertifiedListingService,
  PropertyTrackerService,
  TrackingService,
} from '../../../services';
import {Spacing} from '../../../styles';
import {
  PropertyListedIndicator,
  MyPropertyValues,
  MyPropertyRegionalComparison,
  PurchaseDetail,
  ProjectInformation,
  ConnectAgent,
  TrackerValueChangesItem,
  InviterAgencyInfo,
} from './components';
import {Amenities} from '../../Listings/ListingDetails/DetailsContent';
import {
  ObjectUtil,
  PropertyTypeUtil,
  StringUtil,
  CommonUtil,
} from '../../../utils';
import {PropertyTrackerUtil} from './utils';
import {PropertyTypeValueSet} from '../../PropertySearch/Constants';
import {AmenitiesSource} from '../../Amenities/constant';
import {TransactedListingResultList} from '../../PropertySearch/SearchResult/content/List/components';
import {TransactedListingSearchResultGridView} from '../../PropertySearch/SearchResult/content/Grid/components';
import {XValueDisclaimer} from '../../X-Value/Result/Components';

class MyPropertyDetails extends LargeTitleComponent {
  constructor(props) {
    super(props);

    Navigation.events().bindComponent(this);

    this.directToManageProperty = this.directToManageProperty.bind(this);
    this.directToListingResult = this.directToListingResult.bind(this);
    this.directToListedListing = this.directToListedListing.bind(this);
    this.directToFindAgent = this.directToFindAgent.bind(this);
    this.onTrackerPOUpdated = this.onTrackerPOUpdated.bind(this);
    this.chatWithAgent = this.chatWithAgent.bind(this);
  }

  state = {
    trackerPO: null,
    propertyAmenities: null,
    certifyListingNavigation: false,
    hideComponents: false,
  };

  componentDidMount() {
    this.addRightButtons();
    const {initialTrackerPO, ptUserId, certifyListing} = this.props;
    if (!ObjectUtil.isEmpty(initialTrackerPO)) {
      this.setState(
        {
          trackerPO: initialTrackerPO,
        },
        () => {
          this.updateComparableTransactions();
          this.loadTrendData();
          this.loadPropertyTrackersNearby();
          this.trackMyPropertyDetails();
          if (certifyListing) {
            this.directToManageProperty(certifyListing);
          }
          this.checkCertifiedListing();
        },
      );
    } else if (!ObjectUtil.isEmpty(ptUserId) || ptUserId > 0) {
      this.setState(
        {
          loadingState: LoadingState.Loading,
        },
        () => {
          this.loadTrackerPOWithPtUserId();
        },
      );
    }
  }

  componentDidUpdate(prevProps, prevState) {
    if (prevProps.properties !== this.props.properties) {
      const {trackerPO} = this.state;
      const {properties, ptUserId} = this.props;
      let ptUserIdToCheck = '';
      if (!ObjectUtil.isEmpty(trackerPO)) {
        ptUserIdToCheck = trackerPO.ptUserId;
      } else if (!ObjectUtil.isEmpty(ptUserId) || ptUserId > 0) {
        ptUserIdToCheck = ptUserId;
      }
      const result = properties.find(function(item) {
        return item.ptUserId == ptUserIdToCheck;
      });
      if (ObjectUtil.isEmpty(result)) {
        Navigation.pop(this.props.componentId);
      } else {
        this.setState(
          {
            trackerPO: result,
          },
          () => {
            this.updateComparableTransactions();
            this.loadTrendData();
            this.loadPropertyTrackersNearby();
          },
        );
      }
    }
  }

  componentDidDisappear() {
    this.setState({hideComponents: true});
  }
  componentDidAppear() {
    this.setState({hideComponents: false});
  }

  checkCertifiedListing() {
    const {trackerPO} = this.state;
    const {ownerNric, dateOwnerNricVerified} = trackerPO;
    if (
      !ObjectUtil.isEmpty(ownerNric) &&
      !ObjectUtil.isEmpty(dateOwnerNricVerified)
    ) {
      CertifiedListingService.findCertifiedListings({
        ptUserId: trackerPO.ptUserId,
      }).then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          if (!ObjectUtil.isEmpty(response.result)) {
            let result = response.result;
            let certifyListingCheck = false;
            for (let j = 0; j < result.length; j++) {
              if (result[j].ownerCertifiedInd == false) {
                certifyListingCheck = true;
                break;
              }
            }
            this.setState({certifyListingNavigation: certifyListingCheck});
          }
        }
      });
    }
  }

  trackMyPropertyDetails() {
    const {trackerPO} = this.state;
    const {ptUserId, postalCode} = trackerPO;
    if (!ObjectUtil.isEmpty(trackerPO) && ptUserId && postalCode) {
      TrackingService.saveLead({
        ptUserId,
        postalCode,
      });
    }
  }

  addRightButtons() {
    const componentId = this.props.componentId;

    return new Promise(function(resolve, reject) {
      Promise.all([
        FeatherIcon.getImageSource('settings', 24, 'white'),
        FeatherIcon.getImageSource('trash-2', 24, 'white'),
      ])
        .then(values => {
          Navigation.mergeOptions(componentId, {
            topBar: {
              rightButtons: [
                {
                  id: 'delete_Btn',
                  icon: values[1],
                  color: SRXColor.Teal,
                },
                {
                  id: 'manange_Btn',
                  icon: values[0],
                  color: SRXColor.Teal,
                  text: 'Manage',
                },
              ],
            },
          });
          resolve(true);
        })
        .catch(error => {
          console.log(error);
          reject(error);
        })
        .done();
    });
  }

  onDeleteProperty() {
    const {trackerPO} = this.state;
    Alert.alert(
      'Delete Property',
      'Once deleted, you will not be able to retrieve. Confirm to delete?',
      [
        {
          text: 'Cancel',
          style: 'cancel',
        },
        {
          text: 'Delete',
          onPress: () => this.deleteProperty(trackerPO),
          style: 'destructive',
        },
      ],
    );
  }

  deleteProperty(trackerPO) {
    this.props.removeTracker(trackerPO);
  }

  directToManageProperty = certifyListing => {
    const {trackerPO} = this.state;
    Navigation.push(this.props.componentId, {
      component: {
        name: 'MyProperties.AddUpdateMyProperty',
        passProps: {
          initiatingComponentId: this.props.componentId,
          propertyUserPO: trackerPO,
          onUpdateSRXPropertyPO: newTrackerPO => {
            this.setState({trackerPO: newTrackerPO});
          },
          certifyListing,
        },
      },
    });
  };

  directToListedListing({listingId, refType}) {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'PropertySearchStack.ListingDetails',
        passProps: {
          listingId,
          refType,
        },
      },
    });
  }

  navigationButtonPressed({buttonId}) {
    switch (buttonId) {
      case 'manange_Btn':
        this.directToManageProperty();
        break;
      case 'delete_Btn':
        this.onDeleteProperty();
        break;
      default:
        break;
    }
  }

  updateComparableTransactions() {
    const {trackerPO} = this.state;
    if (
      !ObjectUtil.isEmpty(trackerPO) &&
      PropertyTrackerUtil.showSaleInformation(trackerPO)
    ) {
      const {saleXvalue} = trackerPO;
      if (!ObjectUtil.isEmpty(saleXvalue)) {
        const {psfAdjustedTransactions} = saleXvalue;
        if (!ObjectUtil.isEmpty(psfAdjustedTransactions)) {
          const comparables = psfAdjustedTransactions.map(item => {
            let address = item.address;
            if (
              !ObjectUtil.isEmpty(item.unitFloor) &&
              !ObjectUtil.isEmpty(item.unitNo)
            ) {
              address += '#' + item.unitFloor + '-xx';
            }

            let size = StringUtil.formatThousand(item.size);
            if (PropertyTypeUtil.isHDB(item.cdResearchSubtype)) {
              size += ' sqm';
            } else {
              size += ' sqft';
            }

            let psf = StringUtil.formatThousand(item.psf, 0) + 'psf';

            let price = StringUtil.formatCurrency(item.transactedPrice);

            Moment.locale('en');
            let date = Moment(item.date.time).format('DD/MM/YYYY');
            value = date.toString();

            const newTransaction = {
              type: 'S',
              address,
              size,
              psf,
              price,
              dateSold: value,
            };

            return newTransaction;
          });
          this.setState({
            comparables,
          });
        }
      }
    }
  }

  loadTrackerPOWithPtUserId() {
    const {ptUserId, certifyListing} = this.props;
    PropertyTrackerService.loadPropertyTrackersById({
      ptUserId,
      populateXvalue: true,
    })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const errorMsg = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(errorMsg)) {
            //error
            Alert.alert(
              AlertMessage.ErrorMessageTitle,
              errorMsg,
              [
                {
                  text: 'OK',
                  onPress: () => Navigation.pop(this.props.componentId),
                },
              ],
              {
                cancelable: false,
              },
            );
          } else {
            const {result} = response;
            let trackerArray = [];
            if (!ObjectUtil.isEmpty(result) && Array.isArray(result)) {
              trackerArray = result.map(item => new SRXPropertyUserPO(item));
            }

            if (trackerArray.length >= 1) {
              //if more than 1, take 1st one
              //by right there should be only 1 result
              this.setState(
                {
                  trackerPO: trackerArray[0],
                  loadingState: LoadingState.Normal,
                },
                () => {
                  this.updateComparableTransactions();
                  this.loadTrendData();
                  this.loadPropertyTrackersNearby();
                  this.trackMyPropertyDetails();
                  if (certifyListing) {
                    this.directToManageProperty(certifyListing);
                  }
                },
              );
            } else {
              //no result
              Alert.alert(
                AlertMessage.ErrorMessageTitle,
                'Property not found.',
                [
                  {
                    text: 'OK',
                    onPress: () => Navigation.pop(this.props.componentId),
                  },
                ],
                {
                  cancelable: false,
                },
              );
            }
          }
        } else {
          //failed
          Alert.alert(
            AlertMessage.ErrorMessageTitle,
            'Failed to load the property. Please try again later.',
            [
              {
                text: 'OK',
                onPress: () => Navigation.pop(this.props.componentId),
              },
            ],
            {
              cancelable: false,
            },
          );
        }
      })
      .catch(error => {
        Alert.alert(
          AlertMessage.ErrorMessageTitle,
          'Failed to load the property. Please try again later.',
          [
            {
              text: 'OK',
              onPress: () => Navigation.pop(this.props.componentId),
            },
          ],
          {
            cancelable: false,
          },
        );
      });
  }

  loadPropertyTrackersNearby() {
    const {trackerPO} = this.state;
    if (!ObjectUtil.isEmpty(trackerPO)) {
      PropertyTrackerService.loadPropertyTrackersNearby({
        ptUserId: trackerPO.ptUserId,
      }).then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          let amenitiesArray = [];
          const {amenities} = response;
          amenities.map(item => {
            amenitiesArray.push(new AmenitiesCategoryPO(item));
          });
          response.amenities = amenitiesArray;
          this.setState({propertyAmenities: response});
        }
      });
    }
  }

  loadTrendData() {
    const {trackerPO} = this.state;
    if (!ObjectUtil.isEmpty(trackerPO)) {
      PropertyTrackerService.loadPropertyTrackerXValueTrend({
        ptUserId: trackerPO.ptUserId,
        isSale: PropertyTrackerUtil.showSaleInformation(trackerPO),
      }).then(resp => {
        if (!ObjectUtil.isEmpty(resp)) {
          const {xValueTrend} = resp;
          if (Array.isArray(xValueTrend)) {
            this.setState({
              xValueTrend,
            });
          }
        }
      });
    }
  }

  onTrackerPOUpdated(newTrackerPO) {
    const {trackerPO} = this.state;
    this.setState(
      {
        trackerPO: newTrackerPO,
      },
      () => {
        //update redux state
        this.props.updateTracker({
          originalTrackerPO: trackerPO,
          newTrackerPO,
        });
      },
    );
  }

  directToListingResult({isSale, isTransaction}) {
    const {trackerPO} = this.state;

    const title =
      'Nearby ' +
      (isSale ? 'Sale' : 'Rental') +
      ' ' +
      (isTransaction ? 'Transactions' : 'Listings');

    let subtypes;
    if (PropertyTypeUtil.isHDB(trackerPO.cdResearchSubtype)) {
      // PropertyTypeValueSet
      subtypes = Array.from(PropertyTypeValueSet.hdb).join(',');
    } else if (PropertyTypeUtil.isCondo(trackerPO.cdResearchSubtype)) {
      subtypes = Array.from(PropertyTypeValueSet.condo).join(',');
    } else if (PropertyTypeUtil.isLanded(trackerPO.cdResearchSubtype)) {
      subtypes = Array.from(PropertyTypeValueSet.landed).join(',');
    }

    console.log(subtypes);

    Navigation.push(this.props.componentId, {
      component: {
        name: 'PropertySearchStack.searchResult',
        passProps: {
          title,
          disabledSearchModification: true,
          disableGoogleAnalytic: true,
          initialSearchOptions: {
            type: isSale ? 'S' : 'R',
            isTransacted: isTransaction,
            searchText: trackerPO.postalCode,
            suggestionEntryType: 'POSTAL',
            cdResearchSubTypes: subtypes,
          },
          source: TransactedListingResultList.Sources.MyPropertyDetails,
          directorySource:
            TransactedListingSearchResultGridView.Sources.MyPropertyDetails,
        },
      },
    });
  }

  directToFindAgent() {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'ConciergeStack.homeScreen',
      },
    });
  }

  renderSeparator() {
    return (
      <Separator
        edgeInset={{top: 0, bottom: 0, left: Spacing.M, right: Spacing.M}}
      />
    );
  }

  renderInviterAgentInfo() {
    const {trackerPO} = this.state;
    return (
      <InviterAgencyInfo
        trackerPO={trackerPO}
        chatWithAgent={this.chatWithAgent}
      />
    );
  }

  chatWithAgent() {
    console.log(this.state);
    const {trackerPO} = this.state;
    const {inviterAgentPO} = trackerPO;
    const {minimalPO} = inviterAgentPO;

    let mobile = minimalPO.mobile.substring(1, 9);
    let userId = minimalPO.userId.toString();
    minimalPO.mobile = mobile;
    minimalPO.userId = userId;

    Navigation.push(this.props.componentId, {
      component: {
        name: 'ChatStack.chatRoom',
        passProps: {
          agentInfo: minimalPO,
        },
      },
    });
    console.log(mobile);
  }

  renderPropertyListedIndicator() {
    const {trackerPO, certifyListingNavigation} = this.state;
    return (
      <PropertyListedIndicator
        trackerPO={trackerPO}
        onViewListing={this.directToListedListing}
        onManageProperty={this.directToManageProperty}
        certifyListingNavigation={certifyListingNavigation}
      />
    );
  }

  renderXValues() {
    const {trackerPO} = this.state;
    return (
      <View style={{marginTop: Spacing.L}}>
        <MyPropertyValues trackerPO={trackerPO} />
        {this.renderSeparator()}
      </View>
    );
  }

  renderValueChanges() {
    const {trackerPO} = this.state;
    if (!ObjectUtil.isEmpty(trackerPO)) {
      let changesArray = [];
      const {saleValueChanges, rentValueChanges} = trackerPO;
      if (
        PropertyTrackerUtil.showSaleInformation(trackerPO) &&
        !ObjectUtil.isEmpty(saleValueChanges)
      ) {
        changesArray = saleValueChanges;
      } else if (!ObjectUtil.isEmpty(rentValueChanges)) {
        changesArray = rentValueChanges;
      }

      return changesArray.map(item => {
        return (
          <View>
            <TrackerValueChangesItem valueChangePO={item} />
            {this.renderSeparator()}
          </View>
        );
      });
    }
  }

  renderPropertyPSF() {
    const {trackerPO} = this.state;
    return (
      <View>
        <MyPropertyRegionalComparison trackerPO={trackerPO} />
        {this.renderSeparator()}
      </View>
    );
  }

  renderPurchaseDetails() {
    const {trackerPO} = this.state;
    return (
      <View>
        <PurchaseDetail
          trackerPO={trackerPO}
          onUpdate={this.onTrackerPOUpdated}
        />
        {this.renderSeparator()}
      </View>
    );
  }

  renderProjectInformation() {
    const {trackerPO} = this.state;
    return (
      <View>
        <ProjectInformation trackerPO={trackerPO} />
        {this.renderSeparator()}
      </View>
    );
  }

  renderAgentConnection() {
    return <ConnectAgent onConnectAgent={this.directToFindAgent} />;
  }

  renderNearbySaleListings() {
    return (
      <View style={{marginTop: -1}}>
        <Separator />
        <TouchableHighlight
          onPress={() =>
            this.directToListingResult({isSale: true, isTransaction: false})
          }>
          <View
            style={{
              padding: Spacing.M,
              flexDirection: 'row',
              alignItems: 'center',
              backgroundColor: SRXColor.AccordionBackground,
            }}>
            <BodyText style={{flex: 1, marginRight: Spacing.XS}}>
              Nearby Sale Listings
            </BodyText>
            <FeatherIcon
              name="chevron-right"
              size={20}
              color={SRXColor.Black}
            />
          </View>
        </TouchableHighlight>
        <Separator />
      </View>
    );
  }

  renderNearbyRentalListings() {
    return (
      <View style={{marginTop: -1}}>
        <Separator />
        <TouchableHighlight
          onPress={() =>
            this.directToListingResult({isSale: false, isTransaction: false})
          }>
          <View
            style={{
              padding: Spacing.M,
              flexDirection: 'row',
              alignItems: 'center',
              backgroundColor: SRXColor.White,
            }}>
            <BodyText style={{flex: 1, marginRight: Spacing.XS}}>
              Nearby Rental Listings
            </BodyText>
            <FeatherIcon
              name="chevron-right"
              size={20}
              color={SRXColor.Black}
            />
          </View>
        </TouchableHighlight>
        <Separator />
      </View>
    );
  }

  renderNearbySaleTransactions() {
    return (
      <View style={{marginTop: -1}}>
        <Separator />
        <TouchableHighlight
          onPress={() =>
            this.directToListingResult({isSale: true, isTransaction: true})
          }>
          <View
            style={{
              padding: Spacing.M,
              flexDirection: 'row',
              alignItems: 'center',
              backgroundColor: SRXColor.AccordionBackground,
            }}>
            <BodyText style={{flex: 1, marginRight: Spacing.XS}}>
              Nearby Sale Transactions
            </BodyText>
            <FeatherIcon
              name="chevron-right"
              size={20}
              color={SRXColor.Black}
            />
          </View>
        </TouchableHighlight>
        <Separator />
      </View>
    );
  }

  renderNearbyRentalTransactions() {
    return (
      <View style={{marginTop: -1}}>
        <Separator />
        <TouchableHighlight
          onPress={() =>
            this.directToListingResult({isSale: false, isTransaction: true})
          }>
          <View
            style={{
              padding: Spacing.M,
              flexDirection: 'row',
              alignItems: 'center',
              backgroundColor: SRXColor.White,
            }}>
            <BodyText style={{flex: 1, marginRight: Spacing.XS}}>
              Nearby Rental Transactions
            </BodyText>
            <FeatherIcon
              name="chevron-right"
              size={20}
              color={SRXColor.Black}
            />
          </View>
        </TouchableHighlight>
        <Separator />
      </View>
    );
  }

  renderTrendGraph() {
    const {xValueTrend} = this.state;
    if (!ObjectUtil.isEmpty(xValueTrend)) {
      return (
        <View>
          <XTrendGraph
            xValueTrend={xValueTrend}
            allowPeriods={[
              XTrendGraph.PeriodOptions.fiveYears,
              XTrendGraph.PeriodOptions.threeYears,
              XTrendGraph.PeriodOptions.oneYear,
            ]}
          />
          <Separator />
        </View>
      );
    }
  }

  renderXValueDisclaimer() {
    return (
      <View>
        <View
          style={{
            paddingLeft: Spacing.M,
            paddingRight: Spacing.M,
            marginTop: Spacing.M,
            marginBottom: Spacing.M,
          }}>
          <XValueDisclaimer />
        </View>
        <Separator />
      </View>
    );
  }

  renderAmenities() {
    const {propertyAmenities, trackerPO} = this.state;
    if (
      !ObjectUtil.isEmpty(trackerPO) &&
      !ObjectUtil.isEmpty(propertyAmenities)
    ) {
      return (
        <Amenities
          locationTitle={trackerPO.address}
          latitude={trackerPO.latitude}
          longitude={trackerPO.longitude}
          showFuzzyLocation={false}
          componentId={this.props.componentId}
          amenitiesGroups={propertyAmenities.amenities}
          nearbyUsers={propertyAmenities.nearbyUsers}
          source={AmenitiesSource.MyPropertyDetails}
        />
      );
    }
  }

  renderTransactions() {
    const {comparables} = this.state;
    if (!ObjectUtil.isEmpty(comparables)) {
      return (
        <View style={{backgroundColor: SRXColor.AccordionBackground}}>
          <Transactions data={comparables} />
          <Separator />
        </View>
      );
    }
  }

  getScreenTitle() {
    const {trackerPO} = this.state;
    if (!ObjectUtil.isEmpty(trackerPO)) {
      return trackerPO.getFullAddress();
    }
    return '';
  }

  render() {
    const {loadingState, hideComponents} = this.state;
    if (loadingState === LoadingState.Loading) {
      return (
        <SafeAreaView
          style={{flex: 1, justifyContent: 'center', alignItems: 'center'}}>
          <ActivityIndicator />
        </SafeAreaView>
      );
    } else {
      return (
        <SafeAreaView style={{flex: 1}}>
          <KeyboardAwareScrollView
            style={{flex: 1, backgroundColor: SRXColor.White}}
            keyboardShouldPersistTaps={'always'}
            onScroll={this.onScroll}>
            {this.renderLargeTitle(this.getScreenTitle())}
            {this.renderInviterAgentInfo()}
            {this.renderPropertyListedIndicator()}
            {this.renderXValues()}
            {this.renderValueChanges()}
            {this.renderPropertyPSF()}
            {this.renderPurchaseDetails()}
            {this.renderProjectInformation()}
            {hideComponents == false ? this.renderTrendGraph() : null}
            {this.renderXValueDisclaimer()}
            {hideComponents == false ? this.renderAmenities() : null}
            {this.renderTransactions()}
            {hideComponents == false ? (
              <View>
                {this.renderNearbySaleListings()}
                {this.renderNearbyRentalListings()}
                {this.renderNearbySaleTransactions()}
                {this.renderNearbyRentalTransactions()}
              </View>
            ) : null}
            {this.renderAgentConnection()}
          </KeyboardAwareScrollView>
        </SafeAreaView>
      );
    }
  }
}

MyPropertyDetails.propTypes = {
  initialTrackerPO: PropTypes.instanceOf(SRXPropertyUserPO),
  /**
   * if cannot provide initialTrackerPO,
   * then must provide ptUserId
   *
   * Either one is required to display this screen
   */
  ptUserId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  //certifyListing is optional, to define whether to navigate to manageProperty
  certifyListing: PropTypes.bool,
};

const mapStateToProps = state => {
  return {
    properties: state.myPropertiesData.list,
  };
};

export default connect(
  mapStateToProps,
  {updateTracker, removeTracker},
)(MyPropertyDetails);
