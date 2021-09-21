package sg.searchhouse.agentconnect.viewmodel.fragment.main.menu

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.util.PackageUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class MenuViewModel constructor(application: Application) : CoreViewModel(application) {

    @Inject
    lateinit var applicationContext: Context

    val version = MutableLiveData<String>()

    init {
        viewModelComponent.inject(this)
        version.value =
            "${applicationContext.getString(R.string.app_name)} ${PackageUtil.getAppVersionName()}"
    }
}
