package sg.searchhouse.agentconnect.model.api.listing

import android.content.Context
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import java.io.Serializable

data class UnsubmittedCeaFormPO(
    @SerializedName("formId")
    val formId: String,
    @SerializedName("formType")
    val formType: Int,
    @SerializedName("propertyString")
    val propertyString: String,
    @SerializedName("firstSubmitted")
    val firstSubmitted: String,
    @SerializedName("bedrooms")
    val bedrooms: Int,
    @SerializedName("built")
    val built: Double,
    @SerializedName("land")
    val land: Double,
    @SerializedName("cdResearchSubtype")
    val cdResearchSubtype: Int,
    @SerializedName("propertyClassification")
    val propertyClassification: String,
    @SerializedName("commencementDate")
    val commencementDate: String,
    @SerializedName("expiryDate")
    val expiryDate: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("listPrice")
    val listPrice: Int,
    @SerializedName("model")
    val model: String,
    @SerializedName("roomRental")
    val roomRental: String
) : Serializable {

    fun getFormTypeAndPropertyTypeFullLabel(context: Context): String {
        CeaExclusiveEnum.CeaFormType.values().find { it.value == formType }?.let {
            return "${context.getString(it.itemLabel)} $propertyClassification"
        }
        return ""
    }

    fun getCreatedDate(): String {
        if (!TextUtils.isEmpty(firstSubmitted)) {
            return "Created on $firstSubmitted"
        }
        return ""
    }

    fun getAskingPrice(context: Context): String {
        if (listPrice > 0) {
            return NumberUtil.getFormattedCurrency(listPrice)
        }
        return context.getString(R.string.label_dash)
    }

    fun getExpectedPrice(context: Context): String {
        if (price > 0) {
            return NumberUtil.getFormattedCurrency(price)
        }
        return context.getString(R.string.label_dash)
    }

    fun getValidityDate(context: Context): String {
        if (!TextUtils.isEmpty(commencementDate) && !TextUtils.isEmpty(expiryDate)) {
            val commencementDateString = DateTimeUtil.convertDateToString(
                DateTimeUtil.convertStringToDate(
                    commencementDate,
                    DateTimeUtil.FORMAT_DATE_13
                ), DateTimeUtil.FORMAT_DATE_14
            )
            val expiryDateString = DateTimeUtil.convertDateToString(
                DateTimeUtil.convertStringToDate(
                    expiryDate,
                    DateTimeUtil.FORMAT_DATE_13
                ), DateTimeUtil.FORMAT_DATE_14
            )
            return "$commencementDateString to $expiryDateString"
        }
        return context.getString(R.string.label_dash)
    }
}