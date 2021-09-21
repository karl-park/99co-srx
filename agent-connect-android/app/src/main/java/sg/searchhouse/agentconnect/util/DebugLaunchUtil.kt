package sg.searchhouse.agentconnect.util

import android.app.Activity
import android.os.Bundle
import sg.searchhouse.agentconnect.BuildConfig
import sg.searchhouse.agentconnect.enumeration.api.*
import sg.searchhouse.agentconnect.enumeration.app.WatchlistEnum
import sg.searchhouse.agentconnect.model.api.cobrokesms.CobrokeSmsListingPO
import sg.searchhouse.agentconnect.model.api.location.AddressPropertyTypePO
import sg.searchhouse.agentconnect.model.api.watchlist.WatchlistPropertyCriteriaPO
import sg.searchhouse.agentconnect.model.app.XValueEntryParams
import sg.searchhouse.agentconnect.view.activity.agent.profile.SubscriptionCreditPackageDetailsActivity
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.activity.listing.*
import sg.searchhouse.agentconnect.view.activity.listing.community.SearchPlanningAreasActivity
import sg.searchhouse.agentconnect.view.activity.project.ProjectInfoActivity
import sg.searchhouse.agentconnect.view.activity.transaction.FilterTransactionActivity
import sg.searchhouse.agentconnect.view.activity.watchlist.EditWatchlistCriteriaActivity
import sg.searchhouse.agentconnect.view.activity.watchlist.ListingsWatchlistActivity
import sg.searchhouse.agentconnect.view.activity.watchlist.TransactionsWatchlistActivity
import sg.searchhouse.agentconnect.view.activity.xvalue.XValueActivity
import sg.searchhouse.agentconnect.view.fragment.listing.certifiedlisting.HomeOwnerDetailDialogFragment
import sg.searchhouse.agentconnect.view.helper.search.SearchResultActivityEntry
import sg.searchhouse.agentconnect.view.helper.search.TransactionsActivityEntry

// Launch activity or fragment for debug purpose
@Suppress("unused")
object DebugLaunchUtil {
    // Launch activity for development/debug purpose, would never appear on production build
    @Suppress("MemberVisibilityCanBePrivate")
    fun debugActivity(activity: BaseActivity, classy: Class<*>? = null, extras: Bundle? = null) {
        if (BuildConfig.DEBUG && classy != null) {
            activity.launchActivity(classy, extras)
        }
    }

    fun debugListingDetails(activity: BaseActivity) {
        ListingDetailsActivity.launch(
            activity,
            listingId = "78553092",
            listingType = "A"
        )
    }

    fun debugHomeOwnerDetailDialogFragment(activity: BaseActivity) {
        HomeOwnerDetailDialogFragment.launch(activity.supportFragmentManager, "84192611")
    }

    fun debugListingDetailsEdit(activity: BaseActivity) {
        ListingDetailsActivity.launch(
            activity,
            listingId = "81661282",
            listingType = "A",
            launchMode = ListingDetailsActivity.LaunchMode.EDIT
        )
    }

    fun debugFilterListing(activity: BaseActivity) {
        val extras = Bundle()
        extras.putSerializable(
            FilterListingActivity.EXTRA_KEY_INPUT_PROPERTY_PURPOSE,
            ListingEnum.PropertyPurpose.RESIDENTIAL
        )
        debugActivity(activity, FilterListingActivity::class.java, extras)
    }

    fun debugFilterTransaction(activity: BaseActivity) {
        FilterTransactionActivity.launchBySearchText(
            activity = activity,
            requestCode = 73,
            propertyPurpose = ListingEnum.PropertyPurpose.RESIDENTIAL,
            ownershipType = ListingEnum.OwnershipType.SALE,
            query = "Serangoon Road"
        )
    }

    fun debugGroupTransactions(activity: BaseActivity) {
        TransactionsActivityEntry.launchByHdbTown(
            activity,
            LocationEnum.HdbTown.ANGMOKIO.id.toString(),
            activity.getString(LocationEnum.HdbTown.ANGMOKIO.label),
            ListingEnum.PropertyPurpose.RESIDENTIAL,
            ListingEnum.OwnershipType.SALE,
            propertyMainType = ListingEnum.PropertyMainType.HDB
        )
    }

