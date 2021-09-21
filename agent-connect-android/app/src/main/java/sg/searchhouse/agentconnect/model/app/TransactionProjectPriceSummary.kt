package sg.searchhouse.agentconnect.model.app

import android.content.Context
import android.text.TextUtils
import sg.searchhouse.agentconnect.R

class TransactionProjectPriceSummary(
    var highestPrice: String? = null,
    var lowestPrice: String? = null
) {
    fun isPriceLoaded(): Boolean {
        return !TextUtils.isEmpty(highestPrice) && !TextUtils.isEmpty(lowestPrice)
    }

    fun getPriceRange(context: Context): String {
        return context.getString(R.string.project_item_price, lowestPrice, highestPrice)
    }
}