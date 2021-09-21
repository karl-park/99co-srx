package sg.searchhouse.agentconnect.viewmodel.activity.listing.portal

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.gson.JsonObject
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.PortalListingRepository
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.*
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.PortalListingsType
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.AppPortalType
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.GetPortalAPIsResponse
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalAccountPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalListingPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalLoginResponse
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class PortalListingsViewModel constructor(application: Application) :
    ApiBaseViewModel<PortalLoginResponse>(application) {

    @Inject
    lateinit var portalListingRepository: PortalListingRepository

    @Inject
    lateinit var applicationContext: Context

    var successCount: Int = 0
    var failCount: Int = 0
    val failedListingIds = arrayListOf<String>()
    var clientPortalPO: PortalAccountPO? = null

    val portalAccountPO = MutableLiveData<PortalAccountPO>()
    val listings: LiveData<List<PortalListingPO>> =
        Transformations.map(mainResponse) { response ->
            response?.listings ?: emptyList()
        }
    val portalListingsByType = MutableLiveData<List<PortalListingPO>>()
    val selectedListings = MutableLiveData<ArrayList<PortalListingPO>>()
    val listingsMessage = MutableLiveData<String>()
    val isReadMore = MutableLiveData<Boolean>()
    val loggedOut = MutableLiveData<ApiStatus<DefaultResultResponse>>()
    val isSelectAll: LiveData<Boolean> =
        Transformations.map(selectedListings) { items ->
            portalListingsByType.value?.let {
                if (it.isEmpty()) {
                    false
                } else {
                    items.size == portalListingsByType.value?.size
                }
            }
        }
    val isListingsImported = MutableLiveData<Boolean>()
    val toggleListingsSyncStatus = MutableLiveData<ApiStatus<DefaultResultResponse>>()

    val selectedListingsType = MutableLiveData<PortalListingsType>()
    val actionBtnLabel: LiveData<String> = Transformations.map(selectedListingsType) {
        return@map when (it) {
            PortalListingsType.ALL_LISTINGS -> applicationContext.getString(R.string.action_import_to_srx)
            PortalListingsType.IMPORTED_LISTING -> applicationContext.getString(R.string.action_sync_listings)
            else -> ""
        }
    }
    val portalApiResponse = MutableLiveData<GetPortalAPIsResponse>()
    val activeListingsResult = MutableLiveData<ArrayList<JsonObject>>()
    val apiErrorMessage = MutableLiveData<String>()

    init {
        viewModelComponent.inject(this)
        selectedListingsType.value = PortalListingsType.ALL_LISTINGS
        resetData()
    }

    fun resetData() {
        selectedListings.value = arrayListOf()
        listingsMessage.value = ""
        isReadMore.value = false
        isListingsImported.value = false
    }

    //Server -> getting portal listings
    fun getServerPortalListings() {
        performRequest(portalListingRepository.getPortalListings(AppPortalType.PROPERTY_GURU.value))
    }

    //Client -> getting client portal listings
    @SuppressLint("LogNotTimber")
    fun getClientPortalListings(agentId: Int) {
        val activeListings = portalApiResponse.value?.templates?.activeListings ?: return
        val tempResults = arrayListOf<JsonObject>()
        activeListings.map { item ->
            val template = GetPortalAPIsResponse.Templates.TemplateHolder(
                item.api,
                item.apiPlaceholders,
                item.httpMethod,
                item.name,
                item.params,
                item.headers
            )

            if (template.params?.containsKey("agent_id") == true) {
                template.params?.set("agent_id", agentId.toString())
            }

            //TODO: in future create another request util for portal data source
            ApiUtil.performRequest(
                applicationContext,
                portalListingRepository.getListingsFromPortal(
                    template.headers,
                    agentId.toString(),
                    template
                ),
                onSuccess = { response ->
                    tempResults.add(response)
                    if (tempResults.size == activeListings.size) {
                        activeListingsResult.postValue(tempResults)
                        return@performRequest
                    }
                },
                onFail = { apiError ->
                    Log.e("PortalListingViewModel", apiError.error)
                    mainStatus.postValue(ApiStatus.StatusKey.FAIL)
                },
                onError = {
                    Log.e(
                        "PortalListingViewModel",
                        "Error in getting client portal listings"
                    )
                    mainStatus.postValue(ApiStatus.StatusKey.ERROR)
                },
                enablePrintRequestBody = false
            )
        }
    }

    //Client -> get listing from srx
    fun clientGetListings(listings: List<JsonObject>) {
        mainStatus.postValue(ApiStatus.StatusKey.LOADING)
        ApiUtil.performRequest(
            applicationContext,
            portalListingRepository.clientGetListings(
                AppPortalType.PROPERTY_GURU.value,
                listings
            ),
            onSuccess = {
                mainResponse.postValue(it)
                mainStatus.postValue(ApiStatus.StatusKey.SUCCESS)
            },
            onFail = {
                mainStatus.postValue(ApiStatus.StatusKey.FAIL)
            },
            onError = {
                mainStatus.postValue(ApiStatus.StatusKey.ERROR)
            }
        )
    }

    //Import listings
    fun importPortalListings() {
        val mode = SessionUtil.getListingPortalMode() ?: PortalMode.SERVER
        val listings = selectedListings.value ?: arrayListOf()
        //clear failed listings
        successCount = 0
        failCount = 0
        failedListingIds.clear()
        listings.map { portalListing ->
            if (mode == PortalMode.SERVER) {
                serverImportListings(portalListing)
            } else {
                getListingFromPortal(portalListing)
            }
        }
    }

    private fun serverImportListings(portalListingPO: PortalListingPO) {
        val selectedListingsCount = selectedListings.value?.count() ?: 0

        ApiUtil.performRequest(
            applicationContext,
            portalListingRepository.importPortalListing(portalListingPO),
            onSuccess = { handleServerClientImportSuccess(it.result, selectedListingsCount) },
            onFail = { handleServerClientImportFailed(portalListingPO, selectedListingsCount) },
            onError = { handleServerClientImportError(selectedListingsCount) }
        )
    }

    private fun getListingFromPortal(portalListingPO: PortalListingPO) {
        val listing = portalApiResponse.value?.templates?.listing ?: return
        ApiUtil.performRequest(
            applicationContext,
            portalListingRepository.getPortalListing(
                listing.headers,
                listing.api,
                portalListingPO.id
            ),
            onSuccess = { response ->
                clientImportListings(response.string(), portalListingPO)
            },
            onFail = { Log.e("PortalListings", it.error) },
            onError = {
                Log.e(
                    "PortalListings",
                    "Error in getting listing from portal with dynamic url"
                )
            },
            enablePrintRequestBody = false
        )
    }

    private fun clientImportListings(
        listing: String,
        portalListingPO: PortalListingPO
    ) {
        val selectedListingsCount = selectedListings.value?.count() ?: 0

        ApiUtil.performRequest(
            applicationContext,
            portalListingRepository.clientImportListing(
                AppPortalType.PROPERTY_GURU.value,
                listing
            ),
            onSuccess = { handleServerClientImportSuccess(it.result, selectedListingsCount) },
            onFail = {
                apiErrorMessage.postValue(it.error)
                handleServerClientImportFailed(portalListingPO, selectedListingsCount)
            },
            onError = { handleServerClientImportError(selectedListingsCount) }
        )
    }

    private fun handleServerClientImportSuccess(result: String, selectedListingsCount: Int) {
        if (result == "success") {
            successCount++
        }
        if (successCount + failCount == selectedListingsCount) {
            isListingsImported.postValue(true)
        }
    }

    private fun handleServerClientImportFailed(
        portalListingPO: PortalListingPO,
        selectedListingsCount: Int
    ) {
        failCount++
        failedListingIds.add(portalListingPO.id)
        if (successCount + failCount == selectedListingsCount) {
            isListingsImported.postValue(true)
        }
    }

    private fun handleServerClientImportError(selectedListingsCount: Int) {
        failCount++
        if (successCount + failCount == selectedListingsCount) {
            isListingsImported.postValue(false) // should be false
        }
    }

    //Sync
    fun toggleListingsSync(listing: PortalListingPO) {
        ApiUtil.performRequest(
            applicationContext,
            portalListingRepository.toggleListingsSync(listOf(listing)),
            onSuccess = { toggleListingsSyncStatus.postValue(ApiStatus.successInstance(it)) },
            onFail = { toggleListingsSyncStatus.postValue(ApiStatus.failInstance(it)) },
            onError = { toggleListingsSyncStatus.postValue(ApiStatus.errorInstance()) }
        )
    }

    fun logoutPortalServer() {
        ApiUtil.performRequest(
            applicationContext,
            portalListingRepository.serverLogoutPortal(AppPortalType.PROPERTY_GURU.value),
            onSuccess = {
                loggedOut.postValue(ApiStatus.successInstance(it))
            },
            onFail = {
                loggedOut.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                loggedOut.postValue(ApiStatus.errorInstance())
            }
        )
    }

    override fun shouldResponseBeOccupied(response: PortalLoginResponse): Boolean {
        return true
    }
}