package sg.searchhouse.agentconnect.view.activity.auth

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_user_authentication.*
import kotlinx.android.synthetic.main.layout_login.*
import kotlinx.android.synthetic.main.layout_signup.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.ApiConstant
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.databinding.ActivityUserAuthenticationBinding
import sg.searchhouse.agentconnect.model.api.auth.LoginResponseData
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus.StatusKey.FAIL
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus.StatusKey.SUCCESS
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.fragment.auth.ForgotPasswordDialogFragment
import sg.searchhouse.agentconnect.view.fragment.auth.MobileVerificationDialogFragment
import sg.searchhouse.agentconnect.viewmodel.activity.auth.SignInViewModel

class SignInActivity : ViewModelActivity<SignInViewModel, ActivityUserAuthenticationBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeLiveData()
        handleListeners()
    }

    private fun handleListeners() {
        //On Click Listeners
        btn_customer_service.setOnClickListener {
            IntentUtil.dialPhoneNumber(this, AppConstant.CONTACT_CUSTOMER_SERVICE)
        }

        tv_forgot_password.setOnClickListener {
            val email = viewModel.email.value ?: ""
            ForgotPasswordDialogFragment.newInstance(email)
                .show(supportFragmentManager, ForgotPasswordDialogFragment.TAG)
        }
    }

    private fun observeLiveData() {
        viewModel.loginStatusLiveData.observe(this, Observer { apiStatus ->
            when (apiStatus.key) {
                SUCCESS -> {
                    val loginResult = apiStatus.body ?: return@Observer
                    when (loginResult.result) {
                        ApiConstant.SIGN_IN_STATUS_SUCCESS -> performLogin()
                        ApiConstant.SIGN_IN_STATUS_REQUIRE_OTP -> showConfirmationDialog(loginResult.loginResponseData)
                        ApiConstant.SIGN_IN_STATUS_RESET -> showResetAccountDialog(loginResult.userId)
                    }
                }
                FAIL -> {
                    ViewUtil.showMessage(apiStatus.error?.error)
                }
                else -> {
                    //Do nothing for other status
                }
            }
        })

        viewModel.resetDeviceStatus.observe(this, Observer { apiStatus ->
            when (apiStatus.key) {
                SUCCESS -> {
                    ViewUtil.showMessage(R.string.msg_reset_account_success)
                    viewModel.loginAction()
                }
                FAIL -> {
                    ViewUtil.showMessage(apiStatus.error?.error)
                }
                else -> {
                    //do nothing...
                }
            }
        })

        //Checking for errors
        viewModel.errorEmail.observe(this, Observer {
            when (it) {
                null -> {
                    text_layout_username.setEndIconTintList(
                        ContextCompat.getColorStateList(
                            this,
                            R.color.gray
                        )
                    )
                }
                else -> {
                    text_layout_username.setEndIconTintList(
                        ContextCompat.getColorStateList(
                            this,
                            R.color.red
                        )
                    )
                }
            }
        })

        viewModel.errorPassword.observe(this, Observer {
            when (it) {
                null -> {
                    text_layout_password.setEndIconTintList(
                        ContextCompat.getColorStateList(
                            this,
                            R.color.gray
                        )
                    )
                }
                else -> {
                    text_layout_password.setEndIconTintList(
                        ContextCompat.getColorStateList(
                            this,
                            R.color.red
                        )
                    )
                }
            }
        })

        viewModel.errorCeaNo.observe(this, Observer {
            when (it) {
                null -> {
                    text_layout_cea.setEndIconTintList(
                        ContextCompat.getColorStateList(
                            this,
                            R.color.gray
                        )
                    )
                }
                else -> {
                    text_layout_cea.setEndIconTintList(
                        ContextCompat.getColorStateList(
                            this,
                            R.color.red
                        )
                    )
                }
            }
        })

        viewModel.errorMobile.observe(this, Observer {
            when (it) {
                null -> {
                    text_layout_mobile.setEndIconTintList(
                        ContextCompat.getColorStateList(
                            this,
                            R.color.gray
                        )
                    )
                }
                else -> {
                    text_layout_mobile.setEndIconTintList(
                        ContextCompat.getColorStateList(
                            this,
                            R.color.red
                        )
                    )
                }
            }
        })
    }

    fun performLogin() {
        //FOR SRX STREET SINE USER ONLY
        val email = viewModel.email.value ?: ""
        if (AuthUtil.isStreetSineUser(email)) {
            val password = viewModel.password.value ?: ""
            SessionUtil.setStreetSineUserAuth(this, email, password)
        }
        performRequestsAfterLogin()
    }

    private fun showConfirmationDialog(loginResponseData: LoginResponseData?) {
        loginResponseData?.run {
            MobileVerificationDialogFragment.launch(supportFragmentManager, this)
        }
    }

    fun showResetAccountDialog(userId: String) {
        DialogUtil(this)
            .showActionDialog(R.string.msg_reset_account) {
                viewModel.resetAccount(userId)
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionUtil.REQUEST_CODE_CALL -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    IntentUtil.dialPhoneNumber(this, AppConstant.CONTACT_CUSTOMER_SERVICE)
                }
                return
            }
        }//end of when
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_user_authentication
    }

    override fun getViewModelClass(): Class<SignInViewModel> {
        return SignInViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return null
    }
}