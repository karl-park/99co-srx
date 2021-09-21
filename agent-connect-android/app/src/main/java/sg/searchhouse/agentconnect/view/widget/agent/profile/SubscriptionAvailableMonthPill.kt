package sg.searchhouse.agentconnect.view.widget.agent.profile

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillSubscriptionAvailableMonthBinding

class SubscriptionAvailableMonthPill(context: Context) : FrameLayout(context, null) {
    val binding: PillSubscriptionAvailableMonthBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.pill_subscription_available_month, this, true
    )
}