package sg.searchhouse.agentconnect.model.api.listing

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.common.NameValuePO

data class ListingDetailPO(
    @SerializedName("listingPO")
    val listingPO: ListingPO,
    @SerializedName("developer")
    val developer: String,
    @SerializedName("fuzzyLatitude")
    val fuzzyLatitude: String,
    @SerializedName("fuzzyLongitude")
    val fuzzyLongitude: String,
    @SerializedName("amenitiesCategories")
    val amenitiesCategories: List<AmenitiesCategoryPO>,
    @SerializedName("primaryAmenitiesGroups")
    val primaryAmenitiesGroups: List<AmenitiesGroupPO>,
    @SerializedName("features")
    val features: List<NameValuePO>,
    @SerializedName("fixtures")
    val fixtures: List<NameValuePO>,
    @SerializedName("facilities")
    val facilities: List<NameValuePO>,
    @SerializedName("area")
    val area: List<NameValuePO>
)