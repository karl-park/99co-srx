package sg.searchhouse.agentconnect.event.dashboard

data class SubscriberPromptDismissEvent(
    val launchDashboard: Boolean?,
    val showAutoImportPopup: Boolean?
)