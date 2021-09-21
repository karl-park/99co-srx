package sg.searchhouse.agentconnect.view.activity.base

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.layout_activity_parent.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.BgToastBinding
import sg.searchhouse.agentconnect.databinding.LayoutFeedbackMessageBinding
import sg.searchhouse.agentconnect.dependency.component.DaggerViewComponent
import sg.searchhouse.agentconnect.dependency.component.ViewComponent
import sg.searchhouse.agentconnect.dependency.module.ViewModule
import sg.searchhouse.agentconnect.dsl.launchActivityV2
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.PaymentPurpose
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.event.agent.NotifyPaymentSuccessEvent
import sg.searchhouse.agentconnect.event.agent.NotifyRetrieveConfigEvent
import sg.searchhouse.agentconnect.event.app.*
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.view.activity.auth.SignInActivity
import sg.searchhouse.agentconnect.view.activity.main.MainActivity
import sg.searchhouse.agentconnect.view.activity.main.SplashActivity
import sg.searchhouse.agentconnect.view.fragment.auth.NonSubscriberDialogFragment
import sg.searchhouse.agentconnect.view.widget.common.ActivityParentLayout
import sg.searchhouse.agentconnect.viewmodel.base.BaseViewModel
import javax.inject.Inject

// ATTENTION Please sub-class your activity on ViewModelActivity (with ViewModel binding)
// or ClassicActivity (without ViewModel binding) instead
abstract class BaseActivity(private val isSliding: Boolean = false) : AppCompatActivity() {
    // Please use listenRxBus method to listen RX event
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var viewModel: BaseViewModel

    private lateinit var viewComponent: ViewComponent

    @Inject
    lateinit var dialogUtil: DialogUtil

    private var isActivityVisible: Boolean = false
    private var isSessionRemoved: Boolean = false

    private var nonSubscriberDialog: NonSubscriberDialogFragment? = null

