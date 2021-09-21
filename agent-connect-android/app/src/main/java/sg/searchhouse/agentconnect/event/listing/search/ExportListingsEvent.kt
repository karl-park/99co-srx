package sg.searchhouse.agentconnect.event.listing.search

import sg.searchhouse.agentconnect.enumeration.api.ListingEnum

class ExportListingsEvent(val exportOption: ListingEnum.ExportOption, val withAgentContact: Boolean, val withPhoto: Boolean)