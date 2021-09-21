package sg.searchhouse.agentconnect.event.listing.user

import sg.searchhouse.agentconnect.model.api.listing.ListingPO

class NotifySelectedListingsEvent(val listings: ArrayList<ListingPO>)