import {ObjectUtil} from '../utils';

class ListingRemoteOptionPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      this.id = data.id;
      this.videoCallInd = data.videoCallInd;
    }
  }
}

export {ListingRemoteOptionPO};
