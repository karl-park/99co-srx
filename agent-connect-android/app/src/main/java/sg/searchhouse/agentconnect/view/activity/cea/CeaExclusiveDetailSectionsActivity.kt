package sg.searchhouse.agentconnect.view.activity.cea

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_cea_exclusive_detail_sections.*
import kotlinx.android.synthetic.main.activity_cea_exclusive_main_sections.list_exclusive_details
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityCeaExclusiveDetailSectionsBinding
import sg.searchhouse.agentconnect.dsl.widget.postDelayed
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum.*
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum.CeaFormRowTypeKeyValue.*
import sg.searchhouse.agentconnect.model.api.cea.CeaFormRowPO
import sg.searchhouse.agentconnect.model.api.cea.CeaFormSubmissionPO
import sg.searchhouse.agentconnect.model.api.cea.CeaFormTemplatePO
import sg.searchhouse.agentconnect.model.api.cea.CeaFormTermPO
import sg.searchhouse.agentconnect.event.cea.UpdateCeaSelfDefinedTerm
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.cea.CeaExclusiveDetailSectionsAdapter
import sg.searchhouse.agentconnect.view.fragment.cea.CeaAddNewTermDialogFragment
import sg.searchhouse.agentconnect.viewmodel.activity.cea.CeaExclusiveDetailSectionsViewModel
import java.util.*
import kotlin.collections.ArrayList

