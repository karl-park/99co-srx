package sg.searchhouse.agentconnect.model.api.auth

import com.google.gson.annotations.SerializedName
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum.*
import java.io.Serializable

data class GetConfigResponse(
    @SerializedName("config")
    val config: Config
) : Serializable {
    data class Config(
        @SerializedName("features")
        val features: Map<String, String>?,
        @SerializedName("srx99CombinedSub")
        val srx99CombinedSub: Boolean,
        @SerializedName("subscriptionType")
        val subscriptionType: String
    ) {
        fun getAccessibilityInd(module: AdvisorModule): AccessibilityInd? {
            features?.run {
                if (this.isNotEmpty() && this.containsKey(module.value)) {
                    return AccessibilityInd.values()
                        .find { it.value == this.getValue(module.value) }
                }
            }
            return AccessibilityInd.YES
        }

        fun isFunctionAccessibility(
            module: AdvisorModule,
            function: InAccessibleFunction
        ): Boolean {
            module.inAccessibleFunctions?.run {
                if (this.isNotEmpty() && this.contains(function)) {
                    //should be false since function includes in inaccessible function list
                    return false
                }
            }
            return true
        }
    }
}