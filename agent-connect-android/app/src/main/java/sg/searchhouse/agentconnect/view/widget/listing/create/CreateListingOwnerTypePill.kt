package sg.searchhouse.agentconnect.view.widget.listing.create

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.PillCreateListingOwnerTypeBinding

class CreateListingOwnerTypePill(context: Context) : FrameLayout(context, null) {
    val binding: PillCreateListingOwnerTypeBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.pill_create_listing_owner_type,
        this,
        true
    )
}