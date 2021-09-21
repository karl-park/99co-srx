package sg.searchhouse.agentconnect.data.datasource

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import sg.searchhouse.agentconnect.constant.Endpoint
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.agent.*
import sg.searchhouse.agentconnect.model.api.agentclient.GetAgentClientsInviteMessageResponse
import sg.searchhouse.agentconnect.model.api.agentclient.GetAgentClientsResponse
import sg.searchhouse.agentconnect.model.api.agentdirectory.FindAgentsResponse
import sg.searchhouse.agentconnect.model.api.auth.*
import sg.searchhouse.agentconnect.model.api.calculator.*
import sg.searchhouse.agentconnect.model.api.cea.*
import sg.searchhouse.agentconnect.model.api.chat.*
import sg.searchhouse.agentconnect.model.api.cobrokesms.GetIncludePublicListingIndicatorResponse
import sg.searchhouse.agentconnect.model.api.cobrokesms.GetSmsTemplatesResponse
import sg.searchhouse.agentconnect.model.api.cobrokesms.SendSmsLimitEmailResponse
import sg.searchhouse.agentconnect.model.api.cobrokesms.SendSmsResponse
import sg.searchhouse.agentconnect.model.api.community.*
import sg.searchhouse.agentconnect.model.api.flashreport.ListActiveMarketingFlashReportResponse
import sg.searchhouse.agentconnect.model.api.flashreport.ListAllMarketingFlashReportResponse
import sg.searchhouse.agentconnect.model.api.homereport.GenerateHomeReportResponse
import sg.searchhouse.agentconnect.model.api.homereport.GetHomeReportUsageResponse
import sg.searchhouse.agentconnect.model.api.listing.*
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.*
import sg.searchhouse.agentconnect.model.api.listing.map.FindMapRegionViewResponse
import sg.searchhouse.agentconnect.model.api.listing.map.FindMapViewResponse
import sg.searchhouse.agentconnect.model.api.listing.search.ExportListingsRequest
import sg.searchhouse.agentconnect.model.api.listing.search.ExportListingsResponse
import sg.searchhouse.agentconnect.model.api.listing.search.FindListingsCountResponse
import sg.searchhouse.agentconnect.model.api.listing.user.FindMyListingsResponse
import sg.searchhouse.agentconnect.model.api.listing.user.ListingsGroupSummaryResponse
import sg.searchhouse.agentconnect.model.api.location.*
import sg.searchhouse.agentconnect.model.api.lookup.*
import sg.searchhouse.agentconnect.model.api.project.*
import sg.searchhouse.agentconnect.model.api.propertyindex.LoadMarketWatchGraphResponse
import sg.searchhouse.agentconnect.model.api.propertyindex.LoadMarketWatchIndicesResponse
import sg.searchhouse.agentconnect.model.api.propertynews.FindNewsArticlesResponse
import sg.searchhouse.agentconnect.model.api.propertynews.GetNewsArticleCategoriesResponse
import sg.searchhouse.agentconnect.model.api.transaction.*
import sg.searchhouse.agentconnect.model.api.userprofile.GetProfileResponse
import sg.searchhouse.agentconnect.model.api.userprofile.GreetingResponse
import sg.searchhouse.agentconnect.model.api.watchlist.*
import sg.searchhouse.agentconnect.model.api.xvalue.*

interface SrxDataSource {
    /***************** User Profile *********************/
    @GET(Endpoint.GET_PROFILE)
    fun getProfile(): Call<GetProfileResponse>

    @Multipart
    @POST(Endpoint.UPDATE_PHOTO)
    fun updatePhoto(@Part file: MultipartBody.Part?): Call<DefaultResultResponse>

    @POST(Endpoint.REMOVE_PHOTO)
    fun removePhoto(): Call<DefaultResultResponse>

    /***************** User Authentication *********************/
    @POST(Endpoint.USER_AUTH_LOGIN)
    fun loginWithEmail(@Body loginRequest: LoginWithEmailRequest): Call<LoginResponse>

    @POST(Endpoint.USER_AUTH_LOGIN)
    fun loginWithCea(@Body loginRequest: LoginWithCeaRequest): Call<LoginResponse>

    @POST(Endpoint.USER_AUTH_VERIFY_OTP)
    fun verifyOTP(@Body loginResponseData: LoginResponseData): Call<VerifyOtpResponse>

    @POST(Endpoint.USER_AUTH_RESEND_OTP)
    fun resendOTP(@Body loginResponseData: LoginResponseData): Call<VerifyOtpResponse>

    @POST(Endpoint.USER_AUTH_RESET_PASSWORD)
    fun resetPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Call<ResetPasswordResponse>

    @POST(Endpoint.USER_AUTH_CREATE_ACCOUNT)
    fun createAccount(@Body createAccountRequest: CreateAccountRequest): Call<CreateAccountResponse>

    @POST(Endpoint.SCHEDULE_CALL_BACK)
    fun scheduleCallback(@Body scheduleCallbackRequest: ScheduleCallbackRequest): Call<ScheduleCallbackResponse>

    @POST(Endpoint.USER_AUTH_RESET_DEVICE)
    fun resetDevice(@Body resetDeviceRequest: ResetDeviceRequest): Call<ResetDeviceResponse>

    @POST(Endpoint.USER_AUTH_REGISTER_TOKEN)
    fun registerToken(@Body registerTokenRequest: RegisterTokenRequest): Call<DefaultResultResponse>

    @POST(Endpoint.USER_AUTH_UNREGISTER_TOKEN)
    fun unRegisterToken(): Call<DefaultResultResponse>

    @POST(Endpoint.USER_AUTH_LOGOUT)
    fun logout(): Call<DefaultResultResponse>

    @GET(Endpoint.USER_AUTH_CHECK_VERSION)
    fun checkVersion(): Call<CheckVersionResponse>

    @POST(Endpoint.USER_AUTH_LOGIN_AS_AGENT)
    fun loginAsAgent(@Body agent: AgentPO): Call<DefaultResultResponse>

    @GET(Endpoint.USER_AUTH_CONFIG_GET_CONFIG)
    fun getConfig(): Call<GetConfigResponse>

    /***************** Agent CV *********************/
    @POST(Endpoint.AGENT_GET_AGENT_DETAILS)
    fun getAgentDetails(): Call<GetAgentDetailsResponse>

    @GET(Endpoint.AGENT_GET_AGENT_CV)
    fun getAgentCV(@Query("agentId") agentId: Int): Call<GetAgentCvResponse>

    @POST(Endpoint.AGENT_SAVE_UPDATE_AGENT_CV)
    fun saveOrUpdateUserAgentCV(@Body agentCv: AgentCvPO): Call<SaveOrUpdateAgentCvResponse>

    @GET(Endpoint.AGENT_CHECK_PUBLIC_URL_AVAILABLE)
    fun checkIsPublicUrlAvailable(@Query("publicUrl") publicUrl: String): Call<DefaultResultResponse>

