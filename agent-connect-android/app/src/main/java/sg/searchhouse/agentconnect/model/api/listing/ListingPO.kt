package sg.searchhouse.agentconnect.model.api.listing

import android.content.Context
import android.text.TextUtils
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.enumeration.api.LocationEnum
import sg.searchhouse.agentconnect.model.api.agent.AgentPO
import sg.searchhouse.agentconnect.model.api.community.CommunityPostListingSponsoredPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalListingPO
import sg.searchhouse.agentconnect.util.*
import java.io.Serializable
import kotlin.math.roundToInt

/**
 * Doc: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/690290736/Listing+V1+Data+Structures
 */
open class ListingPO(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("encryptedId")
    val encryptedId: String = "",
    @SerializedName("type")
    val type: String = "",
    @SerializedName("agentPO")
    val agentPO: AgentPO? = null,
    @SerializedName("askingPrice")
    val askingPrice: String = "",
    @SerializedName("askingPriceModel")
    val askingPriceModel: String = "",
    @SerializedName("psf")
    val psf: String = "",
    @SerializedName("rooms")
    val rooms: String = "",
    @SerializedName("bathroom")
    val bathroom: String = "",
    @SerializedName("address")
    val address: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("cdResearchType")
    val cdResearchType: Int = 0,
    @SerializedName("cdResearchSubType")
    val cdResearchSubType: Int = 0,
    @SerializedName("propertyType")
    val propertyType: String = "",
    @SerializedName("model")
    val model: String = "",
    @SerializedName("listingHeader")
    val listingHeader: String = "",
    @SerializedName("remarks")
    val remarks: String = "",
    @SerializedName("remarksAgent")
    val remarksAgent: String = "",
    @SerializedName("builtAreaSizeSqm")
    val builtAreaSizeSqm: Double = 0.0,
    @SerializedName("builtAreaSizeSqft")
    val builtAreaSizeSqft: Double = 0.0,
    @SerializedName("builtAreaPricePsf")
    val builtAreaPricePsf: Double = 0.0,
    @SerializedName("landAreaSizeSqm")
    val landAreaSizeSqm: Double = 0.0,
    @SerializedName("landAreaSizeSqft")
    val landAreaSizeSqft: Double = 0.0,
    @SerializedName("landAreaPricePsf")
    val landAreaPricePsf: Double = 0.0,
    @SerializedName("xListingInd")
    val xListingInd: Boolean = false,
    @SerializedName("xListingPendingInd")
    val xListingPendingInd: Boolean = false,
    @SerializedName("pcCode")
    val pcCode: String = "",
    @SerializedName("quality")
    val quality: String = "",
    @SerializedName("block")
    val block: String = "",
    @SerializedName("postalCode")
    val postalCode: String = "",
    @SerializedName("fuzzyPostalCode")
    val fuzzyPostalCode: String = "",
    @SerializedName("latitude")
    val latitude: String = "",
    @SerializedName("longitude")
    val longitude: String = "",
    @SerializedName("postalDistrictId")
    val postalDistrictId: Int = 0,
    @SerializedName("postalHdbTownId")
    val postalHdbTownId: Int = 0,
    @SerializedName("builtYear")
    val builtYear: String = "",
    @SerializedName("tenure")
    val tenure: String = "",
    @SerializedName("tenureCode")
    val tenureCode: Int = 0,
    @SerializedName("furnish")
    val furnish: String = "",
    @SerializedName("actualDatePosted")
    val actualDatePosted: String = "",
    @SerializedName("dateFirstPosted")
    val dateFirstPosted: String = "",
    @SerializedName("leaseTerm")
    val leaseTerm: Int = 0,
    @SerializedName("ownerCertifiedInd")
    val ownerCertifiedInd: Boolean = false,
    @SerializedName("virtualTour")
    val virtualTour: ListingVirtualTourPO? = null,
    @SerializedName("videoPOs")
    val videoPOs: List<ListingVideoPO> = listOf(),
    @SerializedName("droneViews")
    val droneViews: List<DroneViewPO> = listOf(),
    @SerializedName("listingPhoto")
    val listingPhoto: String = "",
    @SerializedName("exclusive")
    val exclusive: String = "",
    @SerializedName("featured")
    val featured: FeaturedPO? = null,
    @SerializedName("floor")
    val floor: String = "",
    @SerializedName("similarListings")
    val similarListings: List<ListingPO> = listOf(),
    @SerializedName("listingType")
    val listingType: String = "",
    @SerializedName("newLaunchInd")
    val newLaunchInd: Boolean = false,
    @SerializedName("distanceDesc2")
    val distanceDesc2: String? = null,
    @SerializedName("liveInd")
    val liveInd: Boolean? = false,
    @SerializedName("xValueDisplayInd")
    val xValueDisplayInd: String = "",
    @SerializedName("portalListings")
    val portalListings: List<PortalListingPO> = emptyList(),
    @SerializedName("isFLSlotAvailable")
    val isFLSlotAvailable: Boolean = false,
    @SerializedName("dateExp")
    val dateExp: String? = null,
    @SerializedName("unitFloor")
    val unitFloor: String? = null,
    @SerializedName("unitNumber")
    val unitNumber: String? = null,
    @SerializedName("group")
    val group: Int? = null,
    @SerializedName("nonQualityPhotoCount")
    val nonQualityPhotoCount: Int? = null,
    @SerializedName("remoteOption")
    val remoteOption: ListingRemoteOptionPO? = null,
    @SerializedName("status")
    val status: String = "",
    @SerializedName("sponsored")
    val sponsored: CommunityPostListingSponsoredPO? = null
) : Serializable {

    fun getOwnershipType(): ListingEnum.OwnershipType? {
        return ListingEnum.OwnershipType.values().find {
            it.value == type
        }
    }

    fun getListingName(): String {
        var listingName = ""
        //to show block number for HDB, don't need to show for the rest property types
        if (!TextUtils.isEmpty(block)) {
            if (PropertyTypeUtil.isHDB(cdResearchSubType)) {
                if (TextUtils.isEmpty(model) || !StringUtil.equals(
                        model,
                        ListingEnum.Model.WALK_UP_APT.model,
                        true
                    )
                ) {
                    listingName += "Blk $block "
                }
            }
        } //end of block

        if (!TextUtils.isEmpty(name)) {
            listingName += name
        } else if (!TextUtils.isEmpty(address)) {
            listingName += address
        }

        return listingName
    }

    private fun getListingNameForOwnListing(): String {
        var listingName = ""

        if (!TextUtils.isEmpty(block)) {
            //block number is not empty
            val blockNumber = if (PropertyTypeUtil.isLanded(cdResearchSubType)) {
                block
            } else {
                "Blk $block"
            }

            when {
                !TextUtils.isEmpty(name) && name.contains(block) -> {
                    listingName += if (!PropertyTypeUtil.isLanded(cdResearchSubType)) {
                        "Blk $name"
                    } else {
                        name
                    }
                }
                !TextUtils.isEmpty(name) && !name.contains(block) -> {
                    listingName += "$blockNumber $name"
                }
                !TextUtils.isEmpty(address) -> {
                    listingName += "$blockNumber $address"
                }
            }
        } else {
            //block number is empty
            if (!TextUtils.isEmpty(name)) {
                listingName += name
            } else if (!TextUtils.isEmpty(address)) {
                listingName += address
            }
        }

        return listingName
    }

    open fun getListingNameForMyListing(): String {
        val listingName = getListingNameForOwnListing()
        return if (!TextUtils.isEmpty(unitFloor) && !TextUtils.isEmpty(unitNumber)) {
            "$listingName #$unitFloor-$unitNumber"
        } else {
            listingName
        }
    }

    // TODO Use list or string concat instead
    fun getDisplaySizePsf(): String {
        var list = emptyList<String>()

        // For size
        getDisplaySize()?.run { list = list + this }

        //for psf
        getFormattedPsf()?.run { list = list + this }

        return list.joinToString(" | ")
    }

    fun getPropertySubTypeDisplaySize(context: Context): String {
        var list = emptyList<String>()
        val mainType = PropertyTypeUtil.getPropertyMainType(cdResearchSubType)
            ?.run { context.getString(label) }
        val subType =
            PropertyTypeUtil.getPropertySubType(cdResearchSubType)?.run { context.getString(label) }
        subType?.run {
            val type = "$mainType $subType".trim()
            list = list + type
        }
        getDisplaySize()?.run { list = list + this }
        return list.joinToString(" | ")
    }

    private fun getDisplaySize(): String? {
        return if (PropertyTypeUtil.isHDB(cdResearchSubType)) {
            val sizeNum = builtAreaSizeSqm.roundToInt()
            if (sizeNum > 0) {
                "${NumberUtil.formatThousand(sizeNum)} sqm"
            } else {
                null
            }
        } else if (PropertyTypeUtil.isLanded(cdResearchSubType) && getOwnershipType() == ListingEnum.OwnershipType.SALE) {
            //for landed property, if it is for rent, then show built size & built psf
            val sizeNum = landAreaSizeSqft.roundToInt()
            if (sizeNum > 0) {
                "${NumberUtil.formatThousand(sizeNum)} sqft"
            } else {
                null
            }
        } else {
            val sizeNum = builtAreaSizeSqft.roundToInt()
            if (sizeNum > 0) {
                "${NumberUtil.formatThousand(sizeNum)} sqft"
            } else {
                null
            }
        }
    }

    private fun getFormattedPsf(): String? {
        return if (PropertyTypeUtil.isLanded(cdResearchSubType) && getOwnershipType() == ListingEnum.OwnershipType.SALE) {
            val psfRounded = landAreaPricePsf.roundToInt()
            if (psfRounded > 0) {
                "\$${NumberUtil.formatThousand(psfRounded)} psf"
            } else {
                null
            }
        } else {
            val psfRounded = when (getOwnershipType()) {
                ListingEnum.OwnershipType.SALE -> builtAreaPricePsf.roundToInt()
                ListingEnum.OwnershipType.RENT -> (builtAreaPricePsf * 100).roundToInt() / 100
                else -> (builtAreaPricePsf * 100).roundToInt() / 100
            }
            if (psfRounded > 0) {
                "\$${NumberUtil.formatThousand(psfRounded)} psf"
            } else {
                null
            }
        }
    }

    fun getSpreadsheetPsf(): String {
        val builtPsf = getPsfNumberByOwnershipType(builtAreaPricePsf)
        return when {
            PropertyTypeUtil.isLanded(cdResearchSubType) -> {
                val landPsf = getPsfNumberByOwnershipType(landAreaPricePsf)
                "\$${builtPsf} (B) | \$${landPsf} (L)"
            }
            else -> {
                "\$${builtPsf} (B)"
            }
        }
    }

    fun getSpreadsheetBuiltSize(): String {
        val builtSqft = NumberUtil.roundDoubleString(builtAreaSizeSqft, 0)
        val landSqft = NumberUtil.roundDoubleString(landAreaSizeSqft, 0)
        val builtSqm = NumberUtil.roundDoubleString(builtAreaSizeSqm, 0)

        return when {
            PropertyTypeUtil.isLanded(cdResearchSubType) -> {
                when {
                    builtAreaSizeSqft > 0.0 && landAreaSizeSqft > 0.0 -> "\$${builtSqft}sqft (B) | \$${landSqft}sqft (L)"
                    builtAreaSizeSqft <= 0.0 && landAreaSizeSqft < 0.0 -> ""
                    builtAreaSizeSqft <= 0.0 -> "\$${landSqft}sqft (L)"
                    landAreaSizeSqft <= 0.0 -> "\$${builtSqft}sqft (B)"
                    else -> ""
                }
            }
            PropertyTypeUtil.isHDB(cdResearchSubType) -> {
                when {
                    builtAreaSizeSqm > 0.0 -> "\$${builtSqm}sqm (B)"
                    else -> ""
                }
            }
            else -> {
                when {
                    builtAreaSizeSqft > 0.0 -> "\$${builtSqft}sqft (B)"
                    else -> ""
                }
            }
        }
    }

    private fun getPsfNumberByOwnershipType(psf: Double): String {
        return when (getOwnershipType()) {
            ListingEnum.OwnershipType.SALE -> NumberUtil.roundDoubleString(psf, 0)
            else -> NumberUtil.roundDoubleString(psf, 2)
        }
    }

    @Throws(NumberFormatException::class)
    fun getFormattedAskingPrice(): String {
        //check number string or not first
        var formattedAskingPrice = ""
        if (NumberUtil.isNaturalNumber(askingPrice)) {
            formattedAskingPrice = if (!TextUtils.isEmpty(askingPrice)) {
                NumberUtil.getFormattedCurrency(askingPrice.toInt())
            } else {
                "View to offer"
            }
        }
        return formattedAskingPrice
    }

    fun getListingImageUrl(context: Context): String {
        //if agent purchase drone videos -> show drone image
        //if agent purchase virtual tour videos -> show v360 image
        //if agent has only listing photo -> show normal listing photo
        var imageUrl = ""
        var hasDroneOrV360 = false
        //for x drone
        if (droneViews.isNotEmpty()) {
            droneViews.forEach {
                imageUrl = it.thumbnailUrl
                hasDroneOrV360 = true
            }
        }

        //for virtual tour
        val virtualTourThumbnail = virtualTour?.thumbnailUrl ?: ""
        if (!TextUtils.isEmpty(virtualTourThumbnail)) {
            imageUrl = virtualTourThumbnail
            hasDroneOrV360 = true
        }

        //if no drone and virtual tour
        if (!hasDroneOrV360) {
            imageUrl = ImageUtil.maybeAppendBaseUrl(context, listingPhoto)
        }
        return imageUrl
    }

    // e.g. 3, 3+1, for display with icon
    open fun getRoomsNumber(): String {
        val roomsValue = rooms.replace(" bedroom", "").trim()
        return if (roomsValue == "0") {
            ""
        } else {
            roomsValue
        }
    }

    open fun getBathroomNumber(): String {
        if (NumberUtil.isNaturalNumber(bathroom)) {
            if (bathroom.toInt() > 0) {
                return bathroom
            }
        }
        return ""
    }

    fun getAskingPriceInt(): Int? {
        return askingPrice.toIntOrNull()
    }

    open fun askingPriceLabel(): String {
        return getAskingPriceInt()?.run {
            NumberUtil.getFormattedCurrency(this)
        } ?: run {
            askingPrice
        }
    }

    open fun askingPriceLabelShortened(): String {
        return getAskingPriceInt()?.run {
            "\$${NumberUtil.getThousandMillionShortForm(this)}"
        } ?: run {
            askingPrice
        }
    }

    private fun getModelType(): String {
        var modelString = ""
        if (!TextUtils.isEmpty(model)) {
            if (!StringUtil.equals(model, propertyType, ignoreCase = true)) {
                modelString = model
            }
        }

        return modelString
    }

    fun getLeaseTermDescription(): String {
        //For RENT
        var leaseTermDescription = ""
        if (leaseTerm == 0) {
            leaseTermDescription = "Flexible"
        } else if (leaseTerm >= 1) {
            leaseTermDescription = "$leaseTerm months"
        }
        return leaseTermDescription
    }

    //added sale listing details
    // TODO: Encode into XML
    private fun getSaleListingDescription(context: Context): String {
        //to show listing details.. for SALE
        val combinationArray: ArrayList<String> = ArrayList()

        //property type
        if (!TextUtils.isEmpty(propertyType)) {
            combinationArray.add(propertyType)
        }
        //model
        if (!TextUtils.isEmpty(getModelType())) {
            combinationArray.add(getModelType())
        }
        //tenure
        if (!TextUtils.isEmpty(tenure) && !PropertyTypeUtil.isHDB(cdResearchSubType)) {
            combinationArray.add(tenure)
        }
        //built year and top
        if (!TextUtils.isEmpty(builtYear)) {
            if (NumberUtil.isNaturalNumber(builtYear)) {
                //if built year is more earlier than current year -> show TOP
                if (builtYear.toInt() >= DateTimeUtil.getCurrentYear()) {
                    combinationArray.add("$builtYear (TOP)")
                } else {
                    combinationArray.add("$builtYear (Built)")
                }
            }
        }

        if (!TextUtils.isEmpty(distanceDesc2)) {
            combinationArray.add(context.getString(R.string.listing_item_distance, distanceDesc2))
        }

        //not sure need to add distance or not in .. detail
        return combinationArray.joinToString(" • ")
    }

    //For rent listing detail
    private fun getRentListingDescription(context: Context): String {

        //to show listing details.. for RENT (Furnishing and lease term)
        val combinationArray: ArrayList<String> = ArrayList()

        //property type
        if (!TextUtils.isEmpty(propertyType)) {
            combinationArray.add(propertyType)
        }
        //model
        if (!TextUtils.isEmpty(getModelType())) {
            combinationArray.add(getModelType())
        }
        //furnish
        if (!TextUtils.isEmpty(furnish)) {
            val selectedFurnish = ListingEnum.Furnish.values().find { it.value == furnish }
            selectedFurnish?.let {
                combinationArray.add(context.resources.getString(it.label))
            }
        }
        //lease term
        if (!TextUtils.isEmpty(leaseTerm.toString())) {
            combinationArray.add(getLeaseTermDescription())
        }
        //built year and top
        if (!TextUtils.isEmpty(builtYear)) {
            if (NumberUtil.isNaturalNumber(builtYear)) {
                //if built year is more earlier than current year -> show TOP
                if (builtYear.toInt() >= DateTimeUtil.getCurrentYear()) {
                    combinationArray.add("$builtYear (TOP)")
                } else {
                    combinationArray.add("$builtYear (Built)")
                }
            }
        }

        if (!TextUtils.isEmpty(distanceDesc2)) {
            combinationArray.add(context.getString(R.string.listing_item_distance, distanceDesc2))
        }

        //not sure need to add distance or not in .. detail
        return combinationArray.joinToString(" • ")
    }

    fun getListingDescription(context: Context): String {
        return when (getOwnershipType()) {
            ListingEnum.OwnershipType.SALE -> getSaleListingDescription(context)
            ListingEnum.OwnershipType.RENT -> getRentListingDescription(context)
            else -> ""
        }
    }

    open fun hasDroneView(): Boolean {
        return droneViews.isNotEmpty()
    }

    open fun hasVirtualTour(): Boolean {
        return virtualTour != null && !TextUtils.isEmpty(virtualTour.url)
    }

    fun hasClassified(): Boolean {
        return !TextUtils.isEmpty(pcCode)
    }

    fun getDistrictHdbTownLabel(context: Context): String {
        return if (PropertyTypeUtil.isHDB(cdResearchSubType)) {
            R.string.label_listing_details_hdb_town
        } else {
            R.string.label_listing_details_district
        }.let {
            context.getString(it)
        }
    }

    fun getBuiltYearLabel(context: Context): String {
        val builtYearInt = NumberUtil.toInt(builtYear)
            ?: return context.getString(R.string.listing_label_built_year)
        return when {
            builtYearInt >= DateTimeUtil.getCurrentYear() -> R.string.listing_label_top
            else -> R.string.listing_label_built_year
        }.let {
            context.getString(it)
        }
    }

    private fun getHdbTownName(context: Context): String {
        return LocationEnum.HdbTown.values().find { it.id == postalHdbTownId }?.let {
            context.getString(it.label)
        } ?: ""
    }

    private fun getDistrictName(context: Context): String {
        return LocationEnum.District.values().find { it.id == postalDistrictId }?.let {
            context.getString(it.label)
        } ?: ""
    }

    private fun getShortDistrictName(): String {
        return LocationEnum.District.values().find { it.id == postalDistrictId }?.name ?: ""
    }

    fun getDistrictHdbTownName(context: Context): String {
        return if (PropertyTypeUtil.isHDB(cdResearchSubType)) {
            getHdbTownName(context)
        } else {
            getDistrictName(context)
        }
    }

    // Same as getDistrictHdbTownName except district in short form e.g. `D1` instead of e.g. `D1 - Tanjung Pagar`
    private fun getShortDistrictHdbTownName(context: Context): String {
        return if (PropertyTypeUtil.isHDB(cdResearchSubType)) {
            getHdbTownName(context)
        } else {
            getShortDistrictName()
        }
    }

    fun getFloorName(context: Context): String {
        return ListingEnum.Floor.values().find {
            StringUtil.equals(
                it.value,
                floor,
                true
            )
        }?.let {
            context.getString(it.label)
        } ?: ""
    }

    fun getFurnishName(context: Context): String {
        return ListingEnum.Furnish.values().find {
            StringUtil.equals(
                it.value,
                furnish,
                true
            )
        }?.label?.let {
            context.getString(it)
        } ?: ""
    }

    fun getAgentPhoto(): String {
        return agentPO?.photo ?: ""
    }

    fun getAgentName(): String {
        return agentPO?.name ?: ""
    }

    // Return combo of listing ID and type
    open fun getListingIdType(): String {
        return "$id,$listingType"
    }

    // Whether to show `featured` label
    fun isFeatured(): Boolean {
        return featured?.category ?: 0 > 0
    }

    fun hasId(listingId: String, listingType: String): Boolean {
        return TextUtils.equals(this.id, listingId) && TextUtils.equals(
            this.listingType,
            listingType
        )
    }

    fun getLatLng(): LatLng? {
        val latitudeDouble = latitude.toDoubleOrNull() ?: return null
        val longitudeDouble = longitude.toDoubleOrNull() ?: return null
        return LatLng(latitudeDouble, longitudeDouble)
    }

    private fun getFullAddress(): String {
        var addressString = ""

        if (!TextUtils.isEmpty(block)) {
            val isHdb = PropertyTypeUtil.isHDB(cdResearchSubType)
            val isCondo = PropertyTypeUtil.isCondo(cdResearchSubType)

            if (isHdb || isCondo) {
                if (TextUtils.isEmpty(model) || !StringUtil.equals(
                        model,
                        ListingEnum.Model.WALK_UP_APT.model,
                        ignoreCase = true
                    )
                ) {
                    addressString += "$block "
                }
            }
        }

        if (!TextUtils.isEmpty(address)) {
            addressString += address
        }

        return addressString
    }

    private fun getFullAddressWithPostalCode(): String {
        var addressString = getFullAddress()
        if (!TextUtils.isEmpty(addressString)) {
            if (!TextUtils.isEmpty(postalCode)) {
                addressString += " ($postalCode)"
            }
        }
        return addressString
    }

    // With postal code, only for HDB and condo
    fun getFullAddressMaybeWithPostalCode(): String {
        // TODO: Refactor
        val isHdb = ListingEnum.PropertyMainType.HDB.propertySubTypes.any {
            it.type == cdResearchSubType
        }

        val isCondo = ListingEnum.PropertyMainType.CONDO.propertySubTypes.any {
            it.type == cdResearchSubType
        }

        return if (isHdb || isCondo) {
            getFullAddressWithPostalCode()
        } else {
            getFullAddress()
        }
    }

    fun getListingDetailTitle(context: Context): String {
        var title = getListingName()

        val districtHdbTownName = getShortDistrictHdbTownName(context)

        if (!TextUtils.isEmpty(districtHdbTownName)) {
            title += " ($districtHdbTownName)"
        }

        val propertyTypeDescription =
            PropertyTypeUtil.getPropertyTypeDescriptionResId(cdResearchSubType)
                ?.run { context.getString(this) }

        if (!TextUtils.isEmpty(model)) {
            title += ", $model"
        } else {
            if (!TextUtils.isEmpty(propertyTypeDescription)) {
                title += ", $propertyTypeDescription"
            }
        }

        return title
    }

    fun getListingTypeId(): String {
        return "$listingType$id"
    }

    //backend return "Yes" "No" for exclusive
    open fun isExclusive(): Boolean {
        if (!TextUtils.isEmpty(exclusive)) {
            if (StringUtil.equals(exclusive, "Yes", ignoreCase = true)) {
                return true
            } else if (StringUtil.equals(exclusive, "No", ignoreCase = true)) {
                return false
            }
        }
        return false
    }

    // To determine the height of tag layout in listings list and grid w.r.t. number of tags
    fun isTagsMultiline(): Boolean {
        var numberOfTags = 0
        if (isExclusive()) numberOfTags++
        if (hasDroneView()) numberOfTags++
        if (hasVirtualTour()) numberOfTags++
        if (isFeatured()) numberOfTags++
        return numberOfTags > 2
    }

    fun isListingIdType(listingId: String, listingType: String): Boolean {
        return StringUtil.equals(this.id, listingId, ignoreCase = true) &&
                StringUtil.equals(this.listingType, listingType, ignoreCase = true)
    }

    fun getDateExpMyListingFooterLabel(context: Context): String {
        return dateExp?.run {
            context.getString(
                R.string.label_my_listing_expiry,
                DateTimeUtil.getConvertedFormatDate(
                    this,
                    DateTimeUtil.FORMAT_DATE_3,
                    DateTimeUtil.FORMAT_DATE_4
                )
            )
        } ?: ""
    }

    fun getDateExpMyListingPostedDateLabel(context: Context): String {
        return context.getString(
            R.string.label_my_listing_posted, DateTimeUtil.getConvertedFormatDate(
                actualDatePosted,
                DateTimeUtil.FORMAT_DATE_3,
                DateTimeUtil.FORMAT_DATE_4
            )
        )
    }

    fun isOwnListing(): Boolean {
        val loggedInUserId = SessionUtil.getCurrentUser()?.id ?: return false
        return loggedInUserId == agentPO?.userId
    }

    fun isPublicListing(): Boolean {
        return listingType == ListingEnum.ListingType.PUBLIC_LISTING.value
    }

    fun isCommercialListing(): Boolean {
        return PropertyTypeUtil.isCommercial(cdResearchSubType)
    }

    fun getNonQualityPhotoCountString(): String {
        return if (nonQualityPhotoCount == null || nonQualityPhotoCount == 0) {
            ""
        } else {
            "$nonQualityPhotoCount"
        }
    }

    fun getNonQualityPicturesLabel(context: Context): String {
        return if (nonQualityPhotoCount == null || nonQualityPhotoCount == 0) {
            ""
        } else {
            context.resources.getQuantityString(
                R.plurals.label_non_quality_photo_pictures,
                nonQualityPhotoCount
            )
        }
    }

    fun allowVideoViewing(): Boolean {
        return remoteOption?.videoCallInd ?: false
    }

    fun getFormattedActualDatePosted(context: Context): String {
        val date = DateTimeUtil.convertStringToDate(actualDatePosted, DateTimeUtil.FORMAT_DATE_3)
        val now = DateTimeUtil.getNow()
        val isToday = DateTimeUtil.isSameDay(date.time, now.time)
        val days = DateTimeUtil.getDaysBetweenDates(date, now)

        return when {
            days < 0 -> {
                // Not supposed to happen, leave blank
                ""
            }
            isToday -> {
                context.getString(R.string.label_today)
            }
            days < 30 -> {
                context.getString(R.string.days_ago_short, NumberUtil.formatThousand(days))
            }
            days < 365 -> {
                val months = DateTimeUtil.getMonthsBetweenDates(date, now)
                context.getString(R.string.months_ago_short, NumberUtil.formatThousand(months))
            }
            else -> {
                val years = DateTimeUtil.getYearsBetweenDates(date, now)
                context.getString(R.string.years_ago_short, NumberUtil.formatThousand(years))
            }
        }
    }

    fun getListingStatus(): ListingEnum.ListingStatus {
        return ListingEnum.ListingStatus.values().find { it.value == status }
            ?: ListingEnum.ListingStatus.NOT_APPLICABLE
    }

    fun getPropertySubType(): ListingEnum.PropertySubType? {
        return ListingEnum.PropertySubType.values().find { it.type == cdResearchSubType }
    }

    fun getPropertySubTypeLabel(context: Context): String? {
        return getPropertySubType()
            ?.run { context.getString(label) }
    }

    fun getPGListing(): PortalListingPO? {
        return portalListings.find { it.getPortalTypeEnum() == ListingManagementEnum.PortalType.PROPERTY_GURU }
    }

    fun getNinetyNineListing(): PortalListingPO? {
        return portalListings.find { it.getPortalTypeEnum() == ListingManagementEnum.PortalType.NINETY_NINE_CO }
    }
}