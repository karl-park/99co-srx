package sg.searchhouse.agentconnect.view.activity.common

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityWebViewBinding
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.DataBindActivity

class WebViewActivity : DataBindActivity<ActivityWebViewBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ViewUtil.loadWebView(binding.webView, intent.getStringExtra(EXTRA_URL))
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_web_view
    }

    override fun getToolbar(): Toolbar? {
        return null
    }

    companion object {
        const val EXTRA_URL = "EXTRA_URL"
        fun launch(context: Context, url: String) {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra(EXTRA_URL, url)
            context.startActivity(intent)
        }
    }
}
