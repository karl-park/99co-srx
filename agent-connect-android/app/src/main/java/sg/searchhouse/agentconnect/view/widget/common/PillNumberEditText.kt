package sg.searchhouse.agentconnect.view.widget.common

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import sg.searchhouse.agentconnect.databinding.EditTextNewPillNumberBinding
import sg.searchhouse.agentconnect.dsl.widget.requestFocusAndShowKeyboard
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.util.NumberUtil.MAX_INT

class PillNumberEditText constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {
    private val binding =
        EditTextNewPillNumberBinding.inflate(LayoutInflater.from(context), this, true)

    private var numberTextTransformer: ((number: Int?, thousandFormattedNumber: String?) -> String)? =
        null

    private var onNumberChangeListener: ((number: Int?) -> Unit)? = null

    private var maxValue = MAX_INT

    private var previousText: String = ""

    private var isReassignValue: Boolean = false

    var value: Int? = null

    private var dynamicHint: String? = null

    init {
        binding.etDisplay.setOnClickListener {
            // Whenever user try to move cursor, set it to last position
            moveCursorToEnd()
        }
        binding.etDisplay.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {}

            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                previousText = charSequence?.toString() ?: ""
                moveCursorToEnd()
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (isReassignValue) {
                    isReassignValue = false
                    // Whenever user try to move cursor, set it to last position
                    return
                }

                // Case when cursor is dragged into middle of text, revert any change
                if (charSequence != null && binding.etDisplay.selectionEnd < charSequence.length) {
                    revert()
                    return
                }

                val inputText = charSequence?.toString() ?: ""

                val diff = getDifference(inputText, previousText)
                val stringDiff = diff.first

                when (diff.second) {
                    DiffType.INCREMENT -> performIncrement(stringDiff)
                    DiffType.DECREMENT -> performDecrement(stringDiff)
                    DiffType.CLEAR -> updateValue(null)
                    DiffType.NO_CHANGE -> {
                    }
                }
            }
        })
    }

    private fun moveCursorToEnd() {
        binding.etDisplay.post {
            binding.etDisplay.setSelection(binding.etDisplay.text.toString().length)
        }
    }

    private fun performIncrement(stringDiff: String) {
        val numberDiff = NumberUtil.toInt(stringDiff)
        if (numberDiff != null) {
            // Make sure new value is number
            val newActualValue = "${NumberUtil.toString(value)}${numberDiff}"
            validateUpdateValue(newActualValue)
        } else {
            // Invalid increment, rollback to old value
            revert()
        }
    }

    // May use this when the hint is dynamic
    // By default, the text value for null integer in #numberTextTransformer is already taken as hint
    fun setHint(hint: String?) {
        dynamicHint = hint
        resetHint()
    }

    // TODO Maybe add into XML attribute when free
    fun setHintColor(@ColorRes colorResId: Int) {
        binding.etDisplay.setHintTextColor(binding.etDisplay.context.getColor(colorResId))
    }

    override fun setBackground(background: Drawable?) {
        binding.etDisplay.background = background
    }

    override fun setEnabled(enabled: Boolean) {
        binding.etDisplay.isEnabled = enabled
    }

    private fun performDecrement(stringDiff: String) {
        val diffLength = stringDiff.length

        val originalActualValue = NumberUtil.toString(value)

        if (originalActualValue.length > diffLength) {
            // Create new value
            val newActualValue =
                originalActualValue.substring(0, originalActualValue.length - diffLength)
            validateUpdateValue(newActualValue)
        } else {
            updateValue(null)
        }
    }

    private fun validateUpdateValue(newActualValue: String) {
        // Must be numeric string
        if (!NumberUtil.isNaturalNumber(newActualValue)) {
            revert()
            return
        }

        // Must not overflow
        if (NumberUtil.isIntOverflow(newActualValue)) {
            revert()
            return
        }

        val number = NumberUtil.toInt(newActualValue)!!

        // Must not more than max value
        if (number > maxValue) {
            revert()
            return
        }

        updateValue(number)
    }

    private fun revert() {
        updateValue(value)
    }

    private fun updateValue(number: Int?) {
        // Update actual value
        value = number

        // Update display text box
        val formattedNumberString = number?.run { NumberUtil.formatThousand(this) }
        val newDisplayValue = numberTextTransformer?.invoke(number, formattedNumberString)
        isReassignValue = true
        binding.etDisplay.setText(
            if (number != null) {
                newDisplayValue
            } else {
                binding.etDisplay.hint = newDisplayValue
                ""
            }
        )

        // Invoke callback
        onNumberChangeListener?.invoke(number)
    }

    private fun getDifference(currentText: String, previousText: String): Pair<String, DiffType> {
        val increment = currentText.replaceFirst(previousText, "")
        val decrement = previousText.replaceFirst(currentText, "")

        return if (TextUtils.isEmpty(currentText)) {
            Pair("", DiffType.CLEAR)
        } else if (TextUtils.isEmpty(previousText) && !TextUtils.isEmpty(currentText)) {
            Pair(increment, DiffType.INCREMENT)
        } else if (!TextUtils.equals(increment, currentText)) {
            Pair(increment, DiffType.INCREMENT)
        } else if (!TextUtils.equals(decrement, previousText)) {
            Pair(decrement, DiffType.DECREMENT)
        } else {
            Pair("", DiffType.NO_CHANGE)
        }
    }

    fun resetHint() {
        numberTextTransformer?.let { binding.etDisplay.hint = getHint(it) }
    }

    fun setup(
        maxValue: Int? = null,
        numberTextTransformer: (number: Int?, formattedNumberString: String?) -> String,
        onNumberChangeListener: ((number: Int?) -> Unit)? = null
    ) {
        this.maxValue = maxValue ?: MAX_INT
        this.numberTextTransformer = numberTextTransformer
        this.onNumberChangeListener = onNumberChangeListener
        binding.etDisplay.hint = getHint(numberTextTransformer)
    }

    private fun getHint(numberTextTransformer: (number: Int?, formattedNumberString: String?) -> String): String {
        return dynamicHint ?: numberTextTransformer.invoke(null, "")
    }

    fun clear() {
        setNumber(null)
    }

    fun setOnEditorActionListener(onEditorActionListener: (view: View, actionId: Int?, keyEvent: KeyEvent?) -> Boolean) {
        binding.etDisplay.setOnEditorActionListener { textView, actionId, keyEvent ->
            onEditorActionListener.invoke(textView, actionId, keyEvent)
        }
    }

    fun setNumber(number: Int?) {
        isReassignValue = false
        if (number != null) {
            clear()
            val text = NumberUtil.toString(number)
            binding.etDisplay.append(text)
        } else {
            binding.etDisplay.setText("")
        }
    }

    fun requestFocusAndShowKeyboard() {
        binding.etDisplay.requestFocusAndShowKeyboard()
    }

    private enum class DiffType {
        INCREMENT, DECREMENT, CLEAR, NO_CHANGE
    }
}