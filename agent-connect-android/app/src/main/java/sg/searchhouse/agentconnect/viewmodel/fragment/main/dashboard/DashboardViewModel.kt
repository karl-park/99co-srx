package sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard

import android.app.Application
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel

class DashboardViewModel constructor(application: Application) : CoreViewModel(application) {
    val shouldShowMarketWatch = MutableLiveData<Boolean>().apply { value = false }
    val shouldShowWatchList = MutableLiveData<Boolean>().apply { value = false }
}