package sg.searchhouse.agentconnect.constant

object ApiConstant {

    const val MAX_RESULTS_CONVERSATION_LIST = 10
    const val MAX_RESULTS_AGENT_DIRECTORY = 25
    const val MAX_RESULTS_FIND_MESSAGES = 30 //todo; will change to 50

    //Login result //TODO to refine..
    const val SIGN_IN_STATUS_SUCCESS = 1
    const val SIGN_IN_STATUS_REQUIRE_OTP = 2
    const val SIGN_IN_STATUS_RESET = 3

    //Other user type
    const val SSM_SYSTEM_SOURCE_ID = "1216822"

    // Transactions
    const val PROJECT_COMPLETION_STATUS_UNKNOWN = "Unknown"

    //Login Agent session
    const val SESSION_KEY_LOGIN_AS_AGENT = "userSessionImpersonatedToken"
}