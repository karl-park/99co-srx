package sg.searchhouse.agentconnect.model.api.xvalue

import java.io.Serializable

data class GenerateXValueReportRequestBody(
    val shortAddress: String,
    val srxValuationRequestId: Int,
    val crunchResearchStreetId: Int,
    val showFullReport: Boolean = true
) : Serializable