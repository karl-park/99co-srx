package sg.searchhouse.agentconnect.viewmodel.activity.propertynews

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.PropertyNewsRepository
import sg.searchhouse.agentconnect.model.api.propertynews.FindNewsArticlesResponse
import sg.searchhouse.agentconnect.model.api.propertynews.OnlineCommunicationPO
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class PropertyNewsViewModel constructor(application: Application) :
    ApiBaseViewModel<FindNewsArticlesResponse>(application) {

    @Inject
    lateinit var propertyNewsRepository: PropertyNewsRepository
    @Inject
    lateinit var applicationContext: Context

    var title: String = ""
    val page: MutableLiveData<Int> = MutableLiveData()
    val total: MutableLiveData<Int> = MutableLiveData()
    var propertyNews: ArrayList<Any> = arrayListOf()
    val categories: MutableLiveData<Map<String, String>> = MutableLiveData()
    var categoriesName: MutableLiveData<String> = MutableLiveData()

    init {
        viewModelComponent.inject(this)
        categoriesName.value = applicationContext.getString(R.string.label_all_categories)
    }

    fun findNewsArticles(page: Int, categories: String) {
        performRequest(propertyNewsRepository.findNewsArticles(page, title, categories))
    }

    fun loadMoreFindNewsArticles(page: Int, categories: String) {
        performNextRequest(propertyNewsRepository.findNewsArticles(page, title, categories))
    }

    fun canLoadMore(): Boolean {
        val size = propertyNews.filterIsInstance<OnlineCommunicationPO>().size
        val total = total.value ?: return false
        return size < total
    }

    fun getNewsArticleCategories() {
        ApiUtil.performRequest(
            applicationContext,
            propertyNewsRepository.getNewsArticleCategories(),
            onSuccess = { response ->
                categories.postValue(response.categories)
            },
            onFail = { println(it.error) },
            onError = { println("Error in property news view model") })
    }

    override fun shouldResponseBeOccupied(response: FindNewsArticlesResponse): Boolean {
        return response.articles.isNotEmpty()
    }
}
