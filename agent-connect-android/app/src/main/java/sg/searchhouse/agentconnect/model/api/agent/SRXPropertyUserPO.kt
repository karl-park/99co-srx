package sg.searchhouse.agentconnect.model.api.agent

import android.content.Context
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.util.NumberUtil
import java.io.Serializable

data class SRXPropertyUserPO constructor(
    @SerializedName("name")
    val name: String,
    @SerializedName("mobileNum")
    val mobileNum: Int,
    @SerializedName("fullAddress")
    val fullAddress: String,
    @SerializedName("sizeWithDesc")
    val sizeWithDesc: String,
    @SerializedName("capitalGainXValue")
    val capitalGainXValue: Int,
    @SerializedName("capitalGainQuantum")
    val capitalGainQuantum: Int,
    @SerializedName("capitalGainPercent")
    val capitalGainPercent: Double,
    @SerializedName("capitalGainLastTransactedPrice")
    val capitalGainLastTransactedPrice: Int,
    @SerializedName("capitalGainLastTransactedDate")
    val capitalGainLastTransactedDate: String,
    @SerializedName("ptUserId")
    val ptUserId: Int,
    @SerializedName("capitalGainRentalYield")
    val capitalGainRentalYield: Double,
    @SerializedName("numPtViews")
    val numPtViews: String,
    @SerializedName("lastPtViewDateFormatted")
    val lastPtViewDateFormatted: String,
    @SerializedName("urlForInviterView")
    val urlForInviterView: String
) : Serializable {

    fun getFormattedCapitalGainXValue(): String {
        return "\$${NumberUtil.getThousandShortForm(capitalGainXValue)}"
    }

    private fun getFormattedCapitalGainQuantum(): String {
        return "${NumberUtil.maybeGetNegativeSign(capitalGainQuantum)}\$${NumberUtil.getThousandShortForm(
            capitalGainQuantum
        )}"
    }

    private fun getFormattedCapitalGainPercent(): String {
        return "${NumberUtil.formatThousand(capitalGainPercent)}%"
    }

    fun getFormattedRentalYield(): String {
        return if (capitalGainRentalYield > 0.0) {
            "${NumberUtil.formatThousand(capitalGainRentalYield, decimalPlace = 2)}%"
        } else {
            ""
        }
    }

    fun getFullAddressTrimmed(): String {
        return fullAddress.trim()
    }

    fun getFormattedCapitalGainQuantumPercent(): String {
        return if (isCapitalGainApplicable()) {
            "${getFormattedCapitalGainQuantum()} (${getFormattedCapitalGainPercent()})"
        } else {
            ""
        }
    }

    fun getCapitalGainQuantumTextColor(context: Context): Int {
        return context.getColor(R.color.black)
    }

    private fun getFormattedCapitalGainLastTransactedPrice(): String {
        return "\$${NumberUtil.getThousandShortForm(capitalGainLastTransactedPrice)}"
    }

    fun getMySgHomeClientItemFooter(context: Context): String {
        return if (isCapitalGainApplicable()) {
            context.getString(
                R.string.agent_client_list_item_footer,
                getFormattedCapitalGainLastTransactedPrice(),
                capitalGainLastTransactedDate
            )
        } else {
            ""
        }
    }

    fun isCapitalGainApplicable(): Boolean {
        return capitalGainLastTransactedDate.isNotEmpty()
    }

    private fun getNumPtViewsInt(): Int {
        return try {
            numPtViews.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }

    fun getLastSeenLabel(context: Context): String {
        val number = getNumPtViewsInt()
        return context.resources.getQuantityString(
            R.plurals.label_clients_last_seen,
            number,
            number,
            lastPtViewDateFormatted
        )
    }

    fun isLastSeenApplicable(): Boolean {
        return getNumPtViewsInt() > 0
    }
}
