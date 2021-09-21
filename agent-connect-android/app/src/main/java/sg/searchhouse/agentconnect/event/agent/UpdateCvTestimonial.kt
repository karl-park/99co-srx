package sg.searchhouse.agentconnect.event.agent

import sg.searchhouse.agentconnect.model.api.agent.AgentCvPO

data class UpdateCvTestimonial(
    val testimonial: AgentCvPO.Testimonial,
    val isDelete: Boolean
)