package sg.searchhouse.agentconnect.view.adapter.amenity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemAmenityBinding
import sg.searchhouse.agentconnect.enumeration.api.LocationEnum
import sg.searchhouse.agentconnect.model.api.listing.AmenitiesPO

class AmenityAdapter constructor(
    private val onAmenitySelectListener: (AmenitiesPO) -> Unit,
    private val onTransportSelectListener: (LocationEnum.TravelMode) -> Unit
) :
    RecyclerView.Adapter<AmenityAdapter.AmenityViewHolder>() {
    var items: List<AmenitiesPO> = emptyList()
    private var selectedAmenityId: Int? = null

    var travelMode: LocationEnum.TravelMode? = null

    var transitDuration: String? = null
    var drivingDuration: String? = null
    var walkingDuration: String? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AmenityViewHolder {
        val binding = ListItemAmenityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AmenityViewHolder(
            binding
        )
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: AmenityViewHolder, position: Int) {
        val amenity = items[position]
        holder.binding.amenity = amenity
        holder.binding.isExpand = selectedAmenityId == amenity.id
        holder.binding.selectedTravelMode = travelMode
        holder.binding.transitDuration = transitDuration
        holder.binding.walkingDuration = walkingDuration
        holder.binding.drivingDuration = drivingDuration
        holder.binding.layoutHeader.setOnClickListener {
            onAmenitySelectListener.invoke(amenity)
        }
        holder.binding.layoutSelectTransport.btnTransportBusMrt.root.setOnClickListener {
            onTransportSelectListener.invoke(
                LocationEnum.TravelMode.TRANSIT
            )
        }
        holder.binding.layoutSelectTransport.btnTransportDrive.root.setOnClickListener {
            onTransportSelectListener.invoke(
                LocationEnum.TravelMode.DRIVING
            )
        }
        holder.binding.layoutSelectTransport.btnTransportWalk.root.setOnClickListener {
            onTransportSelectListener.invoke(
                LocationEnum.TravelMode.WALKING
            )
        }
        holder.binding.executePendingBindings()
    }

    fun populate(amenityPOs: List<AmenitiesPO>) {
        items = amenityPOs
        notifyDataSetChanged()
    }

    fun selectAmenity(amenityPO: AmenitiesPO?) {
        // TODO: Animate expand collapse
        selectedAmenityId = amenityPO?.id
        notifyDataSetChanged()
    }

    fun selectTravelMode(travelMode: LocationEnum.TravelMode) {
        this.travelMode = travelMode
        notifyDataSetChanged()
    }

    class AmenityViewHolder(val binding: ListItemAmenityBinding) :
        RecyclerView.ViewHolder(binding.root)
}