package sg.searchhouse.agentconnect.model.api.homereport

import java.io.Serializable

data class GenerateHomeReportRequestBody(
    val id: Int,
    val agency: String,
    val block: String,
    val cdResearchSubtype: Int,
    val unit: String,
    val streetKey: String,
    val streetName: String,
    val clientName: String?
) : Serializable