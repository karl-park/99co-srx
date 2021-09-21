package sg.searchhouse.agentconnect.viewmodel.fragment.agent.cv

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.data.repository.AgentRepository
import sg.searchhouse.agentconnect.model.api.agent.AgentCvPO
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class CvEditGeneralInfoFragmentViewModel constructor(application: Application) :
    CoreViewModel(application) {

    @Inject
    lateinit var agentRepository: AgentRepository
    @Inject
    lateinit var applicationContext: Context

    val agentCv: MutableLiveData<AgentCvPO> = MutableLiveData()

    init {
        viewModelComponent.inject(this)
    }
}