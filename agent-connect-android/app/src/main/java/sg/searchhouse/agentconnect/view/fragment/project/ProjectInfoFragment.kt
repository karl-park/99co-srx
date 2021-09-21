package sg.searchhouse.agentconnect.view.fragment.project

import android.animation.LayoutTransition
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_project_info.*
import kotlinx.android.synthetic.main.layout_header_project_info.view.layout_header
import kotlinx.android.synthetic.main.layout_project_info_section.view.list
import kotlinx.android.synthetic.main.layout_project_info_section_main.view.*
import kotlinx.android.synthetic.main.layout_project_info_section_site_map.*
import kotlinx.android.synthetic.main.layout_project_info_section_site_map.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentProjectInfoBinding
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import sg.searchhouse.agentconnect.dsl.widget.setupLink
import sg.searchhouse.agentconnect.model.api.common.NameValuePO
import sg.searchhouse.agentconnect.model.api.listing.AmenitiesGroupPO
import sg.searchhouse.agentconnect.model.api.listing.AmenitiesPO
import sg.searchhouse.agentconnect.model.api.project.FloorplanPhotoPO
import sg.searchhouse.agentconnect.model.api.project.PhotoPO
import sg.searchhouse.agentconnect.model.api.project.SRXProjectBedroomPO
import sg.searchhouse.agentconnect.model.api.project.SRXProjectDetailsPO
import sg.searchhouse.agentconnect.model.app.ProjectKeyInfo
import sg.searchhouse.agentconnect.event.project.NotifyProjectInfoToActivityEvent
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.activity.common.ImageActivity
import sg.searchhouse.agentconnect.view.activity.project.ProjectInfoActivity
import sg.searchhouse.agentconnect.view.activity.project.SiteMapMediaActivity
import sg.searchhouse.agentconnect.view.adapter.project.*
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.view.helper.search.SearchResultActivityEntry
import sg.searchhouse.agentconnect.view.helper.search.TransactionsActivityEntry
import sg.searchhouse.agentconnect.view.widget.project.ProjectInfoSectionLayout
import sg.searchhouse.agentconnect.viewmodel.fragment.project.ProjectInfoFragmentViewModel

