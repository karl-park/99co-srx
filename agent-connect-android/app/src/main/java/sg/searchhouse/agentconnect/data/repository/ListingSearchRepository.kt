package sg.searchhouse.agentconnect.data.repository

import android.text.TextUtils
import retrofit2.Call
import sg.searchhouse.agentconnect.constant.AppConstant.BATCH_SIZE_LISTINGS
import sg.searchhouse.agentconnect.constant.AppConstant.DEFAULT_BATCH_SIZE
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum.*
import sg.searchhouse.agentconnect.model.api.listing.*
import sg.searchhouse.agentconnect.model.api.listing.search.ExportListingsRequest
import sg.searchhouse.agentconnect.model.api.listing.search.ExportListingsResponse
import sg.searchhouse.agentconnect.model.api.listing.search.FindListingsCountResponse
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.StringUtil
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API:             https://streetsine.atlassian.net/wiki/spaces/SIN/pages/691208209/Listing+Search+V1+API
 * Data structure:  https://streetsine.atlassian.net/wiki/spaces/SIN/pages/690290736/Listing+V1+Data+Structures
 */
@Singleton
class ListingSearchRepository @Inject constructor(private val srxDataSource: SrxDataSource) {

    @Throws(IllegalArgumentException::class)
    fun getListing(listingId: String, listingType: String): Call<GetListingResponse> {
        val listingIdInt = if (NumberUtil.isNaturalNumber(listingId)) {
            NumberUtil.toInt(listingId)
        } else {
            // Handle legacy
            val modified = listingId.replace("A", "").replace("P", "")
            NumberUtil.toInt(modified)
        } ?: throw IllegalArgumentException("Invalid listing ID")

        require(ListingType.values().any {
            TextUtils.equals(
                it.value,
                listingType
            )
        }) { "Invalid listing type" }

        return srxDataSource.getListing(listingIdInt.toString(), listingType)
    }

    // Used in listings activity
    fun findListings(
        searchText: String?,
        startResultIndex: Int,
        type: OwnershipType,
        propertyPurpose: PropertyPurpose,
        orderCriteria: OrderCriteria,
        isTransacted: Boolean,
        propertyMainType: PropertyMainType?,
        propertySubTypes: String?,
        bedroomCounts: String?,
        bathroomCounts: String?,
        propertyAge: PropertyAge?,
        tenures: String?,
        floors: String?,
        furnishes: String?,
        amenitiesIds: String?,
        districtIds: String?,
        hdbTownIds: String?,
        minDateFirstPosted: MinDateFirstPosted?,
        hasVirtualTours: Boolean?,
        hasDroneViews: Boolean?,
        ownerCertification: Boolean?,
        exclusiveListing: Boolean?,
        xListingPrice: Boolean?,
        minPrice: Int?,
        maxPrice: Int?,
        minPSF: Int?,
        maxPSF: Int?,
        minBuiltSize: Int?,
        maxBuiltSize: Int?,
        minLandSize: Int?,
        maxLandSize: Int?,
        minBuiltYear: Int?,
        maxBuiltYear: Int?,
        projectLaunchStatus: ProjectLaunchStatus?,
        isIncludeNearBy: Boolean,
        isProjectLaunchStatusApplicable: Boolean,
        rentalType: RentalType?,
        seed: String?
    ): Call<FindListingsResponse> {
        return findListingsCore(
            searchText,
            startResultIndex,
            BATCH_SIZE_LISTINGS,
            type,
            propertyPurpose,
            orderCriteria,
            isTransacted,
            propertyMainType,
            propertySubTypes,
            bedroomCounts,
            bathroomCounts,
            propertyAge,
            tenures,
            floors,
            furnishes,
            amenitiesIds,
            districtIds,
            hdbTownIds,
            minDateFirstPosted,
            hasVirtualTours,
            hasDroneViews,
            ownerCertification,
            exclusiveListing,
            xListingPrice,
            minPrice,
            maxPrice,
            minPSF,
            maxPSF,
            minBuiltSize,
            maxBuiltSize,
            minLandSize,
            maxLandSize,
            minBuiltYear,
            maxBuiltYear,
            projectLaunchStatus,
            isIncludeNearBy,
            isProjectLaunchStatusApplicable,
            rentalType,
            seed
        )
    }

