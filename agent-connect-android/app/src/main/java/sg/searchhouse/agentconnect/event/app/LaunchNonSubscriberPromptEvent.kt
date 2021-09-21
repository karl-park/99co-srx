package sg.searchhouse.agentconnect.event.app

import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum

class LaunchNonSubscriberPromptEvent(
    val allowDismiss: Boolean,
    val module: AccessibilityEnum.AdvisorModule
)