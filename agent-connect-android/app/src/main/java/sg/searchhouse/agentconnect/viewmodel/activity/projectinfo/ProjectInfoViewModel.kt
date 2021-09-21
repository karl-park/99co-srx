package sg.searchhouse.agentconnect.viewmodel.activity.projectinfo

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel

class ProjectInfoViewModel constructor(application: Application) : CoreViewModel(application) {
    val mapParams = MutableLiveData<MapParams>()
    class MapParams(val position: LatLng, val postalCode: String)
}