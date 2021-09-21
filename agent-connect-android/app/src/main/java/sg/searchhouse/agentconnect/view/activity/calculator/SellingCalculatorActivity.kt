package sg.searchhouse.agentconnect.view.activity.calculator

import android.content.Context
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_calculator_selling.*
import kotlinx.android.synthetic.main.layout_header_calculator_selling.*
import kotlinx.android.synthetic.main.layout_selling_calculator_inputs.*
import kotlinx.android.synthetic.main.tab_layout_selling_calculator.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityCalculatorSellingBinding
import sg.searchhouse.agentconnect.dsl.launchActivityV2
import sg.searchhouse.agentconnect.enumeration.app.CalculatorAppEnum.SellingCalculatorEntryType
import sg.searchhouse.agentconnect.enumeration.app.SellingCalculatorTab
import sg.searchhouse.agentconnect.event.calculator.AmendSellingInputEvent
import sg.searchhouse.agentconnect.event.calculator.EnterDetailsEvent
import sg.searchhouse.agentconnect.event.calculator.UpdateSellingCalculatorEvent
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.calculator.SellingCalculatorPagerAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.calculator.SellingCalculatorViewModel

class SellingCalculatorActivity :
    ViewModelActivity<SellingCalculatorViewModel, ActivityCalculatorSellingBinding>() {
    private lateinit var entryType: SellingCalculatorEntryType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtras()
        setupView()
        observeLiveData()
        listenRxBuses()
    }

    private fun setupExtras() {
        entryType = intent.getSerializableExtra(EXTRA_ENTRY_TYPE) as SellingCalculatorEntryType?
            ?: throw IllegalArgumentException("Missing `EXTRA_ENTRY_TYPE` IN `SellingCalculatorActivity`!")
    }

    private fun listenRxBuses() {
        listenRxBus(UpdateSellingCalculatorEvent::class.java) {
            viewModel.calculateSellingResponse.postValue(it.calculateSellingResponse)
        }
        listenRxBus(EnterDetailsEvent::class.java) {
            btn_tab_enter_details.performClick()
        }
    }

    private fun observeLiveData() {
        viewModel.selectedSellingCalculatorTab.observe(this, Observer {
            view_pager.currentItem = it.position
        })
    }

    private fun setupView() {
        setupViewPager()
        setOnClickListeners()
    }

    private fun setupViewPager() {
        view_pager.adapter = SellingCalculatorPagerAdapter(this, supportFragmentManager)
        viewModel.selectedSellingCalculatorTab.postValue(
            when (entryType) {
                SellingCalculatorEntryType.SELLING -> SellingCalculatorTab.LOOK_UP_PROPERTY
                SellingCalculatorEntryType.SELLING_STAMP_DUTY -> SellingCalculatorTab.ENTER_DETAILS
            }
        )
    }

    private fun setOnClickListeners() {
        btn_tab_lookup_property.setOnClickListener {
            viewModel.selectedSellingCalculatorTab.postValue(SellingCalculatorTab.LOOK_UP_PROPERTY)
        }
        btn_tab_enter_details.setOnClickListener {
            viewModel.selectedSellingCalculatorTab.postValue(SellingCalculatorTab.ENTER_DETAILS)
        }
        btn_amend.setOnClickListener { RxBus.publish(AmendSellingInputEvent()) }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_calculator_selling
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }

    override fun getViewModelClass(): Class<SellingCalculatorViewModel> {
        return SellingCalculatorViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    companion object {
        private const val EXTRA_ENTRY_TYPE = "EXTRA_ENTRY_TYPE"

        fun launch(context: Context, entryType: SellingCalculatorEntryType) {
            context.launchActivityV2<SellingCalculatorActivity> {
                putExtra(EXTRA_ENTRY_TYPE, entryType)
            }
        }
    }
}