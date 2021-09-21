import PropTypes from 'prop-types';
import {
  ObjectUtil,
  PropertyTypeUtil,
  CommonUtil,
  StringUtil,
  DebugUtil,
  PropertyOptionsUtil,
} from '../utils';
import {AppConstant, Models} from '../constants';
import {
  DroneViewPO,
  VirtualTourPO,
  AgentPO,
  YoutubePO,
  FeaturedListingPO,
  ListingRemoteOptionPO,
} from '../dataObject';
import {TenureTypeDescription, TenureType} from '../constants/Tenures';
import Moment from 'moment';

class ListingPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      if (data.id) {
        if (typeof data.id === 'string') {
          this.key = data.id;
        } else if (typeof data.id === 'number') {
          this.key = data.id.toString();
        } else {
          this.key = data.key;
        }
      }
      this.actualDatePosted = data.actualDatePosted;
      this.address = data.address;
      this.askingPrice = data.askingPrice;
      this.askingPriceModel = data.askingPriceModel;
      this.bathroom = data.bathroom;
      this.block = data.block;
      this.builtAreaPricePsf = data.builtAreaPricePsf;
      this.builtAreaSizeSqft = data.builtAreaSizeSqft;
      this.builtAreaSizeSqm = data.builtAreaSizeSqm;
      this.builtYear = data.builtYear;
      this.cdResearchSubType = data.cdResearchSubType;
      this.distanceDesc2 = data.distanceDesc2;
      this.districtHdbTown = data.districtHdbTown;
      this.droneViews = [];
      if (!ObjectUtil.isEmpty(data.droneViews)) {
        data.droneViews.map(item => {
          this.droneViews.push(new DroneViewPO(item));
        });
      }
      this.encryptedId = data.encryptedId;
      this.exclusive = data.exclusive;
      this.floor = data.floor;
      this.furnish = data.furnish;
      this.id = data.id;
      this.landAreaPricePsf = data.landAreaPricePsf;
      this.landAreaSizeSqft = data.landAreaSizeSqft;
      this.latitude = data.latitude;
      this.leaseTerm = data.leaseTerm;
      this.listingHeader = data.listingHeader;
      this.listingPhoto = data.listingPhoto;
      this.listingType = data.listingType;
      this.liveInd = data.liveInd;
      this.longitude = data.longitude;
      this.model = data.model;
      this.name = data.name;
      this.newLaunchInd = data.newLaunchInd;
      this.ownerCertifiedInd = data.ownerCertifiedInd;
      this.pcCode = data.pcCode;
      this.postalCode = data.postalCode;
      this.postalDistrictId = data.postalDistrictId;
      this.postalHdbTownId = data.postalHdbTownId;
      this.propertyType = data.propertyType;
      this.quality = data.quality;
      this.remarks = data.remarks;
      this.rooms = data.rooms;
      this.status = data.status;
      this.tenure = data.tenure;
      this.type = data.type;
      this.tenureCode = data.tenureCode;
      this.videoPOs = [];
      if (!ObjectUtil.isEmpty(data.videoPOs)) {
        data.videoPOs.map(item => {
          this.videoPOs.push(new YoutubePO(item));
        });
      }
      this.virtualTour = null;
      if (
        !ObjectUtil.isEmpty(data.virtualTour) &&
        !ObjectUtil.isEmpty(data.virtualTour.url)
      ) {
        this.virtualTour = new VirtualTourPO(data.virtualTour);
      }
      this.agentPO = null;
      if (!ObjectUtil.isEmpty(data.agentPO)) {
        this.agentPO = new AgentPO(data.agentPO);
      }
      this.xListingInd = data.xListingInd;
      this.featured = null;
      if (!ObjectUtil.isEmpty(data.featured)) {
        this.featured = new FeaturedListingPO(data.featured);
      }
      this.remoteOption = null;
      if (!ObjectUtil.isEmpty(data.remoteOption)) {
        this.remoteOption = new ListingRemoteOptionPO(data.remoteOption);
      }
    }
  }

  getListingId = () => {
    const {id} = this;

    if (id) {
      if (typeof id === 'string') {
        if (id.startsWith('A')) {
          return id.substring(1);
        } else {
          return id;
        }
      } else if (typeof id === 'number') {
        return id.toString();
      }
    }
    return null;
  };

  getListingURL = () => {
    const listingId = this.getListingId();
    if (!ObjectUtil.isEmpty(listingId)) {
      const url = DebugUtil.retrieveStoreDomainURL();
      return url + '/l/' + listingId;
    }
    return null;
  };

  getImageUrl = () => {
    const {listingPhoto} = this;

    return CommonUtil.handleImageUrl(listingPhoto);
  };

  //remove logic to grab drone image ( sync with web )
  getListingImageUrl = () => {
    let imageUrl = '';
    if (this.hasVirtualTour()) {
      imageUrl = this.getVirtualTour().thumbnailUrl;
    } else {
      imageUrl = this.getImageUrl();
    }
    return imageUrl;
  };

  isSale = () => {
    if (this.type === 'R') {
      return false;
    }
    return true;
  };

  getDroneViews = () => {
    const {droneViews} = this;

    return droneViews;
  };

  hasDroneView = () => {
    let droneView = this.getDroneViews();

    return !ObjectUtil.isEmpty(droneView);
  };

  isExclusive = () => {
    const {exclusive} = this;
    if (!ObjectUtil.isEmpty(exclusive)) {
      if (exclusive.toLowerCase() === 'yes') {
        return true;
      }
    }

    return false;
  };

  getVirtualTour = () => {
    const {virtualTour} = this;

    return virtualTour;
  };

  hasVirtualTour = () => {
    let virtualTour = this.getVirtualTour();

    return !ObjectUtil.isEmpty(virtualTour);
  };

  getVideoPOs = () => {
    const {videoPOs} = this;

    return videoPOs;
  };

  hasVideoPOs = () => {
    let videoPOs = this.getVideoPOs();

    return !ObjectUtil.isEmpty(videoPOs);
  };

  hasXListing = () => {
    return this.xListingInd;
  };

  hasClassified = () => {
    const {pcCode} = this;

    return !ObjectUtil.isEmpty(pcCode);
  };

  getListingName = () => {
    let addressString = '';

    const {name, address, block, cdResearchSubType, model} = this;

    if (!ObjectUtil.isEmpty(block)) {
      if (PropertyTypeUtil.isHDB(cdResearchSubType)) {
        if (
          ObjectUtil.isEmpty(model) ||
          !StringUtil.compareStrings(model, Models.Model.walkup_Apt, true)
        ) {
          addressString += 'Blk ' + block + ' ';
        }
      }
    }

    if (!ObjectUtil.isEmpty(name)) {
      addressString += name;
    } else if (!ObjectUtil.isEmpty(address)) {
      addressString += address;
    }

    return addressString;
  };

  getListingDetailTitle = () => {
    let title = this.getListingName();

    const {districtHdbTown, cdResearchSubType, model} = this;

    if (!ObjectUtil.isEmpty(districtHdbTown)) {
      title = title + ' (' + districtHdbTown + ')';
    }

    if (PropertyTypeUtil.isHDB(cdResearchSubType)) {
      title =
        title +
        ', ' +
        PropertyTypeUtil.getPropertyTypeDescription(cdResearchSubType);
    } else {
      if (!ObjectUtil.isEmpty(model)) {
        title = title + ', ' + model;
      } else {
        const propertyTypeDesc = PropertyTypeUtil.getPropertyTypeDescription(
          cdResearchSubType,
        );
        if (!ObjectUtil.isEmpty(propertyTypeDesc)) {
          title = title + ', ' + propertyTypeDesc;
        }
      }
    }
    return title;
  };

  getFullAddress = () => {
    let addressString = '';

    const {address, block, cdResearchSubType, model} = this;

    if (!ObjectUtil.isEmpty(block)) {
      if (
        PropertyTypeUtil.isHDB(cdResearchSubType) ||
        PropertyTypeUtil.isCondo(cdResearchSubType)
      ) {
        if (
          ObjectUtil.isEmpty(model) ||
          !StringUtil.compareStrings(model, Models.Model.walkup_Apt, true)
        ) {
          addressString += block + ' ';
        }
      }
    }

    if (!ObjectUtil.isEmpty(address)) {
      addressString += address;
    }

    return addressString;
  };

  getFullAddressWithPostalCode = () => {
    let addressString = this.getFullAddress();
    if (!ObjectUtil.isEmpty(addressString)) {
      const {postalCode} = this;
      if (!ObjectUtil.isEmpty(postalCode)) {
        addressString += ' (' + postalCode + ')';
      }
    }
    return addressString;
  };

  getListingHeader = () => {
    if (!ObjectUtil.isEmpty(this.listingHeader)) {
      return this.listingHeader;
    }
    return null;
  };

  getPropertyType = () => {
    return PropertyTypeUtil.getPropertyTypeDescription(this.cdResearchSubType);
  };

  getAskingPrice = () => {
    const {askingPrice} = this;

    const price = StringUtil.formatCurrency(askingPrice);
    if (!ObjectUtil.isEmpty(price)) {
      return price;
    }
    return 'View to Offer';
  };

  getRoom = () => {
    const {rooms} = this;

    if (!ObjectUtil.isEmpty(rooms)) {
      return StringUtil.replace(rooms, 'bedroom', '').trim();
    }

    return null;
  };

  getBathroom = () => {
    const {bathroom} = this;

    if (!ObjectUtil.isEmpty(bathroom) && bathroom !== '0') {
      return bathroom;
    }
    return null;
  };

  getAgentPO = () => {
    const {agentPO} = this;

    return agentPO;
  };

  getModel = () => {
    const {model, propertyType} = this;
    if (!ObjectUtil.isEmpty(model)) {
      if (model.toLowerCase() !== propertyType.toLowerCase()) {
        return model;
      }
    }
    return '';
  };

  getSizeDisplay = () => {
    var combinationArray = [];

    const {
      builtAreaSizeSqft,
      builtAreaSizeSqm,
      landAreaSizeSqft,
      builtAreaPricePsf,
      landAreaPricePsf,
      cdResearchSubType,
      type,
    } = this;

    const isSale = type !== 'R';

    //Size
    if (PropertyTypeUtil.isHDB(cdResearchSubType)) {
      const sizeNum = Math.round(builtAreaSizeSqm);

      if (sizeNum > 0) {
        const sizeFinal = StringUtil.formatThousand(sizeNum) + ' sqm';

        combinationArray.push(sizeFinal);
      }
    } else if (PropertyTypeUtil.isLanded(cdResearchSubType) && isSale) {
      //for landed property, if it is for rent, then show built size & built psf
      const sizeNum = Math.round(landAreaSizeSqft);

      if (sizeNum > 0) {
        const sizeFinal = StringUtil.formatThousand(sizeNum) + ' sqft';

        combinationArray.push(sizeFinal);
      }
    } else {
      const sizeNum = Math.round(builtAreaSizeSqft);

      if (sizeNum > 0) {
        const sizeFinal = StringUtil.formatThousand(sizeNum) + ' sqft';

        combinationArray.push(sizeFinal);
      }
    }

    if (!this.isTransacted()) {
      //Psf
      //if the listing is transacted, do not show
      if (PropertyTypeUtil.isLanded(cdResearchSubType) && isSale) {
        const psfRounded = Math.round(landAreaPricePsf);

        if (psfRounded > 0) {
          combinationArray.push(
            '$' + StringUtil.formatThousand(psfRounded) + ' psf',
          );
        }
      } else {
        const psfRounded = isSale
          ? Math.round(builtAreaPricePsf)
          : Math.round(builtAreaPricePsf * 100) / 100;

        if (psfRounded > 0) {
          combinationArray.push(
            '$' + StringUtil.formatThousand(psfRounded) + ' psf',
          );
        }
      }
    }

    var joinString = combinationArray.join(' | ');
    if (
      PropertyTypeUtil.isLanded(cdResearchSubType) &&
      isSale &&
      combinationArray.length != 0
    ) {
      joinString += ' (Land) ';
    }

    if (this.getLandedBuiltSizeDisplay() != '') {
      if (combinationArray.length == 0) {
        joinString += this.getLandedBuiltSizeDisplay();
      } else {
        joinString += '\n' + this.getLandedBuiltSizeDisplay();
      }
    }

    return joinString;
  };

  getLandedBuiltSizeDisplay() {
    // this method is specially for Landed and Sale, for other condition return ""

    const {builtAreaPricePsf, cdResearchSubType, type} = this;

    const isSale = type !== 'R';
    var combinationArray = [];

    if (PropertyTypeUtil.isLanded(cdResearchSubType) && isSale) {
      const sizeNum = Math.round(this.builtAreaSizeSqft);

      if (sizeNum > 0) {
        const sizeFinal = StringUtil.formatThousand(sizeNum) + ' sqft';

        combinationArray.push(sizeFinal);
      }

      if (!this.isTransacted()) {
        const psfRounded = Math.round(builtAreaPricePsf);

        if (psfRounded > 0) {
          combinationArray.push(
            '$' + StringUtil.formatThousand(psfRounded) + ' psf',
          );
        }
      }

      var joinString = combinationArray.join(' | ');
      if (
        PropertyTypeUtil.isLanded(cdResearchSubType) &&
        isSale &&
        combinationArray.length != 0
      ) {
        joinString += ' (Built) ';
      }
      return joinString;
    }

    return '';
  }

  getLeaseTermDescription() {
    if (this.leaseTerm == 0) {
      return 'Flexible';
    } else if (this.leaseTerm >= 1) {
      return this.leaseTerm + ' months';
    }
    return '';
  }

  getSMSMessageTemplate() {
    let message = 'Hi';
    const agent = this.getAgentPO;
    if (!ObjectUtil.isEmpty(agent) && !ObjectUtil.isEmpty(agent.name)) {
      message += ' ' + agent.name;
    }
    message += ". I'm interested in your";
    if (this.type === 'R') {
      message += ' rental';
    } else {
      message += ' sale';
    }
    message += ' listing';

    if (!ObjectUtil.isEmpty(this.getListingName())) {
      message += ' for ' + this.getListingName();
    }
    message += ' on SRX ';
    const url = this.getListingURL();
    if (!ObjectUtil.isEmpty(url)) {
      message += '(' + url + ')';
    }
    message += '. Please contact me. Thanks 😊';
    return message;
  }

  getEnquiryMessageTemplate() {
    const {name, address, cdResearchSubType} = this;
    //I found this 2 bedroom Jalan Bahagia HDB 3 Rooms property on www.srx.com.sg and would like more information. Please send me more details. Thank you.
    let message = 'I found this ';

    if (
      !PropertyTypeUtil.isCommercial(cdResearchSubType) &&
      !ObjectUtil.isEmpty(this.getRoom())
    ) {
      message += this.getRoom() + ' bedroom ';
    }

    if (!ObjectUtil.isEmpty(name)) {
      message += name + ' ';
    } else if (!ObjectUtil.isEmpty(address)) {
      message += address + ' ';
    }

    message +=
      'property on www.srx.com.sg and would like more information. Please send me more details. Thank you.';

    return message;
  }

  getDistanceDescription() {
    let distanceDescription = '';
    if (!ObjectUtil.isEmpty(this.distanceDesc2)) {
      if (
        this.distanceDesc2.toLowerCase().includes('km') ||
        this.distanceDesc2.toLowerCase().includes('m')
      ) {
        distanceDescription = this.distanceDesc2 + ' away';
      }
    }
    return distanceDescription;
  }

  isTransacted() {
    return !ObjectUtil.isEmpty(this.status) && this.status === 'S';
  }

  //Sale Listing Details
  getSaleListingDetail() {
    var listingInfoArray = [];

    //Property Type
    if (!ObjectUtil.isEmpty(this.propertyType)) {
      listingInfoArray.push(this.propertyType);
    }

    //Model
    if (!ObjectUtil.isEmpty(this.getModel())) {
      listingInfoArray.push(this.getModel());
    }

    //Tenure (currently tenureCode is 1 to 6)
    if (
      this.tenureCode &&
      this.tenureCode > 0 &&
      this.tenureCode <= 6 &&
      !PropertyTypeUtil.isHDB(this.cdResearchSubType)
    ) {
      listingInfoArray.push(
        PropertyOptionsUtil.getTenureDescription(this.tenureCode),
      );
    }

    //Built Year
    if (!ObjectUtil.isEmpty(this.builtYear)) {
      let year = parseInt(new Date().getFullYear());
      if (parseInt(this.builtYear) >= year) {
        listingInfoArray.push(this.builtYear + ' (TOP) ');
      } else {
        listingInfoArray.push(this.builtYear + ' (Built) ');
      }
    }

    //Distance description
    if (!ObjectUtil.isEmpty(this.getDistanceDescription())) {
      listingInfoArray.push(this.getDistanceDescription());
    }

    return listingInfoArray.join(' • ');
  }

  getRentListingDetail() {
    var listingInfoArray = [];

    //Property Type
    if (!ObjectUtil.isEmpty(this.propertyType)) {
      listingInfoArray.push(this.propertyType);
    }

    //Model
    if (!ObjectUtil.isEmpty(this.getModel())) {
      listingInfoArray.push(this.getModel());
    }

    //Furnish
    if (!ObjectUtil.isEmpty(this.furnish)) {
      listingInfoArray.push(
        PropertyOptionsUtil.getFurnishLevelDescription(this.furnish),
      );
    }

    //Lease Term
    if (!ObjectUtil.isEmpty(this.leaseTerm.toString())) {
      listingInfoArray.push(this.getLeaseTermDescription());
    }

    //Built Year
    if (!ObjectUtil.isEmpty(this.builtYear)) {
      let year = parseInt(new Date().getFullYear());
      if (parseInt(this.builtYear) >= year) {
        listingInfoArray.push(this.builtYear + ' (TOP) ');
      } else {
        listingInfoArray.push(this.builtYear + ' (Built) ');
      }
    }

    //Distance description
    if (!ObjectUtil.isEmpty(this.getDistanceDescription())) {
      listingInfoArray.push(this.getDistanceDescription());
    }

    return listingInfoArray.join(' • ');
  }

  //timestamp in list item
  getFormattedActualDatePosted(dayUnit) {
    let datePosted = '';
    const currentDate = Moment().format('DD-MMM-YYYY');
    const startDate = Moment(this.actualDatePosted, 'DD-MMM-YYYY');
    const endDate = Moment(currentDate, 'DD-MMM-YYYY');
    const resultDate = Moment.duration(endDate.diff(startDate)).asDays();
    if (resultDate === 0) {
      datePosted = 'Today';
    } else if (resultDate <= 30) {
      datePosted = resultDate + (!ObjectUtil.isEmpty(dayUnit) ? dayUnit : 'd');
    }
    return datePosted;
  }
}

export {ListingPO};
