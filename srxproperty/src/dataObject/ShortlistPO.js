import { ListingPO } from "./ListingPO";
import { ObjectUtil, PropertyTypeUtil, CommonUtil, StringUtil } from "../utils";

class ShortlistPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      if (data.id) {
        if (typeof data.id === "string") {
          this.key = data.id;
        } else if (typeof data.id === "number") {
          this.key = data.id.toString();
        } else {
          this.key = data.key;
        }
      }
      this.agentConnectTagId = data.agentConnectTagId;
      this.appointment = data.appointment; //to be changed to corresponsding object class
      this.appointmentState = data.appointmentState;
      this.dateCreated = data.dateCreated;
      this.encryptedRefId = data.encryptedRefId;
      this.id = data.id;
      this.inspection = data.inspection; //to be changed to corresponsding object class
      this.inspectionState = data.inspectionState;
      this.offers = data.offers;
      //  ... add yourself if other argument needed
      this.refId = data.refId;
      if (!ObjectUtil.isEmpty(data.refListing))
        this.refListing = new ListingPO(data.refListing);
    }
  }
}

export { ShortlistPO };
