package sg.searchhouse.agentconnect.view.activity.agent.profile

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.annotation.StringRes
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_agent_profile.*
import kotlinx.android.synthetic.main.layout_cv_profile.*
import kotlinx.android.synthetic.main.layout_profile_watchlist.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.SharedPreferenceKey.PREF_SHOULD_REFRESH_DASHBOARD_LISTINGS_WATCHLIST
import sg.searchhouse.agentconnect.constant.SharedPreferenceKey.PREF_SHOULD_REFRESH_DASHBOARD_TRANSACTIONS_WATCHLIST
import sg.searchhouse.agentconnect.databinding.ActivityAgentProfileBinding
import sg.searchhouse.agentconnect.dsl.launchActivity
import sg.searchhouse.agentconnect.dsl.launchActivityForResult
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.dsl.widget.postDelayed
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutManager
import sg.searchhouse.agentconnect.enumeration.api.AccessibilityEnum
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.PaymentPurpose
import sg.searchhouse.agentconnect.enumeration.api.AgentProfileAndCvEnum.SubscriptionCreditSource
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.event.agent.VisitGroupSubscriptionWebEvent
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.view.activity.agent.client.MySgHomeClientsActivity
import sg.searchhouse.agentconnect.view.activity.agent.cv.AgentCvActivity
import sg.searchhouse.agentconnect.view.activity.agent.cv.CvCreateUrlActivity
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.activity.common.CropImageActivity
import sg.searchhouse.agentconnect.view.activity.watchlist.AddWatchlistCriteriaActivity
import sg.searchhouse.agentconnect.view.activity.watchlist.EditWatchlistCriteriaActivity
import sg.searchhouse.agentconnect.view.adapter.watchlist.WatchlistProfileCriteriaAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.agent.profile.AgentProfileViewModel

