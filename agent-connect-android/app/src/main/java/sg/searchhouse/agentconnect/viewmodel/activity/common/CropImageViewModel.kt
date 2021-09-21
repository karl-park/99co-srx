package sg.searchhouse.agentconnect.viewmodel.activity.common

import android.app.Application
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel

class CropImageViewModel constructor(application: Application) :
    CoreViewModel(application) {
    val imageUrl = MutableLiveData<String>()
}