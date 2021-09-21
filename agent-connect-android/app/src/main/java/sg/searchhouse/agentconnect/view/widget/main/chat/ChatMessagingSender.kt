package sg.searchhouse.agentconnect.view.widget.main.chat

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.LayoutMessagingScreenSenderBinding

class ChatMessagingSender constructor(context: Context, attributeSet: AttributeSet? = null) :
    LinearLayout(context, attributeSet) {

    var onSendMessage: ((String) -> Unit)? = null

    val binding: LayoutMessagingScreenSenderBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.layout_messaging_screen_sender,
        this,
        true
    )

    init {
        setSendButtonState(false)
        handleComponentEvent()
    }

    private fun handleComponentEvent() {
        binding.ibMessagingSender.setOnClickListener {
            setSendButtonState(false)
            onSendMessage?.invoke(binding.etMessagingText.text.toString())
        }

        //enable disable sender button
        binding.etMessagingText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                charSequence?.let { chars ->
                    setSendButtonState(chars.isNotEmpty())
                }
            }
        })
    }

    fun clearMessageAfterSending() {
        binding.etMessagingText.text?.clear()
        setSendButtonState(false)
    }

    fun setSendButtonState(enabled: Boolean) {
        binding.isSendButtonEnabled = enabled
    }
}