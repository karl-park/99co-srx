package sg.searchhouse.agentconnect.view.fragment.listing.create

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_fragment_listing_address_search.*
import kotlinx.android.synthetic.main.edit_text_search_white.view.*
import kotlinx.android.synthetic.main.layout_create_listing_search_empty.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.ErrorCode
import sg.searchhouse.agentconnect.databinding.DialogFragmentListingAddressSearchBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.AddressSearchSource
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.GetPortalAPIsResponse
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalAccountPO
import sg.searchhouse.agentconnect.model.api.location.PropertyPO
import sg.searchhouse.agentconnect.event.listing.create.NotifySelectedPropertyEvent
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.CreateListingTrackerPO
import sg.searchhouse.agentconnect.util.AuthUtil
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.listing.portal.PortalListingsActivity
import sg.searchhouse.agentconnect.view.adapter.listing.create.AddressSearchAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelDialogFragment
import sg.searchhouse.agentconnect.view.fragment.cea.CeaAgreementFormsDialogFragment
import sg.searchhouse.agentconnect.view.fragment.listing.portal.PGImportDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.create.ListingAddressSearchViewModel

//Note: Using dialog fragment is more suitable for listing address search than activity
//Note: This screen is independent. Needed to be closed address search screen after showing all coming dialog screens.
class ListingAddressSearchDialogFragment :
    ViewModelDialogFragment<ListingAddressSearchViewModel, DialogFragmentListingAddressSearchBinding>() {

    private lateinit var adapter: AddressSearchAdapter

    companion object {
        private const val TAG = "TAG_LISTING_ADDRESS_SEARCH_DIALOG_FRAGMENT"
        private const val ARGUMENT_KEY_SEARCH_SOURCE = "ARGUMENT_KEY_SEARCH_SOURCE"
        private const val ARGUMENT_KEY_OWNERSHIP_TYPE = "ARGUMENT_KEY_OWNERSHIP_TYPE"
        private const val ARGUMENT_KEY_CREATE_LISTING_TRACKER =
            "ARGUMENT_KEY_CREATE_LISTING_TRACKER"

        fun newInstance(
            addressSearchSource: AddressSearchSource,
            ownershipType: ListingEnum.OwnershipType? = null,
            createListingTracker: CreateListingTrackerPO? = null
        ): ListingAddressSearchDialogFragment {
            val dialog = ListingAddressSearchDialogFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARGUMENT_KEY_SEARCH_SOURCE, addressSearchSource)
            ownershipType?.run { bundle.putSerializable(ARGUMENT_KEY_OWNERSHIP_TYPE, this) }
            createListingTracker?.let {
                bundle.putSerializable(
                    ARGUMENT_KEY_CREATE_LISTING_TRACKER,
                    it
                )
            }
            dialog.arguments = bundle
            return dialog
        }

        fun launch(
            fragmentManager: FragmentManager,
            addressSearchSource: AddressSearchSource,
            ownershipType: ListingEnum.OwnershipType? = null,
            createListingTracker: CreateListingTrackerPO? = null
        ) {
            newInstance(addressSearchSource, ownershipType, createListingTracker).show(
                fragmentManager,
                TAG
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupArgumentParams()
        observeLiveData()
    }

    private fun setupArgumentParams() {
        val bundle = arguments ?: return
        bundle.getSerializable(ARGUMENT_KEY_SEARCH_SOURCE)?.run {
            viewModel.addressSearchSource.value = this as AddressSearchSource
        } ?: throw Throwable("Missing address search source")

        bundle.getSerializable(ARGUMENT_KEY_OWNERSHIP_TYPE)?.run {
            viewModel.ownershipType.value = this as ListingEnum.OwnershipType
        }

        bundle.getSerializable(ARGUMENT_KEY_CREATE_LISTING_TRACKER)?.run {
            viewModel.setCreateListingTrackerObject(this as CreateListingTrackerPO)
        }
    }

    private fun setupViews() {
        setUpAdapterAndList()
        setOnClickListeners()
        showKeyboard()
    }

    private fun setUpAdapterAndList() {
        adapter = AddressSearchAdapter { onSelectProperty(it) }
        list_properties.layoutManager = LinearLayoutManager(activity)
        list_properties.adapter = adapter
    }

    private fun setOnClickListeners() {
        et_search_address.setOnTextChangedListener { viewModel.findProperties(it) }
        tv_cancel.setOnClickListener { dialog?.dismiss() }
        btn_import_pg.setOnClickListener {
            AuthUtil.checkModuleAccessibility(
                module = AccessibilityEnum.AdvisorModule.PG_IMPORT,
                onSuccessAccessibility = { viewModel.getPortalAPIs() })
        }
        btn_cea_exclusive.setOnClickListener {
            AuthUtil.checkModuleAccessibility(
                module = AccessibilityEnum.AdvisorModule.CEA_EXCLUSIVE,
                onSuccessAccessibility = {
                    activity?.run { CeaAgreementFormsDialogFragment.launch(supportFragmentManager) }
                    dialog?.dismiss()
                }
            )
        }
    }

    private fun showKeyboard() {
        et_search_address.requestFocus()
        et_search_address.post { ViewUtil.showKeyboard(et_search_address.edit_text) }
    }

    private fun observeLiveData() {
        //Find Properties
        viewModel.properties.observeNotNull(this) {
            adapter.properties = it
            adapter.notifyDataSetChanged()
        }

        //Import listing
        viewModel.getPortalAPIsResponse.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    val response = it.body ?: return@observeNotNull
                    val mode = ListingManagementEnum.PortalMode.values()
                        .find { portal -> portal.value == response.mode }
                        ?: return@observeNotNull ErrorUtil.handleError("Undefined portal mode")

                    //Save mode in session
                    SessionUtil.setListingPortalMode(mode)

                    //store portal apis result in variables
                    viewModel.portalApiResponse.value = response

                    when (mode) {
                        ListingManagementEnum.PortalMode.SERVER -> viewModel.getPortalListings()
                        ListingManagementEnum.PortalMode.CLIENT -> {
                            activity?.run {
                                val email = SessionUtil.getPortalAuthenticationEmail(this)
                                val password = SessionUtil.getPortalAuthenticationPassword(this)
                                if (email == null || password == null) {
                                    viewModel.importListingStatus.postValue(ApiStatus.StatusKey.SUCCESS)
                                    return@run showPortalLogin(supportFragmentManager, response)
                                } else {
                                    return@run viewModel.portalLogin(email, password)
                                }
                            }
                        }
                    }
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }

        //FOR SERVER SIDE
        viewModel.getPortalListingsResponse.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> showPortalListings()
                ApiStatus.StatusKey.FAIL -> {
                    val error = it.error ?: return@observeNotNull
                    if (error.errorCode == ErrorCode.NO_LOGGED_IN_PORTAL) {
                        val portalApiResponse =
                            viewModel.portalApiResponse.value ?: return@observeNotNull
                        activity?.run {
                            showPortalLogin(
                                supportFragmentManager = supportFragmentManager,
                                response = portalApiResponse
                            )
                        }
                    } else {
                        ViewUtil.showMessage(error.error)
                    }
                }
                else -> {
                    //do nothing
                }
            }
        }

        //FOR CLIENT SIDE
        viewModel.clientLoginResponse.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> showPortalListings(it?.body?.portalAccount)
                ApiStatus.StatusKey.FAIL -> ViewUtil.showMessage(it.error?.error)
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.portalLoginError.observeNotNull(this) { ViewUtil.showMessage(it) }
    }

    //direct to other screens
    private fun showPortalLogin(
        supportFragmentManager: FragmentManager,
        response: GetPortalAPIsResponse
    ) {
        PGImportDialogFragment.launch(
            fragmentManager = supportFragmentManager,
            apiResponse = response
        )
        dialog?.dismiss()
    }

    private fun showPortalListings(portalAccount: PortalAccountPO? = null) {
        activity?.run {
            PortalListingsActivity.launch(
                activity = this,
                portalApiResponse = viewModel.portalApiResponse.value,
                portalAccountPO = portalAccount
            )
            dialog?.dismiss()
        }
    }

    private fun onSelectProperty(propertyPO: PropertyPO) {
        when (viewModel.addressSearchSource.value) {
            AddressSearchSource.MAIN_SCREEN -> {
                activity?.run {
                    CreateUpdateListingDialogFragment.launch(
                        fragmentManager = supportFragmentManager,
                        purpose = ListingManagementEnum.ListingManagementPurpose.CREATE,
                        property = propertyPO,
                        ownershipType = viewModel.ownershipType.value
                            ?: ListingEnum.OwnershipType.SALE,
                        createListingTrackerPO = viewModel.createListingTracker.value
                    )
                }
                dialog?.dismiss()
            }
            AddressSearchSource.CREATE_LISTING -> {
                RxBus.publish(NotifySelectedPropertyEvent(propertyPO))
                dialog?.dismiss()
            }
            else -> {
                //do nothing
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setupFullScreenWindow()
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_listing_address_search
    }

    override fun getViewModelClass(): Class<ListingAddressSearchViewModel> {
        return ListingAddressSearchViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}