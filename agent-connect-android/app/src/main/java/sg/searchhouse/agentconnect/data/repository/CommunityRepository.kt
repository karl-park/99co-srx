package sg.searchhouse.agentconnect.data.repository

import retrofit2.Call
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.enumeration.api.CommunityEnum
import sg.searchhouse.agentconnect.model.api.community.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API:             https://streetsine.atlassian.net/wiki/spaces/SIN/pages/1339064349/Community+V1+API
 * Data structure:  https://streetsine.atlassian.net/wiki/spaces/SIN/pages/1140719619/Communities+Data+Structure
 */
@Singleton
class CommunityRepository @Inject constructor(private val srxDataSource: SrxDataSource) {
    fun getPlanningAreaSubZoneCommunities(
        withMembersOnly: Boolean = false
    ): Call<GetCommunitiesResponse> {
        val communityLevels = listOf(
            CommunityEnum.CommunityLevel.SUB_ZONE,
            CommunityEnum.CommunityLevel.PLANNING_AREA
        ).map { it.value }.joinToString(",")
        return srxDataSource.getCommunities(communityLevels, withMembersOnly)
    }

    fun getMemberCount(communityIds: List<Int>): Call<GetCommunityMemberCountResponse> {
        return srxDataSource.getMemberCount(communityIds.joinToString(","))
    }

    fun getMemberCountByHyperTarget(targetId: Int): Call<GetCommunityMemberCountByHyperTargetResponse> {
        return srxDataSource.getMemberCountByHyperTarget(targetId)
    }
}