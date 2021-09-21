import { ObjectUtil, CommonUtil, DebugUtil } from "../utils";

class OnlineCommunicationPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      this.content = data.content;
      this.contentShort = data.contentShort;
      this.datePosted = data.datePosted;
      this.encryptedId = data.encryptedId;
      this.imageUrl = data.imageUrl;
      this.imageUrlSmall = data.imageUrlSmall;
      this.slug = data.slug;
      this.source = data.source;
      this.title = data.title;
    }
  }

  getImageUrl = () => {
    if (!ObjectUtil.isEmpty(this.imageUrl)) {
      return CommonUtil.handleImageUrl(this.imageUrl);
    }
    return "";
  };

  getImageSmallUrl = () => {
    if (!ObjectUtil.isEmpty(this.imageUrlSmall)) {
      return CommonUtil.handleImageUrl(this.imageUrlSmall);
    }
    return "";
  };

  getNewsURL = () => {
    const newsId = this.getNewsId();
    if (!ObjectUtil.isEmpty(newsId)) {
      const url = DebugUtil.retrieveStoreDomainURL();
      return url + "/singapore-property-news/" + newsId + "/" + this.slug;
    }
    return null;
  };

  //encryptedId d5c2t7v5o2b to 52752
  getNewsId = () => {
    const { encryptedId } = this;
    if (encryptedId) {
      const result = encryptedId.match(/\d/g).join("");
      return result;
    }
    return null;
  };
}

export { OnlineCommunicationPO };
