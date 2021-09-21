package sg.searchhouse.agentconnect.view.activity.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar

// Convenient super class for activity without view model
abstract class ClassicActivity(isSliding: Boolean = false) : BaseActivity(isSliding) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())
        maybeSetupActivityParentLayout()
        maybeSetupActionBar(getToolbar())
    }

    // e.g. R.layout.activity_your
    @LayoutRes
    abstract fun getLayoutResId(): Int

    abstract fun getToolbar(): Toolbar?
}