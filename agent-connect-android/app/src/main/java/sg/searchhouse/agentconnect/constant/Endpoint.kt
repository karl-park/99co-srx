package sg.searchhouse.agentconnect.constant

/*
* All end points must be in this file
* **/

object Endpoint {
    // General
    const val PRIVACY_POLICY = "/privacy-policy"
    const val TERMS_OF_USE = "/terms-of-use"
    const val TERMS_OF_SALE = "/terms-of-sale"

    //User Profile
    private const val USER_PROFILE = "/api/v1/user/info"
    const val GET_PROFILE = "$USER_PROFILE?action=getProfile"
    const val UPDATE_PHOTO = "$USER_PROFILE?action=updatePhoto"
    const val REMOVE_PHOTO = "$USER_PROFILE?action=removePhoto"

    //User Authentication Api Endpoints
    private const val USER_AUTHENTICATION = "/api/v1/auth/user/authentication"
    const val USER_AUTH_LOGIN = "$USER_AUTHENTICATION?action=login"
    const val USER_AUTH_VERIFY_OTP = "$USER_AUTHENTICATION?action=verifyOTP"
    const val USER_AUTH_RESEND_OTP = "$USER_AUTHENTICATION?action=resendOTP"
    const val USER_AUTH_RESET_PASSWORD = "$USER_AUTHENTICATION?action=resetPassword"
    const val USER_AUTH_CREATE_ACCOUNT = "$USER_AUTHENTICATION?action=createAccount"
    const val USER_AUTH_RESET_DEVICE = "$USER_AUTHENTICATION?action=resetDevice"
    const val USER_AUTH_REGISTER_TOKEN = "$USER_AUTHENTICATION?action=registerToken"
    const val USER_AUTH_UNREGISTER_TOKEN = "$USER_AUTHENTICATION?action=unRegisterToken"
    const val USER_AUTH_LOGOUT = "$USER_AUTHENTICATION?action=logout"
    const val USER_AUTH_CHECK_VERSION = "$USER_AUTHENTICATION?action=checkVersion"
    const val USER_AUTH_LOGIN_AS_AGENT = "$USER_AUTHENTICATION?action=loginAsAgent"

    private const val USER_AUTH_CONFIG = "/api/v1/user/config"
    const val USER_AUTH_CONFIG_GET_CONFIG = "$USER_AUTH_CONFIG?action=getConfig"

    //schedule
    private const val SCHEDULE = "/api/v1/agent/schedule/callback"
    const val SCHEDULE_CALL_BACK = "$SCHEDULE?action=scheduleCallback"

    //Agent Detail Information
    // https://streetsine.atlassian.net/wiki/spaces/SIN/pages/695238693/Agent+Details+V1+API
    private const val AGENT_DETAIL = "/api/v1/agent/details"
    const val AGENT_GET_AGENT_DETAILS = "$AGENT_DETAIL?action=getAgentDetails"
    const val AGENT_GET_AGENT_CV = "$AGENT_DETAIL?action=getAgentCV"
    const val AGENT_SAVE_UPDATE_AGENT_CV = "$AGENT_DETAIL?action=saveOrUpdateUserAgentCV"
    const val AGENT_CHECK_PUBLIC_URL_AVAILABLE = "$AGENT_DETAIL?action=checkIsPublicUrlAvailable"
    const val AGENT_GET_AGENT_FULL_PROFILE = "$AGENT_DETAIL?action=getAgentFullProfile"

    //Subscription
    private const val SUBSCRIPTION = "/api/v1/agent/subscription"
    const val SUBSCRIPTION_GET_PACKAGE_DETAILS =
        "$SUBSCRIPTION?action=getSubscriptionPackageDetails"
    const val SUBSCRIPTION_CREATE_TRIAL_ACCOUNT = "$SUBSCRIPTION?action=createTrialAccount"

    //SRX Credits
    // https://streetsine.atlassian.net/wiki/spaces/SIN/pages/736952355/SRX+Credits+V1+API
    private const val CREDIT = "/api/v1/srxcredits/details"
    const val CREDIT_GET_PACKAGE_DETAILS = "$CREDIT?action=getSrxCreditPackageDetails"

