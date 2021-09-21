package sg.searchhouse.agentconnect.model.api.transaction

import android.content.Context
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.dsl.remove
import sg.searchhouse.agentconnect.util.DateTimeUtil

data class TransactionListItem(
    @SerializedName("psf")
    val psf: String,
    @SerializedName("latitude")
    val latitude: Int,
    @SerializedName("postalCode")
    val postalCode: String,
    @SerializedName("cov")
    val cov: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("flatModel")
    val flatModel: String,
    @SerializedName("proprietarySourceColor")
    val proprietarySourceColor: String,
    @SerializedName("streetName")
    val streetName: String,
    @SerializedName("typeOfSale")
    val typeOfSale: String,
    @SerializedName("unRead")
    val unRead: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("propertyType")
    val propertyType: String,
    @SerializedName("typeOfArea")
    val typeOfArea: String,
    @SerializedName("cobrokeInd")
    val cobrokeInd: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("unitFloor")
    val unitFloor: String,
    @SerializedName("proprietarySource")
    val proprietarySource: String,
    @SerializedName("tenure")
    val tenure: String,
    @SerializedName("longitude")
    val longitude: Int,
    @SerializedName("address")
    val address: String,
    @SerializedName("town")
    val town: String,
    @SerializedName("leaseCommence")
    val leaseCommence: String,
    @SerializedName("minPsf")
    val minPsf: String,
    @SerializedName("maxPsf")
    val maxPsf: String,
    @SerializedName("valuation")
    val valuation: String,
    @SerializedName("size")
    val size: String,
    @SerializedName("postalDistrict")
    val postalDistrict: Int,
    @SerializedName("minPrice")
    val minPrice: String,
    @SerializedName("dateSold")
    val dateSold: String,
    @SerializedName("roomRentalInd")
    val roomRentalInd: String,
    @SerializedName("noOfOwners")
    val noOfOwners: String,
    @SerializedName("maxPrice")
    val maxPrice: String,
    @SerializedName("addressColor")
    val addressColor: String,
    @SerializedName("projectName")
    val projectName: String,
    @SerializedName("projectId")
    val projectId: String,
    @SerializedName("remarks")
    val remarks: String
) {
    fun getFormattedDateSold(): String {
        return DateTimeUtil.getConvertedFormatDate(
            dateSold,
            DateTimeUtil.FORMAT_DATE_8,
            DateTimeUtil.FORMAT_DATE_9
        )
    }

    fun getDisplayProjectAddress(): String {
        return if (!TextUtils.isEmpty(projectName) && !TextUtils.equals(address, projectName)) {
            "$address\n($projectName)"
        } else {
            address
        }
    }

    // TODO: Weird bug when try to split line, refactor when free
    fun getFormattedSizeFrom(): String {
        return "${size.substringBefore("-").trim()} -"
    }

    fun getFormattedSizeTo(): String {
        return size.substringAfter("-").trim()
    }

    fun getProjectOfficialHdbCsvContent(): String {
        val formattedDateSold = getFormattedDateSold().remove(",")
        val displayProjectAddress = getDisplayProjectAddress().remove(",")
        val price = price.remove(",")

        return "$formattedDateSold,$displayProjectAddress,$price"
    }

    fun getProjectOfficialNlpCsvContent(): String {
        val formattedDateSold = getFormattedDateSold().remove(",")
        val displayProjectAddress = getDisplayProjectAddress().remove(",")
        val price = price.remove(",")
        val formattedSizeFrom = getFormattedSizeFrom().remove(",")
        val formattedSizeTo = getFormattedSizeTo().remove(",")
        val sizeRange = "$formattedSizeFrom-$formattedSizeTo"

        return "$formattedDateSold,$displayProjectAddress,$price,$sizeRange"
    }

    companion object {
        fun getProjectOfficialGenericCsvTitle(context: Context): String {
            val dateSold = context.getString(R.string.label_column_transaction_date_sold)
            val address = context.getString(R.string.label_column_transaction_address)
            val price = context.getString(R.string.label_column_transaction_price)
            val sizeRange = context.getString(R.string.label_column_transaction_size_range)

            return "$dateSold,$address,$price,$sizeRange"
        }

        fun getProjectOfficialHdbCsvTitle(context: Context): String {
            val dateSold = context.getString(R.string.label_column_transaction_date_sold)
            val address = context.getString(R.string.label_column_transaction_address)
            val price = context.getString(R.string.label_column_transaction_price)

            return "$dateSold,$address,$price"
        }
    }
}