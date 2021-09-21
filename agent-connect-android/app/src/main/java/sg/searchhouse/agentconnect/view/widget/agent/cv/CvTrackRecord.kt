package sg.searchhouse.agentconnect.view.widget.agent.cv

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.LayoutCvTrackRecordBinding
import sg.searchhouse.agentconnect.model.api.agent.AgentPO

class CvTrackRecord(context: Context, attributeSet: AttributeSet? = null) :
    LinearLayout(context, attributeSet) {

    val binding: LayoutCvTrackRecordBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.layout_cv_track_record,
        this,
        true
    )

    init {
        binding.switchTrackRecord.setOnCheckedChangeListener { compoundButton, b ->
            binding.viewModel?.let { viewModel ->
                viewModel.agentCvPO.value?.let { cv ->
                    if (compoundButton.isPressed) {
                        cv.showTransactions = b
                        viewModel.saveAgentCv()
                    }
                }
            }
        }
    }

    fun populateTrackRecord(agentPO: AgentPO) {
        agentPO.transactionSummary?.let { transaction ->
            binding.hdbSaleTransactions =
                context.getString(R.string.transaction_sold, transaction.saleHdbTotal.toString())
            binding.hdbRentedTransactions =
                context.getString(R.string.transaction_rented, transaction.rentHdbTotal.toString())
            binding.privateSaleTransactions = context.getString(
                R.string.transaction_sold,
                transaction.salePrivateTotal.toString()
            )
            binding.privateRentedTransactions = context.getString(
                R.string.transaction_rented,
                transaction.rentPrivateTotal.toString()
            )
        }

    }
}