    // Used in filter listings activity
    fun findListingsCount(
        searchText: String?,
        type: OwnershipType,
        propertyPurpose: PropertyPurpose,
        isTransacted: Boolean,
        propertyMainType: PropertyMainType?,
        propertySubTypes: String?,
        bedroomCounts: String?,
        bathroomCounts: String?,
        propertyAge: PropertyAge?,
        tenures: String?,
        floors: String?,
        furnishes: String?,
        amenitiesIds: String?,
        districtIds: String?,
        hdbTownIds: String?,
        minDateFirstPosted: MinDateFirstPosted?,
        hasVirtualTours: Boolean?,
        hasDroneViews: Boolean?,
        ownerCertification: Boolean?,
        exclusiveListing: Boolean?,
        xListingPrice: Boolean?,
        minPrice: Int?,
        maxPrice: Int?,
        minPSF: Int?,
        maxPSF: Int?,
        minBuiltSize: Int?,
        maxBuiltSize: Int?,
        minLandSize: Int?,
        maxLandSize: Int?,
        minBuiltYear: Int?,
        maxBuiltYear: Int?,
        projectLaunchStatus: ProjectLaunchStatus?,
        isIncludeNearBy: Boolean,
        isProjectLaunchStatusApplicable: Boolean,
        rentalType: RentalType?
    ): Call<FindListingsResponse> {
        return findListingsCore(
            searchText,
            0,
            0,
            type,
            propertyPurpose,
            OrderCriteria.DEFAULT,
            isTransacted,
            propertyMainType,
            propertySubTypes,
            bedroomCounts,
            bathroomCounts,
            propertyAge,
            tenures,
            floors,
            furnishes,
            amenitiesIds,
            districtIds,
            hdbTownIds,
            minDateFirstPosted,
            hasVirtualTours,
            hasDroneViews,
            ownerCertification,
            exclusiveListing,
            xListingPrice,
            minPrice,
            maxPrice,
            minPSF,
            maxPSF,
            minBuiltSize,
            maxBuiltSize,
            minLandSize,
            maxLandSize,
            minBuiltYear,
            maxBuiltYear,
            projectLaunchStatus,
            isIncludeNearBy,
            isProjectLaunchStatusApplicable,
            rentalType,
            null
        )
    }

