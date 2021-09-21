import {ObjectUtil, StringUtil} from '../utils';

class CommunityPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      this.id = data.id;
      this.cdCommunityLevel = data.cdCommunityLevel;
      this.membersTotal = data.membersTotal;
      this.name = data.name;
      if (!ObjectUtil.isEmpty(data.parentCommunity)) {
        this.parentCommunity = new CommunityPO(data.parentCommunity);
      }
      if (!ObjectUtil.isEmpty(data.geoJson)) {
        console.log(JSON.parse(data.geoJson));
        this.geoJson = JSON.parse(data.geoJson);
      }
    }
  }
}

class CommunityTopDownPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      this.community = new CommunityPO(data.community);
      this.ptUserId = data.ptUserId;
      this.children = [];
      if (!ObjectUtil.isEmpty(data.children) && Array.isArray(data.children)) {
        this.children = data.children.map(item => {
          return new CommunityTopDownPO(item);
        });
      }
    }
  }
}

class CommunityItem {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      this.id = data.id;
      this.cdCommunityLevel = data.cdCommunityLevel;
      if (data.id === 1) {
        this.name = 'My Communities'; //"All of Singapore"
      } else {
        this.name = data.name;
      }
      this.childs = [];
      this.membersTotal = data.membersTotal;
      if (
        !ObjectUtil.isEmpty(data.geoJson) &&
        !ObjectUtil.isEmpty(data.geoJson.features) &&
        !ObjectUtil.isEmpty(data.geoJson.features[0].geometry)
      ) {
        this.geometry = data.geoJson.features[0].geometry;
      }
    }
  }
}

export {CommunityPO, CommunityTopDownPO, CommunityItem};
