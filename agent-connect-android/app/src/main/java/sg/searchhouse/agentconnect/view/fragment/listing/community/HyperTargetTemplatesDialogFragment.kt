package sg.searchhouse.agentconnect.view.fragment.listing.community

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_fragment_hyper_target_templates.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentHyperTargetTemplatesBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutManager
import sg.searchhouse.agentconnect.event.community.EditHyperTargetTemplateEvent
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.community.CommunityHyperTargetTemplatePO
import sg.searchhouse.agentconnect.view.adapter.base.BaseListAdapter
import sg.searchhouse.agentconnect.view.adapter.listing.community.HyperTargetTemplatePaginatedAdapter
import sg.searchhouse.agentconnect.view.fragment.base.PaginatedViewModelFullWidthDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.community.HyperTargetTemplatesDialogViewModel

class HyperTargetTemplatesDialogFragment :
    PaginatedViewModelFullWidthDialogFragment<HyperTargetTemplatesDialogViewModel, DialogFragmentHyperTargetTemplatesBinding>() {
    companion object {
        const val TAG = "HyperTargetTemplatesDialogFragment"

        fun newInstance(): HyperTargetTemplatesDialogFragment {
            return HyperTargetTemplatesDialogFragment()
        }
    }

    private val adapter =
        HyperTargetTemplatePaginatedAdapter { delegateLaunchEditHyperTarget(it) }

    private fun delegateLaunchEditHyperTarget(hyperTarget: CommunityHyperTargetTemplatePO) {
        RxBus.publish(EditHyperTargetTemplateEvent(hyperTarget))
        dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeLiveData()
    }

    private fun setupViews() {
        list.setupLayoutManager()
        list.adapter = adapter
        btn_ok.setOnClickListener { dismiss() }
    }

    private fun observeLiveData() {
        viewModel.hyperTemplateMemberCounts.observe(this) {
            adapter.memberCounts = it ?: emptyList()
            adapter.notifyDataSetChanged()
        }
        viewModel.listItems.observeNotNull(this) { listItems ->
            val templatePOs = listItems.filterIsInstance<CommunityHyperTargetTemplatePO>()
            viewModel.performGetMemberCountByHyperTargetPOs(templatePOs)
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_hyper_target_templates
    }

    override fun getViewModelClass(): Class<HyperTargetTemplatesDialogViewModel> {
        return HyperTargetTemplatesDialogViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }

    override fun getList(): RecyclerView {
        return list
    }

    override fun getAdapter(): BaseListAdapter {
        return adapter
    }

    override fun isPreloadList(): Boolean {
        return true
    }
}