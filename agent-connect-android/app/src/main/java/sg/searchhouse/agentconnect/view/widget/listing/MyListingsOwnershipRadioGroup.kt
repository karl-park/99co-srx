package sg.searchhouse.agentconnect.view.widget.listing

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.RadioGroupMyListingsOwnershipBinding
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum

class MyListingsOwnershipRadioGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    var binding: RadioGroupMyListingsOwnershipBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.radio_group_my_listings_ownership,
        this,
        true
    )

    var onTabClickListener: ((ListingEnum.OwnershipType) -> Unit)? = null

    init {
        binding.rbRent.setOnClickListener {
            onTabClickListener?.invoke(ListingEnum.OwnershipType.RENT)
        }

        binding.rbSale.setOnClickListener {
            onTabClickListener?.invoke(ListingEnum.OwnershipType.SALE)
        }
    }

    // Update list item count on tab title e.g. Sale, Sale(3), Rent, Rent(5)
    fun updateTitleCounts(saleCount: Int, rentCount: Int) {
        binding.rbSale.text = when (saleCount) {
            0 -> context.getString(R.string.label_sale)
            else -> context.getString(R.string.label_sale_with_count, saleCount)
        }
        binding.rbRent.text = when (rentCount) {
            0 -> context.getString(R.string.label_rent)
            else -> context.getString(R.string.label_rent_with_count, rentCount)
        }
    }
}