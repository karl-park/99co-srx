import { CommonUtil, ObjectUtil } from "../utils";
import { AgentCvPO } from "./AgentCvPO";
import { ListingSummaryPO } from "./ListingSummaryPO";
import { TransactionSummaryPO } from "./TransactionSummaryPO";

class AgentPO {
  constructor(data) {
    if (data) {
      const {
        agency,
        agencyName,
        agencyLogo,
        agentCvPO,
        agentCvUrl,
        ceaRegNo,
        contactNumber, //from contactInformation
        email,
        emailAddress, //from contactInformation
        encryptedUserId,
        eyecatcherMsg,
        id,
        licenseNumber,
        listingSummary,
        mobile,
        name,
        numOfListings,
        photo,
        subscription,
        trackingId,
        transactionSummary,
        userId
      } = data;
      this.agency = agency;
      this.agencyName = agencyName;
      this.agencyLogo = agencyLogo;

      this.agentCvPO = null;
      if (!ObjectUtil.isEmpty(agentCvPO)) {
        this.agentCvPO = new AgentCvPO(agentCvPO);
      }

      this.agentCvUrl = agentCvUrl;
      this.ceaRegNo = ceaRegNo;

      if (mobile) this.mobile = mobile;
      else if (contactNumber) this.mobile = contactNumber;

      if (email) this.email = email;
      else if (emailAddress) this.email = emailAddress;

      this.encryptedUserId = encryptedUserId;
      this.eyecatcherMsg = eyecatcherMsg;
      this.id = id;
      this.mobile = mobile;

      if (id) this.id = id;
      else if (userId) this.id = userId;

      this.listingSummary = new ListingSummaryPO(listingSummary);
      this.licenseNumber = licenseNumber;
      this.displayFullMobile = false; //self define variable
      this.name = name;
      this.numOfListings = numOfListings;
      this.photo = photo;
      this.subscription = subscription;
      this.trackingId = trackingId;

      this.transactionSummary = new TransactionSummaryPO(transactionSummary);

      this.userId = userId;
    }
  }

  getAgentId = () => {
    return this.userId;
  };

  getAgentCvPO = () => {
    const { agentCvPO } = this;
    return agentCvPO;
  };

  getMobileNumber = () => {
    if (this.mobile) {
      var mobileString = "";
      if (typeof this.mobile === "string") {
        mobileString = this.mobile.replace(/[\(\)]+/g, "");
      } else if (typeof this.mobile === "number") {
        mobileString = this.mobile.toString();
      }
    }

    return mobileString;
  };

  getMobileNumberForDisplay = () => {
    var mobileString = this.getMobileNumber();

    if (this.displayFullMobile) {
      return mobileString;
    } else {
      if (mobileString.length > 4) {
        return mobileString.substring(0, 4) + " XXXX";
      }
    }

    return mobileString;
  };

  isExpert = () => {
    return (
      this.subscription &&
      (this.subscription === "HRD" || this.subscription === "Expert")
    );
  };

  getAgentPhoto = () => {
    if (!ObjectUtil.isEmpty(this.photo)) {
      return CommonUtil.handleImageUrl(this.photo);
    } else if (!ObjectUtil.isEmpty(this.id) || this.id > 0) {
      return CommonUtil.handleImageUrl(
        "/mobile/iphone/photo.mobile3?type=A&userId=" +
          this.id +
          "&userid=" +
          this.id
      );
    }
    return "";
  };

  getAgencyLogoURL = () => {
    if (!ObjectUtil.isEmpty(this.agencyLogo)) {
      return CommonUtil.handleImageUrl(this.agencyLogo);
    }
    return "";
  };

  getExpertAnalyzerAgent = () => {
    let expertSubscriptionString = "";
    if (this.isExpert()) {
      expertSubscriptionString = this.subscription + " Analyzer";
    }
    return expertSubscriptionString;
  };

  getActiveListings = () => {
    let activeListings = "";
    if (this.numOfListings && this.numOfListings > 0) {
      if (this.numOfListings === 1) {
        activeListings = this.numOfListings + " Active Listing";
      } else {
        activeListings = this.numOfListings + " Active Listings";
      }
    }
    return activeListings;
  };
}

export { AgentPO };
