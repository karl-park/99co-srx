package sg.searchhouse.agentconnect.event.agent

import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.PaymentPurpose
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.SubscriptionCreditSource

data class NotifyPaymentSuccessEvent(
    val source: SubscriptionCreditSource,
    val paymentPurpose: PaymentPurpose
)