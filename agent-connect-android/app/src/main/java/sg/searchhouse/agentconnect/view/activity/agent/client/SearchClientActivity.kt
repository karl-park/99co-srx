package sg.searchhouse.agentconnect.view.activity.agent.client

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityClientSearchBinding
import sg.searchhouse.agentconnect.dsl.widget.setOnEnterListener
import sg.searchhouse.agentconnect.util.PermissionUtil
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.agent.client.ClientSearchAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.agent.client.SearchClientViewModel

class SearchClientActivity :
    ViewModelActivity<SearchClientViewModel, ActivityClientSearchBinding>() {
    private val adapter = ClientSearchAdapter {
        val intent = Intent()
        intent.putExtra(EXTRA_OUTPUT_PROPERTY_PO, it)
        setResult(RESULT_ADDRESS_OK, intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.searchText.observe(this) {
            viewModel.performRequest(it ?: "")
        }

        viewModel.properties.observe(this) {
            adapter.properties = it ?: emptyList()
            adapter.notifyDataSetChanged()
        }

        viewModel.currentLocation.observe(this) {
            binding.etSearch.binding.editText.setText(it.postalCode.toString())
        }
    }

    private fun setupViews() {
        setupList()
        setOnClickListeners()
        setTextBoxListeners()
        showKeyboard()
    }

    private fun setupList() {
        binding.list.layoutManager = LinearLayoutManager(this)
        binding.list.adapter = adapter
    }

    private fun setTextBoxListeners() {
        binding.etSearch.setOnTextChangedListener {
            viewModel.searchText.postValue(it)
        }
        binding.etSearch.binding.editText.setOnEnterListener {
            binding.etSearch.binding.editText.post {
                ViewUtil.hideKeyboard(binding.etSearch.binding.editText) {
                    val intent = Intent()
                    intent.putExtra(EXTRA_OUTPUT_CLIENT_NAME, binding.etSearch.binding.editText.text.toString())
                    setResult(RESULT_CLIENT_NAME_OK, intent)
                    finish()
                }
            }
        }
    }

    private fun showKeyboard() {
        binding.etSearch.binding.editText.requestFocus()
        binding.etSearch.binding.editText.post { ViewUtil.showKeyboard(binding.etSearch.binding.editText) }
    }

    private fun setOnClickListeners() {
        binding.btnCancel.setOnClickListener { finish() }
        binding.btnCurrentLocation.setOnClickListener {
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

    override fun getLayoutResId(): Int {
        return R.layout.activity_client_search
    }

    override fun getViewModelClass(): Class<SearchClientViewModel> {
        return SearchClientViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return null
    }

    companion object {
        const val EXTRA_OUTPUT_PROPERTY_PO = "EXTRA_OUTPUT_PROPERTY_PO"
        const val EXTRA_OUTPUT_CLIENT_NAME = "EXTRA_OUTPUT_CLIENT_NAME"

        const val RESULT_ADDRESS_OK = 2
        const val RESULT_CLIENT_NAME_OK = 3
    }
}