import {ObjectUtil} from '../utils';

class CommunityPostMediaPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      this.id = data.id;
      this.type = data.type;
      this.mediaType = data.mediaType;
      this.url = data.url;
      this.thumbnailUrl = data.urlThumbnail;
      this.dateCreate = data.dateCreate;
      this.postId = data.postId;
    }
  }
}

export {CommunityPostMediaPO};
