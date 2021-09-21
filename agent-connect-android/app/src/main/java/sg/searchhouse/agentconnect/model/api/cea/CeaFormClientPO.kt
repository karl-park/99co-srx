package sg.searchhouse.agentconnect.model.api.cea

import android.content.Context
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import java.io.Serializable
import java.util.*

data class CeaFormClientPO(
    @SerializedName("partyType")
    val partyType: String,
    @SerializedName("partyNric")
    val partyNric: String,
    @SerializedName("partyName")
    val partyName: String,
    @SerializedName("partyContact")
    val partyContact: String,
    @SerializedName("partyEmail")
    val partyEmail: String,
    @SerializedName("partyAddress")
    val partyAddress: String,
    @SerializedName("representerContact")
    val representerContact: String,
    @SerializedName("representerEmail")
    val representerEmail: String,
    @SerializedName("representerAddress")
    val representerAddress: String,
    @SerializedName("representerName")
    val representerName: String,
    @SerializedName("representerNric")
    val representerNric: String,
    @SerializedName("representing")
    val representing: String,
    @SerializedName("nationality")
    val nationality: String,
    @SerializedName("partyRace")
    val partyRace: String,
    @SerializedName("salutation")
    val salutation: String,
    @SerializedName("citizenship")
    val citizenship: String,
    @SerializedName("postalCode")
    val postalCode: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("blkNo")
    val blkNo: String,
    @SerializedName("floor")
    val floor: String,
    @SerializedName("unit")
    val unit: String,
    @SerializedName("representerPostalCode")
    val representerPostalCode: String,
    @SerializedName("representerBlkNo")
    val representerBlkNo: String,
    @SerializedName("representerFloor")
    val representerFloor: String,
    @SerializedName("representerUnit")
    val representerUnit: String,
    @SerializedName("isCompany")
    val isCompany: String,
    @SerializedName("partySignature")
    var partySignature: String,
    var photoUrl: String = ""
) : Serializable {

    fun getClientSignatureLabel(context: Context): String {
        if (!TextUtils.isEmpty(partyName)) {
            return context.getString(R.string.label_cea_client_signature, partyName)
        }
        return ""
    }

    fun getSignatureKey(): String {
        return partyName.trim().toUpperCase(Locale.getDefault()) + "-" + partyNric.trim().toUpperCase(
            Locale.getDefault()
        )
    }

}