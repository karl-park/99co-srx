package sg.searchhouse.agentconnect.view.widget.listing

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.LayoutSrxChatListingInfoBinding

class SrxChatListingInfoLayout(context: Context) : FrameLayout(context, null) {
    val binding: LayoutSrxChatListingInfoBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.layout_srx_chat_listing_info,
        this,
        true
    )
}