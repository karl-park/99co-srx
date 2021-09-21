package sg.searchhouse.agentconnect.model.api.project

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.util.NumberUtil
import java.io.Serializable

data class SRXProjectBedroomPO(
    @SerializedName("roomType")
    val roomType: String,
    @SerializedName("maxSize")
    val maxSize: Int,
    @SerializedName("minSize")
    val minSize: Int
) : Serializable {
    var isHdb = false
    private fun getFormattedMinSize(): String {
        return getFormattedSize(minSize)
    }

    private fun getFormattedMaxSize(): String {
        return getFormattedSize(maxSize)
    }

    private fun getFormattedSize(size: Int): String {
        val thousandFormatted = NumberUtil.formatThousand(size)
        return when (isHdb) {
            true -> "$thousandFormatted sqm"
            false -> "$thousandFormatted sqft"
        }
    }

    fun getSizeRange(): String {
        return "${getFormattedMinSize()} - ${getFormattedMaxSize()}"
    }
}