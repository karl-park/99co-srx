package sg.searchhouse.agentconnect.view.activity.listing

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_listing_details.*
import kotlinx.android.synthetic.main.activity_listing_details.layout_enquire
import kotlinx.android.synthetic.main.layout_listing_details_contact_agent.*
import kotlinx.android.synthetic.main.layout_listing_details_description.*
import kotlinx.android.synthetic.main.layout_listing_details_edit.view.*
import kotlinx.android.synthetic.main.layout_listing_details_enquire_agent.*
import kotlinx.android.synthetic.main.layout_listing_details_enquire_agent.view.btn_enquire
import kotlinx.android.synthetic.main.layout_listing_details_facilities.*
import kotlinx.android.synthetic.main.layout_listing_details_home_value_estimator.*
import kotlinx.android.synthetic.main.layout_listing_details_home_value_estimator_transactions.*
import kotlinx.android.synthetic.main.layout_listing_details_home_value_estimator_x_trend.*
import kotlinx.android.synthetic.main.layout_listing_details_key_info.*
import kotlinx.android.synthetic.main.layout_listing_details_location.*
import kotlinx.android.synthetic.main.layout_listing_details_similar_listings.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant.DEFAULT_MAP_ZOOM
import sg.searchhouse.agentconnect.databinding.ActivityListingDetailsBinding
import sg.searchhouse.agentconnect.databinding.LayoutListingDetailsTransactedBinding
import sg.searchhouse.agentconnect.dsl.findChildLayoutBindingById
import sg.searchhouse.agentconnect.dsl.launchActivity
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.SrxCreditMainType
import sg.searchhouse.agentconnect.enumeration.api.LocationEnum
import sg.searchhouse.agentconnect.enumeration.app.EnquireAgentDialogOption
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.event.listing.create.NotifyPostListingEvent
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.listing.FullListingPO
import sg.searchhouse.agentconnect.model.api.listing.ListingDetailPO
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.model.api.xvalue.HomeReportTransaction
import sg.searchhouse.agentconnect.model.api.xvalue.XTrendKeyValue
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.view.activity.agent.cv.AgentCvActivity
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.activity.chat.ChatMessagingActivity
import sg.searchhouse.agentconnect.view.activity.listing.user.FeaturesCreditApplicationActivity
import sg.searchhouse.agentconnect.view.adapter.listing.media.ListingMediaHeaderAdapter
import sg.searchhouse.agentconnect.view.adapter.listing.search.AmenityOptionAdapter
import sg.searchhouse.agentconnect.view.adapter.listing.search.ListingLatestTransactionAdapter
import sg.searchhouse.agentconnect.view.adapter.listing.search.SimilarListingAdapter
import sg.searchhouse.agentconnect.view.fragment.listing.certifiedlisting.CertifiedListingDialogFragment
import sg.searchhouse.agentconnect.view.fragment.listing.create.PostListingDialogFragment
import sg.searchhouse.agentconnect.view.helper.xvalue.XTrendGraphWrapper
import sg.searchhouse.agentconnect.view.widget.listing.MyListingsFabAddOn
import sg.searchhouse.agentconnect.viewmodel.activity.listing.ListingDetailsViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.listing.ListingDetailsViewModel.ToolbarStyle

