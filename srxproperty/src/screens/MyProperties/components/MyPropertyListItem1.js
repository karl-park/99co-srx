import React, {Component} from 'react';
import {
  View,
  Text,
  Image,
  TouchableOpacity,
  ActivityIndicator,
} from 'react-native';
import {
  ExtraSmallBodyText,
  SmallBodyText,
  EntypoIcon,
  TouchableHighlight,
  BodyText,
} from '../../../components';
import {Spacing} from '../../../styles';
import {StringUtil, ObjectUtil} from '../../../utils';
import {SRXColor, pt_Occupancy, LoadingState} from '../../../constants';
import PropTypes from 'prop-types';
import {SRXPropertyUserPO} from '../../../dataObject';
import {PropertyTrackerUtil} from '../Details/utils';
import {CertifiedListingService} from '../../../services';
import {MyProperties_PurpleTag} from '../../../assets';

class MyPropertyListItem1 extends Component {
  static propTypes = {
    srxPropertyUserPOs: PropTypes.arrayOf(
      PropTypes.instanceOf(SRXPropertyUserPO),
    ),
    loadingState: PropTypes.string,
    directToMyProperty: PropTypes.func,
  };

  static defaultProps = {
    srxPropertyUserPOs: [],
  };

  constructor(props) {
    super(props);
    this.onSelect = this.onSelect.bind(this);
    this.state = {
      hasRentListing: false,
      hasSaleListing: false,
      isRentCertified: true,
      isSaleCertified: true,
    };
  }

