package sg.searchhouse.agentconnect.view.widget.listing.create

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.CardPostListingFacilitiesBinding
import sg.searchhouse.agentconnect.model.api.lookup.LookupListingFeaturesFixturesAreasResponse.Data
import sg.searchhouse.agentconnect.view.adapter.listing.create.SpecialFeaturesGridAdapter
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.create.PostListingViewModel

class PostListingFacilitiesCard(context: Context, attributeSet: AttributeSet? = null) :
    LinearLayout(context, attributeSet) {

    var viewModel: PostListingViewModel? = null

    val binding: CardPostListingFacilitiesBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.card_post_listing_facilities,
        this,
        true
    )

    //Features List
    private var featuresList = emptyList<Any>()
    private val featuresAdapter = SpecialFeaturesGridAdapter(onSelectItem = {
        updateEachItem(item = it, type = Type.FEATURE)
    }, onSelectAllByEachCategory = { isSelectAll ->
        updateEachSection(isSelectAll = isSelectAll, type = Type.FEATURE)
    })

    //Fixtures List
    private var fixturesList = emptyList<Any>()
    private val fixturesAdapter = SpecialFeaturesGridAdapter(onSelectItem = {
        updateEachItem(item = it, type = Type.FIXTURE)
    }, onSelectAllByEachCategory = { isSelectAll ->
        updateEachSection(isSelectAll = isSelectAll, type = Type.FIXTURE)
    })

    //Area List
    private var areasList = emptyList<Any>()
    private val areasAdapter = SpecialFeaturesGridAdapter(onSelectItem = {
        updateEachItem(item = it, type = Type.AREA)
    }, onSelectAllByEachCategory = { isSelectAll ->
        updateEachSection(isSelectAll = isSelectAll, type = Type.AREA)
    })

    init {
        setupAdapterAndLayoutManager()
    }

    private fun setupAdapterAndLayoutManager() {
        //Features
        val layoutManager = GridLayoutManager(context, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (featuresAdapter.items[position]) {
                    is String -> 2
                    is Data -> 1
                    else -> 2
                }
            }
        }
        binding.listFeatures.layoutManager = layoutManager
        binding.listFeatures.adapter = featuresAdapter

        //Fixtures
        val fixtureLayoutManager = GridLayoutManager(context, 2)
        fixtureLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (fixturesAdapter.items[position]) {
                    is String -> 2
                    is Data -> 1
                    else -> 2
                }
            }
        }
        binding.listFixtures.layoutManager = fixtureLayoutManager
        binding.listFixtures.adapter = fixturesAdapter

        //Areas
        val areaLayoutManager = GridLayoutManager(context, 2)
        areaLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (areasAdapter.items[position]) {
                    is String -> 2
                    is Data -> 1
                    else -> 2
                }
            }
        }
        binding.listAreas.layoutManager = areaLayoutManager
        binding.listAreas.adapter = areasAdapter
    }

    fun populateFacilities(
        features: List<Data>?,
        fixtures: List<Data>?,
        areas: List<Data>?
    ) {
        if (features?.isNotEmpty() == true) {
            featuresList =
                listOf(context.resources.getString(R.string.label_special_features)) + features
            featuresAdapter.items = featuresList
            featuresAdapter.notifyDataSetChanged()
        }

        if (fixtures?.isNotEmpty() == true) {
            fixturesList =
                listOf(context.resources.getString(R.string.label_fixtures_and_fittings)) + fixtures
            fixturesAdapter.items = fixturesList
            fixturesAdapter.notifyDataSetChanged()
        }

        if (areas?.isNotEmpty() == true) {
            areasList =
                listOf(context.resources.getString(R.string.label_outdoor_indoor_area)) + areas
            areasAdapter.items = areasList
            areasAdapter.notifyDataSetChanged()
        }

        binding.isListOccupied = true
    }

    fun populateSelectedItems(features: List<String>, fixtures: List<String>, areas: List<String>) {
        //Id string from Data object
        if (features.isNotEmpty()) {
            featuresAdapter.selectedItems = features
            featuresAdapter.notifyDataSetChanged()
        }

        if (fixtures.isNotEmpty()) {
            fixturesAdapter.selectedItems = fixtures
            fixturesAdapter.notifyDataSetChanged()
        }

        if (areas.isNotEmpty()) {
            areasAdapter.selectedItems = areas
            areasAdapter.notifyDataSetChanged()
        }
    }

    private fun updateEachItem(item: Data, type: Type) {
        when (type) {
            Type.FEATURE -> {
                val editPO = viewModel?.listingEditPO?.value ?: return
                if (editPO.features.contains(item.id)) {
                    editPO.features.remove(item.id)
                } else {
                    editPO.features.add(item.id)
                }

                featuresAdapter.selectedItems = editPO.features
                featuresAdapter.notifyDataSetChanged()
                viewModel?.updateListingEditPO()
            }
            Type.FIXTURE -> {
                val editPO = viewModel?.listingEditPO?.value ?: return
                if (editPO.fixtures.contains(item.id)) {
                    editPO.fixtures.remove(item.id)
                } else {
                    editPO.fixtures.add(item.id)
                }

                fixturesAdapter.selectedItems = editPO.fixtures
                fixturesAdapter.notifyDataSetChanged()
                viewModel?.updateListingEditPO()
            }
            Type.AREA -> {
                val editPO = viewModel?.listingEditPO?.value ?: return
                if (editPO.area.contains(item.id)) {
                    editPO.area.remove(item.id)
                } else {
                    editPO.area.add(item.id)
                }

                areasAdapter.selectedItems = editPO.area
                areasAdapter.notifyDataSetChanged()
                viewModel?.updateListingEditPO()
            }
        }
    }

    private fun updateEachSection(isSelectAll: Boolean, type: Type) {
        when (type) {
            Type.FEATURE -> {
                val editPO = viewModel?.listingEditPO?.value ?: return
                editPO.features.clear()

                if (isSelectAll) {
                    val allFeatures = featuresList.filterIsInstance<Data>().map { it.id }
                    editPO.features.addAll(allFeatures)
                }

                featuresAdapter.selectedItems = editPO.features
                featuresAdapter.notifyDataSetChanged()
                viewModel?.updateListingEditPO()
            }
            Type.FIXTURE -> {
                val editPO = viewModel?.listingEditPO?.value ?: return
                editPO.fixtures.clear()

                if (isSelectAll) {
                    val allFixtures = fixturesList.filterIsInstance<Data>().map { it.id }
                    editPO.fixtures.addAll(allFixtures)
                }

                fixturesAdapter.selectedItems = editPO.fixtures
                fixturesAdapter.notifyDataSetChanged()
                viewModel?.updateListingEditPO()
            }
            Type.AREA -> {
                val editPO = viewModel?.listingEditPO?.value ?: return
                editPO.area.clear()

                if (isSelectAll) {
                    val allAreas = areasList.filterIsInstance<Data>().map { it.id }
                    editPO.area.addAll(allAreas)
                }

                areasAdapter.selectedItems = editPO.area
                areasAdapter.notifyDataSetChanged()
                viewModel?.updateListingEditPO()
            }
        }
    }

    enum class Type {
        FEATURE,
        FIXTURE,
        AREA
    }
}