    //Payment
    private const val PAYMENT = "/api/v1/payment/details"
    const val PAYMENT_GENERATE_PAYMENT_LINK = "$PAYMENT?action=generatePaymentLink"

    //Agent Transactions
    // https://streetsine.atlassian.net/wiki/spaces/SIN/pages/723320839/Agent+Transactions+V1+API
    private const val AGENT_TRANSACTION = "/api/v1/agent/transactions"
    const val AGENT_TRANSACTION_GET_AGENT_TRANSACTIONS =
        "$AGENT_TRANSACTION?action=getAgentTransactions"
    const val AGENT_TRANSACTION_CONCEAL_TRANSACTION = "$AGENT_TRANSACTION?action=concealTransaction"
    const val AGENT_TRANSACTION_REVEAL_TRANSACTION = "$AGENT_TRANSACTION?action=revealTransaction"
    const val AGENT_TRANSACTION_FIND_OTHER_AGENT_TRANSACTION =
        "$AGENT_TRANSACTION?action=findOtherAgentTransactions"

    // Greeting
    const val GET_APP_GREETING = "/api/v1/marketing/greeting?action=getAppGreeting"

    // Listings management
    private const val LISTINGS_MANAGEMENT = "/api/v1/listings/management"
    const val LISTINGS_MANAGEMENT_GET_LISTINGS_SUMMARY =
        "$LISTINGS_MANAGEMENT?action=getListingsSummary"
    const val LISTINGS_MANAGEMENT_GET_LISTINGS_GROUP_SUMMARY =
        "$LISTINGS_MANAGEMENT?action=getListingsGroupSummary"
    const val LISTINGS_MANAGEMENT_FIND_LISTINGS =
        "$LISTINGS_MANAGEMENT?action=findListings"
    const val LISTINGS_MANAGEMENT_FIND_LISTINGS_COUNT =
        "$LISTINGS_MANAGEMENT?action=findListingsCount"
    const val LISTINGS_MANAGEMENT_CREATE_LISTING = "$LISTINGS_MANAGEMENT?action=createListing"
    const val LISTINGS_MANAGEMENT_GET_LISTING = "$LISTINGS_MANAGEMENT?action=getListing"
    const val LISTINGS_MANAGEMENT_UPDATE_LISTING = "$LISTINGS_MANAGEMENT?action=updateListing"
    const val LISTINGS_MANAGEMENT_POST_LISTING = "$LISTINGS_MANAGEMENT?action=postListing"
    const val LISTINGS_MANAGEMENT_UPLOAD_PHOTO = "$LISTINGS_MANAGEMENT?action=uploadPhoto"
    const val LISTINGS_MANAGEMENT_DELETE_PHOTOS = "$LISTINGS_MANAGEMENT?action=deletePhotos"
    const val LISTINGS_MANAGEMENT_GET_POST_CREDITS = "$LISTINGS_MANAGEMENT?action=getPostingCredits"
    const val LISTINGS_MANAGEMENT_MOVE_PHOTO = "$LISTINGS_MANAGEMENT?action=movePhoto"
    const val LISTINGS_MANAGEMENT_REQUEST_OWNER_CERTIFICATION =
        "$LISTINGS_MANAGEMENT?action=requestOwnerCertification"
    const val LISTINGS_MANAGEMENT_SET_COVER_PHOTO = "$LISTINGS_MANAGEMENT?action=setCoverPhoto"
    const val LISTINGS_MANAGEMENT_GET_ACTIVATE_SRX_CREDIT_SUMMARY =
        "$LISTINGS_MANAGEMENT?action=getActivateSrxCreditSummary"
    const val LISTINGS_MANAGEMENT_ACTIVATE_SRX_CREDIT =
        "$LISTINGS_MANAGEMENT?action=activateSrxCreditForListings"
    const val LISTINGS_MANAGEMENT_REPOST_LISTINGS = "$LISTINGS_MANAGEMENT?action=repostListings"
    const val LISTINGS_MANAGEMENT_TAKE_OFF_LISTINGS = "$LISTINGS_MANAGEMENT?action=takeOffListings"
    const val LISTINGS_MANAGEMENT_DELETE_LISTINGS = "$LISTINGS_MANAGEMENT?action=deleteListings"
    const val LISTINGS_MANAGEMENT_GET_ADDON_PROMPTS = "$LISTINGS_MANAGEMENT?action=getAddonPrompts"
    const val LISTINGS_MANAGEMENT_COPY_LISTING = "$LISTINGS_MANAGEMENT?action=copyListing"
    const val LISTINGS_MANAGEMENT_VET_PHOTO = "$LISTINGS_MANAGEMENT?action=vetPhoto"
    const val LISTINGS_MANAGEMENT_APPEAL_PHOTO = "$LISTINGS_MANAGEMENT?action=appealPhoto"
    const val LISTINGS_MANAGEMENT_OWNER_CERTIFICATION_TEMPLATE =
        "$LISTINGS_MANAGEMENT?action=getOwnerCertificationNotificationTemplate"
    const val LISTINGS_MANAGEMENT_SEND_OWNER_CERTIFICATIONS =
        "$LISTINGS_MANAGEMENT?action=sendOwnerCertificationNotifications"