class ListingDetailsActivity :
    ViewModelActivity<ListingDetailsViewModel, ActivityListingDetailsBinding>(),
    OnMapReadyCallback {

    private lateinit var mapFragment: SupportMapFragment
    private var map: GoogleMap? = null
    private lateinit var listingId: String
    private var xTrendGraphWrapper: XTrendGraphWrapper? = null
    private val latestTransactionAdapter = ListingLatestTransactionAdapter()
    private var dialogCopyListing: AlertDialog? = null

    private fun getLayoutTransactedBinding(): LayoutListingDetailsTransactedBinding = binding.findChildLayoutBindingById(R.id.layout_transacted)

    private var listingMediaAdapter =
        ListingMediaHeaderAdapter {
            val intent = Intent(this, ListingMediaActivity::class.java)
            intent.putExtra(ListingMediaActivity.EXTRA_KEY_POSITION, it)
            // The presence of listingParam is guaranteed for the adapter item to be clicked
            val listingParam = viewModel.listingParam.value!!

            intent.putExtra(ListingMediaActivity.EXTRA_KEY_LISTING_ID, listingParam.first)
            intent.putExtra(ListingMediaActivity.EXTRA_KEY_LISTING_TYPE, listingParam.second)
            intent.putExtra(ListingMediaActivity.EXTRA_KEY_POSITION, it)

            startActivityForResult(intent, REQUEST_CODE_SHOW_MEDIA)
        }

    private var similarListingAdapter =
        SimilarListingAdapter {
            launch(this, it.id, it.listingType)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupStatusBar()
        setToolbarStyle(ToolbarStyle.DEFAULT)
        setupInputExtras()
        setupActionBar()
        observeLiveData()
        setupViews()
        setOnClickListeners()
        setupRxBus()
    }

    private fun setupRxBus() {
        listenRxBus(NotifyPostListingEvent::class.java) {
            val listingId = getListingId() ?: return@listenRxBus
            val listingType = getListingType() ?: return@listenRxBus
            viewModel.performRequest(listingId = listingId, listingType = listingType)
        }
    }

    private fun setupStatusBar() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    private fun setupInputExtras() {
        listingId = intent.getStringExtra(EXTRA_KEY_LISTING_ID) ?: return finish()
        val listingType = intent.getStringExtra(EXTRA_KEY_LISTING_TYPE) ?: return finish()

        // For debug
        println("listing details, listingId = $listingId, listingType = $listingType")

        viewModel.listingParam.postValue(Pair(listingId, listingType))

        //for listing management
        intent.getSerializableExtra(EXTRA_KEY_LISTING_GROUP)?.run {
            viewModel.listingGroup.value = this as ListingManagementEnum.ListingGroup
        } ?: setDefaultListingGroup()
    }

    private fun setDefaultListingGroup() {
        //note: set active group as default listing group
        viewModel.listingGroup.value = ListingManagementEnum.ListingGroup.ACTIVE
    }

    private fun setupMap() {
        mapFragment = fragment_map as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setToolbarStyle(toolbarStyle: ToolbarStyle) {
        viewModel.toolbarStyle.postValue(toolbarStyle)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu?.run { setMenuItemColor(this) }
        return true
    }

    private fun setMenuItemColor(menu: Menu) {
        val toolbarStyle = viewModel.toolbarStyle.value ?: return
        try {
            ViewUtil.setMenuItemIconColor(
                this,
                menu,
                R.id.menu_item_share,
                toolbarStyle.menuIconColor
            )
        } catch (e: NullPointerException) {
            ErrorUtil.handleError("Encounter error when set menu item color", e)
        }
    }

    private fun setupViews() {
        layout_container.setupLayoutAnimation()
        setupOnScrollChangeListener()
        setupImagesViewPager()
        setupLocationAmenitiesList()
        setupLatestTransactionsList()
        setupSimilarListingsList()
        setupMap()
    }

    private fun setupOnScrollChangeListener() {
        scroll_view.setOnScrollChangeListener { _, _, y, _, oldY ->
            val offsetY =
                resources.getDimensionPixelSize(R.dimen.listing_details_photo_height) - toolbar.layoutParams.height
            if (offsetY in (oldY + 1)..y) {
                setToolbarStyle(ToolbarStyle.DEFAULT)
            } else if (offsetY in y until oldY) {
                setToolbarStyle(ToolbarStyle.TRANSLUCENT)
            }
        }
    }

    private fun setupImagesViewPager() {
        view_pager.adapter = listingMediaAdapter
        view_pager_indicator.attachToViewPager(view_pager)
    }

    private fun setupLocationAmenitiesList() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        list_location.layoutManager = layoutManager
        list_location.adapter =
            AmenityOptionAdapter { amenityOption ->
                viewAmenities(amenityOption)
            }
    }

    private fun setupSimilarListingsList() {
        val similarListingLayoutManager = LinearLayoutManager(this)
        similarListingLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        list_similar_listings.layoutManager = similarListingLayoutManager
        list_similar_listings.adapter = similarListingAdapter
    }

    private fun setupLatestTransactionsList() {
        val latestTransactionsLayoutManager = LinearLayoutManager(this)
        latestTransactionsLayoutManager.orientation = LinearLayoutManager.VERTICAL
        list_latest_transactions.layoutManager = latestTransactionsLayoutManager
        list_latest_transactions.adapter = latestTransactionAdapter
    }

    private fun getListingId(): String? {
        return viewModel.listingParam.value?.first
    }

    private fun getListingType(): String? {
        return viewModel.listingParam.value?.second
    }

    private fun viewAmenities(amenityOption: LocationEnum.AmenityOption) {
        val listingId = getListingId() ?: return
        val listingType = getListingType() ?: return
        AmenitiesActivity.launch(
            this,
            listingId = listingId,
            listingType = listingType,
            amenityOption = amenityOption
        )
    }

    private fun setOnClickListeners() {
        btn_expand_key_info.setOnClickListener { viewModel.isExpandKeyInfo.postValue(true) }
        btn_collapse_key_info.setOnClickListener { viewModel.isExpandKeyInfo.postValue(false) }
        btn_expand_description.setOnClickListener { viewModel.isExpandDescription.postValue(true) }
        btn_collapse_description.setOnClickListener { viewModel.isExpandDescription.postValue(false) }
        btn_expand_transactions.setOnClickListener { viewModel.isExpandTransactions.postValue(true) }
        btn_collapse_transactions.setOnClickListener {
            viewModel.isExpandTransactions.postValue(
                false
            )
        }
        layout_location_header.setOnClickListener { viewModel.isExpandLocation.postValue(viewModel.isExpandLocation.value != true) }
        layout_home_value_estimator_header.setOnClickListener {
            viewModel.isExpandHomeValueEstimator.postValue(
                viewModel.isExpandHomeValueEstimator.value != true
            )
        }
        view_map.setOnClickListener {
            viewAmenities(LocationEnum.AmenityOption.OTHERS)
        }
        layout_facilities_header.setOnClickListener {
            viewModel.isExpandFacilities.postValue(
                viewModel.isExpandFacilities.value != true
            )
        }
        layout_similar_listings_header.setOnClickListener {
            viewModel.isExpandSimilarListings.postValue(
                viewModel.isExpandSimilarListings.value != true
            )
        }
        layout_contact_agent_header.setOnClickListener {
            viewModel.isExpandContactAgent.postValue(
                viewModel.isExpandContactAgent.value != true
            )
        }
        layout_enquire.btn_enquire.setOnClickListener {
            showEnquireListDialog()
        }
        layout_edit.btn_edit.setOnClickListener {
            editListing()
        }
        layout_edit.btn_srx_add_on.setOnClickListener { showAddOnDialog() }
        btn_view_agent_cv.setOnClickListener { viewAgentCv() }
        layout_agent.setOnClickListener { viewAgentCv() }
        getLayoutTransactedBinding().btnCopy.setOnClickListener { copyListing() }
    }

    private fun viewAgentCv() {
        //added accessibility of agent cv
        AuthUtil.checkModuleAccessibility(
            module = AccessibilityEnum.AdvisorModule.AGENT_CV,
            onSuccessAccessibility = {
                viewModel.fullListingPO.value?.agentPO?.userId?.let { userId ->
                    AgentCvActivity.launch(this, userId)
                }
            }
        )
    }

    private fun editListing() {
        // !!: The listing ID is the prerequisite to show edit button, hence always not null here
        PostListingDialogFragment.launch(
            supportFragmentManager,
            viewModel.listingPO.value?.id!!
        )
    }

    private fun showAddOnDialog() {
        val addOnList = viewModel.getAddOnList()
        dialogUtil.showListDialog(
            addOnList.map { it.subLabel },
            { _, position ->
                when (addOnList[position]) {
                    MyListingsFabAddOn.AddOnFab.COPY -> {
                        copyListing()
                    }
                    MyListingsFabAddOn.AddOnFab.V360 -> {
                        AuthUtil.checkModuleAccessibility(
                            module = AccessibilityEnum.AdvisorModule.LISTING_MANAGEMENT,
                            function = AccessibilityEnum.InAccessibleFunction.LISTING_MANAGEMENT_V360,
                            onSuccessAccessibility = {
                                val listingIdTypes =
                                    viewModel.listingPO.value?.getListingIdType() ?: ""
                                launchFeaturesCreditApplication(
                                    listingIdTypes = listingIdTypes,
                                    srxMainType = SrxCreditMainType.V360
                                )
                            }
                        )
                    }
                    MyListingsFabAddOn.AddOnFab.X_DRONE -> {
                        AuthUtil.checkModuleAccessibility(
                            module = AccessibilityEnum.AdvisorModule.LISTING_MANAGEMENT,
                            function = AccessibilityEnum.InAccessibleFunction.LISTING_MANAGEMENT_X_DRONE,
                            onSuccessAccessibility = {
                                val listingIdTypes =
                                    viewModel.listingPO.value?.getListingIdType() ?: ""
                                launchFeaturesCreditApplication(
                                    listingIdTypes = listingIdTypes,
                                    srxMainType = SrxCreditMainType.DRONE
                                )
                            }
                        )
                    }
                    MyListingsFabAddOn.AddOnFab.FEATURED_LISTING -> {
                        AuthUtil.checkModuleAccessibility(
                            module = AccessibilityEnum.AdvisorModule.LISTING_MANAGEMENT,
                            function = AccessibilityEnum.InAccessibleFunction.LISTING_MANAGEMENT_FEATURE_LISTINGS,
                            onSuccessAccessibility = {
                                val listingIdTypes =
                                    viewModel.listingPO.value?.getListingIdType() ?: ""
                                launchFeaturesCreditApplication(
                                    listingIdTypes = listingIdTypes,
                                    srxMainType = SrxCreditMainType.FEATURED_LISTING
                                )
                            }
                        )


                    }
                    MyListingsFabAddOn.AddOnFab.CERTIFIED_LISTING -> {
                        AuthUtil.checkModuleAccessibility(
                            module = AccessibilityEnum.AdvisorModule.LISTING_MANAGEMENT,
                            function = AccessibilityEnum.InAccessibleFunction.LISTING_MANAGEMENT_CERTIFIED_LISTINGS,
                            onSuccessAccessibility = {
                                val listingIdTypes =
                                    viewModel.listingPO.value?.getListingIdType() ?: ""
                                CertifiedListingDialogFragment.launch(
                                    fragmentManager = supportFragmentManager,
                                    listingIdType = listingIdTypes
                                )
                            }
                        )
                    }
                    MyListingsFabAddOn.AddOnFab.COMMUNITY_POST -> {
                        maybeVisitSponsorListing()
                    }
                    else -> {
                        //do nothing
                    }
                }
            })
    }

    private fun maybeVisitSponsorListing() {
        AuthUtil.checkModuleAccessibility(
            module = AccessibilityEnum.AdvisorModule.LISTING_MANAGEMENT,
            onSuccessAccessibility = {
                viewModel.listingPO.value?.id?.toIntOrNull()?.let {
                    SponsorListingActivity.launchForResult(this, it, REQUEST_CODE_SPONSOR)
                }
            }
        )
    }

    private fun copyListing() {
        dialogCopyListing = dialogUtil.showProgressDialog(R.string.dialog_message_copying_listing)
        viewModel.performGetManagementListing()
    }

    private fun launchFeaturesCreditApplication(
        listingIdTypes: String,
        srxMainType: SrxCreditMainType
    ) {
        FeaturesCreditApplicationActivity.launch(
            activity = this,
            creditType = srxMainType,
            selectedListingIdTypes = arrayListOf(listingIdTypes)
        )
    }

    private fun showEnquireListDialog() {
        val list = EnquireAgentDialogOption.MainOption.values().map { it.label }
        dialogUtil.showListDialog(list, { dialogInterface, position ->
            when (position) {
                EnquireAgentDialogOption.MainOption.CALL_AGENT.position -> callAgent()
                EnquireAgentDialogOption.MainOption.MESSAGE.position -> showSecondaryEnquireListDialog()
                EnquireAgentDialogOption.MainOption.WHATSAPP.position -> whatsAppAgent()
                EnquireAgentDialogOption.MainOption.CHAT.position -> chatWithAgent()
            }
            dialogInterface.dismiss()
        })
    }

    private fun showSecondaryEnquireListDialog() {
        val list = EnquireAgentDialogOption.MessageOption.values().map { it.label }
        dialogUtil.showListDialog(list, { dialogInterface, position ->
            when (position) {
                EnquireAgentDialogOption.MessageOption.TEXT_MESSAGE.position -> textMessageAgent()
                EnquireAgentDialogOption.MessageOption.APP_ENQUIRY.position -> sendAppEnquiry()
            }
            dialogInterface.dismiss()
        })
    }

    private fun sendAppEnquiry() {
        // TODO: Pending
        ViewUtil.showMessage("Pending")
    }

    private fun textMessageAgent() {
        viewModel.fullListingPO.value?.agentPO?.getSanitizedMobileNumber()?.let {
            // Full listing PO is value guaranteed to be not null
            val listingPO = viewModel.fullListingPO.value?.listingDetailPO?.listingPO!!
            val templateMessage = getTemplateMessage(listingPO)
            IntentUtil.sendSms(this, it, templateMessage)
        } ?: ViewUtil.showMessage(R.string.error_agent_mobile_not_available)
    }

    private fun chatWithAgent() {
        val agentPO = viewModel.fullListingPO.value?.agentPO
            ?: return ErrorUtil.handleError("Missing `agentPO`")
        val listingPO = viewModel.listingPO.value
            ?: return ErrorUtil.handleError("Missing `listingPO`")
        val listingId = listingPO.id
        val listingType = listingPO.listingType
        if (!ListingEnum.ListingType.values()
                .any { it.value == listingType }
        ) return ErrorUtil.handleError("Invalid listing type $listingType")
        ChatMessagingActivity.launch(
            this,
            agent = agentPO,
            listingTypeId = "$listingType,$listingId"
        )
    }

    private fun callAgent() {
        viewModel.fullListingPO.value?.agentPO?.getSanitizedMobileNumber()?.let {
            IntentUtil.dialPhoneNumber(this, it)
        } ?: ViewUtil.showMessage(R.string.error_agent_mobile_not_available)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionUtil.REQUEST_CODE_CALL -> {
                PermissionUtil.handlePermissionResult(
                    Manifest.permission.CALL_PHONE,
                    permissions,
                    grantResults
                ) { callAgent() }
            }
        }
    }

    private fun whatsAppAgent() {
        viewModel.fullListingPO.value?.agentPO?.getSanitizedMobileNumberWithSgCountryCode()?.let {
            // Full listing PO is value guaranteed to be not null
            val listingPO = viewModel.fullListingPO.value?.listingDetailPO?.listingPO!!
            val templateMessage = getTemplateMessage(listingPO)
            IntentUtil.openWhatsApp(
                this,
                it, templateMessage
            )
        } ?: ViewUtil.showMessage(R.string.error_agent_mobile_not_available)
    }

    private fun getTemplateMessage(listingPO: ListingPO): String {
        val listingId = listingPO.id
        val listingName = listingPO.getListingName()
        val baseUrl = ApiUtil.getBaseUrl(this)
        return when (listingPO.listingType) {
            ListingEnum.ListingType.SRX_LISTING.value -> {
                getString(R.string.template_message_enquire_agent, listingName, baseUrl, listingId)
            }
            else -> {
                // Template:
                // Hi, I am interested in your [propertySubType],
                // [projectName] ([transactionType]) [bedroomCount],
                // [shortenedAskingPrice]. Please contact me. Rgds, [myUserName] [myAgencyName]

                val propertySubType = listingPO.getPropertySubTypeLabel(this)
                    ?: throw IllegalArgumentException("Missing `cdResearchSubType` in live listing!")
                val projectName = listingPO.getListingName()
                val transactionType =
                    listingPO.getOwnershipType()?.label?.run { getString(this) }
                        ?: throw IllegalArgumentException("Missing `ownershipType` in live listing!")
                val bedroomCount = if (!TextUtils.isEmpty(listingPO.rooms)) {
                    " ${listingPO.getRoomsNumber()}rm"
                } else {
                    ""
                }
                val shortenedAskingPrice = listingPO.askingPriceLabelShortened()

                val me = SessionUtil.getCurrentUser()
                    ?: throw IllegalStateException("Missing current user")

                val myUserName = me.name
                val myAgencyName = if (!TextUtils.isEmpty(me.companyName)) {
                    " ${me.companyName}"
                } else {
                    ""
                }
                getString(
                    R.string.template_message_enquire_agent_public,
                    propertySubType,
                    projectName,
                    transactionType,
                    bedroomCount,
                    shortenedAskingPrice,
                    myUserName,
                    myAgencyName,
                )
            }
        }
    }

    private fun setupActionBar() {
        ViewUtil.setToolbarHomeIconColor(toolbar, R.color.white)
        supportActionBar?.title = ""
    }

    private fun observeLiveData() {
        viewModel.listingParam.observeNotNull(this) { listingParam ->
            viewModel.performRequest(
                listingParam.first,
                listingParam.second
            )
        }

        viewModel.listingPO.observe(this) {
            val listingPO = it ?: return@observe
            setupLocation(listingPO)
            setupSimilarListings(listingPO.similarListings)
            setupXTrend(listingPO)
            invalidateOptionsMenu()
        }

        viewModel.listingDetailPO.observe(this) {
            val listingDetailPO = it ?: return@observe
            setupFacilities(listingDetailPO)
        }

        viewModel.fullListingPO.observe(this) {
            val fullListingPO = it ?: return@observe
            setupImages(fullListingPO)
        }

        viewModel.isExpandContactAgent.observe(this) {
            if (it == true) {
                scroll_view.postDelayed({
                    ViewUtil.scrollToBottom(scroll_view)
                }, DELAY_IN_MILLIS_SCROLL_TO_BOTTOM)
            }
        }

        viewModel.isShowOccupied.observe(this) {
            if (it == true) {
                setToolbarStyle(ToolbarStyle.TRANSLUCENT)
            } else {
                setToolbarStyle(ToolbarStyle.DEFAULT)
            }
        }

        viewModel.toolbarStyle.observe(this) {
            val toolbarStyle = it ?: return@observe
            updateToolbarStyle(toolbarStyle)
        }

        viewModel.xTrendList.observeNotNull(this) {
            plotGraph(it)
        }

        viewModel.selectedTimeScale.observeNotNull(this) {
            xTrendGraphWrapper?.updateTimeScale(it)
        }

        viewModel.latestTransactions.observe(this) {
            if (it?.isNotEmpty() == true) {
                populateLatestTransactions(it)
            }
        }

        viewModel.isExpandTransactions.observe(this) {
            expandCollapseTransactions(it == true)
        }

        viewModel.getManagementListingResponse.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.FAIL -> {
                    dialogCopyListing?.dismiss()
                    ViewUtil.showMessage(it.error?.error)
                }
                ApiStatus.StatusKey.ERROR -> {
                    dialogCopyListing?.dismiss()
                }
                else -> {
                    //Do nothing
                }
            }
        }

        viewModel.copyListingResponse.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    dialogCopyListing?.dismiss()
                    val listingId = it.body?.listingEditPO?.id?.toString() ?: return@observeNotNull
                    showPostListingDialog(listingId)
                }
                ApiStatus.StatusKey.FAIL -> {
                    dialogCopyListing?.dismiss()
                    ViewUtil.showMessage(it.error?.error)
                }
                ApiStatus.StatusKey.ERROR -> {
                    dialogCopyListing?.dismiss()
                }
                else -> {
                    //do nothing
                }
            }
        }
    }

    private fun showPostListingDialog(listingId: String) {
        PostListingDialogFragment.launch(supportFragmentManager, listingId)
    }

    private fun expandCollapseTransactions(isExpand: Boolean) {
        val latestTransactions = viewModel.latestTransactions.value ?: return
        populateLatestTransactions(latestTransactions, isExpand)
    }

    private fun populateLatestTransactions(
        latestTransactions: List<HomeReportTransaction>,
        isFull: Boolean = false
    ) {
        latestTransactionAdapter.listItems = when (isFull) {
            true -> {
                latestTransactions
            }
            false -> {
                latestTransactions.getOrNull(0)?.let { first ->
                    listOf(first)
                } ?: emptyList()
            }
        }
        latestTransactionAdapter.notifyDataSetChanged()
    }

    private fun plotGraph(xTrendKeyValues: List<XTrendKeyValue>) {
        xTrendGraphWrapper = XTrendGraphWrapper(this, chart_x_trend, xTrendKeyValues)
        viewModel.selectedTimeScale.value?.let { xTrendGraphWrapper?.updateTimeScale(it) }
    }

    private fun setupXTrend(listingPO: ListingPO) {
        if (shouldPlotXTrend(listingPO)) {
            viewModel.performGetXTrend(listingPO.id, listingPO.listingType)
            viewModel.performGetListingLatestTransactions(listingPO)
        }
    }

    private fun shouldPlotXTrend(listingPO: ListingPO): Boolean {
        val isCommercial = PropertyTypeUtil.isCommercial(listingPO.cdResearchSubType)
        val isPortalListing = listingPO.portalListings.isNotEmpty()
        return !isCommercial && !isPortalListing
    }

    private fun updateToolbarStyle(toolbarStyle: ToolbarStyle) {
        toolbar.setBackgroundColor(ContextCompat.getColor(this, toolbarStyle.backgroundColor))
        ViewUtil.setToolbarHomeIconColor(toolbar, toolbarStyle.menuIconColor)
        window.statusBarColor = when (toolbarStyle) {
            ToolbarStyle.TRANSLUCENT -> Color.TRANSPARENT
            ToolbarStyle.DEFAULT -> Color.BLACK
        }
        invalidateOptionsMenu()
    }

    private fun setupSimilarListings(similarListings: List<ListingPO>) {
        similarListingAdapter.items = similarListings
        similarListingAdapter.notifyDataSetChanged()
    }

    private fun setupImages(fullListingPO: FullListingPO) {
        listingMediaAdapter.listItems = fullListingPO.getValidListingMedias()
        listingMediaAdapter.notifyDataSetChanged()
    }

    private fun setupFacilities(listingDetailPO: ListingDetailPO) {
        val list = listingDetailPO.area + listingDetailPO.features + listingDetailPO.fixtures
        list.mapIndexed { index, feature ->
            val columnLayout = when {
                index % 2 == 0 -> layout_facilities_content_column_one
                else -> layout_facilities_content_column_two
            }
            val textView = TextView(this, null, 0, R.style.Body)
            val layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            layoutParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.spacing_m)
            layoutParams.leftMargin = resources.getDimensionPixelSize(R.dimen.margin_facility_tick)
            textView.layoutParams = layoutParams
            textView.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_check,
                0,
                0,
                0
            )
            textView.compoundDrawables.getOrNull(0)
                ?.setTint(resources.getColor(R.color.black_invertible, null))
            textView.text = feature.name
            columnLayout.addView(textView)
        }
    }

    private fun setupLocation(listingPO: ListingPO) {
        val latitude = listingPO.latitude.toDoubleOrNull() ?: return
        val longitude = listingPO.longitude.toDoubleOrNull() ?: return
        val latLng = LatLng(latitude, longitude)
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_MAP_ZOOM))
        map?.addMarker(MarkerOptions().position(latLng))
    }

    override fun onMapReady(map: GoogleMap?) {
        this.map = map
        map?.uiSettings?.setAllGesturesEnabled(false)
    }

    private fun showHideShareIcon(menu: Menu) {
        val listingPO = viewModel.listingPO.value ?: return
        menu.findItem(R.id.menu_item_share)?.isVisible = !listingPO.isPublicListing()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_listing_details, menu)
        menu?.run { showHideShareIcon(this) }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_share -> {
                shareListing()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun shareListing() {
        val listingId = viewModel.listingParam.value?.first ?: return
        val listingPO = viewModel.listingPO.value ?: return
        val listingUrl = getListingUrl(listingId)
        val message = if (listingPO.isOwnListing()) {
            getString(R.string.message_share_own_listing, listingUrl)
        } else {
            getString(R.string.message_share_other_listing, listingUrl)
        }
        IntentUtil.shareText(this, message)
    }

    private fun getListingUrl(listingId: String): String {
        return "${ApiUtil.getBaseUrl(this)}/l/$listingId"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SHOW_MEDIA -> {
                val position = data?.getIntExtra(ListingMediaActivity.EXTRA_KEY_POSITION, 0) ?: 0
                view_pager.setCurrentItem(position, false)
            }
            REQUEST_CODE_SPONSOR -> {
                RxBus.publish(NotifyPostListingEvent())
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        const val EXTRA_KEY_LISTING_ID = "EXTRA_KEY_LISTING_ID"
        const val EXTRA_KEY_LISTING_TYPE = "EXTRA_KEY_LISTING_TYPE"
        const val EXTRA_KEY_LAUNCH_MODE = "EXTRA_KEY_LAUNCH_MODE"
        private const val EXTRA_KEY_LISTING_GROUP = "EXTRA_KEY_LISTING_GROUP"
        const val DELAY_IN_MILLIS_SCROLL_TO_BOTTOM: Long = 100

        const val REQUEST_CODE_SHOW_MEDIA = 1
        private const val REQUEST_CODE_SPONSOR = 2
        fun launch(
            activity: Activity,
            listingId: String,
            listingType: String,
            launchMode: LaunchMode = LaunchMode.VIEW,
            listingGroup: ListingManagementEnum.ListingGroup? = null
        ) {
            val extras = Bundle()
            extras.putString(EXTRA_KEY_LISTING_ID, listingId)
            extras.putString(EXTRA_KEY_LISTING_TYPE, listingType)
            extras.putSerializable(EXTRA_KEY_LAUNCH_MODE, launchMode)
            listingGroup?.run { extras.putSerializable(EXTRA_KEY_LISTING_GROUP, this) }
            activity.launchActivity(ListingDetailsActivity::class.java, extras)
        }
    }

    enum class LaunchMode {
        VIEW, EDIT
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_listing_details
    }

    override fun getViewModelClass(): Class<ListingDetailsViewModel> {
        return ListingDetailsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }
}