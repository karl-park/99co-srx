package sg.searchhouse.agentconnect.view.adapter.agent.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemInstallmentBankBinding
import sg.searchhouse.agentconnect.helper.DataBindingAdapter
import sg.searchhouse.agentconnect.model.api.agent.InstallmentPaymentOptionPO
import sg.searchhouse.agentconnect.view.widget.agent.profile.SubscriptionAvailableMonthPill

class InstallmentBanksAdapter(val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val items: ArrayList<InstallmentPaymentOptionPO> = arrayListOf()
    var isDataSetChangedBySelectionEachBank = false
    var selectedPaymentOption: InstallmentPaymentOptionPO? = null
    var selectedAvailableMonth: Int = 0
    private var showInstallmentMonths: Boolean = false

    companion object {
        const val BANK_LOGO_SIZE = 150
        const val BANK_LOGO_MARGIN_RIGHT = 40
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListItemInstallmentBankViewHolder(
            ListItemInstallmentBankBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemInstallmentBankViewHolder -> {
                val instalment = items[position]
                //Showing Bank logos
                if (!isDataSetChangedBySelectionEachBank) {
                    populateBankLogos(instalment, holder)
                }
                //Showing available months
                holder.binding.showInstallmentLayout = showInstallmentMonths
                //Installment payment
                holder.binding.instalmentPayment = instalment
                holder.binding.selectedBankId = selectedPaymentOption?.bankId
                holder.binding.rbInstallmentBank.setOnCheckedChangeListener { compoundButton, isChecked ->
                    if (compoundButton.isPressed) {
                        if (isChecked) {
                            showInstallmentMonths = instalment.showMonthOptions
                            if (instalment.showMonthOptions) {
                                populateAvailableMonths(instalment, holder)
                            }
                            isDataSetChangedBySelectionEachBank = true
                            selectedPaymentOption = instalment
                            notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    private fun populateBankLogos(
        instalment: InstallmentPaymentOptionPO,
        holder: ListItemInstallmentBankViewHolder
    ) {
        holder.binding.layoutInstallmentBankLogos.removeAllViews()
        instalment.logos.forEach { logo ->
            val logoImage = ImageView(context)
            val layoutParams = LinearLayout.LayoutParams(BANK_LOGO_SIZE, BANK_LOGO_SIZE)
            layoutParams.setMargins(0, 0, BANK_LOGO_MARGIN_RIGHT, 0)
            logoImage.layoutParams = layoutParams
            DataBindingAdapter.loadImage(logoImage, logo)
            holder.binding.layoutInstallmentBankLogos.addView(logoImage)
        }
    }

    private fun populateAvailableMonths(
        instalment: InstallmentPaymentOptionPO,
        holder: ListItemInstallmentBankViewHolder
    ) {
        holder.binding.layoutInstallmentMonths.removeAllViews()
        instalment.monthsAvailable.forEach { availableMonth ->
            val pill = SubscriptionAvailableMonthPill(context)
            pill.binding.availableMonth = context.getString(
                R.string.label_available_months,
                availableMonth.toString()
            )
            pill.binding.isSelected = availableMonth == selectedAvailableMonth
            pill.binding.btnAvailableMonth.setOnClickListener {
                selectedAvailableMonth = availableMonth
                populateAvailableMonths(instalment, holder)
            }
            holder.binding.layoutInstallmentMonths.addView(pill)
        }
    }

    class ListItemInstallmentBankViewHolder(val binding: ListItemInstallmentBankBinding) :
        RecyclerView.ViewHolder(binding.root)
}