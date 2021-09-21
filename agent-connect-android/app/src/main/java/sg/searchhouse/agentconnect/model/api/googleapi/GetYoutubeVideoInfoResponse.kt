package sg.searchhouse.agentconnect.model.api.googleapi

import com.google.gson.annotations.SerializedName

data class GetYoutubeVideoInfoResponse(
    @SerializedName("items")
    val items: ArrayList<YoutubeVideoInfo>,
    @SerializedName("error")
    val error: YoutubeError?
) {
    data class YoutubeError(
        @SerializedName("code")
        val code: String,
        @SerializedName("message")
        val message: String,
        @SerializedName("errors")
        val errors: List<SubError>,
        @SerializedName("status")
        val status: String
    ) {
        data class SubError(
            @SerializedName("message")
            val message: String,
            @SerializedName("domain")
            val domain: String,
            @SerializedName("reason")
            val reason: String
        )
    }

    class YoutubeVideoInfo(
        @SerializedName("kind")
        val kind: String,
        @SerializedName("etag")
        val etag: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("status")
        val status: Status
    ) {
        class Status(
            @SerializedName("uploadStatus")
            val uploadStatus: String,
            @SerializedName("privacyStatus")
            val privacyStatus: String,
            @SerializedName("license")
            val license: String,
            @SerializedName("embeddable")
            val embeddable: Boolean,
            @SerializedName("publicStatsViewable")
            val publicStatsViewable: Boolean
        )
    }
}