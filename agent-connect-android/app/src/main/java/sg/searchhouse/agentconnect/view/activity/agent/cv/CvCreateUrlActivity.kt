package sg.searchhouse.agentconnect.view.activity.agent.cv

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityCvCreateUrlBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.viewmodel.activity.agent.cv.CvCreateUrlViewModel

class CvCreateUrlActivity : ViewModelActivity<CvCreateUrlViewModel, ActivityCvCreateUrlBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showKeyboard()
        observeLiveData()
        handleViewListeners()
    }

    private fun showKeyboard() {
        binding.etCvUrl.requestFocus()
        binding.etCvUrl.post { ViewUtil.showKeyboard(binding.etCvUrl) }
    }

    private fun observeLiveData() {
        viewModel.checkCvUrlStatus.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.saveAgentStatus.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    showAgentCv()
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it?.error?.error)
                }
                else -> {
                    //Do nothing
                }
            }
        }
    }

    private fun handleViewListeners() {
        binding.btnSaveAndContinue.setOnClickListener { viewModel.checkIsPublicUrlAvailable() }
    }

    private fun showAgentCv() {
        val userPO = SessionUtil.getCurrentUser() ?: return
        AgentCvActivity.launch(activity = this, agentId = userPO.id)
        finish()
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_cv_create_url
    }

    override fun getViewModelClass(): Class<CvCreateUrlViewModel> {
        return CvCreateUrlViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }
}