    // Used in listings activity
    private fun findListingsCore(
        searchText: String?,
        startResultIndex: Int,
        maxResults: Int?,
        type: OwnershipType,
        propertyPurpose: PropertyPurpose,
        orderCriteria: OrderCriteria,
        isTransacted: Boolean,
        propertyMainType: PropertyMainType?,
        propertySubTypes: String?,
        bedroomCounts: String?,
        bathroomCounts: String?,
        propertyAge: PropertyAge?,
        tenures: String?,
        floors: String?,
        furnishes: String?,
        amenitiesIds: String?,
        districtIds: String?,
        hdbTownIds: String?,
        minDateFirstPosted: MinDateFirstPosted?,
        hasVirtualTours: Boolean?,
        hasDroneViews: Boolean?,
        ownerCertification: Boolean?,
        exclusiveListing: Boolean?,
        xListingPrice: Boolean?,
        minPrice: Int?,
        maxPrice: Int?,
        minPSF: Int?,
        maxPSF: Int?,
        minBuiltSize: Int?,
        maxBuiltSize: Int?,
        minLandSize: Int?,
        maxLandSize: Int?,
        minBuiltYear: Int?,
        maxBuiltYear: Int?,
        projectLaunchStatus: ProjectLaunchStatus?,
        isIncludeNearBy: Boolean,
        isProjectLaunchStatusApplicable: Boolean,
        rentalType: RentalType?,
        seed: String?
    ): Call<FindListingsResponse> {
        val wantedListingGroupsString = getWantedListingGroups(
            searchText = searchText,
            districtIds = districtIds,
            hdbTownIds = hdbTownIds,
            isIncludeNearBy = isIncludeNearBy,
            isIncludeListingFeatures = isFilteredByListingFeatures(
                hasVirtualTours,
                hasDroneViews,
                ownerCertification,
                exclusiveListing,
                xListingPrice
            )
        ).joinToString(",") { it.value }

        val cdResearchSubTypesString = if (propertySubTypes == null) {
            val thisPropertyMainType: PropertyMainType =
                propertyMainType ?: when (propertyPurpose) {
                    PropertyPurpose.RESIDENTIAL -> PropertyMainType.RESIDENTIAL
                    PropertyPurpose.COMMERCIAL -> PropertyMainType.COMMERCIAL
                }
            thisPropertyMainType.propertySubTypes.map { it.type }.joinToString(",")
        } else {
            val thisPropertySubTypes = propertySubTypes.split(",")
            val isAllCommercial = thisPropertySubTypes.size == 1 && TextUtils.equals(
                thisPropertySubTypes[0],
                "${PropertySubType.ALL_COMMERCIAL.type}"
            )
            if (isAllCommercial) {
                PropertyMainType.COMMERCIAL.propertySubTypes.map {
                    it.type
                }.joinToString(",")
            } else {
                thisPropertySubTypes.filter {
                    val allCommercial = "${PropertySubType.ALL_COMMERCIAL.type}"
                    !TextUtils.equals(it, allCommercial)
                }.joinToString(",")
            }
        }

        val orderCriteriaString = orderCriteria.value

        val bedrooms = bedroomCounts?.split(",")?.filter {
            !TextUtils.equals(it, BedroomCount.ANY.value.toString()) &&
                    !TextUtils.equals(it, BedroomCount.SIX_AND_ABOVE.value.toString())
        }?.joinToString(",")?.let {
            if (!TextUtils.isEmpty(it)) {
                it
            } else {
                null
            }
        }

        val minBedrooms = bedroomCounts?.split(",")?.find {
            TextUtils.equals(it, BedroomCount.SIX_AND_ABOVE.value.toString())
        }?.let { NumberUtil.toInt(it) }

        val bathrooms = bathroomCounts?.split(",")?.filter {
            !TextUtils.equals(it, BathroomCount.ANY.value.toString()) &&
                    !TextUtils.equals(it, BathroomCount.SIX_AND_ABOVE.value.toString())
        }?.joinToString(",")?.let {
            if (!TextUtils.isEmpty(it)) {
                it
            } else {
                null
            }
        }

        val minBathrooms = bathroomCounts?.split(",")?.find {
            TextUtils.equals(it, BathroomCount.SIX_AND_ABOVE.value.toString())
        }?.let { NumberUtil.toInt(it) }

        var localBuiltYearMin: Int? = null
        var localBuiltYearMax: Int? = null

        if (minBuiltYear != null || maxBuiltYear != null) {
            localBuiltYearMin = minBuiltYear
            localBuiltYearMax = maxBuiltYear
        } else {
            propertyAge?.let {
                localBuiltYearMin = DateTimeUtil.getCurrentYear() - it.to
                localBuiltYearMax = DateTimeUtil.getCurrentYear() - it.from
            }
        }

        val minDateFirstPostedValue = minDateFirstPosted?.value?.let { daysFromToday ->
            when {
                daysFromToday >= 0 -> {
                    val startDate = DateTimeUtil.getDateBeforeNow(
                        minDateFirstPosted.value,
                        minDateFirstPosted.unitTime
                    )
                    DateTimeUtil.convertDateToString(
                        startDate.time,
                        DateTimeUtil.FORMAT_DATE_YYYY_MM_DD_HYPHEN
                    )
                }
                else -> null
            }
        }

        var minRentPrice: Int? = null
        var maxRentPrice: Int? = null

        var minSalePrice: Int? = null
        var maxSalePrice: Int? = null

        when (type) {
            OwnershipType.RENT -> {
                minRentPrice = minPrice
                maxRentPrice = maxPrice
            }
            OwnershipType.SALE -> {
                minSalePrice = minPrice
                maxSalePrice = maxPrice
            }
            else -> {
            }
        }

        val projectLaunchStatusValue = if (isProjectLaunchStatusApplicable) {
            projectLaunchStatus?.value
        } else {
            null
        }

        val validatedMinLandSize = if (propertyMainType == PropertyMainType.LANDED) {
            minLandSize
        } else {
            null
        }

        val validatedMaxLandSize = if (propertyMainType == PropertyMainType.LANDED) {
            maxLandSize
        } else {
            null
        }

        return srxDataSource.findListings(
            startResultIndex = startResultIndex,
            maxResults = maxResults ?: BATCH_SIZE_LISTINGS,
            wantedListingGroups = wantedListingGroupsString,
            type = type.value,
            cdResearchSubTypes = cdResearchSubTypesString,
            searchText = StringUtil.getStringIfNotEmpty(searchText),
            orderCriteria = orderCriteriaString,
            isTransacted = isTransacted,
            bedrooms = bedrooms,
            minBedrooms = minBedrooms,
            bathrooms = bathrooms,
            minBathrooms = minBathrooms,
            builtYearMin = localBuiltYearMin,
            builtYearMax = localBuiltYearMax,
            cdTenure = StringUtil.getStringIfNotEmpty(tenures),
            selectedAmenitiesIds = amenitiesIds,
            selectedDistrictIds = districtIds,
            selectedHdbTownIds = hdbTownIds,
            floor = StringUtil.getStringIfNotEmpty(floors),
            furnish = StringUtil.getStringIfNotEmpty(furnishes),
            vt360Filter = hasVirtualTours,
            droneViewFilter = hasDroneViews,
            ownerCertificationFilter = ownerCertification,
            exclusiveFilter = exclusiveListing,
            spvFilter = xListingPrice,
            minDateFirstPosted = minDateFirstPostedValue,
            minRentPrice = minRentPrice,
            maxRentPrice = maxRentPrice,
            minSalePrice = minSalePrice,
            maxSalePrice = maxSalePrice,
            minPSF = minPSF?.toDouble(),
            maxPSF = maxPSF?.toDouble(),
            minBuiltSize = minBuiltSize?.toDouble(),
            maxBuiltSize = maxBuiltSize?.toDouble(),
            minLandSizeSqft = validatedMinLandSize,
            maxLandSizeSqft = validatedMaxLandSize,
            projectLaunchStatus = projectLaunchStatusValue,
            isRoomRental = rentalType?.value,
            seed = seed
        )
    }

