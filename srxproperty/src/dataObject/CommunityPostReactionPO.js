import {ObjectUtil} from '../utils';

class CommunityPostReactionPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      this.cdReaction = data.cdReaction;
      this.name = data.name;
      this.count = data.count;
      this.iconUrl = data.iconUrl;
      this.reacted = data.reacted;
    }
  }
}

export {CommunityPostReactionPO};
