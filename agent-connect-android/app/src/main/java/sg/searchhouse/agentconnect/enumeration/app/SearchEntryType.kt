package sg.searchhouse.agentconnect.enumeration.app

enum class SearchEntryType {
    QUERY, AMENITY, DISTRICT, HDB_TOWN,
    PROPERTY_MAIN_TYPE, PROPERTY_SUB_TYPE, // listings only
    QUERY_PROPERTY_SUB_TYPE, PROJECT // transactions only
}