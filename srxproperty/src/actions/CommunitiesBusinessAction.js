import {BusinessWithCommunitiesPO} from '../dataObject/BusinessWithCommunitiesPO';
import {CommunitiesBusinessService} from '../services';
import {ObjectUtil} from '../utils';
import {
  LOAD_COMMUNITIES_BUSINESS,
  LOAD_COMMUNITIES_BUSINESS_FAILED,
  LOAD_COMMUNITIES_BUSINESS_SUCCESS,
  CLEAR_COMMUNITIES_BUSINESS,
} from './types';

const getElectoralCommunityBusiness = () => {
  return dispatch => {
    dispatch({type: LOAD_COMMUNITIES_BUSINESS});

    CommunitiesBusinessService.getElectoralCommunityBusiness()
      .then(response => {
        if (
          !ObjectUtil.isEmpty(response) &&
          !ObjectUtil.isEmpty(response.businessWithCommunitiesPO)
        ) {
          const businessWithCommunitiesPO = new BusinessWithCommunitiesPO(
            response.businessWithCommunitiesPO,
          );
          dispatch({
            type: LOAD_COMMUNITIES_BUSINESS_SUCCESS,
            payload: businessWithCommunitiesPO,
          });
        } else {
          dispatch({
            type: LOAD_COMMUNITIES_BUSINESS_SUCCESS,
            payload: null,
          });
        }
      })
      .catch(error => {
        dispatch({
          type: LOAD_COMMUNITIES_BUSINESS_FAILED,
          payload: error,
        });
      });
  };
};

const clearBusiness = () => {
  return {type: CLEAR_COMMUNITIES_BUSINESS, payload: null};
};

export {getElectoralCommunityBusiness, clearBusiness};
