package sg.searchhouse.agentconnect.model.api.location

import com.google.gson.annotations.SerializedName

data class FindLocationSuggestionsResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("suggestions")
    val suggestions: List<LocationEntryPO>
)