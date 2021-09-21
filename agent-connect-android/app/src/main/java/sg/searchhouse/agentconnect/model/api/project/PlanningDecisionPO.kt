package sg.searchhouse.agentconnect.model.api.project

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.model.api.common.SrxDate
import java.io.Serializable

data class PlanningDecisionPO(
    @SerializedName("date")
    val date: SrxDate,
    @SerializedName("type")
    val type: String,
    @SerializedName("typeDescription")
    val typeDescription: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String
) : Serializable {
    fun getDateAndType(): String {
        return "${date.getFormattedDate4()} [$typeDescription]"
    }
}