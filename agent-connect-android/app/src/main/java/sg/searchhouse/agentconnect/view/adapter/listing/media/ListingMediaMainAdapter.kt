package sg.searchhouse.agentconnect.view.adapter.listing.media

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemListingMediaBinding
import sg.searchhouse.agentconnect.databinding.ListItemListingPhotoBinding
import sg.searchhouse.agentconnect.databinding.ListItemListingVideoBinding
import sg.searchhouse.agentconnect.model.api.listing.*
import sg.searchhouse.agentconnect.util.ListingMediaUtil
import sg.searchhouse.agentconnect.util.StringUtil
import sg.searchhouse.agentconnect.view.adapter.base.AppPagerAdapter

class ListingMediaMainAdapter(val onClickListener: (listingMedia: ListingMedia) -> Unit) :
    AppPagerAdapter<ListingMedia>() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return when (val thisListItem = listItems[position]) {
            is PhotoPO -> {
                val binding = DataBindingUtil
                    .inflate<ListItemListingPhotoBinding>(
                        LayoutInflater.from(container.context),
                        R.layout.list_item_listing_photo, container, false
                    )
                binding.imageUrl = thisListItem.url
                container.addView(binding.root)
                binding.root
            }
            is ListingVideoPO -> {
                val binding = DataBindingUtil
                    .inflate<ListItemListingVideoBinding>(
                        LayoutInflater.from(container.context),
                        R.layout.list_item_listing_video, container, false
                    )
                binding.imageUrl = thisListItem.getMediaThumbnailUrl()
                container.addView(binding.root)
                binding.imageView.setOnClickListener { onClickListener.invoke(thisListItem) }
                binding.root
            }
            is ListingVirtualTourPO -> instantiateMedia(
                container,
                thisListItem
            )
            is DroneViewPO -> instantiateMedia(
                container,
                thisListItem
            )
            else -> instantiateMedia(container, thisListItem)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun instantiateMedia(
        container: ViewGroup,
        thisListItem: ListingMedia
    ): Any {
        val binding = DataBindingUtil
            .inflate<ListItemListingMediaBinding>(
                LayoutInflater.from(container.context),
                R.layout.list_item_listing_media, container, false
            )
        container.addView(binding.root)
        val isMemoryPermitted = ListingMediaUtil.isMemoryPermitted()
        binding.isShowWebView = isMemoryPermitted
        if (isMemoryPermitted) {
            // Show web view
            binding.webView.loadUrl(thisListItem.getMediaUrl())
            binding.webView.settings.javaScriptEnabled = true
        } else {
            // Show thumbnail image
            binding.url = StringUtil.encodeUrl(thisListItem.getMediaThumbnailUrl())
            binding.title = ListingMediaUtil.getMediaTitle(binding.root.context, thisListItem)
        }
        binding.viewSelect.setOnClickListener { onClickListener.invoke(thisListItem) }
        return binding.root
    }
}