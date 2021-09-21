package sg.searchhouse.agentconnect.viewmodel.activity.agent.cv

import android.app.Application
import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.data.repository.AgentRepository
import sg.searchhouse.agentconnect.model.api.agent.AgentCvPO
import sg.searchhouse.agentconnect.model.api.agent.AgentPO
import sg.searchhouse.agentconnect.model.api.agent.GetAgentCvResponse
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class AgentCvViewModel constructor(application: Application) :
    ApiBaseViewModel<GetAgentCvResponse>(application) {

    @Inject
    lateinit var applicationContext: Context
    @Inject
    lateinit var agentRepository: AgentRepository

    val agentPO: MutableLiveData<AgentPO> = MutableLiveData()
    val agentCvPO: MutableLiveData<AgentCvPO> = MutableLiveData()
    val isOwnCv: MutableLiveData<Boolean> = MutableLiveData()

    val isShowGeneralInfo: LiveData<Boolean> =
        Transformations.map(agentCvPO) { response ->
            response?.let { cv ->
                if (isOwnCv.value == true) {
                    //true => for own cv !TextUtils.isEmpty(cv.aboutMe)
                    true
                } else cv.showAboutMe && !TextUtils.isEmpty(cv.aboutMe)
            }
        }

    val isShowTrackRecord: LiveData<Boolean> =
        Transformations.map(agentCvPO) { response ->
            response?.let { cv ->
                if (isOwnCv.value == true) {
                    true
                } else cv.showTransactions
            }
        }

    val isShowTestimonial: LiveData<Boolean> =
        Transformations.map(agentCvPO) { response ->
            response?.let {
                if (isOwnCv.value == true) {
                    true
                } else it.showTestimonials && it.testimonials.isNotEmpty()
            }
        }

    val isShowListing: LiveData<Boolean> =
        Transformations.map(agentCvPO) { response ->
            response?.let {
                if (isOwnCv.value == true) {
                    true
                } else it.showListings
            }
        }

    val showTestimonialPlaceholder: MutableLiveData<Boolean> = MutableLiveData()
    val showGeneralInfoPlaceholder: MutableLiveData<Boolean> = MutableLiveData()
    val showTrackRecordPlaceholder: MutableLiveData<Boolean> = MutableLiveData()
    val showListingPlaceholder: MutableLiveData<Boolean> = MutableLiveData()

    init {
        viewModelComponent.inject(this)

        showTestimonialPlaceholder.value = true
        showGeneralInfoPlaceholder.value = true
        showTrackRecordPlaceholder.value = true
        showListingPlaceholder.value = true
    }

    fun getAgentCv(agentId: Int) {
        performRequest(agentRepository.getAgentCv(agentId))
    }

    fun saveAgentCv() {
        agentCvPO.value?.let { cv ->
            ApiUtil.performRequest(
                applicationContext,
                agentRepository.saveOrUpdateUserAgentCV(cv),
                onSuccess = {
                    agentCvPO.postValue(it.result)
                },
                onFail = {
                    println(it.error)
                },
                onError = {
                    println("on error ")
                }
            )
        }
    }

    override fun shouldResponseBeOccupied(response: GetAgentCvResponse): Boolean {
        return true
    }

}