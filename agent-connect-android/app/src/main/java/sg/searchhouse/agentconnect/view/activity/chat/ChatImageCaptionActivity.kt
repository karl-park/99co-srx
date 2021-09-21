package sg.searchhouse.agentconnect.view.activity.chat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_chat_image_caption.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityChatImageCaptionBinding
import sg.searchhouse.agentconnect.dsl.launchActivityForResult
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.viewmodel.activity.chat.ChatImageCaptionViewModel

class ChatImageCaptionActivity :
    ViewModelActivity<ChatImageCaptionViewModel, ActivityChatImageCaptionBinding>() {
    private lateinit var imageUriString: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtras()
        setupViews()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.bitmap.observeNotNull(this) {
            image_view.setImageBitmap(it)
        }
    }

    private fun setupViews() {
        setupActionBar()
        setupListeners()
    }

    private fun setupListeners() {
        btn_send.setOnClickListener {
            val data = Intent()
            data.putExtra(EXTRA_OUTPUT_CAPTION, et_caption.text.toString())
            data.putExtra(EXTRA_OUTPUT_ROTATE_ANGLE, viewModel.angle.value ?: 0f)
            setResult(Activity.RESULT_OK, data)
            finish()
        }
        btn_rotate.setOnClickListener {
            rotateImage()
        }
    }

    private fun rotateImage() {
        val newAngle = (viewModel.angle.value ?: 0f) + 90f
        viewModel.angle.postValue(newAngle)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        dialogUtil.showActionDialog(R.string.dialog_message_confirm_quit_caption) { super.onBackPressed() }
    }

    private fun setupExtras() {
        imageUriString = intent.getStringExtra(EXTRA_IMAGE)
            ?: throw IllegalArgumentException("Missing `EXTRA_IMAGE` in `ChatImageCaptionActivity`!")
        viewModel.imageUriString.postValue(imageUriString)
    }

    private fun setupActionBar() {
        ViewUtil.setToolbarHomeIconColor(toolbar, R.color.white)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_chat_image_caption
    }

    override fun getViewModelClass(): Class<ChatImageCaptionViewModel> {
        return ChatImageCaptionViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }

    companion object {
        private const val EXTRA_IMAGE = "EXTRA_IMAGE"

        const val EXTRA_OUTPUT_CAPTION = "EXTRA_OUTPUT_CAPTION"
        const val EXTRA_OUTPUT_ROTATE_ANGLE = "EXTRA_OUTPUT_ROTATE_ANGLE"

        fun launch(activity: Activity, imageLocalFileUri: String, requestCode: Int) {
            val extras = Bundle()
            extras.putString(EXTRA_IMAGE, imageLocalFileUri)
            activity.launchActivityForResult(
                ChatImageCaptionActivity::class.java,
                extras = extras,
                requestCode = requestCode
            )
        }
    }
}
