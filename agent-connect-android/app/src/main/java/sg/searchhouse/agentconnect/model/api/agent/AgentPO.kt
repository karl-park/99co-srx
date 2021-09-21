package sg.searchhouse.agentconnect.model.api.agent

import android.content.Context
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.util.StringUtil
import java.io.Serializable

data class AgentPO constructor(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("encryptedUserId")
    val encryptedUserId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("ceaRegNo")
    val ceaRegNo: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("agency")
    val agency: String,
    @SerializedName("agencyId")
    val agencyId: Int? = null,
    @SerializedName("agencyName")
    val agencyName: String,
    @SerializedName("licenseNumber")
    val licenseNumber: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("photo")
    val photo: String,
    @SerializedName("agencyLogo")
    val agencyLogo: String,
    @SerializedName("subscription")
    val subscription: String,
    @SerializedName("subscriptionType")
    val subscriptionType: String,
    @SerializedName("numOfListings")
    val numOfListings: Int,
    @SerializedName("agentCvPO")
    val agentCvPO: AgentCvPO?,
    @SerializedName("agentCvUrl")
    val agentCvUrl: String,
    @SerializedName("agencyPartOfSRXConsortium")
    val agencyPartOfSRXConsortium: Boolean,
    @SerializedName("transactionSummary")
    val transactionSummary: TransactionSummary?,
    @SerializedName("listingSummary")
    val listingSummary: ListingSummary?,

    var isSelected: Boolean = false
) : Serializable {

    data class TransactionSummary(
        @SerializedName("rentHdbTotal")
        val rentHdbTotal: Int,
        @SerializedName("rentHighest")
        val rentHighest: Int,
        @SerializedName("rentPrivateTotal")
        val rentPrivateTotal: Int,
        @SerializedName("rentTotal")
        val rentTotal: Int,
        @SerializedName("saleHdbTotal")
        val saleHdbTotal: Int,
        @SerializedName("saleHighest")
        val saleHighest: Int,
        @SerializedName("salePrivateTotal")
        val salePrivateTotal: Int,
        @SerializedName("saleTotal")
        val saleTotal: Int
    ) : Serializable

    data class ListingSummary(
        @SerializedName("rentTotal")
        val rentTotal: Int,
        @SerializedName("saleTotal")
        val saleTotal: Int
    ) : Serializable

    fun isExpertAnalyzer(): Boolean {
        return (subscription == "HRD" || subscription == "Expert")
    }

    fun getCeaNoAndLicenseNumber(): String {
        return StringBuilder().append(ceaRegNo).append(" / ").append(licenseNumber).toString()
    }

    fun getActiveListings(): String {
        var activeListings = ""
        if (numOfListings > 0) {
            activeListings = when (numOfListings) {
                1 -> {
                    "$numOfListings Active Listing"
                }
                else -> {
                    "$numOfListings Active Listings"
                }
            }
        }
        return activeListings
    }

    // Handle case when CEA empty
    fun ceaRegNumberFormatted(context: Context): String {
        return if (TextUtils.isEmpty(ceaRegNo)) {
            context.getString(R.string.label_cea_not_available)
        } else {
            ceaRegNo
        }
    }

    fun getCeaNoWithCEAInitial(context: Context): String {
        return if (TextUtils.isEmpty(ceaRegNo)) {
            context.getString(R.string.label_cea_not_available)
        } else {
            "CEA : $ceaRegNo"
        }
    }

    // Sanitized Mobile Number
    fun getSanitizedMobileNumber(): String? {
        return StringUtil.getSanitizedMobileNumber(mobile)
    }

    // Sanitized Mobile Number with SG country code
    fun getSanitizedMobileNumberWithSgCountryCode(): String? {
        return StringUtil.getSanitizedMobileNumberWithSgCountryCode(mobile)
    }

    fun getMobileNumberForDisplay(): String {
        getSanitizedMobileNumber()?.let {
            return it.substring(0, 4).plus(" XXXX")
        }
        return ""
    }
}

