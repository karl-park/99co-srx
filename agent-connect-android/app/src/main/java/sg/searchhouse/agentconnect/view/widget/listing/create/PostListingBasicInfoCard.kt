package sg.searchhouse.agentconnect.view.widget.listing.create

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.CardPostListingBasicInfoBinding
import sg.searchhouse.agentconnect.model.api.listing.ListingEditPO

class PostListingBasicInfoCard(context: Context, attributeSet: AttributeSet? = null) :
    LinearLayout(context, attributeSet) {

    val binding: CardPostListingBasicInfoBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.card_post_listing_basic_info,
        this,
        true
    )

    fun setListingEditPO(listingEditPO: ListingEditPO) {
        binding.listingEditPO = listingEditPO
    }

}