package sg.searchhouse.agentconnect.view.adapter.searchoption

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.databinding.ListItemDistrictBinding
import sg.searchhouse.agentconnect.databinding.ListItemRegionBinding
import sg.searchhouse.agentconnect.databinding.ListItemRegionTitleBinding
import sg.searchhouse.agentconnect.model.api.lookup.LookupSingaporeDistrictsResponse.*

class DistrictSearchAdapter(private val districts: ArrayList<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var selectedDistrictsIds: ArrayList<Int> = ArrayList()
    var onSelectRegion: ((Region) -> Unit)? = null
    var onSelectDistrict: ((Region.District) -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_REGION_TITLE -> {
                RegionTitleViewHolder(ListItemRegionTitleBinding.inflate(LayoutInflater.from(parent.context)))
            }
            VIEW_TYPE_REGION -> {
                RegionViewHolder(ListItemRegionBinding.inflate(LayoutInflater.from(parent.context)))
            }
            VIEW_TYPE_DISTRICT -> {
                DistrictViewHolder(ListItemDistrictBinding.inflate(LayoutInflater.from(parent.context)))
            }
            else -> {
                throw Throwable("Wrong View Type in district search adapter")
            }
        }
    }

    override fun getItemCount(): Int {
        return districts.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return when (holder) {
            is RegionTitleViewHolder -> {
                val regionTitle = districts[position] as String
                holder.binding.regionTitle = regionTitle
            }
            is RegionViewHolder -> {
                val region = districts[position] as Region

                region.isSelected = selectedDistrictsIds.containsAll(region.districtIds)
                holder.binding.cbRegion.isChecked = region.isSelected

                holder.binding.region = region

                holder.binding.llRegion.setOnClickListener {
                    region.isSelected = !holder.binding.cbRegion.isChecked
                    holder.binding.region = region
                    onSelectRegion?.invoke(region)
                }
            }
            is DistrictViewHolder -> {
                val district = districts[position] as Region.District
                //district checked state
                holder.binding.cbDistrict.isChecked = selectedDistrictsIds.contains(district.districtId)
                holder.binding.district = district
                holder.binding.llDistrict.setOnClickListener {
                    onSelectDistrict?.invoke(district)
                }
            }
            else -> {
                throw Throwable("Wrong View Type in Region Search Adapter")
            }
        }
    }

    fun selectedDistrictsIds(selectedDistricts: ArrayList<Int>) {
        selectedDistrictsIds.clear()
        selectedDistrictsIds.addAll(selectedDistricts)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (districts[position]) {
            is String -> VIEW_TYPE_REGION_TITLE
            is Region -> VIEW_TYPE_REGION
            is Region.District -> VIEW_TYPE_DISTRICT
            else -> {
                throw Throwable("Wrong View Type in district search adapter")
            }
        }
    }

    class RegionTitleViewHolder(val binding: ListItemRegionTitleBinding) : RecyclerView.ViewHolder(binding.root)
    class RegionViewHolder(val binding: ListItemRegionBinding) : RecyclerView.ViewHolder(binding.root)
    class DistrictViewHolder(val binding: ListItemDistrictBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        const val VIEW_TYPE_REGION_TITLE = 0
        const val VIEW_TYPE_REGION = 1
        const val VIEW_TYPE_DISTRICT = 2
    }
}