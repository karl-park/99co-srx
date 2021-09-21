import { CommonUtil, ObjectUtil } from "../utils";
import { AmenityPO } from "./AmenityPO";

class AmenitiesCategoryPO {
  constructor(data) {
    if (data) {
      if (
        !ObjectUtil.isEmpty(data.amenities) &&
        Array.isArray(data.amenities)
      ) {
        this.amenities = [];
        data.amenities.map(item => {
          this.amenities.push(new AmenityPO(item));
        });
      } else {
        this.amenities = [];
      }
      this.id = data.id;
      this.name = data.name;
    }
  }
}

export { AmenitiesCategoryPO };
