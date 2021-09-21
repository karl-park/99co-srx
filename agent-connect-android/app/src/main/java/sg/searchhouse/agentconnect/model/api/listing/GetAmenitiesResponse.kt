package sg.searchhouse.agentconnect.model.api.listing

import com.google.gson.annotations.SerializedName

data class GetAmenitiesResponse(
    @SerializedName("amenities")
    val amenities: Amenities,
    @SerializedName("result")
    val result: String
) {
    data class Amenities(
        @SerializedName("groups")
        val groups: List<Group> = listOf()
    ) {
        data class Group(
            @SerializedName("elements")
            val elements: List<Element> = listOf(),
            @SerializedName("name")
            val name: String
        ) {
            data class Element(
                @SerializedName("amenities")
                val amenities: List<Amenity> = listOf(),
                @SerializedName("name")
                val name: String
            ) {
                data class Amenity(
                    @SerializedName("cdAmenitiesDescription")
                    val cdAmenitiesDescription: String,
                    @SerializedName("cdAmenityId")
                    val cdAmenityId: Int = 0,
                    @SerializedName("description")
                    val description: String,
                    @SerializedName("dgpRegion")
                    val dgpRegion: String,
                    @SerializedName("distance")
                    val distance: String,
                    @SerializedName("district")
                    val district: Int = 0,
                    @SerializedName("id")
                    val id: Int = 0,
                    @SerializedName("keyWord")
                    val keyWord: String,
                    @SerializedName("latitude")
                    val latitude: String,
                    @SerializedName("lineShortName")
                    val lineShortName: String,
                    @SerializedName("longitude")
                    val longitude: String,
                    @SerializedName("medianPsfs")
                    val medianPsfs: List<Any> = listOf(),
                    @SerializedName("name")
                    val name: String,
                    @SerializedName("shortName")
                    val shortName: String,
                    @SerializedName("type")
                    val type: Int = 0,
                    @SerializedName("url")
                    val url: String,
                    @SerializedName("virtualInd")
                    val virtualInd: String
                )
            }
        }
    }
}