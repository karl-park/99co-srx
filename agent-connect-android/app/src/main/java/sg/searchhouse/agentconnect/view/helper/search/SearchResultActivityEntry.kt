package sg.searchhouse.agentconnect.view.helper.search

import android.app.Activity
import android.os.Bundle
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.app.SearchEntryType
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.activity.listing.ListingsActivity

// Entry methods of search result, e.g. `ListingsActivity`, etc.
// TODO Narrow down to `ListingsActivity` only
object SearchResultActivityEntry {
    const val EXTRA_KEY_ENTRY_TYPE = "EXTRA_KEY_ENTRY_TYPE"

    const val EXTRA_KEY_QUERY = "EXTRA_KEY_QUERY"
    const val EXTRA_KEY_TITLE = "EXTRA_KEY_TITLE"
    const val EXTRA_KEY_OWNERSHIP_TYPE = "EXTRA_KEY_OWNERSHIP_TYPE"
    const val EXTRA_KEY_PROPERTY_PURPOSE = "EXTRA_KEY_PROPERTY_PURPOSE"
    const val EXTRA_KEY_PROPERTY_MAIN_TYPE = "EXTRA_KEY_PROPERTY_MAIN_TYPE"
    const val EXTRA_KEY_PROPERTY_SUB_TYPE = "EXTRA_KEY_PROPERTY_SUB_TYPE"

    const val EXTRA_KEY_AMENITY_IDS = "EXTRA_KEY_AMENITY_IDS"
    const val EXTRA_KEY_AMENITY_NAMES = "EXTRA_KEY_AMENITY_NAMES"

    const val EXTRA_KEY_DISTRICT_IDS = "EXTRA_KEY_DISTRICT_IDS"
    const val EXTRA_KEY_DISTRICT_NAMES = "EXTRA_KEY_DISTRICT_NAMES"

    const val EXTRA_KEY_HDB_TOWN_IDS = "EXTRA_KEY_HDB_TOWN_IDS"
    const val EXTRA_KEY_HDB_TOWN_NAMES = "EXTRA_KEY_HDB_TOWN_NAMES"

    const val EXTRA_KEY_PROJECT_ID = "EXTRA_KEY_PROJECT_ID"
    const val EXTRA_KEY_PROJECT_NAME = "EXTRA_KEY_PROJECT_NAME"
    const val EXTRA_KEY_PROJECT_DESCRIPTION = "EXTRA_KEY_PROJECT_DESCRIPTION"

    /**
     * optionalTitle:
     * In case title different from query,
     * i.e. when search by postal code
     * query by postal code but display title as address
     */
    fun launchListingsBySearchText(
        fromActivity: BaseActivity,
        query: String,
        propertyPurpose: ListingEnum.PropertyPurpose,
        ownershipType: ListingEnum.OwnershipType = ListingEnum.OwnershipType.SALE,
        isFinishOrigin: Boolean = true,
        optionalTitle: String? = null
    ) {
        val extras = Bundle()
        extras.putString(EXTRA_KEY_QUERY, query)
        extras.putString(EXTRA_KEY_TITLE, optionalTitle)
        extras.putSerializable(EXTRA_KEY_OWNERSHIP_TYPE, ownershipType)
        extras.putSerializable(EXTRA_KEY_PROPERTY_PURPOSE, propertyPurpose)
        extras.putSerializable(EXTRA_KEY_ENTRY_TYPE, SearchEntryType.QUERY)
        fromActivity.launchActivity(ListingsActivity::class.java, extras)
        fromActivity.setResult(Activity.RESULT_OK)
        if (isFinishOrigin) {
            fromActivity.finish()
        }
    }

    fun launchByPropertyMainType(
        fromActivity: BaseActivity,
        toActivityClass: Class<*>,
        query: String?,
        propertyMainType: ListingEnum.PropertyMainType,
        propertyPurpose: ListingEnum.PropertyPurpose,
        ownershipType: ListingEnum.OwnershipType
    ) {
        val extras = Bundle()
        extras.putSerializable(EXTRA_KEY_QUERY, query)
        extras.putSerializable(EXTRA_KEY_PROPERTY_MAIN_TYPE, propertyMainType)
        extras.putSerializable(EXTRA_KEY_OWNERSHIP_TYPE, ownershipType)
        extras.putSerializable(EXTRA_KEY_PROPERTY_PURPOSE, propertyPurpose)
        extras.putSerializable(EXTRA_KEY_ENTRY_TYPE, SearchEntryType.PROPERTY_MAIN_TYPE)
        fromActivity.launchActivity(toActivityClass, extras)
        fromActivity.setResult(Activity.RESULT_OK)
        fromActivity.finish()
    }

