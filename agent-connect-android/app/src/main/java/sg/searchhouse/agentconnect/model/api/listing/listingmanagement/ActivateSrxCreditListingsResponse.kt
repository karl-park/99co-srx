package sg.searchhouse.agentconnect.model.api.listing.listingmanagement

import android.content.Context
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.model.api.community.CommunityHyperTargetTemplatePO
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.util.NumberUtil
import java.io.Serializable

/**
 * Doc: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/690290736/Listing+V1+Data+Structures
 */
data class ActivateSrxCreditListingsResponse(
    @SerializedName("summary")
    val summary: ActivateSrxCreditListingSummaryPO?,
    @SerializedName("result")
    val result: String
) {
    data class ActivateSrxCreditListingSummaryPO(
        @SerializedName("listings")
        val listings: List<ActivateSrxCreditListingPO>?,
        // Available: User currently have
        @SerializedName("creditsAvailable")
        val creditsAvailable: Int,
        // Deductible: Cost
        @SerializedName("creditsDeductible")
        val creditsDeductible: Int,
        // Backend typo
        @SerializedName("succesfullyActivated")
        val succesfullyActivated: Boolean,
        @SerializedName("flExpiryMsg")
        val flExpiryMsg: String,
        @SerializedName("sampleValuationReportUrl")
        val sampleValuationReportUrl: String,
        @SerializedName("sponsoredLocation")
        val sponsoredLocation: String,
        @SerializedName("numberOfWeeks")
        val numberOfWeeks: Int,
        @SerializedName("targetType")
        val targetType: Int,
        @SerializedName("hyperTargetPO")
        val hyperTargetPO: CommunityHyperTargetTemplatePO?,
        @SerializedName("totalMembersCount")
        val totalMembersCount: Int,
        @SerializedName("selectedMembersCount")
        val selectedMembersCount: Int,
    ) {
        data class ActivateSrxCreditListingPO(
            @SerializedName("srxCreditActivationAvailability")
            val srxCreditActivationAvailability: Boolean,
            @SerializedName("srxCreditActivationRemark")
            val srxCreditActivationRemark: String,
            @SerializedName("srxCreditActivated")
            val srxCreditActivated: Boolean,
            @SerializedName("featuredTypeList")
            val featuredTypeList: List<FeaturedType>,
            @SerializedName("virtualTourNewProjects")
            val virtualTourNewProjects: List<VirtualTourNewProjectPO>,
            @SerializedName("availableBookingSlots")
            val availableBookingSlots: List<Long>,
            var selectedTimeSlot: String? = null,
            var optionalRemark: String? = null,
            var activationListing: SrxCreditListingActivationPO.ListingActivationPO? = null
        ) : ListingPO() {
            fun getHyperTargetTemplatePO(): CommunityHyperTargetTemplatePO? {
                return activationListing?.sponsored?.hyperTargetTemplate
            }

            data class FeaturedType(
                @SerializedName("name")
                val name: String,
                @SerializedName("type")
                val type: Boolean,
                @SerializedName("value")
                val value: Int
            ) : Serializable

            data class VirtualTourNewProjectPO(
                @SerializedName("id")
                val id: Int,
                @SerializedName("url")
                val url: String,
                @SerializedName("thumbnailUrl")
                val thumbnailUrl: String,
                @SerializedName("roomType")
                val roomType: String
            ) : Serializable
        }

        fun getFormattedCreditsDeductible(context: Context): String {
            return context.resources.getQuantityString(
                R.plurals.label_credit_count,
                creditsDeductible,
                NumberUtil.formatThousand(creditsDeductible)
            )
        }

        fun getFormattedCreditsAvailable(context: Context): String {
            return context.resources.getQuantityString(
                R.plurals.label_credit_count,
                creditsAvailable,
                NumberUtil.formatThousand(creditsAvailable)
            )
        }

        fun isCreditsEnough(): Boolean {
            return creditsAvailable >= creditsDeductible
        }

        fun hasFlExpiryMsg(): Boolean {
            return flExpiryMsg.isNotEmpty()
        }
    }

    fun getFirstSrxCreditActivationRemark(context: Context): String {
        return summary?.listings?.firstOrNull()?.srxCreditActivationRemark
            ?: context.getString(R.string.toast_sponsor_listing_fail)
    }
}