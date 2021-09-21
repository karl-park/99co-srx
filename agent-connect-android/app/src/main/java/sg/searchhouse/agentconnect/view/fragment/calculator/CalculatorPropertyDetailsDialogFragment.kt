package sg.searchhouse.agentconnect.view.fragment.calculator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import kotlinx.android.synthetic.main.dialog_fragment_calculator_property_details.*
import kotlinx.android.synthetic.main.layout_calculator_property_details_form_basic.*
import kotlinx.android.synthetic.main.layout_calculator_property_details_form_landed.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentCalculatorPropertyDetailsBinding
import sg.searchhouse.agentconnect.dsl.widget.hideKeyboard
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.XValueEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.xvalue.SearchWithWalkupResponse
import sg.searchhouse.agentconnect.model.app.XValueEntryParams
import sg.searchhouse.agentconnect.event.calculator.EnterDetailsEvent
import sg.searchhouse.agentconnect.event.calculator.GetSellingCalculatorXValueEvent
import sg.searchhouse.agentconnect.event.calculator.PopulateDefaultPropertyTextBoxesEvent
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.AuthUtil
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.xvalue.XValueAddressSearchActivity
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFullWidthDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.calculator.CalculatorPropertyDetailsDialogViewModel

class CalculatorPropertyDetailsDialogFragment :
    ViewModelFullWidthDialogFragment<CalculatorPropertyDetailsDialogViewModel, DialogFragmentCalculatorPropertyDetailsBinding>() {
    private val areaTypes = XValueEnum.AreaType.values()
    private val areaTypeLabels = areaTypes.map { it.label }

    private val tenures = XValueEnum.Tenure.values()
    private val tenureLabels = tenures.map { it.label }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
        listenRxBuses()
        setOnClickListeners()
        setupViews()
        maybePopulateDefaultParams()
    }

    private fun listenRxBuses() {
        listenRxBus(PopulateDefaultPropertyTextBoxesEvent::class.java) {
            populateDefaultEntryParams(it.xValueEntryParams)
        }
    }

    private fun maybePopulateDefaultParams() {
        viewModel.defaultWalkupResponseData =
            arguments?.getSerializable(ARGUMENT_WALKUP_RESPONSE_DATA) as SearchWithWalkupResponse.Data?
        viewModel.defaultXValueEntryParams =
            arguments?.getSerializable(ARGUMENT_X_VALUE_ENTRY_PARAMS) as XValueEntryParams?
        if (viewModel.hasDefaultParams()) {
            viewModel.property.postValue(viewModel.defaultWalkupResponseData)
        }
    }

    private fun populateDefaultEntryParams(xValueEntryParams: XValueEntryParams) {
        xValueEntryParams.run {
            et_built_area?.setNumber(area)
            et_ext_area?.setNumber(areaExt)
            et_area_gfa?.setNumber(areaGfa)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.page.postValue(1)
    }

    private fun setupViews() {
        setupNumberBoxes()
        setupDropDowns()
    }

    private fun setupDropDowns() {
        btn_area_type.setOnClickListener {
            dialogUtil.showListDialog(areaTypeLabels, { _, position ->
                viewModel.areaType.postValue(areaTypes[position])
            }, R.string.hint_area_type)
        }

        btn_tenure.setOnClickListener {
            dialogUtil.showListDialog(tenureLabels, { _, position ->
                viewModel.tenure.postValue(tenures[position])
            }, R.string.hint_tenure)
        }

        btn_property_type.setOnClickListener {
            val availableSubTypes = getAvailableSubTypes()
            val subTypeLabels = availableSubTypes.map { it.label }
            dialogUtil.showListDialog(subTypeLabels, { _, position ->
                viewModel.propertySubType.postValue(availableSubTypes[position])
            }, R.string.dialog_title_property_types)
        }
    }

    private fun getAvailableSubTypes(): List<ListingEnum.PropertySubType> {
        // TODO: Refactor into view model when free
        return when {
            viewModel.isHdb() -> ListingEnum.PropertyMainType.HDB.propertySubTypes
            viewModel.isCondo() -> ListingEnum.PropertyMainType.CONDO.propertySubTypes
            viewModel.isLanded() -> ListingEnum.PropertyMainType.LANDED.propertySubTypes
            else -> {
                ListingEnum.PropertyMainType.HDB.propertySubTypes +
                        ListingEnum.PropertyMainType.CONDO.propertySubTypes +
                        ListingEnum.PropertyMainType.LANDED.propertySubTypes +
                        ListingEnum.PropertyMainType.COMMERCIAL.propertySubTypes
            }
        }
    }

    private fun setupNumberBoxes() {
        et_built_area.setup(numberTextTransformer = { number, formattedNumberString ->
            if (number != null) {
                "$formattedNumberString"
            } else {
                when (viewModel.isCurrentSelectionHdb()) {
                    true -> getString(R.string.hint_area_sqm)
                    false -> getString(R.string.hint_area_sqft)
                }
            }
        }, onNumberChangeListener = {
            viewModel.area.postValue(it)
        })
        et_built_area.setHintColor(R.color.black_invertible)

        et_ext_area.setup(numberTextTransformer = { number, formattedNumberString ->
            if (number != null) {
                "$formattedNumberString"
            } else {
                getString(R.string.hint_area_ext)
            }
        }, onNumberChangeListener = {
            viewModel.areaExt.postValue(it)
        })

        et_area_gfa.setup(numberTextTransformer = { number, formattedNumberString ->
            if (number != null) {
                "$formattedNumberString"
            } else {
                getString(R.string.hint_area_gfa)
            }
        }, onNumberChangeListener = {
            viewModel.areaGfa.postValue(it)
        })

        et_built_year.setup(DateTimeUtil.getCurrentYear(), numberTextTransformer = { number, _ ->
            if (number != null) {
                "$number"
            } else {
                getString(R.string.hint_built_year)
            }
        }, onNumberChangeListener = {
            viewModel.builtYear.postValue(it)
        })
    }

    private fun observeLiveData() {
        viewModel.property.observeDismissError { data ->
            data?.run { viewModel.requestPropertyType(this) }
            btn_search.text = data?.address
        }

        viewModel.propertyType.observe(viewLifecycleOwner) {
            // This is to be in sync with viewModel.reset()
            // TODO: Do binding for number boxes to remove this hardcoding
            if (!viewModel.hasDefaultParams()) {
                resetNumberBoxes()
            }
        }

        viewModel.propertySubType.observeDismissError {
            if (it != null) {
                et_built_area?.resetHint() // To toggle area unit between sqft and sqm
                viewModel.isPropertySubTypeValidated.postValue(true)
            }
            viewModel.resetValidations()
        }

        viewModel.unitFloor.observeNotNullDismissError {
            viewModel.isUnitFloorValidated.postValue(true)
        }

        viewModel.unitNumber.observeNotNullDismissError {
            viewModel.isUnitNumberValidated.postValue(true)
        }

        viewModel.area.observeNotNullDismissError {
            viewModel.isAreaValidated.postValue(true)
        }

        viewModel.areaType.observeNotNullDismissError {
            viewModel.isAreaTypeValidated.postValue(true)
        }

        viewModel.tenure.observeDismissError {
            viewModel.isTenureValidated.postValue(true)
        }

        viewModel.builtYear.observeDismissError {
            viewModel.isBuiltYearValidated.postValue(true)
        }

        viewModel.getProjectResponse.observe(viewLifecycleOwner) {
            et_built_area.setNumber(it?.data?.getOrNull(0)?.size)
        }

        viewModel.shouldPerformGetProject.observe(viewLifecycleOwner) {
            if (it == true) {
                viewModel.performGetProject()
            }
        }

        viewModel.mainResponse.observeNotNull(viewLifecycleOwner) {
            val xValue = it.xvalue?.xValue ?: return@observeNotNull
            val address = viewModel.property.value?.address ?: ""

            val entryParams = viewModel.getEntryParams()
            val walkupResponseData = viewModel.property.value
                ?: throw IllegalStateException("`walkupResponseData` should be present here!")

            RxBus.publish(
                GetSellingCalculatorXValueEvent(
                    xValue,
                    address,
                    entryParams,
                    walkupResponseData
                )
            )
            dismiss()
        }

        viewModel.mainStatus.observeNotNull(viewLifecycleOwner) { statusKey ->
            when (statusKey) {
                ApiStatus.StatusKey.FAIL -> {
                    viewModel.isShowPropertyNotFoundError.postValue(true)
                    ViewUtil.showMessage(R.string.toast_calculate_x_value_fail)
                }
                ApiStatus.StatusKey.ERROR -> {
                    ViewUtil.showMessage(R.string.toast_calculate_x_value_error)
                    viewModel.isShowPropertyNotFoundError.postValue(true)
                }
                else -> {
                }
            }
        }

        viewModel.isShowPropertyNotFoundError.observe(viewLifecycleOwner) {
            if (it == true) {
                layout_container.hideKeyboard()
            }
        }
    }

    // Observe and dismiss property not found error upon param update
    private fun <T> LiveData<T>.observeDismissError(onDataChanged: (T?) -> Unit) =
        observe(viewLifecycleOwner) {
            onDataChanged.invoke(it)
            viewModel.isShowPropertyNotFoundError.postValue(false)
        }

    // Observe and dismiss property not found error upon param update
    private fun <T> LiveData<T>.observeNotNullDismissError(onDataChanged: (T?) -> Unit) =
        observeNotNull(viewLifecycleOwner) {
            onDataChanged.invoke(it)
            viewModel.isShowPropertyNotFoundError.postValue(false)
        }

    private fun resetNumberBoxes() {
        et_built_area.clear()
        et_ext_area.clear()
        et_area_gfa.clear()
        et_built_year.clear()
    }

    private fun setOnClickListeners() {
        btn_search.setOnClickListener {
            checkXValueModuleAccessibility {
                launchActivityForResult(
                    XValueAddressSearchActivity::class.java,
                    requestCode = REQUEST_CODE_GET_ADDRESS
                )
            }
        }
        btn_get_size.setOnClickListener { viewModel.performGetProject() }
        btn_calculate.setOnClickListener { checkXValueModuleAccessibility { viewModel.calculateXValue() } }
        btn_enter_details.setOnClickListener {
            RxBus.publish(EnterDetailsEvent())
            dismiss()
        }
    }

    private fun checkXValueModuleAccessibility(onSuccessAccessibility: (() -> Unit)) {
        AuthUtil.checkModuleAccessibility(
            module = AccessibilityEnum.AdvisorModule.X_VALUE,
            onSuccessAccessibility = { onSuccessAccessibility.invoke() }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_GET_ADDRESS -> {
                if (resultCode == Activity.RESULT_OK) {
                    val property =
                        data?.getSerializableExtra(XValueAddressSearchActivity.EXTRA_RESULT_PROPERTY) as SearchWithWalkupResponse.Data?
                    viewModel.property.postValue(property)
                }
            }
            REQUEST_CODE_GET_EXISTING_X_VALUE -> {
                if (resultCode == Activity.RESULT_OK) {
                    activity?.finish()
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    companion object {
        const val REQUEST_CODE_GET_ADDRESS = 1
        const val REQUEST_CODE_GET_EXISTING_X_VALUE = 2

        private const val TAG = "CalculatorPropertyDetailsDialogFragment"

        private const val ARGUMENT_X_VALUE_ENTRY_PARAMS = "ARGUMENT_X_VALUE_ENTRY_PARAMS"
        private const val ARGUMENT_WALKUP_RESPONSE_DATA = "ARGUMENT_WALKUP_RESPONSE_DATA"

        @JvmStatic
        fun newInstance(
            xValueEntryParams: XValueEntryParams? = null,
            walkupResponseData: SearchWithWalkupResponse.Data? = null
        ): CalculatorPropertyDetailsDialogFragment {
            val arguments = Bundle()
            arguments.putSerializable(ARGUMENT_X_VALUE_ENTRY_PARAMS, xValueEntryParams)
            arguments.putSerializable(ARGUMENT_WALKUP_RESPONSE_DATA, walkupResponseData)
            val fragment = CalculatorPropertyDetailsDialogFragment()
            fragment.arguments = arguments
            return fragment
        }

        fun launch(
            supportFragmentManager: FragmentManager,
            xValueEntryParams: XValueEntryParams? = null,
            walkupResponseData: SearchWithWalkupResponse.Data? = null
        ) {
            newInstance(xValueEntryParams, walkupResponseData).show(supportFragmentManager, TAG)
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_calculator_property_details
    }

    override fun getViewModelClass(): Class<CalculatorPropertyDetailsDialogViewModel> {
        return CalculatorPropertyDetailsDialogViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}