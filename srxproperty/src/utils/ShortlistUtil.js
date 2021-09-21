import { store } from "../store";
import { ObjectUtil } from "./ObjectUtil";

isShortlist = ({ listingId }) => {
  const shortlistData = store.getState().shortlistData;
  if (!ObjectUtil.isEmpty(shortlistData)) {
    const { shortlistedListingIds } = shortlistData;
    if (!ObjectUtil.isEmpty(shortlistedListingIds)) {
      var listingId_number = listingId;
      if (typeof listingId === "string") {
        listingId_number = parseInt(listingId);
      }
      return shortlistedListingIds.includes(listingId_number);
    }
  }
  return false;
};

getShortlistPOWithListingId = ({ listingId }) => {
  const shortlistData = store.getState().shortlistData;
  if (!ObjectUtil.isEmpty(shortlistData)) {
    const { shortlistedListingIds, shortlistedItems } = shortlistData;
    if (!ObjectUtil.isEmpty(shortlistedListingIds)) {
      var listingId_number = listingId;
      if (typeof listingId === "string") {
        listingId_number = parseInt(listingId);
      }
      const itemIndex = shortlistedListingIds.indexOf(listingId_number);
      if (itemIndex >= 0 && shortlistedItems.length > itemIndex) {
        return shortlistedItems[itemIndex];
      }
    }
  }
  return null;
};

const ShortlistUtil = {
  isShortlist,
  getShortlistPOWithListingId
};

export { ShortlistUtil };
