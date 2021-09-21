import React, {Component} from 'react';
import {
  Image,
  View,
  Linking,
  Platform,
  StyleSheet,
  ImageBackground,
  TouchableOpacity,
} from 'react-native';
import PropTypes from 'prop-types';

import {
  Placeholder_Agent,
  Placeholder_General,
  Listings_BathTubIcon,
  Listings_BedIcon,
  TransactedListings_SoldBanner,
  TransactedListings_RentedBanner,
} from '../../../assets';
import {
  Button,
  Heading2,
  FeatherIcon,
  SmallBodyText,
  ExtraSmallBodyText,
  Separator,
  BodyText,
  TouchableHighlight,
} from '../../../components';
import {
  ObjectUtil,
  CommonUtil,
  PermissionUtil,
  PropertyTypeUtil,
} from '../../../utils';
import {SRXColor} from '../../../constants';
import {ListingPO} from '../../../dataObject';

import {Spacing, Typography} from '../../../styles';

class ChatListingListItem extends Component {
  constructor(props) {
    super(props);
  }

  renderLeftContent() {
    const {listingPO} = this.props;
    if (!ObjectUtil.isEmpty(listingPO)) {
      const {liveInd, listingStatus} = listingPO;

      if (liveInd || (!liveInd && listingStatus != 'S')) {
        return <View style={{width: 80}}>{this.renderListingImage()}</View>;
      }
    }

    return;
  }

  //Listing Image
  renderListingImage() {
    const {listingPO, isMessage} = this.props;
    let imageUrl = listingPO.getListingImageUrl();

    if (listingPO.liveInd) {
      if (isIOS) {
        return (
          <View>
            <ImageBackground
              style={isMessage ? styles.messageImageStyle : styles.imageStyle}
              defaultSource={Placeholder_General}
              source={{uri: imageUrl}}
              resizeMode={'cover'}
            />
          </View>
        );
      } else {
        return (
          <ImageBackground
            style={isMessage ? styles.messageImageStyle : styles.imageStyle}
            source={Placeholder_General}
            resizeMode={'cover'}>
            <Image
              style={styles.subImageStyle}
              source={{uri: imageUrl}}
              resizeMode={'cover'}
            />
          </ImageBackground>
        );
      }
    } else {
      if (isIOS) {
        return (
          <View>
            <ImageBackground
              style={isMessage ? styles.messageImageStyle : styles.imageStyle}
              defaultSource={Placeholder_General}
              source={{uri: imageUrl}}
              resizeMode={'cover'}>
              <Image
                style={[
                  styles.subImageStyle,
                  {tintColor: 'gray', opacity: 0.8},
                ]}
                source={{uri: imageUrl}}
                resizeMode={'cover'}
              />
            </ImageBackground>
            <ExtraSmallBodyText style={{fontSize: 10, textAlign: 'center'}}>
              Expired Listing
            </ExtraSmallBodyText>
          </View>
        );
      } else {
        return (
          <ImageBackground
            style={isMessage ? styles.messageImageStyle : styles.imageStyle}
            source={Placeholder_General}
            resizeMode={'cover'}>
            <Image
              style={[styles.subImageStyle, {tintColor: 'gray'}]}
              source={{uri: imageUrl}}
              resizeMode={'cover'}
            />
            <Image
              style={[
                styles.subImageStyle,
                {position: 'absolute', opacity: 0.3},
              ]}
              source={{uri: imageUrl}}
              resizeMode={'cover'}
            />
            {/* {this.renderAgentInfo()} */}
          </ImageBackground>
        );
      }
    }
  }

  //Agent Information
  renderAgentInfo() {
    return (
      <View style={styles.agentInfoStyle}>
        {this.renderPropertyType()}
        <View
          style={{
            flex: 1,
            justifyContent: 'center',
            marginHorizontal: Spacing.XS,
          }}>
          {this.renderAgentImageAndName()}
          {this.renderAgencyImageAndPhone()}
        </View>
      </View>
    );
  }

