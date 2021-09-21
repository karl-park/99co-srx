package sg.searchhouse.agentconnect.util

import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.event.app.LaunchNonSubscriberPromptEvent

object AuthUtil {

    private const val SAM_EMAIL = "sam.baker@streetsine.com"
    private const val JEREMY_EMAIL = "jeremy.lee@streetsine.com"

    fun isStreetSineUser(email: String): Boolean {
        return email.contains("@streetsine.com") &&
                !StringUtil.equals(email, SAM_EMAIL, ignoreCase = true) &&
                !StringUtil.equals(email, JEREMY_EMAIL, ignoreCase = true)
    }

    fun isAccessibilityModule(
        module: AccessibilityEnum.AdvisorModule,
        function: AccessibilityEnum.InAccessibleFunction? = null
    ): Boolean {
        val configValue = SessionUtil.getSubscriptionConfigOrNull() ?: return true
        return when (configValue.getAccessibilityInd(module)) {
            AccessibilityEnum.AccessibilityInd.YES -> true
            AccessibilityEnum.AccessibilityInd.NO -> false
            AccessibilityEnum.AccessibilityInd.LIMITED -> {
                //Noted : there are inAccessible function list in each module.
                //when backend return limited means some functions are not accessible.
                // so check function is included in inAccessible list or not
                val tempFunction = function ?: return true
                return configValue.isFunctionAccessibility(module, tempFunction)
            }
            else -> return false
        }
    }

    private fun getAccessibilityInd(module: AccessibilityEnum.AdvisorModule): AccessibilityEnum.AccessibilityInd? {
        val configValue = SessionUtil.getSubscriptionConfigOrNull() ?: return null
        return configValue.getAccessibilityInd(module)
    }

    fun checkModuleAccessibility(
        module: AccessibilityEnum.AdvisorModule,
        function: AccessibilityEnum.InAccessibleFunction? = null,
        onSuccessAccessibility: (() -> Unit)? = null,
        onFailAccessibility: (() -> Unit)? = null,
        onLimitedAccessibility: (() -> Unit)? = null,
        allowDismiss: Boolean = true
    ) {
        when (getAccessibilityInd(module) ?: AccessibilityEnum.AccessibilityInd.NO) {
            AccessibilityEnum.AccessibilityInd.YES -> {
                onSuccessAccessibility?.invoke()
            }
            AccessibilityEnum.AccessibilityInd.NO -> {
                onFailAccessibility?.run {
                    invoke()
                } ?: showNonSubscriberPrompt(allowDismiss = allowDismiss, module = module)
            }
            AccessibilityEnum.AccessibilityInd.LIMITED -> {
                // !!: `configValue` is guaranteed not null here in `getAccessibilityInd` method
                // Because if null, it will end up in `AccessibilityInd.NO` case
                val configValue = SessionUtil.getSubscriptionConfigOrNull()!!
                function?.run {
                    if (configValue.isFunctionAccessibility(module, this)) {
                        onSuccessAccessibility?.invoke()
                    } else {
                        onFailAccessibility?.run {
                            invoke()
                        } ?: showNonSubscriberPrompt(allowDismiss = allowDismiss, module = module)
                        // TODO Identify `InAccessibleFunction` in `showNonSubscriberPrompt` method?
                    }
                } ?: onLimitedAccessibility?.run { invoke() } ?: onSuccessAccessibility?.invoke()
            }
        }
    }

    fun showNonSubscriberPrompt(allowDismiss: Boolean, module: AccessibilityEnum.AdvisorModule) {
        RxBus.publish(LaunchNonSubscriberPromptEvent(allowDismiss, module))
    }

}