package sg.searchhouse.agentconnect.service

import android.app.IntentService
import sg.searchhouse.agentconnect.dependency.component.DaggerServiceComponent
import sg.searchhouse.agentconnect.dependency.component.ServiceComponent
import sg.searchhouse.agentconnect.dependency.module.AppModule

abstract class BaseIntentService constructor(val name: String) : IntentService(name) {
    // Inject superclass into this to use Dagger objects
    lateinit var serviceComponent: ServiceComponent

    override fun onCreate() {
        super.onCreate()
        serviceComponent = DaggerServiceComponent.builder().appModule(AppModule(application)).build()
    }
}