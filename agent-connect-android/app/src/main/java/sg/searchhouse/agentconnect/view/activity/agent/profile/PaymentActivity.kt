package sg.searchhouse.agentconnect.view.activity.agent.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityPaymentBinding
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.PaymentPurpose
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.SubscriptionCreditSource
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.event.agent.NotifyPaymentSuccessEvent
import sg.searchhouse.agentconnect.util.DialogUtil
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.view.activity.base.BaseActivity

class PaymentActivity : BaseActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private var dialogProgress: AlertDialog? = null
    private var sourceScreen: SubscriptionCreditSource? = null
    private var paymentPurpose: PaymentPurpose? = null

    companion object {
        const val EXTRA_KEY_PAYMENT_LINK = "EXTRA_KEY_PAYMENT_LINK"
        private const val EXTRA_KEY_SOURCE_SCREEN = "EXTRA_KEY_SOURCE_SCREEN"
        private const val EXTRA_KEY_PAYMENT_PURPOSE = "EXTRA_KEY_PAYMENT_PURPOSE"

        fun launch(
            activity: BaseActivity,
            paymentPurpose: PaymentPurpose?,
            paymentLink: String,
            source: SubscriptionCreditSource?
        ) {
            val intent = Intent(activity, PaymentActivity::class.java)
            intent.putExtra(EXTRA_KEY_PAYMENT_LINK, paymentLink)
            paymentPurpose?.let { intent.putExtra(EXTRA_KEY_PAYMENT_PURPOSE, paymentPurpose) }
            source?.let { intent.putExtra(EXTRA_KEY_SOURCE_SCREEN, it) }
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment)
        binding.lifecycleOwner = this
        setSupportActionBar(binding.toolbar)
        getIntentData()
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun getIntentData() {
        intent.hasExtra(EXTRA_KEY_SOURCE_SCREEN)
            .let {
                sourceScreen =
                    intent.getSerializableExtra(EXTRA_KEY_SOURCE_SCREEN) as SubscriptionCreditSource
            }

        intent.getStringExtra(EXTRA_KEY_PAYMENT_LINK)?.let {
            dialogProgress = dialogUtil.showProgressDialog(R.string.progress_dialog_message)
            setupWebView(it)
        } ?: ErrorUtil.handleError("Missing payment link in payment activity")

        intent.getSerializableExtra(EXTRA_KEY_PAYMENT_PURPOSE)?.run {
            paymentPurpose = this as PaymentPurpose
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(paymentUrl: String) {
        binding.webViewPayment.settings.javaScriptEnabled = true
        binding.webViewPayment.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let {
                    Log.i("LOADING URL", it)
                    view?.loadUrl(it)
                }
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                //TODO in progress of handling success url
                dismissProgressDialog()

                url?.let {
                    Log.i("LOADED URL", it)

                    if (it.contains("/receipt/")) {
                        showPaymentSuccessDialog()
                    } else if (it.contains("/payment-response?") && it.contains("mobileMsg")) {
                        Uri.parse(it)?.getQueryParameter("mobileMsg")?.let { message ->
                            showPaymentSuccessDialog(message)
                        }
                    }
                }
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                dismissProgressDialog()
            }

        }
        binding.webViewPayment.loadUrl(paymentUrl)
    }

    private fun dismissProgressDialog() {
        dialogProgress?.let { alertDialog ->
            if (alertDialog.isShowing) {
                alertDialog.dismiss()
            }
        }
    }

    private fun showPaymentSuccessDialog(message: String? = null) {
        var paymentMessage = getString(R.string.msg_payment_success)
        message?.let { paymentMessage = it }
        DialogUtil(this).showActionDialog(
            paymentMessage
        ) {
            backToPreviousScreen()
        }
    }

    private fun directToAgentProfile() {
        val source = sourceScreen ?: return
        val paymentPurpose = paymentPurpose ?: return
        RxBus.publish(NotifyPaymentSuccessEvent(source = source, paymentPurpose = paymentPurpose))

        val intent = Intent(this, AgentProfileActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        this.finish()
    }

    private fun closePaymentScreen() {
        val source = sourceScreen ?: return
        val paymentPurpose = paymentPurpose ?: return
        RxBus.publish(NotifyPaymentSuccessEvent(source = source, paymentPurpose = paymentPurpose))
        this.finish()
    }

    private fun backToPreviousScreen() {
        when (sourceScreen) {
            SubscriptionCreditSource.NON_SUBSCRIBER_PROMPT -> closePaymentScreen()
            SubscriptionCreditSource.AGENT_CV -> directToAgentProfile()
            SubscriptionCreditSource.MY_LISTING_FEATURE -> closePaymentScreen()
            SubscriptionCreditSource.SPONSOR_LISTING -> closePaymentScreen()
        }
    }

    override fun onBackPressed() {
        when (sourceScreen) {
            SubscriptionCreditSource.NON_SUBSCRIBER_PROMPT -> finish()
            SubscriptionCreditSource.AGENT_CV -> directToAgentProfile()
            SubscriptionCreditSource.MY_LISTING_FEATURE -> finish()
            SubscriptionCreditSource.SPONSOR_LISTING -> finish()
        }
    }
}