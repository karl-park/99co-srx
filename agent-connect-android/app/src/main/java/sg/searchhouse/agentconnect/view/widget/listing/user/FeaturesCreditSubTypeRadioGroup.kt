package sg.searchhouse.agentconnect.view.widget.listing.user

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatRadioButton
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.ActivateSrxCreditListingsResponse.ActivateSrxCreditListingSummaryPO.ActivateSrxCreditListingPO.VirtualTourNewProjectPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.ActivateSrxCreditListingsResponse.ActivateSrxCreditListingSummaryPO.ActivateSrxCreditListingPO.FeaturedType

class FeaturesCreditSubTypeRadioGroup(context: Context, attributeSet: AttributeSet? = null) :
    RadioGroup(context, attributeSet) {

    init {
        orientation = HORIZONTAL
    }

    fun setupV360RadioGroup(
        v360Types: List<VirtualTourNewProjectPO>,
        selectedV360Type: VirtualTourNewProjectPO? = null,
        onSelect: (VirtualTourNewProjectPO) -> Unit
    ) {
        removeAllViews()
        val radioButtons = v360Types.mapIndexed { index, virtualTourNewProjectPO ->
            val radioButton = AppCompatRadioButton(context)
            radioButton.text = virtualTourNewProjectPO.roomType
            radioButton.setOnClickListener {
                onSelect.invoke(virtualTourNewProjectPO)
            }
            addView(radioButton)

            if (index < v360Types.size - 1) {
                val layoutParams = radioButton.layoutParams as LayoutParams
                layoutParams.marginEnd =
                    context.resources.getDimensionPixelOffset(R.dimen.spacing_s)
            }

            return@mapIndexed radioButton
        }

        if (selectedV360Type != null) {
            radioButtons.mapIndexed { index, appCompatRadioButton ->
                appCompatRadioButton.isChecked = v360Types[index].id == selectedV360Type.id
            }
        }
    }

    fun setup(
        featuredTypes: List<FeaturedType>,
        selectedFeaturedType: FeaturedType? = null,
        onSelect: (FeaturedType) -> Unit
    ) {
        removeAllViews()
        val radioButtons = featuredTypes.mapIndexed { index, featuredType ->
            val radioButton = AppCompatRadioButton(context)
            radioButton.text = resources.getQuantityString(
                R.plurals.label_feature_listing_type_with_count,
                featuredType.value,
                featuredType.name,
                featuredType.value.toString()
            )
            radioButton.setOnClickListener {
                onSelect.invoke(featuredType)
            }
            addView(radioButton)

            // Set right margin
            if (index < featuredTypes.size - 1) {
                val layoutParams = radioButton.layoutParams as LayoutParams
                layoutParams.marginEnd =
                    context.resources.getDimensionPixelOffset(R.dimen.spacing_s)
            }

            return@mapIndexed radioButton
        }

        // Populate check
        selectedFeaturedType?.run {
            radioButtons.mapIndexed { index, appCompatRadioButton ->
                appCompatRadioButton.isChecked =
                    TextUtils.equals(featuredTypes[index].name, selectedFeaturedType.name)
            }
        } ?: run {
            radioButtons.mapIndexed { index, appCompatRadioButton ->
                appCompatRadioButton.isChecked = when (index) {
                    0 -> true
                    else -> false
                }
            }
        }
    }

    fun enableDisableRadioGroup(isEnabled: Boolean) {
        (0 until childCount).map {
            val radioButton = getChildAt(it) as AppCompatRadioButton
            radioButton.isEnabled = isEnabled
        }
    }
}