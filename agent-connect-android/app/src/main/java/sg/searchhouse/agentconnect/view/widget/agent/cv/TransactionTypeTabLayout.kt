package sg.searchhouse.agentconnect.view.widget.agent.cv

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.TabLayoutTransactionTypeBinding
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum

class TransactionTypeTabLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {

    val binding: TabLayoutTransactionTypeBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.tab_layout_transaction_type,
        this,
        true
    )

    var onTabClickListener: ((AgentProfileAndCvEnum.TransactionType) -> Unit)? = null

    init {
        binding.btnTabSold.setOnClickListener {
            onTabClickListener?.invoke(AgentProfileAndCvEnum.TransactionType.SOLD)
        }

        binding.btnTabRented.setOnClickListener {
            onTabClickListener?.invoke(AgentProfileAndCvEnum.TransactionType.RENTED)
        }
    }
}