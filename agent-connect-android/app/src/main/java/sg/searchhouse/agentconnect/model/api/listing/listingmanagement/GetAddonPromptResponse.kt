package sg.searchhouse.agentconnect.model.api.listing.listingmanagement

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.common.NameValuePO

data class GetAddonPromptResponse(
    @SerializedName("prompts") val prompts: List<NameValuePO>,
    @SerializedName("result") val result: String
)