  componentDidMount() {
    const {srxPropertyUserPO} = this.props;
    console.log('SRXPROPertyUSERPO');
    console.log(srxPropertyUserPO);
    const {occupancy} = srxPropertyUserPO;
    if (occupancy === pt_Occupancy.Own) {
      CertifiedListingService.findListedListings({
        ptUserId: srxPropertyUserPO.ptUserId,
      }).then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const {rentListings, saleListings} = response;
          var hasRentListing = false;
          var hasSaleListing = false;
          var isRentCertified = true;
          var isSaleCertified = true;
          if (rentListings.length > 0) {
            hasRentListing = true;
          }

          if (saleListings.length > 0) {
            hasSaleListing = true;
          }
          for (var i = 0; i < rentListings.length; i++) {
            let listing = rentListings[i];
            if (!listing.ownerCertifiedInd) {
              isRentCertified = false;
              break;
            }
          }

          for (var i = 0; i < saleListings.length; i++) {
            let listing = saleListings[i];
            if (!listing.ownerCertifiedInd) {
              isSaleCertified = false;
              break;
            }
          }
        }

        this.setState({
          isSaleCertified,
          isRentCertified,
          hasSaleListing,
          hasRentListing,
        });
        // if (!ObjectUtil.isEmpty(response)) {
        //   if (!ObjectUtil.isEmpty(response.result)) {
        //     let result = response.result;
        //   }
        // } else {
        //   return;
        // }
        console.log('Check Property List Item 1 response');
        console.log(response);
        // this.setState({})
      });
    }
  }

  onSelect() {
    const {srxPropertyUserPO, directToMyProperty, loadingState} = this.props;
    console.log('MyPropertyListItem ' + loadingState);
    if (directToMyProperty && loadingState === LoadingState.Loaded) {
      directToMyProperty(srxPropertyUserPO);
    }
  }

  renderAddress() {
    const {srxPropertyUserPO} = this.props;
    return (
      <ExtraSmallBodyText
        style={{overflow: 'hidden', flex: 8, lineHeight: 16}}
        numberOfLines={2}>
        {srxPropertyUserPO.getFullAddress()}
      </ExtraSmallBodyText>
    );
  }

  renderXValueAndLastMonthChanges() {
    const {loadingState} = this.props;
    if (loadingState === LoadingState.Loading) {
      return (
        <View
          style={{
            alignItems: 'center',
            flex: 7,
            paddingTop: Spacing.XS,
            paddingBottom: Spacing.XS,
          }}>
          <ActivityIndicator />
        </View>
      );
    } else if (loadingState === LoadingState.Loaded) {
      return (
        <View style={{flexDirection: 'row', flex: 7}}>
          {this.renderXValue()}
          {this.renderLastMonthChanges()}
        </View>
      );
    }
  }

  renderXValue() {
    const {srxPropertyUserPO} = this.props;
    var title, value;

    if (!PropertyTrackerUtil.showSaleInformation(srxPropertyUserPO)) {
      title = 'Rental X-Value';
      value = srxPropertyUserPO.getFormattedRentXValue();
    } else {
      title = 'Sale X-Value';
      value = srxPropertyUserPO.getFormattedSaleXValue();
    }
    return (
      <View style={{flex: 4}}>
        <SmallBodyText style={{color: SRXColor.Purple, textAlign: 'center'}}>
          {value ? value : '-'}
        </SmallBodyText>
        <Text style={{fontSize: 10, color: SRXColor.Gray, textAlign: 'center'}}>
          {title}
        </Text>
      </View>
    );
  }

  renderLastMonthChanges() {
    const {srxPropertyUserPO} = this.props;
    var difference;
    var formattedDiff = '';
    if (!PropertyTrackerUtil.showSaleInformation(srxPropertyUserPO)) {
      difference = srxPropertyUserPO.getLastMonthRentXValueChangesInPercent();
      if (!isNaN(difference)) {
        formattedDiff =
          StringUtil.formatThousand(Math.abs(difference), 2) + '%';
      }
    } else {
      difference = srxPropertyUserPO.getLastMonthSaleXValueChangesInPercent();
      if (!isNaN(difference)) {
        formattedDiff =
          StringUtil.formatThousand(Math.abs(difference), 2) + '%';
      }
    }

    return (
      <View style={{flex: 3}}>
        <View style={{flexDirection: 'row', flex: 1, alignSelf: 'center'}}>
          <SmallBodyText style={{color: SRXColor.Purple}}>
            {formattedDiff ? formattedDiff : '-'}
          </SmallBodyText>
          {this.renderLastMonthChangesIndicator(difference)}
        </View>
        <Text style={{fontSize: 10, color: SRXColor.Gray, textAlign: 'center'}}>
          Last month
        </Text>
      </View>
    );
  }

  renderLastMonthChangesIndicator(difference) {
    if (difference > 0) {
      return (
        <EntypoIcon
          name={'triangle-up'}
          size={16}
          color={SRXColor.Green}
          style={{minWidth: 16}}
        />
      );
    } else if (difference < 0) {
      return (
        <EntypoIcon
          name={'triangle-down'}
          size={16}
          color={SRXColor.Red}
          style={{minWidth: 16}}
        />
      );
    }
  }

  directToAddUpdateProperty = () => {
    const {srxPropertyUserPO, directToAddUpdateProperty} = this.props;
    if (directToAddUpdateProperty) {
      directToAddUpdateProperty(srxPropertyUserPO);
    }
  };

  renderListingStatus() {
    const {hasRentListing, hasSaleListing} = this.state;
    const {loadingState} = this.props;

    if (hasRentListing || hasSaleListing) {
      if (loadingState === LoadingState.Loaded) {
        return (
          <TouchableHighlight onPress={() => this.directToAddUpdateProperty()}>
            {this.renderListingStatusContent()}
          </TouchableHighlight>
        );
      } else if (loadingState === LoadingState.Loading) {
        return <View>{this.renderListingStatusContent()}</View>;
      }
      // return (
      //   <View
      //     style={{
      //       padding: Spacing.XS,
      //       borderTopWidth: 1,
      //       borderTopColor: '#e0e0e0',
      //     }}>
      //     <View style={{alignItems: 'center'}}>
      //       <TouchableOpacity onPress={() => this.directToAddUpdateProperty()}>
      //         <View style={{flexDirection: 'row'}}>
      //           <View style={{flexDirection: 'row'}}>
      //             <Image
      //               source={MyProperties_PurpleTag}
      //               style={{height: 15, width: 15, marginRight: Spacing.XS}}
      //             />
      //             <ExtraSmallBodyText style={{color: SRXColor.Purple}}>
      //               {indicatorText}
      //             </ExtraSmallBodyText>
      //           </View>
      //           {isOwnerVerified && !isAllCertified ? (
      //             <ExtraSmallBodyText>
      //               <ExtraSmallBodyText> | </ExtraSmallBodyText>
      //               <ExtraSmallBodyText style={{color: SRXColor.Teal}}>
      //                 Certify listing
      //               </ExtraSmallBodyText>
      //             </ExtraSmallBodyText>
      //           ) : null}
      //         </View>
      //       </TouchableOpacity>
      //       {isOwnerVerified ? null : (
      //         <TouchableOpacity
      //           onPress={() => this.directToAddUpdateProperty()}>
      //           <ExtraSmallBodyText
      //             style={{color: SRXColor.Teal, marginTop: Spacing.XS}}>
      //             Verify Ownership to find out by who!
      //           </ExtraSmallBodyText>
      //         </TouchableOpacity>
      //       )}
      //     </View>
      //   </View>
      // );
    } else {
      return <View />;
    }
  }

  renderListingStatusContent() {
    const {
      hasRentListing,
      hasSaleListing,
      isRentCertified,
      isSaleCertified,
    } = this.state;
    const {srxPropertyUserPO} = this.props;

    var indicatorText = '';
    var isAllCertified = true;
    if (hasRentListing && hasSaleListing) {
      indicatorText = 'This property is listed for sale and rent';
      isAllCertified = isRentCertified && isSaleCertified;
    } else if (hasSaleListing) {
      indicatorText = 'This property is listed for sale';
      isAllCertified = isSaleCertified;
    } else if (hasRentListing) {
      indicatorText = 'This property is listed for rent';
      isAllCertified = isRentCertified;
    }

    var isOwnerVerified =
      !ObjectUtil.isEmpty(srxPropertyUserPO.ownerNric) ||
      !ObjectUtil.isEmpty(srxPropertyUserPO.dateOwnerNricVerified);

    return (
      <View style={Styles.listingStatusContainer}>
        <View style={{alignItems: 'center'}}>
          <View style={{flexDirection: 'row'}}>
            <View style={{flexDirection: 'row'}}>
              <Image
                source={MyProperties_PurpleTag}
                style={{height: 15, width: 15, marginRight: Spacing.XS}}
              />
              <ExtraSmallBodyText style={{color: SRXColor.Purple}}>
                {indicatorText}
              </ExtraSmallBodyText>
            </View>

            {isOwnerVerified && !isAllCertified ? (
              <ExtraSmallBodyText>
                <ExtraSmallBodyText> | </ExtraSmallBodyText>
                <ExtraSmallBodyText style={{color: SRXColor.Teal}}>
                  Certify listing
                </ExtraSmallBodyText>
              </ExtraSmallBodyText>
            ) : null}
          </View>
          {isOwnerVerified ? null : (
            <ExtraSmallBodyText
              style={{color: SRXColor.Teal, marginTop: Spacing.XS}}>
              Verify Ownership to find out by who!
            </ExtraSmallBodyText>
          )}
        </View>
      </View>
    );
  }

  renderMainContent() {
    return (
      <View
        style={{
          flex: 1,
          backgroundColor: SRXColor.White,
          padding: Spacing.XS,
        }}>
        <View style={Styles.myPropertylistItemContainer}>
          {this.renderAddress()}
          {this.renderXValueAndLastMonthChanges()}
        </View>
      </View>
    );
  }

  render() {
    const {srxPropertyUserPO, loadingState} = this.props;
    if (!ObjectUtil.isEmpty(srxPropertyUserPO)) {
      //Note: if tap cell item many times while loading state,
      // show details screen many times after loaded state.
      //need to separate touchablehightlight and view depend on state
      return (
        <View style={Styles.continer}>
          <View style={Styles.propertyListMainItemContainer}>
            {loadingState === LoadingState.Loaded ? (
              <TouchableHighlight style={{flex: 1}} onPress={this.onSelect}>
                {this.renderMainContent()}
              </TouchableHighlight>
            ) : (
              <View style={{flex: 1}}>{this.renderMainContent()}</View>
            )}
            {this.renderListingStatus()}
          </View>
        </View>
      );
    }
    return <View />;
  } //end of render
}

