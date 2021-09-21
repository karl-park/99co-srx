package sg.searchhouse.agentconnect.model.api.agent

import android.content.Context
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.enumeration.api.TransactionEnum
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.PropertyTypeUtil

data class GetAgentTransactions(
    @SerializedName("rentalTransactions")
    val rentalTransactions: AgentTransaction,
    @SerializedName("saleTransactions")
    val saleTransactions: AgentTransaction
) {
    data class AgentTransaction(
        @SerializedName("category")
        val category: String,
        @SerializedName("itemsPerPage")
        val itemsPerPage: Int,
        @SerializedName("listingOwnerName")
        val listingOwnerName: String,
        @SerializedName("listingPOs")
        val listingPOs: ArrayList<TransactedListingPO>,
        @SerializedName("maxTransactedPrice")
        val maxTransactedPrice: Int,
        @SerializedName("page")
        val page: Int,
        @SerializedName("searchCriteriaString")
        val searchCriteriaString: String,
        @SerializedName("searchType")
        val searchType: String,
        @SerializedName("total")
        val total: Int,
        @SerializedName("type")
        val type: String
    ) {
        data class TransactedListingPO(
            @SerializedName("address")
            val address: String,
            @SerializedName("crunchResearchCorporateTransactionId")
            val crunchResearchCorporateTransactionId: Int,
            @SerializedName("districtHdbTown")
            val districtHdbTown: String,
            @SerializedName("size")
            val size: String,
            @SerializedName("size2")
            val size2: String,
            @SerializedName("transactedPrice")
            val transactedPrice: Int,
            @SerializedName("type")
            val type: String,
            @SerializedName("concealedByAgent")
            val concealedByAgent: Boolean,
            @SerializedName("cdResearchSubType")
            val cdResearchSubType: Int,
            @SerializedName("name")
            val name: String,
            @SerializedName("rooms")
            val rooms: String,
            @SerializedName("bathroom")
            val bathroom: String,
            @SerializedName("dateTransactedFormatted")
            val dateTransactedFormatted: String,
            @SerializedName("unitFloor")
            val unitFloor: String,
            @SerializedName("unitNumber")
            val unitNumber: String
        ) {

            //get property main type
            fun getTransactionPropertyMainType(): Int {
                //For transaction grouping
                return when {
                    PropertyTypeUtil.isHDB(cdResearchSubType) -> TransactionEnum.TransactionPropertyMainType.HDB.value
                    PropertyTypeUtil.isCondo(cdResearchSubType) -> TransactionEnum.TransactionPropertyMainType.Condo.value
                    PropertyTypeUtil.isLanded(cdResearchSubType) -> TransactionEnum.TransactionPropertyMainType.Landed.value
                    PropertyTypeUtil.isCommercial(cdResearchSubType) -> TransactionEnum.TransactionPropertyMainType.Commercial.value
                    else -> 0
                }
            }

            fun getFormattedTransactedPrice(): String {
                return if (transactedPrice > 0) {
                    NumberUtil.getFormattedCurrency(transactedPrice)
                } else {
                    ""
                }
            }

            fun getFloorAndUnitNumber(): String {
                return if (!TextUtils.isEmpty(unitFloor) && !TextUtils.isEmpty(unitNumber)) {
                    "#$unitFloor-$unitNumber"
                } else if (!TextUtils.isEmpty(unitFloor)) {
                    "#$unitFloor"
                } else if (!TextUtils.isEmpty(unitNumber)) {
                    unitNumber
                } else {
                    ""
                }
            }

            fun getSizeWithUnit(context: Context): String {
                if (PropertyTypeUtil.isHDB(cdResearchSubType)) {
                    if (NumberUtil.isNaturalNumber(size2)) {
                        return "${NumberUtil.formatThousand(size2.toInt())} ${context.getString(
                            R.string.label_size_sqm
                        )}"
                    }

                } else {
                    if (NumberUtil.isNaturalNumber(size)) {
                        return "${NumberUtil.formatThousand(size.toInt())} ${context.getString(
                            R.string.label_size_sqft
                        )}"
                    }
                }
                return ""
            }

            fun getFormattedTransactedDate(): String {
                //Show transacted date format (Eg : 22 Mar 19)
                if (!TextUtils.isEmpty(dateTransactedFormatted)) {
                    return DateTimeUtil.convertDateToString(
                        DateTimeUtil.convertStringToDate(
                            dateTransactedFormatted,
                            DateTimeUtil.FORMAT_DATE_3
                        ), DateTimeUtil.FORMAT_DATE_5
                    )
                }
                return dateTransactedFormatted
            }
        }
    }
}