package sg.searchhouse.agentconnect.model.api.lookup

import com.google.gson.annotations.SerializedName

data class LookupSingaporeDistrictsResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("singaporeDistricts")
    val singaporeDistricts: ArrayList<Region>
) {
    class Region(
        @SerializedName("regionName")
        val regionName: String,
        @SerializedName("districts")
        val districts: ArrayList<District>,

        var districtIds: ArrayList<Int>,
        var isSelected: Boolean = false
    ) {
        class District(
            @SerializedName("districtId")
            val districtId: Int,
            @SerializedName("generalLocation")
            val generalLocation: String
        ) {
            fun getDistrictDescription(): String {
                return StringBuilder().append("D").append(districtId).append(" - ")
                    .append(generalLocation.replace(",", " / ")).toString()
            }
        }
    }
}