const Styles = {
  continer: {
    borderRadius: Spacing.XS / 2,
    //Shadow for iOS
    shadowColor: 'rgb(110,129,154)',
    shadowOffset: {height: 1, width: 0},
    shadowOpacity: 0.32,
    shadowRadius: 1,
    marginBottom: Spacing.XS,
    backgroundColor: SRXColor.White,
  },
  myPropertylistItemContainer: {
    flex: 1,
    flexDirection: 'row',
    // overflow: 'hidden',
    //Padding
    // padding: Spacing.XS,
    //Border
    // borderRadius: Spacing.XS / 2,
    // borderWidth: 1,
    // borderColor: '#e0e0e0',
    //alignment
    justifyContent: 'center',
    alignItems: 'center',
    //background
    backgroundColor: SRXColor.White,
  },
  propertyListMainItemContainer: {
    // padding: Spacing.XS/2,
    // Border
    borderRadius: Spacing.XS / 2,
    borderWidth: 1,
    borderColor: '#e0e0e0',
    backgroundColor: SRXColor.White,
    overflow: 'hidden',
  },
  listingStatusContainer: {
    padding: Spacing.XS,
    borderTopWidth: 1,
    borderTopColor: '#e0e0e0',
    backgroundColor: SRXColor.White,
  },
};

export {MyPropertyListItem1};
