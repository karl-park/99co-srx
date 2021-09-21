package sg.searchhouse.agentconnect.event.calculator

import sg.searchhouse.agentconnect.model.api.xvalue.SearchWithWalkupResponse
import sg.searchhouse.agentconnect.model.app.XValueEntryParams

class GetSellingCalculatorXValueEvent(
    val xValue: Int,
    val address: String,
    val xValueEntryParams: XValueEntryParams,
    val walkupResponseData: SearchWithWalkupResponse.Data
)