package sg.searchhouse.agentconnect.view.activity.searchoption

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityMrtSearchBinding
import sg.searchhouse.agentconnect.dsl.getIntList
import sg.searchhouse.agentconnect.dsl.launchActivityForResult
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.view.adapter.searchoption.TrainLineAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.searchoption.MrtSearchViewModel

class MrtSearchActivity : LookupBaseActivity<MrtSearchViewModel, ActivityMrtSearchBinding>() {
    private val trainLineAdapter = TrainLineAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupList()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.trainLines.observeNotNull(this) { trainLines ->
            trainLineAdapter.selectedLineStations = getDefaultLineStations()
            trainLineAdapter.items = trainLines
            trainLineAdapter.notifyDataSetChanged()
        }
    }

    private fun getDefaultLineStations(): ArrayList<TrainLineAdapter.LineStation> {
        val defaultStations = intent.extras?.getString(EXTRA_INPUT_MRT_STATION_IDS)
        val defaultMrtIds = defaultStations?.getIntList() ?: emptyList()
        return defaultMrtIds.map { id ->
            val station = viewModel.getStations().find { it.amenityId == id }
                ?: throw IllegalArgumentException("Mrt of amenity ID $id not found!")
            TrainLineAdapter.LineStation(station.stationName, id)
        } as ArrayList<TrainLineAdapter.LineStation>
    }

    private fun setupList() {
        binding.list.layoutManager = LinearLayoutManager(this)
        binding.list.adapter = trainLineAdapter
    }

    private fun search() {
        val allStations = viewModel.getStations()

        val selectedStationIdArray =
            trainLineAdapter.selectedLineStations.map { station -> station.stationId }

        val selectedStationIds = selectedStationIdArray.joinToString(",")
        val selectedStationNames = selectedStationIdArray.filter { stationId ->
            allStations.any { station -> station.amenityId == stationId }
        }.joinToString(", ") { stationId ->
            allStations.find { station -> station.amenityId == stationId }!!.stationName
        }

        val intent = Intent()
        intent.putExtra(EXTRA_MRT_STATION_IDS, selectedStationIds)
        intent.putExtra(EXTRA_MRT_STATION_NAMES, selectedStationNames)
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        private const val EXTRA_INPUT_MRT_STATION_IDS = "EXTRA_INPUT_MRT_STATION_IDS"
        const val EXTRA_MRT_STATION_IDS = "EXTRA_MRT_STATION_IDS"
        const val EXTRA_MRT_STATION_NAMES = "EXTRA_MRT_STATION_NAMES"

        fun launchForResult(
            activity: Activity,
            defaultMrtStationIds: String? = null,
            requestCode: Int,
            returnOption: ReturnOption = ReturnOption.SEARCH
        ) {
            val extras = Bundle()
            extras.putString(EXTRA_INPUT_MRT_STATION_IDS, defaultMrtStationIds)
            extras.putSerializable(EXTRA_RETURN_OPTION, returnOption)
            activity.launchActivityForResult(
                MrtSearchActivity::class.java,
                extras,
                requestCode = requestCode
            )
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_mrt_search
    }

    override fun getViewModelClass(): Class<MrtSearchViewModel> {
        return MrtSearchViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar {
        return binding.toolbar
    }

    override fun onReturnMenuItemClicked() {
        search()
    }
}