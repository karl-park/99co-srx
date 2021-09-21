package sg.searchhouse.agentconnect.model.api.cea

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UIColorPO(
    @SerializedName("red")
    val red: Float,
    @SerializedName("green")
    val green: Float,
    @SerializedName("blue")
    val blue: Float,
    @SerializedName("alpha")
    val alpha: Float
) : Serializable