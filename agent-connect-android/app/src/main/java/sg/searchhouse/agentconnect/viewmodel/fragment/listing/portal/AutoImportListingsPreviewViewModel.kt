package sg.searchhouse.agentconnect.viewmodel.fragment.listing.portal

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import okhttp3.ResponseBody
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.PortalListingRepository
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.PortalMode
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.GetPortalAPIsResponse
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalAccountPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalListingPO
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class AutoImportListingsPreviewViewModel constructor(application: Application) :
    CoreViewModel(application) {

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var portalListingRepository: PortalListingRepository

    var importSuccessCount = 0
    var importFailedCount = 0

    val totalListingCountLabel = MutableLiveData<String>()
    val listings = MutableLiveData<List<PortalListingPO>>()
    val portalAccountPO = MutableLiveData<PortalAccountPO>()

    val btnState = MutableLiveData<ButtonState>()
    val actionBtnLabel: LiveData<String> = Transformations.map(btnState) {
        return@map when (it) {
            ButtonState.SUBMITTING -> applicationContext.getString(R.string.action_importing_listings)
            else -> applicationContext.getString(R.string.action_import_to_srx)
        }
    }
    val isActionBtnEnabled: LiveData<Boolean> = Transformations.map(btnState) {
        return@map when (it) {
            ButtonState.SUBMITTING -> false
            else -> true
        }
    }

    val isImportingListingDone = MutableLiveData<Boolean>()

    val listingsMessage = MutableLiveData<String>()
    val selectedListings = MutableLiveData<List<PortalListingPO>>()
    val isSelectAll: LiveData<Boolean> = Transformations.map(selectedListings) { items ->
        val list = listings.value ?: emptyList()
        return@map if (items.isEmpty()) {
            false
        } else {
            items.size == list.size
        }
    }

    private val portalAPIResponse = MutableLiveData<GetPortalAPIsResponse>()

    init {
        viewModelComponent.inject(this)
        btnState.value = ButtonState.NORMAL
    }

    fun performImportListings() {
        val listings = selectedListings.value ?: emptyList()
        when (portalAPIResponse.value?.getPortalMode()) {
            PortalMode.CLIENT -> {
                btnState.postValue(ButtonState.SUBMITTING)
                importSuccessCount = 0
                importFailedCount = 0
                listings.map { portalListingPO -> getListingsFromPortalAPI(portalListingPO) }
            }
            PortalMode.SERVER -> {
                btnState.postValue(ButtonState.SUBMITTING)
                importSuccessCount = 0
                importFailedCount = 0
                listings.map { portalListingPO -> performServerListingImport(portalListingPO) }
            }
            else -> {
                btnState.postValue(ButtonState.NORMAL)
            }
        }
    }

    //client
    private fun getListingsFromPortalAPI(portalListingPO: PortalListingPO) {
        val listingTemplateHolder = portalAPIResponse.value?.templates?.listing ?: return
        ApiUtil.performExternalRequest(
            applicationContext,
            portalListingRepository.getPortalListing(
                listingTemplateHolder.headers,
                listingTemplateHolder.api,
                portalListingPO.id
            ),
            onSuccess = { response ->
                performClientListingImport(response.string(), portalListingPO)
            },
            onFail = {
                println(it)
            },
            onError = {
                println("Error")
            },
            enablePrintRequestBody = false,
            errorClass = ResponseBody::class.java
        )
    }

    //client
    private fun performClientListingImport(
        listing: String,
        portalListingPO: PortalListingPO
    ) {
        ApiUtil.performRequest(
            applicationContext,
            portalListingRepository.clientImportListing(
                ListingManagementEnum.AppPortalType.PROPERTY_GURU.value,
                listing,
                accountId = portalAccountPO.value?.id
            ),
            onSuccess = {
                if (it.result == "success") {
                    handleSuccessAndError(ApiStatus.StatusKey.SUCCESS, portalListingPO)
                }
            },
            onFail = {
                btnState.postValue(ButtonState.ERROR)
                handleSuccessAndError(ApiStatus.StatusKey.FAIL, portalListingPO)
            },
            onError = {
                btnState.postValue(ButtonState.ERROR)
                handleSuccessAndError(ApiStatus.StatusKey.ERROR, portalListingPO)
            }
        )
    }

    //server
    private fun performServerListingImport(portalListingPO: PortalListingPO) {
        ApiUtil.performRequest(
            applicationContext,
            portalListingRepository.importPortalListing(portalListingPO),
            onSuccess = {
                if (it.result == "success") {
                    handleSuccessAndError(ApiStatus.StatusKey.SUCCESS, portalListingPO)
                }
            },
            onFail = {
                btnState.postValue(ButtonState.ERROR)
                handleSuccessAndError(ApiStatus.StatusKey.FAIL, portalListingPO)
            },
            onError = {
                btnState.postValue(ButtonState.ERROR)
                handleSuccessAndError(ApiStatus.StatusKey.ERROR, portalListingPO)
            }

        )
    }


    //both client and server
    private fun handleSuccessAndError(
        statusKey: ApiStatus.StatusKey,
        portalListingPO: PortalListingPO
    ) {
        //TODO: to continue doing this
        when (statusKey) {
            ApiStatus.StatusKey.SUCCESS -> {
                importSuccessCount += 1
                if (importSuccessCount + importFailedCount == selectedListings.value?.count()) {
                    isImportingListingDone.postValue(true)
                }
            }
            ApiStatus.StatusKey.FAIL -> {
                //TODO: to add portal listing id
                importFailedCount += 1
                if (importSuccessCount + importFailedCount == selectedListings.value?.count()) {
                    isImportingListingDone.postValue(true)
                }
            }
            ApiStatus.StatusKey.ERROR -> {
                importFailedCount += 1
                if (importSuccessCount + importFailedCount == selectedListings.value?.count()) {
                    isImportingListingDone.postValue(true)
                }
            }
            else -> {
                btnState.postValue(ButtonState.NORMAL)
            }
        }
    }

    @SuppressLint("LogNotTimber")
    fun getAPIs() {
        ApiUtil.performRequest(
            applicationContext,
            portalListingRepository.getPortalAPIs(),
            onSuccess = { response -> portalAPIResponse.postValue(response) },
            onFail = { Log.e("getAPIS", it.toString()) },
            onError = { Log.e("getAPIS", "Error in calling getAPIs") }
        )
    }
}