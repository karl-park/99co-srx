package sg.searchhouse.agentconnect.enumeration.app

// Flag used to block changes of other params
// Use case: FilterListingActivity
enum class MimeType(val value: String) {
    PDF("application/pdf"), CSV("text/csv"), PLAIN_TEXT("text/plain")
}