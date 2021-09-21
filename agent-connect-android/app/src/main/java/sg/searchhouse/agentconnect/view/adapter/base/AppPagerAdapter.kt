package sg.searchhouse.agentconnect.view.adapter.base

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

abstract class AppPagerAdapter<T> : PagerAdapter() {
    var listItems: List<T> = emptyList()

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (`object` is View) {
            container.removeView(`object`)
        }
    }

    override fun getCount(): Int {
        return listItems.size
    }
}