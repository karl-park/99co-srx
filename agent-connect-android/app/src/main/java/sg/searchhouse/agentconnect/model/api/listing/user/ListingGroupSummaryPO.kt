package sg.searchhouse.agentconnect.model.api.listing.user

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum

data class ListingGroupSummaryPO(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("rentTotal")
    val rentTotal: Int,
    @SerializedName("saleTotal")
    val saleTotal: Int
) {


    fun getGroup(): ListingManagementEnum.ListingGroup? {
        return ListingManagementEnum.ListingGroup.values().find { it.value == id }
    }
}