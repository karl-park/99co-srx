package sg.searchhouse.agentconnect.model.api.listing

import android.content.Context
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum.OwnershipType
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum.OwnershipType.*
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.PropertyClassification
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.ShowHideXValueIndicator
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PostListingPhotoPO
import sg.searchhouse.agentconnect.util.*
import java.io.Serializable
import java.text.ParseException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

//TODO: refine with post listing
//Note: may refer listing edit po documentation (https://streetsine.atlassian.net/wiki/spaces/SIN/pages/690290736/Listing+V1+Data+Structures)
data class ListingEditPO(
    @SerializedName("id")
    var id: Int? = null,
    //use sale as default value
    @SerializedName("type")
    var type: String = SALE.value,
    @SerializedName("category")
    var category: Int = 0,
    @SerializedName("propertyType")
    var propertyType: Int = 0,
    @SerializedName("unitFloor")
    var unitFloor: String = "",
    @SerializedName("unitNo")
    var unitNo: String = "",
    @SerializedName("builtArea")
    var builtArea: Double = 0.0,
    @SerializedName("landArea")
    var landArea: Double = 0.0,
    @SerializedName("blk")
    var blk: String = "",
    @SerializedName("postalCode")
    var postalCode: Int = 0,
    @SerializedName("roomRental")
    var roomRental: String = "",
    @SerializedName("buildingName")
    var buildingName: String = "",
    @SerializedName("streetName")
    var streetName: String = "",
    @SerializedName("model")
    var model: String = "",
    @SerializedName("tenure")
    var tenure: String = "",
    @SerializedName("propertyCompleteDate")
    var propertyCompleteDate: Long = 0L,
    @SerializedName("bedrooms")
    var bedrooms: Int = 0,
    @SerializedName("xValue")
    var xValue: Int = 0,
    @SerializedName("xValueDisplayInd")
    var xValueDisplayInd: String = "",
    @SerializedName("listedPrice")
    var listedPrice: Int = 0,
    @SerializedName("listedPriceModel")
    var listedPriceModel: String = "",
    @SerializedName("tenanted")
    var tenanted: String = "",
    @SerializedName("tenantedAmount")
    var tenantedAmount: Int = 0,
    @SerializedName("listingHeader")
    var listingHeader: String = "",
    @SerializedName("remarks")
    var remarks: String = "",
    @SerializedName("remarksAgent")
    var remarksAgent: String = "",
    @SerializedName("furnish")
    var furnish: String? = null, //reset applicable
    @SerializedName("furnishLevel")
    var furnishLevel: String? = null, //reset applicable
    @SerializedName("floor")
    var floor: String? = null,  //reset applicable
    @SerializedName("bathroom")
    var bathroom: Int = 0,
    @SerializedName("features")
    var features: ArrayList<String> = arrayListOf(),
    @SerializedName("fixtures")
    var fixtures: ArrayList<String> = arrayListOf(),
    @SerializedName("area")
    var area: ArrayList<String> = arrayListOf(),
    @SerializedName("noFeaturesFixturesAreasInd")
    var noFeaturesFixturesAreasInd: Boolean = false,
    @SerializedName("monthlyUtilitiesCost")
    var monthlyUtilitiesCost: Int = 0,
    @SerializedName("availableDate")
    var availableDate: String = "",
    @SerializedName("leaseTerm")
    var leaseTerm: Int = 0,
    @SerializedName("roomType")
    var roomType: Int? = null,
    @SerializedName("developer")
    var developer: String = "",
    @SerializedName("ownerType")
    var ownerType: Int? = null, //reset applicable
    @SerializedName("videos")
    var videos: ArrayList<ListingVideoPO> = arrayListOf(),
    @SerializedName("quality")
    var quality: Int? = null,
    @SerializedName("address")
    var address: String = "",
    @SerializedName("takeover")
    var takeover: String = "",
    @SerializedName("takeoverAmount")
    var takeoverAmount: Int = 0,
    @SerializedName("photosSubmitted")
    var photosSubmitted: ArrayList<PostListingPhotoPO> = arrayListOf(),
    @SerializedName("floorPlanPhotosSubmitted")
    var floorPlanPhotosSubmitted: ArrayList<PostListingPhotoPO> = arrayListOf(),
    @SerializedName("projectPhotos")
    var projectPhotos: ArrayList<PostListingPhotoPO> = arrayListOf(),
    @SerializedName("group")
    var group: Int? = null,
    @SerializedName("remoteOption")
    var remoteOption: ListingRemoteOptionPO? = null,

    var isAcknowledged: Boolean = false
) : Serializable {
    fun getQualityString(): String {
        return quality?.toString() ?: ""
    }

    //get listing information
    fun getListingName(): String {
        if (!TextUtils.isEmpty(address)) {
            return address
        } else {
            var listingName = ""
            //to show block number for HDB, don't need to show for the rest property types
            if (!TextUtils.isEmpty(blk)) {
                if (PropertyTypeUtil.isHDB(propertyType)) {
                    if (TextUtils.isEmpty(model) || !StringUtil.equals(
                            model,
                            ListingEnum.Model.WALK_UP_APT.model,
                            true
                        )
                    ) {
                        listingName += "Blk $blk "
                    }
                }
            } //end of block
            //Building name or street name
            if (!TextUtils.isEmpty(buildingName)) {
                listingName += buildingName
            } else if (!TextUtils.isEmpty(streetName)) {
                listingName += streetName
            }
            //postal code
            if (postalCode > 0) {
                listingName += " ($postalCode)"
            }

            return listingName
        }
    }

    fun getListingInformation(context: Context): String {
        val combinationArray: ArrayList<String> = ArrayList()

        if (category > 0) {
            PropertyClassification.values().find { it.value == category }?.let {
                combinationArray.add(context.resources.getString(it.label))
            }
        }

        if (propertyType > 0) {
            ListingEnum.PropertySubType.values().find { it.type == propertyType }?.let {
                combinationArray.add(context.resources.getString(it.label))
            }
        }

        if (propertyCompleteDate > 0) {
            val year = DateTimeUtil.convertDateToString(
                Date(propertyCompleteDate * 1000L),
                DateTimeUtil.FORMAT_YEAR
            )
            if (year.toInt() >= DateTimeUtil.getCurrentYear()) {
                combinationArray.add("$year (TOP)")
            } else {
                combinationArray.add("$year (Built)")
            }
        }

        if (!TextUtils.isEmpty(tenure) && NumberUtil.isNaturalNumber(tenure)) {
            ListingEnum.Tenure.values().find { it.value == tenure.toInt() }?.let {
                combinationArray.add(context.resources.getString(it.labelFull))
            }
        }

        if (!TextUtils.isEmpty(model)) {
            combinationArray.add(model)
        }

        return combinationArray.joinToString(" • ")
    }

    fun getSize(): String {
        var size = ""
        size += when {
            PropertyTypeUtil.isLanded(propertyType) -> {
                if (landArea > 0) {
                    "${NumberUtil.formatThousand(landArea.roundToInt())} sqft"
                } else {
                    ""
                }
            }
            PropertyTypeUtil.isHDB(propertyType) -> {
                if (builtArea > 0) {
                    "${NumberUtil.formatThousand(builtArea.roundToInt())} sqm"
                } else {
                    ""
                }
            }
            else -> {
                if (builtArea > 0) {
                    "${NumberUtil.formatThousand(builtArea.roundToInt())} sqft"
                } else {
                    ""
                }
            }
        }
        return size
    }

    fun getFormattedXValue(): String {
        if (xValue > 0) {
            return NumberUtil.getFormattedCurrency(xValue)
        }
        return ""
    }

    fun getPropertyClassificationLabel(): Int {
        val classification =
            PropertyClassification.values().find { category == it.value }
        classification?.let {
            return it.label
        }
        return 0
    }

    fun getPropertyTypeLabel(): Int {
        val propertySubType = ListingEnum.PropertySubType.values().find { propertyType == it.type }
        propertySubType?.let {
            return it.label
        }
        return 0
    }

    fun getTenureLabel(): Int {
        if (NumberUtil.isNaturalNumber(tenure)) {
            val propertyTenure =
                ListingManagementEnum.CreateListingTenure.values()
                    .find { it.value == tenure.toInt() }
            propertyTenure?.let {
                return it.label
            }
        }
        return 0
    }

    fun getCompletionYear(): String {
        if (propertyCompleteDate > 0) {
            return DateTimeUtil.convertDateToString(
                Date(propertyCompleteDate * 1000L),
                DateTimeUtil.FORMAT_YEAR
            )
        }
        return ""
    }

    fun isSale(): Boolean {
        return type == SALE.value
    }

    fun isRoomRental(): Boolean {
        return type == RENT.value && roomRental == "true"
    }

    fun getFormattedAvailableDate(context: Context): String {
        var formattedDate = ""
        if (!TextUtils.isEmpty(availableDate)) {
            try {
                formattedDate = DateTimeUtil.convertDateToString(
                    DateTimeUtil.convertStringToDate(
                        availableDate,
                        DateTimeUtil.FORMAT_DATE_2
                    ), DateTimeUtil.FORMAT_DATE
                )
            } catch (e: ParseException) {
                ErrorUtil.handleError(context, R.string.exception_parse, e)
            }
        }
        return formattedDate
    }

    fun isCommercial(): Boolean {
        return PropertyClassification.COMMERCIAL_INDUSTRIAL.value == category
    }

    fun getBathroomString(context: Context): String {
        return if (bathroom > 0) {
            bathroom.toString()
        } else {
            context.getString(R.string.label_select)
        }
    }

    fun getVideoUrl(): String {
        if (videos.isNotEmpty()) {
            val videoPO = videos[0]
            return videoPO.url
        }
        return ""
    }

    fun hasXValue(): Boolean {
        return xValue > 0
    }

    fun getSelectedPriceModel(): String {
        if (listedPriceModel == "undefined") {
            return ""
        }
        return listedPriceModel
    }

    fun showXValueInSearch(): Boolean {
        return ShowHideXValueIndicator.values().find { it.value == xValueDisplayInd }?.booleanValue
            ?: return false
    }

    fun getRoomTypeDescription(context: Context): String {
        ListingManagementEnum.RoomType.values()
            .find { it.value == roomType }?.label?.let { label -> return context.getString(label) }
            ?: return ""
    }

    fun getLeaseTermDescription(context: Context): String {
        return ListingManagementEnum.LeaseTerm.values()
            .find { it.value == leaseTerm }?.label?.let { label -> context.getString(label) } ?: ""
    }

    fun showRentalType(): Boolean {
        return !isSale() && isCommercial()
    }

    fun getRentalTypeDescription(context: Context): String {
        return ListingManagementEnum.RentalType.values()
            .find { it.value == roomRental }?.label?.let { label -> return context.getString(label) }
            ?: ""
    }

    fun getSelectedTakeoverAmount(context: Context): String {
        var takeoverDescription = ""
        if (!TextUtils.isEmpty(takeover)) {
            //could be take over and non takeover
            val selectedTakeOver =
                ListingManagementEnum.AssignmentType.values().find { takeover == it.value }

            selectedTakeOver?.run { takeoverDescription = context.getString(this.label) }

            if (selectedTakeOver == ListingManagementEnum.AssignmentType.TAKEOVER) {
                if (takeoverAmount in 1 until NumberUtil.MAX_INT) {
                    takeoverDescription += " ${NumberUtil.getFormattedCurrency(takeoverAmount)}"
                }
            }
        }

        return takeoverDescription
    }

    fun isShowTakeOverAmount(isExpand: Boolean): Boolean {
        return takeover == ListingManagementEnum.AssignmentType.TAKEOVER.value && isExpand
    }

    fun getSelectedTenancyAmount(context: Context): String {
        var tenancyDescription = ""
        if (!TextUtils.isEmpty(tenanted)) {
            val selectedTenanted =
                ListingManagementEnum.TenancyStatus.values().find { tenanted == it.value }

            selectedTenanted?.run { tenancyDescription = context.getString(this.label) }

            if (selectedTenanted == ListingManagementEnum.TenancyStatus.TENANTED) {
                if (tenantedAmount in 1 until NumberUtil.MAX_INT) {
                    tenancyDescription += " ${NumberUtil.getFormattedCurrency(tenantedAmount)}"
                }
            }
        }
        return tenancyDescription
    }

    fun isShowTenantedAmount(isExpandTenancyStatus: Boolean): Boolean {
        return tenanted == ListingManagementEnum.TenancyStatus.TENANTED.value && isExpandTenancyStatus
    }

    fun getVideoViewingIndicator(): Boolean {
        return remoteOption?.videoCallInd ?: false
    }

    fun isDraft(): Boolean {
        return group == ListingManagementEnum.ListingGroup.DRAFTS.value || group == null
    }

    fun getOwnershipType(): OwnershipType? {
        return when (type) {
            SALE.value -> SALE
            RENT.value -> {
                when (roomRental) {
                    "true" -> ROOM_RENTAL
                    "false" -> RENT
                    else -> null
                }
            }
            else -> null
        }
    }
}
