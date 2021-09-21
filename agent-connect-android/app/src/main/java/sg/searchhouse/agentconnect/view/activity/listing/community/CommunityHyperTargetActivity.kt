package sg.searchhouse.agentconnect.view.activity.listing.community

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_community_hyper_target.*
import kotlinx.android.synthetic.main.button_filter_property_type_residential.view.*
import kotlinx.android.synthetic.main.layout_filter_listing_residential_property_main_types.view.*
import kotlinx.android.synthetic.main.layout_hyper_section_header.view.*
import kotlinx.android.synthetic.main.layout_range_number_boxes.view.*
import kotlinx.android.synthetic.main.layout_range_number_labels.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityCommunityHyperTargetBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.toUpperCaseDefault
import sg.searchhouse.agentconnect.dsl.widget.setOnTextChangedListener
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.TransactionEnum
import sg.searchhouse.agentconnect.enumeration.app.HyperTargetActionType
import sg.searchhouse.agentconnect.model.api.community.CommunityHyperTargetTemplatePO
import sg.searchhouse.agentconnect.model.api.location.LocationEntryPO
import sg.searchhouse.agentconnect.model.app.WatchlistSearchRadius
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.activity.common.LocationSearchActivity
import sg.searchhouse.agentconnect.view.widget.listing.community.HyperTargetPropertySubTypePill
import sg.searchhouse.agentconnect.view.widget.listing.community.HyperTargetTenureTypePill
import sg.searchhouse.agentconnect.viewmodel.activity.listing.community.CommunityHyperTargetViewModel