    fun debugProjectTransactions(activity: BaseActivity) {
        // 66062: Landed
        // 3465: Tower view
        TransactionsActivityEntry.launchByProject(
            activity,
            66062,
            "The Sail@Marina Bay",
            "Very friendly town",
            isShowTower = true,
            propertySubType = ListingEnum.PropertySubType.TERRACE,
            propertyPurpose = ListingEnum.PropertyPurpose.RESIDENTIAL,
            ownershipType = ListingEnum.OwnershipType.SALE
        )
    }

    fun debugListings(activity: BaseActivity) {
        SearchResultActivityEntry.launchListingsBySearchText(
            activity,
            "Toa Payoh",
            ListingEnum.PropertyPurpose.RESIDENTIAL,
            ListingEnum.OwnershipType.SALE
        )
    }

    fun debugAmenities(activity: BaseActivity) {
        AmenitiesActivity.launch(
            activity,
            listingId = "80973102",
            listingType = "A",
            amenityOption = LocationEnum.AmenityOption.OTHERS
        )
    }

    fun debugMyListings(activity: BaseActivity) {
        MyListingsActivity.launch(
            activity,
            ListingEnum.OwnershipType.SALE,
            ListingEnum.PropertyMainType.CONDO
        )
    }

    fun debugSearch(activity: BaseActivity) {
        SearchActivity.launch(
            activity,
            SearchActivity.SearchType.LISTINGS,
            ListingEnum.PropertyPurpose.COMMERCIAL,
            ListingEnum.OwnershipType.SALE
        )
    }

    fun debugXValue(activity: BaseActivity) {
        val address = AddressPropertyTypePO(
            buildingKey = "JUR067",
            buildingName = "Jurong West Primary School",
            buildingNum = "30",
            district = 0,
            hdbTownId = 0,
            postalCode = 648368,
            propertySubType = 4,
            propertySubTypeList = listOf(2, 3, 4),
            propertyType = XValueEnum.PropertyType.HDB.value,
            streetKey = "JUR0057",
            streetName = "Jurong West Street 61"
        )

        val xValueEntryParams = XValueEntryParams(
            propertyType = XValueEnum.PropertyType.HDB,
            propertySubType = ListingEnum.PropertySubType.HDB_5_ROOMS,
            unitFloor = "2",
            unitNumber = "111",
            areaType = null,
            tenure = null,
            builtYear = null,
            area = 1130,
            areaExt = null,
            areaGfa = null,
            address = address
        )

        XValueActivity.launch(activity, xValueEntryParams)
    }

    fun debugXValueLongGraph(activity: BaseActivity) {
        val address = AddressPropertyTypePO(
            buildingKey = "",
            buildingName = "",
            buildingNum = "808",
            district = 0,
            hdbTownId = 0,
            postalCode = 520808,
            propertySubType = 0,
            propertySubTypeList = listOf(1, 2),
            propertyType = XValueEnum.PropertyType.HDB.value,
            streetKey = "TAM0022",
            streetName = "Tampines Avenue 4"
        )

        val xValueEntryParams = XValueEntryParams(
            propertyType = XValueEnum.PropertyType.HDB,
            propertySubType = ListingEnum.PropertySubType.HDB_3_ROOMS,
            unitFloor = "06",
            unitNumber = "139",
            areaType = null,
            tenure = null,
            builtYear = null,
            area = 73,
            areaExt = null,
            areaGfa = null,
            address = address
        )

        XValueActivity.launch(activity, xValueEntryParams)
    }

    fun debugProjectInfo(activity: BaseActivity) {
        //ProjectInfoActivity.launch(activity, 267) // Has site map
        // ProjectInfoActivity.launch(activity, 3456) // Has floor plan
        ProjectInfoActivity.launch(activity, 83712) // Has planning decision
    }

    fun debugSmsBlast(activity: BaseActivity) {
        val listingPOs = listOf(
            CobrokeSmsListingPO(isMclp = "1", sellerUserId = "479081", listingId = "84932261"),
            CobrokeSmsListingPO(isMclp = "1", sellerUserId = "3177981", listingId = "85077401"),
            CobrokeSmsListingPO(isMclp = "1", sellerUserId = "3177981", listingId = "85077551"),
            CobrokeSmsListingPO(isMclp = "1", sellerUserId = "858542", listingId = "82822582"),
            CobrokeSmsListingPO(isMclp = "1", sellerUserId = "449451", listingId = "84165761")
        )
        SmsBlastActivity.launch(activity, listingPOs)
    }

