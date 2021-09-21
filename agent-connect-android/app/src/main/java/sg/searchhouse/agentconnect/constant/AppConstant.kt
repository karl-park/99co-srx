package sg.searchhouse.agentconnect.constant

import sg.searchhouse.agentconnect.R

object AppConstant {
    const val APP_NAME = "AgentConnect"

    const val STAGING_FLAG = "-staging"

    const val SERVER_INSTANCE_DEFAULT = -1
    const val SERVER_INSTANCE_STAGING = 0
    const val SERVER_INSTANCE_PRODUCTION = 1
    const val SERVER_INSTANCE_CUSTOM = 2

    const val TEST_SERVER_PORT = 8080

    const val DEFAULT_SERVER_TIMEOUT: Long = 60 * 5

    const val BASE_URL_TEST = "http://localhost:$TEST_SERVER_PORT"

    const val CONTACT_CUSTOMER_SERVICE = "+65 6635 3388"
    const val EMAIL_CUSTOMER_SERVICE = "customerservice@srx.com.sg"
    const val COUNTRY_CODE_SINGAPORE = 65
    const val COUNTRY_CODE_SINGAPORE_PLUS = "+65"

    const val DEFAULT_BATCH_SIZE = 10
    const val BATCH_SIZE_DOUBLE = 20

    const val BATCH_SIZE_LISTINGS = 50
    const val BATCH_SIZE_TRANSACTIONS = 30
    const val MAX_LIMIT_EXPORT_TRANSACTIONS = BATCH_SIZE_TRANSACTIONS
    const val BATCH_SIZE_DASHBOARD_WATCHLIST = 3

    const val ONE_SQUARE_FEET_TO_SQUARE_METER = 0.0929
    const val ONE_SQUARE_METER_TO_SQUARE_FEET = 10.7639

    const val DIR_LISTING_REPORT = "listing_reports/"
    const val DIR_X_VALUE_REPORT = "x_value_reports/"
    const val DIR_LISTING_IMAGE = "listing_images/"
    const val DIR_HOME_REPORT = "home_reports/"
    const val DIR_FLASH_REPORT = "flash_reports/"
    const val DIR_NEW_LAUNCHES_REPORT = "new_launches_reports/"
    const val DIR_TRANSACTION_REPORT = "transaction_reports/"
    const val DIR_MISC = "misc/"

    const val URL_GOOGLE_PLAY_SPREADSHEET = "market://search?q=spreadsheet"
    const val URL_DEFAULT_PDF_READER = "market://details?id=com.adobe.reader"

    const val URL_FEATURED_LISTING_TYPES =
        "https://s3-ap-southeast-1.amazonaws.com/static.streetsine/product-brochure/Featured-Listings.pdf"
    const val FILE_NAME_FEATURED_LISTING_TYPES = "Featured-Listings.pdf"

    const val FILE_NAME_VALUATION_SAMPLE_REPORT = "Valuation-Sample-Report.pdf"

    const val DEFAULT_MAP_ZOOM = 17.3f

    const val NEARBY_SEARCH_RADIUS = 2000 // in meter

    const val SOURCE_SRX = "SRX"

    const val MAX_LIMIT_SEND_SRX_CHAT = 200
    const val MAX_LIMIT_EXPORT_LISTINGS = 200
    const val MAX_LIMIT_EXPORT_LISTING_DETAILS_PDF = 50

    const val NOTIFICATION_TYPE_SSM = "AgentToAgentAlert"
    const val NOTIFICATION_TYPE_ANNOUNCEMENT_LISTING = "ancsl"

    const val BATCH_SIZE_AGENTS = 50

    const val MAX_HUMAN_AGE = 150

    const val MIN_AGE_BUY_HOUSE = 21

    val PROFILE_COLORS = arrayOf(
        R.color.profile_color_0,
        R.color.profile_color_1,
        R.color.profile_color_2,
        R.color.profile_color_3,
        R.color.profile_color_4,
        R.color.profile_color_5,
        R.color.profile_color_6,
        R.color.profile_color_7,
        R.color.profile_color_8,
        R.color.profile_color_9,
        R.color.profile_color_10,
        R.color.profile_color_11,
        R.color.profile_color_12
    )
}