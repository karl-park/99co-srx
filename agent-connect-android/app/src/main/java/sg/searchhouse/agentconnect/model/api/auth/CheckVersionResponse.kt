package sg.searchhouse.agentconnect.model.api.auth

import com.google.gson.annotations.SerializedName

data class CheckVersionResponse(
    @SerializedName("upgrade")
    val upgrade: Upgrade,
    @SerializedName("data")
    val data: String
) {
    data class Upgrade(
        @SerializedName("availableUpgradeVersion")
        val availableUpgradeVersion: String,
        @SerializedName("installedVersion")
        val installedVersion: String,
        @SerializedName("mandatoryUpgrade")
        val mandatoryUpgrade: Boolean,
        @SerializedName("upgradeAvailable")
        val upgradeAvailable: Boolean
    )
}