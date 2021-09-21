package sg.searchhouse.agentconnect.view.adapter.agent.client

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemClientUploadPhotoBinding

class ClientPhotoAdapter(
    val items: List<Uri>,
    val onRemovePhoto: ((Uri, Int) -> Unit)
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ClientPhotoItemViewHolder(
            ListItemClientUploadPhotoBinding.inflate(
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
            is ClientPhotoItemViewHolder -> {
                val uri = items[position] as Uri
                holder.binding.ivUploadedPhoto.setImageURI(uri)
                holder.binding.ibRemovePhoto.setOnClickListener {
                    onRemovePhoto.invoke(
                        uri,
                        position
                    )
                }
            }
        }
    }

    class ClientPhotoItemViewHolder(val binding: ListItemClientUploadPhotoBinding) :
        RecyclerView.ViewHolder(binding.root)
}