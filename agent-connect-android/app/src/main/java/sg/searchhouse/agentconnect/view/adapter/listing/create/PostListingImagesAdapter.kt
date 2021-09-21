package sg.searchhouse.agentconnect.view.adapter.listing.create

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.sephiroth.android.library.xtooltip.ClosePolicy
import it.sephiroth.android.library.xtooltip.Tooltip
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.LayoutPhotoPickerBinding
import sg.searchhouse.agentconnect.databinding.ListItemPostListingFailedPhotoBinding
import sg.searchhouse.agentconnect.databinding.ListItemPostListingSuccessPhotoBinding
import sg.searchhouse.agentconnect.databinding.ListItemPostListingWrongStatusPhotoBinding
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.QualityListingPhotoStatus
import sg.searchhouse.agentconnect.enumeration.api.ListingManagementEnum.ListingPhotoType
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PhotoPickerTemplate
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.PostListingPhotoPO
import sg.searchhouse.agentconnect.util.ErrorUtil
import sg.searchhouse.agentconnect.view.helper.listing.create.OnStartDragListener

class PostListingImagesAdapter(
    private val context: Context,
    private val photoType: ListingPhotoType,
    private val onClickPhotoPicker: (() -> Unit),
    private val onRemovePhoto: ((PostListingPhotoPO) -> Unit),
    private val onMovePhoto: ((PostListingPhotoPO, Int) -> Unit),
    private val onAppealNonQualityPhoto: ((PostListingPhotoPO) -> Unit)? = null,
    private val onClickSetAsCover: ((PostListingPhotoPO) -> Unit)? = null,
    private val onStartDragListener: OnStartDragListener? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_PHOTO_PICKER = 0
        const val VIEW_TYPE_SUCCESS_PHOTO = 1
        const val VIEW_TYPE_FAIL_PHOTO = 2
        const val VIEW_TYPE_WRONG_STATUS_PHOTO = 3
    }

    var items = arrayListOf<Any>()

    private var showLoading: Boolean = false

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PHOTO_PICKER -> {
                PhotoPickerViewHolder(
                    LayoutPhotoPickerBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_SUCCESS_PHOTO -> {
                SuccessPhotoViewHolder(
                    ListItemPostListingSuccessPhotoBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_FAIL_PHOTO -> {
                FailedPhotoViewHolder(
                    ListItemPostListingFailedPhotoBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_WRONG_STATUS_PHOTO -> {
                WrongPhotoStatusViewHolder(
                    ListItemPostListingWrongStatusPhotoBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> throw Throwable("Wrong View Type in PostListingImagesAdapter")
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun getItemId(position: Int): Long {
        //Note: Inconsistency detected. Invalid item position 0 offset : -1 while deleting first item
        //Added setHasStableIds and getItemId because of blinking when call notifyDataSetChanged but it's causing above issue.
        //so return hashCode int as item ID is to solve both issues
        return when (val item = items[position]) {
            is PostListingPhotoPO -> item.id.toLong()
            is PhotoPickerTemplate -> item.hashCode().toLong()
            else -> position.toLong()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SuccessPhotoViewHolder -> {
                val photo = items[position] as PostListingPhotoPO
                holder.binding.photo = photo
                holder.binding.listingPhotoType = photoType
                if (photoType == ListingPhotoType.LISTING_IMAGE) {
                    holder.binding.isCoverPhoto = photo.coverPhoto == true

                    holder.itemView.setOnTouchListener { _, event ->
                        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                            onStartDragListener?.onStartDrag(holder)
                        }
                        return@setOnTouchListener true
                    }
                }
                holder.binding.tvSetAsCover.setOnClickListener { onClickSetAsCover?.invoke(photo) }
                holder.binding.ibRemovePhoto.setOnClickListener { onRemovePhoto.invoke(photo) }
            }
            is FailedPhotoViewHolder -> {
                val photo = items[position] as PostListingPhotoPO
                holder.binding.photo = photo
                holder.binding.ivNonQuality.setOnClickListener {
                    //Tooltip to show reason why photo become non quality
                    showNonQualityReason(photo = photo, position = position, holder = holder)
                }
                holder.binding.ivNonQualityInfo.setOnClickListener {
                    //Tooltip to show reason why photo become non quality
                    showNonQualityReason(photo = photo, position = position, holder = holder)
                }
                holder.binding.ibRemovePhoto.setOnClickListener { onRemovePhoto.invoke(photo) }
                holder.binding.tvAppeal.setOnClickListener { onAppealNonQualityPhoto?.invoke(photo) }
            }
            is PhotoPickerViewHolder -> {
                holder.binding.showLoadingIndicator = showLoading
                holder.binding.flPhotoPicker.setOnClickListener { onClickPhotoPicker.invoke() }
            }
            is WrongPhotoStatusViewHolder -> {
                //do nothing just show layout
            }
        }
    }

    private fun showNonQualityReason(
        photo: PostListingPhotoPO,
        position: Int,
        holder: FailedPhotoViewHolder
    ) {
        photo.getQualityListingPhotoReason()?.run {
            if (!TextUtils.isEmpty(this)) {
                val tooltip =
                    Tooltip.Builder(context)
                        .anchor(holder.binding.ivNonQualityInfo, 0, 0, true)
                        .styleId(R.style.NonQualityPhotoToolTip)
                        .text(this)
                        .closePolicy(ClosePolicy.TOUCH_OUTSIDE_NO_CONSUME)
                        .create()
                val showDirection = if ((position + 1) % 3 == 0) {
                    Tooltip.Gravity.LEFT
                } else {
                    Tooltip.Gravity.BOTTOM
                }
                tooltip.show(holder.binding.ivNonQualityInfo, showDirection, false)
            }
        } //end of run
    }

    fun showHideLoadingIndicator(isLoading: Boolean) {
        showLoading = isLoading
        notifyItemChanged(items.size - 1)
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is PostListingPhotoPO -> {
                val item = items[position] as PostListingPhotoPO
                when (photoType) {
                    ListingPhotoType.LISTING_IMAGE -> {
                        when (item.getQualityListingPhotoStatus()) {
                            QualityListingPhotoStatus.PASS -> VIEW_TYPE_SUCCESS_PHOTO
                            QualityListingPhotoStatus.FAIL -> VIEW_TYPE_FAIL_PHOTO
                            else -> VIEW_TYPE_WRONG_STATUS_PHOTO
                        }
                    }
                    ListingPhotoType.FLOOR_PLAN -> {
                        VIEW_TYPE_SUCCESS_PHOTO
                    }
                    else -> {
                        VIEW_TYPE_WRONG_STATUS_PHOTO
                    }
                }
            }
            is PhotoPickerTemplate -> VIEW_TYPE_PHOTO_PICKER
            else -> throw  ClassCastException("Wrong list item in special features grid item")
        }
    }

    fun onItemSelected(viewHolder: RecyclerView.ViewHolder) {
        //do nothing for now
    }

    fun onItemDropped(fromPosition: Int?, toPosition: Int?) {
        if (fromPosition != null && toPosition != null) {
            val movedPhoto = items[toPosition] as PostListingPhotoPO
            onMovePhoto.invoke(movedPhoto, toPosition)
        }
    }

    fun clearView(viewHolder: RecyclerView.ViewHolder) {
        //do nothing for now
    }

    class PhotoPickerViewHolder(val binding: LayoutPhotoPickerBinding) :
        RecyclerView.ViewHolder(binding.root)

    class SuccessPhotoViewHolder(val binding: ListItemPostListingSuccessPhotoBinding) :
        RecyclerView.ViewHolder(binding.root)

    class FailedPhotoViewHolder(val binding: ListItemPostListingFailedPhotoBinding) :
        RecyclerView.ViewHolder(binding.root)

    class WrongPhotoStatusViewHolder(val binding: ListItemPostListingWrongStatusPhotoBinding) :
        RecyclerView.ViewHolder(binding.root)
}