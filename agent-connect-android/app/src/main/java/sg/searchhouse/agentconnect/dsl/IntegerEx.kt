package sg.searchhouse.agentconnect.dsl

import sg.searchhouse.agentconnect.util.ViewUtil

// TODO Cast to `StringRes` only
fun Int.showToast() = ViewUtil.showMessage(this)