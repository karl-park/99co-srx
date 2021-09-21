package sg.searchhouse.agentconnect.enumeration.app

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

enum class SettingMenu(val value: Int, @StringRes val label: Int, @DrawableRes val icon: Int) {
    //TODO DO NOT REMOVE comments -> comment them for this phase. will include other menu in next phase
    MY_PROFILE(0, R.string.setting_item_my_profile, 0),
    MY_LISTINGS(1, R.string.setting_item_my_listings, R.drawable.ic_layout),
    MY_CLIENTS(2, R.string.setting_item_my_clients, R.drawable.ic_users),
    NEWS(3, R.string.setting_item_news, R.drawable.ic_file_text),
    CALCULATORS(4, R.string.setting_item_calculators, R.drawable.ic_divide),
    FIND_AGENT(5, R.string.setting_item_find_agent, R.drawable.ic_agent),

    //LOAN_REFERRAL(5, R.string.setting_item_loan_referral, R.drawable.ic_handshake),
    //TRAINING(6, R.string.setting_item_training, R.drawable.ic_teach),
    //HELP_AND_SUPPORT(7, R.string.setting_item_help_and_support, R.drawable.ic_help_circle),
    //INVITE_AGENTS(8, R.string.setting_item_invite_agents, R.drawable.ic_user_plus),
    //FEEDBACK(9, R.string.setting_item_feedback, R.drawable.ic_edit),

    PRIVACY_STATEMENT(10, R.string.setting_item_privacy_statement, R.drawable.ic_shield),
    TERMS_OF_USE(11, R.string.setting_item_terms_of_use, R.drawable.ic_file),
    TERMS_OF_SALE(12, R.string.setting_item_terms_of_sale, R.drawable.ic_tag),
    LOGIN_AS_AGENT(14, R.string.setting_item_login_as_agent, R.drawable.ic_user_plus),
    SWITCH_SERVER(13, R.string.setting_item_switch_server, R.drawable.ic_globe),
    SIGN_OUT(15, R.string.setting_item_sign_out, R.drawable.ic_power)
}