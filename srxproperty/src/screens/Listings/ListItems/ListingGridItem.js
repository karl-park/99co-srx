import React, {Component} from 'react';
import {StyleSheet, Image, View, ImageBackground, Platform} from 'react-native';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';

import {
  Heading2,
  Heading2_Currency,
  ExtraSmallBodyText,
  FeatherIcon,
  FontAwesomeIcon,
  Button,
  TouchableHighlight,
  Text,
  SmallBodyText,
  LottieView,
} from '../../../components';
import {LoginStack} from '../../../config';
import {SRXColor} from '../../../constants';
import {ListingPO} from '../../../dataObject';
import {Spacing, CheckboxStyles} from '../../../styles';
import {
  ObjectUtil,
  ShortlistUtil,
  PropertyTypeUtil,
  CommonUtil,
} from '../../../utils';
import {
  Listings_BathTubIcon,
  Listings_BedIcon,
  Listing_Video_Call,
  Listing_CertifiedIconV2,
  Placeholder_General,
  Placeholder_Agent,
} from '../../../assets';
import {
  shortlistListing,
  removeShortlist,
  saveViewedListings,
} from '../../../actions';
import {ModelDescription} from '../../../constants/Models';

const isIOS = Platform.OS === 'ios';
class ListingGridItem extends Component {
  //Prop types
  static propTypes = {
    listingPO: PropTypes.instanceOf(ListingPO),
  };

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

  //direct to listing details screen
  onPressShowListingDetails = () => {
    const {listingPO, onSelectListing} = this.props;
    onSelectListing(listingPO);
  };

