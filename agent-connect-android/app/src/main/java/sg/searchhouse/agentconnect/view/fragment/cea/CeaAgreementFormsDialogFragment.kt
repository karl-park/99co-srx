package sg.searchhouse.agentconnect.view.fragment.cea

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentCeaAgreementFormsBinding
import sg.searchhouse.agentconnect.enumeration.api.CeaExclusiveEnum
import sg.searchhouse.agentconnect.view.activity.cea.CeaExclusiveMainSectionsActivity
import sg.searchhouse.agentconnect.view.adapter.cea.CeaAgreementFormsAdapter
import sg.searchhouse.agentconnect.view.fragment.base.FullScreenDialogFragment

class CeaAgreementFormsDialogFragment : FullScreenDialogFragment() {

    private lateinit var binding: DialogFragmentCeaAgreementFormsBinding
    private lateinit var adapter: CeaAgreementFormsAdapter

    companion object {
        private const val TAG_CEA_AGREEMENT_FORMS = "TAG_CEA_AGREEMENT_FORMS"
        fun newInstance() = CeaAgreementFormsDialogFragment()

        fun launch(fragmentManager: FragmentManager) {
            newInstance().show(fragmentManager, TAG_CEA_AGREEMENT_FORMS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_fragment_cea_agreement_forms,
            container,
            false
        )
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListAndAdapter()
        handleListeners()
    }

    private fun setupListAndAdapter() {
        adapter =
            CeaAgreementFormsAdapter(CeaExclusiveEnum.CeaFormType.values().toList()) { formType ->
                activity?.let { activity ->
                    CeaExclusiveMainSectionsActivity.launch(
                        activity,
                        formType
                    )
                    dialog?.dismiss()
                }
            }
        binding.listCeaForms.layoutManager = LinearLayoutManager(activity)
        binding.listCeaForms.adapter = adapter
    }

    private fun handleListeners() {
        binding.toolbar.setNavigationOnClickListener {
            dialog?.dismiss()
        }
    }
}