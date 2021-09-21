import React, {Component} from 'react';
import {
  StyleSheet,
  Image,
  View,
  Text,
  ImageBackground,
  Alert,
  Platform,
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
  Button,
  TouchableHighlight,
  LottieView,
} from '../../../components';
import {LoginStack} from '../../../config';
import {SRXColor, AlertMessage} from '../../../constants';
import {ListingPO} from '../../../dataObject';
import {Spacing, CheckboxStyles} from '../../../styles';
import {ObjectUtil, CommonUtil, ShortlistUtil} from '../../../utils';
import {
  Listings_BathTubIcon,
  Listings_BedIcon,
  Listing_Video_Call,
  Placeholder_Agent,
  Placeholder_General,
  Listing_CertifiedIconV2,
} from '../../../assets';
import {
  shortlistListing,
  removeShortlist,
  saveViewedListings,
} from '../../../actions';
import {ShortlistedAgent} from '../../Shortlist';
import {ModelDescription} from '../../../constants/Models';
const isIOS = Platform.OS === 'ios';
class ListingListItem3 extends Component {
  static propTypes = {
    listingPO: PropTypes.instanceOf(ListingPO),
    removeFromSelectedListingList: PropTypes.func,
    onEnquireListing: PropTypes.func,
  };

  constructor(props) {
    super(props);
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

  onEnquiryNowPressed = () => {
    const {listingPO, onEnquireListing} = this.props;
    onEnquireListing(listingPO);
  };

  //Search Listing
  renderProjectNameAndPrice() {
    return (
      <View style={{flex: 1, paddingHorizontal: Spacing.S}}>
        {this.renderProjectName()}
      </View>
    );
  }

  renderListingImageLayout() {
    return (
      <View style={{width: 150, overflow: 'hidden', borderBottomLeftRadius: 6}}>
        {this.renderListingImage()}
      </View>
    );
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
          buttonStyle={[styles.checkBoxContainer]}
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

  //agent info section
  renderAgentInfoAndListingAddress() {
    return (
      <View style={[styles.agentInfoContainer, styles.subContainerStyle]}>
        {this.renderAgentInfo()}
        {this.renderListingAddress()}
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

  renderListingInfoLayout() {
    return (
      <View
        style={{
          flex: 1,
          paddingLeft: Spacing.XS,
          paddingRight: Spacing.M,
          paddingTop: Spacing.XS / 2,
        }}>
        {this.renderListingDetailType()}
        {this.renderNewProject()}
        {this.renderSize()}
        {this.renderRoomsAndVideoCall()}
        {this.renderExclusiveV360Drone()}
        {this.renderEnquireButton()}
      </View>
    );
  }

  renderProjectName() {
    const {listingPO} = this.props;
    return (
      <View style={styles.projectNameContainer}>
        {this.renderListingCheckbox()}
        <Heading2 numberOfLines={2} style={{overflow: 'hidden', flex: 1}}>
          {listingPO.getListingName() + ' '}
        </Heading2>
        <View style={{alignItems: 'flex-end'}}>
          {this.renderPrice()}
          {this.renderXListing()}
        </View>
      </View>
    );
  }

  renderListingCheckbox() {
    const {shortListedTab, listingPO} = this.props;
    let isSelected = this.isListingSelected(listingPO);
    return (
      <Button
        buttonStyle={{
          paddingVertical: Spacing.XS,
          paddingRight: Spacing.L / 2,
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

  renderListingDetailType() {
    const {listingPO} = this.props;

    var listingTypeInfo = '';

    if (listingPO.isSale()) {
      listingTypeInfo = listingPO.getSaleListingDetail();
    } else {
      listingTypeInfo = listingPO.getRentListingDetail();
    }

    //Condominium • Executive Condominium • Leasehold-99 • 2019 (TOP) => Executive Condominium • Leasehold-99 • 2019 (TOP)
    if (listingTypeInfo.includes(ModelDescription.executive_Condo)) {
      var indexOfFirst = listingTypeInfo.indexOf(
        ModelDescription.executive_Condo,
      );
      let listingTypeInfo1 = listingTypeInfo;
      listingTypeInfo = listingTypeInfo1.substring(indexOfFirst);
    }

    if (!ObjectUtil.isEmpty(listingTypeInfo)) {
      return (
        <View style={{height: 30}}>
          <ExtraSmallBodyText style={{color: '#858585'}}>
            {listingTypeInfo}
          </ExtraSmallBodyText>
        </View>
      );
    }
  }

  renderNewProject() {
    const {listingPO} = this.props;

    return (
      <View style={{flexDirection: 'row', marginVertical: Spacing.XS / 2}}>
        {listingPO.newLaunchInd === true ? (
          <View style={styles.exclusiveV360DroneNewProjectContainer}>
            <ExtraSmallBodyText style={{color: SRXColor.Purple}}>
              New Project
            </ExtraSmallBodyText>
          </View>
        ) : (
          <View style={{height: 16}} />
        )}
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
        <View
          style={[
            styles.bedroomBathroomContainer,
            styles.rightContentItemMargin,
          ]}>
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
          style={[
            styles.bedroomBathroomContainer,
            styles.rightContentItemMargin,
          ]}>
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

  renderVideoCall() {
    const {remoteOption} = this.props.listingPO;
    if (!ObjectUtil.isEmpty(remoteOption) && remoteOption.videoCallInd) {
      return (
        <View
          style={{
            flexDirection: 'row',
            justifyContent: 'flex-end',
            flex: 1,
            marginBottom: Spacing.XS / 2,
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

  renderExclusiveV360Drone = () => {
    const {listingPO} = this.props;
    return (
      <View
        style={{
          flexDirection: 'row',
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

  renderSize() {
    const {listingPO} = this.props;
    if (!ObjectUtil.isEmpty(listingPO.getSizeDisplay())) {
      return (
        <ExtraSmallBodyText
          style={{color: '#858585', marginBottom: Spacing.XS / 2}}>
          {listingPO.getSizeDisplay()}
        </ExtraSmallBodyText>
      );
    }
  }

  renderPrice() {
    const {listingPO} = this.props;
    if (!ObjectUtil.isEmpty(listingPO.getAskingPrice())) {
      return (
        <Heading2_Currency>{listingPO.getAskingPrice()}</Heading2_Currency>
      );
    }
  }

  renderRoomsAndVideoCall() {
    return (
      <View style={{flexDirection: 'row', alignItems: 'center'}}>
        {this.renderBedroomBathroom()}
        {this.renderVideoCall()}
      </View>
    );
  }

  renderXListing() {
    const {listingPO} = this.props;
    if (listingPO.hasXListing()) {
      return (
        //change x listing price to certified valuation
        <Text style={{color: SRXColor.Currency, marginLeft: 4, fontSize: 10}}>
          With SRX Certified Valuation
        </Text>
      );
    }
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

  renderEnquireButton() {
    return (
      <Button
        buttonType={Button.buttonTypes.primary}
        buttonStyle={{
          justifyContent: 'center',
          height: 30,
          borderRadius: 15,
          paddingHorizontal: Spacing.M,
          marginBottom: Spacing.XS,
          marginRight: Spacing.XL + Spacing.XS,
        }}
        textStyle={{fontWeight: 'normal', lineHeight: 25, fontSize: 14}}
        onPress={() => this.onEnquiryNowPressed()}>
        Enquire Now
      </Button>
    );
  }

  render() {
    const {listingPO} = this.props;
    if (!ObjectUtil.isEmpty(listingPO)) {
      return (
        <TouchableHighlight onPress={() => this.onPressShowListingDetails()}>
          <View style={styles.containerStyle}>
            <View style={styles.subContainerStyle}>
              {this.renderProjectNameAndPrice()}
              <View
                style={{
                  flex: 1,
                  flexDirection: 'row',
                  borderTopWidth: 1,
                  borderTopColor: '#e0e0e0',
                }}>
                {this.renderListingImageLayout()}
                {this.renderListingInfoLayout()}
              </View>
            </View>
            {this.renderAgentInfoAndListingAddress()}
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
    height: '100%',
    backgroundColor: SRXColor.White,
  },
  agentInfoContainer: {
    marginTop: Spacing.XS,
    alignItems: 'center',
    flexDirection: 'row',
    backgroundColor: SRXColor.White,
    paddingVertical: Spacing.XS,
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
  projectNameContainer: {
    flexDirection: 'row',
    marginVertical: Spacing.XS / 2,
    alignItems: 'center',
  },
  checkBoxContainer: {
    width: 35,
    height: 30,
    alignItems: 'center',
    paddingTop: 3,
    paddingBottom: 3,
    paddingLeft: Spacing.XS,
    paddingRight: Spacing.XS / 2,
  },
  rightContentItemMargin: {
    marginBottom: Spacing.XS / 2,
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
  certifyListingStyle: {
    position: 'absolute',
    bottom: 0,
    right: 0,
    left: 0,
    paddingBottom: 3,
    paddingTop: 3,
    backgroundColor: SRXColor.Purple,
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
)(ListingListItem3);
