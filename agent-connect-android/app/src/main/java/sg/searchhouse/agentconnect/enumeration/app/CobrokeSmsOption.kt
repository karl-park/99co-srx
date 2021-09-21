package sg.searchhouse.agentconnect.enumeration.app

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

enum class CobrokeSmsOption(val position: Int, @StringRes val label: Int) {
    MESSAGES(0, R.string.dialog_option_cobroke_phone_sms_messages),
    MESSENGER(1, R.string.dialog_option_cobroke_phone_sms_messenger),
    HANGOUTS(2, R.string.dialog_option_cobroke_phone_sms_hangouts),
    WHATSAPP(3, R.string.dialog_option_cobroke_phone_sms_whatsapp)
}