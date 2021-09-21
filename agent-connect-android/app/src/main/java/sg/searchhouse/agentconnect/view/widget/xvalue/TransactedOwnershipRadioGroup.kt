package sg.searchhouse.agentconnect.view.widget.xvalue

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatRadioButton
import sg.searchhouse.agentconnect.databinding.RadioGroupOwnershipTransactedBinding
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum.OwnershipType

class TransactedOwnershipRadioGroup(context: Context, attrs: AttributeSet? = null) :
    RadioGroup(context, attrs) {
    var onSelectOwnershipListener: ((OwnershipType) -> Unit)? = null

    private val binding = RadioGroupOwnershipTransactedBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.rbSold.setOnClickListener {
            onSelectOwnershipListener?.invoke(OwnershipType.SALE)
        }

        binding.rbRented.setOnClickListener {
            onSelectOwnershipListener?.invoke(OwnershipType.RENT)
        }
    }

    fun setValue(ownershipType: OwnershipType) {
        when (ownershipType) {
            OwnershipType.SALE -> setChecked(binding.rbSold, true)
            OwnershipType.RENT -> setChecked(binding.rbRented, true)
            else -> {}
        }
    }

    private fun setChecked(radioButton: AppCompatRadioButton, isChecked: Boolean) {
        radioButton.isChecked = isChecked

        // To prevent animation when update check programmatically
        binding.rbSold.jumpDrawablesToCurrentState()
        binding.rbRented.jumpDrawablesToCurrentState()
    }

    fun disableSoldRadio() {
        binding.rbSold.isEnabled = false
        binding.rbRented.isEnabled = true
    }

    fun disableRentedRadio() {
        binding.rbSold.isEnabled = true
        binding.rbRented.isEnabled = false
    }

}