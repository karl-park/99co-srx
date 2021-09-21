package sg.searchhouse.agentconnect.model.api.propertynews

import com.google.gson.annotations.SerializedName

data class FindNewsArticlesResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("articles")
    val articles: List<OnlineCommunicationPO>,
    @SerializedName("total")
    val total: Int
)