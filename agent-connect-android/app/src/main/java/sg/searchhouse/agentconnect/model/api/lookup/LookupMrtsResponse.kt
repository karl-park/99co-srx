package sg.searchhouse.agentconnect.model.api.lookup

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.util.NumberUtil

data class LookupMrtsResponse(
    @SerializedName("railTransitInformation")
    val railTransitInformation: List<TrainLine>,
    @SerializedName("result")
    val result: String
) {
    data class TrainLine(
        @SerializedName("hexColor")
        val hexColor: String,
        @SerializedName("lineCode")
        val lineCode: String,
        @SerializedName("lineName")
        val lineName: String,
        @SerializedName("stations")
        val stations: List<Station>
    ) {
        data class Station(
            @SerializedName("amenityId")
            val amenityId: Int,
            @SerializedName("stationName")
            val stationName: String
        ) {
            fun stationCode(lineCode: String): Int {
                // Example stationName:
                // Serangoon (NE12, CC13)
                // Jurong East (EW24 NS1)
                // Xilin (DT34 Due 2024)

                val fullCode =
                    stationName.substring(stationName.indexOf("(") + 1, stationName.indexOf(")"))
                val fullCodeArray = fullCode.split(" ", ", ")
                val numberCodeString =
                    fullCodeArray.find { it.contains(lineCode) }?.replace(lineCode, "")

                return try {
                    NumberUtil.toInt(numberCodeString ?: "") ?: -1
                } catch (e: NumberFormatException) {
                    -1
                }
            }
        }
    }
}