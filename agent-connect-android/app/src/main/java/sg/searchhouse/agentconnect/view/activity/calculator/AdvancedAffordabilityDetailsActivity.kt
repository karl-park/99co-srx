package sg.searchhouse.agentconnect.view.activity.calculator

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityAdvancedAffordabilityDetailsBinding
import sg.searchhouse.agentconnect.dsl.launchActivity
import sg.searchhouse.agentconnect.model.api.calculator.AffordAdvancedPO
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.viewmodel.activity.calculator.AdvancedAffordabilityDetailsViewModel

class AdvancedAffordabilityDetailsActivity :
    ViewModelActivity<AdvancedAffordabilityDetailsViewModel, ActivityAdvancedAffordabilityDetailsBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtras()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.btnShareToClient.setOnClickListener {
            viewModel.affordAdvancedPO.value?.shortenUrl?.run {
                IntentUtil.visitUrl(this@AdvancedAffordabilityDetailsActivity, this)
            }
        }
    }

    private fun setupExtras() {
        val affordAdvancedPO =
            intent.extras?.getSerializable(EXTRA_AFFORD_ADVANCED_PO) as AffordAdvancedPO?
                ?: throw IllegalArgumentException("Missing `EXTRA_AFFORD_ADVANCED_PO`!")
        viewModel.affordAdvancedPO.postValue(affordAdvancedPO)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_advanced_affordability_details
    }

    override fun getViewModelClass(): Class<AdvancedAffordabilityDetailsViewModel> {
        return AdvancedAffordabilityDetailsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar {
        return binding.toolbar
    }

    companion object {
        private const val EXTRA_AFFORD_ADVANCED_PO = "EXTRA_AFFORD_ADVANCED_PO"

        fun launch(activity: Activity, affordAdvancedPO: AffordAdvancedPO) {
            val extras = Bundle()
            extras.putSerializable(EXTRA_AFFORD_ADVANCED_PO, affordAdvancedPO)
            activity.launchActivity(
                AdvancedAffordabilityDetailsActivity::class.java,
                extras = extras
            )
        }
    }
}
