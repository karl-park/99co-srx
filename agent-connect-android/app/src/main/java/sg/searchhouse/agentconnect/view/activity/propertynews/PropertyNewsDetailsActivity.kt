package sg.searchhouse.agentconnect.view.activity.propertynews

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityPropertyNewsDetailsBinding
import sg.searchhouse.agentconnect.model.api.propertynews.OnlineCommunicationPO
import sg.searchhouse.agentconnect.viewmodel.activity.propertynews.PropertyNewsDetailsViewModel
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_property_news_details.*
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.util.IntentUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.propertynews.PropertyNewsDetailsAdapter
import kotlin.math.floor

class PropertyNewsDetailsActivity :
    ViewModelActivity<PropertyNewsDetailsViewModel, ActivityPropertyNewsDetailsBinding>() {

    private lateinit var adapter: PropertyNewsDetailsAdapter

    private var allPropertyNews: ArrayList<OnlineCommunicationPO> = arrayListOf()
    private var propertyNewsIndex: Int = 0
    private var title: String = ""
    private var categoryKey: String = ""

    companion object {

        const val EXTRA_KEY_PROPERTY_NEWS_INDEX = "EXTRA_KEY_PROPERTY_NEWS_INDEX"
        const val EXTRA_KEY_PROPERTY_NEWS_SEARCH_TEXT = "EXTRA_KEY_PROPERTY_NEWS_SEARCH_TEXT"
        const val EXTRA_KEY_PROPERTY_NEWS_SEARCH_CATEGORY_KEY =
            "EXTRA_KEY_PROPERTY_NEWS_SEARCH_CATEGORY_KEY"
        const val EXTRA_KEY_PROPERTY_NEWS_SEARCH_CATEGORY_DESC =
            "EXTRA_KEY_PROPERTY_NEWS_SEARCH_CATEGORY_DESCRIPTION"

        fun launch(
            activity: Activity,
            position: Int,
            searchText: String,
            categoryKey: String,
            categoryDescription: String
        ) {
            val intent = Intent(activity, PropertyNewsDetailsActivity::class.java)
            intent.putExtra(EXTRA_KEY_PROPERTY_NEWS_INDEX, position)
            intent.putExtra(EXTRA_KEY_PROPERTY_NEWS_SEARCH_TEXT, searchText)
            intent.putExtra(EXTRA_KEY_PROPERTY_NEWS_SEARCH_CATEGORY_KEY, categoryKey)
            intent.putExtra(EXTRA_KEY_PROPERTY_NEWS_SEARCH_CATEGORY_DESC, categoryDescription)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupInputExtras()
        setupListAndAdapter()
        observeLiveData()
        setupListeners()
    }

    private fun setupInputExtras() {
        propertyNewsIndex = intent.getIntExtra(EXTRA_KEY_PROPERTY_NEWS_INDEX, 0)

        title = intent.getStringExtra(EXTRA_KEY_PROPERTY_NEWS_SEARCH_TEXT) ?: ""

        categoryKey = intent.getStringExtra(EXTRA_KEY_PROPERTY_NEWS_SEARCH_CATEGORY_KEY) ?: ""

        binding.toolbar.title =
            intent.getStringExtra(EXTRA_KEY_PROPERTY_NEWS_SEARCH_CATEGORY_DESC)
                ?: getString(R.string.title_property_news)

        //update page value and call api
        viewModel.page.value = (floor((propertyNewsIndex / 10).toDouble()) + 1).toInt()
    }

    private fun setupListAndAdapter() {
        adapter =
            PropertyNewsDetailsAdapter(
                viewModel.propertyNews,
                onShareButtonClickListener = { onlineCommunicationPO, _ ->
                    val propertyNewsUrl = onlineCommunicationPO.getPropertyNewsUrl(this)
                    IntentUtil.shareText(
                        this, getString(R.string.message_share_news, propertyNewsUrl)
                    )
                })
        list_property_news_details.layoutManager = LinearLayoutManager(this)
        list_property_news_details.adapter = adapter

        ViewUtil.listenVerticalScrollEnd(list_property_news_details, reachBottom = {
            if (viewModel.canLoadMore()) {
                if (viewModel.propertyNews.last() !is Loading) {
                    viewModel.propertyNews.add(Loading())
                    adapter.notifyItemChanged(viewModel.propertyNews.size - 1)
                    viewModel.page.value?.let {
                        viewModel.page.postValue(it + 1)
                    }
                }
            }
        })
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observe(this, Observer { response ->
            response?.let {
                val index = propertyNewsIndex % 10
                viewModel.total.postValue(it.total)
                viewModel.propertyNews.removeAll(viewModel.propertyNews.filterIsInstance<Loading>())

                if (it.articles.isNotEmpty()) {
                    viewModel.propertyNews.clear()
                    allPropertyNews.addAll(it.articles)
                }

                for (i in index until allPropertyNews.size) {
                    viewModel.propertyNews.add(allPropertyNews[i])
                }
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.page.observe(this, Observer { page ->
            val pageIndex = page ?: return@Observer
            if (pageIndex == 1) {
                viewModel.findNewsArticles(pageIndex, title, categoryKey)
            } else {
                viewModel.loadMoreFindNewsArticles(pageIndex, title, categoryKey)
            }
        })
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_property_news_details
    }

    override fun getViewModelClass(): Class<PropertyNewsDetailsViewModel> {
        return PropertyNewsDetailsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }
}
