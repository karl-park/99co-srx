package sg.searchhouse.agentconnect.view.activity.cea

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityCeaExclusiveConfirmationSectionsBinding
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum
import sg.searchhouse.agentconnect.model.api.cea.CeaFormSubmissionPO
import sg.searchhouse.agentconnect.model.api.cea.CeaFormTemplatePO
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.JsonUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.activity.main.MainActivity
import sg.searchhouse.agentconnect.view.adapter.cea.CeaExclusiveDetailSectionsAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.cea.CeaExclusiveConfirmationSectionsViewModel

class CeaExclusiveConfirmationSectionsActivity :
    ViewModelActivity<CeaExclusiveConfirmationSectionsViewModel, ActivityCeaExclusiveConfirmationSectionsBinding>() {

    private lateinit var adapter: CeaExclusiveDetailSectionsAdapter

    companion object {
        private const val EXTRA_KEY_CEA_FORM_TYPE = "EXTRA_KEY_CEA_FORM_TYPE"
        private const val EXTRA_KEY_CEA_FORM_TEMPLATE = "EXTRA_KEY_CEA_FORM_TEMPLATE"
        private const val EXTRA_KEY_CEA_SUBMISSION_PO = "EXTRA_KEY_CEA_SUBMISSION_PO"
        private const val EXTRA_KEY_CEA_SIGNATURES = "EXTRA_KEY_CEA_SIGNATURES"

        fun launch(
            activity: Activity,
            formType: CeaExclusiveEnum.CeaFormType,
            template: CeaFormTemplatePO,
            submissionPO: CeaFormSubmissionPO? = null,
            signatureMaps: String? = null
        ) {
            val intent = Intent(activity, CeaExclusiveConfirmationSectionsActivity::class.java)
            intent.putExtra(EXTRA_KEY_CEA_FORM_TYPE, formType)
            intent.putExtra(EXTRA_KEY_CEA_FORM_TEMPLATE, template)
            submissionPO?.let { intent.putExtra(EXTRA_KEY_CEA_SUBMISSION_PO, it) }
            signatureMaps?.let { intent.putExtra(EXTRA_KEY_CEA_SIGNATURES, it) }
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtraParams()
        setupListAndAdapter()
        observeLiveData()
        handleListeners()
    }

    private fun setupExtraParams() {
        if (intent.hasExtra(EXTRA_KEY_CEA_FORM_TYPE)) {
            val formType =
                intent.getSerializableExtra(EXTRA_KEY_CEA_FORM_TYPE) as CeaExclusiveEnum.CeaFormType
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
        if (intent.hasExtra(EXTRA_KEY_CEA_SIGNATURES)) {
            val signatureJsonString = intent.getStringExtra(EXTRA_KEY_CEA_SIGNATURES) ?: return
            viewModel.signatures.value = JsonUtil.parseToMapFile(signatureJsonString)
        }
    }

    private fun setupListAndAdapter() {
        adapter = CeaExclusiveDetailSectionsAdapter(this, dialogUtil) { row, position ->
            println(row)
            println(position)
        }
        binding.listConfirmationDetails.layoutManager = LinearLayoutManager(this)
        binding.listConfirmationDetails.adapter = adapter
    }

    private fun observeLiveData() {
        viewModel.template.observe(this, Observer { ceaTemplateForm ->
            val template = ceaTemplateForm ?: return@Observer
            //title
            binding.collapsingToolbar.title = template.formTitle

            val items = ArrayList<Any>()
            template.confirmationSections.forEach { section ->
                items.add(section)
                if (section.rowArray.isNotEmpty()) {
                    items.addAll(section.rowArray)
                }
            }
            adapter.updateItems(items)
            viewModel.confirmationSections.value = items

        })

        viewModel.submitFormResponse.observe(this, Observer {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    val bodyResponse = it.body ?: return@Observer
                    if (bodyResponse.result == "success") {
                        ViewUtil.showMessage(bodyResponse.msg)
                        showMainActivity()
                    }
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //Do nothing
                }
            }
        })
    }


    private fun showMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    private fun handleListeners() {
        binding.btnCreateCeaExclusive.setOnClickListener {
            viewModel.createSubmission()
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_cea_exclusive_confirmation_sections
    }

    override fun getViewModelClass(): Class<CeaExclusiveConfirmationSectionsViewModel> {
        return CeaExclusiveConfirmationSectionsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

}