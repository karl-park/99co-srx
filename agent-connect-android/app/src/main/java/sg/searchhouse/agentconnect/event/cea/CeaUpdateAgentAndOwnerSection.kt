package sg.searchhouse.agentconnect.event.cea

import android.graphics.Bitmap
import sg.searchhouse.agentconnect.model.api.cea.CeaFormTemplatePO

data class CeaUpdateAgentAndOwnerSection(
    val template: CeaFormTemplatePO,
    val signature: Pair<String, Bitmap>?,
    val isEstateAgentPO: Boolean?
)