package sg.searchhouse.agentconnect.viewmodel.activity.report.newlaunches

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import okhttp3.OkHttpClient
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.ProjectRepository
import sg.searchhouse.agentconnect.enumeration.api.ReportEnum
import sg.searchhouse.agentconnect.model.api.project.GetNewLaunchesResponse
import sg.searchhouse.agentconnect.model.api.project.GetNewLaunchesResponse.NewLaunchProject
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.service.GenerateGeneralReportService
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class NewLaunchesReportsViewModel constructor(application: Application) :
    ApiBaseViewModel<GetNewLaunchesResponse>(application) {

    @Inject
    lateinit var projectRepository: ProjectRepository

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var okHttpClient: OkHttpClient


    var page: Int = 1
    var selectedPropertyMainTypes: String? = null
    var selectedCdResearchSubTypes: String? = null
    var selectedTenure: String? = null
    var selectedCompletion: String? = null
    val reports = arrayListOf<Any>()

    val searchText = MutableLiveData<String>()
    val searchQuery = MutableLiveData<String>()
    val selectedDistrictTownNames = MutableLiveData<String>()
    val selectedDistrictIds = MutableLiveData<String>()
    val selectedHdbTownNames = MutableLiveData<String>()
    val selectedHdbTownIds = MutableLiveData<String>()
    val selectedReports = MutableLiveData<ArrayList<NewLaunchProject>>()
    val orderCriteria =
        MutableLiveData<ReportEnum.NewLaunchesReportSortType>().apply {
            value = ReportEnum.NewLaunchesReportSortType.NEW_LAUNCH_DATE_DESC
        }
    val showDefaultView = MutableLiveData<Boolean>().apply { value = false }
    val selectedProjectLabel: LiveData<String> = Transformations.map(selectedReports) {
        return@map if (it != null) {
            applicationContext.resources.getQuantityString(
                R.plurals.label_selected_report_count,
                it.size,
                NumberUtil.formatThousand(it.size)
            )
        } else {
            applicationContext.getString(R.string.label_no_project_selected)
        }
    }
    val total = MutableLiveData<Int>()
    val getNewLaunchesCountResponse = MutableLiveData<ApiStatus<GetNewLaunchesResponse>>()
    val showCloseBtn = MutableLiveData<Boolean>()
    val hintText = MutableLiveData<String>()

    init {
        viewModelComponent.inject(this)
        hintText.value = applicationContext.getString(R.string.label_enter_project_name_or_location)
        showCloseBtn.value = false
        initializePropertySubTypes()
    }

    private fun initializePropertySubTypes() {
        //TODO: if pass null means must return all values but backend keep returning error when nothing is passed.
        if (selectedCdResearchSubTypes == null) {
            selectedCdResearchSubTypes = ""
            ReportEnum.NewLaunchesPropertyType.values().map { type ->
                selectedCdResearchSubTypes += type.propertySubType.map { it.type }
                    .joinToString(",") + ","
            }
        }
    }

    private fun getNewLaunches() {
        page = 1
        performRequest(
            projectRepository.getNewLaunches(
                page = page,
                countOnly = false,
                property = orderCriteria.value?.property,
                order = orderCriteria.value?.order,
                cdResearchSubtypes = selectedCdResearchSubTypes,
                tenure = selectedTenure,
                completion = selectedCompletion,
                searchText = searchQuery.value,
                districts = selectedDistrictIds.value,
                hdbTownships = selectedHdbTownIds.value
            )
        )
    }

    fun performNewLaunchesRequest() {
        getNewLaunchesCountOnly()
    }

    private fun getNewLaunchesCountOnly() {
        page = 1
        ApiUtil.performRequest(
            applicationContext,
            projectRepository.getNewLaunches(
                page = page,
                countOnly = true,
                property = orderCriteria.value?.property,
                order = orderCriteria.value?.order,
                cdResearchSubtypes = selectedCdResearchSubTypes,
                tenure = selectedTenure,
                completion = selectedCompletion,
                searchText = searchQuery.value,
                districts = selectedDistrictIds.value,
                hdbTownships = selectedHdbTownIds.value
            ),
            onSuccess = {
                total.postValue(it.count)
                getNewLaunches()
            },
            onFail = {
                getNewLaunchesCountResponse.postValue(ApiStatus.failInstance(it))
            },
            onError = {
                getNewLaunchesCountResponse.postValue(ApiStatus.errorInstance())
            }
        )
    }

    fun loadMoreNewLaunchesReports() {
        performNextRequest(
            projectRepository.getNewLaunches(
                page = page,
                countOnly = false,
                property = orderCriteria.value?.property,
                order = orderCriteria.value?.order,
                cdResearchSubtypes = selectedCdResearchSubTypes,
                tenure = selectedTenure,
                completion = selectedCompletion,
                searchText = searchQuery.value,
                districts = selectedDistrictIds.value,
                hdbTownships = selectedHdbTownIds.value
            )
        )
    }

    fun downloadReport(project: NewLaunchProject) {
        GenerateGeneralReportService.launch(
            applicationContext,
            project.reportUrl,
            ReportEnum.ReportServiceType.NEW_LAUNCHES_REPORTS,
            project.name
        )
    }

    fun canLoadNext(): Boolean {
        val size = reports.filterIsInstance<NewLaunchProject>().size
        val total = total.value ?: return false
        return size < total
    }

    override fun shouldResponseBeOccupied(response: GetNewLaunchesResponse): Boolean {
        return reports.isNotEmpty()
    }
}