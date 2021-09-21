import request from "./axios";
import { store } from "../store";
import { ObjectUtil } from "../utils";

function getAllShortListedItemsByUser({ sortBy }) {
  var encryptedUserId;
  const userPO = store.getState().loginData.userPO;
  if (!ObjectUtil.isEmpty(userPO)) {
    encryptedUserId = userPO.encryptedUserId;
  }

  return request({
    url: "/mobile/iphone/consumer3.mobile3",
    method: "POST",
    params: {
      action: "getAllShortListedItemsByUser",
      getUserId: encryptedUserId,
      sortBy
    }
  });
}

function getSharedListingsUrl({ listingIds }) {
  return request({
    url: "/srx/getSharedListingsUrl/listing.srx?",
    method: "GET",
    params: {
      action: "getSharedListingsUrl",
      listingIds
    }
  });
}

/*
 * Shortlist 1 listing
 */
function shortListItem({ refType, encryptedRefId }) {
  return shortListItems({ items: [{ refType, encryptedRefId }] });
}

/*
 *  Shortlist array of listings
 * items = [{ refType: xx, encryptedRefId: xx}, { refType: xx, encryptedRefId: xx}, ...]
 */
function shortListItems({ items }) {
  return request({
    url: "/mobile/iphone/consumer3.mobile3",
    method: "POST",
    params: {
      action: "shortListItem",
      items: JSON.stringify(items)
    }
  });
}

/*
 * remove 1 shortlist
 */
function removeShortlist({ shortlistId }) {
  return deleteShortListedItemsById({ shortlistIds: [shortlistId] });
}

/*
 * remove multiple shortlists
 * items = [xxx,xxx, ...]
 */
function deleteShortListedItemsById({ shortlistIds }) {
  return request({
    url: "/mobile/iphone/consumer3.mobile3",
    method: "POST",
    params: {
      action: "deleteShortListedItemsById",
      itemIds: JSON.stringify(shortlistIds) //an array of shortlist id
      //omitListingDetail: "Y",
    }
  });
}

const ShortlistService = {
  getAllShortListedItemsByUser,
  getSharedListingsUrl,
  shortListItem,
  shortListItems,
  removeShortlist,
  deleteShortListedItemsById
};

export { ShortlistService };
