package sg.searchhouse.agentconnect.view.fragment.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// Convenient super class for fragment with view model
abstract class ViewModelDialogFragment<T : ViewModel, U : ViewDataBinding> : BaseDialogFragment() {
    lateinit var viewModel: T
    lateinit var binding: U

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            getLayoutResId(),
            container,
            false
        )
        viewModel = getViewModelFromProvider()
        bindViewModelXml()
        binding.lifecycleOwner = this
        return binding.root
    }

    private fun getViewModelFromProvider(): T {
        return getViewModelKey()?.let { key ->
            ViewModelProvider(this).get(key, getViewModelClass())
        } ?: run {
            ViewModelProvider(this).get(getViewModelClass())
        }
    }

    // e.g. R.layout.fragment_your
    abstract fun getLayoutResId(): Int

    // e.g. YourViewModel::class.java
    abstract fun getViewModelClass(): Class<T>

    // Please copy and paste `binding.viewModel = viewModel`
    abstract fun bindViewModelXml()

    // Key to distinct view models from each others when multiple same view models were used
    // Null if not applicable
    abstract fun getViewModelKey(): String?
}