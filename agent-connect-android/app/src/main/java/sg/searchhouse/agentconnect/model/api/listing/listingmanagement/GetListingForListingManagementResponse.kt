package sg.searchhouse.agentconnect.model.api.listing.listingmanagement

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.listing.ListingEditPO
import sg.searchhouse.agentconnect.model.api.listing.ListingOwnerCertificationPO

data class GetListingForListingManagementResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("listingEditPO")
    val listingEditPO: ListingEditPO,
    @SerializedName("ownerCertification")
    val ownerCertification: ListingOwnerCertificationPO
)