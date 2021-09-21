package sg.searchhouse.agentconnect.viewmodel.activity.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import sg.searchhouse.agentconnect.data.repository.*
import sg.searchhouse.agentconnect.dsl.performAsyncQueryStrict
import sg.searchhouse.agentconnect.dsl.performRequest
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.model.api.agent.GetAgentDetailsResponse
import sg.searchhouse.agentconnect.model.api.auth.CheckVersionResponse
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.GetPortalAPIsResponse
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.GetPortalAPIsResponse.Templates.TemplateHolder
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalGetListingsAutoResponse
import sg.searchhouse.agentconnect.model.api.userprofile.GetProfileResponse
import sg.searchhouse.agentconnect.model.db.SsmConversationEntity
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class MainViewModel constructor(application: Application) :
    ApiBaseViewModel<GetAgentDetailsResponse>(application) {

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var userProfileRepository: UserProfileRepository

    @Inject
    lateinit var agentRepository: AgentRepository

    @Inject
    lateinit var chatRepository: ChatRepository

    @Inject
    lateinit var chatDbRepository: ChatDbRepository

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var portalListingRepository: PortalListingRepository

    val agentMobileNumber = MutableLiveData<Int>()

    val shouldShowBottomBar = MutableLiveData<Boolean>()
    val getUserProfileStatus = MutableLiveData<GetProfileResponse>()
    val checkVersionStatus = MutableLiveData<ApiStatus<CheckVersionResponse>>()

    val getHasConversationStatus = MutableLiveData<ApiStatus<SsmConversationEntity>>()

    //PORTAL
    val getPortalAPIResponse = MutableLiveData<ApiStatus<GetPortalAPIsResponse>>()
    val getListingAutoResponse =
        MutableLiveData<ApiStatus<PortalGetListingsAutoResponse>>()

    init {
        viewModelComponent.inject(this)
    }

    fun getAgentDetails() {
        //get logged in agent detail information to check consortium or not
        performRequest(agentRepository.getAgentDetails())
    }

    fun getUserProfile() {
        ApiUtil.performRequest(
            applicationContext,
            userProfileRepository.getProfile(),
            onSuccess = { response ->
                getUserProfileStatus.postValue(response)
            },
            onFail = { println(it.error) },
            onError = { println("Error in get user profile") }
        )
    }

    fun checkHasConversationFromLocal() {
        chatDbRepository.getOneConversation().performAsyncQueryStrict(onSuccess = {
            getHasConversationStatus.postValue(ApiStatus.successInstance(it))
        }, onEmpty = {
            getHasConversationStatus.postValue(ApiStatus.successInstance(null))
        }, onError = {
            getHasConversationStatus.postValue(ApiStatus.errorInstance())
        })
    }

    fun checkVersion() {
        ApiUtil.performRequest(
            applicationContext,
            authRepository.checkVersion(),
            onSuccess = { checkVersionStatus.postValue(ApiStatus.successInstance(it)) },
            onFail = { checkVersionStatus.postValue(ApiStatus.failInstance(it)) },
            onError = { checkVersionStatus.postValue(ApiStatus.errorInstance()) }
        )
    }

    //Auto Import Listings
    fun getPortalAPIs() {
        ApiUtil.performRequest(
            applicationContext,
            portalListingRepository.getPortalAPIs(),
            onSuccess = { response ->
                when (response.getPortalMode()) {
                    ListingManagementEnum.PortalMode.CLIENT -> {
                        getPortalAgentByMobile(
                            agentTemplate = response.templates.agent,
                            listingsTemplate = response.templates.activeListings
                        )
                    }
                    ListingManagementEnum.PortalMode.SERVER -> {
                        getServerListingsAuto()
                    }
                }
            },
            onFail = {
                getPortalAPIResponse.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                getPortalAPIResponse.postValue(ApiStatus.errorInstance())
            },
            isShowErrorToast = false
        )
    }

    private fun getPortalAgentByMobile(
        agentTemplate: TemplateHolder?,
        listingsTemplate: List<TemplateHolder>
    ) {
        agentTemplate?.run {
            val mobileNumber = agentMobileNumber.value ?: return
            ApiUtil.performRequest(
                applicationContext,
                portalListingRepository.getPortalAgentInfoByMobile(
                    this.headers,
                    this.api,
                    mobileNumber
                ),
                onSuccess = { response ->
                    response.get("records")?.run {
                        if (this.asJsonArray.size() > 0) {
                            val agentId = this.asJsonArray.elementAt(0)?.asJsonObject?.get("id")
                                ?: return@performRequest
                            getActiveListingsFromPortal(
                                rawAgentData = response,
                                listingsTemplate = listingsTemplate,
                                agentId = agentId.toString()
                            )
                        }
                    }
                },
                onFail = {
                    println(it)
                },
                onError = {
                    println("error")
                },
                isShowErrorToast = false
            )
        }
    }

    private fun getActiveListingsFromPortal(
        agentId: String,
        rawAgentData: JsonObject,
        listingsTemplate: List<TemplateHolder>
    ) {
        val tempResults = mutableListOf<JsonObject>()
        listingsTemplate.forEach { template ->
            if (template.params?.containsKey("agent_id") == true) {
                template.params?.set("agent_id", agentId)
            }

            println(template)

            ApiUtil.performExternalRequest(
                applicationContext,
                portalListingRepository.getListingsFromPortal(
                    template.headers,
                    agentId,
                    template
                ),
                onSuccess = { response ->
                    tempResults.add(response)
                    if (tempResults.size == listingsTemplate.size) {
                        getClientListingsAuto(
                            rawAgentData = rawAgentData,
                            listingsTemplate = tempResults
                        )
                    }
                },
                onFail = {
                    println(it)
                },
                onError = {
                    println("error")
                },
                enablePrintRequestBody = false,
                errorClass = JsonObject::class.java,
                isShowErrorToast = false
            )
        }
    }

    private fun getClientListingsAuto(
        rawAgentData: JsonObject,
        listingsTemplate: List<JsonObject>
    ) {
        ApiUtil.performRequest(
            applicationContext,
            portalListingRepository.clientGetListingsAuto(
                portalType = ListingManagementEnum.AppPortalType.PROPERTY_GURU.value,
                rawAgentData = rawAgentData,
                rawListings = listingsTemplate
            ),
            onSuccess = {
                getListingAutoResponse.postValue(ApiStatus.successInstance(it))
            },
            onFail = {
                getListingAutoResponse.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                getListingAutoResponse.postValue(ApiStatus.errorInstance())
            },
            isShowErrorToast = false
        )
    }

    private fun getServerListingsAuto() {
        portalListingRepository.serverGetListingsAuto(portalType = ListingManagementEnum.AppPortalType.PROPERTY_GURU.value)
            .performRequest(
                applicationContext,
                onSuccess = {
                    getListingAutoResponse.postValue(ApiStatus.successInstance(it))
                },
                onFail = {
                    getListingAutoResponse.postValue(ApiStatus.failInstance(it))
                },
                onError = {
                    getListingAutoResponse.postValue(ApiStatus.errorInstance())
                },
                isShowErrorToast = false
            )
    }

    override fun shouldResponseBeOccupied(response: GetAgentDetailsResponse): Boolean {
        return true
    }
}