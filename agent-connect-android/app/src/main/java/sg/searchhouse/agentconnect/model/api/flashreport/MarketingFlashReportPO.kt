package sg.searchhouse.agentconnect.model.api.flashreport

import android.content.Context
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.enumeration.api.ReportEnum
import sg.searchhouse.agentconnect.model.api.common.SrxDate
import sg.searchhouse.agentconnect.util.DateTimeUtil
import java.io.Serializable

data class MarketingFlashReportPO(
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("dateCreate")
    val dateCreate: SrxDate,
    @SerializedName("dateUpd")
    val dateUpd: SrxDate,
    @SerializedName("headline")
    val headline: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("marketFlashReportType")
    val marketFlashReportType: String,
    @SerializedName("observation")
    val observation: String,
    @SerializedName("readableReferenceDate")
    val readableReferenceDate: Int,
    @SerializedName("referenceDate")
    val referenceDate: SrxDate,
    @SerializedName("reportLink")
    val reportLink: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("graphicUrl")
    val graphicUrl: String
) : Serializable {

    fun getFormattedObservation(): String {
        return "<style>img{display: inline;height: auto;max-width: 100%;}p{line-height:1.6}div{line-height:1.6}</style>$observation"
    }

    fun getFlashReportType(context: Context): String {
        ReportEnum.FlashReportType.values().find { it.type == marketFlashReportType }?.let {
            return context.getString(it.label)
        }
        return marketFlashReportType
    }

    fun getFormattedReferenceDateCreated(): String {
        return getFormattedDate(
            DateTimeUtil.convertTimeStampToString(
                referenceDate.time,
                DateTimeUtil.FORMAT_DATE_8
            )
        )
    }

    private fun getFormattedDate(date: String): String {
        val year = DateTimeUtil.getConvertedFormatDate(
            date,
            DateTimeUtil.FORMAT_DATE_8,
            DateTimeUtil.FORMAT_YEAR_SHORT
        )
        val month = DateTimeUtil.getConvertedFormatDate(
            date,
            DateTimeUtil.FORMAT_DATE_8,
            DateTimeUtil.FORMAT_MONTH
        )
        return "$month \'$year"
    }
}