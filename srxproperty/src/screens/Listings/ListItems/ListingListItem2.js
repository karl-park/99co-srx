import React, {Component} from 'react';
import {
  StyleSheet,
  Image,
  View,
  Text,
  ImageBackground,
  Alert,
  Platform,
  PermissionsAndroid,
  Linking,
} from 'react-native';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';

import {
  Heading2,
  Heading2_Currency,
  SmallBodyText,
  ExtraSmallBodyText,
  FeatherIcon,
  FontAwesomeIcon,
  Separator,
  Button,
  TouchableHighlight,
  LottieView,
} from '../../../components';
import {LoginStack} from '../../../config';
import {
  SRXColor,
  AlertMessage,
  LeadTypes,
  LeadSources,
} from '../../../constants';
import {ListingPO} from '../../../dataObject';
import {Spacing, CheckboxStyles} from '../../../styles';
import {
  ObjectUtil,
  CommonUtil,
  ShortlistUtil,
  PermissionUtil,
  GoogleAnalyticUtil,
} from '../../../utils';
import {
  Listings_BathTubIcon,
  Listings_BedIcon,
  Listing_CertifiedIconV2,
  Listing_Video_Call,
  Placeholder_Agent,
  Placeholder_General,
} from '../../../assets';
import {
  shortlistListing,
  removeShortlist,
  saveViewedListings,
} from '../../../actions';
import {ShortlistedAgent} from '../../Shortlist';
import {TrackingService} from '../../../services';

const isIOS = Platform.OS === 'ios';
class ListingListItem2 extends Component {
  static propTypes = {
    listingPO: PropTypes.instanceOf(ListingPO),
    removeFromSelectedListingList: PropTypes.func,
  };

  constructor(props) {
    super(props);
  }

  trackContactAgentActionsForListing(type) {
    /**
     * type
     * C - call, S - Sms, W - whatsapp
     */
    const {listingPO} = this.props;
    if (
      !ObjectUtil.isEmpty(listingPO) &&
      !ObjectUtil.isEmpty(listingPO.getListingId())
    ) {
      TrackingService.trackListingCallSMSOrWhatsapp({
        listingId: listingPO.getListingId(),
        type,
      }); //no need response
    }
  }

  isListingSelected = listingPO => {
    const {selectedListingList} = this.props;
    let isSelected = false;
    if (
      !ObjectUtil.isEmpty(selectedListingList) &&
      !ObjectUtil.isEmpty(listingPO)
    ) {
      if (selectedListingList.length > 0) {
        if (selectedListingList.includes(listingPO)) {
          isSelected = true;
        }
      }
    }
    return isSelected;
  };

  onSelectedEnquiryList = () => {
    const {onSelectedEnquiryList, listingPO} = this.props;
    if (onSelectedEnquiryList) {
      onSelectedEnquiryList(listingPO, true);
    }
  };

  removeFromSelectedListingList = () => {
    const {removeFromSelectedListingList, listingPO} = this.props;
    if (removeFromSelectedListingList) {
      removeFromSelectedListingList(listingPO);
    }
  };

  onEnquiryNowPressed = () => {
    const {listingPO, onEnquireListing} = this.props;
    onEnquireListing(listingPO);
  };

