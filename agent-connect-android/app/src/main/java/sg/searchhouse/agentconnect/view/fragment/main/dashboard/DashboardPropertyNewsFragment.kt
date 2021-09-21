package sg.searchhouse.agentconnect.view.fragment.main.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.FragmentDashboardPropertyNewsBinding
import sg.searchhouse.agentconnect.model.api.propertynews.OnlineCommunicationPO
import sg.searchhouse.agentconnect.event.app.LoginAsAgentEvent
import sg.searchhouse.agentconnect.view.activity.propertynews.PropertyNewsActivity
import sg.searchhouse.agentconnect.view.activity.propertynews.PropertyNewsDetailsActivity
import sg.searchhouse.agentconnect.view.adapter.propertynews.PropertyNewsDashboardAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard.DashboardPropertyNewsViewModel

class DashboardPropertyNewsFragment :
    ViewModelFragment<DashboardPropertyNewsViewModel, FragmentDashboardPropertyNewsBinding>() {

    private lateinit var adapter: PropertyNewsDashboardAdapter
    private var propertyNews = arrayListOf<OnlineCommunicationPO>()

    companion object {
        fun newInstance() = DashboardPropertyNewsFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupListAndAdapter()
        loadPropertyNews()
        observeRxBus()
        observeLiveData()
        handleListeners()
    }

    private fun setupListAndAdapter() {
        adapter = PropertyNewsDashboardAdapter(propertyNews) { position ->
            viewPropertyNewsDetail(position)
        }
        binding.listPropertyNews.layoutManager = LinearLayoutManager(activity)
        binding.listPropertyNews.adapter = adapter
    }

    private fun loadPropertyNews() {
        viewModel.findPropertyNews()
    }

    private fun observeRxBus() {
        listenRxBus(LoginAsAgentEvent::class.java) {
            loadPropertyNews()
        }
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observe(this, Observer { response ->
            response?.let {
                propertyNews.clear()
                propertyNews.addAll(it.articles)
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun viewPropertyNewsDetail(position: Int) {
        //TODO: move this method to PropertyNewsActivity
        val intent = Intent(activity, PropertyNewsDetailsActivity::class.java)
        intent.putExtra(PropertyNewsDetailsActivity.EXTRA_KEY_PROPERTY_NEWS_INDEX, position)
        startActivity(intent)
    }

    private fun handleListeners() {
        binding.btnViewAll.setOnClickListener {
            startActivity(Intent(activity, PropertyNewsActivity::class.java))
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_dashboard_property_news
    }

    override fun getViewModelClass(): Class<DashboardPropertyNewsViewModel> {
        return DashboardPropertyNewsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}