class CommunityHyperTargetActivity :
    ViewModelActivity<CommunityHyperTargetViewModel, ActivityCommunityHyperTargetBinding>() {
    private var residentialSubTypePillListings: List<HyperTargetPropertySubTypePill>? = null
    private var tenureTypePills: List<HyperTargetTenureTypePill>? = null

    private val radii = (MIN_RADIUS_IN_METER..MAX_RADIUS_IN_METER step RADIUS_STEP).map {
        WatchlistSearchRadius(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        observeLiveData()
        setupExtras()
    }

    private fun setupExtras() {
        val actionType =
            intent.getSerializableExtra(EXTRA_INPUT_ACTION_TYPE) as HyperTargetActionType?
                ?: throw IllegalArgumentException("Missing `EXTRA_INPUT_ACTION_TYPE`!")
        binding.actionType = actionType
        val inputTemplatePO =
            intent.getSerializableExtra(EXTRA_INPUT_TEMPLATE_PO) as CommunityHyperTargetTemplatePO?
                ?: CommunityHyperTargetTemplatePO()
        viewModel.templatePO.value = inputTemplatePO // NOTE Do not `postValue`
    }

    private fun observeLiveData() {
        viewModel.propertySubTypes.observe(this) {
            residentialSubTypePillListings?.map { it.binding.invalidateAll() }
        }
        viewModel.propertyMainType.observe(this) { populatePropertySubTypes(it) }
        viewModel.tenureType.observe(this) {
            tenureTypePills?.map { it.binding.invalidateAll() }
        }
        viewModel.templatePO.observeNotNull(this) {
            if (!viewModel.isTemplateInitiated) {
                viewModel.isTemplateInitiated = true
                initiateForm(it)
            }
        }
    }

    private fun initiateForm(templatePO: CommunityHyperTargetTemplatePO?) {
        btn_search.text = templatePO?.location

        setSeekBarRadius(templatePO?.radius)

        val mainType = templatePO?.getPropertyMainType()
        viewModel.propertyMainType.postValue(mainType)

        layout_x_value.et_min.setNumber(templatePO?.xvalueMin)
        layout_x_value.et_max.setNumber(templatePO?.xvalueMax)

        layout_x_value_labels.tv_min.text =
            templatePO?.xvalueMin?.run { "\$${NumberUtil.formatThousand(this)}" }?.toString()
                ?: getString(R.string.label_any)
        layout_x_value_labels.tv_max.text =
            templatePO?.xvalueMax?.run { "\$${NumberUtil.formatThousand(this)}" }?.toString()
                ?: getString(R.string.label_any)

        // NOTE This is 2 decimal double values convert to integer value * 100
        layout_capital_gain.et_min.setNumber(templatePO?.getCapitalGainMinTimesHundred())
        layout_capital_gain.et_max.setNumber(templatePO?.getCapitalGainMaxTimesHundred())

        layout_capital_gain_labels.tv_min.text =
            templatePO?.capitalGainMin?.run {
                "${NumberUtil.formatThousand(this, decimalPlace = 2)}%"
            } ?: getString(R.string.label_any)
        layout_capital_gain_labels.tv_max.text =
            templatePO?.capitalGainMax?.run {
                "${NumberUtil.formatThousand(this, decimalPlace = 2)}%"
            } ?: getString(R.string.label_any)

        et_target_name.setText(templatePO?.name)
    }

    private fun setupViews() {
        layout_container.setupLayoutAnimation()
        setupTextBoxes()
        setupRadius()
        populateTenureType()
        setupOnClickListeners()
        setupOnResidentialPropertyMainTypeOnClickListeners()
    }

    private fun setupOnClickListeners() {
        btn_search.setOnClickListener {
            LocationSearchActivity.launchForResult(
                this,
                requestCode = REQUEST_CODE_SEARCH_LOCATION,
                includeNewProject = true
            )
        }
        setupOnHeaderClickListeners()
    }

    // TODO Refactor because copied from `WatchlistCriteriaFormActivity`
    // Set text truncated to max 50 chars
    private fun AppCompatEditText.setTruncatedText(inputText: String?) = run {
        val truncated = if (inputText != null && inputText.length >= 50) {
            "${inputText.substring(0, 50)}..."
        } else {
            inputText
        }
        setText(truncated)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            requestCode == REQUEST_CODE_SEARCH_LOCATION && resultCode == Activity.RESULT_OK -> {
                populateLocationSearchResult(data)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun populateLocationSearchResult(data: Intent?) {
        val locationEntryPO =
            data?.getSerializableExtra(LocationSearchActivity.EXTRA_RESULT_LOCATION_ENTRY) as LocationEntryPO?
        viewModel.setLocation(locationEntryPO?.displayText ?: "")
        et_target_name.setTruncatedText(locationEntryPO?.displayText)
        viewModel.isShowLocationError.postValue(false)
        setSeekBarRadius()
    }

    private fun setupRadius() {
        val labels = radii.map { it.getLabel(this).toUpperCaseDefault() }
        sb_search_radius.setup(labels, radii) { viewModel.setRadius(it.valueInMeter) }
    }

    private fun setSeekBarRadius(radiusInMeter: Int? = DEFAULT_RADIUS_IN_METER) {
        sb_search_radius.binding.seekBar.progress =
            radii.indexOfFirst { it.valueInMeter == radiusInMeter }
    }

    private fun setupTextBoxes() {
        val xValueTransformer = { number: Int?, numberString: String? ->
            if (number != null) {
                "\$${numberString}"
            } else {
                getString(R.string.label_any)
            }
        }

        layout_x_value.et_min.setup(
            numberTextTransformer = xValueTransformer,
            onNumberChangeListener = {
                viewModel.setMinXValue(it)
            })
        layout_x_value.et_max.setup(
            numberTextTransformer = xValueTransformer,
            onNumberChangeListener = {
                viewModel.setMaxXValue(it)
            })

        val capitalGainTransformer = { number: Int?, _: String? ->
            if (number != null) {
                val actualNumber = NumberUtil.roundDoubleString(number.toDouble() / 100, 2)
                resources.getString(R.string.number_percentage, actualNumber)
            } else {
                getString(R.string.label_any_percent)
            }
        }

        layout_capital_gain.et_min.setup(
            numberTextTransformer = capitalGainTransformer,
            onNumberChangeListener = {
                viewModel.setMinCapitalGain(it)
            })
        layout_capital_gain.et_max.setup(
            numberTextTransformer = capitalGainTransformer,
            onNumberChangeListener = {
                viewModel.setMaxCapitalGain(it)
            })

        et_target_name.setOnTextChangedListener {
            viewModel.setName(it)
            viewModel.isShowNameError.postValue(false)
        }
    }

    private fun setupOnHeaderClickListeners() {
        layout_header_x_value.layout_header.setOnClickListener {
            viewModel.isExpandXValue.postValue(viewModel.isExpandXValue.value != true)
        }
        layout_header_capital_gain.layout_header.setOnClickListener {
            viewModel.isExpandCapitalGain.postValue(viewModel.isExpandCapitalGain.value != true)
        }
        layout_header_tenure.layout_header.setOnClickListener {
            viewModel.isExpandTenure.postValue(viewModel.isExpandTenure.value != true)
        }
        layout_header_target_name.layout_header.setOnClickListener {
            viewModel.isExpandHyperTargetName.postValue(viewModel.isExpandHyperTargetName.value != true)
        }
    }

    private fun setupOnResidentialPropertyMainTypeOnClickListeners() {
        layout_residential_main_property_types.btn_property_main_type_residential.button.setOnClickListener {
            viewModel.propertyMainType.postValue(ListingEnum.PropertyMainType.RESIDENTIAL)
        }
        layout_residential_main_property_types.btn_property_main_type_hdb.button.setOnClickListener {
            viewModel.propertyMainType.postValue(ListingEnum.PropertyMainType.HDB)
        }
        layout_residential_main_property_types.btn_property_main_type_condo.button.setOnClickListener {
            viewModel.propertyMainType.postValue(ListingEnum.PropertyMainType.CONDO)
        }
        layout_residential_main_property_types.btn_property_main_type_landed.button.setOnClickListener {
            viewModel.propertyMainType.postValue(ListingEnum.PropertyMainType.LANDED)
        }
    }

    private fun populatePropertySubTypes(propertyMainType: ListingEnum.PropertyMainType?) {
        layout_property_sub_types.removeAllViews()
        propertyMainType?.let {
            if (propertyMainType != ListingEnum.PropertyMainType.RESIDENTIAL) {
                residentialSubTypePillListings =
                    propertyMainType.propertySubTypes.map { addPropertySubTypeButton(it) }
            }
            val selectedSubTypes = if (!viewModel.isPropertySubTypesInitiated) {
                // Preload
                viewModel.isPropertySubTypesInitiated = true
                viewModel.templatePO.value?.getPropertySubTypes() ?: emptyList()
            } else {
                // Selection
                propertyMainType.propertySubTypes
            }
            viewModel.setPropertySubTypesValue(selectedSubTypes)
        }
    }

    private fun populateTenureType() {
        tenureTypePills = TransactionEnum.TenureType.values().map { addTenureTypePill(it) }
    }

    private fun addTenureTypePill(tenureType: TransactionEnum.TenureType): HyperTargetTenureTypePill {
        val pill = HyperTargetTenureTypePill(this)
        pill.binding.tenureType = tenureType
        pill.binding.viewModel = viewModel
        pill.binding.button.setOnClickListener { viewModel.toggleTenureType(tenureType) }
        layout_tenure_type.addView(pill)
        return pill
    }

    private fun addPropertySubTypeButton(propertySubType: ListingEnum.PropertySubType): HyperTargetPropertySubTypePill {
        val pill = HyperTargetPropertySubTypePill(this)
        pill.binding.propertySubType = propertySubType
        pill.binding.viewModel = viewModel
        pill.binding.button.setOnClickListener { viewModel.togglePropertySubType(propertySubType) }
        layout_property_sub_types.addView(pill)
        return pill
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (binding.actionType == HyperTargetActionType.EDIT) {
            menuInflater.inflate(R.menu.menu_community_hyper_target, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_save -> {
                validateSaveHyperTarget()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun validateSaveHyperTarget() {
        if (viewModel.isHyperTargetValid()) {
            saveHyperTarget()
        } else {
            ViewUtil.showMessage(R.string.toast_invalid_hyper_target)
        }
    }

    private fun saveHyperTarget() {
        val data = Intent()
        data.putExtra(EXTRA_OUTPUT_TEMPLATE_PO, viewModel.templatePO.value)
        setResult(RESULT_OK, data)
        finish()
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_community_hyper_target
    }

    override fun getViewModelClass(): Class<CommunityHyperTargetViewModel> {
        return CommunityHyperTargetViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }

    companion object {
        private const val DEFAULT_RADIUS_IN_METER = 2000

        private const val MIN_RADIUS_IN_METER = 100
        private const val MAX_RADIUS_IN_METER = 4000
        private const val RADIUS_STEP = 100

        private const val REQUEST_CODE_SEARCH_LOCATION = 5

        const val EXTRA_INPUT_ACTION_TYPE = "EXTRA_INPUT_ACTION_TYPE"
        const val EXTRA_INPUT_TEMPLATE_PO = "EXTRA_INPUT_TEMPLATE_PO"

        const val EXTRA_OUTPUT_TEMPLATE_PO = "EXTRA_OUTPUT_TEMPLATE_PO"

        fun launchEdit(
            activity: BaseActivity,
            hyperTarget: CommunityHyperTargetTemplatePO,
            requestCode: Int
        ) {
            val extras = Bundle()
            extras.putSerializable(EXTRA_INPUT_ACTION_TYPE, HyperTargetActionType.EDIT)
            extras.putSerializable(EXTRA_INPUT_TEMPLATE_PO, hyperTarget)
            activity.launchActivityForResult(
                CommunityHyperTargetActivity::class.java,
                extras = extras,
                requestCode = requestCode
            )
        }

        fun launchView(
            activity: BaseActivity,
            hyperTarget: CommunityHyperTargetTemplatePO
        ) {
            val extras = Bundle()
            extras.putSerializable(EXTRA_INPUT_ACTION_TYPE, HyperTargetActionType.VIEW)
            extras.putSerializable(EXTRA_INPUT_TEMPLATE_PO, hyperTarget)
            activity.launchActivity(CommunityHyperTargetActivity::class.java, extras = extras)
        }

        fun launchCreate(
            activity: BaseActivity,
            requestCode: Int
        ) {
            val extras = Bundle()
            extras.putSerializable(EXTRA_INPUT_ACTION_TYPE, HyperTargetActionType.EDIT)
            activity.launchActivityForResult(
                CommunityHyperTargetActivity::class.java,
                extras = extras,
                requestCode = requestCode
            )
        }
    }
}
