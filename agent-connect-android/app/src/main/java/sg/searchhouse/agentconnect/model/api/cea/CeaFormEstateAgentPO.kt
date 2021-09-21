package sg.searchhouse.agentconnect.model.api.cea

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class CeaFormEstateAgentPO(
    @SerializedName("partyAgencyName")
    var partyAgencyName: String,
    @SerializedName("partyAgencyLicense")
    var partyAgencyLicense: String,
    @SerializedName("partyAgencyAddress")
    var partyAgencyAddress: String,
    @SerializedName("partyType")
    var partyType: String,
    @SerializedName("partyNric")
    var partyNric: String,
    @SerializedName("partyName")
    var partyName: String,
    @SerializedName("partyContact")
    var partyContact: String,
    @SerializedName("ceaRegNo")
    var ceaRegNo: String,
    @SerializedName("cdAgency")
    var cdAgency: String,
    @SerializedName("photoUrl")
    var photoUrl: String
) : Serializable {

    fun getAgentSignatureKey(): String {
        val tempPartyName = partyName.trim().toUpperCase(Locale.getDefault())
        return "AGENT_$tempPartyName"
    }
}