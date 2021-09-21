package sg.searchhouse.agentconnect.enumeration.app

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

enum class CobrokeDialogOption(val position: Int, @StringRes val label: Int) {
    SRX_CHAT(0, R.string.dialog_option_cobroke_srx_chat),
    PHONE_SMS(1, R.string.dialog_option_cobroke_phone_sms),
    SRX_SMS_BLAST(2, R.string.dialog_option_cobroke_srx_sms_blast)
}