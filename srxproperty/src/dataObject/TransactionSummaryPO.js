import { ObjectUtil } from "../utils";

class TransactionSummaryPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      this.rentHdbTotal = data.rentHdbTotal;
      this.rentHighest = data.rentHighest;
      this.rentPrivateTotal = data.rentPrivateTotal;
      this.rentTotal = data.rentTotal;
      this.saleHdbTotal = data.saleHdbTotal;
      this.saleHighest = data.saleHighest;
      this.salePrivateTotal = data.salePrivateTotal;
      this.saleTotal = data.saleTotal;
    }
  }
}

export { TransactionSummaryPO };
