package sg.searchhouse.agentconnect.viewmodel.activity.chat

import android.app.Application
import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.repository.ChatDbRepository
import sg.searchhouse.agentconnect.data.repository.ChatRepository
import sg.searchhouse.agentconnect.dsl.performAsyncQueryLenient
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.agent.AgentPO
import sg.searchhouse.agentconnect.model.api.chat.GetSsmConversationInfoResponse
import sg.searchhouse.agentconnect.model.api.chat.SsmConversationSettingPO
import sg.searchhouse.agentconnect.model.api.userprofile.UserPO
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class ChatMessagingInfoViewModel constructor(application: Application) :
    ApiBaseViewModel<GetSsmConversationInfoResponse>(application) {

    @Inject
    lateinit var chatRepository: ChatRepository

    @Inject
    lateinit var chatDbRepository: ChatDbRepository

    @Inject
    lateinit var applicationContext: Context

    var userPO: UserPO? = null
    var phoneNumberToCall: Int = 0
    var members = arrayListOf<UserPO>()
    var removedMembers = arrayListOf<UserPO>()

    //Live data
    val editTitle = MutableLiveData<Boolean>()
    val groupName = MutableLiveData<String>()
    val errorGroupName = MutableLiveData<String>()
    val conversationSetting = MutableLiveData<SsmConversationSettingPO>()
    val removeMembersStatus = MutableLiveData<ApiStatus<DefaultResultResponse>>()
    val updateSettingStatus = MutableLiveData<ApiStatus<DefaultResultResponse>>()
    val showGroupName = MutableLiveData<Boolean>()
    val conversationId = MutableLiveData<String>()

    init {
        viewModelComponent.inject(this)

        editTitle.value = false
    }

    fun getSsmConversationInfo(conversationId: String) {
        if (NumberUtil.isNaturalNumber(conversationId)) {
            performRequest(chatRepository.getSsmConversationInfo(conversationId.toInt()))
        }
    }

    fun removeMembersFromConversation(members: ArrayList<UserPO>) {
        val setting = conversationSetting.value ?: return
        removeMembersStatus.postValue(ApiStatus.loadingInstance())
        ApiUtil.performRequest(
            applicationContext,
            chatRepository.removeMembersFromConversation(setting.conversationId, members),
            onSuccess = { responseBody ->
                removeMembersStatus.postValue(
                    ApiStatus.successInstance(
                        responseBody
                    )
                )
            },
            onFail = { apiError -> removeMembersStatus.postValue(ApiStatus.failInstance(apiError)) },
            onError = { removeMembersStatus.postValue(ApiStatus.errorInstance()) }
        )
    }

    fun addMembersToConversation(agents: ArrayList<AgentPO>) {
        val users: ArrayList<UserPO> = ArrayList()
        //userId in agentPO is Int & id in userPO is string
        agents.forEach {
            users.add(
                UserPO(
                    id = it.userId,
                    email = it.email,
                    name = it.name,
                    mobileCountryCode = AppConstant.COUNTRY_CODE_SINGAPORE,
                    mobileLocalNum = it.mobile.toInt(),
                    photo = it.photo,
                    companyName = it.agencyName,
                    debugAllowed = false
                )
            )
        }
        val setting = conversationSetting.value ?: return
        ApiUtil.performRequest(
            applicationContext,
            chatRepository.addMembersToConversation(setting.conversationId, users),
            onSuccess = { getSsmConversationInfo(setting.conversationId) },
            onFail = { apiError -> Log.e("MessagingInfo", apiError.error) },
            onError = { Log.e("MessagingInfo", "Getting error in addMembers") }
        )
    }

    private fun updateSsmConversationSettings() {
        val setting = conversationSetting.value ?: return
        val groupName = groupName.value ?: return
        updateSettingStatus.postValue(ApiStatus.loadingInstance())
        ApiUtil.performRequest(
            applicationContext,
            chatRepository.updateSsmConversationSettings(
                SsmConversationSettingPO(
                    setting.conversationId,
                    groupName,
                    setting.photoUrl,
                    setting.adminInd,
                    setting.publicInd,
                    setting.adminCommunicationInd,
                    setting.notificationsDisabledInd
                )
            ),
            onSuccess = { response ->
                updateSettingStatus.postValue(
                    ApiStatus.successInstance(
                        response
                    )
                )
            },
            onFail = { apiError ->
                updateSettingStatus.postValue(
                    ApiStatus.failInstance(
                        apiError
                    )
                )
            },
            onError = { updateSettingStatus.postValue(ApiStatus.errorInstance()) }
        )
    }

    fun findConversationByUserId(id: Int) {
        chatDbRepository.findConversationIdByUserId(userId = id).performAsyncQueryLenient(
            onSuccess = {
                conversationId.postValue(it)
            }, onFail = {
                conversationId.postValue(null)
            })
    }

    fun editMode() {
        val isEditTitle = editTitle.value ?: return
        if (isEditTitle) {
            if (isValidate()) {
                editTitle.value = !isEditTitle
                updateSsmConversationSettings()
            }
        } else {
            editTitle.value = !isEditTitle
            errorGroupName.value = ""
        }
    }

    private fun isValidate(): Boolean {
        val groupName = groupName.value ?: return false
        return if (TextUtils.isEmpty(groupName.trim())) {
            errorGroupName.value = applicationContext.getString(R.string.error_required_field)
            false
        } else {
            true
        }
    }

    fun afterTextChangeGroupName(editable: Editable?) {
        val groupName = editable?.toString() ?: ""
        if (groupName.trim().isNotEmpty()) {
            errorGroupName.value = ""
        }
    }

    override fun shouldResponseBeOccupied(response: GetSsmConversationInfoResponse): Boolean {
        return response.members.isNotEmpty()
    }
}