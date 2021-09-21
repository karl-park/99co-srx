package sg.searchhouse.agentconnect.viewmodel.fragment.listing.user

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.data.repository.ListingManagementRepository
import sg.searchhouse.agentconnect.dsl.performRequest
import sg.searchhouse.agentconnect.model.api.common.NameValuePO
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class FeaturedPromptViewModel constructor(application: Application) :
    CoreViewModel(application) {
    @Inject
    lateinit var listingManagementRepository: ListingManagementRepository

    @Inject
    lateinit var applicationContext: Context
    
    val featuredListingPrompts = MutableLiveData<List<NameValuePO>>()

    init {
        viewModelComponent.inject(this)
    }

    // Check if need to show prompt dialog for featured listings
    fun getAddonPrompts() {
        listingManagementRepository.getAddonPrompts().performRequest(
            applicationContext,
            onSuccess = { response ->
                response.run {
                    if (prompts.isNotEmpty()) {
                        featuredListingPrompts.postValue(prompts)
                    }
                }
            }, onFail = {
                // Do nothing
            }, onError = {
                // Do nothing
            })
    }
}