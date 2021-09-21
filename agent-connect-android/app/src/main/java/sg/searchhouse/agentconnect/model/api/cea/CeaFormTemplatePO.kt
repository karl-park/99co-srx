package sg.searchhouse.agentconnect.model.api.cea

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CeaFormTemplatePO(
    @SerializedName("formId")
    val formId: Int,
    @SerializedName("formType")
    val formType: Int,
    @SerializedName("formTitle")
    val formTitle: String,
    @SerializedName("agentSections")
    val agentSections: List<CeaFormSectionPO>,
    @SerializedName("estateAgent")
    val estateAgent: CeaFormEstateAgentPO,
    @SerializedName("clients")
    val clients: List<CeaFormClientPO>,
    @SerializedName("mainSections")
    val mainSections: List<CeaFormSectionPO>,
    @SerializedName("detailSections")
    val detailSections: List<CeaFormSectionPO>,
    @SerializedName("termSections")
    val termSections: List<CeaFormSectionPO>,
    @SerializedName("partySections")
    val partySections: List<CeaFormSectionPO>,
    @SerializedName("partyTermSections")
    val partyTermSections: List<CeaFormSectionPO>,
    @SerializedName("confirmationSections")
    val confirmationSections: List<CeaFormSectionPO>,
    @SerializedName("agencyTerms")
    val agencyTerms: List<CeaFormTermPO>,
    @SerializedName("selfDefinedTerms")
    val selfDefinedTerms: List<CeaFormTermPO>
) : Serializable