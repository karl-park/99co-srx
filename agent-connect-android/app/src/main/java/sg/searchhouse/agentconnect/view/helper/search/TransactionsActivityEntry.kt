package sg.searchhouse.agentconnect.view.helper.search

import android.os.Bundle
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.TransactionEnum
import sg.searchhouse.agentconnect.enumeration.app.SearchEntryType
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.activity.transaction.GroupTransactionsActivity
import sg.searchhouse.agentconnect.view.activity.transaction.ProjectTransactionsActivity

// Entry methods of search result to `GroupTransactionsActivity` and `ProjectTransactionActivity`
object TransactionsActivityEntry {
    // TODO Refactor
    private const val EXTRA_KEY_ENTRY_TYPE = "EXTRA_KEY_ENTRY_TYPE"

    private const val EXTRA_KEY_QUERY = "EXTRA_KEY_QUERY"
    private const val EXTRA_KEY_OWNERSHIP_TYPE = "EXTRA_KEY_OWNERSHIP_TYPE"
    private const val EXTRA_KEY_PROPERTY_PURPOSE = "EXTRA_KEY_PROPERTY_PURPOSE"
    private const val EXTRA_KEY_PROPERTY_MAIN_TYPE = "EXTRA_KEY_PROPERTY_MAIN_TYPE"
    private const val EXTRA_KEY_PROPERTY_SUB_TYPE = "EXTRA_KEY_PROPERTY_SUB_TYPE"

    private const val EXTRA_KEY_AMENITY_IDS = "EXTRA_KEY_AMENITY_IDS"
    private const val EXTRA_KEY_AMENITY_NAMES = "EXTRA_KEY_AMENITY_NAMES"

    private const val EXTRA_KEY_DISTRICT_IDS = "EXTRA_KEY_DISTRICT_IDS"
    private const val EXTRA_KEY_DISTRICT_NAMES = "EXTRA_KEY_DISTRICT_NAMES"

    private const val EXTRA_KEY_HDB_TOWN_IDS = "EXTRA_KEY_HDB_TOWN_IDS"
    private const val EXTRA_KEY_HDB_TOWN_NAMES = "EXTRA_KEY_HDB_TOWN_NAMES"

    private const val EXTRA_KEY_PROJECT_ID = "EXTRA_KEY_PROJECT_ID"
    private const val EXTRA_KEY_PROJECT_NAME = "EXTRA_KEY_PROJECT_NAME"
    private const val EXTRA_KEY_PROJECT_DESCRIPTION = "EXTRA_KEY_PROJECT_DESCRIPTION"
    private const val EXTRA_KEY_PROJECT_IS_SHOW_TOWER = "EXTRA_KEY_PROJECT_IS_SHOW_TOWER"

    fun launchBySearchText(
        fromActivity: BaseActivity,
        query: String,
        propertyPurpose: ListingEnum.PropertyPurpose,
        ownershipType: ListingEnum.OwnershipType = ListingEnum.OwnershipType.SALE,
        propertyMainType: ListingEnum.PropertyMainType,
        propertySubType: ListingEnum.PropertySubType? = null
    ) {
        val extras = Bundle()
        extras.putString(EXTRA_KEY_QUERY, query)
        extras.putSerializable(EXTRA_KEY_OWNERSHIP_TYPE, ownershipType)
        extras.putSerializable(EXTRA_KEY_PROPERTY_PURPOSE, propertyPurpose)
        extras.putSerializable(EXTRA_KEY_ENTRY_TYPE, SearchEntryType.QUERY)
        extras.putSerializable(EXTRA_KEY_PROPERTY_MAIN_TYPE, propertyMainType)
        extras.putSerializable(EXTRA_KEY_PROPERTY_SUB_TYPE, propertySubType)
        fromActivity.launchActivity(GroupTransactionsActivity::class.java, extras)
    }