  onPressShortList = () => {
    const {listingPO, isShowShortList, userPO, shortListedTab} = this.props;
    const isShortlist = ShortlistUtil.isShortlist({
      listingId: listingPO.getListingId(),
    });
    if (isShortlist) {
      //REMOVE
      if (shortListedTab) {
        //Show dialog if from shortlist tab
        Alert.alert(
          AlertMessage.SuccessMessageTitle,
          listingPO.getListingDetailTitle() +
            ' will be removed from my shortlist',
          [
            {text: 'Cancel', style: 'cancel'},
            {
              text: 'Remove',
              style: 'destructive',
              onPress: () => {
                const shortlistPO = ShortlistUtil.getShortlistPOWithListingId({
                  listingId: listingPO.getListingId(),
                });
                if (!ObjectUtil.isEmpty(shortlistPO)) {
                  const {id} = shortlistPO;
                  this.removeFromSelectedListingList();
                  this.props.removeShortlist({shortlistId: id});
                }
                if (isShowShortList) {
                  isShowShortList(false);
                }
              },
            },
          ],
        );
      } else {
        //hide short list
        const shortlistPO = ShortlistUtil.getShortlistPOWithListingId({
          listingId: listingPO.getListingId(),
        });
        if (!ObjectUtil.isEmpty(shortlistPO)) {
          const {id} = shortlistPO;
          this.props.removeShortlist({shortlistId: id});
        }
        if (isShowShortList) {
          isShowShortList(false);
        }
      }
    } else {
      //SAVE
      this.props.shortlistListing({
        encryptedListingId: listingPO.encryptedId,
        listingType: listingPO.listingType,
        listingPO: listingPO,
      });
      //save to short list
      if (isShowShortList) {
        isShowShortList(true);
      }
    }
  };

  onPressShowListingDetails = () => {
    const {listingPO, saveViewedListings, onSelectListing} = this.props;
    //call redux method
    saveViewedListings({newListingId: listingPO.getListingId()});
    onSelectListing(listingPO);
  };

  callAgentClicked() {
    if (isIOS) {
      this.callAgent();
    } else {
      this.requestAndroidCallPermission();
    }
  }

  requestAndroidCallPermission = () => {
    PermissionUtil.requestAndroidCallPermission().then(granted => {
      if (granted === PermissionsAndroid.RESULTS.GRANTED) {
        this.callAgent();
      }
    });
  };

  callAgent() {
    const agentPO = this.getAgentPO();
    GoogleAnalyticUtil.trackLeads({
      leadType: LeadTypes.call,
      source: LeadSources.featuredListingPlus,
    });
    this.trackContactAgentActionsForListing('C');
    if (
      !ObjectUtil.isEmpty(agentPO) &&
      !ObjectUtil.isEmpty(agentPO.getMobileNumber())
    ) {
      const url = 'tel:' + agentPO.getMobileNumber();

      Linking.canOpenURL(url)
        .then(supported => {
          if (!supported) {
            console.log(
              "Featured Plus Listing - call - Can't handle url: " + url,
            );
          } else {
            return Linking.openURL(url);
          }
        })
        .catch(err =>
          console.error(
            'Featured Plus Listing - An error occurred - call agent - ',
            err,
          ),
        );
    }
  }

  getAgentPO() {
    const {listingPO} = this.props;
    if (!ObjectUtil.isEmpty(listingPO)) {
      return listingPO.getAgentPO();
    }
  }

  whatsAppAgent() {
    const {listingPO} = this.props;
    const agentPO = this.getAgentPO();
    GoogleAnalyticUtil.trackLeads({
      leadType: LeadTypes.whatsapp,
      source: LeadSources.featuredListingPlus,
    });
    this.trackContactAgentActionsForListing('W');

    if (
      !ObjectUtil.isEmpty(agentPO) &&
      !ObjectUtil.isEmpty(agentPO.getMobileNumber())
    ) {
      var url =
        'https://api.whatsapp.com/send?phone=65' + agentPO.getMobileNumber(); //+"&text=xxx"

      var msgTemplate = '';
      if (!ObjectUtil.isEmpty(listingPO)) {
        msgTemplate = listingPO.getSMSMessageTemplate();
      }
      url += '&text=' + msgTemplate;

      Linking.canOpenURL(url)
        .then(supported => {
          if (!supported) {
            console.log(
              "Featured Plus Listing - whatsapp - Can't handle url: " + url,
            );
          } else {
            return Linking.openURL(url);
          }
        })
        .catch(err =>
          console.error(
            'Featured Plus Listing - An error occurred - whatsapp agent - ',
            err,
          ),
        );
    }
  }