    private fun getWantedListingGroups(
        searchText: String?,
        districtIds: String?,
        hdbTownIds: String?,
        isIncludeNearBy: Boolean,
        isIncludeListingFeatures: Boolean
    ): List<ListingGroup> {
        return if (TextUtils.isEmpty(searchText) || !TextUtils.isEmpty(districtIds) || !TextUtils.isEmpty(
                hdbTownIds
            )
        ) {
            if (isIncludeListingFeatures) {
                //by requirement, if filter by one of listing features, show only srx listing,
                listOf(ListingGroup.SRX_STP)
            } else {
                listOf(ListingGroup.SRX_STP, ListingGroup.TABLE_LP)
            }

        } else {
            if (isIncludeNearBy) {
                listOf(
                    ListingGroup.MCLP_ALL_MATCH_SEARCH_TERM,
                    ListingGroup.MCLP_ALL_NEARBY,
                    ListingGroup.TABLE_LP
                )
            } else {
                listOf(
                    ListingGroup.MCLP_ALL_MATCH_SEARCH_TERM,
                    ListingGroup.TABLE_LP
                )
            }
        }
    }

    private fun isFilteredByListingFeatures(
        hasVirtualTours: Boolean?,
        hasDroneViews: Boolean?,
        ownerCertification: Boolean?,
        exclusiveListing: Boolean?,
        xListingPrice: Boolean?
    ): Boolean {
        return hasVirtualTours == true || hasDroneViews == true
                || ownerCertification == true || exclusiveListing == true
                || xListingPrice == true
    }

