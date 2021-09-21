package sg.searchhouse.agentconnect.view.adapter.searchoption

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemAllStationsBinding
import sg.searchhouse.agentconnect.databinding.ListItemStationBinding
import sg.searchhouse.agentconnect.model.api.lookup.LookupMrtsResponse
import sg.searchhouse.agentconnect.model.app.Loading
import sg.searchhouse.agentconnect.view.viewholder.LoadingViewHolder

class StationAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: ArrayList<Any> = arrayListOf()
    var selectedLineStations: ArrayList<TrainLineAdapter.LineStation> = arrayListOf()
    var onStationClickListener: ((LookupMrtsResponse.TrainLine.Station) -> Unit)? = null
    var onLineAllStationsClickListener: ((LineAllStationsOption) -> Unit)? = null

    init { setHasStableIds(true) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ALL_STATIONS -> {
                val binding = ListItemAllStationsBinding.inflate(LayoutInflater.from(parent.context))
                AllStationsViewHolder(binding)
            }
            VIEW_TYPE_STATION -> {
                val binding = ListItemStationBinding.inflate(LayoutInflater.from(parent.context))
                StationViewHolder(binding)
            }
            VIEW_TYPE_LOADING -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.loading_indicator, parent, false)
                LoadingViewHolder(view)
            }
            else -> {
                throw Throwable("Wrong view type")
            }
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_ALL_STATIONS -> {
                val stationViewHolder = holder as AllStationsViewHolder
                val allStations = items[position] as AllStations
                stationViewHolder.binding.title = allStations.allStationsLabel

                val checkedStationsSize = selectedLineStations.filter { TextUtils.equals(it.trainLineName, allStations.lineName) }.size
                stationViewHolder.binding.isSelected = checkedStationsSize == allStations.stationsSize

                stationViewHolder.binding.root.setOnClickListener {
                    val isSelected = when (holder.binding.isSelected) {
                        true -> false
                        else -> true
                    }
                    onLineAllStationsClickListener?.invoke(when (isSelected) {
                        true -> LineAllStationsOption.SELECT_ALL
                        else -> LineAllStationsOption.DESELECT_ALL
                    })
                }
            }
            VIEW_TYPE_STATION -> {
                val stationViewHolder = holder as StationViewHolder
                val station = items[position] as LookupMrtsResponse.TrainLine.Station
                stationViewHolder.binding.station = station
                stationViewHolder.binding.isSelected = selectedLineStations.any {
                    lineStation -> lineStation.stationId == station.amenityId
                }
                stationViewHolder.binding.root.setOnClickListener {
                    onStationClickListener?.invoke(station)
                }
            }
            VIEW_TYPE_LOADING -> {}
            else -> {}
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is AllStations -> VIEW_TYPE_ALL_STATIONS
            is LookupMrtsResponse.TrainLine.Station -> VIEW_TYPE_STATION
            is Loading -> VIEW_TYPE_LOADING
            else -> throw ClassCastException("Wrong list item object")
        }
    }

    class StationViewHolder(val binding: ListItemStationBinding) : RecyclerView.ViewHolder(binding.root)
    class AllStationsViewHolder(val binding: ListItemAllStationsBinding) : RecyclerView.ViewHolder(binding.root)
    open class AllStations(val lineName: String, val allStationsLabel: String, val stationsSize: Int)

    companion object {
        const val VIEW_TYPE_ALL_STATIONS = 1
        const val VIEW_TYPE_STATION = 2
        const val VIEW_TYPE_LOADING = 3

        enum class LineAllStationsOption {
            SELECT_ALL, DESELECT_ALL
        }
    }
}