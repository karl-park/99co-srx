package sg.searchhouse.agentconnect.view.fragment.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_search_x_value.*
import kotlinx.android.synthetic.main.layout_x_value_search_form_basic.*
import kotlinx.android.synthetic.main.layout_x_value_search_form_landed.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentSearchXValueBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.XValueEnum
import sg.searchhouse.agentconnect.model.api.xvalue.SearchWithWalkupResponse
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.model.app.XValueEntryParams
import sg.searchhouse.agentconnect.util.AuthUtil
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.xvalue.XValueActivity
import sg.searchhouse.agentconnect.view.activity.xvalue.XValueAddressSearchActivity
import sg.searchhouse.agentconnect.view.activity.xvalue.XValueSearchActivity
import sg.searchhouse.agentconnect.view.adapter.xvalue.XValueExistingResultAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.search.SearchXValueViewModel

/**
 * Search X value
 */
class SearchXValueFragment :
    ViewModelFragment<SearchXValueViewModel, FragmentSearchXValueBinding>() {
    private val areaTypes = XValueEnum.AreaType.values()
    private val areaTypeLabels = areaTypes.map { it.label }

    private val tenures = XValueEnum.Tenure.values()
    private val tenureLabels = tenures.map { it.label }

    private val adapter = XValueExistingResultAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
        setOnClickListeners()
        setupViews()
    }

    override fun onResume() {
        super.onResume()
        list.scrollTo(0, 0)
        viewModel.page.postValue(1)
    }

    private fun setupViews() {
        setupNumberBoxes()
        setupDropDowns()
        setupList()
    }

    private fun setupList() {
        adapter.onSelectItem = { result ->
            AuthUtil.checkModuleAccessibility(
                module = AccessibilityEnum.AdvisorModule.X_VALUE,
                onSuccessAccessibility = {
                    activity?.run { XValueActivity.launch(this, result) }
                }
            )
        }
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = adapter
        ViewUtil.listenVerticalScrollEnd(list, reachBottom = {
            if (viewModel.canLoadNext()) {
                viewModel.page.postValue((viewModel.page.value ?: 1) + 1)
            }
        })
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
        viewModel.listItems.observe(viewLifecycleOwner) {
            adapter.listItems = it ?: emptyList()
            adapter.notifyDataSetChanged()
        }

        viewModel.mainResponse.observe(viewLifecycleOwner) { getExistingXValuesResponse ->
            getExistingXValuesResponse?.let {
                val newItems = it.xvalues.results
                viewModel.listItems.postValue(
                    when (viewModel.page.value) {
                        1 -> newItems
                        else -> {
                            val originalItems = viewModel.listItems.value ?: emptyList()
                            originalItems + newItems
                        }
                    }
                )
            }
        }

        viewModel.property.observeNotNull(viewLifecycleOwner) {
            viewModel.requestPropertyType(it)
            btn_search.text = it.address
        }

        viewModel.propertyType.observe(viewLifecycleOwner) {
            // This is to be in sync with viewModel.reset()
            // TODO: Do binding for number boxes to remove this hardcoding
            resetNumberBoxes()
        }

        viewModel.propertySubType.observe(viewLifecycleOwner) {
            if (it != null) {
                et_built_area.resetHint() // To toggle area unit between sqft and sqm
                viewModel.isPropertySubTypeValidated.postValue(true)
            }
            viewModel.resetValidations()
        }

        viewModel.unitFloor.observeNotNull(viewLifecycleOwner) {
            viewModel.isUnitFloorValidated.postValue(true)
        }

        viewModel.unitNumber.observeNotNull(viewLifecycleOwner) {
            viewModel.isUnitNumberValidated.postValue(true)
        }

        viewModel.area.observeNotNull(viewLifecycleOwner) {
            viewModel.isAreaValidated.postValue(true)
        }

        viewModel.areaType.observeNotNull(viewLifecycleOwner) {
            viewModel.isAreaTypeValidated.postValue(true)
        }

        viewModel.tenure.observeNotNull(viewLifecycleOwner) {
            viewModel.isTenureValidated.postValue(true)
        }

        viewModel.builtYear.observeNotNull(viewLifecycleOwner) {
            viewModel.isBuiltYearValidated.postValue(true)
        }

        viewModel.getProjectResponse.observe(viewLifecycleOwner) {
            et_built_area.setNumber(it?.data?.getOrNull(0)?.size)
        }

        viewModel.page.observeNotNull(viewLifecycleOwner) { page ->
            if (page > 1) {
                adapter.listItems += Loading()
                adapter.notifyDataSetChanged()
            } else {
                adapter.listItems = emptyList()
                adapter.notifyDataSetChanged()
            }
            viewModel.performGetExistingXValues()
        }

        viewModel.sort.observe(viewLifecycleOwner) {
            viewModel.page.postValue(1)
        }

        viewModel.shouldPerformGetProject.observe(viewLifecycleOwner) {
            if (it == true) {
                viewModel.performGetProject()
            }
        }
    }

    private fun resetNumberBoxes() {
        et_built_area.clear()
        et_ext_area.clear()
        et_area_gfa.clear()
        et_built_year.clear()
    }

    private fun setOnClickListeners() {
        btn_search.setOnClickListener {
            AuthUtil.checkModuleAccessibility(
                module = AccessibilityEnum.AdvisorModule.X_VALUE,
                onSuccessAccessibility = {
                    val intent = Intent(activity, XValueAddressSearchActivity::class.java)
                    startActivityForResult(intent, REQUEST_CODE_GET_ADDRESS)
                }
            )
        }

        btn_calculate.setOnClickListener {
            if (viewModel.areParamsValid()) {
                val xValueEntryParams = XValueEntryParams(
                    propertyType = viewModel.propertyType.value,
                    propertySubType = viewModel.propertySubType.value!!, // checked in viewModel.areParamsValid()
                    unitFloor = viewModel.unitFloor.value,
                    unitNumber = viewModel.unitNumber.value,
                    areaType = viewModel.areaType.value,
                    tenure = viewModel.tenure.value,
                    builtYear = viewModel.builtYear.value,
                    area = viewModel.area.value!!,
                    areaExt = viewModel.areaExt.value,
                    areaGfa = viewModel.areaGfa.value,
                    address = viewModel.address.value!! // checked in viewModel.areParamsValid()
                )

                AuthUtil.checkModuleAccessibility(
                    module = AccessibilityEnum.AdvisorModule.X_VALUE,
                    onSuccessAccessibility = {
                        activity?.let { mActivity ->
                            XValueActivity.launch(
                                mActivity,
                                xValueEntryParams
                            )
                        }
                    }
                )
            }
        }

        btn_get_size.setOnClickListener {
            viewModel.performGetProject()
        }

        btn_sort.setOnClickListener {
            AuthUtil.checkModuleAccessibility(
                module = AccessibilityEnum.AdvisorModule.X_VALUE,
                onSuccessAccessibility = {
                    val sorts = XValueEnum.GetExistingXValuesOrder.values()
                    val sortLabels = sorts.map {
                        it.label
                    }

                    dialogUtil.showListDialog(sortLabels, { _, position ->
                        viewModel.sort.postValue(sorts[position])
                    })
                } //end of success
            )
        }

        btn_search_x_value.setOnClickListener {
            AuthUtil.checkModuleAccessibility(
                module = AccessibilityEnum.AdvisorModule.X_VALUE,
                onSuccessAccessibility = {
                    val intent = Intent(activity, XValueSearchActivity::class.java)
                    startActivityForResult(intent, REQUEST_CODE_GET_EXISTING_X_VALUE)
                }
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        println(resultCode)
        println(requestCode)
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

        @JvmStatic
        fun newInstance(): SearchXValueFragment {
            return SearchXValueFragment()
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_search_x_value
    }

    override fun getViewModelClass(): Class<SearchXValueViewModel> {
        return SearchXValueViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}