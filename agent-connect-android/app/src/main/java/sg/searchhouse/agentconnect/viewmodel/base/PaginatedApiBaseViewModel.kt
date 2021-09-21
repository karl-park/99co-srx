package sg.searchhouse.agentconnect.viewmodel.base

import android.app.Application
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus

// Reusable base view model to populate view based for paginated API
// T: API response
// U: Class of list item
abstract class PaginatedApiBaseViewModel<T, U : Any> constructor(application: Application) :
    ApiBaseViewModel<T>(application) {

    val listItems = MutableLiveData<List<Any>>()

    val page = MutableLiveData<Int>()

    fun performRequest() {
        val page = page.value ?: return
        when (page) {
            getFirstPage() -> performRequest(getRequest(page))
            else -> performNextRequest(getRequest(page))
        }
    }

    fun maybeAddPage() {
        if (mainStatus.value != ApiStatus.StatusKey.LOADING_NEXT_LIST_ITEM) {
            if (!isTotalReached()) {
                val currentPage = page.value ?: return
                page.postValue(currentPage + 1)
            }
        }
    }

    fun loadFirstPage() {
        page.postValue(getFirstPage())
    }

    private fun isTotalReached(): Boolean {
        val total = getResponseTotal() ?: 0
        val currentSize = listItems.value?.size ?: 0
        return currentSize >= total
    }

    // WARNING: Do not override this!
    override fun shouldResponseBeOccupied(response: T): Boolean {
        val currentPage = page.value ?: getFirstPage()
        val responseListItems = getResponseListItems() ?: emptyList()
        val newListItems = if (currentPage > getFirstPage()) {
            val existing = listItems.value ?: emptyList()
            existing + responseListItems
        } else {
            getResponseListItems()
        } ?: emptyList()
        listItems.postValue(newListItems)
        return newListItems.isNotEmpty()
    }

    abstract fun getResponseTotal(): Int?

    abstract fun getResponseListItems(): List<U>?

    abstract fun getRequest(page: Int): Call<T>

    // Page start count defined by backend, zero or one
    abstract fun getFirstPage(): Int
}