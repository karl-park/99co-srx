package sg.searchhouse.agentconnect.view.adapter.listing.media

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemListingPhotoThubnailBinding
import sg.searchhouse.agentconnect.model.api.listing.ListingMedia

class ListingMediaPickerAdapter constructor(val onClickListener: ((position: Int) -> Unit)) :
    RecyclerView.Adapter<ListingMediaPickerAdapter.ThumbnailViewHolder>() {

    var items: List<ListingMedia> = emptyList()
    var selectedPosition: Int = 0

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailViewHolder {
        val binding = ListItemListingPhotoThubnailBinding.inflate(LayoutInflater.from(parent.context))
        return ThumbnailViewHolder(
            binding
        )
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int) {
        val listingMedia = items[position]
        holder.binding.listingMedia = listingMedia
        holder.binding.position = position
        holder.binding.selectedPosition = selectedPosition
        holder.itemView.setOnClickListener { onClickListener.invoke(position) }
    }

    class ThumbnailViewHolder(val binding: ListItemListingPhotoThubnailBinding) :
        RecyclerView.ViewHolder(binding.root)
}