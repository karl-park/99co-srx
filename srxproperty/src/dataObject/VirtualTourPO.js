import { CommonUtil } from "../utils";
import { Listing_V360Icon } from "../assets";

class VirtualTourPO {
  constructor(data) {
    if (data) {
      this.id = data.id;
      this.thumbnailUrl = CommonUtil.handleImageUrl(data.thumbnailUrl);
      this.url = CommonUtil.handleImageUrl(data.url);

      //MediaItem structure
      this.isWebContent = true;
    }
  }
  getOverlayIcon() {
    return Listing_V360Icon;
  }
}

export { VirtualTourPO };
