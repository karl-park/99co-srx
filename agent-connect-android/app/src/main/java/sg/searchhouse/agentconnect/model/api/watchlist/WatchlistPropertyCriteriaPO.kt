package sg.searchhouse.agentconnect.model.api.watchlist

import android.content.Context
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.dsl.getIntList
import sg.searchhouse.agentconnect.dsl.getIntListCount
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.TransactionEnum
import sg.searchhouse.agentconnect.enumeration.app.WatchlistEnum
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.PropertyTypeUtil
import java.io.Serializable

class WatchlistPropertyCriteriaPO(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("location")
    val location: String?,
    @SerializedName("type")
    var type: Int?,
    @SerializedName("saleType")
    val saleType: String,
    @SerializedName("radius")
    val radius: Int?,
    @SerializedName("priceMin")
    val priceMin: Int?,
    @SerializedName("priceMax")
    val priceMax: Int?,
    @SerializedName("typeOfArea")
    val typeOfArea: String?,
    @SerializedName("tenure")
    val tenure: Int?,
    @SerializedName("sizeMin")
    val sizeMin: Int?,
    @SerializedName("sizeMax")
    val sizeMax: Int?,
    @SerializedName("psfMin")
    val psfMin: Double?,
    @SerializedName("psfMax")
    val psfMax: Double?,
    @SerializedName("roomRental")
    val roomRental: Boolean?,
    @SerializedName("newLaunch")
    val newLaunch: Boolean?,
    @SerializedName("readInd")
    val readInd: Boolean? = null,
    @SerializedName("hiddenInd")
    val hiddenInd: Boolean? = null,
    @SerializedName("bedrooms")
    val bedrooms: String?,
    @SerializedName("bathrooms")
    val bathrooms: String?,
    @SerializedName("cdResearchSubtypes")
    val cdResearchSubtypes: String?,
    @SerializedName("locationDistricts")
    val locationDistricts: String?,
    @SerializedName("locationHdbTowns")
    val locationHdbTowns: String?,
    @SerializedName("locationAmenities")
    val locationAmenities: String?,
    @SerializedName("numRecentItems")
    val numRecentItems: Int? = null,
    @SerializedName("mrts")
    val mrts: String? = null,
    @SerializedName("schools")
    val schools: String? = null,
    @SerializedName("locationFullNameList")
    val locationFullNameList: List<String>? = null
) : Serializable {
    @Throws(IllegalArgumentException::class)
    fun getSaleTypeEnum(): ListingEnum.OwnershipType {
        return ListingEnum.OwnershipType.values().find { it.value == saleType }
            ?: throw IllegalArgumentException("Invalid `saleType` ${saleType}!")
    }

    fun getPropertyMainType(): ListingEnum.PropertyMainType? {
        val firstSubType =
            cdResearchSubtypes?.getIntList()?.getOrNull(0)
        return firstSubType?.run { PropertyTypeUtil.getPropertyMainType(this) }
    }

    fun getAreaTypeEnum(): WatchlistEnum.AreaType? {
        return WatchlistEnum.AreaType.values().find { it.value == typeOfArea }
    }

    @Throws(IllegalArgumentException::class)
    fun getTenureTypeEnum(): TransactionEnum.TenureType {
        return TransactionEnum.TenureType.values().find { it.value == tenure }
            ?: throw IllegalArgumentException("Invalid `tenure` ${tenure}!")
    }

    @Throws(IllegalArgumentException::class)
    fun getRentalTypeEnum(): WatchlistEnum.RentalType {
        return WatchlistEnum.RentalType.values().find { it.value == roomRental }
            ?: throw IllegalArgumentException("Invalid `roomRental` ${roomRental}!")
    }

    fun getNumRecentItemsLabel(context: Context): String {
        val numRecentItemsInt = numRecentItems ?: 0
        return when (type) {
            WatchlistEnum.WatchlistType.LISTINGS.value -> {
                when (numRecentItemsInt) {
                    0 -> context.resources.getString(R.string.count_watchlist_recent_listings_zero)
                    else -> context.resources.getQuantityString(
                        R.plurals.count_watchlist_recent_listings,
                        numRecentItemsInt,
                        NumberUtil.formatThousand(numRecentItemsInt)
                    )
                }
            }
            WatchlistEnum.WatchlistType.TRANSACTIONS.value -> {
                when (numRecentItemsInt) {
                    0 -> context.resources.getString(R.string.count_watchlist_recent_transactions_zero)
                    else -> context.resources.getQuantityString(
                        R.plurals.count_watchlist_recent_transactions,
                        numRecentItemsInt,
                        NumberUtil.formatThousand(numRecentItemsInt)
                    )
                }
            }
            else -> ""
        }
    }

    fun getBedroomEnums(): List<WatchlistEnum.BedroomCount> {
        return bedrooms?.split(",")?.mapNotNull { bedroom ->
            WatchlistEnum.BedroomCount.values().find { it.value == bedroom }
        } ?: listOf(WatchlistEnum.BedroomCount.ANY)
    }

    fun getBathroomEnums(): List<WatchlistEnum.BathroomCount> {
        return bathrooms?.split(",")?.mapNotNull { bathroom ->
            WatchlistEnum.BathroomCount.values().find { it.value == bathroom }
        } ?: listOf(WatchlistEnum.BathroomCount.ANY)
    }

    // Label on profile watchlist tag item
    @Throws(IllegalArgumentException::class, NumberFormatException::class)
    fun getProfilePropertyTypeLabel(context: Context): String {
        val subTypes = PropertyTypeUtil.getPropertySubTypes(cdResearchSubtypes ?: "")
        return if (PropertyTypeUtil.isAllResidentialSubTypes(subTypes)) {
            context.getString(ListingEnum.PropertySubType.ALL_PRIVATE_RESIDENTIAL.label)
        } else if (PropertyTypeUtil.isAllCommercialSubTypes(subTypes)) {
            context.getString(R.string.listing_sub_type_all_commercial_complete)
        } else if (subTypes.isNotEmpty()) {
            // Assumption: All sub types belong to same main type
            val subTypeValue = subTypes[0].type
            val mainType = PropertyTypeUtil.getPropertyMainType(subTypeValue)
                ?: throw IllegalArgumentException("Sub type $subTypeValue not belong to any main type!")
            val mainTypeLabel = context.getString(mainType.label)
            val subTypeLabels = if (subTypes.size > 1) {
                context.resources.getQuantityString(
                    R.plurals.profile_watchlist_property_sub_type_count,
                    subTypes.size,
                    subTypes.size.toString()
                )
            } else {
                context.getString(subTypes[0].label)
            }

            if (mainType == ListingEnum.PropertyMainType.RESIDENTIAL) {
                // When all private residential selected, otherwise it would display as "All - All private residential"
                subTypeLabels
            } else {
                "$mainTypeLabel - $subTypeLabels"
            }
        } else {
            context.getString(ListingEnum.PropertyMainType.RESIDENTIAL.label)
        }
    }

    private fun getMrtCount(): Int {
        return mrts?.split(",")?.size ?: 0
    }

    fun hasProfileLocationLabel(context: Context): Boolean {
        return getProfileLocationLabel(context).isNotEmpty()
    }

    fun getProfileLocationLabel(context: Context): String {
        return if (locationFullNameList?.size == 1) {
            locationFullNameList[0]
        } else {
            when {
                location?.isNotEmpty() == true -> {
                    location
                }
                mrts?.isNotEmpty() == true -> {
                    context.resources.getQuantityString(
                        R.plurals.tag_profile_watchlist_mrts,
                        mrts.getIntListCount(),
                        mrts.getIntListCount().toString()
                    )
                }
                locationDistricts?.isNotEmpty() == true -> {
                    locationFullNameList?.joinToString(", ") { it } ?: ""
                }
                locationHdbTowns?.isNotEmpty() == true -> {
                    context.resources.getQuantityString(
                        R.plurals.tag_profile_watchlist_areas,
                        locationHdbTowns.getIntListCount(),
                        locationHdbTowns.getIntListCount().toString()
                    )
                }
                schools?.isNotEmpty() == true -> {
                    context.resources.getQuantityString(
                        R.plurals.tag_profile_watchlist_schools,
                        schools.getIntListCount(),
                        schools.getIntListCount().toString()
                    )
                }
                else -> {
                    ""
                }
            }
        }
    }

    fun getProjectTypeEnum(): WatchlistEnum.ProjectType? {
        return WatchlistEnum.ProjectType.values().find { it.value == newLaunch }
    }

    fun isHidden(): Boolean {
        return hiddenInd == true
    }
}