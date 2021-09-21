package sg.searchhouse.agentconnect.view.widget.main.chat

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.tab_item_chat.view.*
import sg.searchhouse.agentconnect.R

class ChatTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TabLayout(context, attrs, defStyleAttr) {

    var onCheckChangedTabCheckBox: ((Boolean) -> Unit)? = null

    override fun setupWithViewPager(viewPager: ViewPager?) {
        super.setupWithViewPager(viewPager)

        val tabAll = LayoutInflater.from(context).inflate(R.layout.tab_item_chat, null)
        tabAll.tv_tab_item_chat.text = context.resources.getString(R.string.label_all)

        val tabPublic = LayoutInflater.from(context).inflate(R.layout.tab_item_chat, null)
        tabPublic.tv_tab_item_chat.text = context.resources.getString(R.string.label_public)

        val tabSRX = LayoutInflater.from(context).inflate(R.layout.tab_item_chat, null)
        tabSRX.tv_tab_item_chat.text = context.resources.getString(R.string.label_srx)

        val tabAgents = LayoutInflater.from(context).inflate(R.layout.tab_item_chat, null)
        tabAgents.tv_tab_item_chat.text = context.resources.getString(R.string.label_agents)

        getTabAt(0)?.customView = tabAll
        getTabAt(1)?.customView = tabPublic
        getTabAt(2)?.customView = tabSRX
        getTabAt(3)?.customView = tabAgents
    }
    //Edit Mode
    fun setEditMode(editMode: Boolean) {
        if (editMode) {
            getTabAt(selectedTabPosition)?.customView?.cb_tab_item_chat?.let {
                it.visibility = View.VISIBLE
            }
        } else {
            //hide all tab items checkbox and false to check box state
            for (i in 0..3) {
                getTabAt(i)?.customView?.cb_tab_item_chat?.let {
                    it.visibility = View.GONE
                    it.isChecked = false
                }
            }
        } //end of else

        getTabAt(selectedTabPosition)?.customView?.cb_tab_item_chat?.let { checkbox ->
            checkbox.setOnClickListener {
                val tabCheckBox = it as AppCompatCheckBox
                onCheckChangedTabCheckBox?.invoke(tabCheckBox.isChecked)
            }
        }
    }

    fun changeTabCheckBoxState(checkAll: Boolean) {
        getTabAt(selectedTabPosition)?.customView?.cb_tab_item_chat?.let { it.isChecked = checkAll }
    }
}