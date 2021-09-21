package sg.searchhouse.agentconnect.enumeration.app

enum class FirebaseAnalyticsEventKeys(val key: String) {
    CREATE_LISTING_BUTTON_CLICKED("create_listing_button_clicked"),
    PUBLISH_LISTING_SUCCESS("publish_listing_success"),

    NAVIGATION_BAR_TAB_CLICKED("navigation_bar_tab_clicked"),
    X_TAB_OPTION_CLICKED("x_tab_option_clicked"),

    MENU_ITEM_CLICKED("menu_item_clicked"),
    CALCULATOR_MENU_OPTION_CLICKED("calculator_menu_option_clicked")
}

enum class FirebaseAnalyticsDefaultPropertyKeys(val key: String) {
    USER_ID("user_id"),
    SRX_USER_ID("srx_user_id"),
    NAME("name"),
    EMAIL("email"),
    COUNTRY_CODE("country_code"),
    MOBILE("mobile"),
    AGENCY("agency"),
    IP_ADDRESS("ip_address"),
    APP_VERSION("app_version"),
    DEVICE_ID("device_id"),
    TIME_STAMP("time_stamp")
}

enum class FirebaseAnalyticsPropertyKeys(val key: String) {
    CREATE_LISTING_LISTING_ID("listing_id"),
    CREATE_LISTING_TIMESTAMP("time_stamp"),

    NAVIGATION_BAR_TAB_NAME("tab_name"),
    BOTTOM_APP_BAR_OPTION_NAME("option_name"),

    MENU_ITEM_NAME("menu_item_name"),
    CALCULATOR_MENU_OPTION_NAME("calculator_menu_option_name")
}