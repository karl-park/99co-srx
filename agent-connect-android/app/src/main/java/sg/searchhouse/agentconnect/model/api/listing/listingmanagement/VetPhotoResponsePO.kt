package sg.searchhouse.agentconnect.model.api.listing.listingmanagement

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum

data class VetPhotoResponsePO(
    @SerializedName("result")
    val result: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("reason")
    val reason: String
) {
    fun getQualityListingPhotoStatus(): ListingManagementEnum.QualityListingPhotoStatus? {
        return ListingManagementEnum.QualityListingPhotoStatus.values().find { it.value == status }
    }
}