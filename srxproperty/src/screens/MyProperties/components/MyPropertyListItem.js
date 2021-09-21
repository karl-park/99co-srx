import React, {Component} from 'react';
import {View, Image, StyleSheet, TouchableOpacity} from 'react-native';
import PropTypes from 'prop-types';
import {
  TouchableHighlight,
  Heading2,
  Heading2_Currency,
  SmallBodyText,
  ExtraSmallBodyText,
  Button,
  Separator,
  FeatherIcon,
  EntypoIcon,
  Text,
} from '../../../components';
import {SRXPropertyUserPO} from '../../../dataObject';
import {ObjectUtil, StringUtil} from '../../../utils';
import {Spacing} from '../../../styles';
import {SRXColor, pt_Occupancy, pt_Purpose} from '../../../constants';
import {PropertyTrackerUtil} from '../Details/utils';
import {CertifiedListingService} from '../../../services';
import {MyProperties_PurpleTag} from '../../../assets';

class MyPropertyListItem extends Component {
  constructor(props) {
    super(props);
    this.onPressDelete = this.onPressDelete.bind(this);
    this.onPressManage = this.onPressManage.bind(this);
    this.onSelect = this.onSelect.bind(this);
    this.state = {
      hasRentListing: false,
      hasSaleListing: false,
      isRentCertified: true,
      isSaleCertified: true,
    };
  }

