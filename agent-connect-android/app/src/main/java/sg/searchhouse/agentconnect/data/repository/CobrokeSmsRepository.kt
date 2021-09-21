package sg.searchhouse.agentconnect.data.repository

import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.model.api.cobrokesms.*
import javax.inject.Inject
import javax.inject.Singleton

// https://streetsine.atlassian.net/wiki/spaces/SIN/pages/870350849/Cobroke+Notification
@Singleton
class CobrokeSmsRepository @Inject constructor(private val srxDataSource: SrxDataSource) {
    fun getIncludePublicListingIndicator(): Call<GetIncludePublicListingIndicatorResponse> {
        return srxDataSource.getIncludePublicListingIndicator()
    }

    fun getSmsTemplates(listingPos: List<CobrokeSmsListingPO>): Call<GetSmsTemplatesResponse> {
        // TODO Refactor
        val body: RequestBody = Gson().toJson(listingPos).toRequestBody(MultipartBody.FORM)
        return srxDataSource.getSmsTemplates(body)
    }

    fun sendSms(
        listingPos: List<CobrokeSmsListingPO>,
        smsTemplateId: Int,
        smsDateTimeValueEpochTimeInMs: Long? = null
    ): Call<SendSmsResponse> {
        val listingPosJson =
            Gson().toJson(listingPos)
        val listingPosPart =
            MultipartBody.Part.createFormData("listingPos", listingPosJson)

        return srxDataSource.sendSms(
            listingPosPart,
            smsTemplateId,
            smsDateTimeValueEpochTimeInMs
        )
    }

    fun sendSmsLimitEmail(): Call<SendSmsLimitEmailResponse> {
        return srxDataSource.sendSmsLimitEmail()
    }
}