package sg.searchhouse.agentconnect.enumeration.app

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

class EnquireAgentDialogOption {
    enum class MainOption(val position: Int, @StringRes val label: Int) {
        CHAT(0, R.string.dialog_option_chat),
        CALL_AGENT(1, R.string.dialog_option_call_agent),
        MESSAGE(2, R.string.dialog_option_message),
        WHATSAPP(3, R.string.dialog_option_whatsapp)
    }

    enum class MessageOption(val position: Int, @StringRes val label: Int) {
        TEXT_MESSAGE(0, R.string.dialog_option_text_message),
        APP_ENQUIRY(1, R.string.dialog_option_app_enquiry)
    }
}