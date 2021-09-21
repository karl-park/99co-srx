import request, {multiPartRequest} from './axios';
import {ObjectUtil} from '../utils';

function addUpdateComment({id, postId, comment, photos}) {
  const params = {
    action: 'addUpdateComment',
    id,
    postId,
    comment,
  };

  if (!ObjectUtil.isEmpty(photos)) {
    photos.forEach(item => {
      params[item.key] = item.value;
    });
  }

  return multiPartRequest('/api/v1/community/post', params);
}

function deleteComments({commentIds}) {
  return request({
    url: '/api/v1/community/post?action=deleteComments',
    method: 'POST',
    data: commentIds,
  });
}

function deletePosts({postIds}) {
  return request({
    url: '/api/v1/community/post?action=deletePosts',
    method: 'POST',
    data: postIds,
  });
}

function deleteMedia({media}) {
  return request({
    url: '/api/v1/community/post?action=deleteMedia',
    method: 'POST',
    data: media,
  });
}

function getNotifications({startIndex, maxResults}) {
  return request({
    url: '/api/v1/community/notification',
    method: 'GET',
    params: {
      action: 'getNotifications',
      startIndex,
      maxResults,
    },
  });
}

function getComments({postId, startIndex, maxResults, orderBy}) {
  return request({
    url: '/api/v1/community/post',
    method: 'GET',
    params: {
      action: 'getComments',
      postId,
      startIndex,
      maxResults,
      orderBy,
    },
  });
}

function getCommunitiesTopDown() {
  return request({
    url: '/api/v1/community/onboarding',
    method: 'GET',
    params: {
      action: 'getCommunitiesTopDown',
    },
  });
}

function getCommunities() {
  return request({
    url: '/api/v1/community/onboarding',
    method: 'GET',
    params: {
      action: 'getCommunities',
    },
  });
}

function getCommunityByLocation({latitude, longitude}) {
  return request({
    url: '/api/v1/community/onboarding',
    method: 'GET',
    params: {
      action: 'getCommunityByLocation',
      latitude,
      longitude,
    },
  });
}

function getPost({id}) {
  return request({
    url: '/api/v1/community/post',
    method: 'GET',
    params: {
      action: 'getPost',
      id,
    },
  });
}

function getPostUrl({id}) {
  return request({
    url: '/api/v1/community/post',
    method: 'GET',
    params: {
      action: 'getPostUrl',
      id,
    },
  });
}

function getSampleCommunity() {
  return request({
    url: '/api/v1/community/onboarding',
    method: 'GET',
    params: {
      action: 'getSampleCommunity',
    },
  });
}

function postReaction({id, reactionId}) {
  return request({
    url: '/api/v1/community/post/reaction',
    method: 'POST',
    params: {
      action: 'postReaction',
      id,
      reactionId,
    },
  });
}

function getUnreadCount() {
  return request({
    url: '/api/v1/community/notification',
    method: 'GET',
    params: {
      action: 'getUnreadCount',
    },
  });
}

function markAsRead({id}) {
  return request({
    url: '/api/v1/community/notification',
    method: 'POST',
    params: {
      action: 'markAsRead',
      id,
    },
  });
}

function findPosts({
  startIndex,
  maxResults,
  orderBy,
  communityId,
  types,
  removeHtml,
}) {
  return request({
    url: '/api/v1/community/post',
    method: 'GET',
    params: {
      action: 'findPosts',
      startIndex: startIndex ?? 0,
      maxResults: maxResults ?? 10,
      orderBy,
      communityId,
      types,
      removeHtml,
    },
  });
}

function sharePost({postId, communityId, comment}) {
  var formData = {postId: postId, communityId: communityId, comment: comment};
  return request({
    url: '/api/v1/community/post?action=sharePost',
    method: 'POST',
    data: formData,
  });
}

function addUpdatePost({post, community, photos}) {
  let params = {
    action: 'addUpdatePost',
    post: JSON.stringify(post),
    community: JSON.stringify(community),
  };
  if (!ObjectUtil.isEmpty(photos)) {
    photos.forEach(item => {
      for (var key in item) {
        const value = item[key];
        if (value) {
          params[key] = value;
        }
      }
    });
  }
  return multiPartRequest('/api/v1/community/post', params);
}

function getRelatedCommunitiesToShare({postId, communityId}) {
  return request({
    url: '/api/v1/community/post',
    method: 'GET',
    params: {
      action: 'getRelatedCommunitiesToShare',
      postId,
      communityId,
    },
  });
}

function getRelatedCommunitiesTopDownToShare({postId, communityId}) {
  return request({
    url: '/api/v1/community/post',
    method: 'GET',
    params: {
      action: 'getRelatedCommunitiesTopDownToShare',
      postId,
      communityId,
    },
  });
}

function reportAbuse({postId, reason}) {
  return request({
    url: '/api/v1/community/post',
    method: 'POST',
    params: {
      action: 'reportAbuse',
    },
    data: {
      postId,
      reason,
    },
  });
}

function findCommunityAdvisor({communityId}) {
  return request({
    url: '/api/v1/community/post',
    params: {
      action: 'findCommunityAdvisor',
      communityId,
    },
  });
}

function trackCommunityAdvisorImpressionAction({impressionId, trackAction}) {
  return request({
    url: '/api/v1/community/post',
    method: 'POST',
    params: {
      action: 'trackCommunityAdvisorImpressionAction',
      impressionId,
      trackAction,
    },
  });
}

const CommunitiesService = {
  addUpdateComment,
  deleteComments,
  deletePosts,
  deleteMedia,
  getNotifications,
  getUnreadCount,
  getComments,
  getCommunities,
  getCommunitiesTopDown,
  getCommunityByLocation,
  getPost,
  getPostUrl,
  getSampleCommunity,
  markAsRead,
  findPosts,
  sharePost,
  postReaction,
  addUpdatePost,
  getRelatedCommunitiesToShare,
  getRelatedCommunitiesTopDownToShare,
  reportAbuse,
  findCommunityAdvisor,
  trackCommunityAdvisorImpressionAction
};

export {CommunitiesService};