    // Listings management (Pg Import)
    private const val LISTINGS_MANAGEMENT_PORTAL = "api/v1/listings/management/portal"
    const val LISTINGS_MANAGEMENT_GET_APIS = "$LISTINGS_MANAGEMENT_PORTAL?action=getAPIs"
    const val LISTINGS_MANAGEMENT_PORTAL_SERVER_LOGIN =
        "$LISTINGS_MANAGEMENT_PORTAL?action=login"
    const val LISTINGS_MANAGEMENT_PORTAL_SERVER_LOGOUT = "$LISTINGS_MANAGEMENT_PORTAL?action=logout"
    const val LISTINGS_MANAGEMENT_PORTAL_GET_LISTINGS =
        "$LISTINGS_MANAGEMENT_PORTAL?action=getListings"
    const val LISTINGS_MANAGEMENT_PORTAL_IMPORT_LISTING =
        "$LISTINGS_MANAGEMENT_PORTAL?action=importListing"
    const val LISTINGS_MANAGEMENT_PORTAL_CLIENT_LOGIN =
        "$LISTINGS_MANAGEMENT_PORTAL?action=clientLogin"
    const val LISTINGS_MANAGEMENT_PORTAL_CLIENT_GET_LISTINGS =
        "$LISTINGS_MANAGEMENT_PORTAL?action=clientGetListings"
    const val LISTINGS_MANAGEMENT_PORTAL_CLIENT_IMPORT_LISTING =
        "$LISTINGS_MANAGEMENT_PORTAL?action=clientImportListing"
    const val LISTINGS_MANAGEMENT_PORTAL_UPDATE_PORTAL_ACCOUNT =
        "$LISTINGS_MANAGEMENT_PORTAL?action=updatePortalAccount"
    const val LISTINGS_MANAGEMENT_PORTAL_TOGGLE_LISTINGS_SYNC =
        "$LISTINGS_MANAGEMENT_PORTAL?action=toggleListingsSync"
    const val LISTINGS_MANAGEMENT_PORTAL_CLIENT_GET_LISTINGS_AUTO =
        "$LISTINGS_MANAGEMENT_PORTAL?action=clientGetListingsAuto"
    const val LISTINGS_MANAGEMENT_PORTAL_SERVER_GET_LISTINGS_AUTO =
        "$LISTINGS_MANAGEMENT_PORTAL?action=getListingsAuto"

