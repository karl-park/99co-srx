package sg.searchhouse.agentconnect.viewmodel.activity.projectinfo

import android.app.Application
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.model.api.project.PhotoPO
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel

class SiteMapMediaViewModel constructor(application: Application) :
    CoreViewModel(application) {
    val position = MutableLiveData<Int>()
    val photoPOs = MutableLiveData<List<PhotoPO>>()
}