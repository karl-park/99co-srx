package sg.searchhouse.agentconnect.model.api.listing.user

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import java.io.Serializable

data class ListingManagementGroupPO(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("total")
    val total: Int,
    @SerializedName("saleTotal")
    val saleTotal: Int,
    @SerializedName("rentTotal")
    val rentTotal: Int,
    @SerializedName("listings")
    val listings: List<ListingPO>
) : Serializable
