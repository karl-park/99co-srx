import { CommonUtil } from "../utils";
import {
  Listing_XDroneIcon,
  Listing_XDroneLevelIcon,
  Listing_XDroneVideoIcon
} from "../assets";

class DroneViewPO {
  constructor(data) {
    if (data) {
      this.id = data.id;
      this.thumbnailUrl = CommonUtil.handleImageUrl(data.thumbnailUrl);
      this.type = data.type;
      this.url = CommonUtil.handleImageUrl(data.url);

      //MediaItem structure
      this.isWebContent = true;
    }
  }
  getOverlayIcon() {
    if (this.type == 2) {
      return Listing_XDroneLevelIcon;
    } else if (this.type == 3) {
      return Listing_XDroneVideoIcon;
    } else {
      return Listing_XDroneIcon;
    }
  }
}

export { DroneViewPO };
