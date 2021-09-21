import {ObjectUtil} from '../utils';
import request, {multiPartRequest} from './axios';

function addBusinessSponsoredPost({postBusinessSponsored, photos}) {
  const params = {
    action: 'addBusinessSponsoredPost',
    postBusinessSponsored: JSON.stringify(postBusinessSponsored),
  };

  if (!ObjectUtil.isEmpty(photos)) {
    photos.forEach(item => {
      params[item.key] = item.value;
    });
  }

  return multiPartRequest('/api/v1/community/post/business', params);
}

function findPosts({businessId, startIndex, maxResults}) {
  return request({
    url: '/api/v1/community/post/business',
    method: 'GET',
    params: {
      action: 'findPosts',
      startIndex: startIndex ?? 0,
      maxResults: maxResults ?? 10,
      businessId,
    },
  });
}

function getBusinessSponsoredPost({postId}) {
  return request({
    url: '/api/v1/community/post/business',
    method: 'GET',
    params: {
      action: 'getBusinessSponsoredPost',
      id: postId,
    },
  });
}

const CommunitiesPostBusinessService = {
  addBusinessSponsoredPost,
  findPosts,
  getBusinessSponsoredPost,
};

export {CommunitiesPostBusinessService};
