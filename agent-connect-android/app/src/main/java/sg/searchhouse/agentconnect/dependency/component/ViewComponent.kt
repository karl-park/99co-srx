package sg.searchhouse.agentconnect.dependency.component

import dagger.Component
import sg.searchhouse.agentconnect.dependency.module.ViewModule
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.fragment.base.BaseDialogFragment
import sg.searchhouse.agentconnect.view.fragment.base.BaseFragment
import sg.searchhouse.agentconnect.view.fragment.main.menu.MenuFragment
import sg.searchhouse.agentconnect.view.widget.agent.cv.CvTestimonials
import sg.searchhouse.agentconnect.view.widget.search.SearchListingShortcutLayout
import javax.inject.Singleton

@Component(modules = [ViewModule::class])
@Singleton
interface ViewComponent {
    fun inject(baseActivity: BaseActivity)
    fun inject(baseFragment: BaseFragment)
    fun inject(baseDialogFragment: BaseDialogFragment)

    // Inject when you need specific classes or not under activity/fragment
    fun inject(menuFragment: MenuFragment)

    fun inject(searchListingShortcutLayout: SearchListingShortcutLayout)
    fun inject(cvTestimonials: CvTestimonials)
}