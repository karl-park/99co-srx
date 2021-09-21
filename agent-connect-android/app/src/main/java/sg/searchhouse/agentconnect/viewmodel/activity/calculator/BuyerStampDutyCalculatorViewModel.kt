package sg.searchhouse.agentconnect.viewmodel.activity.calculator

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.CalculatorRepository
import sg.searchhouse.agentconnect.enumeration.api.CalculatorEnum
import sg.searchhouse.agentconnect.model.api.calculator.CalculateStampDutyBuyResponse
import sg.searchhouse.agentconnect.model.api.calculator.StampDutyInputPO
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class BuyerStampDutyCalculatorViewModel(application: Application) :
    ApiBaseViewModel<CalculateStampDutyBuyResponse>(application) {
    val selectedPropertyType = MutableLiveData<CalculatorEnum.PropertyType>().apply {
        value = CalculatorEnum.PropertyType.RESIDENTIAL
    }

    val selectedApplicationType = MutableLiveData<CalculatorEnum.ApplicationType>().apply {
        value = CalculatorEnum.ApplicationType.SINGLE
    }

    val selectedBuyerProfile = MutableLiveData<CalculatorEnum.BuyerProfile>().apply {
        value = CalculatorEnum.BuyerProfile.SINGAPOREAN
    }

    val ownedPropertyNumber = MutableLiveData<Int>().apply { value = 0 }

    val selectedBuyerProfile2 = MutableLiveData<CalculatorEnum.BuyerProfile>().apply {
        value = CalculatorEnum.BuyerProfile.SINGAPOREAN
    }

    val ownedPropertyNumber2 = MutableLiveData<Int>().apply { value = 0 }

    val purchasePrice = MutableLiveData<Int>()

    val totalStampDuty: LiveData<String> = Transformations.map(mainResponse) {
        getCurrencyLabel(it?.result?.totalStampDuty)
    }

    val buyerStampDuty: LiveData<String> = Transformations.map(mainResponse) {
        getCurrencyLabel(it?.result?.buyerStampDuty)
    }

    val additionalBuyerStampDuty: LiveData<String> = Transformations.map(mainResponse) {
        getCurrencyLabel(it?.result?.addBuyerStampDuty)
    }

    val additionalStampDutyDescription: LiveData<String> = Transformations.map(mainResponse) {
        it?.result?.addBuyerStampDutyPct?.run {
            applicationContext.getString(
                R.string.label_additional_buyer_stamp_duty,
                "${toString()}%"
            )
        } ?: run {
            applicationContext.getString(R.string.label_additional_buyer_stamp_duty_empty)
        }
    }

    val shouldEnableShareButton = MediatorLiveData<Boolean>()

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
        setupShouldEnableShareButton()
    }

    private fun setupShouldEnableShareButton() {
        shouldEnableShareButton.addSource(mainResponse) {
            updateShouldEnableShareButton()
        }
        shouldEnableShareButton.addSource(purchasePrice) {
            updateShouldEnableShareButton()
        }
    }

    private fun updateShouldEnableShareButton() {
        val purchasePrice = purchasePrice.value ?: 0
        shouldEnableShareButton.postValue(mainResponse.value != null && purchasePrice > 0)
    }

    fun updateOwnedPropertyNumber(isPlus: Boolean) {
        ownedPropertyNumber.updateOwnedPropertyNumber(isPlus)
    }

    fun updateOwnedPropertyNumber2(isPlus: Boolean) {
        ownedPropertyNumber2.updateOwnedPropertyNumber(isPlus)
    }

    private fun MutableLiveData<Int>.updateOwnedPropertyNumber(isPlus: Boolean) = run {
        val currentNumber = value ?: 0
        if (isPlus) {
            postValue(currentNumber + 1)
        } else {
            if (currentNumber > 0) {
                postValue(currentNumber - 1)
            }
        }
    }

    fun performRequest() {
        getStampDutyInputPO()?.run {
            performRequest(calculatorRepository.calculateStampDutyBuy(this))
        } ?: run {
            mainResponse.postValue(null)
        }
    }

    private fun getStampDutyInputPO(): StampDutyInputPO? {
        val pptyPrice = purchasePrice.value?.toLong() ?: return null
        val pptyType = selectedPropertyType.value?.value ?: return null
        val appType = selectedApplicationType.value?.value ?: return null

        val buyerProfile = selectedBuyerProfile.value?.value ?: return null
        val noOfProperty = ownedPropertyNumber.value ?: return null

        val applicationDetail = StampDutyInputPO.ApplicationDetail(buyerProfile, noOfProperty)

        val applicantDetails =
            if (selectedApplicationType.value == CalculatorEnum.ApplicationType.JOINT_APPLICANT) {
                val buyerProfile2 = selectedBuyerProfile2.value?.value ?: return null
                val noOfProperty2 = ownedPropertyNumber2.value ?: return null
                val applicationDetail2 =
                    StampDutyInputPO.ApplicationDetail(buyerProfile2, noOfProperty2)
                listOf(applicationDetail, applicationDetail2)
            } else {
                listOf(applicationDetail)
            }

        return StampDutyInputPO(pptyPrice, pptyType, appType, applicantDetails)
    }

    override fun shouldResponseBeOccupied(response: CalculateStampDutyBuyResponse): Boolean {
        return true
    }
}