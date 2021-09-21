package sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard

import android.app.Application
import android.content.Context
import sg.searchhouse.agentconnect.data.repository.PropertyNewsRepository
import sg.searchhouse.agentconnect.model.api.propertynews.FindNewsArticlesResponse
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class DashboardPropertyNewsViewModel constructor(application: Application) :
    ApiBaseViewModel<FindNewsArticlesResponse>(application) {

    @Inject
    lateinit var propertyNewsRepository: PropertyNewsRepository
    @Inject
    lateinit var applicationContext: Context

    init {
        viewModelComponent.inject(this)
    }

    fun findPropertyNews() {
        performRequest(propertyNewsRepository.findTopThreeNewArticles())
    }

    override fun shouldResponseBeOccupied(response: FindNewsArticlesResponse): Boolean {
        return true
    }
}