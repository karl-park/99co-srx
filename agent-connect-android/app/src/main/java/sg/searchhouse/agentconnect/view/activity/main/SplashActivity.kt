package sg.searchhouse.agentconnect.view.activity.main

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.ApiConstant
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.constant.SharedPreferenceKey
import sg.searchhouse.agentconnect.databinding.ActivitySplashScreenBinding
import sg.searchhouse.agentconnect.model.app.NotificationParams
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.activity.auth.SignInActivity
import sg.searchhouse.agentconnect.view.activity.listing.portal.PortalListingsActivity
import sg.searchhouse.agentconnect.view.activity.chat.ChatMessagingActivity
import sg.searchhouse.agentconnect.viewmodel.activity.main.SplashViewModel

class SplashActivity : ViewModelActivity<SplashViewModel, ActivitySplashScreenBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleSplashScreen()
        observeLiveData()
    }

    private fun handleSplashScreen() {
        val isUserLoggedIn = SessionUtil.getCookies().size > 0
        val notificationParam = NotificationParams(bundle = intent.extras)
        if (notificationParam.getNotificationType() == null) {
            //note -> directly open the app -> when open app, sometimes, intent.extras is not null
            //so, need to check with notification type include or not, test with kill app and then reopen again
            //TODO: comment showSplashScreen(isUserLoggedIn) method and see what values include in intent.extras bundle
            //DialogUtil(this).showActionDialog("Checked bundle values ${intent.extras}")
            showSplashScreen(isUserLoggedIn)
        } else {
            //note -> extras has values -> FCM notification when app is in background
            setupExtraParams(isUserLoggedIn, notificationParam)
        }
    }

    //Note : for FCM notification when app is in background state
    private fun setupExtraParams(isUserLoggedIn: Boolean, notificationParams: NotificationParams) {
        if (isUserLoggedIn) {
            handleBundleParams(notificationParams)
        } else {
            showSignInScreen()
        }
    }

    private fun showSplashScreen(isUserLoggedIn: Boolean) {
        CoroutineScope(Dispatchers.Main).launch {
            if (!Prefs.getBoolean(SharedPreferenceKey.PREF_SHOW_SPLASH_SCREEN, false)) {
                //set flag
                Prefs.putBoolean(SharedPreferenceKey.PREF_SHOW_SPLASH_SCREEN, true)
                //wait time to direct to next screen
                delay(1500L)
            }
            if (isUserLoggedIn) {
                showMainScreen()
            } else {
                viewModel.email = SessionUtil.getStreetSineUserEmail(this@SplashActivity) ?: ""
                viewModel.password =
                    SessionUtil.getStreetSineUserPassword(this@SplashActivity) ?: ""
                if (!TextUtils.isEmpty(viewModel.email) && !TextUtils.isEmpty(viewModel.password)) {
                    viewModel.loginWithEmail(viewModel.email, viewModel.password)
                } else {
                    showSignInScreen()
                }
            }
        }
    }

    //Note : for FCM notification when app is in background state
    private fun handleBundleParams(notification: NotificationParams) {
        val type = notification.getNotificationType()
        if (type == AppConstant.NOTIFICATION_TYPE_SSM) {
            val conversationId = notification.getConversationId()
            val enquiryId = notification.getEnquiryId()
            val announcement = notification.getAnnouncement()
            when {
                StringUtil.equals(
                    announcement,
                    AppConstant.NOTIFICATION_TYPE_ANNOUNCEMENT_LISTING,
                    ignoreCase = true
                ) -> {
                    showPortalListingsScreen()
                }
                !TextUtils.isEmpty(conversationId.trim()) -> {
                    showMessagingScreen(conversationId)
                }
                !TextUtils.isEmpty(enquiryId.trim()) -> {
                    showEnquiryChatScreen(enquiryId)
                }
            }
        }
    }

    private fun observeLiveData() {
        viewModel.loginStatus.observe(this, Observer { response ->
            when (response.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    val loginResult = response.body ?: return@Observer
                    when (loginResult.result) {
                        ApiConstant.SIGN_IN_STATUS_SUCCESS -> performRequestsAfterLogin()
                        ApiConstant.SIGN_IN_STATUS_RESET -> showResetAccountDialog(loginResult.userId)
                        ApiConstant.SIGN_IN_STATUS_REQUIRE_OTP -> println("do nothing")
                    }
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(response.error?.error)
                }
                else -> {
                    println("do nothing")
                }
            }
        })

        viewModel.resetAccountStatus.observe(this, Observer {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    if (!TextUtils.isEmpty(viewModel.email) && !TextUtils.isEmpty(viewModel.password)) {
                        deleteAllTables()
                        viewModel.loginWithEmail(viewModel.email, viewModel.password)
                    }
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    println("do nothing.")
                }
            }
        })
    }

    private fun showPortalListingsScreen() {
        PortalListingsActivity.launch(this)
        finish()
    }

    private fun showMessagingScreen(conversationId: String) {
        ChatMessagingActivity.launch(this, conversationId = conversationId)
        finish()
    }

    private fun showEnquiryChatScreen(enquiryId: String) {
        MainActivity.launch(this@SplashActivity, enquiryId = enquiryId)
        finish()
    }

    private fun showSignInScreen() {
        startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    private fun showMainScreen() {
        MainActivity.launch(this)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    private fun showResetAccountDialog(userId: String) {
        DialogUtil(this)
            .showActionDialog(R.string.msg_reset_account) {
                viewModel.resetAccount(userId)
            }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_splash_screen
    }

    override fun getViewModelClass(): Class<SplashViewModel> {
        return SplashViewModel::class.java
    }

    override fun bindViewModelXml() {
        //do nothing
    }

    override fun getToolbar(): Toolbar? {
        return null
    }
}