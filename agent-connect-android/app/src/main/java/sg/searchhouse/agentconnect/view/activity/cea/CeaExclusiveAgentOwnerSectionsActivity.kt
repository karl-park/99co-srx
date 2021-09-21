package sg.searchhouse.agentconnect.view.activity.cea

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityCeaExclusiveAgentOwnerSectionsBinding
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum
import sg.searchhouse.agentconnect.model.api.cea.*
import sg.searchhouse.agentconnect.event.cea.CeaUpdateAgentAndOwnerSection
import sg.searchhouse.agentconnect.event.cea.CeaUpdateSignature
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ImageUtil
import sg.searchhouse.agentconnect.util.JsonUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.cea.CeaClientsAdapter
import sg.searchhouse.agentconnect.view.fragment.cea.CeaExclusiveTermSectionsDialogFragment
import sg.searchhouse.agentconnect.viewmodel.activity.cea.CeaExclusiveAgentOwnerSectionsViewModel
import java.io.File
import kotlin.collections.LinkedHashMap

class CeaExclusiveAgentOwnerSectionsActivity :
    ViewModelActivity<CeaExclusiveAgentOwnerSectionsViewModel, ActivityCeaExclusiveAgentOwnerSectionsBinding>() {

    private lateinit var adapter: CeaClientsAdapter

    companion object {
        private const val EXTRA_KEY_CEA_FORM_TYPE = "EXTRA_KEY_CEA_FORM_TYPE"
        private const val EXTRA_KEY_CEA_FORM_TEMPLATE = "EXTRA_KEY_CEA_FORM_TEMPLATE"
        private const val EXTRA_KEY_CEA_SUBMISSION_PO = "EXTRA_KEY_CEA_SUBMISSION_PO"

        fun launch(
            activity: Activity,
            formType: CeaExclusiveEnum.CeaFormType,
            template: CeaFormTemplatePO,
            submissionPO: CeaFormSubmissionPO? = null
        ) {
            val intent = Intent(activity, CeaExclusiveAgentOwnerSectionsActivity::class.java)
            intent.putExtra(EXTRA_KEY_CEA_FORM_TYPE, formType)
            intent.putExtra(EXTRA_KEY_CEA_FORM_TEMPLATE, template)
            submissionPO?.let { intent.putExtra(EXTRA_KEY_CEA_SUBMISSION_PO, it) }
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtras()
        setupListAndAdapter()
        observeRxBus()
        observeLiveData()
        handleListeners()
        enableSwipeToDelete()
    }

    private fun setupExtras() {
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
    }

    private fun setupListAndAdapter() {
        adapter = CeaClientsAdapter(onClickItem = { client ->
            showPartySections(client)
        }, onClickAddSignature = { client -> showTermsSections(client = client) })
        binding.listClients.layoutManager = LinearLayoutManager(this)
        binding.listClients.adapter = adapter
    }

    private fun observeRxBus() {
        listenRxBus(CeaUpdateAgentAndOwnerSection::class.java) {
            viewModel.template.value = it.template
            if (it.signature != null && it.isEstateAgentPO != null) {
                updateSignatureKeyAndValue(it.signature, it.isEstateAgentPO)
            }
        }
        listenRxBus(CeaUpdateSignature::class.java) {
            if (it.source == CeaExclusiveEnum.SignatureSource.AGENT_OWNER_SCREEN) {
                updateSignatureKeyAndValue(it.pair, it.isEstateAgentPO)
            }
        }
    }

    private fun observeLiveData() {
        viewModel.template.observe(this, Observer {
            val template = it ?: return@Observer
            //Title
            binding.collapsingToolbar.title = template.formTitle
            //estate agent
            viewModel.ceaSubmission.value?.estateAgent = template.estateAgent
            //Client values to show list
            viewModel.clients.value = template.clients
            adapter.updateClients(template.clients)
            //party terms
            viewModel.partyTerms.value = template.partySections
        })

        viewModel.updateFormResponse.observe(this, Observer { apiStatus ->
            when (apiStatus.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    val responseBody = apiStatus.body ?: return@Observer
                    showConfirmationSections(responseBody.template)
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(apiStatus.error?.error)
                }
                else -> {
                    println("DO nothing")
                }
            }
        })

        viewModel.deleteClientResponse.observe(this, Observer { apiStatus ->
            when (apiStatus.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    val clients = viewModel.clients.value ?: return@Observer
                    val position = viewModel.removedClientPosition ?: return@Observer
                    val tempSignatureMaps = viewModel.signatureMaps.value ?: linkedMapOf()
                    val tempClients = arrayListOf<CeaFormClientPO>()

                    if (position >= 0) {
                        //get removed client by position
                        val removedClient = clients.getOrNull(position) ?: return@Observer
                        //Note: -> remove REMOVED client's signature from signature lists
                        val filterSignatures =
                            tempSignatureMaps.filter { it.key != removedClient.getSignatureKey() } as LinkedHashMap<String, Bitmap>
                        //REMOVE clients
                        tempClients.addAll(clients)
                        tempClients.removeAt(position)
                        adapter.removeClient(position)
                        //Update values
                        viewModel.signatureMaps.value = filterSignatures
                        viewModel.clients.value = tempClients
                    }
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(apiStatus.error?.error)
                }
                else -> {
                    println("Do Nothing")
                }
            }
        })
    }

    private fun handleListeners() {
        binding.tvAddPartyInfo.setOnClickListener { showPartySections() }
        binding.layoutAgentSignature.tvAddSignature.setOnClickListener {
            showTermsSections(
                estateAgentPO = viewModel.estateAgent.value ?: return@setOnClickListener
            )
        }
        binding.layoutAgentSignature.layoutSignature.setOnClickListener {
            showTermsSections(
                estateAgentPO = viewModel.estateAgent.value ?: return@setOnClickListener
            )
        }
        binding.btnSaveAndContinue.setOnClickListener {
            val signatures = viewModel.signatureMaps.value ?: linkedMapOf()
            val clients = viewModel.clients.value ?: listOf()
            val requiredItems = getRequiredItems()
            when {
                clients.isEmpty() -> {
                    ViewUtil.showMessage(R.string.msg_cea_add_at_least_one)
                }
                signatures.size != clients.size + 1 -> {
                    ViewUtil.showMessage(R.string.msg_cea_required_signatures)
                }
                requiredItems?.isEmpty() == false -> {
                    val firstRequiredField = requiredItems.first()
                    ViewUtil.showMessage(
                        getString(
                            R.string.msg_required_field_agent_sections,
                            firstRequiredField
                        )
                    )
                }
                else -> {
                    viewModel.createFormSubmission()
                }
            }
        }
        binding.layoutAgentInfo.cardAgentClientInfo.setOnClickListener { showAgentSections() }
    }

    private fun getRequiredItems(): List<String>? {
        //Note: need validations for agent sections. sometimes, agent may missed out to fill out their information
        //TODO: this method is not sure. need to check again for consortium and non consortium agent
        val items = viewModel.template.value?.agentSections?.flatMap { it.rowArray } ?: return null
        val requiredItems = arrayListOf<String>()
        items.map {
            if (it.required && TextUtils.isEmpty(it.rowValue) && it.visible) {
                requiredItems.add(it.rowTitle)
            }
        }
        return requiredItems
    }

    private fun showConfirmationSections(template: CeaFormTemplatePO) {
        val submissionPO = viewModel.ceaSubmission.value ?: return
        val formType = viewModel.formType.value ?: return
        //Bit map to file arrays
        val signatures = viewModel.signatureMaps.value ?: linkedMapOf()
        val signatureFiles = LinkedHashMap<String, File>()
        var signatureJsonString: String? = null
        if (signatures.isNotEmpty()) {
            //start index of signature 1
            var count = 1
            signatures.forEach { map ->
                signatureFiles[map.key] =
                    File(
                        ImageUtil.saveImageFromBitmap(
                            context = this,
                            bitmap = map.value,
                            fileName = getString(
                                R.string.msg_signature_file_name,
                                count.toString()
                            )
                        )
                    )
                count++
            }
            signatureJsonString = JsonUtil.parseToJsonString(signatureFiles)
        }
        CeaExclusiveConfirmationSectionsActivity.launch(
            this,
            formType,
            template,
            submissionPO,
            signatureJsonString
        )
    }

    private fun showTermsSections(
        client: CeaFormClientPO? = null,
        estateAgentPO: CeaFormEstateAgentPO? = null
    ) {
        val terms = viewModel.template.value?.partyTermSections ?: return
        CeaExclusiveTermSectionsDialogFragment.launch(
            supportFragmentManager,
            terms,
            CeaExclusiveEnum.SignatureSource.AGENT_OWNER_SCREEN,
            client = client,
            estateAgent = estateAgentPO
        )
    }

    private fun updateSignatureKeyAndValue(
        pair: Pair<String, Bitmap>,
        isEstateAgentSignature: Boolean
    ) {
        val maps = viewModel.signatureMaps.value ?: linkedMapOf()
        maps[pair.first] = pair.second

        if (isEstateAgentSignature) {
            viewModel.isAgentSignatureAdded.value = true
            viewModel.agentSignature.value = pair.second
        } else {
            adapter.updateSignatureMaps(maps)
        }
        viewModel.signatureMaps.value = maps
    }

    private fun showPartySections(client: CeaFormClientPO? = null) {
        val formType = viewModel.formType.value ?: return
        val template = viewModel.template.value ?: return
        val ceaSubmission = viewModel.ceaSubmission.value ?: return
        val maps = viewModel.signatureMaps.value ?: linkedMapOf()
        //Update value
        client?.let { ceaClient ->
            if (maps.containsKey(ceaClient.getSignatureKey())) {
                ceaClient.partySignature = getString(R.string.msg_cea_signed)
            }
        }
        CeaExclusiveAgentPartySectionsActivity.launch(
            activity = this,
            formType = formType,
            template = template,
            section = CeaExclusiveEnum.CeaSection.PARTY_SECTION,
            submissionPO = ceaSubmission,
            client = client
        )
    }

    private fun showAgentSections() {
        val formType = viewModel.formType.value ?: return
        val template = viewModel.template.value ?: return
        val ceaSubmission = viewModel.ceaSubmission.value ?: return
        //TODO: update party signature from agent sections....
        CeaExclusiveAgentPartySectionsActivity.launch(
            activity = this,
            formType = formType,
            template = template,
            section = CeaExclusiveEnum.CeaSection.AGENT_SECTION,
            submissionPO = ceaSubmission
        )
    }

    private fun enableSwipeToDelete() {
        //TODO: in future separate as helper
        val mBackground = ColorDrawable()
        val backgroundColor = ContextCompat.getColor(this, R.color.red)
        val clearPaint = Paint()
        val deleteDrawable = ContextCompat.getDrawable(this, R.drawable.ic_trash)
        val intrinsicWidth = deleteDrawable?.intrinsicWidth ?: 0
        val intrinsicHeight = deleteDrawable?.intrinsicHeight ?: 0

        val simpleItemTouchCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteClient(viewHolder.adapterPosition)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                val itemView = viewHolder.itemView
                val itemHeight = itemView.height
                val isCancelled = dX == 0f && !isCurrentlyActive

                if (isCancelled) {
                    c.drawRect(
                        itemView.right + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat(),
                        clearPaint
                    )
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    return
                }
                //Change background color when swiping to delete
                mBackground.color = backgroundColor
                mBackground.setBounds(
                    (itemView.right + dX).toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                mBackground.draw(c)
                //Add delete trash icon by adjusting icon size
                val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
                val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
                val deleteIconRight = itemView.right - deleteIconMargin
                val deleteIconBottom = deleteIconTop + intrinsicHeight
                deleteDrawable?.setBounds(
                    deleteIconLeft,
                    deleteIconTop,
                    deleteIconRight,
                    deleteIconBottom
                )
                deleteDrawable?.setTint(
                    ContextCompat.getColor(
                        this@CeaExclusiveAgentOwnerSectionsActivity,
                        R.color.white_invertible
                    )
                )
                deleteDrawable?.draw(c)
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }
        ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(binding.listClients)
    }

    private fun deleteClient(position: Int) {
        viewModel.removedClientPosition = position
        //will be start from 1
        viewModel.deleteClient(position + 1)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_cea_exclusive_agent_owner_sections
    }

    override fun getViewModelClass(): Class<CeaExclusiveAgentOwnerSectionsViewModel> {
        return CeaExclusiveAgentOwnerSectionsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }
}