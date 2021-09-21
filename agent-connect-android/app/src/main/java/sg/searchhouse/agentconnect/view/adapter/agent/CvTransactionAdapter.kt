package sg.searchhouse.agentconnect.view.adapter.agent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemTransactedListingBinding
import sg.searchhouse.agentconnect.model.api.listing.ListingPO
import sg.searchhouse.agentconnect.util.NumberUtil

class CvTransactionAdapter(
    val items: ArrayList<ListingPO>,
    private var onCallAgent: (String) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListItemTransactedListingViewHolder(
            ListItemTransactedListingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemTransactedListingViewHolder -> {
                val listingPO = items[position]
                holder.binding.listingPO = listingPO
                holder.binding.tvAgentPhone.setOnClickListener {
                    val agentMobile = listingPO.agentPO?.mobile ?: ""
                    if (NumberUtil.isNaturalNumber(agentMobile)) {
                        onCallAgent.invoke(agentMobile)
                    }
                }
            }
        }
    }

    class ListItemTransactedListingViewHolder(val binding: ListItemTransactedListingBinding) :
        RecyclerView.ViewHolder(binding.root)
}