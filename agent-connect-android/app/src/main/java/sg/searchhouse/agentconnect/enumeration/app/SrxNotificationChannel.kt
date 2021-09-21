package sg.searchhouse.agentconnect.enumeration.app

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

enum class SrxNotificationChannel(val channelId: String, @StringRes val channelName: Int, @StringRes val description: Int) {
    GENERAL(
        "general",
        R.string.notification_channel_name_general,
        R.string.notification_channel_description_general
    ),
    MARKETING(
        "marketing",
        R.string.notification_channel_name_marketing,
        R.string.notification_channel_description_marketing
    )
}