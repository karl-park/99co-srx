package sg.searchhouse.agentconnect.view.activity.propertynews

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_property_news.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityPropertyNewsBinding
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.propertynews.PropertyNewsAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.propertynews.PropertyNewsViewModel

class PropertyNewsActivity :
    ViewModelActivity<PropertyNewsViewModel, ActivityPropertyNewsBinding>() {

    private lateinit var adapter: PropertyNewsAdapter

    private var searchText: String = ""
    private var categoryKey: String = ""
    private var categoryDescription: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupListAndAdapter()
        performRequest()
        observeLiveData()
        handleViewListeners()
    }

    private fun performRequest() {
        viewModel.getNewsArticleCategories()
    }

    private fun setupListAndAdapter() {
        viewModel.page.value = 1

        adapter = PropertyNewsAdapter(
            viewModel.propertyNews,
            onItemClickListener = { _, position -> showPropertyNewsDetails(position) }
        )
        list_property_news.layoutManager = LinearLayoutManager(this)
        list_property_news.adapter = adapter

        ViewUtil.listenVerticalScrollEnd(list_property_news, reachBottom = {
            if (viewModel.canLoadMore()) {
                if (viewModel.propertyNews.last() !is Loading) {
                    viewModel.propertyNews.add(Loading())
                    adapter.notifyItemChanged(viewModel.propertyNews.size - 1)
                    viewModel.page.value?.let { viewModel.page.postValue(it + 1) }
                }
            }
        })
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observe(this, Observer { response ->
            response?.let {
                viewModel.total.postValue(it.total)
                viewModel.propertyNews.removeAll(viewModel.propertyNews.filterIsInstance<Loading>())
                if (it.articles.isNotEmpty()) {
                    viewModel.page.value?.let { pageIndex ->
                        if (pageIndex == 1) {
                            viewModel.propertyNews.clear()
                        }
                        viewModel.propertyNews.addAll(it.articles)
                    }
                } else {
                    viewModel.propertyNews.clear()
                }
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.page.observe(this, Observer { page ->
            page?.let {
                if (it == 1) {
                    viewModel.findNewsArticles(it, categoryKey)
                } else {
                    viewModel.loadMoreFindNewsArticles(it, categoryKey)
                }

            }
        })
    }

    private fun handleViewListeners() {
        et_search_news.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                charSequence?.let { chars ->
                    if (chars.trim().length > 2 || chars.trim().isEmpty()) {
                        searchText = chars.toString()
                        viewModel.title = chars.toString()
                        viewModel.page.value = 1
                    }
                }
            }
        })

        tab_categories.setOnClickListener {
            viewModel.categories.value?.let { items ->
                val categoryKeys = items.keys.toMutableList()
                val categoryValues = items.values.toMutableList()
                categoryKeys.add(0, "0")
                categoryValues.add(0, getString(R.string.label_all_categories))
                dialogUtil.showWheelPickerDialog(
                    categoryValues,
                    { _, position ->
                        categoryKey = categoryKeys[position]
                        categoryDescription = categoryValues[position]
                        viewModel.categoriesName.value = categoryValues[position]

                        //to show loading indicator when search
                        viewModel.propertyNews.clear()
                        adapter.notifyDataSetChanged()

                        viewModel.findNewsArticles(1, categoryKey)
                    },
                    R.string.label_categories
                ) //show list dialog
            }
        }
    }

    private fun showPropertyNewsDetails(position: Int) {
        PropertyNewsDetailsActivity.launch(
            this,
            position,
            searchText,
            categoryKey,
            categoryDescription
        )
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_property_news
    }

    override fun getViewModelClass(): Class<PropertyNewsViewModel> {
        return PropertyNewsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

    enum class Channel(val value: String) {
        PROPERTY_NEWS("MCR")
    }
}

