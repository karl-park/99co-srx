package sg.searchhouse.agentconnect.view.widget.agent.profile

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.LayoutProfileWalletBinding
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.PaymentPurpose
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.SubscriptionCreditSource
import sg.searchhouse.agentconnect.event.agent.VisitGroupSubscriptionWebEvent
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.agent.UserCreditPO
import sg.searchhouse.agentconnect.util.AuthUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.view.activity.agent.profile.SubscriptionCreditPackageDetailsActivity
import sg.searchhouse.agentconnect.view.adapter.agent.profile.WalletCreditAdapter

class ProfileWallet(context: Context, attributeSet: AttributeSet? = null) :
    LinearLayout(context, attributeSet) {

    private var userCreditPO: UserCreditPO? = null

    private var adapter = WalletCreditAdapter(onSelectWalletCredit = { credit: UserCreditPO ->
        userCreditPO = credit
        if (credit.isPostListing()) {
            RxBus.publish(VisitGroupSubscriptionWebEvent())
        } else if (NumberUtil.isNaturalNumber(credit.type)) {
            binding.viewModel?.getSubscriptionPackageDetails(credit.type.toInt())
        }
    }, onClickLearnMore = {
        // TODO Learn more about sponsor
    })

    val binding: LayoutProfileWalletBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.layout_profile_wallet,
        this,
        true
    )

    init {
        binding.listCredits.layoutManager = LinearLayoutManager(context)
        binding.listCredits.adapter = adapter
        binding.cardSharedWallet.btnTopUp.setOnClickListener { topUpSharedWallet() }
    }

    private fun topUpSharedWallet() {
        RxBus.publish(VisitGroupSubscriptionWebEvent())
    }

    fun populateCredits(items: ArrayList<UserCreditPO>) {
        adapter.items.clear()
        adapter.items.addAll(items)
        adapter.notifyDataSetChanged()
    }

    fun directToSubscriptionPackageScreen() {
        AuthUtil.checkModuleAccessibility(
            module = AccessibilityEnum.AdvisorModule.AGENT_PROFILE,
            function = AccessibilityEnum.InAccessibleFunction.AGENT_PROFILE_WALLET,
            onSuccessAccessibility = {
                val creditType = userCreditPO?.type ?: ""
                if (NumberUtil.isInt(creditType)) {
                    SubscriptionCreditPackageDetailsActivity.launch(
                        context = context,
                        paymentPurpose = PaymentPurpose.CREDIT,
                        source = SubscriptionCreditSource.AGENT_CV,
                        creditType = creditType.toInt()
                    )
                }
            })
    }
}