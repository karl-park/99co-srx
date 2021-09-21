package sg.searchhouse.agentconnect.viewmodel.fragment.listing.create

import android.app.Application
import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.repository.LocationSearchRepository
import sg.searchhouse.agentconnect.data.repository.PortalListingRepository
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.AppPortalType
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.ClientLoginResponse
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.GetPortalAPIsResponse
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalLoginResponse
import sg.searchhouse.agentconnect.model.api.location.FindPropertiesResponse
import sg.searchhouse.agentconnect.model.api.location.PropertyPO
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.CreateListingTrackerPO
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class ListingAddressSearchViewModel constructor(application: Application) :
    ApiBaseViewModel<FindPropertiesResponse>(application) {

    @Inject
    lateinit var locationSearchRepository: LocationSearchRepository

    @Inject
    lateinit var portalListingRepository: PortalListingRepository

    @Inject
    lateinit var applicationContext: Context

    //find properties
    val addressSearchSource = MutableLiveData<ListingManagementEnum.AddressSearchSource>()
    val ownershipType = MutableLiveData<ListingEnum.OwnershipType>()

    private val _createListingTracker = MutableLiveData<CreateListingTrackerPO>()
    val createListingTracker: LiveData<CreateListingTrackerPO>
        get() = _createListingTracker

    val properties: LiveData<List<PropertyPO>> =
        Transformations.map(mainResponse) { it?.properties ?: emptyList() }
    val showAddressSearchList: LiveData<Boolean> =
        Transformations.map(properties) { response ->
            response?.isNotEmpty() == true
        }

    //Portal Listings
    val portalApiResponse = MutableLiveData<GetPortalAPIsResponse>()
    val importListingStatus = MutableLiveData<ApiStatus.StatusKey>()
    val portalLoginError = MutableLiveData<String>()

    //Portal API calls response
    val getPortalAPIsResponse = MutableLiveData<ApiStatus<GetPortalAPIsResponse>>()
    val getPortalListingsResponse = MutableLiveData<ApiStatus<PortalLoginResponse>>()
    val clientLoginResponse = MutableLiveData<ApiStatus<ClientLoginResponse>>()

    val shouldShowEmptyButtons = MediatorLiveData<Boolean>()

    init {
        viewModelComponent.inject(this)
        setupShouldShowEmptyButtons()
    }

    fun setCreateListingTrackerObject(createListingTrackerPO: CreateListingTrackerPO) {
        _createListingTracker.value = createListingTrackerPO
    }

    private fun setupShouldShowEmptyButtons() {
        shouldShowEmptyButtons.addSource(showAddressSearchList) {
            shouldShowEmptyButtons.postValue(checkShouldShowEmptyButtons())
        }
        shouldShowEmptyButtons.addSource(addressSearchSource) {
            shouldShowEmptyButtons.postValue(checkShouldShowEmptyButtons())
        }
    }

    private fun checkShouldShowEmptyButtons(): Boolean {
        return showAddressSearchList.value != true && addressSearchSource.value != ListingManagementEnum.AddressSearchSource.CREATE_LISTING
    }

    //API CALLS
    fun findProperties(searchText: String) {
        if (!TextUtils.isEmpty(searchText)) {
            performRequest(
                locationSearchRepository.findProperties(
                    searchText,
                    AppConstant.BATCH_SIZE_DOUBLE
                )
            )
        } else {
            mainResponse.postValue(null)
        }
    }

    //Portal Listing
    fun getPortalAPIs() {
        importListingStatus.postValue(ApiStatus.StatusKey.LOADING)
        ApiUtil.performRequest(
            applicationContext,
            portalListingRepository.getPortalAPIs(),
            onSuccess = {
                getPortalAPIsResponse.postValue(ApiStatus.successInstance(it))
            },
            onFail = {
                getPortalAPIsResponse.postValue(ApiStatus.failInstance(it))
                importListingStatus.postValue(ApiStatus.StatusKey.FAIL)
            },
            onError = {
                getPortalAPIsResponse.postValue(ApiStatus.errorInstance())
                importListingStatus.postValue(ApiStatus.StatusKey.ERROR)
            }
        )
    }

    //Call Portal Listings for SERVER SIDE only
    fun getPortalListings() {
        ApiUtil.performRequest(
            applicationContext,
            portalListingRepository.getPortalListings(AppPortalType.PROPERTY_GURU.value),
            onSuccess = {
                getPortalListingsResponse.postValue(ApiStatus.successInstance(it))
                importListingStatus.postValue(ApiStatus.StatusKey.SUCCESS)
            },
            onFail = {
                getPortalListingsResponse.postValue(ApiStatus.failInstance(it))
                importListingStatus.postValue(ApiStatus.StatusKey.FAIL)
            },
            onError = {
                getPortalListingsResponse.postValue(ApiStatus.errorInstance())
                importListingStatus.postValue(ApiStatus.StatusKey.ERROR)
            }
        )
    }

    //Call portal login for CLIENT SIDE only
    fun portalLogin(email: String, password: String) {
        val loginTemplate = portalApiResponse.value?.templates?.login ?: return
        loginTemplate.params?.set("email", email)
        loginTemplate.params?.set("password", password)

        ApiUtil.performRequest(
            applicationContext,
            portalListingRepository.portalLogin(loginTemplate),
            onSuccess = { response ->
                if (response.containsKey("user_id")) {
                    if (response.getValue("user_id") is Double) {
                        //calling srx server again for login
                        clientLogin(email, password)
                    }
                } else if (response.containsKey("errors")) {
                    //rare to encounter this error .. but in case
                    portalLoginError.postValue(applicationContext.getString(R.string.error_msg_portal_login_info))
                }
            },
            onFail = {
                Log.e("PortalLogin", "Getting failed in portal login")
                importListingStatus.postValue(ApiStatus.StatusKey.FAIL)
            },
            onError = {
                Log.e("PortalLogin", "Getting error in portal login")
                importListingStatus.postValue(ApiStatus.StatusKey.ERROR)
            },
            enablePrintRequestBody = false
        )
    }

    //FOR CLIENT SIDE
    private fun clientLogin(email: String, password: String) {
        ApiUtil.performRequest(
            applicationContext,
            portalListingRepository.clientLoginPortal(
                email,
                password,
                AppPortalType.PROPERTY_GURU.value
            ),
            onSuccess = {
                clientLoginResponse.postValue(ApiStatus.successInstance(it))
                importListingStatus.postValue(ApiStatus.StatusKey.SUCCESS)
            },
            onFail = {
                clientLoginResponse.postValue(ApiStatus.failInstance(it))
                importListingStatus.postValue(ApiStatus.StatusKey.FAIL)
            },
            onError = {
                clientLoginResponse.postValue(ApiStatus.errorInstance())
                importListingStatus.postValue(ApiStatus.StatusKey.ERROR)
            }
        )
    }

    override fun shouldResponseBeOccupied(response: FindPropertiesResponse): Boolean {
        return response.properties.isNotEmpty()
    }
}