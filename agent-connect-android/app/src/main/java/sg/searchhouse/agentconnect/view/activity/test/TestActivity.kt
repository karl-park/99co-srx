package sg.searchhouse.agentconnect.view.activity.test

import androidx.appcompat.widget.Toolbar
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.view.activity.base.ClassicActivity

// Purposefully blank activity for testing purpose
class TestActivity : ClassicActivity() {
    override fun getLayoutResId(): Int {
        return R.layout.activity_test
    }

    override fun getToolbar(): Toolbar? {
        return null
    }
}