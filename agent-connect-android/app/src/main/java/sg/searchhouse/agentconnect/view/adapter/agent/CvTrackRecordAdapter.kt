package sg.searchhouse.agentconnect.view.adapter.agent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemMyListingPropertyTypeBinding
import sg.searchhouse.agentconnect.databinding.ListItemTrackRecordBinding
import sg.searchhouse.agentconnect.model.api.agent.GetAgentTransactions.AgentTransaction.*

class CvTrackRecordAdapter(private var onToggleTransaction: (transactionId: Int, isRevealed: Boolean) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<Any> = listOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TRACK_RECORD -> {
                ListItemTrackRecordViewHolder(
                    ListItemTrackRecordBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }
            VIEW_TYPE_PROPERTY_TYPE_HEADER -> {
                PropertyTypeHeaderViewHolder(
                    ListItemMyListingPropertyTypeBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent, false
                    )
                )
            }
            else -> {
                throw Throwable("Wrong view type")
            }
        }
    }


    override fun getItemCount(): Int {
        return items.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemTrackRecordViewHolder -> {
                val transaction = items[position] as TransactedListingPO
                holder.binding.transactedListingPO = transaction
                holder.binding.switchTrackRecord.setOnCheckedChangeListener { compoundButton, b ->
                    if (compoundButton.isPressed) {
                        onToggleTransaction.invoke(
                            transaction.crunchResearchCorporateTransactionId,
                            b
                        )
                    }
                }
            }
            is PropertyTypeHeaderViewHolder -> {
                val header = items[position] as PropertyTypeHeader
                holder.binding.title = header.title
            }
            else -> {
                throw Throwable("Wrong View Type in on Binding View holder")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TransactedListingPO -> VIEW_TYPE_TRACK_RECORD
            is PropertyTypeHeader -> VIEW_TYPE_PROPERTY_TYPE_HEADER
            else -> throw ClassCastException("Wrong list item object")
        }
    }

    class ListItemTrackRecordViewHolder(val binding: ListItemTrackRecordBinding) :
        RecyclerView.ViewHolder(binding.root)


    class PropertyTypeHeaderViewHolder(val binding: ListItemMyListingPropertyTypeBinding) :
        RecyclerView.ViewHolder(binding.root)


    class PropertyTypeHeader(val title: String)

    companion object {
        const val VIEW_TYPE_TRACK_RECORD = 1
        const val VIEW_TYPE_PROPERTY_TYPE_HEADER = 2
    }

}