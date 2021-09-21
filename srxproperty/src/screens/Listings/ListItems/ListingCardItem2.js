import React, {Component} from 'react';
import {StyleSheet, Image, View, TouchableHighlight, Alert} from 'react-native';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import {shortlistListing, removeShortlist} from '../../../actions';
import {
  Placeholder_General,
  Listings_BedIcon,
  Listings_BathTubIcon,
} from '../../../assets';
import {
  BodyText,
  Heading2_Currency,
  ExtraSmallBodyText,
  Heading2,
  Button,
  FeatherIcon,
  FontAwesomeIcon,
} from '../../../components';
import {SRXColor, AlertMessage} from '../../../constants';
import {LoginStack} from '../../../config';
import {ListingPO} from '../../../dataObject';
import {Spacing} from '../../../styles';
import {ObjectUtil, ShortlistUtil} from '../../../utils';

class ListingCardItem2 extends Component {
  static propTypes = {
    listingPO: PropTypes.instanceOf(ListingPO).isRequired,
    onSelected: PropTypes.func,
  };

  onShortListBtnPressed = () => {
    //Allows LoggedIn User to save shortlist
    const {listingPO, userPO} = this.props;
    const isShortlist = ShortlistUtil.isShortlist({
      listingId: listingPO.getListingId(),
    });
    if (isShortlist) {
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
                this.props.removeShortlist({shortlistId: id});
              }
            },
          },
        ],
      );
    } else {
      //SAVE
      if (userPO) {
        //check user has already logged in or not
        this.props.shortlistListing({
          encryptedListingId: listingPO.encryptedId,
          listingType: listingPO.listingType,
        });
      } else {
        LoginStack.showSignInRegisterModal();
      }
    }
  };

  renderListingImage = () => {
    const {listingPO} = this.props;
    let imageUrl = listingPO.getListingImageUrl();
    return (
      <Image
        style={styles.imageStyle}
        defaultSource={Placeholder_General}
        source={{uri: imageUrl}}
        resizeMode={'cover'}
      />
    );
  };

  renderListingName() {
    const {listingPO} = this.props;
    if (!ObjectUtil.isEmpty(listingPO.getListingName())) {
      return (
        <BodyText numberOfLines={1}>{listingPO.getListingName()}</BodyText>
      );
    }
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

  renderPrice() {
    const {listingPO} = this.props;
    if (!ObjectUtil.isEmpty(listingPO.getAskingPrice())) {
      return (
        <Heading2_Currency numberOfLines={1}>
          {listingPO.getAskingPrice()}
        </Heading2_Currency>
      );
    }
  }

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

  renderShortlistButton() {
    const {listingPO} = this.props;
    const isShortlist = ShortlistUtil.isShortlist({
      listingId: listingPO.getListingId(),
    });
    return (
      <View style={{flex: 1, position: 'absolute', right: 0}}>
        <Button
          rightView={
            isShortlist ? (
              <FontAwesomeIcon name={'heart'} size={20} color={SRXColor.Teal} />
            ) : (
              <FeatherIcon name={'heart'} size={20} color={SRXColor.Black} />
            )
          }
          onPress={() => this.onShortListBtnPressed()}
        />
      </View>
    );
  }

  render() {
    const {listingPO, onSelected} = this.props;
    if (!ObjectUtil.isEmpty(listingPO)) {
      return (
        <TouchableHighlight
          style={[styles.containerStyle, styles.containerBorder]}
          onPress={() => {
            if (onSelected) onSelected(listingPO);
          }}>
          <View style={{backgroundColor: SRXColor.White, padding: Spacing.XS}}>
            <View style={{marginBottom: Spacing.XS}}>
              {this.renderListingImage()}
            </View>
            {this.renderListingName()}
            <View style={{marginVertical: Spacing.XS}}>
              {this.renderSize()}
              {this.renderPrice()}
            </View>
            <View style={{flexDirection: 'row'}}>
              {this.renderBedroom()}
              {this.renderBathroom()}
              {this.renderShortlistButton()}
            </View>
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
    overflow: 'hidden',
    width: 136 + Spacing.XS * 2,
    shadowColor: 'rgb(110,129,154)',
    shadowOffset: {width: 1, height: 2},
    shadowOpacity: 0.32,
    shadowRadius: 1,
  },
  containerBorder: {
    borderRadius: 5,
    borderWidth: 1,
    borderColor: '#e0e0e0',
  },
  imageStyle: {
    borderRadius: 5,
    width: 136,
    height: 88,
  },
  bedroomBathroomContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  rightContentItemMargin: {
    marginBottom: Spacing.XS,
    marginRight: Spacing.XS / 2,
  },
});

const mapStateToProps = state => {
  return {
    shortlistData: state.shortlistData,
    userPO: state.loginData.userPO,
    // viewlistData: state.viewlistData
  };
};

export default connect(
  mapStateToProps,
  {
    shortlistListing,
    removeShortlist,
  },
)(ListingCardItem2);
