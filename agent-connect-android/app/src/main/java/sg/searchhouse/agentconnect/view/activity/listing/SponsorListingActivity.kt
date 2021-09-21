package sg.searchhouse.agentconnect.view.activity.listing

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_sponsor_listing.*
import kotlinx.android.synthetic.main.layout_community_default.*
import kotlinx.android.synthetic.main.layout_community_hyper_target.*
import kotlinx.android.synthetic.main.layout_community_hyper_target_draft_card.*
import kotlinx.android.synthetic.main.layout_community_hyper_target_existing.*
import kotlinx.android.synthetic.main.layout_community_hyper_target_templates.*
import kotlinx.android.synthetic.main.tab_layout_community_post_promote_type.*
import kotlinx.android.synthetic.main.tab_layout_community_post_target.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivitySponsorListingBinding
import sg.searchhouse.agentconnect.dsl.getIntList
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.widget.setOnTextChangedListener
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutManager
import sg.searchhouse.agentconnect.dsl.widget.setupLinks
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum
import sg.searchhouse.agentconnect.enumeration.api.CommunityEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.enumeration.app.CommunityPostEnum
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.event.community.EditHyperTargetTemplateEvent
import sg.searchhouse.agentconnect.event.community.RemoveTargetCommunityEvent
import sg.searchhouse.agentconnect.model.api.community.CommunityHyperTargetTemplatePO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.ActivateSrxCreditListingsResponse.ActivateSrxCreditListingSummaryPO
import sg.searchhouse.agentconnect.util.AuthUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.agent.profile.SubscriptionCreditPackageDetailsActivity
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.activity.listing.community.CommunityHyperTargetActivity
import sg.searchhouse.agentconnect.view.activity.listing.community.SearchPlanningAreasActivity
import sg.searchhouse.agentconnect.view.adapter.community.TargetCommunityAdapter
import sg.searchhouse.agentconnect.view.adapter.listing.community.HyperTargetTemplateAdapter
import sg.searchhouse.agentconnect.view.fragment.listing.community.HyperTargetTemplatesDialogFragment
import sg.searchhouse.agentconnect.view.widget.listing.community.SponsorDurationPill
import sg.searchhouse.agentconnect.viewmodel.activity.listing.SponsorListingViewModel

