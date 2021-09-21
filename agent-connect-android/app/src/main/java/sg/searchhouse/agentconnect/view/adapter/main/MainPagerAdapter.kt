package sg.searchhouse.agentconnect.view.adapter.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import sg.searchhouse.agentconnect.view.fragment.main.dashboard.DashboardFragment
import sg.searchhouse.agentconnect.view.fragment.main.chat.ChatFragment
import sg.searchhouse.agentconnect.view.fragment.main.FindAgentFragment
import sg.searchhouse.agentconnect.view.fragment.main.menu.MenuFragment

enum class MainBottomNavigationTab(val position: Int, val key: String, val screen: String) {
    DASHBOARD(0, "dashboard", "DashboardFragment"),
    CHAT(1, "chat", "ChatFragment"),
    FIND_AGENT(2, "find agent", "FindAgentFragment"),
    MENU(3, "menu", "MenuFragment"),
    X_TAB(4, "X", "")
}

class MainPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    @Throws(IllegalArgumentException::class)
    override fun getItem(position: Int): Fragment {
        return when (position) {
            MainBottomNavigationTab.DASHBOARD.position -> DashboardFragment.newInstance()
            MainBottomNavigationTab.CHAT.position -> ChatFragment.newInstance()
            MainBottomNavigationTab.FIND_AGENT.position -> FindAgentFragment.newInstance()
            MainBottomNavigationTab.MENU.position -> MenuFragment.newInstance()
            else -> throw IllegalArgumentException("Invalid fragment pager position")
        }
    }

    override fun getCount(): Int {
        return 4
    }
}