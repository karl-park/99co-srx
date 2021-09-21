package sg.searchhouse.agentconnect.view.fragment.auth

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.dialog_fragment_verification_mobile.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentVerificationMobileBinding
import sg.searchhouse.agentconnect.model.api.auth.LoginResponseData
import sg.searchhouse.agentconnect.model.api.auth.VerifyOtpResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus.StatusKey.*
import sg.searchhouse.agentconnect.util.DateTimeUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.auth.SignInActivity
import sg.searchhouse.agentconnect.view.activity.auth.UpdateCredentialsActivity
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.auth.MobileVerificationViewModel
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus.StatusKey.SUCCESS as API_SUCCESS

class MobileVerificationDialogFragment :
    ViewModelDialogFragment<MobileVerificationViewModel, DialogFragmentVerificationMobileBinding>() {

    //TODO: create custom OTP pin view if have time.currently
    //use https://github.com/mukeshsolanki/android-otpview-pinview

    lateinit var mContext: Context
    lateinit var timerResendOTP: CountDownTimer

    companion object {
        private const val TAG_MOBILE_VERIFICATION = "TAG_MOBILE_VERIFICATION"
        private const val EXTRA_KEY_VERIFY_MOBILE = "EXTRA_KEY_VERIFY_MOBILE"

        fun newInstance(loginResponseData: LoginResponseData): MobileVerificationDialogFragment {
            val dialogFragment = MobileVerificationDialogFragment()
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_KEY_VERIFY_MOBILE, loginResponseData)
            dialogFragment.arguments = bundle
            return dialogFragment
        }

        fun launch(fragmentManager: FragmentManager, loginResponseData: LoginResponseData) {
            newInstance(loginResponseData).show(fragmentManager, TAG_MOBILE_VERIFICATION)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupExtraParams()
        setupOTPViews()
        startCountDownTimer()
        handleListeners()
        observeLiveData()
    }

    private fun setupExtraParams() {
        val bundle = arguments ?: return
        viewModel.loginResponseData.value =
            bundle.getSerializable(EXTRA_KEY_VERIFY_MOBILE) as LoginResponseData

        timerResendOTP = ResendCountDown(180000L, 1000L)
    }

    private fun setupOTPViews() {
        otp_view.requestFocus()
        otp_view.post { ViewUtil.showKeyboard(otp_view) }
    }

    private fun startCountDownTimer() {
        timerResendOTP.start()
        tv_count_down_timer?.visibility = View.VISIBLE
        tv_resend_otp?.isEnabled = false
    }

    private fun handleListeners() {
        tb_verification_mobile.setNavigationOnClickListener { dialog?.dismiss() }
        otp_view.setOtpCompletionListener { otp -> viewModel.otpComplete(otp) }
    }

    private fun observeLiveData() {
        viewModel.verifyMobileStatus.observe(viewLifecycleOwner, Observer { apiStatus ->
            when (apiStatus.key) {
                API_SUCCESS -> {
                    val result = apiStatus.body ?: return@Observer
                    //Cancel count Down
                    cancelCountDownTimer()
                    //dismiss dialog
                    dialog?.dismiss()
                    if (result.success.existingUser) {
                        if (result.success.deviceResetRequired) {
                            // to reset required ... still not found this state.
                            (activity as SignInActivity).showResetAccountDialog(result.data.userId)
                        } else {
                            //direct to dashboard
                            (activity as SignInActivity).performLogin()
                        }
                    } else {
                        //success and direct to update credential screen
                        showUpdateCredentialsScreen(result)
                    }
                }
                FAIL -> {
                    ViewUtil.showMessage(apiStatus.error?.error)
                }
                else -> {
                    //Do nothing
                }
            }
        })

        viewModel.resendOTPStatus.observe(viewLifecycleOwner, Observer { apiStatus ->
            when (apiStatus.key) {
                API_SUCCESS -> {
                    apiStatus.body?.data?.let {
                        ViewUtil.showMessage(R.string.msg_send_otp)
                    }
                }
                FAIL -> {
                    //fail and cancel count down timer
                    cancelCountDownTimer()
                    ViewUtil.showMessage(apiStatus.error?.error)
                }
                ERROR -> {
                    //fail and cancel count down timer
                    cancelCountDownTimer()
                }
                LOADING -> {
                    //start calling api so disable resend button and start count down timer
                    startCountDownTimer()
                }
                else -> {
                    //Do nothing
                }
            }
        })
    }

    private fun showUpdateCredentialsScreen(verifyOTP: VerifyOtpResponse) {
        UpdateCredentialsActivity.launch(mContext, verifyOTP)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onStart() {
        super.onStart()
        setupFullScreenWindow()
    }

    private fun cancelCountDownTimer() {
        timerResendOTP.cancel()
        tv_count_down_timer?.visibility = View.GONE
        tv_resend_otp?.isEnabled = true
    }

    inner class ResendCountDown(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
            tv_count_down_timer?.visibility = View.GONE
            tv_resend_otp?.isEnabled = true
        }

        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            tv_count_down_timer?.text =
                "(${DateTimeUtil.convertMillisecondToMinutesSecondsFormat(millisUntilFinished)})"
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_verification_mobile
    }

    override fun getViewModelClass(): Class<MobileVerificationViewModel> {
        return MobileVerificationViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}