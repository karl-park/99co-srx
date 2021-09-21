package sg.searchhouse.agentconnect.dsl

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

fun <T> LiveData<T>.observeNotNull(lifecycleOwner: LifecycleOwner, onDataChanged: (T) -> Unit) =
    observe(lifecycleOwner, { it?.run { onDataChanged.invoke(this) } })