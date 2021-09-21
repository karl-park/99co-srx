package sg.searchhouse.agentconnect.view.fragment.listing.portal

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentPgImportBinding
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.portal.PGImportDialogViewModel
import androidx.lifecycle.Observer
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.GetPortalAPIsResponse
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalAccountPO
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus.StatusKey.FAIL
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.listing.portal.PortalListingsActivity
import sg.searchhouse.agentconnect.view.activity.listing.portal.PortalSettingsActivity
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelDialogFragment
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus.StatusKey.SUCCESS as API_SUCCESS

class PGImportDialogFragment :
    ViewModelDialogFragment<PGImportDialogViewModel, DialogFragmentPgImportBinding>() {

    companion object {
        private const val TAG_PORTAL_DIALOG_FRAGMENT = "TAG_PG_IMPORT_DIALOG_FRAGMENT"
        private const val ARGUMENT_KEY_PORTAL_API_RESPONSE = "ARGUMENT_KEY_PORTAL_API_RESPONSE"

        fun newInstance(apiResponse: GetPortalAPIsResponse): PGImportDialogFragment {
            val dialogFragment = PGImportDialogFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARGUMENT_KEY_PORTAL_API_RESPONSE, apiResponse)
            dialogFragment.arguments = bundle
            return dialogFragment
        }

        fun launch(fragmentManager: FragmentManager, apiResponse: GetPortalAPIsResponse) {
            newInstance(apiResponse).show(fragmentManager, TAG_PORTAL_DIALOG_FRAGMENT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupArgumentParams()
        setupPortalSession()
        showKeyboard()
        observeLiveData()
        handleListeners()
    }

    private fun setupArgumentParams() {
        val bundle = arguments ?: return
        bundle.getSerializable(ARGUMENT_KEY_PORTAL_API_RESPONSE)?.run {
            viewModel.portalApiResponse.value = this as GetPortalAPIsResponse
        } ?: ErrorUtil.handleError("missing portal api response")
    }

    private fun setupPortalSession() {
        //GET portal mode
        SessionUtil.getListingPortalMode()?.run { viewModel.portalMode.value = this }
        //Saved remember me checked
        SessionUtil.getPortalAuthRememberMe().let { isRememberMe ->
            if (isRememberMe) {
                activity?.run {
                    viewModel.email.value = SessionUtil.getPortalAuthenticationEmail(this)
                    viewModel.password.value = SessionUtil.getPortalAuthenticationPassword(this)
                }
            }
            viewModel.isRememberMeChecked.value = isRememberMe
        }
    }

    private fun showKeyboard() {
        binding.etEmailAddress.requestFocus()
        binding.etEmailAddress.post { ViewUtil.showKeyboard(binding.etEmailAddress) }
    }

    private fun observeLiveData() {
        viewModel.loginStatusLiveData.observe(viewLifecycleOwner, Observer { apiStatus ->
            when (apiStatus.key) {
                API_SUCCESS -> {
                    val response = apiStatus.body ?: return@Observer
                    handlePortalAuth()
                    showPortalListingsScreen(response.portalAccount)
                }
                FAIL -> {
                    ViewUtil.showMessage(apiStatus.error?.error)
                }
                else -> {
                    //Do nothing
                }
            }
        })

        viewModel.clientLoginStatusLiveData.observe(viewLifecycleOwner, Observer { apiStatus ->
            when (apiStatus.key) {
                API_SUCCESS -> {
                    val response = apiStatus.body ?: return@Observer
                    handlePortalAuth()
                    showPortalListingsScreen(response.portalAccount)
                }
                FAIL -> {
                    ViewUtil.showMessage(apiStatus.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        })

        viewModel.isRememberMeChecked.observe(viewLifecycleOwner, Observer { isChecked ->
            if (isChecked != null) {
                SessionUtil.setPortalAuthRememberMe(isChecked)
            }
        })
    }

    private fun handlePortalAuth() {
        val isChecked = viewModel.isRememberMeChecked.value ?: return
        val activity = activity ?: return
        if (isChecked) {
            val email = viewModel.email.value ?: return
            val password = viewModel.password.value ?: return
            SessionUtil.setPortalAuthentication(activity, email, password)
        } else {
            SessionUtil.removePortalAuthentication(activity)
        }
    }

    private fun showPortalListingsScreen(portalAccount: PortalAccountPO) {
        dialog?.dismiss()
        activity?.run {
            if (portalAccount.syncFrequency == 0 || portalAccount.syncOption == 0) {
                PortalSettingsActivity.launch(
                    activity = this,
                    source = PortalSettingsActivity.Source.PORTAL_LOGIN,
                    portalAPIsResponse = viewModel.portalApiResponse.value,
                    portalAccountPO = portalAccount,
                    email = viewModel.email.value,
                    password = viewModel.password.value
                )
            } else {
                PortalListingsActivity.launch(
                    activity = this,
                    portalApiResponse = viewModel.portalApiResponse.value,
                    portalAccountPO = portalAccount
                )
            }
            this.finish()
        }
    }

    private fun handleListeners() {
        binding.toolbar.setNavigationOnClickListener {
            dialog?.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        setupFullScreenWindow()
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_pg_import
    }

    override fun getViewModelClass(): Class<PGImportDialogViewModel> {
        return PGImportDialogViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}
