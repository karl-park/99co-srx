package sg.searchhouse.agentconnect.event.cea

import android.graphics.Bitmap
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum

data class CeaUpdateSignature(
    val pair: Pair<String, Bitmap>,
    val isEstateAgentPO: Boolean,
    val source: CeaExclusiveEnum.SignatureSource
)