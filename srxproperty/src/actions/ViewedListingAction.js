import { UPDATE_VIEWED_LISTINGS } from "./types";
import { ListingUtil } from "../utils";

const retrieveViewedListings = () => {
    return dispatch => {
        ListingUtil.retrieveViewedListingIdList().then(result => {
            if (result) {
                dispatch({ type: UPDATE_VIEWED_LISTINGS, payload: result });
            }
        });
    }
}

const saveViewedListings = ({ newListingId }) => {
    return dispatch => {
        ListingUtil.retrieveViewedListingIdList().then(result => {
            let viewedListings = [];
            if (result) {
                if (Array.isArray(result)) {
                    if (!result.includes(newListingId)) {
                        viewedListings = [newListingId, ...result];
                    } else {
                        viewedListings = result;
                    }
                }
            } else {
                viewedListings = [newListingId];
            }
            ListingUtil.saveViewedListingId({ viewedListings }).then(result => {
                dispatch({ type: UPDATE_VIEWED_LISTINGS, payload: viewedListings });
            })
        });
    }
}

export {
    retrieveViewedListings,
    saveViewedListings,
}