    fun launchByPropertySubType(
        fromActivity: BaseActivity,
        toActivityClass: Class<*>,
        propertySubType: ListingEnum.PropertySubType,
        propertyPurpose: ListingEnum.PropertyPurpose,
        ownershipType: ListingEnum.OwnershipType
    ) {
        val extras = Bundle()
        extras.putSerializable(SearchResultActivityEntry.EXTRA_KEY_QUERY, "")
        extras.putSerializable(
            SearchResultActivityEntry.EXTRA_KEY_PROPERTY_SUB_TYPE,
            propertySubType
        )
        extras.putSerializable(SearchResultActivityEntry.EXTRA_KEY_OWNERSHIP_TYPE, ownershipType)
        extras.putSerializable(
            SearchResultActivityEntry.EXTRA_KEY_PROPERTY_PURPOSE,
            propertyPurpose
        )
        extras.putSerializable(
            SearchResultActivityEntry.EXTRA_KEY_ENTRY_TYPE,
            SearchEntryType.PROPERTY_SUB_TYPE
        )
        fromActivity.launchActivity(toActivityClass, extras)
    }

    fun launchByAmenity(
        fromActivity: BaseActivity,
        amenityIds: String,
        amenityNames: String,
        propertyPurpose: ListingEnum.PropertyPurpose,
        ownershipType: ListingEnum.OwnershipType,
        propertyMainType: ListingEnum.PropertyMainType
    ) {
        val extras = Bundle()
        extras.putString(EXTRA_KEY_AMENITY_IDS, amenityIds)
        extras.putString(EXTRA_KEY_AMENITY_NAMES, amenityNames)
        extras.putSerializable(EXTRA_KEY_OWNERSHIP_TYPE, ownershipType)
        extras.putSerializable(EXTRA_KEY_PROPERTY_PURPOSE, propertyPurpose)
        extras.putSerializable(EXTRA_KEY_ENTRY_TYPE, SearchEntryType.AMENITY)
        extras.putSerializable(EXTRA_KEY_PROPERTY_MAIN_TYPE, propertyMainType)
        fromActivity.launchActivity(GroupTransactionsActivity::class.java, extras)
    }

    fun launchByDistrict(
        fromActivity: BaseActivity,
        districtIds: String,
        districtNames: String,
        propertyPurpose: ListingEnum.PropertyPurpose,
        ownershipType: ListingEnum.OwnershipType,
        propertyMainType: ListingEnum.PropertyMainType
    ) {
        val extras = Bundle()
        extras.putString(EXTRA_KEY_DISTRICT_IDS, districtIds)
        extras.putString(EXTRA_KEY_DISTRICT_NAMES, districtNames)
        extras.putSerializable(EXTRA_KEY_OWNERSHIP_TYPE, ownershipType)
        extras.putSerializable(EXTRA_KEY_PROPERTY_PURPOSE, propertyPurpose)
        extras.putSerializable(EXTRA_KEY_ENTRY_TYPE, SearchEntryType.DISTRICT)
        extras.putSerializable(EXTRA_KEY_PROPERTY_MAIN_TYPE, propertyMainType)
        fromActivity.launchActivity(GroupTransactionsActivity::class.java, extras)
    }

    fun launchByHdbTown(
        fromActivity: BaseActivity,
        hdbTownIds: String,
        hdbTownNames: String,
        propertyPurpose: ListingEnum.PropertyPurpose,
        ownershipType: ListingEnum.OwnershipType,
        propertyMainType: ListingEnum.PropertyMainType
    ) {
        val extras = Bundle()
        extras.putString(EXTRA_KEY_HDB_TOWN_IDS, hdbTownIds)
        extras.putString(EXTRA_KEY_HDB_TOWN_NAMES, hdbTownNames)
        extras.putSerializable(EXTRA_KEY_OWNERSHIP_TYPE, ownershipType)
        extras.putSerializable(EXTRA_KEY_PROPERTY_PURPOSE, propertyPurpose)
        extras.putSerializable(EXTRA_KEY_ENTRY_TYPE, SearchEntryType.HDB_TOWN)
        extras.putSerializable(EXTRA_KEY_PROPERTY_MAIN_TYPE, propertyMainType)
        fromActivity.launchActivity(GroupTransactionsActivity::class.java, extras)
    }

