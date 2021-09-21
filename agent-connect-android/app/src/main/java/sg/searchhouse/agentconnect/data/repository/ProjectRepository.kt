package sg.searchhouse.agentconnect.data.repository

import retrofit2.Call
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.enumeration.api.ProjectEnum
import sg.searchhouse.agentconnect.model.api.project.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Doc: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/797835265/Project+V1+API
 */
@Singleton
class ProjectRepository @Inject constructor(private val srxDataSource: SrxDataSource) {
    fun getProjectInformation(id: Int): Call<GetProjectInformationResponse> {
        return srxDataSource.getProjectInformation(id)
    }

    fun getAllPlanningDecisionTypes(): Call<GetAllPlanningDecisionTypesResponse> {
        return srxDataSource.getAllPlanningDecisionTypes()
    }

    fun getProjectPlanningDecision(
        id: Int,
        filterType: String = ProjectEnum.FilterType.ALL.value,
        filterMonth: ProjectEnum.DecisionDate = ProjectEnum.DecisionDate.ALL
    ): Call<GetProjectPlanningDecisionResponse> {
        return srxDataSource.getProjectPlanningDecision(id, filterType, filterMonth.months)
    }

    //for new launches report
    fun getNewLaunches(
        page: Int,
        countOnly: Boolean = false,
        property: String?,
        order: String?,
        cdResearchSubtypes: String?,
        tenure: String?,
        completion: String?,
        districts: String?,
        hdbTownships: String?,
        searchText: String?
    ): Call<GetNewLaunchesResponse> {
        return srxDataSource.getNewLaunches(
            page = page,
            pageSize = AppConstant.BATCH_SIZE_DOUBLE,
            countOnly = countOnly,
            property = property,
            order = order,
            cdResearchSubtypes = cdResearchSubtypes,
            tenure = tenure,
            completion = completion,
            searchText = searchText,
            districts = districts,
            hdbTownships = hdbTownships
        )
    }

    fun getNewLaunchesNotificationTemplate(): Call<GetNewLaunchesNotificationTemplates> {
        return srxDataSource.getNewLaunchesNotificationTemplate()
    }

    fun sendReportToClient(
        crunchResearchStreetIds: String,
        mobileNumbers: String
    ): Call<SendReportToClientResponse> {
        return srxDataSource.sendReportToClient(
            crunchResearchStreetIds = crunchResearchStreetIds,
            mobileNumbers = mobileNumbers
        )
    }
}