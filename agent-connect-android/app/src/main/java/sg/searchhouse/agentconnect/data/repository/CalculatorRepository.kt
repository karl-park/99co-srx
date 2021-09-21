package sg.searchhouse.agentconnect.data.repository

import retrofit2.Call
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.model.api.calculator.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Doc: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/928317455/Calculator+API
 */
@Singleton
class CalculatorRepository @Inject constructor(private val srxDataSource: SrxDataSource) {
    fun calculateAffordQuickMaxPurchasePrice(
        monthlyIncome: Int,
        monthlyDebt: Int,
        downpayment: Int
    ): Call<CalculateMaxPurchasePriceResponse> {
        return srxDataSource.calculateAffordQuickMaxPurchasePrice(
            MaxPurchasePricePO(monthlyIncome.toLong(), monthlyDebt.toLong(), downpayment.toLong())
        )
    }

    fun calculateAffordQuickIncomeRequired(
        propPurchasePrice: Int,
        monthlyDebt: Int,
        downpayment: Int
    ): Call<CalculateIncomeRequiredResponse> {
        return srxDataSource.calculateAffordQuickIncomeRequired(
            IncomeRequiredPO(propPurchasePrice.toLong(), monthlyDebt.toLong(), downpayment.toLong())
        )
    }

    fun calculateAffordAdvanced(affordAdvancedInputPO: AffordAdvancedInputPO): Call<CalculateAffordAdvancedResponse> {
        return srxDataSource.calculateAffordAdvanced(affordAdvancedInputPO)
    }

    fun calculateSelling(
        sellingPrice: Long,
        purchasePrice: Long,
        bsdAbsd: Long,
        saleDate: String,
        purchaseDate: String
    ): Call<CalculateSellingResponse> {
        return srxDataSource.calculateSell(
            SellingInputPO(
                sellingPrice,
                purchasePrice,
                bsdAbsd,
                saleDate,
                purchaseDate
            )
        )
    }

    fun calculateStampDutyBuy(stampDutyInputPO: StampDutyInputPO): Call<CalculateStampDutyBuyResponse> {
        return srxDataSource.calculateStampDutyBuy(stampDutyInputPO)
    }

    fun calculateStampDutyRent(rentDutyInputPO: RentDutyInputPO): Call<CalculateStampDutyRentResponse> {
        return srxDataSource.calculateStampDutyRent(rentDutyInputPO)
    }
}