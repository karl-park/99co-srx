package sg.searchhouse.agentconnect.view.widget.property

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import sg.searchhouse.agentconnect.databinding.SelectorResidentialMainBinding
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum

class ResidentialMainSelector(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {
    val binding: SelectorResidentialMainBinding =
        SelectorResidentialMainBinding.inflate(LayoutInflater.from(context), this, true)
    var onSelectPropertyMainType: ((ListingEnum.PropertyMainType) -> Unit)? = null

    init {
        binding.btnPropertyMainTypeResidential.button.setOnClickListener {
            onSelectPropertyMainType?.invoke(ListingEnum.PropertyMainType.RESIDENTIAL)
        }
        binding.btnPropertyMainTypeHdb.button.setOnClickListener {
            onSelectPropertyMainType?.invoke(ListingEnum.PropertyMainType.HDB)
        }
        binding.btnPropertyMainTypeCondo.button.setOnClickListener {
            onSelectPropertyMainType?.invoke(ListingEnum.PropertyMainType.CONDO)
        }
        binding.btnPropertyMainTypeLanded.button.setOnClickListener {
            onSelectPropertyMainType?.invoke(ListingEnum.PropertyMainType.LANDED)
        }
    }
}