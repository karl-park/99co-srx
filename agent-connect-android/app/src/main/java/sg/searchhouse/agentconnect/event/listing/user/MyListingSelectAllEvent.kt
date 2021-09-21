package sg.searchhouse.agentconnect.event.listing.user

class MyListingSelectAllEvent(val option: Option) {
    enum class Option {
        SELECT_ALL, UNSELECT_ALL
    }
}