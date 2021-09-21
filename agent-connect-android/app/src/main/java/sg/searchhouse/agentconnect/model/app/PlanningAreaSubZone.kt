package sg.searchhouse.agentconnect.model.app

class PlanningAreaSubZone(val planningAreaId: Int, val subZoneId: Int?) {
    override fun equals(other: Any?): Boolean {
        return if (other is PlanningAreaSubZone) {
            other.planningAreaId == planningAreaId && other.subZoneId == subZoneId
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = planningAreaId
        result = 31 * result + (subZoneId ?: 0)
        return result
    }
}