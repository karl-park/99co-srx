package sg.searchhouse.agentconnect.dsl

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

fun <U : ViewDataBinding> ViewDataBinding.findChildLayoutBindingById(@IdRes layoutResId: Int): U =
    DataBindingUtil.findBinding(root.findViewById(layoutResId))
        ?: throw IllegalStateException("Missing child layout binding")

fun <U : ViewDataBinding> ViewDataBinding.getBinding(): U =
    DataBindingUtil.findBinding(root)
        ?: throw IllegalStateException("Wrong view for data binding")

fun <U : ViewDataBinding> Context.inflate(
    @LayoutRes layoutResId: Int,
    root: ViewGroup,
    attachToRoot: Boolean
): U = DataBindingUtil.inflate(LayoutInflater.from(this), layoutResId, root, attachToRoot)
