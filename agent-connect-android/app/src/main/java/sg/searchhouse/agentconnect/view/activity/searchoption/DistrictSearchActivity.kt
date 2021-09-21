package sg.searchhouse.agentconnect.view.activity.searchoption

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityDistrictBinding
import sg.searchhouse.agentconnect.dsl.getIntList
import sg.searchhouse.agentconnect.dsl.launchActivityForResult
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.model.api.lookup.LookupSingaporeDistrictsResponse.Region
import sg.searchhouse.agentconnect.view.adapter.searchoption.DistrictSearchAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.searchoption.DistrictSearchViewModel

class DistrictSearchActivity :
    LookupBaseActivity<DistrictSearchViewModel, ActivityDistrictBinding>() {
    private lateinit var districts: ArrayList<Any>
    lateinit var adapter: DistrictSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeAdapter()
        viewModel.loadDistricts()
        handleDistrictsResponse()
        setPreSelectedDistricts()
    }

    private fun onSearchSelectedDistricts() {
        viewModel.selectedDistrictIds.value?.let {
            val intent = Intent()
            // Header label e.g. "D1, D2"
            intent.putExtra(EXTRA_SELECTED_DISTRICT_NAMES, it.joinToString(", ") { id -> "D$id" })
            intent.putExtra(EXTRA_SELECTED_DISTRICT_IDS, it.joinToString(","))
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun setPreSelectedDistricts() {
        if (intent.hasExtra(EXTRA_SELECTED_DISTRICT_IDS)) {
            if (!TextUtils.isEmpty(
                    intent.extras?.getString(EXTRA_SELECTED_DISTRICT_IDS).toString()
                )
            ) {
                val defaultDistricts =
                    intent.extras?.getString(EXTRA_SELECTED_DISTRICT_IDS)?.getIntList()
                        ?: emptyList()
                updateSelectedDistrictsToAdapter(ArrayList(defaultDistricts))
            }
        }
    }

    private fun handleDistrictsResponse() {
        viewModel.mainResponse.observeNotNull(this) {
            it.singaporeDistricts.forEach { region ->
                //get district ids and add in regions array
                region.districtIds =
                    region.districts.map { district -> district.districtId } as ArrayList<Int>
                //add title
                districts.add(region.regionName)
                //add region
                districts.add(region)
                //add district
                region.districts.forEach { district -> districts.add(district) }
            }
            adapter.notifyDataSetChanged()
        }
    }

    private val onSelectRegion: (Region) -> Unit = { region ->
        //get from view model
        val selectedDistrictIds = viewModel.selectedDistrictIds.value as ArrayList<Int>
        if (region.isSelected) {
            //true -> add all district from selected region
            region.districts.forEach { district -> selectedDistrictIds.add(district.districtId) }
        } else {
            //false -> remove all district from selected region
            region.districts.forEach { district -> selectedDistrictIds.remove(district.districtId) }
        }
        updateSelectedDistrictsToAdapter(selectedDistrictIds)
    }

    private val onSelectDistrict: (Region.District) -> Unit = { district ->
        //Add or Remove district id to selected arrays
        viewModel.selectedDistrictIds.value?.let { selectedDistrictIds ->
            if (selectedDistrictIds.contains(district.districtId)) {
                //remove from selected district
                selectedDistrictIds.remove(district.districtId)
            } else {
                //add to selected district
                selectedDistrictIds.add(district.districtId)
            }
            updateSelectedDistrictsToAdapter(selectedDistrictIds)
        }
    }

    private fun updateSelectedDistrictsToAdapter(selectedDistrictIds: ArrayList<Int>) {
        //update view model and update adapter
        if (selectedDistrictIds.isNotEmpty()) {
            adapter.selectedDistrictsIds(ArrayList(selectedDistrictIds.distinct()))
            viewModel.selectedDistrictIds.value = ArrayList(selectedDistrictIds.distinct())
        } else {
            adapter.selectedDistrictsIds(selectedDistrictIds)
            viewModel.selectedDistrictIds.value = selectedDistrictIds
        }
    }

    private fun initializeAdapter() {
        //initialize adapter, array list and recycler view
        districts = ArrayList()

        adapter = DistrictSearchAdapter(districts)
        adapter.onSelectRegion = onSelectRegion
        adapter.onSelectDistrict = onSelectDistrict

        binding.rvDistricts.layoutManager = LinearLayoutManager(this)
        binding.rvDistricts.adapter = adapter
    }

    companion object {
        const val EXTRA_SELECTED_DISTRICT_IDS = "selected_district_ids"
        const val EXTRA_SELECTED_DISTRICT_NAMES = "selected_district_names"

        fun launchForResult(
            activity: Activity,
            defaultDistricts: String? = null,
            requestCode: Int,
            returnOption: ReturnOption = ReturnOption.SEARCH
        ) {
            val extras = Bundle()
            extras.putString(EXTRA_SELECTED_DISTRICT_IDS, defaultDistricts)
            extras.putSerializable(EXTRA_RETURN_OPTION, returnOption)
            activity.launchActivityForResult(
                DistrictSearchActivity::class.java,
                extras,
                requestCode = requestCode
            )
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_district
    }

    override fun getViewModelClass(): Class<DistrictSearchViewModel> {
        return DistrictSearchViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar {
        return binding.toolbar
    }

    override fun onReturnMenuItemClicked() {
        onSearchSelectedDistricts()
    }
}