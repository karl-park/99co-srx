package sg.searchhouse.agentconnect.model.api.userprofile

import com.google.gson.annotations.SerializedName

data class GreetingResponse constructor(
    @SerializedName("result")
    val id: String,
    @SerializedName("appGreeting")
    val appGreeting: AppGreetingPO
) {
    class AppGreetingPO (
        @SerializedName("id")
        val id: Int,
        @SerializedName("appName")
        val appName: String,
        @SerializedName("dateFrom")
        val dateFrom: String,
        @SerializedName("dateTo")
        val dateTo: String,
        @SerializedName("greeting")
        val greeting: String
    )
}