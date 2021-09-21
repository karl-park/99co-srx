package sg.searchhouse.agentconnect.data.repository

import retrofit2.Call
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.model.api.propertynews.FindNewsArticlesResponse
import sg.searchhouse.agentconnect.model.api.propertynews.GetNewsArticleCategoriesResponse
import sg.searchhouse.agentconnect.view.activity.propertynews.PropertyNewsActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyNewsRepository @Inject constructor(private val srxDataSource: SrxDataSource) {

    fun getNewsArticleCategories(): Call<GetNewsArticleCategoriesResponse> {
        return srxDataSource.getNewsArticleCategories()
    }

    fun findTopThreeNewArticles(
    ): Call<FindNewsArticlesResponse> {
        return srxDataSource.findNewsArticles(
            limit = 3,
            page = 1,
            channel = PropertyNewsActivity.Channel.PROPERTY_NEWS.value,
            title = "",
            categories = ""
        )
    }

    fun findNewsArticles(
        page: Int,
        title: String,
        categories: String
    ): Call<FindNewsArticlesResponse> {
        return srxDataSource.findNewsArticles(
            AppConstant.DEFAULT_BATCH_SIZE,
            page,
            PropertyNewsActivity.Channel.PROPERTY_NEWS.value,
            title,
            categories
        )
    }
}

