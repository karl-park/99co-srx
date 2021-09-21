package sg.searchhouse.agentconnect.dsl

import android.content.Context
import android.content.Intent

// Note: For positive integer or 0 only
fun Intent.getIntExtraOrNull(name: String): Int? = run {
    val value = getIntExtra(name, -1)
    return if (value == -1) {
        null
    } else {
        value
    }
}

// Easy initialize intent
// Example:
// val intent = createIntent { putExtra("ID", 1) }
inline fun <reified T : Any> Context.createIntent(noinline init: Intent.() -> Unit): Intent {
    val intent = Intent(this, T::class.java)
    intent.init()
    return intent
}