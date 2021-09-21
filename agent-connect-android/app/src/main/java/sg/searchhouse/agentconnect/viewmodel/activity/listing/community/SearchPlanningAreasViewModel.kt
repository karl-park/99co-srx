package sg.searchhouse.agentconnect.viewmodel.activity.listing.community

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.data.repository.CommunityRepository
import sg.searchhouse.agentconnect.model.api.community.CommunityTopDownPO
import sg.searchhouse.agentconnect.model.api.community.GetCommunitiesResponse
import sg.searchhouse.agentconnect.model.app.QuerySubZone
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class SearchPlanningAreasViewModel constructor(application: Application) :
    ApiBaseViewModel<GetCommunitiesResponse>(application) {
    @Inject
    lateinit var communityRepository: CommunityRepository

    @Inject
    lateinit var applicationContext: Context

    val query = MutableLiveData<String>()

    val querySubZones = MutableLiveData<List<QuerySubZone>>()

    val hasQuery: LiveData<Boolean> = Transformations.map(query) {
        it?.isNotEmpty() == true
    }

    val allSubZones = MutableLiveData<List<QuerySubZone>>()

    private fun getAllSubZones(planningAreas: List<CommunityTopDownPO>): List<QuerySubZone> {
        val allSubZones = planningAreas.map { planningArea ->
            val planningAreaId = planningArea.getCommunityId()
            val planningAreaName = planningArea.community.name
            QuerySubZone(
                planningAreaId,
                planningAreaName = planningAreaName,
                subZonePO = null
            )
        }
        val subZones = planningAreas.map { planningArea ->
            val planningAreaId = planningArea.getCommunityId()
            val planningAreaName = planningArea.community.name
            val subZones = planningArea.children
            subZones.map { subZone ->
                QuerySubZone(
                    planningAreaId,
                    planningAreaName = planningAreaName,
                    subZonePO = subZone
                )
            }
        }.flatten()
        return allSubZones + subZones
    }

    init {
        viewModelComponent.inject(this)
        performRequest()
    }

    fun performRequest() {
        performRequest(communityRepository.getPlanningAreaSubZoneCommunities())
    }

    override fun shouldResponseBeOccupied(response: GetCommunitiesResponse): Boolean {
        allSubZones.postValue(getAllSubZones(response.communities))
        return response.communities.isNotEmpty()
    }
}
