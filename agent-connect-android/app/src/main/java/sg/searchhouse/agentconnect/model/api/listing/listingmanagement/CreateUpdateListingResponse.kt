package sg.searchhouse.agentconnect.model.api.listing.listingmanagement

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.listing.ListingEditPO

data class CreateUpdateListingResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("listingEditPO")
    val listingEditPO: ListingEditPO
)