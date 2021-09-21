package sg.searchhouse.agentconnect.viewmodel.activity.projectinfo

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.TransactionRepository
import sg.searchhouse.agentconnect.dsl.toProjectSearchMode
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ProjectEnum
import sg.searchhouse.agentconnect.model.api.project.FilterProjectEditPO
import sg.searchhouse.agentconnect.model.api.transaction.FindProjectsByKeywordResponse
import sg.searchhouse.agentconnect.model.api.transaction.TransactionSearchResultPO
import sg.searchhouse.agentconnect.view.activity.project.ProjectDirectoryActivity
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class ProjectDirectoryViewModel constructor(application: Application) :
    ApiBaseViewModel<FindProjectsByKeywordResponse>(application) {

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var transactionRepository: TransactionRepository

    var searchByOrderCriteriaIndicator: Boolean = false

    val projects = MutableLiveData<List<TransactionSearchResultPO>>()
    val source = MutableLiveData<ProjectDirectoryActivity.Source>()
    val searchQuery = MutableLiveData<String>()
    val searchText = MutableLiveData<String>()
    val hdbTownIds = MutableLiveData<String>()
    val hdbTownNames = MutableLiveData<String>()
    val districtIds = MutableLiveData<String>()
    val districtNames = MutableLiveData<String>()
    val propertyMainType = MutableLiveData<ListingEnum.PropertyMainType>()
    val isFilterEnabled = MutableLiveData<Boolean>()
    val filterEditPO = MutableLiveData<FilterProjectEditPO>()
    val projectSearchType = MutableLiveData<ProjectDirectoryActivity.ProjectSearchType>()
    val orderCriteria =
        MutableLiveData<ProjectEnum.SortType>().apply { value = ProjectEnum.SortType.NAME_ASC }
    val showDefaultView = MutableLiveData<Boolean>().apply { value = false }
    val hintText = MutableLiveData<String>()
    var propertyPurpose: ListingEnum.PropertyPurpose? = null

    init {
        viewModelComponent.inject(this)
        hintText.value =
            applicationContext.getString(R.string.label_enter_project_name_or_location)
    }

    private fun getSearchMode(): ProjectEnum.SearchMode {
        return propertyPurpose?.toProjectSearchMode() ?: ProjectEnum.SearchMode.RESIDENTIAL
    }

    fun loadProjectsByLocationSearch() {
        performRequest(
            transactionRepository.findProjects(
                searchMode = getSearchMode(),
                cdResearchSubtypes = null,
                searchText = searchQuery.value,
                districts = districtIds.value,
                hdbTowns = hdbTownIds.value,
                property = orderCriteria.value?.property,
                order = orderCriteria.value?.order
            )
        )
    }

    fun loadProjectsByFilterAndSort() {
        val projectEditPO = filterEditPO.value ?: FilterProjectEditPO()
        performRequest(
            transactionRepository.findProjects(
                searchMode = getSearchMode(),
                cdResearchSubtypes = projectEditPO.cdResearchSubtypes,
                searchText = searchQuery.value,
                districts = districtIds.value,
                hdbTowns = hdbTownIds.value,
                radius = projectEditPO.radius,
                tenureType = projectEditPO.tenureType,
                typeOfArea = projectEditPO.typeOfArea,
                age = projectEditPO.age,
                ageMax = projectEditPO.ageMax,
                property = orderCriteria.value?.property,
                order = orderCriteria.value?.order
            )
        )
    }

    override fun shouldResponseBeOccupied(response: FindProjectsByKeywordResponse): Boolean {
        return response.projects.isNotEmpty()
    }
}