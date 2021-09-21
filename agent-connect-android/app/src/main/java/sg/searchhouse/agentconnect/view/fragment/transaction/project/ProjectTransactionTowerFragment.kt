package sg.searchhouse.agentconnect.view.fragment.transaction.project

import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_project_transaction_tower.*
import kotlinx.android.synthetic.main.layout_transaction_tower_unit_title.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentProjectTransactionTowerBinding
import sg.searchhouse.agentconnect.databinding.GridItemTransactionTowerUnitTitleBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.model.api.transaction.TowerViewForLastSoldTransactionResponse
import sg.searchhouse.agentconnect.event.transaction.LockFragmentScrollEvent
import sg.searchhouse.agentconnect.event.transaction.UnlockActivityScrollEvent
import sg.searchhouse.agentconnect.view.adapter.transaction.project.ProjectTransactionTowerBlockAdapter
import sg.searchhouse.agentconnect.view.adapter.transaction.project.ProjectTransactionTowerFloorAdapter
import sg.searchhouse.agentconnect.view.adapter.transaction.project.ProjectTransactionTowerUnitAdapter
import sg.searchhouse.agentconnect.view.fragment.base.BaseFragment
import sg.searchhouse.agentconnect.view.fragment.transaction.base.TransactionCaveatsDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.transaction.TransactionProjectTowerViewModel

class ProjectTransactionTowerFragment : BaseFragment() {

