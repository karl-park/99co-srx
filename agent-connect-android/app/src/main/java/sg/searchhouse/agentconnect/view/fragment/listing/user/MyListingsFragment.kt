package sg.searchhouse.agentconnect.view.fragment.listing.user

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_my_listings.*
import kotlinx.android.synthetic.main.layout_draft_cea_forms_empty.view.*
import kotlinx.android.synthetic.main.layout_list.list
import kotlinx.android.synthetic.main.layout_my_listings_empty.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentMyListingsBinding
import sg.searchhouse.agentconnect.dsl.widget.listenScrollToBottom
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.event.listing.create.NotifyPostListingEvent
import sg.searchhouse.agentconnect.event.listing.user.*
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.view.activity.listing.MyListingsActivity
import sg.searchhouse.agentconnect.view.activity.listing.user.FeaturesCreditApplicationActivity
import sg.searchhouse.agentconnect.view.adapter.cea.DraftCeaFormsAdapter
import sg.searchhouse.agentconnect.view.adapter.listing.user.MyListingAdapter
import sg.searchhouse.agentconnect.view.adapter.listing.user.MyListingsPagerAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.view.fragment.cea.CeaAgreementFormsDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.user.MyListingsFragmentViewModel

class MyListingsFragment :
    ViewModelFragment<MyListingsFragmentViewModel, FragmentMyListingsBinding>() {

    private lateinit var myListingTab: MyListingsPagerAdapter.MyListingsTab

    private lateinit var adapter: MyListingAdapter

    private val ceaFormsAdapter = DraftCeaFormsAdapter(
        onClickCeaFormListener = {
            val id = NumberUtil.toInt(it.formId) ?: return@DraftCeaFormsAdapter
            RxBus.publish(LaunchCeaExclusiveForm(it.formType, id))
        })

    // TODO Maybe refactor
    private fun launchAddFeaturedListing(listingIdType: String) {
        this.activity?.run {
            FeaturesCreditApplicationActivity.launch(
                this,
                ListingManagementEnum.SrxCreditMainType.FEATURED_LISTING,
                arrayListOf(listingIdType)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupArguments()
    }

    private fun setupArguments() {
        myListingTab =
            arguments?.getSerializable(ARGUMENT_KEY_MY_LISTING_TAB) as MyListingsPagerAdapter.MyListingsTab
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupDefaultPropertyMainType()
    }

    // Prioritise main type on load list if given (from dashboard user listings card buttons)
    private fun setupDefaultPropertyMainType() {
        activity?.intent?.getIntExtra(
            MyListingsActivity.EXTRA_KEY_PROPERTY_PRIMARY_SUB_TYPE,
            -1
        )?.run {
            viewModel.defaultPropertyMainType =
                PropertyTypeUtil.getPropertyMainTypeByPrimarySubType(primarySubType = this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListAndAdapter()
        listenRxBuses()
        setOnListeners()
    }

    private fun setupListAndAdapter() {
        //LISTING
        adapter = createListingsAdapter()
        list.layoutManager = LinearLayoutManager(this.activity)
        list.adapter = adapter
        list.itemAnimator = null // Disable animation when item selected
        //CEA
        list_draft_cea_forms.layoutManager = LinearLayoutManager(this.activity)
        list_draft_cea_forms.adapter = ceaFormsAdapter
        list_draft_cea_forms.itemAnimator = null
    }

    private fun createListingsAdapter(): MyListingAdapter {
        return MyListingAdapter(
            myListingTab,
            onClickListener = { listingId: String, listingType: String, listingGroup: Int ->
                when (myListingTab) {
                    MyListingsPagerAdapter.MyListingsTab.DRAFT -> {
                        RxBus.publish(LaunchEditListingEvent(listingId, listingType))
                    }
                    else -> {
                        RxBus.publish(
                            LaunchListingDetailEvent(
                                listingId = listingId,
                                listingType = listingType,
                                listingGroup = viewModel.getListingGroup(listingGroup)
                                    ?: return@MyListingAdapter
                            )
                        )
                    }
                }
            },
            onViewPortalListingClickListener = { portalUrl, _ ->
                val context = context ?: return@MyListingAdapter
                if (StringUtil.isWebUrlValid(portalUrl)) {
                    IntentUtil.visitUrl(context, portalUrl)
                } else {
                    ViewUtil.showMessage(R.string.toast_error_invalid_portal_listing_url)
                }
            },
            onAddFeaturedButtonClickListener = { listingIdType ->
                launchAddFeaturedListing(
                    listingIdType
                )
            })
    }

    private fun listenRxBuses() {
        listenRxBus(UpdateDraftModeEvent::class.java) { event ->
            //Noted: only for draft mode
            if (myListingTab == MyListingsPagerAdapter.MyListingsTab.DRAFT) {
                if (viewModel.selectedDraftMode.value != event.draftMode) {
                    RxBus.publish(UpdateSelectionOptionEvent(MyListingSelectAllEvent.Option.UNSELECT_ALL))
                    viewModel.selectedDraftMode.postValue(event.draftMode)
                }
            } else {
                viewModel.showDraftCeaForms.postValue(false)
            }
        }

        listenRxBus(UpdateOrderFilterOptionsEvent::class.java) { event ->
            setupFilterOptions(
                orderCriteria = event.orderCriteria,
                propertyMainType = event.propertyMainType,
                propertySubTypes = event.propertySubTypes,
                propertyAge = event.propertyAge,
                bedrooms = event.bedrooms,
                tenure = event.tenure,
                searchText = event.searchText
            )
        }

        listenRxBus(UpdateOwnershipTypeEvent::class.java) { event ->
            if (event.isDraftCeaOwnershipType) {
                if (viewModel.ceaOwnershipType != event.ownershipType) {
                    viewModel.ceaOwnershipType = event.ownershipType
                    ceaFormsAdapter.clearSelectedIds()
                    RxBus.publish(UpdateSelectionOptionEvent(MyListingSelectAllEvent.Option.UNSELECT_ALL))
                    findDraftCeaForms()
                }
            } else {
                if (viewModel.ownershipType != event.ownershipType) {
                    viewModel.ownershipType = event.ownershipType
                    RxBus.publish(UpdateSelectionOptionEvent(MyListingSelectAllEvent.Option.UNSELECT_ALL))
                    loadMyListingsByOwnershipType()
                }
            }
        }

        listenRxBus(MyListingSelectAllEvent::class.java) { event ->
            if (isResumed) {
                if (viewModel.selectedDraftMode.value == ListingManagementEnum.ListingDraftMode.CEA_FORMS) {
                    ceaFormsAdapter.selectedCeaFormIds.clear()
                    if (event.option == MyListingSelectAllEvent.Option.SELECT_ALL) {
                        ceaFormsAdapter.selectedCeaFormIds.addAll(ceaFormsAdapter.forms.map { it.formId })
                    }
                    RxBus.publish(SelectedCeaFormIdsToRemove(ceaFormsAdapter.selectedCeaFormIds))
                    ceaFormsAdapter.notifyDataSetChanged()
                } else {
                    adapter.selectedListings.clear()
                    if (event.option == MyListingSelectAllEvent.Option.SELECT_ALL) {
                        adapter.selectedListings.addAll(adapter.items.filterIsInstance<ListingPO>())
                    }
                    RxBus.publish(NotifySelectedListingsEvent(adapter.selectedListings))
                    adapter.notifyDataSetChanged()
                }

            }
        }

        listenRxBus(NotifyPostListingEvent::class.java) { performRequest() }

        listenRxBus(UpdateDraftCeaFormsEvent::class.java) { findDraftCeaForms() }
    }

    private fun setOnListeners() {
        layout_empty.btn_create_listing.setOnClickListener {
            RxBus.publish(
                LaunchCreateListingEvent(
                    isFromMyListing = true
                )
            )
        }
        layout_cea_forms_empty.btn_create_cea_exclusive.setOnClickListener {
            activity?.run { CeaAgreementFormsDialogFragment.launch(supportFragmentManager) }
        }

        list.listenScrollToBottom {
            val existingListings = viewModel.listingPOs.value ?: return@listenScrollToBottom
            if (viewModel.canLoadMore() && existingListings.last() !is Loading) {
                adapter.items = existingListings + Loading()
                adapter.notifyItemChanged(adapter.items.size - 1)
                viewModel.loadMoreMyListings(myListingTab.listingGroups)
            }
        }
    }

    private fun setupFilterOptions(
        orderCriteria: ListingManagementEnum.OrderCriteria?,
        propertyMainType: ListingEnum.PropertyMainType?,
        propertySubTypes: ListingEnum.PropertySubType?,
        propertyAge: ListingEnum.PropertyAge?,
        bedrooms: ListingEnum.BedroomCount?,
        tenure: ListingEnum.Tenure?,
        searchText: String?
    ) {
        viewModel.orderCriteria = orderCriteria ?: ListingManagementEnum.OrderCriteria.DEFAULT

        viewModel.propertySubTypes =
            propertySubTypes?.run { listOf(this) } ?: propertyMainType?.propertySubTypes

        viewModel.propertyAge = propertyAge

        viewModel.bedroomCount = bedrooms

        viewModel.tenure = tenure

        viewModel.searchText = searchText

        performRequest()
    }

    private fun loadMyListingsByOwnershipType() {
        //Note: to reset all filter options when ownership type changes
        setupFilterOptions(
            orderCriteria = viewModel.orderCriteria,
            propertyMainType = null,
            propertySubTypes = null,
            propertyAge = null,
            bedrooms = null,
            tenure = null,
            searchText = null
        )
    }

    private fun performRequest() {
        viewModel.loadMyListings(myListingTab.listingGroups)
    }

    private fun observeLiveData() {
        viewModel.selectedDraftMode.observeNotNull(this) { draftMode ->
            if (draftMode == ListingManagementEnum.ListingDraftMode.CEA_FORMS) {
                viewModel.showDraftCeaForms.postValue(true)
                findDraftCeaForms()
            } else {
                viewModel.showDraftCeaForms.postValue(false)
                performRequest()
            }
        }
        viewModel.draftCeaForms.observeNotNull(this) { list ->
            publishRxBus(IsSelectionApplicableEvent(list.isNotEmpty()))
            ceaFormsAdapter.updateList(list)
        }

        viewModel.mainResponse.observeNotNull(this) {
            val tempListingPOs = viewModel.getListingPOs(it)
            val existingListingsPOs = viewModel.listingPOs.value ?: emptyList()
            //getting total
            viewModel.total.value = viewModel.getListingPOsTotal(it)
            when (viewModel.startIndex) {
                0 -> {
                    adapter.selectedListings.clear()
                    viewModel.listingPOs.postValue(tempListingPOs)
                }
                else -> {
                    val totalListingPOs: List<ListingPO> =
                        (existingListingsPOs + tempListingPOs).filterIsInstance<ListingPO>()
                            .distinctBy { listing -> listing.getListingTypeId() }
                    viewModel.listingPOs.postValue(totalListingPOs)
                    RxBus.publish(
                        UpdateSelectionOptionEvent(
                            MyListingSelectAllEvent.Option.UNSELECT_ALL,
                            false
                        )
                    )
                }
            }
        }

        viewModel.listingPOs.observeNotNull(this) { listingPOs ->
            publishRxBus(IsSelectionApplicableEvent(listingPOs.isNotEmpty()))
            adapter.items = listingPOs
            adapter.notifyDataSetChanged()
        }

        viewModel.mainStatus.observe(this) { status ->
            if (isResumed && status != ApiStatus.StatusKey.SUCCESS) {
                RxBus.publish(IsSelectionApplicableEvent(false))
            }
        }
    }

    private fun findDraftCeaForms() {
        if (myListingTab == MyListingsPagerAdapter.MyListingsTab.DRAFT &&
            viewModel.selectedDraftMode.value == ListingManagementEnum.ListingDraftMode.CEA_FORMS
        ) {
            viewModel.findUnsubmittedCeaForms()
        }
    }

    override fun onResume() {
        super.onResume()
        observeLiveData()
        val isListOccupied = viewModel.listingPOs.value?.isNotEmpty() == true
        publishRxBus(IsSelectionApplicableEvent(isApplicable = isListOccupied))
        RxBus.publish(UpdateSelectionOptionEvent(MyListingSelectAllEvent.Option.UNSELECT_ALL))
        performRequest()
    }

    companion object {
        @JvmStatic
        fun newInstance(myListingsTab: MyListingsPagerAdapter.MyListingsTab): MyListingsFragment {
            val fragment = MyListingsFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARGUMENT_KEY_MY_LISTING_TAB, myListingsTab)
            fragment.arguments = bundle
            return fragment
        }

        private const val ARGUMENT_KEY_MY_LISTING_TAB = "ARGUMENT_KEY_MY_LISTING_TAB"
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_my_listings
    }

    override fun getViewModelClass(): Class<MyListingsFragmentViewModel> {
        return MyListingsFragmentViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return myListingTab.name
    }
}