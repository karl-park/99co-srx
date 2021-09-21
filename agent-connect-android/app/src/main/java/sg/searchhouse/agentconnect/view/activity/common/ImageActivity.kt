package sg.searchhouse.agentconnect.view.activity.common

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityImageBinding
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.viewmodel.activity.common.ImageViewModel

class ImageActivity :
    ViewModelActivity<ImageViewModel, ActivityImageBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFromExtra()
        setupActionBar()
    }

    private fun setupFromExtra() {
        viewModel.imageUrl.postValue(intent.getStringExtra(EXTRA_IMAGE_URL))
        viewModel.title.postValue(intent.getStringExtra(EXTRA_TITLE))
    }

    private fun setupActionBar() {
        ViewUtil.setToolbarHomeIconColor(binding.toolbar, R.color.white)
    }

    companion object {
        private const val EXTRA_IMAGE_URL = "EXTRA_IMAGE_URL"
        private const val EXTRA_TITLE = "EXTRA_TITLE"

        fun launch(baseActivity: BaseActivity, imageUrl: String, title: String) {
            val extras = Bundle()
            extras.putString(EXTRA_IMAGE_URL, imageUrl)
            extras.putString(EXTRA_TITLE, title)
            baseActivity.launchActivity(ImageActivity::class.java, extras)
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_image
    }

    override fun getViewModelClass(): Class<ImageViewModel> {
        return ImageViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar {
        return binding.toolbar
    }
}
