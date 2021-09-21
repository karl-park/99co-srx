package sg.searchhouse.agentconnect.util

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.pixplicity.easyprefs.library.Prefs
import sg.searchhouse.agentconnect.constant.SharedPreferenceKey
import sg.searchhouse.agentconnect.model.api.homereport.GenerateHomeReportRequestBody
import sg.searchhouse.agentconnect.model.api.xvalue.SearchWithWalkupResponse
import sg.searchhouse.agentconnect.model.app.ExistingHomeReport
import java.net.URLDecoder
import java.net.URLEncoder

object HomeReportUtil {
    private const val MAX_HISTORY_SIZE = 10

    fun saveNewHomeReport(
        address: String,
        url: String,
        localFileName: String,
        requestBody: GenerateHomeReportRequestBody,
        walkupResponseData: SearchWithWalkupResponse.Data,
        details: ExistingHomeReport.Details
    ) {
        val report = ExistingHomeReport(
            address,
            url,
            localFileName,
            requestBody,
            walkupResponseData,
            details
        )
        val reports = (listOf(report) + getExistingHomeReports()).take(MAX_HISTORY_SIZE)
        val reportsString = reports.joinToString(";") {
            //  Encode to escape the delimiter `;`
            URLEncoder.encode(Gson().toJson(it, ExistingHomeReport::class.java), "utf-8")
        }
        Prefs.putString(SharedPreferenceKey.PREF_EXISTING_HOME_REPORTS, reportsString)
    }

    fun getExistingHomeReports(): List<ExistingHomeReport> {
        val homeReportsString =
            Prefs.getString(SharedPreferenceKey.PREF_EXISTING_HOME_REPORTS, "")
        return homeReportsString.split(";").mapNotNull {
            try {
                val decoded = URLDecoder.decode(it, "utf-8")
                Gson().fromJson(decoded, ExistingHomeReport::class.java)
            } catch (e: JsonSyntaxException) {
                null
            }
        }
    }
}