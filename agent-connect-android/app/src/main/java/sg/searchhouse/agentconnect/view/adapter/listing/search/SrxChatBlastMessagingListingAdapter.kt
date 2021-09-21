package sg.searchhouse.agentconnect.view.adapter.listing.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemSrxChatMessagingListingBinding
import sg.searchhouse.agentconnect.model.api.chat.SsmConversationListingBlastPO
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.util.NumberUtil
import sg.searchhouse.agentconnect.view.widget.listing.SrxChatListingInfoLayout

class SrxChatBlastMessagingListingAdapter(
    val context: Context,
    private val onClickMessageItem: ((SsmConversationListingBlastPO) -> Unit)
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = listOf<SsmConversationListingBlastPO>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListItemSrxChatBlastMessagingListingViewHolder(
            ListItemSrxChatMessagingListingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemSrxChatBlastMessagingListingViewHolder -> {
                val conversation = items[position]
                holder.binding.conversation = conversation

                if (conversation.unreadCount > 0) {
                    holder.binding.layoutUnreadCount.tvBadge.text =
                        NumberUtil.formatThousand(conversation.unreadCount)
                    holder.binding.layoutUnreadCount.tvBadge.visibility = View.VISIBLE
                } else {
                    holder.binding.layoutUnreadCount.tvBadge.visibility = View.GONE
                }

                if (conversation.listingsBlasted.isNotEmpty()) {
                    conversation.listingsBlasted.forEachIndexed { index, listingPO ->
                        populateListingInfo(
                            holder,
                            listingPO,
                            index != conversation.listingsBlasted.size - 1
                        )
                    }
                }
                holder.binding.cardAgentListingInfo.setOnClickListener {
                    onClickMessageItem.invoke(
                        conversation
                    )
                }
            }
        }
    }

    private fun populateListingInfo(
        holder: ListItemSrxChatBlastMessagingListingViewHolder,
        listing: ListingPO,
        showDivider: Boolean
    ) {
        val layout = SrxChatListingInfoLayout(context)
        layout.binding.listingPO = listing
        layout.binding.showDivider = showDivider
        holder.binding.layoutListings.addView(layout)
    }

    fun updateConversationList(conversations: List<SsmConversationListingBlastPO>) {
        this.items = conversations
        notifyDataSetChanged()
    }

    class ListItemSrxChatBlastMessagingListingViewHolder(val binding: ListItemSrxChatMessagingListingBinding) :
        RecyclerView.ViewHolder(binding.root)
}