  smsAgent() {
    const {listingPO} = this.props;
    const agentPO = this.getAgentPO();
    GoogleAnalyticUtil.trackLeads({
      leadType: LeadTypes.sms,
      source: LeadSources.featuredListingPlus,
    });
    this.trackContactAgentActionsForListing('S');
    if (
      !ObjectUtil.isEmpty(agentPO) &&
      !ObjectUtil.isEmpty(agentPO.getMobileNumber())
    ) {
      var url = 'sms:' + agentPO.getMobileNumber(); //+"&body=xxx"
      if (isIOS) {
        url += '&body=';
      } else {
        url += '?body=';
      }
      var msgTemplate = '';
      if (!ObjectUtil.isEmpty(listingPO)) {
        msgTemplate = listingPO.getSMSMessageTemplate();
      }
      url += msgTemplate;

      Linking.canOpenURL(url)
        .then(supported => {
          if (!supported) {
            console.log(
              "Featured Plus Listing - sms - Can't handle url: " + url,
            );
          } else {
            return Linking.openURL(url);
          }
        })
        .catch(err =>
          console.error(
            'Featured Plus Listing - An error occurred - sms agent - ',
            err,
          ),
        );
    }
  }

  renderListingImage() {
    const {listingPO} = this.props;
    let imageUrl = listingPO.getListingImageUrl();
    if (isIOS) {
      return (
        <ImageBackground
          style={styles.imageStyle}
          defaultSource={Placeholder_General}
          source={{uri: imageUrl}}
          resizeMode={'cover'}>
          {this.renderShortlistTimeStampRow()}
          {this.renderCertifyListings()}
        </ImageBackground>
      );
    } else {
      //Not a good way to solve android default source issue
      return (
        <ImageBackground
          style={styles.imageStyle}
          source={Placeholder_General}
          resizeMode={'cover'}>
          <Image
            style={{flex: 1, backgroundColor: 'transparent'}}
            source={{uri: imageUrl}}
            resizeMode={'cover'}
          />
          {this.renderShortlistTimeStampRow()}
          {this.renderCertifyListings()}
        </ImageBackground>
      );
    }
  }

  renderCertifyListings() {
    const {listingPO} = this.props;
    if (listingPO.ownerCertifiedInd) {
      return (
        <View style={styles.certifyListingStyle}>
          <View style={{flexDirection: 'row', alignSelf: 'center', flex: 1}}>
            <Image
              style={{
                height: 18,
                width: 18,
                alignSelf: 'center',
                marginRight: Spacing.XS,
              }}
              resizeMode={'contain'}
              source={Listing_CertifiedIconV2}
            />
            <ExtraSmallBodyText
              style={{color: SRXColor.White, alignSelf: 'center'}}>
              Certified Listing
            </ExtraSmallBodyText>
          </View>
        </View>
      );
    }
    return;
  }

  renderShortlistTimeStampRow() {
    return (
      <View style={styles.shortlistTimeStampRow}>
        {this.renderShorlistedIcon()}
        {this.renderTimeStamp()}
      </View>
    );
  }

  renderShorlistedIcon() {
    const {listingPO} = this.props;
    const isShortlist = ShortlistUtil.isShortlist({
      listingId: listingPO.getListingId(),
    });
    return (
      <View style={{flex: 1, marginTop: 3, marginRight: 3}}>
        <Button
          buttonStyle={styles.shortlistContainer}
          leftView={
            isShortlist ? (
              <FontAwesomeIcon name={'heart'} size={20} color={SRXColor.Teal} />
            ) : (
              <FeatherIcon name={'heart'} size={20} color={'white'} />
            )
          }
          onPress={() => this.onPressShortList()}
        />
      </View>
    );
  }

  renderTimeStamp() {
    const {listingPO} = this.props;
    let timeStamp = listingPO.getFormattedActualDatePosted();
    if (timeStamp) {
      return (
        <View
          style={{
            flexDirection: 'row',
            justifyContent: 'flex-end',
          }}>
          <View style={styles.timeStampContainer}>
            <FeatherIcon name={'clock'} size={15} color={'#858585'} />
            <ExtraSmallBodyText style={{color: '#858585', marginLeft: 4}}>
              {timeStamp}
            </ExtraSmallBodyText>
          </View>
        </View>
      );
    }
  }

