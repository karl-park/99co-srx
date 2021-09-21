package sg.searchhouse.agentconnect.viewmodel.fragment.calculator

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.data.repository.CalculatorRepository
import sg.searchhouse.agentconnect.enumeration.app.CalculatorAppEnum
import sg.searchhouse.agentconnect.model.api.calculator.CalculateSellingResponse
import sg.searchhouse.agentconnect.model.api.xvalue.SearchWithWalkupResponse
import sg.searchhouse.agentconnect.model.app.XValueEntryParams
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import java.util.*
import javax.inject.Inject

class SellingCalculatorFragmentViewModel constructor(application: Application) :
    ApiBaseViewModel<CalculateSellingResponse>(application) {

    val entryType = MutableLiveData<CalculatorAppEnum.SellingCalculatorEntryType>()

    // For re-open of get property details dialog
    val xValueEntryParams = MutableLiveData<XValueEntryParams>()
    val walkupResponseData = MutableLiveData<SearchWithWalkupResponse.Data>()

    val address = MutableLiveData<String>()
    val estSellingPrice = MutableLiveData<Long>()
    val saleDate = MutableLiveData<Date>()
    val purchaseDate = MutableLiveData<Date>()
    val purchasePrice = MutableLiveData<Long>()
    val buyerStampDuty = MutableLiveData<Long>()

    val saleDateLabel: LiveData<String> = Transformations.map(saleDate) { date ->
        getDateLabel(date)
    }

    val purchaseDateLabel: LiveData<String> = Transformations.map(purchaseDate) { date ->
        getDateLabel(date)
    }

    val shouldEnableShareButton = MediatorLiveData<Boolean>()

    val isShowDateError = MediatorLiveData<Boolean>()

    @Inject
    lateinit var applicationContext: Context

    @Inject
    lateinit var calculatorRepository: CalculatorRepository

    init {
        viewModelComponent.inject(this)
        setupShouldEnableShareButton()
        setupIsShowDateError()
    }

    private fun setupIsShowDateError() {
        isShowDateError.addSource(saleDate) {
            isShowDateError.postValue(shouldShowDateError())
        }
        isShowDateError.addSource(purchaseDate) {
            isShowDateError.postValue(shouldShowDateError())
        }
    }

    private fun shouldShowDateError(): Boolean {
        if (purchaseDate.value == null) return false
        if (saleDate.value == null) return false
        return !areDatesValid()
    }

    private fun areDatesValid(): Boolean {
        val purchaseDate = purchaseDate.value ?: return false
        val saleDate = saleDate.value ?: return false
        val daysBetweenDates = DateTimeUtil.getDaysBetweenDates(purchaseDate, saleDate)
        return daysBetweenDates > 0
    }

    private fun setupShouldEnableShareButton() {
        shouldEnableShareButton.addSource(mainResponse) {
            updateShouldEnableShareButton()
        }
        shouldEnableShareButton.addSource(estSellingPrice) {
            updateShouldEnableShareButton()
        }
        shouldEnableShareButton.addSource(purchasePrice) {
            updateShouldEnableShareButton()
        }
    }

    private fun updateShouldEnableShareButton() {
        val purchasePrice = purchasePrice.value ?: 0
        val estSellingPrice = estSellingPrice.value ?: 0
        shouldEnableShareButton.postValue(mainResponse.value != null && purchasePrice > 0 && estSellingPrice > 0)
    }

    private fun getDateLabel(date: Date?): String {
        return date?.run { DateTimeUtil.convertDateToString(this, DateTimeUtil.FORMAT_DATE_4) }
            ?: ""
    }

    private fun getServerDateLabel(date: Date): String {
        return DateTimeUtil.convertDateToString(date, DateTimeUtil.FORMAT_DATE_12)
    }

    fun performRequest() {
        val sellingPrice = estSellingPrice.value ?: return clearResponse()
        val saleDate = saleDate.value?.run { getServerDateLabel(this) } ?: return clearResponse()
        val purchaseDate =
            purchaseDate.value?.run { getServerDateLabel(this) } ?: return clearResponse()
        val purchasePrice = purchasePrice.value ?: return clearResponse()
        val buyerStampDuty = buyerStampDuty.value ?: return clearResponse()

        performRequest(
            calculatorRepository.calculateSelling(
                sellingPrice = sellingPrice,
                saleDate = saleDate,
                purchaseDate = purchaseDate,
                purchasePrice = purchasePrice,
                bsdAbsd = buyerStampDuty
            )
        )
    }

    private fun clearResponse() {
        mainResponse.postValue(null)
    }

    override fun shouldResponseBeOccupied(response: CalculateSellingResponse): Boolean {
        return true
    }
}