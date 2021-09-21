package sg.searchhouse.agentconnect.view.activity.searchoption

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_hdb_town.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityHdbTownBinding
import sg.searchhouse.agentconnect.dsl.getIntList
import sg.searchhouse.agentconnect.dsl.launchActivityForResult
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.model.api.lookup.LookupHdbTownsResponse.HdbTown
import sg.searchhouse.agentconnect.view.adapter.searchoption.HdbTownSearchAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.searchoption.HdbTownSearchViewModel

class HdbTownSearchActivity : LookupBaseActivity<HdbTownSearchViewModel, ActivityHdbTownBinding>() {
    lateinit var hdbTowns: ArrayList<HdbTown>
    private lateinit var selectedHdbTownNames: ArrayList<String>
    lateinit var adapter: HdbTownSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeAdapter()
        //call api to get hdb towns
        viewModel.loadHdbTowns()
        setInitialSelectedHdbTowns()
        handleHdbTownsResponse()
    }

    private fun initializeAdapter() {
        hdbTowns = ArrayList()
        selectedHdbTownNames = ArrayList()

        adapter = HdbTownSearchAdapter(hdbTowns)
        adapter.onSelectHdbTown = onSelectHdbTown

        rv_hdb_towns.layoutManager = LinearLayoutManager(this)
        rv_hdb_towns.adapter = adapter
    }

    private fun setInitialSelectedHdbTowns() {
        if (intent.hasExtra(EXTRA_SELECTED_HDB_TOWN_IDS)) {
            if (!TextUtils.isEmpty(
                    intent.extras?.getString(EXTRA_SELECTED_HDB_TOWN_IDS).toString()
                )
            ) {

                val selectedHdbTowns = ArrayList(
                    intent.extras?.getString(EXTRA_SELECTED_HDB_TOWN_IDS)?.getIntList()
                        ?: emptyList()
                )

                //UPDATE -> adapter and view model
                adapter.getSelectedHdbTownIds(selectedHdbTowns)
                viewModel.selectedHdbTownIds.value = selectedHdbTowns

            }//end of check if
        }
    }

    private fun handleHdbTownsResponse() {
        viewModel.mainResponse.observeNotNull(this) {
            hdbTowns.addAll(it.hdbTowns)
            adapter.notifyDataSetChanged()
        }
    }

    private val onSelectHdbTown: (HdbTown) -> Unit = { hdbTown ->
        viewModel.selectedHdbTownIds.value?.let { selectedHdbIds ->
            if (selectedHdbIds.contains(hdbTown.amenityId)) {
                selectedHdbIds.remove(hdbTown.amenityId)
                selectedHdbTownNames.remove(hdbTown.townName)
            } else {
                selectedHdbIds.add(hdbTown.amenityId)
                selectedHdbTownNames.add(hdbTown.townName)
            }
            //UPDATE -> adapter and view model
            adapter.getSelectedHdbTownIds(selectedHdbIds)
            viewModel.selectedHdbTownIds.value = selectedHdbIds
        }
    }

    private fun onSearchSelectedHdbTowns() {
        viewModel.selectedHdbTownIds.value?.let {
            val intent = Intent()
            intent.putExtra(EXTRA_SELECTED_HDB_TOWN_IDS, it.joinToString(","))
            intent.putExtra(EXTRA_SELECTED_HDB_TOWN_NAMES, selectedHdbTownNames.joinToString(", "))
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    companion object {
        const val EXTRA_SELECTED_HDB_TOWN_IDS = "selected_hdb_town_ids"
        const val EXTRA_SELECTED_HDB_TOWN_NAMES = "selected_hdb_town_names"

        fun launchForResult(
            activity: Activity,
            defaultHdbTowns: String? = null,
            requestCode: Int,
            returnOption: ReturnOption = ReturnOption.SEARCH
        ) {
            val extras = Bundle()
            extras.putString(EXTRA_SELECTED_HDB_TOWN_IDS, defaultHdbTowns)
            extras.putSerializable(EXTRA_RETURN_OPTION, returnOption)
            activity.launchActivityForResult(
                HdbTownSearchActivity::class.java,
                extras,
                requestCode = requestCode
            )
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_hdb_town
    }

    override fun getViewModelClass(): Class<HdbTownSearchViewModel> {
        return HdbTownSearchViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return toolbar
    }

    override fun onReturnMenuItemClicked() {
        onSearchSelectedHdbTowns()
    }
}