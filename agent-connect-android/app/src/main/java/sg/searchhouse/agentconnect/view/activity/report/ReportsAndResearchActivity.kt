package sg.searchhouse.agentconnect.view.activity.report

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_reports_and_researchs.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.enumeration.api.ReportEnum.*
import sg.searchhouse.agentconnect.util.AuthUtil
import sg.searchhouse.agentconnect.view.activity.base.ClassicActivity
import sg.searchhouse.agentconnect.view.activity.report.flashreport.MarketFlashReportFullListActivity
import sg.searchhouse.agentconnect.view.activity.report.homereport.HomeReportActivity
import sg.searchhouse.agentconnect.view.activity.listing.SearchActivity
import sg.searchhouse.agentconnect.view.activity.report.newlaunches.NewLaunchesReportsActivity
import sg.searchhouse.agentconnect.view.adapter.report.ReportsAndResearchAdapter

class ReportsAndResearchActivity : ClassicActivity() {

    private lateinit var adapter: ReportsAndResearchAdapter

    companion object {
        fun launch(activity: Activity) {
            activity.startActivity(Intent(activity, ReportsAndResearchActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupListAndAdapter()
    }

    private fun setupListAndAdapter() {
        adapter =
            ReportsAndResearchAdapter(
                ReportType.values().toList(),
                onSelectReport = { reportType ->
                    when (reportType) {
                        ReportType.HOME_REPORT -> {
                            showHomeReport()
                        }
                        ReportType.X_VALUE_REPORT -> {
                            showXValueReport()
                        }
                        ReportType.NEW_LAUNCHES_REPORT -> {
                            AuthUtil.checkModuleAccessibility(
                                module = AccessibilityEnum.AdvisorModule.NEW_PROJECT,
                                onSuccessAccessibility = {
                                    showNewLaunchesReport()
                                }
                            )
                        }
                        ReportType.FLASH_REPORT -> {
                            AuthUtil.checkModuleAccessibility(
                                module = AccessibilityEnum.AdvisorModule.FLASH_REPORT,
                                onSuccessAccessibility = {
                                    showFlashReport()
                                }
                            )
                        }
                    }
                })
        rv_reports.layoutManager = LinearLayoutManager(this)
        rv_reports.adapter = adapter
    }


    private fun showXValueReport() {
        val extras = Bundle()
        extras.putSerializable(
            SearchActivity.EXTRA_KEY_EXPAND_MODE,
            SearchActivity.ExpandMode.FULL
        )
        extras.putSerializable(SearchActivity.EXTRA_SEARCH_TYPE, SearchActivity.SearchType.X_VALUE)
        launchActivity(SearchActivity::class.java, extras)
    }

    private fun showHomeReport() {
        startActivity(Intent(this, HomeReportActivity::class.java))
    }

    private fun showFlashReport() {
        MarketFlashReportFullListActivity.launch(this)
    }

    private fun showNewLaunchesReport() {
        NewLaunchesReportsActivity.launch(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_reports_and_researchs
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }
}