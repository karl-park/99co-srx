package sg.searchhouse.agentconnect.view.activity.project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.ExtraKey
import sg.searchhouse.agentconnect.databinding.ActivityProjectLocationSearchBinding
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.event.search.NotifyToCloseSearchPanelEvent
import sg.searchhouse.agentconnect.event.search.ShowLookupResultEvent
import sg.searchhouse.agentconnect.event.search.ShowPropertyMainTypeResultEvent
import sg.searchhouse.agentconnect.event.search.ShowQueryResultEvent
import sg.searchhouse.agentconnect.util.SearchHistoryUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.activity.searchoption.DistrictSearchActivity
import sg.searchhouse.agentconnect.view.activity.searchoption.HdbTownSearchActivity
import sg.searchhouse.agentconnect.view.fragment.project.SearchProjectsFragment
import sg.searchhouse.agentconnect.viewmodel.activity.projectinfo.ProjectSearchViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.search.SearchCommonViewModel.SearchResultType

class ProjectSearchActivity :
    ViewModelActivity<ProjectSearchViewModel, ActivityProjectLocationSearchBinding>(
        isSliding = true
    ) {

    companion object {
        const val EXTRA_KEY_SEARCH_RESULT_TYPE = "EXTRA_KEY_SEARCH_RESULT_TYPE"

        const val EXTRA_KEY_HDB_TOWN_IDS = "EXTRA_KEY_HDB_TOWN_IDS"
        const val EXTRA_KEY_HDB_TOWN_NAMES = "EXTRA_KEY_HDB_TOWN_NAMES"
        const val EXTRA_KEY_DISTRICT_IDS = "EXTRA_KEY_DISTRICT_IDS"
        const val EXTRA_KEY_DISTRICT_NAMES = "EXTRA_KEY_DISTRICT_NAMES"
        const val EXTRA_KEY_QUERY = "EXTRA_KEY_QUERY"
        const val EXTRA_KEY_PROPERTY_MAIN_TYPE = "EXTRA_KEY_PROPERTY_MAIN_TYPE"

        const val REQUEST_CODE_SEARCH_PROJECTS_BY_MRT = 1
        const val REQUEST_CODE_SEARCH_PROJECTS_BY_DISTRICT = 2
        const val REQUEST_CODE_SEARCH_PROJECTS_BY_HDB_TOWN = 3
        const val REQUEST_CODE_SEARCH_PROJECTS_BY_SCHOOL = 4

        fun launch(
            activity: Activity,
            requestCode: Int?,
            searchType: SearchResultType,
            propertyPurpose: ListingEnum.PropertyPurpose? = null
        ) {
            val intent = Intent(activity, ProjectSearchActivity::class.java)
            if (searchType == SearchResultType.PROJECTS) {
                if (propertyPurpose != null) {
                    intent.putExtra(ExtraKey.EXTRA_PROJECT_PROPERTY_PURPOSE, propertyPurpose)
                } else {
                    throw IllegalArgumentException("Search projects must provide `propertyPurpose`!")
                }
            }
            intent.putExtra(EXTRA_KEY_SEARCH_RESULT_TYPE, searchType)
            if (requestCode != null) {
                activity.startActivityForResult(intent, requestCode)
            } else {
                activity.startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupParamsFromExtra()
        observeRxBus()
        observeLiveData()
    }

    private fun setupParamsFromExtra() {
        // NOTE: Order sensitive
        intent.getSerializableExtra(ExtraKey.EXTRA_PROJECT_PROPERTY_PURPOSE)?.run {
            viewModel.propertyPurpose = this as ListingEnum.PropertyPurpose
        }

        intent.getSerializableExtra(EXTRA_KEY_SEARCH_RESULT_TYPE)?.run {
            viewModel.searchResultType.value = this as SearchResultType
        } ?: throw IllegalArgumentException("Missing Search Type")
    }

    private fun observeRxBus() {
        listenRxBus(NotifyToCloseSearchPanelEvent::class.java) {
            onBackPressed()
        }
        listenRxBus(ShowLookupResultEvent::class.java) { event ->
            launchFromLookupResult(event)
        }
        listenRxBus(ShowQueryResultEvent::class.java) { event ->
            launchByQuery(event)
        }
        listenRxBus(ShowPropertyMainTypeResultEvent::class.java) { event ->
            launchByPropertySubTypes(event)
        }
    }

    private fun observeLiveData() {
        viewModel.searchResultType.observe(this, Observer { searchResultType ->
            if (searchResultType != null) {
                addProjectSearchFragment(searchResultType)
            }
        })
    }

    private fun addProjectSearchFragment(searchResultType: SearchResultType) {
        val fragment = SearchProjectsFragment.newInstance(
            propertyPurpose = viewModel.propertyPurpose ?: ListingEnum.PropertyPurpose.RESIDENTIAL,
            searchResultType = searchResultType
        )
        supportFragmentManager.beginTransaction()
            .add(
                R.id.layout_search_location,
                fragment,
                SearchProjectsFragment.TAG_PROJECT_LOCATION_SEARCH
            )
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SEARCH_PROJECTS_BY_HDB_TOWN -> {
                val searchResultType = viewModel.searchResultType.value ?: return
                val ids =
                    data?.getStringExtra(HdbTownSearchActivity.EXTRA_SELECTED_HDB_TOWN_IDS)
                        ?: return
                val names =
                    data.getStringExtra(HdbTownSearchActivity.EXTRA_SELECTED_HDB_TOWN_NAMES)
                        ?: return
                //save
                SearchHistoryUtil.maybeAddHistory(
                    this, names, ids,
                    SearchHistoryUtil.SearchType.HDB_TOWN_ID,
                    searchResultType.sharedPreferenceKey
                )
                launchByHDBTowns(ids, names, searchResultType)
            }
            REQUEST_CODE_SEARCH_PROJECTS_BY_DISTRICT -> {
                val searchResultType = viewModel.searchResultType.value ?: return
                val districtIds =
                    data?.getStringExtra(DistrictSearchActivity.EXTRA_SELECTED_DISTRICT_IDS)
                        ?: return
                val districtNames =
                    data.getStringExtra(DistrictSearchActivity.EXTRA_SELECTED_DISTRICT_NAMES)
                        ?: return
                //save
                SearchHistoryUtil.maybeAddHistory(
                    this, districtNames, districtIds,
                    SearchHistoryUtil.SearchType.DISTRICT_ID,
                    searchResultType.sharedPreferenceKey
                )
                launchByDistricts(districtIds, districtNames, searchResultType)
            }
        }
    }

    private fun launchFromLookupResult(showLookupResultEvent: ShowLookupResultEvent) {

        val searchType = when (showLookupResultEvent.lookupType) {
            ShowLookupResultEvent.LookupType.HDB_TOWN_ID -> SearchHistoryUtil.SearchType.HDB_TOWN_ID
            ShowLookupResultEvent.LookupType.DISTRICT_ID -> SearchHistoryUtil.SearchType.DISTRICT_ID
            ShowLookupResultEvent.LookupType.AMENITY_ID -> SearchHistoryUtil.SearchType.AMENITY_ID
        }

        SearchHistoryUtil.maybeAddHistory(
            this,
            showLookupResultEvent.labels,
            showLookupResultEvent.ids,
            searchType,
            showLookupResultEvent.searchResultType.sharedPreferenceKey
        )

        when (showLookupResultEvent.lookupType) {
            ShowLookupResultEvent.LookupType.AMENITY_ID -> {
                launchByMRTQuery(showLookupResultEvent)
            }
            ShowLookupResultEvent.LookupType.HDB_TOWN_ID -> launchByHDBTowns(
                showLookupResultEvent.ids,
                showLookupResultEvent.labels,
                showLookupResultEvent.searchResultType
            )
            ShowLookupResultEvent.LookupType.DISTRICT_ID -> {
                launchByDistricts(
                    showLookupResultEvent.ids,
                    showLookupResultEvent.labels,
                    showLookupResultEvent.searchResultType
                )
            }
        }
    }

    private fun launchByHDBTowns(
        hdbTownIds: String,
        hdbTownNames: String,
        searchResultType: SearchResultType
    ) {
        val extras = Bundle()
        extras.putString(EXTRA_KEY_HDB_TOWN_IDS, hdbTownIds)
        extras.putString(EXTRA_KEY_HDB_TOWN_NAMES, hdbTownNames)
        viewModel.propertyPurpose?.run {
            extras.putSerializable(ExtraKey.EXTRA_PROJECT_PROPERTY_PURPOSE, this)
        }
        launchActivity(searchResultType.activityClass, extras)
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun launchByDistricts(
        districtIds: String,
        districtNames: String,
        searchResultType: SearchResultType
    ) {
        val extras = Bundle()
        extras.putString(EXTRA_KEY_DISTRICT_IDS, districtIds)
        extras.putString(EXTRA_KEY_DISTRICT_NAMES, districtNames)
        viewModel.propertyPurpose?.run {
            extras.putSerializable(ExtraKey.EXTRA_PROJECT_PROPERTY_PURPOSE, this)
        }
        launchActivity(searchResultType.activityClass, extras)
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun launchByMRTQuery(event: ShowLookupResultEvent) {
        val extras = Bundle()
        extras.putString(EXTRA_KEY_QUERY, event.labels)
        viewModel.propertyPurpose?.run {
            extras.putSerializable(ExtraKey.EXTRA_PROJECT_PROPERTY_PURPOSE, this)
        }
        launchActivity(event.searchResultType.activityClass, extras)
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun launchByQuery(event: ShowQueryResultEvent) {
        val query = event.query
        SearchHistoryUtil.maybeAddHistory(
            this,
            query,
            query,
            SearchHistoryUtil.SearchType.QUERY,
            event.searchResultType.sharedPreferenceKey
        )

        val extras = Bundle()
        extras.putString(EXTRA_KEY_QUERY, query)
        viewModel.propertyPurpose?.run {
            extras.putSerializable(ExtraKey.EXTRA_PROJECT_PROPERTY_PURPOSE, this)
        }
        launchActivity(event.searchResultType.activityClass, extras)
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun launchByPropertySubTypes(event: ShowPropertyMainTypeResultEvent) {
        if (!TextUtils.isEmpty(event.query)) {
            SearchHistoryUtil.maybeAddHistory(
                this,
                event.query ?: "",
                event.propertyMainType.name,
                SearchHistoryUtil.SearchType.PROPERTY_MAIN_TYPE,
                event.searchResultType.sharedPreferenceKey

            )
        }

        val extras = Bundle()
        extras.putSerializable(EXTRA_KEY_QUERY, event.query ?: "")
        extras.putSerializable(EXTRA_KEY_PROPERTY_MAIN_TYPE, event.propertyMainType)
        viewModel.propertyPurpose?.run {
            extras.putSerializable(ExtraKey.EXTRA_PROJECT_PROPERTY_PURPOSE, this)
        }
        launchActivity(event.searchResultType.activityClass, extras)
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_project_location_search
    }

    override fun getViewModelClass(): Class<ProjectSearchViewModel> {
        return ProjectSearchViewModel::class.java
    }

    override fun bindViewModelXml() {
        println("do not need to bind in xml")
    }

    override fun getToolbar(): Toolbar? {
        return null
    }
}