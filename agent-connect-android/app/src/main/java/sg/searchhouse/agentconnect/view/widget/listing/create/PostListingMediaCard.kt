package sg.searchhouse.agentconnect.view.widget.listing.create

import android.content.Context
import android.content.DialogInterface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.CardPostListingMediaBinding
import sg.searchhouse.agentconnect.dsl.widget.setOnEditorActionListener
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.ListingPhotoType
import sg.searchhouse.agentconnect.model.api.listing.ListingEditPO
import sg.searchhouse.agentconnect.model.api.listing.ListingRemoteOptionPO
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PhotoPickerTemplate
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PostListingPhotoPO
import sg.searchhouse.agentconnect.view.helper.listing.create.ItemTouchHelperCallback
import sg.searchhouse.agentconnect.view.helper.listing.create.OnStartDragListener
import sg.searchhouse.agentconnect.util.DialogUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.adapter.listing.create.PostListingImagesAdapter
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.create.PostListingViewModel
import java.lang.IndexOutOfBoundsException

class PostListingMediaCard(context: Context, attributeSet: AttributeSet? = null) :
    LinearLayout(context, attributeSet), OnStartDragListener {

    var viewModel: PostListingViewModel? = null

    private lateinit var photoAdapter: PostListingImagesAdapter
    private var itemTouchHelper: ItemTouchHelper? = null

    private lateinit var floorPlanAdapter: PostListingImagesAdapter


    var onOpenPicker: ((ListingPhotoType) -> Unit)? = null

    val binding: CardPostListingMediaBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.card_post_listing_media,
        this,
        true
    )

    init {
        setupListingImagesList()
        setupFloorPlanImagesList()
        setupVideoURL()
        setupVideoViewing()
    }

    fun setupListingEditPO(listingEditPO: ListingEditPO) {
        binding.listingEditPO = listingEditPO
    }

    fun populateSubmittedPhotos(
        listingPhotos: List<PostListingPhotoPO>,
        floorPlanPhotos: List<PostListingPhotoPO>,
        listingEditPO: ListingEditPO
    ) {
        if (listingPhotos.isNotEmpty()) {
            showHideNonQualityInfo(listingPhotos)
            photoAdapter.items.addAll(listingPhotos)
            photoAdapter.items.add(PhotoPickerTemplate())
            photoAdapter.notifyDataSetChanged()
        } else {
            photoAdapter.items.add(PhotoPickerTemplate())
            photoAdapter.notifyDataSetChanged()
        }

        if (floorPlanPhotos.isNotEmpty()) {
            floorPlanAdapter.items.addAll(floorPlanPhotos)
            floorPlanAdapter.items.add(PhotoPickerTemplate())
            floorPlanAdapter.notifyDataSetChanged()
        } else {
            floorPlanAdapter.items.add(PhotoPickerTemplate())
            floorPlanAdapter.notifyDataSetChanged()
        }

        //for video binding
        binding.listingEditPO = listingEditPO
    }

    private fun setupListingImagesList() {
        photoAdapter = PostListingImagesAdapter(
            context = context,
            photoType = ListingPhotoType.LISTING_IMAGE,
            onClickPhotoPicker = { onOpenPicker?.invoke(ListingPhotoType.LISTING_IMAGE) },
            onRemovePhoto = { onRemovePhoto(photo = it, type = ListingPhotoType.LISTING_IMAGE) },
            onMovePhoto = { photo, position -> movePhoto(photo = photo, position = position) },
            onAppealNonQualityPhoto = { appealNonQualityPhoto(photo = it) },
            onClickSetAsCover = { setAsCover(photo = it) },
            onStartDragListener = this
        )
        binding.listListingImages.layoutManager = GridLayoutManager(context, 3)
        binding.listListingImages.adapter = photoAdapter

        val callback: ItemTouchHelper.Callback =
            ItemTouchHelperCallback(
                photoAdapter
            )
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper?.attachToRecyclerView(binding.listListingImages)
    }

    private fun setupFloorPlanImagesList() {
        floorPlanAdapter = PostListingImagesAdapter(
            context = context,
            photoType = ListingPhotoType.FLOOR_PLAN,
            onClickPhotoPicker = {
                onOpenPicker?.invoke(ListingPhotoType.FLOOR_PLAN)
            },
            onRemovePhoto = {
                onRemovePhoto(photo = it, type = ListingPhotoType.FLOOR_PLAN)
            },
            onMovePhoto = { photo, position ->
                movePhoto(photo = photo, position = position)
            }
        )
        binding.listFloorPlanImages.layoutManager = GridLayoutManager(context, 3)
        binding.listFloorPlanImages.adapter = floorPlanAdapter
        binding.listFloorPlanImages.setHasFixedSize(true)
    }

    private fun setupVideoURL() {
        binding.etVideoUrl.setOnEditorActionListener(onActionDone = { binding.etVideoUrl.clearFocus() })

        binding.etVideoUrl.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val videoUrl = binding.etVideoUrl.text.toString().trim()
                if (videoUrl.isNotEmpty()) {
                    ViewUtil.hideKeyboard(binding.etVideoUrl)
                    viewModel?.checkYoutubeVideoLink(videoUrl = videoUrl)
                }
            }
        }
    }

    private fun setupVideoViewing() {
        binding.switchVideoViewing.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                val editPO = viewModel?.listingEditPO?.value ?: return@setOnCheckedChangeListener
                if (editPO.remoteOption != null) {
                    editPO.remoteOption?.videoCallInd = isChecked
                } else {
                    editPO.remoteOption =
                        ListingRemoteOptionPO(id = editPO.id ?: 0, videoCallInd = isChecked)
                }
                viewModel?.updateListingEditPO()
            }
        }
    }

    fun isShowingLoadingIndicator(isLoading: Boolean, type: ListingPhotoType?) {
        when (type) {
            ListingPhotoType.LISTING_IMAGE -> {
                photoAdapter.showHideLoadingIndicator(isLoading)
            }
            ListingPhotoType.FLOOR_PLAN -> {
                floorPlanAdapter.showHideLoadingIndicator(isLoading)
            }
            else -> {
                //do nothing
            }
        }
    }

    fun updateAppealStatusAfterRequest(photo: PostListingPhotoPO) {
        val index = photoAdapter.items.indexOf(photo)
        if (index != -1 && index < photoAdapter.items.size) {
            photoAdapter.notifyItemChanged(index)
        }
    }

    fun addUploadedPhoto(photo: PostListingPhotoPO, type: ListingPhotoType?) {
        try {
            when (type) {
                ListingPhotoType.LISTING_IMAGE -> {
                    if (photoAdapter.items.isNotEmpty()) {
                        photoAdapter.items.add(photoAdapter.items.size - 1, photo)
                        photoAdapter.notifyItemInserted(photoAdapter.items.size - 1)
                    } else {
                        photoAdapter.items.add(0, photo)
                        photoAdapter.notifyItemInserted(0)
                    }
                    showHideNonQualityInfo(listOf(photo))
                }
                ListingPhotoType.FLOOR_PLAN -> {
                    if (floorPlanAdapter.items.isNotEmpty()) {
                        floorPlanAdapter.items.add(floorPlanAdapter.items.size - 1, photo)
                        floorPlanAdapter.notifyItemInserted(floorPlanAdapter.items.size - 1)
                    } else {
                        floorPlanAdapter.items.add(0, photo)
                        floorPlanAdapter.notifyItemInserted(0)
                    }
                }
                else -> {
                    //do nothing
                }
            }
        } catch (ex: IndexOutOfBoundsException) {
            ex.printStackTrace()
        }
    }

    private fun showHideNonQualityInfo(items: List<PostListingPhotoPO>) {
        val failedListingPhotos =
            items.filter { it.getQualityListingPhotoStatus() == ListingManagementEnum.QualityListingPhotoStatus.FAIL }
        binding.showNonQualityInfo = failedListingPhotos.isNotEmpty()
    }

    private fun onRemovePhoto(
        photo: PostListingPhotoPO,
        type: ListingPhotoType
    ) {
        DialogUtil(context)
            .showActionDialog(R.string.msg_delete_images_confirmation) {
                viewModel?.deletePhotos(photo, type)
            }
    }

    fun deleteUploadedPhoto(photo: PostListingPhotoPO, photoType: ListingPhotoType?) {
        when (photoType) {
            ListingPhotoType.LISTING_IMAGE -> {
                val index = photoAdapter.items.indexOf(photo)
                if (index != -1 && index < photoAdapter.items.size) {
                    photoAdapter.items.remove(photo)
                    photoAdapter.notifyItemRemoved(index)
                }
            }
            ListingPhotoType.FLOOR_PLAN -> {
                val index = floorPlanAdapter.items.indexOf(photo)
                if (index != -1 && index < floorPlanAdapter.items.size) {
                    floorPlanAdapter.items.remove(photo)
                    floorPlanAdapter.notifyItemRemoved(index)
                }
            }
            else -> {
                //do nothing
            }
        }
    }

    fun repopulateListings(listingEditPO: ListingEditPO, type: ListingPhotoType?) {
        when (type) {
            ListingPhotoType.LISTING_IMAGE -> {
                photoAdapter.items.clear()
                photoAdapter.items.addAll(listingEditPO.photosSubmitted)
                photoAdapter.items.add(PhotoPickerTemplate())
                photoAdapter.notifyDataSetChanged()
                showHideNonQualityInfo(listingEditPO.photosSubmitted)
            }
            ListingPhotoType.FLOOR_PLAN -> {
                floorPlanAdapter.items.clear()
                floorPlanAdapter.items.addAll(listingEditPO.floorPlanPhotosSubmitted)
                floorPlanAdapter.items.add(PhotoPickerTemplate())
                floorPlanAdapter.notifyDataSetChanged()
            }
            else -> {
                //do nothing
            }
        }
    }

    private fun appealNonQualityPhoto(photo: PostListingPhotoPO) {
        val alertDialog = DialogUtil(context).showActionDialog(
            message = R.string.dialog_msg_request_an_appeal_will_be_sent,
            title = R.string.dialog_title_request_an_appeal,
            positiveButtonLabel = R.string.dialog_positive_button_label_appeal,
            negativeButtonLabel = R.string.label_cancel
        ) {
            viewModel?.appealPhoto(postListingPhotoPO = photo)
        }
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            ?.setTextColor(context.getColor(R.color.post_listing_appeal_text))
    }

    private fun movePhoto(photo: PostListingPhotoPO, position: Int) {
        val imagePosition = position + 1
        val uploadPhoto = PostListingPhotoPO(
            id = photo.id,
            position = imagePosition,
            url = photo.url,
            thumbnailUrl = photo.thumbnailUrl,
            coverPhoto = imagePosition == 1,
            qualityStatus = photo.qualityStatus,
            qualityReason = photo.qualityReason,
            appealStatus = photo.appealStatus
        )
        viewModel?.movePhoto(uploadPhoto, imagePosition)
    }

    private fun setAsCover(photo: PostListingPhotoPO) {
        viewModel?.setCoverPhoto(photo)
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper?.startDrag(viewHolder)
    }
}