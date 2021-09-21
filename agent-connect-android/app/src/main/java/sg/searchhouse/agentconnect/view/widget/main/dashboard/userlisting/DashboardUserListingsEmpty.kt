package sg.searchhouse.agentconnect.view.widget.main.dashboard.userlisting

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import sg.searchhouse.agentconnect.databinding.CardDashboardUserListingsEmptyBinding

class DashboardUserListingsEmpty(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {
    var showCreateListingDialog: (() -> Unit)? = null

    private val binding = CardDashboardUserListingsEmptyBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.btnCreateListing.setOnClickListener { showCreateListingDialog?.invoke() }
    }
}