//Note : Cea has many customization and hard codes
class CeaExclusiveDetailSectionsActivity :
    ViewModelActivity<CeaExclusiveDetailSectionsViewModel, ActivityCeaExclusiveDetailSectionsBinding>() {

    private lateinit var adapter: CeaExclusiveDetailSectionsAdapter

    companion object {
        private const val EXTRA_KEY_CEA_FORM_TYPE = "EXTRA_KEY_CEA_FORM_TYPE"
        private const val EXTRA_KEY_CEA_FORM_TEMPLATE = "EXTRA_KEY_CEA_FORM_TEMPLATE"
        private const val EXTRA_KEY_CEA_SUBMISSION_PO = "EXTRA_KEY_CEA_SUBMISSION_PO"

        fun launch(
            activity: Activity,
            formType: CeaFormType,
            template: CeaFormTemplatePO,
            submissionPO: CeaFormSubmissionPO? = null
        ) {
            val intent = Intent(activity, CeaExclusiveDetailSectionsActivity::class.java)
            intent.putExtra(EXTRA_KEY_CEA_FORM_TYPE, formType)
            intent.putExtra(EXTRA_KEY_CEA_FORM_TEMPLATE, template)
            submissionPO?.let { intent.putExtra(EXTRA_KEY_CEA_SUBMISSION_PO, it) }
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtras()
        setupAdapterAndList()
        observeRxBus()
        observeLiveData()
        handleListeners()
    }

    private fun setupExtras() {
        if (intent.hasExtra(EXTRA_KEY_CEA_FORM_TYPE)) {
            val formType =
                intent.getSerializableExtra(EXTRA_KEY_CEA_FORM_TYPE) as CeaFormType
            viewModel.formType.value = formType
        }
        if (intent.hasExtra(EXTRA_KEY_CEA_FORM_TEMPLATE)) {
            viewModel.template.value =
                intent.getSerializableExtra(EXTRA_KEY_CEA_FORM_TEMPLATE) as CeaFormTemplatePO
        }
        if (intent.hasExtra(EXTRA_KEY_CEA_SUBMISSION_PO)) {
            viewModel.ceaSubmissionPO.value =
                intent.getSerializableExtra(EXTRA_KEY_CEA_SUBMISSION_PO) as CeaFormSubmissionPO
        }
    }

    private fun observeRxBus() {
        listenRxBus(UpdateCeaSelfDefinedTerm::class.java) { updateCeaTerm ->
            val terms = viewModel.selfDefinedTerms.value ?: arrayListOf()
            terms.find { it.id == updateCeaTerm.term.id }?.let {
                it.term = updateCeaTerm.term.term
            } ?: terms.add(updateCeaTerm.term)
            viewModel.selfDefinedTerms.value = terms
        }
    }

    private fun observeLiveData() {

        viewModel.template.observe(this, Observer { ceaFormTemplate ->
            val template = ceaFormTemplate ?: return@Observer
            binding.collapsingToolbar.title = template.formTitle

            val items: ArrayList<Any> = arrayListOf()
            template.detailSections.forEach { detailSection ->
                items.add(detailSection)
                detailSection.rowArray.forEach { item ->
                    when (item.keyValue) {
                        PRICE_COMMISSION.value, RATE_COMMISSION.value -> {
                            viewModel.commissionRates[item.keyValue] = item
                            item.rowType = CeaFormRowType.CUSTOM_COMMISSION_NUMBER_PAD.value
                            item.rowTitle = getString(R.string.label_cea_commission)
                            if (item.keyValue == RATE_COMMISSION.value && !TextUtils.isEmpty(item.rowValue)) {
                                val position =
                                    items.indexOfFirst { it is CeaFormRowPO && it.keyValue == PRICE_COMMISSION.value }
                                items[position] = item
                            } else if (item.keyValue == PRICE_COMMISSION.value) {
                                items.add(item)
                            }
                        }
                        PRICE_RENEWAL_COMMISSION.value, RATE_RENEWAL_COMMISSION.value -> {
                            viewModel.renewalCommissionRates[item.keyValue] = item
                            item.rowType = CeaFormRowType.CUSTOM_COMMISSION_NUMBER_PAD.value
                            item.rowTitle = getString(R.string.label_cea_renewal_commission)
                            if (item.keyValue == RATE_RENEWAL_COMMISSION.value &&
                                !TextUtils.isEmpty(item.rowValue)
                            ) {
                                val position =
                                    items.indexOfFirst { it is CeaFormRowPO && it.keyValue == PRICE_RENEWAL_COMMISSION.value }
                                items[position] = item
                            } else if (item.keyValue == PRICE_RENEWAL_COMMISSION.value) {
                                items.add(item)
                            }
                        }
                        else -> {
                            items.add(item)
                        }
                    }
                }
            } //end of details sections

            template.termSections.forEach { termSection ->
                items.add(termSection)
                if (termSection.rowArray.isNotEmpty()) {
                    items.addAll(termSection.rowArray)
                }
            } //end of terms sections

            viewModel.detailSections.value = items
            //Update adapter
            adapter.updateCommissionItems(viewModel.commissionRates)
            adapter.updateRenewalCommissionItems(viewModel.renewalCommissionRates)
            adapter.updateItems(items)

            //Update Self Defined Terms
            val tempSelfDefinedTerms = arrayListOf<CeaFormTermPO>()
            tempSelfDefinedTerms.addAll(template.selfDefinedTerms)
            viewModel.selfDefinedTerms.value = tempSelfDefinedTerms

        })

        viewModel.updateFormResponse.observe(this, Observer {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    it.body?.let { response ->
                        showAgentOwnerSections(response.template)
                    }
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    Log.e("CEA_DETAIL", "Getting error in calling createUpdateForm API")
                }
            }
        })

        viewModel.selfDefinedTerms.observe(this, Observer { items ->
            items?.let {
                binding.layoutSelfTermDefined.populateItems(it)
            }
        })
    }

    private fun setupAdapterAndList() {
        adapter = CeaExclusiveDetailSectionsAdapter(this, dialogUtil) { row, position ->
            updateValue(row, position)
        }
        binding.listExclusiveDetails.layoutManager = LinearLayoutManager(this)
        binding.listExclusiveDetails.adapter = adapter
        binding.layoutSelfTermDefined.onClickCeaTermItem = onClickCeaTermItem
        binding.layoutSelfTermDefined.onRemoveCeaTermItem = onRemoveCeaTermItem
    }

    private fun updateValue(row: CeaFormRowPO, position: Int) {
        val items = viewModel.detailSections.value ?: arrayListOf()
        when (row.keyValue) {
            PRICE_COMMISSION.value -> items[position] = row
            RATE_COMMISSION.value -> items[position] = row
            PRICE_RENEWAL_COMMISSION.value -> items[position] = row
            RATE_RENEWAL_COMMISSION.value -> items[position] = row
            else -> {

                items.find { it is CeaFormRowPO && it.keyValue == row.keyValue }?.let {
                    if (it is CeaFormRowPO) {
                        it.rowValue = row.rowValue
                    }
                }

                //for conflict of interest
                if (row.keyValue == CONFLICT_OF_INTEREST.value) {
                    items.find { it is CeaFormRowPO && it.keyValue == ESTATE_AGENT_DISCLOSURE.value }
                        ?.let {
                            if (it is CeaFormRowPO) {
                                it.visible = row.rowValue == "true"
                                it.required = row.rowValue == "true"
                                adapter.updateItems(items)
                            }
                        }
                }

                //for renewal commission liable
                if (row.keyValue == RENEWAL_COMMISSION_LIABLE.value) {
                    for (i in position until items.size) {
                        if (items[i] is CeaFormRowPO) {
                            val item = items[i] as CeaFormRowPO
                            when (item.keyValue) {
                                RENEWAL_TIME.value, PRICE_RENEWAL_COMMISSION.value, RATE_RENEWAL_COMMISSION.value -> {
                                    item.visible = row.rowValue == "true"
                                    item.required = row.rowValue == "true"
                                    //reset value if disable commission liable value
                                    if (!row.getRowValueBooleanForToggleType()) {
                                        item.rowValue = ""
                                    }
                                }
                                else -> {
                                    println(item.keyValue)
                                }
                            }
                        }
                    }
                    adapter.updateItems(items)
                }
            }
        }
        viewModel.detailSections.value = items
    }

    private fun handleListeners() {
        binding.layoutSelfTermDefined.binding.tvAddNewItem.setOnClickListener {
            val items = viewModel.selfDefinedTerms.value ?: arrayListOf()
            if (items.isEmpty()) {
                showCeaAddNewTermDialog(CeaFormTermPO(0, ""))
            } else {
                val index = items.maxBy { it.id }?.id ?: return@setOnClickListener
                showCeaAddNewTermDialog(CeaFormTermPO(index + 1, ""))
            }
        }
        binding.btnSaveAndContinue.setOnClickListener { saveCeaSubmission() }
    }

    private fun saveCeaSubmission() {
        val requiredItems = getRequiredItems() ?: return
        if (requiredItems.isEmpty()) {
            viewModel.updateCeaSubmission()
        } else {
            val firstRequiredItem = requiredItems.firstKey() ?: return
            val y = list_exclusive_details.y +
                    list_exclusive_details.getChildAt(firstRequiredItem).y
            scrollview_cea_details.postDelayed {
                scrollview_cea_details.smoothScrollTo(
                    0,
                    y.toInt()
                )
            }
        }
    }

    private fun getRequiredItems(): SortedMap<Int, String>? {
        val sections = viewModel.detailSections.value ?: return null
        val requiredItems = hashMapOf<Int, String>()
        sections.forEachIndexed { index, item ->
            if (item is CeaFormRowPO && item.required && TextUtils.isEmpty(item.rowValue) && item.visible) {
                requiredItems[index] = item.keyValue
            }
        }
        adapter.updateRequiredFields(requiredItems.values.toList())
        return requiredItems.toSortedMap()
    }

    private fun showAgentOwnerSections(template: CeaFormTemplatePO) {
        val formType = viewModel.formType.value ?: return
        val ceaSubmission = viewModel.ceaSubmissionPO.value ?: return
        CeaExclusiveAgentOwnerSectionsActivity.launch(this, formType, template, ceaSubmission)
    }

    private val onClickCeaTermItem: (CeaFormTermPO) -> Unit = { term ->
        showCeaAddNewTermDialog(term)
    }

    private val onRemoveCeaTermItem: (Int) -> Unit = { position ->
        val items = viewModel.selfDefinedTerms.value ?: arrayListOf()
        items.removeAt(position)
        viewModel.selfDefinedTerms.value = items
    }

    private fun showCeaAddNewTermDialog(term: CeaFormTermPO) {
        CeaAddNewTermDialogFragment.launch(supportFragmentManager, term)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_cea_exclusive_detail_sections
    }

    override fun getViewModelClass(): Class<CeaExclusiveDetailSectionsViewModel> {
        return CeaExclusiveDetailSectionsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }
}