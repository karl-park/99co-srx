package sg.searchhouse.agentconnect.enumeration.app

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import sg.searchhouse.agentconnect.R

enum class MarketDirection(@ColorRes val color: Int, @DrawableRes val arrowDrawable: Int) {
    UP(R.color.green_indicator_up, R.drawable.ic_triangle_up_small),
    NEUTRAL(R.color.green_indicator_up, R.drawable.ic_triangle_neutral),
    DOWN(R.color.red, R.drawable.ic_triangle_down_small)
}