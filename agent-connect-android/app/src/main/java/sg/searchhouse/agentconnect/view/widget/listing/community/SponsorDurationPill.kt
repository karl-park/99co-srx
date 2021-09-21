package sg.searchhouse.agentconnect.view.widget.listing.community

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillSponsorDurationBinding

class SponsorDurationPill(context: Context) : FrameLayout(context, null) {
    val binding: PillSponsorDurationBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.pill_sponsor_duration,
        this,
        true
    )
}