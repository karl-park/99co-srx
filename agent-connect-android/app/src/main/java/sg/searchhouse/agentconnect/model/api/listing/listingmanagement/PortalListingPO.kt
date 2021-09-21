package sg.searchhouse.agentconnect.model.api.listing.listingmanagement

import android.content.Context
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.AppPortalType
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.PortalSyncIndicator
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.NumberUtil
import java.io.Serializable
import java.util.*

/**
 * Doc: https://streetsine.atlassian.net/wiki/spaces/SIN/pages/736362511/Listing+Management+PG+Import+V1+API#ListingManagement(PGImport)V1API-PortalListingPO
 */
class PortalListingPO(
    @SerializedName("id")
    val id: String,
    @SerializedName("portalType")
    val portalType: Int,
    @SerializedName("address")
    val address: String,
    @SerializedName("askingPrice")
    val askingPrice: Int,
    @SerializedName("bedrooms")
    val bedrooms: Int,
    @SerializedName("bathrooms")
    val bathrooms: Int,
    @SerializedName("srxListingId")
    val srxListingId: Int,
    @SerializedName("srxImportedDate")
    val srxImportedDate: Long,
    @SerializedName("srxFolder")
    val srxFolder: String,
    @SerializedName("error")
    val error: String,
    @SerializedName("syncInd")
    var syncInd: Int,
    @SerializedName("portalUrl")
    val portalUrl: String,
    @SerializedName("accountId")
    val accountId: Int // id from PortalAccountPO
) : Serializable {
    fun getPortalListingId(context: Context): String {
        if (!TextUtils.isEmpty(id)) {
            AppPortalType.values().find { it.value == portalType }?.let {
                if (it == AppPortalType.PROPERTY_GURU) {
                    return context.getString(it.portalListingId, id)
                }
            }
        }
        return ""
    }

    fun getPortalTypeEnum(): ListingManagementEnum.PortalType? {
        return ListingManagementEnum.PortalType.values().find { it.value == portalType }
    }

    fun getFormattedAskingPrice(): String {
        return NumberUtil.getFormattedCurrency(askingPrice)
    }

    fun getFormattedSrxImportedDate(context: Context): String {
        if (srxImportedDate > 0) {
            val convertedDateFormatted = DateTimeUtil.convertDateToString(
                Date(srxImportedDate * 1000L),
                DateTimeUtil.FORMAT_DATE_10
            )
            return context.getString(R.string.msg_last_synced, convertedDateFormatted)
        }
        return ""
    }

    fun getPortalListingSyncIndicator(): Boolean {
        return syncInd == PortalSyncIndicator.SYNC_ENABLED.value
    }

    fun isHideSyncIndicator(): Boolean {
        return syncInd == 0
    }

    fun getIdLabel(): String {
        return "ID $id"
    }
}