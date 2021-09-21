package sg.searchhouse.agentconnect.enumeration.app

import androidx.annotation.StringRes
import sg.searchhouse.agentconnect.R

class CommunityPostEnum {
    enum class PromoteType(@StringRes val label: Int) {
        POST(R.string.community_post_promote_type_post),
        LISTING(R.string.community_post_promote_type_listing)
    }

    enum class Target(@StringRes val label: Int, val value: Int) {
        COMMUNITY(R.string.community_post_target_community, 1),
        HYPER(R.string.community_post_target_hyper, 2)
    }
}