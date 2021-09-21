import { ObjectUtil } from "../../../../utils";
import { pt_Occupancy } from "../../../../constants";

showSaleInformation = trackerPO => {
  /**
   * My Residence, My Investment property & Property of interest
   *    ==> show sale xvalue, etc
   *
   * My rented property
   *    ==> show rental xvalue
   *
   * if occupancy is not selected (from older version which didn't have this parameter)
   *   ==> show as default (sale xvalue)
   */
  let canShow = false;
  if (!ObjectUtil.isEmpty(trackerPO)) {
    canShow = true;
    const occupancy = trackerPO.getOccupancy();
    if (occupancy === pt_Occupancy.Rented) {
      canShow = false;
    }
  }

  return canShow;
};

const PropertyTrackerUtil = {
  showSaleInformation
};

export { PropertyTrackerUtil };
