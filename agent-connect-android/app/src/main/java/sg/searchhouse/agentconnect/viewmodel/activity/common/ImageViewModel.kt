package sg.searchhouse.agentconnect.viewmodel.activity.common

import android.app.Application
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel

class ImageViewModel constructor(application: Application) :
    CoreViewModel(application) {
    val imageUrl = MutableLiveData<String>()
    val title = MutableLiveData<String>()
}