package sg.searchhouse.agentconnect.viewmodel.activity.calculator

import android.app.Application
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.model.api.calculator.AffordAdvancedPO
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel

class AdvancedAffordabilityDetailsViewModel(application: Application) : CoreViewModel(application) {
    val affordAdvancedPO = MutableLiveData<AffordAdvancedPO>()

    init {
        viewModelComponent.inject(this)
    }
}
