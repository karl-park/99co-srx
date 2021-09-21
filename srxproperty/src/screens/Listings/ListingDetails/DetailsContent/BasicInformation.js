import React, {Component} from 'react';
import {View, Image, TouchableOpacity} from 'react-native';
import PropTypes from 'prop-types';
import {
  Listings_BathTubIcon,
  Listings_BedIcon,
  Listing_CertifiedIcon,
  Listing_CertifiedIconV2,
  Listing_Video_Call,
} from '../../../../assets';
import {
  BodyText,
  Button,
  FeatherIcon,
  ExtraSmallBodyText,
  Heading1,
  Heading2,
  Heading2_Currency,
  Subtext,
  Text,
  OcticonsIcon,
  LottieView,
} from '../../../../components';
import {ListingPO} from '../../../../dataObject';
import {Spacing} from '../../../../styles';
import {SRXColor} from '../../../../constants';
import {ObjectUtil} from '../../../../utils';
import {QualityBar} from '../DetailsComponent';

class BasicInformation extends Component {
  static propTypes = {
    listingPO: PropTypes.instanceOf(ListingPO),
  };

  static defaultProps = {
    listingPO: null,
  };

  state = {
    showCertifiedListingDescription: false,
  };

  toggle() {
    const {showCertifiedListingDescription} = this.state;
    let certifiedListingInd = false;
    certifiedListingInd = !showCertifiedListingDescription;
    this.setState({
      showCertifiedListingDescription: certifiedListingInd,
    });
  }

  renderProjectNameAndAddress = () => {
    const {listingPO} = this.props;

    if (!ObjectUtil.isEmpty(listingPO)) {
      return (
        <View style={{marginBottom: Spacing.L}}>
          <Heading1>{listingPO.getListingDetailTitle()}</Heading1>
          {!ObjectUtil.isEmpty(listingPO.getListingHeader()) ? (
            <Subtext>{listingPO.getListingHeader()}</Subtext>
          ) : null}
        </View>
      );
    }
  };

  renderExclusiveV360DroneAndVideoCall() {
    return (
      <View
        style={{
          flexDirection: 'row',
        }}>
        {this.renderExclusiveV360Drone()}
        {this.renderVideoCall()}
      </View>
    );
  }

  renderExclusiveV360Drone = () => {
    const {listingPO} = this.props;
    const {remoteOption} = this.props.listingPO;
    if (
      listingPO.isExclusive() ||
      listingPO.hasVirtualTour() ||
      listingPO.hasDroneView()
    ) {
      if (!ObjectUtil.isEmpty(remoteOption) && remoteOption.videoCallInd) {
        return (
          <View
            style={{
              flexDirection: 'row',
              flexWrap: 'wrap',
              flex: 1,
              alignItems: 'center',
            }}>
            {this.renderExclusive()}
            {this.renderDrone()}
            {this.renderV360()}
          </View>
        );
      } else {
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
      }
    } else {
      return <View style={{height: Spacing.L}} />;
    }
  };

  renderContentOnLeft = () => {
    return (
      <View
        style={{flex: 1, alignItems: 'flex-start', marginBottom: Spacing.XS}}>
        {this.renderSize()}
        {this.renderPrice()}
        {/* {this.renderXListingAndExclusiveListing()} */}
      </View>
    );
  };

  renderContentOnRight = () => {
    return (
      <View style={{alignItems: 'flex-end'}}>
        {this.renderBedroomBathroom()}
        {/* {this.renderQuality()} */}
      </View>
    );
  };

  renderSize() {
    const {listingPO} = this.props;

    if (!ObjectUtil.isEmpty(listingPO)) {
      let isEmpty = listingPO.getLandedBuiltSizeDisplay() == '';
      return (
        <Subtext style={Styles.leftContentItemMargin}>
          {listingPO.getSizeDisplay()}
        </Subtext>
      );
    }
  }

  renderPrice() {
    const {listingPO} = this.props;

    if (!ObjectUtil.isEmpty(listingPO)) {
      const {askingPriceModel} = listingPO;
      const checkAskingPriceModel =
        askingPriceModel == '' || askingPriceModel == 'X-Listing Price';
      return (
        <View style={Styles.leftContentItemMargin}>
          <Heading2_Currency>{listingPO.getAskingPrice()}</Heading2_Currency>
          {checkAskingPriceModel ? null : (
            <Text
              style={{color: SRXColor.Purple, marginRight: 4, fontSize: 10}}>
              {askingPriceModel.length < 20
                ? askingPriceModel
                : askingPriceModel.substr(0, 17) + '...'}
            </Text>
          )}
          {this.renderXListing()}
        </View>
      );
    }
  }

  renderXListingAndExclusiveListing() {
    return (
      <View style={[Styles.leftContentItemMargin, {flexDirection: 'row'}]}>
        {this.renderXListing()}
      </View>
    );
  }

  renderXListing() {
    const {listingPO} = this.props;

    if (!ObjectUtil.isEmpty(listingPO)) {
      if (listingPO.hasXListing()) {
        return (
          <Text style={{color: SRXColor.Purple, marginRight: 4, fontSize: 10}}>
            With SRX Certified Valuation
          </Text>
        );
      }
    }
  }

  renderExclusive() {
    const {listingPO} = this.props;
    if (!ObjectUtil.isEmpty(listingPO)) {
      if (listingPO.isExclusive()) {
        return (
          <View style={[Styles.exclusiveV360DroneContainer]}>
            <ExtraSmallBodyText style={{color: SRXColor.Purple}}>
              Exclusive
            </ExtraSmallBodyText>
          </View>
        );
      }
    }
  }