    fun launchByPropertySubType(
        fromActivity: BaseActivity,
        toActivityClass: Class<*>,
        propertySubType: ListingEnum.PropertySubType,
        propertyPurpose: ListingEnum.PropertyPurpose,
        ownershipType: ListingEnum.OwnershipType
    ) {
        val extras = Bundle()
        extras.putSerializable(EXTRA_KEY_QUERY, "")
        extras.putSerializable(EXTRA_KEY_PROPERTY_SUB_TYPE, propertySubType)
        extras.putSerializable(EXTRA_KEY_OWNERSHIP_TYPE, ownershipType)
        extras.putSerializable(EXTRA_KEY_PROPERTY_PURPOSE, propertyPurpose)
        extras.putSerializable(EXTRA_KEY_ENTRY_TYPE, SearchEntryType.PROPERTY_SUB_TYPE)
        fromActivity.launchActivity(toActivityClass, extras)
        fromActivity.setResult(Activity.RESULT_OK)
        fromActivity.finish()
    }

    fun launchByAmenity(
        fromActivity: BaseActivity,
        toActivityClass: Class<*>,
        amenityIds: String,
        amenityNames: String,
        propertyPurpose: ListingEnum.PropertyPurpose,
        ownershipType: ListingEnum.OwnershipType
    ) {
        val extras = Bundle()
        extras.putString(EXTRA_KEY_AMENITY_IDS, amenityIds)
        extras.putString(EXTRA_KEY_AMENITY_NAMES, amenityNames)
        extras.putSerializable(EXTRA_KEY_OWNERSHIP_TYPE, ownershipType)
        extras.putSerializable(EXTRA_KEY_PROPERTY_PURPOSE, propertyPurpose)
        extras.putSerializable(EXTRA_KEY_ENTRY_TYPE, SearchEntryType.AMENITY)
        fromActivity.launchActivity(toActivityClass, extras)
        fromActivity.setResult(Activity.RESULT_OK)
        fromActivity.finish()
    }

    fun launchByDistrict(
        fromActivity: BaseActivity,
        toActivityClass: Class<*>,
        districtIds: String,
        districtNames: String,
        propertyPurpose: ListingEnum.PropertyPurpose,
        ownershipType: ListingEnum.OwnershipType
    ) {
        val extras = Bundle()
        extras.putString(EXTRA_KEY_DISTRICT_IDS, districtIds)
        extras.putString(EXTRA_KEY_DISTRICT_NAMES, districtNames)
        extras.putSerializable(EXTRA_KEY_OWNERSHIP_TYPE, ownershipType)
        extras.putSerializable(EXTRA_KEY_PROPERTY_PURPOSE, propertyPurpose)
        extras.putSerializable(EXTRA_KEY_ENTRY_TYPE, SearchEntryType.DISTRICT)
        fromActivity.launchActivity(toActivityClass, extras)
        fromActivity.setResult(Activity.RESULT_OK)
        fromActivity.finish()
    }

    fun launchByHdbTown(
        fromActivity: BaseActivity,
        toActivityClass: Class<*>,
        hdbTownIds: String,
        hdbTownNames: String,
        propertyPurpose: ListingEnum.PropertyPurpose,
        ownershipType: ListingEnum.OwnershipType
    ) {
        val extras = Bundle()
        extras.putString(EXTRA_KEY_HDB_TOWN_IDS, hdbTownIds)
        extras.putString(EXTRA_KEY_HDB_TOWN_NAMES, hdbTownNames)
        extras.putSerializable(EXTRA_KEY_OWNERSHIP_TYPE, ownershipType)
        extras.putSerializable(EXTRA_KEY_PROPERTY_PURPOSE, propertyPurpose)
        extras.putSerializable(EXTRA_KEY_ENTRY_TYPE, SearchEntryType.HDB_TOWN)
        fromActivity.launchActivity(toActivityClass, extras)
        fromActivity.setResult(Activity.RESULT_OK)
        fromActivity.finish()
    }
}