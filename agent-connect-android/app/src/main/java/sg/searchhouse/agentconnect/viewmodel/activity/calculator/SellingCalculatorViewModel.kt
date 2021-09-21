package sg.searchhouse.agentconnect.viewmodel.activity.calculator

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.enumeration.app.SellingCalculatorTab
import sg.searchhouse.agentconnect.model.api.calculator.CalculateSellingResponse
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel

class SellingCalculatorViewModel(application: Application) : CoreViewModel(application) {
    val selectedSellingCalculatorTab = MutableLiveData<SellingCalculatorTab>().apply {
        value = SellingCalculatorTab.LOOK_UP_PROPERTY
    }

    val calculateSellingResponse = MutableLiveData<CalculateSellingResponse>()

    val sellerStampDuty: LiveData<String> = Transformations.map(calculateSellingResponse) {
        it?.result?.sellerStampDuty?.run { NumberUtil.getFormattedCurrency(this.toInt()) } ?: "-"
    }

    val capitalGain: LiveData<String> = Transformations.map(calculateSellingResponse) {
        it?.result?.capitalGain?.run {
            "${NumberUtil.roundDouble(this, decimalPlace = 2)}%"
        } ?: "-"
    }
}