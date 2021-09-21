package sg.searchhouse.agentconnect.model.api.homereport

import com.google.gson.annotations.SerializedName

// https://streetsine.atlassian.net/wiki/spaces/SIN/pages/752746541/Home+Report+V1+API
data class GenerateHomeReportResponse(
    @SerializedName("results")
    val results: Results
) {
    data class Results(
        @SerializedName("extractedLink")
        val extractedLink: String
    )
}