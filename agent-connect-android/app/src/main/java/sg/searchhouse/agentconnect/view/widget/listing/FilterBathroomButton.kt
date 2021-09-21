package sg.searchhouse.agentconnect.view.widget.listing

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ButtonFilterBathroomBinding

class FilterBathroomButton (context: Context) : FrameLayout(context, null) {
    val binding: ButtonFilterBathroomBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.button_filter_bathroom, this, true)
}