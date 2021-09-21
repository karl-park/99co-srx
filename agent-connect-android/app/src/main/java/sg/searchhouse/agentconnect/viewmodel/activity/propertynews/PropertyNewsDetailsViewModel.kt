package sg.searchhouse.agentconnect.viewmodel.activity.propertynews

import android.app.Application
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.data.repository.PropertyNewsRepository
import sg.searchhouse.agentconnect.model.api.propertynews.FindNewsArticlesResponse
import sg.searchhouse.agentconnect.model.api.propertynews.OnlineCommunicationPO
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class PropertyNewsDetailsViewModel constructor(application: Application) :
    ApiBaseViewModel<FindNewsArticlesResponse>(application) {

    @Inject
    lateinit var propertyNewsRepository: PropertyNewsRepository

    val page: MutableLiveData<Int> = MutableLiveData()
    val total: MutableLiveData<Int> = MutableLiveData()
    var propertyNews: ArrayList<Any> = arrayListOf()

    init {
        viewModelComponent.inject(this)
    }

    fun findNewsArticles(page: Int, title: String, categories: String) {
        performRequest(propertyNewsRepository.findNewsArticles(page, title, categories))
    }

    fun loadMoreFindNewsArticles(page: Int, title: String, categories: String) {
        performNextRequest(propertyNewsRepository.findNewsArticles(page, title, categories))
    }

    fun canLoadMore(): Boolean {
        val size = propertyNews.filterIsInstance<OnlineCommunicationPO>().size
        val total = total.value ?: return false
        return size < total
    }

    override fun shouldResponseBeOccupied(response: FindNewsArticlesResponse): Boolean {
        return response.articles.isNotEmpty()
    }
}

