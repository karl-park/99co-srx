import {ObjectUtil} from '../utils';

class CommunityPostSponsoredPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      this.dateStart = data.dateStart;
      this.dateEnd = data.dateEnd;
      this.sponsoredType = data.sponsoredType; // 1 - Listing. 2 - Business.
    }
  }
}

export {CommunityPostSponsoredPO};
