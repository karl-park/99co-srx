package sg.searchhouse.agentconnect.view.adapter.searchoption

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ListItemTrainLineBinding
import sg.searchhouse.agentconnect.model.api.lookup.LookupMrtsResponse

class TrainLineAdapter : RecyclerView.Adapter<TrainLineAdapter.TrainLineViewHolder>() {
    var items: List<LookupMrtsResponse.TrainLine> = listOf()
    var selectedLineStations: ArrayList<LineStation> = arrayListOf()
    private var expandedTrainLineNames: ArrayList<String> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainLineViewHolder {
        val binding = ListItemTrainLineBinding.inflate(LayoutInflater.from(parent.context))
        return TrainLineViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: TrainLineViewHolder, position: Int) {
        val trainLine = items[position]
        holder.binding.trainLine = trainLine
        holder.binding.position = position
        holder.binding.isExpand = expandedTrainLineNames.any { name ->
            TextUtils.equals(
                name,
                trainLine.lineName
            )
        }
        holder.binding.root.setOnClickListener { showHideStations(holder, trainLine) }
    }

    private fun showHideStations(
        holder: TrainLineViewHolder,
        trainLine: LookupMrtsResponse.TrainLine
    ) {
        val isThisTrainLineChecked =
            expandedTrainLineNames.any { name -> TextUtils.equals(name, trainLine.lineName) }

        holder.binding.isExpand = !isThisTrainLineChecked

        if (isThisTrainLineChecked) {
            expandedTrainLineNames.remove(trainLine.lineName)
            clearStationList(holder)
        } else {
            expandedTrainLineNames.add(trainLine.lineName)
            populateStationList(holder, trainLine)
        }
    }

    private fun populateStationList(
        holder: TrainLineViewHolder,
        trainLine: LookupMrtsResponse.TrainLine
    ) {
        holder.binding.listStations.layoutManager = LinearLayoutManager(holder.itemView.context)
        val adapter = StationAdapter()
        adapter.onStationClickListener = { station ->
            selectOrDeselectStation(trainLine, station)
            adapter.notifyDataSetChanged()
        }

        adapter.onLineAllStationsClickListener = {
            selectOrDeselectAllStations(trainLine, it)
            adapter.notifyDataSetChanged()
        }

        val arrayList = arrayListOf<Any>()

        // TODO: Do proper check when server returns type of train line
        val allStationsLabel = holder.itemView.context.getString(
            if (trainLine.lineName.contains(LABEL_LRT)) {
                R.string.label_all_lrt_stations
            } else {
                R.string.label_all_mrt_stations
            }, trainLine.lineName
        )

        val allStations = StationAdapter.AllStations(
            trainLine.lineName,
            allStationsLabel,
            trainLine.stations.size
        )

        arrayList.add(allStations)
        arrayList.addAll(trainLine.stations)
        adapter.items = arrayList
        adapter.selectedLineStations = selectedLineStations

        holder.binding.listStations.adapter = adapter
    }

    private fun selectOrDeselectStation(
        trainLine: LookupMrtsResponse.TrainLine,
        station: LookupMrtsResponse.TrainLine.Station
    ) {
        val isStationSelected =
            selectedLineStations.any { lineStation -> lineStation.stationId == station.amenityId }
        if (isStationSelected) {
            selectedLineStations.find { it.stationId == station.amenityId }?.let { lineStation ->
                selectedLineStations.remove(lineStation)
            }
        } else {
            val lineStation = LineStation(trainLine.lineName, station.amenityId)
            selectedLineStations.add(lineStation)
        }
    }

    private fun selectOrDeselectAllStations(
        trainLine: LookupMrtsResponse.TrainLine,
        lineAllStationsOption: StationAdapter.Companion.LineAllStationsOption
    ) {
        when (lineAllStationsOption) {
            StationAdapter.Companion.LineAllStationsOption.SELECT_ALL -> {
                trainLine.stations.map { station ->
                    if (!selectedLineStations.any { thisStation -> thisStation.stationId == station.amenityId }) {
                        val lineStation = LineStation(trainLine.lineName, station.amenityId)
                        selectedLineStations.add(lineStation)
                    }
                }
            }
            StationAdapter.Companion.LineAllStationsOption.DESELECT_ALL -> {
                trainLine.stations.map { station ->
                    val thisStation =
                        selectedLineStations.find { s -> s.stationId == station.amenityId }
                    if (thisStation != null) {
                        selectedLineStations.remove(thisStation)
                    }
                }
            }
        }
    }

    private fun clearStationList(holder: TrainLineViewHolder) {
        holder.binding.listStations.layoutManager = LinearLayoutManager(holder.itemView.context)
        var adapter: RecyclerView.Adapter<*>? =
            holder.binding.listStations.adapter as? StationAdapter ?: return
        adapter = adapter as StationAdapter
        adapter.items = arrayListOf()
        adapter.notifyDataSetChanged()
    }

    class TrainLineViewHolder(val binding: ListItemTrainLineBinding) :
        RecyclerView.ViewHolder(binding.root)

    // TODO: Use train line ID from server if available
    open class LineStation(val trainLineName: String, val stationId: Int)

    companion object {
        const val LABEL_LRT = "LRT"
    }
}