package sg.searchhouse.agentconnect.viewmodel.activity.listing.portal

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.data.repository.PortalListingRepository
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.GetPortalAPIsResponse
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalAccountPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.UpdatePortalAccountResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class PortalSettingsViewModel constructor(application: Application) :
    CoreViewModel(application) {

    @Inject
    lateinit var portalListingRepository: PortalListingRepository

    @Inject
    lateinit var applicationContext: Context

    val syncOptions = MutableLiveData<List<GetPortalAPIsResponse.SyncData>>()
    val selectedSyncOption = MutableLiveData<GetPortalAPIsResponse.SyncData>()

    val syncFrequencies = MutableLiveData<List<GetPortalAPIsResponse.SyncData>>()
    val selectedSyncFrequency = MutableLiveData<GetPortalAPIsResponse.SyncData>()

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val portalAccountPO = MutableLiveData<PortalAccountPO>()
    val portalApiResponse = MutableLiveData<GetPortalAPIsResponse>()

    val applySettingsBtnLabel = MutableLiveData<String>()
    val isApplySettingsBtnEnabled = MutableLiveData<Boolean>()
    val updatePortalAccountStatus = MutableLiveData<ApiStatus<UpdatePortalAccountResponse>>()

    init {
        viewModelComponent.inject(this)
        isApplySettingsBtnEnabled.value = false
    }

    fun getPortalAPIs() {
        ApiUtil.performRequest(
            applicationContext,
            portalListingRepository.getPortalAPIs(),
            onSuccess = { response ->
                syncOptions.postValue(response.syncOptions)
                syncFrequencies.postValue(response.syncFrequencies)
            },
            onFail = { println(it.error) },
            onError = { println("on error calling getPortalAPIs") }
        )
    }

    fun updatePortalAccountServer() {

        isApplySettingsBtnEnabled.value = false
        updatePortalAccountStatus.postValue(ApiStatus.loadingInstance())

        val syncOption = selectedSyncOption.value?.value ?: 0
        val syncFrequency = selectedSyncFrequency.value?.value ?: 0
        val pwd = password.value ?: ""
        portalAccountPO.value?.let { portalAccountPO ->
            val portalAccount = PortalAccountPO(
                id = portalAccountPO.id,
                username = portalAccountPO.username,
                password = pwd,
                portalType = portalAccountPO.portalType,
                portalId = portalAccountPO.portalId,
                syncOption = syncOption,
                syncFrequency = syncFrequency
            )

            ApiUtil.performRequest(
                applicationContext,
                portalListingRepository.updatePortalAccount(portalAccount),
                onSuccess = { updatePortalAccountStatus.postValue(ApiStatus.successInstance(it)) },
                onFail = { updatePortalAccountStatus.postValue(ApiStatus.failInstance(it)) },
                onError = { updatePortalAccountStatus.postValue(ApiStatus.errorInstance()) }
            )
        }
    }
}