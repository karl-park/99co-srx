package sg.searchhouse.agentconnect.view.widget.listing.create

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.card_post_listing_price_and_specific_info.view.*
import kotlinx.android.synthetic.main.edit_text_new_pill_number.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.CardPostListingPriceAndSpecificInfoBinding
import sg.searchhouse.agentconnect.dsl.widget.setOnEditorActionListener
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.ShowHideXValueIndicator
import sg.searchhouse.agentconnect.model.api.listing.ListingEditPO
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.DialogUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.create.PostListingViewModel

class PostListingPriceAndSpecificInfoCard(context: Context, attributeSet: AttributeSet? = null) :
    LinearLayout(context, attributeSet) {

    var viewModel: PostListingViewModel? = null

    val binding: CardPostListingPriceAndSpecificInfoBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.card_post_listing_price_and_specific_info,
        this,
        true
    )

    private val priceTransformer = { number: Int?, numberString: String? ->
        if (number != null) {
            "\$$numberString"
        } else {
            ""
        }
    }

    private var priceOptionsList = emptyList<CreateListingPriceOptionPill>()
    private var roomTypeList = emptyList<CreateListingRoomTypePill>()
    private var leaseTermList = emptyList<CreateListingLeaseTermPill>()
    private var rentalTypeList = emptyList<CreateListingRentalTypePill>()
    private var assignmentTypeList = emptyList<CreateListingAssignmentTypePill>()
    private var tenancyList = emptyList<CreateListingTenancyPill>()


    init {
        setupOnClickListeners()
        setupAskingPrice()
        setupUtilitiesCost()
        setupTenantedAmount()
        setupTakeOverAmount()
    }

    fun populatePriceAndSpecificInfo(listingEditPO: ListingEditPO) {

        binding.listingEditPO = listingEditPO

        populatePriceOptions()
        if (listingEditPO.isSale()) {
            populateTenancyStatus()
        } else {
            if (listingEditPO.isCommercial()) {
                populateRentalType()
            } else {
                populateRoomType()
            }
            populateLeaseTerm()
            populateAssignmentType()
        }

        //asking price
        if (listingEditPO.listedPrice in 1 until NumberUtil.MAX_INT) {
            et_asking_price.setNumber(listingEditPO.listedPrice)
        } else if (listingEditPO.hasXValue()) {
            listingEditPO.listedPrice = listingEditPO.xValue
            et_asking_price.setNumber(listingEditPO.xValue)
        }

        //monthly utilities cost
        if (listingEditPO.monthlyUtilitiesCost in 1 until NumberUtil.MAX_INT) {
            et_utilities_amount.setNumber(listingEditPO.monthlyUtilitiesCost)
        }

        //Tenanted amount
        if (listingEditPO.tenantedAmount in 1 until NumberUtil.MAX_INT) {
            et_tenancy_amount.setNumber(listingEditPO.tenantedAmount)
        }

        //Takeover amount
        if (listingEditPO.takeoverAmount in 1 until NumberUtil.MAX_INT) {
            et_assignment_type_amount.setNumber(listingEditPO.takeoverAmount)
        }
    }

    fun updatePriceAndSpecificCard(editPO: ListingEditPO) {
        binding.listingEditPO = editPO

        priceOptionsList.map { it.binding.invalidateAll() }
        roomTypeList.map { it.binding.invalidateAll() }
        leaseTermList.map { it.binding.invalidateAll() }
        rentalTypeList.map { it.binding.invalidateAll() }
        assignmentTypeList.map { it.binding.invalidateAll() }
        tenancyList.map { it.binding.invalidateAll() }
    }

    fun updateListingEditPO(editPO: ListingEditPO) {
        binding.listingEditPO = editPO
    }

    private fun setupOnClickListeners() {
        //Show X Value or Not
        switch_show_x_value.setOnCheckedChangeListener { _, isChecked ->
            val editPO = viewModel?.listingEditPO?.value ?: return@setOnCheckedChangeListener
            if (isChecked) {
                editPO.xValueDisplayInd = ShowHideXValueIndicator.SHOW.value
            } else {
                editPO.xValueDisplayInd = ShowHideXValueIndicator.HIDE.value
            }
            viewModel?.updateListingEditPO()
        }

        rl_price_options_header.setOnClickListener {
            binding.isExpandPriceOptions = binding.isExpandPriceOptions != true
        }

        rl_room_type.setOnClickListener {
            binding.isExpandRoomType = binding.isExpandRoomType != true
        }

        rl_lease_terms.setOnClickListener {
            binding.isExpandLeaseTerm = binding.isExpandLeaseTerm != true
        }

        rl_rental_type.setOnClickListener {
            binding.isExpandRentalType = binding.isExpandRentalType != true
        }

        rl_tenancy_status.setOnClickListener {
            binding.isExpandTenancyStatus = binding.isExpandTenancyStatus != true
        }

        rl_assignment_type.setOnClickListener {
            binding.isExpandAssignmentType = binding.isExpandAssignmentType != true
        }

        tv_available_date.setOnClickListener { showDatePickerDialog() }
    }

    //Asking Price or Listed Price
    private fun setupAskingPrice() {
        //set up
        et_asking_price.setup(NumberUtil.MAX_INT, priceTransformer, { number: Int? ->
            val editPO = viewModel?.listingEditPO?.value ?: return@setup
            editPO.listedPrice = number ?: 0
        })

        et_asking_price.et_display.imeOptions = EditorInfo.IME_ACTION_DONE
        et_asking_price.et_display.setTextColor(ContextCompat.getColor(context, R.color.purple))

        et_asking_price.et_display.setOnFocusChangeListener { _, isFocus ->
            if (!isFocus) {
                viewModel?.updateListingEditPO()
            }
        }

        //action done
        et_asking_price.et_display.imeOptions = EditorInfo.IME_ACTION_DONE
        et_asking_price.et_display.setOnEditorActionListener(onActionDone = {
            ViewUtil.hideKeyboard(et_asking_price.et_display)
            et_asking_price.et_display.clearFocus()
        })
    }

    //Utilities Cost RENT
    private fun setupUtilitiesCost() {
        et_utilities_amount.setup(NumberUtil.MAX_INT, priceTransformer, { number: Int? ->
            val editPO = viewModel?.listingEditPO?.value ?: return@setup
            editPO.monthlyUtilitiesCost = number ?: -1 //reset applicable
        })

        et_utilities_amount.et_display.setTextColor(ContextCompat.getColor(context, R.color.purple))
        et_utilities_amount.setHintColor(R.color.light_gray_invertible)
        et_utilities_amount.setHint(context.getString(R.string.hint_enter_utilities_cost))

        et_utilities_amount.et_display.setOnFocusChangeListener { _, isFocus ->
            if (!isFocus) {
                viewModel?.updateListingEditPO()
            }
        }

        et_utilities_amount.et_display.imeOptions = EditorInfo.IME_ACTION_DONE
        et_utilities_amount.et_display.setOnEditorActionListener(onActionDone = {
            ViewUtil.hideKeyboard(et_utilities_amount.et_display)
            et_utilities_amount.et_display.clearFocus()
        })
    }

    private fun setupTenantedAmount() {
        et_tenancy_amount.setup(NumberUtil.MAX_INT, priceTransformer, { number: Int? ->
            val editPO = viewModel?.listingEditPO?.value ?: return@setup
            editPO.tenantedAmount = number ?: -1 //reset applicable
        })
        et_tenancy_amount.et_display.setTextColor(ContextCompat.getColor(context, R.color.purple))
        et_tenancy_amount.setHint(context.getString(R.string.hint_enter_tenancy_amount))
        et_tenancy_amount.setHintColor(R.color.light_gray_invertible)

        et_tenancy_amount.et_display.setOnFocusChangeListener { _, isFocus ->
            if (!isFocus) {
                viewModel?.updateListingEditPO()
            }
        }
        et_tenancy_amount.et_display.imeOptions = EditorInfo.IME_ACTION_DONE
        et_tenancy_amount.et_display.setOnEditorActionListener(onActionDone = {
            ViewUtil.hideKeyboard(et_tenancy_amount.et_display)
            et_tenancy_amount.et_display.clearFocus()
        })
    }

    private fun setupTakeOverAmount() {
        et_assignment_type_amount.setup(NumberUtil.MAX_INT, priceTransformer, { number: Int? ->
            //todo: to assign zero for not takeover
            val editPO = viewModel?.listingEditPO?.value ?: return@setup
            editPO.takeoverAmount = number ?: -1
        })

        et_assignment_type_amount.et_display.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.purple
            )
        )
        et_assignment_type_amount.setHint(context.getString(R.string.hint_enter_takeover_amount))
        et_assignment_type_amount.setHintColor(R.color.light_gray_invertible)
        et_assignment_type_amount.et_display.setOnFocusChangeListener { _, isFocus ->
            if (!isFocus) {
                viewModel?.updateListingEditPO()
            }
        }

        et_assignment_type_amount.et_display.imeOptions = EditorInfo.IME_ACTION_DONE
        et_assignment_type_amount.et_display.setOnEditorActionListener(onActionDone = {
            ViewUtil.hideKeyboard(et_assignment_type_amount.et_display)
            et_assignment_type_amount.et_display.clearFocus()
        })
    }

    private fun populatePriceOptions() {
        priceOptionsList =
            ListingManagementEnum.PriceOption.values().map { addEachPriceOption(priceOption = it) }
    }

    private fun addEachPriceOption(priceOption: ListingManagementEnum.PriceOption): CreateListingPriceOptionPill {
        val pill = CreateListingPriceOptionPill(context)
        pill.binding.priceOption = priceOption
        pill.binding.viewModel = viewModel
        pill.binding.btnPriceOption.setOnClickListener {
            val editPO = viewModel?.listingEditPO?.value ?: return@setOnClickListener
            val selectedValue = context.resources.getString(priceOption.value)
            if (selectedValue == editPO.listedPriceModel) {
                editPO.listedPriceModel = "undefined"//reset applicable
            } else {
                editPO.listedPriceModel = selectedValue
            }
            viewModel?.isUpdateSpecificInfoCard?.postValue(true)
            viewModel?.updateListingEditPO()
        }
        flex_box_layout_price_options.addView(pill)
        return pill
    }

    //RENT
    private fun populateRoomType() {
        roomTypeList =
            ListingManagementEnum.RoomType.values().map { addEachRoomType(roomType = it) }
    }

    private fun addEachRoomType(roomType: ListingManagementEnum.RoomType): CreateListingRoomTypePill {
        val pill = CreateListingRoomTypePill(context)
        pill.binding.roomType = roomType
        pill.binding.viewModel = viewModel
        pill.binding.btnRoomType.setOnClickListener {
            val editPO = viewModel?.listingEditPO?.value ?: return@setOnClickListener
            if (editPO.roomType == roomType.value) {
                editPO.roomType = -1 //reset applicable
            } else {
                editPO.roomType = roomType.value
            }
            viewModel?.isUpdateSpecificInfoCard?.postValue(true)
            viewModel?.updateListingEditPO()
        }
        flex_box_room_type.addView(pill)
        return pill
    }

    //RENT
    private fun populateLeaseTerm() {
        leaseTermList =
            ListingManagementEnum.LeaseTerm.values().map { addEachLeaseTerm(leaseTerm = it) }
    }

    private fun addEachLeaseTerm(leaseTerm: ListingManagementEnum.LeaseTerm): CreateListingLeaseTermPill {
        val pill = CreateListingLeaseTermPill(context)
        pill.binding.leaseTerm = leaseTerm
        pill.binding.viewModel = viewModel
        pill.binding.btnLeaseTerm.setOnClickListener {
            val editPO = viewModel?.listingEditPO?.value ?: return@setOnClickListener
            if (editPO.leaseTerm != leaseTerm.value) {
                editPO.leaseTerm = leaseTerm.value
                viewModel?.isUpdateSpecificInfoCard?.postValue(true)
                viewModel?.updateListingEditPO()
            }
        }
        flex_box_lease_terms.addView(pill)
        return pill
    }

    //Commercial Rent
    private fun populateRentalType() {
        rentalTypeList =
            ListingManagementEnum.RentalType.values().map { addEachRentalType(rentalType = it) }
    }

    private fun addEachRentalType(rentalType: ListingManagementEnum.RentalType): CreateListingRentalTypePill {
        val pill = CreateListingRentalTypePill(context)
        pill.binding.rentalType = rentalType
        pill.binding.viewModel = viewModel
        pill.binding.btnRentalType.setOnClickListener {
            val editPO = viewModel?.listingEditPO?.value ?: return@setOnClickListener
            if (editPO.roomRental != rentalType.value) {
                editPO.roomRental = rentalType.value
                viewModel?.isUpdateSpecificInfoCard?.postValue(true)
                viewModel?.updateListingEditPO()
            }
        }
        flex_box_rental_type.addView(pill)
        return pill
    }

    private fun populateAssignmentType() {
        assignmentTypeList = ListingManagementEnum.AssignmentType.values()
            .map { addEachAssignmentType(assignmentType = it) }
    }

    private fun addEachAssignmentType(assignmentType: ListingManagementEnum.AssignmentType): CreateListingAssignmentTypePill {
        val pill = CreateListingAssignmentTypePill(context)
        pill.binding.assignmentType = assignmentType
        pill.binding.viewModel = viewModel
        pill.binding.btnAssignmentType.setOnClickListener {
            val editPO = viewModel?.listingEditPO?.value ?: return@setOnClickListener
            if (editPO.takeover != assignmentType.value) {
                editPO.takeover = assignmentType.value
                if (editPO.takeover == ListingManagementEnum.AssignmentType.NO_TAKEOVER.value) {
                    editPO.takeoverAmount = 0
                    et_assignment_type_amount.clear()
                }
                viewModel?.isUpdateSpecificInfoCard?.postValue(true)
                viewModel?.updateListingEditPO()
            }
        }
        flex_box_assignment_types.addView(pill)
        return pill
    }

    private fun populateTenancyStatus() {
        tenancyList =
            ListingManagementEnum.TenancyStatus.values().map { addEachTenancy(tenancy = it) }
    }

    private fun addEachTenancy(tenancy: ListingManagementEnum.TenancyStatus): CreateListingTenancyPill {
        val pill = CreateListingTenancyPill(context)
        pill.binding.tenancyStatus = tenancy
        pill.binding.viewModel = viewModel
        pill.binding.btnTenancyStatus.setOnClickListener {
            val editPO = viewModel?.listingEditPO?.value ?: return@setOnClickListener
            if (editPO.tenanted != tenancy.value) {
                editPO.tenanted = tenancy.value
                if (editPO.tenanted == ListingManagementEnum.TenancyStatus.NOT_TENANTED.value) {
                    editPO.tenantedAmount = 0
                    et_tenancy_amount.clear()
                }
                viewModel?.isUpdateSpecificInfoCard?.postValue(true)
                viewModel?.updateListingEditPO()
            }
        }
        flex_box_layout_tenancy_status.addView(pill)
        return pill
    }

    private fun showDatePickerDialog() {
        val editPO = viewModel?.listingEditPO?.value ?: return
        val formattedDate = if (TextUtils.isEmpty(editPO.availableDate)) {
            null
        } else {
            DateTimeUtil.convertStringToDate(editPO.availableDate, DateTimeUtil.FORMAT_DATE_2)
        }

        DialogUtil(context).showDatePickerDialog(
            initialDate = formattedDate,
            onDateChangedListener = { year: Int, monthOfYear: Int, dayOfMonth: Int ->
                editPO.availableDate = String.format("%02d", dayOfMonth) +
                        "-" + String.format("%02d", monthOfYear + 1) +
                        "-$year"
                viewModel?.isUpdateSpecificInfoCard?.postValue(true)
                viewModel?.updateListingEditPO()
            }
        )
    }
}