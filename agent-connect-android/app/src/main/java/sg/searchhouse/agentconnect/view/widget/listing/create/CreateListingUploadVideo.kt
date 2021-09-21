package sg.searchhouse.agentconnect.view.widget.listing.create

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.LayoutCreateListingUploadVideoBinding

//todo nwethazin need to check
class CreateListingUploadVideo(context: Context, attributeSet: AttributeSet? = null) :
    LinearLayout(context, attributeSet) {

    val binding: LayoutCreateListingUploadVideoBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.layout_create_listing_upload_video,
        this,
        true
    )

    init {
//        et_video_url.setOnEditorActionListener { v, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                //hide keyboard
//                ViewUtil.hideKeyboard(et_video_url)
//                et_video_url.clearFocus()
//                true
//            } else {
//                false
//            }
//        }
//
//        et_video_url.setOnFocusChangeListener { view, b ->
//            if (!b) {
//                val videoUrl = et_video_url.text.toString().trim()
//                if (videoUrl.isNotEmpty()) {
//                    ViewUtil.hideKeyboard(et_video_url)
//                    binding.viewModel?.checkYoutubeLink(videoUrl)
//                }
//            }
//        }
    }
}