package sg.searchhouse.agentconnect.model.api.agent

import android.content.Context
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.util.NumberUtil

/**
 * Reference: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/695238693/Agent+Details+V1+API
 * `getAgentFullProfile#data#credits`
 */
data class UserCreditPO(
    @SerializedName("maxChar")
    val maxChar: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("value")
    val value: String
) {
    fun getCredits(context: Context): String {
        if (NumberUtil.isNaturalNumber(value)) {
            return context.resources.getQuantityString(
                R.plurals.label_credit_count,
                value.toInt(),
                NumberUtil.formatThousand(value.toInt())
            )
        }
        return value
    }

    // TODO Get enums of `type` field
    fun isPostListing(): Boolean = type == "999"
}