    @GET(Endpoint.AGENT_GET_AGENT_FULL_PROFILE)
    fun getAgentFullProfile(): Call<GetAgentFullProfileResponse>

    /********************************Subscription*********************************/
    @GET(Endpoint.SUBSCRIPTION_GET_PACKAGE_DETAILS)
    fun getSubscriptionPackageDetails(): Call<GetPackageDetailsResponse>

    @POST(Endpoint.SUBSCRIPTION_CREATE_TRIAL_ACCOUNT)
    fun createTrialAccount(): Call<DefaultResultResponse>

    /*****************************SRX Credits***********************************/
    @GET(Endpoint.CREDIT_GET_PACKAGE_DETAILS)
    fun getSrxCreditPackageDetails(@Query("creditMainType") creditMainType: Int): Call<GetPackageDetailsResponse>

    /******************************Payment******************************/
    @GET(Endpoint.PAYMENT_GENERATE_PAYMENT_LINK)
    fun generatePaymentLink(
        @Query("paymentType") paymentType: Int,
        @Query("packageId") packageId: String,
        @Query("isInstallment") isInstallment: Boolean,
        @Query("bankId") bankId: Int?,
        @Query("installmentMonth") installmentMonth: Int?
    ): Call<GeneratePaymentLinkResponse>

    /***************** Agent Transactions *********************/
    @GET(Endpoint.AGENT_TRANSACTION_GET_AGENT_TRANSACTIONS)
    fun getAgentTransactions(@Query("sortOrder") sortOrder: String): Call<GetAgentTransactions>

    @GET(Endpoint.AGENT_TRANSACTION_CONCEAL_TRANSACTION)
    fun concealTransaction(@Query("transactionId") transactionId: Int): Call<DefaultResultResponse>

    @GET(Endpoint.AGENT_TRANSACTION_REVEAL_TRANSACTION)
    fun revealTransaction(@Query("transactionId") transactionId: Int): Call<DefaultResultResponse>

    @GET(Endpoint.AGENT_TRANSACTION_FIND_OTHER_AGENT_TRANSACTION)
    fun findOtherAgentTransactions(
        @Query("agentUserId") agentUserId: Int,
        @Query("type") type: String,
        @Query("cdResearchSubtypes") cdResearchSubtypes: String
    ): Call<FindOtherAgentTransactionsResponse>

    /***************** Dashboard *********************/
    @GET(Endpoint.GET_APP_GREETING)
    fun getAppGreeting(): Call<GreetingResponse>

    /***************** Listing Management APIs *********************/
    @GET(Endpoint.LISTINGS_MANAGEMENT_GET_LISTINGS_SUMMARY)
    fun getListingsSummary(): Call<ListingsSummaryResponse>

    @GET(Endpoint.LISTINGS_MANAGEMENT_GET_LISTINGS_GROUP_SUMMARY)
    fun getListingsGroupSummary(): Call<ListingsGroupSummaryResponse>

    @GET(Endpoint.LISTINGS_MANAGEMENT_FIND_LISTINGS)
    fun findMyListings(
        @Query("startResultIndex") startResultIndex: Int,
        @Query("maxResults") maxResults: Int,
        @Query("groupIds") groupId: String,
        @Query("orderCriteria") orderCriteria: String,
        @Query("type") type: String? = null,
        @Query("cdResearchSubtypes", encoded = true) cdResearchSubtypes: String? = null,
        @Query("builtYearMin") builtYearMin: Int? = null,
        @Query("builtYearMax") builtYearMax: Int? = null,
        @Query("bedrooms") bedrooms: Int? = null,
        @Query("cdTenure", encoded = true) cdTenure: String? = null,
        @Query("address") address: String? = null
    ): Call<FindMyListingsResponse>

