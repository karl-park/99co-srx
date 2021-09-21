package sg.searchhouse.agentconnect.view.adapter.amenity

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import sg.searchhouse.agentconnect.databinding.ListItemAmenityGoogleBinding
import sg.searchhouse.agentconnect.enumeration.api.LocationEnum
import sg.searchhouse.agentconnect.model.api.googleapi.GoogleNearByResponse
import sg.searchhouse.agentconnect.util.LocationUtil
import sg.searchhouse.agentconnect.util.NumberUtil

class AmenityGoogleAdapter constructor(
    val locationUtil: LocationUtil,
    private val onAmenitySelectListener: (GoogleNearByResponse.Result) -> Unit,
    private val onTransportSelectListener: (LocationEnum.TravelMode) -> Unit
) :
    RecyclerView.Adapter<AmenityGoogleAdapter.AmenityViewHolder>() {

    var items: List<GoogleNearByResponse.Result> = emptyList()
    private var selectedNearByResultId: String? = null

    var travelMode: LocationEnum.TravelMode? = null

    var transitDuration: String? = null
    var drivingDuration: String? = null
    var walkingDuration: String? = null

    var listingLatLng: LatLng? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AmenityViewHolder {
        val binding = ListItemAmenityGoogleBinding.inflate(
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
        val nearByResult = items[position]
        holder.binding.nearByResult = nearByResult
        holder.binding.isExpand = TextUtils.equals(selectedNearByResultId, nearByResult.id)
        holder.binding.selectedTravelMode = travelMode
        holder.binding.transitDuration = transitDuration
        holder.binding.walkingDuration = walkingDuration
        holder.binding.drivingDuration = drivingDuration
        holder.binding.layoutHeader.setOnClickListener {
            onAmenitySelectListener.invoke(nearByResult)
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

        val thisLatLng = nearByResult.getLatLng()
        val thisListingLatLng = listingLatLng
        if (thisLatLng != null && thisListingLatLng != null) {
            val distance = locationUtil.getDistance(thisLatLng, thisListingLatLng)
            holder.binding.distance = "${NumberUtil.roundDouble(distance, 2)}km"
        }
        holder.binding.executePendingBindings()
    }

    fun populate(results: List<GoogleNearByResponse.Result>) {
        items = results
        notifyDataSetChanged()
    }

    fun selectResult(result: GoogleNearByResponse.Result?) {
        // TODO: Animate expand collapse
        selectedNearByResultId = result?.id
        notifyDataSetChanged()
    }

    fun selectTravelMode(travelMode: LocationEnum.TravelMode) {
        this.travelMode = travelMode
        notifyDataSetChanged()
    }

    class AmenityViewHolder(val binding: ListItemAmenityGoogleBinding) :
        RecyclerView.ViewHolder(binding.root)
}