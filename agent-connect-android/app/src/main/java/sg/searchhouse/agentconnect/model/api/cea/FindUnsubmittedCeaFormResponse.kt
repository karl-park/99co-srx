package sg.searchhouse.agentconnect.model.api.cea

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.listing.UnsubmittedCeaFormPO

class FindUnsubmittedCeaFormResponse(
    @SerializedName("total")
    val total: Int,
    @SerializedName("result")
    val result: String,
    @SerializedName("forms")
    val forms: List<UnsubmittedCeaFormPO>
)