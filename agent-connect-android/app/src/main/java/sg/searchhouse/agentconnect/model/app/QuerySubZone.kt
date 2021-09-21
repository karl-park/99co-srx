package sg.searchhouse.agentconnect.model.app

import android.content.Context
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.dsl.toQueryText
import sg.searchhouse.agentconnect.model.api.community.CommunityTopDownPO

class QuerySubZone(
    val planningAreaId: Int,
    val planningAreaName: String,
    val subZonePO: CommunityTopDownPO?,
) {
    fun getNameQueryText(): String {
        return (subZonePO?.community?.name ?: planningAreaName).toQueryText()
    }

    fun getNameLabel(context: Context): String {
        return subZonePO?.community?.name ?: context.getString(
            R.string.label_all_sub_zones,
            planningAreaName
        )
    }
}