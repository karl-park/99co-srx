package sg.searchhouse.agentconnect.event.agent

import sg.searchhouse.agentconnect.model.api.agent.AgentCvPO

data class UpdateCvTestimonialList(
    val testimonials: ArrayList<AgentCvPO.Testimonial>
)