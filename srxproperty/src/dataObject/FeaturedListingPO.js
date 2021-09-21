import { ObjectUtil } from "../utils";

class FeaturedListingPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      this.category = data.category; //1 - Featured Listing. 2 - Featured Plus Listing
    }
  }
}

export { FeaturedListingPO };