  renderProjectInfoAndEnquireButton() {
    return (
      <View
        style={{
          marginHorizontal: Spacing.XS,
          marginBottom: Spacing.XS / 2,
          marginTop: Spacing.M,
        }}>
        {this.renderCheckboxAndProjectName()}
        {this.renderListingDetailType()}
        {this.renderSizeAndVideoCall()}
        {this.renderPriceAndRooms()}
        {this.renderXListing()}
        {this.renderExclusiveV360DroneAndEnquireButton()}
      </View>
    );
  }

  renderCheckboxAndProjectName() {
    const {listingPO} = this.props;
    return (
      <View style={{flexDirection: 'row', alignItems: 'center'}}>
        {this.renderListingCheckbox()}
        {this.renderProjectName()}
      </View>
    );
  }

  renderListingCheckbox() {
    const {shortListedTab, listingPO} = this.props;
    let isSelected = this.isListingSelected(listingPO);
    return (
      <Button
        buttonStyle={{
          marginRight: Spacing.XS,
        }}
        onPress={() => this.onSelectedEnquiryList()}
        leftView={
          isSelected ? (
            <View style={CheckboxStyles.checkStyle}>
              <FeatherIcon name={'check'} size={15} color={'white'} />
            </View>
          ) : (
            <View style={CheckboxStyles.unCheckStyle} />
          )
        }
      />
    );
  }

  renderProjectName() {
    const {listingPO} = this.props;
    return (
      <Heading2 numberOfLines={1}>{listingPO.getListingName() + ' '}</Heading2>
    );
  }

  renderListingDetailType() {
    const {listingPO} = this.props;

    var listingTypeInfo = '';
    if (listingPO.isSale()) {
      listingTypeInfo = listingPO.getSaleListingDetail();
    } else {
      listingTypeInfo = listingPO.getRentListingDetail();
    }

    if (!ObjectUtil.isEmpty(listingTypeInfo)) {
      return (
        <View
          style={{
            flexDirection: 'row',
            flexWrap: 'wrap',
            alignItems: 'center',
          }}>
          <ExtraSmallBodyText
            style={{color: '#858585', marginVertical: Spacing.XS}}>
            {listingTypeInfo}
          </ExtraSmallBodyText>
          {listingPO.newLaunchInd === true ? (
            <View style={styles.exclusiveV360DroneNewProjectContainer}>
              <ExtraSmallBodyText style={{color: SRXColor.Purple}}>
                New Project
              </ExtraSmallBodyText>
            </View>
          ) : null}
        </View>
      );
    }
  }
  renderSizeAndVideoCall() {
    return (
      <View style={{flexDirection: 'row', alignItems: 'center'}}>
        {this.renderSize()}
        {this.renderVideoCall()}
      </View>
    );
  }

  renderSize() {
    const {listingPO} = this.props;
    if (!ObjectUtil.isEmpty(listingPO.getSizeDisplay())) {
      return (
        <ExtraSmallBodyText style={{color: '#858585'}}>
          {listingPO.getSizeDisplay()}
        </ExtraSmallBodyText>
      );
    }
  }

  renderVideoCall() {
    const {remoteOption} = this.props.listingPO;
    if (!ObjectUtil.isEmpty(remoteOption) && remoteOption.videoCallInd) {
      return (
        <View
          style={{
            flexDirection: 'row',
            justifyContent: 'flex-end',
            flex: 1,
          }}>
          <LottieView
            autoPlay
            loop
            style={{width: 30, height: 30, backgroundColor: 'transparent'}}
            source={Listing_Video_Call}
          />
        </View>
      );
    }
  }

