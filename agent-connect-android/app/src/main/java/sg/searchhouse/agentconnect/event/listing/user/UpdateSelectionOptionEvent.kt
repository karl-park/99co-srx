package sg.searchhouse.agentconnect.event.listing.user

class UpdateSelectionOptionEvent(
    val option: MyListingSelectAllEvent.Option,
    val isUpdateSelectedLists: Boolean = true
)