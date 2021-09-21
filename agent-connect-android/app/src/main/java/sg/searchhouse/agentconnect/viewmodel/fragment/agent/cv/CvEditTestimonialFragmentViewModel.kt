package sg.searchhouse.agentconnect.viewmodel.fragment.agent.cv

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class CvEditTestimonialFragmentViewModel constructor(application: Application) :
    CoreViewModel(application) {

    @Inject
    lateinit var applicationContext: Context

    val clientName: MutableLiveData<String> = MutableLiveData()
    val testimonial: MutableLiveData<String> = MutableLiveData()

    init {
        viewModelComponent.inject(this)
    }

}