package sg.searchhouse.agentconnect.viewmodel.activity.calculator

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.data.repository.CalculatorRepository
import sg.searchhouse.agentconnect.model.api.calculator.CalculateStampDutyRentResponse
import sg.searchhouse.agentconnect.model.api.calculator.RentDutyInputPO
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import java.util.*
import javax.inject.Inject

class RentalStampDutyCalculatorViewModel(application: Application) :
    ApiBaseViewModel<CalculateStampDutyRentResponse>(application) {

    val monthlyRent = MutableLiveData<Int>()
    val otherMonthlyCharges = MutableLiveData<Int>()

    val totalStampDuty: LiveData<String> = Transformations.map(mainResponse) {
        getCurrencyLabel(it?.result?.totalStampDuty)
    }

    val startDate = MutableLiveData<Date>()
    val endDate = MutableLiveData<Date>()

    val startDateLabel: LiveData<String> = Transformations.map(startDate) { date ->
        getDateLabel(date)
    }

    val endDateLabel: LiveData<String> = Transformations.map(endDate) { date ->
        getDateLabel(date)
    }

    val isShowDateError = MediatorLiveData<Boolean>()

    val shouldEnableShareButton = MediatorLiveData<Boolean>()

    private fun getDateLabel(date: Date?): String {
        return date?.run { DateTimeUtil.convertDateToString(this, DateTimeUtil.FORMAT_DATE_4) }
            ?: ""
    }

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
        setupIsShowDateError()
        setupShouldEnableShareButton()
    }

    private fun setupShouldEnableShareButton() {
        shouldEnableShareButton.addSource(mainResponse) {
            updateShouldEnableShareButton()
        }
        shouldEnableShareButton.addSource(monthlyRent) {
            updateShouldEnableShareButton()
        }
    }

    private fun updateShouldEnableShareButton() {
        val monthlyRent = monthlyRent.value ?: 0
        shouldEnableShareButton.postValue(mainResponse.value != null && monthlyRent > 0)
    }

    private fun setupIsShowDateError() {
        isShowDateError.addSource(startDate) {
            isShowDateError.postValue(shouldShowDateError())
        }
        isShowDateError.addSource(endDate) {
            isShowDateError.postValue(shouldShowDateError())
        }
    }

    private fun shouldShowDateError(): Boolean {
        if (startDate.value == null) return false
        if (endDate.value == null) return false
        return !areDatesValid()
    }

    private fun areDatesValid(): Boolean {
        val startDate = startDate.value ?: return false
        val endDate = endDate.value ?: return false
        val daysBetweenDates = DateTimeUtil.getDaysBetweenDates(startDate, endDate)
        return daysBetweenDates > 0
    }

    private fun getInputDateString(date: Date): String {
        return DateTimeUtil.convertDateToString(date, DateTimeUtil.FORMAT_DATE_12)
    }

    fun performRequest() {
        getInputPO()?.run {
            performRequest(calculatorRepository.calculateStampDutyRent(this))
        } ?: run {
            mainResponse.postValue(null)
        }
    }

    private fun getInputPO(): RentDutyInputPO? {
        val monthlyRent = monthlyRent.value?.toLong() ?: return null
        val otherMonthlyCharges = otherMonthlyCharges.value?.toLong() ?: return null
        if (!areDatesValid()) return null
        val startDate =
            startDate.value?.run { getInputDateString(this) } ?: return null
        val endDate =
            endDate.value?.run { getInputDateString(this) } ?: return null
        return RentDutyInputPO(monthlyRent, otherMonthlyCharges, startDate, endDate)
    }

    override fun shouldResponseBeOccupied(response: CalculateStampDutyRentResponse): Boolean {
        return true
    }
}