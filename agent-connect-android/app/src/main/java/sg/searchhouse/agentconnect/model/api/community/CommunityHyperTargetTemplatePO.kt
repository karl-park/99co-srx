package sg.searchhouse.agentconnect.model.api.community

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.util.PropertyTypeUtil
import java.io.Serializable

/**
 * Doc: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/690290736/Listing+V1+Data+Structures
 */
data class CommunityHyperTargetTemplatePO(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("location")
    var location: String? = null,
    @SerializedName("radius")
    var radius: Int? = null,
    @SerializedName("xvalueMin")
    var xvalueMin: Int? = null,
    @SerializedName("xvalueMax")
    var xvalueMax: Int? = null,
    @SerializedName("capitalGainMin")
    var capitalGainMin: Double? = null,
    @SerializedName("capitalGainMax")
    var capitalGainMax: Double? = null,
    @SerializedName("tenure")
    var tenure: Int? = null,
    @SerializedName("cdResearchSubtypes")
    var cdResearchSubtypes: String? = null
) : Serializable {
    fun isValid(): Boolean {
        return isNameValid() && isLocationValid()
    }

    // Assumption: Residential types only
    fun getPropertyMainType(): ListingEnum.PropertyMainType {
        return PropertyTypeUtil.getPropertyMainType(cdResearchSubtypes ?: "")
            ?: ListingEnum.PropertyMainType.RESIDENTIAL
    }

    fun getPropertySubTypes(): List<ListingEnum.PropertySubType> {
        return PropertyTypeUtil.getPropertySubTypes(cdResearchSubtypes ?: "")
    }

    fun isNameValid(): Boolean {
        return name?.isNotEmpty() == true
    }

    fun isLocationValid(): Boolean {
        return location?.isNotEmpty() == true
    }

    fun getCapitalGainMinTimesHundred(): Int? {
        return capitalGainMin?.run { this * 100 }?.toInt()
    }

    fun getCapitalGainMaxTimesHundred(): Int? {
        return capitalGainMax?.run { this * 100 }?.toInt()
    }
}