  //Right Corner Label
  renderPropertyType() {
    const {listingPO} = this.props;
    let bannerSource = TransactedListings_SoldBanner;
    if (!listingPO.isSale()) {
      bannerSource = TransactedListings_RentedBanner;
    }
    return (
      <Image
        style={{
          height: 53,
          width: 56,
          alignSelf: 'flex-end',
          position: 'absolute',
        }}
        resizeMode={'contain'}
        source={bannerSource}
      />
    );
  }

  //Agent Image And Name
  renderAgentImageAndName() {
    const {listingPO} = this.props;
    let agentPO = listingPO.getAgentPO();

    if (!ObjectUtil.isEmpty(agentPO)) {
      let agentImageUrl = CommonUtil.handleImageUrl(agentPO.getAgentPhoto());
      return (
        <View
          style={{
            flexDirection: 'row',
            alignItems: 'center',
          }}>
          <Image
            style={styles.agentImageStyle}
            defaultSource={Placeholder_Agent}
            source={{uri: agentImageUrl}}
            resizeMode={'cover'}
          />
          {this.renderAgentName()}
        </View>
      );
    }
  }

  //Agent Name
  renderAgentName() {
    const {listingPO} = this.props;
    let agentPO = listingPO.getAgentPO();

    return (
      <SmallBodyText
        style={{lineHeight: 16, flex: 1, fontWeight: '600'}}
        numberOfLines={1}>
        {agentPO.name}
      </SmallBodyText>
    );
  }

  //Agency Image and Phone
  renderAgencyImageAndPhone() {
    const {listingPO} = this.props;
    let agentPO = listingPO.getAgentPO();
    if (!ObjectUtil.isEmpty(agentPO)) {
      let agencyImageURL = agentPO.getAgencyLogoURL();
      return (
        <View
          style={{
            flexDirection: 'row',
            alignItems: 'center',
          }}>
          <Image
            style={styles.agencyLogo}
            defaultSource={Placeholder_General}
            source={{uri: agencyImageURL}}
            resizeMode={'contain'}
          />
        </View>
      );
    }
  }

  //Right Content
  renderRightContent() {
    const {listingPO} = this.props;
    const {liveInd, listingStatus} = listingPO;
    return (
      <View style={{flex: 1, paddingLeft: Spacing.XS}}>
        {this.renderProjectName()}
        {!liveInd && listingStatus == 'S' ? (
          this.renderSold()
        ) : (
          <View style={{marginTop: Spacing.XS}}>{this.renderSize()}</View>
        )}
        {!liveInd && listingStatus == 'S' ? null : this.renderBedroomBathroom()}
      </View>
    );
  }

  renderSold() {
    const {listingPO} = this.props;
    return (
      <View
        style={{
          flexDirection: 'row',
          alignItems: 'center',
          marginTop: Spacing.XS,
        }}>
        <View style={styles.soldContainer}>
          <ExtraSmallBodyText style={{color: SRXColor.White}}>
            Sold
          </ExtraSmallBodyText>
        </View>
        <ExtraSmallBodyText
          style={{color: SRXColor.Gray, marginBottom: Spacing.XS}}>
          {listingPO.getPropertyType()}
        </ExtraSmallBodyText>
        {this.renderSize()}
      </View>
    );
  }

  //Project Name
  renderProjectName() {
    const {listingPO} = this.props;
    return (
      <View
        style={{
          flexDirection: 'row',
          justifyContent: 'space-between',
          marginTop: Spacing.XS,
        }}>
        <Heading2 style={{fontSize: 14}} numberOfLines={1}>
          {listingPO.getListingName()}
        </Heading2>
      </View>
    );
  }

