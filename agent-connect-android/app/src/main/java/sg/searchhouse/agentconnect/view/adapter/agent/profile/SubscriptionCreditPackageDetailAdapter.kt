package sg.searchhouse.agentconnect.view.adapter.agent.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.Endpoint
import sg.searchhouse.agentconnect.databinding.ListItemSubscriptionCreditPackageBinding
import sg.searchhouse.agentconnect.databinding.ListItemSubscriptionCreditPackageTrialBinding
import sg.searchhouse.agentconnect.dsl.widget.setupLinks
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum.SubscriptionTypeEnum
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.PaymentPlan
import sg.searchhouse.agentconnect.model.api.agent.InstallmentPaymentOptionPO
import sg.searchhouse.agentconnect.model.api.agent.PackageDetailPO
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.util.ViewUtil

class SubscriptionCreditPackageDetailAdapter(
    private val context: Context,
    private val subscriptionPackages: ArrayList<PackageDetailPO>,
    private val proceedToPayment: ((String, Boolean, Int?, Int?) -> Unit)
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var adapter = InstallmentBanksAdapter(context)
    private val paymentOptions: ArrayList<InstallmentPaymentOptionPO> = arrayListOf()

    private var selectedPackageId: String = ""
    private var selectedPackageIdPosition: Int? = null
    private var isInstallment = false

    companion object {
        const val VIEW_TYPE_SUBSCRIPTION_TYPICAL = 1
        const val VIEW_TYPE_SUBSCRIPTION_TRIAL = 2
    }

    init {
        setHasStableIds(true)
    }

    fun preselectTrialPackage() {
        subscriptionPackages.find { it.packageId == SubscriptionTypeEnum.TRIAL.value }?.run {
            val index =
                subscriptionPackages.indexOfFirst { it.packageId == SubscriptionTypeEnum.TRIAL.value }
            selectPackage(packageId, index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SUBSCRIPTION_TRIAL -> {
                ListItemPackageTrialViewHolder(
                    ListItemSubscriptionCreditPackageTrialBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_SUBSCRIPTION_TYPICAL -> {
                ListItemPackageViewHolder(
                    ListItemSubscriptionCreditPackageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> throw IllegalStateException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return subscriptionPackages.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return when (subscriptionPackages[position].packageId) {
            SubscriptionTypeEnum.TRIAL.value -> VIEW_TYPE_SUBSCRIPTION_TRIAL
            else -> VIEW_TYPE_SUBSCRIPTION_TYPICAL
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemPackageViewHolder -> {
                val packageDetail = subscriptionPackages[position]
                holder.binding.subscriptionPackage = packageDetail

                //Showing Bank List
                holder.binding.listInstallmentBanks.layoutManager = LinearLayoutManager(context)
                holder.binding.listInstallmentBanks.adapter = adapter
                showPaymentOptions()

                //Showing package info
                holder.binding.selectedPackageId = selectedPackageId
                holder.binding.layoutPackageInfo.setOnClickListener {
                    selectPackage(packageDetail.packageId, position)
                }

                //Payment plan -> installment or single
                if (isInstallment) {
                    holder.binding.paymentPlan = PaymentPlan.INSTALMENT
                    holder.binding.radioPaymentPlan.setValue(PaymentPlan.INSTALMENT)
                } else {
                    holder.binding.paymentPlan = PaymentPlan.SINGLE
                    holder.binding.radioPaymentPlan.setValue(PaymentPlan.SINGLE)
                }
                holder.binding.radioPaymentPlan.showHideInstallmentPaymentPlan(packageDetail.installmentApplicable)
                holder.binding.radioPaymentPlan.onSelectPaymentPlanListener = { paymentPlan ->
                    isInstallment = paymentPlan == PaymentPlan.INSTALMENT
                    holder.binding.paymentPlan = paymentPlan
                }

                holder.binding.cbMembershipPayment.setOnCheckedChangeListener { compoundButton, b ->
                    if (compoundButton.isPressed) {
                        holder.binding.isEnabledPaymentButton = b
                    }
                }

                holder.binding.btnProceedPayment.setOnClickListener {
                    if (!isInstallment) {
                        proceedToPayment.invoke(
                            packageDetail.packageId,
                            isInstallment,
                            null,
                            null
                        )
                    } else {
                        adapter.selectedPaymentOption?.let { paymentOption ->
                            //TODO: in future need to revamp frontend validation.
                            // currently, use this way first
                            if (paymentOption.showMonthOptions && adapter.selectedAvailableMonth == 0) {
                                ViewUtil.showMessage(R.string.error_missing_available_month)
                            } else {
                                proceedToPayment.invoke(
                                    packageDetail.packageId,
                                    isInstallment,
                                    paymentOption.bankId,
                                    adapter.selectedAvailableMonth
                                )
                            }
                        }
                    } //end of else
                }

                holder.binding.executePendingBindings()
            }
            is ListItemPackageTrialViewHolder -> {
                val packageDetail = subscriptionPackages[position]
                holder.binding.subscriptionPackage = packageDetail
                holder.binding.selectedPackageId = selectedPackageId
                holder.binding.layoutPackageInfo.setOnClickListener {
                    selectPackage(packageDetail.packageId, position)
                }
                holder.binding.btnSubscribe.setOnClickListener {
                    proceedToPayment.invoke(packageDetail.packageId, false, null, null)
                }
                holder.binding.tvAgreement.setupLinks(
                    R.string.msg_agreement_subscribe_trial, listOf(
                        R.string.link_agreement_subscribe_trial_terms_of_use,
                        R.string.link_agreement_subscribe_trial_terms_of_sale
                    ),
                    listOf({
                        IntentUtil.visitSrxUrl(context, Endpoint.TERMS_OF_USE)
                    }, {
                        IntentUtil.visitSrxUrl(context, Endpoint.TERMS_OF_SALE)
                    })
                )
                holder.binding.executePendingBindings()
            }
        }
    }

    private fun selectPackage(packageId: String, position: Int) {
        if (selectedPackageId != packageId) {
            selectedPackageId = packageId
            selectedPackageIdPosition = position
            isInstallment = false
            notifyDataSetChanged()
        }
    }

    private fun showPaymentOptions() {
        if (paymentOptions.isNotEmpty()) {
            adapter.items.clear()
            adapter.items.addAll(paymentOptions)
            adapter.selectedAvailableMonth = 0
            adapter.selectedPaymentOption = paymentOptions[0]
            adapter.isDataSetChangedBySelectionEachBank = false
            adapter.notifyDataSetChanged()
        }
    }

    fun setInstallmentPaymentOptions(options: List<InstallmentPaymentOptionPO>) {
        paymentOptions.clear()
        paymentOptions.addAll(options)
    }

    class ListItemPackageViewHolder(val binding: ListItemSubscriptionCreditPackageBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ListItemPackageTrialViewHolder(val binding: ListItemSubscriptionCreditPackageTrialBinding) :
        RecyclerView.ViewHolder(binding.root)
}