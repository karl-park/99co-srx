import { UPDATE_SHORTLIST_ITEM, UPDATE_SHORTLIST_STATE } from "../actions";
import { LoadingState } from "../constants";

const INITIAL_STATE = {
  shortlistedItems: [],
  shortlistedListingIds: [],
  loadingState: LoadingState.Normal
};

const getShortlistedListingIds = shortlistPOArray => {
  const shortlistedListingIds = [];
  if (Array.isArray(shortlistPOArray)) {
    shortlistPOArray.map((item, index) => {
      shortlistedListingIds.push(item.refId);
    });
  }
  return shortlistedListingIds;
};

const updateShortlistedItemToState = ({ originalState, newShortlists }) => {
  const shortlistedListingIds = getShortlistedListingIds(newShortlists);
  return {
    ...originalState,
    shortlistedItems: newShortlists,
    shortlistedListingIds,
    loadingState: LoadingState.Loaded //changed to loaded
  };
};

export default (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case UPDATE_SHORTLIST_ITEM:
      return updateShortlistedItemToState({
        originalState: state,
        newShortlists: action.payload
      });
    case UPDATE_SHORTLIST_STATE:
      return Object.assign({}, state, {
        loadingState: action.shortlistState //log in failed
      });
    default:
      return state;
  }
};
