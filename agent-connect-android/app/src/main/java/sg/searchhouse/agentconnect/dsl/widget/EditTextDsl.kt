package sg.searchhouse.agentconnect.dsl.widget

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import sg.searchhouse.agentconnect.util.ViewUtil

fun EditText.setOnTextChangedListener(onTextChanged: (text: String) -> Unit) = run {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged.invoke(s.toString())
        }
    })
}

// Click enter on keyboard
fun EditText.setOnEnterListener(onEnterClicked: () -> Unit) = run {
    setOnKeyListener { _, keyCode, keyEvent ->
        when (keyEvent.action) {
            KeyEvent.ACTION_UP -> {
                when (keyCode) {
                    KeyEvent.KEYCODE_ENTER -> {
                        onEnterClicked.invoke()
                        true
                    }
                    else -> false
                }
            }
            else -> false
        }
    }
}

fun EditText.requestFocusAndShowKeyboard() = run {
    requestFocus()
    ViewUtil.showKeyboard(this)
}

fun EditText.setOnEditorActionListener(onActionDone: () -> Unit) =
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onActionDone.invoke()
            true
        } else {
            false
        }
    }