    fun launchByProject(
        fromActivity: BaseActivity,
        projectId: Int,
        projectName: String,
        projectDescription: String = "",
        isShowTower: Boolean,
        propertySubType: ListingEnum.PropertySubType,
        propertyPurpose: ListingEnum.PropertyPurpose,
        ownershipType: ListingEnum.OwnershipType = ListingEnum.OwnershipType.SALE,
        minAge: Int? = null,
        maxAge: Int? = null,
        minSize: Int? = null,
        maxSize: Int? = null,
        areaType: TransactionEnum.AreaType? = null,
        tenureType: TransactionEnum.TenureType? = null,
        defaultTabPosition: Int = 0
    ) {
        val extras = Bundle()
        extras.putInt(EXTRA_KEY_PROJECT_ID, projectId)
        extras.putString(EXTRA_KEY_PROJECT_NAME, projectName)
        extras.putString(EXTRA_KEY_PROJECT_DESCRIPTION, projectDescription)
        extras.putBoolean(EXTRA_KEY_PROJECT_IS_SHOW_TOWER, isShowTower)
        extras.putSerializable(ProjectTransactionsActivity.EXTRA_KEY_OWNERSHIP_TYPE, ownershipType)
        extras.putSerializable(
            ProjectTransactionsActivity.EXTRA_KEY_PROPERTY_PURPOSE,
            propertyPurpose
        )
        extras.putSerializable(
            ProjectTransactionsActivity.EXTRA_KEY_PROPERTY_SUB_TYPE,
            propertySubType
        )

        minAge?.run { extras.putInt(ProjectTransactionsActivity.EXTRA_MIN_AGE, this) }
        maxAge?.run { extras.putInt(ProjectTransactionsActivity.EXTRA_MAX_AGE, this) }
        minSize?.run { extras.putInt(ProjectTransactionsActivity.EXTRA_MIN_SIZE, this) }
        maxSize?.run { extras.putInt(ProjectTransactionsActivity.EXTRA_MAX_SIZE, this) }
        extras.putSerializable(ProjectTransactionsActivity.EXTRA_AREA_TYPE, areaType)
        extras.putSerializable(ProjectTransactionsActivity.EXTRA_TENURE, tenureType)

        // For rotate screen
        extras.putSerializable(ProjectTransactionsActivity.EXTRA_DEFAULT_TAB_POSITION, defaultTabPosition)

        fromActivity.launchActivity(ProjectTransactionsActivity::class.java, extras)
    }

    fun launchBySearchTextAndPropertySubType(
        fromActivity: BaseActivity,
        query: String,
        propertySubType: ListingEnum.PropertySubType,
        propertyPurpose: ListingEnum.PropertyPurpose,
        ownershipType: ListingEnum.OwnershipType,
        propertyMainType: ListingEnum.PropertyMainType
    ) {
        val extras = Bundle()
        extras.putString(EXTRA_KEY_QUERY, query)
        extras.putSerializable(EXTRA_KEY_OWNERSHIP_TYPE, ownershipType)
        extras.putSerializable(EXTRA_KEY_PROPERTY_PURPOSE, propertyPurpose)
        extras.putSerializable(EXTRA_KEY_ENTRY_TYPE, SearchEntryType.QUERY_PROPERTY_SUB_TYPE)
        extras.putSerializable(EXTRA_KEY_PROPERTY_MAIN_TYPE, propertyMainType)
        extras.putSerializable(EXTRA_KEY_PROPERTY_SUB_TYPE, propertySubType)
        fromActivity.launchActivity(GroupTransactionsActivity::class.java, extras)
    }
}