    fun debugEditWatchlist(activity: Activity) {
        // Valid dummy values:
        // MRTs: 4453,4961,4971
        // Schools: 600622,4717,3784
        // Districts: 1,2,4
        // Hdb towns: 3,4,5

        val criteria = WatchlistPropertyCriteriaPO(
            id = 123,
            name = "Kim Cheng Jit Square",
            location = null,
            type = WatchlistEnum.WatchlistType.LISTINGS.value,
            saleType = ListingEnum.OwnershipType.RENT.value,
            radius = 2100,
            priceMin = 300000,
            priceMax = 700000,
            typeOfArea = WatchlistEnum.AreaType.STRATA.value,
            tenure = TransactionEnum.TenureType.LEASEHOLD.value,
            sizeMin = null,
            sizeMax = null,
            psfMin = null,
            psfMax = null,
            roomRental = WatchlistEnum.RentalType.ROOM_RENTAL.value,
            bedrooms = "1,4",
            bathrooms = "2,6+",
            cdResearchSubtypes = "6", // ListingEnum.PropertySubType.CONDOMINIUM.type.toString()
            locationDistricts = null,
            locationHdbTowns = null,
            locationAmenities = null,
            mrts = null,
            newLaunch = null
        )
        EditWatchlistCriteriaActivity.launch(activity, criteria)
    }

    fun debugTransactionsWatchlist(activity: Activity) {
        // Here only care about id, name, location, numRecentItems
        // The rest doesn't matter
        val criteria = WatchlistPropertyCriteriaPO(
            id = 1011,
            name = "Kallang Any",
            location = "Kallang MRT",
            type = WatchlistEnum.WatchlistType.LISTINGS.value,
            saleType = ListingEnum.OwnershipType.RENT.value,
            radius = 2100,
            priceMin = 300000,
            priceMax = 700000,
            typeOfArea = WatchlistEnum.AreaType.STRATA.value,
            tenure = TransactionEnum.TenureType.LEASEHOLD.value,
            sizeMin = null,
            sizeMax = null,
            psfMin = null,
            psfMax = null,
            roomRental = WatchlistEnum.RentalType.ROOM_RENTAL.value,
            bedrooms = "1,4",
            bathrooms = "2,6+",
            cdResearchSubtypes = "6", // ListingEnum.PropertySubType.CONDOMINIUM.type.toString()
            locationDistricts = null,
            locationHdbTowns = null,
            locationAmenities = null,
            mrts = null,
            numRecentItems = 25,
            newLaunch = null
        )
        TransactionsWatchlistActivity.launchByCriteria(activity, criteria)
    }

    fun debugListingsWatchlist(activity: Activity) {
        // Here only care about id, name, location, numRecentItems
        // The rest doesn't matter
        val criteria = WatchlistPropertyCriteriaPO(
            id = 1061,
            name = "Yokohama",
            location = null,
            type = WatchlistEnum.WatchlistType.LISTINGS.value,
            saleType = ListingEnum.OwnershipType.RENT.value,
            radius = 2100,
            priceMin = 300000,
            priceMax = 700000,
            typeOfArea = WatchlistEnum.AreaType.STRATA.value,
            tenure = TransactionEnum.TenureType.LEASEHOLD.value,
            sizeMin = null,
            sizeMax = null,
            psfMin = null,
            psfMax = null,
            roomRental = WatchlistEnum.RentalType.ROOM_RENTAL.value,
            bedrooms = "1,4",
            bathrooms = "2,6+",
            cdResearchSubtypes = "6", // ListingEnum.PropertySubType.CONDOMINIUM.type.toString()
            locationDistricts = null,
            locationHdbTowns = null,
            locationAmenities = null,
            mrts = null,
            numRecentItems = 623,
            newLaunch = null
        )
        ListingsWatchlistActivity.launchByCriteria(activity, criteria)
    }

    fun debugSponsorListing(activity: BaseActivity) {
        SponsorListingActivity.launch(activity, 88147031)
    }

    fun debugSearchPlanningArea(activity: BaseActivity) {
        SearchPlanningAreasActivity.launchForResult(activity, communityIds = null, requestCode = 10)
    }

    fun debugSubscriptionCreditPackageDetails(activity: BaseActivity) {
        SubscriptionCreditPackageDetailsActivity.launch(
            activity,
            AgentProfileAndCvEnum.PaymentPurpose.SUBSCRIPTION,
            AgentProfileAndCvEnum.SubscriptionCreditSource.NON_SUBSCRIBER_PROMPT
        )
    }
}