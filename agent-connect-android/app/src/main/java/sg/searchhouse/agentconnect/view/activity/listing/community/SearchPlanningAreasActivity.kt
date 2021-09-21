package sg.searchhouse.agentconnect.view.activity.listing.community

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.WorkerThread
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_search_planning_areas.*
import kotlinx.android.synthetic.main.edit_text_search_white.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivitySearchPlanningAreasBinding
import sg.searchhouse.agentconnect.dsl.getIntList
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.toQueryText
import sg.searchhouse.agentconnect.dsl.widget.postDelayed
import sg.searchhouse.agentconnect.dsl.widget.requestFocusAndShowKeyboard
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutManager
import sg.searchhouse.agentconnect.model.app.QuerySubZone
import sg.searchhouse.agentconnect.event.community.ScrollToExpandedPlanningAreaEvent
import sg.searchhouse.agentconnect.event.community.UpdateCommunityListEvent
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.community.PlanningAreaAdapter
import sg.searchhouse.agentconnect.view.adapter.community.SubZoneQueryAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.listing.community.SearchPlanningAreasViewModel

class SearchPlanningAreasActivity :
    ViewModelActivity<SearchPlanningAreasViewModel, ActivitySearchPlanningAreasBinding>() {
    private val mainAdapter = PlanningAreaAdapter()
    private val queryAdapter = SubZoneQueryAdapter()

    private lateinit var initialCommunityIds: List<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtras()
        setupViews()
        observeLiveData()
        listenRxBuses()
    }

    private fun setupExtras() {
        initialCommunityIds =
            intent.getStringExtra(EXTRA_INPUT_COMMUNITY_IDS)?.getIntList() ?: emptyList()
    }

    private fun listenRxBuses() {
        listenRxBus(UpdateCommunityListEvent::class.java) {
            // NOTE: Order sensitive
            // The `selectedItems` of `queryAdapter` depends on the `selectedItems` of `mainAdapter`
            // because "select all" module of `mainAdapter`
            mainAdapter.updatePlanningAreaSubZone(it.planningAreaSubZone)
            queryAdapter.selectedItems = mainAdapter.selectedItems
            queryAdapter.notifyDataSetChanged()
        }
        listenRxBus(ScrollToExpandedPlanningAreaEvent::class.java) {
            scrollToExpandedPlanningArea(it.planningAreaId)
        }
    }

    /**
     * Assumption: All items equal height
     */
    private fun scrollToExpandedPlanningArea(expandedPlanningAreaId: Int) {
        list_empty_query.postDelayed {
            val position = mainAdapter.getPositionByPlanningAreaId(expandedPlanningAreaId)
            val firstItem = list_empty_query.getChildAt(0) ?: return@postDelayed
            val itemHeight = firstItem.height
            scroll_view.smoothScrollTo(0, position * itemHeight)
        }
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observeNotNull(this) {
            mainAdapter.items = it.communities
            mainAdapter.selectItems(initialCommunityIds)
            mainAdapter.notifyDataSetChanged()
        }
        viewModel.query.observeNotNull(this) {
            if (it.isNotEmpty()) {
                populateQueryList(it)
            }
        }
        viewModel.querySubZones.observeNotNull(this) {
            queryAdapter.items = it
            queryAdapter.notifyDataSetChanged()
        }
    }

    private fun populateQueryList(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.querySubZones.postValue(getQuerySubZones(query))
        }
    }

    @WorkerThread
    private fun getQuerySubZones(query: String): List<QuerySubZone> {
        val allSubZones = viewModel.allSubZones.value ?: emptyList()
        return allSubZones.filter {
            it.getNameQueryText().contains(query.toQueryText())
        }
    }

    private fun setupViews() {
        setupLists()
        et_search.setOnTextChangedListener { viewModel.query.postValue(it) }
        et_search.postDelayed {
            et_search.edit_text.requestFocusAndShowKeyboard()
        }
    }

    private fun setupLists() {
        list_empty_query.setupLayoutManager()
        list_empty_query.adapter = mainAdapter
        list.setupLayoutManager()
        list.adapter = queryAdapter
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_search_planning_areas
    }

    override fun getViewModelClass(): Class<SearchPlanningAreasViewModel> {
        return SearchPlanningAreasViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search_planning_areas, menu)
        return true
    }

    private fun getResultSelectedItems(): List<Int> {
        val selectedPlanningAreas =
            mainAdapter.selectedItems.filter { it.subZoneId == null }.map { it.planningAreaId }

        // Filter out subZones with its parent planning area selected, except if it is null (means `select all`)
        return mainAdapter.selectedItems.filter {
            val isPlanningAreaSelected = selectedPlanningAreas.contains(it.planningAreaId)
            val isSubZoneEmpty = it.subZoneId == null
            !isPlanningAreaSelected || isSubZoneEmpty
        }.map { it.subZoneId ?: it.planningAreaId }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_done -> {
                val data = Intent()
                data.putExtra(
                    EXTRA_OUTPUT_COMMUNITY_IDS,
                    getResultSelectedItems().joinToString(",")
                )
                setResult(RESULT_OK, data)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_OUTPUT_COMMUNITY_IDS = "EXTRA_OUTPUT_COMMUNITY_IDS"
        private const val EXTRA_INPUT_COMMUNITY_IDS = "EXTRA_INPUT_COMMUNITY_IDS"

        fun launchForResult(baseActivity: BaseActivity, communityIds: String?, requestCode: Int) {
            val extras = Bundle()
            extras.putString(EXTRA_INPUT_COMMUNITY_IDS, communityIds)
            baseActivity.launchActivityForResult(
                SearchPlanningAreasActivity::class.java,
                extras,
                requestCode
            )
        }
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }
}
