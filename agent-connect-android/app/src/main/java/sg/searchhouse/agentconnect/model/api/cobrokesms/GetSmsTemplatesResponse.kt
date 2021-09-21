package sg.searchhouse.agentconnect.model.api.cobrokesms

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetSmsTemplatesResponse(
    @SerializedName("templates")
    val templates: List<TemplatesItem>,
    @SerializedName("getSmsTemplates")
    val getSmsTemplates: Boolean = false
): Serializable {
    data class TemplatesItem(
        @SerializedName("message")
        val message: String = "",
        @SerializedName("templateId")
        val templateId: String = "",
        @SerializedName("title")
        val title: String = "",
        @SerializedName("seq")
        val seq: String = ""
    )
}