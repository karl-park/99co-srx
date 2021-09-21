package sg.searchhouse.agentconnect.enumeration.app

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

enum class ClientModeOption(
    val position: Int,
    val value: Int,
    @StringRes val label: Int,
    @StringRes val title: Int
) {
    //TODO: will add srx_chat in future
//    SRX_CHAT(
//        0,
//        R.string.dialog_option_client_srx_chat,
//        R.string.title_client_send_message_via_srx_chat
//    ),
    SMS(0, 3, R.string.dialog_option_client_sms, R.string.title_client_send_message_via_SMS),
    EMAIL(1, 2, R.string.dialog_option_client_email, R.string.title_client_send_message_via_email),
}