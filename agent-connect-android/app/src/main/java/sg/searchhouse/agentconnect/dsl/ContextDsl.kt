package sg.searchhouse.agentconnect.dsl

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment

fun Context.getNotificationManager(): NotificationManager =
    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

@Deprecated("Use `launchActivityV2`")
fun Context.launchActivity(classy: Class<*>, extras: Bundle? = null) = run {
    val intent = Intent(this, classy)
    extras?.let { intent.putExtras(it) }
    this.startActivity(intent)
}

inline fun <reified T : Any> Context.launchActivityV2(noinline init: (Intent.() -> Unit)? = null) {
    val intent = createIntent<T> { init?.invoke(this) }
    startActivity(intent)
}

@Deprecated("Use `launchActivityForResultV2`")
fun Activity.launchActivityForResult(classy: Class<*>, extras: Bundle? = null, requestCode: Int) =
    run {
        val intent = Intent(this, classy)
        extras?.let { intent.putExtras(it) }
        this.startActivityForResult(intent, requestCode)
    }

inline fun <reified T : Any> Activity.launchActivityForResultV2(
    requestCode: Int,
    noinline init: (Intent.() -> Unit)? = null
) {
    val intent = createIntent<T> { init?.invoke(this) }
    startActivityForResult(intent, requestCode)
}

fun Fragment.launchActivityForResult(classy: Class<*>, extras: Bundle? = null, requestCode: Int) =
    run {
        val intent = Intent(context, classy)
        extras?.let { intent.putExtras(it) }
        this.startActivityForResult(intent, requestCode)
    }