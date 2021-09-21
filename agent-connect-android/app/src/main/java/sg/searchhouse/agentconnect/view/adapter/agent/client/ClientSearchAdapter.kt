package sg.searchhouse.agentconnect.view.adapter.agent.client

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemCreateListingPropertyAddressBinding
import sg.searchhouse.agentconnect.model.api.location.PropertyPO

class ClientSearchAdapter constructor(private val onSelectProperty: ((PropertyPO) -> Unit)) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var properties: List<PropertyPO> = emptyList()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AddressSearchViewHolder(
            ListItemCreateListingPropertyAddressBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return properties.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return when (holder) {
            is AddressSearchViewHolder -> {
                val address = properties[position]
                holder.binding.property = address
                holder.binding.llPropertyAddress.setOnClickListener {
                    onSelectProperty.invoke(address)
                }
            }
            else -> {
                throw Throwable("Wrong View Type in Address Search Adapter")
            }
        }
    }

    class AddressSearchViewHolder(val binding: ListItemCreateListingPropertyAddressBinding) :
        RecyclerView.ViewHolder(binding.root)

}