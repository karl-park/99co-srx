package sg.searchhouse.agentconnect.view.widget.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import sg.searchhouse.agentconnect.databinding.EditTextSearchWhiteBinding
import sg.searchhouse.agentconnect.dsl.widget.setOnTextChangedListener

// TODO Refactor with `PurpleSearchEditText`
// White `EditText` with `clear text` button
class WhiteSearchEditText constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {
    val binding: EditTextSearchWhiteBinding =
        EditTextSearchWhiteBinding.inflate(LayoutInflater.from(context), this, true)

    var onClearBtnClickLister: (() -> Unit)? = null

    init {
        setupListeners()
    }

    private fun setupListeners() {
        binding.editText.setOnTextChangedListener {
            toggleBtnClearVisibility(it)
        }
        binding.btnClear.setOnClickListener {
            binding.editText.setText("")
            onClearBtnClickLister?.invoke()
        }
    }

    fun setOnTextChangedListener(onTextChanged: (text: String) -> Unit) {
        binding.editText.setOnTextChangedListener {
            onTextChanged.invoke(it)
            toggleBtnClearVisibility(it)
        }
    }

    private fun toggleBtnClearVisibility(text: String) {
        binding.btnClear.visibility = when {
            text.isNotEmpty() -> View.VISIBLE
            else -> View.GONE
        }
    }

    fun clearEditText() {
        binding.editText.text?.clear()
    }
}