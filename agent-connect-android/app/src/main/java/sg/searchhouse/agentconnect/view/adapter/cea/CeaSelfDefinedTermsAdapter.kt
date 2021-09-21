package sg.searchhouse.agentconnect.view.adapter.cea

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemCeaSelfDefinedTermBinding
import sg.searchhouse.agentconnect.model.api.cea.CeaFormTermPO

class CeaSelfDefinedTermsAdapter(
    private val items: List<CeaFormTermPO>,
    private var onClickItem: (term: CeaFormTermPO) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListItemSelfDefinedTermViewHolder(
            ListItemCeaSelfDefinedTermBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListItemSelfDefinedTermViewHolder -> {
                val item = items[position]
                holder.binding.termPO = item
                holder.itemView.setOnClickListener {
                    onClickItem.invoke(item)
                }
            }
        }
    }

    class ListItemSelfDefinedTermViewHolder(val binding: ListItemCeaSelfDefinedTermBinding) :
        RecyclerView.ViewHolder(binding.root)
}