package sg.searchhouse.agentconnect.view.activity.listing.portal

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityPortalImportAndSyncSettingsBinding
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.GetPortalAPIsResponse
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PortalAccountPO
import sg.searchhouse.agentconnect.event.listing.portal.UpdatePortalListingsEvent
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.DialogUtil
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.listing.portal.SyncFrequencyAdapter
import sg.searchhouse.agentconnect.view.widget.listing.portal.CardListingSyncOption
import sg.searchhouse.agentconnect.viewmodel.activity.listing.portal.PortalSettingsViewModel

class PortalSettingsActivity :
    ViewModelActivity<PortalSettingsViewModel, ActivityPortalImportAndSyncSettingsBinding>() {

    private lateinit var adapter: SyncFrequencyAdapter
    private lateinit var timerSynchronizingListings: CountDownTimer
    private lateinit var source: Source
    private var dialogProgress: AlertDialog? = null

    companion object {
        private const val EXTRA_KEY_SOURCE = "EXTRA_KEY_SOURCE"
        private const val EXTRA_KEY_PORTAL_ACCOUNT = "EXTRA_KEY_PORTAL_ACCOUNT"
        private const val EXTRA_KEY_EMAIL = "EXTRA_KEY_EMAIL"
        private const val EXTRA_KEY_PASSWORD = "EXTRA_KEY_PASSWORD"
        private const val EXTRA_KEY_PORTAL_API_RESPONSE = "EXTRA_KEY_PORTAL_API_RESPONSE"
        private const val TIME_TARGET_MILLI_SECONDS = 10_000L
        private const val TIME_INTERVAL_MILLI_SECONDS = 1000L

        fun launch(
            activity: Activity,
            source: Source,
            portalAPIsResponse: GetPortalAPIsResponse? = null,
            portalAccountPO: PortalAccountPO? = null,
            email: String? = null,
            password: String? = null
        ) {
            val intent = Intent(activity, PortalSettingsActivity::class.java)
            intent.putExtra(EXTRA_KEY_SOURCE, source)
            portalAPIsResponse?.run { intent.putExtra(EXTRA_KEY_PORTAL_API_RESPONSE, this) }
            portalAccountPO?.run { intent.putExtra(EXTRA_KEY_PORTAL_ACCOUNT, this) }
            email?.run { intent.putExtra(EXTRA_KEY_EMAIL, this) }
            password?.run { intent.putExtra(EXTRA_KEY_PASSWORD, this) }
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBarAndTimer()
        setupParamsFromExtra()
        setupList()
        performRequest()
        handleListeners()
        observeLiveData()
    }

    private fun setupParamsFromExtra() {
        if (intent.hasExtra(EXTRA_KEY_SOURCE)) {
            source = intent.getSerializableExtra(EXTRA_KEY_SOURCE) as Source
        }

        if (intent.hasExtra(EXTRA_KEY_PORTAL_API_RESPONSE)) {
            viewModel.portalApiResponse.value =
                intent.getSerializableExtra(EXTRA_KEY_PORTAL_API_RESPONSE) as GetPortalAPIsResponse
        }

        if (intent.hasExtra(EXTRA_KEY_PORTAL_ACCOUNT)) {
            viewModel.portalAccountPO.value =
                intent.getSerializableExtra(EXTRA_KEY_PORTAL_ACCOUNT) as PortalAccountPO
        }

        if (intent.hasExtra(EXTRA_KEY_EMAIL) && intent.hasExtra(EXTRA_KEY_PASSWORD)) {
            viewModel.email.value = intent.getStringExtra(EXTRA_KEY_EMAIL) ?: ""
            viewModel.password.value = intent.getStringExtra(EXTRA_KEY_PASSWORD) ?: ""
        }
    }

    private fun setupActionBarAndTimer() {
        timerSynchronizingListings = SynchronizeListingsCountDown(
            TIME_TARGET_MILLI_SECONDS,
            TIME_INTERVAL_MILLI_SECONDS
        )
    }

    private fun setupList() {
        adapter = SyncFrequencyAdapter { sync ->
            viewModel.selectedSyncFrequency.value = sync
            viewModel.isApplySettingsBtnEnabled.value = true
        }
        binding.listSyncFrequency.layoutManager = LinearLayoutManager(this)
        binding.listSyncFrequency.adapter = adapter
    }

    private fun performRequest() {
        viewModel.getPortalAPIs()
    }

    private fun handleListeners() {
        binding.btnApplySettings.setOnClickListener {
            DialogUtil(this).showActionDialog(
                message = R.string.dialog_message_sync_listings_confirmation,
                positiveButtonLabel = R.string.label_proceed,
                negativeButtonLabel = R.string.label_cancel
            ) {
                updatePortalAccountByMode()

            }
        }
    }

    private fun updatePortalAccountByMode() {
        val mode = SessionUtil.getListingPortalMode() ?: ListingManagementEnum.PortalMode.SERVER
        if (mode == ListingManagementEnum.PortalMode.SERVER) {
            viewModel.updatePortalAccountServer()
        } else {
            //TODO: currently call server update portal account first
            viewModel.updatePortalAccountServer()
        }
    }

    private fun observeLiveData() {
        viewModel.selectedSyncOption.observe(this, Observer { option ->
            option?.let {
                //update button text
                viewModel.applySettingsBtnLabel.value =
                    if (option.value == ListingManagementEnum.PortalSyncOption.AUTO.value) {
                        getString(R.string.action_apply_settings)
                    } else {
                        getString(R.string.action_apply_settings_and_select_listings)
                    }
            }
        })

        viewModel.syncOptions.observe(this, Observer { options ->
            options?.let { optionList ->
                if (optionList.isNotEmpty()) {
                    val syncOptionValue = viewModel.portalAccountPO.value?.syncOption ?: 0
                    if (syncOptionValue > 0) {
                        val synOption = optionList.first { it.value == syncOptionValue }
                        viewModel.selectedSyncOption.value = synOption
                    } else {
                        viewModel.selectedSyncOption.value = null
                    }
                    populateSyncOptions(optionList)
                }
            }
        })

        viewModel.syncFrequencies.observe(this, Observer { frequencies ->
            frequencies?.let { frequencyList ->
                if (frequencyList.isNotEmpty()) {
                    //update new sync lists
                    adapter.syncList = frequencyList
                    val syncFrequencyValue = viewModel.portalAccountPO.value?.syncFrequency ?: 0
                    if (syncFrequencyValue > 0) {
                        val syncFrequency = frequencyList.first { it.value == syncFrequencyValue }
                        viewModel.selectedSyncFrequency.value = syncFrequency
                        adapter.updateSelectSynFrequency(syncFrequency)
                    } else {
                        viewModel.selectedSyncFrequency.value = null
                        resetSelectedSyncFrequency()
                    }
                }
            }
        })

        viewModel.updatePortalAccountStatus.observe(this, Observer {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    viewModel.portalAccountPO.value = it.body?.portalAccount
                    viewModel.selectedSyncOption.value?.let { syncData ->
                        when (syncData.value) {
                            ListingManagementEnum.PortalSyncOption.AUTO.value -> {
                                dialogProgress = showLottieFrameLoadingDialog()
                                timerSynchronizingListings.start()
                            }
                            ListingManagementEnum.PortalSyncOption.MANUAL.value -> showPortalListingsScreen()
                            else -> finish()
                        }
                    }
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    println("do nothing...")
                }
            }
        })

        viewModel.selectedSyncOption.observe(this, Observer {
            it?.let { syncData ->
                initializeSyncOptionAndFrequency(syncData)
            }
        })
    }

    private fun populateSyncOptions(options: List<GetPortalAPIsResponse.SyncData>) {
        binding.flexBoxSyncOptions.removeAllViews()
        options.map { option ->
            val card = CardListingSyncOption(this)
            card.binding.label = option.name
            card.binding.isSelected = if (viewModel.selectedSyncOption.value == null) {
                null
            } else {
                option.value == viewModel.selectedSyncOption.value?.value
            }
            card.binding.cardSyncOption.setOnClickListener {
                viewModel.selectedSyncOption.value = option
                viewModel.selectedSyncFrequency.value = null
                populateSyncOptions(options)
            }
            binding.flexBoxSyncOptions.addView(card)
        }
    }

    private fun initializeSyncOptionAndFrequency(option: GetPortalAPIsResponse.SyncData) {
        val syncOptionList = viewModel.syncOptions.value ?: emptyList()
        val frequencyList = viewModel.syncFrequencies.value ?: emptyList()
        if (syncOptionList.isNotEmpty() && frequencyList.isNotEmpty()) {
            val defaultOption = viewModel.portalAccountPO.value?.syncOption ?: 0
            val defaultFrequency = viewModel.portalAccountPO.value?.syncFrequency ?: 0
            syncOptionList.find { option -> option.value == defaultOption }
                ?.let { preSyncOption ->
                    if (preSyncOption.value == option.value) {
                        if (defaultFrequency > 0) {
                            frequencyList.find { it.value == defaultFrequency }
                                ?.let { syncFrequency ->
                                    viewModel.selectedSyncFrequency.value = syncFrequency
                                    viewModel.isApplySettingsBtnEnabled.value = true
                                    adapter.updateSelectSynFrequency(syncFrequency)
                                }
                        } else {
                            resetSelectedSyncFrequency()
                        }
                    } else {
                        resetSelectedSyncFrequency()
                    }
                } ?: resetSelectedSyncFrequency()
        }
    }

    private fun resetSelectedSyncFrequency() {
        viewModel.isApplySettingsBtnEnabled.value = false
        adapter.resetSelectSyncFrequency()
    }

    private fun showPortalListingsScreen() {
        finish()
        when (source) {
            Source.PORTAL_LOGIN -> {
                PortalListingsActivity.launch(
                    activity = this,
                    portalApiResponse = viewModel.portalApiResponse.value,
                    portalAccountPO = viewModel.portalAccountPO.value
                )
            }
            Source.PORTAL_LISTING -> {
                RxBus.publish(
                    UpdatePortalListingsEvent(
                        portalAccountPO = viewModel.portalAccountPO.value ?: return
                    )
                )
            }
        }
    }

    private fun showLottieFrameLoadingDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this, R.style.FullScreenWhiteBackgroundAlertDialog)
        builder.setCancelable(false)
        val view =
            layoutInflater.inflate(R.layout.layout_lottie_frame_loading_animation, null, false)
        val processingLabel: TextView = view.findViewById(R.id.tv_processing)
        processingLabel.setText(R.string.msg_importing)
        builder.setView(view)
        return builder.show()
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_portal_import_and_sync_settings
    }

    override fun getViewModelClass(): Class<PortalSettingsViewModel> {
        return PortalSettingsViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    inner class SynchronizeListingsCountDown(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
            dialogProgress?.dismiss()
            showPortalListingsScreen()
        }

        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            println("Doing nothing onTick")
        }
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

    enum class Source(val screen: Int) {
        PORTAL_LOGIN(1),
        PORTAL_LISTING(2)
    }
}