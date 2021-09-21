package sg.searchhouse.agentconnect.model.api.xvalue

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetProjectResponse(
    @SerializedName("data")
    val `data`: List<Data>
) : Serializable {
    data class Data(
        @SerializedName("block")
        val block: String,
        @SerializedName("building")
        val building: String,
        @SerializedName("gfa")
        val gfa: Int,
        @SerializedName("lastConstructed")
        val lastConstructed: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("pesSize")
        val pesSize: Int,
        @SerializedName("pesSizeVerified")
        val pesSizeVerified: Boolean,
        @SerializedName("size")
        val size: Int,
        @SerializedName("street")
        val street: String,
        @SerializedName("streetId")
        val streetId: Int,
        @SerializedName("tenure")
        val tenure: String,
        @SerializedName("type")
        val type: Int,
        @SerializedName("typeOfArea")
        val typeOfArea: String
    )
}