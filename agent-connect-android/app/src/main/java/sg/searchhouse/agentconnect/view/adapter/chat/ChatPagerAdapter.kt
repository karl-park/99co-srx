package sg.searchhouse.agentconnect.view.adapter.chat

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import sg.searchhouse.agentconnect.view.fragment.main.chat.AgentConversationListFragment
import sg.searchhouse.agentconnect.view.fragment.main.chat.AllConversationListFragment
import sg.searchhouse.agentconnect.view.fragment.main.chat.PublicConversationListFragment
import sg.searchhouse.agentconnect.view.fragment.main.chat.SRXConversationListFragment
import java.lang.IllegalArgumentException

class ChatPagerAdapter(context: Context, fm: FragmentManager) : FragmentPagerAdapter(
    fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    var mContext: Context = context

    companion object {
        const val TAB_COUNT = 4
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> AllConversationListFragment.newInstance()
            1 -> PublicConversationListFragment.newInstance()
            2 -> SRXConversationListFragment.newInstance()
            3 -> AgentConversationListFragment.newInstance()
            else -> throw IllegalArgumentException("Invalid fragment pager position")
        }
    }

    override fun getCount(): Int {
        return TAB_COUNT
    }

}