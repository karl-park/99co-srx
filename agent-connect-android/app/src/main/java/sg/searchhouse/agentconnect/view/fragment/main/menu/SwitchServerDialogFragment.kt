package sg.searchhouse.agentconnect.view.fragment.main.menu

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.AppConstant
import sg.searchhouse.agentconnect.databinding.DialogFragmentSwitchServerBinding
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.util.StringUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.main.SplashActivity
import sg.searchhouse.agentconnect.view.adapter.main.ServerUrlAdapter
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.main.menu.SwitchServerViewModel

class SwitchServerDialogFragment :
    ViewModelDialogFragment<SwitchServerViewModel, DialogFragmentSwitchServerBinding>() {

    private lateinit var adapter: ServerUrlAdapter
    private var changedServerUrl: String = ""

    companion object {
        private const val TAG_SWITCH_SERVER_DIALOG = "TAG_SWITCH_SERVER_DIALOG"

        fun newInstance() = SwitchServerDialogFragment()

        fun launch(fm: FragmentManager) {
            newInstance().show(fm, TAG_SWITCH_SERVER_DIALOG)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        handleListeners()
        observeLiveData()
    }

    private fun setupList() {
        adapter = ServerUrlAdapter(SRXServer.values().toList()) { server ->
            changeServer(server = server)
        }
        binding.listServers.layoutManager = LinearLayoutManager(this.activity)
        binding.listServers.adapter = adapter
    }

    private fun handleListeners() {
        binding.toolbar.setNavigationOnClickListener { dialog?.dismiss() }

        binding.btnChange.setOnClickListener {
            val inputServerUrl = viewModel.inputServerUrl.value ?: ""
            if (!TextUtils.isEmpty(inputServerUrl) && StringUtil.isWebUrlValid(inputServerUrl)) {
                viewModel.errorUrl.value = ""
                ViewUtil.hideKeyboard(binding.etInputServer)
                changeServer(inputServerUrl = inputServerUrl)
            } else {
                viewModel.errorUrl.value = "Invalid web url"
            }
        }
    }

    private fun changeServer(server: SRXServer? = null, inputServerUrl: String? = null) {
        when {
            server != null -> {
                changedServerUrl = getString(server.serverUrl)
                viewModel.logout()
            }
            inputServerUrl != null -> {
                changedServerUrl = inputServerUrl
                viewModel.logout()
            }
            else -> {
                ViewUtil.showMessage(R.string.error_msg_select_server)
            }
        }
    }

    private fun observeLiveData() {
        viewModel.logoutStatus.observe(viewLifecycleOwner, Observer {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    SessionUtil.setDebugServerBaseUrl(changedServerUrl)
                    SessionUtil.setServer(AppConstant.SERVER_INSTANCE_CUSTOM)
                    SessionUtil.removeCookieAndUserInfo()
                    ViewUtil.showMessage(R.string.msg_changed_url)
                    restartApp()
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //Do nothing
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        setupFullScreenWindow()
    }

    private fun restartApp() {
        activity?.finishAffinity()
        val intent = Intent(activity, SplashActivity::class.java)
        this.startActivity(intent)
    }

    enum class SRXServer(val value: Int, @StringRes val serverUrl: Int, @StringRes val serverName: Int) {
        PRODUCTION(1, R.string.base_url_production, R.string.label_server_production),
        DEMO1(2, R.string.base_url_demo1, R.string.label_server_demo1),
        DEMO2(3, R.string.base_url_demo2, R.string.label_server_demo2),
        DEMO3(5, R.string.base_url_demo3, R.string.label_server_demo3),
        DEMO4(6, R.string.base_url_demo4, R.string.label_server_demo4),
        DEMO5(7, R.string.base_url_demo5, R.string.label_server_demo5),
        DEMO6(8, R.string.base_url_demo6, R.string.label_server_demo6),
        SRX_TRAINER(4, R.string.base_url_srxtrainer, R.string.label_server_srx_trainer)
    }


    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_switch_server
    }

    override fun getViewModelClass(): Class<SwitchServerViewModel> {
        return SwitchServerViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}