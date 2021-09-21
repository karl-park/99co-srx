package sg.searchhouse.agentconnect.view.widget.listing.create

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillCreateListingRoomTypeBinding

class CreateListingRoomTypePill(context: Context) : FrameLayout(context, null) {
    val binding: PillCreateListingRoomTypeBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.pill_create_listing_room_type,
        this,
        true
    )
}