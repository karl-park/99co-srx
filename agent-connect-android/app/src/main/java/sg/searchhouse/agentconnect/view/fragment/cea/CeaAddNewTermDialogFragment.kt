package sg.searchhouse.agentconnect.view.fragment.cea

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentCeaAddNewTermBinding
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.cea.CeaFormTermPO
import sg.searchhouse.agentconnect.event.cea.UpdateCeaSelfDefinedTerm
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.fragment.base.FullScreenDialogFragment

class CeaAddNewTermDialogFragment : FullScreenDialogFragment() {

    private lateinit var binding: DialogFragmentCeaAddNewTermBinding
    private var selfDefinedTerm: CeaFormTermPO? = null

    companion object {
        private const val TAG_CEA_NEW_TERM_DIALOG = "TAG_CEA_NEW_TERM_DIALOG"
        private const val EXTRA_KEY_SELF_DEFINED_TERM = "EXTRA_KEY_SELF_DEFINED_TERM"

        fun newInstance(term: CeaFormTermPO): CeaAddNewTermDialogFragment {
            val dialogFragment = CeaAddNewTermDialogFragment()
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_KEY_SELF_DEFINED_TERM, term)
            dialogFragment.arguments = bundle
            return dialogFragment
        }

        fun launch(fragmentManager: FragmentManager, term: CeaFormTermPO) {
            newInstance(term).show(fragmentManager, TAG_CEA_NEW_TERM_DIALOG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_fragment_cea_add_new_term,
            container,
            false
        )
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupParamsFromExtra()
        showKeyboard()
        handleListeners()
    }

    private fun setupParamsFromExtra() {
        val arguments = arguments ?: return
        arguments.getSerializable(EXTRA_KEY_SELF_DEFINED_TERM)?.let { term ->
            selfDefinedTerm = term as CeaFormTermPO
            binding.etTerm.setText(selfDefinedTerm?.term)
        }
    }

    private fun showKeyboard() {
        binding.etTerm.requestFocus()
        binding.etTerm.post {
            ViewUtil.showKeyboard(binding.etTerm)
        }
    }

    private fun handleListeners() {

        binding.toolbar.setNavigationOnClickListener {
            dialog?.dismiss()
        }

        binding.tvSaveNewTerm.setOnClickListener {
            ViewUtil.hideKeyboard(binding.etTerm)
            val item = selfDefinedTerm ?: return@setOnClickListener
            item.term = binding.etTerm.text.toString()
            RxBus.publish(UpdateCeaSelfDefinedTerm(item))
            dialog?.dismiss()
        }
    }

}