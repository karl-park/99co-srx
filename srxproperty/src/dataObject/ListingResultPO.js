import { ObjectUtil } from "../utils";
import { ListingPO } from "../dataObject";

class ListingResultPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      this.total = data.total;
      this.type = data.type;

      // for listing
      this.listingPOs = [];
      if (!ObjectUtil.isEmpty(data.listingPOs)) {
        data.listingPOs.map(item => {
          this.listingPOs.push(new ListingPO(item));
        });
      }
    }
  }
}

export { ListingResultPO };
