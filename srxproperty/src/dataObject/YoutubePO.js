import { CommonUtil, ObjectUtil } from "../utils";
import { Listing_YoutubeIcon } from "../assets";

class YoutubePO {
    constructor(data) {
        if (data) {
            this.id = data.id;
            this.url = "";
            this.thumbnailUrl = "";
            if (!ObjectUtil.isEmpty(data.url)) {
                var regExp = /^.*(youtu\.be\/|v\/|u\/\w\/|embed\/|watch\?v=|\&v=)([^#\&\?]*).*/;
                var match = data.url.match(regExp);
                if (match && match[2].length == 11) {
                    this.url = "https://www.youtube.com/embed/"+match[2];
                    this.thumbnailUrl = "https://img.youtube.com/vi/"+match[2]+"/default.jpg";
                } 
            }

            //MediaItem structure
            this.isWebContent = true;
        }
    }
    getOverlayIcon() {
        return Listing_YoutubeIcon;
    }
}

export { YoutubePO };