class AgentProfileActivity :
    ViewModelActivity<AgentProfileViewModel, ActivityAgentProfileBinding>() {
    private val watchlistAdapter =
        WatchlistProfileCriteriaAdapter(onCriteriaSelected = {
            EditWatchlistCriteriaActivity.launchForResult(this, it, REQUEST_CODE_EDIT_WATCHLIST)
        }, onCriteriaHiddenCheckChanged = { criteria, _ ->
            viewModel.performUpdatePropertyCriteriaHiddenInd(criteria)
        })

    private var uploadPhotoProgressDialog: AlertDialog? = null
    private var removePhotoProgressDialog: AlertDialog? = null

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        observeLiveData()
        listenRxBuses()
        handleViewListeners()
        checkModuleAccessibilities()
        maybeScrollToWatchlists()
    }

    private fun listenRxBuses() {
        listenRxBus(VisitGroupSubscriptionWebEvent::class.java) { visitGroupSubscriptionWeb() }
    }

    private fun visitGroupSubscriptionWeb() {
        IntentUtil.visitUrl(this, getTopUpSharedCreditUrl())
    }

    private fun getTopUpSharedCreditUrl(): String {
        val ceaNumber =
            viewModel.getCeaNumber() ?: throw IllegalArgumentException("Missing CEA number")
        return "${ApiUtil.get99BaseUrl(this)}/select-credit-packages?cea_number=$ceaNumber&source=mobile&platform=srx"
    }

    private fun maybeScrollToWatchlists() {
        val launchType =
            intent.extras?.getSerializable(EXTRA_LAUNCH_TYPE) as LaunchType?
                ?: LaunchType.DEFAULT
        if (launchType == LaunchType.MANAGE_WATCHLIST) {
            scroll_view.postDelayed {
                scroll_view.smoothScrollTo(0, layout_profile_watchlist.y.toInt())
            }
        }
    }

    private fun checkModuleAccessibilities() {
        AuthUtil.checkModuleAccessibility(
            module = AccessibilityEnum.AdvisorModule.WATCHLISTS,
            onSuccessAccessibility = {
                viewModel.shouldShowWatchList.postValue(true)
                viewModel.watchListPage.postValue(1)
            }
        )
    }

    private fun setupViews() {
        setupWatchListList()
        setOnClickListeners()
    }

    private fun setupWatchListList() {
        list.setupLayoutManager()
        list.adapter = watchlistAdapter
        ViewUtil.listenVerticalScrollEnd(scroll_view, reachBottom = {
            viewModel.maybeLoadMoreWatchLists()
        })
    }

    private fun setOnClickListeners() {
        btn_add_criteria.setOnClickListener {
            launchActivityForResult(
                AddWatchlistCriteriaActivity::class.java,
                requestCode = REQUEST_CODE_ADD_WATCHLIST
            )
        }
        iv_profile_photo.setOnClickListener {
            val photo = binding.layoutAgentProfile.binding.agentPO?.photo
            val list = when (Uri.parse(photo).queryParameterNames.contains("r")) {
                true -> listOf(
                    R.string.dialog_list_item_take_profile_photo,
                    R.string.dialog_list_item_choose_profile_photo,
                    R.string.dialog_list_item_remove_profile_photo
                )
                else -> listOf(
                    R.string.dialog_list_item_take_profile_photo,
                    R.string.dialog_list_item_choose_profile_photo
                )
            }

            dialogUtil.showListDialog(list, onClickListener = { _, position ->
                when (position) {
                    ProfilePhotoAction.TAKE_PHOTO.position -> {
                        openCamera()
                    }
                    ProfilePhotoAction.CHOOSE_PHOTO.position -> {
                        openPhotoGallery()
                    }
                    ProfilePhotoAction.REMOVE_PHOTO.position -> {
                        dialogUtil.showActionDialog(R.string.dialog_message_confirm_delete_profile_photo) {
                            viewModel.performRemovePhoto()
                        }
                    }
                }
            })
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionUtil.REQUEST_CODE_READ_WRITE_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openPhotoGallery()
                }
            }
            PermissionUtil.REQUEST_CODE_CAMERA -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openCamera()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun openCamera() {
        imageUri = ImageUtil.generateImageFile(this)
        IntentUtil.openCamera(
            this,
            REQUEST_CODE_CAMERA, imageFileUri = imageUri
        )
    }

    private fun openPhotoGallery() {
        IntentUtil.openGallery(activity = this, requestCode = REQUEST_CODE_GALLERY)
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observe(this, Observer { response ->
            response?.data?.let {
                //full profile
                viewModel.agentFullProfile.value = it

                //agent profile
                it.profile?.let { profile ->
                    binding.layoutAgentProfile.binding.agentPO = profile
                }

                //subscription status
                if (it.subscriptionStatus.isNotEmpty()) {
                    viewModel.subscriptionStatus.value =
                        getString(R.string.label_membership_status, it.subscriptionStatus)
                }

                //subscription
                it.subscription?.let { subscription ->
                    viewModel.subscription.value = subscription
                }

                //populate credits
                binding.layoutProfileWallet.populateCredits(it.credits)
                binding.layoutProfileWallet.binding.viewModel = viewModel
            }
        })

        viewModel.agentPO.observe(this, Observer { agent ->
            //Already added check accessibility
            agent?.let {
                if (it.agentCvPO == null || (it.agentCvPO.changedPublicUrlInd == false
                            && TextUtils.isEmpty(it.agentCvPO.publicProfileUrl))
                ) {
                    startActivity(Intent(this, CvCreateUrlActivity::class.java))
                } else {
                    AgentCvActivity.launch(this, it.userId)
                }
            }

        })

        viewModel.getAgentCvFailed.observe(this, Observer { error ->
            error?.let {
                ViewUtil.showMessage(it)
            }
        })

        viewModel.subscriptionPackageStatus.observe(this, Observer {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    binding.layoutProfileWallet.directToSubscriptionPackageScreen()
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it?.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        })

        viewModel.watchListPage.observeNotNull(this) {
            viewModel.performGetWatchLists()
            if (it > 1) {
                watchlistAdapter.addListItem(Loading())
                scroll_view.fullScroll(View.FOCUS_DOWN)
            }
        }

        viewModel.getWatchListsResponse.observe(this) {
            val watchlistPage = viewModel.watchListPage.value ?: return@observe
            when (watchlistPage) {
                1 -> {
                    watchlistAdapter.updateListItems(it?.watchlists ?: emptyList())
                }
                else -> {
                    watchlistAdapter.addListItems(it?.watchlists ?: emptyList())
                }
            }
        }

        viewModel.isPropertyCriteriaHiddenIndUpdated.observeNotNull(this) {
            // Refresh dashboard watchlist sections upon show/hide criteria
            notifyRefreshDashboardWatchlist()
        }

        viewModel.updatePhotoStatus.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.LOADING -> {
                    uploadPhotoProgressDialog = dialogUtil.showProgressDialog()
                    uploadPhotoProgressDialog?.show()
                }
                ApiStatus.StatusKey.SUCCESS -> {
                    uploadPhotoProgressDialog?.dismiss()
                    viewModel.isProfilePhotoUpdated.postValue(true)
                    viewModel.performGetAgentFullProfile()
                }
                ApiStatus.StatusKey.FAIL -> {
                    uploadPhotoProgressDialog?.dismiss()
                    val errorMessages =
                        it.error?.error ?: getString(R.string.error_generic_contact_srx)
                    ViewUtil.showMessage(errorMessages)
                }
                ApiStatus.StatusKey.ERROR -> {
                    uploadPhotoProgressDialog?.dismiss()
                }
                else -> {
                }
            }
        }

        viewModel.removePhotoStatus.observeNotNull(this) {
            when (it.key) {
                ApiStatus.StatusKey.LOADING -> {
                    removePhotoProgressDialog = dialogUtil.showProgressDialog()
                    removePhotoProgressDialog?.show()
                }
                ApiStatus.StatusKey.SUCCESS -> {
                    removePhotoProgressDialog?.dismiss()
                    viewModel.isProfilePhotoUpdated.postValue(true)
                    viewModel.performGetAgentFullProfile()
                }
                ApiStatus.StatusKey.FAIL -> {
                    removePhotoProgressDialog?.dismiss()
                    val errorMessages =
                        it.error?.error ?: getString(R.string.error_generic_contact_srx)
                    ViewUtil.showMessage(errorMessages)
                }
                ApiStatus.StatusKey.ERROR -> {
                    removePhotoProgressDialog?.dismiss()
                }
                else -> {
                }
            }
        }
    }

    override fun finish() {
        if (viewModel.isProfilePhotoUpdated.value == true) {
            setResult(RESULT_PROFILE_PHOTO_UPDATED)
        }
        super.finish()
    }

    private fun notifyRefreshDashboardWatchlist() {
        Prefs.putBoolean(PREF_SHOULD_REFRESH_DASHBOARD_TRANSACTIONS_WATCHLIST, true)
        Prefs.putBoolean(PREF_SHOULD_REFRESH_DASHBOARD_LISTINGS_WATCHLIST, true)
    }

    private fun handleViewListeners() {
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        binding.layoutViewCv.layoutItemAgentProfile.setOnClickListener {
            AuthUtil.checkModuleAccessibility(
                module = AccessibilityEnum.AdvisorModule.AGENT_CV,
                onSuccessAccessibility = {
                    SessionUtil.getCurrentUser()?.let {
                        viewModel.getAgentCv(it.id)
                    }
                })
        }

        binding.layoutViewClients.layoutItemAgentProfile.setOnClickListener {
            AuthUtil.checkModuleAccessibility(
                module = AccessibilityEnum.AdvisorModule.MY_CLIENTS,
                onSuccessAccessibility = { launchActivity(MySgHomeClientsActivity::class.java) })
        }

        binding.btnRenewMembership.setOnClickListener {
            SubscriptionCreditPackageDetailsActivity.launch(
                context = this,
                paymentPurpose = PaymentPurpose.SUBSCRIPTION,
                source = SubscriptionCreditSource.AGENT_CV
            )
        }

        btn_upload_photo.setOnClickListener { openPhotoGallery() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    REQUEST_CODE_ADD_WATCHLIST, REQUEST_CODE_EDIT_WATCHLIST -> {
                        notifyRefreshDashboardWatchlist()
                        launchActivity(AgentProfileActivity::class.java)
                        finish()
                    }
                    REQUEST_CODE_GALLERY -> {
                        imageUri = data?.data ?: return
                        CropImageActivity.launch(
                            this@AgentProfileActivity,
                            imageUri.toString(),
                            REQUEST_CODE_CROP_PHOTO
                        )
                    }
                    REQUEST_CODE_CAMERA -> {
                        imageUri?.run {
                            CropImageActivity.launch(
                                this@AgentProfileActivity,
                                toString(),
                                REQUEST_CODE_CROP_PHOTO
                            )
                        } ?: ViewUtil.showMessage(R.string.error_camera_image_not_available)
                    }
                    REQUEST_CODE_CROP_PHOTO -> {
                        if (resultCode == Activity.RESULT_OK) {
                            imageUri?.run {
                                // TODO Move to view model
                                CoroutineScope(Dispatchers.IO).launch {
                                    val coordinateList =
                                        data?.getStringExtra(CropImageActivity.EXTRA_OUTPUT_COORDINATE)
                                            ?.split(",")?.mapNotNull { it.toIntOrNull() }
                                    val cropSize =
                                        data?.getIntExtra(
                                            CropImageActivity.EXTRA_OUTPUT_CROP_SIZE,
                                            0
                                        ) ?: 0
                                    val coordinate = when (coordinateList?.size) {
                                        2 -> Pair(coordinateList[0], coordinateList[1])
                                        else -> null
                                    }
                                    val croppedImageUrl =
                                        getCroppedImageUri(this@run, coordinate, cropSize)
                                    uploadImage(croppedImageUrl ?: this@run)
                                }
                            }
                        }
                    }
                    else -> {
                        super.onActivityResult(requestCode, resultCode, data)
                    }
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    @WorkerThread
    private fun getCroppedImageUri(
        inputUri: Uri,
        coordinate: Pair<Int, Int>?,
        cropSize: Int
    ): Uri? {
        return ImageUtil.getSquareCroppedImageFile(
            applicationContext,
            inputUri,
            coordinate,
            cropSize
        )?.toUri()
    }

    private fun uploadImage(imageUri: Uri) {
        val file = ImageUtil.getFileFromUri(applicationContext, imageUri) ?: return
        viewModel.performUpdatePhoto(file)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_agent_profile
    }

    override fun getViewModelClass(): Class<AgentProfileViewModel> {
        return AgentProfileViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return null
    }

    companion object {
        private const val REQUEST_CODE_EDIT_WATCHLIST = 3
        private const val REQUEST_CODE_ADD_WATCHLIST = 4
        private const val REQUEST_CODE_GALLERY = 5
        private const val REQUEST_CODE_CAMERA = 6
        private const val REQUEST_CODE_CROP_PHOTO = 7

        const val RESULT_PROFILE_PHOTO_UPDATED = 2

        private const val EXTRA_LAUNCH_TYPE = "EXTRA_LAUNCH_TYPE"

        enum class LaunchType {
            DEFAULT, MANAGE_WATCHLIST
        }

        enum class ProfilePhotoAction(@StringRes val label: Int, val position: Int) {
            TAKE_PHOTO(R.string.dialog_list_item_take_profile_photo, 0),
            CHOOSE_PHOTO(R.string.dialog_list_item_choose_profile_photo, 1),
            REMOVE_PHOTO(R.string.dialog_list_item_remove_profile_photo, 2)
        }

        fun launch(activity: Activity, launchType: LaunchType = LaunchType.DEFAULT) {
            val extras = Bundle()
            extras.putSerializable(EXTRA_LAUNCH_TYPE, launchType)
            activity.launchActivity(AgentProfileActivity::class.java, extras)
        }

        fun launchForResult(
            activity: Activity,
            launchType: LaunchType = LaunchType.DEFAULT,
            requestCode: Int
        ) {
            val extras = Bundle()
            extras.putSerializable(EXTRA_LAUNCH_TYPE, launchType)
            activity.launchActivityForResult(AgentProfileActivity::class.java, extras, requestCode)
        }
    }
}