  renderPriceAndRooms() {
    return (
      <View
        style={{
          flexDirection: 'row',
          flex: 1,
          marginTop: Spacing.XS / 2,
        }}>
        {this.renderPrice()}
        {this.renderRooms()}
      </View>
    );
  }

  renderPrice() {
    const {listingPO} = this.props;
    if (!ObjectUtil.isEmpty(listingPO.getAskingPrice())) {
      return (
        <Heading2_Currency>{listingPO.getAskingPrice()}</Heading2_Currency>
      );
    }
  }

  renderRooms() {
    return (
      <View style={{flexDirection: 'row', flex: 1, justifyContent: 'flex-end'}}>
        {this.renderBedroomBathroom()}
      </View>
    );
  }

  renderBedroomBathroom = () => {
    return (
      <View
        style={{
          flexDirection: 'row',
        }}>
        {this.renderBedroom()}
        {this.renderBathroom()}
      </View>
    );
  };

  renderBedroom() {
    const {listingPO} = this.props;
    const bedroom = listingPO.getRoom();
    if (!ObjectUtil.isEmpty(bedroom)) {
      return (
        <View style={styles.bedroomBathroomContainer}>
          <Image
            style={{height: 20, width: 20}}
            resizeMode={'contain'}
            source={Listings_BedIcon}
          />
          <Heading2 style={{paddingLeft: Spacing.XS}}>{bedroom}</Heading2>
        </View>
      );
    }
  }

  renderBathroom() {
    const {listingPO} = this.props;
    const bathroom = listingPO.getBathroom();
    if (!ObjectUtil.isEmpty(bathroom)) {
      return (
        <View
          style={[styles.bedroomBathroomContainer, {marginLeft: Spacing.S}]}>
          <Image
            style={{height: 20, width: 20}}
            resizeMode={'contain'}
            source={Listings_BathTubIcon}
          />
          <Heading2 style={{paddingLeft: Spacing.XS}}>{bathroom}</Heading2>
        </View>
      );
    }
  }

  renderXListing() {
    const {listingPO} = this.props;
    if (listingPO.hasXListing()) {
      return (
        //change x listing price to certified valuation
        <Text style={{color: SRXColor.Currency, fontSize: 10}}>
          With SRX Certified Valuation
        </Text>
      );
    }
  }
  renderExclusiveV360DroneAndEnquireButton() {
    return (
      <View
        style={{
          flexDirection: 'row',
          flex: 1,
          marginVertical: Spacing.XS,
          alignItems: 'center',
        }}>
        {this.renderExclusiveV360Drone()}
        <View
          style={{
            flexDirection: 'row',
            flex: 1,
            justifyContent: 'flex-end',
          }}>
          <Button
            buttonType={Button.buttonTypes.primary}
            buttonStyle={{
              height: 30,
              borderRadius: 15,
              paddingHorizontal: Spacing.M,
              // marginBottom: Spacing.XS,
              // marginRight: 50
            }}
            textStyle={{fontWeight: 'normal', lineHeight: 25, fontSize: 14}}
            onPress={() => this.onEnquiryNowPressed()}>
            Enquire Now
          </Button>
        </View>
      </View>
    );
  }

  renderExclusiveV360Drone = () => {
    return (
      <View
        style={{
          flexDirection: 'row',
          marginTop: Spacing.M,
        }}>
        {this.renderExclusive()}
        {this.renderDrone()}
        {this.renderV360()}
      </View>
    );
  };

  renderExclusive() {
    const {listingPO} = this.props;
    if (listingPO.isExclusive()) {
      return (
        <View
          style={[styles.exclusiveV360DroneRow, styles.rightContentItemMargin]}>
          <View style={styles.exclusiveV360DroneNewProjectContainer}>
            <ExtraSmallBodyText style={{color: SRXColor.Purple}}>
              Exclusive
            </ExtraSmallBodyText>
          </View>
        </View>
      );
    }
  }

