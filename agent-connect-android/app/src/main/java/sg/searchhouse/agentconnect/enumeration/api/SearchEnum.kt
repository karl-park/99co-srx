package sg.searchhouse.agentconnect.enumeration.api

class SearchEnum {
    // source param in findLocationSuggestions
    enum class FindLocationSuggestionSource(val value: String) {
        LISTING_SEARCH("listingSearch"),
        HDB_SEARCH("hdbSearch"),
        FLOORPLAN_SEARCH("floorplanSearch"),
        BUILDING_SEARCH("buildingSearch")
    }

    // Types of LocationEntryPO
    enum class LocationEntryType {
        HDB_ESTATE, DISTRICT, CONDO, POSTAL, STREET, BUILDING, HDB, LANDED, COMMERCIAL, MRT, MIXED
    }
}