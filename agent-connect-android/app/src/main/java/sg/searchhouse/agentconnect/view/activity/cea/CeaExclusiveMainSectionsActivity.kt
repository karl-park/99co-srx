package sg.searchhouse.agentconnect.view.activity.cea

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_cea_exclusive_main_sections.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityCeaExclusiveMainSectionsBinding
import sg.searchhouse.agentconnect.dsl.widget.postDelayed
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum.CeaFormRowTypeKeyValue
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum.CeaFormType
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.cea.CeaFormRowPO
import sg.searchhouse.agentconnect.model.api.cea.CeaFormTemplatePO
import sg.searchhouse.agentconnect.event.cea.CeaGetInfoByApiCall
import sg.searchhouse.agentconnect.event.listing.user.UpdateDraftCeaFormsEvent
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.PropertyTypeUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.cea.CeaExclusiveMainSectionsAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.cea.CeaExclusiveMainSectionsViewModel

//NOTE : in exclusive detail screen -> show only mainSections
//TODO: refine codes for better performance in future
class CeaExclusiveMainSectionsActivity :
    ViewModelActivity<CeaExclusiveMainSectionsViewModel, ActivityCeaExclusiveMainSectionsBinding>() {

    private lateinit var adapter: CeaExclusiveMainSectionsAdapter

    companion object {
        private const val EXTRA_KEY_FORM_TYPE = "EXTRA_KEY_FORM_TYPE"
        private const val EXTRA_KEY_FORM_ID = "EXTRA_KEY_FORM_ID"

        fun launch(activity: Activity, formType: CeaFormType, formId: Int? = null) {
            val intent = Intent(activity, CeaExclusiveMainSectionsActivity::class.java)
            intent.putExtra(EXTRA_KEY_FORM_TYPE, formType)
            formId?.let { intent.putExtra(EXTRA_KEY_FORM_ID, it) }
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeRxBus()
        setupExtras()
        performRequest()
        setupListAndAdapter()
        observeLiveData()
        handleListeners()
    }

    private fun observeRxBus() {
        listenRxBus(CeaGetInfoByApiCall::class.java) {
            when (it.keyValue) {
                CeaFormRowTypeKeyValue.POSTAL_CODE -> {
                    //POSTAL CODE
                    val value = it.row?.rowValue ?: return@listenRxBus
                    if (NumberUtil.isNaturalNumber(value)) {
                        //update values in map list
                        updateValueInMap(it.row)
                        //call api
                        viewModel.getAddressByPostalCode(value.toIntOrNull())
                    }
                }
                CeaFormRowTypeKeyValue.BUILT_MIN -> {
                    val getSizeParams = viewModel.getSizeParams.value ?: linkedMapOf()
                    if (getSizeParams.isNotEmpty()) {
                        viewModel.getPropertySize()
                    }
                }
                else -> {
                    println("do nothing for now")
                }
            }
        }
    }

    private fun setupExtras() {
        if (intent.hasExtra(EXTRA_KEY_FORM_ID)) {
            viewModel.formId.value = intent.getIntExtra(EXTRA_KEY_FORM_ID, 0)
        }

        if (intent.hasExtra(EXTRA_KEY_FORM_TYPE)) {
            viewModel.formType.value =
                intent.getSerializableExtra(EXTRA_KEY_FORM_TYPE) as CeaFormType
        }

    }

    private fun performRequest() {
        viewModel.getModels()
    }

    private fun setupListAndAdapter() {
        adapter =
            CeaExclusiveMainSectionsAdapter(
                this,
                dialogUtil,
                onUpdateValueRowType = { updateValueInMap(it) })
        binding.listExclusiveDetails.layoutManager = LinearLayoutManager(this)
        binding.listExclusiveDetails.adapter = adapter
    }

    private fun observeLiveData() {
        viewModel.template.observe(this, Observer { response ->
            val template = response ?: return@Observer

            binding.collapsingToolbar.title = template.formTitle

            val mainSections: ArrayList<Any> = arrayListOf()
            template.mainSections.forEach { section ->
                mainSections.add(section)
                if (section.rowArray.isNotEmpty()) {
                    mainSections.addAll(section.rowArray)
                }
            }

            viewModel.formId.value = template.formId
            viewModel.mainSections.value = mainSections
            adapter.updateItems(mainSections)

            //TODO: to add more sections
            var formValues = mapOf<String, CeaFormRowPO>()
            template.detailSections.forEach { section ->
                formValues = formValues + section.rowArray.associateBy { it.keyValue }
            }
            viewModel.formValues.value = formValues

        })

        viewModel.formType.observe(this, Observer { formType ->
            formType?.let {
                viewModel.getFormTemplate(formType)
            }
        })

        viewModel.models.observe(this, Observer { items ->
            adapter.updateModels(items)
        })

        viewModel.getAddressByPostalCodeResponse.observe(this, Observer { status ->
            when (status.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    status.body?.let { response ->
                        val items = viewModel.mainSections.value ?: listOf()
                        //ADDRESS
                        items.find { it is CeaFormRowPO && it.keyValue == CeaFormRowTypeKeyValue.ADDRESS.value }
                            ?.let { item ->
                                if (item is CeaFormRowPO) {
                                    item.rowValue = response.addressPropertyType.streetName
                                }
                            }
                        //BLOCK NUMBER
                        items.find { it is CeaFormRowPO && it.keyValue == CeaFormRowTypeKeyValue.BLOCK_NUMBER.value }
                            ?.let { item ->
                                if (item is CeaFormRowPO) {
                                    item.rowValue = response.addressPropertyType.buildingNum
                                    updateGetSizeParamsValue(item)
                                }
                            }
                        viewModel.mainSections.value = items
                        adapter.updateItems(items)
                    }
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(status.error?.error)
                }
                else -> {
                    Log.i("CeaExclusive", "calling getAddressByPostal code api")
                }
            }
        })

        viewModel.getPropertySizeResponse.observe(this, Observer { status ->
            when (status.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    status.body?.let { response ->
                        val getSizeParams = viewModel.getSizeParams.value ?: linkedMapOf()
                        if (getSizeParams.isNotEmpty()) {

                            val items = viewModel.mainSections.value ?: listOf()
                            val propertyType =
                                getSizeParams[CeaFormRowTypeKeyValue.CD_RESEARCH_SUB_TYPE.value]
                                    ?: ""
                            if (NumberUtil.isNaturalNumber(propertyType)) {
                                if (PropertyTypeUtil.isLanded(propertyType.toInt())) {
                                    items.find { it is CeaFormRowPO && it.keyValue == CeaFormRowTypeKeyValue.LAND_MIN.value }
                                        ?.let { item ->
                                            if (item is CeaFormRowPO) {
                                                item.rowValue = response.size.toString()
                                            }
                                        }
                                } else {
                                    items.find { it is CeaFormRowPO && it.keyValue == CeaFormRowTypeKeyValue.BUILT_MIN.value }
                                        ?.let { item ->
                                            if (item is CeaFormRowPO) {
                                                item.rowValue = response.size.toString()
                                            }
                                        }
                                }
                            }
                            viewModel.mainSections.value = items
                            adapter.updateItems(items)
                        }
                    }
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(status.error?.error)
                }
                else -> {
                    Log.e("CeaExclusive", "Error in getting property size..")
                }
            }
        })

        viewModel.createUpdateFormResponse.observe(this, Observer { status ->
            when (status.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    status.body?.let {
                        viewModel.formId.value = it.template.formId
                        showCeaExclusiveDetails(template = it.template)
                    }
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(status.error?.error)
                }
                else -> {
                    Log.i("CeaExclusive", "calling create update form code api")
                }
            }
        })
    }

    private fun showCeaExclusiveDetails(template: CeaFormTemplatePO) {
        val formType = viewModel.formType.value ?: return
        CeaExclusiveDetailSectionsActivity.launch(
            this,
            formType,
            template,
            viewModel.ceaFormSubmissionPO
        )
    }

    private fun handleListeners() {
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.btnSaveAndContinue.setOnClickListener { saveCeaSubmission() }
    }

    private fun saveCeaSubmission() {
        val requiredItems = getRequiredItems()?.toSortedMap() ?: return
        if (requiredItems.isEmpty()) {
            viewModel.createCeaSubmission()
        } else {
            val firstRequiredItem = requiredItems.firstKey() ?: return
            val y = list_exclusive_details.y +
                    list_exclusive_details.getChildAt(firstRequiredItem).y
            scrollview_cea.postDelayed { scrollview_cea.smoothScrollTo(0, y.toInt()) }
        }
    }

    private fun getRequiredItems(): Map<Int, String>? {
        val items = viewModel.mainSections.value ?: return null
        val requiredItems = hashMapOf<Int, String>()
        items.forEachIndexed { index, item ->
            if (item is CeaFormRowPO) {
                if (item.required && TextUtils.isEmpty(item.rowValue) && item.visible) {
                    requiredItems[index] = item.keyValue
                }
            }
        }
        adapter.updateRequiredItems(requiredItems.values.toList())
        return requiredItems
    }

    private fun updateValueInMap(row: CeaFormRowPO) {
        val sections = viewModel.mainSections.value ?: emptyList()
        sections.find { it is CeaFormRowPO && it.keyValue == row.keyValue }?.let {
            if (it is CeaFormRowPO) {
                it.rowValue = row.rowValue
            }
        }

        if (row.keyValue == CeaFormRowTypeKeyValue.TENANTED.value) {
            sections.find { it is CeaFormRowPO && it.keyValue == CeaFormRowTypeKeyValue.TENANTED_AMOUNT.value }
                ?.let {
                    if (it is CeaFormRowPO) {
                        it.visible = row.rowValue == "true"
                        //refresh adapter
                        adapter.updateItems(sections)
                    }
                }
        }

        //Store size params
        updateGetSizeParamsValue(row)

        viewModel.mainSections.value = sections
    }

    private fun updateGetSizeParamsValue(row: CeaFormRowPO) {
        val getSizeParams = viewModel.getSizeParams.value ?: linkedMapOf()
        if (viewModel.sizeParams.contains(row.keyValue)) {
            if (row.keyValue == CeaFormRowTypeKeyValue.CD_RESEARCH_SUB_TYPE.value) {
                getSizeParams[row.keyValue] = row.mapper?.get(row.rowValue) ?: ""
            } else {
                getSizeParams[row.keyValue] = row.rowValue
            }
            viewModel.getSizeParams.value = getSizeParams
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        RxBus.publish(UpdateDraftCeaFormsEvent())
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_cea_exclusive_main_sections
    }

    override fun getViewModelClass(): Class<CeaExclusiveMainSectionsViewModel> {
        return CeaExclusiveMainSectionsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

}