    //Listing Management Cea Exclusive
    private const val CEA_EXCLUSIVE = "api/v1/listings/management/ceaform"
    const val CEA_EXCLUSIVE_GET_FORM_TEMPLATE =
        "$CEA_EXCLUSIVE?action=getFormTemplate"
    const val CEA_EXCLUSIVE_SUBMIT_FORM = "$CEA_EXCLUSIVE?action=submitForm"
    const val CEA_EXCLUSIVE_CREATE_UPDATE_FORM = "$CEA_EXCLUSIVE?action=createUpdateForm"
    const val CEA_EXCLUSIVE_DELETE_CLIENT = "$CEA_EXCLUSIVE?action=deleteClient"
    const val CEA_EXCLUSIVE_REMOVE_DRAFT_CEA_FORMS = "$CEA_EXCLUSIVE?action=deleteUnsubmittedForms"
    const val CEA_EXCLUSIVE_FIND_UNSUBMITTED_CEA_FORMS =
        "$CEA_EXCLUSIVE?action=findUnsubmittedCeaForms"

    //Chat SSM
    private const val CHAT = "/api/v1/chat/ssm"
    const val CHAT_LOAD_SSM_CONVERSATIONS = "$CHAT?action=findSsmConversations"
    const val CHAT_LEAVE_SSM_CONVERSATIONS = "$CHAT?action=leaveSsmConversations"
    const val CHAT_CREATE_SSM_CONVERSATION = "$CHAT?action=createSsmConversation"
    const val CHAT_FIND_SSM_MESSAGES = "$CHAT?action=findSsmMessages"
    const val CHAT_SEND_SSM_MESSAGE = "$CHAT?action=sendSsmMessage"
    const val CHAT_RESET_UNREAD_COUNT = "$CHAT?action=resetUnreadCount"
    const val CHAT_GET_SSM_CONVERSATION_INFO = "$CHAT?action=getSsmConversationInfo"
    const val CHAT_REMOVE_MEMBERS_FROM_CONVERSATION = "$CHAT?action=removeMembersFromConversation"
    const val CHAT_UPDATE_SSM_CONVERSATION_SETTING = "$CHAT?action=updateSsmConversationSettings"
    const val CHAT_ADD_MEMBER_TO_CONVERSATION = "$CHAT?action=addMembersToConversation"
    const val CHAT_BLACKLIST_SSM_CONVERSATIONS = "$CHAT?action=blacklistLeaveSsmConversations"
    const val CHAT_GET_SSM_CONVERSATION = "$CHAT?action=getSsmConversation"
    const val CHAT_CREATE_SSM_LISTINGS_BLAST =
        "$CHAT?action=createSsmListingsBlastConversation"

    //AgentPO Enquiry
    private const val AGENT_ENQUIRY = "/api/v1/agent/enquiry"
    const val DELETE_ENQUIRIES = "$AGENT_ENQUIRY?action=deleteEnquiries"

    //AgentPO Directory
    private const val AGENT_DIRECTORY = "/api/v1/agent/directory"
    const val FIND_AGENT = "$AGENT_DIRECTORY?action=findAgents"
    const val BLACK_LIST_AGENTS = "$AGENT_DIRECTORY?action=blacklistAgents"

    // Listing search
    private const val LISTING_SEARCH = "/api/v1/listings/search"
    const val GET_AMENITIES_LIST = "$LISTING_SEARCH?action=getAmenitiesList"
    const val GET_LISTING = "$LISTING_SEARCH?action=getListing"
    const val FIND_LISTINGS = "$LISTING_SEARCH?action=findListings"
    const val FIND_LISTINGS_COUNT = "$LISTING_SEARCH?action=findListingsCount"
    const val EXPORT_LISTINGS = "$LISTING_SEARCH?action=exportListings"

    // TODO Temporary
    const val GET_LISTING_X_TREND = "/srx/listings/loadXValueTrend/redefinedSearch.srx"
    const val GET_LISTING_LATEST_TRANSACTIONS =
        "/mobile/iphone/consumer2.mobile3?action=loadXValueTrend"

    // Listing map
    private const val LISTING_MAP = "/api/v1/listings/map"
    const val FIND_MAP_REGION_VIEW = "$LISTING_MAP?action=findMapRegionView"
    const val FIND_MAP_VIEW = "$LISTING_MAP?action=findMapView"