    companion object {
        private const val EXTRA_KEY_PROJECT_ID = "EXTRA_KEY_PROJECT_ID"

        fun newInstance(projectId: Int): ProjectTransactionTowerFragment {
            val fragment =
                ProjectTransactionTowerFragment()
            val bundle = Bundle()
            bundle.putInt(EXTRA_KEY_PROJECT_ID, projectId)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentProjectTransactionTowerBinding
    private lateinit var viewModel: TransactionProjectTowerViewModel

    private val unitAdapter = ProjectTransactionTowerUnitAdapter { unit ->
        showCaveatsDialog(unit)
    }

    private val blockAdapter = ProjectTransactionTowerBlockAdapter { block ->
        viewModel.selectedBlock.postValue(block)
    }
    private val floorAdapter = ProjectTransactionTowerFloorAdapter()

    // To decide which is the active list of scrolling
    private var currentList: ListSelection? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.fragment_project_transaction_tower,
            container,
            false
        )
        viewModel = ViewModelProvider(this).get(TransactionProjectTowerViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBlockList()
        setupFloorList()
        observeLiveData()
        setupParamFromExtras()
        listenRxBuses()
        setListeners()
    }

    private fun setupBlockList() {
        list_block.layoutManager = getBlockLayoutManager()
        list_block.adapter = blockAdapter
    }

    private fun getBlockLayoutManager(): LinearLayoutManager {
        val layoutManager = LinearLayoutManager(this.context)
        when (resources.configuration.orientation) {
            ORIENTATION_PORTRAIT -> {
                layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            }
            ORIENTATION_LANDSCAPE -> {
                layoutManager.orientation = LinearLayoutManager.VERTICAL
            }
        }
        return layoutManager
    }

    private fun setListeners() {
        setOnScrollListeners()
        setOnClickListeners()
    }

    private fun setOnScrollListeners() {
        list_unit.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (currentList == ListSelection.LIST_UNIT) {
                    list_floor.scrollBy(0, dy)
                }
            }
        })
        list_floor.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (currentList == ListSelection.LIST_FLOOR) {
                    list_unit.scrollBy(0, dy)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        scroll_view_unit.scrollX = 0
    }

    private fun setupFloorList() {
        list_floor.layoutManager = LinearLayoutManager(activity)
        list_floor.adapter = floorAdapter
    }

    private fun setOnClickListeners() {
        list_unit.setOnTouchListener { _, _ ->
            if (currentList != ListSelection.LIST_UNIT) {
                currentList = ListSelection.LIST_UNIT
            }
            false
        }
        list_floor.setOnTouchListener { _, _ ->
            if (currentList != ListSelection.LIST_FLOOR) {
                currentList = ListSelection.LIST_FLOOR
            }
            false
        }
        layout_root.setOnClickListener { publishRxBus(UnlockActivityScrollEvent()) }
    }

    private fun listenRxBuses() {
        listenRxBus(LockFragmentScrollEvent::class.java) {
            list_unit.isNestedScrollingEnabled = !it.isLock
            list_floor.isNestedScrollingEnabled = !it.isLock
        }
    }

    private fun showCaveatsDialog(unit: TowerViewForLastSoldTransactionResponse.UnitsItem) {
        if (unit.getCaveatCount() > 0) {
            TransactionCaveatsDialogFragment.newInstance(unit)
                .show(
                    childFragmentManager,
                    TransactionCaveatsDialogFragment.TAG
                )
        }
    }

    private fun setupParamFromExtras() {
        val projectId = arguments?.getInt(EXTRA_KEY_PROJECT_ID, -1)
        require(projectId != -1) { "Invalid or missing project ID" }
        viewModel.projectId.postValue(projectId)
    }

    private fun observeLiveData() {
        viewModel.projectId.observeNotNull(viewLifecycleOwner) {
            viewModel.performRequest(it)
        }

        viewModel.blocks.observe(viewLifecycleOwner) { blocks ->
            blockAdapter.items = blocks ?: emptyList()
            blockAdapter.notifyDataSetChanged()
            if (blocks != null && blocks.isNotEmpty()) {
                viewModel.selectedBlock.postValue(blocks.getOrNull(0))
            }
        }

        viewModel.selectedBlock.observe(viewLifecycleOwner) {
            blockAdapter.selectedItem = it
            blockAdapter.notifyDataSetChanged()
        }

        viewModel.selectedFloors.observeNotNull(viewLifecycleOwner) { floors ->
            populateUnits(floors)
            populateFloors(floors)
        }

        viewModel.normalisedUnitNumbers.observeNotNull(viewLifecycleOwner) { unitNumbers ->
            populateHeader(unitNumbers)
            setupUnitList(unitNumbers)
        }
    }

    private fun populateFloors(floors: List<Pair<String, List<Any>>>) {
        floorAdapter.items = floors.map { floorUnits ->
            ProjectTransactionTowerFloorAdapter.TowerFloor(floorUnits.first)
        }
        floorAdapter.notifyDataSetChanged()
        list_floor.scrollToPosition(0)
    }

    private fun setupUnitList(unitNumbers: List<String>) {
        val floorLayoutManager = GridLayoutManager(activity, unitNumbers.size)
        list_unit.layoutManager = floorLayoutManager
        list_unit.adapter = unitAdapter
    }

    private fun populateUnits(floors: List<Pair<String, List<Any>>>) {
        val floorUnits = floors.map { floorUnits -> floorUnits.second }.flatten()
        unitAdapter.items = floorUnits
        unitAdapter.notifyDataSetChanged()
        list_unit.scrollTo(0, 0)
        layout_swipe_refresh.setOnRefreshListener {
            publishRxBus(UnlockActivityScrollEvent())
            layout_swipe_refresh.isRefreshing = false
        }
    }

    private fun populateHeader(unitNumbers: List<String>) {
        layout_container.removeAllViews()
        unitNumbers.map {
            val binding = DataBindingUtil.inflate<GridItemTransactionTowerUnitTitleBinding>(
                LayoutInflater.from(
                    activity
                ), R.layout.grid_item_transaction_tower_unit_title, layout_container, false
            )
            binding.unitNumber = it
            layout_container.addView(binding.root)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        list_block.layoutManager = getBlockLayoutManager()
    }

    enum class ListSelection {
        LIST_FLOOR, LIST_UNIT
    }
}