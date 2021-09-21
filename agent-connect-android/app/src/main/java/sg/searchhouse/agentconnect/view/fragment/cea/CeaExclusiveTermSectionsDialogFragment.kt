package sg.searchhouse.agentconnect.view.fragment.cea

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentCeaExclusiveTermSectionsBinding
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum.*
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.cea.CeaFormClientPO
import sg.searchhouse.agentconnect.model.api.cea.CeaFormEstateAgentPO
import sg.searchhouse.agentconnect.model.api.cea.CeaFormRowPO
import sg.searchhouse.agentconnect.model.api.cea.CeaFormSectionPO
import sg.searchhouse.agentconnect.event.cea.CeaUpdateSignature
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.adapter.cea.CeaExclusiveDetailSectionsAdapter
import sg.searchhouse.agentconnect.view.fragment.base.FullScreenDialogFragment
import java.io.Serializable

class CeaExclusiveTermSectionsDialogFragment : FullScreenDialogFragment() {

    private lateinit var binding: DialogFragmentCeaExclusiveTermSectionsBinding
    private lateinit var adapter: CeaExclusiveDetailSectionsAdapter

    private var terms = arrayListOf<Any>()
    private var signatureKey: String = ""
    private var isEstateAgentPO: Boolean = false
    private var signatureSource: SignatureSource = SignatureSource.AGENT_OWNER_SCREEN

    companion object {
        private const val TAG_CEA_PARTY_TERMS = "TAG_CEA_PARTY_TERMS"
        private const val EXTRA_KEY_CEA_PARTY_TERMS = "EXTRA_KEY_CEA_PARTY_TERMS"
        private const val EXTRA_KEY_CEA_ESTATE_AGENT = "EXTRA_KEY_CEA_ESTATE_AGENT"
        private const val EXTRA_KEY_CEA_CLIENT = "EXTRA_KEY_CEA_CLIENT"
        private const val EXTRA_KEY_CEA_SIGNATURE_SOURCE = "EXTRA_KEY_CEA_SIGNATURE_SOURCE"

        fun newInstance(
            partyTerms: List<CeaFormSectionPO>,
            source: SignatureSource,
            estateAgent: CeaFormEstateAgentPO? = null,
            client: CeaFormClientPO? = null
        ): CeaExclusiveTermSectionsDialogFragment {
            val dialogFragment = CeaExclusiveTermSectionsDialogFragment()
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_KEY_CEA_PARTY_TERMS, partyTerms as Serializable)
            bundle.putSerializable(EXTRA_KEY_CEA_SIGNATURE_SOURCE, source)
            estateAgent?.let { bundle.putSerializable(EXTRA_KEY_CEA_ESTATE_AGENT, it) }
            client?.let { bundle.putSerializable(EXTRA_KEY_CEA_CLIENT, it) }
            dialogFragment.arguments = bundle
            return dialogFragment
        }

        fun launch(
            fragmentManager: FragmentManager,
            partyTerms: List<CeaFormSectionPO>,
            source: SignatureSource,
            estateAgent: CeaFormEstateAgentPO? = null,
            client: CeaFormClientPO? = null
        ) {
            newInstance(partyTerms, source, estateAgent, client).show(
                fragmentManager,
                TAG_CEA_PARTY_TERMS
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_fragment_cea_exclusive_term_sections,
            container,
            false
        )
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupParamsFromExtra()
        setupListAndAdapter()
        handleListeners()
    }

    private fun setupParamsFromExtra() {
        val bundle = arguments ?: return
        bundle.getSerializable(EXTRA_KEY_CEA_PARTY_TERMS)?.let {
            (it as List<*>).filterIsInstance<CeaFormSectionPO>().forEach { section ->
                terms.add(section)
                if (section.rowArray.isNotEmpty()) {
                    terms.addAll(section.rowArray)
                }
            }
        }
        bundle.getSerializable(EXTRA_KEY_CEA_SIGNATURE_SOURCE)?.let {
            signatureSource = it as SignatureSource
        }
        bundle.getSerializable(EXTRA_KEY_CEA_ESTATE_AGENT)
            ?.let { agent ->
                val estateAgentPO = agent as CeaFormEstateAgentPO
                signatureKey = estateAgentPO.getAgentSignatureKey()
                isEstateAgentPO = true
                binding.tvPartyName.text =
                    getString(R.string.label_cea_party_name, estateAgentPO.partyName)

            }
        bundle.getSerializable(EXTRA_KEY_CEA_CLIENT)
            ?.let { client ->
                val clientPO = client as CeaFormClientPO
                signatureKey = clientPO.getSignatureKey()
                isEstateAgentPO = false
                binding.tvPartyName.text =
                    getString(R.string.label_cea_party_name, clientPO.partyName)
            }
    }

    private fun setupListAndAdapter() {
        val context = activity?.applicationContext ?: return
        adapter = CeaExclusiveDetailSectionsAdapter(
            context,
            dialogUtil,
            onUpdateValue = { row, _ -> updateValue(row) })
        adapter.updateItems(terms)
        binding.listCeaPartyTerms.layoutManager = LinearLayoutManager(activity)
        binding.listCeaPartyTerms.adapter = adapter
    }

    private fun updateValue(row: CeaFormRowPO) {
        terms.find { it is CeaFormRowPO && it.keyValue == row.keyValue }?.let {
            if (it is CeaFormRowPO) {
                it.rowValue = row.rowValue
            }
        }
    }

    private fun handleListeners() {
        binding.toolbar.setNavigationOnClickListener {
            dialog?.dismiss()
        }
        binding.tvClearSignature.setOnClickListener {
            binding.layoutSignaturePad.clear()
        }
        binding.btnConfirmSignature.setOnClickListener {
            val ceaRowFormPO = terms.filterIsInstance<CeaFormRowPO>()
                .find { it.keyValue == CeaFormRowTypeKeyValue.UNDERSTOOD_TERMS.value }
                ?: return@setOnClickListener
            if (!ceaRowFormPO.getRowValueBooleanForToggleType()) {
                ViewUtil.showMessage(R.string.msg_cea_acknowledge_terms_and_condition)
            } else if (binding.layoutSignaturePad.isEmpty) {
                ViewUtil.showMessage(R.string.msg_cea_required_sign)
            } else {
                //Reset row value after confirm signing
                ceaRowFormPO.rowValue = ""
                RxBus.publish(
                    CeaUpdateSignature(
                        Pair(signatureKey, binding.layoutSignaturePad.signatureBitmap),
                        isEstateAgentPO,
                        signatureSource
                    )
                )
                dialog?.dismiss()
            }
        }
    }
}