    //Lookup
    private const val LOOKUP = "/api/v1/lookup"
    const val LOOKUP_HDB_TOWNS = "$LOOKUP?action=hdbTowns"
    const val LOOKUP_SINGAPORE_DISTRICTS = "$LOOKUP?action=singaporeDistricts"
    const val LOOKUP_MRTS = "$LOOKUP?action=railTransitInformation"
    const val LOOKUP_SCHOOLS = "$LOOKUP?action=schools"
    const val LOOKUP_PROPERTY_MODELS = "$LOOKUP?action=models"
    const val LOOKUP_BEDROOMS = "$LOOKUP?action=bedrooms"
    const val LOOKUP_LISTING_FEATURES_FIXTURES_AREAS = "$LOOKUP?action=listingFeaturesFixturesAreas"

    //Location search
    private const val LOCATION_SEARCH = "/api/v1/location/search"
    const val LOCATION_SEARCH_FIND_LOCATION_SUGGESTIONS =
        "$LOCATION_SEARCH?action=findLocationSuggestions"
    const val LOCATION_SEARCH_FIND_CURRENT_LOCATION = "$LOCATION_SEARCH?action=findCurrentLocation"
    const val LOCATION_SEARCH_FIND_PROPERTIES = "$LOCATION_SEARCH?action=findProperties"
    const val LOCATION_SEARCH_GET_PROPERTY_TYPE = "$LOCATION_SEARCH?action=getPropertyType"
    const val LOCATION_SEARCH_GET_ADDRESS_PROPERTY_TYPE =
        "$LOCATION_SEARCH?action=getAddressPropertyType"
    const val LOCATION_SEARCH_GET_PROPERTY_DATA = "$LOCATION_SEARCH?action=getPropertyData"
    const val LOCATION_SEARCH_GET_ADDRESS_BY_PROPERTY_TYPE =
        "$LOCATION_SEARCH?action=getAddressPropertyType"

    //Transaction
    private const val TRANSACTION_HOME_REPORT = "/api/v1/transactions/homeReport"
    const val TRANSACTION_HOME_REPORT_GET_LATEST_RENTAL_TRANSACTIONS =
        "$TRANSACTION_HOME_REPORT?action=getLatestRentalTransactions"
    const val TRANSACTION_HOME_REPORT_GET_LATEST_SALE_TRANSACTIONS =
        "$TRANSACTION_HOME_REPORT?action=getLatestSaleTransactions"
    const val TRANSACTION_HOME_REPORT_LOAD_TOWER_VIEW_FOR_LAST_SOLD_TRANSACTION =
        "$TRANSACTION_HOME_REPORT?action=loadTowerViewForLastSoldTransaction"
    const val FIND_SALE_PROJECTS_BY_KEYWORD =
        "$TRANSACTION_HOME_REPORT?action=findSaleProjectsByKeyword"
    const val FIND_RENTAL_PROJECTS_BY_KEYWORD =
        "$TRANSACTION_HOME_REPORT?action=findRentalProjectsByKeyword"
    const val TRANSACTION_HOME_REPORT_FIND_PROJECTS_BY_KEYWORD =
        "$TRANSACTION_HOME_REPORT?action=findProjectsByKeyword"

    //Transactions search
    private const val TRANSACTIONS_SEARCH = "/api/v1/transactions/search"
    const val TABLE_LIST = "$TRANSACTIONS_SEARCH?action=tableList"
    const val PROJECT_LIST =
        "$TRANSACTIONS_SEARCH?action=projectList"
    const val PAGINATED_PROJECT_LIST =
        "$TRANSACTIONS_SEARCH?action=paginatedProjectList"
    const val TRANSACTION_SUMMARY =
        "$TRANSACTIONS_SEARCH?action=summary"

    // Property News
    private const val MARKETING_NEWS = "api/v1/marketing/news"
    const val GET_NEWS_ARTICLE_CATEGORIES = "$MARKETING_NEWS?action=getNewsArticleCategories"
    const val FIND_NEWS_ARTICLES = "$MARKETING_NEWS?action=findNewsArticles"

