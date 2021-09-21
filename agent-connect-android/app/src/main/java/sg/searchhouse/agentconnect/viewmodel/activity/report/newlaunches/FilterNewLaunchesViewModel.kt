package sg.searchhouse.agentconnect.viewmodel.activity.report.newlaunches

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.ProjectRepository
import sg.searchhouse.agentconnect.enumeration.api.ReportEnum
import sg.searchhouse.agentconnect.model.api.project.GetNewLaunchesResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.view.widget.report.FilterNewLaunchesCompletionPill
import sg.searchhouse.agentconnect.view.widget.report.FilterNewLaunchesPropertyTypePill
import sg.searchhouse.agentconnect.view.widget.report.FilterNewLaunchesTenurePill
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class FilterNewLaunchesViewModel constructor(application: Application) :
    CoreViewModel(application) {

    @Inject
    lateinit var projectRepository: ProjectRepository

    @Inject
    lateinit var applicationContext: Context

    var searchText: String? = null
    var districtTowns: String? = null
    var hdbTowns: String? = null
    var propertySubTypeList = emptyList<FilterNewLaunchesPropertyTypePill>()
    var tenureList = emptyList<FilterNewLaunchesTenurePill>()
    var completionList = emptyList<FilterNewLaunchesCompletionPill>()

    val propertyTypes = MutableLiveData<List<ReportEnum.NewLaunchesPropertyType>>()
    val previousPropertySubTypes = MutableLiveData<List<ReportEnum.NewLaunchesPropertyType>>()
    val selectedTenure =
        MutableLiveData<ReportEnum.NewLaunchesTenure>().apply {
            value = ReportEnum.NewLaunchesTenure.ALL
        }
    val selectedCompletion =
        MutableLiveData<ReportEnum.NewLaunchesCompletion>().apply {
            value = ReportEnum.NewLaunchesCompletion.ALL
        }
    val newLaunchesCountStatus = MutableLiveData<ApiStatus<GetNewLaunchesResponse>>()
    val totalProjectCount = MutableLiveData<String>()
    val isDisplayCount = MutableLiveData<Boolean>()

    init {
        viewModelComponent.inject(this)
    }

    private fun getNewLaunchesCountOnly() {
        isDisplayCount.postValue(false)
        ApiUtil.performRequest(
            applicationContext,
            projectRepository.getNewLaunches(
                page = 1,
                countOnly = true,
                property = null,
                order = null,
                cdResearchSubtypes = getCdResearchSubTypesFromTypes(),
                tenure = getTenure(),
                completion = getCompletion(),
                searchText = searchText,
                districts = districtTowns,
                hdbTownships = hdbTowns
            ),
            onSuccess = {
                when (it.count) {
                    0 -> {
                        totalProjectCount.postValue(applicationContext.resources.getString(R.string.label_no_project))
                    }
                    else -> {
                        totalProjectCount.postValue(
                            applicationContext.resources.getQuantityString(
                                R.plurals.label_projects_count,
                                it.count,
                                NumberUtil.formatThousand(it.count)
                            )
                        )
                    }
                }

                isDisplayCount.postValue(true)
            },
            onFail = {
                isDisplayCount.postValue(true)
                newLaunchesCountStatus.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                isDisplayCount.postValue(true)
                newLaunchesCountStatus.postValue(ApiStatus.errorInstance())
            }
        )
    }

    fun updatePropertySubTypes(subType: ReportEnum.NewLaunchesPropertyType) {
        val list = propertyTypes.value ?: emptyList()
        if (list.contains(subType)) {
            propertyTypes.postValue(list - subType)
        } else {
            propertyTypes.postValue(list + subType)
        }
    }

    fun hasPropertySubType(subType: ReportEnum.NewLaunchesPropertyType): Boolean {
        return propertyTypes.value?.any { it == subType } == true
    }

    fun isTenureSelected(tenure: ReportEnum.NewLaunchesTenure): Boolean {
        val tempTenure = selectedTenure.value ?: return false
        return tempTenure == tenure
    }

    fun hasCompletion(item: ReportEnum.NewLaunchesCompletion): Boolean {
        val completion = selectedCompletion.value ?: return false
        return completion == item
    }

    fun getCdResearchSubTypesFromTypes(): String {
        return propertyTypes.value?.joinToString(",") { type ->
            type.propertySubType.map { it.type }.joinToString(",")
        } ?: ""
    }

    fun getTenure(): String? {
        return selectedTenure.value?.run {
            return@run if (this == ReportEnum.NewLaunchesTenure.ALL) {
                null
            } else {
                this.value
            }
        }
    }

    fun getCompletion(): String? {
        return selectedCompletion.value?.run {
            return@run if (this == ReportEnum.NewLaunchesCompletion.ALL) {
                null
            } else {
                this.value
            }
        }
    }
}