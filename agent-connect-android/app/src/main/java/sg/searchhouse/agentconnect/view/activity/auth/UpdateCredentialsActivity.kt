package sg.searchhouse.agentconnect.view.activity.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_update_credentials.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityUpdateCredentialsBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.model.api.auth.VerifyOtpResponse
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus.StatusKey.*
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.viewmodel.activity.auth.UpdateCredentialsViewModel

class UpdateCredentialsActivity :
    ViewModelActivity<UpdateCredentialsViewModel, ActivityUpdateCredentialsBinding>() {

    companion object {
        private const val EXTRA_KEY_OTP_RESPONSE = "EXTRA_KEY_OTP_RESPONSE"

        fun launch(context: Context, response: VerifyOtpResponse) {
            val intent = Intent(context, UpdateCredentialsActivity::class.java)
            intent.putExtra(EXTRA_KEY_OTP_RESPONSE, response)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtraParam()
        observeLiveData()
    }

    private fun setupExtraParam() {
        if (intent.hasExtra(EXTRA_KEY_OTP_RESPONSE)) {
            viewModel.verifyOTPResponse.value =
                intent.getSerializableExtra(EXTRA_KEY_OTP_RESPONSE) as VerifyOtpResponse
        }
    }

    private fun observeLiveData() {
        viewModel.verifyOTPResponse.observe(this, Observer {
            val response = it ?: return@Observer
            iv_agent_photo.url = response.success.agentPhotoUrl
        })

        viewModel.createAccountStatus.observe(this, Observer { apiStatus ->
            when (apiStatus.key) {
                SUCCESS -> directToDashboard()
                FAIL -> ViewUtil.showMessage(apiStatus.error?.error)
                else -> {
                    //Do nothing
                }
            }
        })

        viewModel.isAgreementChecked.observeNotNull(this) { isChecked ->
            if (isChecked == true) {
                viewModel.updateBtnState.postValue(ButtonState.NORMAL)
            } else {
                viewModel.updateBtnState.postValue(ButtonState.ERROR)
            }
        }
    }

    private fun directToDashboard() {
        performRequestsAfterLogin()
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_update_credentials
    }

    override fun getViewModelClass(): Class<UpdateCredentialsViewModel> {
        return UpdateCredentialsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return null
    }
}