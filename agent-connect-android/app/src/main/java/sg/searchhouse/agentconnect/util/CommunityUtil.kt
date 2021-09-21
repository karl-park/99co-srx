package sg.searchhouse.agentconnect.util

import sg.searchhouse.agentconnect.model.api.community.CommunityTopDownPO

object CommunityUtil {
    fun getFlatPlanningAreasAndSubZones(planningAreas: List<CommunityTopDownPO>): List<CommunityTopDownPO> {
        return planningAreas + getSubZones(planningAreas)
    }

    fun getSubZones(planningAreas: List<CommunityTopDownPO>): List<CommunityTopDownPO> {
        return planningAreas.map { it.children }.flatten()
    }
}