    //X-Value
    private const val AGENT_X_VALUE = "/api/v1/agent/xvalue"
    const val GET_EXISTING_X_VALUES = "$AGENT_X_VALUE?action=getExistingXValues"
    const val SEARCH_WITH_WALKUP = "$AGENT_X_VALUE?action=searchWithWalkup"
    const val GET_PROJECT = "$AGENT_X_VALUE?action=getProject"
    const val CALCULATE = "$AGENT_X_VALUE?action=calculate"
    const val UPDATE_REQUEST = "$AGENT_X_VALUE?action=updateRequest"
    const val PROMOTION_GET_XVALUE_TREND = "$AGENT_X_VALUE?action=promotionGetXValueTrend"
    const val LOAD_HOME_REPORT_SALE_TRANSACTION_REVISED =
        "$AGENT_X_VALUE?action=loadHomeReportSaleTransactionRevised"
    const val LOAD_HOME_REPORT_RENTAL_INFORMATION_REVISED =
        "$AGENT_X_VALUE?action=loadHomeReportRentalInformationRevised"
    const val GET_VALUATION_DETAIL = "$AGENT_X_VALUE?action=getValuationDetail"
    const val GENERATE_XVALUE_PROPERTY_REPORT = "$AGENT_X_VALUE?action=generateXValuePropertyReport"

    //Home reports
    private const val HOME_REPORT = "/api/v1/homereport"
    const val GENERATE_HOME_REPORT = "$HOME_REPORT?action=generateHomeReport&async=false"
    const val GET_HOME_REPORT_USAGE = "$HOME_REPORT?action=getHomeReportUsage"

    //Project details
    private const val PROJECT_DETAILS = "/api/v1/project/details"
    const val GET_PROJECT_INFORMATION = "$PROJECT_DETAILS?action=getProjectInformation"
    const val GET_ALL_PLANNING_DECISION_TYPES =
        "$PROJECT_DETAILS?action=getAllPlanningDecisionTypes"
    const val GET_PROJECT_PLANNING_DECISION = "$PROJECT_DETAILS?action=getProjectPlanningDecision"
    const val PROJECT_DETAILS_GET_NEW_LAUNCHES = "$PROJECT_DETAILS?action=getNewLaunches"
    const val PROJECT_DETAILS_GET_NEW_LAUNCHES_TEMPLATES =
        "$PROJECT_DETAILS?action=getNewLaunchesNotificationTemplate"
    const val PROJECT_DETAILS_SEND_REPORTS = "$PROJECT_DETAILS?action=sendReportToClient"

    const val SEARCH_ONE_MAP = "/static/mobile/onemap.jsp?maptype=SM&searchval="
    const val SEARCH_LITHOSHEET = "/static/mobile/onemap.jsp?maptype=LL&searchval="

    // Property index
    private const val PROPERTY_INDEX = "api/v1/propertyindex"
    const val LOAD_MARKET_WATCH_INDICES = "$PROPERTY_INDEX?action=loadMarketWatchIndices"
    const val LOAD_MARKET_WATCH_GRAPH = "$PROPERTY_INDEX?action=loadMarketWatchGraph"

    //Flash reports
    private const val MARKET_FLASH_REPORT = "/api/v1/marketflashreport"
    const val FLASH_REPORT_LIST_ALL_FLASH_REPORTS =
        "$MARKET_FLASH_REPORT?action=listAllMarketingFlashReport"
    const val FLASH_REPORT_LIST_ACTIVE_FLASH_REPORTS =
        "$MARKET_FLASH_REPORT?action=listActiveMarketingFlashReport"

    //Cobroke notification
    private const val COBROKE_SMS = "/api/v1/cobrokeNotification"
    const val COBROKE_SMS_GET_INCLUDE_PUBLIC_LISTING_INDICATOR =
        "$COBROKE_SMS?action=getIncludePublicListingIndicator"
    const val COBROKE_SMS_GET_SMS_TEMPLATES = "$COBROKE_SMS?action=getSmsTemplates"
    const val COBROKE_SMS_SEND_SMS = "$COBROKE_SMS?action=sendSMS"
    const val COBROKE_SMS_SEND_SMS_LIMIT_EMAIL = "$COBROKE_SMS?action=sendSmsLimitEmail"

