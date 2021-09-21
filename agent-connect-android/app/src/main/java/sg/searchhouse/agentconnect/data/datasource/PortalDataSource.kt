package sg.searchhouse.agentconnect.data.datasource

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

interface PortalDataSource {

    //Note: By requirement of portal data source, appends headers which are given from backend
    @FormUrlEncoded
    @POST
    fun portalLogin(
        @HeaderMap headers: ConcurrentHashMap<String, String>,
        @Url url: String,
        @FieldMap params: HashMap<String?, String>
    ): Call<LinkedHashMap<String, Any>>

    @GET
    fun getActiveListingByAgentId(
        @HeaderMap headers: ConcurrentHashMap<String, String>,
        @Url url: String,
        @Query("agent_id") agent_id: String
    ): Call<JsonObject>

    @GET
    fun getListing(
        @HeaderMap headers: ConcurrentHashMap<String, String>,
        @Url url: String
    ): Call<ResponseBody>

    @GET
    fun getPortalListingsInfo(
        @HeaderMap headers: ConcurrentHashMap<String, String>,
        @Url url: String
    ): Call<JsonObject>
}