  //Listing Detailed Type
  renderListingDetailType() {
    const {listingPO} = this.props;
    var listingTypeInfo = '';
    if (listingPO.isSale()) {
      listingTypeInfo = listingPO.getSaleListingDetail();
    } else {
      listingTypeInfo = listingPO.getRentListingDetail();
    }
    return (
      <ExtraSmallBodyText style={{color: SRXColor.Gray, marginTop: Spacing.XS}}>
        {listingTypeInfo}
      </ExtraSmallBodyText>
    );
  }

  //Size
  renderSize() {
    const {listingPO} = this.props;
    const {liveInd, listingStatus} = listingPO;
    return (
      <ExtraSmallBodyText
        style={[styles.leftContentItemMargin, {color: SRXColor.Gray}]}>
        {listingPO.getSizeDisplay()}
      </ExtraSmallBodyText>
    );
  }

  //Rooms
  renderBedroomBathroom = () => {
    return (
      <View style={{flexDirection: 'row'}}>
        {this.renderBedroom()}
        {this.renderBathroom()}
      </View>
    );
  };

  //Bedrooms
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

  //Bathroom
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
  render() {
    const {containerStyle, listingPO, onItemPress} = this.props;
    if (!ObjectUtil.isEmpty(listingPO)) {
      return (
        <TouchableHighlight
          style={containerStyle}
          underlayColor={'rgba(0,0,0, 0.2)'}
          onPress={onItemPress}>
          <View
            style={{
              flexDirection: 'row',
              alignItems: 'center',
            }}>
            {this.renderLeftContent()}
            {this.renderRightContent()}
          </View>
        </TouchableHighlight>
      );
    } else {
      return <View />;
    }
  }
}

ChatListingListItem.propTypes = {
  listingPO: PropTypes.instanceOf(ListingPO).isRequired,
  isMessage: PropTypes.bool,
  //isMessage will reduce the size of image
};

ChatListingListItem.defaultProps = {
  listingPO: PropTypes.instanceOf(ListingPO),
  isMessage: false,
};

const styles = StyleSheet.create({
  mainContainer: {
    flex: 1,
    // paddingVertical: Spacing.S,
    // paddingHorizontal: Spacing.M
  },
  imageStyle: {
    width: '100%',
    height: 80,
    backgroundColor: SRXColor.AccordionBackground,
    alignItems: 'center',
  },
  messageImageStyle: {
    width: '100%',
    height: 50,
    backgroundColor: SRXColor.AccordionBackground,
    alignItems: 'center',
  },
  subImageStyle: {
    width: '100%',
    height: '100%',
    backgroundColor: 'transparent',
  },
  rightContentItemMargin: {
    marginBottom: Spacing.XS,
    marginRight: Spacing.M,
  },
  bedroomBathroomContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  leftContentItemMargin: {
    marginBottom: Spacing.XS,
  },
  descriptionStyle: {
    fontStyle: 'italic',
    color: SRXColor.Gray,
    textAlign: 'center',
    marginVertical: Spacing.XS,
  },
  agentInfoStyle: {
    width: '100%',
    height: '100%',
    backgroundColor: 'rgba(255, 255, 255, 0.75)',
    position: 'absolute',
    overflow: 'hidden',
  },
  agentImageStyle: {
    borderWidth: 1,
    borderColor: 'rgba(0,0,0,0.2)',
    alignItems: 'center',
    justifyContent: 'center',
    width: 24,
    height: 24,
    backgroundColor: SRXColor.White,
    borderRadius: 12,
    marginRight: Spacing.XS / 2,
  },
  agencyLogo: {
    height: 30,
    width: 40,
    marginVertical: Spacing.XS / 2,
    marginRight: Spacing.XS / 2,
  },
  soldContainer: {
    borderRadius: 4,
    borderWidth: 1,
    backgroundColor: SRXColor.Purple,
    paddingHorizontal: Spacing.XS / 2,
    marginRight: Spacing.XS,
    marginBottom: Spacing.XS,
  },
});

export {ChatListingListItem};
