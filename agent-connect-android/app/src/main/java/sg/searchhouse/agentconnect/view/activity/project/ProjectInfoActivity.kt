package sg.searchhouse.agentconnect.view.activity.project

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityProjectInfoBinding
import sg.searchhouse.agentconnect.event.project.NotifyProjectInfoToActivityEvent
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.project.ProjectInfoPagerAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.projectinfo.ProjectInfoViewModel

class ProjectInfoActivity : ViewModelActivity<ProjectInfoViewModel, ActivityProjectInfoBinding>() {
    private lateinit var pagerAdapter: ProjectInfoPagerAdapter

    private var projectName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewPager()
        setOnClickListeners()
        listenRxBuses()
    }

    private fun setOnClickListeners() {
        binding.btnViewMap.setOnClickListener {
            val mapParams = viewModel.mapParams.value ?: return@setOnClickListener
            ProjectMapActivity.launch(this, projectName, mapParams.position, mapParams.postalCode)
        }
    }

    private fun listenRxBuses() {
        listenRxBus(NotifyProjectInfoToActivityEvent::class.java) {
            projectName = it.projectName
            binding.collapsingToolbar.title = projectName
            viewModel.mapParams.postValue(
                ProjectInfoViewModel.MapParams(
                    it.position,
                    it.postalCode
                )
            )
        }
    }

    private fun setupViewPager() {
        pagerAdapter = ProjectInfoPagerAdapter(this, supportFragmentManager)
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                ViewPager.SCROLL_STATE_IDLE
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // Do nothing
            }

            override fun onPageSelected(position: Int) {
                // Do nothing
            }
        })
        binding.viewPager.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_project_info
    }

    override fun getToolbar(): Toolbar {
        return binding.toolbar
    }

    companion object {
        const val EXTRA_PROJECT_ID = "EXTRA_PROJECT_ID"
        const val EXTRA_IS_LIMIT_TRANSACTIONS_STACK = "EXTRA_IS_LIMIT_TRANSACTIONS_STACK"

        // TODO Use intent util startActivity
        // NOTE isLimitTransactionsStack: If true, this project info page will disable "View all transactions" button
        fun launch(baseActivity: BaseActivity, projectId: Int, isLimitTransactionsStack: Boolean = false) {
            val extras = Bundle()
            extras.putInt(EXTRA_PROJECT_ID, projectId)
            extras.putBoolean(EXTRA_IS_LIMIT_TRANSACTIONS_STACK, isLimitTransactionsStack)
            baseActivity.launchActivity(ProjectInfoActivity::class.java, extras)
        }
    }

    override fun getViewModelClass(): Class<ProjectInfoViewModel> {
        return ProjectInfoViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }
}