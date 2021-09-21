package sg.searchhouse.agentconnect.model.api.lookup

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.HashMap

data class LookupModelsResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("models")
    val models: HashMap<Int, ArrayList<String>>
)