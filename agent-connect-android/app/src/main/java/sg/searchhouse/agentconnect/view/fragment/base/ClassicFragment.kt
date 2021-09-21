package sg.searchhouse.agentconnect.view.fragment.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

// Convenient super class for fragment with view model
abstract class ClassicFragment<U : ViewDataBinding> : BaseFragment() {
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
        binding.lifecycleOwner = this
        return binding.root
    }

    // e.g. R.layout.fragment_your
    abstract fun getLayoutResId(): Int
}