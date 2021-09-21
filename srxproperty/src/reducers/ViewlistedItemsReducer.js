import { UPDATE_VIEWED_LISTINGS } from "../actions";

const INITIAL_STATE = { viewlistedItems: [] }

const updateViewListedItemsToState = ({ originalState, newViewLists }) => {
    return {
        ...originalState,
        viewlistedItems: newViewLists
    };
}

export default (state = INITIAL_STATE, action) => {

    switch (action.type) {
        case UPDATE_VIEWED_LISTINGS:
            return updateViewListedItemsToState({
                originalState: state,
                newViewLists: action.payload
            });
        default:
            return state;
    }
};
