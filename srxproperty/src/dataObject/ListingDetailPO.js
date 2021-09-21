import { ObjectUtil, PropertyTypeUtil, CommonUtil, StringUtil } from "../utils";
import { ListingPO, AmenitiesCategoryPO } from "../dataObject";

class ListingDetailPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      this.listingPO = new ListingPO(data.listingPO);

      this.completeDate = data.completeDate;
      this.developer = data.developer;
      this.facilities = data.facilities;
      this.features = data.features;
      this.fixtures = data.fixtures;
      this.fuzzyLatitude = data.fuzzyLatitude;
      this.fuzzyLongitude = data.fuzzyLongitude;
      this.latitude = data.latitude;
      this.longitude = data.longitude;
      this.amenitiesCategories = [];
      if (
        !ObjectUtil.isEmpty(data.amenitiesCategories) &&
        Array.isArray(data.amenitiesCategories)
      ) {
        data.amenitiesCategories.map(item => {
          this.amenitiesCategories.push(new AmenitiesCategoryPO(item));
        });
      }
    }
  }
}

export { ListingDetailPO };
