package sg.searchhouse.agentconnect.view.fragment.agent.client

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_fragment_invite_agent_client.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentInviteAgentClientBinding
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFullWidthDialogFragment
import sg.searchhouse.agentconnect.view.fragment.common.QrCodeDialogFragment
import sg.searchhouse.agentconnect.viewmodel.activity.agent.client.InviteAgentClientsViewModel

class InviteAgentClientDialogFragment :
    ViewModelFullWidthDialogFragment<InviteAgentClientsViewModel, DialogFragmentInviteAgentClientBinding>() {
    companion object {
        const val TAG = "InviteAgentClientDialogFragment"

        fun newInstance(): InviteAgentClientDialogFragment {
            return InviteAgentClientDialogFragment()
        }

        fun launch(supportFragmentManager: FragmentManager) {
            newInstance().show(supportFragmentManager, TAG)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        viewModel.performRequest()
    }

    private fun setOnClickListeners() {
        btn_dismiss.setOnClickListener { dismiss() }
        layout_header.setOnClickListener { dismiss() }
        list_item_invite_client_app.setOnClickListener {
            viewModel.inviteMessage.value?.run {
                IntentUtil.shareText(it.context, this)
                dismiss()
            }
        }
        list_item_invite_client_copy_link.setOnClickListener {
            viewModel.mainResponse.value?.run {
                ViewUtil.copyToClipBoard(it.context, inviteUrl)
                ViewUtil.showFeedbackMessage(R.string.toast_copied_link)
            }
        }
        list_item_invite_client_qr_code.setOnClickListener {
            viewModel.mainResponse.value?.run {
                QrCodeDialogFragment.launch(
                    childFragmentManager,
                    inviteUrl
                )
            }
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_invite_agent_client
    }

    override fun getViewModelClass(): Class<InviteAgentClientsViewModel> {
        return InviteAgentClientsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}