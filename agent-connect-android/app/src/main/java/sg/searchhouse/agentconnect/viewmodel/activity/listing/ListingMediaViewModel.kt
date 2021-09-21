package sg.searchhouse.agentconnect.viewmodel.activity.listing

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.data.repository.ListingSearchRepository
import sg.searchhouse.agentconnect.model.api.listing.GetListingResponse
import sg.searchhouse.agentconnect.model.api.listing.ListingMedia
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class ListingMediaViewModel constructor(application: Application) :
    ApiBaseViewModel<GetListingResponse>(application) {
    @Inject
    lateinit var listingSearchRepository: ListingSearchRepository

    @Inject
    lateinit var applicationContext: Context

    val position = MutableLiveData<Int>()
    val listingMedias: LiveData<List<ListingMedia>> = Transformations.map(mainResponse) { response ->
        response?.let { getListingMedias(it) }
    }

    private fun getListingMedias(response: GetListingResponse): List<ListingMedia> {
        return response.fullListing.getValidListingMedias()
    }

    init {
        viewModelComponent.inject(this)
    }

    fun performRequest(listingId: String, listingType: String) {
        performRequest(listingSearchRepository.getListing(listingId, listingType))
    }

    override fun shouldResponseBeOccupied(response: GetListingResponse): Boolean {
        return getListingMedias(response).isNotEmpty()
    }
}