package sg.searchhouse.agentconnect.enumeration.api

class AccessibilityEnum {

    enum class SubscriptionTypeEnum(val value: String) {
        NON_STARTER("NONSTARTER"),
        STARTER("STARTER"),
        PROFESSIONAL("HRC"),
        INVESTOR("HRB"),
        ADVISOR("HRH"),
        ANALYZER_LITE("HRF"),
        ANALYZER_PLUS("HRG"),
        ANALYZER("HRD"),
        NEW_LAUNCHES_MASTER("HRI"),
        TRIAL("HRZ")
    }

    //Note: this enum list should be same with list from backend
    enum class AdvisorModule(
        val value: String,
        val inAccessibleFunctions: List<InAccessibleFunction>?
    ) {
        DASHBOARD("DASHBOARD", listOf(InAccessibleFunction.DASHBOARD_MARKET_WATCH)),
        CHAT("CHAT", listOf(InAccessibleFunction.CHAT_CREATE, InAccessibleFunction.CHAT_SEARCH)),
        LISTING_MANAGEMENT(
            "LISTING_MANAGEMENT",
            listOf(
                InAccessibleFunction.LISTING_MANAGEMENT_V360,
                InAccessibleFunction.LISTING_MANAGEMENT_X_DRONE,
                InAccessibleFunction.LISTING_MANAGEMENT_VALUATION,
                InAccessibleFunction.LISTING_MANAGEMENT_FEATURE_LISTINGS,
                InAccessibleFunction.LISTING_MANAGEMENT_CERTIFIED_LISTINGS
            )
        ),
        CEA_EXCLUSIVE("CEA_EXCLUSIVES", null),
        NEWS("NEWS", null),
        AGENT_PROFILE("AGENT_PROFILE", listOf(InAccessibleFunction.AGENT_PROFILE_WALLET)),
        HOME_REPORT("HOME_REPORT", listOf(InAccessibleFunction.HOME_REPORT_GENERATE_REPORT)),
        PG_IMPORT("PG_IMPORT", null),
        PG_AUTO_IMPORT("PG_AUTO_IMPORT", null),
        AGENT_CV("AGENT_CV", null),
        MY_CLIENTS("MY_CLIENTS", null),
        SEARCH_PANEL("SEARCH_PANEL", null),
        LISTING_SEARCH("LISTINGS_SEARCH", null),
        TRANSACTION_SEARCH("TRANSACTION_SEARCH", null),
        TRANSACTION_SEARCH_COMMERCIAL("TRANSACTION_SEARCH_COMMERCIAL", null),
        TRANSACTION_SEARCH_EXPORT("TRANSACTION_SEARCH_EXPORT", null),
        X_VALUE("XVALUE", null),
        PROJECT_INFO("PROJECT_INFO", null),
        PROJECT_INFO_COMMERCIAL("PROJECT_INFO_COMMERCIAL", null),
        AGENT_DIRECTORY("AGENT_DIRECTORY", null),
        FLASH_REPORT("FLASH_REPORT_HISTORY", null),
        NEW_PROJECT("NEW_PROJECT_REPORTS", null),
        WATCHLISTS("WATCHLISTS", null),
        XVALUE_REPORT("XVALUE_REPORT", null)
    }

    //this is front end list
    enum class InAccessibleFunction(val value: String) {
        CHAT_CREATE("CHAT_CREATE"),
        CHAT_SEARCH("CHAT_SEARCH"),
        LISTING_MANAGEMENT_V360("V360"),
        LISTING_MANAGEMENT_X_DRONE("X_DRONE"),
        LISTING_MANAGEMENT_VALUATION("VALUATION"),
        LISTING_MANAGEMENT_FEATURE_LISTINGS("FEATURE_LISTING"),
        LISTING_MANAGEMENT_CERTIFIED_LISTINGS("CERTIFIED_LISTING"),
        DASHBOARD_MARKET_WATCH("MARKET_WATCH"),
        AGENT_PROFILE_WALLET("AGENT_PROFILE_WALLET"),
        HOME_REPORT_GENERATE_REPORT("HOME_REPORT_GENERATE_REPORT")
    }

    enum class AccessibilityInd(val value: String) {
        YES("YES"),
        NO("NO"),
        LIMITED("LIMITED")
    }
}