import {ObjectUtil} from '../utils';
import {BusinessPO} from './BusinessPO';
import {CommunityPO} from './CommunityPO';

class BusinessWithCommunitiesPO {
  constructor(data) {
    if (data) {
      if (!ObjectUtil.isEmpty(data.business)) {
        this.business = new BusinessPO(data.business);
      }
      if (
        !ObjectUtil.isEmpty(data.communities) &&
        Array.isArray(data.communities)
      ) {
        this.communities = data.communities.map(item => new CommunityPO(item));
      }
    }
  }
}

export {BusinessWithCommunitiesPO};
