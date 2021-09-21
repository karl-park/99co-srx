package sg.searchhouse.agentconnect.model.api.cea

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CeaFormSectionPO(
    @SerializedName("sectionTitle")
    val sectionTitle: String,
    @SerializedName("sectionSubtitle")
    val sectionSubtitle: String,
    @SerializedName("rowArray")
    val rowArray: List<CeaFormRowPO>
) : Serializable