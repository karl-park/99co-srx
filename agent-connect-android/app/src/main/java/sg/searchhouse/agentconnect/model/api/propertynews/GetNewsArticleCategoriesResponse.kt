package sg.searchhouse.agentconnect.model.api.propertynews

import com.google.gson.annotations.SerializedName

data class GetNewsArticleCategoriesResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("categories")
    val categories: Map<String, String>
) 