    private var activityParentLayout: ActivityParentLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewComponent =
            DaggerViewComponent.builder().viewModule(ViewModule(this)).build()
        viewComponent.inject(this)
        viewModel = ViewModelProvider(this).get(BaseViewModel::class.java)
        if (isSliding) {
            this.overridePendingTransition(R.anim.activity_slide_up, R.anim.fade_out)
        }
        setupRxCompositeDisposable()
        listenLocalRxBuses()
        observeBaseLiveData()
    }

    @SuppressLint("RestrictedApi")
    protected fun maybeSetupActionBar(toolbar: Toolbar?) {
        toolbar?.let {
            setSupportActionBar(toolbar)
            supportActionBar?.let {
                it.setDefaultDisplayHomeAsUpEnabled(true)
                it.setDisplayShowHomeEnabled(true)
            }
        }
    }

    private fun listenLocalRxBuses() {
        // Listen to global events
        // In order to listen to activity specific event, add new disposable into compositeDisposable on that activity
        // Example implementation: MainActivity

        // Listen sign out
        listenRxBus(SignOutEvent::class.java) {
            if (isActivityVisible) {
                if (it.isUserAction) {
                    viewModel.logout()
                } else {
                    if (!isSessionRemoved) {
                        removeSession()
                    }
                }
            }
        }

        // Listen show message
        listenRxBus(ShowMessageEvent::class.java) {
            if (isActivityVisible) {
                showMessage(it.message)
            }
        }

        // Listen show message by Res ID
        listenRxBus(ShowMessageResEvent::class.java) {
            if (isActivityVisible) {
                showMessage(it.messageResId)
            }
        }

        listenRxBus(NotifyPaymentSuccessEvent::class.java) {
            if (it.paymentPurpose == PaymentPurpose.SUBSCRIPTION) {
                nonSubscriberDialog?.dismissNonSubscriberDialog()
                viewModel.getConfig()
            }
        }

        listenRxBus(LaunchNonSubscriberPromptEvent::class.java) {
            if (isActivityVisible) {
                showSubscriberPrompt(allowDismiss = it.allowDismiss, module = it.module)
            }
        }

        listenRxBus(ShowFeedbackMessageEvent::class.java) { event ->
            if (isActivityVisible) {
                event.message?.run {
                    showFeedbackMessage(this)
                } ?: event.messageResId?.run {
                    showFeedbackMessage(getString(this))
                }

            }
        }
    }

    private fun setupRxCompositeDisposable() {
        compositeDisposable = CompositeDisposable()
    }

    private fun observeBaseLiveData() {
        viewModel.apiCallResult.observeNotNull(this) {
            if (isActivityVisible) {
                when (it.key) {
                    ApiStatus.StatusKey.SUCCESS -> {
                        removeSession()
                    }
                    ApiStatus.StatusKey.FAIL -> {
                        showMessage(it.error?.error)
                    }
                    else -> {
                        //do nothing
                    }
                }
            }
        }

        viewModel.registerTokenResult.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.FAIL -> {
                    showMessage(it?.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.getConfigStatus.observeNotNull(this) { apiStatus ->
            when (apiStatus.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    apiStatus.body?.config?.run {
                        SessionUtil.setSubscriptionConfig(this)
                        RxBus.publish(NotifyRetrieveConfigEvent(this.subscriptionType))
                    }
                }
                ApiStatus.StatusKey.FAIL -> {
                    showMessage(apiStatus.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }
    }

    fun deleteAllTables() {
        viewModel.deleteAllTables()
    }

    private fun removeSession() {
        deleteAllTables()
        //remove cookie and user
        SessionUtil.removePrefs(this)
        //Flag session removed or not
        isSessionRemoved = true
        //direct to sign in activity
        finishAffinity()
        launchActivity(SignInActivity::class.java)
    }

    override fun onPause() {
        isActivityVisible = false
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        isActivityVisible = true
    }

    // TODO: Dispose at onPause when free
    // Ref: https://lorentzos.com/you-should-unsubscribe-onpause-51b3db60ce48
    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    fun <T> listenRxBus(klazz: Class<T>, onNext: (T) -> Unit) {
        val disposable = RxBus.listen(klazz).subscribe {
            onNext.invoke(it)
        }
        compositeDisposable.add(disposable)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @Deprecated("Use `launchActivityV2`")
    fun launchActivity(classy: Class<*>, extras: Bundle? = null) {
        val intent = Intent(this, classy)
        extras?.let { intent.putExtras(it) }
        this.startActivity(intent)
    }

    @Deprecated("Use `launchActivityForResultV2`")
    fun launchActivityForResult(classy: Class<*>, extras: Bundle? = null, requestCode: Int) {
        val intent = Intent(this, classy)
        extras?.let { intent.putExtras(it) }
        this.startActivityForResult(intent, requestCode)
    }

    override fun finish() {
        super.finish()
        if (isSliding) {
            overridePendingTransition(R.anim.fade_in, R.anim.activity_slide_down)
        }
    }

    private fun showMessage(message: String?) {
        message?.let {
            showMessageCore(it)
        } ?: run {
            ErrorUtil.handleError(this, R.string.exception_empty_message)
        }
    }

    private fun showMessageCore(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        val binding = DataBindingUtil.inflate<BgToastBinding>(
            LayoutInflater.from(this),
            R.layout.bg_toast,
            null,
            false
        )
        binding.text = message
        toast.view = binding.root
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    protected fun performRequestsAfterLogin() {
        generateFCMToken()
        launchActivityV2<MainActivity>()
        finish()
    }

    protected fun getLoggedInUserConfig() {
        //Note: after update subscription, wait 15 minutes to update
        viewModel.getConfig()
    }

    private fun generateFCMToken() {
        if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            val fireBaseInstance = FirebaseInstanceId.getInstance().instanceId
            fireBaseInstance.addOnSuccessListener { viewModel.registerToken(it.token) }
            fireBaseInstance.addOnFailureListener { exception ->
                exception.printStackTrace()
                showMessage(R.string.msg_failed_to_generate_token)
            }
        } else {
            dialogUtil.showNotificationInfoDialog()
        }
    }

    protected fun showSubscriberPrompt(
        allowDismiss: Boolean? = null,
        module: AccessibilityEnum.AdvisorModule? = null
    ) {
        if (nonSubscriberDialog == null || nonSubscriberDialog?.isDialogShowing() == false) {
            nonSubscriberDialog =
                NonSubscriberDialogFragment.newInstance(
                    allowDismiss,
                    module
                )
            nonSubscriberDialog?.show(supportFragmentManager, NonSubscriberDialogFragment.TAG)
        }
    }

    private fun showMessage(@StringRes messageResId: Int) {
        showMessage(getString(messageResId))
    }


    private fun showFeedbackMessage(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        val binding = DataBindingUtil.inflate<LayoutFeedbackMessageBinding>(
            LayoutInflater.from(this),
            R.layout.layout_feedback_message,
            null,
            false
        )
        binding.message = message
        toast.view = binding.root
        toast.show()
    }


    private val parentLayoutExemptedClasses =
        listOf(SplashActivity::class.java, SignInActivity::class.java)

    fun maybeSetupActivityParentLayout() {
        checkActivityParentLayout {
            activityParentLayout = layout_activity_parent.parent as ActivityParentLayout
        }
    }

    // Ensure activity layout surrounded by `ActivityParentLayout`
    // For home report download progress feature
    private fun checkActivityParentLayout(onActivityParentLayoutApplicable: () -> Unit) {
        val simpleClassName = this.localClassName.substringAfterLast(".")
        val isExempted = parentLayoutExemptedClasses.any { simpleClassName == it.simpleName }
        if (isExempted) return
        if (layout_activity_parent == null) {
            throw IllegalArgumentException("Make sure to wrap ActivityParentLayout around your activity root layout!")
        } else {
            onActivityParentLayoutApplicable.invoke()
        }
    }
}