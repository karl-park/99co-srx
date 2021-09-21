package sg.searchhouse.agentconnect.view.widget.listing

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.TabLayoutMyListingsDraftModeBinding
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum

class MyListingsDraftModeTabLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {

    var onTabClickListener: ((ListingManagementEnum.ListingDraftMode) -> Unit)? = null
    val binding: TabLayoutMyListingsDraftModeBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.tab_layout_my_listings_draft_mode, this, true
    )

    init {
        binding.tabListings.setOnClickListener {
            onTabClickListener?.invoke(ListingManagementEnum.ListingDraftMode.LISTING)
        }
        binding.tabCeaForms.setOnClickListener {
            onTabClickListener?.invoke(ListingManagementEnum.ListingDraftMode.CEA_FORMS)
        }
    }

    fun updateDraftModeCount(listingCount: Int, ceaFormCount: Int) {
        binding.tabListings.text = when (listingCount) {
            0 -> context.getString(R.string.label_my_listing_draft_listing)
            else -> context.getString(R.string.label_my_listing_draft_listing_count, listingCount)
        }
        binding.tabCeaForms.text = when (ceaFormCount) {
            0 -> context.getString(R.string.label_my_listing_draft_cea_forms)
            else -> context.getString(R.string.label_my_listing_draft_cea_forms_count, ceaFormCount)
        }
    }
}