import {ObjectUtil} from './ObjectUtil';
import {CommunityItem} from '../dataObject';

function convertCommunityTopDownPOArray(communities) {
  if (!ObjectUtil.isEmpty(communities)) {

    let communityItems = communities.map(item => {
      //item is CommunitiesTopDownPO
      return convertTopDownPOToCommunityItem(item);
    });

    return communityItems;
  } else {
    return null;
  }
}

function convertTopDownPOToCommunityItem(topDownPO) {
  let communityItem = new CommunityItem(topDownPO.community);
  communityItem.ptUserId = topDownPO.ptUserId;// ***This line is important as this is the only line setting the ptUserId 
  if (!ObjectUtil.isEmpty(topDownPO.children) && Array.isArray(topDownPO.children)) {
    const {children} = topDownPO;
    communityItem.childs = children.map(item => {
      return convertTopDownPOToCommunityItem(item)
    })
  }

  return communityItem;
}

function convertCommunityPOArray(communities) {
  if (!ObjectUtil.isEmpty(communities)) {
    let communitiesWithChilds = communities.map(item => {
      return getCommunityAncestor(item, null);
    });

    //merge if parent is the same
    let combined = combine(communitiesWithChilds);
    return combined;
  } else {
    return null;
  }
}

/**
 * To reverse a communityPO from parent relationship to child relationship
 * Result is a CommunityItem
 * @param {CommunityPO} parentCommunity parentComminity of a CommunityPO, pass a CommunityPO that you want to start with
 * @param {CommunityItem} communityItem CommunityItem converted from the CommunityPO which used to get parentCommunity, pass `null`
 */
function getCommunityAncestor(parentCommunity, communityItem) {
  if (!ObjectUtil.isEmpty(parentCommunity)) {
    let parent = new CommunityItem(parentCommunity);
    if (!ObjectUtil.isEmpty(communityItem)) {
      parent.childs = [...parent.childs, communityItem];
    }
    let parentParent = getCommunityAncestor(
      parentCommunity.parentCommunity,
      parent,
    );
    return parentParent;
  }
  return communityItem;
}

//assuming all most top parent is the same (which is id=1)
//the rest just merge into it, without combining
//the function will be updated again, though I don't know how to do
//TODO: Update the logic
function combine(communityItems) {
  if (communityItems.length == 1) {
    return communityItems;
  } else {
    //always keep the 1st one and merge others in
    let first = communityItems[0];
    for (var i = 1; i < communityItems.length; i++) {
      let compareCommunity = communityItems[i];
      if (first.id === compareCommunity.id) {
        first.childs = [...first.childs, ...compareCommunity.childs];
      }
    }
    return [first];
  }
}

const CommunitiesUtil = {
  convertCommunityPOArray,
  convertCommunityTopDownPOArray,
};

export {CommunitiesUtil};
