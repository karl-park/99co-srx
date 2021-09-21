package sg.searchhouse.agentconnect.model.api.listing.listingmanagement

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.enumeration.api.CommunityEnum
import sg.searchhouse.agentconnect.model.api.community.CommunityHyperTargetTemplatePO

/**
 * Docs: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/690290736/Listing+V1+Data+Structures
 */
data class SrxCreditListingActivationPO(
    val srxCreditMainType: Int,
    val listing: List<ListingActivationPO>
) {
    data class ListingActivationPO(
        val id: Int,
        val value: Boolean?,
        var virtualTour: ListingActivationVirtualTour? = null,
        var valuation: ListingActivationValuation? = null,
        var sponsored: ListingActivationSponsored? = null
    ) {
        data class ListingActivationVirtualTour(
            val virtualTourNewProjectId: Int?,
            var bookingSlot: Long?,
            var remarks: String?
        )

        data class ListingActivationValuation(
            val underConstruction: Boolean?,
            val bookingSlot: Long?,
            var remarks: String?
        )

        data class ListingActivationSponsored(
            val title: String? = null,
            val remarks: String? = null,
            val numberOfWeeks: Int = CommunityEnum.SponsorDuration.ONE_WEEK.value,
            val communityIds: String?,
            val hyperTargetTemplate: CommunityHyperTargetTemplatePO? = null,
            val targetType: Int
        )
    }
}