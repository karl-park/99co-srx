package sg.searchhouse.agentconnect.view.activity.calculator

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityCalculatorsBinding
import sg.searchhouse.agentconnect.dsl.launchActivityV2
import sg.searchhouse.agentconnect.enumeration.app.CalculatorAppEnum
import sg.searchhouse.agentconnect.enumeration.app.CalculatorAppEnum.CalculatorType
import sg.searchhouse.agentconnect.view.activity.base.DataBindActivity
import sg.searchhouse.agentconnect.view.adapter.calculator.CalculatorMenuAdapter
import sg.searchhouse.agentconnect.view.fragment.calculator.StampDutyDialogFragment

class CalculatorsActivity : DataBindActivity<ActivityCalculatorsBinding>() {
    private val adapter = CalculatorMenuAdapter(CalculatorType.values().toList()) {
        selectCalculator(it)
    }

    private var stampDutyDialogFragment: StampDutyDialogFragment? = null

    private fun selectCalculator(calculatorType: CalculatorType) {
        when (calculatorType) {
            CalculatorType.AFFORDABILITY_QUICK -> launchActivityV2<AffordabilityCalculatorActivity>()
            CalculatorType.AFFORDABILITY_ADVANCED -> launchActivityV2<AdvancedAffordabilityCalculatorActivity>()
            CalculatorType.SELLING -> {
                SellingCalculatorActivity.launch(
                    this,
                    CalculatorAppEnum.SellingCalculatorEntryType.SELLING
                )
            }
            CalculatorType.STAMP_DUTY -> showStampDutyDialogFragment()
        }
    }

    private fun showStampDutyDialogFragment() {
        if (stampDutyDialogFragment != null) {
            if (stampDutyDialogFragment?.isVisible != true) {
                stampDutyDialogFragment?.show(supportFragmentManager, StampDutyDialogFragment.TAG)
            }
        } else {
            stampDutyDialogFragment = StampDutyDialogFragment.newInstance()
            stampDutyDialogFragment?.show(supportFragmentManager, StampDutyDialogFragment.TAG)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupListAndAdapter()
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_calculators
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

    private fun setupListAndAdapter() {
        binding.list.layoutManager = LinearLayoutManager(this)
        binding.list.adapter = adapter
    }
}