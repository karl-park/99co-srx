package sg.searchhouse.agentconnect.viewmodel.fragment.listing.portal

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.ListingManagementRepository
import sg.searchhouse.agentconnect.dsl.performRequest
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.GetOwnerCertificationNotificationTemplateResponse
import sg.searchhouse.agentconnect.model.app.DeviceContact
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class AutoImportCertifiedListingsViewModel constructor(application: Application) :
    CoreViewModel(application) {

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var listingManagementRepository: ListingManagementRepository

    val template = MutableLiveData<String>()
    val getTemplateResponse =
        MutableLiveData<ApiStatus<GetOwnerCertificationNotificationTemplateResponse>>()

    val contacts = MutableLiveData<MutableList<DeviceContact>>()
    val sendOwnerCertificationResponse = MutableLiveData<ApiStatus<DefaultResultResponse>>()

    private val actionBtnState = MutableLiveData<ButtonState>()
    val actionBtnLabel: LiveData<String> = Transformations.map(actionBtnState) {
        return@map when (it) {
            ButtonState.SUBMITTING -> applicationContext.getString(R.string.action_sending_invite)
            else -> applicationContext.getString(R.string.action_send_invite)
        }
    }
    val isActionBtnEnabled: LiveData<Boolean> = Transformations.map(actionBtnState) {
        return@map when (it) {
            ButtonState.SUBMITTING -> false
            else -> true
        }
    }

    init {
        viewModelComponent.inject(this)
        initializeData()
    }

    private fun initializeData() {
        actionBtnState.value = ButtonState.NORMAL
    }

    fun performGetTemplate() {
        getOwnerCertificationNotificationTemplate()
    }

    private fun getOwnerCertificationNotificationTemplate() {
        listingManagementRepository.getOwnerCertificationNotificationTemplate().performRequest(
            applicationContext,
            onSuccess = {
                if (it.result == "success") {
                    template.postValue(it.template)
                }
            },
            onFail = {
                getTemplateResponse.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                getTemplateResponse.postValue(ApiStatus.errorInstance())
            }
        )
    }


    fun performSendInvite() {
        val items = contacts.value?.mapNotNull { it.mobileNumber } ?: return

        actionBtnState.postValue(ButtonState.SUBMITTING)

        performSendOwnerCertificationNotifications(items)
    }

    private fun performSendOwnerCertificationNotifications(items: List<String>) {
        listingManagementRepository.sendOwnerCertificationNotifications(items).performRequest(
            applicationContext,
            onSuccess = {
                sendOwnerCertificationResponse.postValue(ApiStatus.successInstance(it))
                actionBtnState.postValue(ButtonState.SUBMITTED)
            },
            onFail = {
                sendOwnerCertificationResponse.postValue(ApiStatus.failInstance(it))
                actionBtnState.postValue(ButtonState.NORMAL)
            },
            onError = {
                sendOwnerCertificationResponse.postValue(ApiStatus.errorInstance())
                actionBtnState.postValue(ButtonState.NORMAL)
            }
        )
    }
}