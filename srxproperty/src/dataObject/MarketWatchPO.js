import { ObjectUtil } from "../utils";

class MarketWatchPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      this.date = data.date;
      this.dateUpd = data.dateUpd;
      this.increase = data.increase;
      this.month = data.month;
      this.monthlyVolChange = data.monthlyVolChange;
      this.year = data.year;
      this.subType = data.subType;
    } //end of data
  } //end of constructor

  getSubType = () => {
    const { subType } = this;

    var subTypeString = "";
    if (!ObjectUtil.isEmpty(subType)) {
      subTypeString = subType;
    }
    return subTypeString;
  };

  getMarketWatchPrice = () => {
    //increase for market watch price
    const { increase } = this;
    if (increase) {
      return increase;
    }
    return null;
  };

  getMarketWatchVolume = () => {
    //monthlyVolChange for market watch volume
    const { monthlyVolChange } = this;
    if (monthlyVolChange) {
      return monthlyVolChange;
    }
    return null;
  };

  getMarketWatchDate = () => {
    const { date } = this;
    if (date) {
      return date;
    }
    return null;
  };

  getLastUpdatedDate = () => {
    const { dateUpd } = this;
    if (dateUpd) {
      return dateUpd;
    }
    return null;
  };
}

export { MarketWatchPO };
