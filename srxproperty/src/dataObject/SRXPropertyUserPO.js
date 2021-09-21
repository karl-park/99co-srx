import {
  ObjectUtil,
  CommonUtil,
  NumberUtil,
  StringUtil,
  PropertyTypeUtil
} from "../utils";
import { pt_OwnerOption } from "../constants";

class SRXPropertyUserPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      this.key = "SRXPropertyUserPO_" + data.ptUserId;

      this.acceptedInviteSource = data.acceptedInviteSource;
      this.address = data.address;
      this.agentPO = data.agentPO;
      this.buildingKey = data.buildingKey;
      this.buildingNumber = data.buildingNumber;
      this.builtYear = data.builtYear;
      this.capitalGainDate = data.capitalGainDate;
      this.capitalGainLastTransactedDate = data.capitalGainLastTransactedDate;
      this.capitalGainLastTransactedDateUnformatted =
        data.capitalGainLastTransactedDateUnformatted;
      this.capitalGainLastTransactedPrice = data.capitalGainLastTransactedPrice;
      this.capitalGainPercent = data.capitalGainPercent;
      this.capitalGainQuantum = data.capitalGainQuantum;
      this.capitalGainRentXValue = data.capitalGainRentXValue;
      this.capitalGainRentalYield = data.capitalGainRentalYield;
      this.capitalGainUserInputtedInd = data.capitalGainUserInputtedInd;
      this.capitalGainXValue = data.capitalGainXValue;
      this.cdResearchSubtype = data.cdResearchSubtype;
      this.cdResearchType = data.cdResearchType;
      this.dateOtpVerified = data.dateOtpVerified;
      this.dateOtpVerifiedFormatted = data.dateOtpVerifiedFormatted;
      this.dateOwnerNricVerified = data.dateOwnerNricVerified;
      this.distance = data.distance;
      this.district = data.district;
      this.effectiveTenure = data.effectiveTenure;
      this.email = data.email;
      this.enewsletterInd = data.enewsletterInd;
      this.homeMaintenanceApplicable = data.homeMaintenanceApplicable;
      this.inviteId = data.inviteId;
      this.inviteUser = data.inviteUser;
      this.inviterAgentPO = data.inviterAgentPO;
      this.landType = data.landType;
      this.landTypeDescription = data.landTypeDescription;
      this.lastPtViewDate = data.lastPtViewDate;
      this.latitude = data.latitude;
      this.listingAlertCriteriaList = data.listingAlertCriteriaList;
      this.listingAlertFrequency = data.listingAlertFrequency;
      this.listingAlertInd = data.listingAlertInd;
      this.listedSaleListingId = data.listedSaleListingId;
      this.listedRentListingId = data.listedRentListingId;
      this.longitude = data.longitude;
      this.mobileNum = data.mobileNum;
      this.model = data.model;
      this.name = data.name;
      this.numPtViews = data.numPtViews;
      this.occupancy = data.occupancy;
      this.openToOffersInd = data.openToOffersInd;
      this.ownerNric = data.ownerNric;
      this.ownerOptions = data.ownerOptions;
      this.photoUrl = data.photoUrl;
      this.postalCode = data.postalCode;
      this.postalCodeInteger = data.postalCodeInteger;
      this.projectName = data.projectName;
      this.projectPhoto = data.projectPhoto;
      this.ptAddressId = data.ptAddressId;
      this.ptUserId = data.ptUserId; //<== unique identifier
      this.purpose = data.purpose;
      this.regionalComparison = data.regionalComparison;
      this.rentValueChanges = data.rentValueChanges;
      this.rentXvalue = data.rentXvalue;
      this.rentalYield = data.rentalYield;
      this.saleValueChanges = data.saleValueChanges;
      this.saleXvalue = data.saleXvalue;
      this.sendRefinanceMsgInd = data.sendRefinanceMsgInd;
      this.size = data.size;
      this.socialId = data.socialId;
      this.source = data.source;
      this.streetId = data.streetId;
      this.streetKey = data.streetKey;
      this.streetName = data.streetName;
      this.tempEmail = data.tempEmail;
      this.tempName = data.tempName;
      this.tenure = data.tenure;
      this.tenureDescription = data.tenureDescription;
      this.transactionAlertInd = data.transactionAlertInd;
      this.unitFloor = data.unitFloor;
      this.unitNo = data.unitNo;
      this.urlForSharing = data.urlForSharing;
      this.userId = data.userId;
      this.userToken = data.userToken;
      this.validAddressForSignUp = data.validAddressForSignUp;
      this.validForSignUp = data.validForSignUp;
    }
  } //end of constructor

  getFullAddress() {
    const {
      address,
      buildingNumber,
      cdResearchSubtype,
      streetName,
      projectName,
      unitFloor,
      unitNo
    } = this;
    let arrayToJoinWithComma = [],
      arrayToJoinWithSpace = [];
    let firstPart = "";

    /**
     * Format:
     * HDB: property address, project name (if applicable), unit floor, unit no.
     * Condo: property address, project name, unit floor, unit no.
     * Landed: property address, project name (if applicable)
     */

    /**
     * the address is showing building number + streetName
     * However when the property is an walkup apartment, it didnt show the exact building number, eg. 65A, address will show 65 streetName
     */
    if (
      !ObjectUtil.isEmpty(streetName) &&
      !ObjectUtil.isEmpty(buildingNumber)
    ) {
      firstPart = buildingNumber + " " + streetName;
    } else if (!ObjectUtil.isEmpty(address)) {
      firstPart = address;
    }

    if (!ObjectUtil.isEmpty(firstPart)) {
      arrayToJoinWithComma.push(firstPart);
    }

    if (!ObjectUtil.isEmpty(projectName) && !firstPart.includes(projectName)) {
      arrayToJoinWithSpace.push(projectName);
    }
    if (
      (PropertyTypeUtil.isCondo(cdResearchSubtype) ||
        PropertyTypeUtil.isHDB(cdResearchSubtype)) &&
      !ObjectUtil.isEmpty(unitFloor) &&
      !ObjectUtil.isEmpty(unitNo)
    ) {
      arrayToJoinWithSpace.push("#" + unitFloor + "-" + unitNo);
    }

    const secondPart = arrayToJoinWithSpace.join(" ");
    if (!ObjectUtil.isEmpty(secondPart)) {
      arrayToJoinWithComma.push(secondPart);
    }

    return arrayToJoinWithComma.join(", ");
  }

  getSizeInSqm() {
    const { size } = this;
    /**
     * According to the stupid misleading documentation,
     * size is in sqm
     * however, if it is non-hdb it is actually sqft
     * any confusion please look for backend
     */
    return size;
  }

  getSizeDisplay() {
    const { size, cdResearchSubtype } = this;
    if (PropertyTypeUtil.isHDB(cdResearchSubtype)) {
      return StringUtil.formatThousand(size) + " Sqm";
    } else {
      return (
        StringUtil.formatThousand(size) + " Sqft"
      );
    }
  }

  getSaleXValue() {
    const { saleXvalue } = this;

    if (!ObjectUtil.isEmpty(saleXvalue)) {
      return saleXvalue.xValue || 0;
    }
    return 0;
  }

  getFormattedSaleXValue() {
    const xValue = this.getSaleXValue();
    if (xValue > 0) {
      return StringUtil.formatCurrency(xValue);
    }
    return "";
  }

  getSaleXValueLast30Days() {
    const { saleXvalue } = this;

    if (!ObjectUtil.isEmpty(saleXvalue)) {
      return saleXvalue.xValue30Days || 0;
    }
    return 0;
  }

  getLastMonthSaleXValueChangesInPercent() {
    /**
     * returning in number or undefine
     */
    const latestXValue = this.getSaleXValue();
    const lastMonthXValue = this.getSaleXValueLast30Days();

    const difference = latestXValue - lastMonthXValue;
    const differenceInPercent = (difference / lastMonthXValue) * 100;

    if (latestXValue > 0 && lastMonthXValue > 0) {
      return differenceInPercent;
    }

    return; //remain undefined
  }

  getSaleXValueLastQuarter() {
    const { saleXvalue } = this;

    if (!ObjectUtil.isEmpty(saleXvalue)) {
      return saleXvalue.xValueLastQuarter || 0;
    }
    return 0;
  }

  getCapitalGainSinceLastQuarter() {
    /**
     * returning in number or undefine
     */
    const latestXValue = this.getSaleXValue();
    const lastQuarterXValue = this.getSaleXValueLastQuarter();

    if (latestXValue > 0 && lastQuarterXValue > 0) {
      return latestXValue - lastQuarterXValue;
    }

    return; //remain undefined
  }

  getRentXValue() {
    const { rentXvalue } = this;

    if (!ObjectUtil.isEmpty(rentXvalue)) {
      return rentXvalue.xValue || 0;
    }
    return 0;
  }

  getFormattedRentXValue() {
    const xValue = this.getRentXValue();
    if (xValue > 0) {
      return StringUtil.formatCurrency(xValue);
    }
    return "";
  }

  getRentXValueLast30Days() {
    const { rentXvalue } = this;

    if (!ObjectUtil.isEmpty(rentXvalue)) {
      return rentXvalue.xValue30Days || 0;
    }
    return 0;
  }

  getLastMonthRentXValueChanges() {
    /**
     * returning in number or undefine
     */
    const latestXValue = this.getRentXValue();
    const lastMonthXValue = this.getRentXValueLast30Days();

    const difference = latestXValue - lastMonthXValue;

    if (latestXValue > 0 && lastMonthXValue > 0) {
      return difference;
    }

    return; //remain undefined
  }

  getLastMonthRentXValueChangesInPercent() {
    /**
     * returning in number or undefine
     */
    const latestXValue = this.getRentXValue();
    const lastMonthXValue = this.getRentXValueLast30Days();

    const difference = latestXValue - lastMonthXValue;
    const differenceInPercent = (difference / lastMonthXValue) * 100;

    if (latestXValue > 0 && lastMonthXValue > 0) {
      return differenceInPercent;
    }

    return; //remain undefined
  }

  getRentalYield() {
    const { rentalYield } = this;

    return rentalYield;
  }

  getOccupancy() {
    //refer to pt_Occupancy
    return this.occupancy;
  }

  getPurpose() {
    //refer to pt_Purpose
    return this.purpose;
  }

  getOwnerOptions() {
    //refer to pt_Occupancy
    return this.ownerOptions; //this is an array
  }

  getOpenToOffersInd() {
    return this.openToOffersInd;
  }

  getRegionalComparison() {
    return this.regionalComparison;
  }

  getTransactedPrice() {
    return this.capitalGainLastTransactedPrice;
  }

  getTransactedDate() {
    return this.capitalGainLastTransactedDate; //format DD MMM YYYY
  }
}

export { SRXPropertyUserPO };
