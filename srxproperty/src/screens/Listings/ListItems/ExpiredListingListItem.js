import React, {Component} from 'react';
import {
  StyleSheet,
  Image,
  View,
  Text,
  ImageBackground,
  Platform,
  TouchableOpacity,
} from 'react-native';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';

import {Placeholder_Agent, Placeholder_General} from '../../../assets';
import {
  Heading2,
  Heading2_Currency,
  SmallBodyText,
  ExtraSmallBodyText,
  FontAwesomeIcon,
  FeatherIcon,
} from '../../../components';
import {SRXColor} from '../../../constants';
import {ListingPO} from '../../../dataObject';
import {Spacing} from '../../../styles';
import {ObjectUtil, CommonUtil, ShortlistUtil} from '../../../utils';
import {
  Listings_BathTubIcon,
  Listings_BedIcon,
  Listing_CertifiedIconV2,
} from '../../../assets';
import {removeShortlist} from '../../../actions';
import {ModelDescription} from '../../../constants/Models';

const isIOS = Platform.OS === 'ios';
class ExpiredListingListItem extends Component {
  static propTypes = {
    listingPO: PropTypes.instanceOf(ListingPO),
  };

  constructor(props) {
    super(props);
  }

  onPressRemoveShortList = () => {
    const {listingPO, isShowShortList} = this.props;
    const isShortlist = ShortlistUtil.isShortlist({
      listingId: listingPO.getListingId(),
    });
    if (isShortlist) {
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
  };

  //Search Listing
  renderProjectNameAndPrice() {
    return (
      <View style={{flex: 1, paddingHorizontal: Spacing.S, opacity: 0.5}}>
        {this.renderProjectName()}
      </View>
    );
  }

  renderListingImageLayout() {
    return (
      <View style={{width: 150, overflow: 'hidden', borderBottomLeftRadius: 6}}>
        {this.renderListingImage()}
        {this.renderExpiredAndRemove()}
      </View>
    );
  }

  renderListingImage() {
    const {listingPO} = this.props;
    let imageUrl = listingPO.getListingImageUrl();
    if (isIOS) {
      return (
        <View style={{height: '65%'}}>
          <ImageBackground
            style={styles.imageStyle}
            defaultSource={Placeholder_General}
            source={{uri: imageUrl}}
            resizeMode={'cover'}>
            {this.renderShortlistTimeStampRow()}
            {this.renderCertifyListings()}
          </ImageBackground>
        </View>
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

  renderExpiredAndRemove() {
    return (
      <View
        style={{
          borderColor: '#e0e0e0',
          borderRightWidth: 1,
          height: '35%',
          paddingVertical: Spacing.XS / 2,
          justifyContent: 'center',
        }}>
        <SmallBodyText style={{alignSelf: 'center'}}>
          Expired listing
        </SmallBodyText>
        <TouchableOpacity onPress={() => this.onPressRemoveShortList()}>
          <SmallBodyText style={{alignSelf: 'center', color: SRXColor.Teal}}>
            Remove
          </SmallBodyText>
        </TouchableOpacity>
      </View>
    );
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
    return (
      <View style={{flex: 1, marginTop: 3, marginRight: 3}}>
        <View style={[styles.checkBoxContainer]}>
          <FontAwesomeIcon name={'heart'} size={20} color={SRXColor.Teal} />
        </View>
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
      <View
        style={[
          styles.agentInfoContainer,
          styles.subContainerStyle,
          isIOS ? null : {borderWidth: 1, borderColor: '#e0e0e0'},
          {opacity: 0.5},
        ]}>
        {this.renderAgentInfo()}
        {this.renderListingAddress()}
      </View>
    );
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
          paddingHorizontal: Spacing.XS,
          paddingTop: Spacing.XS / 2,
          opacity: 0.5,
        }}>
        {this.renderListingDetailType()}
        {this.renderNewProject()}
        {this.renderSize()}
        {this.renderRoomsAndClassfied()}
        {this.renderExclusiveV360Drone()}
      </View>
    );
  }

  renderProjectName() {
    const {listingPO} = this.props;
    return (
      <View style={styles.projectNameContainer}>
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

  //New Project indicator
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

  renderSize() {
    const {listingPO} = this.props;
    if (!ObjectUtil.isEmpty(listingPO.getSizeDisplay())) {
      return (
        <ExtraSmallBodyText
          style={{color: '#858585', marginBottom: Spacing.XS}}>
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

  renderRoomsAndClassfied() {
    return (
      <View style={{flexDirection: 'row', alignItems: 'center'}}>
        {this.renderBedroomBathroom()}
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

  render() {
    const {listingPO} = this.props;
    if (!ObjectUtil.isEmpty(listingPO)) {
      return (
        <View style={styles.containerStyle}>
          <View
            style={[
              styles.subContainerStyle,
              isIOS ? null : {borderWidth: 1, borderColor: '#e0e0e0'},
            ]}>
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
    height: isIOS ? '100%' : '65%',
    backgroundColor: SRXColor.White,
    opacity: 0.5,
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
    marginVertical: Spacing.S,
    alignItems: 'center',
    backgroundColor: SRXColor.White,
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
  v360DroneContainer: {
    borderRadius: 15,
    backgroundColor: 'rgba(0,0,0,0.5)',
    width: 30,
    height: 30,
    alignItems: 'center',
    justifyContent: 'center',
  },
  sectionContainerStyle: {
    flex: 1,
    paddingVertical: Spacing.S,
    paddingHorizontal: Spacing.M,
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
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

export default connect(
  null,
  {
    removeShortlist,
  },
)(ExpiredListingListItem);
