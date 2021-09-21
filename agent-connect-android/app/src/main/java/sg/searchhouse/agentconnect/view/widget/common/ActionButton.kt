package sg.searchhouse.agentconnect.view.widget.common

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class ActionButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    AppCompatButton(context, attrs, defStyleAttr) {

    override fun setEnabled(enabled: Boolean) {
        alpha = when {
            enabled -> 1.0f
            else -> 0.5f
        }
        super.setEnabled(enabled)
    }
}