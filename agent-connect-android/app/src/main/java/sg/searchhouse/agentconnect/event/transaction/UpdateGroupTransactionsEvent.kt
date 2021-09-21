package sg.searchhouse.agentconnect.event.transaction

import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.model.api.transaction.TransactionSearchCriteriaV2VO

class UpdateGroupTransactionsEvent(
    val ownershipType: ListingEnum.OwnershipType,
    val propertyMainType: ListingEnum.PropertyMainType?,
    val transactionSearchCriteriaV2VO: TransactionSearchCriteriaV2VO,
    val isEnablePagination: Boolean
)