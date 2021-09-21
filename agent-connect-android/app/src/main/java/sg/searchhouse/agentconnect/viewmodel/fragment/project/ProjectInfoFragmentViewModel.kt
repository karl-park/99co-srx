package sg.searchhouse.agentconnect.viewmodel.fragment.project

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.data.repository.ProjectRepository
import sg.searchhouse.agentconnect.model.api.project.FloorplanPhotoPO
import sg.searchhouse.agentconnect.model.api.project.GetProjectInformationResponse
import sg.searchhouse.agentconnect.model.api.project.PhotoPO
import sg.searchhouse.agentconnect.util.StringUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class ProjectInfoFragmentViewModel(application: Application) :
    ApiBaseViewModel<GetProjectInformationResponse>(application) {
    @Inject
    lateinit var projectRepository: ProjectRepository
    @Inject
    lateinit var applicationContext: Context

    val isShowUnitDetails: LiveData<Boolean> = Transformations.map(mainResponse) {
        it?.details?.bedrooms?.isNotEmpty() == true
    }

    val isShowFixtures: LiveData<Boolean> = Transformations.map(mainResponse) {
        it?.details?.fixturesRemarks?.isNotEmpty() == true
    }

    val isShowFacilities: LiveData<Boolean> = Transformations.map(mainResponse) {
        it?.details?.facilities?.isNotEmpty() == true
    }

    val isShowSiteMap: LiveData<Boolean> = Transformations.map(mainResponse) {
        val details = it?.details
        val hasSiteElevationMaps = details?.projectImageLinks?.any { photoPO ->
            photoPO.type == PhotoPO.Type.SITE_MAP.value || photoPO.type == PhotoPO.Type.ELEVATION_MAP.value
        } == true
        val hasFloorPlans = details?.floorplans?.isNotEmpty() == true
        hasSiteElevationMaps || hasFloorPlans
    }

    val hasSiteMaps: LiveData<Boolean> = Transformations.map(mainResponse) {
        hasType(it?.details?.projectImageLinks, PhotoPO.Type.SITE_MAP)
    }

    val hasElevationMaps: LiveData<Boolean> = Transformations.map(mainResponse) {
        hasType(it?.details?.projectImageLinks, PhotoPO.Type.ELEVATION_MAP)
    }

    val hasFloorPlans: LiveData<Boolean> = Transformations.map(mainResponse) {
        it?.details?.floorplans?.isNotEmpty() == true
    }

    val selectedFloorPlan = MutableLiveData<FloorplanPhotoPO>()

    val selectedBlock = MutableLiveData<String>()
    val selectedFloor = MutableLiveData<String>()
    val selectedUnit = MutableLiveData<String>()

    val selectedFloorPlanUrl: LiveData<String> = Transformations.map(selectedFloorPlan) {
        it?.url
    }

    private fun hasType(projectImageLinks: List<PhotoPO>?, type: PhotoPO.Type): Boolean {
        return projectImageLinks?.any { photoPO ->
            isType(photoPO, type)
        } == true
    }

    private fun isType(photoPO: PhotoPO, type: PhotoPO.Type): Boolean {
        return StringUtil.equals(
            photoPO.type,
            type.value,
            ignoreCase = true
        )
    }

    val isExpandKeyInfo = MutableLiveData<Boolean>()
    val isExpandUnitDetails = MutableLiveData<Boolean>()
    val isExpandFixtures = MutableLiveData<Boolean>()
    val isExpandFacilities = MutableLiveData<Boolean>()
    val isExpandSiteMap = MutableLiveData<Boolean>()
    val isExpandAmenityGroups = MutableLiveData<List<Boolean>>()

    init {
        viewModelComponent.inject(this)
        isExpandKeyInfo.value = true
    }

    fun performGetProjectInfo(projectId: Int) {
        performRequest(projectRepository.getProjectInformation(projectId))
    }

    override fun shouldResponseBeOccupied(response: GetProjectInformationResponse): Boolean {
        return true
    }
}