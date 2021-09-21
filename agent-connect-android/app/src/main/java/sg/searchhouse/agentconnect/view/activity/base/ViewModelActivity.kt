package sg.searchhouse.agentconnect.view.activity.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// Convenient super class for activity with view model
abstract class ViewModelActivity<T : ViewModel, U : ViewDataBinding>(isSliding: Boolean = false) :
    BaseActivity(isSliding) {
    lateinit var viewModel: T
    lateinit var binding: U

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        maybeSetupActionBar(getToolbar())
    }

    private fun initViewModel() {
        binding =
            DataBindingUtil.setContentView(this, getLayoutResId())
        viewModel = ViewModelProvider(this).get(getViewModelClass())
        maybeSetupActivityParentLayout()
        bindViewModelXml()
        binding.lifecycleOwner = this
    }

    // e.g. R.layout.activity_your
    @LayoutRes
    abstract fun getLayoutResId(): Int

    // e.g. YourViewModel::class.java
    abstract fun getViewModelClass(): Class<T>

    // Please copy and paste `binding.viewModel = viewModel`
    abstract fun bindViewModelXml()

    abstract fun getToolbar(): Toolbar?
}