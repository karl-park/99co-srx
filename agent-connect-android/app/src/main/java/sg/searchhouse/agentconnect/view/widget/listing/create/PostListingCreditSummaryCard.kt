package sg.searchhouse.agentconnect.view.widget.listing.create

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.CardPostListingCreditSummaryBinding
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.create.PostListingViewModel

class PostListingCreditSummaryCard(context: Context, attributeSet: AttributeSet? = null) :
    LinearLayout(context, attributeSet) {

    val binding: CardPostListingCreditSummaryBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.card_post_listing_credit_summary,
        this,
        true
    )

    fun populateCreditBalanceAndCost(postingCost: Int, balance: Int) {
        binding.postingCreditCost = this.resources.getQuantityString(
            R.plurals.label_credit_count,
            postingCost,
            NumberUtil.formatThousand(postingCost)
        )
        binding.showEmptyWallet = balance == 0
    }

    fun updateViewModel(viewModel: PostListingViewModel) {
        binding.viewModel = viewModel
    }
}