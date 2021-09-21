package sg.searchhouse.agentconnect.view.activity.xvalue

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityXValueSearchBinding
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.xvalue.XValueExistingResultAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.xvalue.XValueSearchViewModel

class XValueSearchActivity :
    ViewModelActivity<XValueSearchViewModel, ActivityXValueSearchBinding>() {
    private lateinit var adapter: XValueExistingResultAdapter

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
        binding.tvCancel.setOnClickListener {
            finish()
        }
    }

    private fun setUpAdapterAndList() {
        adapter = XValueExistingResultAdapter()
        adapter.onSelectItem = {
            XValueActivity.launch(this, it)
            setResult(Activity.RESULT_OK)
            finish()
        }
        binding.list.layoutManager = LinearLayoutManager(this)
        binding.list.adapter = adapter
    }

    private fun observeLiveData() {
        viewModel.mainResponse.observe(this) { response ->
            adapter.listItems = response?.xvalues?.results ?: emptyList()
            adapter.notifyDataSetChanged()
        }

        viewModel.searchText.observe(this) {
            if (TextUtils.isEmpty(it)) {
                adapter.listItems = emptyList()
                adapter.notifyDataSetChanged()
            } else {
                viewModel.performGetExistingXValues()
            }
        }
    }

    private fun showKeyboard() {
        binding.etSearch.requestFocus()
        binding.etSearch.post {
            ViewUtil.showKeyboard(binding.etSearch)
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_x_value_search
    }

    override fun getViewModelClass(): Class<XValueSearchViewModel> {
        return XValueSearchViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return null
    }
}