class SponsorListingActivity :
    ViewModelActivity<SponsorListingViewModel, ActivitySponsorListingBinding>() {

    private val targetCommunityAdapter = TargetCommunityAdapter()
    private val hyperTargetTemplateAdapter =
        HyperTargetTemplateAdapter { updateHyperTargetTemplate(it) }

    private var durationPills: List<SponsorDurationPill>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtras()
        setupViews()
        observeLiveData()
        listenRxBuses()
    }

    private fun listenRxBuses() {
        listenRxBus(RemoveTargetCommunityEvent::class.java) { event ->
            val original = viewModel.targetCommunityIds.value ?: emptyList()
            viewModel.targetCommunityIds.postValue(original.filter { it != event.communityId })
        }
        listenRxBus(EditHyperTargetTemplateEvent::class.java) { event ->
            updateHyperTargetTemplate(event.hyperTargetTemplatePO)
        }
    }

    private fun observeLiveData() {
        viewModel.listingId.observe(this) {
            viewModel.performGetActivateSrxCreditSummary()
        }
        viewModel.summary.observe(this) {
            if (it != null && !viewModel.isSummaryInitialized) {
                viewModel.isSummaryInitialized = true
                initializeSummary(it)
            }
        }
        viewModel.getSrxCreditPackageDetailsStatus.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    visitSubscriptionPackagePage()
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                ApiStatus.StatusKey.ERROR -> {
                    // Do nothing
                }
                else -> {
                }
            }
        }
        viewModel.activateSrxCreditListingsStatus.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    ViewUtil.showMessage(R.string.toast_sponsor_listing_success)
                    finish()
                }
                ApiStatus.StatusKey.FAIL -> {
                    // Do nothing
                }
                ApiStatus.StatusKey.ERROR -> {
                    // Do nothing
                }
                else -> {
                }
            }
        }
        viewModel.targetCommunityIds.observe(this) {
            viewModel.performGetCommunityMemberCounts()
            viewModel.performGetActivateSrxCreditSummary(isReload = true)
        }
        viewModel.targetCommunities.observe(this) {
            targetCommunityAdapter.canRemoveItem =
                viewModel.hasExistingTargetCommunities.value != true
            targetCommunityAdapter.items = it ?: emptyList()
            targetCommunityAdapter.notifyDataSetChanged()
        }
        viewModel.listingHeader.observe(this) {
            if (it?.isNotEmpty() == true && viewModel.isShowListingHeaderError.value == true) {
                viewModel.isShowListingHeaderError.postValue(false)
            }
        }
        viewModel.listingRemark.observe(this) {
            if (it?.isNotEmpty() == true && viewModel.isShowListingRemarkError.value == true) {
                viewModel.isShowListingRemarkError.postValue(false)
            }
        }
        viewModel.sponsorDuration.observe(this) {
            durationPills?.map { it.binding.invalidateAll() }
        }
        viewModel.hyperTargetTemplates.observe(this) {
            hyperTargetTemplateAdapter.templates = it ?: emptyList()
            hyperTargetTemplateAdapter.notifyDataSetChanged()
            viewModel.performGetMemberCountByHyperTargetPOs(it ?: emptyList())
        }
        viewModel.newHyperTargetTemplatePO.observe(this) {
            viewModel.performGetActivateSrxCreditSummary(isReload = true)
        }
        viewModel.hyperTemplateMemberCounts.observe(this) {
            hyperTargetTemplateAdapter.memberCounts = it ?: emptyList()
            hyperTargetTemplateAdapter.notifyDataSetChanged()
        }
    }

    private fun initializeSummary(summary: ActivateSrxCreditListingSummaryPO) {
        // Preload target locations
        viewModel.targetCommunityIds.postValue(summary.sponsoredLocation.getIntList())
        viewModel.existingHyperTargetTemplatePO.postValue(summary.hyperTargetPO)

        // Preload text boxes
        val firstListing = summary.listings?.firstOrNull() ?: return
        viewModel.listingHeader.postValue(firstListing.listingHeader)
        viewModel.listingRemark.postValue(firstListing.remarks)

        viewModel.newHyperTargetTemplatePO.postValue(firstListing.getHyperTargetTemplatePO())

        viewModel.performGetCommunityHyperTargets()
    }

    private fun setupExtras() {
        val listingId = intent.extras?.getInt(EXTRA_LISTING_ID, -1)
        viewModel.listingId.postValue(listingId)
    }

    private fun setupViews() {
        layout_container.setupLayoutAnimation()
        setupList()
        populateDurations()
        setupListeners()
        setupAgreementTextLink()
    }

    private fun setupList() {
        list_target_communities.setupLayoutManager(RecyclerView.HORIZONTAL)
        list_target_communities.adapter = targetCommunityAdapter

        list_hyper_target_templates.setupLayoutManager()
        list_hyper_target_templates.adapter = hyperTargetTemplateAdapter
    }

    private fun confirmSubmit(onConfirm: () -> Unit) {
        val creditsDeductible = viewModel.summary.value?.creditsDeductible ?: 0
        val dialogMessage = resources.getQuantityString(
            R.plurals.dialog_confirm_post_community,
            creditsDeductible,
            NumberUtil.formatThousand(creditsDeductible)
        )
        dialogUtil.showActionDialog(dialogMessage) { onConfirm.invoke() }
    }

    override fun onBackPressed() {
        if (viewModel.isSubmitting.value == true) {
            ViewUtil.showMessage(R.string.toast_submitting)
        } else {
            dialogUtil.showActionDialog(R.string.dialog_confirm_quit_draft_community_post) {
                super.onBackPressed()
            }
        }
    }

    private fun onClickConfirmButton() {
        if (viewModel.canSubmit()) {
            confirmSubmit {
                try {
                    viewModel.performActivateSrxCreditForListings()
                } catch (e: IllegalArgumentException) {
                    val message =
                        e.message ?: "Missing arguments for `performActivateSrxCreditForListings`!"
                    ViewUtil.showMessage(message)
                }
            }
        } else {
            viewModel.showErrors()
        }
    }

    private fun setupListeners() {
        cb_agree.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isCheckAgreement.postValue(isChecked)
        }
        et_listing_header.setOnTextChangedListener { viewModel.listingHeader.postValue(it) }
        et_listing_remark.setOnTextChangedListener { viewModel.listingRemark.postValue(it) }
        btn_confirm.setOnClickListener { onClickConfirmButton() }
        layout_wallet_item_sponsor.setOnClickListener {
            viewModel.performGetSrxCreditPackageDetails()
        }
        ib_wallet.setOnClickListener {
            viewModel.performGetSrxCreditPackageDetails()
        }
        ib_clear_listing_header.setOnClickListener {
            dialogUtil.showActionDialog(R.string.dialog_confirm_clear_text) {
                viewModel.listingHeader.postValue(null)
            }
        }
        ib_clear_listing_remark.setOnClickListener {
            dialogUtil.showActionDialog(R.string.dialog_confirm_clear_text) {
                viewModel.listingRemark.postValue(null)
            }
        }

        btn_tab_listing.setOnClickListener {
            viewModel.promoteType.postValue(CommunityPostEnum.PromoteType.LISTING)
        }
        btn_tab_post.setOnClickListener {
            viewModel.promoteType.postValue(CommunityPostEnum.PromoteType.POST)
        }

        btn_tab_community.setOnClickListener {
            if (!viewModel.hasExistingHyperTargetTemplate()) {
                viewModel.target.postValue(CommunityPostEnum.Target.COMMUNITY)
            }
        }
        btn_tab_hyper.setOnClickListener {
            if (!viewModel.hasExistingRegularCommunities()) {
                viewModel.target.postValue(CommunityPostEnum.Target.HYPER)
            }
        }
        btn_search.setOnClickListener {
            val targetCommunityIds = viewModel.targetCommunityIds.value?.joinToString(",")
            SearchPlanningAreasActivity.launchForResult(
                this,
                targetCommunityIds,
                REQUEST_CODE_TARGET_COMMUNITY
            )
        }

        btn_create_hyper_target.setOnClickListener { launchCreateHyperTarget() }
        btn_create_hyper_target_2.setOnClickListener { launchCreateHyperTarget() }
        btn_edit_hyper_target_template.setOnClickListener {
            launchEditHyperTarget()
        }
        btn_remove_hyper_target_template.setOnClickListener { promptRemoveHyperTarget() }
        btn_view_hyper_target_template.setOnClickListener { launchViewHyperTarget() }
        btn_more_hyper_target_templates.setOnClickListener { viewMoreHyperTargetTemplates() }
    }

    private fun viewMoreHyperTargetTemplates() {
        HyperTargetTemplatesDialogFragment.newInstance()
            .show(supportFragmentManager, HyperTargetTemplatesDialogFragment.TAG)
    }

    private fun launchCreateHyperTarget() {
        CommunityHyperTargetActivity.launchCreate(this, REQUEST_CODE_HYPER_TARGET)
    }

    private fun launchEditHyperTarget() {
        val hyperTarget = viewModel.newHyperTargetTemplatePO.value ?: return
        CommunityHyperTargetActivity.launchEdit(this, hyperTarget, REQUEST_CODE_HYPER_TARGET)
    }

    private fun launchEditHyperTarget(hyperTarget: CommunityHyperTargetTemplatePO) {
        CommunityHyperTargetActivity.launchEdit(this, hyperTarget, REQUEST_CODE_HYPER_TARGET)
    }

    private fun launchViewHyperTarget() {
        val hyperTarget = viewModel.existingHyperTargetTemplatePO.value ?: return
        CommunityHyperTargetActivity.launchView(this, hyperTarget)
    }

    private fun promptRemoveHyperTarget() {
        dialogUtil.showActionDialog(R.string.dialog_confirm_remove_hyper_target) {
            viewModel.newHyperTargetTemplatePO.postValue(null)
            ViewUtil.showMessage(R.string.toast_remove_hyper_target_removed)
        }
    }

    private fun populateDurations() {
        durationPills = CommunityEnum.SponsorDuration.values().map { addDurationPill(it) }
    }

    private fun addDurationPill(sponsorDuration: CommunityEnum.SponsorDuration): SponsorDurationPill {
        val pill = SponsorDurationPill(this)
        pill.binding.sponsorDuration = sponsorDuration
        pill.binding.viewModel = viewModel
        pill.binding.button.setOnClickListener { viewModel.toggleSponsorDuration(sponsorDuration) }
        layout_durations.addView(pill)
        return pill
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_TARGET_COMMUNITY -> {
                if (resultCode == RESULT_OK) {
                    val communityIds =
                        data?.getStringExtra(SearchPlanningAreasActivity.EXTRA_OUTPUT_COMMUNITY_IDS)
                            ?: ""
                    viewModel.targetCommunityIds.postValue(communityIds.getIntList())
                }
            }
            REQUEST_CODE_HYPER_TARGET -> {
                if (resultCode == RESULT_OK) {
                    val templatePO =
                        data?.getSerializableExtra(CommunityHyperTargetActivity.EXTRA_OUTPUT_TEMPLATE_PO) as CommunityHyperTargetTemplatePO
                    updateHyperTargetTemplate(templatePO)
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun updateHyperTargetTemplate(templatePO: CommunityHyperTargetTemplatePO) {
        viewModel.newHyperTargetTemplatePO.postValue(templatePO)
    }

    private fun visitSubscriptionPackagePage() {
        AuthUtil.checkModuleAccessibility(
            module = AccessibilityEnum.AdvisorModule.AGENT_PROFILE,
            function = AccessibilityEnum.InAccessibleFunction.AGENT_PROFILE_WALLET,
            onSuccessAccessibility = {
                SubscriptionCreditPackageDetailsActivity.launch(
                    context = this,
                    paymentPurpose = AgentProfileAndCvEnum.PaymentPurpose.CREDIT,
                    source = AgentProfileAndCvEnum.SubscriptionCreditSource.SPONSOR_LISTING,
                    creditType = ListingManagementEnum.SrxCreditMainType.SPONSORED_POST.value
                )
            })
    }

    private fun setupAgreementTextLink() {
        val links = listOf(
            R.string.agreement_sponsor_listing_link_term_of_use,
            R.string.agreement_sponsor_listing_link_term_of_sale
        )
        val onTermOfUseClick = {
            // TODO
        }
        val onTermOfSaleClick = {
            // TODO
        }
        val onClickListeners = listOf(onTermOfUseClick, onTermOfSaleClick)
        tv_agreement.setupLinks(R.string.agreement_sponsor_listing, links, onClickListeners)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_sponsor_listing
    }

    override fun getViewModelClass(): Class<SponsorListingViewModel> {
        return SponsorListingViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }

    companion object {
        private const val EXTRA_LISTING_ID = "EXTRA_LISTING_ID"
        private const val REQUEST_CODE_TARGET_COMMUNITY = 3
        private const val REQUEST_CODE_HYPER_TARGET = 4

        fun launch(activity: BaseActivity, listingId: Int) {
            val extras = Bundle()
            extras.putInt(EXTRA_LISTING_ID, listingId)
            activity.launchActivity(SponsorListingActivity::class.java, extras)
        }

        fun launchForResult(activity: BaseActivity, listingId: Int, requestCode: Int) {
            val extras = Bundle()
            extras.putInt(EXTRA_LISTING_ID, listingId)
            activity.launchActivityForResult(
                SponsorListingActivity::class.java,
                extras,
                requestCode
            )
        }
    }
}
