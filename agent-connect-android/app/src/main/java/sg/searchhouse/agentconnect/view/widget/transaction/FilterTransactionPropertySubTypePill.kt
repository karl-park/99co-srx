package sg.searchhouse.agentconnect.view.widget.transaction

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillFilterTransactionPropertySubTypeBinding

class FilterTransactionPropertySubTypePill(context: Context) : FrameLayout(context, null) {
    val binding: PillFilterTransactionPropertySubTypeBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.pill_filter_transaction_property_sub_type,
        this,
        true
    )
}