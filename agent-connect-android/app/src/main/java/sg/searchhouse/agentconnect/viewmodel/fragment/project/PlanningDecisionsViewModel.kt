package sg.searchhouse.agentconnect.viewmodel.fragment.project

import android.app.Application
import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.data.repository.ProjectRepository
import sg.searchhouse.agentconnect.enumeration.api.ProjectEnum
import sg.searchhouse.agentconnect.model.api.common.NameValuePO
import sg.searchhouse.agentconnect.model.api.project.GetAllPlanningDecisionTypesResponse
import sg.searchhouse.agentconnect.model.api.project.GetProjectPlanningDecisionResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class PlanningDecisionsViewModel(application: Application) :
    ApiBaseViewModel<GetAllPlanningDecisionTypesResponse>(application) {
    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var projectRepository: ProjectRepository

    val selectedDecisionType = MutableLiveData<NameValuePO>()

    val decisionsResponse = MutableLiveData<GetProjectPlanningDecisionResponse>()
    val decisionsStatus = MutableLiveData<ApiStatus.StatusKey>()

    val isShowDecisionsOccupied: MediatorLiveData<Boolean> = MediatorLiveData()
    val isShowDecisionsEmpty: MediatorLiveData<Boolean> = MediatorLiveData()

    val decisionDate: MutableLiveData<ProjectEnum.DecisionDate> by lazy {
        MutableLiveData<ProjectEnum.DecisionDate>().apply {
            value = ProjectEnum.DecisionDate.ALL
        }
    }

    init {
        viewModelComponent.inject(this)
        setupIsShowDecisionOccupied()
        setupIsShowDecisionEmpty()
    }

    fun performGetTypes() {
        performRequest(projectRepository.getAllPlanningDecisionTypes())
    }

    fun performGetDecisions(projectId: Int) {
        val decisionType = selectedDecisionType.value ?: return
        val decisionDate = decisionDate.value ?: return

        decisionsStatus.postValue(ApiStatus.StatusKey.LOADING)
        ApiUtil.performRequest(
            applicationContext,
            projectRepository.getProjectPlanningDecision(
                projectId,
                decisionType.name,
                decisionDate
            ),
            onSuccess = { response ->
                decisionsResponse.postValue(response)
                decisionsStatus.postValue(ApiStatus.StatusKey.SUCCESS)
            },
            onFail = {
                decisionsStatus.postValue(ApiStatus.StatusKey.FAIL)
            },
            onError = {
                decisionsStatus.postValue(ApiStatus.StatusKey.ERROR)
            }
        )
    }

    private fun isDecisionsOccupied(decisionsResponse: GetProjectPlanningDecisionResponse?): Boolean {
        return decisionsResponse?.details?.isNotEmpty() == true
    }

    // TODO Refactor
    private fun setupIsShowDecisionOccupied() {
        isShowDecisionsOccupied.value = false
        isShowDecisionsOccupied.addSource(decisionsResponse) { response ->
            isShowDecisionsOccupied.postValue(isDecisionsOccupied(response))
        }
        isShowDecisionsOccupied.addSource(decisionsStatus) { status ->
            if (status !in listOf(
                    ApiStatus.StatusKey.SUCCESS,
                    ApiStatus.StatusKey.LOADING_NEXT_LIST_ITEM
                )
            ) {
                isShowDecisionsOccupied.postValue(false)
            }
        }
    }

    // TODO Refactor
    private fun setupIsShowDecisionEmpty() {
        isShowDecisionsEmpty.value = false
        isShowDecisionsEmpty.addSource(decisionsResponse) { response ->
            isShowDecisionsEmpty.postValue(!isDecisionsOccupied(response))
        }
        isShowDecisionsEmpty.addSource(decisionsStatus) { status ->
            if (status != ApiStatus.StatusKey.SUCCESS) {
                isShowDecisionsEmpty.postValue(false)
            }
        }
    }

    override fun shouldResponseBeOccupied(response: GetAllPlanningDecisionTypesResponse): Boolean {
        return response.details.isNotEmpty()
    }
}