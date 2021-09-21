import {
  LOAD_MY_PROPERTIES,
  LOAD_MY_PROPERTIES_SUCCESS,
  LOAD_MY_PROPERTIES_FAILED,
  REMOVE_MY_PROPERTY,
  UPDATE_MY_PROPERTY,
} from '../actions';
import {LoadingState} from '../constants';
import {SRXPropertyUserPO} from '../dataObject';
import {ObjectUtil} from '../utils';

const INITIAL_STATE = {list: [], loadingState: LoadingState.Normal};

export default (state = INITIAL_STATE, action) => {
  const {list} = state;

  switch (action.type) {
    case LOAD_MY_PROPERTIES:
      return {
        ...state,
        list: action.payload,
        loadingState: LoadingState.Loading,
      };
    case LOAD_MY_PROPERTIES_SUCCESS:
      return {
        ...state,
        list: action.payload,
        loadingState: LoadingState.Loaded,
      };
    case LOAD_MY_PROPERTIES_FAILED:
      return {
        ...state,
        loadingState: LoadingState.Failed,
      };
    case REMOVE_MY_PROPERTY:
      const newPropertyArray = [];
      if (!ObjectUtil.isEmpty(list)) {
        list.forEach(item => {
          if (
            item instanceof SRXPropertyUserPO &&
            item.ptUserId !== action.payload.ptUserId
          ) {
            newPropertyArray.push(item);
          }
        });
      }
      return {
        ...state,
        list: newPropertyArray,
      };
    case UPDATE_MY_PROPERTY:
      const {originalTrackerPO, newTrackerPO} = action.payload;
      var index = list.findIndex(
        item => item.ptUserId === originalTrackerPO.ptUserId,
      );
      if (index >= 0) {
        let newList = [...list];
        newList.splice(index, 1, newTrackerPO);
        return {
          ...state,
          list: newList,
        };
      }
    default:
      return state;
  }
};
