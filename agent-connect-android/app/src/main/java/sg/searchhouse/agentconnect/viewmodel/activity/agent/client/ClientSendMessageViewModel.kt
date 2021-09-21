package sg.searchhouse.agentconnect.viewmodel.activity.agent.client

import android.app.Application
import android.content.Context
import android.net.Uri
import android.text.Editable
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.AgentClientRepository
import sg.searchhouse.agentconnect.data.repository.AgentRepository
import sg.searchhouse.agentconnect.enumeration.app.ClientModeOption
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.agent.GetAgentDetailsResponse
import sg.searchhouse.agentconnect.model.api.agent.SRXPropertyUserPO
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.ImageUtil
import sg.searchhouse.agentconnect.util.StringUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import java.util.*
import javax.inject.Inject

class ClientSendMessageViewModel constructor(application: Application) :
    CoreViewModel(application) {

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var repository: AgentClientRepository

    @Inject
    lateinit var agentRepository: AgentRepository

    val photos = arrayListOf<Uri>()

    val mode = MutableLiveData<ClientModeOption>()
    val clients = MutableLiveData<List<SRXPropertyUserPO>>()

    val clientNames = MutableLiveData<String>()
    val message = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    val senderName = MutableLiveData<String>()

    val link = MutableLiveData<String>()
    val isLinkShowed = MutableLiveData<Boolean>()
    val errorLink = MutableLiveData<String>()

    val isAttachedPhotosShowed = MutableLiveData<Boolean>()
    val uploadedPhotoUris = MutableLiveData<List<Uri>>()
    val maxPhotosError = MutableLiveData<String>()

    val btnState = MutableLiveData<ButtonState>()
    val isBtnEnabled: LiveData<Boolean> = Transformations.map(btnState) {
        return@map when (it) {
            ButtonState.SUBMITTING -> false
            else -> true
        }
    }
    val btnTextLabel: LiveData<String> = Transformations.map(btnState) {
        return@map when (it) {
            ButtonState.SUBMITTING -> applicationContext.getString(R.string.action_sending_message)
            else -> applicationContext.getString(R.string.action_send_message)
        }
    }

    val sendMessageStatus = MutableLiveData<ApiStatus.StatusKey>()
    val sendMessageResponse = MutableLiveData<ApiStatus<DefaultResultResponse>>()
    val successMessage = MutableLiveData<String>()

    val getAgentDetailStatus = MutableLiveData<ApiStatus<GetAgentDetailsResponse>>()

    init {
        viewModelComponent.inject(this)
        setupInitialData()
    }

    private fun setupInitialData() {
        btnState.value = ButtonState.NORMAL
    }

    fun onSendMessageButtonClicked() {
        if (isValidate()) {
            sendMessagesToClients()
        }
    }

    fun getAgentDetails() {
        ApiUtil.performRequest(
            applicationContext,
            agentRepository.getAgentDetails(),
            onSuccess = {
                senderName.postValue("-${it.data.name} ${it.data.agencyName.toUpperCase(Locale.getDefault())}")
            },
            onFail = {
                getAgentDetailStatus.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                getAgentDetailStatus.postValue(ApiStatus.errorInstance())
            }
        )
    }

    private fun isValidate(): Boolean {
        var isValidated = true

        if (isLinkShowed.value == true && link.value == null) {
            errorLink.value = applicationContext.getString(R.string.error_required_field)
            isValidated = false
        } else if (isLinkShowed.value == true &&
            !StringUtil.isWebUrlValid(link.value?.toString()?.trim() ?: "")
        ) {
            errorLink.value = applicationContext.getString(R.string.error_msg_invalid_web_url)
            isValidated = false
        } else {
            errorLink.value = null
        }

        if (TextUtils.isEmpty(message.value)) {
            errorMessage.value = applicationContext.getString(R.string.error_required_field)
            isValidated = false
        } else {
            errorMessage.value = null
        }

        return isValidated
    }

    private fun sendMessagesToClients() {
        btnState.value = ButtonState.SUBMITTING

        val clientMode = mode.value ?: return
        val clientIds = clients.value ?: return
        val recipientPtUserIds = clientIds.joinToString(",") { it.ptUserId.toString() }
        val uris = uploadedPhotoUris.value ?: listOf()
        val files = if (uris.isEmpty()) {
            null
        } else {
            uris.mapIndexedNotNull { index, uri ->
                ImageUtil.getFileFromUri(
                    applicationContext,
                    uri,
                    index
                )
            }
        }

        ApiUtil.performRequest(
            applicationContext,
            repository.sendClientMessages(
                mode = clientMode,
                message = message.value ?: "",
                recipientPtUserIds = recipientPtUserIds,
                links = link.value,
                files = files
            ),
            onSuccess = {
                sendMessageStatus.postValue(ApiStatus.StatusKey.SUCCESS)
                sendMessageResponse.postValue(ApiStatus.successInstance(it))
                btnState.postValue(ButtonState.SUBMITTED)
            },
            onFail = {
                sendMessageStatus.postValue(ApiStatus.StatusKey.FAIL)
                sendMessageResponse.postValue(ApiStatus.failInstance(it))
                btnState.postValue(ButtonState.NORMAL)
            },
            onError = {
                sendMessageStatus.postValue(ApiStatus.StatusKey.ERROR)
                sendMessageResponse.postValue(ApiStatus.errorInstance())
                btnState.postValue(ButtonState.NORMAL)
            }
        )
    }

    fun afterTextChangedLink(editable: Editable?) {
        val link = editable?.toString()?.trim() ?: ""
        when {
            TextUtils.isEmpty(link) -> errorLink.value =
                applicationContext.getString(R.string.error_required_field)
            !StringUtil.isWebUrlValid(link) -> errorLink.value =
                applicationContext.getString(R.string.error_msg_invalid_web_url)
            else -> errorLink.value = null
        }
    }

    fun afterTextChangedMessage(editable: Editable?) {
        if (!TextUtils.isEmpty(editable?.toString())) {
            errorMessage.value = null
        } else {
            errorMessage.value = applicationContext.getString(R.string.error_required_field)
        }
    }
}