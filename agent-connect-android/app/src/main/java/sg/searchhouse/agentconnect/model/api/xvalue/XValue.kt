package sg.searchhouse.agentconnect.model.api.xvalue

import android.content.Context
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.StringConstant
import sg.searchhouse.agentconnect.enumeration.api.XValueEnum
import sg.searchhouse.agentconnect.model.api.common.SrxDate
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.StringUtil
import java.io.Serializable

data class XValue(
    @SerializedName("block")
    val block: String,
    @SerializedName("cdResearchSubtype")
    val cdResearchSubtype: Int,
    @SerializedName("dateRequested")
    val dateRequested: SrxDate,
    @SerializedName("dateRequestedReadable")
    val dateRequestedReadable: String,
    @SerializedName("fullAddress")
    val fullAddress: String,
    @SerializedName("goodWillPercent")
    val goodWillPercent: Int,
    @SerializedName("leaseCommence")
    val leaseCommence: Int,
    @SerializedName("listingPrice")
    val listingPrice: Int,
    @SerializedName("pesAdjustment")
    val pesAdjustment: Double,
    @SerializedName("pesSize")
    val pesSize: Int,
    @SerializedName("postalCode")
    val postalCode: Int,
    @SerializedName("projectId")
    val projectId: Int,
    @SerializedName("projectName")
    val projectName: String,
    @SerializedName("propertyType")
    val propertyType: String,
    @SerializedName("renovationCost")
    val renovationCost: Int,
    @SerializedName("renovationYear")
    val renovationYear: Int?,
    @SerializedName("rentalId")
    val rentalId: Int,
    @SerializedName("rentalXValue")
    val rentalXValue: Int,
    @SerializedName("requestId")
    val requestId: Int,
    @SerializedName("saleOrRental")
    val saleOrRental: String,
    @SerializedName("salePsf")
    val salePsf: Double,
    @SerializedName("shortAddress")
    val shortAddress: String,
    @SerializedName("sizeSqft")
    val sizeSqft: Int,
    @SerializedName("sizeSqm")
    val sizeSqm: Int,
    @SerializedName("srxValuationPrice")
    val srxValuationPrice: Int,
    @SerializedName("srxValuationRequestId")
    val srxValuationRequestId: Int,
    @SerializedName("srxValuationStatus")
    val srxValuationStatus: String,
    @SerializedName("tenure")
    val tenure: String,
    @SerializedName("typeOfArea")
    val typeOfArea: String,
    @SerializedName("xListing")
    val xListing: Any?,
    @SerializedName("xValue")
    val xValue: Int,
    @SerializedName("xValuePlus")
    val xValuePlus: Int
) : Serializable {
    fun getBlockAndProjectName(): String {
        return listOf(block, projectName).joinToString(" ")
    }

    fun getXValueLabel(context: Context): String {
        return when {
            xValue > 0 -> {
                context.getString(R.string.label_price, NumberUtil.formatThousand(xValue))
            }
            rentalXValue > 0 -> {
                context.getString(R.string.label_price, NumberUtil.formatThousand(rentalXValue))
            }
            else -> {
                ""
            }
        }
    }

    fun getSizeLabel(context: Context): String {
        val formattedSqft = NumberUtil.formatThousand(sizeSqft)
        val formattedSqm = NumberUtil.formatThousand(sizeSqm)
        return context.getString(R.string.label_area_sqft_sqm, formattedSqft, formattedSqm)
    }

    fun getFormattedSalePsf(context: Context): String {
        return if (salePsf > 0) {
            context.getString(R.string.label_price, NumberUtil.formatThousand(salePsf, 2))
        } else {
            ""
        }
    }

    fun getFormattedSaleXValue(context: Context): String {
        return if (xValue > 0) {
            context.getString(R.string.label_price, NumberUtil.formatThousand(xValue))
        } else {
            ""
        }
    }

    fun getFormattedRentalXValue(context: Context): String {
        return if (rentalXValue > 0) {
            context.getString(R.string.label_price, NumberUtil.formatThousand(rentalXValue))
        } else {
            ""
        }
    }

    fun getDescription(): String {
        val list = mutableListOf(propertyType)
        list += "$sizeSqft sqft ($sizeSqm sqm)" // Size
        if (!TextUtils.isEmpty(tenure) && !StringUtil.equals(
                tenure,
                StringConstant.UNKNOWN,
                ignoreCase = true
            )
        ) {
            list += tenure
        }
        return list.joinToString(" ${StringConstant.DOT} ")
    }

    @Throws(IllegalArgumentException::class)
    fun getOwnershipType(): XValueEnum.OwnershipType {
        return XValueEnum.OwnershipType.values().find { it.value == saleOrRental }
            ?: throw IllegalArgumentException("`saleOrRent` value is ${saleOrRental}!")
    }
}