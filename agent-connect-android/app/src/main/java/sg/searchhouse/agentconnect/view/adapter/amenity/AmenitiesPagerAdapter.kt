package sg.searchhouse.agentconnect.view.adapter.amenity

import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import sg.searchhouse.agentconnect.enumeration.api.LocationEnum
import sg.searchhouse.agentconnect.model.app.AmenityLocation
import sg.searchhouse.agentconnect.view.fragment.amenity.AmenitiesFragment
import sg.searchhouse.agentconnect.view.fragment.amenity.AmenitiesGoogleFragment
import sg.searchhouse.agentconnect.view.fragment.amenity.AmenitiesYourLocationFragment

class AmenitiesPagerAdapter(fm: FragmentManager, private val listingLocation: AmenityLocation) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val externalAmenity = ExternalAmenity.values().find { it.tabIndex == position }
        return when {
            position == 0 -> AmenitiesYourLocationFragment.newInstance(listingLocation)
            externalAmenity != null -> AmenitiesGoogleFragment.newInstance(amenityOptions[position],
                listingLocation, externalAmenity.placeTypes.joinToString(",") { it.value }
            )
            else -> AmenitiesFragment.newInstance(amenityOptions[position], listingLocation)
        }
    }

    fun getItemPosition(amenityOption: LocationEnum.AmenityOption): Int {
        return amenityOptions.indexOfFirst {
            TextUtils.equals(it.value, amenityOption.value)
        }
    }

    @Throws(IndexOutOfBoundsException::class)
    fun getItemFromPosition(position: Int): LocationEnum.AmenityOption {
        return amenityOptions[position]
    }

    override fun getCount(): Int {
        return amenityOptions.size
    }

    @Suppress("unused")
    enum class ExternalAmenity(val tabIndex: Int, val placeTypes: List<LocationEnum.PlaceType>) {
        FNB(5, listOf(LocationEnum.PlaceType.RESTAURANT)),
        HOSPITAL(6, listOf(LocationEnum.PlaceType.DOCTOR)),
        PLACES_OF_WORSHIP(
            7,
            listOf(LocationEnum.PlaceType.CHURCH, LocationEnum.PlaceType.MOSQUE)
        )
    }

    companion object {
        val amenityOptions = LocationEnum.AmenityOption.values()
    }
}