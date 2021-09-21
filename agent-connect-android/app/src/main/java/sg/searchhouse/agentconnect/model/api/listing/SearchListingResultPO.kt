package sg.searchhouse.agentconnect.model.api.listing

import com.google.gson.annotations.SerializedName

data class SearchListingResultPO(
    @SerializedName("srxStpListings")
    val srxStpListings: ListingResultPO?,
    @SerializedName("listingPropertyListings")
    val listingPropertyListings: ListingResultPO?,
    @SerializedName("mclpAllMatchSearchTerm")
    val mclpAllMatchSearchTerm: ListingResultPO?,
    @SerializedName("mclpAllNearby")
    val mclpAllNearby: ListingResultPO?
) {
    fun getListingPOs(): List<ListingPO> {
        val srxStpListings = srxStpListings?.listingPOs ?: emptyList()
        val listingPropertyListings = listingPropertyListings?.listingPOs ?: emptyList()
        val mclpAllMatchSearchTerm = mclpAllMatchSearchTerm?.listingPOs ?: emptyList()
        val mclpAllNearby = mclpAllNearby?.listingPOs ?: emptyList()
        return srxStpListings + listingPropertyListings + mclpAllMatchSearchTerm + mclpAllNearby
    }

    fun getGrandTotal(): Int {
        val srxStpListingsTotal = srxStpListings?.total ?: 0
        val listingPropertyListingsTotal = listingPropertyListings?.total ?: 0
        val mclpAllMatchSearchTermTotal = mclpAllMatchSearchTerm?.total ?: 0
        val mclpAllNearbyTotal = mclpAllNearby?.total ?: 0
        return srxStpListingsTotal + listingPropertyListingsTotal + mclpAllMatchSearchTermTotal + mclpAllNearbyTotal
    }
}