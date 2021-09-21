package sg.searchhouse.agentconnect.data.repository

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.cea.*
import sg.searchhouse.agentconnect.model.api.location.GetAddressPropertyTypeResponse
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CeaExclusiveRepository @Inject constructor(private val srxDataSource: SrxDataSource) {

    fun getFormTemplate(formType: Int, formId: Int? = null): Call<GetFormTemplateResponse> {
        return srxDataSource.getFormTemplate(formType, formId)
    }

    fun createUpdateForm(ceaFormSubmissionPO: CeaFormSubmissionPO): Call<GetFormTemplateResponse> {
        return srxDataSource.createUpdateForm(ceaFormSubmissionPO)
    }

    fun submitForm(formId: Int, files: List<File>): Call<CeaSubmitFormResponse> {
        val parts = arrayListOf<MultipartBody.Part>()
        files.map { file ->
            parts.add(
                MultipartBody.Part.createFormData(
                    file.name,
                    file.name,
                    file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                )
            )
        }
        return srxDataSource.submitForm(formId, parts)

    }

    fun getAddressByPropertyType(
        postalCode: Int,
        buildingNum: String? = null,
        skipCommercial: Boolean = true
    ): Call<GetAddressPropertyTypeResponse> {
        return srxDataSource.getAddressPropertyType(postalCode, skipCommercial, buildingNum)
    }

    fun deleteClient(formId: Int, position: Int): Call<DefaultResultResponse> {
        return srxDataSource.deleteClient(CeaDeleteClientRequest(formId, position))
    }

    fun findUnsubmittedCeaForms(type: String): Call<FindUnsubmittedCeaFormResponse> {
        return srxDataSource.findUnsubmittedCeaForms(type)
    }

    fun removeUnsubmittedCeaForms(ceaFormIds: List<String>): Call<DefaultResultResponse> {
        return srxDataSource.deleteUnsubmittedCeaForms(ceaFormIds)
    }
}