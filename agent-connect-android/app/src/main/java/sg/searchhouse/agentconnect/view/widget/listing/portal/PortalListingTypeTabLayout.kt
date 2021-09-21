package sg.searchhouse.agentconnect.view.widget.listing.portal

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.TabLayoutPortalListingTypeBinding
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum

class PortalListingTypeTabLayout constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : LinearLayout(context, attributeSet) {

    var onToggleListingsType: ((ListingManagementEnum.PortalListingsType) -> Unit)? = null
    val binding: TabLayoutPortalListingTypeBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.tab_layout_portal_listing_type,
        this,
        true
    )

    init {
        binding.tabShowAll.setOnClickListener {
            onToggleListingsType?.invoke(ListingManagementEnum.PortalListingsType.ALL_LISTINGS)
        }
        binding.tabImportedToSrx.setOnClickListener {
            onToggleListingsType?.invoke(ListingManagementEnum.PortalListingsType.IMPORTED_LISTING)
        }
    }

    fun setValue(type: ListingManagementEnum.PortalListingsType) {
        binding.listingType = type
    }
}