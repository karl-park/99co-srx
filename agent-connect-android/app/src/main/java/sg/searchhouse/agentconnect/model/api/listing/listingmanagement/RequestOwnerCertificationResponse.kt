package sg.searchhouse.agentconnect.model.api.listing.listingmanagement

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.listing.ListingOwnerCertificationPO

data class RequestOwnerCertificationResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("ownerCertification")
    val ownerCertification: ListingOwnerCertificationPO
)