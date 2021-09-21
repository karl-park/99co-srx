package sg.searchhouse.agentconnect.view.activity.common

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_location_search.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityLocationSearchBinding
import sg.searchhouse.agentconnect.dsl.launchActivityForResult
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.util.PermissionUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.search.SearchAutoCompleteAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.common.LocationSearchViewModel

class LocationSearchActivity :
    ViewModelActivity<LocationSearchViewModel, ActivityLocationSearchBinding>() {
    private var adapter = SearchAutoCompleteAdapter {
        val data = Intent()
        data.putExtra(EXTRA_RESULT_LOCATION_ENTRY, it)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtras()
        observeLiveData()
        setupViews()
    }

    private fun setupExtras() {
        val isIncludeNewProject =
            intent.extras?.getBoolean(EXTRA_INPUT_INCLUDE_NEW_PROJECT) ?: false
        viewModel.isIncludeNewProject.postValue(isIncludeNewProject)
    }

    private fun setupViews() {
        setupList()
        setOnClickListeners()
        et_search_address.requestFocus()
        ViewUtil.showKeyboard(et_search_address)
    }

    private fun setOnClickListeners() {
        tv_cancel.setOnClickListener { finish() }
        btn_current_location.setOnClickListener {
            maybeGetCurrentLocation(this) { location ->
                viewModel.findCurrentLocation(location)
            }
        }
    }

    // TODO Refactor get current location as a module
    private fun maybeGetCurrentLocation(activity: Activity, getLocationName: (Location) -> Unit) {
        val isPermissionGranted = PermissionUtil.maybeRequestLocationPermissions(activity)
        if (isPermissionGranted) {
            viewModel.getCurrentLocation(getLocationName)
        }
    }

    private fun setupList() {
        rv_auto_complete_properties.layoutManager = LinearLayoutManager(this)
        rv_auto_complete_properties.adapter = adapter
    }

    private fun observeLiveData() {
        viewModel.suggestions.observeNotNull(this) { suggestions ->
            val list = arrayListOf<Any>()
            suggestions.map { pair ->
                list.add(SearchAutoCompleteAdapter.EntryType(pair.first))
                list.addAll(pair.second)
            }
            adapter.items = list
            adapter.notifyDataSetChanged()
        }
        viewModel.currentLocation.observeNotNull(this) {
            et_search_address.setText(it.postalCode.toString())
        }
    }

    companion object {
        const val EXTRA_RESULT_LOCATION_ENTRY = "EXTRA_RESULT_LOCATION_ENTRY"
        private const val EXTRA_INPUT_INCLUDE_NEW_PROJECT = "EXTRA_INPUT_INCLUDE_NEW_PROJECT"

        fun launchForResult(
            activity: Activity,
            requestCode: Int,
            includeNewProject: Boolean = false
        ) {
            val extras = Bundle()
            extras.putBoolean(EXTRA_INPUT_INCLUDE_NEW_PROJECT, includeNewProject)
            activity.launchActivityForResult(
                LocationSearchActivity::class.java,
                extras = extras,
                requestCode = requestCode
            )
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_location_search
    }

    override fun getViewModelClass(): Class<LocationSearchViewModel> {
        return LocationSearchViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return null
    }
}