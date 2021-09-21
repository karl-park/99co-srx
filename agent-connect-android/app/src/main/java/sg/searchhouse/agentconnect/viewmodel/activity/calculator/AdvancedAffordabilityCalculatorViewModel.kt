package sg.searchhouse.agentconnect.viewmodel.activity.calculator

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.data.repository.CalculatorRepository
import sg.searchhouse.agentconnect.dsl.performRequest
import sg.searchhouse.agentconnect.enumeration.api.CalculatorEnum
import sg.searchhouse.agentconnect.model.api.calculator.AffordAdvancedInputPO
import sg.searchhouse.agentconnect.model.api.calculator.CalculateAffordAdvancedResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class AdvancedAffordabilityCalculatorViewModel(application: Application) :
    ApiBaseViewModel<CalculateAffordAdvancedResponse>(application) {
    val selectedPropertyType = MutableLiveData<CalculatorEnum.AdvancedPropertyType>().apply {
        value = CalculatorEnum.AdvancedPropertyType.HDB
    }

    val selectedApplicationType = MutableLiveData<CalculatorEnum.ApplicationType>().apply {
        value = CalculatorEnum.ApplicationType.SINGLE
    }

    val selectedBorrowerProfile = MutableLiveData<CalculatorEnum.BuyerProfile>().apply {
        value = CalculatorEnum.BuyerProfile.SINGAPOREAN
    }
    val ownedPropertyNumber = MutableLiveData<CalculatorEnum.PropertyNumber>().apply {
        value = CalculatorEnum.PropertyNumber.ZERO
    }
    val propertyLoan = MutableLiveData<CalculatorEnum.PropertyNumber>().apply {
        value = CalculatorEnum.PropertyNumber.ZERO
    }
    val borrowerAge = MutableLiveData<Int>()
    val monthlyFixedIncome = MutableLiveData<Int>()
    val monthlyVariableIncome = MutableLiveData<Int>()
    val monthlyDebtExpenses = MutableLiveData<Int>()
    val monthlyPropertyLoan = MutableLiveData<Int>()
    val otherCommitments = MutableLiveData<Int>()
    val cashOnHand = MutableLiveData<Int>()
    val cpfOaAmount = MutableLiveData<Int>()
    val isShowAgeError = MutableLiveData<Boolean>().apply { value = false }

    val selectedBorrowerProfile2 = MutableLiveData<CalculatorEnum.BuyerProfile>().apply {
        value = CalculatorEnum.BuyerProfile.SINGAPOREAN
    }
    val ownedPropertyNumber2 = MutableLiveData<CalculatorEnum.PropertyNumber>().apply {
        value = CalculatorEnum.PropertyNumber.ZERO
    }
    val propertyLoan2 = MutableLiveData<CalculatorEnum.PropertyNumber>().apply {
        value = CalculatorEnum.PropertyNumber.ZERO
    }
    val borrowerAge2 = MutableLiveData<Int>()
    val monthlyFixedIncome2 = MutableLiveData<Int>()
    val monthlyVariableIncome2 = MutableLiveData<Int>()
    val monthlyDebtExpenses2 = MutableLiveData<Int>()
    val monthlyPropertyLoan2 = MutableLiveData<Int>()
    val otherCommitments2 = MutableLiveData<Int>()
    val cashOnHand2 = MutableLiveData<Int>()
    val cpfOaAmount2 = MutableLiveData<Int>()
    val isShowAgeError2 = MutableLiveData<Boolean>().apply { value = false }

    val purchasePrice = MutableLiveData<Int>()

    val maxPurchasePrice: LiveData<String> = Transformations.map(mainResponse) {
        getCurrencyLabel(it?.result?.maxPurchasePrice)
    }

    val maxLoanAmount: LiveData<String> = Transformations.map(mainResponse) {
        getCurrencyLabel(it?.result?.maxLoan)
    }

    val isEnableSubmitButton = MediatorLiveData<Boolean>()

    val shareStatus = MutableLiveData<ApiStatus<CalculateAffordAdvancedResponse>>()

    private fun getCurrencyLabel(number: Long?): String {
        val toInteger = number?.toInt()
        return toInteger?.run { NumberUtil.getFormattedCurrency(this) } ?: run { "-" }
    }

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var calculatorRepository: CalculatorRepository

    init {
        viewModelComponent.inject(this)
        setupEnableSubmitButton()
    }

    private fun setupEnableSubmitButton() {
        isEnableSubmitButton.addSource(mainResponse) {
            isEnableSubmitButton.postValue(checkIsEnableSubmitButton())
        }
        isEnableSubmitButton.addSource(shareStatus) {
            isEnableSubmitButton.postValue(checkIsEnableSubmitButton())
        }
    }

    private fun checkIsEnableSubmitButton(): Boolean {
        val maxPurchasePrice = mainResponse.value?.result?.maxPurchasePrice ?: 0
        val maxLoan = mainResponse.value?.result?.maxLoan ?: 0
        val isMainResponseValid = maxPurchasePrice > 0 && maxLoan > 0
        val isShareRequestInProgress = shareStatus.value?.key == ApiStatus.StatusKey.LOADING
        return isMainResponseValid && !isShareRequestInProgress
    }

    fun updateOwnedPropertyNumber(isPlus: Boolean) {
        ownedPropertyNumber.updateNumber(isPlus)
    }

    fun updateOwnedPropertyNumber2(isPlus: Boolean) {
        ownedPropertyNumber2.updateNumber(isPlus)
    }

    fun updatePropertyLoanNumber(isPlus: Boolean) {
        propertyLoan.updateNumber(isPlus)
    }

    fun updatePropertyLoanNumber2(isPlus: Boolean) {
        propertyLoan2.updateNumber(isPlus)
    }

    private fun MutableLiveData<CalculatorEnum.PropertyNumber>.updateNumber(isPlus: Boolean) =
        run {
            val currentNumber = value ?: CalculatorEnum.PropertyNumber.ZERO
            if (isPlus) {
                when (currentNumber) {
                    CalculatorEnum.PropertyNumber.ZERO -> {
                        postValue(CalculatorEnum.PropertyNumber.ONE)
                    }
                    CalculatorEnum.PropertyNumber.ONE -> {
                        postValue(CalculatorEnum.PropertyNumber.TWO_PLUS)
                    }
                    else -> {
                    }
                }
            } else {
                when (currentNumber) {
                    CalculatorEnum.PropertyNumber.TWO_PLUS -> {
                        postValue(CalculatorEnum.PropertyNumber.ONE)
                    }
                    CalculatorEnum.PropertyNumber.ONE -> {
                        postValue(CalculatorEnum.PropertyNumber.ZERO)
                    }
                    else -> {
                    }
                }
            }
        }

    fun maybePerformRequest() {
        getAffordAdvancedInputPO()?.run {
            performRequest(calculatorRepository.calculateAffordAdvanced(this))
        } ?: run {
            mainResponse.postValue(null)
        }
    }

    // Request with url generated
    fun maybePerformShareRequest() {
        shareStatus.postValue(ApiStatus.loadingInstance())
        getAffordAdvancedInputPO(share = true)?.run {
            calculatorRepository.calculateAffordAdvanced(this)
                .performRequest(applicationContext, onSuccess = {
                    shareStatus.postValue(ApiStatus.successInstance(it))
                }, onFail = {
                    shareStatus.postValue(ApiStatus.failInstance(it))
                }, onError = {
                    shareStatus.postValue(ApiStatus.errorInstance())
                })
        } ?: run {
            shareStatus.postValue(ApiStatus.errorInstance())
        }
    }

    private fun getAffordAdvancedInputPO(share: Boolean = false): AffordAdvancedInputPO? {
        val pptyType = selectedPropertyType.value?.value ?: return null
        val appType = selectedApplicationType.value?.value ?: return null

        val applicationDetails = getFirstApplicationDetails() ?: return null
        val applicantDetails =
            if (selectedApplicationType.value == CalculatorEnum.ApplicationType.JOINT_APPLICANT) {
                val applicationDetails2 = getFirstApplicationDetails2() ?: return null
                listOf(applicationDetails, applicationDetails2)
            } else {
                listOf(applicationDetails)
            }
        return AffordAdvancedInputPO(pptyType, appType, share, applicantDetails)
    }

    private fun getCpfOaAmount(buyerProfile: CalculatorEnum.BuyerProfile, textBoxValue: Int?): Int {
        return when (buyerProfile) {
            CalculatorEnum.BuyerProfile.SINGAPOREAN, CalculatorEnum.BuyerProfile.SPR -> {
                textBoxValue ?: 0
            }
            else -> {
                0
            }
        }
    }

    private fun getFirstApplicationDetails(): AffordAdvancedInputPO.ApplicationDetail? {
        val buyerProfile = selectedBorrowerProfile.value?.value ?: return null
        val noOfProperty = (ownedPropertyNumber.value ?: CalculatorEnum.PropertyNumber.ZERO).value
        val age = borrowerAge.value ?: return null
        val monthlyFixedIncome = monthlyFixedIncome.value ?: 0
        val monthlyVariableIncome = monthlyVariableIncome.value ?: 0
        val monthlyDebtExpenses = monthlyDebtExpenses.value ?: 0
        val monthlyPropertyLoan = monthlyPropertyLoan.value ?: 0
        val propertyLoan = (propertyLoan.value ?: CalculatorEnum.PropertyNumber.ZERO).value
        val commitments = otherCommitments.value ?: 0
        val cashOnHand = cashOnHand.value ?: 0
        val cpfOaAmount =
            getCpfOaAmount(selectedBorrowerProfile.value!!, cpfOaAmount.value) // Guaranteed
        return AffordAdvancedInputPO.ApplicationDetail(
            age = age,
            buyerProfile = buyerProfile,
            monthlyFixedIncome = monthlyFixedIncome.toLong(),
            monthlyVariableIncome = monthlyVariableIncome.toLong(),
            monthlyDebt = monthlyDebtExpenses.toLong(),
            monthlyPropertyLoan = monthlyPropertyLoan.toLong(),
            monthlyAddCommitment = commitments.toLong(),
            noOfProperty = noOfProperty,
            noOfPropertyLoan = propertyLoan,
            cash = cashOnHand.toLong(),
            cpfOA = cpfOaAmount.toLong()
        )
    }

    private fun getFirstApplicationDetails2(): AffordAdvancedInputPO.ApplicationDetail? {
        val buyerProfile2 = selectedBorrowerProfile2.value?.value ?: return null
        val noOfProperty2 = (ownedPropertyNumber2.value ?: CalculatorEnum.PropertyNumber.ZERO).value
        val age2 = borrowerAge2.value ?: return null
        val monthlyFixedIncome2 = monthlyFixedIncome2.value ?: 0
        val monthlyVariableIncome2 = monthlyVariableIncome2.value ?: 0
        val monthlyDebtExpenses2 = monthlyDebtExpenses2.value ?: 0
        val monthlyPropertyLoan2 = monthlyPropertyLoan2.value ?: 0
        val propertyLoan2 = (propertyLoan2.value ?: CalculatorEnum.PropertyNumber.ZERO).value
        val commitments2 = otherCommitments2.value ?: 0
        val cashOnHand2 = cashOnHand2.value ?: 0
        val cpfOaAmount2 =
            getCpfOaAmount(selectedBorrowerProfile2.value!!, cpfOaAmount2.value) // Guaranteed
        return AffordAdvancedInputPO.ApplicationDetail(
            age = age2,
            buyerProfile = buyerProfile2,
            monthlyFixedIncome = monthlyFixedIncome2.toLong(),
            monthlyVariableIncome = monthlyVariableIncome2.toLong(),
            monthlyDebt = monthlyDebtExpenses2.toLong(),
            monthlyPropertyLoan = monthlyPropertyLoan2.toLong(),
            monthlyAddCommitment = commitments2.toLong(),
            noOfProperty = noOfProperty2,
            noOfPropertyLoan = propertyLoan2,
            cash = cashOnHand2.toLong(),
            cpfOA = cpfOaAmount2.toLong()
        )
    }

    override fun shouldResponseBeOccupied(response: CalculateAffordAdvancedResponse): Boolean {
        return true
    }
}