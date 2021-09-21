package sg.searchhouse.agentconnect.view.widget.agent.profile

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioGroup
import sg.searchhouse.agentconnect.databinding.RadioGroupPaymentPlanBinding
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.PaymentPlan

class PaymentPlanRadioGroup(context: Context, attributeSet: AttributeSet? = null) :
    RadioGroup(context, attributeSet) {

    var onSelectPaymentPlanListener: ((PaymentPlan) -> Unit)? = null

    private val binding = RadioGroupPaymentPlanBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.rbSingle.setOnClickListener { onSelectPaymentPlanListener?.invoke(PaymentPlan.SINGLE) }
        binding.rbInstalment.setOnClickListener { onSelectPaymentPlanListener?.invoke(PaymentPlan.INSTALMENT) }
    }

    fun setValue(paymentPlan: PaymentPlan) {
        when (paymentPlan) {
            PaymentPlan.SINGLE -> binding.rbSingle.isChecked = true
            PaymentPlan.INSTALMENT -> binding.rbInstalment.isChecked = true
        }
    }

    fun showHideInstallmentPaymentPlan(instalmentPlanFlag: Boolean) {
        if (instalmentPlanFlag) {
            binding.rbInstalment.visibility = View.VISIBLE
        } else {
            binding.rbInstalment.visibility = View.GONE
        }
    }
}