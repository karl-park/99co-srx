package sg.searchhouse.agentconnect.view.widget.listing.create

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.CardPostListingAdditionalInfoBinding
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.model.api.lookup.LookupBedroomsResponse
import sg.searchhouse.agentconnect.util.DialogUtil
import sg.searchhouse.agentconnect.util.ListingInfoUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.StringUtil
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.create.PostListingViewModel

class PostListingAdditionalInfoCard(context: Context, attributeSet: AttributeSet? = null) :
    LinearLayout(context, attributeSet) {

    private val dialogNewUtil = DialogUtil(context)

    private var viewModel: PostListingViewModel? = null

    //bedroom
    var bedrooms = emptyList<LookupBedroomsResponse.Bedroom>()
    private var preSelectBedRoom =
        LookupBedroomsResponse.Bedroom(0, context.getString(R.string.label_select))
    private var selectedBedRoom = preSelectBedRoom

    //bathroom
    private var bathrooms = emptyList<String>()
    private var preSelectBathRoom = context.getString(R.string.label_select)
    private var selectedBathRoom = preSelectBathRoom


    private var ownerTypeList = emptyList<CreateListingOwnerTypePill>()
    private var furnishLevelList = emptyList<CreateListingFurnishLevelPill>()
    private var floorLocationList = emptyList<CreateListingFloorPill>()
    private var furnishList = emptyList<CreateListingFurnishPill>()

    val binding: CardPostListingAdditionalInfoBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.card_post_listing_additional_info,
        this,
        true
    )

    init {
        setupDropDownOnClickListeners()
    }

    private fun setupDropDownOnClickListeners() {
        binding.dropdownBedrooms.setOnClickListener {
            if (bedrooms.isNotEmpty()) {
                val bedRoomsLabel = bedrooms.map { it.description }
                dialogNewUtil.showWheelPickerDialog(
                    bedRoomsLabel,
                    { _, position ->
                        val tempBedRoom = bedrooms[position]
                        val editPO = viewModel?.listingEditPO?.value ?: return@showWheelPickerDialog
                        editPO.bedrooms = tempBedRoom.id

                        selectedBedRoom = tempBedRoom
                        binding.bedRoomDescription = tempBedRoom.description

                        viewModel?.updateListingEditPO()
                    },
                    R.string.label_bedrooms,
                    bedRoomsLabel.indexOf(selectedBedRoom.description)
                )
            }
        }

        binding.dropdownBathrooms.setOnClickListener {
            if (bathrooms.isNotEmpty()) {
                dialogNewUtil.showWheelPickerDialog(
                    bathrooms,
                    { _, position ->
                        val bathroom = bathrooms[position]
                        val editPO = viewModel?.listingEditPO?.value ?: return@showWheelPickerDialog
                        if (NumberUtil.isInt(bathroom)) {
                            editPO.bathroom = bathrooms[position].toInt()
                        } else {
                            editPO.bathroom = -1 //reset applicable
                        }
                        selectedBathRoom = bathroom
                        viewModel?.isUpdateAdditionalInfoCard?.postValue(true)
                        viewModel?.updateListingEditPO()
                    },
                    R.string.label_bathrooms,
                    bathrooms.indexOf(selectedBathRoom)
                )
            }
        }
    }

    fun updateViewModel(postListingViewModel: PostListingViewModel) {
        val listingEditPO = postListingViewModel.listingEditPO.value ?: return

        //update view model
        viewModel = postListingViewModel
        binding.listingEditPO = listingEditPO

        //populate selected bedrooms
        selectedBedRoom = bedrooms.find { it.id == listingEditPO.bedrooms } ?: preSelectBedRoom
        binding.bedRoomDescription = selectedBedRoom.description

        //populate selected bathroom
        selectedBathRoom = listingEditPO.bathroom.toString()

        ownerTypeList.map { it.binding.invalidateAll() }
        furnishLevelList.map { it.binding.invalidateAll() }
        floorLocationList.map { it.binding.invalidateAll() }
        furnishList.map { it.binding.invalidateAll() }
    }

    fun populateBedrooms(items: List<LookupBedroomsResponse.Bedroom>) {
        bedrooms = listOf(preSelectBedRoom) + items
    }

    fun populateAdditionalInformation() {
        val editPO = viewModel?.listingEditPO?.value ?: return
        populateBathrooms()
        populateFloorLocation()
        if (editPO.isCommercial()) {
            populateOwnerType()
            populateFurnishLevel()
        } else {
            populateFurnished()
        }
    }

    private fun populateBathrooms() {
        bathrooms = ListingInfoUtil.getBathRooms(context)
    }

    private fun populateFloorLocation() {
        floorLocationList = ListingEnum.Floor.values().map { addEachFloorLocation(floor = it) }
    }

    private fun addEachFloorLocation(floor: ListingEnum.Floor): CreateListingFloorPill {
        val pill = CreateListingFloorPill(context)
        pill.binding.floor = floor
        pill.binding.viewModel = viewModel
        pill.binding.btnFloorLocation.setOnClickListener {
            val editPO = viewModel?.listingEditPO?.value ?: return@setOnClickListener
            if (StringUtil.equals(editPO.floor?.trim(), floor.value.trim(), ignoreCase = true)) {
                editPO.floor = "undefined" //reset applicable
            } else {
                editPO.floor = StringUtil.toUpperCase(floor.value)
            }
            viewModel?.isUpdateAdditionalInfoCard?.postValue(true)
            viewModel?.updateListingEditPO()
        }
        binding.flexBoxLayoutFloor.addView(pill)
        return pill
    }

    private fun populateOwnerType() {
        ownerTypeList =
            ListingManagementEnum.OwnerType.values().map { addEachOwnerType(ownerType = it) }
    }

    private fun addEachOwnerType(ownerType: ListingManagementEnum.OwnerType): CreateListingOwnerTypePill {
        val pill = CreateListingOwnerTypePill(context)
        pill.binding.ownerType = ownerType
        pill.binding.viewModel = viewModel
        pill.binding.btnOwnerType.setOnClickListener {
            val editPO = viewModel?.listingEditPO?.value ?: return@setOnClickListener
            if (editPO.ownerType == ownerType.value) {
                editPO.ownerType = -1 //reset applicable
            } else {
                editPO.ownerType = ownerType.value
            }
            viewModel?.isUpdateAdditionalInfoCard?.postValue(true)
            viewModel?.updateListingEditPO()
        }
        binding.flexBoxOwnerTypes.addView(pill)
        return pill
    }

    private fun populateFurnishLevel() {
        furnishLevelList =
            ListingManagementEnum.FurnishLevel.values().map { addEachFurnishLevel(furnish = it) }
    }

    private fun addEachFurnishLevel(furnish: ListingManagementEnum.FurnishLevel): CreateListingFurnishLevelPill {
        val pill = CreateListingFurnishLevelPill(context)
        pill.binding.furnishLevel = furnish
        pill.binding.viewModel = viewModel
        pill.binding.btnFurnishLevel.setOnClickListener {
            val editPO = viewModel?.listingEditPO?.value ?: return@setOnClickListener
            if (editPO.furnishLevel == furnish.value) {
                editPO.furnishLevel = "undefined" //reset applicable
            } else {
                editPO.furnishLevel = furnish.value
            }
            viewModel?.isUpdateAdditionalInfoCard?.postValue(true)
            viewModel?.updateListingEditPO()
        }
        binding.flexBoxFurnishLevel.addView(pill)
        return pill
    }

    private fun populateFurnished() {
        furnishList = ListingEnum.Furnish.values().map { addEachFurnish(furnish = it) }
    }

    private fun addEachFurnish(furnish: ListingEnum.Furnish): CreateListingFurnishPill {
        val pill = CreateListingFurnishPill(context)
        pill.binding.furnish = furnish
        pill.binding.viewModel = viewModel
        pill.binding.btnFurnish.setOnClickListener {
            val editPO = viewModel?.listingEditPO?.value ?: return@setOnClickListener
            if (editPO.furnish == furnish.value) {
                editPO.furnish = "undefined" //reset applicable
            } else {
                editPO.furnish = furnish.value
            }
            viewModel?.isUpdateAdditionalInfoCard?.postValue(true)
            viewModel?.updateListingEditPO()
        }
        binding.flexBoxLayoutFurnished.addView(pill)
        return pill
    }
}