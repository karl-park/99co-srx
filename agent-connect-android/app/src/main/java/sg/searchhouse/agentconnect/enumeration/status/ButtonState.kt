package sg.searchhouse.agentconnect.enumeration.status

enum class ButtonState(val value: String) {
    NORMAL("Normal"),
    SUBMITTING("Submitting"),
    SUBMITTED("Submitted"),
    ERROR("ApiError")
}