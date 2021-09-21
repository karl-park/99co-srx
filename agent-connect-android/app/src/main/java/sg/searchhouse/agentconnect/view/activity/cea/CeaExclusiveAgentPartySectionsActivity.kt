package sg.searchhouse.agentconnect.view.activity.cea

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityCeaExclusiveAgentPartySectionsBinding
import sg.searchhouse.agentconnect.dsl.widget.postDelayed
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum.*
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum.CeaFormRowTypeKeyValue.*
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.cea.CeaFormClientPO
import sg.searchhouse.agentconnect.model.api.cea.CeaFormRowPO
import sg.searchhouse.agentconnect.model.api.cea.CeaFormSubmissionPO
import sg.searchhouse.agentconnect.model.api.cea.CeaFormTemplatePO
import sg.searchhouse.agentconnect.event.cea.CeaGetInfoByApiCall
import sg.searchhouse.agentconnect.event.cea.CeaUpdateAgentAndOwnerSection
import sg.searchhouse.agentconnect.event.cea.CeaUpdateSignature
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.JsonUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.cea.CeaExclusiveDetailSectionsAdapter
import sg.searchhouse.agentconnect.view.fragment.cea.CeaExclusiveTermSectionsDialogFragment
import sg.searchhouse.agentconnect.viewmodel.activity.cea.CeaExclusiveAgentPartySectionsViewModel
import java.util.*
import kotlin.collections.ArrayList