    // For use of smart filter number labels
    // TODO: It is repetition of findListingsCore calling findListingsCount, refactor when have time
    fun findListingsCountHeader(
        searchText: String?,
        type: OwnershipType,
        propertyMainType: PropertyMainType,
        propertySubTypes: String? = null,
        bedroomCounts: String? = null,
        propertyAge: PropertyAge? = null,
        tenures: String? = null,
        amenitiesIds: String?,
        districtIds: String?,
        hdbTownIds: String?,
        projectLaunchStatus: ProjectLaunchStatus?,
        isIncludeNearBy: Boolean,
        isProjectLaunchStatusApplicable: Boolean
    ): Call<FindListingsCountResponse> {
        //note: do need to consider for filtering by listing features since hide smart filter counts if filter
        val wantedListingGroupsString = getWantedListingGroups(
            searchText = searchText,
            districtIds = districtIds,
            hdbTownIds = hdbTownIds,
            isIncludeNearBy = isIncludeNearBy,
            isIncludeListingFeatures = false
        ).joinToString(",") { it.value }

        val cdResearchSubTypesString = propertySubTypes?.split(",")?.filter {
            val allCommercial = "${PropertySubType.ALL_COMMERCIAL.type}"
            !TextUtils.equals(it, allCommercial)
        }?.joinToString(",")
            ?: propertyMainType.propertySubTypes.map { it.type }.joinToString(",")

        val bedrooms = bedroomCounts?.split(",")?.filter {
            !TextUtils.equals(it, BedroomCount.ANY.value.toString()) &&
                    !TextUtils.equals(it, BedroomCount.SIX_AND_ABOVE.value.toString())
        }?.joinToString(",")?.let {
            if (!TextUtils.isEmpty(it)) {
                it
            } else {
                null
            }
        }

        val minBedrooms = bedroomCounts?.split(",")?.find {
            TextUtils.equals(it, BedroomCount.SIX_AND_ABOVE.value.toString())
        }?.let { NumberUtil.toInt(it) }

        var localBuiltYearMin: Int? = null
        var localBuiltYearMax: Int? = null

        propertyAge?.let {
            localBuiltYearMin = DateTimeUtil.getCurrentYear() - it.to
            localBuiltYearMax = DateTimeUtil.getCurrentYear() - it.from
        }

        val projectLaunchStatusValue = if (isProjectLaunchStatusApplicable) {
            projectLaunchStatus?.value
        } else {
            null
        }

        return srxDataSource.findListingsCount(
            wantedListingGroups = wantedListingGroupsString,
            type = type.value,
            cdResearchSubTypes = cdResearchSubTypesString,
            searchText = StringUtil.getStringIfNotEmpty(searchText),
            bedrooms = bedrooms,
            minBedrooms = minBedrooms,
            builtYearMin = localBuiltYearMin,
            builtYearMax = localBuiltYearMax,
            cdTenure = tenures,
            selectedAmenitiesIds = amenitiesIds,
            selectedDistrictIds = districtIds,
            selectedHdbTownIds = hdbTownIds,
            projectLaunchStatus = projectLaunchStatusValue,
            isTransacted = false
        )
    }

    //CV listings
    //TODO move to agent repository
    fun findListingsForCv(
        startIndex: Int,
        ownershipType: OwnershipType,
        filterUserId: Int
    ): Call<FindListingsResponse> {
        val residentialSubTypesString =
            PropertyMainType.RESIDENTIAL.propertySubTypes.map { it.type }.joinToString(",")
        val commercialSubTypesString =
            PropertyMainType.COMMERCIAL.propertySubTypes.map { it.type }.joinToString(",")
        val cdResearchSubTypesString =
            residentialSubTypesString.plus(",").plus(commercialSubTypesString)
        return srxDataSource.findListings(
            startResultIndex = startIndex,
            maxResults = DEFAULT_BATCH_SIZE,
            wantedListingGroups = ListingGroup.SRX_STP.value,
            type = ownershipType.value,
            cdResearchSubTypes = cdResearchSubTypesString,
            filterUserId = filterUserId
        )
    }

    fun findListingsByIds(maxResults: Int, listingIds: String): Call<FindListingsResponse> {
        val wantedListingsGroup =
            listOf(ListingGroup.SRX_STP, ListingGroup.TABLE_LP).joinToString(",") { it.value }
        return srxDataSource.findListingsByIds(
            startResultIndex = 0,
            maxResults = maxResults,
            wantedListingGroups = wantedListingsGroup,
            listingIds = listingIds
        )
    }

    fun getListingLatestTransactions(
        listingPO: ListingPO,
        ownershipType: OwnershipType
    ): Call<GetListingLatestTransactionsResponse> {
        return srxDataSource.getListingLatestTransactions(
            listingPO.id,
            listingPO.postalCode,
            listingPO.cdResearchSubType,
            ownershipType.value
        )
    }

    fun getListingXTrend(listingId: String, listingType: String): Call<GetListingXTrendResponse> {
        return srxDataSource.getListingXTrend(listingId, listingType)
    }

    fun exportListings(
        srxStpListings: List<Int>,
        otherListings: List<Int>,
        withAgentContact: Boolean,
        withPhoto: Boolean
    ): Call<ExportListingsResponse> {
        val exportListingsRequest = ExportListingsRequest(
            srxstp = srxStpListings,
            tableLP = otherListings,
            withAgentContact = withAgentContact,
            withPhoto = withPhoto
        )
        return srxDataSource.exportListings(exportListingsRequest)
    }
}