package sg.searchhouse.agentconnect.viewmodel.activity.agent.cv

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.data.repository.AgentRepository
import sg.searchhouse.agentconnect.model.api.agent.AgentCvPO
import sg.searchhouse.agentconnect.util.ApiUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class CvTestimonialsViewModel constructor(application: Application) :
    CoreViewModel(application) {

    @Inject
    lateinit var applicationContext: Context
    @Inject
    lateinit var agentRepository: AgentRepository

    val testimonials: MutableLiveData<ArrayList<AgentCvPO.Testimonial>> = MutableLiveData()

    init {
        viewModelComponent.inject(this)
    }

    fun getAgentCv(agentId: Int) {
        ApiUtil.performRequest(
            applicationContext,
            agentRepository.getAgentCv(agentId),
            onSuccess = { response ->
                response.data.agentCvPO?.let { cv ->
                    testimonials.postValue(cv.testimonials)
                }
            },
            onFail = { println(it.error) },
            onError = { println("Error in getting agent cv") }
        )
    }
}