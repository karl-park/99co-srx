package sg.searchhouse.agentconnect.view.adapter.listing.marketing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.LayoutFeaturedPromptBinding
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.helper.RxBus
import sg.searchhouse.agentconnect.model.api.common.NameValuePO
import sg.searchhouse.agentconnect.event.dashboard.ShowFeaturedListingPromptEvent
import sg.searchhouse.agentconnect.view.adapter.base.AppPagerAdapter

class FeaturedPromptPagerAdapter(val onClickListener: (listingIdType: String) -> Unit) :
    AppPagerAdapter<NameValuePO>() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = DataBindingUtil
            .inflate<LayoutFeaturedPromptBinding>(
                LayoutInflater.from(container.context),
                R.layout.layout_featured_prompt, container, false
            )
        val prompt = listItems[position]

        binding.run {
            featuredPrompt = prompt
            btnAddNow.setOnClickListener {
                onClickListener.invoke("${prompt.name},${ListingEnum.ListingType.SRX_LISTING.value}")
            }
            layoutSwipeRefresh.setOnRefreshListener {
                // Swipe down to dismiss prompt
                RxBus.publish(ShowFeaturedListingPromptEvent(false))
            }
            container.addView(root)
        }

        return binding.root
    }
}