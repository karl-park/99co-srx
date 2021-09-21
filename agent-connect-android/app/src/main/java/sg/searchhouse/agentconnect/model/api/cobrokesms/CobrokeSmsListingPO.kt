package sg.searchhouse.agentconnect.model.api.cobrokesms

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CobrokeSmsListingPO(
    @SerializedName("isMclp")
    val isMclp: String = "",
    @SerializedName("sellerUserId")
    val sellerUserId: String = "",
    @SerializedName("listingId")
    val listingId: String = ""
): Serializable {
    enum class IsMclp(val value: String) {
        YES("1"), NO("0")
    }
}