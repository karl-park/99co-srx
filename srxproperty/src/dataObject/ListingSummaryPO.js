import { ObjectUtil } from "../utils";

class ListingSummaryPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      this.rentTotal = data.rentTotal;
      this.saleTotal = data.saleTotal;
    }
  }
}

export { ListingSummaryPO };