  //on Toggle shortlist
  onPressShortList = () => {
    //Allows LoggedIn User to save shortlist
    const {listingPO, isShowShortList, userPO} = this.props;
    const isShortlisted = ShortlistUtil.isShortlist({
      listingId: listingPO.getListingId(),
    });
    if (isShortlisted) {
      //REMOVE
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
    } else {
      //SAVE
      //  if (userPO) {
      //check user has already logged in or not
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

  renderListingImageLayout() {
    return (
      <View
        style={{
          overflow: 'hidden',
          borderTopLeftRadius: 6,
          borderTopRightRadius: 6,
        }}>
        {this.renderListingImage()}
      </View>
    );
  }

  //Listing Image
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
          {this.renderCheckboxAndShortlistRow()}
          {this.renderCertifyListings()}
        </ImageBackground>
      );
    } else {
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
          {this.renderCheckboxAndShortlistRow()}
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

  //checkbox and shortlist icon row
  renderCheckboxAndShortlistRow() {
    return (
      <View style={styles.checkboxShortlistRow}>
        {this.renderListingCheckbox()}
        <View
          style={{
            flexDirection: 'row',
            justifyContent: 'flex-end',
          }}>
          {this.renderShortlistedIcon()}
        </View>
      </View>
    );
  }

  renderListingCheckbox() {
    const {listingPO} = this.props;
    let isSelected = this.isListingSelected(listingPO);
    return (
      <View style={{flex: 1}}>
        <Button
          buttonStyle={styles.checkboxOrShortlistIconContainer}
          leftView={
            isSelected ? (
              <View style={CheckboxStyles.checkStyle}>
                <FeatherIcon name={'check'} size={15} color={'white'} />
              </View>
            ) : (
              <View style={CheckboxStyles.unCheckStyle} />
            )
          }
          onPress={() => this.onSelectedEnquiryList()}
        />
      </View>
    );
  }

  renderShortlistedIcon() {
    const {listingPO} = this.props;
    const isShortlisted = ShortlistUtil.isShortlist({
      listingId: listingPO.getListingId(),
    });
    return (
      <Button
        buttonStyle={styles.checkboxOrShortlistIconContainer}
        leftView={
          isShortlisted ? (
            <FontAwesomeIcon name={'heart'} size={20} color={SRXColor.Teal} />
          ) : (
            <FeatherIcon name={'heart'} size={20} color={'white'} />
          )
        }
        onPress={() => this.onPressShortList()}
      />
    );
  }

  //v360
  renderV360Icon() {
    const {listingPO} = this.props;
    if (listingPO.hasVirtualTour()) {
      return (
        <View style={styles.v360DroneContainer}>
          <Image
            style={{height: 20, width: 20}}
            resizeMode={'contain'}
            source={Listings_V360Icon_Blue}
          />
        </View>
      );
    }
  }

  //drone
  renderDroneIcon() {
    const {listingPO} = this.props;
    if (listingPO.hasDroneView()) {
      return (
        <View style={[styles.v360DroneContainer, {marginLeft: 5}]}>
          <Image
            style={{height: 20, width: 20}}
            resizeMode={'contain'}
            source={Listings_XDroneIcon_Blue}
          />
        </View>
      );
    }
  }

  //project name
  renderProjectName() {
    const {listingPO} = this.props;
    var listingName = !ObjectUtil.isEmpty(listingPO.getListingName())
      ? listingPO.getListingName()
      : '';

    return (
      <View style={{height: 60, flexDirection: 'row'}}>
        <Heading2
          numberOfLines={2}
          style={{
            overflow: 'hidden',
            flex: 1,
            lineHeight: 22,
            marginBottom: Spacing.XS,
          }}>
          {listingName + ' '}
        </Heading2>
      </View>
    );
  }

  //Listing Details Info
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
        <View style={{height: 50}}>
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

  //Size
  renderListingSize() {
    const {listingPO} = this.props;
    return (
      <View style={{height: 70, justifyContent: 'center'}}>
        <ExtraSmallBodyText
          style={{color: SRXColor.Gray, marginBottom: Spacing.XS}}>
          {listingPO.getSizeDisplay()}
        </ExtraSmallBodyText>
      </View>
    );
  }

  renderListingPriceAndSRXVerifiedStatus = () => {
    return (
      <View
        style={{
          marginBottom: Spacing.XS,
        }}>
        {this.renderLitsingPrice()}
        {this.renderSRXVerifiedStatus()}
      </View>
    );
  };

  //asking price
  renderLitsingPrice() {
    const {listingPO} = this.props;
    return <Heading2_Currency>{listingPO.getAskingPrice()}</Heading2_Currency>;
  }

  //verified status
  renderSRXVerifiedStatus() {
    const {listingPO} = this.props;
    var srxVerifiedStatus = listingPO.hasXListing()
      ? 'With SRX Certified Valuation'
      : '';
    if (listingPO.hasXListing()) {
      return (
        //change x listing price to certified valuation
        <Text style={{color: SRXColor.Currency, fontSize: 10}}>
          {srxVerifiedStatus}
        </Text>
      );
    } else {
      return <View style={{height: 14}} />;
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

  renderBedroomBathroom = () => {
    const {listingPO} = this.props;
    const bedroom = listingPO.getRoom();
    const bathroom = listingPO.getBathroom();
    if (!ObjectUtil.isEmpty(bedroom) || !ObjectUtil.isEmpty(bathroom)) {
      return (
        <View
          style={{
            flexDirection: 'row',
            alignItems: 'center',
          }}>
          {this.renderBedroom()}
          {this.renderBathroom()}
        </View>
      );
    } else {
      return <View style={{height: 28}} />;
    }
  };

  renderBedroom() {
    const {listingPO} = this.props;
    const bedroom = listingPO.getRoom();
    if (!ObjectUtil.isEmpty(bedroom)) {
      return (
        <View
          style={[
            styles.bedroomBathroomContainer,
            {marginBottom: Spacing.XS, marginRight: Spacing.S},
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
            {marginBottom: Spacing.XS, marginRight: Spacing.S},
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
            marginBottom: Spacing.XS
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
    if (
      listingPO.isExclusive() ||
      listingPO.hasVirtualTour() ||
      listingPO.hasDroneView()
    ) {
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
    } else {
      return <View style={{height: Spacing.L}} />;
    }
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

  renderAgentInfo() {
    const {listingPO} = this.props;
    let agentPO = listingPO.getAgentPO();
    let agentImageUrl = CommonUtil.handleImageUrl(agentPO.photo);
    return (
      <View
        style={{
          flexDirection: 'row',
          width: 150,
          padding: Spacing.XS,
          alignItems: 'center',
        }}>
        <Image
          style={styles.agentImageStyle}
          defaultSource={Placeholder_Agent}
          source={{uri: agentImageUrl}}
          resizeMode={'cover'}
        />
        <SmallBodyText
          style={{lineHeight: 22, fontWeight: '600', width: 110}}
          numberOfLines={1}>
          {agentPO.name}
        </SmallBodyText>
      </View>
    );
  }

  renderListingGridItemContent() {
    //  const { containerStyle } = this.props;
    return (
      <View
        style={{
          paddingTop: Spacing.XS,
          paddingBottom: Spacing.S,
          backgroundColor: SRXColor.White,
        }}>
        <View
          style={[
            styles.itemContainerStyle,
            isIOS ? null : {borderWidth: 1, borderColor: '#e0e0e0'},
          ]}>
          {this.renderListingImageLayout()}
          <View style={{marginLeft: Spacing.XS / 2}}>
            {this.renderProjectName()}
            {this.renderListingDetailType()}
            {this.renderNewProject()}
            {this.renderListingSize()}
            {this.renderListingPriceAndSRXVerifiedStatus()}
            {this.renderRoomsAndVideoCall()}
            {this.renderExclusiveV360Drone()}
          </View>
        </View>
        <View
          style={[
            styles.itemContainerStyle,
            {marginTop: Spacing.XS},
            isIOS ? null : {borderWidth: 1, borderColor: '#e0e0e0'},
          ]}>
          {this.renderAgentInfo()}
        </View>
      </View>
    );
  }

  render() {
    const {listingPO} = this.props;
    if (!ObjectUtil.isEmpty(listingPO)) {
      return (
        <TouchableHighlight onPress={() => this.onPressShowListingDetails()}>
          {this.renderListingGridItemContent()}
        </TouchableHighlight>
      );
    } else {
      return <View />;
    }
  }
}

const styles = StyleSheet.create({
  // itemContainerStyle: {
  //   flex: 1,
  //   padding: Spacing.S
  // },
  itemContainerStyle: {
    //border
    borderRadius: 8,
    backgroundColor: SRXColor.White,
    //Shadow for iOS
    shadowColor: 'rgb(110,129,154)',
    shadowOffset: {width: 0, height: 1},
    shadowOpacity: 0.4,
    shadowRadius: 4,
  },
  imageStyle: {
    width: '100%',
    height: 121,
    backgroundColor: SRXColor.AccordionBackground,
    marginBottom: Spacing.XS,
  },
  checkboxShortlistRow: {
    flexDirection: 'row',
    flex: 1,
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    marginTop: 3,
    marginRight: 3,
  },
  v360DroneContainer: {
    borderRadius: 15,
    backgroundColor: 'rgba(0,0,0,0.5)',
    width: 30,
    height: 30,
    alignItems: 'center',
    justifyContent: 'center',
  },
  checkboxOrShortlistIconContainer: {
    width: 35,
    height: 30,
    alignItems: 'center',
    paddingTop: 3,
    paddingBottom: 3,
    paddingLeft: Spacing.XS,
    paddingRight: Spacing.XS / 2,
  },
  bedroomBathroomContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  rightContentItemMargin: {
    marginBottom: Spacing.XS,
    marginRight: Spacing.XS / 2,
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
  certifyListingStyle: {
    position: 'absolute',
    bottom: 0,
    right: 0,
    left: 0,
    paddingBottom: 3,
    paddingTop: 3,
    backgroundColor: SRXColor.Purple,
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
)(ListingGridItem);
