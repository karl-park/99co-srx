import {LOGIN_HAS_ERROR, SAVE_LOGIN_USER_PO, NOT_LOGIN} from '../actions';
import {LoadingState} from '../constants';

const INITIAL_STATE = {
  errorMessage: {},
  userPO: null,
  loadingState: LoadingState.Normal,
};

export default (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case LOGIN_HAS_ERROR:
      return Object.assign({}, state, {
        errorMessage: {
          ...INITIAL_STATE.errorMessage,
          errorResponse: action.errorMessage,
        },
        loadingState: LoadingState.Failed, //changed to failed
      });

    case SAVE_LOGIN_USER_PO:
      return Object.assign({}, state, {
        userPO: action.userPO,
        loadingState: LoadingState.Loaded, //changed to loaded
      });

    case NOT_LOGIN:
      return Object.assign({}, state, {
        loadingState: LoadingState.Failed, //log in failed
      });

    default:
      return state;
  }
};
