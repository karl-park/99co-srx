package sg.searchhouse.agentconnect.view.adapter.project

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import sg.searchhouse.agentconnect.view.fragment.project.PlanningDecisionsFragment
import sg.searchhouse.agentconnect.view.fragment.project.ProjectInfoFragment

class ProjectInfoPagerAdapter(val context: Context, fm: FragmentManager) : FragmentPagerAdapter(
    fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ProjectInfoFragment.newInstance()
            1 -> PlanningDecisionsFragment.newInstance()
            else -> throw IllegalArgumentException("Wrong adapter position")
        }
    }

    override fun getCount(): Int {
        return 2
    }
}