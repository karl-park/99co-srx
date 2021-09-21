package sg.searchhouse.agentconnect.viewmodel.fragment.listing.community

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import sg.searchhouse.agentconnect.data.repository.CommunityRepository
import sg.searchhouse.agentconnect.data.repository.ListingManagementRepository
import sg.searchhouse.agentconnect.dsl.performRequestsLenientOneByOne
import sg.searchhouse.agentconnect.model.api.community.CommunityHyperTargetTemplatePO
import sg.searchhouse.agentconnect.model.api.community.GetCommunityHyperTargetsResponse
import sg.searchhouse.agentconnect.viewmodel.base.PaginatedApiBaseViewModel
import javax.inject.Inject

class HyperTargetTemplatesDialogViewModel(application: Application) :
    PaginatedApiBaseViewModel<GetCommunityHyperTargetsResponse, CommunityHyperTargetTemplatePO>(
        application
    ) {
    @Inject
    lateinit var listingManagementRepository: ListingManagementRepository

    @Inject
    lateinit var communityRepository: CommunityRepository

    @Inject
    lateinit var applicationContext: Context

    val hyperTemplateMemberCounts = MutableLiveData<List<Int?>>()

    init {
        viewModelComponent.inject(this)
    }

    // Populate count on existing hyper target templates
    fun performGetMemberCountByHyperTargetPOs(hyperTargetTemplatePOs: List<CommunityHyperTargetTemplatePO>) {
        val existingCounts = hyperTemplateMemberCounts.value ?: emptyList()
        hyperTargetTemplatePOs.filterIndexed { index, _ -> index >= existingCounts.size }.map {
            val id = it.id ?: throw IllegalStateException("id must not be null!")
            communityRepository.getMemberCountByHyperTarget(id)
        }.performRequestsLenientOneByOne(applicationContext, onItemSuccess = { responses ->
            val newCounts = responses.map { it?.selected }
            hyperTemplateMemberCounts.postValue(existingCounts + newCounts)
        })
    }

    override fun getResponseTotal(): Int? {
        return mainResponse.value?.targets?.total
    }

    override fun getResponseListItems(): List<CommunityHyperTargetTemplatePO>? {
        return mainResponse.value?.targets?.results ?: emptyList()
    }

    override fun getRequest(page: Int): Call<GetCommunityHyperTargetsResponse> {
        return listingManagementRepository.getCommunityHyperTargets(page * MAX_RESULT, MAX_RESULT)
    }

    override fun getFirstPage(): Int {
        return 0
    }

    companion object {
        private const val MAX_RESULT = 10
    }
}