package sg.searchhouse.agentconnect.view.widget.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatRadioButton
import sg.searchhouse.agentconnect.databinding.RadioGroupOwnershipBinding
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum.OwnershipType

class OwnershipRadioGroup(context: Context, attrs: AttributeSet? = null) :
    RadioGroup(context, attrs) {
    var onSelectOwnershipListener: ((OwnershipType) -> Unit)? = null

    private val binding = RadioGroupOwnershipBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.rbSale.setOnClickListener {
            onSelectOwnershipListener?.invoke(OwnershipType.SALE)
        }

        binding.rbRent.setOnClickListener {
            onSelectOwnershipListener?.invoke(OwnershipType.RENT)
        }

        binding.rbRoomRental.setOnClickListener {
            onSelectOwnershipListener?.invoke(OwnershipType.ROOM_RENTAL)
        }
    }

    fun setValue(ownershipType: OwnershipType) {
        when (ownershipType) {
            OwnershipType.SALE -> setChecked(binding.rbSale, true)
            OwnershipType.RENT -> setChecked(binding.rbRent, true)
            OwnershipType.ROOM_RENTAL -> setChecked(binding.rbRoomRental, true)
        }
    }

    private fun setChecked(radioButton: AppCompatRadioButton, isChecked: Boolean) {
        radioButton.isChecked = isChecked

        // To prevent animation when update check programmatically
        binding.rbSale.jumpDrawablesToCurrentState()
        binding.rbRent.jumpDrawablesToCurrentState()
        binding.rbRoomRental.jumpDrawablesToCurrentState()
    }

    fun showHideRoomRental(roomRentalFlag: Boolean) {
        if (roomRentalFlag) {
            binding.rbRoomRental.visibility = View.VISIBLE
        } else {
            binding.rbRoomRental.visibility = View.GONE
        }
    }

    fun disableSaleRadio() {
        binding.rbSale.isEnabled = false
        binding.rbRoomRental.isEnabled = true
        binding.rbRent.isEnabled = true
    }

    fun disableRentRadios() {
        binding.rbSale.isEnabled = true
        binding.rbRoomRental.isEnabled = false
        binding.rbRent.isEnabled = false
    }

}