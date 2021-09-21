import {ObjectUtil} from '../utils';

class UserPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      this.email = data.email;
      this.id = data.id;
      if (data.encryptedId) this.encryptedUserId = data.encryptedId;
      else if (data.encryptedUserId)
        this.encryptedUserId = data.encryptedUserId;
      this.mobileCountryCode = data.mobileCountryCode;
      this.mobileLocalNum = data.mobileLocalNum;
      this.mobileVerified = data.mobileVerified;
      this.name = data.name;
      this.photo = data.photo;
      this.communityAlias = data.communityAlias;
      this.agent = data.agent; //boolean
    }
  }

  getCommunityPostUserName() {
    if (!ObjectUtil.isEmpty(this.communityAlias)) {
      return this.communityAlias;
    } else {
      return this.name;
    }
  }
}

export {UserPO};
