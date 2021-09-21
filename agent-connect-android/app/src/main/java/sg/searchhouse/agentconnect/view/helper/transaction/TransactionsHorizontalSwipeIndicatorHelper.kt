package sg.searchhouse.agentconnect.view.helper.transaction

import com.airbnb.lottie.LottieAnimationView
import com.pixplicity.easyprefs.library.Prefs
import sg.searchhouse.agentconnect.constant.SharedPreferenceKey

object TransactionsHorizontalSwipeIndicatorHelper {
    fun maybeShowGroupIndicator(lottieAnimationView: LottieAnimationView) {
        maybeShowIndicator(
            lottieAnimationView,
            SharedPreferenceKey.PREF_IS_GROUP_TRANSACTIONS_SWIPE_INDICATOR_DISPLAYED
        )
    }

    fun maybeShowProjectIndicator(lottieAnimationView: LottieAnimationView) {
        maybeShowIndicator(
            lottieAnimationView,
            SharedPreferenceKey.PREF_IS_PROJECT_TRANSACTIONS_SWIPE_INDICATOR_DISPLAYED
        )
    }

    private fun maybeShowIndicator(
        lottieAnimationView: LottieAnimationView,
        sharedPreferenceKey: String
    ) {
        if (!Prefs.getBoolean(
                sharedPreferenceKey,
                false
            )
        ) {
            lottieAnimationView.playAnimation()
            Prefs.putBoolean(sharedPreferenceKey, true)
        }
    }
}