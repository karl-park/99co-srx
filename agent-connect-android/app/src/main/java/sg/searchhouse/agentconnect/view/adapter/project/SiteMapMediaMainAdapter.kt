package sg.searchhouse.agentconnect.view.adapter.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemListingPhotoBinding
import sg.searchhouse.agentconnect.model.api.project.PhotoPO
import sg.searchhouse.agentconnect.view.adapter.base.AppPagerAdapter

class SiteMapMediaMainAdapter : AppPagerAdapter<PhotoPO>() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val thisListItem = listItems[position]
        val binding = DataBindingUtil
            .inflate<ListItemListingPhotoBinding>(
                LayoutInflater.from(container.context),
                R.layout.list_item_listing_photo, container, false
            )
        binding.imageUrl = thisListItem.url
        container.addView(binding.root)
        return binding.root
    }
}