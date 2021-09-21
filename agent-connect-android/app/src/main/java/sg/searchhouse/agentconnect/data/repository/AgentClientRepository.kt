package sg.searchhouse.agentconnect.data.repository

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.enumeration.app.ClientModeOption
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.agentclient.GetAgentClientsInviteMessageResponse
import sg.searchhouse.agentconnect.model.api.agentclient.GetAgentClientsResponse
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

// Doc: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/930512913/Agent+Client+V1+API
@Singleton
class AgentClientRepository @Inject constructor(private val srxDataSource: SrxDataSource) {
    fun getAgentClients(sortAsc: Boolean, searchText: String): Call<GetAgentClientsResponse> {
        return srxDataSource.getAgentClients(sortAsc, searchText)
    }

    fun getAgentClientsInviteMessage(): Call<GetAgentClientsInviteMessageResponse> {
        return srxDataSource.getAgentClientsInviteMessage()
    }

    fun sendClientMessages(
        mode: ClientModeOption,
        message: String,
        recipientPtUserIds: String,
        links: String? = null,
        files: List<File>? = null
    ): Call<DefaultResultResponse> {

        //Note: By api requirement, need to convert to request body
        val modeParam = mode.value.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val messageParam = message.toRequestBody("text/plain".toMediaTypeOrNull())
        val recipientPtUserIdsParam =
            recipientPtUserIds.toRequestBody("text/plain".toMediaTypeOrNull())
        val linkParam = links?.toRequestBody("text/plain".toMediaTypeOrNull())

        val requestBodyParts = if (files != null) {
            val multipartBody = MultipartBody.Builder()
            multipartBody.setType(MultipartBody.FORM)
            files.map { file ->
                multipartBody.addFormDataPart(
                    "File${file.name}",
                    file.name,
                    file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                )
            }
            val requestBody = multipartBody.build()
            requestBody.parts
        } else {
            null
        }

        return srxDataSource.sendMessage(
            mode = modeParam,
            message = messageParam,
            recipientPtUserIds = recipientPtUserIdsParam,
            links = linkParam,
            files = requestBodyParts
        )
    }
}