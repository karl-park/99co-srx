import {
  LOAD_COMMUNITIES,
  LOAD_COMMUNITIES_SUCCESS,
  LOAD_COMMUNITIES_FAILED,
  LOAD_COMMUNITIES_PREVIEW,
  LOAD_COMMUNITIES_PREVIEW_SUCCESS,
  LOAD_COMMUNITIES_PREVIEW_FAILED,
  SELECT_COMMUNITY,
  CLEAR_COMMUNITIES,
  SELECT_SORTBY,
} from './types';
import {ObjectUtil, CommonUtil} from '../utils';
import {CommunitiesService} from '../services';
import {CommunityPO, CommunityPostPO, CommunityTopDownPO} from '../dataObject';

const getCommunities = () => {
  return dispatch => {
    dispatch({type: LOAD_COMMUNITIES});

    CommunitiesService.getCommunitiesTopDown()
      .then(resp => {
        if (!ObjectUtil.isEmpty(resp)) {
          const errorMsg = CommonUtil.getErrorMessageFromSRXResponse(resp);
          if (!ObjectUtil.isEmpty(errorMsg)) {
            dispatch({
              type: LOAD_COMMUNITIES_FAILED,
            });
          } else {
            const {result, communitiesTopDown} = resp;
            if (
              !ObjectUtil.isEmpty(communitiesTopDown) &&
              Array.isArray(communitiesTopDown)
            ) {
              const communitiesArray = communitiesTopDown.map(
                item => new CommunityTopDownPO(item),
              );
              dispatch({
                type: LOAD_COMMUNITIES_SUCCESS,
                payload: communitiesArray,
              });
            } else {
              dispatch({
                type: LOAD_COMMUNITIES_SUCCESS,
                payload: [],
              });
            }
          }
        } else {
          dispatch({
            type: LOAD_COMMUNITIES_FAILED,
          });
        }
      })
      .catch(error => {
        dispatch({type: LOAD_COMMUNITIES_FAILED});
      });
  };
};

const clearCommunities = () => {
  return {type: CLEAR_COMMUNITIES, payload: []};
};

const selectCommunity = ({communityItem}) => {
  return {type: SELECT_COMMUNITY, payload: communityItem};
};

const selectSortBy = ({sortByItem}) => {
  return {type: SELECT_SORTBY, payload: sortByItem};
};

const loadPreviewPosts = () => {
  return dispatch => {
    dispatch({type: LOAD_COMMUNITIES_PREVIEW});
    let postType = '1,3,5'; // normal, listing, news, unable to use CommunitiesConstant here
    CommunitiesService.findPosts({
      startIndex: 0,
      maxResults: 5,
      removeHtml: true,
      types: postType,
    })
      .then(response => {
        const {posts} = response;
        const newPosts = [];

        if (!ObjectUtil.isEmpty(posts)) {
          posts.map(item => {
            newPosts.push(new CommunityPostPO(item));
          });
        }
        dispatch({
          type: LOAD_COMMUNITIES_PREVIEW_SUCCESS,
          payload: newPosts,
        });
      })
      .catch(error => {
        dispatch({type: LOAD_COMMUNITIES_PREVIEW_FAILED, payload: error});
      });
  };
};

export {
  getCommunities,
  clearCommunities,
  selectCommunity,
  selectSortBy,
  loadPreviewPosts,
};
