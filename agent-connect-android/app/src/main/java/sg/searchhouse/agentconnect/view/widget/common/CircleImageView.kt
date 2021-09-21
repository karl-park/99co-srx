package sg.searchhouse.agentconnect.view.widget.common

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.util.ImageUtil

//TODO: to refine this circle image view in future
class CircleImageView : AppCompatImageView {

    var url: String = ""
        set(value) {
            field = value
            Glide.with(this)
                .load(ImageUtil.maybeAppendBaseUrl(context, url))
                .placeholder(R.drawable.ic_agent_photo_placeholder)
                .error(R.drawable.ic_agent_photo_placeholder)
                .fitCenter()
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(this)
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        //get attrs lists from attrs.xml
        val attrArray = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
        //get attr url
        val url = attrArray.getString(R.styleable.CircleImageView_url)

        url?.let {
            print(url)
            Glide.with(this)
                .load(ImageUtil.maybeAppendBaseUrl(context, url))
                .placeholder(R.drawable.ic_agent_photo_placeholder)
                .error(R.drawable.ic_agent_photo_placeholder)
                .fitCenter()
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(this)
        }

        attrArray.recycle()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


}