  renderV360() {
    const {listingPO} = this.props;
    if (listingPO.hasVirtualTour()) {
      return (
        <View style={[Styles.exclusiveV360DroneContainer]}>
          <ExtraSmallBodyText style={{color: SRXColor.Purple}}>
            v360{'\u00b0'}
          </ExtraSmallBodyText>
        </View>
      );
    }
  }

  renderDrone() {
    const {listingPO} = this.props;
    if (listingPO.hasDroneView()) {
      return (
        <View style={[Styles.exclusiveV360DroneContainer]}>
          <ExtraSmallBodyText style={{color: SRXColor.Purple}}>
            Drone
          </ExtraSmallBodyText>
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
            alignItems: 'center',
          }}>
          <ExtraSmallBodyText style={{color: SRXColor.Purple}}>
            Video viewing available
          </ExtraSmallBodyText>
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

  renderCertifiedListing() {
    const {listingPO} = this.props;
    const {showCertifiedListingDescription} = this.state;
    if (listingPO.ownerCertifiedInd) {
      return (
        <TouchableOpacity onPress={() => this.toggle()}>
          <View
            style={{
              backgroundColor: SRXColor.Purple,
              paddingHorizontal: Spacing.M,
              paddingVertical: 2,
            }}>
            <View
              style={{
                flexDirection: 'row',
                justifyContent: 'space-between',
              }}>
              <View style={{flexDirection: 'row'}}>
                <Image
                  style={{height: 18, width: 18, alignSelf: 'center'}}
                  resizeMode={'contain'}
                  source={Listing_CertifiedIconV2}
                />
                <ExtraSmallBodyText
                  style={{
                    marginLeft: Spacing.XS,
                    color: SRXColor.White,
                    alignSelf: 'center',
                  }}>
                  Certified Listing
                </ExtraSmallBodyText>
              </View>
              {showCertifiedListingDescription ? (
                <FeatherIcon
                  name="chevron-up"
                  size={24}
                  color={SRXColor.White}
                  style={{alignSelf: 'center', marginLeft: Spacing.XS}}
                />
              ) : (
                <FeatherIcon
                  name="chevron-down"
                  size={24}
                  color={SRXColor.White}
                  style={{alignSelf: 'center', marginLeft: Spacing.XS}}
                />
              )}
            </View>
            {this.renderCertifiedListingDescription()}
          </View>
        </TouchableOpacity>
      );
    }
  }

  renderCertifiedListingDescription() {
    const {showCertifiedListingDescription} = this.state;
    return showCertifiedListingDescription ? (
      <ExtraSmallBodyText style={{color: SRXColor.White, paddingBottom: 2}}>
        A certified listing is a genuine listing certified by the property owner
      </ExtraSmallBodyText>
    ) : null;
  }

  renderClassified() {
    const {listingPO} = this.props;
    if (!ObjectUtil.isEmpty(listingPO)) {
      if (listingPO.hasClassified()) {
        return (
          <Subtext style={Styles.leftContentItemMargin}>
            Also on Classifieds
          </Subtext>
        );
      }
    }
  }

  renderBedroomBathroom = () => {
    return (
      <View style={{flexDirection: 'row'}}>
        {this.renderBedroom()}
        {this.renderBathroom()}
      </View>
    );
  };

  renderBedroom() {
    const {listingPO} = this.props;
    if (!ObjectUtil.isEmpty(listingPO)) {
      const bedroom = listingPO.getRoom();

      if (!ObjectUtil.isEmpty(bedroom)) {
        return (
          <View
            style={[
              Styles.bedroomBathroomContainer,
              Styles.rightContentItemMargin,
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
  }

  renderBathroom() {
    const {listingPO} = this.props;
    if (!ObjectUtil.isEmpty(listingPO)) {
      const bathroom = listingPO.getBathroom();

      if (!ObjectUtil.isEmpty(bathroom)) {
        return (
          <View
            style={[
              Styles.bedroomBathroomContainer,
              Styles.rightContentItemMargin,
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
  }

  renderQuality() {
    const {listingPO} = this.props;
    if (!ObjectUtil.isEmpty(listingPO)) {
      const {quality} = listingPO;
      return (
        <View
          style={[
            {flexDirection: 'row', alignItems: 'center'},
            Styles.rightContentItemMargin,
          ]}>
          <Subtext style={{marginRight: Spacing.XS}}>Ad Quality</Subtext>
          <QualityBar quality={quality} />
        </View>
      );
    }
  }

  render() {
    const {style} = this.props;
    return (
      <View style={style}>
        {this.renderCertifiedListing()}
        <View
          style={[
            {
              paddingHorizontal: Spacing.M,
              paddingTop: Spacing.M,
            },
          ]}>
          {this.renderProjectNameAndAddress()}
          <View style={{flexDirection: 'row'}}>
            {this.renderContentOnLeft()}
            {this.renderContentOnRight()}
          </View>
          <View style={{marginBottom: Spacing.XS}}>
            {this.renderExclusiveV360DroneAndVideoCall()}
            {this.renderClassified()}
          </View>
        </View>
      </View>
    );
  }
}

const Styles = {
  rightContentItemMargin: {
    marginBottom: Spacing.L,
    marginLeft: Spacing.M,
  },
  bedroomBathroomContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  leftContentItemMargin: {
    marginBottom: Spacing.XS,
  },
  exclusiveV360DroneContainer: {
    borderRadius: 4,
    borderWidth: 1,
    borderColor: SRXColor.Purple,
    paddingHorizontal: Spacing.XS / 2,
    marginRight: Spacing.M,
    marginBottom: Spacing.XS,
  },
};

export {BasicInformation};