class ProjectInfoFragment :
    ViewModelFragment<ProjectInfoFragmentViewModel, FragmentProjectInfoBinding>() {
    private val bedroomUnitAdapter = ProjectInfoBedroomUnitAdapter()
    private val projectKeyInfoAdapter = ProjectKeyInfoAdapter()
    private val fixtureAdapter = ProjectInfoFixtureAdapter()
    private val facilityAdapter = ProjectInfoFacilityAdapter()
    private val siteMapAdapter = ProjectInfoSiteMapAdapter { items, position ->
        val activity = activity as BaseActivity? ?: return@ProjectInfoSiteMapAdapter
        SiteMapMediaActivity.launch(activity, items, position)
    }
    private val elevationMapAdapter = ProjectInfoSiteMapAdapter { items, position ->
        val activity = activity as BaseActivity? ?: return@ProjectInfoSiteMapAdapter
        SiteMapMediaActivity.launch(activity, items, position)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
        setupViews()
        performGetProjectInfo()
    }

    private fun setupViews() {
        setupLayoutTransition()
        setupDisclaimerText()
        setupSections()
        setupOnClickListeners()
        setupButtonStates()
    }

    // Animate movement of other cards when one card height changes
    private fun setupLayoutTransition() {
        val layoutTransition = LayoutTransition()
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        layout_container.layoutTransition = layoutTransition
    }

    private fun setupButtonStates() {
        val isLimitTransactionStack =
            activity?.intent?.getBooleanExtra(
                ProjectInfoActivity.EXTRA_IS_LIMIT_TRANSACTIONS_STACK,
                false
            )
                ?: false
        layout_project_info_key_info.btn_view_all_transactions.run {
            isEnabled = !isLimitTransactionStack
        }
    }

    private fun setupOnClickListeners() {
        layout_project_info_key_info.btn_view_all_listings.setOnClickListener {
            val baseActivity = activity as BaseActivity? ?: return@setOnClickListener
            val project = viewModel.mainResponse.value?.details ?: return@setOnClickListener
            SearchResultActivityEntry.launchListingsBySearchText(
                baseActivity,
                project.name,
                PropertyTypeUtil.getPropertyPurpose(project.propertySubtype),
                isFinishOrigin = false
            )
        }
        layout_project_info_key_info.btn_view_all_transactions.setOnClickListener {
            val baseActivity = activity as BaseActivity? ?: return@setOnClickListener
            val project = viewModel.mainResponse.value?.details ?: return@setOnClickListener
            TransactionsActivityEntry.launchByProject(
                baseActivity,
                project.id,
                project.name,
                isShowTower = PropertyTypeUtil.isShowTransactionsTower(project.propertySubtype),
                propertySubType = project.getPropertySubTypeObject(),
                propertyPurpose = PropertyTypeUtil.getPropertyPurpose(project.propertySubtype)
            )
        }
    }

    private fun setupDisclaimerText() {
        tv_disclaimer.setupLink(
            R.string.msg_project_info_disclaimer_collapsed,
            R.string.link_button_project_info_disclaimer
        ) {
            tv_disclaimer.text = context?.getString(R.string.msg_project_info_disclaimer)
        }
    }

    private fun setupSections() {
        setupSection(layout_project_info_key_info, viewModel.isExpandKeyInfo, projectKeyInfoAdapter)
        setupSection(
            layout_project_info_unit_details,
            viewModel.isExpandUnitDetails,
            bedroomUnitAdapter
        )
        setupSection(
            layout_project_info_fixture_details,
            viewModel.isExpandFixtures,
            fixtureAdapter
        )
        setupFacilitySection()
        setupSiteMapSection()
    }

    private fun setupSiteMapSection() {
        layout_project_info_site_map.layout_header.setOnClickListener {
            LiveDataUtil.toggleBoolean(viewModel.isExpandSiteMap)
        }
        setupList(
            layout_project_info_site_map.list_site_map,
            siteMapAdapter,
            RecyclerView.HORIZONTAL
        )
        setupList(
            layout_project_info_site_map.list_elevation_map,
            elevationMapAdapter,
            RecyclerView.HORIZONTAL
        )
    }

    private fun setupSection(
        projectInfoDefaultLayout: View,
        isExpandLiveData: MutableLiveData<Boolean>,
        adapter: RecyclerView.Adapter<*>
    ) {
        projectInfoDefaultLayout.layout_header.setOnClickListener {
            LiveDataUtil.toggleBoolean(isExpandLiveData)
        }
        setupList(projectInfoDefaultLayout.list, adapter)
    }

    private fun setupFacilitySection() {
        layout_project_info_facilities.layout_header.setOnClickListener {
            LiveDataUtil.toggleBoolean(viewModel.isExpandFacilities)
        }
        val context = activity ?: return

        layout_project_info_facilities.list.layoutManager = GridLayoutManager(context, 2)
        layout_project_info_facilities.list.adapter = facilityAdapter
    }

    private fun setupList(
        list: RecyclerView,
        adapter: RecyclerView.Adapter<*>,
        orientation: Int = RecyclerView.VERTICAL
    ) {
        list.layoutManager = createLayoutManager(orientation)
        list.adapter = adapter
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                val details = response.details
                val position = LatLng(details.latitude, details.longitude)
                publishRxBus(
                    NotifyProjectInfoToActivityEvent(
                        details.name,
                        position,
                        details.postalCode
                    )
                    , onResumeOnly = false
                )
                populateKeyInfo(details)

                val isHdb = PropertyTypeUtil.isHDB(details.propertySubtype)
                populateUnitDetails(details.bedrooms, isHdb)

                populateFixtureDetails(details.fixturesRemarks)
                populateFacilities(details.facilities)
                populateSiteMaps(details.projectImageLinks)
                populateFloorPlans(details.floorplans)
                populateAmenities(details.primaryAmenitiesGroups)
            }
        }
        viewModel.isExpandAmenityGroups.observe(viewLifecycleOwner) { isExpandAmenityGroups ->
            isExpandAmenityGroups?.mapIndexed { index, isExpand ->
                val amenityGroupLayout =
                    layout_amenity_groups.getChildAt(index) as ProjectInfoSectionLayout?
                        ?: return@observe
                amenityGroupLayout.binding.isExpand = isExpand
            }
        }
        viewModel.selectedBlock.observe(viewLifecycleOwner) { selectedBlock ->
            val floorPlans = viewModel.mainResponse.value?.details?.floorplans ?: return@observe
            val defaultFloor = floorPlans.filter { floorPlan ->
                StringUtil.equals(floorPlan.blk, selectedBlock)
            }.minBy { it.floorNo }?.floorNo
            viewModel.selectedFloor.postValue(defaultFloor)
        }
        viewModel.selectedFloor.observe(viewLifecycleOwner) { selectedFloor ->
            val floorPlans = viewModel.mainResponse.value?.details?.floorplans ?: return@observe
            val selectedBlock = viewModel.selectedBlock.value ?: return@observe
            val defaultUnit = floorPlans.filter { floorPlan ->
                StringUtil.equals(floorPlan.blk, selectedBlock)
            }.filter { floorPlan ->
                StringUtil.equals(floorPlan.floorNo, selectedFloor)
            }.minBy { it.unitNo }?.unitNo
            viewModel.selectedUnit.postValue(defaultUnit)
        }
        viewModel.selectedUnit.observe(viewLifecycleOwner) { selectedUnit ->
            val selectedBlock = viewModel.selectedBlock.value ?: return@observe
            val selectedFloor = viewModel.selectedFloor.value ?: return@observe
            val floorPlans = viewModel.mainResponse.value?.details?.floorplans ?: return@observe
            val selectedFloorPlan = floorPlans.firstOrNull {
                StringUtil.equals(it.blk, selectedBlock)
                        && StringUtil.equals(it.floorNo, selectedFloor)
                        && StringUtil.equals(it.unitNo, selectedUnit)
            }
            viewModel.selectedFloorPlan.postValue(selectedFloorPlan)
        }
    }

    private fun populateFloorPlans(floorPlans: List<FloorplanPhotoPO>) {
        if (floorPlans.isEmpty()) return
        btn_block.setOnClickListener {
            val blocks = floorPlans.groupBy { it.blk }.map { it.key }
            dialogUtil.showStringListDialog(blocks, { _, position ->
                viewModel.selectedBlock.postValue(blocks[position])
            }, R.string.title_dialog_site_map_block)
        }

        btn_floor.setOnClickListener {
            val selectedBlock = viewModel.selectedBlock.value ?: return@setOnClickListener
            val floors = floorPlans.filter {
                StringUtil.equals(it.blk, selectedBlock)
            }.groupBy { it.floorNo }.map { it.key }
            dialogUtil.showStringListDialog(floors, { _, position ->
                viewModel.selectedFloor.postValue(floors[position])
            }, R.string.title_dialog_site_map_floor)
        }

        btn_unit.setOnClickListener {
            val selectedBlock = viewModel.selectedBlock.value ?: return@setOnClickListener
            val selectedFloor = viewModel.selectedFloor.value ?: return@setOnClickListener
            val units = floorPlans.filter {
                StringUtil.equals(it.blk, selectedBlock) &&
                        StringUtil.equals(it.floorNo, selectedFloor)
            }.groupBy { it.unitNo }.map { it.key }
            dialogUtil.showStringListDialog(units, { _, position ->
                viewModel.selectedUnit.postValue(units[position])
            }, R.string.title_dialog_site_map_unit)
        }

        card_floor_plan.setOnClickListener {
            val activity = activity as BaseActivity? ?: return@setOnClickListener
            val selectedFloorPlans = viewModel.selectedFloorPlan.value ?: return@setOnClickListener
            ImageActivity.launch(
                activity,
                selectedFloorPlans.url,
                selectedFloorPlans.getBlockFloorUnit()
            )
        }

        val firstBlock = floorPlans.first().blk
        viewModel.selectedBlock.postValue(firstBlock)
    }

    private fun populateFacilities(facilities: List<NameValuePO>) {
        val even = facilities.filterIndexed { index, _ -> index % 2 == 0 }
        val odd = facilities.filterIndexed { index, _ -> index % 2 == 1 }
        facilityAdapter.items = even + odd
        facilityAdapter.notifyDataSetChanged()
    }

    private fun populateFixtureDetails(fixtures: List<NameValuePO>) {
        fixtureAdapter.items = fixtures
        fixtureAdapter.notifyDataSetChanged()
    }

    private fun populateSiteMaps(projectImageLinks: List<PhotoPO>) {
        populateSiteMapList(projectImageLinks, siteMapAdapter, PhotoPO.Type.SITE_MAP)
        populateSiteMapList(projectImageLinks, elevationMapAdapter, PhotoPO.Type.ELEVATION_MAP)
    }

    private fun populateSiteMapList(
        projectImageLinks: List<PhotoPO>,
        adapter: ProjectInfoSiteMapAdapter,
        photoPOType: PhotoPO.Type
    ) {
        adapter.items = projectImageLinks.filter {
            StringUtil.equals(
                it.type,
                photoPOType.value,
                ignoreCase = true
            )
        }
        adapter.notifyDataSetChanged()
    }

    private fun populateAmenities(primaryAmenitiesGroups: List<AmenitiesGroupPO>) {
        val context = activity ?: return
        primaryAmenitiesGroups.mapIndexed { index, amenitiesGroupPO ->
            val projectInfoAmenitiesLayout = ProjectInfoSectionLayout(context)
            val binding = projectInfoAmenitiesLayout.binding
            binding.viewModel = viewModel
            binding.title = amenitiesGroupPO.category
            binding.isExpand = false
            binding.isVisible = amenitiesGroupPO.amenities.isNotEmpty()
            binding.layoutHeader.layoutHeader.setOnClickListener {
                updateIsExpandAmenityGroup(index)
            }
            binding.layoutContainer.setupLayoutAnimation()
            populateAmenityList(binding.list, amenitiesGroupPO.amenities)
            layout_amenity_groups.addView(projectInfoAmenitiesLayout)
        }
        layout_amenity_groups.setupLayoutAnimation()
        viewModel.isExpandAmenityGroups.postValue(primaryAmenitiesGroups.map { false })
    }

    private fun updateIsExpandAmenityGroup(amenityGroupIndex: Int) {
        val isExpandAmenityGroup = viewModel.isExpandAmenityGroups.value ?: return
        val updatedIsExpandAmenityGroup =
            isExpandAmenityGroup.mapIndexed { thisIndex, isExpand ->
                when (thisIndex) {
                    amenityGroupIndex -> !isExpand
                    else -> isExpand
                }
            }
        viewModel.isExpandAmenityGroups.postValue(updatedIsExpandAmenityGroup)
    }

    private fun populateAmenityList(list: RecyclerView, amenities: List<AmenitiesPO>) {
        val adapter = ProjectInfoAmenityAdapter()
        setupList(list, adapter)
        adapter.items = amenities
        adapter.notifyDataSetChanged()
    }

    private fun createLayoutManager(orientation: Int = RecyclerView.VERTICAL): LinearLayoutManager {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = orientation
        return layoutManager
    }

    private fun populateKeyInfo(details: SRXProjectDetailsPO) {
        val address = ProjectKeyInfo(R.string.label_project_info_key_info_address, details.address)

        //Note: Hide both completed and top year for HDB.
        var completed = ProjectKeyInfo(R.string.label_project_info_key_info_completed, "")
        var top = ProjectKeyInfo(R.string.label_project_info_key_info_top, "")

        if (!PropertyTypeUtil.isHDB(details.propertySubtype)) {

            NumberUtil.toInt(details.getCompletedYear())?.let { completeYear ->

                val currentYear = DateTimeUtil.getCurrentYear()
                val isValid = (completeYear != 1800 && completeYear != 1900)
                val showTOP = completeYear > currentYear

                if (showTOP && isValid) {
                    top = ProjectKeyInfo(
                        R.string.label_project_info_key_info_top,
                        details.getDateTopYear()
                    )
                } else if (isValid) {
                    completed = ProjectKeyInfo(
                        R.string.label_project_info_key_info_completed,
                        details.getCompletedYear()
                    )
                }
            }
        }

        val district = ProjectKeyInfo(
            R.string.label_project_info_key_info_district,
            details.district.toString()
        )
        val distanceFromCity = ProjectKeyInfo(
            R.string.label_project_info_key_info_distance_from_city,
            "${details.distanceFrCity} km",
            details.distanceFrCity == 0.0
        )
        val type = ProjectKeyInfo(
            R.string.label_project_info_key_info_type,
            details.propertySubtypeDescription
        )
        val tenure = ProjectKeyInfo(R.string.label_project_info_key_info_tenure, details.tenure)
        val developer =
            ProjectKeyInfo(R.string.label_project_info_key_info_developer, details.developer)
        val plotRatio = ProjectKeyInfo(
            R.string.label_project_info_key_info_plot_ratio,
            details.plotRatio.toString(),
            details.plotRatio == 0.0
        )
        val totalUnits = ProjectKeyInfo(
            R.string.label_project_info_key_info_total_units,
            details.noOfUnits.toString(),
            details.noOfUnits == 0
        )

        val keyInfos = listOf(
            address,
            completed,
            top,
            district,
            distanceFromCity,
            type,
            tenure,
            developer,
            plotRatio,
            totalUnits
        ).filter {
            val value = it.value
            !it.isZero && !TextUtils.isEmpty(value)
                    && !value.contains("null") && !value.contains("N.A.")
        }

        projectKeyInfoAdapter.items = keyInfos
        projectKeyInfoAdapter.notifyDataSetChanged()
    }

    private fun populateUnitDetails(bedrooms: List<SRXProjectBedroomPO>, isHdb: Boolean) {
        bedroomUnitAdapter.items = bedrooms.map {
            it.isHdb = isHdb
            it
        }
        bedroomUnitAdapter.notifyDataSetChanged()
    }

    private fun performGetProjectInfo() {
        val projectId = activity?.intent?.getIntExtra(ProjectInfoActivity.EXTRA_PROJECT_ID, 0)
        if (!NumberUtil.isZeroOrNull(projectId)) {
            viewModel.performGetProjectInfo(projectId!!)
        } else {
            viewModel.mainStatus.postValue(ApiStatus.StatusKey.ERROR)
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_project_info
    }

    override fun getViewModelClass(): Class<ProjectInfoFragmentViewModel> {
        return ProjectInfoFragmentViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }

    companion object {
        fun newInstance(): ProjectInfoFragment {
            return ProjectInfoFragment()
        }
    }
}