class CeaExclusiveAgentPartySectionsActivity :
    ViewModelActivity<CeaExclusiveAgentPartySectionsViewModel, ActivityCeaExclusiveAgentPartySectionsBinding>() {

    private lateinit var adapter: CeaExclusiveDetailSectionsAdapter

    companion object {
        private const val EXTRA_KEY_CEA_FORM_TYPE = "EXTRA_KEY_CEA_FORM_TYPE"
        private const val EXTRA_KEY_CEA_FORM_TEMPLATE = "EXTRA_KEY_CEA_FORM_TEMPLATE"
        private const val EXTRA_KEY_CEA_SUBMISSION_PO = "EXTRA_KEY_CEA_SUBMISSION_PO"
        private const val EXTRA_KEY_CEA_CLIENT_PO = "EXTRA_KEY_CEA_CLIENT_PO"
        private const val EXTRA_KEY_CEA_SECTION = "EXTRA_KEY_CEA_SECTION"

        fun launch(
            activity: Activity,
            formType: CeaFormType,
            template: CeaFormTemplatePO,
            section: CeaSection,
            submissionPO: CeaFormSubmissionPO? = null,
            client: CeaFormClientPO? = null
        ) {
            val intent = Intent(activity, CeaExclusiveAgentPartySectionsActivity::class.java)
            intent.putExtra(EXTRA_KEY_CEA_FORM_TYPE, formType)
            intent.putExtra(EXTRA_KEY_CEA_FORM_TEMPLATE, template)
            intent.putExtra(EXTRA_KEY_CEA_SECTION, section)
            submissionPO?.let { intent.putExtra(EXTRA_KEY_CEA_SUBMISSION_PO, submissionPO) }
            client?.let { intent.putExtra(EXTRA_KEY_CEA_CLIENT_PO, client) }
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupParamsFromExtra()
        setupAdapterAndList()
        observeRxBus()
        observeLiveData()
        handleListeners()
    }

    private fun setupParamsFromExtra() {
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
            viewModel.ceaSubmission.value =
                intent.getSerializableExtra(EXTRA_KEY_CEA_SUBMISSION_PO) as CeaFormSubmissionPO
        }
        if (intent.hasExtra(EXTRA_KEY_CEA_CLIENT_PO)) {
            viewModel.ceaClient =
                intent.getSerializableExtra(EXTRA_KEY_CEA_CLIENT_PO) as CeaFormClientPO
        }
        if (intent.hasExtra(EXTRA_KEY_CEA_SECTION)) {
            viewModel.ceaSection = intent.getSerializableExtra(EXTRA_KEY_CEA_SECTION) as CeaSection
        }
    }

    private fun setupAdapterAndList() {
        adapter = CeaExclusiveDetailSectionsAdapter(this, dialogUtil) { row, position ->
            updateValue(row, position)
        }
        binding.listPartyDetails.layoutManager = LinearLayoutManager(this)
        binding.listPartyDetails.adapter = adapter
    }

    private fun observeRxBus() {
        listenRxBus(CeaGetInfoByApiCall::class.java) {
            when (it.keyValue) {
                PARTY_POSTAL_CODE, REPRESNETER_POSTAL_CODE -> {
                    val value = it.row?.rowValue ?: return@listenRxBus
                    if (NumberUtil.isNaturalNumber(value)) {
                        updateValue(it.row, -1)
                        viewModel.selectedPostalCodeType = it.keyValue
                        viewModel.getAddressByPostalCode(value.toIntOrNull())
                    }
                }
                else -> {
                    println("do nothing for now")
                }
            }
        }

        listenRxBus(CeaUpdateSignature::class.java) {
            if (it.source == SignatureSource.AGENT_PARTY_SECTION) {
                viewModel.hasSignature.value = true
                viewModel.signaturePair.value = it.pair
                viewModel.isEstateAgentPO.value = it.isEstateAgentPO
            }
        }
    }

    private fun observeLiveData() {
        viewModel.hasSignature.observe(this, Observer { isSigned ->
            val sections = viewModel.agentPartySections.value ?: return@Observer
            val signatureRowPO =
                sections.filterIsInstance<CeaFormRowPO>()
                    .find { it.keyValue == PARTY_SIGNATURE.value }
                    ?: return@Observer
            signatureRowPO.rowValue = if (isSigned) {
                getString(R.string.label_cea_signed)
            } else {
                getString(R.string.label_cea_required)
            }
            adapter.updateItems(sections)
        })

        viewModel.template.observe(this, Observer { response ->
            val template = response ?: return@Observer
            //Title
            binding.collapsingToolbar.title = template.formTitle

            if (viewModel.ceaSection == CeaSection.PARTY_SECTION) {
                //1_ Getting party sections
                val sections = ArrayList<Any>()
                template.partySections.forEach { section ->
                    sections.add(section)
                    if (section.rowArray.isNotEmpty()) {
                        sections.addAll(section.rowArray)
                    }
                }
                //if it's is edit case
                viewModel.ceaClient?.let { ceaClient ->
                    val ceaClientString = JsonUtil.parseToJsonString(ceaClient) ?: return@Observer
                    val map = JsonUtil.parseMap(ceaClientString) ?: return@Observer
                    var isRepresentativeSeller = false
                    sections.filterIsInstance<CeaFormRowPO>()
                        .forEach { rowPO ->
                            rowPO.rowValue = map[rowPO.keyValue] ?: ""
                            //SELLER
                            if (rowPO.keyValue == REPRESENTING_SELLER.value) {
                                isRepresentativeSeller = rowPO.rowValue == "true"
                            }
                            if (isRepresentativeSeller &&
                                viewModel.representerKeyValues.contains(rowPO.keyValue)
                            ) {
                                rowPO.visible = true
                            }
                        }
                }
                viewModel.agentPartySections.value = sections
                adapter.updateItems(sections)

            } else if (viewModel.ceaSection == CeaSection.AGENT_SECTION) {
                val sections = ArrayList<Any>()
                template.agentSections.forEach { section ->
                    sections.add(section)
                    if (section.rowArray.isNotEmpty()) {
                        sections.addAll(section.rowArray)
                    }
                }
                viewModel.ceaEstateAgent = template.estateAgent
                viewModel.agentPartySections.value = sections
                adapter.updateItems(sections)
            }
        })

        viewModel.updateFormResponse.observe(this, Observer { response ->
            when (response.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    val body = response.body ?: return@Observer
                    var signature: Pair<String, Bitmap>? = null
                    viewModel.signaturePair.value?.let {
                        viewModel.ceaClientSignatureKey?.let { key ->
                            signature = Pair(key, it.second)
                        }
                        return@let viewModel.ceaAgentSignatureKey?.let { key ->
                            signature = Pair(key, it.second)
                        }
                    }
                    RxBus.publish(
                        CeaUpdateAgentAndOwnerSection(
                            body.template,
                            signature,
                            viewModel.isEstateAgentPO.value
                        )
                    )
                    finish()
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(response.error?.error)
                }
                else -> {
                    println("do nothing")
                }
            }
        })

        viewModel.getAddressResponse.observe(this, Observer { apiStatus ->
            when (apiStatus.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    val response = apiStatus.body ?: return@Observer
                    val items = viewModel.agentPartySections.value ?: return@Observer
                    val ceaRows = items.filterIsInstance<CeaFormRowPO>()

                    if (viewModel.selectedPostalCodeType == PARTY_POSTAL_CODE) {
                        ceaRows.find { it.keyValue == ADDRESS.value }?.rowValue =
                            response.addressPropertyType.streetName
                        ceaRows.find { it.keyValue == BLK_NO.value }?.rowValue =
                            response.addressPropertyType.buildingNum
                    } else if (viewModel.selectedPostalCodeType == REPRESNETER_POSTAL_CODE) {
                        ceaRows.find { it.keyValue == REPRESENTER_ADDRESS.value }?.rowValue =
                            response.addressPropertyType.streetName
                        ceaRows.find { it.keyValue == REPRESENTER_BLOCK_NUM.value }?.rowValue =
                            response.addressPropertyType.buildingNum
                    }
                    viewModel.agentPartySections.value = items
                    adapter.updateItems(items)
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(apiStatus.error?.error)
                }
                else -> {
                    println("Do nothing")
                }
            }
        })
    }

    private fun handleListeners() {
        binding.btnSaveAndContinue.setOnClickListener { saveUpdateCeaSubmission() }
    }

    private fun saveUpdateCeaSubmission() {
        val requiredItems = getRequiredItems() ?: return
        if (requiredItems.isEmpty()) {
            viewModel.createCeaSubmission()
        } else {
            val firstRequiredItem = requiredItems.firstKey() ?: return
            val y = binding.listPartyDetails.y + binding.listPartyDetails.getChildAt(firstRequiredItem).y
            binding.scrollviewCeaAgentParty.postDelayed {
                binding.scrollviewCeaAgentParty.smoothScrollTo(
                    0,
                    y.toInt()
                )
            }
        }
    }

    private fun getRequiredItems(): SortedMap<Int, String>? {
        val items = viewModel.agentPartySections.value ?: return null
        val requiredItems = hashMapOf<Int, String>()
        items.forEachIndexed { index, item ->
            if (item is CeaFormRowPO && item.required && TextUtils.isEmpty(item.rowValue) && item.visible) {
                requiredItems[index] = item.keyValue
            }
        }
        adapter.updateRequiredFields(requiredItems.values.toList())
        return requiredItems.toSortedMap()
    }

    private fun updateValue(row: CeaFormRowPO, position: Int) {
        val items = viewModel.agentPartySections.value ?: arrayListOf()
        items.find { it is CeaFormRowPO && it.keyValue == row.keyValue }?.let {
            if (it is CeaFormRowPO) {
                it.rowValue = row.rowValue
            }
        }

        //Note: hard coded in fronted because of backend legacy. please don't do changes...
        if (row.keyValue == REPRESENTING_SELLER.value) {
            for (i in position until items.size) {
                if (items[i] is CeaFormRowPO) {
                    val item = (items[i] as CeaFormRowPO)
                    when (item.keyValue) {
                        REPRESENTER_NAME.value, REPRESENTER_NRIC.value,
                        REPRESENTER_CONTACT.value, REPRESENTER_EMAIL.value,
                        REPRESNETER_POSTAL_CODE.value -> {
                            item.visible = row.rowValue == "true"
                            item.required = row.rowValue == "true"
                        }
                        REPRESENTER_ADDRESS.value, REPRESENTER_BLOCK_NUM.value,
                        REPRESENTER_FLOOR.value, REPRESENTER_UNIT.value -> {
                            item.visible = row.rowValue == "true"
                        }
                    }
                }
            }
            adapter.updateItems(items)
            viewModel.agentPartySections.value = items
        } else if (row.keyValue == PARTY_SIGNATURE.value) {
            showTermsSections()
        }
    }

    private fun showTermsSections() {
        val terms = viewModel.template.value?.partyTermSections ?: return
        CeaExclusiveTermSectionsDialogFragment.launch(
            supportFragmentManager,
            terms,
            SignatureSource.AGENT_PARTY_SECTION,
            viewModel.ceaEstateAgent,
            viewModel.ceaClient
        )
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_cea_exclusive_agent_party_sections
    }

    override fun getViewModelClass(): Class<CeaExclusiveAgentPartySectionsViewModel> {
        return CeaExclusiveAgentPartySectionsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar {
        return binding.toolbar
    }
}