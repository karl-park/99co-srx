package sg.searchhouse.agentconnect.enumeration.status

enum class ListState(val value: String) {
    HAS_LIST("has_list"),
    NO_LIST("no_list"),
    ERROR_LIST("error_list"),
    LOADING_LIST("loading_list")
}