package sg.searchhouse.agentconnect.view.activity.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

// Convenient super class for activity with view data binding
abstract class DataBindActivity<U : ViewDataBinding>(isSliding: Boolean = false) :
    BaseActivity(isSliding) {
    lateinit var binding: U

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBind()
        maybeSetupActionBar(getToolbar())
    }

    private fun initDataBind() {
        binding =
            DataBindingUtil.setContentView(this, getLayoutResId())
        maybeSetupActivityParentLayout()
        binding.lifecycleOwner = this
    }

    // e.g. R.layout.activity_your
    @LayoutRes
    abstract fun getLayoutResId(): Int

    abstract fun getToolbar(): Toolbar?
}