  renderV360() {
    const {listingPO} = this.props;
    if (listingPO.hasVirtualTour()) {
      return (
        <View
          style={[styles.exclusiveV360DroneRow, styles.rightContentItemMargin]}>
          <View style={styles.exclusiveV360DroneNewProjectContainer}>
            <ExtraSmallBodyText style={{color: SRXColor.Purple}}>
              v360{'\u00b0'}
            </ExtraSmallBodyText>
          </View>
        </View>
      );
    }
  }

  renderDrone() {
    const {listingPO} = this.props;
    if (listingPO.hasDroneView()) {
      return (
        <View
          style={[styles.exclusiveV360DroneRow, styles.rightContentItemMargin]}>
          <View style={styles.exclusiveV360DroneNewProjectContainer}>
            <ExtraSmallBodyText style={{color: SRXColor.Purple}}>
              Drone
            </ExtraSmallBodyText>
          </View>
        </View>
      );
    }
  }

  //agent info section
  renderAgentInfoListingAddressAndWhatsappCallSMS() {
    return (
      <View
        style={[
          styles.subContainerStyle,
          {marginTop: Spacing.XS, paddingVertical: Spacing.XS},
        ]}>
        <View style={styles.agentInfoContainer}>
          {this.renderAgentInfo()}
          {this.renderListingAddress()}
        </View>
        {this.renderWhatsappCallSMS()}
      </View>
    );
  }

  renderAgentInfo() {
    const {listingPO} = this.props;
    let agentPO = listingPO.getAgentPO();
    let agentImageUrl = CommonUtil.handleImageUrl(agentPO.photo);
    let agencyLogo = agentPO.getAgencyLogoURL();
    return (
      <View
        style={{
          flexDirection: 'row',
          width: 150,
          paddingHorizontal: Spacing.XS,
          alignItems: 'center',
        }}>
        <Image
          style={styles.agentImageStyle}
          defaultSource={Placeholder_Agent}
          source={{uri: agentImageUrl}}
          resizeMode={'cover'}
        />
        <SmallBodyText
          style={{lineHeight: 22, fontWeight: '600', width: 100}}
          numberOfLines={1}>
          {agentPO.name}
        </SmallBodyText>
      </View>
    );
  }

  renderListingAddress() {
    const {listingPO} = this.props;
    if (!ObjectUtil.isEmpty(listingPO.getListingHeader())) {
      return (
        <ExtraSmallBodyText
          numberOfLines={2}
          style={{flex: 1, marginHorizontal: Spacing.XS}}>
          {listingPO.getListingHeader()}
        </ExtraSmallBodyText>
      );
    }
  }

  renderWhatsappCallSMS() {
    return (
      <View
        style={{
          marginTop: Spacing.M,
          flexDirection: 'row',
          marginHorizontal: Spacing.XS,
        }}>
        {this.renderWhatsapp()}
        {this.renderCall()}
        {this.renderSMS()}
      </View>
    );
  }

  renderWhatsapp() {
    return (
      <Button
        buttonStyle={[
          styles.whatsAppCallSMSContainer,
          {alignItems: 'center', marginRight: Spacing.XS},
        ]}
        textStyle={{color: SRXColor.Purple, fontSize: 14}}
        onPress={() => this.whatsAppAgent()}>
        Whatsapp
      </Button>
    );
  }

  renderCall() {
    return (
      <Button
        buttonStyle={[
          styles.whatsAppCallSMSContainer,
          {alignItems: 'center', marginRight: Spacing.XS},
        ]}
        textStyle={{color: SRXColor.Purple, fontSize: 14}}
        onPress={() => this.callAgentClicked()}>
        Call
      </Button>
    );
  }

  renderSMS() {
    return (
      <Button
        buttonStyle={[styles.whatsAppCallSMSContainer, {alignItems: 'center'}]}
        textStyle={{color: SRXColor.Purple, fontSize: 14}}
        onPress={() => this.smsAgent()}>
        SMS
      </Button>
    );
  }

