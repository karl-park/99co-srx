package sg.searchhouse.agentconnect.event.transaction

import sg.searchhouse.agentconnect.model.api.transaction.TransactionListItem
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus

class UpdateProjectTransactionsEvent(
    val transactions: List<TransactionListItem>,
    val mainStatus: ApiStatus.StatusKey,
    val projectTransactionType: ProjectTransactionType
) {
    enum class ProjectTransactionType {
        SALE, RENT_REAL_TIME, RENT_OFFICIAL, ANY
    }
}