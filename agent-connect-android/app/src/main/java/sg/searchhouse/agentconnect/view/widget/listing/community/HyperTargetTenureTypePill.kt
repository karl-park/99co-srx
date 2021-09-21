package sg.searchhouse.agentconnect.view.widget.listing.community

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillFilterTenureTypeBinding
import sg.searchhouse.agentconnect.databinding.PillHyperTargetTenureTypeBinding

class HyperTargetTenureTypePill (context: Context) : FrameLayout(context, null) {
    val binding: PillHyperTargetTenureTypeBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pill_hyper_target_tenure_type, this, true)
}