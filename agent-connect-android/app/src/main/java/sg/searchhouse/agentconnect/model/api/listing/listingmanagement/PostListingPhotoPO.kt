package sg.searchhouse.agentconnect.model.api.listing.listingmanagement

import android.content.Context
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum

data class PostListingPhotoPO(
    @SerializedName("id")
    val id: String,
    @SerializedName("position")
    var position: Int = 0,
    @SerializedName("url")
    val url: String,
    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String,
    @SerializedName("coverPhoto")
    var coverPhoto: Boolean = false,
    @SerializedName("qualityStatus")
    val qualityStatus: Int,
    @SerializedName("qualityReason")
    val qualityReason: String,
    @SerializedName("appealStatus")
    var appealStatus: Int?,

    var vetPhoto: VetPhotoResponsePO? = null
) {
    fun getQualityListingPhotoStatus(): ListingManagementEnum.QualityListingPhotoStatus? {
        return if (vetPhoto != null) {
            vetPhoto?.getQualityListingPhotoStatus()
        } else {
            ListingManagementEnum.QualityListingPhotoStatus.values()
                .find { it.value == qualityStatus }
        }
    }

    fun getQualityListingPhotoReason(): String? {
        return if (vetPhoto?.reason?.isNotEmpty() == true) {
            vetPhoto?.reason
        } else {
            qualityReason
        }
    }

    fun getQualityListingAppealStatus(): ListingManagementEnum.QualityListingAppealStatus? {
        return ListingManagementEnum.QualityListingAppealStatus.values()
            .find { appealStatus == it.value }
    }

    fun getQualityListingAppealStatusLabel(context: Context): String {
        val appealStatusEnum = getQualityListingAppealStatus()
        return if (appealStatusEnum == null) {
            ""
        } else {
            context.getString(appealStatusEnum.label)
        }
    }
}