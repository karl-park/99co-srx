import {
  LOAD_COMMUNITIES_BUSINESS,
  LOAD_COMMUNITIES_BUSINESS_SUCCESS,
  LOAD_COMMUNITIES_BUSINESS_FAILED,
  CLEAR_COMMUNITIES_BUSINESS,
} from '../actions';
import {LoadingState} from '../constants';
import {ObjectUtil} from '../utils';

const INITIAL_STATE = {
  loadingState: LoadingState.Normal,
  business: null,
  communities: [],
};

export default (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case LOAD_COMMUNITIES_BUSINESS:
      return {
        ...state,
        loadingState: LoadingState.Loading,
      };
    case LOAD_COMMUNITIES_BUSINESS_SUCCESS:
      if (!ObjectUtil.isEmpty(action.payload)) {
        return {
          ...state,
          loadingState: LoadingState.Loaded,
          business: action.payload.business,
          communities: action.payload.communities ?? [],
        };
      } else {
        return {
          ...state,
          loadingState: LoadingState.Loaded,
          business: null,
          communities: [],
        };
      }
    case LOAD_COMMUNITIES_BUSINESS_FAILED:
      return {
        ...state,
        loadingState: LoadingState.Failed,
        business: null,
        communities: [],
      };
    case CLEAR_COMMUNITIES_BUSINESS:
      return INITIAL_STATE;
    default:
      return state;
  }
};
