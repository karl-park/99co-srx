package sg.searchhouse.agentconnect.view.widget.listing.portal

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.CardItemListingsSyncOptionBinding

class CardListingSyncOption(context: Context) : FrameLayout(context, null) {
    val binding: CardItemListingsSyncOptionBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.card_item_listings_sync_option,
        this,
        true
    )
}