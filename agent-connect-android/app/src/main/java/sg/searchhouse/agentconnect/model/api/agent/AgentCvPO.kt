package sg.searchhouse.agentconnect.model.api.agent

import android.content.Context
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.util.ApiUtil
import java.io.Serializable

data class AgentCvPO(
    @SerializedName("appointment")
    val appointment: String,
    @SerializedName("aboutMe")
    var aboutMe: String,
    @SerializedName("testimonials")
    val testimonials: ArrayList<Testimonial>,
    @SerializedName("showAboutMe")
    var showAboutMe: Boolean,
    @SerializedName("showListings")
    var showListings: Boolean,
    @SerializedName("showProfile")
    var showProfile: Boolean,
    @SerializedName("showTestimonials")
    var showTestimonials: Boolean,
    @SerializedName("showTransactions")
    var showTransactions: Boolean,
    @SerializedName("publicProfileUrl")
    var publicProfileUrl: String?,
    @SerializedName("changedPublicUrlInd")
    val changedPublicUrlInd: Boolean?
) : Serializable {

    data class Testimonial(
        @SerializedName("clientName")
        var clientName: String,
        @SerializedName("deleted")
        var deleted: Boolean,
        @SerializedName("id")
        val id: Int,
        @SerializedName("photoDeleted")
        val photoDeleted: Boolean,
        @SerializedName("photoUrl")
        val photoUrl: String,
        @SerializedName("tempPhotoId")
        val tempPhotoId: Int,
        @SerializedName("testimonial")
        var testimonial: String,
        @SerializedName("testimonialWriteupLabel")
        val testimonialWriteupLabel: String
    ) : Serializable

    fun getAgentCvUrlWithBaseUrl(context: Context): String {
        publicProfileUrl?.let {
            if (!TextUtils.isEmpty(it)) {
                return "${ApiUtil.getBaseUrl(context)}/${it}"
            }
        }
        return ""
    }
}