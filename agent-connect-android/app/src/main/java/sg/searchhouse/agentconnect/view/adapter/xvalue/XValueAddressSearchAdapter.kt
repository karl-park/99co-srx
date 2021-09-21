package sg.searchhouse.agentconnect.view.adapter.xvalue

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemPropertyAddressXValueBinding
import sg.searchhouse.agentconnect.model.api.xvalue.SearchWithWalkupResponse

class XValueAddressSearchAdapter :
    RecyclerView.Adapter<XValueAddressSearchAdapter.AddressSearchViewHolder>() {

    var properties: List<SearchWithWalkupResponse.Data> = emptyList()

    var onSelectProperty: ((SearchWithWalkupResponse.Data) -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressSearchViewHolder {
        return AddressSearchViewHolder(
            ListItemPropertyAddressXValueBinding.inflate(
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

    override fun onBindViewHolder(holder: AddressSearchViewHolder, position: Int) {
        val address = properties[position]
        holder.binding.property = address
        holder.binding.llPropertyAddress.setOnClickListener {
            onSelectProperty?.invoke(address)
        }
    }

    class AddressSearchViewHolder(val binding: ListItemPropertyAddressXValueBinding) :
        RecyclerView.ViewHolder(binding.root)

}