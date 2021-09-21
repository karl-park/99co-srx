package sg.searchhouse.agentconnect.view.fragment.listing.create

import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.card_post_listing_credit_summary.view.*
import kotlinx.android.synthetic.main.card_post_listing_facilities.view.*
import kotlinx.android.synthetic.main.dialog_fragment_post_listing.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentPostListingBinding
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.ListingPhotoType
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus.StatusKey.*
import sg.searchhouse.agentconnect.enumeration.status.ButtonState
import sg.searchhouse.agentconnect.event.listing.create.NotifyPostListingEvent
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.listing.ListingEditPO
import sg.searchhouse.agentconnect.tracking.CreateListingTracker
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelDialogFragment
import sg.searchhouse.agentconnect.view.fragment.listing.certifiedlisting.CertifiedListingHelpDialogFragment
import sg.searchhouse.agentconnect.view.fragment.listing.certifiedlisting.HomeOwnerDetailDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.create.PostListingViewModel

class PostListingDialogFragment :
    ViewModelDialogFragment<PostListingViewModel, DialogFragmentPostListingBinding>() {

    private var cameraImageUri: Uri? = null

    companion object {
        private const val TAG_POST_LISTING_DIALOG_FRAGMENT = "TAG_POST_LISTING_DIALOG_FRAGMENT"
        private const val ARGUMENT_KEY_NEW_LISTING_INDICATOR = "ARGUMENT_KEY_NEW_LISTING_INDICATOR"
        private const val ARGUMENT_KEY_LISTING_ID = "ARGUMENT_KEY_LISTING_ID"

        const val REQUEST_CODE_GALLERY = 1
        const val REQUEST_CODE_CAMERA = 2

        fun newInstance(
            listingId: String,
            isNewListing: Boolean
        ): PostListingDialogFragment {
            val dialog = PostListingDialogFragment()
            val bundle = Bundle()
            bundle.putString(ARGUMENT_KEY_LISTING_ID, listingId)
            bundle.putBoolean(ARGUMENT_KEY_NEW_LISTING_INDICATOR, isNewListing)
            dialog.arguments = bundle
            return dialog
        }

        fun launch(
            fragmentManager: FragmentManager,
            listingId: String,
            isNewListing: Boolean = false
        ) {
            newInstance(listingId, isNewListing).show(
                fragmentManager,
                TAG_POST_LISTING_DIALOG_FRAGMENT
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupArgumentParams()
        observeLiveData()
        setupListeners()
        layout_media.onOpenPicker = onOpenPhotoPicker
    }

    private fun setupArgumentParams() {
        val bundle = arguments ?: return
        bundle.getString(ARGUMENT_KEY_LISTING_ID, null)?.run {
            //new listing indicator
            viewModel.isNewListing.value =
                bundle.getBoolean(ARGUMENT_KEY_NEW_LISTING_INDICATOR, false)
            //listing id
            viewModel.listingId.postValue(this)

        } ?: throw Throwable("Missing listing id in post listing screen")
    }

    private fun observeLiveData() {
        viewModel.listingId.observeNotNull(this) { id ->
            if (!TextUtils.isEmpty(id)) {
                viewModel.getBedrooms()
                viewModel.getListing(listingId = id)
                viewModel.getPostingCredits(listingId = id)
            } else {
                ViewUtil.showMessage("Undefined listing id")
            }
        }

        viewModel.isNewListing.observeNotNull(this) { isNew ->
            if (isNew) {
                ViewUtil.showFeedbackMessage(R.string.msg_auto_saved_drafts)
            } else {
                ViewUtil.showFeedbackMessage(R.string.msg_auto_saved_find_draft)
            }
        }

        viewModel.initialListingEditPO.observeNotNull(this) { editPO ->
            //update listing group and listing edit po
            viewModel.listingGroup.value =
                ListingManagementEnum.ListingGroup.values().find { it.value == editPO.group }

            viewModel.listingEditPO.value = editPO

            //get fixtures and features
            viewModel.getFixturesAndFeatures(editPO.category)

            //bind info to different sections
            setupListingInfoInEachSection(editPO)
        }

        viewModel.getPostingCreditsResponse.observeNotNull(this) {
            when (it.key) {
                SUCCESS -> {
                    val result = it.body ?: return@observeNotNull
                    viewModel.postListingCredits.value = result
                    layout_credit_summary.populateCreditBalanceAndCost(
                        postingCost = result.postingCost,
                        balance = result.balance
                    )
                }
                FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.bedroomsResponse.observeNotNull(this) {
            when (it.key) {
                SUCCESS -> {
                    viewModel.bedrooms.postValue(it.body?.bedrooms)
                }
                FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.bedrooms.observeNotNull(this) { bedrooms ->
            if (bedrooms.isNotEmpty()) {
                layout_additional_info.populateBedrooms(items = bedrooms)
            }
        }

        viewModel.featuresFixturesResponse.observeNotNull(this) {
            when (it.key) {
                SUCCESS -> {
                    val response = it.body ?: return@observeNotNull
                    layout_facilities.populateFacilities(
                        features = response.features,
                        fixtures = response.fixtures,
                        areas = response.areas
                    )
                }
                FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.updateListingResponse.observeNotNull(this) {
            when (it.key) {
                SUCCESS -> {
                    val editPO = it?.body?.listingEditPO ?: return@observeNotNull
                    viewModel.listingEditPO.value = editPO

                    //update listing edit po for price and specific card
                    layout_price_and_specific_info.updateListingEditPO(editPO)
                }
                FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.checkYouTubeUrlResponse.observeNotNull(this) { response ->
            when {
                response.error != null -> {
                    ViewUtil.showMessage(R.string.msg_invalid_youtube_link)
                }
                response.items.isNotEmpty() -> {
                    ViewUtil.showMessage(R.string.msg_valid_youtube_url)
                }
                else -> {
                    ViewUtil.showMessage(R.string.msg_wrong_youtube_url)
                }
            }
        }

        viewModel.isUpdateSpecificInfoCard.observeNotNull(this) { isUpdate ->
            if (isUpdate) {
                val editPO = viewModel.listingEditPO.value ?: return@observeNotNull
                layout_price_and_specific_info.updatePriceAndSpecificCard(editPO)
            }
        }

        viewModel.isUpdateAdditionalInfoCard.observeNotNull(this) { isUpdate ->
            if (isUpdate) {
                layout_additional_info.updateViewModel(postListingViewModel = viewModel)
            }
        }

        viewModel.imageFiles.observeNotNull(this) { files ->
            if (files.isNotEmpty()) {
                val file = files.first()
                val type = viewModel.listingPhotoType ?: return@observeNotNull
                viewModel.uploadNewPhoto(file = file, photoType = type)
            }
        }

        viewModel.uploadNewPhotoResponse.observeNotNull(this) {
            when (it.key) {
                LOADING -> {
                    layout_media.isShowingLoadingIndicator(true, viewModel.listingPhotoType)
                }
                SUCCESS -> {
                    it.body?.let { response ->
                        layout_media.isShowingLoadingIndicator(false, viewModel.listingPhotoType)
                        layout_media.addUploadedPhoto(response.photo, viewModel.listingPhotoType)
                        val tempFiles =
                            viewModel.imageFiles.value?.toMutableList() ?: mutableListOf()
                        if (tempFiles.isNotEmpty()) {
                            tempFiles.remove(tempFiles.first())
                            viewModel.imageFiles.value = tempFiles
                        }

                        viewModel.arrangeNonQualityPhotos(response.photo)
                    }
                }
                FAIL -> {
                    ViewUtil.showMessage(it?.error?.error)
                    layout_media.isShowingLoadingIndicator(false, viewModel.listingPhotoType)
                }
                ERROR -> {
                    layout_media.isShowingLoadingIndicator(false, viewModel.listingPhotoType)
                }
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.vetPhotoResponse.observeNotNull(this) {
            when (it.key) {
                FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                    layout_media.isShowingLoadingIndicator(false, viewModel.listingPhotoType)
                }
                ERROR -> {
                    layout_media.isShowingLoadingIndicator(false, viewModel.listingPhotoType)
                }
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.appealPhoto.observeNotNull(this) {
            layout_media.updateAppealStatusAfterRequest(photo = it)
        }

        viewModel.appealPhotoResponse.observeNotNull(this) {
            when (it.key) {
                FAIL -> ViewUtil.showMessage(it.error?.error)
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.updateListingAfterUploadedPhoto.observeNotNull(this) {
            when (it.key) {
                FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.removePhotoResponse.observeNotNull(this) {
            when (it.key) {
                SUCCESS -> {
                    val removedPhoto = viewModel.removedPhoto.value ?: return@observeNotNull
                    layout_media.deleteUploadedPhoto(removedPhoto, viewModel.listingPhotoType)
                }
                FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.setCoverPhotoResponse.observeNotNull(this) {
            when (it.key) {
                FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.movePhotoResponse.observeNotNull(this) {
            when (it.key) {
                FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.reloadListingPhotosResponse.observeNotNull(this) {
            val response = it.first
            val isUpdateMediaCard = it.second
            when (response.key) {
                SUCCESS -> {
                    val editPO = response.body?.listingEditPO ?: return@observeNotNull
                    viewModel.listingEditPO.postValue(editPO)
                    if (isUpdateMediaCard) {
                        layout_media.repopulateListings(
                            listingEditPO = editPO,
                            type = viewModel.listingPhotoType
                        )
                    }
                }
                FAIL -> {
                    ViewUtil.showMessage(response.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.postListingResponse.observeNotNull(this) {
            when (it.key) {
                SUCCESS -> {
                    val listingId = viewModel.listingEditPO.value?.id ?: return@observeNotNull
                    CreateListingTracker.trackListingCreationEndTime(
                        requireContext(),
                        listingId.toString()
                    )
                    if (it.body?.ownerCertification == null) {
                        showHomeOwnerDetailScreen(listingId = listingId.toString())
                    }
                    dialog?.dismiss()
                }
                FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.apiCallResponse.observeNotNull(this) {
            when (it.key) {
                SUCCESS -> {
                    ViewUtil.showMessage(it.body?.result)
                    dialog?.dismiss()
                }
                FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }

        viewModel.updateRepostListingResponse.observeNotNull(this) {
            when (it.key) {
                FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        }

    }

    private fun showHomeOwnerDetailScreen(listingId: String) {
        activity?.run {
            HomeOwnerDetailDialogFragment.launch(supportFragmentManager, listingId)
        }
    }

    private fun setupListingInfoInEachSection(editPO: ListingEditPO) {
        //basic info
        layout_listing_info.setListingEditPO(listingEditPO = editPO)

        //price and specific section
        layout_price_and_specific_info.viewModel = viewModel
        layout_price_and_specific_info.populatePriceAndSpecificInfo(listingEditPO = editPO)

        //Header and description
        layout_listing_header_and_desc.binding.viewModel = viewModel

        //additional info
        layout_additional_info.updateViewModel(postListingViewModel = viewModel)
        layout_additional_info.populateAdditionalInformation()

        //facilities
        layout_facilities.viewModel = viewModel
        layout_facilities.populateSelectedItems(
            features = editPO.features,
            fixtures = editPO.fixtures,
            areas = editPO.area
        )

        layout_media.viewModel = viewModel
        layout_media.setupListingEditPO(listingEditPO = editPO)
        layout_media.populateSubmittedPhotos(
            listingPhotos = editPO.photosSubmitted,
            floorPlanPhotos = editPO.floorPlanPhotosSubmitted,
            listingEditPO = editPO
        )

        //credit summary
        layout_credit_summary.binding.viewModel = viewModel
    }

    private fun setupListeners() {
        toolbar.setNavigationOnClickListener { dialog?.dismiss() }

        iv_non_quality_info.setOnClickListener { dialogUtil.showInformationDialog(R.string.msg_listing_quality_info) }

        layout_listing_info.binding.ibEditListing.setOnClickListener { showCreateUpdateListingScreen() }

        layout_credit_summary.tv_read_advisory.setOnClickListener { showAcknowledgementDialog() }
        layout_credit_summary.iv_certified_listing.setOnClickListener { showCertifiedListingScreen() }

        ib_action.setOnClickListener { showActionDialog() }

        btn_post_listing.setOnClickListener {
            val editPO = viewModel.listingEditPO.value ?: return@setOnClickListener
            if (editPO.isAcknowledged) {
                showPostingCreditConfirmation()
            } else {
                ViewUtil.showMessage(R.string.msg_agree_acknowledged)
            }
        }

        layout_media.binding.ivNonQualityPhotoInfo.setOnClickListener {
            activity?.run {
                NonQualityListingPhotoInfoDialogFragment.launch(supportFragmentManager)
            }

        }

        // Toggle "Options above do not apply to my unit" check box
        layout_facilities.cb_apply_options.setOnClickListener {
            val listingEditPO = viewModel.listingEditPO.value ?: return@setOnClickListener
            listingEditPO.noFeaturesFixturesAreasInd = !listingEditPO.noFeaturesFixturesAreasInd

            viewModel.performUpdateListing(listingEditPO)
        }
    }

    private fun showPostingCreditConfirmation() {
        val creditInfo = viewModel.postListingCredits.value ?: return
        val message = getString(
            R.string.msg_confirmation_posting_credit,
            this.resources.getQuantityString(
                R.plurals.label_credit_count,
                creditInfo.balance,
                NumberUtil.formatThousand(creditInfo.balance)
            ),
            this.resources.getQuantityString(
                R.plurals.label_credit_count,
                creditInfo.postingCost,
                NumberUtil.formatThousand(creditInfo.postingCost)
            )
        )
        dialogUtil.showActionDialog(message) {
            if (viewModel.listingGroup.value == ListingManagementEnum.ListingGroup.ACTIVE) {
                viewModel.btnState.value = ButtonState.SUBMITTING
                viewModel.updateListingEditPO(isRepostListing = true)
            } else {
                viewModel.postListing()
            }
        }
    }

    private fun showActionDialog() {
        val listingGroup = viewModel.listingGroup.value ?: return
        val list: List<Int> = if (listingGroup == ListingManagementEnum.ListingGroup.ACTIVE) {
            listOf(R.string.label_take_off_listing)
        } else {
            listOf(R.string.label_delete_listing)
        }
        dialogUtil.showListDialog(
            list, { _, position ->
                when (position) {
                    0 -> {
                        if (listingGroup == ListingManagementEnum.ListingGroup.ACTIVE) {
                            confirmationToTakeOffListing()
                        } else {
                            confirmationToDeleteListing()
                        }
                    }
                    else -> throw Throwable("wrong type in listing group")
                }
            }, null
        )
    }

    private fun showCreateUpdateListingScreen() {
        activity?.run {
            viewModel.updateListingEditPO()
            val listingEditPO = viewModel.listingEditPO.value
                ?: return@run ErrorUtil.handleError("Undefined listing edit PO")
            dialog?.dismiss()
            CreateUpdateListingDialogFragment.launch(
                fragmentManager = supportFragmentManager,
                purpose = ListingManagementEnum.ListingManagementPurpose.UPDATE,
                listingEditPO = listingEditPO
            )
        }
    }

    private fun showCertifiedListingScreen() {
        activity?.run { CertifiedListingHelpDialogFragment.launch(supportFragmentManager) }
    }

    private fun showAcknowledgementDialog() {
        dialogUtil.showActionDialog(
            message = R.string.msg_acknowledge_description,
            positiveButtonLabel = R.string.label_acknowledge,
            onPositiveButtonClickListener = {
                val editPO = viewModel.listingEditPO.value ?: return@showActionDialog
                editPO.isAcknowledged = true
                viewModel.listingEditPO.postValue(editPO)
                layout_credit_summary.updateViewModel(viewModel)
            }
        )
    }

    private val onOpenPhotoPicker: (type: ListingPhotoType) -> Unit = { type ->
        viewModel.listingPhotoType = type
        showPhotoPicker()
    }

    private fun showPhotoPicker() {
        dialogUtil.showImagePickerDialog { _, selectedOption ->
            when (selectedOption) {
                resources.getString(R.string.label_gallery) -> openGallery()
                resources.getString(R.string.label_camera) -> openCamera()
                else -> throw Throwable("Wrong image picker type")
            }
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
                    openGallery()
                }
            }
            PermissionUtil.REQUEST_CODE_CAMERA -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openCamera()
                }
            }
        }
    }

    //note: cannot use openGallery from intent util
    private fun openGallery() {
        val mActivity = activity ?: return
        if (PermissionUtil.requestReadWriteExternalStoragePermission(mActivity)) {
            try {
                val intent = Intent()
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(
                        intent, getString(R.string.label_choose_picture)
                    ),
                    REQUEST_CODE_GALLERY
                )
            } catch (e: SecurityException) {
                ErrorUtil.handleError("Security exception when attempt to open gallery", e)
            }
        }
    }

    //note: cannot use openCamera from intent util
    private fun openCamera() {
        val mActivity = activity ?: return
        cameraImageUri = ImageUtil.generateImageFile(mActivity.applicationContext)
        if (PermissionUtil.requestReadWriteExternalStoragePermission(mActivity) &&
            PermissionUtil.requestCameraPermission(mActivity)
        ) {
            try {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraImageUri?.run {
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, this)
                }
                startActivityForResult(intent, REQUEST_CODE_CAMERA)
            } catch (e: SecurityException) {
                ErrorUtil.handleError("Security exception when attempt to open gallery", e)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_GALLERY -> {
                    val intent = data ?: return
                    when {
                        intent.data != null -> {
                            val uri = intent.data ?: return ViewUtil.showMessage("Undefined image")
                            handleImageUris(listOf(uri))
                        }
                        intent.clipData != null -> {
                            intent.clipData?.itemCount?.let { itemCount ->
                                val uris = arrayListOf<Uri>()
                                for (i in 0 until itemCount) {
                                    val uri = intent.clipData?.getItemAt(i)?.uri ?: return
                                    uris.add(uri)
                                }
                                handleImageUris(uris.toList())
                            }
                        }
                        else -> {
                            //do nothing
                        }
                    }
                }
                REQUEST_CODE_CAMERA -> {
                    cameraImageUri?.run { handleImageUris(listOf(this)) }
                }
                else -> {
                    super.onActivityResult(requestCode, resultCode, data)
                }
            }
        }
    }

    private fun handleImageUris(uris: List<Uri>) {
        val context = activity?.applicationContext ?: return
        val files =
            uris.mapIndexedNotNull { index, uri -> ImageUtil.getFileFromUri(context, uri, index) }
        viewModel.imageFiles.value = files
    }

    private fun confirmationToTakeOffListing() {
        dialogUtil.showActionDialog(R.string.msg_take_off_listing) {
            val listingId = viewModel.listingEditPO.value?.id
                ?: return@showActionDialog ViewUtil.showMessage("Undefined listing id")
            viewModel.takeOffListing(listingId.toString())
        }
    }

    private fun confirmationToDeleteListing() {
        dialogUtil.showActionDialog(R.string.msg_delete_listing) {
            val listingId = viewModel.listingEditPO.value?.id
                ?: return@showActionDialog ViewUtil.showMessage("Undefined listing id")
            viewModel.deleteListing(listingId.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        setupFullScreenWindow()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        RxBus.publish(NotifyPostListingEvent())
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_post_listing
    }

    override fun getViewModelClass(): Class<PostListingViewModel> {
        return PostListingViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}