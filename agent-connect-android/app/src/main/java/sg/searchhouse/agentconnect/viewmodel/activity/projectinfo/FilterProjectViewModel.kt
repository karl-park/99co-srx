package sg.searchhouse.agentconnect.viewmodel.activity.projectinfo

import android.app.Application
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ProjectEnum
import sg.searchhouse.agentconnect.model.api.project.FilterProjectEditPO
import sg.searchhouse.agentconnect.view.activity.project.ProjectDirectoryActivity
import sg.searchhouse.agentconnect.view.widget.project.*
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel

class FilterProjectViewModel constructor(application: Application) : CoreViewModel(application) {

    var typeOfAreasList = emptyList<FilterProjectTypeOfAreaPill>()
    var completionList = emptyList<FilterProjectCompletionPill>()
    var radiusList = emptyList<FilterProjectRadiusPill>()
    var tenureList = emptyList<FilterProjectTenurePill>()
    var propertySubTypeList = emptyList<FilterProjectPropertySubTypePill>()
    var filterProject = FilterProjectEditPO()
    var prevPropertySubTypes: List<ListingEnum.PropertySubType>? = null

    val minAge = MutableLiveData<Int>()
    val maxAge = MutableLiveData<Int>()
    val showRadiusOption = MutableLiveData<Boolean>()
    val prevFilterProject = MutableLiveData<FilterProjectEditPO>()
    val propertySubTypes = MutableLiveData<List<ListingEnum.PropertySubType>>()
    var projectSearchType = MutableLiveData<ProjectDirectoryActivity.ProjectSearchType>()

    val selectedTypeOfArea: MutableLiveData<ProjectEnum.TypeOfArea> by lazy {
        MutableLiveData<ProjectEnum.TypeOfArea>().apply { value = ProjectEnum.TypeOfArea.ANY }
    }
    val selectedCompletion: MutableLiveData<ProjectEnum.Completion> by lazy {
        MutableLiveData<ProjectEnum.Completion>().apply { value = ProjectEnum.Completion.ANY }
    }
    val selectedRadius: MutableLiveData<ProjectEnum.ProjectRadius> by lazy {
        MutableLiveData<ProjectEnum.ProjectRadius>().apply { value = ProjectEnum.ProjectRadius.ANY }
    }
    val selectedTenure: MutableLiveData<ProjectEnum.Tenure> by lazy {
        MutableLiveData<ProjectEnum.Tenure>().apply { value = ProjectEnum.Tenure.ANY }
    }
    var propertyPurpose = MutableLiveData<ListingEnum.PropertyPurpose>().apply {
        value = ListingEnum.PropertyPurpose.RESIDENTIAL
    }
    val propertyMainType = MutableLiveData<ListingEnum.PropertyMainType>().apply {
        value = ListingEnum.PropertyMainType.RESIDENTIAL
    }

    init {
        viewModelComponent.inject(this)
    }

    //TODO: note : currently multi select is only property types -> the rest is single select
    fun updatePropertySubTypes(subType: ListingEnum.PropertySubType) {
        val list = propertySubTypes.value ?: emptyList()
        if (list.contains(subType)) {
            propertySubTypes.postValue(list - subType)
        } else {
            propertySubTypes.postValue(list + subType)
        }
    }

    fun hasTypeOfArea(area: ProjectEnum.TypeOfArea): Boolean {
        val typeOfArea = selectedTypeOfArea.value ?: return false
        return area == typeOfArea
    }

    fun hasCompletion(item: ProjectEnum.Completion): Boolean {
        val completion = selectedCompletion.value ?: return false
        return completion == item
    }

    fun isRadiusSelected(radius: ProjectEnum.ProjectRadius): Boolean {
        val tempRadius = selectedRadius.value ?: return false
        return tempRadius == radius
    }

    fun isTenureSelected(tenure: ProjectEnum.Tenure): Boolean {
        val tempTenure = selectedTenure.value ?: return false
        return tempTenure == tenure
    }

    fun hasPropertySubType(subType: ListingEnum.PropertySubType): Boolean {
        return propertySubTypes.value?.any { it == subType } == true
    }
}