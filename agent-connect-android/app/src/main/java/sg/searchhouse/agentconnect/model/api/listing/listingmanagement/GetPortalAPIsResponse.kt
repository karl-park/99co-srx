package sg.searchhouse.agentconnect.model.api.listing.listingmanagement

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.PortalMode
import java.io.Serializable
import java.lang.IllegalArgumentException

data class GetPortalAPIsResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("mode")
    val mode: String,
    @SerializedName("syncOptions")
    val syncOptions: List<SyncData>,
    @SerializedName("syncFrequencies")
    val syncFrequencies: List<SyncData>,
    @SerializedName("templates")
    val templates: Templates
) : Serializable {
    data class Templates(
        @SerializedName("logout")
        val logout: TemplateHolder,
        @SerializedName("listing")
        val listing: TemplateHolder,
        @SerializedName("activeListings")
        val activeListings: List<TemplateHolder>,
        @SerializedName("login")
        val login: TemplateHolder,
        @SerializedName("agent")
        val agent: TemplateHolder?
    ) : Serializable {
        data class TemplateHolder(
            @SerializedName("api")
            val api: String,
            @SerializedName("apiPlaceholders")
            val apiPlaceholders: Map<String, String>?,
            @SerializedName("httpMethod")
            val httpMethod: String,
            @SerializedName("name")
            val name: String,
            @SerializedName("params")
            var params: HashMap<String?, String>?,
            @SerializedName("headers")
            val headers: Map<String, String>?
        ) : Serializable
    }

    data class SyncData(
        @SerializedName("name")
        val name: String,
        @SerializedName("value")
        val value: Int,
        @SerializedName("description")
        val description: String
    ) : Serializable

    fun getPortalMode(): PortalMode {
        return PortalMode.values().find { it.value == mode }
            ?: throw IllegalArgumentException("Undefined Portal Mode")
    }
}
