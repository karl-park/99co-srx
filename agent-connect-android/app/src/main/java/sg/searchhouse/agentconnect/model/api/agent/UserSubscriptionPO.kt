package sg.searchhouse.agentconnect.model.api.agent

import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.util.DateTimeUtil
import java.io.Serializable

data class UserSubscriptionPO(
    @SerializedName("id")
    val id: String,
    @SerializedName("subscriptionType")
    val subscriptionType: String,
    @SerializedName("subscriptionTypeShort")
    val subscriptionTypeShort: String,
    @SerializedName("subscriptionStart")
    val subscriptionStart: String,
    @SerializedName("subscriptionEnd")
    val subscriptionEnd: String,
    @SerializedName("reportsDownloaded")
    val reportsDownloaded: String,
    @SerializedName("referralCode")
    val referralCode: String,
    @SerializedName("discount")
    val discount: String,
    @SerializedName("payingInd")
    val payingInd: String,
    @SerializedName("dateCreate")
    val dateCreate: String,
    @SerializedName("expired")
    val expired: String,
    @SerializedName("userId")
    val userId: String
) : Serializable {

    fun getSubscriptionTypeUpperCase(): String {
        return subscriptionType.toUpperCase()
    }

    fun getFormattedSubscriptionEnd(): String {
        if (!TextUtils.isEmpty(subscriptionEnd)) {
            return DateTimeUtil.convertDateToString(
                DateTimeUtil.convertStringToDate(
                    subscriptionEnd,
                    DateTimeUtil.FORMAT_DATE_3
                ), DateTimeUtil.FORMAT_DATE_4
            )
        }
        return subscriptionEnd
    }
}