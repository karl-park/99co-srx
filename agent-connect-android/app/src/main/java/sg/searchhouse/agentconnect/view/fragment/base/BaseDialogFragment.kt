package sg.searchhouse.agentconnect.view.fragment.base

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.fragment.app.DialogFragment
import io.reactivex.disposables.CompositeDisposable
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.dependency.component.DaggerViewComponent
import sg.searchhouse.agentconnect.dependency.component.ViewComponent
import sg.searchhouse.agentconnect.dependency.module.ViewModule
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.util.DialogUtil
import javax.inject.Inject

abstract class BaseDialogFragment : DialogFragment() {
    // Please use listenRxBus method to listen RX event
    private lateinit var compositeDisposable: CompositeDisposable

    lateinit var viewComponent: ViewComponent

    @Inject
    lateinit var dialogUtil: DialogUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        compositeDisposable = CompositeDisposable()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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

    fun setupFullScreenWindow(@ColorRes backgroundColor: Int = R.color.white_invertible, isDimBackground: Boolean = true) {
        dialog?.window?.run {
            // Un-dim background if specified
            if (!isDimBackground) {
                setDimAmount(0f)
            }
            //background white
            setBackgroundDrawable(ColorDrawable(resources.getColor(backgroundColor, null)))
            //full screen with animation
            setWindowAnimations(R.style.FullScreenDialogAnimation)
            //full screen
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    // TODO Maybe refactor
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