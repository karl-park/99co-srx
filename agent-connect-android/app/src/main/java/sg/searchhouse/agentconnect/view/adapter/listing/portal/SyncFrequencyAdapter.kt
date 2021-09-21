package sg.searchhouse.agentconnect.view.adapter.listing.portal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemSyncListingsFrequencyBinding
import sg.searchhouse.agentconnect.model.api.listing.listingmanagement.GetPortalAPIsResponse

class SyncFrequencyAdapter(
    private val onSelectSyncFrequency: (GetPortalAPIsResponse.SyncData) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var syncList: List<GetPortalAPIsResponse.SyncData> = listOf()
    private var selectedSyncFrequency: Int = 0

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListItemSyncListingsFrequencyViewHolder(
            ListItemSyncListingsFrequencyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return syncList.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemSyncListingsFrequencyViewHolder -> {
                val syncFrequency = syncList[position]
                holder.binding.syncData = syncFrequency
                holder.binding.showDivider = position != syncList.size - 1
                holder.binding.isChecked = selectedSyncFrequency == syncFrequency.value
                holder.binding.layoutSyncPeriod.setOnClickListener {
                    updateSelectSynFrequency(syncFrequency)
                }
                holder.binding.rbSyncFrequency.setOnClickListener {
                    updateSelectSynFrequency(syncFrequency)
                }
            }
        }
    }

    fun resetSelectSyncFrequency() {
        selectedSyncFrequency = 0
        notifyDataSetChanged()
    }

    fun updateSelectSynFrequency(syncFrequency: GetPortalAPIsResponse.SyncData) {
        selectedSyncFrequency = syncFrequency.value
        notifyDataSetChanged()
        onSelectSyncFrequency.invoke(syncFrequency)
    }

    class ListItemSyncListingsFrequencyViewHolder(val binding: ListItemSyncListingsFrequencyBinding) :
        RecyclerView.ViewHolder(binding.root)
}