package sg.searchhouse.agentconnect.event.transaction

// If isLimited is true, export up to 30 transactions only
class RequestGroupTransactionsCsvEvent(val isLimited: Boolean = false)