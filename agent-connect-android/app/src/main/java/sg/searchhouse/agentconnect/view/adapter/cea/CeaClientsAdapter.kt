package sg.searchhouse.agentconnect.view.adapter.cea

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemCeaClientBinding
import sg.searchhouse.agentconnect.model.api.cea.CeaFormClientPO

class CeaClientsAdapter(

    private var onClickItem: (client: CeaFormClientPO) -> Unit,
    private var onClickAddSignature: (client: CeaFormClientPO) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = arrayListOf<CeaFormClientPO>()
    private var signatureMaps = linkedMapOf<String, Bitmap>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListItemCeaClientViewHolder(
            ListItemCeaClientBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemCeaClientViewHolder -> {
                val item = items[position]
                holder.binding.client = item

                holder.binding.isSignatureAdded = signatureMaps.containsKey(item.getSignatureKey())
                holder.binding.signatureBitmap = signatureMaps[item.getSignatureKey()]

                holder.binding.layoutClientInfo.cardAgentClientInfo.setOnClickListener {
                    onClickItem.invoke(item)
                }
                holder.binding.layoutAddSignature.tvAddSignature.setOnClickListener {
                    onClickAddSignature.invoke(item)
                }
                holder.binding.layoutAddSignature.layoutSignature.setOnClickListener {
                    onClickAddSignature.invoke(item)
                }
            }
        }
    }

    fun updateSignatureMaps(maps: LinkedHashMap<String, Bitmap>) {
        signatureMaps = maps
        notifyDataSetChanged()
    }

    fun updateClients(list: List<CeaFormClientPO>) {
        this.items.clear()
        this.items.addAll(list)
        notifyDataSetChanged()
    }

    fun removeClient(position: Int) {
        this.items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size)
    }

    class ListItemCeaClientViewHolder(val binding: ListItemCeaClientBinding) :
        RecyclerView.ViewHolder(binding.root)
}