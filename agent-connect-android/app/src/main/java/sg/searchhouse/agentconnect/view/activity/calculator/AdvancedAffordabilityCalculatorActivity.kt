package sg.searchhouse.agentconnect.view.activity.calculator

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import kotlinx.android.synthetic.main.activity_calculator_affordability_advanced.*
import kotlinx.android.synthetic.main.activity_calculator_affordability_advanced.layout_container
import kotlinx.android.synthetic.main.edit_text_new_pill_number.view.*
import kotlinx.android.synthetic.main.layout_affordability_advanced_borrower_details.*
import kotlinx.android.synthetic.main.layout_affordability_advanced_borrower_person.view.*
import kotlinx.android.synthetic.main.layout_header_calculator_affordability_advanced.*
import kotlinx.android.synthetic.main.selector_integer.view.*
import kotlinx.android.synthetic.main.tab_layout_advanced_property_type.*
import kotlinx.android.synthetic.main.tab_layout_stamp_duty_application_type.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.databinding.ActivityCalculatorAffordabilityAdvancedBinding
import sg.searchhouse.agentconnect.dsl.*
import sg.searchhouse.agentconnect.dsl.widget.requestFocusAndShowKeyboard
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import sg.searchhouse.agentconnect.enumeration.api.CalculatorEnum
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.viewmodel.activity.calculator.AdvancedAffordabilityCalculatorViewModel

