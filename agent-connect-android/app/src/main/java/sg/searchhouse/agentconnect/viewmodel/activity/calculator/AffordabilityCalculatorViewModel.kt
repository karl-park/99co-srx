package sg.searchhouse.agentconnect.viewmodel.activity.calculator

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.CalculatorRepository
import sg.searchhouse.agentconnect.dsl.performRequest
import sg.searchhouse.agentconnect.enumeration.app.AffordabilityCalculatorOption
import sg.searchhouse.agentconnect.model.api.calculator.AffordQuickIncomeRequiredInput
import sg.searchhouse.agentconnect.model.api.calculator.CalculateIncomeRequiredResponse
import sg.searchhouse.agentconnect.model.api.calculator.CalculateMaxPurchasePriceResponse
import sg.searchhouse.agentconnect.model.api.calculator.MaxPurchasePriceInput
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class AffordabilityCalculatorViewModel(application: Application) : CoreViewModel(application) {
    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var calculatorRepository: CalculatorRepository

    val maxPurchasePriceInput =
        MutableLiveData<MaxPurchasePriceInput>().apply { value = MaxPurchasePriceInput() }

    private val calculateMaxPurchasePriceResponse =
        MutableLiveData<CalculateMaxPurchasePriceResponse>()

    val maxPurchasePrice: LiveData<String> =
        Transformations.map(calculateMaxPurchasePriceResponse) {
            val price = getNonNegativeIntResult(it?.result?.maxPurchasePrice?.toInt())
            "\$${NumberUtil.formatThousand(price)}"
        }

    val maxPurchasePriceDescription: LiveData<String> =
        Transformations.map(maxPurchasePriceInput) {
            val monthlyIncome = it?.monthlyIncome ?: 0
            applicationContext.getString(
                R.string.description_calculate_max_purchase_price,
                NumberUtil.formatThousand(monthlyIncome)
            )
        }

    val affordQuickIncomeRequiredInput =
        MutableLiveData<AffordQuickIncomeRequiredInput>().apply {
            value = AffordQuickIncomeRequiredInput()
        }

    private val calculateIncomeRequiredResponse = MutableLiveData<CalculateIncomeRequiredResponse>()

    val incomeRequired: LiveData<String> = Transformations.map(calculateIncomeRequiredResponse) {
        val price = getNonNegativeIntResult(it?.result?.incomeRequired?.toInt())
        "\$${NumberUtil.formatThousand(price)}"
    }

    val incomeRequiredDescription: LiveData<String> =
        Transformations.map(affordQuickIncomeRequiredInput) {
            val propPurchasePrice = it?.propPurchasePrice ?: 0
            applicationContext.getString(
                R.string.description_calculate_income_required,
                NumberUtil.formatThousand(propPurchasePrice)
            )
        }

    private fun getNonNegativeIntResult(input: Int?): Int {
        val number = input ?: 0
        return when {
            number < 0 -> 0
            else -> number
        }
    }

    init {
        viewModelComponent.inject(this)
    }

    val affordabilityCalculatorOption = MutableLiveData<AffordabilityCalculatorOption>().apply {
        value = AffordabilityCalculatorOption.MAX_PURCHASE_PRICE
    }

    fun setCalculatorOption(calculatorOption: AffordabilityCalculatorOption) {
        if (affordabilityCalculatorOption.value != calculatorOption) {
            affordabilityCalculatorOption.postValue(calculatorOption)
        }
    }

    fun updateMaxPurchaseMonthlyIncome(monthlyIncome: Int?) {
        val input = maxPurchasePriceInput.value!! // Guaranteed not null
        input.monthlyIncome = monthlyIncome
        maxPurchasePriceInput.postValue(input)
    }

    fun updateMaxPurchaseMonthlyDebt(monthlyDebt: Int?) {
        val input = maxPurchasePriceInput.value!! // Guaranteed not null
        input.monthlyDebt = monthlyDebt
        maxPurchasePriceInput.postValue(input)
    }

    fun updateMaxPurchaseDownPayment(downpayment: Int?) {
        val input = maxPurchasePriceInput.value!! // Guaranteed not null
        input.downpayment = downpayment
        maxPurchasePriceInput.postValue(input)
    }

    fun updateIncomeRequiredPropPurchasePrice(propPurchasePrice: Int?) {
        val input = affordQuickIncomeRequiredInput.value!! // Guaranteed not null
        input.propPurchasePrice = propPurchasePrice
        affordQuickIncomeRequiredInput.postValue(input)
    }

    fun updateIncomeRequiredMonthlyDebt(monthlyDebt: Int?) {
        val input = affordQuickIncomeRequiredInput.value!! // Guaranteed not null
        input.monthlyDebt = monthlyDebt
        affordQuickIncomeRequiredInput.postValue(input)
    }

    fun updateIncomeRequiredDownPayment(downpayment: Int?) {
        val input = affordQuickIncomeRequiredInput.value!! // Guaranteed not null
        input.downpayment = downpayment
        affordQuickIncomeRequiredInput.postValue(input)
    }

    fun calculateMaxPurchasePrice(input: MaxPurchasePriceInput) {
        if (input.isValid()) {
            calculatorRepository.calculateAffordQuickMaxPurchasePrice(
                input.monthlyIncome!!,
                input.monthlyDebt!!,
                input.downpayment!!
            ).performRequest(applicationContext, onSuccess = {
                calculateMaxPurchasePriceResponse.postValue(it)
            }, onFail = {
                calculateMaxPurchasePriceResponse.postValue(null)
            }, onError = {
                calculateMaxPurchasePriceResponse.postValue(null)
            })
        } else {
            calculateMaxPurchasePriceResponse.postValue(null)
        }
    }

    fun calculateIncomeRequired(input: AffordQuickIncomeRequiredInput) {
        if (input.isValid()) {
            calculatorRepository.calculateAffordQuickIncomeRequired(
                input.propPurchasePrice!!,
                input.monthlyDebt!!,
                input.downpayment!!
            ).performRequest(applicationContext, onSuccess = {
                calculateIncomeRequiredResponse.postValue(it)
            }, onFail = {
                calculateIncomeRequiredResponse.postValue(null)
            }, onError = {
                calculateIncomeRequiredResponse.postValue(null)
            })
        } else {
            calculateIncomeRequiredResponse.postValue(null)
        }
    }
}