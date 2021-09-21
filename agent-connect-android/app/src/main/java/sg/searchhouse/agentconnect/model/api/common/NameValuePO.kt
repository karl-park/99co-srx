package sg.searchhouse.agentconnect.model.api.common

import com.google.gson.annotations.SerializedName

data class NameValuePO(
    @SerializedName("name")
    val name: String,
    @SerializedName("value")
    val value: String
)