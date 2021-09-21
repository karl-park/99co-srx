package sg.searchhouse.agentconnect.model.app

import sg.searchhouse.agentconnect.model.api.homereport.GenerateHomeReportRequestBody
import sg.searchhouse.agentconnect.model.api.xvalue.SearchWithWalkupResponse
import java.io.Serializable

class ExistingHomeReport(
    val address: String,
    val url: String,
    val localFileName: String,
    // optional for backward compatibility
    val generateHomeReportRequestBody: GenerateHomeReportRequestBody? = null,
    val walkupResponseData: SearchWithWalkupResponse.Data? = null,
    val details: Details? = null
) : Serializable {
    class Details(val floor: String?, val unit: String?, val area: Int) : Serializable
}