import {ObjectUtil} from '../utils';
import {CommunityPostPO} from './CommunityPostPO';

class CommunityPostBusinessSponsoredPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      this.post = new CommunityPostPO(data.post);
      this.cdResearchSubtypes = data.cdResearchSubtypes;
    }
  }
}

export {CommunityPostBusinessSponsoredPO};
