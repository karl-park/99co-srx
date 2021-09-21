package sg.searchhouse.agentconnect.view.widget.agent.cv

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.LayoutCvSocialMediaBinding

class CvSocialMedia(context: Context, attributeSet: AttributeSet? = null) :
    LinearLayout(context, attributeSet) {

    val binding: LayoutCvSocialMediaBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.layout_cv_social_media,
        this,
        true
    )
}