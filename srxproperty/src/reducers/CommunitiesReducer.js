import {
  LOAD_COMMUNITIES,
  LOAD_COMMUNITIES_SUCCESS,
  LOAD_COMMUNITIES_FAILED,
  CLEAR_COMMUNITIES,
  SELECT_COMMUNITY,
  SELECT_SORTBY,
  LOAD_COMMUNITIES_PREVIEW,
  LOAD_COMMUNITIES_PREVIEW_SUCCESS,
  LOAD_COMMUNITIES_PREVIEW_FAILED,
} from '../actions';
import {LoadingState} from '../constants';

const INITIAL_STATE = {
  list: [],
  loadingState: LoadingState.Normal,
  previewList: [],
  previewLoadingState: LoadingState.Normal,
  //Not sure why I can't initialize an object class here.
  //Use normal object 1st, and will be updated with CommunityItem when calling action `selectCommunity`
  selectedCommunity: {
    id: 1,
    cdCommunityLevel: 144,
    name: 'My Communities', //'All of Singapore',
  },
  selectedSortBy: {
    title: 'Most recent',
    value: 'MOST_RECENT',
  },
};

export default (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case LOAD_COMMUNITIES:
      return {
        ...state,
        loadingState: LoadingState.Loading,
      };
    case LOAD_COMMUNITIES_SUCCESS:
      return {
        ...state,
        list: action.payload,
        loadingState: LoadingState.Loaded,
      };
    case LOAD_COMMUNITIES_FAILED:
      return {
        ...state,
        loadingState: LoadingState.Failed,
      };
    case CLEAR_COMMUNITIES:
      return {
        ...state,
        ...INITIAL_STATE,
      };
    case SELECT_COMMUNITY:
      return {
        ...state,
        selectedCommunity: action.payload,
      };
    case SELECT_SORTBY:
      return {
        ...state,
        selectedSortBy: action.payload,
      };
    case LOAD_COMMUNITIES_PREVIEW:
      return {
        ...state,
        previewLoadingState: LoadingState.Loading,
      };
    case LOAD_COMMUNITIES_PREVIEW_SUCCESS:
      return {
        ...state,
        previewList: action.payload,
        previewLoadingState: LoadingState.Loaded,
      };
    case LOAD_COMMUNITIES_PREVIEW_FAILED:
      return {
        ...state,
        previewLoadingState: LoadingState.Failed,
      };
    default:
      return state;
  }
};
