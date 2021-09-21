package sg.searchhouse.agentconnect.view.widget.listing.community

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillHyperTargetPropertySubTypeBinding

class HyperTargetPropertySubTypePill (context: Context) : FrameLayout(context, null) {
    val binding: PillHyperTargetPropertySubTypeBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pill_hyper_target_property_sub_type, this, true)
}