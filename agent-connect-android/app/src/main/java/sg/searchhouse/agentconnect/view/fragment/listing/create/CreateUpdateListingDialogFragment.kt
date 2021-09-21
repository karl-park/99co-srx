package sg.searchhouse.agentconnect.view.fragment.listing.create

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.layout_create_listing_info.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentCreateUpdateListingBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum.OwnershipType
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.ListingManagementPurpose
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.PropertyClassification
import sg.searchhouse.agentconnect.model.api.listing.ListingEditPO
import sg.searchhouse.agentconnect.model.api.location.PropertyPO
import sg.searchhouse.agentconnect.event.listing.create.NotifySelectedPropertyEvent
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.CreateListingTrackerPO
import sg.searchhouse.agentconnect.tracking.CreateListingTracker
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.create.CreateUpdateListingViewModel

class CreateUpdateListingDialogFragment :
    ViewModelDialogFragment<CreateUpdateListingViewModel, DialogFragmentCreateUpdateListingBinding>() {

    companion object {
        private const val TAG_CREATE_UPDATE_LISTING_DIALOG = "TAG_CREATE_UPDATE_LISTING_DIALOG"
        private const val ARGUMENT_KEY_LISTING_MANAGEMENT_PURPOSE =
            "ARGUMENT_KEY_LISTING_MANAGEMENT_PURPOSE"
        private const val ARGUMENT_KEY_LISTING_EDIT_PO = "ARGUMENT_KEY_LISTING_ID"
        private const val ARGUMENT_KEY_PROPERTY_PO = "ARGUMENT_KEY_PROPERTY_PO"
        private const val ARGUMENT_KEY_OWNERSHIP_TYPE = "ARGUMENT_KEY_OWNERSHIP_TYPE"
        private const val ARGUMENT_KEY_CREATE_LISTING_TRACKER_PO =
            "ARGUMENT_KEY_CREATE_LISTING_TRACKER_PO"

        fun newInstance(
            purpose: ListingManagementPurpose,
            property: PropertyPO? = null,
            ownershipType: OwnershipType? = null,
            listingEditPO: ListingEditPO? = null,
            createListingTrackerPO: CreateListingTrackerPO? = null
        ): CreateUpdateListingDialogFragment {
            val dialog = CreateUpdateListingDialogFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARGUMENT_KEY_LISTING_MANAGEMENT_PURPOSE, purpose)
            property?.run { bundle.putSerializable(ARGUMENT_KEY_PROPERTY_PO, this) }
            ownershipType?.run { bundle.putSerializable(ARGUMENT_KEY_OWNERSHIP_TYPE, this) }
            listingEditPO?.run { bundle.putSerializable(ARGUMENT_KEY_LISTING_EDIT_PO, this) }
            createListingTrackerPO?.run {
                bundle.putSerializable(
                    ARGUMENT_KEY_CREATE_LISTING_TRACKER_PO,
                    this
                )
            }
            dialog.arguments = bundle
            return dialog
        }

        fun launch(
            fragmentManager: FragmentManager,
            purpose: ListingManagementPurpose,
            property: PropertyPO? = null,
            ownershipType: OwnershipType? = null,
            listingEditPO: ListingEditPO? = null,
            createListingTrackerPO: CreateListingTrackerPO? = null
        ) {
            newInstance(
                purpose = purpose,
                property = property,
                ownershipType = ownershipType,
                listingEditPO = listingEditPO,
                createListingTrackerPO = createListingTrackerPO
            ).show(
                fragmentManager,
                TAG_CREATE_UPDATE_LISTING_DIALOG
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupArgumentParams()
        observeRxBuses()
        observeLiveData()
        setupViewListeners()
    }

    private fun setupArgumentParams() {
        val bundle = arguments ?: return
        bundle.getSerializable(ARGUMENT_KEY_LISTING_MANAGEMENT_PURPOSE)?.run {
            viewModel.purpose.value = this as ListingManagementPurpose
        } ?: throw Throwable("Missing purpose (create or update) listing param")
    }

    private fun observeRxBuses() {
        listenRxBus(NotifySelectedPropertyEvent::class.java) { setupPropertyInfo(it.propertyPO) }
    }

    private fun observeLiveData() {
        viewModel.purpose.observeNotNull(this) { purpose ->
            //get models first while set up argument params
            viewModel.getModels()

            val bundle = arguments ?: return@observeNotNull
            when (purpose) {
                ListingManagementPurpose.CREATE -> {
                    binding.collapsingToolbar.title = getString(R.string.title_create_new_listing)
                    // create listing tracker po
                    bundle.getSerializable(ARGUMENT_KEY_CREATE_LISTING_TRACKER_PO)?.run {
                        if (this is CreateListingTrackerPO) {
                            viewModel.setCreateListingTracker(this)
                        }
                    }

                    bundle.getSerializable(ARGUMENT_KEY_OWNERSHIP_TYPE)?.run {
                        viewModel.ownershipType.value = this as OwnershipType
                    } ?: ErrorUtil.handleError("Missing Property Type SALE or RENT")
                    bundle.getSerializable(ARGUMENT_KEY_PROPERTY_PO)?.run {
                        viewModel.property.value = this as PropertyPO
                    } ?: ErrorUtil.handleError("Missing property po for create listing")
                }
                ListingManagementPurpose.UPDATE -> {
                    binding.collapsingToolbar.title = getString(R.string.title_update_listing)
                    val listingEditPO =
                        bundle.getSerializable(ARGUMENT_KEY_LISTING_EDIT_PO) as ListingEditPO?
                            ?: throw IllegalArgumentException("Missing `ARGUMENT_KEY_LISTING_EDIT_PO`!")
                    viewModel.setListingEditPO(listingEditPO)
                }
                else -> {
                    //DO nothing
                }
            }
        }

        viewModel.ownershipType.observeNotNull(this) { type ->
            if (type != null) {
                viewModel.listingEditPO.value?.type = type.value
                rg_listing_type.setValue(type)
            } else {
                rg_listing_type.setValue(OwnershipType.SALE)
            }
        }

        viewModel.property.observeNotNull(this) { setupPropertyInfo(it) }

        //Update listing
        viewModel.updateListingEditPO.observeNotNull(this) { listingEditPO ->
            val ownerShipType = if (listingEditPO.isRoomRental() && !listingEditPO.isCommercial()) {
                OwnershipType.ROOM_RENTAL
            } else {
                OwnershipType.values().find { it.value == listingEditPO.type }
                    ?: return@observeNotNull
            }

            rg_listing_type.showHideRoomRental(listingEditPO.category != PropertyClassification.COMMERCIAL_INDUSTRIAL.value)
            rg_listing_type.setValue(ownerShipType)

            if (!listingEditPO.isDraft()) {
                if (ownerShipType == OwnershipType.SALE) {
                    rg_listing_type.disableRentRadios()
                } else {
                    rg_listing_type.disableSaleRadio()
                }
            }
        }

        viewModel.createUpdateListingStatus.observeNotNull(this) { apiStatus ->
            when (apiStatus.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    when (viewModel.purpose.value) {
                        ListingManagementPurpose.CREATE -> {
                            // track listing creation button
                            CreateListingTracker.trackListingCreationStartTime(
                                requireContext(),
                                apiStatus.body?.listingEditPO?.id.toString(),
                                CreateListingTrackerPO(
                                    timeStamp = viewModel.createListingTracker.value?.timeStamp
                                        ?: 0L
                                )
                            )
                            showPostListingScreen(
                                listingId = apiStatus.body?.listingEditPO?.id.toString(),
                                isNewListing = true
                            )
                        }
                        ListingManagementPurpose.UPDATE -> {
                            showPostListingScreen(
                                listingId = apiStatus.body?.listingEditPO?.id.toString(),
                                isNewListing = false
                            )
                        }
                        else -> {
                            //do nothing
                        }
                    }
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(apiStatus.error?.error)
                }
                else -> {
                    //DO nothing
                }
            }
        }

        viewModel.apiError.observeNotNull(this) { ViewUtil.showMessage(it.error) }

        viewModel.hintLandArea.observeNotNull(this) { et_land_area.setHint(it) }

        viewModel.hintBuildArea.observeNotNull(this) { et_built_area.setHint(it) }

        viewModel.listingEditPO.observeNotNull(this) {
            //Update land size and built size
            val landArea = it.landArea.toInt()
            if (landArea > 0) {
                et_land_area.setNumber(landArea)
            } else {
                et_land_area.setNumber(null)
            }

            val builtArea = it.builtArea.toInt()
            if (builtArea > 0) {
                et_built_area.setNumber(builtArea)
            } else {
                et_built_area.setNumber(null)
            }
        }
    }

    private fun setupViewListeners() {
        binding.toolbar.setNavigationOnClickListener {
            when (viewModel.purpose.value) {
                ListingManagementPurpose.CREATE -> {
                    dialog?.dismiss()
                }
                ListingManagementPurpose.UPDATE -> {
                    val listingId =
                        viewModel.listingEditPO.value?.id ?: return@setNavigationOnClickListener
                    showPostListingScreen(listingId = listingId.toString(), isNewListing = false)
                }
                else -> {
                    //do nothing
                }
            }
        }

        //OWNERSHIP TYPE
        rg_listing_type.onSelectOwnershipListener = { ownershipType ->
            if (ownershipType == OwnershipType.ROOM_RENTAL) {
                viewModel.listingEditPO.value?.type = OwnershipType.RENT.value
                viewModel.listingEditPO.value?.roomRental = "true"
            } else {
                viewModel.listingEditPO.value?.type = ownershipType.value
                viewModel.listingEditPO.value?.roomRental = "false"
            }
        }
        //ADDRESS SEARCH
        btn_search_properties.setOnClickListener { showListingAddressSearchScreen() }
        //PROPERTY CLASSIFICATION
        btn_property_classification.setOnClickListener { showPropertyClassificationDialog() }
        //PROPERTY TYPE
        btn_property_type.setOnClickListener { showPropertyTypeDialog() }
        //MODEL
        tv_create_listing_model.setOnClickListener { showModelDialog() }
        //TENURE
        btn_create_listing_tenure.setOnClickListener { showTenureDialog() }
        //BUILT YEAR
        btn_built_year.setOnClickListener { showCompletionYearsDialog() }

        //GET SIZE
        tv_unit.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.getPropertySize()
            }
        }

        tv_unit.setOnEditorActionListener { _, i, _ ->
            return@setOnEditorActionListener if (i == EditorInfo.IME_ACTION_DONE) {
                viewModel.getPropertySize()
                true
            } else {
                false
            }
        }

        //Land Area Size
        et_land_area.setup(NumberUtil.MAX_INT, { number: Int?, numberString: String? ->
            return@setup if (number != null) {
                "$numberString"
            } else {
                viewModel.hintLandArea.value ?: ""
            }
        }, { number: Int? ->
            if (number == null) {
                viewModel.listingEditPO.value?.landArea = 0.0
            } else {
                viewModel.listingEditPO.value?.landArea = number.toDouble()
            }
        })

        //Built Area Size
        et_built_area.setup(NumberUtil.MAX_INT, { number: Int?, numberString: String? ->
            return@setup if (number != null) {
                "$numberString"
            } else {
                viewModel.hintBuildArea.value ?: ""
            }
        }, { number: Int? ->
            if (number == null) {
                viewModel.listingEditPO.value?.builtArea = 0.0
            } else {
                viewModel.listingEditPO.value?.builtArea = number.toDouble()
            }
        })
    }

    //done
    private fun showPropertyClassificationDialog() {
        val classificationList = PropertyClassification.values().toList()
        if (classificationList.isNotEmpty()) {
            dialogUtil.showListDialog(
                classificationList.map { it.label },
                { _, position ->
                    val classification = classificationList[position]
                    viewModel.updatePropertyClassification(classification.value)
                },
                null
            )
        }
    }

    private fun showPropertyTypeDialog() {
        val editPO = viewModel.listingEditPO.value ?: return
        PropertyClassification.values()
            .find { editPO.category == it.value }?.let { classification ->
                val propertyList = classification.propertyTypes.toList()
                return@let dialogUtil.showListDialog(
                    propertyList.map { propertyType -> propertyType.label },
                    { _, position ->
                        val propertySubType = propertyList[position]
                        viewModel.updatePropertyType(propertySubType.type)
                    },
                    null
                )
            }
    }

    private fun showModelDialog() {
        val editPO = viewModel.listingEditPO.value ?: return
        val modelsByPropertyType = PropertyTypeUtil.getModelsByPropertyType(
            editPO.propertyType,
            viewModel.models.value ?: hashMapOf()
        )
        if (modelsByPropertyType.isNotEmpty()) {
            dialogUtil.showWheelPickerDialog(
                modelsByPropertyType,
                { _, position ->
                    val model = modelsByPropertyType[position]
                    //for validation
                    viewModel.isModelValidated.value = model.isNotEmpty()
                    //update model value
                    editPO.model = model
                    viewModel.listingEditPO.value = editPO
                },
                R.string.hint_model,
                modelsByPropertyType.indexOf(editPO.model)
            )
        }
    }

    private fun showTenureDialog() {
        val editPO = viewModel.listingEditPO.value ?: return
        PropertyClassification.values().find { editPO.category == it.value }?.let { category ->
            if (category.tenures.isNotEmpty()) {
                dialogUtil.showListDialog(
                    category.tenures.map { it.label },
                    { _, position ->
                        editPO.tenure = category.tenures[position].value.toString()
                        viewModel.listingEditPO.value = editPO
                    },
                    null
                )
            }
        }
    }

    private fun showCompletionYearsDialog() {
        val editPO = viewModel.listingEditPO.value ?: return
        activity?.run {
            val completionYears =
                ListingInfoUtil.getGeneratedCompletionDates(this.applicationContext)
            return@run dialogUtil.showWheelPickerDialog(
                completionYears,
                { _, position ->
                    val completionYear = completionYears[position]
                    if (NumberUtil.isNaturalNumber(completionYear)) {
                        editPO.propertyCompleteDate =
                            DateTimeUtil.getUnixTimeStamp("$completionYear-01-01")
                    }
                    viewModel.listingEditPO.value = editPO
                },
                R.string.hint_completion_year,
                completionYears.indexOf(editPO.getCompletionYear())
            )
        }
    }

    //done refine
    private fun setupPropertyInfo(property: PropertyPO) {

        val listingEditPO = ListingEditPO()

        listingEditPO.id = viewModel.listingEditPO.value?.id

        val oldListingEditPO = viewModel.listingEditPO.value
        listingEditPO.type = oldListingEditPO?.type ?: OwnershipType.SALE.value
        listingEditPO.roomRental = oldListingEditPO?.roomRental ?: "false"

        listingEditPO.blk = property.buildingNum
        listingEditPO.postalCode = property.postalCode
        listingEditPO.buildingName = property.buildingName
        listingEditPO.streetName = property.streetName
        listingEditPO.address = property.address
        //update view model data
        viewModel.listingEditPO.value = listingEditPO
        viewModel.getPropertyType()
    }

    //done
    private fun showPostListingScreen(listingId: String, isNewListing: Boolean) {
        dialog?.dismiss()
        activity?.run {
            PostListingDialogFragment.launch(
                fragmentManager = supportFragmentManager,
                listingId = listingId,
                isNewListing = isNewListing
            )
        }
    }

    //done
    private fun showListingAddressSearchScreen() {
        activity?.run {
            ListingAddressSearchDialogFragment.launch(
                fragmentManager = supportFragmentManager,
                addressSearchSource = ListingManagementEnum.AddressSearchSource.CREATE_LISTING
            )
        }
    }

    override fun onStart() {
        super.onStart()
        setupFullScreenWindow()
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_create_update_listing
    }

    override fun getViewModelClass(): Class<CreateUpdateListingViewModel> {
        return CreateUpdateListingViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}