    @GET(Endpoint.LISTINGS_MANAGEMENT_FIND_LISTINGS_COUNT)
    fun findMyListingsCount(
        @Query("groupIds") groupId: String,
        @Query("orderCriteria") orderCriteria: String,
        @Query("type") type: String,
        @Query("cdResearchSubtypes", encoded = true) cdResearchSubtypes: String? = null,
        @Query("builtYearMin") builtYearMin: Int? = null,
        @Query("builtYearMax") builtYearMax: Int? = null,
        @Query("bedrooms") bedrooms: Int? = null,
        @Query("cdTenure", encoded = true) cdTenure: String? = null,
        @Query("address") address: String? = null
    ): Call<FindMyListingsResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_CREATE_LISTING)
    fun createListing(@Body listingEditPO: ListingEditPO): Call<CreateUpdateListingResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_UPDATE_LISTING)
    fun updateListing(@Body listingEditPO: ListingEditPO): Call<CreateUpdateListingResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_POST_LISTING)
    fun postListing(@Body listingEditPO: ListingEditPO): Call<PostListingResponse>

    @GET(Endpoint.LISTINGS_MANAGEMENT_GET_LISTING)
    fun getListingForListingManagement(@Query("listingId") listingId: String): Call<GetListingForListingManagementResponse>

    @GET(Endpoint.GET_LISTING_X_TREND)
    fun getListingXTrend(
        @Query("id") id: String,
        @Query("listingType") listingType: String
    ): Call<GetListingXTrendResponse>

    @GET(Endpoint.GET_LISTING_LATEST_TRANSACTIONS)
    fun getListingLatestTransactions(
        @Query("listingId") listingId: String,
        @Query("postalCode") postalCode: String,
        @Query("cdResearchSubtype") cdResearchSubType: Int,
        @Query("saleOrRent") saleOrRent: String
    ): Call<GetListingLatestTransactionsResponse>

    @Multipart
    @POST(Endpoint.LISTINGS_MANAGEMENT_UPLOAD_PHOTO)
    fun uploadPhoto(
        @Part("listingId") listingId: Int,
        @Part("position") position: Int,
        @Part("isFloorPlan") isFloorPlan: Boolean,
        @Part("isCover") isCover: Boolean,
        @Part file: MultipartBody.Part?,
        @Part("photoId") photoId: Int? = null
    ): Call<UploadPhotoResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_DELETE_PHOTOS)
    fun deletePhotos(@Body deletePhotosRequest: DeletePhotosRequest): Call<DefaultResultResponse>

    @GET(Endpoint.LISTINGS_MANAGEMENT_GET_POST_CREDITS)
    fun getPostingCredits(@Query("listingId") listingId: String): Call<GetPostingCreditsResponse>

    @GET(Endpoint.LISTINGS_MANAGEMENT_GET_POST_CREDITS)
    fun getPostingsCredits(@Query("listingIds") listingIds: String): Call<GetPostingCreditsResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_MOVE_PHOTO)
    fun movePhoto(@Body photo: PostListingPhotoPO): Call<DefaultResultResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_SET_COVER_PHOTO)
    fun setCoverPhoto(@Body photo: PostListingPhotoPO): Call<DefaultResultResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_REQUEST_OWNER_CERTIFICATION)
    fun requestOwnerCertification(@Body listingOwnerCertificationPO: ListingOwnerCertificationPO): Call<RequestOwnerCertificationResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_GET_ACTIVATE_SRX_CREDIT_SUMMARY)
    fun getActivateSrxCreditSummary(@Body srxCreditListingActivationPO: SrxCreditListingActivationPO): Call<ActivateSrxCreditListingsResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_ACTIVATE_SRX_CREDIT)
    fun activateSrxCreditForListings(@Body srxCreditListingActivationPO: SrxCreditListingActivationPO): Call<ActivateSrxCreditListingsResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_REPOST_LISTINGS)
    fun repostListings(@Body listingIds: List<String>): Call<DefaultResultResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_TAKE_OFF_LISTINGS)
    fun takeOffListings(@Body listingIds: List<String>): Call<DefaultResultResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_DELETE_LISTINGS)
    fun deleteListings(@Body listingsIds: List<String>): Call<DefaultResultResponse>

    @GET(Endpoint.LISTINGS_MANAGEMENT_GET_ADDON_PROMPTS)
    fun getAddonPrompts(@Query("limit") limit: Int?): Call<GetAddonPromptResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_VET_PHOTO)
    fun vetPhoto(@Body vetPhotoRequest: VetPhotoRequest): Call<VetPhotoResponsePO>

    @POST(Endpoint.LISTINGS_MANAGEMENT_APPEAL_PHOTO)
    fun appealPhoto(@Body appealPhotoRequest: AppealPhotoRequest): Call<DefaultResultResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_COPY_LISTING)
    fun copyListing(@Body listingEditPO: ListingEditPO): Call<CreateUpdateListingResponse>

    @GET(Endpoint.LISTINGS_MANAGEMENT_OWNER_CERTIFICATION_TEMPLATE)
    fun getOwnerCertificationNotificationTemplate(): Call<GetOwnerCertificationNotificationTemplateResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_SEND_OWNER_CERTIFICATIONS)
    fun sendOwnerCertificationNotifications(@Body mobiles: List<String>): Call<DefaultResultResponse>

    /**************************** Listing Management (Portal import - PG,...) APIs ****************************/
    @GET(Endpoint.LISTINGS_MANAGEMENT_GET_APIS)
    fun getPortalAPIs(): Call<GetPortalAPIsResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_PORTAL_SERVER_LOGIN)
    fun serverLoginPortal(@Body loginRequest: PortalLoginRequest): Call<PortalLoginResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_PORTAL_SERVER_LOGOUT)
    fun serverLogoutPortal(@Body portalType: PortalLoggedOutRequest): Call<DefaultResultResponse>

    @GET(Endpoint.LISTINGS_MANAGEMENT_PORTAL_GET_LISTINGS)
    fun getPortalListings(@Query("portalType") portalType: Int): Call<PortalLoginResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_PORTAL_IMPORT_LISTING)
    fun importPortalListing(@Body portalListingPO: PortalListingPO): Call<DefaultResultResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_PORTAL_CLIENT_LOGIN)
    fun clientLoginPortal(@Body loginRequest: PortalLoginRequest): Call<ClientLoginResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_PORTAL_CLIENT_GET_LISTINGS)
    fun clientGetListings(@Body clientGetListingsRequest: ClientGetListingsRequest): Call<PortalLoginResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_PORTAL_CLIENT_IMPORT_LISTING)
    fun clientImportListing(@Body clientImportListingRequest: ClientImportListingRequest): Call<DefaultResultResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_PORTAL_UPDATE_PORTAL_ACCOUNT)
    fun updatePortalAccount(@Body portalAccountPO: PortalAccountPO): Call<UpdatePortalAccountResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_PORTAL_TOGGLE_LISTINGS_SYNC)
    fun toggleListingsSync(@Body portalListings: List<PortalListingPO>): Call<DefaultResultResponse>

    @POST(Endpoint.LISTINGS_MANAGEMENT_PORTAL_CLIENT_GET_LISTINGS_AUTO)
    fun clientGetListingsAuto(@Body clientGetListingsRequest: PortalClientGetListingsAutoRequest): Call<PortalGetListingsAutoResponse>

    @GET(Endpoint.LISTINGS_MANAGEMENT_PORTAL_SERVER_GET_LISTINGS_AUTO)
    fun getListingsAuto(@Query("portalType") portalType: Int): Call<PortalGetListingsAutoResponse>

    /**************************** CEA Exclusive form ****************************/
    @GET(Endpoint.CEA_EXCLUSIVE_GET_FORM_TEMPLATE)
    fun getFormTemplate(
        @Query("formType") formType: Int,
        @Query("formId") formId: Int?
    ): Call<GetFormTemplateResponse>

    @Multipart
    @POST(Endpoint.CEA_EXCLUSIVE_SUBMIT_FORM)
    fun submitForm(
        @Part("formId") formId: Int,
        @Part files: List<MultipartBody.Part>
    ): Call<CeaSubmitFormResponse>

    @POST(Endpoint.CEA_EXCLUSIVE_CREATE_UPDATE_FORM)
    fun createUpdateForm(@Body ceaFormSubmissionPO: CeaFormSubmissionPO): Call<GetFormTemplateResponse>

    @POST(Endpoint.CEA_EXCLUSIVE_DELETE_CLIENT)
    fun deleteClient(@Body ceaDeleteClientRequest: CeaDeleteClientRequest): Call<DefaultResultResponse>

    @POST(Endpoint.CEA_EXCLUSIVE_REMOVE_DRAFT_CEA_FORMS)
    fun deleteUnsubmittedCeaForms(@Body ceaFormIds: List<String>): Call<DefaultResultResponse>

    @GET(Endpoint.CEA_EXCLUSIVE_FIND_UNSUBMITTED_CEA_FORMS)
    fun findUnsubmittedCeaForms(@Query("type") type: String): Call<FindUnsubmittedCeaFormResponse>

    /***************** Chat APIs *********************/
    @GET(Endpoint.CHAT_LOAD_SSM_CONVERSATIONS)
    fun findSsmConversations(
        @Query("types", encoded = true) types: String,
        @Query("startIndex") startIndex: Int,
        @Query("maxResults") maxResults: Int,
        @Query("oneToOneOtherUserType") oneToOneOtherUserType: String?,
        @Query("dateLastLoad") dateLastLoad: String?,
        @Query("isUnreadOnly") isUnreadOnly: Boolean?
    ): Call<LoadSsmConversationsResponse>

    @POST(Endpoint.CHAT_LEAVE_SSM_CONVERSATIONS)
    fun leaveSsmConversations(@Body ssmConversationPOS: ArrayList<SsmConversationPO>): Call<DefaultResultResponse>

    @POST(Endpoint.CHAT_CREATE_SSM_CONVERSATION)
    fun createSsmConversation(@Body createSsmConversation: CreateSsmConversationRequest): Call<CreateSsmConversationResponse>

    @GET(Endpoint.CHAT_FIND_SSM_MESSAGES)
    fun findSsmMessages(
        @Query("conversationId") conversationId: Int,
        @Query("startIndex") startIndex: Int,
        @Query("maxResults") maxResults: Int,
        @Query("messageId") messageId: Int?,
        @Query("isBefore") isBefore: Boolean?,
        @Query("isAscending") isAscending: Boolean?
    ): Call<FindSsmMessagesResponse>

    @Multipart
    @POST(Endpoint.CHAT_SEND_SSM_MESSAGE)
    fun sendSsmMessage(
        @Part("conversationId") conversationId: RequestBody,
        @Part("messageType") messageType: RequestBody,
        @Part("message") message: RequestBody?,
        @Part("listingId") listingId: RequestBody?,
        @Part file: MultipartBody.Part?
    ): Call<SendSsmMessageResponse>

    @POST(Endpoint.CHAT_RESET_UNREAD_COUNT)
    fun resetUnreadCount(@Body ssmConversationPO: SsmConversationPO): Call<DefaultResultResponse>

    @GET(Endpoint.CHAT_GET_SSM_CONVERSATION_INFO)
    fun getSsmConversationInfo(@Query("conversationId") conversationId: Int): Call<GetSsmConversationInfoResponse>

    @POST(Endpoint.CHAT_REMOVE_MEMBERS_FROM_CONVERSATION)
    fun removeMembersFromConversation(@Body removeMembersRequest: RemoveMembersFromConversationRequest): Call<DefaultResultResponse>

    @POST(Endpoint.CHAT_UPDATE_SSM_CONVERSATION_SETTING)
    fun updateSsmConversationSettings(@Body ssmConversationSetting: SsmConversationSettingPO): Call<DefaultResultResponse>

    @POST(Endpoint.CHAT_ADD_MEMBER_TO_CONVERSATION)
    fun addMembersToConversation(@Body addMemberToConversationRequest: AddMemberToConversatioinRequest): Call<DefaultResultResponse>

    @POST(Endpoint.CHAT_BLACKLIST_SSM_CONVERSATIONS)
    fun blacklistLeaveSsmConversations(@Body ssmConversationPOs: List<SsmConversationPO>): Call<DefaultResultResponse>

    @GET(Endpoint.CHAT_GET_SSM_CONVERSATION)
    fun getSsmConversation(@Query("conversationId") conversationId: Int): Call<GetSsmConversationResponse>

    @POST(Endpoint.CHAT_CREATE_SSM_LISTINGS_BLAST)
    fun createSsmListingsBlastConversation(@Body ssmListingBlastConversation: CreateSsmListingsBlastConversationRequest): Call<DefaultResultResponse>

    /***************** SRX AgentPO Directory APIs *********************/
    @GET(Endpoint.FIND_AGENT)
    fun findAgents(
        @Query("page") page: Int,
        @Query("maxResults") maxResults: Int,
        @Query("searchText") searchText: String?,
        @Query("selectedDistrictIds", encoded = true) selectedDistrictIds: String?,
        @Query("selectedHdbTownIds", encoded = true) selectedHdbTownIds: String?,
        @Query("selectedAreaSpecializations", encoded = true) selectedAreaSpecializations: String?
    ): Call<FindAgentsResponse>

    @POST(Endpoint.BLACK_LIST_AGENTS)
    fun blacklistAgents(@Body agentMobileNumbers: ArrayList<String>): Call<DefaultResultResponse>

    /***************** SRX AgentPO Enquiries APIs *********************/
    //Remove enquiries
    @POST(Endpoint.DELETE_ENQUIRIES)
    fun deleteEnquiries(@Body agentEnquiryPOS: ArrayList<SrxAgentEnquiryPO>): Call<DefaultResultResponse>

    /***************** Listing *********************/
    // Listing search
    @GET(Endpoint.GET_AMENITIES_LIST)
    fun getAmenitiesList(): Call<GetAmenitiesResponse>

    @GET(Endpoint.GET_LISTING)
    fun getListing(
        @Query("listingId") listingId: String,
        @Query("listingType") listingType: String
    ): Call<GetListingResponse>

    @GET(Endpoint.FIND_LISTINGS)
    fun findListings(
        @Query("startResultIndex") startResultIndex: Int,
        @Query("maxResults") maxResults: Int,
        @Query("wantedListingGroups", encoded = true) wantedListingGroups: String,
        @Query("type", encoded = true) type: String,
        @Query("cdResearchSubTypes", encoded = true) cdResearchSubTypes: String,
        @Query("orderCriteria") orderCriteria: String? = null,
        @Query("searchText") searchText: String? = null,
        @Query("selectedAmenitiesIds", encoded = true) selectedAmenitiesIds: String? = null,
        @Query("selectedDistrictIds", encoded = true) selectedDistrictIds: String? = null,
        @Query("selectedHdbTownIds", encoded = true) selectedHdbTownIds: String? = null,
        @Query("cdTenure", encoded = true) cdTenure: String? = null,
        @Query("isTransacted") isTransacted: Boolean? = null,
        @Query("builtYearMin") builtYearMin: Int? = null,
        @Query("builtYearMax") builtYearMax: Int? = null,
        @Query("furnish", encoded = true) furnish: String? = null,
        @Query("floor", encoded = true) floor: String? = null,
        @Query("leaseTermOptions", encoded = true) leaseTermOptions: String? = null,
        @Query("bathrooms", encoded = true) bathrooms: String? = null,
        @Query("maxBathrooms") maxBathrooms: Int? = null,
        @Query("minBathrooms") minBathrooms: Int? = null,
        @Query("bedrooms", encoded = true) bedrooms: String? = null,
        @Query("minBedrooms") minBedrooms: Int? = null,
        @Query("maxBedrooms") maxBedrooms: Int? = null,
        @Query("maxBuiltSize") maxBuiltSize: Double? = null,
        @Query("minBuiltSize") minBuiltSize: Double? = null,
        @Query("maxLandSizeSqft") maxLandSizeSqft: Int? = null,
        @Query("minLandSizeSqft") minLandSizeSqft: Int? = null,
        @Query("maxPSF") maxPSF: Double? = null,
        @Query("minPSF") minPSF: Double? = null,
        @Query("maxRentPrice") maxRentPrice: Int? = null,
        @Query("minRentPrice") minRentPrice: Int? = null,
        @Query("maxSalePrice") maxSalePrice: Int? = null,
        @Query("minSalePrice") minSalePrice: Int? = null,
        @Query("minDateFirstPosted") minDateFirstPosted: String? = null,
        @Query("models", encoded = true) models: String? = null,
        @Query("modelsNotRequired", encoded = true) modelsNotRequired: String? = null,
        @Query("isRoomRental") isRoomRental: Boolean? = null,
        @Query("vt360Filter") vt360Filter: Boolean? = null,
        @Query("droneViewFilter") droneViewFilter: Boolean? = null,
        @Query("ownerCertificationFilter") ownerCertificationFilter: Boolean? = null,
        @Query("exclusiveFilter") exclusiveFilter: Boolean? = null,
        @Query("spvFilter") spvFilter: Boolean? = null,
        @Query("projectLaunchStatus") projectLaunchStatus: Int? = null,
        @Query("seed") seed: String? = null,
        @Query("filterUserId") filterUserId: Int? = null
    ): Call<FindListingsResponse>

    @GET(Endpoint.FIND_LISTINGS_COUNT)
    fun findListingsCount(
        @Query("wantedListingGroups", encoded = true) wantedListingGroups: String,
        @Query("type", encoded = true) type: String,
        @Query("cdResearchSubTypes", encoded = true) cdResearchSubTypes: String,
        @Query("searchText") searchText: String? = null,
        @Query("selectedAmenitiesIds", encoded = true) selectedAmenitiesIds: String? = null,
        @Query("selectedDistrictIds", encoded = true) selectedDistrictIds: String? = null,
        @Query("selectedHdbTownIds", encoded = true) selectedHdbTownIds: String? = null,
        @Query("cdTenure", encoded = true) cdTenure: String? = null,
        @Query("isTransacted") isTransacted: Boolean? = null,
        @Query("builtYearMin") builtYearMin: Int? = null,
        @Query("builtYearMax") builtYearMax: Int? = null,
        @Query("furnish", encoded = true) furnish: String? = null,
        @Query("floor", encoded = true) floor: String? = null,
        @Query("leaseTermOptions", encoded = true) leaseTermOptions: String? = null,
        @Query("bathrooms", encoded = true) bathrooms: String? = null,
        @Query("maxBathrooms") maxBathrooms: Int? = null,
        @Query("minBathrooms") minBathrooms: Int? = null,
        @Query("bedrooms", encoded = true) bedrooms: String? = null,
        @Query("minBedrooms") minBedrooms: Int? = null,
        @Query("maxBedrooms") maxBedrooms: Int? = null,
        @Query("maxBuiltSize") maxBuiltSize: Double? = null,
        @Query("minBuiltSize") minBuiltSize: Double? = null,
        @Query("maxPSF") maxPSF: Double? = null,
        @Query("minPSF") minPSF: Double? = null,
        @Query("maxRentPrice") maxRentPrice: Int? = null,
        @Query("minRentPrice") minRentPrice: Int? = null,
        @Query("maxSalePrice") maxSalePrice: Int? = null,
        @Query("minSalePrice") minSalePrice: Int? = null,
        @Query("minDateFirstPosted") minDateFirstPosted: String? = null,
        @Query("models", encoded = true) models: String? = null,
        @Query("modelsNotRequired", encoded = true) modelsNotRequired: String? = null,
        @Query("isRoomRental") isRoomRental: Boolean? = null,
        @Query("vt360Filter") vt360Filter: Boolean? = null,
        @Query("droneViewFilter") droneViewFilter: Boolean? = null,
        @Query("ownerCertificationFilter") ownerCertificationFilter: Boolean? = null,
        @Query("exclusiveFilter") exclusiveFilter: Boolean? = null,
        @Query("spvFilter") spvFilter: Boolean? = null,
        @Query("projectLaunchStatus") projectLaunchStatus: Int? = null
    ): Call<FindListingsCountResponse>

    @POST(Endpoint.EXPORT_LISTINGS)
    fun exportListings(@Body exportListingsRequest: ExportListingsRequest): Call<ExportListingsResponse>

    @GET(Endpoint.FIND_LISTINGS)
    fun findListingsByIds(
        @Query("startResultIndex") startResultIndex: Int,
        @Query("maxResults") maxResults: Int,
        @Query("wantedListingGroups", encoded = true) wantedListingGroups: String,
        @Query("listingIds", encoded = true) listingIds: String
    ): Call<FindListingsResponse>

    // Listing map
    @GET(Endpoint.FIND_MAP_REGION_VIEW)
    fun findMapRegionView(
        @Query("filterType") filterType: Int,
        @Query("filterId") filterId: Int?
    ): Call<FindMapRegionViewResponse>

    @GET(Endpoint.FIND_MAP_VIEW)
    fun findMapView(
        @Query("displayMinLat") displayMinLat: Double,
        @Query("displayMaxLat") displayMaxLat: Double,
        @Query("displayMinLng") displayMinLng: Double,
        @Query("displayMaxLng") displayMaxLng: Double,
        @Query("zoomLevel") zoomLevel: Int,
        @Query("prevCadastralId") prevCadastralId: Int,
        @Query("ignoreDisplayBounds") ignoreDisplayBounds: Boolean,
        @Query("mapZoomLevel") mapZoomLevel: Int,
        @Query("mapCenterLat") mapCenterLat: Double,
        @Query("mapCenterLng") mapCenterLng: Double,
        @Query("circleMarkerCenterLat") circleMarkerCenterLat: Double,
        @Query("circleMarkerCenterLng") circleMarkerCenterLng: Double,
        @Query("movedCircleMarker") movedCircleMarker: Boolean,
        @Query("searchMode") searchMode: String,
        @Query("searchTypeAuto") searchTypeAuto: String,
        @Query("searchText") searchText: String,
        @Query("amenityIds") amenityIds: String,
        @Query("amenityType") amenityType: String
    ): Call<FindMapViewResponse>

    /************************************** Search Option Look up APIs *********************************************/
    @GET(Endpoint.LOOKUP_HDB_TOWNS)
    fun lookupHdbTowns(): Call<LookupHdbTownsResponse>

    @GET(Endpoint.LOOKUP_SINGAPORE_DISTRICTS)
    fun lookupSingaporeDistricts(): Call<LookupSingaporeDistrictsResponse>

    @GET(Endpoint.LOOKUP_MRTS)
    fun lookupMrts(): Call<LookupMrtsResponse>

    @GET(Endpoint.LOOKUP_SCHOOLS)
    fun lookupSchools(): Call<LookupSchoolsResponse>

    @GET(Endpoint.LOOKUP_PROPERTY_MODELS)
    fun lookupModels(): Call<LookupModelsResponse>

    @GET(Endpoint.LOOKUP_BEDROOMS)
    fun lookupBedrooms(): Call<LookupBedroomsResponse>

    @GET(Endpoint.LOOKUP_LISTING_FEATURES_FIXTURES_AREAS)
    fun lookupListingFeaturesFixturesAreas(@Query("cdResearchType") cdResearchType: Int): Call<LookupListingFeaturesFixturesAreasResponse>

    /************************************** Location Search APIs *********************************************/
    @GET(Endpoint.LOCATION_SEARCH_FIND_LOCATION_SUGGESTIONS)
    fun findLocationSuggestions(
        @Query("searchText") searchText: String,
        @Query("source") source: String,
        @Query("isNewProject") isNewProject: Boolean
    ): Call<FindLocationSuggestionsResponse>

    @GET(Endpoint.LOCATION_SEARCH_FIND_CURRENT_LOCATION)
    fun findCurrentLocation(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Call<FindCurrentLocationResponse>

    @GET(Endpoint.LOCATION_SEARCH_FIND_PROPERTIES)
    fun findProperties(
        @Query("searchText") searchText: String,
        @Query("maxResults") maxResults: Int
    ): Call<FindPropertiesResponse>

    @GET(Endpoint.LOCATION_SEARCH_GET_PROPERTY_TYPE)
    fun getPropertyType(
        @Query("postalCode") postalCode: Int,
        @Query("blk") blk: String
    ): Call<GetPropertyTypeResponse>

    @GET(Endpoint.LOCATION_SEARCH_GET_ADDRESS_PROPERTY_TYPE)
    fun getAddressPropertyType(
        @Query("postalCode") postalCode: Int,
        @Query("skipCommercial") skipCommercial: Boolean,
        @Query("blk") blk: String?
    ): Call<GetAddressPropertyTypeResponse>

    @GET(Endpoint.LOCATION_SEARCH_GET_PROPERTY_DATA)
    fun getPropertyData(
        @Query("postalCode") postalCode: Int,
        @Query("blk") blk: String,
        @Query("cdResearchSubtype") cdResearchSubtype: Int,
        @Query("floorNo") floorNo: String?,
        @Query("unitNo") unitNo: String?
    ): Call<GetPropertyDataResponse>

    /************************************** Transaction APIs *********************************/
    @GET(Endpoint.TRANSACTION_HOME_REPORT_FIND_PROJECTS_BY_KEYWORD)
    fun findProjectsByKeyword(
        @Query("searchMode") searchMode: Int,
        @Query("searchText") searchText: String? = null,
        @Query("districts", encoded = true) districts: String? = null,
        @Query("hdbTowns", encoded = true) hdbTowns: String? = null,
        @Query("cdResearchSubtypes", encoded = true) cdResearchSubtypes: String? = null,
        @Query("radius") radius: Int? = null,
        @Query("tenureType") tenureType: Int? = null,
        @Query("typeOfArea") typeOfArea: Int? = null,
        @Query("age") age: Int? = null,
        @Query("ageMax") ageMax: Int? = null,
        @Query("property") property: String? = null,
        @Query("order") order: String? = null
    ): Call<FindProjectsByKeywordResponse>

    @POST(Endpoint.TRANSACTION_HOME_REPORT_GET_LATEST_RENTAL_TRANSACTIONS)
    fun getLatestRentalTransactions(@Body getLatestTransactionsRequest: LatestTransactionsRequest): Call<LatestRentalTransactionsResponse>

    @POST(Endpoint.TRANSACTION_HOME_REPORT_GET_LATEST_SALE_TRANSACTIONS)
    fun getLatestSaleTransactions(@Body getLatestTransactionsRequest: LatestTransactionsRequest): Call<LatestSaleTransactionsResponse>

    @GET(Endpoint.TRANSACTION_HOME_REPORT_LOAD_TOWER_VIEW_FOR_LAST_SOLD_TRANSACTION)
    fun loadTowerViewForLastSoldTransaction(@Query("id") id: Int): Call<TowerViewForLastSoldTransactionResponse>

    @GET(Endpoint.FIND_SALE_PROJECTS_BY_KEYWORD)
    fun findSaleProjectsByKeyword(
        @Query("searchText") searchText: String? = null,
        @Query("districts", encoded = true) districts: String? = null,
        @Query("hdbTowns", encoded = true) hdbTowns: String? = null,
        @Query("postalCode") postalCode: Int? = null,
        @Query("searchMode") searchMode: Int? = null,
        @Query("typeOfArea") typeOfArea: Int? = null,
        @Query("cdResearchSubtypes", encoded = true) cdResearchSubtypes: String? = null,
        @Query("radius") radius: Int? = null,
        @Query("tenureType") tenureType: Int? = null,
        @Query("age") age: Int? = null,
        @Query("ageMax") ageMax: Int? = null,
        @Query("built") built: Int? = null,
        @Query("builtMax") builtMax: Int? = null,
        @Query("amenities", encoded = true) amenities: String? = null
    ): Call<FindSaleRentProjectsByKeywordResponse>

    @GET(Endpoint.FIND_RENTAL_PROJECTS_BY_KEYWORD)
    fun findRentProjectsByKeyword(
        @Query("searchText") searchText: String? = null,
        @Query("districts", encoded = true) districts: String? = null,
        @Query("hdbTowns", encoded = true) hdbTowns: String? = null,
        @Query("postalCode") postalCode: Int? = null,
        @Query("searchMode") searchMode: Int? = null,
        @Query("typeOfArea") typeOfArea: Int? = null,
        @Query("cdResearchSubtypes", encoded = true) cdResearchSubtypes: String? = null,
        @Query("radius") radius: Int? = null,
        @Query("tenureType") tenureType: Int? = null,
        @Query("age") age: Int? = null,
        @Query("ageMax") ageMax: Int? = null,
        @Query("built") built: Int? = null,
        @Query("builtMax") builtMax: Int? = null,
        @Query("amenities", encoded = true) amenities: String? = null
    ): Call<FindSaleRentProjectsByKeywordResponse>

    @FormUrlEncoded
    @POST(Endpoint.TABLE_LIST)
    fun tableList(@Field("params") params: String): Call<TableListResponse>

    @FormUrlEncoded
    @POST(Endpoint.PROJECT_LIST)
    fun projectList(@Field("params") params: String): Call<ProjectListResponse>

    @FormUrlEncoded
    @POST(Endpoint.PAGINATED_PROJECT_LIST)
    fun paginatedProjectList(@Field("params") params: String): Call<PaginatedProjectListResponse>

    @FormUrlEncoded
    @POST(Endpoint.TRANSACTION_SUMMARY)
    fun transactionSummary(@Field("params") params: String): Call<TransactionSummaryResponse>

    /**************************** Property News API ****************************/
    @GET(Endpoint.GET_NEWS_ARTICLE_CATEGORIES)
    fun getNewsArticleCategories(): Call<GetNewsArticleCategoriesResponse>

    @GET(Endpoint.FIND_NEWS_ARTICLES)
    fun findNewsArticles(
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("channel") channel: String,
        @Query("title") title: String? = null,
        @Query("categories") categories: String? = null
    ): Call<FindNewsArticlesResponse>

    /************************************** X VALUE APIs *********************************/
    @GET(Endpoint.GET_EXISTING_X_VALUES)
    fun getExistingXValues(
        @Query("search") search: String?,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("property") property: String,
        @Query("order") order: String
    ): Call<GetExistingXValuesResponse>

    @GET(Endpoint.SEARCH_WITH_WALKUP)
    fun searchWithWalkup(@Query("query") query: String): Call<SearchWithWalkupResponse>

    @GET(Endpoint.GET_PROJECT)
    fun getProject(
        @Query("postal") postal: Int,
        @Query("type") type: Int,
        @Query("floor") floor: String,
        @Query("unit") unit: String,
        @Query("buildingNum") buildingNum: String
    ): Call<GetProjectResponse>

    @GET(Endpoint.CALCULATE)
    fun calculate(
        @Query("streetId") streetId: Int,
        @Query("saleOrRent") saleOrRent: String,
        @Query("floor") floor: String,
        @Query("postal") postal: Int,
        @Query("buildingNum") buildingNum: String,
        @Query("size") size: Int,
        @Query("unit") unit: String,
        @Query("type") type: Int,
        @Query("includePes") includePes: Boolean,
        @Query("pesSize") pesSize: Int?,
        @Query("landType") landType: String?,
        @Query("lastConstructed") lastConstructed: Int?,
        @Query("tenure") tenure: Int?,
        @Query("builtArea") builtArea: Int?
    ): Call<CalculateResponse>

    @GET(Endpoint.UPDATE_REQUEST)
    fun updateRequest(
        @Query("id") sveValuationId: Int,
        @Query("renovationCost") renovationCost: Int?,
        @Query("renovationYear") renovationYear: Int?,
        @Query("goodWill") goodWill: Int
    ): Call<UpdateRequestResponse>

    @GET(Endpoint.PROMOTION_GET_XVALUE_TREND)
    fun promotionGetXValueTrend(
        @Query("postalCode") postalCode: Int,
        @Query("unitNum") unitNum: String,
        @Query("type") type: String,
        @Query("floorNum") floorNum: String,
        @Query("buildingNum") buildingNum: String,
        @Query("builtYear") builtYear: Int?,
        @Query("subType") subType: Int,
        @Query("id") crunchResearchStreetId: Int,
        @Query("landedXValue") landedXValue: Boolean,
        @Query("size") size: Int
    ): Call<PromotionGetXvalueTrendResponse>

    @GET(Endpoint.LOAD_HOME_REPORT_SALE_TRANSACTION_REVISED)
    fun loadHomeReportSaleTransactionRevised(
        @Query("id") crunchResearchStreetId: Int
    ): Call<LoadHomeReportSaleTransactionRevisedResponse>

    @GET(Endpoint.LOAD_HOME_REPORT_RENTAL_INFORMATION_REVISED)
    fun loadHomeReportRentalInformationRevised(
        @Query("id") crunchResearchStreetId: Int
    ): Call<LoadHomeReportRentalInformationRevisedResponse>

    @GET(Endpoint.GET_VALUATION_DETAIL)
    fun getValuationDetail(@Query("id") sveRequestId: Int): Call<GetValuationDetailResponse>

    @GET(Endpoint.GENERATE_XVALUE_PROPERTY_REPORT)
    fun generateXvaluePropertyReport(
        @Query("requestId") requestId: Int,
        @Query("id") id: Int,
        @Query("showFullReport") showFullReport: Boolean
    ): Call<GenerateXvaluePropertyReportResponse>

    /**************************** Home Report API ****************************/
    @GET(Endpoint.GENERATE_HOME_REPORT)
    fun generateHomeReport(
        @Query("id") id: Int,
        @Query("agency") agency: String,
        @Query("block") block: String,
        @Query("cdResearchSubtype") cdResearchSubtype: Int,
        @Query("unit") unit: String,
        @Query("streetKey") streetKey: String,
        @Query("preparedFor") preparedFor: String?
    ): Call<GenerateHomeReportResponse>

    @GET(Endpoint.GET_HOME_REPORT_USAGE)
    fun getHomeReportUsage(): Call<GetHomeReportUsageResponse>

    /**************************** Project API ****************************/
    // https://streetsine.atlassian.net/wiki/spaces/SIN/pages/797835265/Project+V1+API

    @GET(Endpoint.GET_PROJECT_INFORMATION)
    // id: Crunch research street ID
    fun getProjectInformation(@Query("id") id: Int): Call<GetProjectInformationResponse>

    @GET(Endpoint.GET_ALL_PLANNING_DECISION_TYPES)
    fun getAllPlanningDecisionTypes(): Call<GetAllPlanningDecisionTypesResponse>

    @GET(Endpoint.GET_PROJECT_PLANNING_DECISION)
    // id: Crunch research street ID
    fun getProjectPlanningDecision(
        @Query("id") id: Int,
        @Query("filterType") filterType: String?,
        @Query("filterMonth") filterMonth: Int?
    ): Call<GetProjectPlanningDecisionResponse>

    @GET(Endpoint.PROJECT_DETAILS_GET_NEW_LAUNCHES)
    fun getNewLaunches(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("countOnly") countOnly: Boolean,
        @Query("property") property: String? = null,
        @Query("order") order: String? = null,
        @Query("cdResearchSubtypes", encoded = true) cdResearchSubtypes: String? = null,
        @Query("tenure") tenure: String? = null,
        @Query("completion") completion: String? = null,
        @Query("searchText") searchText: String? = null,
        @Query("districts", encoded = true) districts: String? = null,
        @Query("hdbTownships", encoded = true) hdbTownships: String? = null
    ): Call<GetNewLaunchesResponse>

    @GET(Endpoint.PROJECT_DETAILS_GET_NEW_LAUNCHES_TEMPLATES)
    fun getNewLaunchesNotificationTemplate(): Call<GetNewLaunchesNotificationTemplates>

    @GET(Endpoint.PROJECT_DETAILS_SEND_REPORTS)
    fun sendReportToClient(
        @Query("crunchResearchStreetIds") crunchResearchStreetIds: String,
        @Query("mobileNumbers") mobileNumbers: String
    ): Call<SendReportToClientResponse>

    /**************************** Property Index API ****************************/
    @GET(Endpoint.LOAD_MARKET_WATCH_INDICES)
    fun loadMarketWatchIndices(): Call<LoadMarketWatchIndicesResponse>

    @GET(Endpoint.LOAD_MARKET_WATCH_GRAPH)
    fun loadMarketWatchGraph(
        @Query("propertyType") propertyType: String,
        @Query("transactionType") transactionType: String
    ): Call<LoadMarketWatchGraphResponse>

    /**************************** Flash Report ****************************/
    @GET(Endpoint.FLASH_REPORT_LIST_ALL_FLASH_REPORTS)
    fun listAllMarketingFlashReport(
        @Query("page") page: Int? = null,
        @Query("pageSize") pageSize: Int? = null,
        @Query("property") property: String? = null,
        @Query("order") order: String? = null
    ): Call<ListAllMarketingFlashReportResponse>

    @GET(Endpoint.FLASH_REPORT_LIST_ACTIVE_FLASH_REPORTS)
    fun listActiveMarketingFlashReport(): Call<ListActiveMarketingFlashReportResponse>

    /**************************** Cobroke SMS ****************************/
    @GET(Endpoint.COBROKE_SMS_GET_INCLUDE_PUBLIC_LISTING_INDICATOR)
    fun getIncludePublicListingIndicator(): Call<GetIncludePublicListingIndicatorResponse>

    @Multipart
    @POST(Endpoint.COBROKE_SMS_GET_SMS_TEMPLATES)
    fun getSmsTemplates(@Part("listingPos") listingPos: RequestBody): Call<GetSmsTemplatesResponse>

    @Multipart
    @POST(Endpoint.COBROKE_SMS_SEND_SMS)
    fun sendSms(
        @Part listingPos: MultipartBody.Part,
        @Part("smsTemplateId") smsTemplateId: Int,
        @Part("smsDateTimeValueEpochTimeInMs") smsDateTimeValueEpochTimeInMs: Long?
    ): Call<SendSmsResponse>

    @GET(Endpoint.COBROKE_SMS_SEND_SMS_LIMIT_EMAIL)
    fun sendSmsLimitEmail(): Call<SendSmsLimitEmailResponse>

    /**************************** Agent client ****************************/
    @GET(Endpoint.AGENT_CLIENTS_GET_CLIENTS)
    fun getAgentClients(
        @Query("sortAsc") sortAsc: Boolean,
        @Query("searchText") searchText: String
    ): Call<GetAgentClientsResponse>

    @GET(Endpoint.AGENT_CLIENTS_GET_INVITE_MESSAGE)
    fun getAgentClientsInviteMessage(): Call<GetAgentClientsInviteMessageResponse>

    @Multipart
    @POST(Endpoint.AGENT_CLIENTS_SEND_MESSAGE)
    fun sendMessage(
        @Part("mode") mode: RequestBody,
        @Part("message") message: RequestBody,
        @Part("recipientPtUserIds") recipientPtUserIds: RequestBody,
        @Part("links") links: RequestBody? = null,
        @Part files: List<MultipartBody.Part>? = null
    ): Call<DefaultResultResponse>

    /**************************** Calculator ****************************/
    @POST(Endpoint.CALCULATOR_AFFORD_QUICK_MAX_PURCHASE_PRICE)
    fun calculateAffordQuickMaxPurchasePrice(@Body maxPurchasePricePO: MaxPurchasePricePO): Call<CalculateMaxPurchasePriceResponse>

    @POST(Endpoint.CALCULATOR_AFFORD_QUICK_INCOME_REQUIRED)
    fun calculateAffordQuickIncomeRequired(@Body incomeRequiredPO: IncomeRequiredPO): Call<CalculateIncomeRequiredResponse>

    @POST(Endpoint.CALCULATOR_AFFORD_ADVANCED)
    fun calculateAffordAdvanced(@Body affordAdvancedInputPO: AffordAdvancedInputPO): Call<CalculateAffordAdvancedResponse>

    @POST(Endpoint.CALCULATOR_SELL)
    fun calculateSell(@Body sellingInputPO: SellingInputPO): Call<CalculateSellingResponse>

    @POST(Endpoint.CALCULATOR_STAMP_DUTY_BUY)
    fun calculateStampDutyBuy(@Body stampDutyInputPO: StampDutyInputPO): Call<CalculateStampDutyBuyResponse>

    @POST(Endpoint.CALCULATOR_STAMP_DUTY_RENT)
    fun calculateStampDutyRent(@Body rentDutyInputPO: RentDutyInputPO): Call<CalculateStampDutyRentResponse>

    /**************************** Watchlist ****************************/
    @GET(Endpoint.WATCHLIST_GET_PROPERTY_CRITERIA)
    fun getPropertyCriteria(@Query("id") id: Int): Call<GetPropertyCriteriaResponse>

    @POST(Endpoint.WATCHLIST_SAVE_PROPERTY_CRITERIA)
    fun savePropertyCriteria(@Body watchlistPropertyCriteriaPO: WatchlistPropertyCriteriaPO): Call<SavePropertyCriteriaResponse>

    @POST(Endpoint.WATCHLIST_DELETE_PROPERTY_CRITERIA)
    fun deletePropertyCriteria(@Body ids: List<Int>): Call<DeletePropertyCriteriaResponse>

    @POST(Endpoint.WATCHLIST_MARK_PROPERTY_CRITERIA_AS_READ)
    fun markPropertyCriteriaAsRead(@Body ids: List<Int>): Call<DefaultResultResponse>

    @POST(Endpoint.WATCHLIST_UPDATE_PROPERTY_CRITERIA_HIDDEN_IND)
    fun updatePropertyCriteriaHiddenInd(@Body ids: List<Int>): Call<DefaultResultResponse>

    @GET(Endpoint.WATCHLIST_GET_LATEST_PROPERTY_CRITERIA_BY_TYPE)
    fun getLatestPropertyCriteriaByType(
        @Query("type") type: Int?,
        @Query("page") page: Int,
        @Query("maxResult") maxResult: Int,
        @Query("showHidden") showHidden: Boolean
    ): Call<GetLatestPropertyCriteriaByTypeResponse>

    @GET(Endpoint.WATCHLIST_GET_PROPERTY_CRITERIA_LATEST_LISTINGS)
    fun getPropertyCriteriaLatestListings(
        @Query("id") id: Int,
        @Query("page") page: Int,
        @Query("maxResult") maxResult: Int
    ): Call<GetPropertyCriteriaLatestListingsResponse>

    @GET(Endpoint.WATCHLIST_GET_PROPERTY_CRITERIA_LATEST_TRANSACTIONS)
    fun getPropertyCriteriaLatestTransactions(
        @Query("id") id: Int,
        @Query("page") page: Int,
        @Query("maxResult") maxResult: Int
    ): Call<TableListResponse>

    /**************************** Community ****************************/
    @GET(Endpoint.COMMUNITY_GET_COMMUNITIES)
    fun getCommunities(
        @Query("communityLevels") communityLevels: String,
        @Query("withMembersOnly") withMembersOnly: Boolean
    ): Call<GetCommunitiesResponse>

    @GET(Endpoint.COMMUNITY_GET_MEMBER_COUNT)
    fun getMemberCount(@Query("communityIds") communityIds: String): Call<GetCommunityMemberCountResponse>

    @GET(Endpoint.COMMUNITY_GET_HYPER_TARGETS)
    fun getCommunityHyperTargets(
        @Query("startIndex") startIndex: Int,
        @Query("maxResult") maxResult: Int
    ): Call<GetCommunityHyperTargetsResponse>

    @GET(Endpoint.COMMUNITY_GET_MEMBER_COUNT_BY_HYPER_TARGET)
    fun getMemberCountByHyperTarget(@Query("targetId") targetId: Int): Call<GetCommunityMemberCountByHyperTargetResponse>
}