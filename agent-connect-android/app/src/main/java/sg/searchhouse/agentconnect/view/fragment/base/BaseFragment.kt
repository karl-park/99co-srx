package sg.searchhouse.agentconnect.view.fragment.base

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import sg.searchhouse.agentconnect.dependency.component.DaggerViewComponent
import sg.searchhouse.agentconnect.dependency.component.ViewComponent
import sg.searchhouse.agentconnect.dependency.module.ViewModule
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.util.DialogUtil
import sg.searchhouse.agentconnect.util.LocationUtil
import javax.inject.Inject

abstract class BaseFragment : Fragment() {
    // Please use listenRxBus method to listen RX event
    private lateinit var compositeDisposable: CompositeDisposable

    lateinit var viewComponent: ViewComponent

    @Inject
    lateinit var locationUtil: LocationUtil

    @Inject
    lateinit var dialogUtil: DialogUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        compositeDisposable = CompositeDisposable()
        viewComponent =
            DaggerViewComponent.builder().viewModule(this.activity?.let { ViewModule(it) }).build()
        viewComponent.inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    fun <T> listenRxBus(klazz: Class<T>, onNext: (T) -> Unit) {
        val disposable = RxBus.listen(klazz).subscribe {
            onNext.invoke(it)
        }
        compositeDisposable.add(disposable)
    }

    // Publish RxBus, only when this fragment is visible by default
    fun publishRxBus(event: Any, onResumeOnly: Boolean = true) {
        if (onResumeOnly && !isResumed) {
            return
        } else {
            RxBus.publish(event)
        }
    }

    fun launchActivity(classy: Class<*>, extras: Bundle? = null) {
        val intent = Intent(activity, classy)
        extras?.let { intent.putExtras(it) }
        this.startActivity(intent)
    }

    fun launchActivityForResult(classy: Class<*>, extras: Bundle? = null, requestCode: Int) {
        val intent = Intent(activity, classy)
        extras?.let { intent.putExtras(it) }
        this.startActivityForResult(intent, requestCode)
    }
}