package sg.searchhouse.agentconnect.viewmodel.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import sg.searchhouse.agentconnect.dependency.component.DaggerViewModelComponent
import sg.searchhouse.agentconnect.dependency.component.ViewModelComponent
import sg.searchhouse.agentconnect.dependency.module.AppModule

// All view models to inherit this instead of AndroidViewModel
abstract class CoreViewModel constructor(application: Application) :
    AndroidViewModel(application) {
    // Inject superclass into this to use Dagger objects
    val viewModelComponent: ViewModelComponent =
        DaggerViewModelComponent.builder().appModule(AppModule(application)).build()
}