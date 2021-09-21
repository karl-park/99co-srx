import { CommonUtil } from "../utils";

class ListingPhotoPO {
  constructor(data) {
    if (data) {
      this.caption = data.caption;
      this.id = data.id;
      this.thumbnailUrl = CommonUtil.handleImageUrl(data.thumbnailUrl);
      this.url = CommonUtil.handleImageUrl(data.url);
    }
  }
}

export { ListingPhotoPO };
