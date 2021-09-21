package sg.searchhouse.agentconnect.event.transaction

import android.text.TextUtils
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.PropertyTypeUtil

class GetProjectTransactionsEvent(
    val projectId: String,
    val projectName: String,
    val projectDescription: String,
    private val cdResearchSubType: String
) {
    fun getPropertySubType(): ListingEnum.PropertySubType {
        return ListingEnum.PropertySubType.values().find {
            TextUtils.equals(cdResearchSubType, it.type.toString())
        } ?: throw IllegalArgumentException("Missing/invalid property sub type in this project!")
    }

    // Based on its property sub type, decide whether should show tower view on project transaction page
    fun isShowTower(): Boolean {
        return getPropertySubType().run { PropertyTypeUtil.isShowTransactionsTower(type) }
    }

    fun isHdb(): Boolean {
        return NumberUtil.toInt(cdResearchSubType)?.let {
            PropertyTypeUtil.isHDB(it)
        } ?: run {
            false
        }
    }
}