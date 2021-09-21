package sg.searchhouse.agentconnect.view.activity.xvalue

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_x_value_address_search.*
import kotlinx.android.synthetic.main.edit_text_search_purple.view.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityXValueAddressSearchBinding
import sg.searchhouse.agentconnect.model.api.xvalue.SearchWithWalkupResponse
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.xvalue.XValueAddressSearchAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.xvalue.XValueAddressSearchViewModel

class XValueAddressSearchActivity :
    ViewModelActivity<XValueAddressSearchViewModel, ActivityXValueAddressSearchBinding>() {
    private lateinit var adapter: XValueAddressSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeLiveData()
        setupViews()
    }

    private fun setupViews() {
        setUpAdapterAndList()
        setOnClickListeners()
        showKeyboard()
    }

    private fun setOnClickListeners() {
        tv_cancel.setOnClickListener {
            finish()
        }
        et_search_address.setOnTextChangedListener {
            viewModel.afterTextChangedSearchText(it)
        }
    }

    private fun setUpAdapterAndList() {
        adapter =
            XValueAddressSearchAdapter()
        adapter.onSelectProperty = onSelectProperty
        rv_auto_complete_properties.layoutManager = LinearLayoutManager(this)
        rv_auto_complete_properties.adapter = adapter
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observe(this) { response ->
            adapter.properties = response?.run { data } ?: emptyList()
            adapter.notifyDataSetChanged()
        }
        viewModel.searchText.observe(this) {
            viewModel.findProperties()
        }
    }

    private val onSelectProperty: (SearchWithWalkupResponse.Data) -> Unit = { property ->
        val result = Intent()
        result.putExtra(EXTRA_RESULT_PROPERTY, property)
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    private fun showKeyboard() {
        et_search_address.edit_text.requestFocus()
        et_search_address.post {
            ViewUtil.showKeyboard(et_search_address.edit_text)
        }
    }

    companion object {
        const val EXTRA_RESULT_PROPERTY = "EXTRA_RESULT_PROPERTY_PO"
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_x_value_address_search
    }

    override fun getViewModelClass(): Class<XValueAddressSearchViewModel> {
        return XValueAddressSearchViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return null
    }
}