    //Agent client
    private const val AGENT_CLIENTS = "/api/v1/agent/clients"
    const val AGENT_CLIENTS_GET_CLIENTS = "$AGENT_CLIENTS?action=getClients"
    const val AGENT_CLIENTS_GET_INVITE_MESSAGE = "$AGENT_CLIENTS?action=getInviteMessage"
    const val AGENT_CLIENTS_SEND_MESSAGE = "$AGENT_CLIENTS?action=sendMessage"


    //Calculator, affordability (quick)
    private const val CALCULATOR_AFFORD_QUICK = "/api/v1/calculator/affordQck"
    const val CALCULATOR_AFFORD_QUICK_MAX_PURCHASE_PRICE =
        "$CALCULATOR_AFFORD_QUICK?action=maxPurchasePrice"
    const val CALCULATOR_AFFORD_QUICK_INCOME_REQUIRED =
        "$CALCULATOR_AFFORD_QUICK?action=incomeRequired"

    //Calculator, affordability (advanced)
    const val CALCULATOR_AFFORD_ADVANCED = "/api/v1/calculator/affordAdv?action=affordAdvanced"

    //Calculator, selling
    const val CALCULATOR_SELL = "/api/v1/calculator/sell?action=selling"

    //Calculator, affordability (quick)
    private const val CALCULATOR_STAMP_DUTY = "/api/v1/calculator/duty"
    const val CALCULATOR_STAMP_DUTY_BUY = "$CALCULATOR_STAMP_DUTY?action=buyDuty"
    const val CALCULATOR_STAMP_DUTY_RENT = "$CALCULATOR_STAMP_DUTY?action=rentDuty"

    //Watchlist
    private const val WATCHLIST_MANAGE = "/api/v1/watchlist/manage"
    const val WATCHLIST_GET_PROPERTY_CRITERIA = "$WATCHLIST_MANAGE?action=getPropertyCriteria"
    const val WATCHLIST_SAVE_PROPERTY_CRITERIA = "$WATCHLIST_MANAGE?action=savePropertyCriteria"
    const val WATCHLIST_DELETE_PROPERTY_CRITERIA = "$WATCHLIST_MANAGE?action=deletePropertyCriteria"
    const val WATCHLIST_MARK_PROPERTY_CRITERIA_AS_READ =
        "$WATCHLIST_MANAGE?action=markPropertyCriteriaAsRead"
    const val WATCHLIST_UPDATE_PROPERTY_CRITERIA_HIDDEN_IND =
        "$WATCHLIST_MANAGE?action=updatePropertyCriteriaHiddenInd"
    const val WATCHLIST_GET_LATEST_PROPERTY_CRITERIA_BY_TYPE =
        "$WATCHLIST_MANAGE?action=getLatestPropertyCriteriaByType"
    const val WATCHLIST_GET_PROPERTY_CRITERIA_LATEST_LISTINGS =
        "$WATCHLIST_MANAGE?action=getPropertyCriteriaLatestListings"
    const val WATCHLIST_GET_PROPERTY_CRITERIA_LATEST_TRANSACTIONS =
        "$WATCHLIST_MANAGE?action=getPropertyCriteriaLatestTransactions"

    // Community
    private const val COMMUNITY = "/api/v1/community"
    const val COMMUNITY_GET_COMMUNITIES =
        "$COMMUNITY?action=getCommunities"
    const val COMMUNITY_GET_MEMBER_COUNT =
        "$COMMUNITY?action=getMemberCount"
    const val COMMUNITY_GET_HYPER_TARGETS =
        "$LISTINGS_MANAGEMENT?action=getCommunityHyperTargets"
    const val COMMUNITY_GET_MEMBER_COUNT_BY_HYPER_TARGET =
        "$COMMUNITY?action=getMemberCountByHyperTarget"
    const val COMMUNITY_GET_MEMBER_COUNT_BY_HYPER_TARGET_PO =
        "$COMMUNITY?action=getMemberCountByHyperTargetPO"
}