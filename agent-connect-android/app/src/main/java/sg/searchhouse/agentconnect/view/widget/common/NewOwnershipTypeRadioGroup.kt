package sg.searchhouse.agentconnect.view.widget.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RadioGroup
import kotlinx.android.synthetic.main.radio_group_ownership_new.view.*
import sg.searchhouse.agentconnect.databinding.RadioGroupOwnershipNewBinding
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum.OwnershipType

// TODO Replace the old `OwnershipRadioGroup`
class NewOwnershipTypeRadioGroup(context: Context, attrs: AttributeSet? = null) :
    RadioGroup(context, attrs) {
    val binding: RadioGroupOwnershipNewBinding =
        RadioGroupOwnershipNewBinding.inflate(LayoutInflater.from(context), this, true)
    var onSelectOwnership: ((OwnershipType) -> Unit)? = null

    init {
        rb_sale.setOnClickListener {
            onSelectOwnership?.invoke(OwnershipType.SALE)
        }

        rb_rent.setOnClickListener {
            onSelectOwnership?.invoke(OwnershipType.RENT)
        }
    }
}