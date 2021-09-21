package sg.searchhouse.agentconnect.view.activity.calculator

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_calculator_affordability.*
import kotlinx.android.synthetic.main.card_affordability_type.view.*
import kotlinx.android.synthetic.main.edit_text_new_pill_number.view.*
import kotlinx.android.synthetic.main.layout_affordability_income_required.*
import kotlinx.android.synthetic.main.layout_affordability_max_purchase_price.*
import kotlinx.android.synthetic.main.layout_header_calculator_affordability.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityCalculatorAffordabilityBinding
import sg.searchhouse.agentconnect.dsl.widget.setOnEnterListener
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import sg.searchhouse.agentconnect.enumeration.app.AffordabilityCalculatorOption
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.widget.common.PillNumberEditText
import sg.searchhouse.agentconnect.viewmodel.activity.calculator.AffordabilityCalculatorViewModel

class AffordabilityCalculatorActivity :
    ViewModelActivity<AffordabilityCalculatorViewModel, ActivityCalculatorAffordabilityBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.maxPurchasePriceInput.observe(this, Observer {
            viewModel.calculateMaxPurchasePrice(it)
        })

        viewModel.affordQuickIncomeRequiredInput.observe(this, Observer {
            viewModel.calculateIncomeRequired(it)
        })
    }

    private fun setupViews() {
        layout_affordability_header.setupLayoutAnimation()
        setOnClickListeners()
        setupTextBoxes()
        setupTextBoxesNextFocus()
    }

    private fun setupTextBoxesNextFocus() {
        et_max_purchase_price_monthly_fixed_income.et_display.setOnEnterListener {
            et_max_purchase_price_monthly_debt_expenses.requestFocusAndShowKeyboard()
        }
        et_max_purchase_price_monthly_debt_expenses.et_display.setOnEnterListener {
            et_max_purchase_price_down_payment.requestFocusAndShowKeyboard()
        }
        et_max_purchase_price_down_payment.et_display.setOnEnterListener {
            ViewUtil.hideKeyboard(et_max_purchase_price_down_payment.et_display)
        }
        et_income_required_property_purchase_price.et_display.setOnEnterListener {
            et_income_required_down_payment.requestFocusAndShowKeyboard()
        }
        et_income_required_down_payment.et_display.setOnEnterListener {
            et_income_required_monthly_debt_expenses.requestFocusAndShowKeyboard()
        }
        et_income_required_monthly_debt_expenses.et_display.setOnEnterListener {
            ViewUtil.hideKeyboard(et_income_required_monthly_debt_expenses.et_display)
        }
    }

    private fun setupTextBoxes() {
        et_max_purchase_price_monthly_fixed_income.setup {
            viewModel.updateMaxPurchaseMonthlyIncome(it)
        }
        et_max_purchase_price_monthly_debt_expenses.setup {
            viewModel.updateMaxPurchaseMonthlyDebt(it)
        }
        et_max_purchase_price_down_payment.setup { viewModel.updateMaxPurchaseDownPayment(it) }

        et_income_required_property_purchase_price.setup {
            viewModel.updateIncomeRequiredPropPurchasePrice(it)
        }

        et_income_required_down_payment.setup { viewModel.updateIncomeRequiredDownPayment(it) }

        et_income_required_monthly_debt_expenses.setup {
            viewModel.updateIncomeRequiredMonthlyDebt(it)
        }
    }

    private fun PillNumberEditText.setup(onNumberChangeListener: (number: Int?) -> Unit) = run {
        setup(numberTextTransformer = { number, formattedNumberString ->
            number?.run { "\$$formattedNumberString" } ?: ""
        }, onNumberChangeListener = {
            onNumberChangeListener.invoke(it)
        })
        setNumber(0)
    }

    private fun setOnClickListeners() {
        card_max_purchase_price.layout_header.setOnClickListener {
            viewModel.setCalculatorOption(AffordabilityCalculatorOption.MAX_PURCHASE_PRICE)
        }
        card_income_required.layout_header.setOnClickListener {
            viewModel.setCalculatorOption(AffordabilityCalculatorOption.INCOME_REQUIRED)
        }
        card_max_purchase_price.btn_amend.setOnClickListener {
            et_max_purchase_price_monthly_fixed_income.requestFocusAndShowKeyboard()
        }
        card_income_required.btn_amend.setOnClickListener {
            et_income_required_property_purchase_price.requestFocusAndShowKeyboard()
        }
        layout_to_advanced.setOnClickListener {

            when (viewModel.affordabilityCalculatorOption.value) {
                AffordabilityCalculatorOption.MAX_PURCHASE_PRICE -> {
                    AdvancedAffordabilityCalculatorActivity.launch(
                        this,
                        monthlyFixedIncome = et_max_purchase_price_monthly_fixed_income.value,
                        monthlyDebtExpenses = et_max_purchase_price_monthly_debt_expenses.value
                    )
                }
                AffordabilityCalculatorOption.INCOME_REQUIRED -> {
                    AdvancedAffordabilityCalculatorActivity.launch(
                        this,
                        monthlyDebtExpenses = et_income_required_monthly_debt_expenses.value
                    )
                }
                else -> {
                    launchActivity(AdvancedAffordabilityCalculatorActivity::class.java)
                }
            }
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_calculator_affordability
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }

    override fun getViewModelClass(): Class<AffordabilityCalculatorViewModel> {
        return AffordabilityCalculatorViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }
}