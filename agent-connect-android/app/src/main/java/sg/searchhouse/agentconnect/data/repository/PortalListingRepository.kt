package sg.searchhouse.agentconnect.data.repository

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import sg.searchhouse.agentconnect.data.datasource.PortalDataSource
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortalListingRepository @Inject constructor(
    private val srxDataSource: SrxDataSource,
    private val portalDataSource: PortalDataSource
) {

    //PORTAL IMPORT -> Server login
    fun getPortalAPIs(): Call<GetPortalAPIsResponse> {
        return srxDataSource.getPortalAPIs()
    }

    fun serverLoginPortal(loginRequest: PortalLoginRequest): Call<PortalLoginResponse> {
        return srxDataSource.serverLoginPortal(loginRequest)
    }

    fun serverLogoutPortal(portalType: Int): Call<DefaultResultResponse> {
        return srxDataSource.serverLogoutPortal(PortalLoggedOutRequest(portalType))
    }

    fun getPortalListings(portalType: Int): Call<PortalLoginResponse> {
        return srxDataSource.getPortalListings(portalType)
    }

    fun importPortalListing(portalListingPO: PortalListingPO): Call<DefaultResultResponse> {
        return srxDataSource.importPortalListing(portalListingPO)
    }

    fun clientLoginPortal(
        username: String,
        password: String,
        portalType: Int
    ): Call<ClientLoginResponse> {
        return srxDataSource.clientLoginPortal(PortalLoginRequest(username, password, portalType))
    }

    fun clientGetListings(
        portalType: Int,
        rawListings: List<JsonObject>
    ): Call<PortalLoginResponse> {
        return srxDataSource.clientGetListings(ClientGetListingsRequest(portalType, rawListings))
    }

    fun clientImportListing(
        portalType: Int,
        rawListings: Any,
        accountId: Int? = null
    ): Call<DefaultResultResponse> {
        return srxDataSource.clientImportListing(
            ClientImportListingRequest(
                portalType,
                rawListings,
                accountId
            )
        )
    }

    fun updatePortalAccount(portalAccountPO: PortalAccountPO): Call<UpdatePortalAccountResponse> {
        return srxDataSource.updatePortalAccount(portalAccountPO)
    }

    fun toggleListingsSync(portalListings: List<PortalListingPO>): Call<DefaultResultResponse> {
        return srxDataSource.toggleListingsSync(portalListings)
    }

    fun clientGetListingsAuto(
        portalType: Int,
        rawAgentData: JsonObject,
        rawListings: List<JsonObject>
    ): Call<PortalGetListingsAutoResponse> {
        return srxDataSource.clientGetListingsAuto(
            PortalClientGetListingsAutoRequest(
                portalType,
                rawAgentData,
                rawListings
            )
        )
    }

    fun serverGetListingsAuto(
        portalType: Int
    ): Call<PortalGetListingsAutoResponse> {
        return srxDataSource.getListingsAuto(portalType = portalType)
    }

    //Calling client APIs
    //TODO: need to refine concurrent hash map in coming phase 1.9
    fun portalLogin(loginTemplate: GetPortalAPIsResponse.Templates.TemplateHolder): Call<LinkedHashMap<String, Any>> {
        val apiUrl = "${loginTemplate.api}?"
        val param = loginTemplate.params ?: hashMapOf()
        var tempHeaders = ConcurrentHashMap<String, String>()
        loginTemplate.headers?.run { tempHeaders = ConcurrentHashMap(this) }
        return portalDataSource.portalLogin(tempHeaders, apiUrl, param)
    }

    fun getListingsFromPortal(
        headers: Map<String, String>?,
        agentId: String,
        template: GetPortalAPIsResponse.Templates.TemplateHolder
    ): Call<JsonObject> {
        var tempHeaders = ConcurrentHashMap<String, String>()
        headers?.run { tempHeaders = ConcurrentHashMap(this) }
        return portalDataSource.getActiveListingByAgentId(
            tempHeaders,
            template.api,
            agentId
        )
    }

    fun getPortalListing(
        headers: Map<String, String>?,
        inputUrl: String,
        listingId: String
    ): Call<ResponseBody> {
        var tempHeaders = ConcurrentHashMap<String, String>()
        headers?.run { tempHeaders = ConcurrentHashMap(this) }
        val url = inputUrl.replaceFirst("{listingId}", listingId)
        return portalDataSource.getListing(tempHeaders, url)
    }

    fun getPortalAgentInfoByMobile(
        headers: Map<String, String>?,
        inputUrl: String,
        agentMobile: Int
    ): Call<JsonObject> {
        //get headers
        var tempHeaders = ConcurrentHashMap<String, String>()
        headers?.run { tempHeaders = ConcurrentHashMap(this) }
        val url = inputUrl.replaceFirst("{mobile}", agentMobile.toString())
        return portalDataSource.getPortalListingsInfo(headers = tempHeaders, url = url)
    }
}