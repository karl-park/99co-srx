package sg.searchhouse.agentconnect.view.fragment.auth

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.dialog_fragment_forgot_password.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentForgotPasswordBinding
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.viewmodel.fragment.auth.ForgotPasswordViewModel
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelDialogFragment

class ForgotPasswordDialogFragment :
    ViewModelDialogFragment<ForgotPasswordViewModel, DialogFragmentForgotPasswordBinding>() {

    private lateinit var mContext: Context

    companion object {
        const val TAG: String = "FORGOT_PASSWORD_DIALOG"
        private const val EXTRA_KEY_EMAIL = "EXTRA_KEY_EMAIL"

        fun newInstance(email: String): ForgotPasswordDialogFragment {
            val fragment = ForgotPasswordDialogFragment()
            val bundle = Bundle()
            bundle.putString(EXTRA_KEY_EMAIL, email)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupParams()
        observeLiveData()
        handleListeners()
    }

    private fun setupParams() {
        val bundle = arguments ?: return
        viewModel.email.value = bundle.getString(EXTRA_KEY_EMAIL, "")
    }

    private fun observeLiveData() {
        viewModel.apiStatusLiveData.observe(viewLifecycleOwner, Observer { apiStatus ->
            when (apiStatus.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    ViewUtil.showMessage(apiStatus.body?.data?.message)
                    dialog?.dismiss()
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(apiStatus.error?.error)
                }
                else -> {
                    //Do nothing
                }
            }
        })

        viewModel.errorEmail.observe(viewLifecycleOwner, Observer { error ->
            when (error) {
                null -> {
                    et_email_address.setEndIconTintList(
                        ContextCompat.getColorStateList(
                            mContext,
                            R.color.gray
                        )
                    )
                }
                else -> {
                    et_email_address.setEndIconTintList(
                        ContextCompat.getColorStateList(
                            mContext,
                            R.color.red
                        )
                    )
                }
            }
        })
    }

    private fun handleListeners() {
        toolbar.setOnClickListener { dialog?.dismiss() }
    }

    override fun onStart() {
        super.onStart()
        setupFullScreenWindow()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_forgot_password
    }

    override fun getViewModelClass(): Class<ForgotPasswordViewModel> {
        return ForgotPasswordViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}