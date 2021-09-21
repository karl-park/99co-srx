package sg.searchhouse.agentconnect.util

import android.util.Log
import androidx.lifecycle.Lifecycle

object LifecycleUtil {

    fun isLaunchFragmentSafe(lifecycle: Lifecycle): Boolean {
        Log.i("CurrentState", "${lifecycle.currentState}")
        return !(lifecycle.currentState == Lifecycle.State.DESTROYED || lifecycle.currentState == Lifecycle.State.CREATED)
    }
}