package sg.searchhouse.agentconnect.viewmodel.activity.projectinfo

import android.app.Application
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.model.app.ProjectMap
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel

class ProjectMapViewModel constructor(application: Application) : CoreViewModel(application) {
    val map: MutableLiveData<ProjectMap> by lazy {
        MutableLiveData<ProjectMap>().apply {
            value = ProjectMap.GOOGLE_MAP
        }
    }
}