class AdvancedAffordabilityCalculatorActivity :
    ViewModelActivity<AdvancedAffordabilityCalculatorViewModel, ActivityCalculatorAffordabilityAdvancedBinding>() {
    private val buyerProfiles = CalculatorEnum.BuyerProfile.values()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.selectedPropertyType.observePerformRequest()
        viewModel.purchasePrice.observePerformRequest()
        viewModel.selectedApplicationType.observePerformRequest()
        viewModel.borrowerAge.observePerformRequest()
        viewModel.monthlyFixedIncome.observePerformRequest()
        viewModel.monthlyVariableIncome.observePerformRequest()
        viewModel.monthlyDebtExpenses.observePerformRequest()
        viewModel.monthlyPropertyLoan.observePerformRequest()
        viewModel.otherCommitments.observePerformRequest()
        viewModel.ownedPropertyNumber.observePerformRequest()
        viewModel.propertyLoan.observePerformRequest()
        viewModel.cashOnHand.observePerformRequest()
        viewModel.cpfOaAmount.observePerformRequest()
        viewModel.selectedBorrowerProfile.observe(this) {
            if (it?.isCpfApplicable() != true && (viewModel.cpfOaAmount.value ?: 0) > 0) {
                layout_borrower_one.et_cpf_oa_amount.setNumber(0)
            } else {
                viewModel.maybePerformRequest()
            }
        }

        viewModel.borrowerAge2.observePerformRequest()
        viewModel.monthlyFixedIncome2.observePerformRequest()
        viewModel.monthlyVariableIncome2.observePerformRequest()
        viewModel.monthlyDebtExpenses2.observePerformRequest()
        viewModel.monthlyPropertyLoan2.observePerformRequest()
        viewModel.otherCommitments2.observePerformRequest()
        viewModel.ownedPropertyNumber2.observePerformRequest()
        viewModel.propertyLoan2.observePerformRequest()
        viewModel.cashOnHand2.observePerformRequest()
        viewModel.cpfOaAmount2.observePerformRequest()
        viewModel.selectedBorrowerProfile2.observe(this) {
            if (it?.isCpfApplicable() != true && (viewModel.cpfOaAmount2.value ?: 0) > 0) {
                layout_borrower_two.et_cpf_oa_amount.setNumber(0)
            } else {
                viewModel.maybePerformRequest()
            }
        }

        viewModel.shareStatus.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    viewDetails()
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    // Do nothing
                }
            }
        }
    }

    private fun LiveData<*>.observePerformRequest() =
        observe(this@AdvancedAffordabilityCalculatorActivity) { viewModel.maybePerformRequest() }

    private fun setupView() {
        layout_container.setupLayoutAnimation()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        btn_tab_hdb.setOnClickListener {
            viewModel.selectedPropertyType.postValue(CalculatorEnum.AdvancedPropertyType.HDB)
        }
        btn_tab_private.setOnClickListener {
            viewModel.selectedPropertyType.postValue(CalculatorEnum.AdvancedPropertyType.PRIVATE)
        }
        btn_tab_single.setOnClickListener {
            viewModel.selectedApplicationType.postValue(CalculatorEnum.ApplicationType.SINGLE)
        }
        btn_tab_joint_applicant.setOnClickListener {
            viewModel.selectedApplicationType.postValue(CalculatorEnum.ApplicationType.JOINT_APPLICANT)
        }

        setBuyerOneOnClickListeners()
        setBuyerTwoOnClickListeners()

        btn_amend.setOnClickListener {
            layout_borrower_one.et_age.et_display.requestFocusAndShowKeyboard()
        }
        btn_calculate.setOnClickListener { viewModel.maybePerformShareRequest() }
        btn_view_more_details.setOnClickListener { viewModel.maybePerformShareRequest() }
    }

    private fun viewDetails() {
        val result = viewModel.shareStatus.value?.body?.result
        result?.run {
            AdvancedAffordabilityDetailsActivity.launch(
                this@AdvancedAffordabilityCalculatorActivity,
                this
            )
        }
    }

    private fun setBuyerOneOnClickListeners() {
        layout_borrower_one.et_age.setup(
            AppConstant.MAX_HUMAN_AGE,
            numberTextTransformer = { number, formattedNumberString ->
                number?.run {
                    resources.getQuantityString(R.plurals.human_age, this, formattedNumberString)
                } ?: ""
            },
            onNumberChangeListener = {
                viewModel.borrowerAge.postValue(it)
                viewModel.isShowAgeError.postValue(!(it != null && it >= AppConstant.MIN_AGE_BUY_HOUSE))
            })
        layout_borrower_one.et_age.setNumber(AppConstant.MIN_AGE_BUY_HOUSE)
        layout_borrower_one.btn_buyer_profile.setOnClickListener {
            dialogUtil.showListDialog(buyerProfiles.map { it.label }, { _, position ->
                viewModel.selectedBorrowerProfile.postValue(buyerProfiles[position])
            })
        }
        layout_borrower_one.layout_stamp_duty_property_number.btn_minus.setOnClickListener {
            viewModel.updateOwnedPropertyNumber(isPlus = false)
        }
        layout_borrower_one.layout_stamp_duty_property_number.btn_plus.setOnClickListener {
            viewModel.updateOwnedPropertyNumber(isPlus = true)
        }
        layout_borrower_one.layout_stamp_duty_property_loan_number.btn_minus.setOnClickListener {
            viewModel.updatePropertyLoanNumber(isPlus = false)
        }
        layout_borrower_one.layout_stamp_duty_property_loan_number.btn_plus.setOnClickListener {
            viewModel.updatePropertyLoanNumber(isPlus = true)
        }
        layout_borrower_one.et_monthly_fixed_income.setup(numberTextTransformer = { _, formattedNumberString ->
            getPriceLabel(formattedNumberString)
        }, onNumberChangeListener = {
            viewModel.monthlyFixedIncome.postValue(it)
        })
        val defaultFixedIncome = intent.getIntExtra(EXTRA_MONTHLY_FIXED_INCOME, 0)
        layout_borrower_one.et_monthly_fixed_income.setNumber(defaultFixedIncome)
        layout_borrower_one.et_monthly_variable_income.setup(numberTextTransformer = { _, formattedNumberString ->
            getPriceLabel(formattedNumberString)
        }, onNumberChangeListener = {
            viewModel.monthlyVariableIncome.postValue(it)
        })
        layout_borrower_one.et_monthly_variable_income.setNumber(0)
        layout_borrower_one.et_monthly_debt_expenses.setup(numberTextTransformer = { _, formattedNumberString ->
            getPriceLabel(formattedNumberString)
        }, onNumberChangeListener = {
            viewModel.monthlyDebtExpenses.postValue(it)
        })
        val defaultMonthlyDebtExpenses = intent.getIntExtra(EXTRA_MONTHLY_DEBT_EXPENSES, 0)
        layout_borrower_one.et_monthly_debt_expenses.setNumber(defaultMonthlyDebtExpenses)
        layout_borrower_one.et_monthly_property_loan.setup(numberTextTransformer = { _, formattedNumberString ->
            getPriceLabel(formattedNumberString)
        }, onNumberChangeListener = {
            viewModel.monthlyPropertyLoan.postValue(it)
        })
        layout_borrower_one.et_monthly_property_loan.setNumber(0)
        layout_borrower_one.et_other_commitments.setup(numberTextTransformer = { _, formattedNumberString ->
            getPriceLabel(formattedNumberString)
        }, onNumberChangeListener = {
            viewModel.otherCommitments.postValue(it)
        })
        layout_borrower_one.et_other_commitments.setNumber(0)
        layout_borrower_one.et_cash_on_hand.setup(numberTextTransformer = { _, formattedNumberString ->
            getPriceLabel(formattedNumberString)
        }, onNumberChangeListener = {
            viewModel.cashOnHand.postValue(it)
        })
        layout_borrower_one.et_cash_on_hand.setNumber(0)
        layout_borrower_one.et_cpf_oa_amount.setup(numberTextTransformer = { _, formattedNumberString ->
            getPriceLabel(formattedNumberString)
        }, onNumberChangeListener = {
            viewModel.cpfOaAmount.postValue(it)
        })
        layout_borrower_one.et_cpf_oa_amount.setNumber(0)
    }

    private fun getPriceLabel(formattedNumberString: String?): String {
        return NumberUtil.getTextBoxCurrency(applicationContext, formattedNumberString)
    }

    private fun setBuyerTwoOnClickListeners() {
        layout_borrower_two.et_age.setup(
            AppConstant.MAX_HUMAN_AGE,
            numberTextTransformer = { number, formattedNumberString ->
                number?.run {
                    resources.getQuantityString(R.plurals.human_age, this, formattedNumberString)
                } ?: ""
            },
            onNumberChangeListener = {
                viewModel.borrowerAge2.postValue(it)
                viewModel.isShowAgeError2.postValue(!(it != null && it >= AppConstant.MIN_AGE_BUY_HOUSE))
            })
        layout_borrower_two.et_age.setNumber(AppConstant.MIN_AGE_BUY_HOUSE)
        layout_borrower_two.btn_buyer_profile.setOnClickListener {
            dialogUtil.showListDialog(buyerProfiles.map { it.label }, { _, position ->
                viewModel.selectedBorrowerProfile2.postValue(buyerProfiles[position])
            })
        }
        layout_borrower_two.layout_stamp_duty_property_number.btn_minus.setOnClickListener {
            viewModel.updateOwnedPropertyNumber2(isPlus = false)
        }
        layout_borrower_two.layout_stamp_duty_property_number.btn_plus.setOnClickListener {
            viewModel.updateOwnedPropertyNumber2(isPlus = true)
        }
        layout_borrower_two.layout_stamp_duty_property_loan_number.btn_minus.setOnClickListener {
            viewModel.updatePropertyLoanNumber2(isPlus = false)
        }
        layout_borrower_two.layout_stamp_duty_property_loan_number.btn_plus.setOnClickListener {
            viewModel.updatePropertyLoanNumber2(isPlus = true)
        }
        layout_borrower_two.et_monthly_fixed_income.setup(numberTextTransformer = { _, formattedNumberString ->
            getPriceLabel(formattedNumberString)
        }, onNumberChangeListener = {
            viewModel.monthlyFixedIncome2.postValue(it)
        })
        layout_borrower_two.et_monthly_fixed_income.setNumber(0)
        layout_borrower_two.et_monthly_variable_income.setup(numberTextTransformer = { _, formattedNumberString ->
            getPriceLabel(formattedNumberString)
        }, onNumberChangeListener = {
            viewModel.monthlyVariableIncome2.postValue(it)
        })
        layout_borrower_two.et_monthly_variable_income.setNumber(0)
        layout_borrower_two.et_monthly_debt_expenses.setup(numberTextTransformer = { _, formattedNumberString ->
            getPriceLabel(formattedNumberString)
        }, onNumberChangeListener = {
            viewModel.monthlyDebtExpenses2.postValue(it)
        })
        layout_borrower_two.et_monthly_debt_expenses.setNumber(0)
        layout_borrower_two.et_monthly_property_loan.setup(numberTextTransformer = { _, formattedNumberString ->
            getPriceLabel(formattedNumberString)
        }, onNumberChangeListener = {
            viewModel.monthlyPropertyLoan2.postValue(it)
        })
        layout_borrower_two.et_monthly_property_loan.setNumber(0)
        layout_borrower_two.et_other_commitments.setup(numberTextTransformer = { _, formattedNumberString ->
            getPriceLabel(formattedNumberString)
        }, onNumberChangeListener = {
            viewModel.otherCommitments2.postValue(it)
        })
        layout_borrower_two.et_other_commitments.setNumber(0)
        layout_borrower_two.et_cash_on_hand.setup(numberTextTransformer = { _, formattedNumberString ->
            getPriceLabel(formattedNumberString)
        }, onNumberChangeListener = {
            viewModel.cashOnHand2.postValue(it)
        })
        layout_borrower_two.et_cash_on_hand.setNumber(0)
        layout_borrower_two.et_cpf_oa_amount.setup(numberTextTransformer = { _, formattedNumberString ->
            getPriceLabel(formattedNumberString)
        }, onNumberChangeListener = {
            viewModel.cpfOaAmount2.postValue(it)
        })
        layout_borrower_two.et_cpf_oa_amount.setNumber(0)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_calculator_affordability_advanced
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }

    override fun getViewModelClass(): Class<AdvancedAffordabilityCalculatorViewModel> {
        return AdvancedAffordabilityCalculatorViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    companion object {
        private const val EXTRA_MONTHLY_FIXED_INCOME = "EXTRA_MONTHLY_FIXED_INCOME"
        private const val EXTRA_MONTHLY_DEBT_EXPENSES = "EXTRA_MONTHLY_DEBT_EXPENSES"

        fun launch(
            activity: Activity,
            monthlyFixedIncome: Int? = null,
            monthlyDebtExpenses: Int? = null
        ) {
            val extras = Bundle()
            monthlyFixedIncome?.run { extras.putInt(EXTRA_MONTHLY_FIXED_INCOME, this) }
            monthlyDebtExpenses?.run { extras.putInt(EXTRA_MONTHLY_DEBT_EXPENSES, this) }
            activity.launchActivity(AdvancedAffordabilityCalculatorActivity::class.java, extras)
        }
    }
}