package sg.searchhouse.agentconnect.view.fragment.agent.cv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.dialog_fragment_cv_edit_general_info.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentCvEditGeneralInfoBinding
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.agent.AgentCvPO
import sg.searchhouse.agentconnect.event.agent.UpdateAgentCv
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.fragment.base.FullScreenDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.agent.cv.CvEditGeneralInfoFragmentViewModel

class CvEditGeneralInfoDialogFragment : FullScreenDialogFragment() {

    private var agentCv: AgentCvPO? = null
    private lateinit var viewModel: CvEditGeneralInfoFragmentViewModel

    companion object {
        const val TAG = "cv_edit_general_info"
        private const val AGENT_CV = "agent_cv"
        fun newInstance(agentCv: AgentCvPO): CvEditGeneralInfoDialogFragment {
            val dialogFragment = CvEditGeneralInfoDialogFragment()
            val bundle = Bundle()
            bundle.putSerializable(AGENT_CV, agentCv)
            dialogFragment.arguments = bundle
            return dialogFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            it.getSerializable(AGENT_CV)?.let { data ->
                agentCv = data as AgentCvPO
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: DialogFragmentCvEditGeneralInfoBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_fragment_cv_edit_general_info,
            container,
            false
        )
        viewModel = ViewModelProvider(this).get(CvEditGeneralInfoFragmentViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        agentCv?.let { viewModel.agentCv.postValue(it) }

        et_general_info.requestFocus()
        et_general_info.post {
            ViewUtil.showKeyboard(et_general_info)
        }
        handleViewListeners()
    }

    private fun handleViewListeners() {
        toolbar_general_info.setNavigationOnClickListener {
            dialog?.dismiss()
        }

        tv_general_info_save.setOnClickListener {
            et_general_info.clearFocus()
            ViewUtil.hideKeyboard(et_general_info)
            viewModel.agentCv.value?.let { cv ->
                RxBus.publish(UpdateAgentCv(cv))
                dialog?.dismiss()
            }
        }
    }
}