  componentDidMount() {
    const {trackerPO} = this.props;
    const occupancy = trackerPO.getOccupancy();
    if (occupancy === pt_Occupancy.Own) {
      CertifiedListingService.findListedListings({
        ptUserId: trackerPO.ptUserId,
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

  onPressDelete() {
    const {trackerPO, onDelete} = this.props;
    if (onDelete) {
      onDelete(trackerPO);
    }
  }

  onPressManage() {
    const {trackerPO, onManage} = this.props;
    if (onManage) {
      onManage(trackerPO);
    }
  }

  onVerifyOrCertify() {
    const {trackerPO, onVerifyOrCertify} = this.props;
    if (onVerifyOrCertify) {
      onVerifyOrCertify(trackerPO);
    }
  }

  onSelect() {
    const {trackerPO, onItemSelected} = this.props;
    if (onItemSelected) {
      onItemSelected(trackerPO);
    }
  }

  renderXValue() {
    const {trackerPO} = this.props;
    var title, value;
    if (!PropertyTrackerUtil.showSaleInformation(trackerPO)) {
      title = 'Rental X-Value';
      value = trackerPO.getFormattedRentXValue();
    } else {
      title = 'Sale X-Value';
      value = trackerPO.getFormattedSaleXValue();
    }
    return (
      <View style={{flex: 5, marginBottom: Spacing.M}}>
        <ExtraSmallBodyText style={{color: SRXColor.Gray}}>
          {title}
        </ExtraSmallBodyText>
        <Heading2_Currency>{value}</Heading2_Currency>
      </View>
    );
  }

  renderLastMonthChanges() {
    //to be updated once backend provide this
    const {trackerPO} = this.props;
    var difference;
    var formattedDiff = '';
    if (!PropertyTrackerUtil.showSaleInformation(trackerPO)) {
      difference = trackerPO.getLastMonthRentXValueChangesInPercent();
      if (!isNaN(difference)) {
        formattedDiff =
          StringUtil.formatThousand(Math.abs(difference), 2) + '%';
      }
    } else {
      difference = trackerPO.getLastMonthSaleXValueChangesInPercent();
      if (!isNaN(difference)) {
        formattedDiff =
          StringUtil.formatThousand(Math.abs(difference), 2) + '%';
      }
    }

    return (
      <View style={{flex: 4, marginBottom: Spacing.M}}>
        <ExtraSmallBodyText style={{color: SRXColor.Gray}}>
          Last Month
        </ExtraSmallBodyText>
        <View style={{flexDirection: 'row'}}>
          <Heading2_Currency style={{color: SRXColor.Purple}}>
            {formattedDiff}
          </Heading2_Currency>
          {this.renderLastMonthChangesIndicator(difference)}
        </View>
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
          style={{flex: 1, minWidth: 16}}
        />
      );
    } else if (difference < 0) {
      return (
        <EntypoIcon
          name={'triangle-down'}
          size={16}
          color={SRXColor.Red}
          style={{flex: 1, minWidth: 16}}
        />
      );
    }
  }

  renderOfferIndicator() {
    const {trackerPO} = this.props;
    var indicatorText = '';
    // if (for some reason it is listed for sale) {
    //   indicatorText = "For Sale";
    // } else if (for some reason it is listed for rent) {
    //   indicatorText = "For Rent";
    // }

    if (!ObjectUtil.isEmpty(indicatorText)) {
      return (
        <View style={{alignItems: 'center'}}>
          <Text
            style={{
              color: SRXColor.Red,
              fontWeight: 'bold',
              fontSize: 12,
              paddingVertical: 5,
              paddingHorizontal: 7,
              borderWidth: 1,
              borderColor: SRXColor.LightGray,
            }}>
            {indicatorText}
          </Text>
          <View
            style={{
              width: 1,
              height: 19,
              backgroundColor: SRXColor.LightGray,
            }}
          />
        </View>
      );
    }
  }

  renderTrackerInfo() {
    /**
     * |                             343                                  |
     * |(12)|(xvalue, flex: 4)|(16)|(changes, flex: 5)|(16)|(box, 69)|(12)|
     */
    return (
      <View
        style={{
          flexDirection: 'row',
          justifyContent: 'space-between',
          alignItems: 'flex-end',
        }}>
        {this.renderXValue()}
        {/* separator */}
        <View style={{width: Spacing.M}} />
        {this.renderLastMonthChanges()}
        {/** separator */}
        <View style={{width: Spacing.M}} />
        {/** for sale/rent for future version, always occupy the space for consistency */}
        {/* <View style={{ width: 69 }}>{this.renderOfferIndicator()}</View> */}
        <View style={{width: 69}} />
      </View>
    );
  }

  renderAddress() {
    const {trackerPO} = this.props;
    return (
      <SmallBodyText style={{marginBottom: Spacing.XS / 2}}>
        {trackerPO.getFullAddress()}
      </SmallBodyText>
    );
  }

  renderOccupancy() {
    const {trackerPO} = this.props;
    const occupancy = trackerPO.getOccupancy();
    const purpose = trackerPO.getPurpose();
    var occupancyDisplay;
    if (occupancy === pt_Occupancy.Own && purpose === pt_Purpose.Residence) {
      occupancyDisplay = 'My Residence';
    } else if (
      occupancy === pt_Occupancy.Own &&
      purpose === pt_Purpose.Investment
    ) {
      occupancyDisplay = 'My Investment Property';
    } else if (occupancy === pt_Occupancy.Interested) {
      occupancyDisplay = 'Property of Interest';
    } else if (occupancy === pt_Occupancy.Rented) {
      occupancyDisplay = 'My Rented Property';
    }

    if (!ObjectUtil.isEmpty(occupancyDisplay)) {
      return <Heading2 style={{flex: 1}}>{occupancyDisplay}</Heading2>;
    }
  }

  renderOwnerVerifiedIndicator() {
    const {trackerPO} = this.props;
    const {ownerNric, dateOwnerNricVerified} = trackerPO;
    const occupancy = trackerPO.getOccupancy();

    const verified =
      !ObjectUtil.isEmpty(ownerNric) &&
      !ObjectUtil.isEmpty(dateOwnerNricVerified);

    if (occupancy === pt_Occupancy.Own) {
      if (verified) {
        return (
          <View style={{flexDirection: 'row'}}>
            <SmallBodyText style={{color: SRXColor.Teal}}>
              Verified
            </SmallBodyText>
          </View>
        );
      } else {
        return (
          <SmallBodyText style={{color: SRXColor.Orange}}>
            Not Verified
          </SmallBodyText>
        );
      }
    }
  }

  renderOccupancyAndOwnerVerification() {
    return (
      <View style={{flexDirection: 'row', marginBottom: Spacing.XS}}>
        {this.renderOccupancy()}
        {this.renderOwnerVerifiedIndicator()}
      </View>
    );
  }
  renderContent() {
    return (
      <View
        style={{
          paddingHorizontal: Spacing.S,
          paddingTop: Spacing.M,
        }}>
        {this.renderOccupancyAndOwnerVerification()}
        {this.renderAddress()}
        {this.renderTrackerInfo()}
      </View>
    );
  }

  renderDeleteBtn() {
    return (
      <Button
        buttonStyle={{flex: 1, height: 43, justifyContent: 'center'}}
        textStyle={{color: SRXColor.Red}}
        leftView={
          <FeatherIcon
            name={'minus-circle'}
            size={20}
            color={SRXColor.Red}
            style={{marginRight: Spacing.XS}}
          />
        }
        onPress={this.onPressDelete}>
        Delete
      </Button>
    );
  }

  renderManageBtn() {
    return (
      <Button
        buttonStyle={{flex: 1, height: 43, justifyContent: 'center'}}
        textStyle={{color: SRXColor.Teal}}
        leftView={
          <FeatherIcon
            name={'settings'}
            size={20}
            color={SRXColor.Teal}
            style={{marginRight: Spacing.XS}}
          />
        }
        onPress={this.onPressManage}>
        Manage
      </Button>
    );
  }

  renderButtons() {
    return (
      <View style={{flexDirection: 'row'}}>
        {this.renderDeleteBtn()}
        <View
          style={{
            backgroundColor: '#e0e0e0',
            width: 1,
          }}
        />
        {this.renderManageBtn()}
      </View>
    );
  }

  renderListingStatus() {
    const {
      hasRentListing,
      hasSaleListing,
      isRentCertified,
      isSaleCertified,
    } = this.state;
    const {trackerPO} = this.props;

    if (hasRentListing || hasSaleListing) {
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
        !ObjectUtil.isEmpty(trackerPO.ownerNric) ||
        !ObjectUtil.isEmpty(trackerPO.dateOwnerNricVerified);
      return (
        // <View
        //   style={{
        //     padding: Spacing.XS,
        //     borderTopWidth: 1,
        //     borderTopColor: '#e0e0e0',
        //   }}>
        //   <View style={{alignItems: 'center'}}>
        //   <TouchableOpacity onPress={() => this.onVerifyOrCertify()}>
        //     <View style={{flexDirection: 'row'}}>
        //       <View style={{flexDirection: 'row'}}>
        //         <Image
        //           source={MyProperties_PurpleTag}
        //           style={{height: 15, width: 15, marginRight: Spacing.XS}}
        //         />
        //         <ExtraSmallBodyText style={{color: SRXColor.Purple}}>
        //           {indicatorText}
        //         </ExtraSmallBodyText>
        //       </View>

        //       {isOwnerVerified && !isAllCertified ? (

        //           <ExtraSmallBodyText>
        //             <ExtraSmallBodyText> | </ExtraSmallBodyText>
        //             <ExtraSmallBodyText style={{color: SRXColor.Teal}}>
        //               Certify listing
        //             </ExtraSmallBodyText>
        //           </ExtraSmallBodyText>

        //       ) : null}
        //     </View>
        //     </TouchableOpacity>
        //     {isOwnerVerified ? null : (
        //       <TouchableOpacity onPress={() => this.onVerifyOrCertify()}>
        //         <ExtraSmallBodyText
        //           style={{color: SRXColor.Teal, marginTop: Spacing.XS}}>
        //           Verify Ownership to find out by who!
        //         </ExtraSmallBodyText>
        //       </TouchableOpacity>
        //     )}
        //   </View>
        // </View>
        <TouchableHighlight onPress={() => this.onVerifyOrCertify()}>
          <View
            style={{
              padding: Spacing.XS,
              borderTopWidth: 1,
              borderTopColor: '#e0e0e0',
              backgroundColor: SRXColor.White,
            }}>
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
        </TouchableHighlight>
      );
    } else {
      return <View />;
    }
  }

  render() {
    return (
      <View
        style={{
          paddingHorizontal: Spacing.M,
          backgroundColor: SRXColor.White,
        }}>
        <TouchableHighlight
          style={{
            borderRadius: 5,
            borderWidth: 1,
            borderColor: '#e0e0e0',
            marginBottom: Spacing.M,
          }}
          onPress={this.onSelect}>
          <View
            style={{
              backgroundColor: SRXColor.White,
              borderRadius: 5,
              shadowColor: 'rgb(110,129,154)',
              shadowOffset: {width: 1, height: 2},
              shadowOpacity: 0.32,
              shadowRadius: 1,
            }}>
            {this.renderContent()}

            {this.renderListingStatus()}
            <Separator />
            {this.renderButtons()}
          </View>
        </TouchableHighlight>
      </View>
    );
  }
}

MyPropertyListItem.propTypes = {
  trackerPO: PropTypes.instanceOf(SRXPropertyUserPO).isRequired,
  /**
   * function to call when delete btn pressed
   */
  onDelete: PropTypes.func,
  /**
   * function to call when manage btn pressed
   */
  onManage: PropTypes.func,
  /**
   * function to call when pressed
   * will provide trackerPO
   */
  onItemSelected: PropTypes.func,
};

export {MyPropertyListItem};
