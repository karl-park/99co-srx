package sg.searchhouse.agentconnect.view.fragment.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_fragment_stamp_duty.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentStampDutyBinding
import sg.searchhouse.agentconnect.dsl.widget.setOnClickQuickDelayListener
import sg.searchhouse.agentconnect.enumeration.app.CalculatorAppEnum
import sg.searchhouse.agentconnect.view.activity.calculator.BuyerStampDutyCalculatorActivity
import sg.searchhouse.agentconnect.view.activity.calculator.RentalStampDutyCalculatorActivity
import sg.searchhouse.agentconnect.view.activity.calculator.SellingCalculatorActivity
import sg.searchhouse.agentconnect.view.fragment.base.FullWidthDialogFragment

class StampDutyDialogFragment : FullWidthDialogFragment() {
    private lateinit var binding: DialogFragmentStampDutyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_fragment_stamp_duty,
            container,
            false
        )
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        btn_dismiss.setOnClickQuickDelayListener {
            dismiss()
        }
        list_item_stamp_duty_buyer.setOnClickQuickDelayListener {
            launchActivity(BuyerStampDutyCalculatorActivity::class.java)
            dismiss()
        }
        list_item_stamp_duty_seller.setOnClickQuickDelayListener {
            SellingCalculatorActivity.launch(
                it.context,
                CalculatorAppEnum.SellingCalculatorEntryType.SELLING_STAMP_DUTY
            )
            dismiss()
        }
        list_item_stamp_duty_rental.setOnClickQuickDelayListener {
            launchActivity(RentalStampDutyCalculatorActivity::class.java)
            dismiss()
        }
    }

    companion object {
        const val TAG = "StampDutyDialogFragment"

        fun newInstance(): StampDutyDialogFragment {
            return StampDutyDialogFragment()
        }

        fun launch(fragmentManager: FragmentManager) {
            newInstance().show(fragmentManager, TAG)
        }
    }
}