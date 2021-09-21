package sg.searchhouse.agentconnect.model.app

import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.util.StringUtil

data class DeviceContact(
    val name: String?,
    val mobileNumber: String?
) {

    fun getMobileNumberInteger(): String? {
        if (mobileNumber != null) {
            val number = StringUtil.getNumbersFromString(mobileNumber.trim())
            return if (mobileNumber.contains(AppConstant.COUNTRY_CODE_SINGAPORE_PLUS)) {
                "+$number"
            } else {
                "${AppConstant.COUNTRY_CODE_SINGAPORE_PLUS}${number}"
            }
        }
        return null
    }
}