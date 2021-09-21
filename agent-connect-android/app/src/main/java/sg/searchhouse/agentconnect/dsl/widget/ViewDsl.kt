package sg.searchhouse.agentconnect.dsl.widget

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.postDelayedQuick(action: () -> Unit) = postDelayed({
    action.invoke()
}, resources.getInteger(sg.searchhouse.agentconnect.R.integer.anim_time_fast).toLong())

fun View.postDelayed(action: () -> Unit) = postDelayed({
    action.invoke()
}, resources.getInteger(sg.searchhouse.agentconnect.R.integer.anim_time).toLong())

fun View.setOnClickQuickDelayListener(action: (view: View) -> Unit) = setOnClickListener {
    postDelayedQuick {
        action.invoke(it)
    }
}

fun View.hideKeyboard() = run {
    val imm: InputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}