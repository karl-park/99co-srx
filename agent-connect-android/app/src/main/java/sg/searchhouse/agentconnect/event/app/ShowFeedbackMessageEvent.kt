package sg.searchhouse.agentconnect.event.app

import androidx.annotation.StringRes

class ShowFeedbackMessageEvent(
    var message: String? = null,
    @StringRes var messageResId: Int? = null
)