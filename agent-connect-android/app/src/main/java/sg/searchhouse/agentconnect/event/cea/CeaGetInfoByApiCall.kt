package sg.searchhouse.agentconnect.event.cea

import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum
import sg.searchhouse.agentconnect.model.api.cea.CeaFormRowPO

data class CeaGetInfoByApiCall(
    val keyValue: CeaExclusiveEnum.CeaFormRowTypeKeyValue,
    val row: CeaFormRowPO? = null
)