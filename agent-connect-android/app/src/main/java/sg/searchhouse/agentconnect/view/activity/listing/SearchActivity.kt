package sg.searchhouse.agentconnect.view.activity.listing

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivitySearchBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum.PropertyPurpose
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.event.search.*
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.search.SearchPagerAdapter
import sg.searchhouse.agentconnect.view.helper.search.LaunchSearchResultHelper
import sg.searchhouse.agentconnect.viewmodel.activity.listing.SearchViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.search.SearchCommonViewModel

class SearchActivity : ViewModelActivity<SearchViewModel, ActivitySearchBinding>(isSliding = true) {
    private lateinit var launchSearchResultHelper: LaunchSearchResultHelper

    private var defaultSearchType: SearchType? = null
    private lateinit var defaultExpandMode: ExpandMode

    private lateinit var searchPagerAdapter: SearchPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupHelpers()
        setupViews()
        setOnClickListeners()
        observeLiveData()
        listenRxBuses()
        setupFromExtras()
    }

    private fun setupFromExtras() {
        defaultSearchType = intent.getSerializableExtra(EXTRA_SEARCH_TYPE) as SearchType?
        val defaultOwnershipType =
            intent.getSerializableExtra(EXTRA_OWNERSHIP_TYPE) as ListingEnum.OwnershipType?
        val defaultPropertyPurpose =
            intent.getSerializableExtra(EXTRA_PROPERTY_PURPOSE) as PropertyPurpose?
        //Note -> to know search activity is from which source
        defaultExpandMode =
            intent.getSerializableExtra(EXTRA_KEY_EXPAND_MODE) as ExpandMode? ?: ExpandMode.FULL

        defaultOwnershipType?.let { viewModel.ownershipType.postValue(it) }
        defaultPropertyPurpose?.let { viewModel.setPropertyPurpose(it) }
    }

    private fun setOnClickListeners() {
        binding.switchPropertyPurpose.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setPropertyPurpose(isChecked)
        }
    }

    private fun setupHelpers() {
        launchSearchResultHelper =
            LaunchSearchResultHelper(this, viewModel)
    }

    private fun setupViews() {
        searchPagerAdapter = SearchPagerAdapter(this, supportFragmentManager)
        binding.viewPager.adapter = searchPagerAdapter
        binding.ibClose.setOnClickListener { finish() }
        binding.layoutContainer.layoutTransition = LayoutTransition()
        binding.layoutContainer.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun listenRxBuses() {
        listenRxBus(NotifyToCloseSearchPanelEvent::class.java) {
            onBackPressed()
        }

        listenRxBus(CloseSearchPanelEvent::class.java) {
            viewModel.isCloseSearchPanel.postValue(it.isClose)
        }

        listenRxBus(DisplaySearchAppBarEvent::class.java) {
            viewModel.isShowAppBar.postValue(it.isShowAppBar)
        }

        listenRxBus(ShowQueryResultEvent::class.java) { event ->
            launchSearchResultHelper.launchFromQuery(
                event,
                viewModel.fragmentPropertyMainType.value
            )
        }

        listenRxBus(ShowQueryPropertySubTypeResultEvent::class.java) { event ->
            launchSearchResultHelper.launchFromQueryAndPropertySubType(
                event,
                viewModel.fragmentPropertyMainType.value
            )
        }

        listenRxBus(ShowPropertyMainTypeResultEvent::class.java) { event ->
            launchSearchResultHelper.launchFromPropertyMainType(event)
        }

        listenRxBus(ShowPropertySubTypeResultEvent::class.java) { event ->
            launchSearchResultHelper.launchFromPropertySubType(event)
        }

        listenRxBus(ShowLookupResultEvent::class.java) { event ->
            launchSearchResultHelper.launchFromLookupResult(
                event,
                viewModel.fragmentPropertyMainType.value
            )
        }

        listenRxBus(UpdateActivityOwnershipTypeEvent::class.java) { event ->
            viewModel.ownershipType.postValue(event.ownershipType)
        }

        listenRxBus(RequestOwnershipTypeEvent::class.java) {
            val ownershipType = viewModel.ownershipType.value ?: ListingEnum.OwnershipType.SALE
            RxBus.publish(
                UpdateFragmentOwnershipTypeEvent(ownershipType)
            )
        }

        listenRxBus(UpdateSearchFragmentParamEvent::class.java) {
            viewModel.fragmentPropertyMainType.postValue(it.propertyMainType)
        }
    }

    // Setup residential/commercial toggle
    private fun observeLiveData() {
        viewModel.propertyPurpose.observeNotNull(this) { purpose ->
            binding.switchPropertyPurpose.isChecked = purpose == PropertyPurpose.COMMERCIAL

            val previousPosition = binding.viewPager.currentItem
            searchPagerAdapter.setPropertyPurpose(purpose)

            when (purpose) {
                PropertyPurpose.RESIDENTIAL -> {
                    binding.tabSearchResidential.setupWithViewPager(binding.viewPager) { position ->
                        viewModel.canTogglePropertyPurpose.postValue(position < 2) // position == 2 for X-value
                    }
                }
                PropertyPurpose.COMMERCIAL -> {
                    binding.tabSearchCommercial.setupWithViewPager(binding.viewPager)
                }
                else -> {
                    // Do nothing
                }
            }

            binding.viewPager.setCurrentItem(defaultSearchType?.position ?: previousPosition, false)

            // Update property types button on search listing fragment
            RxBus.publish(UpdateSearchPropertyTypeEvent(purpose, defaultExpandMode))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SEARCH_LISTINGS_BY_MRT -> launchSearchResultHelper.launchListingsFromMrts(
                data
            )
            REQUEST_CODE_SEARCH_LISTINGS_BY_DISTRICT -> launchSearchResultHelper.launchFromDistricts(
                SearchCommonViewModel.SearchResultType.LISTINGS,
                data
            )
            REQUEST_CODE_SEARCH_LISTINGS_BY_HDB_TOWN -> launchSearchResultHelper.launchFromHdbTowns(
                SearchCommonViewModel.SearchResultType.LISTINGS,
                data
            )
            REQUEST_CODE_SEARCH_LISTINGS_BY_SCHOOL -> launchSearchResultHelper.launchListingsFromSchools(
                data
            )
            REQUEST_CODE_SEARCH_TRANSACTIONS_BY_MRT -> launchSearchResultHelper.launchTransactionsFromMrts(
                data, viewModel.fragmentPropertyMainType.value
            )
            REQUEST_CODE_SEARCH_TRANSACTIONS_BY_DISTRICT -> launchSearchResultHelper.launchFromDistricts(
                SearchCommonViewModel.SearchResultType.TRANSACTIONS,
                data, viewModel.fragmentPropertyMainType.value
            )
            REQUEST_CODE_SEARCH_TRANSACTIONS_BY_HDB_TOWN -> {
                launchSearchResultHelper.launchFromHdbTowns(
                    SearchCommonViewModel.SearchResultType.TRANSACTIONS,
                    data, viewModel.fragmentPropertyMainType.value
                )
            }
            REQUEST_CODE_SEARCH_TRANSACTIONS_BY_SCHOOL -> launchSearchResultHelper.launchTransactionsFromSchools(
                data, viewModel.fragmentPropertyMainType.value
            )
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        if (viewModel.isCloseSearchPanel.value == false) {
            RxBus.publish(CancelSearchModeEvent())
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        const val REQUEST_CODE_SEARCH_LISTINGS_BY_MRT = 1
        const val REQUEST_CODE_SEARCH_LISTINGS_BY_DISTRICT = 2
        const val REQUEST_CODE_SEARCH_LISTINGS_BY_HDB_TOWN = 3
        const val REQUEST_CODE_SEARCH_LISTINGS_BY_SCHOOL = 4

        const val REQUEST_CODE_SEARCH_TRANSACTIONS_BY_MRT = 5
        const val REQUEST_CODE_SEARCH_TRANSACTIONS_BY_DISTRICT = 6
        const val REQUEST_CODE_SEARCH_TRANSACTIONS_BY_HDB_TOWN = 7
        const val REQUEST_CODE_SEARCH_TRANSACTIONS_BY_SCHOOL = 8

        const val EXTRA_SEARCH_TYPE = "EXTRA_SEARCH_TYPE"
        const val EXTRA_PROPERTY_PURPOSE = "EXTRA_PROPERTY_PURPOSE"
        const val EXTRA_OWNERSHIP_TYPE = "EXTRA_OWNERSHIP_TYPE"
        const val EXTRA_KEY_EXPAND_MODE = "EXTRA_KEY_EXPAND_MODE"

        fun launch(
            activity: BaseActivity,
            searchType: SearchType,
            propertyPurpose: PropertyPurpose?,
            ownershipType: ListingEnum.OwnershipType?,
            source: ExpandMode? = null,
            requestCode: Int? = null
        ) {
            val extras = Bundle()
            extras.putSerializable(EXTRA_SEARCH_TYPE, searchType)
            extras.putSerializable(EXTRA_PROPERTY_PURPOSE, propertyPurpose)
            extras.putSerializable(EXTRA_OWNERSHIP_TYPE, ownershipType)
            extras.putSerializable(EXTRA_KEY_EXPAND_MODE, source)
            if (requestCode != null) {
                activity.launchActivityForResult(SearchActivity::class.java, extras, requestCode)
            } else {
                activity.launchActivity(SearchActivity::class.java, extras)
            }
        }
    }

    enum class SearchType(val position: Int) {
        LISTINGS(0), TRANSACTIONS(1), X_VALUE(2)
    }

    enum class ExpandMode {
        FULL,
        COMPACT
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_search
    }

    override fun getViewModelClass(): Class<SearchViewModel> {
        return SearchViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return null
    }
}