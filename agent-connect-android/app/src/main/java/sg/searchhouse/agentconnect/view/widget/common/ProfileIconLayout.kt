package sg.searchhouse.agentconnect.view.widget.common

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.LayoutProfileIconBinding
import sg.searchhouse.agentconnect.util.StringUtil
import sg.searchhouse.agentconnect.util.ViewUtil

//TODO: to refine this after 1.9 release
class ProfileIconLayout constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {

    var binding: LayoutProfileIconBinding =
        DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_profile_icon,
            this,
            true
        )

    fun setNameForInitial(nameForInitial: String) {
        binding.tvIcon.text = StringUtil.getInitialUpperCase(nameForInitial)
    }

    // Set image URL and name/title as uppercase initial for empty placeholder
    fun setImageUrl(imageUrl: String) {
        binding.showImage = imageUrl.isNotEmpty()
        if (binding.showImage == true) {
            binding.ivCircleImage.url = imageUrl
            binding.showCircleImage = true
        }
    }

    fun populateByInitialLetter(
        imageUrl: String,
        name: String,
        showNormalImage: Boolean = false
    ) {
        binding.showImage = imageUrl.isNotEmpty()
        if (binding.showImage == true) {
            //TODO: it's not a good solution to show image.so clean up this code after phase 1.9.
            if (showNormalImage) {
                binding.normalImageUrl = imageUrl
                binding.showCircleImage = false
            } else {
                binding.ivCircleImage.url = imageUrl
                binding.showCircleImage = true
            }
        } else {
            val initialLetter = StringUtil.getInitialUpperCase(name)
            binding.tvIcon.text = initialLetter
            val generatedColor = if (!TextUtils.isEmpty(initialLetter)) {
                ViewUtil.getColorByAlphabetCharacter(context, initialLetter[0])
            } else {
                ViewUtil.generateRandomColor(context)
            }
            DrawableCompat.setTint(binding.container.background, generatedColor)
        }
    }

}