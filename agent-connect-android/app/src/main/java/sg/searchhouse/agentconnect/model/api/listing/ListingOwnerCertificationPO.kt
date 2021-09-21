package sg.searchhouse.agentconnect.model.api.listing

import com.google.gson.annotations.SerializedName

data class ListingOwnerCertificationPO(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("listingId")
    val listingId: Int,
    @SerializedName("mobileLocalNum")
    val mobileLocalNum: Int,
    @SerializedName("certifiedInd")
    val certifiedInd: Boolean?,
    @SerializedName("dateCertified")
    val dateCertified: String?,
    @SerializedName("dateCreate")
    val dateCreate: String?
)