  renderShortListSection() {
    const {shortListedTab} = this.props;
    if (shortListedTab) {
      return (
        <View style={{flex: 1, margin: Spacing.M}}>
          <ShortlistedAgent stage={1} />
        </View>
      );
    }
  }

  renderSeparator() {
    return <Separator />;
  }

  render() {
    const {listingPO} = this.props;
    if (!ObjectUtil.isEmpty(listingPO)) {
      return (
        <TouchableHighlight onPress={() => this.onPressShowListingDetails()}>
          <View style={styles.containerStyle}>
            <View style={styles.subContainerStyle}>
              {this.renderListingImage()}
              {this.renderProjectInfoAndEnquireButton()}
            </View>
            {this.renderAgentInfoListingAddressAndWhatsappCallSMS()}
          </View>
        </TouchableHighlight>
      );
    } else {
      return <View />;
    }
  }
}

const styles = StyleSheet.create({
  containerStyle: {
    backgroundColor: SRXColor.White,
    flex: 1,
    paddingTop: Spacing.XS,
    paddingBottom: Spacing.S,
    paddingHorizontal: Spacing.M,
  },
  subContainerStyle: {
    backgroundColor: SRXColor.White,
    //border
    borderRadius: 8,
    borderWidth: 1.3,
    borderColor: SRXColor.Purple,
    //Shadow for iOS
    shadowColor: SRXColor.Purple,
    shadowOffset: {width: 0, height: 1},
    shadowOpacity: 0.64,
    shadowRadius: 4,
  },
  imageStyle: {
    width: '100%',
    height: 230,
    backgroundColor: SRXColor.White,
    borderTopLeftRadius: 7,
    borderTopRightRadius: 7,
    overflow: 'hidden',
  },
  certifyListingStyle: {
    position: 'absolute',
    bottom: 0,
    right: 0,
    left: 0,
    paddingBottom: 3,
    paddingTop: 3,
    backgroundColor: SRXColor.Purple,
  },
  agentInfoContainer: {
    // marginTop: Spacing.XS,
    alignItems: 'center',
    flexDirection: 'row',
    backgroundColor: SRXColor.White,
    // paddingVertical: Spacing.XS
  },
  agentImageStyle: {
    borderWidth: 1,
    borderColor: 'rgba(0,0,0,0.2)',
    alignItems: 'center',
    justifyContent: 'center',
    width: 30,
    height: 30,
    backgroundColor: '#fff',
    borderRadius: 15,
    marginRight: Spacing.XS,
  },
  shortlistContainer: {
    width: 35,
    height: 30,
    alignItems: 'center',
    paddingTop: 3,
    paddingBottom: 3,
    paddingLeft: Spacing.XS,
    paddingRight: Spacing.XS / 2,
  },
  rightContentItemMargin: {
    marginBottom: Spacing.XS,
    marginRight: Spacing.S,
  },
  bedroomBathroomContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  shortlistTimeStampRow: {
    flexDirection: 'row',
    flex: 1,
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
  },
  exclusiveV360DroneRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  exclusiveV360DroneNewProjectContainer: {
    borderRadius: 4,
    borderWidth: 1,
    borderColor: SRXColor.Purple,
    paddingHorizontal: Spacing.XS / 2,
  },
  whatsAppCallSMSContainer: {
    borderRadius: 4,
    borderWidth: 1,
    borderColor: SRXColor.Purple,
    paddingVertical: Spacing.XS / 4,
    paddingHorizontal: Spacing.XS,
  },
  timeStampContainer: {
    borderWidth: 1,
    borderColor: '#e0e0e0',
    paddingHorizontal: Spacing.XS,
    height: 30,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: SRXColor.White,
    flexDirection: 'row',
  },
});

const mapStateToProps = state => {
  return {
    shortlistData: state.shortlistData,
    userPO: state.loginData.userPO,
    viewlistData: state.viewlistData,
  };
};

export default connect(
  mapStateToProps,
  {
    shortlistListing,
    removeShortlist,
    saveViewedListings,
  },
)(ListingListItem2);
