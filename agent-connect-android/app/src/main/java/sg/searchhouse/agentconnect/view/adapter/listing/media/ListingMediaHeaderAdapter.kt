package sg.searchhouse.agentconnect.view.adapter.listing.media

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemListingMediaHeaderBinding
import sg.searchhouse.agentconnect.databinding.ListItemListingPhotoHeaderBinding
import sg.searchhouse.agentconnect.databinding.ListItemListingVideoHeaderBinding
import sg.searchhouse.agentconnect.model.api.listing.ListingMedia
import sg.searchhouse.agentconnect.model.api.listing.ListingVideoPO
import sg.searchhouse.agentconnect.model.api.listing.PhotoPO
import sg.searchhouse.agentconnect.util.ListingMediaUtil
import sg.searchhouse.agentconnect.util.StringUtil
import sg.searchhouse.agentconnect.view.adapter.base.AppPagerAdapter

class ListingMediaHeaderAdapter(val onClickListener: (position: Int) -> Unit) :
    AppPagerAdapter<ListingMedia>() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return when (val listingMedia = listItems[position]) {
            is PhotoPO -> {
                val binding = DataBindingUtil
                    .inflate<ListItemListingPhotoHeaderBinding>(
                        LayoutInflater.from(container.context),
                        R.layout.list_item_listing_photo_header, container, false
                    )
                binding.imageUrl = listingMedia.getMediaUrl()
                container.addView(binding.root)
                binding.imageView.setOnClickListener { onClickListener.invoke(position) }
                binding.root
            }
            is ListingVideoPO -> {
                val binding = DataBindingUtil
                    .inflate<ListItemListingVideoHeaderBinding>(
                        LayoutInflater.from(container.context),
                        R.layout.list_item_listing_video_header, container, false
                    )
                binding.imageUrl = listingMedia.getMediaThumbnailUrl()
                container.addView(binding.root)
                binding.imageView.setOnClickListener { onClickListener.invoke(position) }
                binding.root
            }
            else -> {
                val binding = DataBindingUtil
                    .inflate<ListItemListingMediaHeaderBinding>(
                        LayoutInflater.from(container.context),
                        R.layout.list_item_listing_media_header, container, false
                    )
                container.addView(binding.root)
                val isMemoryPermitted = ListingMediaUtil.isMemoryPermitted()
                binding.isShowWebView = isMemoryPermitted
                if (isMemoryPermitted) {
                    // Show web view
                    binding.webView.loadUrl(listingMedia.getMediaUrl())
                    binding.webView.settings.javaScriptEnabled = true
                } else {
                    // Show thumbnail image
                    binding.url = StringUtil.encodeUrl(listingMedia.getMediaThumbnailUrl())
                    binding.title =
                        ListingMediaUtil.getMediaTitle(binding.root.context, listingMedia)
                }
                binding.viewClick.setOnClickListener { onClickListener.invoke(position) }
                binding.root
            }
        }
    }
}