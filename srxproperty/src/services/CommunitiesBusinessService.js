import request, {multiPartRequest} from './axios';

function getMemberCount({communityIds, cdResearchSubtypes}) {
  console.log('Start get member count');
  console.log(communityIds);
  console.log(cdResearchSubtypes);
  console.log('End get member count');
  return request({
    url: '/api/v1/community',
    method: 'GET',
    params: {
      action: 'getMemberCount',
      communityIds,
      cdResearchSubtypes,
    },
  });
}

function getCommunities(communityLevels) {
  return request({
    url: '/api/v1/community',
    method: 'GET',
    params: {
      communityLevels,
      action: 'getCommunities',
      withMembersOnly: true,
    },
  });
}

function getElectoralCommunityBusiness() {
  return request({
    url: '/api/v1/business',
    method: 'GET',
    params: {
      action: 'getElectoralCommunityBusiness',
    },
  });
}

const CommunitiesBusinessService = {
  getMemberCount,
  getCommunities,
  getElectoralCommunityBusiness,
};

export {CommunitiesBusinessService};
