package sg.searchhouse.agentconnect.view.widget.agent.profile

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.LayoutProfileMyClientsBinding

class ProfileMyClients(context: Context, attributeSet: AttributeSet? = null) :
    LinearLayout(context, attributeSet) {

    val binding: LayoutProfileMyClientsBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.layout_profile_my_clients,
        this,
        true
    )
}