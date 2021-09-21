package sg.searchhouse.agentconnect.view.widget.main.chat

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_chat_selected_agent_list.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.model.api.agent.AgentPO
import sg.searchhouse.agentconnect.view.adapter.chat.ChatAgentListAdapter

class ChatSelectedAgentList constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    var selectedAgents: ArrayList<AgentPO> = arrayListOf()
    val adapter = ChatAgentListAdapter(selectedAgents)
    var removeAgentItemFromList: ((AgentPO) -> Unit)? = null

    init {
        View.inflate(context, R.layout.layout_chat_selected_agent_list, this)
        rv_selected_agents.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        rv_selected_agents.adapter = adapter
    }

    fun populateSelectedAgents(agents: ArrayList<AgentPO>) {
        if (agents.isNotEmpty()) {
            //REGISTER listener
            adapter.onRemoveAgentItem = onRemoveAgentItem
            //Bind in adapter
            selectedAgents.clear()
            selectedAgents.addAll(agents)
            adapter.notifyDataSetChanged()
            //show hide layout
            layout_selected_agents.visibility = View.VISIBLE
        } else {
            layout_selected_agents.visibility = View.GONE
        }
    }

    //adapter
    private val onRemoveAgentItem: (AgentPO) -> Unit = { agentPO ->
        removeAgentItemFromList?.invoke(agentPO)
    }
}