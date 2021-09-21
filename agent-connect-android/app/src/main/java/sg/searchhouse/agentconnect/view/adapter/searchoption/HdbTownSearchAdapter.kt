package sg.searchhouse.agentconnect.view.adapter.searchoption

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemHdbTownBinding
import sg.searchhouse.agentconnect.model.api.lookup.LookupHdbTownsResponse.*

class HdbTownSearchAdapter(private val hdbTowns: ArrayList<HdbTown>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var selectedTowns: ArrayList<Int> = ArrayList()
    var onSelectHdbTown: ((HdbTown) -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return HdbTownViewHolder(ListItemHdbTownBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return when (holder) {
            is HdbTownViewHolder -> {
                //get each hdb town
                val hdbTown = hdbTowns[position]
                //checked state
                holder.binding.cbHdbTown.isChecked = selectedTowns.contains(hdbTown.amenityId)
                //update views
                holder.binding.hdbTown = hdbTown
                holder.binding.llHdbTown.setOnClickListener {
                    onSelectHdbTown?.invoke(hdbTown)
                }
            }
            else -> {
                throw Throwable("View Type Wrong in Hdb towns adapter")
            }
        }
    }

    fun getSelectedHdbTownIds(selectedHdbTowns: ArrayList<Int>) {
        selectedTowns.clear()
        selectedTowns.addAll(selectedHdbTowns)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return hdbTowns.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class HdbTownViewHolder(val binding: ListItemHdbTownBinding) :
        RecyclerView.ViewHolder(binding.root)
}