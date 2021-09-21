package sg.searchhouse.agentconnect.model.api.project

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.model.api.common.NameValuePO
import sg.searchhouse.agentconnect.model.api.common.SrxDate
import sg.searchhouse.agentconnect.model.api.listing.AmenitiesGroupPO
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.PropertyTypeUtil
import java.io.Serializable

data class SRXProjectDetailsPO(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("tenure")
    val tenure: String,
    @SerializedName("dateComplete")
    val dateComplete: SrxDate?,
    @SerializedName("dateTop")
    val dateTop: SrxDate?,
    @SerializedName("developer")
    val developer: String,
    @SerializedName("noOfUnits")
    val noOfUnits: Int,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("district")
    val district: Int,
    @SerializedName("distanceFrCity")
    val distanceFrCity: Double,
    @SerializedName("plotRatio")
    val plotRatio: Double,
    @SerializedName("address")
    val address: String,
    @SerializedName("postalCode")
    val postalCode: String,
    @SerializedName("propertySubtype")
    val propertySubtype: Int,
    @SerializedName("propertySubtypeDescription")
    val propertySubtypeDescription: String,
    @SerializedName("districtHdbTown")
    val districtHdbTown: String,
    @SerializedName("bedrooms")
    val bedrooms: List<SRXProjectBedroomPO>,
    @SerializedName("fixturesRemarks")
    val fixturesRemarks: List<NameValuePO>,
    @SerializedName("facilities")
    val facilities: List<NameValuePO>,
    @SerializedName("primaryAmenitiesGroups")
    val primaryAmenitiesGroups: List<AmenitiesGroupPO>,
    @SerializedName("projectImageLinks")
    val projectImageLinks: List<PhotoPO>,
    @SerializedName("floorplans")
    val floorplans: List<FloorplanPhotoPO>
) : Serializable {

    fun getCompletedYear(): String {
        val timeStamp = dateComplete?.time ?: return ""
        return DateTimeUtil.convertTimeStampToString(
            timeStamp,
            DateTimeUtil.FORMAT_YEAR
        )
    }

    fun getPropertySubTypeObject(): ListingEnum.PropertySubType {
        return PropertyTypeUtil.getPropertySubType(propertySubtype)
            ?: throw IllegalArgumentException("Missing/invalid `propertySubType` in `SRXProjectDetailsPO`. Value = $propertySubtype")
    }

    fun getDateTopYear(): String {
        val timeStamp = dateTop?.time ?: return ""
        return DateTimeUtil.convertTimeStampToString(
            timeStamp,
            DateTimeUtil.FORMAT_YEAR
        )
    }
}