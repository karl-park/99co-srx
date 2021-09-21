package sg.searchhouse.agentconnect.model.api.chat

import com.google.gson.annotations.SerializedName

data class GetSsmConversationResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("convo")
    val convo: SsmConversationPO,
    @SerializedName("listingsBlastConvos")
    val listingsBlastConvos: List<SsmConversationListingBlastPO>
)