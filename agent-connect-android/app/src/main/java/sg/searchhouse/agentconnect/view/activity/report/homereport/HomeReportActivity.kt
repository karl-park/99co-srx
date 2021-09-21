package sg.searchhouse.agentconnect.view.activity.report.homereport

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_home_report.*
import kotlinx.android.synthetic.main.layout_home_value_search_form.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.databinding.ActivityHomeReportBinding
import sg.searchhouse.agentconnect.dsl.getOrNull
import sg.searchhouse.agentconnect.dsl.widget.setOnTextChangedListener
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.enumeration.api.HomeReportEnum
import sg.searchhouse.agentconnect.model.api.xvalue.SearchWithWalkupResponse
import sg.searchhouse.agentconnect.event.homereport.UpdateHomeReportUsageStatsEvent
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.activity.xvalue.XValueAddressSearchActivity
import sg.searchhouse.agentconnect.view.adapter.report.homereport.ExistingHomeReportAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.report.homereport.HomeReportViewModel

class HomeReportActivity : ViewModelActivity<HomeReportViewModel, ActivityHomeReportBinding>() {
    private var adapter =
        ExistingHomeReportAdapter {
            when {
                it.generateHomeReportRequestBody != null && it.walkupResponseData != null -> {
                    viewModel.populatePropertyAddressTypeAndForm(it)
                }
                !StringUtil.isEmpty(it.localFileName) -> {
                    // For backward compatibility
                    IntentUtil.viewPdf(this, it.localFileName, AppConstant.DIR_HOME_REPORT)
                }
                !StringUtil.isEmpty(it.url) -> {
                    // For backward compatibility
                    viewModel.downloadReport(it.url)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        listenRxBuses()
        observeLocalLiveData()
    }

    private fun listenRxBuses() {
        listenRxBus(UpdateHomeReportUsageStatsEvent::class.java) {
            viewModel.performGetHomeReportUsage()
        }
    }

    private fun setupViews() {
        layout_generate_report.setupLayoutAnimation()
        setupTextBoxes()
        setupList()
        setOnClickListeners()
    }

    private fun setupList() {
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter
    }

    private fun loadExistingHomeReports() {
        adapter.listItems = HomeReportUtil.getExistingHomeReports()
        adapter.notifyDataSetChanged()
    }

    private fun observeLocalLiveData() {
        viewModel.property.observe(this, Observer { property ->
            if (property != null) {
                viewModel.requestPropertyType(property)
            } else {
                // `property` only becomes null after come back from generated reports
                viewModel.populatePropertyAddressAndTypeResetForm(
                    null,
                    isAfterGenerateReport = true
                )
            }
            viewModel.displayAddress.postValue(property?.address ?: "")
        })

        viewModel.propertyType.observe(this, Observer {
            // This is to be in sync with viewModel.reset()
            // TODO Do binding for number boxes to remove this hardcoding
            resetNumberBoxes()
        })

        viewModel.propertySubType.observe(this, Observer {
            if (it != null) {
                et_built_area.resetHint() // To toggle area unit between sqft and sqm
                viewModel.isPropertySubTypeValidated.postValue(true)
            }
            viewModel.resetValidations()
        })

        viewModel.unitFloor.observe(this, Observer {
            if (it != null) {
                viewModel.isUnitFloorValidated.postValue(true)
            }
        })

        viewModel.unitNumber.observe(this, Observer {
            if (it != null) {
                viewModel.isUnitNumberValidated.postValue(true)
            }
        })

        viewModel.area.observe(this, Observer {
            if (it != null) {
                viewModel.isAreaValidated.postValue(true)
            }
        })

        viewModel.getProjectResponse.observe(this, Observer {
            et_built_area.setNumber(it.data.getOrNull(0)?.size)
        })

        viewModel.shouldPerformGetProject.observe(this, Observer {
            if (it == true) {
                viewModel.performGetProject()
            }
        })

        viewModel.reportLocalFileName.observe(this, Observer {
            viewModel.isReportGenerated.postValue(true)
            IntentUtil.viewPdf(this, it, AppConstant.DIR_HOME_REPORT)
        })

        viewModel.isReportGenerated.observe(this, Observer {
            if (it == true) {
                maybeResetProperty()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        loadExistingHomeReports()
        maybeResetProperty()
    }

    // Fix bug of history not saved due to property being cleared
    private fun maybeResetProperty() {
        if (viewModel.isReportGenerated.value == true) {
            viewModel.property.postValue(null)
            viewModel.isReportGenerated.postValue(false)
        }
    }

    private fun setupTextBoxes() {
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
        et_built_area.setHintColor(R.color.black)
        et_client_name.setOnTextChangedListener {
            viewModel.clientName.postValue(it.getOrNull())
        }
    }

    private fun resetNumberBoxes() {
        et_built_area.clear()
    }

    private fun setOnClickListeners() {
        btn_search.setOnClickListener {
            AuthUtil.checkModuleAccessibility(
                AccessibilityEnum.AdvisorModule.HOME_REPORT,
                AccessibilityEnum.InAccessibleFunction.HOME_REPORT_GENERATE_REPORT,
                onSuccessAccessibility = {
                    val intent = Intent(this, XValueAddressSearchActivity::class.java)
                    startActivityForResult(
                        intent,
                        REQUEST_CODE_GET_ADDRESS
                    )
                }
            )
        }

        btn_get_size.setOnClickListener { viewModel.performGetProject() }

        btn_property_type.setOnClickListener {
            val availableSubTypes = viewModel.getAvailableSubTypes()
            val subTypeLabels = availableSubTypes.map { it.label }
            dialogUtil.showListDialog(subTypeLabels, { _, position ->
                viewModel.propertySubType.postValue(availableSubTypes[position])
            }, R.string.dialog_title_property_types)
        }

        btn_tenure_type.setOnClickListener {
            val tenureTypes =
                listOf(HomeReportEnum.Tenure.LEASEHOLD, HomeReportEnum.Tenure.FREEHOLD)
            val tenureTypeLabels = tenureTypes.map { it.label }
            dialogUtil.showListDialog(tenureTypeLabels, { _, position ->
                viewModel.tenureType.postValue(tenureTypes[position])
            }, R.string.dialog_title_tenure_types)
        }

        btn_get_home_report.setOnClickListener {
            AuthUtil.checkModuleAccessibility(
                AccessibilityEnum.AdvisorModule.HOME_REPORT,
                AccessibilityEnum.InAccessibleFunction.HOME_REPORT_GENERATE_REPORT,
                onSuccessAccessibility = {
                    ViewUtil.hideKeyboard(et_unit_floor)
                    if (viewModel.areParamsValid()) {
                        viewModel.generateReport()
                    }
                }
            )
        }
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
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_home_report
    }

    override fun getViewModelClass(): Class<HomeReportViewModel> {
        return HomeReportViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }

    companion object {
        private const val REQUEST_CODE_GET_ADDRESS = 1
    }
}