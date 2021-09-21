package sg.searchhouse.agentconnect.model.api.chat

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SrxAgentEnquiryPO constructor(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("email")
    val email: String = "",
    @SerializedName("message")
    val message: String = "",
    @SerializedName("mobileLocalNum")
    val mobileLocalNum: String = "",
    @SerializedName("mobileCountryCode")
    val mobileCountryCode: String = "",
    @SerializedName("listingId")
    val listingId: String = "",
    @SerializedName("listingType")
    val listingType: String = "",
    @SerializedName("projectId")
    val projectId: String = "",
    @SerializedName("date")
    val date: String = "",
    @SerializedName("enquirerType")
    val enquirerType: String = "",
    @SerializedName("enquirySource")
    val enquirySource: Int = 0
) : Serializable