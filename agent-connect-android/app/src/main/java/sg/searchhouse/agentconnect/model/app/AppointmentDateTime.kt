package sg.searchhouse.agentconnect.model.app

import sg.searchhouse.agentconnect.util.StringUtil


data class AppointmentDateTime(
    val unixTime: Long,
    val dateString: String,
    val time: String
) {
    fun getFormattedTime(): String {
        //Note: original time format - eg. 11.00 AM, 4:00 PM, etc..
        //but by design requirement, remove minutes if zero. so become 11 AM , 4 PM, etc...
        return StringUtil.removeAll(time, ":00")
    }
}