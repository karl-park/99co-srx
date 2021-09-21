package sg.searchhouse.agentconnect.view.widget.listing.create

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.util.NumberUtil

class ListingQualityBar : LinearLayout {

    private val percentagePerBlock: Int = 20
    var showLabel: Boolean = false
    var quality: String? = null
        set(value) {
            field = value
            value?.let {
                if (NumberUtil.isNaturalNumber(it)) {
                    removeAllViews()
                    showQualityBar(it.toInt())
                }
            }
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        //get attrs lists from attrs.xml
        val attrArray = context.obtainStyledAttributes(attrs, R.styleable.ListingQualityBar)
        //get attr url
        val quality = attrArray.getString(R.styleable.ListingQualityBar_quality)
        showLabel = attrArray.getBoolean(R.styleable.ListingQualityBar_showLabel, false)
        quality?.let {
            if (NumberUtil.isNaturalNumber(it)) {
                removeAllViews()
                showQualityBar(it.toInt())
            }
        }
        attrArray.recycle()
    }

    private fun showQualityBar(listingQuality: Int) {
        if (showLabel) {
            val label = TextView(context, null, 0, R.style.SmallBodyGray)
            label.setText(R.string.label_ad_quality)
            addView(label)
        }

        for (i in 1..5) {
            //OUTER VIEW
            val outerView = LinearLayout(context)
            val outerViewWidth =
                context.resources.getDimensionPixelSize(R.dimen.size_width_quality_listing)
            val params = LayoutParams(outerViewWidth, LayoutParams.MATCH_PARENT)
            if (i < 5) {
                params.marginEnd = context.resources.getDimensionPixelSize(R.dimen.margin_4)
            }
            params.marginStart = when {
                i == 1 && showLabel -> context.resources.getDimensionPixelSize(R.dimen.margin_4)
                else -> 0
            }
            outerView.layoutParams = params
            outerView.background = ContextCompat.getDrawable(
                context,
                R.drawable.view_rounded_quality_listing
            )
            outerView.backgroundTintList =
                ContextCompat.getColorStateList(context, R.color.light_gray)


            //INNER VIEW
            val innerView = LinearLayout(context)
            innerView.background = ContextCompat.getDrawable(
                context,
                R.drawable.view_rounded_quality_listing
            )
            innerView.backgroundTintList =
                ContextCompat.getColorStateList(
                    context,
                    R.color.quality_listing_filled_color
                )

            when {
                listingQuality >= i * percentagePerBlock -> innerView.layoutParams =
                    LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                listingQuality < (i - 1) * percentagePerBlock -> innerView.layoutParams =
                    LayoutParams(0, LayoutParams.MATCH_PARENT)
                else -> {
                    val width = ((listingQuality % 20) * outerViewWidth) / percentagePerBlock
                    innerView.layoutParams =
                        LayoutParams(
                            width,
                            LayoutParams.MATCH_PARENT
                        )
                }
            }

            outerView.addView(innerView)
            //adding outer view to parent linear layout
            addView(outerView)
        }
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}